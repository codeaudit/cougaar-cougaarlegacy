
package sam.display;

import diva.graph.model.Edge;
import diva.canvas.Site;
import diva.canvas.connector.Connector;
//import diva.canvas.connector.StraightConnector;
//import diva.canvas.connector.ManhattanConnector;
import diva.canvas.connector.Arrowhead;
import diva.graph.EdgeRenderer;

import diva.canvas.connector.Blob;

import java.awt.Color;

/**
 * A sam specific implementation of the EdgeRenderer interface.
 * This renderer creates straight-line connectors with
 * an arrow at the head.
 *
 * $Id: samEdgeRenderer.java,v 1.1 2003-05-20 15:23:52 tom Exp $
 *
 * Copyright (c) 1998 The Regents of the University of California.
 * All rights reserved.  See the file COPYRIGHT for details.
 *
 * @author Doug MacKenzie
 * @version $Revision: 1.1 $
 */
public class samEdgeRenderer implements EdgeRenderer
{

   /// Render a visual representation of the given edge.
   public Connector render(Edge edge, Site tailSite, Site headSite)
   {
      // Pick a connector style.
      // Note: ManhattanConnector is not yet implemented.
//      StraightConnector c = new StraightConnector(tailSite, headSite);
      samConnector c = new samConnector(tailSite, headSite);
//    ManhattanConnector c = new ManhattanConnector(tailSite, headSite);

      // Put an arrow head on the end.
      Arrowhead arrow = new Arrowhead( headSite.getX(), headSite.getY(), headSite.getNormal() );
      c.setHeadEnd(arrow);

      // Associate the edge with the connector.
      c.setUserObject(edge);

      // Return it.
      return c;
   }
}
