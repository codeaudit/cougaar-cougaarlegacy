package sam.graphPlanner;



import sam.graphPlanner.graphNode;
import sam.graphPlanner.graphLink;

import java.util.Collection;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.HashSet;
import java.util.Iterator;
/**
 * Title:        Sam
 * Description:  ALP Business Process User Interface
 * <copyright>
 *    Copyright (c) 2000-2001 Defense Advanced Research Projects
 *    Agency (DARPA) and Mobile Intelligence Corporation.
 *    This software to be used only in accordance with the
 *    COUGAAR license agreement.
 * </copyright>
 * Company:      Mobile Intelligence Corp.
 * @author Doug MacKenzie
 * @version 1.0
 */
public class graphPlanner
{
   // Status values.
   private final int RUNNING = 0;
   private final int BLOCKED = 1;
   private final int COMPLETE = 2;

   ///////////////////////////////////////////////////
   // Configuration parms.

   // Once a solution is found, how many extra levels should be tested to
   // find alternative solutions?
   public int ExtraProbeDepth = 1;

   //
   /**
    * Returns true if partial solutions are acceptable if no complete solution exists.<p>
    */
   public boolean acceptPartialSolutions = true;

   // Turns on debug messages.
   private boolean debug = false;

   ///////////////////////////////////////////////////
   // Global state variables

   // The number of probes still having the RUNNING status
   private int numRunning;

   // The number of complete solutions found so far.
   private int numSolutions;

   // Container holding the complete solutions derived so far.
   // Each entry is an ArrayList holding the graphNodes in the solution.
   private ArrayList solutions;

   ///////////////////////////////////////////////////
   // Empty constructor
   /**
    * Empty constructor
    */
   public graphPlanner(boolean debugFlag)
   {
      debug = debugFlag;
   }

   private class subGoal
   {
      public graphNode node;
      public graphLink link;
   };

   private class State
   {
      // What is the status of this solution?
      /**
       * Integer value for the status of the solution.<p>
       */
      public int status;

      // The list of input graphLinks which are not yet connected.
      /**
       * list of input graphLinks which are not yet connected.<p>
       */
      public ArrayList unconnectedInputs;

      // The list of output graphLinks which are not yet connected.
      /**
       * list of output graphLinks which are not yet connected.<p>
       */
      public ArrayList unconnectedOutputs;

      // The set of output graphLinks which are available for fullfilling inputs.
      // This is the union of the outputs sets for all nodes included in the original
      // system plus the solution.
      /**
       * Set of output graphLinks which are available for fullfilling inputs.<p>
       */
      public HashSet allInputs;

      // The set of output graphLinks which are available for fullfilling inputs.
      // This is the union of the outputs sets for all nodes included in the original
      // system plus the solution.
      /**
       * Set of output graphLinks which are available for fullfilling inputs.<p>
       */
      public HashSet allOutputs;

      // The list of state records for our children.
      /**
       * list of state records for our children.<p>
       */
      public ArrayList state;

      // The list of available nodes - Those not yet in the solution.
      /**
       * list of available nodes.<p>
       */
      public Collection availableNodes;

      // The list of nodes in the solution.
      /**
       * list of nodes in the solution.<p>
       */
      public ArrayList solution;


      /**
       * Constructor.
       */
      public State()
      {
         status = RUNNING;
      }

      // Constructor.
      public State(ArrayList _unconnectedInputs, ArrayList _unconnectedOutputs,
                   HashSet _allInputs, HashSet _allOutputs, Collection _availableNodes,
                   ArrayList _solution)
      {
         status = RUNNING;
         state = null;
         unconnectedInputs = _unconnectedInputs;
         unconnectedOutputs = _unconnectedOutputs;
         allInputs = _allInputs;
         allOutputs = _allOutputs;
         availableNodes = _availableNodes;
         solution = _solution;
      }

      // Make a copy suitable for passing to a recursive child.
      public State makeCopy()
      {
         State s = new State();

         s.unconnectedInputs = new ArrayList();
         s.unconnectedInputs.addAll( this.unconnectedInputs );

         s.unconnectedOutputs = new ArrayList();
         s.unconnectedOutputs.addAll( this.unconnectedOutputs );

         s.allInputs = new HashSet();
         s.allInputs.addAll( this.allInputs );

         s.allOutputs = new HashSet();
         s.allOutputs.addAll( this.allOutputs );

         // Leave state null

         s.availableNodes = new HashSet();
         s.availableNodes.addAll( this.availableNodes );

         s.solution = new ArrayList();
         s.solution.addAll( this.solution );

         return s;
      }

