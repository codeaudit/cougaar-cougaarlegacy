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

import org.cougaar.core.cluster.ClusterServesPlugIn;
import org.cougaar.core.plugin.PlugInDelegate;
import org.cougaar.util.ConfigFinder;
import net.jini.discovery.DiscoveryEvent;
import net.jini.discovery.DiscoveryChangeListener;
import net.jini.discovery.LookupDiscoveryManager;
import net.jini.core.discovery.LookupLocator;
import net.jini.core.entry.Entry;
import net.jini.core.event.RemoteEvent;
import net.jini.core.lookup.ServiceID;
import net.jini.space.JavaSpace;
import net.jini.lookup.entry.Name;
import net.jini.lookup.entry.ServiceInfo;
import net.jini.lookup.JoinManager;
import net.jini.lookup.ServiceIDListener;
import java.io.File;
import java.io.InputStream;
import java.io.IOException;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.rmi.RMISecurityManager;
import java.util.Enumeration;
import java.util.Vector;
import com.prc.alp.liaison.LConstants;
import com.prc.alp.liaison.admin.*;
import com.prc.alp.liaison.util.*;

/**
 * Supports the establishment of contact and the administration of interactions
 * between a PlugIn and agents in some external agent society.  <CODE>LiaisonManager</CODE>
 * instances are the focal object for representing a PlugIn as a single agent
 * for purposes of liaison permission administration.
 *
 * @author Brandon L. Buteau
 * @version 1.1
 * @since 1.0
 */

