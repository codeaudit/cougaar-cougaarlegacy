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
 * This default interactor is used by the <CODE>RETSINAUnexpectedWxInteractionFactory</CODE>.
 * It simply returns a "not-understood" message to the originator, unless the 
 * received message itself is a "not-understood" message from a RETSINA InfoAgent,
 * in which case it is simply ignored.  The typical reason an unexpected message was
 * received is because a prior interaction timed out, and the RETSINA agent is still
 * sending back results.  The <CODE>CoABSLiaisonDeputy</CODE> interprets these late
 * results as attempts to start a new interaction by the external agent.
 *
 * @author Brandon L. Buteau
 * @version 1.1
 * @since 1.1
 * @see RETSINAUnexpectedWxInteractionFactory
 * @see com.prc.alp.liaison.plugin.CoABSLiaisonDeputy
 */
public class RETSINAUnexpectedWxInteractor implements CloneableInteractor {
  
  private CoABSLiaisonDeputy deputy = null;
  private ExternalAgentReference weatherRef = null;
  private AgentRep weatherAgent = null;
  private Message msg = null;
  private String state = null;
  
  public RETSINAUnexpectedWxInteractor(CoABSLiaisonDeputy dep, AgentRep weatherAgent) {
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
      throw new RuntimeException("RETSINAUnexpectedWxInteractor constructor:\n" +
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
  }

  public synchronized Object begin(Object o, Interaction iaction)
    throws ActionTimeoutException, InteractionTimeoutException {
    msg = (Message) o;
    //System.out.println("beginning not-understood interaction with " + weatherRef);
    state = "RECEIVE";
    if ( !deputy.canRespondTo(weatherRef) ) {
      System.out.println("Not allowed to respond to weather messages");
      if ( !deputy.waitForPermission() )
        { state = "ABORT"; System.out.println("Not allowed to wait either"); }
      else {
        do {
          System.out.println("RETSINAUnknownWxInteractor waiting for permission");
          iaction.sleep(10000);
        } while ( !deputy.canRespondTo(weatherRef) );
      }
    }
    return state;
  }
  
  public synchronized Object send(Object o, Interaction iaction)
    throws ActionTimeoutException, InteractionTimeoutException {
    state = (String) o;
    if ( !state.equals("SEND") )
      state = "ABORT";
    else {
      RETSINAWeatherMessageParser parser = new RETSINAWeatherMessageParser();
      String rw = parser.extractReplyWith(msg.getRawText());
      if ( rw == null || rw.equals("") || rw.equals("No-reply-expected") )
        state = "END";
      else
        try {
          //System.out.println("Sending not-understood to " + weatherRef);
          String replyID = parser.extractReplyID(msg.getRawText());
          String originalContent = parser.extractContent(msg.getRawText());
          weatherAgent.addMessage
            (new Message(weatherRef.agent, deputy.getGridAgentRep(), "default-language",
                         makeRETSINANotUnderstoodReply(iaction, replyID, originalContent)));
          state = "END";
        } catch (Exception ex) {
          iaction.sleep(0);
          System.err.println("RETSINAUnexpectedWxInteractor.send():\n" +
                             "Failed to add message to weather agent's queue\n" +
                             ex.getMessage());
          state = "ABORT";
        }
    }
    return state;
  }
  
  public synchronized Object receive(Object o, Interaction iaction)
    throws ActionTimeoutException, InteractionTimeoutException {
    state = (String) o;
    if ( !state.equals("RECEIVE") )
      state = "ABORT";
    else      
      state = "SEND";
    return state;
  }
  
  public synchronized Object normalEnd(Object o, Interaction iaction)
    throws ActionTimeoutException, InteractionTimeoutException {
    iaction.sleep(0);
    state = (String) o;
    if ( !state.equals("END") )
      return InteractionResult.ERROR;
    else
      return InteractionResult.NORMAL;
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
      return new ActionType((String) o);
  }
  
  public boolean handle(Object obj, Object context, Interaction iaction) {
    return false;    
  }
  
  public Object clone() {
    return new RETSINAUnexpectedWxInteractor(deputy, weatherAgent);
  }
  
  private String makeRETSINANotUnderstoodReply(Interaction ia, String replyID, String original) {
    return 
      "(reply \n" +
        ":sender " + ((CoABSAgentDescription) deputy.myCoABSAttributes()[0]).name + "\n" +
        ":receiver " + weatherRef.agent + " \n" +
        ":content (sorry :reason (timeout :content " + original + ")) \n" +
        ":reply-with No-reply-expected \n" + 
        ":in-reply-to " + replyID + " \n" +
        ":language default-language \n" +
        ":ontology default-ontology)";
  }
    
}