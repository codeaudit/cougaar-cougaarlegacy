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
 * This descriptor is encapsulates info needed find societies that us the
 *  JINI Lookup Service.
 *  The fields for the constructors allow you to also specify a society name,
 *  the URL for the specific JINI lookup service, and the groups supported by
 *  society.
 *
 * @author John Page
 * @version 1.1
 * @since 1.0
 */
public class ExtJiniSocietyDescriptor extends ExtSocietyDescriptor{
    String  jiniURL;
    String  groupList;

    /** Creates new ExtJiniSocietyDescriptor */
    public ExtJiniSocietyDescriptor() {
        super();
    }
    
    public ExtJiniSocietyDescriptor(String socName){
        super(socName);
    }
    
    public ExtJiniSocietyDescriptor(String socName, String aJiniURL, String aGroupList){
        super(socName);
        jiniURL = aJiniURL;
        groupList = aGroupList;
    } 
      
}