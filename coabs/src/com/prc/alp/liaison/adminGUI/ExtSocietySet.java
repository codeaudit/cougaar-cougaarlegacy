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
import com.prc.alp.liaison.admin.AgentReference;
import com.prc.alp.liaison.admin.ExternalAgentReference;
import  java.util.HashSet;
import  java.util.Iterator;
import java.util.ArrayList;

/**
 * A derivation of AgentSocietySet that supports a set of External agent references.
 *  It only adds specific methods for debugging, and looking up references by
 *  External IDs.
 *
 * @author John Page
 * @version 1.1
 * @since 1.0
 */
public class ExtSocietySet extends AgentSocietySet {

    /** Creates new ExtSocietySet */
    public ExtSocietySet() {
        super();
    }
    /** Create New ExtSocietySet and specify society name */
    public ExtSocietySet(String society){
        super(society);
    }
    
    /** This method returns a text description of the society Mostly for debugging*/

    public String createTextDump(){
        Iterator  it;
        ExternalAgentReference  thisAgent;
        String theDump;
        ArrayList  theChildren;
        
        
        theDump = new String("Agent Society: top is :" + topRef.getName() + " \r");
        theChildren = getChildren(topRef);
        theDump = theDump + "   It has: " + theChildren.size() + " children \r";
        
        it = theChildren.iterator();
        while(it.hasNext()){
            thisAgent = (ExternalAgentReference) it.next();
            theDump = theDump + getTextForExtAgent(thisAgent);           
            theDump = theDump + "r\r";          
        }
        
        return theDump;
    }
    
    
    /** Get a text dump for a specific agent-- only for debugging, and no longer 
        supported. 
     */
    public  String  getTextForExtAgent(ExternalAgentReference thisAgent){
        String  theDump = new String("");
        
        if(thisAgent == null){
            theDump = theDump + "ExternalAgentReference<null>\r";
            return theDump;
        }
        
            theDump = theDump + "       " + thisAgent.getName() + ": \r";
            
            if(thisAgent.isSociety()) 
                theDump = theDump + "             Type: Society\r";
            else if (thisAgent.isNeighborhood())
                theDump = theDump + "             Type: Neighborhood\r";
            else if (thisAgent.isAgent())
                theDump = theDump + "             Type: Agent\r";
            else
                theDump = theDump + "             Type:  BAD TYPE!!\r";
            
            
            theDump = theDump + "             Society: "; 
                
            if(thisAgent.society == null) 
                theDump = theDump + "<null>\r";
            else
                theDump = theDump + thisAgent.society + "\r";
            
            theDump = theDump + "             Neighborhood: " ;
            if(thisAgent.neighborhood == null)
                theDump = theDump + "<null>\r";
            else
                theDump = theDump + thisAgent.neighborhood + "\r";
            
            theDump = theDump + "             Agent: ";
            if(thisAgent.agent == null)
                theDump = theDump + "<null>\r";
            else
                theDump = theDump + thisAgent.agent + "\r";
            
            theDump = theDump + "             Owner: ";
            if(thisAgent.owner == null)
                theDump = theDump + "<null>\r";
            else
                theDump = theDump + thisAgent.owner + "\r";
           
            theDump = theDump + "             Desc: ";
            if(thisAgent.description == null)
                theDump = theDump + "<null>\r";
            else
                theDump = theDump + thisAgent.description + "\r";  
            
            theDump = theDump + "             externalID: ";
            if(thisAgent.externalID == null)
                theDump = theDump + "<null>\r";
            else
                theDump = theDump + thisAgent.externalID + "\r";
            
            theDump = theDump + "r\r";
        
            return theDump;
    }
        
 
    /** Print out contents of Set to System.err */
    public void printOutContents() {  
        String  dump;
        
        dump = createTextDump();
        System.err.print(dump);
      
    }
    
    
    /** Return an external agent ref given its ID , or null if none found..  */
    public AgentReference getAgentRefByID(String agentID){
        Iterator    it;
        ExternalAgentReference  thisAgent;
        
        it = iterator();
        
        while(it.hasNext()){
            thisAgent = (ExternalAgentReference) it.next();
            if(thisAgent.externalID.equals(agentID))
                return(thisAgent);
        }
        // if we've gotten this far, we can't find it
        
        return null;    
    }

}