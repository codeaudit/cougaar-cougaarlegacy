

package sam;

import org.cougaar.core.util.Operator;
import org.cougaar.core.util.OperatorFactory;
import sam.LoadIniFiles.iniParser;
import sam.LoadContractFiles.systemContracts;
import sam.LoadContractFiles.contractLinkPoint;
import sam.LoadContractFiles.setOfContractLinkPoints;
import sam.LoadContractFiles.contractOwnerBase;
import sam.LoadContractFiles.setOfContractOperators;

import sam.display.samJGraph;
import diva.graph.model.GraphModel;
import diva.graph.model.Node;
import diva.graph.GraphView;
import diva.graph.model.Edge;

import java.io.File;
import java.io.*;
import java.util.Vector;
import java.util.Iterator;
import java.util.Collection;
import java.util.Enumeration;
import sam.display.samGraphController;

/**
 * class cluster; Deals with the features of the clusters in the society<p>
 * has the capabilities of loading  and manipulating the plugins of each of the cluster.<p>
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
public class cluster extends contractOwnerBase
{
   // Holds the list of plugin records in this society.
   Vector thePlugins;

   // List of graph edges connecting our node to our suppliers.
   Vector theEdges;

   // If not null, the Graph node representing this plugin.
   Node theNode;

   // if not null, the GraphModel for this society rendered as a graph
   GraphModel ourModel;

   // Remember the full ini file data.
   iniParser.Group theGroup;

   /**
    * Constructor
    * @param String  Name of the Cluster, Parent ContractOwnerBase.<p>
    */

   // Construct an empty cluster
   public cluster(String theName, contractOwnerBase parent)
   {
      super(parent);

      wipe();
      name = theName;
   }

   // Free the existing design and init to an empty configuration
   /**
    *To free the existing design and initialize to an empty configuration
    */

   public void wipe()
   {
      thePlugins = new Vector();
      theEdges = new Vector();

      // Not creating a new group!!!!
      theGroup = new iniParser.Group();
      iniParser.Section thenewSection = new iniParser.Section("plugin");
      theGroup.addSection(thenewSection);
      name = "";
   }


 //////////////////////////adding Component ////////////////////////////////////////////////////////////////////
 /**
  * To add a Component
  * @param String Name of the component being added.<p>
  * Return true on success and false on failure
  * */

   public boolean addComponent(String name)
   {
      // Create a new plugin record with the desired name.
      plugin newplugin = new plugin(name,parent);

      return addComponent(newplugin);
   }

   // Add a new component to the record (i.e., a plugin in a cluster)
   // and sets the different flag.
   // Returns true on success, false on error or unsupported.
   /**
    * To add a new and different component to the record (i.e., adding a plugin to the cluster)
    * @param name String the name of the component being added.<p>
    * Returns true on success and false on failure.<p>
    * */
   public boolean addDifferentComponent(String name)
   {
      // Create a new plugin record with the desired name.
      plugin newplugin = new plugin(name,this);

      // Set the different flag
      newplugin.setDifferent(true);
      return addComponent(newplugin);
   }

   // Add a new component to the record (i.e., a plugin in a cluster)
   // Returns true on success, false on error or unsupported.
   /**
    * To add a new component to the vector that holds the plugins
    * @param child  contractOwnerBase
    * Returns true on success and false on failure.
    * */
   public boolean addComponent(contractOwnerBase child)
   {
      // Add the new plugin to this cluster
      thePlugins.add(child);

      // Add the new slot into the group
      //theGroup.getSection("PlugIns").addSlot("plugin",child.getName() );

      // Attach the contract to this new plugin
      child.attachContracts( MainWindow.theMainWindow.theWorld.getSystemContract(), MainWindow.theMainWindow.properties.getVerbose());

      // Recompute plugin depencencies within this cluster.
      // First, clear the existing links
      clearMemberDependencies();

      // Then, recompute them
      computeMemberDependencies(MainWindow.theMainWindow.properties.getVerbose());

      return true;
   }
   ///////////////////////////////////////////////////////////////////////////////////////////////////

   /////////////////////////////Deleting the selected Component////////////////////////////////////////

   /**
    * To Delete the selected object from the Vector, after deleting it from the graph.<p>
    * @param theTarget contractOwnerBase
    * Returns the contract owner base record.
    *
    * */
   public contractOwnerBase cutComponent(contractOwnerBase theTarget)
   {
     //trying to remove the seleected object from the plugins vector
     thePlugins.remove(theTarget);

     //trying to remove that slot from the group
     //theGroup.getSection("PlugIns").deleteSlot("plugin",theTarget.getName() );

     return theTarget;
   }
   /////////////////////////////////////////////////////////////////////////////////////////////////////
   /**
    * Trying to save the cluster to the ini file.<p>
    * @param file File, debugflag boolean
    * Returns true on success and false on failure.
    * */

  /* // Save our cluster to an ini file.
   public boolean savetofile(File file, boolean debugflag)
   {
      String fname = file.getParent()+ "/" + getName() + ".ini";
      //the name of the file to which the cluster is being saved

      boolean retnvalue = true;

      try
      {
         iniParser p = new iniParser();

         p.parsegenerator(fname,theGroup);
      }//ends try
      catch(Exception e)
      {
         e.printStackTrace();
         retnvalue = false;
      }

      return retnvalue;

   }//ends the save to file method*/
