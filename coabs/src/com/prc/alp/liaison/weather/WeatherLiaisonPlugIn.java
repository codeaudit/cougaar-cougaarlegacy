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

import com.globalinfotek.coabsgrid.*;
import com.prc.alp.liaison.assets.*;
import com.prc.alp.liaison.plugin.*;
import com.prc.alp.liaison.interact.*;
import com.prc.alp.liaison.admin.*;
import com.prc.alp.liaison.LConstants;
import org.cougaar.core.cluster.CollectionSubscription;
import org.cougaar.core.cluster.IncrementalSubscription;
import org.cougaar.core.cluster.ClusterServesPlugIn;
import org.cougaar.domain.planning.ldm.plan.*;
import org.cougaar.domain.planning.ldm.asset.*;
import org.cougaar.util.UnaryPredicate;
import org.cougaar.domain.glm.ldm.*;
import org.cougaar.domain.glm.ldm.plan.*;
import java.util.*;


/**
 * Establishes a liaison with an external RETSINA weather InfoAgent to get
 * weather forecast information in response to the publishing of aggregated
 * forecast tasks.  Returned results are handled through a <CODE>WeatherTrigger</CODE>.
 *
 * @author Brandon L. Buteau
 * @version 1.1
 * @since 1.0
 * @see RequirementAggregationPlugIn
 * @see WeatherTrigger
 */
public class WeatherLiaisonPlugIn extends SimpleLiaisonPlugIn {
  
  public WeatherLiaisonPlugIn () {
    super();
  }

  // Liaison manager -- controls access to potential liaisons
  private LiaisonManager mgr;
  
  // Liaison deputy -- manages interactions with remote agent society (CoABS)
  private CoABSLiaisonDeputy dep;
  
  // A group of weather agents that have been found
  private Vector weatherAgents;
  
  private boolean liaisonSetupCompleted = false;

  // Subscription for all forecast tasks
  private IncrementalSubscription allWeatherTasks;

  // Subscription for my allocations
  private CollectionSubscription allWeatherAllocations;

  // This predicate matches all tasks with verb GetWeatherForecast
  private UnaryPredicate allWeatherTasksPredicate = new UnaryPredicate() {
    public boolean execute(Object o) {
      if (o instanceof Task) {
        Task t = (Task) o;
        return t.getVerb().toString().equals(WConstants.GET_FORECAST_VERB);
      } else
        return false;
    }
  };

  // This predicate matches all allocations with a FORECAST_ASSET_TYPE asset
  private UnaryPredicate allWeatherAllocationsPredicate = new UnaryPredicate() {
    public boolean execute(Object o) {
      if (o instanceof Allocation) {
        Allocation alloc = (Allocation) o;
        Asset a = alloc.getAsset();
        if ( a.getTypeIdentificationPG()
                .getTypeIdentification()
                  .equals(WConstants.FORECAST_ASSET_TYPE) )
          return true;
      }
      return false;
    }
  };
        
  /**
   * Establish subscription for forecast tasks and initialize for connection
   * with CoABS Grid
   **/
  public void setupSubscriptions() {
    
    allWeatherTasks = 
      (IncrementalSubscription) subscribe(allWeatherTasksPredicate);
    allWeatherAllocations =
      (CollectionSubscription) subscribe(allWeatherAllocationsPredicate);
    mgr = createLiaisonManager();
    dep = (CoABSLiaisonDeputy) mgr.createLiaisonDeputy(LConstants.COABS_SOCIETY,
                                                       getDelegate(),
                                                       getParameters());
    
    WeatherTrigger trigger =
      new WeatherTrigger(WeatherTrigger.newMonitor(WeatherTrigger.predicate()),
                         WeatherTrigger.newTester(),
                         WeatherTrigger.newAction());
    publishAdd(trigger);
    
    GregorianCalendar cDay = new GregorianCalendar(2005, 7, 12); // hardwired for now
    
    getCluster().setTime(cDay.getTime().getTime());                                                  
  }

