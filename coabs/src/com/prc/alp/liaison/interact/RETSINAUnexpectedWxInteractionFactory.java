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

import com.globalinfotek.coabsgrid.*;
import com.prc.alp.liaison.admin.*;
import com.prc.alp.liaison.plugin.*;

/**
 * An <CODE>InteractionFactory</CODE> that is invoked by default for RETSINA InfoAgent
 * messages received when no other <CODE>Interaction</CODE> agrees to
 * handle them.  Normally, RETSINA InfoAgents should not be sending unsolicited messages
 * to ALP/Cougaar agents, but this factory will accept those messages if they are
 * received.
 *
 * @author Brandon L. Buteau
 * @version 1.1
 * @since 1.1
 * @see RETSINAUnexpectedWxInteractor
 */
public class RETSINAUnexpectedWxInteractionFactory extends InteractionFactory {
  
  public RETSINAUnexpectedWxInteractionFactory(LiaisonDeputy deputy, Interactor ia,
                                               String name, boolean shared)  {
    super (deputy, ia, name, shared);
  }
  
  public ExternalAgentReference sourceAgent (Object obj) {
    return ((CoABSLiaisonDeputy) deputy).getSender((Message) obj);
  }  
  
  public boolean handle (Object obj) {
    ExternalAgentReference sender = sourceAgent(obj);
    // No other way to test for now except by name
    if ( sender.agent.equals("CNNCurrentWeatherAgent") ||
         sender.agent.equals("CNNForecastWeatherAgent") ||
         sender.agent.equals("WCNCurrentWeatherAgent") ||
         sender.agent.equals("WCNForecastWeatherAgent") ) {
      return true; // message is from a RETSINA weather agent;
    } else
      return false;
  }

}

