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

import net.jini.core.entry.Entry;
import net.jini.core.lookup.ServiceID;
import com.globalinfotek.coabsgrid.*;
import com.prc.alp.liaison.LConstants;
import com.prc.alp.liaison.acl.*;
import com.prc.alp.liaison.admin.*;
import com.prc.alp.liaison.plugin.*;
import fipaos.parser.acl.ACLMessage;
import java.io.IOException;


/**
 * A manager of interactions involving registration with the CoABS Grid.
 * It supports the registration of ALP/Cougaar agents with the Grid's Jini
 * lookup service by supplying a CoABS-defined agent description.  Although
 * these registrations can be done without message passing, this implementation
 * uses an optional Grid feature to accept a confirming ACL message back from
 * the registry when the registration process is complete.  Because it accepts
 * incoming messages through a separate thread of control from the one that
 * initiates its interactions, this must be a <code>MultithreadInteractor</code>.
 *
 * @author Brandon L. Buteau
 * @version 1.1
 * @since 1.0
 */
public class CoABSRegistryInteractor implements MultithreadInteractor {
  
  private CoABSLiaisonDeputy deputy = null;
  private ExternalAgentReference regRef = null;
  private String state = null;
  
  public CoABSRegistryInteractor (CoABSLiaisonDeputy dep) {
    deputy = dep;
  }

  public synchronized Object begin(Object o, Interaction iaction)
    throws ActionTimeoutException, InteractionTimeoutException {
    regRef = (ExternalAgentReference) o;
    state = "SEND";
    /*
    ServiceID sid = deputy.getLiaisonManager().getMyServiceID();
    LiaisonStatusReference template = 
      LiaisonStatusReference.create(regRef, sid.toString());
    LiaisonStatusReference LSR = 
      LiaisonStatusReference.createDefault(regRef, sid.toString());
    LiaisonSpace lspace = new LiaisonSpace(deputy.getLiaisonManager().getMySpace(),
                                           deputy.getLiaisonManager().getMyFullName());
    try {
      lspace.writeIfNotFound(template, LSR);
    } catch (Exception ex) { } // don't care right now
    */
    if ( !deputy.canInitiateTo(regRef) ) {
      if ( !deputy.waitForPermission() )
        state = "ABORT";
      else {
        do {
          System.out.println("CoABSRegistryInteractor waiting for permission");
          iaction.sleep(10000);
        } while ( !deputy.canInitiateTo(regRef) );
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
      String name = deputy.getGridName();
      try {
        Entry[] gridEntries = deputy.myCoABSAttributes();
        ServiceID myID = LiaisonManager.loadServiceID(
          LConstants.CoABS.SERVICE_ID_FILE_PREFIX + name);
        GridAgentHelper gridHelper = deputy.getGridHelper();
        if ( myID != null ) {
          System.out.println(name + " recovered existing CoABS ServiceID:\n" + myID);
          gridHelper.registerServiceID(myID);
          deputy.getRegistry().registerAgent(gridHelper, gridEntries, myID);
          deputy.setServiceID(myID);
          state = "END";
        } else {
          deputy.getRegistry().registerAgent(gridHelper, gridEntries, 
                                             deputy.getGridAgentRep(),
                                             iaction.getID());
          deputy.putPending(iaction.getID(), iaction.getID());
          state = "RECEIVE";
        }

      } catch (Exception ex) {
        iaction.sleep(0);
        System.err.println("CoABSRegistryInteractor.send() failed to open connection to " +
                            deputy.externalManager().society + ":\n" + ex.getMessage());
        ex.printStackTrace();
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
    else {
      Object reply;
      while ( (reply = deputy.getLastReply(iaction.getID())) == null ) {
        // need to wait for Registry to respond with a message
        iaction.sleep(500);
      }
      ACLMessage amsg = (ACLMessage) reply;
      String content = (String) amsg.getContentObject();
      //System.out.println("Registry message content:\n" + content);
      RegistryMessageParser rparser = new RegistryMessageParser();
      // rparser.setDebug(true);
      String id = rparser.parse(content);
      //System.out.println("Retrieved my service ID: " + id);
      ServiceID sid = null;
      String name = deputy.getGridName();
      try {
        sid = deputy.getGridHelper().getServiceID();
      } catch (Exception ex) { 
        iaction.sleep(0);
      }
      if ( sid != null && sid.toString().equals(id) ) {
        deputy.setServiceID(sid);
        try {
          LiaisonManager.saveServiceID(
            LConstants.CoABS.SERVICE_ID_FILE_PREFIX + name, sid);
        } catch (IOException ex) {
          System.out.println("CoABSRegistryInteractor.receive():\n" +
                             "Failed to save new ServiceID file\n" + ex.getMessage());
          state = "ABORT";
        }
        System.out.println(name + " established new CoABS ServiceID:\n" + sid);
        state = "END";
      } else {
        System.err.println("CoABSRegistryInteractor.receive():\n" +
                           "Received incorrect registration acknowledgement");
        state = "ABORT";
      }
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
      return InteractionResult.NORMAL;
  }
  
  public synchronized void abnormalEnd(Object o, Interaction iaction)
    throws ActionTimeoutException, InteractionTimeoutException {
    iaction.sleep(0);
    System.err.println("CoABSRegistryInteractor reached an abnormal state");
  }
  
  public synchronized ActionType nextAction(Object o, Interaction iaction, boolean priorActExpired) {
    if ( o == null )
      return new ActionType("BEGIN");
    else
      return new ActionType((String) o);
  }
    
  public synchronized boolean handle (Object obj, Object context, Interaction iaction) {
    Message msg = (Message) obj;
    ExternalAgentReference sender = (ExternalAgentReference) context;
    String myLocatorRef = deputy.getLiaisonManager().getMyLocatorRef();
    if ( !sender.externalID.equals(myLocatorRef) )
      return false; // message is not from the registry;

    CoABSParser cparser = new CoABSParser();
    // cparser.setDebug(true);
    ACLMessage amsg = cparser.parseRaw(msg.getRawText(), myLocatorRef);
    String acl = amsg.getLanguage();
    if ( !acl.equals("SL0") ) {
      System.err.println("CoABSRegistryInteractor.handle():\n" +
                         "Received registration message in unexpected language " + acl);
      return true;
    }
    String replyID = amsg.getInReplyTo();
    if ( replyID == null ) {
      System.err.println("CoABSRegistryInteractor.handle():\n" +
                         "Received registration message without response key");
      return true;
    }
    if ( deputy.getPending(replyID) == null ) {
      // An unexpected registration confirmation for an unknown interaction
      deputy.removePending(replyID);
      System.err.println("CoABSRegistryInteractor.handle():\n" +
                         "Received unexpected registration confirmation");
      return true;
    }
    if ( ((String) deputy.getPending(replyID)).equals(iaction.getID()) ) {
      // A valid registration confirmation for this interaction
      deputy.removePending(replyID);
      deputy.saveReply(replyID, amsg);
      return true;
    }
    // Otherwise, it's a confirmation from a different interaction, so it needs
    // to be handled by that one
    return false;
  }
  
}