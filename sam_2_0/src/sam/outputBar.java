

package sam;

import org.cougaar.core.util.Operator;
import org.cougaar.core.util.OperatorFactory;
//import alp.util.SetRelationship;


import sam.LoadContractFiles.*;
import diva.graph.GraphView;
import sam.display.samJGraph;
import diva.graph.model.Node;
import diva.graph.model.GraphModel;
import diva.graph.model.Edge;
import diva.canvas.Site;
import sam.EdgeData;

import java.io.*;
import java.util.Enumeration;
import java.util.Vector;
import java.util.Collection;
import java.util.Iterator;
import java.util.ArrayList;
/**
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
 * Used to render a row of outputs along a page
 */

public class outputBar extends contractOwnerBase
{
   // If not null, the Graph node representing this outputBar.
   Node theNode;

   // List of graph edges connecting our node to our suppliers.
   Vector theEdges;

   // Create the outputBar object
   /**
    * Constructor.<p>
    */
   public outputBar(String outputBarName, contractOwnerBase parent)
   {
      super(parent);

      name = outputBarName;
      theEdges = new Vector();
   }

   // Attach the contract records to this outputBar.
   /**
    * Attaches the Contracts for the output bar.<p>
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
         System.err.println("Internal Error: outputbar::attachContracts - Got unknown parent record: " + parent);


      // Shouldn't happen.
      if( contractOperators == null )
      {
         System.out.println("Warning: outputBar " + name + " in " + parent.getName() + " does not have a contract.");
         contract = new setOfContractLinkPoints();
      }
      else
      {
         // Attach only the publications
         // WATCH THIS: The outputBar represents the parent's publish contracts, but it uses them as subscriptions!!!
         contract = new setOfContractLinkPoints(contractOperators, this, true);

         // Warn of missing contract.
         if( debug && contractOperators.subscribe.isEmpty() && contractOperators.subscribe.isEmpty() )
         {
            System.out.println("Warning: outputBar " + name + " in " + parent.getName() + " does not have a contract.");
         }
      }

      if( debug )
      {
         System.err.println("outputBar " + name + " in " + parent.getName() + ": Loaded " +
               getNumInputs() + " subscriptions, " + getNumOutputs() + " publications");
      }
   }

   /// Compute our dependencies based on our contracts.
   /**
    * Compute the dependencies based on the contracts.<p>
    * @param thePlugins
    */
   public void computeDependencies(Collection thePlugins, final boolean debug)
   {
      if( debug )
      {
         System.out.println("  checking outputBar " + getName() + "'s " + contract.subscribe.size() + " subscribe contracts");
      }

      // Step through our subscribe contracts
      int inputSlot = 0;
      Iterator it_subscribe = contract.subscribe.iterator();
      while(it_subscribe.hasNext())
      {
         // Get the link point record.
         contractLinkPoint subscribePoint = (contractLinkPoint)it_subscribe.next();

         // get its contract operator.
         Operator subscribeContract = subscribePoint.getContract();

         // Handle case where we don't have a contract.
         if( subscribeContract == null )
         {
            if( debug )
               System.err.println("Warning: " + getLongName() + " has an empty subscribe contract!");
            continue;
         }

         // Now step through all the other outputBars, so we can see who provides the object.
         Iterator pluginsIT = thePlugins.iterator();
         while(pluginsIT.hasNext())
         {
            contractOwnerBase pubplug = (contractOwnerBase)pluginsIT.next();

            // Skip system plugins.
            if( pubplug.contract.systemObject )
               continue;

            // Skip the case where we are checking a plugin against itself.
            // NOTE: ASSUMES A PLUGIN CAN NOT FULFILL ITS OWN CONTRACTS
            if( pubplug == this )
               continue;

            // Step through this plugin's publish contracts
            int outputSlot = 0;
            Iterator it_publish = pubplug.contract.publish.iterator();
            while(it_publish.hasNext())
            {
               // Get the link point record.
               contractLinkPoint publishPoint = (contractLinkPoint)it_publish.next();

               // get its contract operator.
               Operator publishContract = publishPoint.getContract();

               // Handle case where this plugin doesn't have a contract.
               if( publishContract == null )
               {
                  if( debug )
                     System.err.println("Warning: " + pubplug.getLongName() + " has an empty publish contract!");

                  continue;
               }

               // Finally, we get to compare something!
               if( publishContract.implies(subscribeContract) )
               {
                  // This publisher fullfills our subscription, note the relationship
                  subscribePoint.addLink(publishPoint);
                  publishPoint.addLink(subscribePoint);


                  if( debug )
                  {
                     System.out.println("outputBar " + getName() + "'s subscription contract:\n   " +
                                     subscribeContract.toString() + "\nis fullfilled by plugin " +
                                     pubplug.getName() + "'s publish contract\n   " + publishContract.toString() + "\n");
                     System.out.println("Making link from " + pubplug.getName() + " to " + getName() +
                                        "     outputSlot=" + outputSlot + ",  inputSlot="  + inputSlot);
                  }
               }

               outputSlot ++;
            }
         }

         inputSlot ++;
      }

   }

