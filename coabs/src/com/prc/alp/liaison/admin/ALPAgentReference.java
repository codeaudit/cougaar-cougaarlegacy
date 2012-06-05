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
 * Describes an ALP/Cougaar agent as an entry suitable for reading from or 
 * writing to a JavaSpace.
 *
 * @author Brandon L. Buteau
 * @version 1.1
 * @since 1.0
 */
public class ALPAgentReference extends AgentReference {
  
  public String ALPID = null;
  public Boolean isEnabled = null;
  public Boolean delegatesAuthority = null;

  private static Boolean IS_ENABLED_DEFAULT = new Boolean(true);
  private static Boolean DELEGATES_DEFAULT = new Boolean(false);

  public ALPAgentReference() {
  }
  
  public static ALPAgentReference create (String society, String neighborhood,
                                       String agent, String owner,
                                       String description, String ALPID) {
    ALPAgentReference ref = new ALPAgentReference();
    ref.society = society;
    ref.neighborhood = neighborhood;
    ref.agent = agent;
    ref.owner = owner;
    ref.description = description;
    ref.ALPID = ALPID;
    ref.isNeighborhoodComplete = new Boolean(false);
    ref.neighborhoodSeparator = null;
    return ref;
  }

  public static ALPAgentReference createDefault
    (String society, String neighborhood, String agent, String owner,
     String description, String ALPID) {
    ALPAgentReference ref = create(society, neighborhood, agent, owner,
                                   description, ALPID);
    ref.isNeighborhoodComplete = new Boolean(false);
    ref.neighborhoodSeparator = null;
    ref.delegatesAuthority = DELEGATES_DEFAULT;
    ref.isEnabled = IS_ENABLED_DEFAULT;
    return ref;
  }
  
  public boolean isEnabled () {
    return isEnabled.booleanValue();
  }
  
  public boolean isDelegating () {
    return delegatesAuthority.booleanValue();
  }

  public String toString () {
    String enab = "null";
    if ( isEnabled != null )
      enab = isEnabled.toString();
    String dele = "null";
    if ( delegatesAuthority != null )
      dele = delegatesAuthority.toString();
    return
      "< " + super.toString() +
      " ALPID:" + ALPID + 
      " isEnabled:" + enab + 
      " delegates:" + dele + ">";
  }

}

