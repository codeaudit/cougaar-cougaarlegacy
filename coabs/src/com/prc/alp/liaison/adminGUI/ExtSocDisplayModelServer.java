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
import  com.prc.alp.liaison.admin.ExternalAgentReference;
import  com.prc.alp.liaison.admin.LiaisonStatusReference;
import javax.swing.DefaultListModel;
import javax.swing.tree.DefaultMutableTreeNode;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.HashMap;
import javax.swing.ImageIcon;
import javax.swing.Icon;
import javax.swing.ComboBoxModel;

/**
 * This class prepares swing display models based on the external society currently
 * loaded by the application.
 *
 * @author John Page
 * @version 1.1
 * @since 1.0
 */
public class ExtSocDisplayModelServer extends DisplayModelServer {
    private HashMap treeLookup;  // has  ALPRef/TreeNode pairs

    String  textDump;

    /** Creates new ExtSocDisplayModelServer
     * @param app Pointer to the LiaisonAdminToolController (application shell) that is
     * creating it.  This provides access to all public data structures.
     */
    public ExtSocDisplayModelServer(LiaisonAdminToolController app) {
        super(app);
        textDump = new String("External society. This is a  text dump."); 
        treeLookup = new HashMap();
    }
   
    /** This may be badly named.  This is the method to be invoked whenver a new 
     * external society set is loaded.  It generates all necessary Swing Models 
     */
    public void loadNewSet(ExtSocietySet theSet){
        buildMainWindowExtTrees();
    }
   
    /** Get the root node of the external tree for the specified ALP agent reference. 
     *  Remember that there is a separate external tree for each ALP Agent reference, 
     * the the current selection in the ALP Tree determines which is shown in teh 
     * external tree.
     */
    public DefaultMutableTreeNode getTreeRoot(ALPAgentReference  alpRef){      
        return( (DefaultMutableTreeNode) treeLookup.get(alpRef));             
    }
    
    /** This method produces a list model that will display a list of accessable external societies.
      */
    public ComboBoxModel getMainExtSocListModel (){
        ArrayList  theDescriptors;
        Iterator    it;
        ComboBoxModel  comboModel;
        
        theDescriptors = theApplication.extCollector.getSocietyList();
        comboModel = new SocComboBoxModel(theDescriptors);            
        return comboModel; 
    }
    
    /**  This method builds all the external trees needed for a newly loaded external 
     * society. 
     * The tree list models get a lot more confusing for the external tree, because
     * the external tree is built from LSRs according to the ALPAgentReference selected.
     * Therefore, I don't have one root node, but rather a whole set of them based 
     * on the selected ALP Agent.  
     */
    
    
    
    public void buildMainWindowExtTrees(){
        HashMap thisTreeLookup;   // this hashmap has <LSR><TreeNode> pairs
        ArrayList theChildren;
        Iterator  alpIt;
        Iterator  extIt;
        Iterator  childIt;
        LiaisonStatusReference  thisLSR;
        DefaultMutableTreeNode  thisTNode, thisTNodeRoot, childTNode;
        ALPAgentReference   thisALPAgent;
        ExternalAgentReference thisExtAgent, childExtAgent;
        
        
        // outer loop.  We need to build a tree of LSRS for EACH ALP Agent Ref,
        // and store with the class. 
        

        
        thisTreeLookup = new HashMap();
        //treeLookup.clear();
        alpIt = theApplication.alpSet.iterator();
        
 
        while(alpIt.hasNext()){

            thisALPAgent = (ALPAgentReference) alpIt.next();
            
            // First, we build a set of tree nodes for each LSR that refers 
            // to that agent. Save the root tree node that corresponds to 
            // the LSR that references the external society. 
            
            extIt = theApplication.extSet.iterator();
            thisTreeLookup.clear();
            while(extIt.hasNext()){

                // populate the hashmap with every tNodebelonging to 
                // the external agent. 
                
                thisExtAgent = (ExternalAgentReference) extIt.next();
                
                thisLSR = theApplication.liaisonSet.getStatusRef(thisALPAgent, 
                                                                  thisExtAgent);
                thisTNode = new DefaultMutableTreeNode(thisLSR);
                // save if top of tree....
                
   
                if(thisExtAgent.isSociety())
                {
                    thisTNodeRoot = thisTNode;
                    // save as tree root at for this ALP Agent
                    treeLookup.put(thisALPAgent, thisTNode);
                }                 
                // store tNode in local lookup under external agent  
                thisTreeLookup.put(thisExtAgent, thisTNode);         
            }
            
            
            
            // Now we need to build the tree structure.  We loop through the 
            // external agents, getting their children 
            
            extIt = theApplication.extSet.iterator();
            
            while(extIt.hasNext()){

                thisExtAgent = (ExternalAgentReference) extIt.next();
                // get children of this external agent 

                theChildren = theApplication.extSet.getChildren(thisExtAgent);
                if(theChildren.size() > 0){
                    thisTNode = (DefaultMutableTreeNode) 
                                              thisTreeLookup.get(thisExtAgent);
                    childIt = theChildren.iterator();
                    while(childIt.hasNext()){
                        childExtAgent = (ExternalAgentReference) childIt.next();
                        childTNode = (DefaultMutableTreeNode)thisTreeLookup.get(childExtAgent);
                        thisTNode.add(childTNode);
                    }  // while adding children
                } // if there are children
            } // while checking children for this external agent 
        } // while building a tree for THIS alp agent 
        
    }
    
}