
package sam.display;

import java.io.*;
import java.util.Properties;
import sam.LoadContractFiles.systemContracts;

import sam.MainWindow;
import sam.prettyprint;
import org.cougaar.core.util.Operator;

import diva.graph.model.GraphModel;
import diva.graph.model.Graph;
import diva.graph.model.Node;
import diva.graph.model.BasicNode;
import diva.graph.model.Edge;
import diva.graph.model.GraphEvent;

import diva.graph.AbstractGraphController;
import diva.graph.NodeInteractor;
import diva.graph.EdgeInteractor;
import diva.graph.GraphView;

import diva.canvas.Figure;
import diva.canvas.FigureLayer;
import diva.canvas.GraphicsPane;
import diva.canvas.Site;
import diva.canvas.AbstractSite;

import diva.canvas.connector.AutonomousSite;
import diva.canvas.connector.CenterSite;
import diva.canvas.connector.PerimeterSite;

import diva.canvas.connector.Connector;
import diva.canvas.connector.ConnectorManipulator;
import diva.canvas.connector.ConnectorEvent;
import diva.canvas.connector.ConnectorListener;
import diva.canvas.connector.ConnectorTarget;

import diva.canvas.event.LayerAdapter;
import diva.canvas.event.LayerEvent;
import diva.canvas.event.MouseFilter;
import diva.canvas.event.LayerMotionListener;

import diva.canvas.interactor.Interactor;
import diva.canvas.interactor.AbstractInteractor;

import diva.canvas.manipulator.GrabHandle;

import diva.canvas.selection.SelectionInteractor;
import diva.canvas.selection.SelectionModel;
import diva.canvas.selection.SelectionDragger;

import diva.util.Filter;
import sam.display.samTarget;

import java.awt.event.InputEvent;
import java.awt.event.MouseEvent;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

import java.util.HashMap;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Collection;

import sam.LoadContractFiles.contractOwnerBase;
import sam.inputBar;
import sam.outputBar;
import sam.EdgeData;
import sam.display.samBaseFigure;
import sam.society;
import sam.community;
import sam.cluster;
import sam.plugin;

/**
 * A basic implementation of GraphController, which works with
 * simple graphs that have edges connecting simple nodes. It
 * sets up some simple interaction on its view's pane.
 *
 * @author 	Michael Shilman (michaels@eecs.berkeley.edu)
 * @version	$Revision: 1.1 $
 * @rating      Red
 * $Id: samGraphController.java,v 1.1 2003-05-20 15:23:52 tom Exp $
 *
 * Copyright (c) 1998 The Regents of the University of California.
 * All rights reserved.  See the file COPYRIGHT for details.
 */
public class samGraphController extends AbstractGraphController
{
   // Handle for the contract popup menu.
   JPopupMenu contractPopup = new JPopupMenu();

   //adding the menu items to the popup menu
   public JMenuItem item1 = new JMenuItem();

   JMenuItem item2;

   //adding the menu items to the popupmenu
   JMenuItem item3;

  // samGraphController gc = MainWindow.theDisplay.getGraphController();


   //flag to go between hide and show dependencies.
   boolean flag;

   //for getting the name of the properties.
   Properties systemProperties;

    /** The selection interactor for drag-selecting nodes
     */
    private static SelectionDragger _selectionDragger;

    // The interactor for printing contract contractPopup.
    private contractPopupInteractor _contractPopupInteractor;

    /** The filter for control operations
     */
    private MouseFilter _controlFilter = new MouseFilter (
            InputEvent.BUTTON1_MASK,
            InputEvent.CTRL_MASK);

    /** The filter for contractPopup mouse events
     */
//    private MouseFilter _contractPopupFilter = new MouseFilter(InputEvent.BUTTON3_MASK);

   // A filter which takes all mouse buttons and all modifiers.
   private MouseFilter _contractPopupFilter = new MouseFilter(InputEvent.BUTTON1_MASK |
                                                              InputEvent.BUTTON2_MASK |
                                                              InputEvent.BUTTON3_MASK,
                                                              0, 0);

   // The currently selected site and its displayedComponent record.
   public samBaseSite selectedSite = null;
   public contractOwnerBase selectedSitesDisplayedComponent = null;

