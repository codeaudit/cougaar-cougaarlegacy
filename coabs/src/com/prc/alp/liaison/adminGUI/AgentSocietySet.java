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
import  net.jini.core.entry.Entry;
import  com.prc.alp.liaison.admin.AgentReference;
import  com.prc.alp.liaison.admin.ExternalAgentReference;
import  java.util.Arrays;
import  java.util.HashSet;
import  java.util.Iterator;
import  java.util.ArrayList;
import  java.util.Comparator;

/**
 * An AgentSocietySet is an abstract class used to collect Liaison agent references, either ALPAgentReferences or ExternalAgentReferences.  This class also defines all of the methods used for navigating the implied tree structure for the society.
 *
 * @author John Page
 * @version 1.1
 * @since 1.0
 */
public abstract class AgentSocietySet extends HashSet {
    String  societyName; // name of the society
    AgentReference topRef; // Agent Reference that represents the Society

    // Inner class definition for Comparator in ordering agent
    // sets.  It is purely alphabetical based on agent name.
    // this version IS case-sensitive.

    static class AgentRefComp implements Comparator{
        public int compare(Object ref1, Object ref2){
            AgentReference   agentRef1, agentRef2;
            String  ref1Name, ref2Name;

            agentRef1 = (AgentReference) ref1;
            agentRef2 = (AgentReference) ref2;

            ref1Name = agentRef1.getName().toLowerCase();
            ref2Name = agentRef2.getName().toLowerCase();
            return ref1Name.compareTo(ref2Name);
        }
        /*  Don't override the equals for Object...
        public boolean equals(final java.lang.Object p1) {
        }
         */
    }

    /** Creates new AlpSocietySet */
    public AgentSocietySet() {
        super();
        societyName = null;
        topRef = null;
    }

    /** this constructor takes and assigns a societyName
     * @param society
     */

    public AgentSocietySet(String society){
        super();
        societyName = society;
        topRef = null;
    }



    /** Adds an agent reference to the set, doing any additional internal bookkeeping, as well as making sure that the set consistency is not violated.  Does nothing if a bad condition is found.
     * @param ref
     */
    public void addRef(AgentReference ref){
        String refSocietyName;
        boolean  isValid = true;

        //if(ref instanceof ExternalAgentReference)
        //System.err.println("Trying to add external agent...");


        refSocietyName = ref.society;
        if(societyName == null){
            societyName = refSocietyName;
            isValid = true;
        } else {
            /* Can only add agent ref if it is part of this society.  Currently,
            I'll do nothing if a clash occurs, although I'll want to throw an exception
            in the future.
             */
            if(societyName.equals(refSocietyName)){
                isValid = true;
            } else {
                isValid = false;
                System.err.println("Trying to add ref from another society!!");
                System.exit(0);
                ;
                // do nothing for now, otherwise toss an exception
            }
        } // else there is already a society name for this set.

        /* Next-- determine whether or not this Ref is to the top level society,
        or a lower one.  If top level, and none is defined, then assign it.
        if top level, but one exists, teh addition is invalid.
        if not top level, who cares? */

        if(isValid){
            // Test below to be replaced with Brandon's new methods.
            if(ref.isSociety()){
                // This is a society level reference
                if(topRef == null){
                    topRef = ref;
                } else {
                    // Duplicate top ref -- don't add
                    System.err.println("Warning!  Attempt to add duplicate Top Ref");
                    isValid = false;
                } // Is topRef defined already?
            } // is Ref a society Ref?
        } // Is this still a valid addition?


        if(isValid) {
            //if(ref instanceof ExternalAgentReference)
            //System.err.println("Initiating the add");
            add((Object)ref);
        } else {
            //System.out.println("Something BAD Happened.");
            // toss an exception??  or do nothing?
        }
    }



    /* This abstract method requires the set to create a String object that describes the
    set and lists its contents. This will vary among subclasses. */
    /** The method is for debugging-- it creates a string that lists the set's contents.
     * @return A description of this set's contents
     */
    public abstract String createTextDump();
    /* This method will print out such a text dump to System.out */
    /** Prints contents of set to System.err.
     */
    public abstract void printOutContents();





    /** Returns a pointer to the agentReference with the specified ID, or null if not found.
     * @param agentID
     * @return An agent reference
     */
    public abstract AgentReference getAgentRefByID(String agentID);

    /** Determine whether a given reference is the
    parent of a second reference.  Note that currently it is only handling
    very simple cases, and not the ones where the neighborhood field is
    hierarchially divided using a delimiter.
     */
    protected boolean IsParentOf(AgentReference possParent, AgentReference possChild){
        // if potential child is a society, then there is NO WAY this can happen
        if(possChild.isSociety()){
            return false;
        }
        // same if the parent is an agent

        if(possParent.isAgent()){
            return false;
        }
        // Currently, until we are worrying about hiearchically subdivided
        // neighborhoods, a neighborhood cannot belong to another.  This will
        // need to be expanded in the future.

        if(possChild.isNeighborhood()){
            if(possParent.isSociety()){
                return true;
            } else {
                return false;
            }
        }

        // Now, if the child is an agent, there are two possibilities, either it
        // belongs to a neighborhood, or it belongs to the society at large. I'm currently
        // hashing out the representation for the latter.  ALso note the the
        // neighborhood comparison, at the moment, assumes atomic neighborhood names.

        if(possParent.isSociety()){
            // we know by this set that all society names are the same, and
            // the agent is an at large member if neighorhood = ""
            if(possChild.neighborhood.equalsIgnoreCase(new String( ""))){
                return true;
            } else {
                return false;
            }
        }

        // This check will need to be expanded to handle non-atomic
        // neighborhoods in the future
        if(possParent.isNeighborhood()){
            if(possParent.getName().equals(possChild.neighborhood)){
                return true;
            } else {
                return false;
            }
        }

        // if we've gotten this far, something VERY BAD has happened.
        System.err.println("AgentSocietySet: Serious Logic flaw!!");
        return false;

    }





