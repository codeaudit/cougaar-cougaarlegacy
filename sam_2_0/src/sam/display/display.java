
package sam.display;

import diva.canvas.*;
import diva.graph.*;
import diva.graph.layout.GridAnnealingLayout;
import diva.graph.layout.GlobalLayout;
import diva.graph.layout.LayoutTarget;

import diva.graph.model.*;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.awt.geom.*;
import java.io.File;
import java.io.FileOutputStream;
import java.io.DataOutputStream;
import javax.swing.filechooser.FileFilter;
import diva.canvas.connector.ConnectorTarget;
import diva.canvas.connector.PerimeterSite;
import diva.canvas.connector.Connector;
import diva.canvas.connector.StraightConnector;
import diva.canvas.connector.ArcConnector;
import sam.LoadContractFiles.contractOwnerBase;

import sam.display.samEdgeRenderer;
import sam.display.samTarget;
import sam.display.samLayout;
import sam.display.samGraphController;
import sam.display.samJGraph;

/**
 * The graph demo demonstrates basic graph editing and layout
 * functionality, illustrates the key points of the graph
 * architecture. A graph is constructed programmatically, and can then
 * be edited interactively by the user. There are two views of the
 * graph: one which has an automatic layout algorithm applied each
 * time a new node is added, and one which uses a random or
 * user-driven layout. <p>
 *
 * The interaction and display in the graph editor, although currently
 * fairly simple, uses the features of the Diva canvas to good
 * effect. The use of two views of the graph highlights the
 * Swing-style model-view-controller architecture of the graph
 * package.
 *
 * @author Michael Shilman  (michaels@eecs.berkeley.edu)
 * @version $Revision: 1.1 $
 * @rating Red
 *
 * $Id: display.java,v 1.1 2003-05-20 15:23:52 tom Exp $
 *
 * Copyright (c) 1998 The Regents of the University of California.
 * All rights reserved.  See the file COPYRIGHT for details.
 */

public class display
{

   // The graph we are displaying.
   public static samJGraph theGraph;

   // The current component being displayed
   public static contractOwnerBase displayedComponent;

   /**
     * The global layout that is applied to the graph when the
     * "layout" button is pressed.
     */
    private GlobalLayout _staticLayout = new GridAnnealingLayout();

    /**
     * The two graph panes that are shown.
     */
    public static GraphPane thePane;


    /**
     * A pointer to the graph pane.
     */
    private LayoutTarget _target = null;

    /**
     * A label to show layout errors.
     */
//    private JLabel _lbl;

   // Remember our graph controller.
   static private samGraphController theGraphController;

   // Report our controller.
   static public samGraphController getGraphController()
   {
      return theGraphController;
   }

    /**
     * Call the layout on the graph when the "layout" button is
     * pressed.
     */
    private class LayoutAction extends AbstractAction
    {

        public LayoutAction()
        {
            super("Layout");
        }

        public void actionPerformed(ActionEvent e)
        {
            try
            {
                _staticLayout.layout(_target, theGraph.getGraphModel().getGraph() );
                showStatus("Okay.");
            }
            catch(Exception ex)
            {
                ex.printStackTrace();
                showStatus(ex.getMessage());
            }
        }

        public void showError(String s)
        {
            System.err.println("ERROR: " + s);
        }
    }

   public display(samJGraph theJGraph)
   {
      // Construct the widgets

      theGraph = theJGraph;
      thePane = (GraphPane)theGraph.getCanvasPane();
      _target = thePane.getGraphView();

      // Attach our graph controller to the view.
      // Creating the samJGraph also creates a samGraphController.
      theGraphController = (samGraphController)thePane.getGraphController();

//      theGraphController = new samGraphController();
//      thePane.getGraphView().setGraphController(theGraphController);

      theGraphController.setConnectorTarget(new samTarget());
/*
      //Initialize static layout test
      JPanel btns = new JPanel();
      btns.setLayout(new GridLayout(1, 3));

      JButton lay = new JButton("Static Layout");
      lay.addActionListener(new LayoutAction());
      btns.add(lay);

      _lbl = new JLabel("Status: ");
      JPanel panel = new JPanel();
      panel.setLayout(new GridLayout(2, 1));
      panel.add(btns);
      panel.add(_lbl);
      contentPane.add("South", panel);

*/
      // Set display features on the graph view.
      GraphView view = thePane.getGraphView();

      // Set the node renderer.
      samRenderer nodeRender = new samRenderer();
      view.setNodeRenderer(nodeRender);

      // Set the edge renderer.
      samEdgeRenderer edgeRender = new samEdgeRenderer();
      view.setEdgeRenderer(edgeRender);

      // Set the percentage of the pane that should be used for layout.
      view.setLayoutPercentage(1.0);




      // Configure the layout engine.
/*
      GridAnnealingLayout le = new GridAnnealingLayout();
      le.setIterationCount(100);
*/
      samLayout le = new samLayout();
      le.setOrientation(samLayout.HORIZONTAL);

      // Remember our layout engine.
      _staticLayout = le;



    // private GlobalLayout _staticLayout = new RandomLayout();
   }

   public void layout()
   {

            try {
                _staticLayout.layout(_target, theGraph.getGraphModel().getGraph());
                showStatus("Okay.");
            }
            catch(Exception ex) {
                ex.printStackTrace();
                showStatus(ex.getMessage());
            }


   }

    private void showStatus(String s) {
//        _lbl.setText("Status: " + s);
    }

    private Shape makePoly(int num, double r) {
        GeneralPath p = new GeneralPath();
        p.moveTo((float)r,0);
        for(int i = 1; i < num; i++) {
            double theta = i*Math.PI*2.0/num;
            double x = r*Math.cos(theta);
            double y = r*Math.sin(theta);
            p.lineTo((float)x, (float)y);
        }
        p.closePath();
        return p;
    }


    /**
     * Debugging output to standard err.
     */
    private void debug(String s) {
	System.err.println(s);
    }

}






