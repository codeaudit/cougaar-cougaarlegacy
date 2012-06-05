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
import com.prc.alp.liaison.admin.ALPAgentReference;
import  java.util.HashSet;
import  java.util.Iterator;
import  java.util.ArrayList; 



/**
 * This class is a specialization of the AgentSociety set that is customized for
 * representing ALP societies.  It contains methods for determining which ALP references may
 * control other references, a mechanism for generating ALPIDs, and a validate() method that insures the
 * set is complete and structurally sound.
 *
 * @author John Page
 * @version 1.1
 * @since 1.0
 */
public class AlpSocietySet extends AgentSocietySet {

    /** Creates new AlpSocietySet.  Will take on society name of the first  */
    public AlpSocietySet() {
        super();            
    }
    
    /** constructor that takes a society name */
    
    public AlpSocietySet(String society){
    super(society);
    }
        
    
    /** This method returns a text description of the society Mostly for debugging*/

    public String createTextDump(){
        Iterator  it;
        ALPAgentReference  thisAgent = null;
        String theDump;
        ArrayList   theChildren;
        ALPAgentReference  testAgent;
 
        it = this.iterator();
        theDump = new String("Agent Society: " + societyName + " has " + this.size() + " agents.\n");
        while(it.hasNext()){
            thisAgent = (ALPAgentReference) it.next();
            /*
            if(thisAgent.isSociety()){
                theDump = theDump + "Society: " + thisAgent.society + "  ALPID: " + thisAgent.ALPID + "\r";
            }else if (thisAgent.isNeighborhood()){
                theDump = theDump + "     Neighborhood: " + thisAgent.neighborhood + "  ALPID: " + thisAgent.ALPID + "\r";
            }else {
                theDump = theDump + "          Agent:" + thisAgent.agent + "  ALPID: " + thisAgent.ALPID + "\r";
            }  
          */
          System.out.println(thisAgent);
        }
        
        theDump = theDump + "\r\r The Top Ref is:";
        if(topRef == null)
            theDump = theDump + "<null>";
        
        
   
        return theDump;
    }
        
 
    /** Print contents of set to System.err */
    public void printOutContents() {  
        String  dump;
        
        dump = createTextDump();
        System.err.print(dump);
      
    }
    
    
    /** Retrieve an agent by its ID */
    public AgentReference getAgentRefByID(String agentID){
        Iterator    it;
        ALPAgentReference  thisAgent;
        
        it = iterator();
        
        while(it.hasNext()){
            thisAgent = (ALPAgentReference) it.next();
            if(thisAgent.ALPID.equals(agentID))
                return(thisAgent);
        }
        // if we've gotten this far, we can't find it
        
        return null;    
    }
    
    /**  returns true if any ancestors are either not enabled or do not 
    delegate authority.  */ 
    public  boolean  isAncestorInControl(AgentReference child){
        ArrayList  ancestors;
        Iterator    it;
        ALPAgentReference  thisAncestor;
        
        ancestors = getAncestors(child);
        
        // if no ancestor, then no problem!
        if (ancestors.size() == 0)
            return false;
        
        it = ancestors.iterator();
        while(it.hasNext()){
            thisAncestor = (ALPAgentReference) it.next();
            // if ancestor not enabled, then I'm not in control 
            if (!(thisAncestor.isEnabled.booleanValue()))
                  return true;
            // if my ancestor does not delegate authority, I must obey. 
            if(!(thisAncestor.delegatesAuthority.booleanValue()))
                  return true; 
        }
        
       // Otherwise, there is no ancestor in control.  
        return false; 
        
        
    }
    
    /**  Returns the controlling ancestor, or returns null if there is 
     none */
    
