

package sam.display;

import diva.canvas.Site;
import diva.canvas.AbstractSite;
import diva.canvas.Figure;
import diva.canvas.Figure;
import diva.canvas.manipulator.BoundsGeometry;

import sam.display.samBaseFigure;

import java.util.Iterator;
import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.awt.Shape;
import javax.swing.SwingConstants;


/** BoundsSite is the local class that implements
 * editable sites of Rectangle objects.
 *
 * $Id: inputSite.java,v 1.1 2003-05-20 15:23:52 tom Exp $
 *
 * Copyright (c) 1998 The Regents of the University of California.
 * All rights reserved.  See the file COPYRIGHT for details.
 *
 */

public class inputSite extends samBaseSite
{
   /// Create a new site with the given ID
   /**
    * Creates a new site with the given ID.<p>
    */
   inputSite(samBaseFigure parent, int theSlot)
   {
      super(parent, theSlot, true, true);
   }

   /// Get the normal to this site in radians between zero and 2pi.
   /**
    * Returns the normal to this site in radians between Zero and 2pi as a double.<p>
    *
    */
   public double getNormal ()
   {
      // For inputs, it is always PI.
      return Math.PI;
   }

   /// Get the x-coordinate of the site, in the local coordinates of the containing pane.
   /**
    * Returns the x-coordinate of the site as a double value.<p>
    */
   public double getX ()
   {
      // Just the left edge of the parent.
      double x = getFigure().getBounds().getX();

//      System.out.println("site " + slot + " of " + parentFigure.getNumInputs() + ": x=" + x);

      return x;
   }

   /// Get the y-coordinate of the site, in the local coordinates of the containing pane.
   /**
    * Returns the y-coordinate of the site as a double value.<p>
    */
   public double getY ()
   {
      // This one is a bit tricky.
      // It depends on how many inputs there are on the figure and our position.
      Rectangle2D rec = getFigure().getBounds();

      double base = rec.getY();

      int numInputs = parentFigure.getNumInputs();

      // Safety check
      if( numInputs <= 0 )
         numInputs = 1;

      double step = rec.getHeight()/numInputs;
      double y = base + step*getSlot() + step/2;

//      System.out.println("site " + slot + " of " + numInputs + ": y=" + y);
      return y;
   }

   // Test if this site has a normal in the given direction.
   /**
    * Returns true if the site is normal in the given direction else returns false.<p>
    * @param direction integer value of the direction.<p>
    */
   public boolean isNormal (int direction)
   {
      return direction == SwingConstants.WEST;
   }

}
