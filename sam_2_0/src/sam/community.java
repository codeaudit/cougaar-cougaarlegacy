
package sam;

import sam.MainWindow;
import sam.display.samJGraph;
import diva.graph.model.GraphModel;
import diva.graph.model.Node;
import diva.graph.GraphView;
import sam.display.samRenderer;
import sam.LoadIniFiles.iniParser;
import sam.LoadContractFiles.systemContracts;
import sam.LoadContractFiles.contractOwnerBase;
import sam.LoadContractFiles.setOfContractOperators;
import sam.LoadContractFiles.setOfContractLinkPoints;
import sam.LoadContractFiles.contractLinkPoint;
import sam.prettyprint;
import sam.display.samGraphController;
import org.cougaar.core.util.Operator;
import org.cougaar.core.util.OperatorFactory;

import diva.canvas.*;
import diva.graph.*;
import diva.graph.layout.GridAnnealingLayout;
import diva.graph.layout.GlobalLayout;
import diva.graph.layout.LayoutTarget;
import diva.graph.model.Edge;

import java.io.File;
import java.io.*;
import java.util.Vector;
import java.util.Iterator;
import java.util.Collection;
import java.util.Enumeration;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeSelectionModel;

/** Class: Community has the detilas of the Community and also deals with the details <p>
 *  for the clusters in the Community<p>
 *  Title:        Sam<p>
 *  Description:  ALP Business Process User Interface<p>
 * <copyright>
 *    Copyright (c) 2000-2001 Defense Advanced Research Projects
 *    Agency (DARPA) and Mobile Intelligence Corporation.
 *    This software to be used only in accordance with the
 *    COUGAAR license agreement.
 * </copyright>
 *  Company:      Mobile Intelligence Corp.<p>
 *  @author:      Sridevi Salagrama
 *  @version 1.0
 */

public class community extends contractOwnerBase
{
   // Holds the list of cluster records in this Community.
   Vector theChildren;

   //List of graph edges connecting our node to our suppliers
   Vector theEdges;

   // If not null, the Graph node representing this Community.
   Node theNode;

   // Remember the ini file contents.
   //public iniParser.Group theGroup;

   // if not null, the GraphModel for this Community rendered as a graph
   GraphModel ourModel;

   // The JGraph swing component used to display this Community.
   public samJGraph theJGraph;

   // The JGraph's main pane.
   public GraphPane thePane;

   public contractOwnerBase displayedComponent;

   public DefaultTreeModel theTreeDisplay;
   public DefaultTreeModel theErrorDisplay;

   //to keep track of the files that have been changed
   public boolean files_changed;

   //Get the Contract object
   private systemContracts theSystemContracts;

   /// Construct an empty Community
   /**
    * Constructor.
    * @param communityname String, parent contractOwnerBase
    */
   public community(String communityName, contractOwnerBase parent)
   {
      super(parent);
      name = communityName;
      wipe();
   }

   /// Free the existing design and init to an empty configuration
   /**
    * Frres the existing design and initializes to an empty configuration.<p>
    */
   public void wipe()
   {
      // Free the old records so they will get garbage collected.
      if( theChildren != null )
         theChildren.clear();
      else
         theChildren = new Vector();

      theEdges = new Vector(); //creating a call to the new vector

      //now creating a new group
      //iniParser.Group thenewGroup = new iniParser.Group();

      //creating a new Section
      iniParser.Section thenewSection = new iniParser.Section("community");

      // Default to the top.
      displayedComponent = MainWindow.theMainWindow.theWorld;//should this be a society????????

      theTreeDisplay = new DefaultTreeModel(this);

   }


//////////////////////////////////////////////////////////////////////////////////////

   // Set the contract objects.
   /**
   * sets the Contrcat objects
   */
   public void setSystemContract(systemContracts contract)
   {
      theSystemContracts = contract;
   }
//////////////////////////////////////////////////////////////////////////////////////
   /**
    * Gets all the available clusters in the community.<p>
    * Returns the names of all the available clusters in the form of an array of strings.<p>
    */

