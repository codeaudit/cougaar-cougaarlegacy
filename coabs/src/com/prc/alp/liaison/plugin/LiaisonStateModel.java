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
 * Defines state transitions for liaison with external agent socieities;
 * not currently in active use.
 * @author Brandon L. Buteau
 * @version 1.1
 * @since 1.0
 */

public interface LiaisonStateModel {

  /** Must be initialized before any interactions are possible **/
  public static final int UNINITIALIZED = 0;
  /** Initialized and attached to an external society **/
  public static final int OPEN = 1;
  /** Able to initiate interactions with external agents **/
  public static final int INITIATE = 2;
  /** Able to respond to interactions initiated by other agents **/
  public static final int RESPOND = 4;

  /** Initialize.  Transition object from UNINITIALIZED to OPEN state.
   *  @exception alp.util.StateModelException Cannot transition to new state.
   **/

  void open() throws StateModelException;

  /** Object should transition to the UNINITIALIZED state.
   *  @exception alp.util.StateModelException Cannot transition to new state.
   **/

  void close() throws StateModelException;

  /** Object should add INITIATE to its current state.
   *  @exception alp.util.StateModelException Cannot transition to new state.
   **/

  void enableInitiate() throws StateModelException;

  /** Called object should remove INITIATE from its state.
   *  @exception alp.util.StateModelException Cannot transition to new state.
   **/

  void disableInitiate() throws StateModelException;

  /** Object should add RESPOND to its current state.
   *  @exception alp.util.StateModelException Cannot transition to new state.
   **/

  void enableRespond() throws StateModelException;

  /** Called object should remove INITIATE state from its state.
   *  @exception alp.util.StateModelException Cannot transition to new state.
   **/

  void disableRespond() throws StateModelException;

  /**
   * Whether or not the current state includes INITIATE ability.
   *
   * @return <CODE>true</CODE> if the current state allows the
   * local agent to initiate interactions with external
   * agents; false otherwise
   */

  boolean canInitiate();

  /**
   * Whether or not the current state includes RESPOND ability.
   *
   * @return <CODE>true</CODE> if the current state allows the
   * local agent to respond to interactions initiated by
   * external agents; false otherwise
   */

  boolean canRespond();

  /** Return the current state of the object
   * @return object state
   **/

  int getState();
}
 
