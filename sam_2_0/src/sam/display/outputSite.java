

package sam.display;

import diva.canvas.Site;
import diva.canvas.AbstractSite;
import diva.canvas.Figure;
import diva.canvas.Figure;
import diva.canvas.manipulator.BoundsGeometry;

import sam.display.samBaseFigure;

import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.awt.Shape;

import sam.InvalidParmException;

import java.util.Iterator;
import java.util. Enumeration;
import javax.swing.SwingConstants;

/** outputSite is the local class that implements
 * editable sites of Rectangle objects.
 *
 * $Id: outputSite.java,v 1.1 2003-05-20 15:23:52 tom Exp $
 *
 * Copyright (c) 1998 The Regents of the University of California.
 * All rights reserved.  See the file COPYRIGHT for details.
 *
 */

public class outputSite  extends samBaseSite
{
   /// Create a new site with the given ID
   outputSite(samBaseFigure parent, int theSlot)
   {
      super(parent, theSlot, false, false);
   }

   /// Get the normal to this site in radians between zero and 2pi.
   public double getNormal ()
   {
      // For outputs, it is always 0.
      return 0.0;
   }

   /// Get the x-coordinate of the site, in the local coordinates of the containing pane.
   public double getX ()
   {
      // Just the right edge of the parent.
      double x = parentFigure.getBounds().getX() + parentFigure.getBounds().getWidth();

//      System.out.println("outputSite " + slot + " of " + parentFigure.getNumInputs() + ": x=" + x);

      return x;
   }

   /// Get the y-coordinate of the site, in the local coordinates of the containing pane.
   public double getY ()
   {
      // This one is a bit tricky.
      // It depends on how many outputs there are on the figure and our position.
      Rectangle2D rec = parentFigure.getBounds();

      double base = rec.getY();

      int numOutputs = parentFigure.getNumOutputs();

      // Safety check
      if( numOutputs <= 0 )
         numOutputs = 1;

      double step = rec.getHeight()/numOutputs;
      double y = base + step*getSlot() + step/2;

//      System.out.println("outputSite " + slot + " of " + numOutputs + ": y=" + y);
      return y;
   }

   // Test if this site has a normal in the given direction.
   public boolean isNormal (int direction)
   {
      return direction == SwingConstants.EAST;
   }

}
