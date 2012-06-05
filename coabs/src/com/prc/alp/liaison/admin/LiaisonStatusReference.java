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
import com.prc.alp.liaison.LConstants;

/**
 * Describes the status of liaisons between an ALP/Cougaar agent and an
 * external agent as an entry suitable for reading from or writing to a JavaSpace.
 *
 * @author Brandon L. Buteau
 * @version 1.1
 * @since 1.0
 */
public class LiaisonStatusReference extends ExternalAgentReference {

  public String ALPID = null;
  public Boolean ALPCanInitiate = null;
  public Boolean ALPCanRespond = null;
  
  private static Boolean CAN_INITIATE_DEFAULT = 
    new Boolean(LConstants.DEFAULT_LIAISON_STATUS);
  private static Boolean CAN_RESPOND_DEFAULT = 
    new Boolean(LConstants.DEFAULT_LIAISON_STATUS);
  
  public LiaisonStatusReference () { }
  
  public static LiaisonStatusReference create (String society, String neighborhood,
                                               String agent, String owner,
                                               String description, String ALPID,
                                               String externalID) {
    LiaisonStatusReference ref = new LiaisonStatusReference();
    ref.society = society;
    ref.neighborhood = neighborhood;
    ref.agent = agent;
    ref.owner = owner;
    ref.description = description;
    ref.ALPID = ALPID;
    ref.externalID = externalID;
    return ref;
  }
  
  public static LiaisonStatusReference create (ExternalAgentReference eref,
                                               String ALPID) {
    LiaisonStatusReference ref = new LiaisonStatusReference();
    ref.society = eref.society;
    ref.neighborhood = eref.neighborhood;
    ref.agent = eref.agent;
    ref.owner = eref.owner;
    ref.description = eref.description;
    ref.ALPID = ALPID;
    ref.externalID = eref.externalID;
    return ref;
  }
    
  public static LiaisonStatusReference createDefault (String society, 
    String neighborhood, String agent, String owner,
    String description, String ALPID, String externalID) {
    LiaisonStatusReference ref = create(society, neighborhood, agent, owner,
                                        description, ALPID, externalID);
    ref.ALPCanInitiate = CAN_INITIATE_DEFAULT;
    ref.ALPCanRespond = CAN_RESPOND_DEFAULT;
    return ref;
  }
  
  public static LiaisonStatusReference createDefault (ExternalAgentReference eref,
                                                      String ALPID) {
    LiaisonStatusReference ref = create(eref, ALPID);
    ref.ALPCanInitiate = CAN_INITIATE_DEFAULT;
    ref.ALPCanRespond = CAN_RESPOND_DEFAULT;
    return ref;
  }
  
  public boolean canInitiate() {
    return ALPCanInitiate.booleanValue();
  }
  
  public boolean canRespond() {
    return ALPCanRespond.booleanValue();
  }
    
  public String toString () {
    String init = "null";
    if ( ALPCanInitiate != null )
      init = ALPCanInitiate.toString();
    String resp = "null";
    if ( ALPCanRespond != null )
      resp = ALPCanRespond.toString();
    return
      "<LSR " + super.toString() +
      " ALPID:" + ALPID + 
      " canInitiate:" + init + 
      " canRespond:" + resp + ">";
  }

}

