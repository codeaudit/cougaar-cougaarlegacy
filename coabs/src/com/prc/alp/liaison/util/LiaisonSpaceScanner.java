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

import net.jini.core.lease.Lease;
import net.jini.space.JavaSpace;
import net.jini.core.entry.UnusableEntryException;
import com.prc.alp.liaison.admin.*;
import java.util.*;

/**
 * Lists all of the entries stored in a JavaSpace.  This is useful for debugging
 * purposes, as it gives a complete snapshot of what is currently readable in
 * a JavaSpace, no matter what its class or source.
 *
 * @author Brandon L. Buteau
 * @version 1.1
 * @since 1.0
 */
public class LiaisonSpaceScanner {
  
  public static void main(String[] args) {
    long removalCount = 0;
    long unusableCount = 0;
    if (!validateArgs(args))
            System.exit(-1);
    boolean verbose = args.length == 2 ? true : false;
    String spaceName = verbose ? args[1] : args[0];
    LiaisonSpace lspace = new LiaisonSpace(spaceName);

    ArrayList entries = null;
    try {
      entries = lspace.scanAll(null);
    } catch (Exception ex) {
      System.err.println("Exception while scanning space\n:" + ex.getMessage());
      System.exit(-1);
    }
    Iterator it = entries.iterator();
    while ( it.hasNext() ) {
      Object o = it.next();
      dumpObject(o);  
    }
    System.exit(0);
  }
  
  public static void dumpObject (Object o) {
    Class c = o.getClass();
    System.out.println("Object of class: " + c.getName());
    System.out.println(o);
  }

  public static boolean validateArgs (String[] args) {
    if (args.length < 1) {
      System.err.println("Need to provide a JavaSpace name as an argument");
      usage();
    } else if (args.length > 2) {
      System.err.println("Too many arguments");
      usage();
    } else if (args.length == 2 && !(args[0].equals("-v"))) {
      System.err.println("Unknown option '" + args[0] + "' specified as argument");
      usage();
    } else
      return true;
    return false;
  }

  public static void usage ( ) {
    System.err.println("\tjava <path args, etc.> LiaisonSpaceScanner [-v] <spacename>");
  }
  
}
