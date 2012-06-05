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

package com.prc.alp.liaison.util;

import java.rmi.*;

import net.jini.core.transaction.server.TransactionManager;

import com.sun.jini.mahout.Locator;
import com.sun.jini.outrigger.Finder;

/**
 * Finds a Transaction Manager Jini service by name; not currently in active use.
 * Equivalent functionality (and more) can be found in the <CODE>LiaisonSpace</CODE> class.
 *
 * @author Brandon L. Buteau
 * @version 1.1
 * @since 1.0
 * @see com.prc.alp.liaison.admin.LiaisonSpace
 */
public class LiaisonTMAccessor {
  public static TransactionManager getManager(String name) {
    Locator locator = null;
    Finder finder = null;

    if (System.getSecurityManager() == null) {
    System.setSecurityManager(
        new RMISecurityManager());
    }

    locator = new com.sun.jini.outrigger.DiscoveryLocator();
    finder = new com.sun.jini.outrigger.LookupFinder();

    return (TransactionManager)finder.find(locator, name);
  }
    
  public static TransactionManager getManager() {
      return getManager(com.sun.jini.mahalo.TxnManager.DEFAULT_NAME);
  }

}
