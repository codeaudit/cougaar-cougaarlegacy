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

import com.globalinfotek.coabsgrid.*;
import com.globalinfotek.coabsgrid.fipa98.*;
import fipaos.ont.fipa.ACL;
import fipaos.ont.fipa.fipaman.AgentID;
import fipaos.parser.acl.ACLMessage;
import fipaos.parser.acl.parser.ACLParser;
import java.util.List;
import java.util.Vector;

/**
 * A parser for decoding CoABS FIPA-ACL messages.
 *
 * @author Brandon L. Buteau
 * @version 1.1
 * @since 1.0
 */

public class CoABSParser  {
  
  private boolean debug;
  
  /** 
   * A default no-arg constructor that leaves debug output disabled.
   */
  public CoABSParser () {
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
   * Extracts a CoABS Grid ACL message from the raw text message sent by a
   * CoABS Grid core service agent.
   *
   * @param rawText The complete raw message text as received from a Grid agent
   * @param locatorURL A URL string for a CoABS Grid Jini locator service
   * @return  A FIPA-OS message instance (the form in which Grid core services
   * send messages)
   */
  public ACLMessage parseRaw (String rawText, String locatorURL) {
    if ( debug )
      System.out.println("CoABS message raw text:\n" + rawText);
    String parseText = expandAgentIDs(rawText, locatorURL);
    if ( debug )
      System.out.println("CoABS message with expanded AgentIDs:\n" + parseText);
    ACLMessage amsg = null;
    try {
      amsg = ACLParser.parse(parseText);
    } catch (Exception ex) {
      System.err.println("Failed to parse CoABS message:\n" + ex.getMessage());
      ex.printStackTrace();
      return null;
    }
    return amsg;
  }
  
  private String expandAgentIDs (String rawText, String locatorURL) {
    StringBuffer buf = new StringBuffer(rawText.length() +
                                        2 * (50 + locatorURL.length()));
    int prepos = 0;
    int pos = rawText.indexOf(":sender");
    if ( pos < 0 )
      System.err.println("CoABSParser has a message without a sender");
    buf.append(rawText.substring(prepos, pos - 1));
    prepos = pos + 7;
    pos = rawText.indexOf(":receiver", prepos);
    if ( pos < 0 )
      System.err.println("CoABSParser has a message without a receiver");
    String sender = rawText.substring(prepos, pos).trim();
    buf.append(" :sender (agent-identifier :name " + sender +
               " :addresses (sequence " + locatorURL + "))\n");
    prepos = pos + 9;
    pos = rawText.indexOf(":content", prepos);
    if ( pos < 0 )
      System.err.println("CoABSParser has a message without content");
    String receiver = rawText.substring(prepos, pos).trim();
    buf.append("  :receiver (agent-identifier :name " + receiver +
               " :addresses (sequence " + locatorURL + "))\n");
    buf.append("  " + rawText.substring(pos));
    return buf.toString();   
  }
  
  private String ACLTest() {
    return
    "(inform\n" + 
    ":sender (agent-identifier :name CoABSGridRegistry " +
              ":addresses (sequence jini://hopper.prc.com/serviceIDnnn))\n" +
    ":receiver (agent-identifier :name ALP_Society.test.Management " +
              ":addresses (sequence jini://hopper.prc.com/serviceIDnnn))\n" +
    ":content\n" +
    "  (done\n" +
    "    (action CoabsGridRegistry\n" +
    "      (register-agent\n" +
    "        (:CoABS-agent-description\n" +
    "          (:uniqueID ff5d93c7-3ee8-4496-a200-c20e91275c0b)\n" +
    "          (:name ALP_Society.test.Management)\n" +
    "          (:description An ALP society agent (society:ALP_Society community:test cluster:Management))\n" +
    "          (:organization ALP_Society)\n" +
    "          (:ontologies logistics)))))\n" +
    ":language SL0\n" +
    ":in-reply-to Initial_Registration\n" +
    ":protocol fipa-request\n" +
    ":ontology fipa-agent-management)";
  }

}
