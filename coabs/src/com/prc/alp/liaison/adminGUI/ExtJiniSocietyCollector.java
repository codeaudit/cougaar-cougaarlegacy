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
 * Author:  John Page
 *
 */

package com.prc.alp.liaison.adminGUI;

import com.prc.alp.liaison.LConstants;
import com.prc.alp.liaison.admin.*;
import com.prc.alp.liaison.plugin.*;
import java.util.ArrayList;
import net.jini.discovery.DiscoveryListener; 
import net.jini.discovery.LookupDiscovery;
import net.jini.discovery.LookupDiscoveryManager;
import net.jini.core.discovery.LookupLocator;
import net.jini.core.lookup.ServiceRegistrar;
import net.jini.core.lookup.ServiceTemplate;
import net.jini.core.lookup.ServiceMatches;
import net.jini.core.lookup.ServiceItem;


import java.util.Vector;
import java.io.IOException;
import java.rmi.RemoteException;
import java.rmi.RMISecurityManager;

/**
 * This class uses the JINI lookup service to import a society into the admin
 * tool.  It is assumed that the group name and locator URL are the elements that
 * define the name of the society.
 *
 * @author John Page
 * @version 1.1
 * @since 1.0
 */
public class ExtJiniSocietyCollector extends ExtSocietyCollector {
    public LiaisonAdminToolController  theApplication; 
    //protected  LookupDiscovery  discover;
    protected LookupDiscoveryManager discover;
    protected  ExtJiniDiscoveryListener  myListener;
    protected  ServiceTemplate   template;
    public     ExtSocietySet extSet;
    static public final int  JINI_TIMEOUT_SECS = 20;  // obselete? 
    static public final int  MAX_JINI_AGENTS = 300;

    /** Creates new ExtJiniSocietyCollector
     * @param app Pointer to the LiaisonAdminToolController (application shell) that is
     * creating it.  This provides access to all public data structures.
     */
    public ExtJiniSocietyCollector(LiaisonAdminToolController app) {
        super();
        theApplication = app;
    }
    
        // CUrrenlty knocking it up to return only one CoABS descriptor 
    /** Return a list of society descriptors for all societies that can be found by this collector.
     * @return A list of Jini society descriptors
     */
    public  ArrayList  getSocietyList(){
        ArrayList  theList;
        ExtJiniSocietyDescriptor  theDesc;
        
        theDesc = new ExtJiniSocietyDescriptor(
                       System.getProperty(LConstants.CoABS.SOCIETY_NAME_PROPERTY,
                                          LConstants.CoABS.DEFAULT_GRID_NAME));
        theList = new ArrayList();
        theList.add(theDesc);

        
        return theList;
    }
    
    /** Returns services registered with the Jini Lookup
       specified in the society description.  Unless we know more about the relationship
       between different agents, it will structure this as a single society reference
       with a flat set of agentreferences as its children.
    */
    
    public AgentSocietySet getSocietyFromDescriptor(SocietyDescriptor desc){

        ExternalAgentReference thisAgent; 
        String  idString;   
        ServiceTemplate template; 
        final int MAX_ITEMS = 300; 
        

        extSet = new ExtSocietySet(desc.societyName);  
        theApplication.extSet = extSet;
        
        theApplication.mainWindow.setStatusBar(new String("Loading Society " + desc.societyName + ", please wait..."));
        
        
        // Create a society level root node-- this is currently NOT in the 
        // COABS grid, so we can fake it. 
        thisAgent = ExternalAgentReference.create(desc.societyName, null, null, 
                                     null, new String(""),
                                     new String(desc.societyName));
       
        
        extSet.addRef(thisAgent);
        
        //System.err.print("Just added society Top Ref for the ext Set is: ");
        //System.err.print(thisAgent.getName() + "\r");
        
        // We also need to list the "discover" object as an external agent.  Since
        // this is the pathway to the Jini Registry, it's not in the registry itself. 
        
        idString = System.getProperty(LConstants.CoABS.LOCATOR_PROPERTY,
                                      "jini://localhost:4160/");
        
        //System.err.println(idString);
        
        thisAgent = ExternalAgentReference.create(desc.societyName, 
                                                    new String(""), 
                                                    LConstants.CoABS.DEFAULT_REGISTRY_NAME,
                                                    null, 
                                                    null,
                                                    idString);
        extSet.addRef(thisAgent);
        
        
        // Now we start the ball in motion, setting up a connection to the 
        // lookup services and turn on a listener. 
         if(System.getSecurityManager() == null) {
 
            System.setSecurityManager(new RMISecurityManager());
        }
        

          
        /* I'm currently doing a lookup of ALL groups-- not just the one specified in the
        * name field of the society descriptor. */
      
        try{
            LookupLocator[] locators = new LookupLocator[1];
            locators[0] = new LookupLocator(idString);
            discover = new LookupDiscoveryManager(LConstants.JINI_GROUPS, 
                                                  locators, 
                                                  new ExtJiniDiscoveryListener(this));
            //discover = new LookupDiscovery(LookupDiscovery.ALL_GROUPS);
        } catch (java.net.MalformedURLException ex) {
            System.err.println("ExtJiniSocietyCollector.getSocietyFromDescriptor():\n" +
                               "Bad locator URL: " + idString);
            theApplication.mainWindow.setStatusBar(new String("Error: cannot load."));
            theApplication.extSet = extSet;
            return extSet;
        } catch (java.io.IOException ex){
            System.err.println("ExtJiniSocietyCollector.getSocietyFromDescriptor():\n" +
                               "IO Exception creating discovery manager");
            theApplication.mainWindow.setStatusBar(new String("Error: cannot load."));
            theApplication.extSet = extSet;
            return extSet;
        }
        
        /* Now add the listener */
        
        // Make sure current value of extSet is set for the application.
        
        theApplication.extSet = extSet;
        
        //myListener = new ExtJiniDiscoveryListener(this);
        //discover.addDiscoveryListener(myListener);
     
        // return the set.  Over time, it will hopefully be increased in size via the
        // discovery listener. 
        
        return extSet;
    }
    
/** Disengage any listeners defined for this society */
    
    public void releaseSociety(){
        discover.removeDiscoveryListener(myListener);
        discover = null;
             
    }
    
   
    

}