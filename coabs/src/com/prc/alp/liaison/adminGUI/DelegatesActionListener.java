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
import  java.awt.event.ActionListener;
import  java.awt.event.ActionEvent;
import  java.util.ArrayList;
import  java.util.Iterator;
import  javax.swing.JComboBox;
import  javax.swing.ComboBoxModel;


/**
 * Action Listener attached to the "Delegates Authority" Combo Box.
 *
 * @author John Page
 * @version 1.1
 * @since 1.0
 */
public class DelegatesActionListener extends Object implements ActionListener{
    LiaisonAdminToolController  theApplication;

    /** Creates new delegatesActionLListener
     * @param app Pointer to the LiaisonAdminToolController (application shell) that is
     * creating it.  This provides access to all public data structures.
     */
    public DelegatesActionListener(LiaisonAdminToolController app) {
        super();
        theApplication = app;
    }

    /** Determines if the value for the current ALP Agent Ref has been changed.  If so,
     * start updating all affected Refs.
     * @param ev
     */
    public void actionPerformed(final java.awt.event.ActionEvent ev) {
        ALPAgentReference  alpRef = theApplication.currentAlpRef;
        ExternalAgentReference extRef = theApplication.currentExtRef;
        JComboBox  theBox;
        BooleanComboBoxModel  theModel;
        boolean  theValue;
        
        
        theBox = (JComboBox) ev.getSource();
        theModel = (BooleanComboBoxModel) theBox.getModel();
        theValue = theModel.getValue();
        
        alpRef = theApplication.currentAlpRef;
       
        
        if (alpRef == null)
            return;
        
        if (!(alpRef.delegatesAuthority.booleanValue() == theValue)){
          alpRef.delegatesAuthority = new Boolean(theValue);
          theApplication.mainWindow.setPermissionsValues(alpRef, extRef);
          Updater updater = new Updater(theValue);
          Thread th = new Thread(updater);
          th.start();
        }
    }
    
    /** Thread to perform update of affected agent refs.
     */
    public class Updater implements Runnable {
      
      private boolean theValue;
      
    /** Create updater thread 
     * @param value  */
      public Updater (boolean value) {
        theValue = value;
      }
      
    /** Determine which references are affected.  Write out the changed values to 
      * jave space and update the GUI. 
      */
      public void run () {
        
          ExternalAgentReference childExtRef;
          LiaisonStatusReference  theLSR;
          LiaisonStatusReference  childLSR;
          ALPAgentReference       childAlpRef;
          ALPAgentReference  alpRef = theApplication.currentAlpRef;
          ExternalAgentReference extRef = theApplication.currentExtRef;
          ArrayList  affectedLSRs = new ArrayList();
          ArrayList  affectedALPAgents = new ArrayList();
          ArrayList  extDescendants;
          ArrayList  alpDescendants;
          ArrayList  alpArray = new ArrayList(); 
          ArrayList  extArray = new ArrayList();
          Iterator   extIt, alpIt;

          theApplication.mainWindow.setStatusBar(new String("Updating delegation status..."));
            
            
          affectedALPAgents.add(alpRef);

          // Now things can get a little bit nasty.  If the current Alp Ref has 
          // re-asserted control, we need to make sure that all it's subordinates have the
          // same permissions sets that it does.  Therefore, enjoy the following. 

          if(!(extRef == null)){

              alpDescendants = theApplication.alpSet.getDescendants(alpRef);
              extDescendants = theApplication.extSet.getDescendants(extRef);
              theLSR = (LiaisonStatusReference) 
                        theApplication.liaisonSet.getStatusRef(alpRef, extRef);

              // I'm cheating a bit on the variable name, but I have to make sure that
              // liaisons between any alp descents and the current extRef are included as 
              // well, so
              extDescendants.add(extRef);      
              alpIt = alpDescendants.iterator();
              // outer loop-- go through all alp descendants 
              while(alpIt.hasNext()){
                  childAlpRef = (ALPAgentReference) alpIt.next();
                  // inner loop 
                  extIt = extDescendants.iterator();
                  while (extIt.hasNext()){
                      childExtRef = (ExternalAgentReference) extIt.next();
                      childLSR = theApplication.liaisonSet.getStatusRef(childAlpRef,
                                                                        childExtRef);
                      affectedLSRs.add(childLSR);
                      childLSR.ALPCanInitiate = theLSR.ALPCanInitiate;
                      childLSR.ALPCanRespond = theLSR.ALPCanRespond;
                  } // while through affected external refs     
             } // while through all descendants of alpRef
          }  // if we have assumed control
            
          
            
            // Need to get teh tree to repaint itself.  
            
          theApplication.mainWindow.repaintMainALPTree();

          // also need to repaint external tree.  The command may affect it's
          // rendering 

          theApplication.mainWindow.repaintMainExtTree();

          //  THis should not be needed, but let's update the permissions panel 
          //  just to be on the safe side. 

          theApplication.mainWindow.updatePermissionsPanel(alpRef, theApplication.currentExtRef,
                                                           false);

          // now, write out all of the changed records to the JavaSpace 

          theApplication.alpCollector.writeOutALPListtoJavaSpace(affectedALPAgents);
          theApplication.liaisonCollector.writeOutLSRsToJavaSpace(affectedLSRs);  
          theApplication.mainWindow.setStatusBar("done.");

      }      
        
    }
}