   public String[] getAvailableclusters()
   {
      return theSystemContracts.getListOfClusters();
   }
///////////////////////////////////////////////////////////////////////////////////////////
   /**
    * Gets the available Plugins.<p>
    * Returns the names of all the available plugins in the form of an array of strings.<p>
    */

   public String[] getAvailablePlugins()
   {
      return theSystemContracts.getListOfPlugins();
   }
//////////////////////////////////////////////////////////////////////////////////////////////
   /**
    * Gets all the available communities.<p>
    * Returns the namesof all the communities in the form of an array of String.<p>
    */

    public String[] getAvailableCommunities()
   {
      return theSystemContracts.getListOfCommunities();
   }
//////////////////////////////////////////////////////////////////////////////////////
   /**
    * Gets the contract object.<p>
    *
    */

   // Get the contract objects.
   public systemContracts getSystemContract()
   {
      return theSystemContracts;
   }
/////////////////////////////////////////////////////////////////////////////////////////
   // Add a new component to the record (i.e., a plugin in a cluster)
   // Returns true on success, false on error or unsupported.
   /**
    * Adds a new component ,a cluster to the record.<p>
    * @pram name String.<p>
    * Returns true on success and false on failure.<p>
    */
   public boolean addComponent(String name)
   {
      // Create a new cluster with the desired name
      cluster newchild = new cluster(name, this);

      return addComponent(newchild);
   }
/////////////////////////////////////////////////////////////////////////

   // Add a new component to the record.
   // Returns true on success, false on error or unsupported.
   /**
    * Adds a new component, a community to the record.<p>
    * @Param name String.<p>
    * returns true on success and false on failure.<p>
    */
   public boolean addHighLevelComponent(String name)
   {
      // Create a new community with the desired name
      community newcommunity = new community(name, this);

      return addComponent(newcommunity);
   }
/////////////////////////////////////////////////////////////////////////


   // Add a new component to the record (i.e., a plugin in a cluster)
   // and sets the different flag.
   // Returns true on success, false on error or unsupported.
   /**
    * Adds a new component to the record.<p>
    * @param name String.<p>
    * Returns true on success and false on failure.<p>
    *
    */
   public boolean addDifferentComponent(String name)
   {
      // Create a new cluster with the desired name
      cluster newchild = new cluster(name, this);

      // Set the different flag
      newchild.setDifferent(true);

      return addComponent(newchild);
   }

//////////////////////////////////////////////////////////////////////////
   /**
    * Adds a different level component , acoomunity.<p>
    * @param name String.<p>
    * Returns True on success and false on failure.<p>
    */

   public boolean addDifferentHighLevelComponent(String name)
   {
      // Create a new cluster with the desired name
      community newcommunity = new community(name, this);

      // Set the different flag
      newcommunity.setDifferent(true);

      return addComponent(newcommunity);
   }
////////////////////////////////////////////////////////////////////////////////////
   /**
    * Adds a new component to the record.<p>
    * @param child contractOwnerBase.<p>
    * Returns true on success and false on failure.<p>
    *
    */
   // Add a new component to the record (i.e., a plugin in a cluster)
   // Returns true on success, false on error or unsupported.
   public boolean addComponent(contractOwnerBase child)
   {
      // Add the new cluster to this community
      theChildren.add(child);

      // Attach the contract to this cluster
      child.attachContracts( MainWindow.theMainWindow.theWorld.getSystemContract(), MainWindow.theMainWindow.properties.getVerbose());

      // Recompute cluster depencencies within this community.
      // First, clear the existing links
      clearMemberDependencies();

      // Then, recompute them
      computeMemberDependencies(MainWindow.theMainWindow.properties.getVerbose());

      return true;
   }

///////////////////////////////////////////////////////////////////////////////////////////

