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

import com.prc.alp.liaison.LConstants;
import com.prc.alp.liaison.plugin.*;

/**
 * An independent thread of activity involving the exchange of information
 * between an ALP/Cougaar agent and one or more external agents.  The <code>Interaction</code>
 * engine executes under the control of an <code>Interactor</code> (which knows how to
 * behave during the interaction), a <code>LiaisonDeputy</code> (which maintains permission
 * and intermediate/final results generated during the interaction), and a state
 * (which may be any arbitrary object).
 * <P>
 * Each interaction starts with the execution of its interactor's <code>begin</code> method,
 * which returns a new state.  The interaction then continues to select successive
 * actions by invoking the interactor's <code>nextAction</code> method (which returns
 * the <code>ActionType</code> of the next action to perform) and then performing the chosen
 * action.  Each action produces a new state which is available to control subsequent
 * actions.  This cycle continues until a "NORMAL" or "ABNORMAL" <code>ActionType</code> has been
 * selected and performed, or until a timeout exception has occurred.
 * <P>
 * Interactions are implemented as independent threads for two primary reasons:
 * <ul>
 * <li>ALP/Cougaar agents may engage in multiple, concurrent, and time-sensitive
 *     interactions without worrying about delays in one affecting another</li>
 * <li>ALP/Cougaar agents have the flexibility of starting an interaction and
 *     blocking until it completes or polling later to see when it is done,
 *     regardless of whether the underlying interaction itself is normally
 *     synchronous or asynchronous</li>
 * </ul>
 *
 * @author Brandon L. Buteau
 * @version 1.1
 * @since 1.0
 * @see ActionType
 * @see Interactor
 * @see com.prc.alp.liaison.plugin.LiaisonDeputy
 */
public class Interaction implements Runnable {
  
  private Thread iThread = null;
  private Interactor iActor = null;
  private Object initializer = null;
  private Object state = null;
  private boolean active = false;
  private boolean done = false;
  private boolean debug = false;
  private String ID = null;
  private long startTime;
  private long actionEnd = LConstants.FOREVER;
  private boolean actionExpired = false;
  private LiaisonDeputy deputy = null;
  
  public Interaction (LiaisonDeputy deputy, Interactor ia, Object init) {
    
    ID = createID(ia);
    iThread = new Thread(this, ID);
    iActor = ia;
    initializer = init;
    this.deputy = deputy;
  }
  
  public final Object start (boolean wait) {
    deputy.putInteraction(ID, this);
    startTime = System.currentTimeMillis();
    iThread.start();
    if (wait) {
      while ( iThread.isAlive() ) // now wait for it to finish
        try {
          Thread.sleep(1); // faster than yield()ing
        } catch (InterruptedException ex) { }
      return deputy.finishInteraction(ID);
    }
    return null;
  }
  
  public final void run () {

    System.out.print("[");
    //if ( debug )
    //  System.out.println("Interaction " + name + " is beginning");
    active = true;
    while ( (iThread != null) && active ) {
      ActionType act = null;
      if ( debug )
        System.out.println("Interaction " + ID + " is in state: " + state);
      try {
        act = iActor.nextAction(state, this, actionExpired);
        Object result = dispatchAction(act, state);
        if ( act.getActionValue() == ActionType.END ) {
          if ( result == InteractionResult.ERROR )
            iActor.abnormalEnd(state, this);
          else {
            //System.out.println("Interaction saving result of type " + 
            //                    result.getClass().getName());
            deputy.saveResult(ID, result);
          }
          active = false;
        } else if ( act.getActionValue() == ActionType.ABORT ) {
          active = false;
        } else if ( !actionExpired && result != InteractionResult.TIMEOUT )
          state = result;
      } catch (InterruptedException ex) {
        if ( isStopping()  && debug ) {
          // interaction has been killed from outside via .stop()
          System.out.println("Interaction " + ID + " terminated by timeout");
          break;
        } else if ( hasActionExpired() && debug )
          System.out.println("Interaction " + ID + " " + act + " expired");
 
      }
    }
    if ( active ) { // this means someone invoked .stop() on this interaction
      try {
        iActor.abnormalEnd(state, this);
      } catch (Exception ex) { /* doesn't matter at this point */ }
      active = false;
    }
    //if ( debug )
    //  System.out.println("Interaction " + ID + " has ended");
    System.out.print("]");
    done = true;
  }

