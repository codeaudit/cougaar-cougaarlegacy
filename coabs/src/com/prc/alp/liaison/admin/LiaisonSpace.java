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

package com.prc.alp.liaison.admin;

import net.jini.core.entry.Entry;
import net.jini.core.event.RemoteEvent;
import net.jini.core.lease.Lease;
import net.jini.core.transaction.server.TransactionManager;
import net.jini.core.transaction.Transaction;
import net.jini.core.transaction.TransactionFactory;
import net.jini.core.transaction.TransactionFactory;
import net.jini.space.JavaSpace;
import java.rmi.RMISecurityManager;
import java.util.*;

/**
 * A class that simplifies the use of JavaSpace services.  It provides a number
 * of methods for accessing JavaSpace services and for conditionally
 * manipulating JavaSpace entries.
 *
 * @author Brandon L. Buteau
 * @version 1.1
 * @since 1.0
 */
public class LiaisonSpace {
  
  private static int DEFAULT_REPLACE_TIMEOUT = 5 * 1000;
  private static int DEFAULT_TAKE_TIMEOUT = 5 * 1000;
  private static int DEFAULT_TXN_LEASE = 60 * 1000;
  private static String DEFAULT_LOCATOR_URL = "jini://localhost/";

  private JavaSpace space;
  private String name;

  public static JavaSpace accessSpace(String name) {
    try {
      if (System.getSecurityManager() == null) {
        System.setSecurityManager(
            new RMISecurityManager());
      }

      Object newSpace = (Object)NamedServiceFinder.find(getLocatorRef(), name);
      try {
        JavaSpace bar = (JavaSpace) newSpace;
      } catch (Exception ex) { ex.printStackTrace(); }        
      return (JavaSpace) newSpace;
    } catch (Exception ex) {
      System.err.println("LiaisonSpace.getSpace():\n" +
                         "Failed to access JavaSpace " + name + "\n" +
                         ex.getMessage());
    }
    return null;
  }
  
  public static TransactionManager getTXNManager(String name) {
    try {
      if (System.getSecurityManager() == null) {
        System.setSecurityManager(
            new RMISecurityManager());
      }
      return (TransactionManager)NamedServiceFinder.find(getLocatorRef(), name);
    } catch (Exception ex) {
      System.err.println("LiaisonSpace.getTXNManager():\n" +
                         "Failed to access transaction service " + name + "\n" +
                         ex.getMessage());
    }
    return null;      
  }
    
  public static TransactionManager getTXNManager() {
    return getTXNManager(com.sun.jini.mahalo.TxnManager.DEFAULT_NAME);
  }

  public static void main(String[]args) {
    JavaSpace s = LiaisonSpace.accessSpace(args[0]);
    System.out.println("main() found space: " + s);
  }
  
  private static String getLocatorRef() {
    String loc = System.getProperty("com.prc.alp.liaison.locator");
    if ( loc == null )
      loc = DEFAULT_LOCATOR_URL;
    return loc;
  }
  
  public LiaisonSpace(String name) {
    this.name = name;
    this.space = accessSpace(name);
  }
  
  public LiaisonSpace(JavaSpace space,String name) {
    this.space = space;
    this.name = name;
  }
  
  public JavaSpace getSpace () {
    return space;
  }
  
  public String getName() {
    return name;
  }
  
  public boolean writeIfNotFound (Entry test, Entry post) 
    throws Exception {
    
    boolean posted = false;
    TransactionManager tmgr = getTXNManager();
    Transaction.Created trc = null;
    try {
      trc = TransactionFactory.create(tmgr, DEFAULT_TXN_LEASE);
    } catch (Exception ex) {
      System.err.println("LiaisonSpace.writeIfNotFound():\n" +
                         "Failed to create a transaction for " + name + "\n" +
                         ex.getMessage());
      throw ex;
    }
    Transaction txn = trc.transaction;
    try {
      try {
        Object o = (Object) space.readIfExists(test, txn, JavaSpace.NO_WAIT);
        if ( o == null ) {
          space.write(post, txn, Lease.FOREVER);
          posted = true;
        }
      } catch (Exception ex) {
        System.err.println("LiaisonSpace.writeIfNotFound():\n" +
                           "Failed to access JavaSpace for " + name + "\n" +
                           ex.getMessage());
        ex.printStackTrace();
        txn.abort();
        throw ex;
      }
      txn.commit();
    } catch (Exception ex) {
      System.err.println("LiaisonSpace.writeIfNotFound():\n" +
                         "Failure in mid-transaction for " + name + "\n" +
                         ex.getMessage());
      throw ex;
    }
    return posted;
  }