   // USed to hold on to the selected site for the contract popup menu.
   public samBaseSite savedSite;

    /**
     * Create a new basic controller with default node and edge interactors.
     */
    public samGraphController()
    {
        // The interactors attached to nodes and edges
        SelectionModel sm = getSelectionModel();
        NodeInteractor ni = new NodeInteractor(this, sm);
        EdgeInteractor ei = new EdgeInteractor(this, sm);
        setNodeInteractor(ni);
        setEdgeInteractor(ei);

        samTarget ct = new samTarget() {
	    public boolean accept (Figure f) {
                return (f.getUserObject() instanceof Node);
		// FIXME Used to also have ||
		// (f instanceof FigureWrapper &&
                //             ((FigureWrapper)f).getChild().instanceof Node);
            }
	};
        setConnectorTarget(ct);

    }

    // Return the drag interactor
    public SelectionDragger getSelectionDragger()
    {
       return _selectionDragger;
    }

   // Completely redraw the graph.
   public void completeRedraw()
   {
      //force an update
//      GraphEvent update = new GraphEvent(GraphEvent.STRUCTURE_CHANGED, this, getGraphModel().getGraph());
      GraphEvent update = new GraphEvent(GraphEvent.STRUCTURE_CHANGED, this, MainWindow.theMainWindow.theSociety.theJGraph);

      structureChanged(update);
   }

    /**
     * Initialize all interaction on the graph pane. By the time
     * this method is called, all relevant references to views,
     * panes, and roles must already have been set up.
     */
    public void initializeInteraction ()
    {
        GraphicsPane pane = getGraphView().getGraphicsPane();

        // Create a listener that pops up the contract menu.
        _contractPopupInteractor = new contractPopupInteractor();
        _contractPopupInteractor.setMouseFilter(_contractPopupFilter);
        pane.getBackgroundEventLayer().addInteractor(_contractPopupInteractor);

        // Initialize the contract popup menu.
        processContractPopups contractPopupActionListener = new processContractPopups();

       // item1 = new JMenuItem("View Contract");
       item1.setText("View Contract");
        contractPopup.add(item1);
        item1.addActionListener(contractPopupActionListener);

        item2 = new JMenuItem("Hide Dependencies");
        contractPopup.add(item2);
        item2.addActionListener(contractPopupActionListener);

        item3 = new JMenuItem("Externalize Contracts");
        contractPopup.add(item3);
        item3.addActionListener(contractPopupActionListener);
    }

   /////////////////////////////////////////////////////////////////////////
   // Override the default method in AbstractGraphController so it uses our
   // sites to create the edge figures.
   //
   /// Creates an edge figure for this edge, adds it to the view, and routes it.
   public void createEdgeFigure(Edge e)
   {
      // Get our data record back.
      EdgeData rec = (EdgeData)e.getSemanticObject();

      // Get the graph view.
      GraphView view = getGraphView();

      // Get the src and dst sites.
      samBaseFigure srcFig = (samBaseFigure)view.getNodeFigure(e.getTail());
      samBaseFigure dstFig = (samBaseFigure)view.getNodeFigure(e.getHead());
      Site srcSite = srcFig.getOutputSite(rec.srcSlot);
      Site dstSite = dstFig.getInputSite(rec.dstSlot);

      if( srcSite == null || dstSite == null )
      {
         System.err.println("Internal Error: Edge sites not found in samGraphController::createEdgeFigure\n" +
                            "   srcFig = " + srcFig + ", srcSite = " + srcSite +
                             " dstFig = " + dstFig + ", dstSite = " + dstSite);
         return;
      }

      // Create the figure
      Connector ef = view.getEdgeRenderer().render(e, srcSite, dstSite);
      ef.setInteractor(getEdgeInteractor());

      // Add it to the view
      view.addEdgeFigureToLayer(ef);
      view.addEdgeMapping(e, ef);

      // Re-route it
      ef.route();
   }

   ///////////////////////////////////////////////////////////////
   //// ContractPopup

   /// An inner class that pops up a menu for a contract.
   protected class contractPopupInteractor extends AbstractInteractor
   {
      boolean isup = false;

