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

package com.prc.alp.liaison.interact;

import com.prc.alp.liaison.admin.*;
import com.prc.alp.liaison.plugin.*;

/**
 * An on-demand generator of <code>Interaction</code> objects.  These factories are primarily
 * intended for use by a <code>LiaisonDeputy</code> that needs to be able to spawn new
 * interactions in response to requests made by external agents; however, they could also
 * be used for initiating interactions by ALP/Cougaar agents as well.
 * <P>
 * Each <code>InteractionFactory</code> can operate in either a shared or non-shared mode, which
 * affects how it works with the <code>Interactor</code> its constructor is given.  If a factory
 * is working in shared mode, then the single given <code>Interactor</code> is shared by all of
 * the interactions created by that factory; such interactors should not maintain any
 * internal state.  If the factory is working in non-shared mode, then the single given
 * <code>Interactor</code> is cloned on demand for each new <code>Interaction</code>; such
 * interactors must extend <code>CloneableInteractor</code>.
 *
 * @author Brandon L. Buteau
 * @version 1.1
 * @since 1.1
 * @see CloneableInteractor
 * @see Interaction
 * @see Interactor
 * @see com.prc.alp.liaison.plugin.LiaisonDeputy
 */
public abstract class InteractionFactory {
  
  protected Interactor iActor = null;
  protected LiaisonDeputy deputy = null;
  private String name = null;
  private boolean sharedInteractor = false;
  private boolean debug = false;
  
  public InteractionFactory (LiaisonDeputy deputy, Interactor ia, 
                             String name, boolean shared) {
    this.deputy = deputy;
    iActor = ia;
    this.name = name;
    sharedInteractor = shared;
    Class c = null;
    try {
      c = Class.forName("com.prc.alp.liaison.interact.MultithreadInteractor");
    } catch (ClassNotFoundException ex) { /* better not happen! */ }
    if (  !c.isInstance(ia) ) 
      throw new RuntimeException("InteractionFactory: factory interactor must be multithreaded");
    try {
      c = Class.forName("com.prc.alp.liaison.interact.CloneableInteractor");
    } catch (ClassNotFoundException ex) { /* better not happen! */ }
    if ( !shared && !c.isInstance(ia) ) 
      throw new RuntimeException("InteractionFactory: non-shareable interactor must be cloneable");
  }
  
  public abstract ExternalAgentReference sourceAgent (Object obj);
  
  public abstract boolean handle (Object obj);
  
  public Interaction createInteraction (Object init) {
    if ( !okToRespond(init) || !handle(init) )
      return null;
    Interactor newIA = null;
    if ( sharedInteractor )
      newIA = iActor;
    else
      newIA = (Interactor) ((CloneableInteractor) iActor).clone();
    if ( debug )
      System.out.println("InteractionFactory " + name + " creating new interaction");
    return new Interaction(deputy, newIA, init);
  }
  
  public boolean getDebug () {
    return debug;
  }
  
  public void setDebug (boolean b) {
    debug = b;
  }
  
  private boolean okToRespond (Object obj) {
    ExternalAgentReference regRef = sourceAgent(obj);
    if ( regRef != null && !deputy.canRespondTo(regRef) )
      if ( !deputy.waitForPermission() )
        return false;
    return true;
  }
  
}