  public boolean writeOrReplace (Entry find, Entry replacement) 
    throws Exception {
    
    boolean replaced = false;
    TransactionManager tmgr = getTXNManager();
    Transaction.Created trc = null;
    try {
      trc = TransactionFactory.create(tmgr, DEFAULT_TXN_LEASE);
    } catch (Exception ex) {
      System.err.println("LiaisonSpace.writeOrReplace():\n" +
                         "Failed to create a transaction for " + name + "\n" +
                         ex.getMessage());
      throw ex;
    }
    Transaction txn = trc.transaction;
    try {
      try {
        Object o = (Object) space.take(find, txn, DEFAULT_TAKE_TIMEOUT);
        space.write(replacement, txn, Lease.FOREVER);
        if ( o != null )
          replaced = true;
      } catch (Exception ex) {
        System.err.println("LiaisonSpace.writeOrReplace():\n" +
                           "Failed to write/replace entry for " + name + "\n" +
                           ex.getMessage());
        txn.abort();
        throw ex;
      }
      txn.commit();
    } catch (Exception ex) {
      System.err.println("LiaisonSpace.writeOrReplace():\n" +
                         "Failure in mid-transaction for " + name + "\n" +
                         ex.getMessage());
      throw ex;
    }
    return replaced;
  }
  
  public ArrayList takeAll (Entry find) 
    throws Exception {
    
    ArrayList result = null;
    TransactionManager tmgr = getTXNManager();
    Transaction.Created trc = null;
    try {
      trc = TransactionFactory.create(tmgr, DEFAULT_TXN_LEASE);
    } catch (Exception ex) {
      System.err.println("LiaisonSpace.takeAll():\n" +
                         "Failed to create a transaction for " + name + "\n" +
                         ex.getMessage());
      throw ex;
    }
    Transaction txn = trc.transaction;
    try {
      try {
        Entry o = null;
        while ( (o = (Entry) space.takeIfExists(find, txn, DEFAULT_TAKE_TIMEOUT)) != null )
          result.add(o);
      } catch (Exception ex) {
        System.err.println("LiaisonSpace.takeAll():\n" +
                           "Failed to take entry for " + name + "\n" +
                           ex.getMessage());
        txn.abort();
        throw ex;
      }
      txn.commit();
    } catch (Exception ex) {
      System.err.println("LiaisonSpace.takeAll():\n" +
                         "Failure in mid-transaction for " + name + "\n" +
                         ex.getMessage());
      throw ex;
    }
    return result;
  }
  
  public ArrayList scanAll (Entry find) 
    throws Exception {
    
    ArrayList result = new ArrayList();
    TransactionManager tmgr = getTXNManager();
    Transaction.Created trc = null;
    try {
      trc = TransactionFactory.create(tmgr, DEFAULT_TXN_LEASE);
    } catch (Exception ex) {
      System.err.println("LiaisonSpace.scanAll():\n" +
                         "Failed to create a transaction for " + name + "\n" +
                         ex.getMessage());
      throw ex;
    }
    Transaction txn = trc.transaction;
    try {
      try {
        Entry o = null;
        while ( (o = (Entry) space.takeIfExists(find, txn, DEFAULT_TAKE_TIMEOUT)) != null )
          result.add(o);
        Iterator it = result.iterator();
        while ( it.hasNext() ) {
          Entry e = (Entry) it.next();
          space.write(e, txn, Lease.FOREVER);
        }
      } catch (Exception ex) {
        System.err.println("LiaisonSpace.scanAll():\n" +
                           "Failed to take/rewrite entry for " + name + "\n" +
                           ex.getMessage());
        txn.abort();
        throw ex;
      }
      txn.commit();
    } catch (Exception ex) {
      System.err.println("LiaisonSpace.scanAll():\n" +
                         "Failure in mid-transaction for " + name + "\n" +
                         ex.getMessage());
      throw ex;
    }
    return result;
  }
  
}

