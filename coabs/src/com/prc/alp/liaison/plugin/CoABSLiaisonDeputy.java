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

package com.prc.alp.liaison.plugin;

import org.cougaar.core.plugin.PlugInDelegate;
import com.globalinfotek.coabsgrid.*;
import com.globalinfotek.coabsgrid.fipa98.*;
import com.isx.coabsgrid.loggerclient.LogAgentRepInterface;
import fipaos.parser.acl.ACLMessage;
import fipaos.ont.fipa.fipaman.AgentManagementAction;
import net.jini.core.entry.Entry;
import net.jini.core.lookup.ServiceID;
import java.util.*;
import com.prc.alp.liaison.LConstants;
import com.prc.alp.liaison.acl.*;
import com.prc.alp.liaison.admin.*;
import com.prc.alp.liaison.interact.*;

/** The CoABSLiaisonDeputy class manages interactions with
 * an external society of agents through a CoABS Grid.
 */

public class CoABSLiaisonDeputy
  extends LiaisonDeputy
  implements MessageListener {
  
  public static boolean DEBUG = false;

  private GridAgentHelper gridHelper;
  private MessageQueue gridMessages;
  private Entry[] gridEntries;
  private RegistryInterface gridRegistry;
  private String registryName;
  private String societyName;
  private AgentRepInterface gridAgentRep;
  private LogAgentRepInterface logAgent;
  private ServiceID myID;
  private String myGridName;
  private String coabsLocatorRef;
  private boolean useCoABSLogger = false;

  public CoABSLiaisonDeputy (LiaisonManager manager, 
                             PlugInDelegate pid,
                             Vector parameters) {
    super(manager, pid, parameters);
    String log = LiaisonManager.findLiaisonParameter(parameters, 
                                                     LConstants.CoABS.LOG_PARAMETER);
    if ( log == null )
      log = System.getProperty(LConstants.CoABS.LOG_PROPERTY);
    if ( log != null && log.equals(LConstants.CoABS.USE_LOGGER) )
      useCoABSLogger = true;
    else
      useCoABSLogger = false;
    registryName = LConstants.CoABS.DEFAULT_REGISTRY_NAME; // for now
    societyName = LiaisonManager.findLiaisonParameter(parameters,
                                                      LConstants.CoABS.SOCIETY_PARAMETER);
    if ( societyName == null )
      societyName = System.getProperty(LConstants.CoABS.SOCIETY_NAME_PROPERTY,
                                       LConstants.CoABS.DEFAULT_GRID_NAME);
    coabsLocatorRef = LiaisonManager.findLiaisonParameter(parameters,
                                                          LConstants.CoABS.LOCATOR_PARAMETER);
    if (coabsLocatorRef == null) {
      coabsLocatorRef = System.getProperty(LConstants.CoABS.LOCATOR_PROPERTY);
      if ( coabsLocatorRef == null )
        throw new RuntimeException("CoABSLiaisonDeputy:\n" +
                                   "Unable to identify CoABS Jini locator from parameters or properties");
    }
  }

  public ExternalAgentReference openInternal () {
    try {
      System.out.println("\nAttempt to contact " + societyURI + " (" + coabsLocatorRef + ") by " +
                         manager.getMyFullName());
      CoabsgridConfig.init();
      myGridName = manager.getMyFullName();
      gridHelper = new GridAgentHelper(myGridName);
      if ( !useCoABSLogger )
        gridHelper.turnOffAutoLog();
      gridHelper.addLookupLocators(coabsLocatorRef);
      gridAgentRep = gridHelper.getAgentRep();
      gridMessages = gridHelper.getMessageQueue();
      gridMessages.addMessageListener(this);
      gridRegistry = gridHelper.getRegistry();
    } catch (Exception ex) {
      System.err.println("CoABSLiaisonDeputy.openInternal():\n" +
                         "Failed to initialize properly:\n" + ex.getMessage());
      return null;
    }
    return ExternalAgentReference.create(societyName, "", registryName, "", "",
                                         coabsLocatorRef);
  }
  
  public ServiceID getServiceID () {
    return myID;
  }
  
  public void setServiceID (ServiceID id) {
    myID = id;
    return;
  }
  
  public AgentRepInterface getGridAgentRep () {
    return gridAgentRep;
  }
  
  public GridAgentHelper getGridHelper () {
    return gridHelper;
  }
  
  public RegistryInterface getRegistry () {
    return gridRegistry;
  }
  
  public String getGridName () {
    return myGridName;
  }
    
  public Entry[] myCoABSAttributes() {
    Entry[] e = new Entry[1];

    CoABSAgentDescription desc = new CoABSAgentDescription(myGridName);
    String[] strings = new String[1];
    strings[0] = "logistics";
    desc.ontologies = strings;
    desc.organization = manager.getMySociety();
    desc.description = "An ALP society agent (society:" +
                       manager.getMySociety() + " community:" +
                       manager.getMyCommunity() + " cluster:" +
                       manager.getMyName() + ")";
    e[0] = desc;
    return e;
  }

  public void messageAdded (Message cmsg) {
    
    if ( DEBUG ) {
      System.out.println("Received CoABS Grid message:");
      dumpMessage(cmsg);
    }
    
    ExternalAgentReference sender = getSender(cmsg);
    if ( sender == null  && DEBUG )
      System.out.println("Unable to extract sender agent reference");
    else if ( DEBUG )
      System.out.println("Sender agent is " + sender);
    Enumeration e = interactions.elements();
    boolean msgHandled = false;
    while ( e.hasMoreElements() && !msgHandled ) {
      Interaction in = (Interaction) e.nextElement();
      Interactor ia = in.getInteractor();
      Class c = null;
      try {
        c = Class.forName("com.prc.alp.liaison.interact.MultithreadInteractor");
      } catch (ClassNotFoundException ex) { /* better not happen! */ }
      if ( c.isInstance(ia) ) {
        MultithreadInteractor mtia = (MultithreadInteractor) ia;
        msgHandled = mtia.handle(cmsg, sender, in);
        if ( msgHandled && DEBUG )
          System.out.println("Message handled by " + in.getID());
      }
    }
    if ( !msgHandled ) {
      e = factories.elements();
      while ( e.hasMoreElements() && !msgHandled ) {
        InteractionFactory fct = (InteractionFactory) e.nextElement();
        Interaction in = fct.createInteraction(cmsg);
        if ( in != null ) {
          in.setDebug(fct.getDebug());
          in.start(false);
          msgHandled = true;
        }
      }
    }
    if ( !msgHandled ) {
      System.out.println("CoABSLiaisonDeputy.addMessage():\n" +
                         "Unable to find or create interaction for message");
      dumpMessage(cmsg);
    }
    gridMessages.remove(cmsg);
  }
  
  public ExternalAgentReference getSender (Message msg) {
    String acl = msg.getACL();
    String raw = msg.getRawText();
    ServiceRepInterface senderRep = msg.getSenderServiceRep();
    if ( senderRep != null ) {
      ServiceID sid = null;
      try {
        sid = senderRep.getServiceID();
      } catch (Exception ex) { /* don't care what it is */ }
      if ( sid != null )
        return ExternalAgentReference.create(societyName, "", senderRep.getName(),
                                            "", "", sid.toString());
    } else if ( acl == "FIPA-ACL" ) {
      int rpos = raw.indexOf(":sender ");
      if ( rpos < 0 ) {
        System.err.println("CoABSLiaisonDeputy.getSender():\n" +
                           "Received bad FIPA-ACL message:\n" + raw);
        return null;
      }
      rpos = rpos + 8;
      String sender = raw.substring(rpos, raw.indexOf(' ', rpos) - 1);
      return ExternalAgentReference.create(societyName, "", sender, "", "",
                                           manager.getMyLocatorRef());
    }
    return null;
  }

  public void dumpMessage (Message msg) {
    System.out.println("Message ACL = " + msg.getACL());
    ServiceRepInterface senderRep = msg.getSenderServiceRep();
    String sender = (senderRep == null) ? "<unknown>" : senderRep.getName();
    System.out.println("Message sender = " + sender);
    System.out.println("Message receiver = " + msg.getReceiver());
    System.out.println("Message raw text:\n" + msg.getRawText());
  }
    
}
