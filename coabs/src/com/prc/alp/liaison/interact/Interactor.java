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

/**
 * Defines and implements the behavior associated with a particular kind of <code>Interaction</code>
 * between an ALP/Cougaar agent and one or more external agents.  Each interactor is used
 * by the <code>Interaction</code> engine to execute the functions required to perform that type
 * of interaction correctly.
 * <P>
 * Each of the different action methods of an <code>Interactor</code> 
 * (<code>begin, receive, send, normalEnd,</code> and <code>abnormalEnd</code>) is given an
 * object representing the current state of the interaction and a handle on a single 
 * <code>Interaction</code> instance.  Each uses this information to do whatever is
 * necessary for the current state of that <code>Interaction</code> and returns a new
 * state object.

 * @author Brandon L. Buteau
 * @version 1.1
 * @since 1.0
 * @see Interaction
 */
public interface Interactor
{
  /** 
   * Performs whatever initialization is required at the start of an
   * <CODE>Interaction</CODE>.
   *
   * @param o An arbitrary object supplied to the constructor of an
   * <CODE>Interaction</CODE> instance that provides some
   * context for the start of that interaction
   * @param iaction A references to a particular <CODE>Interaction</CODE>
   *
   * @throws ActionTimeoutException When a timelimit specified for initializing this
   * <CODE>Interaction</CODE> has expired
   * @throws InteractionTimeoutException When a timelimit specified for this entire
   * <CODE>Interaction</CODE> has expired
   *
   * @return An arbitrary object representing a new state for the
   * particular<CODE>Interaction</CODE>
   */
  public Object begin (Object o, Interaction iaction)
    throws ActionTimeoutException, InteractionTimeoutException;
  
  /**
   * Performs whatever is required to receive information from one or more
   * external agents during an <CODE>Interaction</CODE>.
   *
   * @param o An arbitrary object representing the current state of an
   * <CODE>Interaction</CODE> instance
   * @param iaction A reference to a particular <CODE>Interaction</CODE>
   *
   * @throws ActionTimeoutException When a timelimit specified for receiving
   * information during this <CODE>Interaction</CODE> has expired
   * @throws InteractionTimeoutException When a timelimit specified for this entire
   * <CODE>Interaction</CODE> has expired
   *
   * @return An arbitrary object representing a new state for the
   * particular<CODE>Interaction</CODE>
   */
  public Object receive (Object o, Interaction iaction)
    throws ActionTimeoutException, InteractionTimeoutException;
  
  /** 
   * Performs whatever is required to send information to one or more
   * external agents during an <CODE>Interaction</CODE>.
   *
   * @param o An arbitrary object representing the current state of an
   * <CODE>Interaction</CODE> instance
   * @param iaction A reference to a particular <CODE>Interaction</CODE>
   *
   * @throws ActionTimeoutException When a timelimit specified for sending
   * information during this <CODE>Interaction</CODE> has expired
   * @throws InteractionTimeoutException When a timelimit specified for this entire
   * <CODE>Interaction</CODE> has expired
   *
   * @return An arbitrary object representing a new state for the
   * particular<CODE>Interaction</CODE>
   */
  public Object send (Object o, Interaction iaction)
    throws ActionTimeoutException, InteractionTimeoutException;
  
  /** 
   * Performs whatever action or clean-up is required at the end of an
   * <CODE>Interaction</CODE> that has completed normally.
   *
   * @param o An arbitrary object representing the current state of an
   * <CODE>Interaction</CODE> instance
   * @param iaction A reference to a particular <CODE>Interaction</CODE>
   *
   * @throws ActionTimeoutException When a timelimit specified for completing the
   * clean-up of this <CODE>Interaction</CODE> has expired
   * @throws InteractionTimeoutException When a timelimit specified for this entire
   * <CODE>Interaction</CODE> has expired
   *
   * @return An arbitrary object representing a final result for the
   * particular<CODE>Interaction</CODE>
   */
  public Object normalEnd (Object o, Interaction iaction)
    throws ActionTimeoutException, InteractionTimeoutException;
   
  /** 
   * Performs whatever action or clean-up is required at the end of an
   * <CODE>Interaction</CODE> that has completed abnormally.
   *
   * @param o An arbitrary object representing the current state of an
   * <CODE>Interaction</CODE> instance
   * @param iaction A reference to a particular <CODE>Interaction</CODE>
   *
   * @throws ActionTimeoutException When a timelimit specified for completing the
   * clean-up of this <CODE>Interaction</CODE> has expired
   * @throws InteractionTimeoutException When a timelimit specified for this entire
   * <CODE>Interaction</CODE> has expired
   */
  public void abnormalEnd (Object o, Interaction iaction)
    throws ActionTimeoutException, InteractionTimeoutException;
  
  /** 
   * Selects the next action to be performed in the course of a given
   * <CODE>Interaction</CODE>
   * 
   *
   * @param o An arbitrary object representing the current state of an
   * <CODE>Interaction</CODE> instance
   * @param iaction A reference to a particular <CODE>Interaction</CODE>
   * @param priorActExpired <CODE>false</CODE> if the just-finished prior action
   * completed normally or with a (non-timeout) error; <CODE>true</CODE> if the
   * just-finished prior action was interrupted by a timeout.  If the prior act
   * did expire, the current state is the same as the one provided in the prior
   * call to <CODE>nextAction</CODE>; in other words, <CODE>nextAction</CODE> will
   * be called twice successively with the same state (but the second time the
   * value of <CODE>priorActExpired</CODE> will be <CODE>true</CODE>).  This allows
   * it to perform some intelligent action in the event of an action timeout, such 
   * as trying a different action, or trying the same action with a different timeout.
   * (Note that the <CODE>ActionType</CODE> returned includes a specification of the
   * action timeout for the next action to perform.)
   *
   * @return The next action to be performed in the course of the particular
   * <CODE>Interaction</CODE>
   */
  public ActionType nextAction (Object o, Interaction iaction, boolean priorActExpired);
  
}