public class LiaisonManager 
  implements Runnable, LiaisonTrigger {
  
  private static String nameSep = ".";
  private static String idExt = ".sid";
  private static String parameterMarker = "="; // separates param names and values
  private ServiceID myID = null;
  private String myName = null;
  private String myCommunity = null;
  private String mySociety = null;
  private String locatorRef = null;
  private Thread mgrThread = null;
  private JavaSpace mySpace = null;
  private LiaisonSpaceAdapter spaceHelper = null;
  private ALPAgentReference me = null;
  private ALPAgentReference statusRef = null;
  protected JoinManager join = null;
  protected LiaisonManagerProxy proxy = null;
  
  public static ServiceID loadServiceID (String prefixedName) {
    ServiceID id = null;
    ConfigFinder cf = new ConfigFinder(System.getProperty(LConstants.ALP.CONFIG_PATH_PROPERTY) + ";");
    File f = cf.locateFile(prefixedName + idExt);
    if ( f != null) {
      try {
        InputStream is = cf.open(prefixedName + idExt);
        ObjectInputStream ois = new ObjectInputStream(is);
        Object o = ois.readObject();
        if ( !(o instanceof ServiceID) )
          System.err.println("LiaisonManager.loadServiceID():\n" +
                             "Invalid ServiceID file\n");
        else {
          id = (ServiceID) o;
          is.close();
        }
      } catch (Exception ex) {
        System.err.println("LiaisonManager.loadServiceID():\n" +
                           "Could not find or loading ServiceID file\n" +
                           ex.getMessage());
      }
    }
    return id;
  }
  
  public static void saveServiceID (String prefixedName, ServiceID  id) 
    throws IOException {
    URL idURL = null;
    File ipf = new File(System.getProperty(LConstants.ALP.CONFIG_PATH_PROPERTY));
    try { 
      idURL = ipf.toURL();
      idURL = new URL(idURL, prefixedName + idExt);
    } catch (IOException ex) {
      System.err.println("LiaisonManager.saveServiceID():\n" +
                         "Problem getting path for ServiceID file\n" +
                         ex.getMessage());
      throw ex;
    }
    File idFile = new File(idURL.getFile());
    try {
      ObjectOutputStream out = new ObjectOutputStream(
                                    new FileOutputStream(idFile));
      out.writeObject(id);
      out.flush();
      out.close();
    } catch (IOException ex) {
      System.err.println("LiaisonManager.saveServiceID():\n" +
                         "Problem opening or writing ServiceID file\n" +
                         ex.getMessage());
      throw ex;
    }
  }
  
  public static String findLiaisonParameter(Vector params, String pName) {
    Enumeration e = params.elements();
    while (e.hasMoreElements()) {
      String param = (String) e.nextElement();
      if (param.indexOf(pName + parameterMarker) == 0)
        return param.substring(pName.length()+1);
    }
    return null;
  }
  /**
   * A constructor for LiaisonManager that must be called from a PlugIn
   *
   * @param cluster a PlugInAdapter identifying the caller as an agent representing a particular ALP cluster.
   * @param community a String identifying the ALP community to which
   * the caller belongs.
   * @param societyURI a URI String identifying the ALP society to which
   * the caller belongs.
   * @param locatorRef A URL for the Jini lookup service to search for a
   * JavaSpace service through which liaison permissions
   * will be administered
   */
  public LiaisonManager (ClusterServesPlugIn cluster,
                         String community,
                         String societyURI,
                         String locatorRef) {
    myName = cluster.getClusterIdentifier().toString();
    myCommunity = community;
    mySociety = societyURI;
    this.locatorRef = locatorRef;
    mgrThread = new Thread(this, getMyFullName());
  }
  
  /**
   * A PlugIn-independent constructor for a LiaisonManager
   *
   * @param myName a String identifying the caller as an agent in the context
   * of some particular ALP society; need not be globally unique.
   * @param community a String identifying the ALP community to which
   * the caller belongs.
   * @param societyURI a URI String identifying the ALP society to which
   * the caller belongs.
   * @param locatorRef A URL for the Jini lookup service to search for a
   * JavaSpace service through which liaison permissions
   * will be administered
   */
  public LiaisonManager (String myName,
                         String community,
                         String societyURI,
                         String locatorRef) {
    this.myName = myName;
    myCommunity = community;
    mySociety = societyURI;
    this.locatorRef = locatorRef;
    mgrThread = new Thread(this, getMyFullName());
  }
  
  public JavaSpace getMySpace () {
    return mySpace;
  }
  
  public String getMyName () {
    return myName;
  }
  
  public String getMyCommunity () {
    return myCommunity;
  }
  
  public String getMySociety () {
    return mySociety;
  }
  
  public String getMyFullName () {
    return mySociety + nameSep + myCommunity + nameSep + myName;
  }
  
  public ServiceID getMyServiceID () {
    return myID;
  }
  
  public String getMyLocatorRef () {
    return locatorRef;
  }
  
  public LiaisonDeputy createLiaisonDeputy (int deputyType, 
                                            PlugInDelegate pid,
                                            Vector parameters) {
    switch (deputyType) {
      case LConstants.COABS_SOCIETY:   
        return new CoABSLiaisonDeputy(this, pid, parameters);
      default:             
        System.err.println("createLiaisonDeputy(): " +
                           "Unknown liaison type " + deputyType);
    }
    return null;
  }
  
  public void start(boolean wait) {
    mgrThread.start();
    if (wait)
      while (!isEnabled())
        try {
          Thread.sleep(1000);
        } catch (InterruptedException ex) { }
  }
  
  public void run() {

    System.out.println("\nExternal liaison attempted by " + 
                       getMyFullName());

    if (System.getSecurityManager() == null) {
        System.setSecurityManager(
                 new RMISecurityManager());
    }
    
    proxy = new LiaisonManagerProxy(getMyFullName());
    
    /* figure out who I am as a Jini service */
    myID = loadServiceID(LConstants.ALP.SERVICE_ID_FILE_PREFIX + getMyFullName());
    if ( myID != null ) {
      System.out.println(getMyFullName() + " recovered existing liaison ServiceID:\n" + myID.toString());
      try {
        join = new JoinManager(proxy, getLiaisonManagerAttributes(),
                               myID, liaisonDiscoveryManager(), null);
      } catch (Exception ex) {
        System.err.println("LiaisonManager.run():\n" +
                           "Failed to create a JoinManager using ID " + myID +
                           "\n" + ex.getMessage());
        return; /* Have to go away to have the thread expire */ 
      }
    } else {
      try {
        join = new JoinManager(proxy, getLiaisonManagerAttributes(),
                               new IDListener(), liaisonDiscoveryManager(), null);
      } catch (Exception ex) {
        System.err.println("LiaisonManager.run():\n" +
                           "Failed to create a JoinManager for new ID\n" +
                           ex.getMessage());
        return; /* Have to go away to have the thread expire */ 
      }
      while ( myID == null ) // wait for ServiceID to be assigned
        try {
          Thread.sleep(1000);
        } catch (InterruptedException ex) { }
    }
    
    /* register with Liaison Administrator or quit */
    if (!registerForAdministration())  
      return;
    
    Thread me = Thread.currentThread();
    while (me == mgrThread) {
      /* do liaison management */
      // System.out.print("$");
      try {
        Thread.sleep(10000);
      } catch (InterruptedException ex) { }
    }
  }
  
  public void stop() {
    mgrThread = null;
    if ( join != null ) {
      join.terminate();
      join = null;
    }
  }
  
  public void notify (RemoteEvent ev) {
    Thread n = new Thread() {
      public void run() {
        notifyInternal();
      }
    };
    n.start();
  }
  
  private void notifyInternal () {
    getLatestStatus();
    if ( isEnabled() )
      System.out.println(mySociety + " society liaison has been enabled");
    else
      System.out.println(mySociety + " society liaison has been disabled");
  }
  
  public boolean isEnabled() {
    if ( mgrThread == null || !mgrThread.isAlive() || mySpace == null ||
         statusRef == null)
      return false;
    return statusRef.isEnabled.booleanValue();
  }
  
  private LookupDiscoveryManager liaisonDiscoveryManager() 
    throws MalformedURLException, IOException {
    LookupLocator[] locators = new LookupLocator[1];
    locators[0] = new LookupLocator(locatorRef);
    return new LookupDiscoveryManager(LConstants.JINI_GROUPS, locators, null);
  }
  
  private Entry[] getLiaisonManagerAttributes() {
    Entry[] results = new Entry[2];
    ServiceInfo info = new ServiceInfo();
    info.manufacturer = "Litton PRC";
    info.vendor = "Litton PRC";
    info.name = "ALP Liaison Manager";
    info.version = "0.1";
    results[0] = info;
    results[1] = new Name(getMyFullName());
    return results;
  }
  
  private boolean registerForAdministration () {
    me = ALPAgentReference.create(mySociety, myCommunity, myName, "", "", 
                                  myID.toString());
    spaceHelper = new LiaisonSpaceAdapter(mySociety, me, this);
    mySpace = spaceHelper.getSpace();
    if ( mySpace == null )
      return false;
    ALPAgentReference newMe =
      ALPAgentReference.createDefault(mySociety, myCommunity, myName, "", "",
                                      myID.toString());
    newMe.isFromALP = new Boolean(true);
    try {
      if ( spaceHelper.writeIfNotFound(me, newMe) )
        System.out.println(getMyFullName() +
                           " registering first time for administration");
      else {
        System.out.println(getMyFullName() +
                           " already registered for administration");
        getLatestStatus();
      }
    } catch (Exception ex) {
      return false;
    }
    return true;
  }

  private ALPAgentReference getLatestStatus () {
    ALPAgentReference newStatus = null;
    try {
      newStatus = (ALPAgentReference)
                  mySpace.read(me, null, 10000);
    } catch (Exception ex) {
      System.err.println("LiaisonManager.getLatestStatus():\n" +
                         "Failed to read status from JavaSpace" +
                         ex.getMessage());
    }
    if ( newStatus == null )
      System.out.println("LiaisonManager.getLatestStatus():\n" +
                         "No status available from JavaSpace");
    else
      statusRef = newStatus;
    return statusRef;
  }
  /*
  class LiaisonDiscoveryListener implements DiscoveryChangeListener {
    
    public void discarded (DiscoveryEvent ev)
    { describeEvent(ev); }
    
    public void discovered (DiscoveryEvent ev)
    { describeEvent(ev); }
    
    public void changed (DiscoveryEvent ev)
    { describeEvent(ev); }
    
    private void describeEvent (DiscoveryEvent ev) {
      try {
        System.out.println("Received discovery event from registrar:\n" +
                           ev.getRegistrars()[0].getLocator());
      } catch (Exception ex) { }
    }
  }
  */
  
  class IDListener implements ServiceIDListener {
    
    public void serviceIDNotify(ServiceID serviceID) {
      System.out.println(getMyFullName() + " established with new liaison ServiceID:\n" +
                         serviceID);
      try {
        saveServiceID(LConstants.ALP.SERVICE_ID_FILE_PREFIX + getMyFullName(), serviceID);
      } catch (IOException ex) {
        join.terminate();
      }
      myID = serviceID;
    }
  }
}

