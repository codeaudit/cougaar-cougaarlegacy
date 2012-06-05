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
 * Authors:  John Page, Brandon L. Buteau
 *
 */

package com.prc.alp.liaison.adminGUI;
import  com.prc.alp.liaison.admin.ALPAgentReference;
import  com.prc.alp.liaison.admin.LiaisonStatusReference;
import  com.prc.alp.liaison.admin.ExternalAgentReference;
import  javax.swing.event.TreeSelectionListener;
import  javax.swing.event.TreeSelectionEvent;
import  javax.swing.tree.DefaultMutableTreeNode;
import  javax.swing.tree.TreePath;

/**
 * Provides the "callback" activity when a node in the external society tree is
 * selected.
 *
 * @author John Page
 * @author Brandon L. Buteau
 * @version 1.1
 * @since 1.0
 */
public class ExtTreeSelectionListener extends Object implements TreeSelectionListener{
    LiaisonAdminToolController theApplication;

    /** Creates new ExtTreeSelectionListener
     * @param app Pointer to the LiaisonAdminToolController (application shell) that is
     * creating it.  This provides access to all public data structures.
     */
    public ExtTreeSelectionListener(LiaisonAdminToolController app) {
        super();
        theApplication = app;
    }

    /** Swing "callback" for a new node getting selected in teh external tree.  Updates the permissions panel to reflect the current alpRef-extRef pair.
     * @param e
     */
    public void valueChanged(final TreeSelectionEvent e) {
        TreePath  path;
        Object  last;
        DefaultMutableTreeNode  tNode;
        ALPAgentReference  alpRef = null;  // don't know if I need this
        LiaisonStatusReference   theLSR;
        ExternalAgentReference   extRef;


        // Identify the selected Alp Agent
        path = e.getPath();
        last = path.getLastPathComponent();
        tNode = (DefaultMutableTreeNode) last;
        theLSR = (LiaisonStatusReference) tNode.getUserObject();
        // The two things I need to do here is to glom onto the extRef, and set teh
        // global current ref to it.  Then I update permissions, and life is good.

        extRef = (ExternalAgentReference)theApplication.extSet.getAgentRefByID(theLSR.externalID);

        theApplication.currentExtRef = extRef;


        theApplication.mainWindow.updatePermissionsPanel(theApplication.currentAlpRef,
        theApplication.currentExtRef,
        true);

        // Hopefully this will be good to go!


    }
}