/////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
   /// Load the cluster ini file to get the list of plugins.
   /// Returns true on success, false on failure.
   /**
    * To Load from the ini files
    * @param file File, debug boolean
    * Returns true on success and false on failure.
    * */
   public boolean loadFromIniFiles(File file, boolean debug)
   {
    if( debug )
         System.out.println("\nParsing cluster file '" + file.toString() + "'\n");

      // Load into a temporary copy in case doesn't work.
      Vector plugins = new Vector();
      iniParser.Group group;
      // iniParser.Section s;

      // Create the parser object.
      iniParser ip = new iniParser();
       try
       {
         group = ip.parse(file);
         ip.setVerbose(debug);

         // Look through the group and find the "PlugIns" section.
         Vector sections = group.getSections();
         iniParser.Section pluginsSection = null;
         Iterator its = sections.iterator();
         while(its.hasNext())
         {
            iniParser.Section thissection = (iniParser.Section)its.next();

            if( debug )
                System.out.println("Checking Section " + thissection.getName());

                  if(thissection.getName().toLowerCase().equals("plugins"))
                  {
                     // Found it.
                     if( debug )
                        System.out.println("Found the PlugIns section");

                      pluginsSection = thissection;

                     break;
                  }//if ends

         }//ends while loop

         // Did we find it?
         if( pluginsSection == null )
            return false;

         Vector slots = pluginsSection.getSlots();
         Iterator it = slots.iterator();

         while( it.hasNext() )
           {
            iniParser.Slot thisSlot = (iniParser.Slot)it.next();

            // Only take the "plugin = " lines.
            if( thisSlot.getName().toLowerCase().equals("plugin") )
            {
               plugins.addElement( new plugin(thisSlot.getValue(), this) );

               if( debug )
                  System.out.println("Plugin: " + thisSlot.getValue());

            }//if ends
           }//while ends*/
        }//try ends
        catch (Exception ex )
        {
            ex.printStackTrace();
            return false;
        }//catch ends
      // Try to load the cluster's ini file.
//      Vector plugins = new Vector();
      //if( !load(file, plugins, this, debug) )
         //return false;

      // good load, remember the data.
      thePlugins = plugins;
      theGroup = group;

      // Create the input and output bars and add them as fake plugins.
      // Give them our name, since they want our contracts.
      inputBar theInputBar = new inputBar(name, this);
      thePlugins.add(theInputBar);
      outputBar theOutputBar = new outputBar(name, this);
      thePlugins.add(theOutputBar);

      return true;
   }

