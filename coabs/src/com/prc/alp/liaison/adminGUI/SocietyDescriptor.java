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

/**
 * A society descriptor is to contain all information needed to identify and
 * retrieve a society with the appropriate collector.  This is a base class, and
 * will be used to derive for ALP, External, and External Jini Based societies.
 * It may appear that there is a lot of layers of typing around Society descriptors,
 * which for the moment are quite simple.  This may change, however, in the future.
 *
 * @author John Page
 * @version 1.1
 * @since 1.0
 */
public class SocietyDescriptor extends Object {
    String  societyName;

    /** Creates new SocietyDescriptor */
    public SocietyDescriptor() {
    }
    
    /** create a society descriptor with a pre-defined society name */
    public SocietyDescriptor(String name){
        societyName = name;
    }
   
    /** this method overrides the toString to show the name of the society */
    public String toString(){
        String  theString;
        if(societyName == null){
            theString = new String("Society<null>");
        } else {
            theString = new String(societyName);
        }
        
        return theString;
    }
    
    /** A predicate which returns true of the societyDescriptor represents no society, or "none" */
    public  boolean  isEmpty(){
        if(societyName.equalsIgnoreCase(new String("none")))
            return true;
        else
            return false;
    }
 

}