      /// Print the state.
      /**
       * Print the status as a String.<p>
       */
      public String toString()
      {
         String buf = "";

         // add the status value.
         switch(status)
         {
            case RUNNING:
               buf = buf.concat("   Status: Running ");
               break;
            case BLOCKED:
               buf = buf.concat("   Status: Blocked ");
               break;
            case COMPLETE:
               buf = buf.concat("   Status: Complete ");
               break;
            default:
               buf = buf.concat("   Status: Unknown");
               break;
         }

         // Add the pending inputs
         buf = buf.concat("   Pending inputs: Count=" + unconnectedInputs.size() );

         // Add the pending outputs
         buf = buf.concat("   Pending outputs: Count=" + unconnectedOutputs.size() );

         // Add the active inputs
         buf = buf.concat("   Active inputs:   Count=" + allInputs.size() );

         // Add the active outputs
         buf = buf.concat("   Active outputs: Count=" + allOutputs.size() );

         // Add the available nodes
         buf = buf.concat("   Available nodes: Count=" + availableNodes.size() );

         buf = buf.concat("   Solution:\n");
         Iterator it = solution.iterator();
         while( it.hasNext() )
         {
            graphNode node  = (graphNode)it.next();
            buf = buf.concat("   " + node + "\n" );
         }

         return buf;
      }
   }

   // Suggest one or more collections of "components" which together fill the
   // gap between the inputs and outputs in the graph.
   //    theComponents is an arrayList of "component" records.
   // Returns true on success, false if no complete solution exists.
   /**
    * Returns true if a complete solution exists and false if there is no complete solution.<p>
    * @param boundaryNode graphNode.<p>
    * @param availableNodes Collection.<p>
    * @Param results ArrayList.<p>
    * @returns True on success and false on failure.<p>
    */
   public boolean plan(graphNode boundaryNode, Collection availableNodes, ArrayList results)
   {
      if( boundaryNode == null )
      {
         System.err.println("Error: graphPlanner::plan - Called with null boundaryNode");
         return false;
      }

      if( debug )
      {
         System.err.println("graphPlanner::plan(" + boundaryNode + ")");
      }

      // Init.
      solutions = results;
      numSolutions = 0;
      numRunning = 0;

      // Get the list of unconnected inputs (this is a regression planner)
      // and compute the set of active outputs.
      // These are lists of graphLinks
      ArrayList unconnectedInputs = new ArrayList();
      ArrayList unconnectedOutputs = new ArrayList();
      HashSet allInputs = new HashSet();
      HashSet allOutputs = new HashSet();
      HashSet currentNodes = new HashSet();
      if( !gatherStats(boundaryNode, unconnectedInputs, unconnectedOutputs, allInputs, allOutputs, currentNodes, true) )
      {
         return false;
      }

      if( debug )
      {
         System.err.println("Number of pending inputs:  " + unconnectedInputs.size() );
         System.err.println("Number of pending outputs: " + unconnectedOutputs.size() );
         System.err.println("Number of available nodes: " + availableNodes.size());
         System.err.println("Number of all outputs:  " + allOutputs.size());
         System.err.println("Number of all inputs:   " + allInputs.size());
         System.err.println("Current nodes: " + currentNodes.size());
         Iterator it = currentNodes.iterator();
         while( it.hasNext() )
         {
            graphNode node  = (graphNode)it.next();
            System.err.println("   " + node );
         }
         System.err.println("");

      }

      // Duck out if nothing to do.
      if( unconnectedInputs.size() == 0 && unconnectedOutputs.size() == 0)
      {
         return true;
      }

      // Make the initial state record.
      numRunning++;
      HashSet unusedNodes = new HashSet();
      unusedNodes.addAll(availableNodes);

      // The removeAll command does not work, so use the hard way....
      Iterator itUnused = unusedNodes.iterator();
      while( itUnused.hasNext() )
      {
         graphNode unusedNode  = (graphNode)itUnused.next();

         Iterator itUsed = currentNodes.iterator();
         while( itUsed.hasNext() )
         {
            graphNode usedNode  = (graphNode)itUsed.next();

            if( unusedNode.getLongName().equals( usedNode.getLongName() ) )
            {
               // It is used, so delete it.
               itUnused.remove();
               break;
            }
         }
      }

      if( debug )
      {
         System.err.println("Unused Nodes:");
         Iterator it = unusedNodes.iterator();
         while( it.hasNext() )
         {
            graphNode node  = (graphNode)it.next();
            System.err.println("   node: " + node );
         }
      }

      State state = new State(unconnectedInputs, unconnectedOutputs, allInputs, allOutputs, unusedNodes, new ArrayList() );

      int level = 0;
      int firstSolutionLevel = -1;
      boolean foundFirstSolution = false;
      boolean done = false;
      do
      {
         // Try one deeper.
         level ++;

         // do it.
         fixOne( state, level );

         // See if we should quit.
         if( numRunning <= 0 )
         {
            done = true;
         }

         // Remember the level where we found the first solution.
         // There isn't much point in going very far beyond that level.
         if( !foundFirstSolution && numSolutions > 0 )
         {
            foundFirstSolution = true;
            firstSolutionLevel = level;
         }

         // Have we probed the required number of extra levels?
         if( foundFirstSolution && (level >= firstSolutionLevel + ExtraProbeDepth) )
         {
            done = true;
         }

      } while( !done );

      if( debug )
      {
         System.err.println("solutions found: " + numSolutions);
      }

      // If we didn't find any solutions, and they want partial solutions, return some.
      if( numSolutions == 0 && acceptPartialSolutions )
      {
         results.addAll( findBestPartialSolutions(state) );
         numSolutions = results.size();
      }

      // Return true if returning complete solutions, false if partial.
      return numSolutions > 0;
   }

