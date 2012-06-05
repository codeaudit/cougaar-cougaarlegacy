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

/**
 * Describes an external agent (not in an ALP/Cougaar society) as an entry
 * suitable for reading from or writing to a JavaSpace.
 *
 * @author Brandon L. Buteau
 * @version 1.1
 * @since 1.0
 */
public class ExternalAgentReference extends AgentReference {

  public String externalID = null;

  public ExternalAgentReference() { }
  
  public static ExternalAgentReference create (String society, String neighborhood,
                                       String agent, String owner,
                                       String description, String externalID) {
    ExternalAgentReference ref = new ExternalAgentReference();
    ref.society = society;
    ref.neighborhood = neighborhood;
    ref.agent = agent;
    ref.owner = owner;
    ref.description = description;
    ref.externalID = externalID;
    return ref;
  }
  
  public String toString () {
    return
      "< " + super.toString() +
      " externalID:" + externalID + ">";
  }

}

