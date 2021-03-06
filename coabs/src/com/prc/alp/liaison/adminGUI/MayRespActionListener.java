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
 * Provides the functionality for the May Respond to Interactions Combo Box on the
 * permissions panel.
 *
 * @author John Page
 * @version 1.1
 * @since 1.0
 */
public class MayRespActionListener extends Object implements ActionListener{
    LiaisonAdminToolController theApplication;
    /** Creates new MayRespActionListener
     * @param app A pointer to the application shell (LiasionAdminToolController) that created this
     * instance. It can be used to access all instances of all public objects in the application.
     */
    public MayRespActionListener(LiaisonAdminToolController  app) {
        super();
        theApplication = app;
        
    }

    /** Swing Callback.  Catches the new value and validates that all of the 
     *  affected agents and societies are valid  If so, it then launches a thread to 
     *  update the affected LSRs in JavaSpace 
     */
    public void actionPerformed(final java.awt.event.ActionEvent ev) {
        ALPAgentReference  alpRef = theApplication.currentAlpRef;
        ExternalAgentReference extRef = theApplication.currentExtRef;
        LiaisonStatusReference  theLSR;
        JComboBox  theBox;
        BooleanComboBoxModel  theModel;
        boolean  theValue;
                
        theBox = (JComboBox) ev.getSource();
        theModel = (BooleanComboBoxModel) theBox.getModel();
        theValue = theModel.getValue();
        theLSR = theApplication.liaisonSet.getStatusRef(alpRef, extRef);
        
        
        if ((alpRef == null) || (extRef == null) || (theLSR == null))
            return;
        
        //make sure permissions are current, and proper controls are 
        // enabled/disabled.  Must be done in this thread or else it will
        // trigger another call to actionPerformed(), yielding an infinite loop.
        if(!(theLSR.ALPCanRespond.booleanValue() == theValue)){
          theLSR.ALPCanRespond = new Boolean(theValue);
          theApplication.mainWindow.setPermissionsValues(alpRef, extRef);
          Updater updater = new Updater(theValue, theLSR);
          Thread th = new Thread(updater);
          th.start();
        }
    }
    
    /** Inner class used to define the update thread. */
    public class Updater implements Runnable {
      
      private boolean theValue;
      private LiaisonStatusReference theLSR;
      
      /** Creates the update thread. */
      public Updater (boolean value, LiaisonStatusReference lsr) {
        theValue = value;
        theLSR = lsr;
      }
      
      /** Run the update thread.  Compile a list of all affected LSRs, writes them 
          to JavaSpace, and then updates the GUI. 
       */
      
      public void run () {
        
          ExternalAgentReference childExtRef;
          LiaisonStatusReference  childLSR;
          ALPAgentReference       childAlpRef;
          ALPAgentReference  alpRef = theApplication.currentAlpRef;
          ExternalAgentReference extRef = theApplication.currentExtRef;
          ArrayList  affectedLSRs = new ArrayList();
          ArrayList  extDescendants;
          ArrayList  alpDescendants;
          ArrayList  alpArray = new ArrayList(); 
          ArrayList  extArray = new ArrayList();
          Iterator   extIt, alpIt;

          theApplication.mainWindow.setStatusBar(new String("Updating liaison permissions..."));
            
          affectedLSRs.add(theLSR);

          // gather all affect extRefs 
          extArray.add(extRef);
          extDescendants = theApplication.extSet.getDescendants(extRef);
          extIt = extDescendants.iterator();
          while(extIt.hasNext()){
              extArray.add(extIt.next());
          }

          // gather all affected Alp references 
          alpArray.add(alpRef);          
          // if the current ALP node controls subordinates, we must set 
          // their permissions as well. 
          if(!(alpRef.delegatesAuthority.booleanValue())){
              alpDescendants = theApplication.alpSet.getDescendants(alpRef);
              if(alpDescendants.size() > 0){
                  alpIt = alpDescendants.iterator();
                  while(alpIt.hasNext()){
                      alpArray.add(alpIt.next());
                  }
              }
          }

          // Now, cycle through and modify all necessary permissions

          alpIt = alpArray.iterator();
          while(alpIt.hasNext()){
              // outer loop, go through the current alp ref and any descendants
              childAlpRef = (ALPAgentReference) alpIt.next();
              // inner loop-- go through all affect ext refs.  
              extIt = extArray.iterator();
              while(extIt.hasNext()){
                  childExtRef = (ExternalAgentReference) extIt.next();
                  childLSR = theApplication.liaisonSet.getStatusRef(childAlpRef,  
                                                                    childExtRef);
                  childLSR.ALPCanRespond = new Boolean(theValue);
                  affectedLSRs.add(childLSR);
              }  // loop through affected external refs 

          }

          theApplication.liaisonCollector.writeOutLSRsToJavaSpace(affectedLSRs);
        
                
        
            
          // Need to get teh external tree to repaint itself.  
          theApplication.mainWindow.repaintMainExtTree();
          // Update permissions panel but not the values themselves, otherwise will
          // end up in an infinite event loop
          theApplication.mainWindow.updatePermissionsPanel(alpRef, extRef, false);
          theApplication.mainWindow.setStatusBar(new String("done."));
                        
      }        
    }
    
}