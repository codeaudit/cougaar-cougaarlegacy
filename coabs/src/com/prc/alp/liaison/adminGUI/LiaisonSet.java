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
import  com.prc.alp.liaison.admin.ALPAgentReference;
import  com.prc.alp.liaison.admin.ExternalAgentReference;
import  com.prc.alp.liaison.admin.LiaisonStatusReference; 
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

/**
 * This structure is a HashMap that is used to
 *  store a LiaisonStatusReference for each possible ALPAgent/ExternalAgent Pair
 *  created by the crossproduct between the AgentRefs in an ALP Society and
 *  and external society.
 *
 *  In general, in using this class, you shouldn't have to worry about the actual
 *  keys being used, instead, you will be providing AgentRef IDs for the two societies,
 *  and the key will be generated from them.
 *
 * @author John Page
 * @version 1.1
 * @since 1.0
 */
public class LiaisonSet extends HashMap {
    LiaisonAdminToolController  theApplication;
    AlpSocietySet  alpSociety;
    ExtSocietySet  extSociety; 
    


    /* Create liason set for two societies, but with no LSRs already defined yet */
    
    /** Create a new liaison set with all default references.
     * @param app A reference to the LiaisonAdminToolController (applicationShell).  This provides
     * access to all public objects in the application.
     */
    public LiaisonSet(LiaisonAdminToolController app){
       super();
       theApplication = app;
    
       ArrayList    theList;
       alpSociety = theApplication.alpSet;
       extSociety = theApplication.extSet;  
       /* Need to create LSRs for the cross product...*/
       theList = theApplication.liaisonCollector.getDefaultLSRList();
       storeReferences(theList);  
    }
    
    
    /* This constructor assumes that a Liaison set already has a complement 
     * of LSRs defined.  They are passed in as an ArrayList.  This constructor also 
     * validates the set to make sure all LSRs are defined and compliant. It also provides 
     * some debugging methods and the validate method that insures that all possible LSRs
     * are defined for a given Liaison set. 
     */
    
    /** Create a Liaison Set, add a list of already defined LiaisonStatusReferences, and then provide default values for any that are missing.
     * @param app         // this routine assumes a new ALP tree has been brought in by the application,
     *        // and that the AlpDisplayServer has modeling the necessary things.
     * @param theLSRList A list of LSRs retrieved from JavaSpace for the given ALP and external societies.
     */
    public LiaisonSet(LiaisonAdminToolController app,  ArrayList theLSRList){
       super();
       theApplication = app;
       alpSociety = theApplication.alpSet;
       extSociety = theApplication.extSet; 
       storeReferences(theLSRList);
       
    }
    
    
    /** This method loads an ArrayList of  LSRs into the set.  It does not 
     guarantee completeness.  To do that, you must call "validate" on the set afterwords. 
    */
    public  void  storeReferences(ArrayList statList){
       Iterator  it; 
       String  thisKey;
       LiaisonStatusReference  thisLSR; 
       ALPAgentReference  alpRef;
       ExternalAgentReference extRef;
    
     
       it = statList.iterator();
        while(it.hasNext()){
            thisLSR = (LiaisonStatusReference) it.next();
            thisKey = getHashKey(thisLSR.ALPID, thisLSR.externalID);
            put(thisKey, thisLSR);    
        }
        
        
    }
    
    
    /**  Calling this method makes sure that there is an LSR for EVERY alpRef/ExtRef 
         pair.  If one does  not exist, it puts in a default value.  It should be invoked whenever
         creating a new Liaison set from scratch, or after loading every known LSR 
         from JavaSpace.
    */
    public  void  validate(){
        Iterator  alpIterator, extIterator;
        LiaisonStatusReference  thisLSR, ancestorLSR;
        ALPAgentReference  alpRef, ancestor;
        ExternalAgentReference  extRef;
        String  thisKey;
        
        
        // Short circuit if either society does not exist. 
        if((alpSociety == null) | (extSociety == null))
            return;
       
        // This loop makes sure that EVERYBODY has an LSR. 
        alpIterator = alpSociety.iterator();      
        // Outer while Loop-- go through every agent in the ALP  Society      
        while (alpIterator.hasNext()){
            alpRef = (ALPAgentReference) alpIterator.next();
            extIterator = extSociety.iterator();
             // Inner while Loop-- go through all external agents 
            while(extIterator.hasNext()){
                extRef = (ExternalAgentReference) extIterator.next();
                thisLSR = getStatusRef(alpRef, extRef);
                if(thisLSR == null)
                    thisLSR = LiaisonStatusReference.createDefault(extRef, 
                                                               alpRef.ALPID);
        
                thisKey = getHashKey(alpRef.ALPID, extRef.externalID);
                put(thisKey, thisLSR);

            } //while external agents left...           
        } // while alp agents left  
        
        
        // Next I have to make sure that any objects under control have the proper
        // permissions set.  I'm sorry to repeat the looping, but in order for this 
        // to work I HAVE to know that all LSRs are at least defined. 
        
        alpIterator = alpSociety.iterator();      
        // Outer while Loop-- go through every agent in the ALP  Society      
        while (alpIterator.hasNext()){
            alpRef = (ALPAgentReference) alpIterator.next();
            extIterator = extSociety.iterator();
             // Inner while Loop-- go through all external agents 
            while(extIterator.hasNext()){
                extRef = (ExternalAgentReference) extIterator.next();
                thisLSR = getStatusRef(alpRef, extRef);
                ancestor = alpSociety.getControllingAncestor(alpRef);
                if(!(ancestor == null)){
                    ancestorLSR = getStatusRef(ancestor, extRef);
                    thisLSR.ALPCanInitiate = ancestorLSR.ALPCanInitiate;
                    thisLSR.ALPCanRespond = ancestorLSR.ALPCanRespond;
                    alpRef.isEnabled = ancestor.isEnabled;
                    // Note-- delegates permissions are not propagated. 
                } // if controlled 

            } //while external agents left...           
        } 
        
        
        
        
        
        
    }
    
    
    

    
    
    
    /** Generates a Hash Key.  */
    private  String  getHashKey(String alpID, 
                                  String externalID){
      String  theKey;
      
      theKey = new String(alpID + "<>" + externalID);
      return theKey;   
    }
    
    
    