    public  ALPAgentReference getControllingAncestor(AgentReference child){
        ArrayList  ancestors;
        Iterator    it;
        ALPAgentReference  thisAncestor;
        
        ancestors = getAncestors(child);
        
        // if no ancestor, then no problem!
        if (ancestors.size() == 0)
            return null;
        
        // otherwise, starting at the end (top) of the tree, start looking for the 
        // first node, if any, the either is disabled or does not delegate authority.
        
        for (int i = ancestors.size() - 1; i >= 0; i--){
            thisAncestor = (ALPAgentReference) ancestors.get(i);
            if((!(thisAncestor.delegatesAuthority.booleanValue())) |
               (!(thisAncestor.isEnabled.booleanValue())) ){
                    return thisAncestor;
             } // of if
        } // of for 
        
        // if we've gotten this fall, no one is in control of this object. 
        
        return null; 
          
        
    }
    
    /**  This is just a convenience method to add a list of references at once. */
    public  void loadAgents(ArrayList   agentList){
        Iterator it;
        ALPAgentReference  thisRef;
        
        it = agentList.iterator();
        
        while(it.hasNext()){
            thisRef = (ALPAgentReference)it.next();
            addRef(thisRef);
        }       
    }
    
  
    /** Creates a new ALP.  Hopefully unique, but is currently based on system time. */
    public  String  generateALPID(String  agentName, String agentType){
        // For a stopgap, I'll generate an ID that uses the system time, name, and type
       return new String(agentName + System.currentTimeMillis() + agentType);     
    }
    
    /**  This routine is needed to make sure that the ALP Set is structurally 
         complete.  Once  a set of ALP agents is loaded from the Java Space, it is 
         responsible for making sure that defaults are created for any neighborhood or
         society references that are not explicitly defined in the javaspace.  It is 
         imperative that this method be called before any attempts are made to generate 
         any display models of the set. 
    
         Note:  This method only supports a three tiered approach with atomic 
         neighborhoods at the moment.  A more robust algorithem would need to build/validate
         the ancestor path for each node.  */ 
    
    public  void  validate(){
        ALPAgentReference  thisRef;
        ALPAgentReference  theParent;
        AlpSocietySet    cloneSet;

  
        Iterator  it;
   
        // don't bother if we are empty
        if(this.size() == 0)
            return;  
        
        // if there is no society node, we must build one. 
        
        if(this.topRef == null){
           theParent = ALPAgentReference.create(this.societyName, 
                                                null,
                                                null,
                                                null,
                                                null,  
                                               generateALPID(this.societyName,
                                                             new String("Society")));
           theParent.isEnabled = new Boolean(true);
           theParent.delegatesAuthority = new Boolean (false);
           this.addRef(theParent);
        }
        
        cloneSet = (AlpSocietySet) this.clone();
        it = cloneSet.iterator();
        while(it.hasNext()){
            thisRef = (ALPAgentReference) it.next();
            theParent = (ALPAgentReference) getParent(thisRef);
            if(theParent == null){
                if(thisRef.isAgent()){
               // then create the neighborhood
                  theParent = ALPAgentReference.create(this.societyName, 
                                                         thisRef.neighborhood,
                                                         null,
                                                         null,
                                                         null,  
                                                         generateALPID(thisRef.neighborhood,
                                                                       new String("Neighborhood")));
                   theParent.isEnabled = new Boolean(true);
                   theParent.delegatesAuthority = new Boolean (false);
                   this.addRef(theParent);                   
                } else if (thisRef.isNeighborhood()){
                    // create the society 
                     theParent = ALPAgentReference.create(this.societyName, 
                                                         null,
                                                         null,
                                                         null,
                                                         null,  
                                                         generateALPID(thisRef.society,
                                                                       new String("Society")));
                    theParent.isEnabled = new Boolean(true);
                    theParent.delegatesAuthority = new Boolean (false);
                    this.addRef(theParent);
                    
                } else {
                   // Do nothing-- a society doesn't need a parent.  
                }                       
            }
            
            // I'm checking the permissions of childen when I validate the 
            // LSRs, so this may be sufficient.                      
        }             
    }
    
    
    
    
    
    
 
  
    
}