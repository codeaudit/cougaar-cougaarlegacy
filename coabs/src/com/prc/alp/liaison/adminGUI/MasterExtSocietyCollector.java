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
import java.util.ArrayList;
/**
 * This class  provides a very open-ended interface
 * to a wide variety of External Societies.  Although it provides a simple
 * ExternalSocietyCollector Interface to the outside world, it also keeps an
 * aggregation of all possible sub-types of external society collectors supported
 * by the application, and synthesizes all of their responses into a harmonious
 * whole.
 *
 * @author John Page
 * @version 1.1
 * @since 1.0
 */
public class MasterExtSocietyCollector extends ExtSocietyCollector{
    // This class contains all supported types of society collectors.
    ExtJiniSocietyCollector  jiniCollector;
    ExtTestSocietyCollector  testCollector;
    LiaisonAdminToolController theApplication;
    SocietyDescriptor  currentDesc;
    // and so on....
    

    /** Creates new MasterExtSocietyCollector
     * @param app A pointer to the application shell (LiasionAdminToolController) that created this
     * instance. It can be used to access all instances of all public objects in the application.
     */
    public MasterExtSocietyCollector(LiaisonAdminToolController  app) {
        super();
        theApplication = app;
        jiniCollector = new ExtJiniSocietyCollector(app);
        testCollector = new ExtTestSocietyCollector();
    }
    
    
    
     /** Get an ArrayList of SocietyDescriptors for ALL possible external societies 
         that can be retrieved, regardless of subtype. 
     */
      public  ArrayList  getSocietyList(){
        ArrayList  theList;
        ExtSocietyDescriptor theDesc;
        
        theList = new ArrayList();
        theDesc = new ExtSocietyDescriptor(new String ("none"));
        theList.add(theDesc);      
        theList.addAll(testCollector.getSocietyList());
        theList.addAll(jiniCollector.getSocietyList());
        return theList;
      
    }
    
    /** Retrieves teh external society set for a specified society descriptor.  
        This method dispatches the request to the collector that handles the 
        selected subtype.  As a side effect, it sets the application's extSet value to
        the retrieved set.
    */
    public AgentSocietySet getSocietyFromDescriptor(SocietyDescriptor desc){
        currentDesc = desc;
        if(desc instanceof ExtJiniSocietyDescriptor){
            return(jiniCollector.getSocietyFromDescriptor(desc));
        } else if (desc instanceof ExtSocietyDescriptor) {
            return (testCollector.getSocietyFromDescriptor(desc));
        } else {
             // may wish to support an exception here in the future. 
             return null;
        }
    } 
    
    
    /** This is a convenience function to just load the first external society in 
        the list-- good for initialization.  It is no longer being used. 
    */
    public AgentSocietySet getFirstExternalSociety(){
        ArrayList   theList;
        SocietyDescriptor desc;
        
        theList = getSocietyList();
        if(theList.size() == 0) {
            return null;
        } else{
            desc = (SocietyDescriptor) theList.get(0);
            return(getSocietyFromDescriptor(desc));
        }   
    }
    
    /** "Let go" of the current society, and turn off any updates and listeners associated with 
     *  it.
     */ 
    public void releaseSociety(){
       if(currentDesc instanceof ExtJiniSocietyDescriptor){
            jiniCollector.releaseSociety();
        } else if (currentDesc instanceof ExtSocietyDescriptor) 
            testCollector.releaseSociety();      
    }
    

}