   /// Load the community ini file to get the list of clusters, then
   /// load each of their ini files to get the plugins.
   /// Returns true on success, false on failure.
   /**
    * Loads the clusters information from the ini files.<p>
    * @param file File,debug boolean
    * Returns true on success and false on failure.<p>
    */
   public boolean loadFromIniFiles(File file, boolean debug)
   {
      if( debug )
         System.out.println("\nParsing community root file '" + file.toString() + "'\n");

      // Try to load the root community ini file.
      Vector clusters = new Vector();

      // Create the parser object.
      iniParser ip = new iniParser();
      ip.setVerbose(debug);

      //iniParser.Section theSlots = ip.parse(file,"Clusters");

      iniParser.Group group;
      // iniParser.Section s;
      try
        {
            group = ip.parse(file);
            if( group == null )
            {
               // File not found.
               System.err.println("Unable to parse the file " + file.toString());
               return false;
            }


            // Look through the group and find the "PlugIns" section.
            Vector sections = group.getSections();
            iniParser.Section clustersection = null;
            Iterator its = sections.iterator();

            while(its.hasNext())
            {
               iniParser.Section thissection = (iniParser.Section)its.next();

               if( debug )
                     System.out.println("Checking Section " + thissection.getName());

               if(thissection.getName().toLowerCase().equals("clusters"))
               {
                  // Found it.
                  if( debug )
                      System.out.println("Found the Clusters section");

                  clustersection = thissection;

                     break;
               }//if ends

            }//ends while loop

         // Did we find it?
         if( clustersection == null )
         {
            System.err.println("Didn't find a \"Cluster\" section in " + file.toString() );
            return false;
         }

         Vector slots = clustersection.getSlots();
         Iterator it = slots.iterator();
         while( it.hasNext() )
         {
            iniParser.Slot thisSlot = (iniParser.Slot)it.next();

            // Only take the "cluster = " lines.
            if( thisSlot.getName().toLowerCase().equals("cluster") )
            {
               clusters.addElement( new cluster(thisSlot.getValue(), this) );

               if( debug )
                  System.out.println("Cluster: " + thisSlot.getValue());
            }
         } //while
      }
      catch (Exception ex )
      {
         ex.printStackTrace();
         return false;
      }

      // Success, so commit.
      theChildren = clusters;

     // Now load each of the cluster objects from its ini file.
      Iterator it = theChildren.iterator();

      while( it.hasNext() )
      {
         // Get the cluster record.
         cluster thisCluster = (cluster)it.next();

         // Build a file name for its ini file.
         File clusterFile = new File(file.getParent(), thisCluster.getName() + ".ini");

         if( !thisCluster.loadFromIniFiles(clusterFile, debug) )
         {
            System.out.println("\nError loading " + thisCluster.getName() + " from '" + clusterFile.toString() + "'\n");
         }
      }

      // Create the input and output bars and add them as fake clusters.
      // Give them our name, since they want our contracts.
      inputBar theInputBar = new inputBar(name, this);
      theChildren.add(theInputBar);
      outputBar theOutputBar = new outputBar(name, this);
      theChildren.add(theOutputBar);

      // Notify that we have changed the model.
      theTreeDisplay.reload();


      return true;
   }

/////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
   /**
    * Writing the community to a file.<p>
    * @param file File, debugflag boolean.<p>
    * Returns true on success and false on failure.<p>
    */
  /* public boolean savetofile(File file, boolean debugflag)
   {
      //trying to save the contents of the cluster ro the file
      // Write the Community.soc file

      if( file == null )
      {
         System.err.println("Internal Error: Null file passed to Community::savetofile");
         return false;
      }

      String fname = file.getParent()+ "/" + getName() + ".soc";
      //the name of the file to which the Community is being saved

      boolean retnvalue = true;

      try
      {
         iniParser p = new iniParser();
          p.parsegenerator(file, theGroup);

      }//ends try
      catch(Exception e)
      {
         e.printStackTrace();
         retnvalue = false;
      }


      // Let each cluster write their .ini file.
     if(theChildren !=null)
     {
       Iterator its = theChildren.iterator();
      while(its.hasNext())
       {
         contractOwnerBase thisCluster = (contractOwnerBase)its.next();

         if( debugflag )
            System.out.println("writing " + thisCluster);

         thisCluster.savetofile(file, debugflag);
        }
      }
      return retnvalue;

   }//ends the method for the*/
//////////////////////////////////////////////////////////////////////////////////////////////////
   /**
    * Attaches contracts to the community.<p>
    * @param theSystemContracts, debug boolean.<p>
    */


