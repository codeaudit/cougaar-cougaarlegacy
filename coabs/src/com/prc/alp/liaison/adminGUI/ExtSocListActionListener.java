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
import  java.awt.event.ActionListener;
import  java.awt.event.ActionEvent;
import  java.util.ArrayList;
import  java.util.Iterator;
import  javax.swing.JComboBox;
import  javax.swing.ComboBoxModel;


/**
 * This class provides the action for whenever the user wishes to load a new
 * external society.  If you wish to programmatically load a different external
 * society, please make sure to change the value of the external society combo
 * box which will then invoke this action.
 *
 * @author WJPageJohn Page
 * @version 1.1
 * @since 1.0
 */
public class ExtSocListActionListener extends Object implements ActionListener{
    LiaisonAdminToolController  theApplication;

    /** Creates new ExtSocListActionListener
     * @param app Pointer to the LiaisonAdminToolController (application shell) that is
     * creating it.  This provides access to all public data structures.
     */
    public ExtSocListActionListener(LiaisonAdminToolController app) {
        super();
        theApplication = app; 
    }

    /** Swing Callback.  releases previous society, and then starts the loading of the 
        new one.
    */
    public void actionPerformed(final ActionEvent ev) {
        JComboBox  theBox;
        ExtSocietyDescriptor  extDesc;
        String  statusString;
        
        
        theBox = (JComboBox) ev.getSource();
        extDesc = (ExtSocietyDescriptor) theBox.getSelectedItem();
        
        
        // stop updates from the previous society. 
        
        theApplication.extCollector.releaseSociety();
        
        
        // Unload the tree if "none" is selected 
        if(extDesc.isEmpty()){
            
            statusString = new String("Unloading External Society");
            theApplication.mainWindow.setStatusBar(statusString);
            theApplication.mainWindow.hideExtTree();    
            theApplication.extSet = null;
            theApplication.liaisonSet = null;
            theApplication.currentExtRef = null;
            theApplication.mainWindow.updatePermissionsPanel(theApplication.currentAlpRef, null, true);
            theApplication.mainWindow.setStatusBar(new String("done"));
            
            return;
        }
        
        // if there is no ALP society loaded, return to "none"
        
        if (theApplication.alpSet == null){
              theApplication.mainWindow.setFirstExtTree();  
              return;
        }
        
        Loader loader = new Loader(extDesc);
        Thread th = new Thread(loader);
        th.start();
        
    }
    
    /** Class used to define loading thread */
    public class Loader implements Runnable {
      
      private ExtSocietyDescriptor extDesc;
      
      /** Create loader...  */
      public Loader (ExtSocietyDescriptor desc) {
        extDesc = desc;
      }
      
      /** Body of the loader thread.  Does all necessary steps need to load in a
       *  new external society and update all of the application. 
       */ 
      public void run () {
        
        if(theApplication.alpSet.size() == 0){
               theApplication.mainWindow.setFirstExtTree();
        }
                       
        String statusString = new String("Loading current liaison status...");
        theApplication.mainWindow.setStatusBar(statusString);
        // pull in LiaisonStatus Refs.
        
        ArrayList theLSRs =
          theApplication.liaisonCollector.getLSRsFromJavaSpace(
            theApplication.alpSet.societyName,
            extDesc.societyName);
        //System.err.println("Finished loading LSRs from JavaSpace");
        statusString = new String("Loading external society '" + extDesc.toString() + "'...");
        theApplication.mainWindow.setStatusBar(statusString);
        // Pull in the society definitions 
        theApplication.extSet =  (ExtSocietySet)
                theApplication.extCollector.getSocietyFromDescriptor(extDesc);
        // trace statement...
        //theApplication.extSet.printOutContents();
        //System.err.println("Creating LiasonSet");
        theApplication.liaisonSet = new LiaisonSet(theApplication, theLSRs);
        theApplication.mainWindow.validateAndUpdateExternalTree();
        theApplication.mainWindow.updatePermissionsPanel(theApplication.currentAlpRef,
                                                         theApplication.currentExtRef,
                                                         true);  
        theApplication.mainWindow.setStatusBar(new String("done."));      
    }
  }
}