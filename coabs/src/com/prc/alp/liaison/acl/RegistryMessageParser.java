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

package com.prc.alp.liaison.acl;

/**
 * The RegistryMessageParser class extracts a service ID string from
 * a CoABS Grid Registry registration message.
 *
 * @author Brandon L. Buteau
 * @version 1.1
 * @since 1.0
 */

public class RegistryMessageParser  {
  
  private boolean debug;
  
  /**
   * A default no-arg constructor that leaves debug output disabled.
   */
  public RegistryMessageParser () {
    debug = false;
  }
  
  /** 
   * Retrieves the current debug status for this parser.
   *
   * @return A value of <CODE>true</CODE> indicating that debug output is
   * enabled or<CODE>false</CODE> indicating that debug output is
   * disabled.
   */
  public boolean getDebug () {
    return debug;
  }
  
  /**
   * Sets the current debug status for this parser.
   *
   * @param flag A value of <CODE>true</CODE> means to enable debug output,
   * while a <CODE>false</CODE> means to disable debug output.
   */
  public void setDebug (boolean flag) {
    debug = flag;
  }
  
  /**
   * Extracts the unique ID from a CoABS Grid Registry reply to a registration
   * request.
   *
   * @param content The raw text of the complete reply returned by the Grid Registry
   * @return A unique identifier for this registration; typically, a Jini Service ID
   * for the registering agent
   */
  public String parse (String content) {
    if ( debug )
      System.out.println("CoABS registry message raw text:\n" + content);
    int pos = content.indexOf("(:uniqueID ");
    if ( pos < 0 ) {
      if ( debug )
        System.out.println("Couldn't find :uniqueID field");
      return null;
    }
    int end = content.indexOf(")", pos);
    if ( end < 0 )
      return null;
    return content.substring(pos + 11, end - 1);    
  }
  
}
