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
 * Authors:  John Page, Brandon L. Buteau
 *
 */

package com.prc.alp.liaison.adminGUI;
import  com.prc.alp.liaison.admin.ExternalAgentReference;
import net.jini.discovery.DiscoveryListener; 
import net.jini.discovery.DiscoveryEvent;
import net.jini.core.lookup.ServiceRegistrar;
import net.jini.core.lookup.ServiceMatches;
import net.jini.core.lookup.ServiceTemplate;
import net.jini.core.lookup.ServiceItem;
import net.jini.lookup.entry.Name;
import net.jini.lookup.entry.ServiceInfo;
import  net.jini.core.entry.Entry;
import java.rmi.RemoteException;
import java.util.Iterator;
import java.util.ArrayList;
import com.globalinfotek.coabsgrid.CoABSAgentDescription;
import com.globalinfotek.coabsgrid.AgentRep;
import com.globalinfotek.coabsgrid.ServiceRep;




/**
 * This class acts as a listener for discovering any new external agents that
 *  are registered with JINI.
 *
 * @author John Page
 * @author Brandon L. Buteau
 * @version 1.1
 * @since 1.0
 */
public class ExtJiniDiscoveryListener extends Object implements DiscoveryListener{
    protected ExtJiniSocietyCollector  collector;
    protected LiaisonAdminToolController  theApplication;
    protected ExtSocietySet  extSet;
    private int i;
    
    
    static public final int  MAX_JINI_AGENTS = 300;
    

    /** Creates new ExtJiniDiscoveryListener
     * @param theCollector reference to the Collector that created this listener
     */
    public ExtJiniDiscoveryListener(ExtJiniSocietyCollector theCollector) {
        collector = theCollector;
        theApplication = collector.theApplication;
        extSet = theApplication.extSet;
    }
    
    
    
   /**  this method is called when a lookup service registar is found.  It gets the  
     ones found, and then invokes the appropriate method of the collector to 
     handle them. 
    */
    public void discovered(final DiscoveryEvent ev) {
        ServiceRegistrar[]  newRegs;
        //System.err.println("Discover Event Received!");
        newRegs = ev.getRegistrars();
        //System.err.println(newRegs.length + " registrars received!!"); 
         for (int i = 0; i < newRegs.length; i++)  {
            getAgentsFromRegistrar(newRegs[i]);         
        }     
    }
    
    

    /** currently not implemented */
    
    public void discarded(final  DiscoveryEvent ev) {
    }
    
  
    /** Tries to peel the name from a JINI level service Item.  if there is 
     * no Name field or ServiceInfo, it merely returns the .toString() value
     */
    private  String   getNameFromServiceItem(ServiceItem item){
        String nameString = null;
        String typeString = null;
        for(int i=0; i<item.attributeSets.length; i++){
            Entry att = item.attributeSets[i];
            //System.out.println("Checking attribute of type " + att.getClass().getName());
            if (att instanceof CoABSAgentDescription)
              nameString = ((CoABSAgentDescription) att).name;
            if ( att instanceof Name && nameString == null )
                nameString = ((Name) att).name;
            else if (att instanceof ServiceInfo)
                typeString = ((ServiceInfo) att).name;
        }
        
        // if name not found, then lets get devious
        if (nameString == null) {
             if ( item.service != null )
                nameString = item.service.getClass().getName();
             else
                nameString = "unnamed";
          }
        if (typeString != null )
             nameString = typeString + ":" + nameString;
        
        return nameString;
            
    }
    
    
    /** this method is invoked by the discovery listener every time 
        new service registrar is found.  It is responsible creating the individual Agent
       references and adding them to the set.  Also, it will, in this first pass, 
      trip the flag letting the powers that be know that we are done. 
     */
    
    public void  getAgentsFromRegistrar(ServiceRegistrar thisReg){
        ServiceMatches matches = null;
        ServiceTemplate template;
        int totalMatches;  /* total number of matches overall */
        ServiceItem[] items;  /* array of items returned up to max specified */
        ServiceItem theItem;
        ExternalAgentReference  thisAgent;
        Iterator    it;
        ArrayList   newAgentList = new ArrayList();
        ArrayList   newLSRList = new ArrayList();
        CoABSAgentDescription  coDesc;
        CoABSAgentDescription  thisDesc;
        ServiceRep    thisAgentRep; 
        Entry   attrSets[]  = new Entry[1];


        template = new ServiceTemplate(null, null, null);
        //System.err.println("Top of get Agents From Registrar!"); 
        try{
            matches = thisReg.lookup(template, MAX_JINI_AGENTS); 
        } catch (RemoteException ex) {
            System.err.println("Error doing lookup: " + ex.getMessage());
        }
        
        totalMatches = matches.totalMatches;
        
        //System.err.println("Total number of Agents found is: " + totalMatches);
        
        // now tear these apart and add them to the set.  For now, put in the toString
        // value for everything except the society field, and a null for the neighborhood field. 
        
        for ( i = 0; i < totalMatches; i++){
            String  theString;
            theItem = matches.items[i];
            //System.out.println(getServiceItemDump( theItem));
            //theString = theItem.serviceID.toString();
            //System.out.println("Service object is <" + theItem.service + ">");
            /*
            if ( theItem.service != null )
              System.out.println("Found service " + i + " of class: " + theItem.service.getClass().getName());
            else
              System.out.println("Found service " + i + " of unknown class");
            */
            if(theItem.service instanceof ServiceRep){
                thisAgentRep = (ServiceRep) theItem.service;
                try{
                thisAgent = ExternalAgentReference.create(extSet.societyName, 
                                                      new String(""), 
                                                      thisAgentRep.getName(),
                                                      new String(""), 
                                                      new String(""), 
                                                      thisAgentRep.getServiceIDString());
                extSet.addRef(thisAgent);
                newAgentList.add(thisAgent);
                } catch (java.rmi.RemoteException  ex){
                
                
                }
            
            
           } else {  
                    thisAgent = ExternalAgentReference.create(extSet.societyName, 
                                                      new String(""), 
                                                      getNameFromServiceItem(theItem),
                                                      new String(""), 
                                                      new String(""), 
                                                      theItem.serviceID.toString());
                
                extSet.addRef(thisAgent);
                newAgentList.add(thisAgent);
          }
          //System.out.println("Found agent: " + thisAgent);
        }
        
        
        
        if(totalMatches > 0){

            // try to retrieve any LSRs for the new items.
            newLSRList = theApplication.liaisonCollector.getLSRsForNewAgents(null, newAgentList);
            // Store them in Liaison set.
            theApplication.liaisonSet.storeReferences(newLSRList);
            // validate them. -- make sure that the is a LSR for each 
            // alpRef, extRef pair, and that the permissions do not override 
            // any controlling ancestors.
            theApplication.mainWindow.validateAndUpdateExternalTree();
                  
            
        }
        

        
    }
    
    
    /** returns atext string that lists all of the attributes for a service Item */  
    public  String  getServiceItemDump(ServiceItem item){
       Entry   entries[];
       String  newString = new String("");
    
       entries = item.attributeSets;
       newString = newString + "Dump of Service Item entries:\n";
       for( i = 0; i < entries.length; i++){
         if ( entries[i] != null ) {
                newString = newString + entries[i].getClass().getName();
                newString = newString + entries[i].toString() + "\n";
              }
        } 
        return newString;

    }
        
      

}