  /**
   * Top level plugin execute loop.
   **/
  public void execute() {
    
    //System.out.println("WeatherLiaisonPlugIn::execute");
    
    // Now look through all new forecast tasks and expand
    
    Enumeration e = allWeatherTasks.getAddedList();
    int eCount = 0;
    int iCount = 0;

    // Have we finished our liaison setup yet?
    if ( !liaisonSetupCompleted && e.hasMoreElements() )
      setupLiaison();
    else if ( liaisonSetupCompleted ) { //check for completed liaison interactions
      Enumeration ins = dep.getInteractions();
      while ( ins.hasMoreElements() ) {
        iCount++;
        Interaction ia = (Interaction) ins.nextElement();
        if ( ia.isDone() )  { // must have completed
          iCount--;
          Object f = dep.finishInteraction(ia);
          if ( f == null )
            System.err.println("Got a null forecast object!");
          else if ( f instanceof String ) {
            System.out.println(f);
          } else 
            saveWeatherResult((ForecastConditions) f);
        }
      }
      if ( iCount > 0 ) // there are still some incomplete interactions
        wakeAfterRealTime(5000); // so make sure we see them later
    }

    while ( liaisonSetupCompleted && e.hasMoreElements() ) {
      eCount++;
      Task weatherReq = (Task) e.nextElement();
      if ( !startWeatherInteraction(weatherReq) )
        continue;  // just try again if we didn't really start a liaison interaction
      //try {
      //  Thread.sleep(15000); // but wait before starting another query (RETSINA bug!)
      //} catch (InterruptedException ex) { }
    }

    if ( eCount > 0 ) { // make sure we wake up to see the result
      wakeAfterRealTime(5000); 
      //System.out.println("WeatherLiaisonPlugIn handled " + eCount + " requests");
    }
  }

  private void setupLiaison() {
    mgr.start(true);
    dep.start(System.getProperty(LConstants.CoABS.SOCIETY_NAME_PROPERTY,
                                  LConstants.CoABS.DEFAULT_GRID_NAME),
              null, true);
    //dep.DEBUG = true;
    
    // go register
    CoABSRegistryInteractor reg = new CoABSRegistryInteractor(dep);
    Interaction regInt = new Interaction(dep, reg, dep.externalManager());
    //regInt.setDebug(true);
    regInt.start(true);

    // find a weather agent
    CoABSAgentDescription desc = new CoABSAgentDescription();
    desc.name = WConstants.AGENT_NAMES[WConstants.RETSINA_CNN_FORECAST_AGENT];
    CoABSLookupInteractor lookup = new CoABSLookupInteractor(dep, desc);
    Interaction lookInt = new Interaction(dep, lookup, dep.externalManager());
    //lookInt.setDebug(true);
    System.out.println("Going to try finding " + desc.name);
    weatherAgents = (Vector) lookInt.start(true);
    if ( weatherAgents != null && weatherAgents.isEmpty() )
      weatherAgents = null;
    if ( weatherAgents == null )
      System.out.println("No weather agent found");
    else {
      AgentRep wagent = (AgentRep) weatherAgents.firstElement();
      System.out.println("Found " + wagent.getName());
      RETSINAUnexpectedWxInteractor iActor =
        new RETSINAUnexpectedWxInteractor(dep, wagent);
      RETSINAUnexpectedWxInteractionFactory fct =
        new RETSINAUnexpectedWxInteractionFactory(dep, iActor, 
                                                  "UnexpectedWx", false);
      dep.putFactory("UnexpectedWx", fct);
      liaisonSetupCompleted = true;
    }
  }

