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
import java.util.ArrayList;
import java.util.Iterator;
import com.prc.alp.liaison.admin.ALPAgentReference;
import com.prc.alp.liaison.admin.ExternalAgentReference;


/**
 * This class is the top level controller (or application shell )  for the Liaison Admin Tool.  It is
 * a top-level shell that performs initialization, creates and invokes the GUI,
 * etc.  Study the public fields for this class very carefully.  Also, most classes
 * that are created by this one keep a pointer to it so they can access these
 * structures.
 *
 * @author John Page
 * @version 1.1
 * @since 1.0
 */
public class LiaisonAdminToolController extends Object {
    /** Command Line arguments from main().
     */
    public String   arguments[];  // command line arguments 
    /** Reference to main window and all it's swing objects.
     */
    public LiaisonAdminToolMainGUI mainWindow;
    
    /** Instance of ALP collector use to retrieve Alp Societies.
     */
    public AlpSocietyCollector alpCollector; 
    /** Set that contains all of the ALPAgentReferences for the currently loaded ALP Society.
     */
    public AlpSocietySet   alpSet;
    
    /** Collector for all of the external society types supported.
     */
    public MasterExtSocietyCollector extCollector; 
    /** Set that contains all of the ExternalAgentReferences for the currently loaded external society.
     */
    public ExtSocietySet   extSet;
    
    /** Object that retrieves all of the LiaisonStatusReferences for the currently loaded ALP and External Societies.
     */
    public LiaisonSetCollector  liaisonCollector;
    /** Set of all LiaisonStatusReferences for the currently loaded ALP and External Societies.
     */
    public LiaisonSet  liaisonSet;  
    
    
    /** Holds all of the Swing Models concerning the current ALP society.
     */
    public AlpSocDisplayModelServer    alpDisplayServer;
    /** Holds all of the Swing Models needed to display the external society.
     */
    public ExtSocDisplayModelServer    extDisplayServer;
    
    /** The ALPAgentReference currently selected in the ALP Tree.
     */
    public ALPAgentReference  currentAlpRef; // current selected ref in main ALP Tree
    /** The ExternalAgentReference currently selected in the External Society Tree.
     */
    public ExternalAgentReference currentExtRef; // "" in main external tree 
   
    // The constants are used to interact with the combo boxes on the permissions panel 
    static final String strIS_ENABLED = new String("is enabled");
    static final String strIS_NOT_ENABLED = new String("is NOT enabled");
    
    static final String strMAY_INITIATE = new String("may initiate");
    static final String strMAY_NOT_INITIATE = new String("may NOT initiate");
    
    static final String strMAY_RESPOND = new String("may respond to  ");
    static final String strMAY_NOT_RESPOND = new String("may NOT respond to");
    
    static final String strDELEGATES = new String("delegates permissions for ");
    static final String strNOT_DELEGATES = new String("controls permissions for ");


    /** Creates new LiaisonAdminToolController
     * @param args Command line arguments passed from main().
     */
    public LiaisonAdminToolController(String args[]) {
        
       arguments = args;
       alpCollector = new AlpSocietyCollector(this, args);
       extCollector = new MasterExtSocietyCollector(this);  
       liaisonCollector = new LiaisonSetCollector(this);
       alpDisplayServer = new AlpSocDisplayModelServer(this);
       extDisplayServer = new ExtSocDisplayModelServer(this);
       mainWindow = new LiaisonAdminToolMainGUI(this);
    }
    
    

    /** Main entry point for liaison AdminGui.
     * @param args the command line arguments
     */
    public static void main (String args[]) {
        LiaisonAdminToolController  theApplication;
        

        
        theApplication = new LiaisonAdminToolController(args);

        
        // always set the external tree to none. 
        
        theApplication.mainWindow.setFirstExtTree();
        
        // if command line arguments then
        //   add them to the ALPSocietyCollector 
        //  select the first of the specified arguments else 
        theApplication.mainWindow.setFirstAlpTree();
       
        
        // Call this if NO alp tree specified. 
        theApplication.mainWindow.setStatusBar(new String("Choose an ALP society to get started."));
       
        
        theApplication.mainWindow.show();
        
    }
    
    // gets a string for the current Alp selection for use by the Main gui. 
    // it tries to give an idea of the scope-- you'll see. 
    /** Returns a string describing the current ALP selection that can also include descendants.
     */
    public String  getCurrentAlpSelDesc(){
        String  name;
        
        if(currentAlpRef == null)
            return new String("");
        
        
        name = currentAlpRef.getName();
        
        if(currentAlpRef.isAgent()){
            return (new String("Agent '" + name + "'  "));
        } else if (currentAlpRef.isNeighborhood()){
            return (new String("Agents in neighborhood '" + name + "'  "));          
        } else if (currentAlpRef.isSociety()) {
            return (new String("Agents in society '" + name + "'  "));        
        } else {
            return (new String("BAD SELECTION!!"));
        }
    }
    
    
    // this is a more terse version that only gets the name and "type" as a string. 
    /** Returns the name of the currently selected Alp Reference.
     */
    public String  getCurrentAlpSelName(){
        String  name;
        
        if(currentAlpRef == null)
            return new String("");
        
        name = currentAlpRef.getName();
        
        if(currentAlpRef.isAgent()){
            return (new String("Agent '" + name + "'  "));
        } else if (currentAlpRef.isNeighborhood()){
            return (new String("Neighborhood '" + name + "'  "));          
        } else if (currentAlpRef.isSociety()) {
            return (new String("Society '" + name + "'  "));        
        } else {
            return (new String("BAD SELECTION!!"));
        }
    }
    
    // this returns a text description of the selection in the external tree. 
    
    /** Returns a description of the current External Reference selection that can also include descendants.
     */
    public String getCurrentExtSelDesc(){
        String  name;
        
        if (currentExtRef == null)
            return new String("");
        
        name = currentExtRef.getName();
        
        if(currentExtRef.isAgent()){
            return (new String(" interactions with agent '" + name + "'  "));
        } else if (currentExtRef.isNeighborhood()){
            return (new String(" interactions with agents in neighborhood '" + name + "'  "));          
       } else if (currentExtRef.isSociety()) {
            return (new String(" interactions with agents in society '" + name + "'  "));        
        } else {
            return (new String("BAD SELECTION!!"));
        }
    }
    
    


}