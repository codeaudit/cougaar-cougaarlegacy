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
 * Authors:  John Page, Brandon L. Buteau
 *
 */

package com.prc.alp.liaison.adminGUI;
import java.util.ArrayList; 
import java.lang.reflect.Array;
import com.prc.alp.liaison.admin.ALPAgentReference;
import com.prc.alp.liaison.admin.LiaisonSpace;
import java.util.Iterator;

/**
 * The AlpSocietyCollector provides the functionality needed to bring an ALP Society into
 * the Liason Admin Tool.
 *
 * @author John Page
 * @author Brandon L. Buteau
 * @version 1.1
 * @since 1.0
 */
public class AlpSocietyCollector extends SocietyCollector {
    LiaisonAdminToolController theApplication;
    String[]  commandArgs;

    /** Creates new AlpSocietyCollector
     * @param app Pointer to the LiaisonAdminToolController (application shell) that is
     * creating it.  This provides access to all public data structures.
     * @param args command line arguments from the LiaisonAdminToolController.main.
     */
    public AlpSocietyCollector(LiaisonAdminToolController app, String[] args) {
        theApplication = app; 
        commandArgs = args;
    }
    
    /** return a list of objects representing all ALP Societies reachable from
       the tool  */
    public ArrayList getSocietyList(){
        /* Currently knocking it up with fake data, and only one society. 
           note-- any society called "none" implies that none is loaded. */
        ArrayList  theList;
        AlpSocietyDescriptor  theDesc;
        int arrayLen;
        String  thisArg;
        int strOffset;
        String  newDescString;
        
         theList = new ArrayList();
        
        theDesc = new AlpSocietyDescriptor("none");
        theList.add(theDesc);
        
        // add the command args here.
        
        try{
            arrayLen = Array.getLength(commandArgs);
            for(int i = 0; i< arrayLen; i++){
                // Make sure that we throw away any arguments that occur before the 
                // name of the main class
                thisArg = (String) Array.get(commandArgs, i);
                if(thisArg.startsWith("-ALP=")){
                    strOffset = thisArg.indexOf("=");
                    if(thisArg.length() - 1 > strOffset){
                        newDescString = thisArg.substring(strOffset + 1);
                        theDesc = new AlpSocietyDescriptor(newDescString);
                        theList.add(theDesc);
                    }                 
                }
            }
        } catch (IllegalArgumentException badArg){
            System.err.println("LiaisonAdminTool: AlpSocietyCollector: Bad arguemnt!");
            badArg.printStackTrace(System.err);        
        } catch (ArrayIndexOutOfBoundsException  badIndex){
            System.err.println("LiaisonAdminTool: AlpSocietyCollector: BadIndex!");
            badIndex.printStackTrace(System.err);
        }
        
        
        
        theDesc = new AlpSocietyDescriptor("test");    
        theList.add(theDesc);
        
        return theList;
        
    }  
   
    /** Debug method that prints out a list of agentReferences */
    
    public  String  getRefListDump(ArrayList  theList){
        Iterator it;
        String theString;
        ALPAgentReference thisRef;
        
        if(theList == null){
            theString = new String("Null pointer for array list!!");
            return theString;
        }
        
        theString = new String("List of agents has " + theList.size() + "entries \r");
        it = theList.iterator();
        while(it.hasNext()){
            thisRef = (ALPAgentReference)it.next();
            theString = theString + ("     " + thisRef.getName() + "\n");
        }
        
        return theString;
    }
            
    
    
   /** Return an Alp Society specified by the descriptor. 
    Note-- this will also, as a side effect, set theApplication.alpSet to the 
    returned set value.  We need this in case we launch any listners from this
    method. 
    */
    public AgentSocietySet getSocietyFromDescriptor(SocietyDescriptor desc){
        AlpSocietySet  theSet;
        ALPAgentReference thisAgent; 
        ArrayList       agentRefList;
        
        //System.err.println("Top of get Society from Descriptor");
        
        theSet = new AlpSocietySet(desc.societyName);
        theApplication.alpSet = theSet;
        
         if(desc.societyName.equalsIgnoreCase(new String("none"))){
            //System.err.println("None selected --- empty Set!");
            return null;
        }
        
        // If the name of the society is test, return canned data. 
        //System.err.println("Getting Agent List.");
        
        if(desc.societyName.equalsIgnoreCase(new String("test")))      
            agentRefList = getTestSocietyList(desc);
        else
            agentRefList = getALPAgentsFromJavaSpace(desc);
        
        //System.err.println("Adding the following list of ALP Agents.");
        //System.err.print(getRefListDump(agentRefList));

        // populate the set with return ALPAgentReferences 
        
        //System.err.println("Loading the agents."); 
        theSet.loadAgents(agentRefList);
        
        //System.err.println("contents before validation...");
        //theSet.printOutContents();
        // make sure this is a valid society representation.
        // add any implied agents. 
        theSet.validate();
        //System.err.println("contents after validation");
        //theSet.printOutContents();
            
        return theSet;      
    }
    
