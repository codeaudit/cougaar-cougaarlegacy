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
 * Builds an aggregated, correlated task to get the weather forecast for a
 * particular location and time in response to multiple independent forecast
 * requirements.  It also is responsible for cleaning up aggregated forecast
 * tasks after they have become defunct, perhaps because of a failure to get
 * forecast information in time.
 *
 * @author Brandon L. Buteau
 * @version 1.1
 * @since 1.0
 * @see WeatherLiaisonPlugIn
 * @see WeatherRequirementPlugIn
 */
public class RequirementAggregationPlugIn extends SimplePlugIn {

  // Subscriptions for all weather tasks
  private IncrementalSubscription weatherRequirements;
  private IncrementalSubscription aggregatedWeatherTasks;
  private IncrementalSubscription weatherCompositions;
  private IncrementalSubscription failedWeatherAggregations;
  
  // This predicate matches all tasks with verb ForecastWeather
  private UnaryPredicate weatherRequirementsPredicate = new UnaryPredicate() {
    public boolean execute(Object o) {
        if (o instanceof Task) {
            Task t = (Task) o;
            return t.getVerb().toString().equals(WConstants.FORECAST_VERB);
        } else
            return false;
    }
  };

  // This predicate matches all tasks with verb GetWeatherForecast
  private UnaryPredicate aggregatedWeatherTasksPredicate = new UnaryPredicate() {
    public boolean execute(Object o) {
        if (o instanceof MPTask) {
            MPTask t = (MPTask) o;
            return t.getVerb().toString().equals(WConstants.GET_FORECAST_VERB);
        } else
            return false;
    }
  };

