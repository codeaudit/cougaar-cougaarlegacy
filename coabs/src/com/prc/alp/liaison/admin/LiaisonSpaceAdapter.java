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
import net.jini.space.JavaSpace;


/**
 * A class that extends <CODE>LiaisonSpace</CODE> to provide support for
 * listening and responding to JavaSpace events.
 *
 * @author Brandon L. Buteau
 * @version 1.1
 * @since 1.0
 */
public class LiaisonSpaceAdapter extends LiaisonSpace {
  
  private LiaisonSpaceListener listener;
  private Entry en;
  private LiaisonTrigger trigger;
  
  public LiaisonSpaceAdapter (String name, Entry template, LiaisonTrigger trig) {
    super(name);
    listener = new LiaisonSpaceListener(getSpace());
    listener.listenFor(template, trig);
    en = template;
    trigger = trig;
  }
  
  public LiaisonSpaceAdapter (JavaSpace space, String name, Entry template,
                              LiaisonTrigger trig) {
    super(space, name);
    listener = new LiaisonSpaceListener(space);
    listener.listenFor(template, trig);
    en = template;
    trigger = trig;
  }
  
}

