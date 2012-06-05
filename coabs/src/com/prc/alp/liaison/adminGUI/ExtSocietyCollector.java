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
 * This class is not be instantiated, but is an intermediate derivation between the
 * base SocietyCollector class and all of the specific types of external society collectors.
 * It needs to be written as an abstract class.
 *
 * @author John Page
 * @version 1.1
 * @since 1.0
 */
public class ExtSocietyCollector extends SocietyCollector  {

    

    /** Creates new ExtSocietyCollector */
    public ExtSocietyCollector() {
    }
    
    
    // stub
    public  ArrayList  getSocietyList(){
        ArrayList  theList;
        ExtSocietyDescriptor  theDesc;
        
        theDesc = new ExtSocietyDescriptor("prc");
        theList = new ArrayList();
        theList.add(theDesc);
        
        return theList;
      
    }
    
    public void releaseSociety () {
      return;
    }
    
    
    
   
    public AgentSocietySet getSocietyFromDescriptor(SocietyDescriptor desc){
        return null;
    } 

}