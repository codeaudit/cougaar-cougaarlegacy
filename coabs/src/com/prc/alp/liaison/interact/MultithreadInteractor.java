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

/**
 * An interactor that needs to be manipulated through some other thread besides
 * that of a particular <CODE>Interaction</CODE>.  For example, the
 * <CODE>CoABSLiaisonDeputy</CODE> receives CoABS Grid messages through Java
 * RMI invociations of a member function under control of an RMI thread.  These
 * messages may need to be passed to and integrated with an appropriate existing
 * interaction, which is executing in its own thread.  The <CODE>MultithreadInteractor</CODE>
 * provides a method for linking them together.
 *
 * @author Brandon L. Buteau
 * @version 1.1
 * @since 1.0
 * @see Interaction
 * @see com.prc.alp.liaison.plugin.CoABSLiaisonDeputy
 */
public interface MultithreadInteractor
  extends Interactor
{
  /** 
   * Performs whatever action is required to react to an arbitrary object supplied
   * to a particular <CODE>Interaction</CODE> instance by a separate thread.  It may
   * choose to ignore the object altogether.
   *
   * @param obj An arbitrary object representing something (typically a received
   * message or other information) that an existing <CODE>Interaction</CODE> instance
   * may need to react to
   * @param context An arbitrary object representing some contextual information
   * supplied to this interactor
   * @param iaction A reference to a particular <CODE>Interaction</CODE>
   *
   * @return <CODE>true</CODE> if this interactor accepted and handled the given
   * object; <CODE>false</CODE> otherwise
   */
  public boolean handle (Object obj, Object context, Interaction iaction);
}

