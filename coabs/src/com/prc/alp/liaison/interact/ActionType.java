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
 * Defines the allowable types of actions within interactions and their generic
 * results.
 *
 * @author Brandon L. Buteau
 * @version 1.1
 * @since 1.1
 */
public class ActionType {
  public static final int BEGIN = 0;
  public static final int RECEIVE = 1;
  public static final int SEND = 2;
  public static final int END = 3;
  public static final int ABORT = 4;
  //public static final NoResult NO_RESULT = new NoResult();
  //public static final ErrorResult ERROR_RESULT = new ErrorResult();
  
  private int a;
  private long timeLimit = LConstants.FOREVER;
  
  public ActionType (String act) {
    if ( act.equals("BEGIN") )
      a = BEGIN;
    else if ( act.equals("RECEIVE") )
      a = RECEIVE;
    else if ( act.equals("SEND") )
      a = SEND;
    else if ( act.equals("END") )
      a = END;
    else if ( act.equals("ABORT") )
      a = ABORT;
    else
      throw new RuntimeException("ActionType initialized with improper action type \"" +
                         act + "\"");
  }
  
  public ActionType (String act, long timeLimit) {
    this(act);
    this.timeLimit = timeLimit;
  }
  
  public ActionType (int i) {
    if ( i < 0 || i > 4 )
      throw new RuntimeException("ActionType initialized with improper action type " + i);
    a = i;
    return;
  }
  
  public ActionType (int i, long timeLimit) {
    this(i);
    this.timeLimit = timeLimit;
    return;
  }
  
  public int getActionValue () {
    return a;
  }
  
  public long getTimeLimit () {
    return timeLimit;
  }
  
  public String toString () {
    switch (a) {
      case BEGIN: return "BEGIN";
      case RECEIVE: return "RECEIVE";
      case SEND:    return "SEND";
      case END:     return "END";
      case ABORT:   return "ABORT";
    }
    return null;
  }
  
  /*
  public static class NoResult {
    public NoResult() {};
  }
  
  public static class ErrorResult {
    public ErrorResult() {};
  }
  */
}

