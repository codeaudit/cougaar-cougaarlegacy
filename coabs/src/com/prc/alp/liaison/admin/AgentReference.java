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
 * Describes a generic agent as an entry suitable for reading from or writing to
 * a JavaSpace.
 *
 * @author Brandon L. Buteau
 * @version 1.1
 * @since 1.0
 */
public class AgentReference implements Entry {

  public String society = null;
  public String neighborhood = null;
  public Boolean isNeighborhoodComplete = null;
  public Character neighborhoodSeparator = null;
  public String agent = null;
  public String owner = null;
  public String description = null;
  public Boolean isFromALP = null;

  public AgentReference () { }
  
  public static AgentReference create (String society, String neighborhood,
                                       String agent, String owner,
                                       String description) {
    AgentReference ref = new AgentReference();
    ref.society = society;
    ref.neighborhood = neighborhood;
    ref.agent = agent;
    ref.owner = owner;
    ref.description = description;
    return ref;
  }
  
  public boolean isAgent () {
    return ( society != null &&
             society != "" &&
             neighborhood != null &&
             agent != null &&
             agent != "" );
  }
  
  public boolean isNeighborhood () {
    return ( society != null &&
             society != "" &&
             neighborhood != null &&
             agent == null );
  }
  
  public boolean isSociety () {
    return ( society != null &&
             society != "" &&
             neighborhood == null &&
             agent == null );
  }
  
  public String getName () {
    if ( isAgent() )
      return agent;
    else if ( isNeighborhood() )
      return neighborhood;
    else if ( isSociety() )
      return society;
    return null;
  }
  
  public String toString () {
    return
      "<society:'" + society + "' neighborhood:'" + neighborhood + "' agent:'" +
      agent + "'>";
  }
  
}

