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

import com.globalinfotek.coabsgrid.*;
import com.prc.alp.liaison.acl.*;
import com.prc.alp.liaison.admin.*;
import com.prc.alp.liaison.plugin.*;
import com.prc.alp.liaison.weather.*;
import java.util.*;
import java.text.SimpleDateFormat;


/**
 * This default interactor is used by the <CODE>CoABSDefaultInteractionFactory</CODE>.
 * It simply returns a "not-understood" message to the originator, unless the 
 * received message itself is a "not-understood" message from an external agent,
 * in which case it is simply ignored.
 *
 * @author Brandon L. Buteau
 * @version 1.1
 * @since 1.1
 * @see CoABSDefaultInteractionFactory
 */
public class CoABSDefaultInteractor implements CloneableInteractor {
  
  private CoABSLiaisonDeputy deputy = null;
  private ExternalAgentReference extRef = null;
  private AgentRep extAgent = null;
  private Message msg = null;
  private String state = null;
  
  public CoABSDefaultInteractor(CoABSLiaisonDeputy dep,AgentRep extAgent) {
    deputy = dep;
    ExternalAgentReference regRef = dep.externalManager();
    extRef = new ExternalAgentReference();
    extRef.society = regRef.society;
    extRef.neighborhood = regRef.neighborhood;
    extRef.agent = extAgent.getName();
    extRef.owner = "";
    extRef.description = "";
    try {
      extRef.externalID = extAgent.getServiceID().toString();
    } catch (Exception ex) {
      throw new RuntimeException("CoABSDefaultInteracot.<init>:\n" +
                                 "Failed to get ServiceID for remote agent\n" +
                                 ex.getMessage());
    }
    String myID = deputy.getLiaisonManager().getMyServiceID().toString();
    LiaisonStatusReference template = 
      LiaisonStatusReference.create(extRef, myID);
    LiaisonStatusReference LSR = 
      LiaisonStatusReference.createDefault(extRef, myID);
    LSR.isFromALP = new Boolean(true);
    LiaisonSpace lspace = new LiaisonSpace(deputy.getLiaisonManager().getMySpace(),
                                           deputy.getLiaisonManager().getMyFullName());
    try {
      lspace.writeIfNotFound(template, LSR);
    } catch (Exception ex) { /* don't care right now */ }
    this.extAgent = extAgent;
  }

  public synchronized Object begin(Object o, Interaction iaction)
    throws ActionTimeoutException, InteractionTimeoutException {
    msg = (Message) o;
    //System.out.println("beginning not-understood interaction with " + extRef);
    state = "RECEIVE";
    if ( !deputy.canRespondTo(extRef) ) {
      System.out.println("CoABSDefaultInteractor not allowed to respond to messages from " + extRef);
      if ( !deputy.waitForPermission() )
        { state = "ABORT"; System.out.println("Not allowed to wait either"); }
      else {
        do {
          System.out.println("CoABSDefaultInteractor waiting for permission");
          iaction.sleep(10000);
        } while ( !deputy.canRespondTo(extRef) );
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
      String rw = null; // fix to check for actual reply-with reference
      if ( rw == null || rw.equals("") || rw.equals("ALP-default-not-understood") )
        state = "END";
      else
        try {
          //System.out.println("Sending not-understood to " + extRef);
          extAgent.addMessage
            (new Message(extRef.agent, deputy.getGridAgentRep(), "default-language",
                         makeDefaultNotUnderstoodReply(iaction)));
          state = "END";
        } catch (Exception ex) {
          iaction.sleep(0);
          System.err.println("CoABSDefaultInteractor.send():\n" +
                             "Failed to add message to remote agent's queue\n" +
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
    return new CoABSDefaultInteractor(deputy, extAgent);
  }
  
  private String makeDefaultNotUnderstoodReply(Interaction ia) {
    String cname = ((CoABSAgentDescription) deputy.myCoABSAttributes()[0]).name;
    return 
      "(not-understood \n" +
        ":sender " + cname + "\n" +
        ":receiver " + extRef.agent + " \n" +
        ":content (" + msg.getRawText() + 
                  "\"Unacceptable communicative act for starting an interaction with " + cname + "\") \n" +
        ":reply-with ALP-default-not-understood \n" + 
        ":language sl \n" +
        ":ontology default-ontology)";
  }
    
}