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
import com.prc.alp.liaison.*;
import com.prc.alp.liaison.admin.*;
import com.prc.alp.liaison.interact.*;
import net.jini.space.JavaSpace;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Vector;

/**
 * Mediates interactions with an external society of agents.  Each independently-
 * threaded deputy has a number of responsibilities:
 * <ul>
 * <li>opening contact with an external society.  This facility must be supplied
 * by an implementation of this abstract class.</li>
 * <li>maintaining information relevant to active interactions with that society</li> 
 * <li>checking for possible timeouts associated with active interactions</li>
 * <li>checking the status of liaison permissions in the administration JavaSpace to
 * determine what interactions are allowable.</li>
 * </ul>
 *
 * @author Brandon L. Buteau
 * @version 1.1
 * @since 1.0
 */

public abstract class LiaisonDeputy implements Runnable {
    
  protected LiaisonManager manager = null;
  protected PlugInDelegate pid = null;
  protected Thread depThread = null;
  protected String societyURI = null;
  protected Vector parameters = null;
  protected ExternalAgentReference extManager = null;
  protected Hashtable interactions = null;
  protected Hashtable factories = null;
  protected Hashtable pendingReplies = null;
  protected Hashtable receivedReplies = null;
  protected Hashtable finalResults = null;
  private boolean waitForPermission = false;
  private long interactionTimeLimit = LConstants.DEFAULT_INTERACTION_TIME_LIMIT;
  private boolean active = false;
  
  public LiaisonDeputy(LiaisonManager manager, PlugInDelegate pid, Vector parameters) {
    this.manager = manager;
    this.pid = pid;
    this.parameters = parameters;
    depThread = new Thread(this, manager.getMyFullName() + "_CoABSDeputy");
    interactions = new Hashtable();
    factories = new Hashtable();
    pendingReplies = new Hashtable();
    receivedReplies = new Hashtable();
    finalResults = new Hashtable();
    String check = LiaisonManager.findLiaisonParameter(parameters, 
                                                       LConstants.ALP.WAIT_PARAMETER);
    if ( check == null )
      check = System.getProperty(LConstants.ALP.WAIT_PROPERTY);
    if ( check != null && check.equals(LConstants.ALP.WAIT_ALLOWED) )
      waitForPermission = true;
    else
      waitForPermission = false;
    String timeLimit = LiaisonManager.findLiaisonParameter(parameters,
                                                           LConstants.ALP.TIME_LIMIT_PARAMETER);
    if ( timeLimit == null )
      timeLimit = System.getProperty(LConstants.ALP.TIME_LIMIT_PROPERTY);
    if ( timeLimit != null )
      interactionTimeLimit = (new Long(timeLimit)).longValue();
  }
  
  public final void start(boolean wait) {
     start("", null, wait);
  }
  
  public final void start(String societyURI, Vector parameters, boolean wait) {
    this.societyURI = societyURI;
    this.parameters = parameters;
    depThread.start();
    if (wait)
      while (!active)
        try {
          Thread.sleep(1000);
        } catch (InterruptedException ex) { }
  }
  
  public final void run() {
    while ( !manager.isEnabled() )
      try {
        Thread.sleep(5000);
      } catch (InterruptedException ex) { }
    extManager = open();
    if ( extManager != null ) {
      active = true;
      Thread me = Thread.currentThread();
      while (me == depThread) {
        try {
          Thread.sleep(1000);
        } catch (InterruptedException ex) { }
        /* check interaction time limits */
        Enumeration e = interactions.elements();
        while ( e.hasMoreElements() ) {
          Interaction ia = (Interaction) e.nextElement();
          long now = System.currentTimeMillis();
          if ( !ia.isActive() )
            continue;
          if ( now - ia.getStartTime() > interactionTimeLimit )
            stopInteraction(ia.getID());
          else
            ia.tryToExpireAction();
        }
      }
    }
    active = false;
  }

  public final void stop() {
    depThread = null;
    Enumeration keys = interactions.keys();
    while ( keys.hasMoreElements() ) {
      String key = (String) keys.nextElement();
      stopInteraction(key);
    }
  }
  
  public final boolean isActive() {
    return active;
  }
  
  public final LiaisonManager getLiaisonManager () {
    return manager;
  }
  
  public final PlugInDelegate getPlugInDelegate() {
    return pid;
  }
  
  public final ExternalAgentReference externalManager() {
    return extManager;
  }
  
