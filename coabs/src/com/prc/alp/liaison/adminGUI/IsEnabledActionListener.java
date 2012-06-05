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
import  java.awt.event.ActionListener;
import  java.awt.event.ActionEvent;
import  java.util.ArrayList;
import  java.util.Iterator;
import  javax.swing.JComboBox;
import  javax.swing.ComboBoxModel;

/**
 * Action listener for the IsEnabledComboBox.  If a value is changed, it updates
 * all affected ALPAgentReferences.
 *
 * @author John Page
 * @version 1.1
 * @since 1.0
 */
public class IsEnabledActionListener extends Object implements ActionListener{
    LiaisonAdminToolController  theApplication;
    

    /** Creates new IsEnabledActionLListener
     * @param app Pointer to the LiaisonAdminToolController (application shell) that is
     * creating it.  This provides access to all public data structures.
     */
    public IsEnabledActionListener(LiaisonAdminToolController  app) {
        super();
        theApplication = app;
    }

    /** If the value for the current ALP ref has been changed, this method will 
     *  modify all affected ALP references, and then start a thread to write out 
     * all the values to JavaSpace. 
     */
    public void actionPerformed(final java.awt.event.ActionEvent ev) {
        ALPAgentReference  alpRef;
        ArrayList  descendants;
        Iterator   it;
        JComboBox  theBox;
        BooleanComboBoxModel  theModel;
        boolean  theValue;
        ALPAgentReference childAlpRef;
        ArrayList  affectedAgents = new ArrayList();
        
        theBox = (JComboBox) ev.getSource();
        theModel = (BooleanComboBoxModel) theBox.getModel();
        theValue = theModel.getValue();
        alpRef = theApplication.currentAlpRef;
       
        
        if (alpRef == null)
            return;
        
        if (!(alpRef.isEnabled.booleanValue() == theValue)){
            alpRef.isEnabled = new Boolean(theValue);
          theApplication.mainWindow.setPermissionsValues(alpRef,
                                                     theApplication.currentExtRef);
          Updater updater = new Updater(theValue);
          Thread th = new Thread(updater);
          th.start();
        }
    }
    
    /** Class used to update the javaSpace for IsEnabled */
    public class Updater implements Runnable {
      
      private boolean theValue;
      
      /** Constructs a new updater */
      public Updater (boolean value) {
        theValue = value;
      }
      
      /** Body of thread that writes out all changes to JavaSpace */
      public void run () {
        
          ALPAgentReference       childAlpRef;
          ALPAgentReference  alpRef = theApplication.currentAlpRef;
          ExternalAgentReference extRef = theApplication.currentExtRef;
          ArrayList  descendants;
          ArrayList  affectedAgents = new ArrayList(); 
          Iterator   it;

          theApplication.mainWindow.setStatusBar(new String("Updating liaison enablement..."));
            
            
            affectedAgents.add(alpRef);
            // Need to either enable or disable children according to this. 
            descendants = theApplication.alpSet.getDescendants(alpRef);
            if(descendants.size() > 0){
                it = descendants.iterator();
                while(it.hasNext()){
                    childAlpRef = (ALPAgentReference) it.next();
                    childAlpRef.isEnabled = new Boolean(theValue);
                    affectedAgents.add(childAlpRef);
                }
                              
            }
            
            // Need to get teh tree to repaint itself.  
            
            theApplication.mainWindow.repaintMainALPTree();
            
            //  make sure permissions are current, and proper controls are 
            // enabled/disabled. 
            theApplication.mainWindow.updatePermissionsPanel(alpRef, extRef, false);
            
            // Write out the affected agents to the javaspace. 
            
            theApplication.alpCollector.writeOutALPListtoJavaSpace(affectedAgents);
            theApplication.mainWindow.setStatusBar("done.");
            
            
        }      
        
    }
    
}