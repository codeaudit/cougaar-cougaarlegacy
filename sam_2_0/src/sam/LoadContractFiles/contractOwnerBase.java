
package sam.LoadContractFiles;

import sam.display.samJGraph;
import diva.graph.model.GraphModel;
import diva.graph.model.Node;
import diva.canvas.Figure;
import diva.canvas.selection.SelectionInteractor;
import diva.canvas.selection.SelectionModel;
import diva.canvas.selection.SelectionDragger;
import diva.canvas.interactor.Interactor;

import sam.display.samGraphController;
import sam.display.samBaseFigure;

import sam.graphPlanner.graphNode;
import sam.inputBar;
import sam.outputBar;

import sam.ResultsNode;
import sam.MainWindow;
import org.cougaar.core.util.Operator;

import javax.swing.tree.TreeNode;
import java.util.Enumeration;
import java.util.Collection;
import java.util.ArrayList;
import java.util.Vector;
import java.util.List;
import java.util.Iterator;
import java.io.File;
import java.io.*;

/**
 * Class: contractOwnerBase, the base class for cluster,society and also world.
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

public abstract class contractOwnerBase implements TreeNode, graphNode
{
   // The name of this plugin.
   protected String name;

   // This records parent in the heirarchy
   protected contractOwnerBase parent;

   // If not null, the figure rendered for this node.
   samBaseFigure theFigure;

   // This cluster's I/O contracts and associated link points.
   public setOfContractLinkPoints contract;

   //
   public setOfContractOperators contractOperators;

   // Set if this node should be shown as a different color in the display.
   // Used to highlight generated nodes in the designer.
   private boolean different;
   /**
    * Constructor
    */

   public contractOwnerBase(contractOwnerBase ourParent)
   {
      parent = ourParent;
      contract = new setOfContractLinkPoints();
   }

   // Add a new component to the record (i.e., a plugin in a cluster)
   // Returns true on success, false on error or unsupported.
   /**
    * Adds a new component
    * @param name String
    */
   public boolean addComponent(String name)
   {
      return false;
   }
   /**
    * Adds a different component Like a community to a society
    * @param name String
    */
   public boolean addHighLevelComponent(String name)
   {
      return false;
   }

   // Add a new component to the record (i.e., a plugin in a cluster)
   // and sets the different flag.
   // Returns true on success, false on error or unsupported.
   /**
    * Adds a Different component
    * @param name String
    */
   public boolean addDifferentComponent(String name)
   {
      return false;
   }


   /**
    * Adds a High level component
    * @param name String
    */
   public boolean addDifferentHighLevelComponent(String name)
   {
      return false;
   }
   // Add a new component to the record (i.e., a plugin in a cluster)
   // Returns true on success, false on error or unsupported.
   /**
    * Adds a component
    * @param child contractOwnerBase
    */
   public boolean addComponent(contractOwnerBase child)
   {
      return false;
   }

   /**
    * Returns a contractOwnerBase
    * @param ourparent contractOwnerBase
    */
   public contractOwnerBase cutComponent(contractOwnerBase ourParent)
   {

      return ourParent;
   }
   /**
    * Sets the name
    * @param newName String
    */

   public void setName(final String newName)
   {
      name = newName;
   }

   /**
    * Returns the name as String.
    */
   public String getName()
   {
      return name;
   }

   // Return something like "Cluster xxx".
   /**
    * Returns the name as String the form of "Cluster XXX."<p>
    */
   public abstract String getLongName();

   public contractOwnerBase getParentNode()
   {
      return parent;
   }

   /**
    * Sets the Figure.<p>
    * @param fig samBaseFigure.
    */

   public void setFigure(samBaseFigure fig)
   {
      theFigure = fig;
   }
   /**
    * Returns the figure as asamBaseFigure
    */

   public samBaseFigure getFigure()
   {
      return theFigure;
   }

   // Attach the contract records to this plugin.
   /**
    * Attaches the contract record to the plugin.<p>
    * @param theSystemContracts systemContracts, debug boolean.<p>
    */
   public abstract void attachContracts(systemContracts theSystemContracts, final boolean debug);


   // Free the existing design and init to an empty configuration.
   /**
    * Frees the existing design and initializes it to an empty configuration.<p>
    */
   public void wipe()
   {
      contract.prepareForDelete();
      contract = new setOfContractLinkPoints();
      different = false;
   }

   //Trying to clear all the links
   /**
    * Clears all the Links.<p>
    */
   public void clearLinks()
   {
      contract.clearLinks();
   }

   //trying to add new contracts
   /**
    * Adds New Publish contracts.<p>
    */
   public void addNewPublishContract()
   {
      // Create a default, empty contract.
     Operator contracts = null;

      contractLinkPoint lp = new contractLinkPoint(contracts, true, this, contract.getNumPublications());

      //adding new output records
      contract.addPublishContract(lp);

      //adding the operator to the set of contractoperator
      //contractOperators.addPublishContract(contracts);
   }

   /**
    * Adds new Subscribe contracts.<p>
    */
   public void addNewSubscribeContract()
   {
      // Create a default, empty contract.
      Operator contracts = null;

      contractLinkPoint lp = new contractLinkPoint(contracts, true, this, contract.getNumSubscriptions());

      //adding new output records
      contract.addSubscribeContract(lp);

      //adding the operator to the set of contractoperators.
      //contractOperators.addSubscribeContract(contracts);
    }

   /// Compute the dependencies of our plugins based on their contracts.
   /**
    * Compute the dependencies, based on the contracts.<p>
    * @param theobjects Collection, debug boolean.<p>
    */
   public abstract void computeDependencies(Collection theObjects, final boolean debug);

   // Tell each plugin to compute its dependencies.
   // (appends them to its list, so need to clear them first)
   /**
    * Computing the member dependencies.<p>
    * @param debug boolean.<p>
    */
   public abstract void computeMemberDependencies(final boolean debug);

   // Tell each level to clear its dependencies.
   /**
    * Clear the dependencies.<p>
    */
   public abstract void clearMemberDependencies();

   /// Print the dependencies of our plugins based on their contracts.
   /**
    * Print the dependencies, based on the contracts.<p>
    */
   public abstract void printDependencies();

   // If not null, the Graph node representing this plugin.
   /**
    * Returns the Graph node representing the contractOwnerBaseObject.<p>
    */
   public abstract Node getNode();

   /// Does this node represent a graph we can display?
   /**
    * Returns true if the node represents the graph, that we can display .<p>
    */
   public abstract boolean hasGraph();

   /// Render the cluster as a graph.
   /**
    * Renders the contractOwnerBase as a graph.<p>
    * @param graph samJGraph
    */
   public abstract void renderGraph(samJGraph graph);

   /// Render the cluster as a Node.
   /**
    * Renders the contractOwnerBase as a graph.<p>
    * @param model GraphModel.<p>
    */
   public abstract void renderNode(GraphModel model);

   /// Add all the edges from our producers to the graph.
   /**
    * Add all the edges to the graph.<p>
    * @param model GraphModel.<p>
    */
   public abstract void renderInputEdges(GraphModel model);

   /// Analyze the society and report any problems.
   // Warning level: 0=only errors, 10=all.
   /**
    * Returns true when analyzed the society.<p>
    * @param ourRec ResultsNode, warninglevel integer.<p>
    */
   public abstract boolean analyze(ResultsNode ourRec, int warningLevel);

   /// If possible, move up and display this record as a node in the next level's graph.
   /// Returns true on success, false if top level node.
   /**
    * Returns true on success and false if top level node not present.<p>
    */
   public abstract boolean moveUp();

   /// Get the number of input slots (subscriptions) for this record.
   /**
    * Returns the number of the input slots (subscriptions).<p>
    */
   public abstract int getNumInputs();

   /// Get the number of input slots (publications) for this record.
   /**
    * Returns the number of the output slots (publications).<p>
    */
   public abstract int getNumOutputs();

   /// Is the specified input slot connected?
   /**
    * Returns true if the specified slot is connected.<p>
    * @param slot integer.<p>
    */
   public abstract boolean isInputSlotConnected(int slot);

   /// Is the specified output slot connected?
   /**
    * Returns true if the output slot is connected.<p>
    * @param slot integer.<p>
    */
   public abstract boolean isOutputSlotConnected(int slot);

   //Return the specified slots contract
   /**
    * Returns the Operator.<p>
    * @pram isInput boolean,slot integer.<p>
    */
   public Operator getContractOperator(boolean isInput, int slot)
   {
      return contract.getContractOperator(isInput, slot);
   }

   //trying to add new contracts
   /**
    * add new publish contracts.<p>
    * @param newContract Operator.<p>
    */
   public void addNewPublishContractOperator(Operator newContract)
   {
      contractLinkPoint lp = new contractLinkPoint(newContract, true, this, contract.getNumPublications());

      //adding new output records
      contract.addPublishContract(lp);
   }
   /**
    * Add a new subscribe Contract Operator.<p>
    * @param newContract Operator.<p>
    */

   public void addNewSubscribeContractOperator(Operator newContract)
   {

      contractLinkPoint lp = new contractLinkPoint(newContract, false, this, contract.getNumSubscriptions());

      //adding new output records
      contract.addSubscribeContract(lp);
    }
   /**
    * Returns the specified slot's contract as an XML string.
    * @param isInput boolean, slot integer.<p>
    */
   public String getContract(boolean isInput, int slot)
   {
      return contract.getContract(isInput, slot);
   }


   /**
    * Replace the specified slot's contract with the new one.
    * @param isInput boolean, slot integer, op Operator.<p>
    */
   public boolean setContract(boolean isInput, int slot, Operator op)
   {
      return contract.setContract(isInput, slot, op);
   }


   /**
    * Returns the Treenode at the specified Index.<p>
    * @param childIndex integer.<p>
    */
   public TreeNode getChildAt(int childIndex)
   {
      Enumeration kids = children();

      // Return the specified kid
      int num = 0;
      while( kids.hasMoreElements() )
      {
         contractOwnerBase rec = (contractOwnerBase)kids.nextElement();
         if( num == childIndex )
            return rec;
         num++;
      }

      // Hmm, off the end.
      return null;
   }


   /**
    * Return the number of children.
    */
   public abstract int getChildCount();
   /**
    * Returns the Index of the treenode, as an integer.<p>
    * @param node TreeNode.<p>
    */

   public int getIndex(TreeNode node)
   {
      Enumeration kids = children();

      // Return the specified kid
      int num = 0;
      while( kids.hasMoreElements())
      {
         contractOwnerBase rec = (contractOwnerBase)kids.nextElement();
         if( rec == node )
            return num;
         num++;
      }

      // Hmm, off the end.
      return -1;
   }

   /**
    *
    */
   public boolean getAllowsChildren()
   {
      return true;
   }

   /////////////////////////////////////////////////////////////////////////////////////////
   /**
    * Returns the Treenode.<p>
    */
   public TreeNode getParent()
   {
      return parent;
   }


   /////////////////////////////////////////////////////////////////////////////////////////
   /**
    * Returns true on success and false on failure.<p>
    */
   public boolean isLeaf()
   {
      return getChildCount() == 0;
   }

   /////////////////////////////////////////////////////////////////////////////////////////
   /**
    * Returns our set of children as an enumeration.<p>
    */

   public Enumeration children()
   {
      return new Vector( getChildren() ).elements();
   }

   /////////////////////////////////////////////////////////////////////////////////////////
   ///
   /**
    * Returns the object's name for display in the jTree as a String.<p>
    */
   public abstract String toString();

   /////////////////////////////////////////////////////////////////////////////////////////
   // Select this object.
   /**
    * Select the Object.<p>
    */
   public void select()
   {
      samGraphController gc = (samGraphController)MainWindow.theMainWindow.theSociety.thePane.getGraphController();
      if( gc == null )
      {
         System.err.println("Internal Error: display.getGraphController() returned null!");
      }
      else
      {
         SelectionModel sm = gc.getSelectionModel();
         if( sm == null )
         {
            System.err.println("Internal Error: samGraphController.getSelectionModel() returned null!");
         }
         else
         {
            if( theFigure == null )
            {
               System.err.println("Internal Error: select called with null figure!  Rec=" + this);
            }
            else
            {
               // Do it!
               sm.addSelection(theFigure);
            }
         }
      }
   }

   /////////////////////////////////////////////////////////////////////////////////////////
   // Save ourselves to an ini file.
   // NOTE: Default action is to not do any save.
   /**
    * Returns true on saving the file and false on failure.<p>
    * @param file File, debugflag boolean.<p>
    */
   public boolean savetofile(File file, boolean debugflag)
   {
      return true;
   }

   /////////////////////////////////////////////////////////////////////////////////////////
   // Pass on the hidden status of our input links to our output links.
   // Here is the logic:
   //  If they just hid an input link and they are now all hidden, hide our output links.
   //  If they just showed an input link and they were all hidden, show our output links.
   //  else, do nothing.
   /**
    * Passes the status of the input links to the out put links.<p>
    * @param justhid boolean.<p>
    */
   public void checkLinks(boolean justHid)
   {
      // Count the number of visible input links.
      int num_visible = 0;
      for(int i=0; i<contract.getNumSubscriptions(); i++)
      {
         if( !contract.isSlotHidden(true, i) )
         {
            num_visible ++;
         }
      }

      // Now decide if we should hide or show our output links.
      if( justHid && num_visible == 0 )
      {
         for(int i=0; i<contract.getNumPublications(); i++)
         {
            contract.hideSlot(false, i);
         }
      }
      else if( !justHid && num_visible == 1 )
      {
         for(int i=0; i<contract.getNumPublications(); i++)
         {
            contract.showSlot(false, i);
         }
      }
      // Else, do nothing
   }


   /////////////////////////////////////////////////////////////////////////////////////////
   // methods to support implementing graphNode
   /////////////////////////////////////////////////////////////////////////////////////////
   /**
    * Returns the graphNode
    */
   public graphNode getParentGraphNode()
   {
      return parent;
   }

   // Get this node's children.
   // Returns a list of graphNodes which may be empty if none exist.
   /**
    * Returns the graph nodes as a collection.<p>
    */
   abstract public Collection getChildren();


   /**
    * Get the list of input links.<p>
    */
   public List getInputs()
   {
      return contract.subscribe;
   }

   /**
    * Get the list of output links.<p>
    */
   public List getOutputs()
   {
      return contract.publish;
   }

   /**
    * Returns the named node, or null if it does not exist.<p>
    * @param name String.<p>
    */
   public contractOwnerBase getChildByName(final String name)
   {
      Collection list = getChildren();
      Iterator it = list.iterator();
      while( it.hasNext() )
      {
         contractOwnerBase node = (contractOwnerBase)it.next();

         // Skip the input/output bars, they are not real children.
         if( node instanceof inputBar || node instanceof outputBar )
         {
            continue;
         }

         if( node.getName().equals( name ) )
         {
            return node;
         }
      }

      // Didn't find it.
      return null;
   }


   // Takes the parent for the new subtree as input parameter.
   /**
    * Returns a disjoint copy of the subtree rooted at this node.<p>
    * @param newParent contractOwnerBase.<p>
    */
   abstract public contractOwnerBase recursiveCopy(contractOwnerBase newParent);

   // Used to highlight generated nodes in the designer.
   /**
    * Set if this node should be shown as a different color in the display.<p>
    */
   public void setDifferent(final boolean diff)
   {
      different = diff;
   }


   /**
    * Make this node normal.<p>
    */
   public void clearDifferent()
   {
      different = false;
   }


   /**
    * Returns true if this node is to be shown as a different clor.<p>
    */
   public boolean isDifferent()
   {
      return different;
   }
   ////////////method for writitng the xml///////////
   /**
    * Returns true if the society has been written and saved as an xml else returns false.<p>
    * @param pw PrintWriter, debug boolean.<p>
    */
   public boolean writeToXml(PrintWriter pw, boolean debug)
   {
      return true;
   }

   /////////Method to write the soc files with out the involvement of the group concept that has been used//////////////
   /**
    * Returns true on writing to the soc files.<p>
    *  @param pw PrintWriter, debug boolean.<p>
    */
   public boolean writeOutSoc(PrintWriter pw, boolean debug)
   {
      return true;
   }

   ///////////////////Method to write out the ini files with out using the Group//////////////////////////
   /**
    * Returns true on writing the ini files else false.<p>
    * @param file File, debug boolean.<p>
    */
   public boolean writeIniFile(File file, boolean debug)
   {
      return true;
   }

   ////////////////////////////////////////////////////////////////////////////////////////////////////
   /**
    * Returns true on writing the ini files.<p>
    * @param pw PrintWriter, debug boolean.<p>
    */
   public boolean writeIniFile(PrintWriter pw,  boolean debug)
   {
      return true;
   }

}