//////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

   /**
    * To attach the contracts for that particular cluster
    * @param theSystemContracts systemContracts, debug boolean
    */
   public void attachContracts(systemContracts theSystemContracts, final boolean debug)
   {
      if( debug )
         System.out.println("Attaching contracts for cluster " + getName() );


      // Attach the contracts
      setOfContractOperators contractOperators = theSystemContracts.getClusterContract(name);

      // Shouldn't happen.
      if( contractOperators == null )
      {
         System.out.println("The cluster for which the contracts are to be null is: " + name.toString());
         contract = new setOfContractLinkPoints();
         System.out.println("Warning: Cluster " + name + " does not have a contract.");


      }
      else
      {
         contract = new setOfContractLinkPoints(contractOperators, this);

         // Warn of missing contract.
         if( debug && contractOperators.publish.isEmpty() && contractOperators.subscribe.isEmpty() )
         {
            System.out.println("Warning: Cluster " + name + " does not have a contract.");
         }
      }

      if( debug )
      {
          System.err.println("Cluster " + name + ": Loaded " +
               getNumInputs() + " subscriptions, " + getNumOutputs() + " publications");
      }


      // Allow each of our plugins to attach their contracts.
      Iterator it = thePlugins.iterator();
      while( it.hasNext() )
      {
         // Get the plugin record.
         contractOwnerBase thisPlugin = (contractOwnerBase)it.next();

         // Attach the contracts
         thisPlugin.attachContracts(theSystemContracts, debug);
         //System.out.println("the plugin is:"+ (String)it.next());
      }
   }
///////////////////////////////////////////////////////////////////////////////////////////////////////////////

   /**
    * To Compute the dependencies based on the contracts
    * @param theClusters collection, debug boolean
    *
    */
   /// Compute our dependencies based on our contracts.
   public void computeDependencies(Collection theClusters, final boolean debug)
   {
      if( debug )
      {
         System.out.println("\nComputing dependencies for cluster " + getName());
      }

      // Skip system clusters.
      if( contract.systemObject )
         return;

      if( debug )
      {
         System.out.println("  checking cluster " + getName() + "'s " + contract.subscribe.size() + " subscribe contracts");
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
               System.err.println("Warning: cluster " + name + " has an empty subscribe contract!");

            continue;
         }

         // Now step through all the other clusters, so we can see who provides the object.
         Iterator clustersIT = theClusters.iterator();
         while(clustersIT.hasNext())
         {
            contractOwnerBase pubcluster = (contractOwnerBase)clustersIT.next();

            // Skip system clusters.
            if( pubcluster.contract.systemObject )
               continue;

            // Skip the case where we are checking a cluster against itself.
            // NOTE: ASSUMES A CLUSTER CAN NOT FULFILL ITS OWN CONTRACTS
            if( pubcluster == this )
               continue;

            // Step through this cluster's publish contracts
            int outputSlot = 0;
            Iterator it_publish = pubcluster.contract.publish.iterator();
            while(it_publish.hasNext())
            {
               // Get the link point record.
               contractLinkPoint publishPoint = (contractLinkPoint)it_publish.next();

               // get its contract operator.
               Operator publishContract = publishPoint.getContract();

               // Handle case where this cluster doesn't have a contract.
               if( publishContract == null )
               {
                  if( debug )
                     System.err.println("Warning: cluster " + pubcluster.getName() + " has an empty publish contract!");

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
                     System.out.println("Cluster " + getName() + "'s subscription contract:\n   " +
                                        subscribeContract.toString() + "\nis fullfilled by cluster " +
                                        pubcluster.getName() + "'s publish contract\n   " + publishContract.toString() + "\n");
                     System.out.println("Making link from " + pubcluster.getName() + " to " + getName() +
                                        "     outputSlot=" + outputSlot + ",  inputSlot="  + inputSlot);
                  }
               }

               outputSlot ++;
            }
         }

         inputSlot ++;
      }

      // Now let each of our plugin compute their dependencies.
      computeMemberDependencies(debug);
   }
