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

package com.prc.alp.liaison.plugin;

/**
 * Defines those entities that know how to perform liaison management by being
 * capable of producing a <CODE>LiaisonManager</CODE>.  This is designed as an
 * interface so that PlugIns with this capability can extend other standard
 * ALP/Cougaar PlugIn classes.
 */
public interface Liaison
{
  LiaisonManager getManager();
}

