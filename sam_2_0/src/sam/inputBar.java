

package sam;

import java.io.*;
import java.util.*;
import sam.LoadContractFiles.*;
import diva.graph.GraphView;
import sam.display.samJGraph;
import diva.graph.model.Node;
import diva.graph.model.GraphModel;
import diva.graph.model.Edge;
import diva.canvas.Site;
import sam.EdgeData;
/**
 * class inputBar This class is used to render the input subscriptions flowing into a page.
 * Title:        Sam<p>
 * Description:  ALP Business Process User Interface<p>
 * <copyright>
 *    Copyright (c) 2000-2001 Defense Advanced Research Projects
 *    Agency (DARPA) and Mobile Intelligence Corporation.
 *    This software to be used only in accordance with the
 *    COUGAAR license agreement.
 * </copyright>
 * Company:      Mobile Intelligence Corp.<p>
 * @author Doug MacKenzie
 * @version 1.0
 *
 *
 */

public class inputBar extends contractOwnerBase
{
   // If not null, the Graph node representing this inputBar.
   Node theNode;

   // List of graph edges connecting our node to our suppliers.
   Vector theEdges;

   // Create the inputBar object
   public inputBar(String inputBarName, contractOwnerBase parent)
   {
      super(parent);

      name = inputBarName;
      theEdges = new Vector();
   }

   // Attach the contract records to this inputBar.
   /**
    * Attach the contract record to the input bar.<p>
    * @param theSystemContracts.<p>
    * @param debug boolean.<p>
    */
   public void attachContracts(systemContracts theSystemContracts, final boolean debug)
   {
      // Get our parent's contract.  We'll use the subscriptions for our contract.
      setOfContractOperators contractOperators = null;

      // Switch on our parent's type to get the correct contract.
      if( parent instanceof cluster )
         contractOperators = theSystemContracts.getClusterContract(name);
      else if( parent instanceof society )
         contractOperators = theSystemContracts.getSocietyContract(name);
      else if( parent instanceof community )
         contractOperators = theSystemContracts.getCommunityContract(name);
      else
         System.err.println("Internal Error: inputBar::attachContracts - Got unknown parent record: " + parent);

      // Shouldn't happen.
      if( contractOperators == null )
      {
         System.out.println("Warning: inputBar " + name + " in " + parent.getName() + " does not have a contract.");
         contract = new setOfContractLinkPoints();
      }
      else
      {
         // Attach only the subscribe contracts.
         // WATCH THIS: The inputBar represents the parent's subscribe contracts, but it uses them as publications!!!
         contract = new setOfContractLinkPoints(contractOperators, this, false);

         // Warn of missing contract.
         if( debug && contractOperators.publish.isEmpty() )
         {
            System.out.println("Warning: inputBar " + name + " in " + parent.getName() + " does not have a contract.");
         }
      }

      if( debug )
      {
         System.err.println("inputBar " + name + " in " + parent.getName() + ": Loaded " +
               getNumInputs() + " subscriptions, " + getNumOutputs() + " publications");
      }
   }

   /// Compute our dependencies based on our contracts.
   /**
    * Compute the dependencies based on the contracts.<p>
    * @param thePlugins Collection.<p>
    * @param debug boolean.<p>
    */
   public void computeDependencies(Collection thePlugins, final boolean debug)
   {
      if( debug )
      {
         System.out.println("  checking inputBar " + getName() + "'s " + contract.subscribe.size() + " subscribe contracts");
      }

      // outputBars don't have subscriptions, so nothing to do.
   }

   // Tell each plugin to compute its dependencies.
   // (appends them to its list, so need to clear them first)
   /**
    * Compute the member dependencies, but the input bar doesnot have any members.<p>
    * @param debug boolean.<p>
    */
   public void computeMemberDependencies(final boolean debug)
   {
      // We don't have members.
   }

   // Tell each level to clear its dependencies.
   /**
    * Clear all the Member dependencies, but inputbar doesnot have any members.<p>
    */
   public void clearMemberDependencies()
   {
      // We don't have members.
   }

   /// Report any unresolved dependencies in human readable form.
   /**
    * Return true if there are nay problems associated.<p>
    * @param ourRec ResultsNode.<p>
    * @param warningLevel integer.<p>
    */
   public boolean analyze(ResultsNode ourRec, int warningLevel)
   {
      boolean hadMsg = false;

      // quietly ignore any system objects.
      if( !contract.systemObject )
      {

         // Report any unconnected subscriptions
         Iterator contract_it = contract.subscribe.iterator();
         int offset = 1;
         while(contract_it.hasNext())
         {
            contractLinkPoint theContract = (contractLinkPoint)contract_it.next();

            // Is this subscription dangling?
            if( theContract.links.size() == 0 )
            {
               ourRec.addChildResult("Output " + offset + " is unfulfilled", theContract );
               hadMsg = true;
            }

            offset ++;
         }

         // Report any of our outputs that don't have exactly one consumer
         contract_it = contract.publish.iterator();
         offset = 1;
         while(contract_it.hasNext())
         {
            contractLinkPoint theContract = (contractLinkPoint)contract_it.next();

            // Is this publication dangling?
            if( theContract.links.size() == 0 )
            {
               ourRec.addChildResult("Input " + offset + " is unused", theContract );
               hadMsg = true;
            }

            // Is this publication oversubscribed?
            else if( theContract.links.size() > 1 )
            {
               ourRec.addChildResult("Input " + offset + " is oversubscribed", theContract );
               hadMsg = true;
            }


            offset ++;
         }
      }

      return hadMsg;
   }