      // Catch the event
      public void mousePressed(LayerEvent e)
      {
         //making it cross platform
         if(e.isPopupTrigger())
         showpopup(e);

      }

      // Catch the event
      public void mouseReleased(LayerEvent e )
      {
         //making it cross platform
         if(e.isPopupTrigger())
         showpopup(e);
      }

      // Catch the event
      public void mouseClicked(LayerEvent e)
      {
         showpopup(e);
      }

      // Catch the event
      private void showpopup(LayerEvent e)
      {


         if( isup )
         {
            contractPopup.setVisible(false);
            isup = false;
            savedSite = null;
         }



         if(e.isPopupTrigger())
         {
            Figure fig = e.getFigureSource();
            if( fig != null )
            {
               if( fig instanceof samBaseFigure )
               {
                  samBaseFigure ourFigure = (samBaseFigure)fig;

                  // See if they hit one of our sites.
                  samBaseSite site = ourFigure.checkSiteHit(e.getLayerX(), e.getLayerY());

                  if( site != null )
                  {
                     // Remember the site.
                     savedSite = site;

                     if( site.isHidden() )
                        item2.setText("Show Dependencies");
                     else
                        item2.setText("Hide Dependencies");


                     contractPopup.setLocation((int)e.getLayerX(), (int)e.getLayerY());
                     contractPopup.setVisible(true);
                     isup = true;

                  }
               }
            }
         }
      }
   }

   // Internal class to process popup window
   class processContractPopups implements ActionListener
   {

      public void actionPerformed(ActionEvent event)
      {
         samGraphController gc = (samGraphController)MainWindow.theMainWindow.theSociety.thePane.getGraphController();


         JMenuItem mi = (JMenuItem)event.getSource();
         String label = mi.getText();


         if( label.equals("View Contract") )
         {
            displayContract( gc.savedSite );
            MainWindow.theMainWindow.menuedit2.setEnabled(true);

         }
         else if( label.equals("Hide Dependencies") )
         {
            gc.savedSite.hideSite();
            MainWindow.theMainWindow.theSociety.thePane.repaint();
         }
         else if( label.equals("Show Dependencies") )
         {
            gc.savedSite.showSite();
             MainWindow.theMainWindow.theSociety.thePane.repaint();
         }
         else if (label.equals("Externalize Contracts"))
         {
             systemContracts systemcontract = MainWindow.theMainWindow.theWorld.getSystemContract();

             //getting the name of the operator
             Operator operatorName = gc.savedSite.getContractOperator();

             // if it is publish?
             boolean isPublish = !gc.savedSite.isInput();

             //getting hold of all the displayed components (i.e., plugins)
             Collection theChildren = MainWindow.theMainWindow.theSociety.displayedComponent.getChildren();

             //getting an iterator on the collection
             Iterator it = theChildren.iterator();
            if( !isPublish )
            {
                 while (it.hasNext())
                 {
                     contractOwnerBase child = (contractOwnerBase)it.next();
                     if(child instanceof inputBar)
                     { //if the contract owner base is instance of inputbar then
                       // add the new contrcat to the inputbar
                       child.addNewSubscribeContractOperator(operatorName);
                     }
                 }
                  //add the new contract if it is a input/subscribe to the displayed component
                  MainWindow.theMainWindow.theSociety.displayedComponent.addNewSubscribeContractOperator(operatorName);
            }
            else
            {
                  while (it.hasNext())
                 {
                     contractOwnerBase child = (contractOwnerBase)it.next();
                     if(child instanceof outputBar)
                     {
                        //if the contract owner base is instance of outputbar then
                       // add the new contrcat to the outputbar
                       child.addNewSubscribeContractOperator(operatorName);
                     }
                 }
                   //add the new contract if it is a output/publish to the displayed component
                 MainWindow.theMainWindow.theSociety.displayedComponent.addNewPublishContractOperator(operatorName);
            }
            //clear all the member dependencies.
            MainWindow.theMainWindow.theSociety.displayedComponent.clearMemberDependencies();

            //compute them again.
            MainWindow.theMainWindow.theSociety.displayedComponent.computeMemberDependencies(MainWindow.theMainWindow.properties.getVerbose());

            if( MainWindow.theMainWindow.theSociety.displayedComponent instanceof plugin)
            {
               systemcontract.addPluginContract(MainWindow.theMainWindow.theSociety.displayedComponent.getName(),operatorName,isPublish);

            }
            else if( MainWindow.theMainWindow.theSociety.displayedComponent instanceof cluster)
            {
               systemcontract.addClusterContract(MainWindow.theMainWindow.theSociety.displayedComponent.getName(),operatorName,isPublish);
               System.out.println("Done adding the new contract" + operatorName.toString() + " to " + MainWindow.theMainWindow.theSociety.displayedComponent.getName() );
            }

            else if( MainWindow.theMainWindow.theSociety.displayedComponent instanceof society)
            {
               systemcontract.addSocietyContract(MainWindow.theMainWindow.theSociety.displayedComponent.getName(),operatorName,isPublish);
            }
            else if(MainWindow.theMainWindow.theSociety.displayedComponent instanceof community)
            {
               systemcontract.addCommunityContract(MainWindow.theMainWindow.theSociety.displayedComponent.getName(),operatorName,isPublish);
            }
            else
            {
               System.out.println("Internal Error:: samGraphController::processContractPopups");
            }


            //draw the graph again.
            MainWindow.theMainWindow.theSociety.displayedComponent.renderGraph(MainWindow.theMainWindow.theSociety.theJGraph);

         }


         // Done with the menu.
         gc.contractPopup.setVisible(false);
      }
   }


