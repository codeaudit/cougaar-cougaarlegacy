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
 * A Society Collector is used to import society descriptions into the AdminTool.
 * This is the base abstract class used to derive collectors for ALP societies and
 * a variety of external societies.
 *
 * @author John Page
 * @version 1.1
 * @since 1.0
 */
public abstract class SocietyCollector extends Object {

    /** Creates new SocietyCollector */
    public SocietyCollector() {
    }
    
    
    /** Each Collector must support a method that returns a list of descriptors that can
     * identify and be used to retrieve all societies that can be brought back in. 
     *
     */ 
    public abstract ArrayList getSocietyList();
    
    /**
     * This method retrieves an agentSocietySet for a given descriptor.  This may 
     * include retrieving a portion of the society, and enabling listeners to feed in 
     * dynamic updates.  When you are about to unload a society, you should call 
     * releaseSociety, which does nothing for canned data, but stops the updates for 
     * live data. 
     */
    public abstract AgentSocietySet getSocietyFromDescriptor(SocietyDescriptor desc); 
    
   
    /**  "Let go" of a currently held society set, and diasable any updates or 
          listeners associated with that set.
    */
    public abstract void    releaseSociety();

}