    /** Return the "top node" */
    public  AgentReference getTopReference(){

        return topRef;
    }
    /**  This method returns an ArrayList containing the children of a parent
    note sorted in alphabetical order. -- in the future this may need to support multiple
    types of ordering. This method could be modified to take
    a comparator as an additional argument. */
    public ArrayList getChildren(AgentReference parent){
        Iterator    it;
        ArrayList   theList;
        Object agentArray[];
        AgentReference thisAgent;
        int i;


        theList = new ArrayList();
        it = this.iterator();

        while(it.hasNext()){
            thisAgent = (AgentReference)it.next();
            if(IsParentOf(parent, thisAgent)){
                theList.add(thisAgent);
            } else {

            }
        }
        /* Now sort the returned objects.  If there is a nicer way to do this,
        I'd like to know... */

        it = theList.iterator();
        while (it.hasNext()){
            thisAgent = (AgentReference) it.next();
            //System.err.println(thisAgent.getName());
        }

        if (theList.size() > 0){
            int numKids = theList.size();
            agentArray =  theList.toArray();
            Arrays.sort(agentArray, new AgentRefComp());
            theList.clear();
            for(i=0; i<numKids; i++){
                theList.add(agentArray[i]);
            }
        }
        return theList;
    }

    /** Return an ArrayList containing ALL descendents of a parent
        This list is not sorted.   The parent is NOT included!
        Note-- this algorithm
        will not need to be modified when non-atomic neighborhoods are
        introduced.*/
    public ArrayList getDescendants(AgentReference parent){

        Iterator    it;
        ArrayList  descList;  // Currently Identified descendants
        ArrayList  searchList; // elements which needed to be checked
        // for childen
        ArrayList  newChildrenList;
        AgentReference thisAgentRef;
        AgentReference thisParent;

        descList = new ArrayList();
        searchList = new ArrayList();


        // Short circuit #1-- if AgentReference is an agent, there ARE no
        // descendants.

        if(parent.isAgent()){
            return descList;
        }

        // Short Circuit #2:--  if the parent is a society, EVERYBODY but the
        // parent is returned.

        if(parent.isSociety()){
            it = this.iterator();
            while(it.hasNext()){
                thisAgentRef = (AgentReference)it.next();
                if(!thisAgentRef.isSociety()){
                    descList.add(thisAgentRef);
                }
            }
            return descList;
        }


        // If the parent is a neighborhood, do it the hard way.
        // This code depends on the "getChildren" method, and does not care if
        // we are using atomic or hiearchically nested neighborhoods.

        // quick test for malformed agent ref.

        if(!parent.isNeighborhood()){
            System.err.println("AgentSocietySet:GetDescendants BAD AGENT!!");
            return descList;
        }


        searchList.add(0,parent);
        thisParent = parent;

        while (searchList.size() > 0){
            // add descendants of parent to searchlist
            // Note, there is probably a nice way to optimize this so that
            // we don't have to iterate across the set the whole time.
            newChildrenList = getChildren(thisParent);
            if(newChildrenList.size() > 0){
                descList.addAll(newChildrenList);
                searchList.addAll(newChildrenList);
            }
            //remove parent from searchlist
            searchList.remove(0);
            //prepare to expand anothe member from searchList
            if (searchList.size() > 0)
            thisParent = (AgentReference)searchList.get(0);
        }

        return descList;
    }

    /**
     * Return an agent from the set if it's name is equal to the one specified, or
     * null if not found 
     */
    public  AgentReference getAgentByName(String  agentName){
        Iterator  it;
        AgentReference  thisAgent;

        if(this.size() == 0)
        return null;

        it = this.iterator();
        while(it.hasNext()){
            thisAgent = (AgentReference) it.next();
            if(agentName.equals(thisAgent.getName()))
            return(thisAgent);
        }

        // if we've gotten this far, there is no match.

        return null;


    }

    /** Returns the parent of the specifed agent, or null if the specified agent is 
        the root node. Currently, this implementation assumes no heirarchical subdivisions 
        of neighborhoods.  */
    public AgentReference getParent(AgentReference child){
        // Not implemented.  Haven't convinced myself I need it.
        AgentReference theParent;
        if (child.isSociety())
        return null;

        // This will need to be expanded in the future
        if(child.isNeighborhood()){
            theParent = getAgentByName(child.society);
            return theParent;
        }

        // This will also need to be expanded if neighborhoods are nest.
        if(child.isAgent()){
            theParent = getAgentByName(child.neighborhood);
            return theParent;
        }

        // THis line should never happen.

        return null;


    }

    /** Return an ArrayList of all ancestors for a specified node.  The ordering 
     *  of the list goes from the closest ancestor to the top of the tree. 
     * This routine has NO dependencies on how neighborhoods are treated.
     */ 
    public  ArrayList  getAncestors(AgentReference child){
        AgentReference  theParent;
        ArrayList  returnList = new ArrayList();

        theParent = getParent(child);

        while (!(theParent == null)){
            returnList.add(theParent);
            theParent = getParent(theParent);
        }

        return  returnList;

    }






}