   public void attachContracts(systemContracts theSystemContracts, final boolean debug)
   {
      // Attach the contracts
      setOfContractOperators contractOperators = theSystemContracts.getCommunityContract(name);

      // Shouldn't happen.
      if( contractOperators == null )
      {
         System.out.println("Warning: Community " + name + " does not have a contract.");
         contract = new setOfContractLinkPoints();
      }
      else
      {
         contract = new setOfContractLinkPoints(contractOperators, this);

         // Warn of missing contract.
         if( debug && contractOperators.publish.isEmpty() && contractOperators.subscribe.isEmpty() )
         {
            System.out.println("Warning: Community " + name + " does not have a contract.");
         }
      }

      if( debug )
      {
         System.err.println("Community " + name + ": Loaded " +
               getNumInputs() + " subscriptions, " + getNumOutputs() + " publications");
      }


      // Allow each of the clusters to attach their contracts.
      Iterator it = theChildren.iterator();
      while( it.hasNext() )
      {
         // Get the cluster record.
         contractOwnerBase thisCluster = (contractOwnerBase)it.next();

         // Attach the contracts
         thisCluster.attachContracts(theSystemContracts, debug);
      }

      // Notify that we have changed the model.
      theTreeDisplay.reload();

   }
/////////////////////////////////////////////////////////////////////////////////////////////
   /**
    * Computes the dependencies based on the contracts.<p>
    * @param theChildren collection, debug boolean.<p>
    */
   /// Compute our dependencies based on our contracts.
   public void computeDependencies(Collection theChildren, final boolean debug)
   {
      if( debug )
      {
         System.out.println("\nComputing dependencies for Community " + getName());
      }

      // Skip system Community.
      if( contract.systemObject )
         return;

      if( debug )
      {
         System.out.println("  checking Community " + getName() + "'s " + contract.subscribe.size() + " subscribe contracts");
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
               System.err.println("Warning: Community " + name + " has an empty subscribe contract!");

            continue;
         }

         // Now step through all the other communities, so we can see who provides the object.
         Iterator childrenIT = theChildren.iterator();
         while(childrenIT.hasNext())
         {
            contractOwnerBase children = (contractOwnerBase)childrenIT.next();

            // Skip system communities.
            if( children.contract.systemObject )
               continue;

            // Skip the case where we are checking a Community against itself.
            // NOTE: ASSUMES A COMMUNITY CAN NOT FULFILL ITS OWN CONTRACTS
            if( children == this )
               continue;

            // Step through this cluster's publish contracts
            int outputSlot = 0;
            Iterator it_publish = children.contract.publish.iterator();
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
                     System.err.println("Warning: Community " + children.getName() + " has an empty publish contract!");

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
                     System.out.println("Community " + getName() + "'s subscription contract:\n   " +
                                        subscribeContract.toString() + "\nis fullfilled by cluster " +
                                        children.getName() + "'s publish contract\n   " + publishContract.toString() + "\n");
                     System.out.println("Making link from " + children.getName() + " to " + getName() +
                                        "     outputSlot=" + outputSlot + ",  inputSlot="  + inputSlot);
                  }
               }

               outputSlot ++;
            }
         }

         inputSlot ++;
      }

      // Now let each of our communities compute their dependencies.
      computeMemberDependencies(debug);
   }