   // An internal class to show a contract.
   private void displayContract(samBaseSite site)
   {
      if( site != null )
      {
         // Get the site's contract as an XML string
         String contract = prettyprint.XML(site.getContract());

         // clear any outstanding selections.
         samGraphController sgc = (samGraphController)MainWindow.theMainWindow.theSociety.thePane.getGraphController();


         //will clear rthe first selected triangle/contract
         sgc.clearSelections(true);

         // Select this site.
         sgc.selectedSite = site;
         sgc.selectedSitesDisplayedComponent =  MainWindow.theMainWindow.theSociety.displayedComponent;
         site.highlightSite();

         // Display the contract in the text window.
         MainWindow.setDisplayedText(contract);

         // Repaint
          MainWindow.theMainWindow.theSociety.thePane.repaint();
      }
   }

   /// Clear any outstanding selections.
   /// Pass true to also clear selected sites.
   public void clearSelections( boolean alsoClearSelectedSites )
   {
      // clear any outstanding selections.
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
            sm.clearSelection();
         }
      }


     if( alsoClearSelectedSites )
     {
        // Also clear the selected site.
        if( !(selectedSite == null) )
        {
            selectedSite.normalSite();
            selectedSite = null;
            selectedSitesDisplayedComponent = null;
        }
      }

      // Repaint
       MainWindow.theMainWindow.theSociety.thePane.repaint();
   }


   /// Add a new selected site.
   public void addSelectedSite(samBaseSite site)
   {
      selectedSite  = site;
      selectedSitesDisplayedComponent = MainWindow.theMainWindow.theSociety.displayedComponent;
   }

   public void upDatesite()
   {
      // Is there a highlighted site in this society?
     if( selectedSite != null )
     {
         // Is the selected site visible on this page?
        if(selectedSitesDisplayedComponent.equals(MainWindow.theMainWindow.theSociety.displayedComponent))
        {
            // Yes, show the contract text

             MainWindow.theMainWindow.setDisplayedText( prettyprint.XML( selectedSite.getContract() ) );
         }
         else
         {
            // No, blank out the contract text window.
            MainWindow.theMainWindow.setDisplayedText(" ");
         }
      }
      else
      {
         // No highlighted sites, so make sure the contract display window is blank.
         MainWindow.theMainWindow.setDisplayedText(" ");
      }
   }

 ///////////////////////////////////////////////////////////////////////////////////////////////////

   // Return a collection of the selected objects
   public Collection getSelectedObjects()
   {
      // Return the list of outstanding selections as an arraylist.
      ArrayList objs = new ArrayList();

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
            Iterator it = sm.getSelection();
            while( it.hasNext() )
            {
               Object rec = it.next();
               objs.add( rec );
            }
         }
      }


      return objs;
    }

}
