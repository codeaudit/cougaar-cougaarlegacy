/* * * * * * * * * * * * * * * * * * * * * * * * * * * * *
 *
 * Copyright (c) 2000-2001 PRC Inc., a wholly-owned
 *   subsidiary of Northrop Grumman Corporation.
 *
 *   This software may be used only in accordance
 *   with the Cougaar Open Source License Agreement. 
 *   See http://www.cougaar.org/documents/license.html
 *   or the www.cougaar.org Web site for more information.
 *   All other rights reserved to PRC Inc.
 *
 * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
 *
 * Author:  Brandon L. Buteau
 *
 */

package com.prc.alp.liaison.weather;

import org.cougaar.core.cluster.*;
import org.cougaar.domain.planning.ldm.plan.*;
import org.cougaar.domain.planning.ldm.trigger.*;
import org.cougaar.core.plugin.PlugInDelegate;
import org.cougaar.util.UnaryPredicate;
import org.cougaar.domain.glm.ldm.*;
import org.cougaar.domain.glm.ldm.plan.*;
import java.util.*;
import java.text.SimpleDateFormat;

/**
 * Responds to the receipt and publishing of weather forecast information by
 * a <CODE>WeatherLiaisonPlugIn</CODE>.  It adjusts the itinerary of each
 * PREPAREFORTRANSPORT task which could be affected by a given forecast.  The 
 * adjustment is made so as to keep end time of the UNLOAD role unchanged;
 * roles prior to the UNLOAD operation are pushed back in time (set to an 
 * earlier time) as needed to accomodate weather delays.
 *
 * @author Brandon L. Buteau
 * @version 1.1
 * @since 1.0
 * @see WeatherRequirementPlugIn
 */
public class WeatherTrigger extends Trigger {
  
  public WeatherTrigger (TriggerMonitor mon, TriggerTester test, TriggerAction act) {
    super(mon, test, act);
  }

  // Creates a predicate matching all weather forecast allocations
  public static UnaryPredicate predicate () {
    return 
      new UnaryPredicate() {
        public boolean execute(Object o) {
          if (o instanceof Allocation) {
            Allocation a = (Allocation) o;
            return ( a.getTask().getVerb().toString().equals(WConstants.GET_FORECAST_VERB) &&
                     a.isStale() );
          } else
            return false;
        }
      };
  }
  
  public static Monitor newMonitor (UnaryPredicate pred) {
    return new Monitor(pred);
  }
  
  /**
   * An implementation of the <CODE>TriggerMonitor</CODE> interface that reacts 
   * to aggregated weather forecast tasks that have become stale.  This happens
   * when new weather forecast results are received by the <CODE>WeatherLiaisonPlugIn</CODE>.
   */
  public static class Monitor extends TriggerPredicateBasedMonitor {
    
    public Monitor (UnaryPredicate pred) {
      super(pred);
    }
  
  }
  
  public static Tester newTester () {
    return new Tester();
  }
  
  /**
   * A trival implementation of the <CODE>TriggerTester</CODE> interface that 
   * always returns true.
   */
  public static class Tester implements TriggerTester {
    
    public boolean Test (Object[] objects) {
      //System.out.println("Tester has " + objects.length + " objects to test");
      return true;
    }
    
  }
  
  public static Action newAction () {
    return new Action();
  }
  
  /**
   * An implementation of the <CODE>TriggerAction</CODE> interface that makes
   * changes to the itineraries of PREPAREFORTRANSPORT tasks.  This is performed
   * in an independent thread to allow other triggers to proceed unimpeded.
   */
  public static class Action implements TriggerAction {
    
    private Object[] objects;
    private PlugInDelegate pid;
    
    public void Perform (Object[] objects, PlugInDelegate pid) {
      this.objects = objects;
      this.pid = pid;
      Thread th = new Thread() {
        public void run() {
          updatePlan();
        }
      };
      th.start();
      return;
    }
    
