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

import org.cougaar.core.cluster.IncrementalSubscription;
import org.cougaar.domain.planning.ldm.asset.*;
import org.cougaar.domain.planning.ldm.plan.*;
import org.cougaar.core.plugin.SimplePlugIn;
import org.cougaar.util.UnaryPredicate;
import org.cougaar.domain.glm.ldm.*;
import org.cougaar.domain.glm.ldm.plan.*;
import java.util.*;
import java.text.SimpleDateFormat;


/**
 * Responds to the publishing of new PREPAREFORTRANSPORT tasks by creating
 * new weather requirement tasks.  It extracts location/time data from the
 * PREPAREFORTRANSPORT task itinerary and constructs a new requirement task
 * for each unique location/time.  It also is responsible for cleaning up 
 * requirement tasks after they have become defunct, perhaps because of a 
 * failure to get forecast information in time.
 *
 * @author Brandon L. Buteau
 * @version 1.1
 * @since 1.0
 * @see WeatherRequirementPlugIn
 */
public class WeatherRequirementPlugIn extends SimplePlugIn {

  // Subscription for all PREPAREFORTRANSPORT tasks
  private IncrementalSubscription allTransportTasks;
  private IncrementalSubscription failedWeatherAggregations;
  
  // This predicate matches all tasks with verb PREPAREFORTRANSPORT
  private UnaryPredicate allTransportTasksPredicate = new UnaryPredicate() {
    public boolean execute(Object o) {
        if (o instanceof Task) {
            Task t = (Task) o;
            return t.getVerb().toString().equals(Constants.Verb.PREPAREFORTRANSPORT);
        } else
            return false;
    }
  };

  // This predicate matches all failed weather aggregations
  private UnaryPredicate failedWeatherAggregationsPredicate = new UnaryPredicate() {
    public boolean execute(Object o) {
        if (o instanceof Aggregation) {
            Aggregation agg = (Aggregation) o;
            AllocationResult ar = agg.getReportedResult();
            if ( ar != null && !ar.isSuccess() )
              return true;
            else
              return false;
        } else
            return false;
    }
  };

  /**
   * Establish subscriptions for PREPAREFORTRANSPORT tasks and failed weather aggregations
   **/
  public void setupSubscriptions() {
    
    allTransportTasks = (IncrementalSubscription) subscribe(allTransportTasksPredicate);
    failedWeatherAggregations =
      (IncrementalSubscription) subscribe(failedWeatherAggregationsPredicate);
  }

  /**
   * Top level plugin execute loop.
   **/
  public void execute() {
    
    
    Enumeration t = allTransportTasks.getAddedList();
    
    while ( t.hasMoreElements() ) {
      
      ArrayList newRqmts = new ArrayList();
      Task transport = (Task) t.nextElement();
      PrepositionalPhrase pp = transport.getPrepositionalPhrase(Constants.Preposition.ITINERARYOF);
      Enumeration sched = ((Schedule) pp.getIndirectObject()).getAllScheduleElements();

      while ( sched.hasMoreElements() ) {
        ItineraryElement it = (ItineraryElement) sched.nextElement();
        if ( !it.getRole().equals(Constants.Verb.TRANSIT) )
          continue;
        GeolocLocationImpl fromLoc = (GeolocLocationImpl) it.getStartLocation();
        GregorianCalendar fromCal = getDayForDate(it.getStartDate());
        String onDate = WConstants.DATE_FORMAT.format(fromCal.getTime());
        //System.out.println("Need weather for " + fromLoc.toString() + 
        //                   " on " + onDate + " ... ");
        if ( !priorRequirement(newRqmts, fromLoc, onDate) ) {
          newRqmts.add(createWeatherTask(transport, fromLoc, onDate));
        }
      
        GeolocLocationImpl toLoc = (GeolocLocationImpl) it.getEndLocation();        
        GregorianCalendar toCal = getDayForDate(it.getEndDate());
        onDate = WConstants.DATE_FORMAT.format(toCal.getTime());        
        //System.out.println("Need weather for " + toLoc.toString() + 
        //                  " on " + onDate + " ... ");
        if ( !priorRequirement(newRqmts, toLoc, onDate) ) {
          newRqmts.add(createWeatherTask(transport, toLoc, onDate));
        }
      
      }
    }
    
    Enumeration a = failedWeatherAggregations.getAddedList();
    while ( a.hasMoreElements() ) {
      Aggregation agg = (Aggregation) a.nextElement();
      Task rqmt = agg.getTask();
      publishRemove(rqmt);
    }
      
  }
  
  private boolean priorRequirement (ArrayList arr, GeolocLocationImpl loc, String onDate) {
    Iterator it = arr.iterator();
    while ( it.hasNext() ) {
      Task t = (Task) it.next();
      PrepositionalPhrase pp = t.getPrepositionalPhrase(Constants.Preposition.FOR);
      GeolocLocationImpl ploc = (GeolocLocationImpl) pp.getIndirectObject();
      if ( ploc.getName().equals(loc.getName()) ) {
        pp = t.getPrepositionalPhrase(WConstants.FORECAST_DATE_PREPOSITION);
        if ( onDate.equals((String) pp.getIndirectObject()) )
          return true;
      }
    }
    return false;
  }
  
  private GregorianCalendar getDayForDate (Date d) {
    GregorianCalendar cal = new GregorianCalendar();
    cal.setTime(d);
    cal.set(Calendar.HOUR_OF_DAY, 0);
    cal.set(Calendar.MINUTE, 0);
    cal.set(Calendar.SECOND, 0);
    cal.set(Calendar.MILLISECOND, 0);
    return cal;
  }

  private Task createWeatherTask (Task t,
                                  GeolocLocationImpl loc,
                                  String onDate) {
    NewTask wreq = theLDMF.newTask();
    wreq.setPlan(t.getPlan());
    wreq.setParentTaskUID(t.getParentTaskUID());
    wreq.setVerb(new Verb(WConstants.FORECAST_VERB)); 

    Vector pps = new Vector();
    NewPrepositionalPhrase forpp = new PrepositionalPhraseImpl();
    forpp.setPreposition(Constants.Preposition.FOR);
    forpp.setIndirectObject(loc);
    pps.add(forpp);
    NewPrepositionalPhrase frompp = new PrepositionalPhraseImpl();
    frompp.setPreposition(Constants.Preposition.FROMTASK);
    frompp.setIndirectObject(t);
    pps.add(frompp);
    NewPrepositionalPhrase onpp = new PrepositionalPhraseImpl();
    onpp.setPreposition(WConstants.FORECAST_DATE_PREPOSITION);
    onpp.setIndirectObject(onDate);
    pps.add(onpp);
    wreq.setPrepositionalPhrases(pps.elements());

    NewWorkflow flow = (NewWorkflow) t.getWorkflow();
    flow.addTask(wreq);
    wreq.setWorkflow(flow);
    publishChange(flow);

    publishAdd(wreq);
    return wreq;
  }

}