  public final ExternalAgentReference open () {
    
    extManager = openInternal();
    
    if ( extManager != null ) {
      LiaisonStatusReference template =
        LiaisonStatusReference.create(extManager,
                                      manager.getMyServiceID().toString());
      LiaisonStatusReference newLSR =
        LiaisonStatusReference.createDefault(extManager,
                                             manager.getMyServiceID().toString());
      newLSR.isFromALP = new Boolean(true);
      LiaisonSpace lspace = new LiaisonSpace(manager.getMySpace(),
                                             manager.getMyFullName());
      boolean written = false;
      //System.out.print("Writing " + newLSR + " if " + template + " not found...");
      try {
        written = lspace.writeIfNotFound(template, newLSR);
        /*
        if ( written )
          System.out.println("wrote it out");
        else
          System.out.println("already there");
        */
      } catch (Exception ex) {
        System.err.println("LiaisonDeputy.open():\n" +
                           "Failed to create new liaison status reference\n" +
                           ex.getMessage());
        return null;
      }
      if ( written )
        System.out.println("LiaisonDeputy created new LSR");
    }
    return extManager;
  }
  
  public final boolean canInitiateTo (ExternalAgentReference ext) {
    if ( ext == null )
      return false;
    LiaisonStatusReference template =
      LiaisonStatusReference.create(ext, manager.getMyServiceID().toString());
    JavaSpace space = manager.getMySpace();
    LiaisonStatusReference status = null;
    try {
      status =
        (LiaisonStatusReference) space.read(template, null, 10000);
    } catch (Exception ex) {
      /* System.err.println("LiaisonDeputy.canInitiateTo():\n" +
                         "Failed to read liaison status for:\n" +
                         ext + "\n" +  ex.getMessage()); */
    }    
    if ( status != null && status.canInitiate() )
      return true;
    return false;
  }
  
  public final boolean canRespondTo (ExternalAgentReference ext) {
    if ( ext == null )
      return false;
    LiaisonStatusReference template =
      LiaisonStatusReference.create(ext, manager.getMyServiceID().toString());
    JavaSpace space = manager.getMySpace();
    LiaisonStatusReference status = null;
    try {
      status =
        (LiaisonStatusReference) space.read(template, null, 10000);
    } catch (Exception ex) {
      /*System.err.println("LiaisonDeputy.canRespondTo():\n" +
                         "Failed to read liaison status for:\n" +
                         ext + "\n" +  ex.getMessage()); */
    }    
    if ( status != null && status.canRespond() )
      return true;
    return false;
  }
  
  public boolean waitForPermission () {
    return waitForPermission;
  }
  
  public Enumeration getInteractions() {
    return interactions.elements();
  }
  
  public Interaction getInteraction( String key ) {
    return (Interaction) interactions.get(key);
  }
  
  public Interaction putInteraction ( String key, Interaction ia ) {
    return (Interaction) interactions.put(key, ia);
  }
  
  public Interaction removeInteraction ( String key ) {
    pendingReplies.remove(key);
    receivedReplies.remove(key);
    return (Interaction) interactions.remove(key);
  }
  
  public boolean stopInteraction ( String key ) {
    Interaction ia = (Interaction) interactions.remove(key);
    if ( ia == null )
      return false;
    ia.stop();
    return true;
  }
  
  public Object finishInteraction (String key) {
    Interaction ia = (Interaction) interactions.remove(key);
    return finalResults.remove(key);
  }
  
  public Object finishInteraction (Interaction ia) {
    interactions.remove(ia.getID());
    return finalResults.remove(ia.getID());
  }
  
  public Enumeration getFactories() {
    return factories.elements();
  }
  
  public InteractionFactory getFactory ( String key ) {
    return (InteractionFactory) factories.get(key);
  }
  
  public void putFactory ( String key, InteractionFactory f ) {
    factories.put(key, f);
  }
  
  public InteractionFactory removeFactory ( String key ) {
    return (InteractionFactory) factories.remove(key);
  }
  
  public Object getPending ( String key ) {
    return pendingReplies.get(key);
  }
  
  public void putPending ( String key, Object o ) {
    pendingReplies.put(key, o);
    return;
  }
  
  public Object removePending ( String key ) {
    return pendingReplies.remove(key);
  }
  
  public Vector getReplies ( String key ) {
    return (Vector) receivedReplies.get(key);
  }
  
  public Object getLastReply ( String key ) {
    Vector replies = (Vector) receivedReplies.get(key);
    if ( replies == null )
      return null;
    return replies.lastElement();
  }
    
  public void saveReply (String key, Object o) {
    Vector v = (Vector) receivedReplies.get(key);
    if ( v == null )
      v = new Vector();
    v.add(o);
    receivedReplies.put(key, v);
  }
  
  public void saveResult (String key, Object o) {
    finalResults.put(key, o);
  }
  
  protected abstract ExternalAgentReference openInternal ();

}
