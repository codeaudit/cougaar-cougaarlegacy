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

import org.cougaar.util.StateModelException;

/**
 * An implementation of LiaisonStateModel; not currently in active use.
 *
 * @author Brandon L. Buteau
 * @version 1.1
 * @since 1.0
 */

public class LiaisonStateModelAdapter
  implements LiaisonStateModel {
  
  /** current reflection of the liaison state **/
  private int liaisonState = UNINITIALIZED;

  /**
   * Liaison state model accessor.
   *
   * @return the current liaison state
   */
  public final int getState() {
    return liaisonState; 
  }

  /** Transition the state to OPEN.
   *  @exception alp.util.StateModelException If Cannot transition to new state.  
   **/
  public synchronized void open() throws StateModelException {
     transitState("open)", UNINITIALIZED, OPEN);
  }


  /** Transition the state to UNINITIALIZED
   *  @exception alp.util.StateModelException If Cannot transition to new state.  
   **/
  public synchronized void close() throws StateModelException {
    transitState("close()", OPEN, UNINITIALIZED);
  }

  /** Transition the state to allow INITIATE interactions
   *  @exception alp.util.StateModelException If Cannot transition to new state.  
   **/
  public synchronized void enableInitiate() throws StateModelException {
    if (liaisonState == UNINITIALIZED || (liaisonState & INITIATE) != 0) {
      throw new StateModelException(""+this+".enableInitiate() called in inappropriate state ("+liaisonState+")");
    } else {
      liaisonState = liaisonState + INITIATE;
    }
  }

  /** Transition the state to disallow INITIATE interactions
   *  @exception alp.util.StateModelException If Cannot transition to new state.  
   **/
  public synchronized void disableInitiate() throws StateModelException {
    if (liaisonState == UNINITIALIZED || (liaisonState & INITIATE) == 0) {
      throw new StateModelException(""+this+".disableInitiate() called in inappropriate state ("+liaisonState+")");
    } else {
      liaisonState = liaisonState - INITIATE;
    }
  }

  /** Transition the state to allow RESPOND interactions
   *  @exception alp.util.StateModelException If Cannot transition to new state.  
   **/
  public synchronized void enableRespond() throws StateModelException {
    if (liaisonState == UNINITIALIZED || (liaisonState & RESPOND) != 0) {
      throw new StateModelException(""+this+".enableRespond() called in inappropriate state ("+liaisonState+")");
    } else {
      liaisonState = liaisonState + RESPOND;
    }
  }

  /** Transition the state to disallow RESPOND interactions
   *  @exception alp.util.StateModelException If Cannot transition to new state.  
   **/
  public synchronized void disableRespond() throws StateModelException {
    if (liaisonState == UNINITIALIZED || (liaisonState & INITIATE) == 0) {
      throw new StateModelException(""+this+".disableRespond() called in inappropriate state ("+liaisonState+")");
    } else {
      liaisonState = liaisonState - RESPOND;
    }
  }

/**
 * Whether or not the current state includes INITIATE ability.
 *
 * @return <CODE>true</CODE> if the current state allows the
 * local agent to initiate interactions with external
 * agents; false otherwise
 */
  public boolean canInitiate() {
    return (liaisonState & INITIATE) != 0;
  }

  /**
   * Whether or not the current state includes RESPOND ability.
   *
   * @return <CODE>true</CODE> if the current state allows the
   * local agent to respond to interactions initiated by
   * external agents; false otherwise
   */
  public boolean canRespond() {
    return (liaisonState & RESPOND) != 0;
 }

  /** Accomplish a simple state transition.
   *   Be careful and complain if we are in an inappropriate starting state.
   *   @exception alp.util.StateModelException If Cannot transition to new state.   
   **/
  private synchronized void transitState(String op, int expectedState, int endState) throws StateModelException {
    if (liaisonState != expectedState) {
      throw new StateModelException(""+this+"."+op+" called in inappropriate state ("+liaisonState+")");
    } else {
      liaisonState = endState;
    }
  }
}  

  
  
