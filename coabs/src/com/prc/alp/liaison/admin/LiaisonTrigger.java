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

import net.jini.core.event.RemoteEvent;

/**
 * An interface designed to allow a customized response to JavaSpace events 
 * related to liaison administration.
 *
 * @see LiaisonSpaceListener
 * @author Brandon L. Buteau
 * @version 1.1
 * @since 1.0
 */
public interface LiaisonTrigger
{
  public void notify (RemoteEvent ev);
}

