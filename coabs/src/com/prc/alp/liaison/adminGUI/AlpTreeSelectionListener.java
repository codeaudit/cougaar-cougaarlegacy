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
 * Author:  John Page
 *
 */

package com.prc.alp.liaison.adminGUI;
import  com.prc.alp.liaison.admin.ALPAgentReference;
import  javax.swing.event.TreeSelectionListener;
import  javax.swing.event.TreeSelectionEvent;
import  javax.swing.tree.DefaultMutableTreeNode;
import  javax.swing.tree.TreePath;



/**
 * Provides functionality for when a node on the alp display tree is loaded.
 *
 * @author John Page
 * @version 1.1
 * @since 1.0
 */
public class AlpTreeSelectionListener extends Object implements TreeSelectionListener{
    LiaisonAdminToolController  theApplication;

    /** Creates new AlpTreeSelectionListener
     * @param app Pointer to the LiaisonAdminToolController (application shell) that is
     * creating it.  This provides access to all public data structures.
     */
    public AlpTreeSelectionListener(LiaisonAdminToolController  app) {
        super();
        theApplication = app; 
    }

    /** Swing callback. Updates GUI to reflect new selection in ALP tree.
     * @param e
     */
    public void valueChanged(final TreeSelectionEvent e) {
        TreePath  path; 
        Object  last;
        DefaultMutableTreeNode  tNode;
        ALPAgentReference  alpRef;
        
         
        // Identify the selected Alp Agent
        path = e.getPath();
        last = path.getLastPathComponent();
        tNode = (DefaultMutableTreeNode) last; 
        alpRef = (ALPAgentReference) tNode.getUserObject(); 
        
        theApplication.currentAlpRef = alpRef;
        
        // May wish to save the currently selected item in that tree and restore. 
        // saving all the expansions, etc may be tedious. Currently, I'm just loading the new 
        // tree.  This will set the external node to the top one in the tree.  I'm not going to 
        // make a lookuptable of who's expanded or what.   Not yet, anyway. 
        

        theApplication.mainWindow.loadNewExternalTree(alpRef);
        

        theApplication.mainWindow.updatePermissionsPanel(alpRef, 
                                                         theApplication.currentExtRef,
                                                         true);
        
        // Hopefully this will be good to go! 
        
        
    }
}