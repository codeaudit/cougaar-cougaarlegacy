

package sam;

import sam.LoadContractFiles.contractOwnerBase;
import sam.LoadContractFiles.systemContracts;
import sam.display.samJGraph;
import diva.graph.GraphView;
import diva.graph.model.GraphModel;
import diva.graph.model.Node;
import sam.display.samRenderer;
import sam.MainWindow;
import java.util.Iterator;
import java.util.Collection;
import java.util.Enumeration;
import java.util.TreeMap;
import java.util.Vector;
import java.io.File;
import sam.display.samGraphController;
import javax.swing.tree.DefaultTreeModel;
import sam.societyXmlLoader;
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

public class world extends contractOwnerBase
{
   // Holds the list of society records in this world (usually just one).
   //private Vector theSocieties;
   public Vector theSocieties;

   //The name of the tab in the main window
   public  String tab_name;

   public boolean isNewSociety;

   public societyXmlLoader thesocietyxmlloader = new societyXmlLoader();

         /// Construct an empty world
   public world(String worldName)
   {
      super(null);
      name = worldName;
      wipe();
   }

   /// Free the existing design and init to an empty configuration
   public void wipe()
   {
      // Free the old records so they will get garbage collected.
      if( theSocieties != null )
         theSocieties.clear();
      else
         theSocieties = new Vector();
   }


   /** If a society with the same name exists, then replace it,
    *  otherwise add this new society.
    *  NOTE: Does not rerender the world.
    *  Returns the tab number for the new society.
    */
   public int attachSociety(society newSociety, final String label)
   {
      theSocieties.add(newSociety);
      tab_name = label;

      //To make sure that when another society is being loaded
      //we can replace the default tabbedpane with that.
      if( tab_name != "default")
      {
         isNewSociety= true;
      }

      // Add a new tab for this society to the display window.
      return MainWindow.theMainWindow.addtab(newSociety, label);

   }

   /** Attaches the specified contract to the socieies.
    */

   public void attachContracts(systemContracts theSystemContracts, final boolean debug)
   {
      // The world does not have a contract.
      // Allow each of the societies to attach their contracts.
      Iterator it = theSocieties.iterator();
      while(it.hasNext()  )
      {
         // Get the society record.
         society thisSociety = (society)it.next();

         // Attach the contracts
         thisSociety.attachContracts(theSystemContracts, debug);
      }
   }

   // Walk the society DAG and compute the dependencies based on the contracts
   public void computeDependencies(Collection not_used, final boolean debug)
   {
      // The world does not have a contract.

      // Let each society compute the dependencies of its plugins.
      // Allow each of the societies to attach their contracts.
      Iterator it = theSocieties.iterator();
      while( it.hasNext() )
      {
         // Get the society record.
         society thisSociety = (society)it.next();

         // Attach the contracts
         thisSociety.computeDependencies(theSocieties, debug);
      }

   }

   // Tell each plugin to compute its dependencies.
   // (appends them to its list, so need to clear them first)
   public void computeMemberDependencies(final boolean debug)
   {
      // We don't have members.
   }

   // Tell each society to clear its dependencies.
   public void clearMemberDependencies()
   {
      // Tell each of our societies to clear their dependencies.
      Iterator it = theSocieties.iterator();
      while( it.hasNext() )
      {
         // Get the society record.
         society thisSociety = (society)it.next();

         // Let it clear its dependencies.
         thisSociety.clearMemberDependencies();
         thisSociety.clearLinks();
      }
   }

   /// Report any unresolved dependencies in human readable form.
   public boolean analyze(ResultsNode ourRec, int warningLevel)
   {
      boolean hadMsg = false;

      // The world doesn't have dependencies

      // Let each society analyze itself.

     // Iterator it = theSocieties.iterator();
     // while( it.hasNext() )
     // {
    // society thisSociety = MainWindow.theMainWindow.theSociety;
         // Get the society record.
         //contractOwnerBase cb = (contractOwnerBase)it.next();
          contractOwnerBase cb = (contractOwnerBase)MainWindow.theMainWindow.theSociety;
         // Analyze the society.
         // Note: We only add the cluster record if there is detail below it.
         ResultsNode rec = new ResultsNode(ourRec, cb.getLongName(), cb);
         if( cb.analyze(rec, warningLevel) )
         {
            // Add it.
            ourRec.children.add(rec);
            hadMsg = true;
         }
      //}

      return hadMsg;
   }

