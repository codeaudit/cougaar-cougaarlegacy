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

/**
 * Removes all of the entries from a JavaSpace.  This is useful for cleaning up a
 * persistent or transient JavaSpace that has become corrupted, without having to
 * tinker with the RMI activation demon.
 *
 * @author Brandon L. Buteau
 * @version 1.1
 * @since 1.0
 */
public class LiaisonSpaceCleaner {
  
  public static void main(String[] args) {
    long removalCount = 0;
    long unusableCount = 0;
    if (!validateArgs(args))
            System.exit(-1);
    boolean verbose = args.length == 2 ? true : false;
    String spaceName = verbose ? args[1] : args[0];
    JavaSpace space = null;
    try {
      space = LiaisonSpaceAccessor.getSpace(spaceName);
    } catch (Exception e) {
      e.printStackTrace();
    }    

    Object ob = null;

    for (;;) {
      try {
        ob = (Object) space.takeIfExists(null, null, Long.MAX_VALUE);
        if (ob == null) {
          System.out.println("Removed " + removalCount + 
                              " object" + (removalCount != 1 ? "s" : "") + 
                              " from " + spaceName);
          if (unusableCount > 0)
            System.out.println(unusableCount + " object" + 
                               (unusableCount != 1 ? "s were" : " was") + 
                               " unusable");
          System.exit(0);
        } else {
          if (verbose)
            System.out.println("Removed a " + ob.getClass().getName());
          removalCount++;
        }
      } catch (UnusableEntryException e) {
        // Some kind of bad entry, but it was still taken, so count it
        unusableCount++;
        removalCount++;
      } catch (Exception e) {
        e.printStackTrace();
      }
    }
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
    System.err.println("\tjava <path args, etc.> SpaceCleaner [-v] <spacename>");
  }
  
}