   ////////////////////////////////////////////////////////////////////////////////////////
   // work on each of the remaining inputs and outputs in parallel (iterative deepening).
   /**
    * @param state State.<p>
    * @param remainingdepth integer.<p>
    */
   private void fixOne( State state, int remainingDepth)
   {
      if( debug )
      {
         System.err.println("fixOne: state=" + state + " remainingDepth=" + remainingDepth);
      }

      // Is our solution complete?
      if( state.unconnectedInputs.size() == 0 && state.unconnectedOutputs.size() == 0)
      {
         // Mark us complete and save our solution.
         numRunning --;
         if( addSolution(solutions, state.solution ) )
         {
            // Yea, a unique solution
            state.status = COMPLETE;
         }
         else
         {
            // Boo, someone beat us to it.
            state.status = BLOCKED;
         }

         if( debug )
         {
            System.err.println("fixOne: Completed." + state);
         }

         return;
      }

      // Are we blocked?
      if( state.availableNodes.size() == 0 )
      {
         // Mark us blocked.
         state.status = BLOCKED;
         numRunning --;

         if( debug )
         {
            System.err.println("fixOne: Blocked. Status=" + state);
         }

         return;
      }

      // Are we at the limit?
      if( remainingDepth <= 0 )
      {
         if( debug )
         {
            System.err.println("fixOne: at the depth limit. State=" + state);
         }

         return;
      }

      // First time at this depth?
      boolean blocked = true;
      if( state.state == null )
      {
         // Yes, allocate state for our children.
         state.state = new ArrayList();

         if( debug )
         {
            System.err.println("fixOne: allocating new state for children. State=" + state);
         }

         // Check each of the available nodes to see if they supply or consume something useful.
         Iterator nodeIT = state.availableNodes.iterator();
         while( nodeIT.hasNext() )
         {
            graphNode node  = (graphNode)nodeIT.next();

            // Is this node used already?
            boolean usedit = false;

            if( debug )
                System.err.println("checking Node " + node + ".  It has " + node.getOutputs().size() + " outputs");

            // work on each of the remaining inputs in parallel (iterative deepening).
            for(int i=0; i<state.unconnectedInputs.size(); i++)
            {
               // The link we are considering
               graphLink link = (graphLink)state.unconnectedInputs.get(i);

               // Check each of this node's outputs to see if any match.
               Iterator it = node.getOutputs().iterator();
               while( it.hasNext() )
               {
                  graphLink nodesLink = (graphLink)it.next();

                  if( link.isCompatible(nodesLink) )
                  {
                     if( debug )
                         System.err.println("   it matched");

                     // They match, make sure it won't oversubscribe one of its inputs.
                     if ( !willOversubscribe(node, state) )
                     {
                        // Use it!

                        // Make a copy of our state info for the child and fix it up to reflect their state.
                        State rec = state.makeCopy();
                        state.state.add( rec );

                        // Delete the node record from the list of available.
                        rec.availableNodes.remove(node);

                        // Add the new node to the solution
                        rec.solution.add(node);

                        // Update the I/O sets.
                        rec.allOutputs.addAll( node.getOutputs() );
                        rec.unconnectedOutputs.addAll(node.getOutputs() );
                        rec.allInputs.addAll( node.getInputs() );
                        rec.unconnectedInputs.addAll( node.getInputs() );

                        // Remove any unconnected inputs which will connect to the new outputs.
                        removeCompatible(rec.unconnectedInputs, rec.allOutputs );

                        // Remove any unconnected outputs which will connect to the new inputs.
                        removeCompatible(rec.unconnectedOutputs, rec.allInputs );

                        // Recurse.
                        blocked = false;
                        numRunning ++;
                        fixOne( rec, remainingDepth - 1);

                        usedit = true;
                        break;
                     }
                  }
               }

               if( usedit )
                  break;
            }

            if( usedit )
               continue;

            // work on the outputs too.
            for(int i=0; i<state.unconnectedOutputs.size(); i++)
            {
               // The link we are considering
               graphLink link = (graphLink)state.unconnectedOutputs.get(i);

               // Check each of this node's outputs to see if any match.
               Iterator it = node.getInputs().iterator();
               while( it.hasNext() )
               {
                  graphLink nodesLink = (graphLink)it.next();

                  if( link.isCompatible(nodesLink) )
                  {
                     if( debug )
                         System.err.println("   it matched");

                     // They match, make sure it won't oversubscribe one of its inputs.
                     if ( !willOversubscribe(node, state) )
                     {
                        // Use it!

                        // Make a copy of our state info for the child and fix it up to reflect their state.
                        State rec = state.makeCopy();
                        state.state.add( rec );

                        // Delete the node record from the list of available.
                        rec.availableNodes.remove(node);

                        // Add the new node to the solution
                        rec.solution.add(node);

                        // Update the I/O sets.
                        rec.allOutputs.addAll( node.getOutputs() );
                        rec.unconnectedOutputs.addAll(node.getOutputs() );
                        rec.allInputs.addAll( node.getInputs() );
                        rec.unconnectedInputs.addAll( node.getInputs() );

                        // Remove any unconnected inputs which will connect to the new outputs.
                        removeCompatible(rec.unconnectedInputs, rec.allOutputs );

                        // Remove any unconnected outputs which will connect to the new inputs.
                        removeCompatible(rec.unconnectedOutputs, rec.allInputs );

                        // Recurse.
                        blocked = false;
                        numRunning ++;
                        fixOne( rec, remainingDepth - 1);

                        usedit = true;
                        break;
                     }
                  }
               }

               if( usedit )
                  break;
            }

            if( usedit )
               continue;

            // Well, can we drop it in without adding more unconnected outputs?
/*
            // Check each of this node's outputs and take it if it doesn't add any
            // new unconnected outputs.
            boolean okToUse = true;
            Iterator it = node.getOutputs().iterator();
            while( it.hasNext() )
            {
               graphLink nodesLink = (graphLink)it.next();

               if( state.unconnectedOutputs.
            }
*/
         }
      }
      else
      {
         if( debug )
         {
            System.err.println("fixOne: restarting at level " + remainingDepth);
         }

         // We are restarting this level, so allow each of our children
         // to take one more step deeper.
         Iterator it = state.state.iterator();
         while( it.hasNext() )
         {
            State rec = (State)it.next();
            if( rec.status == RUNNING )
            {
               blocked = false;
               fixOne( rec, remainingDepth - 1);
            }
         }
      }

      // Are all our children blocked?
      if( blocked )
      {
         state.status = BLOCKED;
         numRunning --;

         if( debug )
         {
            System.err.println("fixOne: Blocked because children are blocked. State=" + state);
         }
      }
      else
      {
         Iterator it1 = state.state.iterator();
         while( it1.hasNext() )
         {
            State rec1 = (State)it1.next();

            Iterator it2 = state.state.iterator();
            while( it2.hasNext() )
            {
               State rec2 = (State)it2.next();

               if( rec1 != rec2 && nodeCollectionsAreEqual( rec1.solution, rec2.solution ) )
               {
                  // Prune the duplicate branch.
                  it1.remove();
                  if( debug )
                  {
                     System.err.println("fixOne: Pruned a duplicate state");
                  }

                  break;
               }
            }
         }
      }
   }