  public final synchronized void stop () {
    if ( iThread == null )
      return;
    Thread t = iThread;
    iThread = null;
    t.interrupt();
  }
  
  public final Object finish () {
    return deputy.finishInteraction(ID);
  }
  
  public final synchronized boolean isActive () {
    if ( iThread == null )
      return false;
    else
      return active && iThread.isAlive();
  }
  
  public final synchronized boolean isStopping () {
    if ( iThread == null && !done )
      return true;
    else
      return false;
  }
  
  public final synchronized boolean isDone () {
    return done;
  }
  
  public final synchronized void setActionEnd (ActionType act) {
    if ( act.getTimeLimit() >= 0 )
      actionEnd = System.currentTimeMillis() + act.getTimeLimit();
    else
      actionEnd = LConstants.FOREVER;
    actionExpired = false;
  }
  
  public final synchronized boolean tryToExpireAction() {
    if ( actionEnd >= 0 && !actionExpired &&
         actionEnd < System.currentTimeMillis() ) {
      actionExpired = true;
      iThread.interrupt();
      return true;
    } else
      return false;
  }
  
  public final synchronized boolean hasActionExpired () {
    return actionExpired;
  }
  
  public final boolean getDebug () {
    return debug;
  }
  
  public final void setDebug (boolean flag) {
    debug = flag;
  }
  
  public final String getID () {
    return ID;
  }
  
  public final long getStartTime () {
    return startTime;
  }
  
  public final Interactor getInteractor () {
    return iActor;
  }
  
  public void sleep (long interval) 
    throws ActionTimeoutException, InteractionTimeoutException {
      if ( interval <= 0 ) {
        if ( isStopping() )
          throw new InteractionTimeoutException();
        else if ( hasActionExpired() )
          throw new ActionTimeoutException();
      } else try {
        Thread.sleep(interval);
      } catch (InterruptedException ex) {
        if ( isStopping() )
          throw new InteractionTimeoutException();
        else if ( hasActionExpired() )
          throw new ActionTimeoutException();
      }      
  }
  
  private Object dispatchAction (ActionType act, Object state) 
    throws InterruptedException {
    //if ( debug )
    //  System.out.println("Interaction " + name + " about to perform " + act);
    Object result = InteractionResult.TIMEOUT;
    setActionEnd(act);
    try {
      switch ( act.getActionValue() ) {
        case ActionType.BEGIN:
          result = iActor.begin(initializer, this); break;
        case ActionType.RECEIVE:
          result = iActor.receive(state, this); break;
        case ActionType.SEND:
          result = iActor.send(state, this); break;
        case ActionType.END:
          result = iActor.normalEnd(state, this); break;
        case ActionType.ABORT:
          iActor.abnormalEnd(state, this); break;
        default: // don't think this is possible
          System.err.println("Interaction " + ID + " reached invalid state: " + act);
      }
    } catch (InterruptedException ex) {
      sleep(0); 
    }
    actionEnd = LConstants.FOREVER;
    return result;
  }
  
  private String createID (Interactor ia) {
    String classname = ia.getClass().getName();
    int pos = classname.lastIndexOf('.');
    String ID = (pos < 0) ? classname : classname.substring(pos + 1);
    String creation = "" + System.currentTimeMillis();
    ID = ID + "_" + creation.substring(creation.length() - 8);
    long tag = Math.round(10000 + 10000 * Math.random());
    return ID + ("" + tag).substring(1);
  }
    
}

