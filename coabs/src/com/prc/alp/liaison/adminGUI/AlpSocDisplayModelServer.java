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
import java.util.ArrayList;
import java.util.Iterator;
import javax.swing.tree.DefaultMutableTreeNode;
import java.util.HashMap;
import javax.swing.ImageIcon;
import javax.swing.Icon;
import javax.swing.ComboBoxModel;



/**
 * This class produces the Swing  tree and list models needed to display ALP
 * societies for the application.
 *
 * @author John Page
 * @version 1.1
 * @since 1.0
 */
public class AlpSocDisplayModelServer extends DisplayModelServer{
    /** For debug only.
     */
    public String  textDump;
    /** Top of ALP Society Tree.
     */
    public DefaultMutableTreeNode  mainWindowTreeTop;

    

    /** Creates new AlpSocDisplayModelServer
     * @param app Pointer to the LiaisonAdminToolController (application shell) that is
     * creating it.  This provides access to all public data structures.
     */
    public AlpSocDisplayModelServer(LiaisonAdminToolController app) {
        super(app);
    }
    
    /* Updates Display Models to reflect an AlpSocietySet */
    
    /** This method is invoked to re-generated all relevant swing models when a new ALP Society set  is loaded.
     * @param theSet
     */
    public void loadNewSet(AlpSocietySet theSet){
        //System.err.println("Building main ALP Tree");
        buildMainWindowAlpTree();
        //System.err.println("Done Building");
    
    } 
    
     
    /** Produces a list model that will display a list of external societies.
     * @return The list model for the list of ALP societies.
     */
   public ComboBoxModel getMainAlpSocListModel (){
        ArrayList  theDescriptors;
        Iterator    it;
        ComboBoxModel  comboModel;

        theDescriptors = theApplication.alpCollector.getSocietyList();
        comboModel = new SocComboBoxModel(theDescriptors);               
        return comboModel; 
    }
    
    /* This routine builds a tree model, primarily for the main window of the application.
     * It stores the top of the tree in MainWindowTreeTop.
     */
    /** Builds the tree display for the current Alp society set.
     */
    public void  buildMainWindowAlpTree(){
        ALPAgentReference thisAgent, childAgent;
        Iterator  setIterator, childIterator;
        ArrayList  theChildren;
        DefaultMutableTreeNode tNode, parentTNode, childTNode;
        HashMap  tNodeLookup;  // stores AgentRef/tNode pairs 
        
        // Need to first create a hash map of tnodes for each element in the ALP
        // society.  Also, store the tree node that is at the top.
        
        setIterator = theApplication.alpSet.iterator();
        tNodeLookup = new HashMap();
        
        while(setIterator.hasNext()){
            thisAgent = (ALPAgentReference) setIterator.next();
            tNode = new DefaultMutableTreeNode(thisAgent);
            if(thisAgent.isSociety())
                mainWindowTreeTop = tNode; 
            tNodeLookup.put(thisAgent, tNode);
        }
        
        // Next we need to go through all of the agents in the ALP society, retrieving 
        // the children for each one.  Then, make sure that the asscioted tNodes have the
        // same parent/child relationships defined. 
        
        setIterator = theApplication.alpSet.iterator();  // reset iterator 
        
        while(setIterator.hasNext()){
            thisAgent = (ALPAgentReference)setIterator.next();
            theChildren = theApplication.alpSet.getChildren(thisAgent);
            if(theChildren.size() > 0){
                parentTNode = (DefaultMutableTreeNode) tNodeLookup.get(thisAgent);
                childIterator = theChildren.iterator();
                while(childIterator.hasNext()){
                    childAgent = (ALPAgentReference) childIterator.next();
                    childTNode = (DefaultMutableTreeNode) tNodeLookup.get(childAgent);
                    parentTNode.add(childTNode);
                }
            }
 
       }
    }

}