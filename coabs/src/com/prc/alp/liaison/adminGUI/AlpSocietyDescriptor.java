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
 * This class encapsulate all the knowledge needed to identify and retrieve a specific ALP
 *  society. Current it only contains a name.
 *
 * @author John Page
 * @version 1.1
 * @since 1.0
 */
public class AlpSocietyDescriptor extends SocietyDescriptor{

    /** Creates new AlpSocietyDescriptor */
    public AlpSocietyDescriptor() {
    }
    
    public AlpSocietyDescriptor(String socName){
        super(socName);       
    }

}