    /** Retrieve the LSR  associated with the ALPRef/extRef pair or null if one 
        does not exist. 
    */
    public  LiaisonStatusReference  getStatusRef(ALPAgentReference alpRef,
                                                 ExternalAgentReference extRef){
      String    theKey;
    
      theKey = getHashKey(alpRef.ALPID, extRef.externalID);
      return((LiaisonStatusReference) get(theKey));
                                                    
    }
    
   
    /** this removes an LSR for an AlpRef/ExtRef pair.  It is not currently used. */
    public void removeStatusRef(ALPAgentReference alpRef,
                                ExternalAgentReference extRef){
       String  theKey;
    
        theKey = getHashKey(alpRef.ALPID, extRef.externalID);
        remove(theKey);
                                    
    }
    
    /** Create a dump of the Liaison set to a string for debugging. 
    */
    
    public  String  createLiaisonSetDump(){
        Iterator alpIt, extIt;
        ALPAgentReference alpRef;
        ExternalAgentReference extRef;
        LiaisonStatusReference theLSR; 
        String  theKey;
        
        String  theDump = new String();
        
        theDump = theDump + "Dump for current Liaison Set: \r";
        theDump = theDump + "Contains Alp society " + alpSociety.topRef.getName() + 
                            " which has " + alpSociety.size() + " items \r";
        theDump = theDump + " and External society " + extSociety.topRef.getName() + 
                            " which has " + extSociety.size() + " items.\r\r";
        
        alpIt = alpSociety.iterator();
    
        while (alpIt.hasNext()){
            alpRef = (ALPAgentReference) alpIt.next();
            extIt = extSociety.iterator();
            while(extIt.hasNext()){
                extRef = (ExternalAgentReference) extIt.next();
                theKey = getHashKey(alpRef.ALPID, extRef.externalID);
                theLSR = (LiaisonStatusReference) get(theKey);
                theDump = theDump + "For Alp Agent " + alpRef.getName() + " and external agent " 
                          + extRef.getName() + ":\r";
                theDump = theDump + "     The Key is: " + theKey + "\r";
                if(theLSR == null) 
                    theDump = theDump + "     NULL LIASON STATUS RECORD!!\r";
                else {
                    theDump = theDump + "     LIASON STATUS RECORD\r";
                    theDump = theDump + "          ALPID: " + theLSR.ALPID + 
                              " External ID: " + theLSR.externalID + "\r";
                    theDump = theDump + "          ALPCanInitiate: " + theLSR.ALPCanInitiate + "\r";
                    theDump = theDump + "          ALPCanRespond: " + theLSR.ALPCanRespond + "\r";
              
                }
            }
        }
        
        return theDump;
        
    }
 

}