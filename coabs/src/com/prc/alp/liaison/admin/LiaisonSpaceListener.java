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
import net.jini.core.event.EventRegistration;
import net.jini.core.event.RemoteEvent;
import net.jini.core.event.RemoteEventListener;
import net.jini.core.lease.Lease;
import net.jini.space.JavaSpace;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;

/**
 * An RMI-accessible class that implements <CODE>RemoteEventListener</CODE> to provide support
 * for listening and responding to JavaSpace events through a separate thread.
 *
 * @see LiaisonSpaceAdapter
 * @author Brandon L. Buteau
 * @version 1.1
 * @since 1.0
 */
public class LiaisonSpaceListener implements RemoteEventListener {
  
  private JavaSpace space;
  private EventRegistration evreg;
  private LiaisonTrigger trigger;
  
  public LiaisonSpaceListener (JavaSpace space) {
    this.space = space;
  }
  
  public boolean listenFor(Entry template, LiaisonTrigger trig) {
    trigger = trig;
    try {
      UnicastRemoteObject.exportObject(this);
      evreg = space.notify(template, null, this, Lease.FOREVER, null);
      return true;
    } catch (Exception ex) {
      System.err.println("LiaisonSpaceListener():\n" +
                         "Failed to register for events in " + space +
                         "\n" + ex.getMessage());
      ex.printStackTrace();
      return false;
    }
  }
    
  public void notify(RemoteEvent ev) {
    Handler h = new Handler(ev);
    new Thread(h).start();
  }
  
  public class Handler implements Runnable {
    
    private RemoteEvent event;
    
    public Handler(RemoteEvent ev) {
      event = ev;
    }

    public void run() {
      trigger.notify(event);
    }
  }
}

