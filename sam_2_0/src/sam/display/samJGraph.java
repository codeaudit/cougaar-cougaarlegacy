

package sam.display;

import sam.display.samGraphController;
import diva.graph.*;
import diva.graph.model.*;
import diva.canvas.*;
import diva.canvas.event.*;
import java.awt.*;
import javax.swing.*;
import java.awt.geom.*;

/**
 * A graph widget analagous to java.swing.JTree.
 * JGraph contains a GraphModel and can be customized
 * to render it in an application-specific way through
 * it's GraphPane member variable.
 *
 * @see GraphModel
 * @author 	Michael Shilman (michaels@eecs.berkeley.edu)
 * @version	$Revision: 1.1 $
 * $Id: samJGraph.java,v 1.1 2003-05-20 15:23:52 tom Exp $
 *
 * Copyright (c) 1998 The Regents of the University of California.
 * All rights reserved.  See the file COPYRIGHT for details.
 */

public class samJGraph extends JCanvas
{
    /**
     * Construct a new JGraph with one of our controllers.
     */
    public samJGraph()
    {
      super(new GraphPane( new GraphModel(), new samGraphController() ) );
    }


    /**
     * Return the graph model being viewed.
     */
    public GraphModel getGraphModel() {
        return getGraphPane().getGraphModel();
    }

    /**
     * Set the graph model that is being viewed.
     */
    public void setGraphModel(GraphModel m)
    {
        getGraphPane().setGraphModel(m);
    }

    /**
     * Return the canvas pane, which is of type
     * GraphPane.
     */
    public GraphPane getGraphPane() {
        return (GraphPane)getCanvasPane();
    }

    /**
     * Set the graph pane of this widget.
     */
    public void setGraphPane(GraphPane p) {
        setCanvasPane(p);
    }

    /**
     * Set the canvas pane, but force it to be of type
     * GraphPane.  Throws an illegal argument exception if the canvas
     * pane is anything else. <p>
     *
     * FIXME: Should this throw an exception any time it's called?
     */
    public void setCanvasPane(CanvasPane cp) {
        if(cp instanceof GraphPane) {
            super.setCanvasPane(cp);
        }
        else {
            String err = "samJGraph can only except display panes of type GraphPane.";
            throw (new IllegalArgumentException(err));
        }
    }
}
