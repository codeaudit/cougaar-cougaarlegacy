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
 * An <CODE>InteractionFactory</CODE> that is invoked by default for CoABS Grid
 * messages received when no other <CODE>InteractionFactory</CODE> agrees to
 * handle them.
 *
 * @author Brandon L. Buteau
 * @version 1.1
 * @since 1.1
 */
public class CoABSDefaultInteractionFactory extends InteractionFactory {
  
  public CoABSDefaultInteractionFactory(LiaisonDeputy deputy,Interactor ia,String name,boolean shared)  {
    super (deputy, ia, name, shared);
  }
  
  public ExternalAgentReference sourceAgent (Object obj) {
    return ((CoABSLiaisonDeputy) deputy).getSender((Message) obj);
  }  
  
  public boolean handle (Object obj) {
    // This factory should be checked last, as it will claim to handle anything
    return true; 
  }

}