    private synchronized void updatePlan () {
      //System.out.println("Updating plan for " + objects.length + " allocations");
      for ( int n = 0; n < objects.length; n++ ) {
        Allocation alloc = (Allocation) objects[n];
        pid.openTransaction();  // need this because execution is
                               // in an independent thread
        // calculate impact of weather
        MPTask aggWeather = (MPTask) alloc.getTask();
        GeolocLocationImpl weatherLoc = 
          (GeolocLocationImpl) aggWeather.getPrepositionalPhrase(Constants.Preposition.FOR)
                                        .getIndirectObject();
        String place = weatherLoc.getName();
        String onDate = (String) aggWeather.getPrepositionalPhrase(WConstants.FORECAST_DATE_PREPOSITION)
                                             .getIndirectObject();
        long impact = WConstants.weatherImpact(alloc.getReportedResult());
        if ( impact == 0 ) {
          // do nothing
          //System.out.println("Weather forecast at " + place + " on " + onDate +
          //                   " will not affect transport tasks");
        } else {
          Enumeration e = aggWeather.getParentTasks();
          while ( e.hasMoreElements() ) {
            Task rqmt = (Task) e.nextElement();
            PrepositionalPhrase pp = rqmt.getPrepositionalPhrase(Constants.Preposition.FROMTASK);
            Task transport = (Task) pp.getIndirectObject();
            //System.out.println("Altering itinerary for task " + transport.getUID() + " by " +
            //                   impact/(60*1000) + " minutes");
            saveOriginalItinerary(transport, pid);
            pp = transport.getPrepositionalPhrase(Constants.Preposition.ITINERARYOF);
            NewItineraryElement[] it = getElements((Schedule) pp.getIndirectObject());
            int prefEndElement = it.length - 1; // the element that ends at a preferred time
            if ( it[prefEndElement].getRole().toString().equals(Constants.Verb.TRANSIT) )
              prefEndElement--; // the next-to-last element is the key
            //System.out.println(" due to weather at " + weatherLoc.getName() + " on " + onDate);
            //dumpItinerary(it);
            long cumulativeShift = 0;

            // Start with the task after the preferred-ending task and roll impact forward
            for ( int i = prefEndElement + 1; i < it.length; i++ ) {
              it[i].setStartDate(new Date(it[i].getStartTime() + cumulativeShift));
              String schedPlace = ((GeolocLocationImpl) it[i].getStartLocation()).getName();
              if ( it[i].getRole().toString().equals(Constants.Verb.TRANSIT) &&
                   place.equals(schedPlace) &&
                   withinDate(it[i].getStartTime(), onDate) )
                cumulativeShift = cumulativeShift + impact;
              it[i].setEndDate(new Date(it[i].getEndTime() + cumulativeShift));
              schedPlace = ((GeolocLocationImpl) it[i].getEndLocation()).getName();
              if ( it[i].getRole().toString().equals(Constants.Verb.TRANSIT) &&
                   place.equals(schedPlace) &&
                   withinDate(it[i].getEndTime(), onDate) ) {
                cumulativeShift = cumulativeShift + impact;
                it[i].setEndDate(new Date(it[i].getEndTime() + impact));
              }
            }
            
            // Start with the preferred-ending task and roll impact backward
            cumulativeShift = 0;
            for ( int i = prefEndElement; i >= 0; i-- ) {
              it[i].setEndDate(new Date(it[i].getEndTime() + cumulativeShift));
              String schedPlace = ((GeolocLocationImpl) it[i].getEndLocation()).getName();
              if ( it[i].getRole().toString().equals(Constants.Verb.TRANSIT) &&
                   place.equals(schedPlace) &&
                   withinDate(it[i].getEndTime(), onDate) )
                cumulativeShift = cumulativeShift - impact;
              it[i].setStartDate(new Date(it[i].getStartTime() + cumulativeShift));
              schedPlace = ((GeolocLocationImpl) it[i].getStartLocation()).getName();
              if ( it[i].getRole().toString().equals(Constants.Verb.TRANSIT) &&
                   place.equals(schedPlace) &&
                   withinDate(it[i].getStartTime(), onDate) ) {
                cumulativeShift = cumulativeShift - impact;
                it[i].setStartDate(new Date(it[i].getStartTime() - impact));
              }
            }
            alloc.setStale(false);
            pid.publishChange(alloc);
            pid.publishChange(transport);
          }
        }
        try {
          pid.closeTransaction();
        } catch (SubscriberException ex) {
          System.err.println("WeatherTrigger failed to close a transaction:\n" +
                             ex.getMessage());
        }
      }
      return;
    }
    