   /// Print the dependencies of our societies based on their contracts.
   public void printDependencies()
   {
      System.out.println("Dependencies for " + getName());

      // The world doesn't have dependencies

      // Let each society print its dependencies.
      // Allow each of the societies to attach their contracts.
     Iterator it = theSocieties.iterator();
      while( it.hasNext() )
      {
         // Get the society record.
         society thisSociety = (society)it.next();

         // Print the dependencies
         thisSociety.printDependencies();
      }

   }


   // We never display the world in a graph
   public Node getNode()
   {
      return null;
   }

/*
   /// Get the society record with the specified name.
   public society getSocietyByName(final String name)
   {
      // Find the named society.
      society cl = null;
      Iterator it = theSocieties.iterator();
      while( it.hasNext() )
      {
         // Get the cluster record.
         society thisSociety = (society)it.next();

         // Is this it?
         if( thisSociety.getName().equalsIgnoreCase(name) )
         {
            cl = thisSociety;
            break;
         }
      }

      return cl;

   }
*/

   /// Get the society record with the specified index.
   public society getSocietyByNumber(final int pos)
   {
      if( pos < 0 || pos >= theSocieties.size() )
      {
         System.err.println("Internal error: world::getSocietyByNumber(" + pos + "), but number of societies = " + theSocieties.size());
         return null;
      }

      return (society)theSocieties.get(pos);
   }

   /// Remove the society record with the specified index.
   // returns true on success, false if invalid number.
   public boolean removeSocietyByNumber(final int pos)
   {
      if( pos < 0 || pos >= theSocieties.size() )
         return false;

      theSocieties.remove(pos);
      return true;
   }

   /// Yes, we can display the world as a graph.
   public boolean hasGraph()
   {
      return true;
   }

   /// Render the societies in this world as a graph.
   public void renderGraph(samJGraph graph)
   {
      GraphModel ourModel = new GraphModel();


      // This is a two step process.
      // First, create a node for each society.
      //Iterator it = theSocieties.iterator();
      //while(it.hasNext())
     // {
       //  society thisSociety = (society)it.next();
       society thisSociety = MainWindow.theMainWindow.theSociety;

         thisSociety.renderNode(ourModel);
     // }

      // Finally, create the edges.
      //it =theSocieties.iterator();
      //while(it.hasNext())
     // {
         //society thisSociety = (society)it.next();
 //thisSociety = MainWindow.theMainWindow.theSociety;
         thisSociety.renderInputEdges(ourModel);
     // }


      // Tell the world that it is us.
      MainWindow.theMainWindow.theSociety.displayedComponent = this;

      // Attach our model to the graph display
      graph.setGraphModel(ourModel);

      // Display it.
      MainWindow.theMainWindow.layoutJGraph();


      //if the contract/triangle has been highlighted and matches write the contracts to the editor panel int he main window.
      samGraphController gc = (samGraphController)MainWindow.theMainWindow.theSociety.thePane.getGraphController();
      gc.upDatesite();

      //trying to set the buttons to grey out
      MainWindow.theMainWindow.update_BackandForward();


   }

