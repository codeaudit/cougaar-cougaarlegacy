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

/**
 * Uniquely classed objects that allow the specific action methods of an <code>Interactor</code>
 * to identify whether their execution was successful.  These were implemented to allow action
 * methods to return any other Java objects as a result with application-specific meaning, so it
 * is not necessary to reserve values like <code>null</code> or <code>Boolean(true)</code> for this purpose.
 *
 * @author Brandon L. Buteau
 * @version 1.1
 * @since 1.1
 * @see Interactor
 * @see com.prc.alp.liaison.plugin.LiaisonDeputy
 */
public class InteractionResult {

  /**
   * Indicates an action that completed normally but has no other result value
   * to return.
   */
  public static final NormalResult NORMAL = new NormalResult();
  /**
   * Indicates an action that did not completed normally because of a condition
   * unrelated to a timeout.
   */
  public static final ErrorResult ERROR = new ErrorResult();
  /**
   * Indicates an action that did not completed normally because of a condition
   * related to a timeout, either for that single action or for its entire interaction.
   */
  public static final TimeoutResult TIMEOUT = new TimeoutResult();
  
  private static class NormalResult {
    public NormalResult() {}
  }
  
  private static class ErrorResult {
    public ErrorResult() {}
  }
  
  private static class TimeoutResult {
    public TimeoutResult() {}
  }
  
}

