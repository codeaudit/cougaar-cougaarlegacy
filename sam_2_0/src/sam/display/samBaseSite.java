

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

import org.cougaar.core.util.Operator;


/// The base class for inputSite and outputSite.
/*
 * $Id: samBaseSite.java,v 1.1 2003-05-20 15:23:52 tom Exp $
 *
 * Copyright (c) 1998 The Regents of the University of California.
 * All rights reserved.  See the file COPYRIGHT for details.
 *
 */

public abstract class samBaseSite extends AbstractSite
{
   // The parent figure
   protected samBaseFigure parentFigure;

   // Are we an input slot?
   private boolean weAreInput;

   //Are we Highlighted
   private boolean weAreHighlighted;


   // our slot number.
   // Slots run from 0 to parentFigure.numSlots() - 1.
   // 0 is located at minimum Y.
   private int slot;

   /// Create a new site with the given ID
   samBaseSite(samBaseFigure parent, int theSlot, boolean areInput,boolean areHighlighted)
   {
      parentFigure = parent;
      slot = theSlot;
      weAreInput = areInput;
      weAreHighlighted = areHighlighted;
    }

   /// Get the ID of this site.
   public int getID()
   {
      return slot;
   }

   /// Get the slot number of this site.
   public int getSlot()
   {
      return slot;
   }

   /// Get the figure to which this site is attached, or null if it is not attached.
   public Figure getFigure()
   {
      return parentFigure;
   }

   /// Get the normal to this site in radians between zero and 2pi.
   public abstract double getNormal();

   /// Get the point location of the site.
   public Point2D getPoint ()
   {
      return new Point2D.Double(getX(), getY());
   }

   /// Get the x-coordinate of the site, in the local coordinates of the containing pane.
   public abstract double getX();

   /// Get the y-coordinate of the site, in the local coordinates of the containing pane.
   public abstract double getY();

   /// Test if this site has a "normal" to it. Returns true.
   public boolean hasNormal ()
   {
      return true;
   }

   /// Test if this site has a normal in the given direction.
   public abstract boolean isNormal (int direction);

   /// Return this site's contract as an XML string
   public String getContract()
   {
      // Return our site's contract as an XML string
      return parentFigure.getContract(weAreInput, slot);
   }

   /// Return this site's contract
   public Operator getContractOperator()
   {
      // Return our site's contract
      return parentFigure.getContractOperator(weAreInput, slot);
   }

  ////////////////////////////////////////////////// Highlight this site.///////////////////////////////////////////
   public void highlightSite()
   {
      //areHighlighted = true;
      parentFigure.baseRecord.contract.highlightSlot(weAreHighlighted, slot);
   }

   /// remove highlighting for this site.
   public void normalSite()
   {
      //areHighlighted = false;
      parentFigure.baseRecord.contract.normalSlot(weAreHighlighted, slot);
   }

   /// Is this site highlighted?
   public boolean isHighlighted()
   {
      //return areHighlighted;
      return parentFigure.baseRecord.contract.isSlotHighlighted(weAreHighlighted, slot);
   }

///////////////////////////hide the sites///////////////////////////////////////////////////////////////////
   public void hideSite()
   {
      parentFigure.baseRecord.contract.hideSlot(weAreInput, slot);
   }

   //do not hide the site
   public void showSite()
   {
      parentFigure.baseRecord.contract.showSlot(weAreInput, slot);
   }

   //is this site hidden?
   public boolean isHidden()
   {
      return parentFigure.baseRecord.contract.isSlotHidden(weAreInput, slot);
   }

   //is this site an input?
   public boolean isInput()
   {
      return weAreInput;
   }

}
