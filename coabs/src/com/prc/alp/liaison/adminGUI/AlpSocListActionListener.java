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
 * Action listener for the list of available ALP Societies.  This class provides all the necessary
 * logic for loading a new ALP society.
 *
 * @author John Page
 * @version 1.1
 * @since 1.0
 */
public class AlpSocListActionListener extends Object implements ActionListener {
    LiaisonAdminToolController  theApplication;

    /** Creates new AlpSocListActionListener
     * @param app Pointer to the LiaisonAdminToolController (application shell) that is
     * creating it.  This provides access to all public data structures.
     */
    public AlpSocListActionListener(LiaisonAdminToolController  app) {
        super();
        theApplication = app;
    }

    /** The method responds to a selection of and ALP society list, and initiates the loading of same.
     * @param ev
     */
    public void actionPerformed(final ActionEvent ev) {
        JComboBox  theBox;
        AlpSocietyDescriptor  alpDesc;
        String  statusString;
        
        
        theBox = (JComboBox) ev.getSource();
        alpDesc = (AlpSocietyDescriptor) theBox.getSelectedItem();
        
        // return if "none" is selected.  Actually, this should unload the tree
        // and leave the world blank. 
        
        if(alpDesc.isEmpty())      
        {   
            statusString = new String("Unloading both societies ");
            theApplication.mainWindow.setStatusBar(statusString);
            theApplication.mainWindow.setFirstExtTree();
            theApplication.mainWindow.hideAlpTree();
            // Stop any updates/listeners from the existing society
            theApplication.alpCollector.releaseSociety();
            theApplication.alpSet = null;
            theApplication.currentAlpRef = null;
            theApplication.liaisonSet = null;
            theApplication.mainWindow.updatePermissionsPanel(null, null, true);
            
            return; 
        }
        
        Loader loader = new Loader(alpDesc);
        Thread th = new Thread(loader);
        th.start();
        
        return;
      
    }
      
    /** This class defines the thread that actually loads the selected ALP society.
     */
    public class Loader implements Runnable {
      
      private AlpSocietyDescriptor alpDesc;
      
    /**
     * @param desc  */
      public Loader ( AlpSocietyDescriptor desc ) {
        alpDesc = desc;
      }
      
    /** Perform all of the processing needed to bring a new ALP society into the application,
     *  and display the new settings.
     */
      public void run () {
                
        String statusString = new String("Loading ALP society '" + alpDesc.toString() + "'...");
        theApplication.mainWindow.setStatusBar(statusString);
        
        
        // Start Loading...
        
        //System.err.println("getting society from desc.");
        theApplication.alpSet = (AlpSocietySet) 
                        theApplication.alpCollector.getSocietyFromDescriptor(alpDesc);
        //theApplication.alpSet.printOutContents();
        
        statusString = new String("Updating Interface.");
        
        //System.err.println("Creating Display Models...");
        
        theApplication.alpDisplayServer.loadNewSet(theApplication.alpSet);
      
        //System.err.println("load New Alp Tree");
        theApplication.mainWindow.loadNewAlpTree();
        
  
        theApplication.mainWindow.setStatusBar(new String("done."));
      }
      
    }
    
}