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

package com.prc.alp.liaison.interact;

//import net.jini.core.lookup.ServiceID;
import com.globalinfotek.coabsgrid.*;
import com.prc.alp.liaison.acl.*;
import com.prc.alp.liaison.admin.*;
import com.prc.alp.liaison.plugin.*;
import com.prc.alp.liaison.weather.*;
import java.util.*;
import java.text.SimpleDateFormat;


/**
 * An interactor that knows how to request weather information from selected
 * RETSINA weather InfoAgents.  Currently, this is used by a PlugIn that responds
 * to aggregated requirements for forecasts, the <CODE>WeatherLiaisonPlugIn</CODE>.
 * Weather forecasts for specific geographic locations and times are requested and 
 * returned as the final results of these interactions in <CODE>ForecastConditions</CODE> objects.
 *
 * @author Brandon L. Buteau
 * @version 1.1
 * @since 1.1
 * @see com.prc.alp.liaison.weather.ForecastConditions
 * @see com.prc.alp.liaison.weather.WeatherLiaisonPlugIn
 */
public class RETSINAWeatherInteractor implements MultithreadInteractor {
  
  private static String[] CNNDays = { "MON", "TUES", "WED", "THURS", "FRI", "SAT", "SUN" };
    
  
  private CoABSLiaisonDeputy deputy = null;
  private ExternalAgentReference weatherRef = null;
  private AgentRep weatherAgent = null;
  private GregorianCalendar asof = null;
  private GregorianCalendar onDate = null;
  private String place = null;
  private String state = null;
  
  public RETSINAWeatherInteractor (CoABSLiaisonDeputy dep,
                                    AgentRep weatherAgent,
                                    long executionTime,
                                    long forecastTime) {
    deputy = dep;
    ExternalAgentReference regRef = dep.externalManager();
    weatherRef = new ExternalAgentReference();
    weatherRef.society = regRef.society;
    weatherRef.neighborhood = regRef.neighborhood;
    weatherRef.agent = weatherAgent.getName();
    weatherRef.owner = "";
    weatherRef.description = "";
    try {
      weatherRef.externalID = weatherAgent.getServiceID().toString();
    } catch (Exception ex) {
      throw new RuntimeException("RETSINAWeatherInteractor constructor:\n" +
                                 "Failed to get ServiceID for remote agent\n" +
                                 ex.getMessage());
    }
    String myID = deputy.getLiaisonManager().getMyServiceID().toString();
    LiaisonStatusReference template = 
      LiaisonStatusReference.create(weatherRef, myID);
    LiaisonStatusReference LSR = 
      LiaisonStatusReference.createDefault(weatherRef, myID);
    LSR.isFromALP = new Boolean(true);
    LiaisonSpace lspace = new LiaisonSpace(deputy.getLiaisonManager().getMySpace(),
                                           deputy.getLiaisonManager().getMyFullName());
    try {
      lspace.writeIfNotFound(template, LSR);
    } catch (Exception ex) { /* don't care right now */ }
    this.weatherAgent = weatherAgent;
    String zone = System.getProperty("user.timezone","GMT");
    SimpleTimeZone tz = new SimpleTimeZone(0, zone);
    tz.setID(zone);
    asof = new GregorianCalendar(tz);
    asof.setTime(new Date(executionTime));
    onDate = new GregorianCalendar(tz);
    onDate.setTime(new Date(forecastTime));
  }

  public synchronized Object begin(Object o, Interaction iaction)
    throws ActionTimeoutException, InteractionTimeoutException {
    place = (String) o;
    //System.out.println("beginning interaction about " + place);
    state = "SEND";
    if ( !deputy.canInitiateTo(weatherRef) ) {
      System.out.println("Not allowed to initiate weather query");
      if ( !deputy.waitForPermission() )
      { state = "ABORT"; System.out.println("Not allowed to wait either"); }
      else {
        do {
          System.out.println("RETSINAWeatherInteractor waiting for permission");
          iaction.sleep(10000);
        } while ( !deputy.canInitiateTo(weatherRef) );
      }
    }
    return state;
  }
  
  public synchronized Object send(Object o, Interaction iaction)
    throws ActionTimeoutException, InteractionTimeoutException {
    state = (String) o;
    if ( !state.equals("SEND") )
      state = "ABORT";
    else
      try {
        //System.out.println("Sending request about " + place + " as of " + 
        //                    WConstants.DATE_FORMAT.format(asof.getTime()));
        weatherAgent.addMessage
          (new Message(weatherRef.agent, deputy.getGridAgentRep(), "default-language",
                       makeRETSINAWeatherRequest(iaction, place, asof)));
        deputy.putPending(iaction.getID(), iaction.getID());
        state = "RECEIVE";
      } catch (Exception ex) {
        iaction.sleep(0);
        System.err.println("RETSINAWeatherInteractor.send():\n" +
                           "Failed to add message to weather agent's queue\n" +
                           ex.getMessage());
        state = "ABORT";
      }
    return state;
  }
  