    private boolean withinDate (long time, String onDate) {
      SimpleDateFormat fmt = WConstants.DATE_FORMAT;
      return fmt.format(new Date(time)).equals(onDate);
    }
    
    private void saveOriginalItinerary (Task transport, PlugInDelegate pid) {
      PrepositionalPhrase pp = transport.getPrepositionalPhrase(Constants.Preposition.ITINERARYOF);
      ItineraryElement[] elements = getElements((Schedule) pp.getIndirectObject());
      Vector old = new Vector();
      for ( int i = 0; i < elements.length; i++ ) {
        NewItineraryElement it = pid.getFactory().newItineraryElement();
        it.setStartDate(new Date(elements[i].getStartDate().getTime()));
        it.setEndDate(new Date(elements[i].getEndDate().getTime()));
        it.setStartLocation(elements[i].getStartLocation());
        it.setEndLocation(elements[i].getEndLocation());
        it.setRole(elements[i].getRole());
        old.addElement(it);
      }

      Schedule oldSched = pid.getFactory().newSchedule(old.elements());
      NewPrepositionalPhrase newpp = pid.getFactory().newPrepositionalPhrase();
      newpp.setPreposition(WConstants.ORIGINAL_ITINERARY_PREPOSITION);
      newpp.setIndirectObject(oldSched);
      Enumeration e = transport.getPrepositionalPhrases();
      Vector newpps = new Vector();
      newpps.add(newpp);

      while ( e.hasMoreElements() )
        newpps.add(e.nextElement());

      ((NewTask) transport).setPrepositionalPhrases(newpps.elements());
      // Next line is normally a no-no because it clears change reports,
      // but because of a bug in Task.PrepositionChangeReport, need to clear
      // and then recreate them.
      /*
      Transaction.getCurrentTransaction().getChangeReports(transport);
      Transaction.noteChangeReport(transport, 
                                   new Task.PrepositionChangeReport(WConstants.ORIGINAL_ITINERARY_PREPOSITION));
      Transaction.noteChangeReport(transport,
                                   new Task.PrepositionChangeReport(Constants.Preposition.ITINERARYOF));
      */
    }

    private NewItineraryElement[] getElements (Schedule s) {
      Enumeration sched = s.getAllScheduleElements();
      int itSize = 0;
      while ( sched.hasMoreElements() ) {
        itSize++;
        sched.nextElement();
      }
      NewItineraryElement[] itinerary = new NewItineraryElement[itSize];
      sched = s.getAllScheduleElements();
      int i = 0;
      while ( sched.hasMoreElements() )
        itinerary[i++] = (NewItineraryElement) sched.nextElement();
      return itinerary;
    }

    private void dumpItinerary (NewItineraryElement[] it) {
      SimpleDateFormat fmt = WConstants.DATE_FORMAT;
      for ( int i = 0; i < it.length; i++ ) {
        NewItineraryElement el = it[i];
        System.out.println("Itinerary role: " + el.getRole() + "\n" +   
                           "          from: " + ((GeolocLocationImpl) el.getStartLocation()).getName() + "\n" +
                           "            to: " + ((GeolocLocationImpl) el.getEndLocation()).getName() + "\n"+
                           "         start: " + fmt.format(el.getStartDate()) + "\n" +
                           "           end: " + fmt.format(el.getEndDate()) );
      }
    }

  }
  
}