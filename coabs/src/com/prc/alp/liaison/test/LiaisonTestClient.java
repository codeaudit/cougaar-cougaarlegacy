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

package com.prc.alp.liaison.test;

//import alp.cluster.ClusterServesPlugIn;
//import alp.util.ConfigFinder;
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
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.rmi.RMISecurityManager;
import java.util.Vector;
import com.prc.alp.liaison.admin.*;
import com.prc.alp.liaison.plugin.*;
import com.prc.alp.liaison.util.*;
import com.prc.alp.liaison.LConstants;

/**
 * Tests the establishment of contact and the management of interactions
 * between an application and a liaison administration GUI through a JavaSpace.
 * This functions as a standalone version of a <CODE>LiaisonManager</CODE>.
 *
 * @author Brandon L. Buteau
 * @version 1.1
 * @since 1.0
 * @see com.prc.alp.liaison.adminGUI.LiaisonAdminToolMainGUI
 * @see com.prc.alp.liaison.plugin.LiaisonManager
 */

public class LiaisonTestClient 
  implements Runnable, LiaisonTrigger {
  
  
  private static String nameSep = ".";
  private static String idExt = ".sid";
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
  private LiaisonStatusReference srTemplate = null; 
  private LiaisonSpaceListener srMonitor = null;
  protected JoinManager join = null;
  protected LiaisonManagerProxy proxy = null;
  
  public static void main (String[] args) {
    if ( args.length != 4 ) {
      System.err.println("Usage:\n" +
                         "\tjava LiaisonTestClient <society> <community> <agent> " +
                         "<locatorURL>");
      System.exit(-1);
    }
    LiaisonTestClient client = 
      new LiaisonTestClient (args[0], args[1], args[2], args[3]);
    client.start(true);
    return;
  }
  
  public static ServiceID loadServiceID (String prefixedName) {
    ServiceID id = null;
    File f = new File(prefixedName + idExt);
    if ( f != null) {
      try {
        FileInputStream is = new FileInputStream(f);
        if ( is == null ) {
          System.err.println("Can't find ServiceID file: " + prefixedName + idExt);
          return null;
        }
        ObjectInputStream ois = new ObjectInputStream(is);
        Object o = ois.readObject();
        if ( !(o instanceof ServiceID) )
          System.err.println("LiaisonTestClient.loadServiceID():\n" +
                             "Invalid ServiceID file\n");
        else {
          id = (ServiceID) o;
          ois.close();
        }
      } catch (Exception ex) {
        System.err.println("LiaisonTestClient.loadServiceID():\n" +
                           "Problem finding or loading ServiceID file\n" +
                           ex.getMessage());
      }
    }
    return id;
  }
  
  public static void saveServiceID (String prefixedName, ServiceID  id) 
    throws IOException {
    File idFile = new File(prefixedName + idExt);
    try {
      ObjectOutputStream out = new ObjectOutputStream(
                                    new FileOutputStream(idFile));
      out.writeObject(id);
      out.flush();
      out.close();
    } catch (IOException ex) {
      System.err.println("LiaisonTestClient.saveServiceID():\n" +
                         "Problem opening or writing ServiceID file\n" +
                         ex.getMessage());
      throw ex;
    }
  }
  
  /** Constructor for LiaisonTestClient
   * @param myName a String identifying the caller as an agent in the context
   * of some particular ALP society; need not be globally unique.
   * @param community a String identifying the ALP community to which
   * the caller belongs.
    * @param societyURI a URI String identifying the ALP society to which
   * the caller belongs.
   **/
  public LiaisonTestClient (String societyURI,
                            String community,
                            String myName,
                            String locatorRef) {
    this.myName = myName;
    myCommunity = community;
    mySociety = societyURI;
    this.locatorRef = locatorRef;
    mgrThread = new Thread(this, getMyFullName());
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
        join = new JoinManager(proxy, getLiaisonTestClientAttributes(),
                               myID, liaisonDiscoveryManager(), null);
      } catch (Exception ex) {
        System.err.println("LiaisonTestClient.run():\n" +
                           "Failed to create a JoinManager using ID " + myID +
                           "\n" + ex.getMessage());
        return; /* Have to go away to have the thread expire */ 
      }
    } else
      try {
        join = new JoinManager(proxy, getLiaisonTestClientAttributes(),
                               new IDListener(), liaisonDiscoveryManager(), null);
      } catch (Exception ex) {
        System.err.println("LiaisonTestClient.run():\n" +
                           "Failed to create a JoinManager for new ID\n" +
                           ex.getMessage());
        return; /* Have to go away to have the thread expire */ 
      }
    
    /* register with Liaison Administrator or quit */
    if (!registerForAdministration())  
      return;
    
    Thread me = Thread.currentThread();
    while (me == mgrThread) {
      /* do liaison management */
      try {
        Thread.sleep(10000);
      } catch (InterruptedException ex) { }
      System.out.print(".");
    }
  }
  
  public void stop() {
    mgrThread = null;
    if ( join != null ) {
      join.terminate();
      join = null;
    }
  }
  
  public boolean isEnabled() {
    if ( mgrThread == null || !mgrThread.isAlive() || mySpace == null ||
         statusRef == null)
      return false;
    return statusRef.isEnabled.booleanValue();
  }
  
  public void notify (RemoteEvent ev) {
    getLatestStatus();
    if ( isEnabled() )
      System.out.println(mySociety + " society liaison has been enabled");
    else
      System.out.println(mySociety + " society liaison has been disabled");
  }
  
  private LookupDiscoveryManager liaisonDiscoveryManager() 
    throws MalformedURLException, IOException {
    LookupLocator[] locators = new LookupLocator[1];
    locators[0] = new LookupLocator(locatorRef);
    return new LookupDiscoveryManager(LConstants.JINI_GROUPS, locators, null);
  }
  
  private Entry[] getLiaisonTestClientAttributes() {
    Entry[] results = new Entry[2];
    ServiceInfo info = new ServiceInfo();
    info.manufacturer = "Litton PRC";
    info.vendor = "Litton PRC";
    info.name = "ALP Liaison Test Client";
    info.version = "0.1";
    results[0] = info;
    results[1] = new Name(getMyFullName());
    return results;
  }
  
  private boolean registerForAdministration () {
    me = ALPAgentReference.create(mySociety, myCommunity, myName, "", "",
                                  (myID == null) ? null : myID.toString());
    spaceHelper = new LiaisonSpaceAdapter(mySociety, me, this);
    mySpace = spaceHelper.getSpace();
    if ( mySpace == null )
      return false;
    ALPAgentReference newMe = ALPAgentReference.createDefault(
                                    mySociety, myCommunity, myName, "", "",
                                    (myID == null) ? null : myID.toString());
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

    srTemplate =
      LiaisonStatusReference.create(mySociety, myCommunity, myName, "", "",
                                  (myID == null) ? null : myID.toString(),
                                   locatorRef);
    srMonitor = new LiaisonSpaceListener(mySpace);
    srMonitor.listenFor(srTemplate, new LiaisonMonitor());

    return true;
  }

  private ALPAgentReference getLatestStatus () {
    try {
      statusRef = (ALPAgentReference)
                  mySpace.readIfExists(me, null, JavaSpace.NO_WAIT);
      return statusRef;
    } catch (Exception ex) {
      // Don't need to do anything
    }
    return null;
  }
  
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
  
  class LiaisonMonitor implements LiaisonTrigger {
    
    public void notify (RemoteEvent ev) {
      LiaisonStatusReference status = null;
      try {
        status =
          (LiaisonStatusReference) mySpace.readIfExists(srTemplate, null, JavaSpace.NO_WAIT);
      } catch (Exception ex) { /* don't care */ }
      if ( status != null ) {
        System.out.println("\nRevised liaison status with " + 
                              status.society + "." + status.neighborhood + "." +
                              status.agent + ":\n");
        System.out.println("\tALP can initiate = " + status.ALPCanInitiate);
        System.out.println("\tALP can respond  = " + status.ALPCanRespond);
      }
      return;
    }
  }
    
}