   /**
    *
    * @param node graphNode.<p>
    * @param state State.<p>
    *
    */

   private boolean willOversubscribe(graphNode node, State state)
   {
      Iterator inputsIT = node.getInputs().iterator();
      while( inputsIT.hasNext() )
      {
         graphLink inLink = (graphLink)inputsIT.next();

         // See if this input link will oversubscribe the provider.
         Iterator outputsIT = state.allOutputs.iterator();
         while( outputsIT.hasNext() )
         {
            graphLink outLink = (graphLink)outputsIT.next();

            if( inLink.isCompatible(outLink) && !state.unconnectedOutputs.contains(outLink) )
            {
               if( debug )
                   System.err.println("   Adding node " + node.getLongName() + " would oversubscribe node " + outLink.parentName() + "'s output");

               return true;
            }
         }
      }

      // no problems
      return false;
   }


   ////////////////////////////////////////////////////////////////////////////////////////
   // Used to build the initial sets for the planner from the user's graph.
   // The initial caller should pass isContainer as true.
   /**
    * @param graphNode node.<p>
    * @param List unconnectedInputs.<p>
    * @param List unconnectedOutputs.<p>
    * @param Set inputs.<p>
    * @param Set outputs.<p>
    * @param Set currentNodes.<p>
    * @param boolean isContainer.<p>
    */
   private boolean gatherStats(graphNode node, List unconnectedInputs, List unconnectedOutputs,
                               Set inputs, Set outputs, Set currentNodes, final boolean isContainer)
   {
      if( node == null )
      {
         System.err.println("Error: graphPlanner::gatherStats called with null node");
         return false;
      }

      // We don't want the container in our sets, just its children
      if( !isContainer )
      {
         // Add it to our list.
         currentNodes.add(node);

         Collection inputList = node.getInputs();
         if( inputList == null )
         {
            System.err.println("Error: graphPlanner::gatherStats node " + node + " has null input list");
            return false;
         }
         // Add any inputs for this node.
         Iterator it = inputList.iterator();
         while( it.hasNext() )
         {
            graphLink link = (graphLink)it.next();

            // It is a set, so let it get rid of dups.
            inputs.add(link);

            if( !link.isConnected() )
            {
               unconnectedInputs.add(link);
            }
         }

         Collection outputList = node.getOutputs();
         if( outputList == null )
         {
            System.err.println("Error: graphPlanner::gatherStats node " + node + " has null output list");
            return false;
         }
         // Add any outputs for this node.
         it = outputList.iterator();
         while( it.hasNext() )
         {
            graphLink link = (graphLink)it.next();

            // It is a set, so let it get rid of dups.
            outputs.add(link);

            if( !link.isConnected() )
            {
               unconnectedOutputs.add(link);
            }
         }
      }

      // Let our children do their thing.
      Collection children = node.getChildren();
      if( children == null )
      {
         System.err.println("Error: graphPlanner::gatherStats node " + node + " has null children list");
         return false;
      }

      Iterator it = children.iterator();
      while( it.hasNext() )
      {
         graphNode child = (graphNode)it.next();

         if( child == null )
         {
            System.err.println("Error: graphPlanner::gatherStats node " + node + " has a null record on its children list");
            return false;
         }

         if( !gatherStats( child, unconnectedInputs, unconnectedOutputs, inputs, outputs, currentNodes, false ) )
            return false;
      }

      return true;
   }