   // Tell each plugin to compute its dependencies.
   // (appends them to its list, so need to clear them first)
   public void computeMemberDependencies(final boolean debug)
   {
      // We don't have members.
   }
   public void clearMemberDependencies()
   {
      // We don't have members.
   }


   /// Report any unresolved dependencies in human readable form.
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


   public void wipe()
   {
      theEdges.clear();
   }

   /// Does this node represent a graph we can display?
   public boolean hasGraph()
   {
      return false;
   }

   /// Implement renderGraph, but we can't do it anyway.
   public void renderGraph(samJGraph graph)
   {
      System.out.println("Warning: Tried to render outputBar as a graph.");
      // Should probably throw an exception.
   }

   /// Render the cluster as a graph.
   public void renderNode(GraphModel model)
   {
      // Create the nodes
      theNode = model.createNode(this);

      // Add add it to the graph
      model.addNode(theNode);
   }

   public void renderInputEdges(GraphModel model)
   {
      // start from scratch.
      theEdges.clear();

      // Step through the subscribe contracts and add edges for each link.
      Iterator contract_it = contract.subscribe.iterator();
      Node dstNode = theNode;
      int dstSlot = 0;
      while(contract_it.hasNext())
      {
         contractLinkPoint theContract = (contractLinkPoint)contract_it.next();

         // Step through each contract's subscription links.
         Iterator link_it = theContract.links.iterator();
         while(link_it.hasNext())
         {
            contractLinkPoint provider = (contractLinkPoint)link_it.next();

            Node srcNode = provider.getParent().getNode();
            int  srcSlot = provider.getSlot();

            // Create the edge data record
            EdgeData rec = new EdgeData();
            rec.srcNode = srcNode;
            rec.srcSlot = srcSlot;
            rec.dstNode = dstNode;
            rec.dstSlot = dstSlot;

            // Create the edge.
            Edge edge = model.createEdge(rec);

            theEdges.addElement(edge);

            // Add it to the model.
            model.connectEdge(edge, srcNode, dstNode);
         }

         // Next input slot.
         dstSlot ++;
      }
   }

   // If not null, the Graph node representing this outputBar.
   public Node getNode()
   {
      return theNode;
   }

   // Move up to our parent.
   public boolean moveUp()
   {
      System.out.println("Warning: Tried to moveup in outputBar.");
      // Should probably throw an exception.

      return false;
   }

   /// Get the number of input slots (subscriptions) for this record.
   public int getNumInputs()
   {
      return contract.getNumSubscriptions();
   }

   /// Get the number of output slots (publications) for this record.
   public int getNumOutputs()
   {
      // We don't have outputs
      return 0;
   }

   /// Is the specified input slot connected?
   public boolean isInputSlotConnected(int slot)
   {
      return contract.isInputSlotConnected(slot);
   }

   /// Is the specified input slot connected?
   public boolean isOutputSlotConnected(int slot)
   {
      // We don't have outputs
      return false;
   }

   // Return our set of children as a list.
   // (we don't have any)
   public Collection getChildren()
   {
      return new ArrayList();
   }

   /// Return the number of children.
   public int getChildCount()
   {
      return 0;
   }

   /// Return the object's name for display in the jTree
   public String toString()
   {
      return "Output Contracts";
   }

   // Return our long name.
   public String getLongName()
   {
      return "Output Contracts for " + parent.getLongName();
   }

   // Return a disjoint copy of the subtree rooted at this node.
   public contractOwnerBase recursiveCopy(contractOwnerBase newParent)
   {
      outputBar rtn = new outputBar(name, newParent);

      return rtn;
   }

}
