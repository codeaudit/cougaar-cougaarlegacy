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
 * An interactor that can safely clone itself on demand.  This may be required
 * by some <CODE>InteractionFactory</CODE> specializations that use a <I>non-shareable</I>
 * interactor (i.e., an interactor that maintains some distinct internal state and
 * cannot therefore be shared among separate interactions).
 *
 * @author Brandon L. Buteau
 * @version 1.1
 * @since 1.1
 * @see InteractionFactory
 */
public interface CloneableInteractor
  extends MultithreadInteractor, Cloneable
{
  public Object clone();
}