///////////////////////////////////////////////////////////////////////////////////////////////////////////
   /**
    * Compute Member dependencies.<p>
    *
    */
   // Tell each child to compute its dependencies.
   // (appends them to its list, so need to clear them first)
   public void computeMemberDependencies(final boolean debug)
   {
      Iterator it = theChildren.iterator();
      it = theChildren.iterator();

      while(it.hasNext())
      {
         // Get the next record.
         contractOwnerBase subplug = (contractOwnerBase)it.next();

         // Let it compute its dependencies.
         subplug.computeDependencies(theChildren, debug);
      }
   }

   ////////////////////////////////////////////////////////////////////////////////////
   ////////////////////////////Deleting the selected component////////////////////////////////
   /**
    * Deletes the selected object.<p>
    * @param ourparent contractOwnerBase.<p>
    * Returns the contractOwnerBase.<p>
    */

   public contractOwnerBase cutComponent(contractOwnerBase ourParent)
   {
     //trying to remove the selected object from the Cluster vector
     theChildren.remove(ourParent);

      // Notify that we have changed the model.
      theTreeDisplay.reload();


     return ourParent;
   }
   ////////////////////////////////////////////////////////////////////////////////////////////////
   /**
    * Tells each child to clear its dependencies.<p>
    *
    */

   // Tell each child to clear its dependencies.
   public void clearMemberDependencies()
   {
      // Tell each of our clusters to clear their dependencies.
      Iterator it = theChildren.iterator();
      while(it.hasNext())
      {
         // Get the next record.
         contractOwnerBase theChild = (contractOwnerBase)it.next();

         // Let it clear its dependencies.
         theChild.clearMemberDependencies();
         theChild.clearLinks();

         //System.out.println("The name of the cluster for which the Dependencies are being cleared is"+ theChild.getName());

      }

   }
