

package sam.display;

import diva.canvas.Site;
import diva.canvas.AbstractSite;
import diva.canvas.Figure;
import diva.canvas.manipulator.Geometry;

import java.util.Iterator;
import java.util.Vector;
import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.awt.Shape;


/** BoundsGeometry is a class that provides support for manipulating
 * the bounds of a figure.
 *
 * @version	$Revision: 1.1 $
 * @author 	John Reekie
 * $Id: samGeometry.java,v 1.1 2003-05-20 15:23:52 tom Exp $
 *
 * Copyright (c) 1998 The Regents of the University of California.
 * All rights reserved.  See the file COPYRIGHT for details.
 *
 */

public class samGeometry implements Geometry
{
   /// The figure to which the sites are attached
   private samBaseFigure parentFigure;

   /// The minimum size of the rectangle
   private double _minSize = 1.0;

   /// The defining rectangle
   private Rectangle2D _rect;

   /// The input sites.
   /// A vector of "inputSite" records.
   private Vector inputSites = new Vector();

   /// The output sites.
   /// A vector of "outputSite" records.
   private Vector outputSites = new Vector();

   /// Create a new geometry object on the given figure and with the given initial bounds.
   public samGeometry (samBaseFigure figure, Rectangle2D bounds, int numInputs, int numOutputs)
   {
      parentFigure = figure;
      setShape(bounds);

      // Create the sites
      for(int i=0; i<numInputs; i++)
      {
         inputSites.add(i, new inputSite(parentFigure, i) );
      }

      for(int i=0; i<numOutputs; i++)
      {
         outputSites.add(i, new outputSite(parentFigure, i) );
      }

//      System.err.println("In samGeometry constructor: _numOutputs=" + _numOutputs + " geometry=" + this);
   }

    /// Get the site for the specified input slot.
    public Site getInputSite (int slot)
    {
       // We return null if can't get a real site.
       Site rtn = null;

       try
       {
          rtn = (Site)inputSites.get(slot);
       }
       catch (ArrayIndexOutOfBoundsException e)
       {
          System.err.println("Internal Error: samGeometry::getInputSite(" + slot + ") threw exception: " + e.getMessage() );
       }

       return rtn;
    }

    /// Get the site for the specified output slot.
    public Site getOutputSite (int slot)
    {
       // We return null if can't get a real site.
       Site rtn = null;

       try
       {
          rtn = (Site)outputSites.get(slot);
       }
       catch (ArrayIndexOutOfBoundsException e)
       {
          System.err.println("Internal Error: samGeometry::getOutputSite(" + slot + ") threw exception: " + e.getMessage());
          e.printStackTrace();
       }

       return rtn;
    }

   /** Get the minimum size of the rectangle.
     */
    public double getMinimumSize () {
        return _minSize;
    }

   /// Get the figure to which this geometry object is attached.
   /// Returns null if there isn't one.
   public Figure getFigure()
   {
      return parentFigure;
   }

    /** Get the current shape that defines this geometry
     */
    public Shape getShape () {
        return _rect;
    }

    /** Get the current rectangle that defines this geometry. This
     * returns the same shape as getShape(), but as a Rectangle2D type.
     */
    public Rectangle2D getBounds () {
        return _rect;
    }

    /** Set the minimum size of the rectangle. The default is 1.0.
     */
    public void setMinimumSize (double minimumSize) {
        _minSize = minimumSize;
    }

     /** Set the shape that defines this geometry object.
      * The shape must be a Rectangle2D, or an exception
      * will be thrown.
     */
    public void setShape (Shape shape)
    {
        if ( !(shape instanceof Rectangle2D))
        {
            throw new IllegalArgumentException("Argument must be a Rectangle2D");
        }

        // Important: make a copy of it
        _rect = (Rectangle2D) ((Rectangle2D) shape).clone();
    }

     /** Set the rectangle that defines this geometry object.
      * This is the same as setShape(), but does not need to
      * perform the type check.
     */
    public void setBounds (Rectangle2D rect) {
        // Important: make a copy of it
        _rect = (Rectangle2D) rect.clone();
    }

    /** Return an iteration over the sites in this geometry object.
     */
    public Iterator inputSites ()
    {
       return inputSites.iterator();
    }

    /** Return an iteration over the sites in this geometry object.
     */
    public Iterator outputSites ()
    {
       return outputSites.iterator();
    }

    /** Translate the geometry object
     */
    public void translate (double x, double y) {
        _rect.setFrame(_rect.getX()+x, _rect.getY()+y,
                _rect.getWidth(), _rect.getHeight());
    }

   /// Get the number of inputs to this figure
   public int getNumInputs()
   {
      return inputSites.size();
   }

   /// Get the number of inputs to this figure
   public int getNumOutputs()
   {
      return outputSites.size();
   }
}
