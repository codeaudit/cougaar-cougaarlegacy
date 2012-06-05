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
import javax.swing.DefaultListModel;
import javax.swing.tree.DefaultMutableTreeNode;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.HashMap;
import javax.swing.ImageIcon;
import javax.swing.Icon;

/**
 * This is a base class for the Display Model Servers.  It provides
 * a common point of reference for resources such as icons.
 *
 * @author John Page
 * @version 1.1
 * @since 1.0
 */
public class DisplayModelServer extends Object {
    protected LiaisonAdminToolController theApplication;
    public ImageIcon    stopSign, disStopSign;
    public ImageIcon    societyIcon, disSocietyIcon;
    public ImageIcon    neighborhoodIcon, disNeighborhoodIcon;
    public ImageIcon    agentIcon, disAgentIcon;
    public ImageIcon    crownIcon, disCrownIcon;
    public ImageIcon    blankIcon;
    public ImageIcon    initIcon, respIcon, bothWaysIcon;
    public AdminTreeCellRenderer  trCellRenderer; 
    

    /** Creates new DisplayModelServer
     * @param app Pointer to the LiaisonAdminToolController (application shell) that is
     * creating it.  This provides access to all public data structures.
     */
    public DisplayModelServer(LiaisonAdminToolController app) {
        super();
        theApplication = app;
        stopSign = new ImageIcon(getClass ().getResource ("/com/prc/alp/liaison/adminGUI/stop.jpg"));
        societyIcon = new ImageIcon(getClass ().getResource ("/com/prc/alp/liaison/adminGUI/society.jpg"));
        disSocietyIcon = new ImageIcon(getClass ().getResource ("/com/prc/alp/liaison/adminGUI/societyDisabled.jpg"));
        neighborhoodIcon = new ImageIcon(getClass ().getResource ("/com/prc/alp/liaison/adminGUI/neighborhood.jpg"));
        disNeighborhoodIcon = new ImageIcon(getClass ().getResource ("/com/prc/alp/liaison/adminGUI/neighborhoodDisabled.jpg"));
        agentIcon = new ImageIcon(getClass ().getResource ("/com/prc/alp/liaison/adminGUI/agent.jpg"));
        disAgentIcon = new ImageIcon(getClass ().getResource ("/com/prc/alp/liaison/adminGUI/agentDisabled.jpg"));
        crownIcon = new ImageIcon(getClass ().getResource ("/com/prc/alp/liaison/adminGUI/crown.gif"));
        disCrownIcon = new ImageIcon(getClass ().getResource ("/com/prc/alp/liaison/adminGUI/crownDisabled.gif"));
        blankIcon = new ImageIcon(getClass ().getResource ("/com/prc/alp/liaison/adminGUI/blank.jpg"));
        disStopSign = new ImageIcon(getClass().getResource("/com/prc/alp/liaison/adminGUI/disStop.jpg"));
        initIcon = new ImageIcon(getClass().getResource("/com/prc/alp/liaison/adminGUI/MayInitiate.jpg"));
        respIcon = new ImageIcon(getClass().getResource("/com/prc/alp/liaison/adminGUI/MayRespond.jpg"));
        bothWaysIcon = new ImageIcon(getClass().getResource("/com/prc/alp/liaison/adminGUI/BothWays.jpg"));
        trCellRenderer = new AdminTreeCellRenderer(app, this);
    }
    
 

}