  public synchronized Object receive(Object o, Interaction iaction)
    throws ActionTimeoutException, InteractionTimeoutException {
    state = (String) o;
    if ( !state.equals("RECEIVE") )
      state = "ABORT";
    else {
      Object reply;
      while ( (reply = deputy.getLastReply(iaction.getID())) == null )
        // need to wait for weather agent to respond with a message
        iaction.sleep(500);
      RETSINAWeatherMessageParser parser = new RETSINAWeatherMessageParser();
      //parser.setDebug(true);
      Object forecast = parser.parseForecast((String) reply);
      //System.out.println("RETSINAWeatherInteractor saving Vector " + forecast);
      if ( forecast instanceof String ) {
        forecast = "Forecast unavailable for " +
                   place + ": " + (String) forecast;
        deputy.saveReply(iaction.getID(), forecast);
      } else {
        int diff = (int) (( onDate.getTime().getTime() - asof.getTime().getTime() ) /
                          ( 1000 * 60 * 60 * 24 )); // convert to duration in days
        Vector f = (Vector) forecast;
        if ( diff < 1 || diff >= f.size() ) {
          deputy.saveReply(iaction.getID(),
                           "Forecast data missing for " + place + 
                           " on date " + WConstants.DATE_FORMAT.format(onDate.getTime()));
        } else {
          ForecastConditions result = new ForecastConditions();
          //System.out.println("Received forecast for " + place + " as of " +
          //                   WConstants.DATE_FORMAT.format(asof.getTime()));
          result.place = place;
          result.onDate = onDate;
          result.asOfDate = asof;
          result.conditions = (String) f.elementAt(diff);
          deputy.saveReply(iaction.getID(), result);
        }
      }
      state = "END";
    }
    return state;
  }
  
  public synchronized Object normalEnd(Object o, Interaction iaction)
    throws ActionTimeoutException, InteractionTimeoutException {
    iaction.sleep(0);
    state = (String) o;
    if ( !state.equals("END") )
      return InteractionResult.ERROR;
    else
      return deputy.getLastReply(iaction.getID());
  }
  
  public synchronized void abnormalEnd(Object o, Interaction iaction)
    throws ActionTimeoutException, InteractionTimeoutException {
    iaction.sleep(0);
    System.err.println(iaction.getID() + " reached an abnormal state");
  }
  
  public synchronized ActionType nextAction(Object o, Interaction iaction, boolean priorActExpired) {
    if ( o == null )
      return new ActionType("BEGIN");
    else if ( priorActExpired )
      return new ActionType("ABORT");
    else
      return new ActionType((String) o, 5000);
  }
  
  public boolean handle(Object obj, Object context, Interaction iaction) {
    Message msg = (Message) obj;
    ExternalAgentReference sender = (ExternalAgentReference) context;
    if ( !sender.agent.equals("CNNCurrentWeatherAgent") &&
         !sender.agent.equals("CNNForecastWeatherAgent") &&
         !sender.agent.equals("WCNCurrentWeatherAgent") &&
         !sender.agent.equals("WCNForecastWeatherAgent") ) {
      return false; // message is not from a RETSINA weather agent;
    }
    //System.out.println("RETSINAWeatherInteractor received reply from " + sender.agent);
    RETSINAWeatherMessageParser parser = new RETSINAWeatherMessageParser();
    String replyID = parser.extractReplyID(msg.getRawText());
    //System.out.print("Interaction ID = " + iaction.getName() + "\n" +
    //                   "      Reply ID = " + replyID +" ... ");
    if ( iaction.getID().equals((String) deputy.getPending(replyID)) ) {
      // A valid weather response for this interaction
      //System.out.println("they match");
      deputy.removePending(replyID);
      deputy.saveReply(replyID, msg.getRawText());
      return true;
    }
    // Otherwise, it's a response from a different interaction, so it needs
    // to be handled by that one
    //System.out.println("they don't match");
    return false;    
  }
  
  private String makeRETSINAWeatherRequest(Interaction ia, String location, 
                                           GregorianCalendar asof) {
    String yearday = WConstants.DATE_FORMAT.format(asof.getTime());
    //System.out.println("Getting weather for " + location + " as of " +
    //                    yearday);
    return 
      "(ask-one \n" +
        ":sender " + ((CoABSAgentDescription) deputy.myCoABSAttributes()[0]).name + "\n" +
        ":receiver " + weatherRef.agent + " \n" +
        ":content (objective :name \"getInformation\" \n" +
        ":parameters (listof (pval \"primary-keys\" \"(" + location + 
            "/" + yearday + ")\"))) \n" +
        ":reply-with " + ia.getID() + " \n" +
        ":in-reply-to \n" +
        ":language default-language \n" +
        ":ontology default-ontology)";
  }
  
  private String testRETSINAReply() {
    return
      "(REPLY \n" +
      ":language default-language \n" +
      ":reply-with \n" +
      ":client InterOp \n" +
      ":localport 2965 \n" +
      ":info (objective :name \"getInformation\" \n" +
              ":parameters (listof (pval \"primary-keys\" \"(CairoEgypt/CairoEgypt)\"))) \n" +
      ":receiver InterOp \n" +
      ":ontology default-ontology \n" +
      ":connection-name InterOp**WeatherCNNAgent_ALPVersion**2965 \n" +
      ":content (reply :city (CairoEgypt/CairoEgypt) \n" +
                      ":time (Updated 03:56pm local, 1356 GMT) \n" +
                      ":weather-url (file:///d:\\alp\\dv1\\com\\prc\\data\\html\\retsina\\CairoEgypt/CairoEgypt.html) \n" +
                      ":weather (weather :conditions (mostly sunny) \n" +
                      ":temperature (81 F, 27 C) \n" +
                      ":humidity (47%))) \n" +
      ":in-reply-to KMS084477hopper \n" +
      ":server WeatherCNNAgent_ALPVersion \n" +
      ":sender WeatherCNNAgent_ALPVersion)";
  }
  
}