  // This predicate matches all compositions of weather tasks
  private UnaryPredicate weatherCompositionPredicate = new UnaryPredicate() {
    public boolean execute(Object o) {
        if (o instanceof Composition) {
            Composition c = (Composition) o;
            Task t = c.getCombinedTask();
            return t.getVerb().toString().equals(WConstants.GET_FORECAST_VERB);
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
   * Establish subscription for ForecastWeather tasks
   **/
  public void setupSubscriptions() {
    
    weatherRequirements = (IncrementalSubscription) subscribe(weatherRequirementsPredicate);
    aggregatedWeatherTasks = (IncrementalSubscription) subscribe(aggregatedWeatherTasksPredicate);
    weatherCompositions = (IncrementalSubscription) subscribe(weatherCompositionPredicate);
    failedWeatherAggregations =
      (IncrementalSubscription) subscribe(failedWeatherAggregationsPredicate);
  }

  /**
   * Top level plugin execute loop.
   **/
  public void execute() {
    
    
    Enumeration reqs = weatherRequirements.getAddedList();
    ArrayList newComps = new ArrayList();
    
    while ( reqs.hasMoreElements() ) {
      
      Task rqmt = (Task) reqs.nextElement();
      PrepositionalPhrase rfpp = rqmt.getPrepositionalPhrase(Constants.Preposition.FOR);
      GeolocLocationImpl reqLoc = (GeolocLocationImpl) rfpp.getIndirectObject();
      PrepositionalPhrase rdpp = rqmt.getPrepositionalPhrase(WConstants.FORECAST_DATE_PREPOSITION);
      String reqDate = (String) rdpp.getIndirectObject();
      //System.out.print("!"); //"New weather requirement for " + reqLoc.toString() +  
      //                 " on " + reqDate + " ... ");

      NewComposition comp = null;
      Iterator it = aggregatedWeatherTasks.getCollection().iterator();
      while ( it.hasNext() ) {
        MPTask aggTask = (MPTask) it.next();
        comp = priorPublishedComposition(rqmt, aggTask);
        if ( comp != null ) { // about to change an already published aggregated task
          publishChange(comp);
          //System.out.print("X"); //ln("Adding to existing composition");
          break;
        } else if ( comp == null && newComps.isEmpty() ) {
          //System.out.print("C"); //ln("Creating new composition");
          comp = createWeatherComposition(rqmt, reqLoc, reqDate);
          newComps.add(comp);
          break;
        }
      }
      if ( comp == null ) { // don't have any prior published aggregated tasks
        comp = priorComposition(rqmt, newComps);
        if ( comp == null ) {
          //System.out.print("D"); //ln("Creating new composition");
          comp = createWeatherComposition(rqmt, reqLoc, reqDate);
          newComps.add(comp);
        } else {
          //System.out.print("Y"); //ln("Adding to existing composition");
        }
      }
      
      NewMPTask t = (NewMPTask) comp.getCombinedTask();
      Enumeration parents = t.getParentTasks();
      Vector v = new Vector();
      v.add(rqmt);
      while ( parents.hasMoreElements() )
        v.add(parents.nextElement());
      t.setParentTasks(v.elements());
      
      Aggregation agg = theLDMF.createAggregation(rqmt.getPlan(), rqmt, comp, null);
      comp.addAggregation(agg);
      publishAdd(agg);
      
    }
    
    Enumeration e = weatherCompositions.getChangedList();
    while ( e.hasMoreElements() ) {
      Composition c = (Composition) e.nextElement();
      MPTask mpt = (MPTask) c.getCombinedTask();
      if ( mpt.getComposition().getAggregations() == null ) { 
        // this must be a defunct composition
        publishRemove(mpt.getComposition());
        publishRemove(mpt);
      }
    }
      
    Enumeration a = failedWeatherAggregations.getAddedList();
    while ( a.hasMoreElements() ) {
      Aggregation agg = (Aggregation) a.nextElement();
      NewComposition comp = (NewComposition) agg.getComposition();
      List aggList = comp.getAggregations();
      aggList.remove(agg);
      comp.setAggregations(aggList);
      publishChange(comp);
      publishRemove(agg);
    }
      
  }
  
  private NewComposition priorPublishedComposition (Task rqmt, MPTask aggTask) {
    
    PrepositionalPhrase rfpp = rqmt.getPrepositionalPhrase(Constants.Preposition.FOR);
    GeolocLocationImpl reqLoc = (GeolocLocationImpl) rfpp.getIndirectObject();
    PrepositionalPhrase rdpp = rqmt.getPrepositionalPhrase(WConstants.FORECAST_DATE_PREPOSITION);
    String reqDate = (String) rdpp.getIndirectObject();
    
    PrepositionalPhrase afpp = aggTask.getPrepositionalPhrase(Constants.Preposition.FOR);
    GeolocLocationImpl aggLoc = (GeolocLocationImpl) afpp.getIndirectObject();
    PrepositionalPhrase adpp = aggTask.getPrepositionalPhrase(WConstants.FORECAST_DATE_PREPOSITION);
    String aggDate = (String) adpp.getIndirectObject();
    
    if ( reqLoc.getName().equals(aggLoc.getName()) && reqDate.equals(aggDate) )
      return (NewComposition) aggTask.getComposition();
    else
      return null;
  }
  
  private NewComposition priorComposition (Task rqmt, ArrayList newComps) {
    
    PrepositionalPhrase rfpp = rqmt.getPrepositionalPhrase(Constants.Preposition.FOR);
    GeolocLocationImpl reqLoc = (GeolocLocationImpl) rfpp.getIndirectObject();
    PrepositionalPhrase rdpp = rqmt.getPrepositionalPhrase(WConstants.FORECAST_DATE_PREPOSITION);
    String reqDate = (String) rdpp.getIndirectObject();
    
    PrepositionalPhrase afpp;
    GeolocLocationImpl aggLoc; 
    PrepositionalPhrase adpp;
    String aggDate;
    
    Iterator it = newComps.iterator();
    while ( it.hasNext() ) {
      NewComposition comp = (NewComposition) it.next();
      Task aggTask = comp.getCombinedTask();
      afpp = aggTask.getPrepositionalPhrase(Constants.Preposition.FOR);
      aggLoc = (GeolocLocationImpl) afpp.getIndirectObject();
      adpp = aggTask.getPrepositionalPhrase(WConstants.FORECAST_DATE_PREPOSITION);
      aggDate = (String) adpp.getIndirectObject();
      if ( reqLoc.getName().equals(aggLoc.getName()) && reqDate.equals(aggDate) )
        return comp;
    }
    
    return null;
  }
  
  private NewComposition createWeatherComposition (Task t,
                                                GeolocLocationImpl loc,
                                                String onDate) {
    NewComposition comp = theLDMF.newComposition();
    NewMPTask aggTask = (NewMPTask) theLDMF.newMPTask();
    comp.setCombinedTask(aggTask);
    AllocationResultDistributor dist = AllocationResultDistributor.DEFAULT;
    comp.setDistributor(dist);
    comp.setIsPropagating(false);
    //comp.addTask(aggTask);
    publishAdd(comp);

    Vector pps = new Vector();
    NewPrepositionalPhrase forpp = new PrepositionalPhraseImpl();
    forpp.setPreposition(Constants.Preposition.FOR);
    forpp.setIndirectObject(loc);
    pps.add(forpp);
    NewPrepositionalPhrase onpp = new PrepositionalPhraseImpl();
    onpp.setPreposition(WConstants.FORECAST_DATE_PREPOSITION);
    onpp.setIndirectObject(onDate);
    pps.add(onpp);
    aggTask.setPrepositionalPhrases(pps.elements());
    aggTask.setComposition(comp);
    aggTask.setPlan(t.getPlan());
    aggTask.setVerb(new Verb(WConstants.GET_FORECAST_VERB));
    Vector parents = new Vector(); // will add parents later
    aggTask.setParentTasks(parents.elements());

    //Aggregation agg = theLDMF.createAggregation(t.getPlan(), aggTask, comp, null);

    publishAdd(aggTask);
    //publishAdd(agg);
    
    return comp;
  }

}