   //////////////////////////////////////////////////////////////////////
   private class Solution
   {
      public ArrayList solution;
      public int score;
   };

   // User function to load the solutions array with the best partial solutions.
   // Returns an ArrayList of ArrayList of graphNode records.
   /**
    * Returns an array list of the graph node records.<p>
    * @param State state.<p>
    */
   private ArrayList findBestPartialSolutions(State state)
   {
      // Compute the best solutions
      // Returns an arrayList of "Solution" records.
      ArrayList solutions = internalFindBestPartialSolutions(state);

      // Build a corresponding arraylist of just the solutions
      ArrayList rtn = new ArrayList();
      Iterator it = solutions.iterator();
      while( it.hasNext() )
      {
         Solution sol = (Solution)it.next();
         rtn.add(sol.solution);
      }

      // Return the arrayList of ArrayList of graphNodes.
      return rtn;
   }

   //////////////////////////////////////////////////////////////////////
   // Add the solution to our list, making sure it is not a duplicate.
   // returns true if solution is unique, false if duplicate
   /**
    * Returns true if the solution is unique, and false if duplicate.<p>
    * @param Collection solutions.<p>
    * @param  Solution newSolution.<p>
    */
   private void addPartialSolution(Collection solutions, Solution newSolution)
   {
      boolean dup = false;

      // Empty solution?
      if( newSolution.solution.size() == 0 )
          return;

      // Check if we already know about this solution
      Iterator it = solutions.iterator();
      while( it.hasNext() )
      {
         Solution existing = (Solution)it.next();

         if( nodeCollectionsAreEqual( existing.solution, newSolution.solution ) )
         {
            // Is a duplicate.
            dup = true;
            break;
         }
      }

      if( !dup )
      {
         solutions.add(newSolution);
      }
   }