///////////////////////////////////////////////////////////////////////////////////////////////////////////////////
   /**
    * To tell each plugin to compute its dependencies.<p>
    * @param debug boolean.
    */
   // Tell each plugin to compute its dependencies.
   // (appends them to its list, so need to clear them first)
   public void computeMemberDependencies(final boolean debug)
   {
      Iterator it = thePlugins.iterator();

      it = thePlugins.iterator();
      while(it.hasNext())
      {
         // Get the next record.
         contractOwnerBase subplug = (contractOwnerBase)it.next();

         // Let it compute its dependencies.
         subplug.computeDependencies(thePlugins, debug);
      }
   }
///////////////////////////////////////////////////////////////////////////////////////////////////////////////////

   /**
    * To tell each plugin to clear its member dependencies.
    *
    */

   // Tell each plugin to clear its dependencies.
  public void clearMemberDependencies()
   {
      // Tell each of our plugins to clear their dependencies.
      Iterator it_subplug = thePlugins.iterator();
      while(it_subplug.hasNext())
      {
         // Get the next record.
         contractOwnerBase subplug = (contractOwnerBase)it_subplug.next();

         // Let it clear its dependencies.
         subplug.clearLinks();

        // System.out.println("The name of the plugin for which the Dependencies are being cleared is"+ subplug.getName());
      }


   }
///////////////////////////////////////////////////////////////////////////////////////////////////////////////
   /**
    * To analyze if there are any kind of errors associated.<p>
    * @param ourRec ResultsNode, warninglevel integer.<p>
    * returns true on success and false on failure.<p>
    */
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

         // Let each plugin analyze itself.
         Iterator it = thePlugins.iterator();
         while(it.hasNext())
         {
            contractOwnerBase cb = (contractOwnerBase)it.next();

            // Analyze the plugin.
            // Note: We only add the plugin record if there is detail below it.
            ResultsNode rec = new ResultsNode(ourRec, cb.getLongName(), cb);
            if( cb.analyze(rec, warningLevel) )
            {
               // Add it.
               ourRec.children.add(rec);
               hadMsg = true;
            }
         }
      }

      return hadMsg;
   }
