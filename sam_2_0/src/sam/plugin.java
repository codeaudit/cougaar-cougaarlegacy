

package sam;

import org.cougaar.core.util.Operator;
import org.cougaar.core.util.OperatorFactory;
//import alp.util.SetRelationship;


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
 */
public class plugin extends contractOwnerBase
{
   // If not null, the Graph node representing this plugin.
   Node theNode;

   // List of graph edges connecting our node to our suppliers.
   Vector theEdges;

   // Create the plugin object
   public plugin(String pluginName, contractOwnerBase parent)
   {
      super(parent);

      name = pluginName;
      theEdges = new Vector();
   }

   // Attach the contract records to this plugin.
   public void attachContracts(systemContracts theSystemContracts, final boolean debug)
   {
      if( debug )
         System.out.println("Attaching contracts for plugin " + getName() );

      // Attach the plugin contract
      setOfContractOperators contractOperators = theSystemContracts.getPluginContract(name);

      // Shouldn't happen.
      if( contractOperators == null )
      {
         System.out.println("Warning: Plugin " + name + " in cluster " + parent.getName() + " does not have a contract.");
         contract = new setOfContractLinkPoints();
      }
      else
      {
         contract = new setOfContractLinkPoints(contractOperators, this);

         // Warn of missing contract.
         if( contractOperators.publish.isEmpty() && contractOperators.subscribe.isEmpty() )
         {
            System.out.println("Warning: Plugin " + name + " in cluster " + parent.getName() + " does not have a contract.");
         }
      }

      if( debug )
      {
         System.err.println("Plugin " + name + " in cluster " + parent.getName() + ": Loaded " +
               getNumInputs() + " subscriptions, " + getNumOutputs() + " publications");
      }
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
      System.out.println("Warning: Tried to render Plugin as a graph.");
      // Should probably throw an exception.
   }

   /// Render the cluster as a graph.
   public void renderNode(GraphModel model)
   {
      // Create the nodes
      theNode = model.createNode(this);

      // Add add it to the graph
      model.addNode(theNode);
     /* //String Long_name = getName();
      MainWindow.theMainWindow.fieldname.setText("plugin:" + name);
      System.out.println("plugin:" + name);*/
   }

   /// Compute our dependencies our contracts.
   public void computeDependencies(Collection thePlugins, boolean debug)
   {
      if( debug )
      {
         System.out.println("\nComputing dependencies for plugin " + name);
      }

      // Skip system plugins.
      if( contract.systemObject )
         return;

      if( debug )
      {
         System.out.println("  checking plugin " + getName() + "'s " + contract.subscribe.size() + " subscribe contracts");
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

         // Handle case where this plugin doesn't have a contract.
         if( subscribeContract == null )
         {
            if( debug )
               System.err.println("Warning: plugin " + getName() + " has an empty subscribe contract!");
            continue;
         }

         // Now step through all the other plugins, so we can see who provides the object.
         Iterator pluginsIT = thePlugins.iterator();
         while(pluginsIT.hasNext())
         {
            contractOwnerBase pubplug = (contractOwnerBase)pluginsIT.next();

            if( debug )
            {
               System.out.println("  comparing it to plugin " + pubplug.getName() + "'s " + pubplug.contract.publish.size() + " publish contracts");
            }

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
                     System.err.println("Warning: plugin " + pubplug.getName() + " has an empty publish contract!");
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
                     System.out.println("Plugin " + getName() + "'s subscription contract:\n   " +
                                        subscribeContract.toString() + "\nis fullfilled by plugin " +
                                        getName() + "'s publish contract\n   " + publishContract.toString() + "\n");
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
               ourRec.addChildResult("Subscription " + offset + " is unfulfilled", theContract );
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
               ourRec.addChildResult("Publication " + offset + " is unused", theContract );
               hadMsg = true;
            }

            // Is this publication oversubscribed?
            else if( theContract.links.size() > 1 )
            {
               ourRec.addChildResult("Publication " + offset + " is oversubscribed", theContract );
               hadMsg = true;
            }


            offset ++;
         }
      }

      return hadMsg;
   }


   /// Print the dependencies of our plugins based on their contracts.
   public void printDependencies()
   {
      System.out.println("    Plugin " + getName());



      // Skip system plugins.
      if( contract.systemObject )
      {
         System.out.println("      Is a system plugin and is ignored.");
         return;
      }

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

   // If not null, the Graph node representing this plugin.
   public Node getNode()
   {
      return theNode;
   }

   // Move up to our parent.
   public boolean moveUp()
   {
      getParentNode().renderGraph(MainWindow.theMainWindow.theSociety.theJGraph);
      return true;
   }

   /// Get the number of input slots (subscriptions) for this record.
   public int getNumInputs()
   {
      return contract.getNumSubscriptions();
   }

   /// Get the number of output slots (publications) for this record.
   public int getNumOutputs()
   {
      return contract.getNumPublications();
   }

   /// Is the specified input slot connected?
   public boolean isInputSlotConnected(int slot)
   {
      return contract.isInputSlotConnected(slot);
   }

   /// Is the specified input slot connected?
   public boolean isOutputSlotConnected(int slot)
   {
      return contract.isOutputSlotConnected(slot);
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

      return "Plugin: " + name;
   }

   // Return our long name.
   public String getLongName()

   {

      return toString();
   }

   // Return a disjoint copy of the subtree rooted at this node.
   public contractOwnerBase recursiveCopy(contractOwnerBase newParent)
   {
      plugin rtn = new plugin(name, newParent);

      return rtn;
   }
   ///////////////////////////////////////////Method to write the xml of the plugin to the file/////////////////////////////////

    public boolean writeToXml(PrintWriter pw, boolean debug)
   {

      //this will write the xml for the cluster
      if (debug )
      {
        // System.out.println( " Writing the xml for the plugin to the file : " + fileName.toString());
         System.out.println( "The name of the plugin is : " + getName());
      }

       //printing the xml structure for the plugin
       pw.println("    <plugin name=" +"\""+getName()+"\"" + ">" + "</plugin>" );
       return true;

    }
   ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

   //////////////////////////////////////////Method to write the ini file details///////////////////////////////////////////////
   public boolean writeIniFile(PrintWriter pw,  boolean debug)
   {
      pw.println( "plugin = " + getName());
      return true;
   }
} //ends the class plugin