   ////////////////////////////////////////////////////////////////////////////
   // Internal function to support findBestPartialSolutions.
   // It returns an ArrayList of "Solution" records which are
   // the "Best" solutions for this node and its children.
   // It minimizes the number of unconnectedInputs+unconnectedOutputs.
   /**
    * Internal function to support findBestPartialSolutions.<p>
    * Returns an array list of all the best solutions.<p>
    * @param State state.<p>
    */
   private ArrayList internalFindBestPartialSolutions(State state)
   {
      // Do a depth first search in the state tree and pick solutions which
      // have the minimum number of unconnectedInputs+unconnectedOutputs

      // A container for our solutions.
      // Contains "Solution" records.
      ArrayList rtn = new ArrayList();

      // Anything to do?
      if( state == null || state.state == null )
         return rtn;

      // Ask our kids for their best solutions.
      Iterator it = state.state.iterator();
      while( it.hasNext() )
      {
         State curState = (State)it.next();
         ArrayList kids = internalFindBestPartialSolutions(curState);

         // Add these solutions to our list, making sure not to add duplicates.
         Iterator kidsIT = kids.iterator();
         while( kidsIT.hasNext() )
         {
            Solution sol = (Solution)kidsIT.next();
            addPartialSolution(rtn, sol);
         }
      }

      // Add our solution to the mix
      Solution ours = new Solution();
      ours.solution = state.solution;
      ours.score = scorePartialSolution(state);
      addPartialSolution(rtn, ours);

      // If we have more than one, figure out what the "best" solution in this list is.
      if( rtn.size() > 1 )
      {
         // Find the best solution
         int bestSize = 0;
         boolean firstTime = true;
         it = rtn.iterator();
         while( it.hasNext() )
         {
            Solution curState = (Solution)it.next();
            if( firstTime )
            {
               firstTime = false;
               bestSize = curState.score;
            }
            else if( curState.score < bestSize )
            {
               bestSize = curState.score;
            }
         }

         // Now throw out any worse than the best one.
         it = rtn.iterator();
         while( it.hasNext() )
         {
            Solution curState = (Solution)it.next();
            if( curState.score > bestSize /*+ ExtraProbeDepth*/)
            {
               it.remove();
            }
         }
      }

      return rtn;
   }

