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
import  com.prc.alp.liaison.admin.ExternalAgentReference;
import  java.util.ArrayList;

/**
 * This society collector does not bring in live data, but rather canned data
 *  for testing.  It will not be supported in the future.
 *
 * @author John Page
 * @version 1.1
 * @since 1.0
 */
public class ExtTestSocietyCollector extends ExtSocietyCollector{

    /** Creates new ExtTestSocietyCollector */
    public ExtTestSocietyCollector() {
    }
    
   /** Get the list of test society descriptors available */
    public  ArrayList  getSocietyList(){
        ArrayList  theList;
        ExtSocietyDescriptor  theDesc;
        
        theDesc = new ExtSocietyDescriptor("TestSociety");
        theList = new ArrayList();
        theList.add(theDesc);

        return theList;
    }
    
    /** Returns a test society based on the Three stooges. (Making it a very
        dysfunctional one.  */
    public AgentSocietySet getSocietyFromDescriptor(SocietyDescriptor desc){
        ExtSocietySet  theSet;
        ExternalAgentReference thisAgent; 
   
        
        theSet = new ExtSocietySet(desc.societyName);
        
        // Create a society level reference 
        
        thisAgent = ExternalAgentReference.create(desc.societyName, null, null, 
                                                  "test", "test", "00000gygyg");
        theSet.addRef(thisAgent);  
        
        // create a set of mid level community references 
        
        thisAgent = ExternalAgentReference.create(desc.societyName, "", "Larry", 
                                                    "Nyuk", "Sr. VP", "0000trdd3");
        
        theSet.addRef(thisAgent);
        
        thisAgent = ExternalAgentReference.create(desc.societyName, "", "Curly", 
                                                    "Nyuk Nyuk", "Sr. VP",  "9gjhg77");
        theSet.addRef(thisAgent);
        
        thisAgent = ExternalAgentReference.create(desc.societyName, "", "Moe",
                                                    "Wise Guy, eh?", "Sr. VP", "r4746");
        theSet.addRef(thisAgent);
        
        // now create some "agents" that report to these others.  
        
        thisAgent = ExternalAgentReference.create(desc.societyName, "", "Shemp", 
                                                    "Ouch!", "", "839673");
        
        theSet.addRef(thisAgent);
        
        thisAgent = ExternalAgentReference.create(desc.societyName, "", "CurlyJoe",
                                                    "Doink", "y", "838213"); 
        theSet.addRef(thisAgent);
        
        return(theSet);
    } 
    
    /** THis method not implemented.  It's used to stop receiving any updates 
        for the society. 
    */
    public void  releaseSociety(){
        
    }


}