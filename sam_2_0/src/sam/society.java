
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

import diva.canvas.*;
import diva.graph.*;
import diva.graph.layout.GridAnnealingLayout;
import diva.graph.layout.GlobalLayout;
import diva.graph.layout.LayoutTarget;

import java.io.File;
import java.util.Vector;
import java.util.Iterator;
import java.util.Collection;
import java.util.Enumeration;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeSelectionModel;

//for the xml xml stuff

import org.cougaar.core.util.Operator;
import org.cougaar.core.util.OperatorFactory;
import org.xml.sax.*;
import org.xml.sax.helpers.*;
import org.apache.xerces.parsers.SAXParser;
import org.xml.sax.InputSource;
import sam.LoadContractFiles.*;
import java.util.*;
import java.io.*;
import java.net.URL;
import org.w3c.dom.*;




/** Class: society has the detilas of the society and also deals with the details <p>
 *  for the clusters and the communities in the society<p>
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

public class society extends contractOwnerBase
{
   // Holds the list of cluster records in this society.
   //Vector theClusters;

   //holds the list of the children of the society,
   //as the society might have the cluster or the community as its children
   Vector theChildren;

   // If not null, the Graph node representing this society.
   Node theNode;

   // Remember the ini file contents.
  // public iniParser.Group theGroup;

   // if not null, the GraphModel for this society rendered as a graph
   GraphModel ourModel;

   // The JGraph swing component used to display this society.
   public samJGraph theJGraph;

   // The JGraph's main pane.
   public GraphPane thePane;

   public contractOwnerBase displayedComponent;

   public DefaultTreeModel theTreeDisplay;
   public DefaultTreeModel theErrorDisplay;

   public String  tabbedpane_filename;

   //to keep track of the files that have been changed
   public boolean files_changed;

   //Get the Contract object
   public systemContracts theSystemContracts = new systemContracts();

   /// Construct an empty society
   public society(String societyName, contractOwnerBase parent)
   {
      super(parent);

      name = societyName;
      wipe();
   }

   /// Free the existing design and init to an empty configuration
   public void wipe()
   {
      // Free the old records so they will get garbage collected.
      /*if( theClusters != null )
         theClusters.clear();
      else
         theClusters = new Vector();*/

      if( theChildren != null )
         theChildren.clear();
      else
         theChildren = new Vector();


      //now creating a new group
      //iniParser.Group thenewGroup = new iniParser.Group();

      //creating a new Section
      iniParser.Section thenewSection = new iniParser.Section("cluster");

      //adding the section to the group
      //thenewGroup.addSection(thenewSection);

      //adding the cluster to the new section
      thenewSection.addParameter("cluster");

      // Default to the top.
      displayedComponent = MainWindow.theMainWindow.theWorld;

      theTreeDisplay = new DefaultTreeModel(this);



   }


//////////////////////////////////////////////////////////////////////////////////////

 // Set the contract objects.
   public void setSystemContract(systemContracts contract)
   {
      theSystemContracts = contract;
   }

   //will get you the list of the available clusters
   public String[] getAvailableclusters()
   {
      return theSystemContracts.getListOfClusters();
   }

   //will get you the list of the available communities

   public String[] getAvailablecommunities()
   {
      return theSystemContracts.getListOfCommunities();
   }

   public String[] getAvailablePlugins()
   {
      return theSystemContracts.getListOfPlugins();
   }

    public String[] getAvailableCommunities()
   {
      return theSystemContracts.getListOfCommunities();
   }

   // Get the contract objects.
   public systemContracts getSystemContract()
   {
      return theSystemContracts;
   }