////////////////////////////////////////////////////////////////////////////////////////////////////////////
   /**
    * To print the dependencies of the plugins based on their contracts.<p>
   */
   /// Print the dependencies of our plugins based on their contracts.
   public void printDependencies()
   {
      System.out.println("  Cluster " + getName());

      // Skip system plugins.
      if( contract.systemObject )
      {
         System.out.println("      Is a system cluster and is ignored.");
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



      // Step through each plugin.
      Iterator it = thePlugins.iterator();
      while(it.hasNext())
      {
         contractOwnerBase plug = (contractOwnerBase)it.next();

         // Let the plugin print its dependencies
         plug.printDependencies();

      }
   }
/////////////////////////////////////////////////////////////////////////////////////////////
   /**
    * To see if this node in the JTree has a graph
    * Returns true if there is agraph and false if there is no graph.
    */

   /// Does this node represent a graph we can display?
   public boolean hasGraph()
   {
      return true;
   }
///////////////////////////////////////////////////////////////////////////////////////

   /**
    * To render the graph of the cluster.<p>
    * @param graph SamJGraph
    */
   /// Render the cluster as a graph.
   public void renderGraph(samJGraph graph)
   {
      // Make us a new graph model.
      ourModel = new GraphModel();

      // This is a multi-step process.
      // First, create a node for each plugin.
      Iterator it = thePlugins.iterator();
      while(it.hasNext())
      {
         contractOwnerBase plug = (contractOwnerBase)it.next();

         // Skip system plugins.
         if( plug.contract.systemObject )
            continue;

//            System.out.println("   Adding node for plugin " + plug.getName());

         plug.renderNode(ourModel);
      }

      // Finally, create the edges.
      it = thePlugins.iterator();
      while(it.hasNext())
      {
         contractOwnerBase plug = (contractOwnerBase)it.next();

         // Skip system plugins.
         if( plug.contract.systemObject )
            continue;

//            System.out.println("   Adding input edges for plugin " + plug.getName());

         plug.renderInputEdges(ourModel);
      }


      // Attach our model to the graph display
      graph.setGraphModel(ourModel);

      // Tell the world that it is us.
      MainWindow.theMainWindow.theSociety.displayedComponent = this;

      // Display it.
      MainWindow.theMainWindow.layoutJGraph();

      //trying to display the name of the object or the field in the main window for the user to know
      String Long_name = MainWindow.theMainWindow.theSociety.displayedComponent.getLongName();
      MainWindow.theMainWindow.fieldname.setText(Long_name);
      samGraphController gc = (samGraphController)MainWindow.theMainWindow.theSociety.thePane.getGraphController();
      gc.upDatesite();

      //trying to set the buttons to grey out
      MainWindow.theMainWindow.update_BackandForward();

   }
/////////////////////////////////////////////////////////////////////////////////////////////////////////////////

   /**
    * To render the cluster as a node in the JTree.<p>
    * @param model GraphModel
    */

   /// Render the cluster as a Node.
   public void renderNode(GraphModel model)
   {
      // Create the nodes
      theNode = model.createNode(this);

      // Add add it to the graph
      model.addNode(theNode);
   }
/////////////////////////////////////////////////////////////////////////////////////////////////////////

   /**
    * To Render the edges.<p>
    * @param model GraphModel.
    */

   /// Add all the edges from our producers to the graph.
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
////////////////////////////////////////////////////////////////////////////////////////////////////
   /**
    * Returns the node.
    */

   // If not null, the Graph node representing this plugin.
   public Node getNode()
   {
      return theNode;
   }
//////////////////////////////////////////////////////////////////////////////////////////
   /**
    * To Move up to the parent.<p>
    * returns true on moving to the parent and false on failure.<p>
    */
   // Move up to our parent.
   public boolean moveUp()
   {
      getParentNode().renderGraph(MainWindow.theMainWindow.theSociety.theJGraph);
      return true;
   }
/////////////////////////////////////////////////////////////////////////////////////

   /**
    * To get the number of the input slots
    * Returns the number of the inputslots or the subscriptions.
    */

   /// Get the number of input slots (subscriptions) for this record.
   public int getNumInputs()
   {
      return contract.getNumSubscriptions();
   }
//////////////////////////////////////////////////////////////////////////

   /**
    * To get the number of the output slots.<p>
    * Returns the number of the output slots or the number of the publications.<p>
    */
   /// Get the number of output slots (publications) for this record.
   public int getNumOutputs()
   {
      return contract.getNumPublications();
   }
////////////////////////////////////////////////////////////////////////////////////

   /**
    * Specifies if the inputslot or the subscription is connected or not.<p>
    * Returns true on success and false on failure.<p>
    */

   /// Is the specified input slot connected?
   public boolean isInputSlotConnected(int slot)
   {
      return contract.isInputSlotConnected(slot);
   }
///////////////////////////////////////////////////////////////////////////////////////////////

   /**
    * Specifies if the outputslot or the Publications is connected or not.<p>
    * Returns true on success and false on failure.<p>
    */
   /// Is the specified input slot connected?
   public boolean isOutputSlotConnected(int slot)
   {
      return contract.isOutputSlotConnected(slot);
   }
///////////////////////////////////////////////////////////////////////////////////////////

   /**
    * Gets the collection of the plugins present in the cluster.<p>
    * Returns the collection.
    */
   // Return our set of children as a list.
   public Collection getChildren()
   {
      return thePlugins;
   }
///////////////////////////////////////////////////////////////////////////////////////////
   /**
    * Sets the children for the cluster.<p.
    * @param kids Vector.<p>
    */
   // set our children
   public void setChildren(Vector kids)
   {
      thePlugins = kids;
   }
//////////////////////////////////////////////////////////////////////////////////////////
   /**
   *Gets the number of the plugins in the cluster.<p>
   *Returns the number of the children in the vector that holds the plugins in the cluster.<p>
   */

   /// Return the number of children.
   public int getChildCount()
   {
      return thePlugins.size();
   }
////////////////////////////////////////////////////////////////////////////////////////////////
   /**
    * Displays the object name in the JTree.
    * Returns the name of the object.<p>
    */
   /// Return the object's name for display in the jTree
   public String toString()
   {
      return "Cluster: " + name;
   }
/////////////////////////////////////////////////////////////////////////////////////////////////

   /**
    *Gets the long name of the object like "Cluster: Generator"
    *Returns the name.<p>
    */
   // Return our long name.
   public String getLongName()
   {
      return toString();
   }
///////////////////////////////////////////////////////////////////////////////////////////////
   /**
    * Returns a copy of the subtree rooted at this node.<p>
    * @param newParent contractOwnerBase.
    */

   // Return a disjoint copy of the subtree rooted at this node.
   public contractOwnerBase recursiveCopy(contractOwnerBase newParent)
   {
      cluster rtn = new cluster(name, newParent);

      rtn.theGroup = theGroup.deepCopy();

      // Now dup the children.
      Vector kids = new Vector();
      Iterator it = thePlugins.iterator();
      while(it.hasNext())
      {
         contractOwnerBase old = (contractOwnerBase)it.next();
         kids.add( old.recursiveCopy(rtn) );
      }
      rtn.setChildren( kids );

      return rtn;
   }

   ////////////////////////////////Method to save/write the XML to a file///////////////////////////////////////////////////////////////

   /**
    * Writes the cluster's details as XML.<p>
    * @param Pw PrintWriter, debug boolean.<p>
    * Returns true on success and false on failure.<p>
    */
   public boolean writeToXml(PrintWriter pw, boolean debug)
   {

      //this will write the xml forthe cluster
      if (debug )
      {
         //System.out.println( " Writing the xml for the cluster to the file : " + fileName.toString());
         System.out.println( "The name of the cluster is : " + getName());
      }


      //printing the xml structure for the community
       pw.println("  <cluster name=" +"\""+getName()+"\"" + ">" );

      //get an iterator on the clusters plugins
     Iterator it = getChildren().iterator();
      while(it.hasNext())
      {
         //get the children of the cluster that are plugins
         contractOwnerBase element = (contractOwnerBase)it.next();

         //now get the xml for that particular element that extends the contrcat owner base
         //and this element is either a coomunity or the cluster
        element.writeToXml(pw, debug);

      }
       pw.println("  </cluster>");

       return true;

    }

    //////////////Methods to write out the soc and the ini files////////////////////////////////////////////////////
   /**
    * Writes out the soc file.
    * @param pw PrintWriter , debug boolean
    * returns true on success and false on failure.<p>
    */
    public boolean writeOutSoc(PrintWriter pw, boolean debug)
    {
     //get the information for the clusters in the form of cluster = Generator.
     pw.println("cluster = " + getName());
     return true;

    }

    ///////////////////////Method to write out the ini files////////////////////////////////////////////////////
    /**
     * Writes the ini files.<p>
     * @param filename File, debug boolean.<p>
     * Returns true on success and false on failure.<p>
     */

    public boolean writeIniFile(File fileName, boolean debug)
    {
      //write the cluster's ini file

      if(fileName == null)
      {
         System.err.println( "Internal Error: Null file passed to the cluster::writeInifile the file that is comming in is:" + fileName.toString());
         return false;
      }

      //the name of the file to which the cluster is being written to
      String fname = fileName.getParent()+ "/"+ getName() + ".ini";

      boolean retnvalue = true;
      try
      {
         //now try writing the details, open a fileout put stream
         FileOutputStream fo = new FileOutputStream (fname);

         //now open a print writer
         PrintWriter pw = new PrintWriter(fo);
         pw.println("[Cluster]");

         //print out that the first header indicating that all of these are plugins
         pw.println("[PlugIns]");

         //first loop through all the avalable plugins
         Iterator it = thePlugins.iterator();
         while(it.hasNext())
         {

            //get teh plugin object from the iterator
            contractOwnerBase kid = (contractOwnerBase)it.next();
            kid.writeIniFile(pw, debug);
         }

         pw.flush();

         pw.close();

      }//try ends
      catch(Exception e)
      {
         System.out.println(" Exception occured while trying to write the ini file to " + fname + "at cluster::writeInifile" );
         e.printStackTrace();
         retnvalue = false;
      }
      return retnvalue;
    }
} //ends class cluster.