  private boolean startWeatherInteraction (Task weather) {
    
    // Get the data needed for the interaction
    PrepositionalPhrase pp = weather.getPrepositionalPhrase(Constants.Preposition.FOR);
    GeolocLocationImpl loc = (GeolocLocationImpl) pp.getIndirectObject();
    String place = loc.getName();
    pp = weather.getPrepositionalPhrase(WConstants.FORECAST_DATE_PREPOSITION);
    String onDate = (String) pp.getIndirectObject();
    String translatedPlace = 
      WConstants.translateALPToRemote(place, WConstants.RETSINA_CNN_FORECAST_AGENT);
    //System.out.println("New weather requirement for " + translatedPlace + " on " + onDate);
    if ( translatedPlace == null ) { // probably will fail getting forecast then
      //System.out.println("No available forecast data for " + place);
      failDefunctWeatherTask(weather);
      return false;
    }
    Date on = null;
    try {
      on = WConstants.DATE_FORMAT.parse(onDate);
    } catch (Exception ex) { return false; }
    GregorianCalendar onCal = new GregorianCalendar();
    onCal.setTime(on);
    GregorianCalendar today = new GregorianCalendar();
    today.setTime(new Date(currentTimeMillis()));
    today.set(Calendar.HOUR_OF_DAY, 0);
    today.set(Calendar.MINUTE, 0);
    today.set(Calendar.SECOND, 0);
    today.set(Calendar.MILLISECOND, 0);
    today.add(Calendar.DATE, 1);
    if ( today.after(onCal) ) {
      //System.out.println("Forecast requested for today or earlier; not available");
      failDefunctWeatherTask(weather);
      return false;
    }
    today.add(Calendar.DATE, 4);
    if (today.before(onCal)) {
      //System.out.println("Forecasts for " + onDate + " are not yet available");
      return false;
    }    
    today.add(Calendar.DATE, -5);
    
    // Create an interactor and an interaction, and start the interaction
    RETSINAWeatherInteractor wxagent =
      new RETSINAWeatherInteractor(dep, ((AgentRep) weatherAgents.firstElement()),
                                   today.getTime().getTime(),
                                   onCal.getTime().getTime());
    Interaction wxInt = new Interaction(dep, wxagent, translatedPlace);
    //wxInt.setDebug(true);
    wxInt.start(false); // don't wait for answer to come back
    
    // Create a new allocation to track this forecast
    Asset forecaster = theLDMF.createPrototype("AbstractAsset", "forecaster");
    theLDM.cachePrototype("forecaster", forecaster);
    Asset asset = (Asset) theLDMF.createInstance("forecaster");
    NewTypeIdentificationPG tipg =
      (NewTypeIdentificationPG) theLDMF.createPropertyGroup("TypeIdentificationPG");
    tipg.setTypeIdentification(WConstants.FORECAST_ASSET_TYPE);
    asset.setTypeIdentificationPG(tipg);
    NewItemIdentificationPG iipg =
      (NewItemIdentificationPG) theLDMF.createPropertyGroup("ItemIdentificationPG");
    iipg.setItemIdentification(WConstants.AGENT_NAMES[WConstants.RETSINA_CNN_FORECAST_AGENT]);
    asset.setItemIdentificationPG(iipg);
    publishAdd(asset);
    int []aspect_types = {WConstants.VISIBILITY,
                          WConstants.PRECIPITATION,
                          WConstants.TRACTION,
                          WConstants.TURBULENCE};
    double []results = {10.0, 0.0, 1.0, 0.0};
    AllocationResult estAR =  theLDMF.newAllocationResult(1.0, 
                                true, aspect_types, results);
    Allocation alloc = theLDMF.createAllocation(weather.getPlan(), weather, asset,
                                                estAR, Role.ASSIGNED);
    /*
    String fc = alloc.getAsset().getItemIdentificationPG().getItemIdentification();
    GeolocLocationImpl l = (GeolocLocationImpl) alloc.getTask().getPrepositionalPhrase(Constants.Preposition.FOR).getIndirectObject();
    System.out.println("Publishing forecast allocation for " + l.getName() + 
                       " on " + onDate + " to " + fc +
                       " in plan " + weather.getPlan().getPlanName());
    */
    publishAdd(alloc);
    
    return true;
  }

