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
 * This is a superclass for ExternalSociety Descriptors. It should probably be
 * re-written as an abstract class.
 *
 * @author John Page
 * @version 1.1
 * @since 1.0
 */
public class ExtSocietyDescriptor extends SocietyDescriptor{

    /** Creates new ExtSocietyDescriptor */
    public ExtSocietyDescriptor() {
    }
    
    public ExtSocietyDescriptor(String socName){
        super(socName);
        
    }

}