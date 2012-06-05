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
 * Author:  Brandon L. Buteau
 *
 */

package com.prc.alp.liaison.plugin;

import java.io.Serializable;

/**
 * A proxy service object for allowing the <CODE>LiaisonManager</CODE> representing
 * a PlugIn as an agent to provide services by remote procedure call.  This proxy
 * is stored in the Jini lookup service used for liaison administration; not currently
 * in active use.
 *
 * @author Brandon L. Buteau
 * @version 1.1
 * @since 1.0
 */
public class LiaisonManagerProxy implements Serializable {

  private String name;

  public LiaisonManagerProxy () { }

  public LiaisonManagerProxy (String fullName) {
    name = fullName;
  }

  public String LiaisonManagerService () {
      return "My full name is " + name;
  }
  
}