    /** This method is responsible for returning all ALPAgentReferences that 
      it can find in the JavaSpace with the name desc.societyName; */
    public  ArrayList   getALPAgentsFromJavaSpace(SocietyDescriptor desc){
        String societyName = desc.societyName;  // name of the society 
        
        LiaisonSpace lspace = new LiaisonSpace(societyName);
        ALPAgentReference aref = 
          ALPAgentReference.create(societyName, null, null, null, null, null);
        ArrayList result = null;
        try {
          result = lspace.scanAll(aref);
        } catch (Exception ex) {
          System.err.println("AlpSocietyCollector failed to get ALP agents from " +
                             "JavaSpace " + societyName);
        }
        return result;
        
        //return getTestSocietyList(desc);
        
    } 
    
    /** This method was for debugging only.  Check the source code. */
    public ArrayList getTestSocietyList(SocietyDescriptor  desc){
        ArrayList theList;
        ALPAgentReference thisAgent; 
   
        
        theList  = new ArrayList();
        
        // Create a society level reference 
        
       /* thisAgent = ALPAgentReference.createDefault(desc.societyName, null, null, 
                                                  "Barry Rhine", "The CEO", "0000000001");
        theSet.addRef(thisAgent);  
        
        // create a set of mid level community references 
        
        thisAgent = ALPAgentReference.createDefault(desc.societyName, "DefenseSystems", null, 
                                                    "K.Schneider", "Sr. VP", "00000423");
        
        theSet.addRef(thisAgent);
        
        thisAgent = ALPAgentReference.createDefault(desc.societyName, "DEIS", null, 
                                                    "B. Webb", "Sr. VP",  "98383777");
        theSet.addRef(thisAgent);
        */
        
        thisAgent = ALPAgentReference.createDefault(desc.societyName, "DEIS", "Paul O'Brien",
                                                    "?", "HISS", "666");
        theList.add(thisAgent);
        
        thisAgent = ALPAgentReference.createDefault(desc.societyName, "DEIS", "Ed Clarke",
                                                    "?", "DARAP", "u84747474462");
        theList.add(thisAgent);
        
        
        
        thisAgent = ALPAgentReference.createDefault(desc.societyName, "Civil", null,
                                                    "D. Brown", "Sr. VP", "884746");
        theList.add(thisAgent);
        
        // now create some "agents" that report to these others.  
        
        thisAgent = ALPAgentReference.createDefault(desc.societyName, "DefenseSystems", "Al Pflugrad", 
                                                    "?", "Systems and Process engineering", "8383773");
        
        theList.add(thisAgent);
        
        thisAgent = ALPAgentReference.createDefault(desc.societyName, "DefenseSystems", "J. Franklin",
                                                    "?", "Center for Applied Technology", "8387373"); 
        theList.add(thisAgent);
        
        return(theList);
        
    } 
    

    
    /** This method is no longer supported. */ 
    public AgentSocietySet loadSocietyByName(String socName){
        SocietyDescriptor   socDesc;
        
        socDesc = new SocietyDescriptor(socName);
       return(getSocietyFromDescriptor(socDesc));
        
    }
    
    
    /**  This method turns off/disconnect any listeners that will be bringing in updates.
         It is to be invoked whenever a new society is brought in.  We aren't quite ready to user it yet.
    */
    public void  releaseSociety(){
        
        
    } 
    
    /** This routine writes out a list of agent references to the JavaSpace.  It does not 
     *  have to know if they already exist or not. 
     */
    
    public void writeOutALPListtoJavaSpace(ArrayList  alpRefList){
        Iterator  it = alpRefList.iterator();
        while ( it.hasNext() ) {
          ALPAgentReference  alpRef = (ALPAgentReference) it.next();
          String matchID = alpRef.ALPID;
          LiaisonSpace lspace = new LiaisonSpace(alpRef.society);
          boolean written;
          ALPAgentReference template =
            ALPAgentReference.create(null, null, null, null, null, matchID);
          try {
            lspace.writeOrReplace(template, alpRef);
          } catch (Exception ex) {
            System.err.println("AlpSocietyCollector failed to write ALP agent to " +
                               "JavaSpace " + alpRef.society);
          }
        }
        
        
        // currently this just prints out the items to be saved.
        
        /* Commented out  
        
        if(alpRefList.size() == 0)
        {
            System.err.println("writeOutOutListtoJavaSpace:  Empty list!");
            System.err.println("Nothing going out to society: " + theApplication.currentAlpRef.society);
            return;
        }
        
        System.err.println("WriteOutALPListtoJavaSpace: storing the following in " +
                                theApplication.currentAlpRef.society);
        
        
        
        it = alpRefList.iterator();
        while(it.hasNext()){
            alpRef = (ALPAgentReference) it.next();
            System.err.println("     Saving " + alpRef.getName());
        }
        
        
        */
     
          
    } 

}