   /////////////////////////////////////////////////////////////////////////
   /// Compute a score for the partial solution
   /**
    * Returns the Integer value for the partial solution.<p>
    * @param State state.<p>
    */
   private int scorePartialSolution(State state)
   {
      int numUnconnected = countUniqueLinks(state.unconnectedInputs) +
                           countUniqueLinks(state.unconnectedOutputs);

      // Count the number of nodes that have all of their inputs or outputs unused.
      int numFullyUnconnected = 0;
      Iterator it = state.solution.iterator();
      while( it.hasNext() )
      {
         graphNode node = (graphNode)it.next();

         boolean used = false;
         Iterator inputsIT = node.getInputs().iterator();
         while( inputsIT.hasNext() )
         {
            graphLink link = (graphLink)inputsIT.next();

            // is this link used?
            if( !state.unconnectedInputs.contains(link) )
            {
               used = true;
               break;
            }
         }

         // Count it.
         if( !used )
         {
            // All of this node's inputs are unconnected.
            numFullyUnconnected ++;
         }

         used = false;
         Iterator outputsIT = node.getOutputs().iterator();
         while( outputsIT.hasNext() )
         {
            graphLink link = (graphLink)outputsIT.next();

            // is this link used?
            if( !state.unconnectedOutputs.contains(link) )
            {
               used = true;
               break;
            }
         }

         if( !used )
         {
            numFullyUnconnected ++;
         }

      }

      // Return the score
      return numFullyUnconnected + numUnconnected;
   }

   /////////////////////////////////////////////////////////////////////////
   // Count the number of unique links in the collection of GraphLinks
   /**
    * Returns the number of the unique links in the collection of the Grpahic links.<p>
    * @param links Collection
    */
   private int countUniqueLinks(Collection links)
   {
      ArrayList unique = new ArrayList();

      Iterator srcIT = links.iterator();
      while( srcIT.hasNext() )
      {
         graphLink srcLink = (graphLink)srcIT.next();

         boolean isDup = false;
         Iterator desIT = unique.iterator();
         while( desIT.hasNext() )
         {
            graphLink desLink = (graphLink)desIT.next();

            if( desLink.isEqual(srcLink) )
            {
               isDup = true;
               break;
            }
         }

         if( !isDup )
             unique.add( srcLink );
      }

      return unique.size();
   }

   //////////////////////////////////////////////////////////////////////
   // -- Expensive -- O(n^2) on size of input and output lists.
   /**
    * Remove inputs from the inputList which are satisfied by outputs in the outputList.<p>
    * @param inputList Collection, outputList Collection.<p>
    */
   private void removeCompatible(Collection inputList, Collection outputList )
   {
      Iterator inputIT = inputList.iterator();
      while( inputIT.hasNext() )
      {
         graphLink inLink = (graphLink)inputIT.next();

         Iterator outputIT = outputList.iterator();
         while( outputIT.hasNext() )
         {
            graphLink outLink = (graphLink)outputIT.next();

            if( inLink.isCompatible(outLink) )
            {
               // Delete it.
               inputIT.remove();
               break;
            }
         }
      }
   }

   //////////////////////////////////////////////////////////////////////
   //
   // -- Expensive -- O(n^2) on size of inpuoutput lists.
   /**
    * Returns true if the collections of graphNodes are the same, false otherwise.
    * @param a Collection.<p>
    * @param b Collection.<p>
    */
   private boolean nodeCollectionsAreEqual(Collection a, Collection b )
   {
      if( a.size() != b.size() )
      {
          return false;
      }

      Collection bucket = new ArrayList();
      bucket.addAll( a );

      Iterator aIT = bucket.iterator();
      while( aIT.hasNext() )
      {
         graphNode nodeA = (graphNode)aIT.next();

         Iterator it = b.iterator();
         while( it.hasNext() )
         {
            graphNode nodeB = (graphNode)it.next();

            if( nodeA.getLongName().equals( nodeB.getLongName()) )
            {
               // Delete it.
               aIT.remove();
               break;
            }
         }
      }

      return bucket.size() == 0;
   }

   //////////////////////////////////////////////////////////////////////
   //
   // returns true if solution is unique, false if duplicate
   /**
    * Add the solution to our list, making sure it is not a duplicate.<p>
    * @param solutions Collection.<p>
    * @param newSolution.<p>
    */
   private boolean addSolution(Collection solutions, Collection newSolution)
   {
      boolean dup = false;

      // Empty solution?
      if( newSolution.size() == 0 )
          return false;

      // Check if we already know about this solution
      Iterator it = solutions.iterator();
      while( it.hasNext() )
      {
         Collection existing = (Collection)it.next();

         if( nodeCollectionsAreEqual( existing, newSolution ) )
         {
            // Is a duplicate.
            dup = true;
            break;
         }
      }

      if( !dup )
      {
         solutions.add(newSolution);
         numSolutions ++;
      }

      return !dup;
   }
}