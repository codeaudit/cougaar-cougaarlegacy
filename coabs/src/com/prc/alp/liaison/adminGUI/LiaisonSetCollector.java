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
import  com.prc.alp.liaison.admin.ALPAgentReference;
import  com.prc.alp.liaison.admin.ExternalAgentReference;
import  com.prc.alp.liaison.admin.LiaisonStatusReference;
import  com.prc.alp.liaison.admin.LiaisonSpace;
import  java.util.ArrayList;
import  java.util.Iterator;

/**
 * This class is responsible for pulling in the Liaison Status References for
 *  a given ALP Society and external society.
 *  It pulls these from the same javaspace as the current ALP Society.
 *
 * @author John Page
 * @author Brandon L. Buteau
 * @version 1.1
 * @since 1.0
 */

public class LiaisonSetCollector extends Object {
    LiaisonAdminToolController   theApplication;

    /** Creates new LiaisonSetCollector
     * @param app A pointer to the application shell (LiasionAdminToolController) that created this
     * instance. It can be used to access all instances of all public objects in the application.
     */
    public LiaisonSetCollector(LiaisonAdminToolController app) {
        super();
        theApplication = app;
    }
    
    
   /** Return an array list with the default LSRS for every agent pair in the two 
     societies.  It returns an empty list if either society is empty or null. The 
    use of this method should decline, since the getLSRList can do the same thing
     if no values exist. 
    */
    public  ArrayList   getDefaultLSRList(){
        ArrayList   theList = new ArrayList();
        AlpSocietySet   alpSociety = theApplication.alpSet;
        ExtSocietySet  extSociety = theApplication.extSet;
        Iterator  alpIterator, extIterator;
        LiaisonStatusReference  thisLSR;
        ALPAgentReference  alpRef;
        ExternalAgentReference  extRef;
        
        // Short circuit if either society does not exist. 
        if((alpSociety == null) | (extSociety == null))
            return theList;
        
        alpIterator = alpSociety.iterator();      
        // Outer while Loop-- go through every agent in the ALP  Society      
        while (alpIterator.hasNext()){
            alpRef = (ALPAgentReference) alpIterator.next();
            extIterator = extSociety.iterator();
             // Inner while Loop-- go through all external agents 
            while(extIterator.hasNext()){
                extRef = (ExternalAgentReference) extIterator.next();
                thisLSR = LiaisonStatusReference.createDefault(extRef, 
                                                               alpRef.ALPID);
                // add new item to the list. 
                theList.add(thisLSR);
                  
            } //while external agents left...           
        } // while alp agents left   
        
        return theList;
        
        
    }
    
    
    /** This method returns all LSRS needed for an ALP Society/External Society pair. 
        It first retrieves all relevant LSRs from the JavaSpace  for the alp society and 
      create defaults for the case where one or more is missing. 
    
      WARNING:  THis current version does not work as specified.  It currently only 
      returns those in JavaSpace, but does not create defaults to fill any omissions. 
    */
    
    public  ArrayList getLSRsFromJavaSpace(String alpSocietyName,
                                           String extSocietyName){
        
        LiaisonSpace lspace = new LiaisonSpace(alpSocietyName);
        LiaisonStatusReference LSR = 
          LiaisonStatusReference.create(extSocietyName, null, null, null, null,
                                        null, null);
        ArrayList result = null;
        try {
          result = lspace.scanAll(LSR);
        } catch (Exception ex) {
          System.err.println("LiaisonSetCollector failed to get LSRs from " +
                             "JavaSpace " + alpSocietyName);
        }
        return result;
        
    }
    
   
    /** this method is NOT supported */
    
    public  ArrayList  getLSRsForNewAgents(ArrayList alpAgentList , ArrayList extAgentList){
        ArrayList newList = new ArrayList();
        
        return newList;
             
    }
    
    
   /**  Write out an arrayList of LSRs to the JavaSPace for persistance.   It is not 
     *  necessary to know whether or not they've already been defined. 
     */
    
    public void  writeOutLSRsToJavaSpace(ArrayList  lsrList){
        String alpSocietyName = theApplication.alpSet.societyName;
        Iterator  it = lsrList.iterator();
        LiaisonSpace lspace = null;
        //System.out.println("Have " + lsrList.size() + " LSRs to write out");
        while ( it.hasNext() ) {
          LiaisonStatusReference  LSR = (LiaisonStatusReference) it.next();
          lspace = new LiaisonSpace(alpSocietyName);
          boolean replaced;
          LiaisonStatusReference template =
            LiaisonStatusReference.create(null, null, null, null, null, 
                                          LSR.ALPID, LSR.externalID);
          try {
            //System.out.print("Trying to WOR " + template.ALPID + "/" + template.externalID +
            //                 " with " + LSR + "<" + LSR.ALPCanInitiate + "/" + LSR.ALPCanRespond + ">...");
            replaced = lspace.writeOrReplace(template, LSR);
            /*
            if ( replaced )
              System.out.println("replaced what was there");
            else
              System.out.println("wrote it first time");
            */
          } catch (Exception ex) {
            System.err.println("AlpSocietyCollector failed to write LSR to " +
                               "JavaSpace " + LSR.society);
          }
        }

        
        // currently this just prints out the items to be saved.
        
        /* Commented out 
        
        if(lsrList.size() == 0)
        {
            System.err.println("LiaisonSetCollector: writeOutLSRstoJavaSpace:  Empty list!");
            System.err.println("Nothing going out to society: " + theApplication.currentAlpRef.society);
            return;
        }
        
        System.err.println("WriteOutLSRsToJavaSpace: storing the following in " +
                                theApplication.currentAlpRef.society);
        
        
        
        it = lsrList.iterator();
        while(it.hasNext()){
            lsrRef = (LiaisonStatusReference) it.next();
            alpRef = (ALPAgentReference) 
                          theApplication.alpSet.getAgentRefByID(lsrRef.ALPID);
            
            System.err.println("     Saving (" + alpRef.getName() + ", " + 
                               lsrRef.getName() + ")");
        }
        
        
        */
        
    }
    
    

}