/////////////////////////////////////////////////////////////////////////////////////////////////////////////////
   /**
    * To analyze the problems associated with the unresolved dependencies.<p>
    * @param ourRec ResultsNode, warningLevel int.<p>
    * Returns true on success and false on failure.<p>
    */

   /// Report any unresolved dependencies in human readable form.
   public boolean analyze(ResultsNode ourRec, int warningLevel)
   {
      boolean hadMsg = false;

      // quietly ignore any system objects.
      if( !contract.systemObject )
      {
         // Community contracts are always unconnected outside the Community.
         // The clusters will check if they are used inside the Community.
/*
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
*/
         // Let each child analyze itself.
         Iterator it = theChildren.iterator();
         while( it.hasNext() )
         {
            // Get the cluster record.
            contractOwnerBase cb = (contractOwnerBase)it.next();

            // Analyze the cluster.
            // Note: We only add the cluster record if there is detail below it.
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
//////////////////////////////////////////////////////////////////////////////////////////////////
   /**
    * Prints the dependencies of our plugins based on the contracts.<p>
    *
    */

   /// Print the dependencies of our plugins based on their contracts.
   public void printDependencies()
   {
      System.out.println("Community " + getName());

      // Print the cluster to cluster dependencies, based on their contracts.
              // Not implemented yet

      // Now let each child print the dependencies of its components.
      Iterator it = theChildren.iterator();
      while( it.hasNext() )
      {
         // Get the cluster record.
         contractOwnerBase thisChild = (contractOwnerBase)it.next();

         // Print the dependencies
         thisChild.printDependencies();
      }

   }
/////////////////////////////////////////////////////////////////////////////////////////
   /**
    * Returns the Node  representing the community.<p>
    */

   // If not null, the Graph node representing this Community.
   public Node getNode()
   {
      return theNode;
   }
//////////////////////////////////////////////////////////////////////////////////////
   /**
    * Gets the Cluster with the specified name.<p>
    * @param childname String
    * Returns the contractOwnerBase record with that name.<p>
    */
   /// Get the cluster record with the specified name.
   public contractOwnerBase getChildByName(final String childName)
   {
      // Find the named cluster.
      contractOwnerBase cl = null;
      Iterator it = theChildren.iterator();
      while( it.hasNext() )
      {
         // Get the cluster record.
         contractOwnerBase thisChild = (contractOwnerBase)it.next();


         // Is this it?
         if( thisChild.getName().equalsIgnoreCase(childName) )
         {
            cl = thisChild;
            break;
         }
      }

      return cl;
   }
   ////////////////////////////////////////////////////////////////////////////////////////////////
   /**
    * Renders the Community as a node.<p>
    * @param model GraphModel.<p>
    */

   /// Render the Community as a Node.
   public void renderNode(GraphModel model)
   {
      // Create the nodes
      theNode = model.createNode(this);

      // Add it to the graph
      model.addNode(theNode);
   }
   //////////////////////////////////////////////////////////////////////////////////////////////////
   /**
    * Renders the edges.<p>
    * @param model GraphModel.<p>
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
////////////////////////////////////////////////////////////////////////////////////////////////
   /**
    * Returns true if the community has a graph else returns false.<p>
    */

   /// Yes, we can display the Community as a graph.
   public boolean hasGraph()
   {
      return true;
   }
   ///////////////////////////////////////////////////////////////////////////////////////////
   /**
    * Renders the clusters or the communities in the community as a graph.<p>
    * @param theCallersGraph samJGraph.<p>
    */

   /// Render the clusters or communitites in this Community as a graph.
   public void renderGraph(samJGraph theCallersGraph)
   {
      // Make us a new graph model.
      ourModel = new GraphModel();

      // This is a two step process.
      // First, create a node for each child.
      Iterator it = theChildren.iterator();
      while(it.hasNext())
      {
         contractOwnerBase thisChild = (contractOwnerBase)it.next();

         thisChild.renderNode(ourModel);

      }

      // Finally, create the edges.
      it = theChildren.iterator();
      while(it.hasNext())
      {
         contractOwnerBase thisChild = (contractOwnerBase)it.next();

         thisChild.renderInputEdges(ourModel);
      }

      // Tell the world that it is us.
       MainWindow.theMainWindow.theSociety.displayedComponent = this;

      // Attach our model to the graph display
      theCallersGraph.setGraphModel(ourModel);

      // Display it.
      MainWindow.theMainWindow.layoutJGraph();


      //setting the lable name to the currently selected field
      String Long_name = MainWindow.theMainWindow.theSociety.displayedComponent.getLongName();
      MainWindow.theMainWindow.fieldname.setText(Long_name);
      samGraphController gc = (samGraphController)MainWindow.theMainWindow.theSociety.thePane.getGraphController();
      gc.upDatesite();

      //trying to set the buttons to grey out
      MainWindow.theMainWindow.update_BackandForward();

   }
   /////////////////////////////////////////////////////////////////////////////////////////////
   /**
    * Returns true on displaying the parent as graph and false on failure.<p>
    */

   // Move up to our parent.
   public boolean moveUp()
   {
      getParentNode().renderGraph(MainWindow.theMainWindow.theSociety.theJGraph);
      return true;
   }
//////////////////////////////////////////////////////////////////////////////////////////////
   /**
    * Gets the number of input slots or subscriptions.
    * Returns the number of the inputslots.<p>
    */
   /// Get the number of input slots (subscriptions) for this record.
   public int getNumInputs()
   {
      return contract.getNumSubscriptions();
   }
   /////////////////////////////////////////////////////////////////////////////////////////
   /**
    * Gets the number of the output slots.<p>
    * Returns the number of the output slots or publications.<p>
    */

   /// Get the number of output slots (publications) for this record.
   public int getNumOutputs()
   {
      return contract.getNumPublications();
   }
/////////////////////////////////////////////////////////////////////////////////////////
   /**
    * Returns true if the in put slot is connected and false if it is not.<p>
    * @param slot integer.<p>
    */
   /// Is the specified input slot connected?
   public boolean isInputSlotConnected(int slot)
   {
      // We aren't doing any external objects at the world level,
      // so just assume they are all OK for now.
//    return contract.isInputSlotConnected(slot);

      return true;
   }
////////////////////////////////////////////////////////////////////////////////////////////////
   /**
    * Returns true if the output slot is connected.<p>
    * @param slot integer.<p>
    */
   /// Is the specified input slot connected?
   public boolean isOutputSlotConnected(int slot)
   {
      // We aren't doing any external objects at the world level,
      // so just assume they are all OK for now.
//    return contract.isOutputSlotConnected(slot);

      return true;
   }
//////////////////////////////////////////////////////////////////////////////////////////
   /**
    * Returns the collection of the children in the community.<P>
    */

   // Return our set of children as a list.
   public Collection getChildren()
   {
      return theChildren;
   }
//////////////////////////////////////////////////////////////////////////////////////////////
   /**
    * Sets the children in the community.<p>
    * @param kids Vector.<p>
    */

   // set our children
   public void setChildren(Vector kids)
   {
      theChildren = kids;
   }
/////////////////////////////////////////////////////////////////////////////////////////////////////
   /**
    * Returns the number of children present in the community.<p>
    *
    */

   /// Return the number of children.
   public int getChildCount()
   {
      return theChildren.size();
   }
////////////////////////////////////////////////////////////////////////////
   /**
    * Returns the object's name as a String for the display in the JTree.<p>
    */
   /// Return the object's name for display in the jTree
   public String toString()
   {
      return "Community: " + name;
   }
////////////////////////////////////////////////////////////////////////////////////
   /**
    * Returns the long name of the community. for eg., "Community: General"
    */

   // Return our long name.
   public String getLongName()
   {
      return toString();
   }
//////////////////////////////////////////////////////////////////////////////////////////
   /**
    * Returns the contractOwnerBase object (a Community) of the subtree rooted at this node.<p>
    * @param newParent contractOwnerBase.<p>
    */
   // Return a disjoint copy of the subtree rooted at this node.
   public contractOwnerBase recursiveCopy(contractOwnerBase newParent)
   {
      community rtn = new community(name, newParent);

      //rtn.theGroup = theGroup.deepCopy();

      // Dup our contract.
      rtn.setSystemContract(getSystemContract() );

      // Now dup the children.
      Vector kids = new Vector();
      Iterator it = theChildren.iterator();
      while(it.hasNext())
      {
         contractOwnerBase old = (contractOwnerBase)it.next();
         kids.add( old.recursiveCopy(rtn) );
      }
      rtn.setChildren( kids );

      return rtn;
   }

////////////////////////////////Method to save/write the xml to a file///////////////////////////////////////////////////////////////
   /**
    * Writes the details of the community in the form of XML.<p>
    * @param pw PrintWriter, debug boolean
    * Returns true on success and false on failure.<p>
    */

   public boolean writeToXml(PrintWriter pw, boolean debug)
   {

      //this will write the xml forthe community
      if (debug )
      {
         //System.out.println( " Writing the xml for the community to the file : " + fileName.toString());
         System.out.println( "The name of the Community is : " + getName());
      }

      //printing the xml structure for the community
      pw.println("  <community name="+"\"" + getName() + "\"" + ">" );

      //get an iterator on the communities children
      Iterator it = getChildren().iterator();
      while(it.hasNext())
      {
         //get the children of the community that can be another community or the cluster
         contractOwnerBase element = (contractOwnerBase)it.next();

         //now get the xml for that particular element that extends the contrcat owner base
         //and this element is either a coomunity or the cluster
         element.writeToXml(pw, debug);
      }
       pw.println("  </community>");

       return true;

   }

   /////////////////////////////////////////Method to write the details for the soc file/////////////////////////////////////////////

   /**
    * Writes the details of the cluster in the community to the soc file.<p>
    * @param pw PrintWriter, debug boolean
    * Returns true on success and false on failure.<P>
    */
   public boolean writeOutSoc(PrintWriter pw, boolean debug)
   {
      if(debug)
      System.out.println( " Now writing the cluster details in the society present in the community" );

      //looping through the children in the community, so that details about the clusters
      //can be written to the soc file
      Iterator it = theChildren.iterator();
      while(it.hasNext())
      {
         //get the object that can be a coomunity or a cluster by it self
         contractOwnerBase element = (contractOwnerBase)it.next();

         //now get the details
         element.writeOutSoc(pw,debug);

      }
      return true;
   }

   ///////////////Method to write the details to the ini files///////////////////////////////////////////////////////////////////////

   /**
    * Writes the  details of the cluster in the community to the ini files.<p>
    * @param fileName File, debug boolean
    * Returns true on success and false on failure.<P>
    */
   public boolean writeIniFile(File fileName, boolean debug)
   {
      //now loop through the children to get the details to write to the ini files
      Iterator it = theChildren.iterator();
      while(it.hasNext())
      {
         //now get the object that can be a community oe the cluster
         contractOwnerBase element = (contractOwnerBase)it.next();

         //now try to get the details to write to the ini files
         element.writeIniFile(fileName,debug);
      }
      return true;
   }

} //ends community
