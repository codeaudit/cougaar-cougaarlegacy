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
 * Note:  Heavily modified and adapted from Jini v1.1Beta class
 *        com.sun.jini.outrigger.LookupFinder
 * 
 */
package com.prc.alp.liaison.admin;

import net.jini.core.lookup.*;
import net.jini.core.discovery.LookupLocator;
import net.jini.discovery.*;
import net.jini.core.entry.*;
import net.jini.lookup.entry.Name;
import com.sun.jini.outrigger.*;

import java.net.MalformedURLException;
import java.util.Iterator;




/**
 * A class that implements the methods needed locate a service with a given name
 * in a Jini(tm) Lookup service.
 *
 * @author Brandon L. Buteau
 * @version 1.1
 * @since 1.0
 */
public class NamedServiceFinder {

    private static final boolean DEBUG = false;

    /**
     * Using the Jini lookup service returned by <code>getLocator</code>
     * find the service registered with a
     * <code>net.jini.lookup.entry.Name</code> attribute who's value
     * is <code>name</code>.  If no service is registered under the
     * specified name retry until such a service appears.
     */
    public static Object find (String locatorURL, String name) {
	Object tmpobj = null;

	try {
            ServiceRegistrar reg = (ServiceRegistrar) getLocator(locatorURL).getRegistrar();
            if (DEBUG)
              System.out.println("registrar = " + reg);
	    Entry[] attrs = new Entry[1];
	    Name n = new Name();
	    n.name = name;
	    attrs[0] = n;

	    ServiceTemplate tmpl = new ServiceTemplate(null, null, attrs);

	    if (DEBUG) {
	        System.out.println("LiaisonSpaceFinder.find(): name = " + name);
	        System.out.println("LiaisonSpaceFinder.find(): registrar = " +
							    reg);
	        System.out.println("LiaisonSpaceFinder.find(): tmpl = " + tmpl);
	    }

	    do {
	        tmpobj = reg.lookup(tmpl);
                if (DEBUG)
                  System.out.println("tmpobj in loop = " + tmpobj);
		if (tmpobj == null) {
		    try {
			System.out.println("waiting for " + name);
			Thread.sleep(5000);
		    } catch (Exception te) {
		    }
		}

	    } while (tmpobj == null);
	} catch (Exception e) {
	    System.err.println("LiaisonSpaceFinder.find(): " + e.getMessage());
	    e.printStackTrace();
	}
        if (DEBUG)
	  System.out.println("Found JavaSpace " + name + " = " + tmpobj);
	return tmpobj;
    }

  private static LookupLocator getLocator(String locatorRef) {
    try {
      LookupLocator loc = new LookupLocator(locatorRef);
      return loc;
    } catch (MalformedURLException ex) {
      System.err.println("LiaisonSpaceFinder: bad URL for locator: " + locatorRef);
      return null;
    }
  }
  
}