/////////////////////////////////////////////////////////////////////////////////////////
   // Add a new component to the record (i.e., a plugin in a cluster)
   // Returns true on success, false on error or unsupported.
   public boolean addComponent(String name)
   {
      // Create a new cluster with the desired name
      cluster newcluster = new cluster(name, this);

      return addComponent(newcluster);
   }

   /**
    * Adds a higher level component like the community to the society.<p>
    * @param name String value name of the community.<p>
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
   public boolean addDifferentComponent(String name)
   {
      // Create a new cluster with the desired name
      cluster newcluster = new cluster(name, this);

      // Set the different flag
      newcluster.setDifferent(true);

      return addComponent(newcluster);
   }

   // Add a new component to the record (i.e., a plugin in a cluster)
   // Returns true on success, false on error or unsupported.
   public boolean addComponent(contractOwnerBase child)
   {
      // Add the new cluster to this society
      //theClusters.add(child);

      //add new child to the society
      theChildren.add(child);


      // Attach the contracts to this cluster
      child.attachContracts( MainWindow.theMainWindow.theWorld.getSystemContract(), MainWindow.theMainWindow.properties.getVerbose());

      // Recompute cluster depencencies within this society.
      // First, clear the existing links
      clearMemberDependencies();

      // Then, recompute them
      computeMemberDependencies(MainWindow.theMainWindow.properties.getVerbose());

      return true;
   }

///////////////////////////////////////////////////////////////////////////////////////////

   /// Load the society ini file to get the list of clusters, then
   /// load each of their ini files to get the plugins.
   /// Returns true on success, false on failure.
   public boolean loadFromIniFiles(File file, boolean debug)
   {
      if( debug )
         System.out.println("\nParsing society root file '" + file.toString() + "'\n");

      // Try to load the root society ini file.
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

      // Remember the file name.
      tabbedpane_filename = file.toString();

      // Attempt to fix loading problem where render the graph before
      // we set the "theSociety" reference.
      MainWindow.theMainWindow.theSociety = this;

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

/*   we do not use this any more as we use the write to soc  method istead of this.
   public boolean savetofile(File file, boolean debugflag)
   {
      //trying to save the contents of the cluster to the file
      // Write the society.soc file

      if( file == null )
      {
         System.err.println("Internal Error: Null file passed to society::savetofile");
         return false;
      }

      String fname = file.getParent()+ "/" + getName() + ".soc";
      //the name of the file to which the society is being saved

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

   }//ends the method for the */



   public void attachContracts(systemContracts theSystemContracts, final boolean debug)
   {
      // Attach the contracts
      setOfContractOperators contractOperators = theSystemContracts.getSocietyContract(name);

      // Shouldn't happen.
      if( contractOperators == null )
      {
         System.out.println("Warning: Society " + name + " does not have a contract.");

         contract = new setOfContractLinkPoints();
      }
      else
      {
         contract = new setOfContractLinkPoints(contractOperators, this);

         // Warn of missing contract.
         if( contractOperators.publish.isEmpty() && contractOperators.subscribe.isEmpty() )
         {
             System.out.println("Warning: Society " + name + " does not have a contract.");
         }
      }

      if( debug )
      {
         System.err.println("Society " + name + ": Loaded " +
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

   // Walk the society DAG and compute the dependencies based on the contracts
   public void computeDependencies(Collection theSocieties, final boolean debug)
   {
      if( debug )
      {
         System.out.println("\nComputing dependencies for society " + getName());
      }

      // The society doesn't have dependencies on others.

      computeMemberDependencies(debug);

      // Notify that we have changed the model.
      theTreeDisplay.reload();


   }

   ////////////////////////////////////////////////////////////////////

   // Tell each cluster to compute its dependencies.
   // (appends them to its list, so need to clear them first)
   public void computeMemberDependencies(final boolean debug)
   {
      Iterator it = theChildren.iterator();
      while( it.hasNext() )
      {
         // Get the society record.
         contractOwnerBase thisCluster = (contractOwnerBase)it.next();

         // Attach the contracts
         thisCluster.computeDependencies(theChildren, debug);
      }
   }//ends method compute dependencies
   ////////////////////////////////////////////////////////////////////////////////////
   ////////////////////////////Deleting the selected component////////////////////////////////

   public contractOwnerBase cutComponent(contractOwnerBase ourParent)
   {
     //trying to remove the seleected object from the Cluster vector
     theChildren.remove(ourParent);

      // Notify that we have changed the model.
      theTreeDisplay.reload();

      return ourParent;
   }
   ////////////////////////////////////////////////////////////////////////////////////////////////

   // Tell each cluster to clear its dependencies.
   public void clearMemberDependencies()
   {
      // Tell each of our clusters to clear their dependencies.
      Iterator it = theChildren.iterator();
      while(it.hasNext())
      {
         // Get the next record.
         contractOwnerBase theCluster = (contractOwnerBase)it.next();

         // Let it clear its dependencies.
         theCluster.clearMemberDependencies();
         theCluster.clearLinks();

         //System.out.println("The name of the cluster for which the Dependencies are being cleared is"+ theCluster.getName());

      }

   }


   /// Report any unresolved dependencies in human readable form.
   public boolean analyze(ResultsNode ourRec, int warningLevel)
   {
      boolean hadMsg = false;

      // quietly ignore any system objects.
      if( !contract.systemObject )
      {
         // Society contracts are always unconnected outside the society.
         // The clusters will check if they are used inside the society.
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
         // Let each cluster analyze itself.
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

   /// Print the dependencies of our plugins based on their contracts.
   public void printDependencies()
   {
      System.out.println("Society " + getName());

      // Print the cluster to cluster dependencies, based on their contracts.
              // Not implemented yet

      // Now let each cluster print the dependencies of its plugins.
      Iterator it = theChildren.iterator();
      while( it.hasNext() )
      {
         // Get the cluster record.
         contractOwnerBase thisCluster = (contractOwnerBase)it.next();

         // Print the dependencies
         thisCluster.printDependencies();
      }

   }


   // If not null, the Graph node representing this society.
   public Node getNode()
   {
      return theNode;
   }

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

   /// Render the society as a Node.
   public void renderNode(GraphModel model)
   {
      // Create the nodes
      theNode = model.createNode(this);

      // Add add it to the graph
      model.addNode(theNode);


   }

   public void renderInputEdges(GraphModel model)
   {
      // societies are not interdependent.
   }


   /// Yes, we can display the society as a graph.
   public boolean hasGraph()
   {
      return true;
   }

   /// Render the children in this society as a graph.
   public void renderGraph(samJGraph theCallersGraph)
   {
      // Make us a new graph model.
      ourModel = new GraphModel();

      // This is a two step process.
      // First, create a node for each cluster.
      Iterator it = theChildren.iterator();
      while(it.hasNext())
      {
         contractOwnerBase thisCluster = (contractOwnerBase)it.next();

         thisCluster.renderNode(ourModel);

      }

      // Finally, create the edges.
      it = theChildren.iterator();
      while(it.hasNext())
      {
         contractOwnerBase thisCluster = (contractOwnerBase)it.next();

         thisCluster.renderInputEdges(ourModel);
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
      // We aren't doing any external objects at the world level,
      // so just assume they are all OK for now.
//    return contract.isInputSlotConnected(slot);

      return true;
   }

   /// Is the specified input slot connected?
   public boolean isOutputSlotConnected(int slot)
   {
      // We aren't doing any external objects at the world level,
      // so just assume they are all OK for now.
//    return contract.isOutputSlotConnected(slot);

      return true;
   }

   // Return our set of children as a list.
   public Collection getChildren()
   {
      return theChildren;
   }

   // set our children
   public void setChildren(Vector kids)
   {
      theChildren = kids;
   }

   /// Return the number of children.
   public int getChildCount()
   {
      return theChildren.size();
   }

   /// Return the object's name for display in the jTree
   public String toString()
   {
      return "Society: " + name;
   }

   // Return our long name.
   public String getLongName()
   {
      return toString();
   }

   // Return a disjoint copy of the subtree rooted at this node.
   public contractOwnerBase recursiveCopy(contractOwnerBase newParent)
   {
      society rtn = new society(name, newParent);

     // rtn.theGroup = theGroup.deepCopy();

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

///////////////////////////Method to write the society back as an xml file/ to save it as xml file.////////////////////////////

   public boolean societyXml (File fileName, boolean debug )
   {
      //to write /save the xml of the society
      if (debug)
      {
        System.out.println( " Writing the xml for the society to the file : " + fileName.toString());
        System.out.println( "The name of the Society is : " + getName());
      }

      try
      {
         //trying to open the file to write the xml file
        FileOutputStream fo = new FileOutputStream(fileName);
        PrintWriter pw = new PrintWriter(fo);

         //calling the method to write the xml
         writeToXml(pw,debug);

      }
      catch( Exception e )
      {
         //if unsuccessfull display the exception.
         System.out.println(" Warning ::Exception occured while trying to write to the file : " + fileName.toString() + " in the society's write to xml file"+ e.toString());

      }
      return true;
   }

   public boolean writeToXml(PrintWriter pw, boolean debug)
   {
      //printing out the xml structure fot the society
      pw.println("<society name=" +"\"" + getName() + "\"" + ">");

      //get an iterator on the societies children
      Iterator it = getChildren().iterator();
      while(it.hasNext())
      {
        contractOwnerBase element = (contractOwnerBase)it.next();
        element.writeToXml(pw , debug);
      }
      pw.println("</society>");

      //write them to their intended destination
      pw.flush();

      //close the printwriter
      pw.close();
      return true;
   }
////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
/////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

   //Method to save/ write our the soc files, this method will gennerate the .soc file with out using the Group stuff////////////
   public boolean writeSocFile ( File filename, boolean debug)
   {
      // Write the society.soc file

      if( filename == null )
      {
         System.err.println("Internal Error: Null file passed to society::writeSocFile");
         return false;
      }

       //the name of the file to which the society is being saved
      String fname = filename.getParent()+ "/" + getName() + ".soc";

      boolean retnvalue = true;

      try
      {
         //now open the file out put stream to write to the file
         FileOutputStream fo = new FileOutputStream(filename);

         //now the print writer
         PrintWriter pw = new PrintWriter(fo);

         //print out the first line that will indicate that these are Children in the society.
         pw.println("[Clusters]");

         //now print out each clusters key and value,it should be some thing like this cluster = Generator, etc.,
         //to do this you need to loop through the children of the society,and as this file should
         //only contain the info for the clusters and not the communities

         //getting an iterator on the children of the society
         Iterator childrenIt = theChildren.iterator();
         while (childrenIt.hasNext())
         {
            //get the children in the society, which can be a cluster or a community.
            contractOwnerBase element = (contractOwnerBase)childrenIt.next();

            //call the method for writing the details for the soc file.
            element.writeOutSoc(pw, debug);

            //call the method for writing the details about the ini files
            element.writeIniFile(filename, debug);

         }

         pw.flush();
         pw.close();
      }//ends try
      catch(Exception e)
      {
         System.out.println( "Error occurred during writing the soc file  to the file :" + fname + "at society::write socFile");
         retnvalue = false;
      }

      return retnvalue;
   } //ends the method

}//ends class society