   /// Print our dependencies based on our contracts.
   /**
    * Print the Dependencies based on the contracts.<p>
    */
   public void printDependencies()
   {
      System.out.println("    outputBar " + getName());

      // Step through the contracts.
      System.out.println("      Uses data provided by:");
      Iterator contract_it = contract.subscribe.iterator();
      boolean had_one = false;
      while(contract_it.hasNext())
      {
         contractLinkPoint theContract = (contractLinkPoint)contract_it.next();

         // Step through each contract's subscription links.
         Iterator link_it = theContract.links.iterator();
         while(link_it.hasNext())
         {
            contractLinkPoint provider = (contractLinkPoint)link_it.next();
            System.out.println("         " + provider.getParent().getName() );
            had_one = true;
         }
      }
      if( !had_one )
         System.out.println("         none.");

      // Step through the publish links.
      System.out.println("      Publishes data used by:");
      contract_it = contract.publish.iterator();
      had_one = false;
      while(contract_it.hasNext())
      {
         contractLinkPoint theContract = (contractLinkPoint)contract_it.next();

         // Step through each contract's subscription links.
         Iterator link_it = theContract.links.iterator();
         while(link_it.hasNext())
         {
            contractLinkPoint provider = (contractLinkPoint)link_it.next();
            System.out.println("         " + provider.getParent().getName() );
            had_one = true;
         }
      }
      if( !had_one )
         System.out.println("         none.");


      System.out.println();
   }
   /**
    * Clear.<p>
    */

   public void wipe()
   {
      theEdges.clear();
   }

   /// Does this node represent a graph we can display?
   /**
    * Returns false.<p>
    */
   public boolean hasGraph()
   {
      return false;
   }

   /// Implement renderGraph, but we can't do it anyway.
   /**
    * Implements the render Graph.<p>
    * @param graph samJGraph.<p>
    */
   public void renderGraph(samJGraph graph)
   {
      System.out.println("Warning: Tried to render inputBar as a graph.");
      // Should probably throw an exception.
   }

   /// Render us as a node.
   /**
    * Render as a node.<p>
    * @param model GraphModel.<p>
    */
   public void renderNode(GraphModel model)
   {
      // Create the node
      theNode = model.createNode(this);

      // Add add it to the graph
      model.addNode(theNode);
   }

   /**
    * Render input edges.<p>
    * @param model GraphModel.<p>
    */
   public void renderInputEdges(GraphModel model)
   {
      // We don't have any input edges.
      return;
   }

   // If not null, the Graph node representing this inputBar.
   /**
    * Returns the Graph node representing the inputBar.<p>
    */
   public Node getNode()
   {
      return theNode;
   }

   // Move up to our parent.
   /**
    * Returns false as cannot move up.<p>
    *
    */
   public boolean moveUp()
   {
      System.out.println("Warning: Tried to moveUp in an inputBar.");
      // Should probably throw an exception.

      return false;
   }

   /// Get the number of input slots (subscriptions) for this record.
   /**
    * Returns the number of inputslots, will always return 0, as there will be no inputslots.<p>
    */
   public int getNumInputs()
   {
      // We don't have any inputs.
      return 0;
   }

   /// Get the number of output slots (publications) for this record.
   /**
    * Returns the number of the output slots.<p>
    */
   public int getNumOutputs()
   {
      return contract.getNumPublications();
   }

   /// Is the specified input slot connected?
   /**
    * Returns false as there are no input slots.<p>
    * @param slot integer.<p>
    */
   public boolean isInputSlotConnected(int slot)
   {
      // We don't have any inputs.
      return false;
   }

   /// Is the specified input slot connected?
   /**
    * Returns true if the output slot is connected else returns false.<p>
    */
   public boolean isOutputSlotConnected(int slot)
   {
      return contract.isOutputSlotConnected(slot);
   }

   // Return our set of children as a list.
   // (we don't have any)
   /**
    * Returns the children as a collection, but the input bar does not have any children.<p>
    */
   public Collection getChildren()
   {
      return new ArrayList();
   }

   /// Return the number of children.
   /**
    * Returns the number of children present as an integer.<p>
    * Will always return 0, as the input bar does not have any  children.<p>
    */
   public int getChildCount()
   {
      return 0;
   }

   /// Return the object's name for display in the jTree
   /**
    * Returns the name of the object that should be displayed in the Jtree as a String.<p>
    *
    */
   public String toString()
   {
      return "Input Contracts";
   }

   // Return our long name.
   /**
    * Returns the name as String as Input Contracts for XXX.<p>
    */
   public String getLongName()
   {
      return "Input Contracts for " + parent.getLongName();
   }

   // Return a disjoint copy of the subtree rooted at this node.
   /**
    * Returns the disjoint copy of the subtree at this node.<p>
    */
   public contractOwnerBase recursiveCopy(contractOwnerBase newParent)
   {
      inputBar rtn = new inputBar(name, newParent);

      return rtn;
   }
}