  private void saveWeatherResult (ForecastConditions forecast) {
    //System.out.println("Saving " + forecast);
    
    String place = WConstants.translateRemoteToALP(forecast.place,
                                                   WConstants.RETSINA_CNN_FORECAST_AGENT);
    String onDate = WConstants.DATE_FORMAT.format(forecast.onDate.getTime());    
    PlanElementForAssessor alloc = findWeatherForecastAllocation(place, onDate);
    if ( alloc == null ) {
      System.err.println("Have weather forecast but no prior allocation!");
      return;
    }
    int []aspect_types = {WConstants.CONDITION,
                          WConstants.VISIBILITY,
                          WConstants.PRECIPITATION,
                          WConstants.TRACTION,
                          WConstants.TURBULENCE};
    double []results = WConstants.getRETSINAConditionValues(forecast.conditions);    
    AllocationResult receivedAR =  theLDMF.newAllocationResult(1.0, 
                                      true, aspect_types, results);
    alloc.setReceivedResult(receivedAR);
    AllocationResult estAR =  theLDMF.newAllocationResult(1.0, 
                                      true, aspect_types, results);
    alloc.setEstimatedResult(estAR);
    ((Allocation) alloc).setStale(true);
    publishChange(alloc);
  }
  
  private PlanElementForAssessor findWeatherForecastAllocation(String place, String onDate) {
    //System.out.println("Looking for forecast allocation for " + place + " on " + onDate);
    //System.out.println("Checking against " + allWeatherAllocations.getCollection().size() +
    //                   " allocations");
    Iterator allocs = allWeatherAllocations.getCollection().iterator();
    while ( allocs.hasNext() ) {
      PlanElement alloc = (PlanElement) allocs.next();
      Task weather = alloc.getTask();
      GeolocLocationImpl loc = 
        (GeolocLocationImpl) weather
                               .getPrepositionalPhrase(Constants.Preposition.FOR)
                                 .getIndirectObject();
      String taskDate = (String) weather
                                   .getPrepositionalPhrase(WConstants.FORECAST_DATE_PREPOSITION)
                                     .getIndirectObject();
      //System.out.println("Checking against " + loc.getName() + " on " + taskDate);                               
      if ( place.equals(loc.getName()) && onDate.equals(taskDate) )
        return (PlanElementForAssessor) alloc;
    }
    return null;
  }
  
  private void failDefunctWeatherTask (Task w) {
    MPTask mpt = (MPTask) w; // all of these tasks are multi-parent tasks
    Asset forecaster = theLDMF.createPrototype("AbstractAsset", "forecaster");
    theLDM.cachePrototype("forecaster", forecaster);
    Asset asset = (Asset) theLDMF.createInstance("forecaster");
    NewTypeIdentificationPG tipg =
      (NewTypeIdentificationPG) theLDMF.createPropertyGroup("TypeIdentificationPG");
    tipg.setTypeIdentification(WConstants.FORECAST_ASSET_TYPE);
    asset.setTypeIdentificationPG(tipg);
    NewItemIdentificationPG iipg =
      (NewItemIdentificationPG) theLDMF.createPropertyGroup("ItemIdentificationPG");
    iipg.setItemIdentification(WConstants.AGENT_NAMES[WConstants.RETSINA_CNN_FORECAST_AGENT]);
    asset.setItemIdentificationPG(iipg);
    publishAdd(asset);
    double []results = { WConstants.RETSINAConditionValues[0][0] };
    int []aspect_types = { WConstants.CONDITION };
    AllocationResult estAR = theLDMF.newAllocationResult(0.0, false, aspect_types, results);
    Allocation alloc = theLDMF.createAllocation(w.getPlan(), w, asset,
                                                estAR, Role.ASSIGNED);
    publishAdd(alloc);

    return;
  }
  
}