   // Load the society from the ini files.
   // Returns the society, or null if unable to load.
   public society loadSocietyFromIniFiles(File file, String contractFileName, final boolean debugParsers, final boolean debugContracts, final boolean verbose)
   {
      //------------- Load the society from the ini files. -------------
      // Trim the name for displaying.  Just the last name after the final "."
      String name = file.getName();

      String rootName = name.substring(name.lastIndexOf('.')+1);
      if( rootName.equals("soc") )
      {
         rootName = name.substring(0, name.lastIndexOf('.'));
      }

      // Create the society record.
      society tmpSociety = new society(rootName,this);
      // Try to load the society.
     if( !tmpSociety.loadFromIniFiles( file, debugParsers ) )
      {
         System.out.println(tmpSociety.loadFromIniFiles( file, debugParsers ));
         return null;
      }

      // Commit
      int tab = attachSociety(tmpSociety, tmpSociety.getName() );
      if( tab < 0 )
      {
         System.err.println("world::loadSocietyFromIniFiles - attachSociety returned illegal tab:" + tab);
         return null;
      }

      // --------------- Load the contracts from the XML files -------------
      // Build a file name for the plugin contracts.
      File contractFile = new File(file.getParent(), contractFileName);

        // Create the contract object
      systemContracts tmpContracts = new systemContracts();

      // Try to load the contracts
      if( !tmpContracts.loadFile( contractFile, debugParsers ) )
      {
         System.err.println("Unable to load the contracts file " + contractFile.toString() );
         return null;
      }

      // Set the contracts for this society
      tmpSociety.setSystemContract(tmpContracts);


      //------------- Walk the society DAG and add pointers to the contracts --------
      tmpSociety.attachContracts(tmpContracts, debugContracts);


      // Walk the society DAG and compute the dependencies based on the contracts
      tmpSociety.computeDependencies(null, debugContracts);


      // Now print the dependencies
      if( verbose )
         printDependencies();

      // Activate it.
      // This sets the activesociety reference, which we use to find the contracts.
      MainWindow.theMainWindow.activateTab(tab);

      return tmpSociety;

   }


  /* public boolean savetofile(File file,boolean debugflag)
   {
      if( file == null )
      {
         System.err.println("Internal Error: Null file passed to world::savetofile");
         return false;
      }


        // if (theSocieties!=null)
           // {
             // Iterator its = theSocieties.iterator();
             // while(its.hasNext())
             // {
               //   society thisSociety =(society)its.next();
                  society thisSociety =MainWindow.theMainWindow.theSociety;
                  thisSociety.savetofile(file,debugflag);
              // }//endswhile
           // }//ends if
            return true;

      }//ends the savesociety method*/


   // No where to go from here.
   public boolean moveUp()
   {
//      System.out.println("In world moveup: nothing to do");
      return false;
   }

   /// Get the number of input slots (subscriptions) for this record.
   public int getNumInputs()
   {
      // we don't have contracts.
      return 0;
   }

   /// Get the number of output slots (publications) for this record.
   public int getNumOutputs()
   {
      // we don't have contracts.
      return 0;
   }

   /// Is the specified input slot connected?
   public boolean isInputSlotConnected(int slot)
   {
      // we don't have contracts.
      return false;
   }

   /// Add all the edges from our producers to the graph.
   public void renderInputEdges(GraphModel model)
   {
      // We don't have any
   }

   /// Is the specified input slot connected?
   public boolean isOutputSlotConnected(int slot)
   {
      // we don't have contracts.
      return false;
   }

   /// Render us as a Node.
   public void renderNode(GraphModel model)
   {
      // We don't do that.
   }

   // Return our set of children as a list.
   public Collection getChildren()
   {
      return theSocieties;
   }

   // set of children
   public void setChildren(Vector kids)
   {
      theSocieties = kids;
   }

   /// Return the number of children.
   public int getChildCount()
   {
      return theSocieties.size();
   }

    /// Return the object's name for display in the jTree
   public String toString()
   {
      return name;
   }

   // Return our long name.
   public String getLongName()
   {
      return "World: " + name;
   }

   public void setSystemContract(systemContracts contract)
   {
      MainWindow.theMainWindow.theSociety.setSystemContract(contract);
   }

   public systemContracts getSystemContract()
   {
      return MainWindow.theMainWindow.theSociety.getSystemContract();
   }

   // Return a disjoint copy of the subtree rooted at this node.
   public contractOwnerBase recursiveCopy(contractOwnerBase newParent)
   {
      world rtn = new world(name);

      // Now dup the children.
      Vector kids = new Vector();
      Iterator its = theSocieties.iterator();
      while(its.hasNext())
      {
         contractOwnerBase oldSociety = (contractOwnerBase)its.next();

         kids.add( oldSociety.recursiveCopy(rtn) );
      }
      rtn.setChildren( kids );

      return rtn;
   }

   public boolean writeSocFile(File file,boolean debugflag)
   {
      if( file == null )
      {
         System.err.println("Internal Error: Null file passed to world::savetofile");
         return false;
      }
      society thisSociety =MainWindow.theMainWindow.theSociety;
      thisSociety.writeSocFile(file,debugflag);

      return true;

   }//ends the savesociety method

}
