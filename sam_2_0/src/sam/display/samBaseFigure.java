/* samBaseFigure.java  */

package sam.display;

import sam.LoadContractFiles.contractOwnerBase;

import diva.canvas.toolbox.BasicFigure;
import diva.canvas.Site;
import diva.canvas.event.EventAcceptor;
import diva.canvas.event.LayerEvent;

import sam.display.samBaseSite;
import sam.MainWindow;
import org.cougaar.core.util.Operator;
import javax.swing.*;


import java.awt.*;


import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Shape;
import java.awt.geom.AffineTransform;
import java.awt.geom.Rectangle2D;
import java.awt.event.MouseEvent;

import java.util.Iterator;

/** A pluginFigure is a graphical representation of the plugin
 *  with one input site on the left side for each subscription
 *  and one output site on the right side for each publication.
 *
 *  It is currently rendered as a rectangle.
 */
public class samBaseFigure extends BasicFigure implements EventAcceptor
{
   private final int SiteHeight = 12;
   private final int SiteWidth  = 12;
   private final int HighlightHeight = 24;
   private final int HighlightWidth  = 24;
   private final int HighlightOffset = HighlightWidth - SiteWidth;

   private final Color SiteGoodColor = (Color.green).darker();
   private final Color SiteBadColor = Color.red;
   private final Color SiteHighlightColor = Color.yellow;
   private final Color SiteHiddenColor = Color.lightGray;

    /** The enabled flag.
     */
    private boolean _enabled = true;

    /** The consuming flag.
     */
    private boolean _consuming = true;


   // The text we print in the figure to identify it.
   String displayName;

   //to hold the name of the component
   JTextField nameBox = new JTextField();

   // The geometry object
   private samGeometry geometry;

   // Our corresponding contract record
   contractOwnerBase baseRecord;


   // A private class holding a triangle's points
   private class triangle
   {
      public int xList[] = new int[3];
      public int yList[] = new int[3];
      int numPoints = 3;

      final double xOffset = 1.5;

      public triangle()
      {
      }

      public void loadInput(double tipX, double tipY)
      {
         xList[0] = (int)(tipX + SiteWidth + xOffset);
         xList[1] = (int)(tipX + xOffset);
         xList[2] = (int)(tipX + xOffset);

         yList[0] = (int)(tipY);
         yList[1] = (int)(tipY+SiteHeight/2);
         yList[2] = (int)(tipY-SiteHeight/2);
      }

      public void loadOutput(double tipX, double tipY)
      {
         xList[0] = (int)(tipX - xOffset);
         xList[1] = (int)(tipX - SiteWidth - xOffset);
         xList[2] = (int)(tipX - SiteWidth - xOffset);

         yList[0] = (int)(tipY);
         yList[1] = (int)(tipY+SiteHeight/2);
         yList[2] = (int)(tipY-SiteHeight/2);
      }

      public void loadInputHighlight(double tipX, double tipY)
      {
         xList[0] = (int)(tipX + HighlightWidth + xOffset);
         xList[1] = (int)(tipX + xOffset);
         xList[2] = (int)(tipX + xOffset);

         yList[0] = (int)(tipY);
         yList[1] = (int)(tipY+HighlightHeight/2);
         yList[2] = (int)(tipY-HighlightHeight/2);
      }

      public void loadOutputHighlight(double tipX, double tipY)
      {
         xList[0] = (int)(tipX - xOffset);
         xList[1] = (int)(tipX - HighlightWidth - xOffset);
         xList[2] = (int)(tipX - HighlightWidth - xOffset);

         yList[0] = (int)(tipY);
         yList[1] = (int)(tipY+HighlightHeight/2);
         yList[2] = (int)(tipY-HighlightHeight/2);
      }
   }

   // Create a new figure with the given shape, s unit-width continuous black outline and no fill.
   public samBaseFigure (Shape shape, String nodeName,int numInputs, int numOutputs, contractOwnerBase rec)
   {
      super(shape);

      // Save the state info.
      displayName = nodeName;
     // nameBox.setText(nodeName);
    //  System.out.println(" the value of node name is : " + nodeName);
     // System.out.println(" the value of textfield is: " + nameBox.getText().toString());
      baseRecord = rec;

      // Remember the outline of this shape.
      geometry = new samGeometry(this, getBounds(), numInputs, numOutputs);

      if( geometry == null )
         System.err.println("Internal Error: samBaseFigure::constructor got null geometery record");
   }


   /// Translate the figure the given distance
   public void translate (double dx, double dy)
   {
      Shape s = getShape();
      if (s instanceof Rectangle2D)
      {
         Rectangle2D r = (Rectangle2D)s;
         repaint();
         double newX = r.getX() + dx;
         double newY = r.getY() + dy;

         r.setFrame(newX, newY, r.getWidth(), r.getHeight());
         repaint();

      }
      else
      {
         super.translate(dx,dy);
      }

      // Update the sites.
      geometry.translate(dx,dy);
   }


   /// Override the paint method so we can write the name.
   public void paint (Graphics2D g)
   {
      // Duck out if we are not visible
      if (!isVisible())
      {
         return;
      }

      // Get the super class to draw the basic shape
      super.paint(g);

       // Write the text
      Rectangle2D rec = getBounds();
      float x = (float)(rec.getX() + 5.0);
      float y = (float)(rec.getY() + rec.getHeight()/2);

      g.setFont(new Font(displayName,Font.PLAIN, 14));
      g.setColor(Color.black);
      g.drawString(displayName, x, y);

      // Draw the input sites.
      Iterator it = geometry.inputSites();
      while( it.hasNext() )
      {
         inputSite site = (inputSite)it.next();

         // If the site is highlighted, draw the highlight first.
         if( site.isHighlighted() )
         {
            g.setColor( SiteHighlightColor );
            triangle tri = new triangle();
            tri.loadInputHighlight(site.getX(), site.getY());
            g.fillPolygon(tri.xList, tri.yList,tri.numPoints);

         }

         // Set the color
         if( site.isHidden() )
            g.setColor( SiteHiddenColor);
         else if( baseRecord.isInputSlotConnected(site.getSlot()) )
            g.setColor( SiteGoodColor );
         else
            g.setColor( SiteBadColor );

         // Draw the shape.
         triangle tri = new triangle();
         tri.loadInput(site.getX(), site.getY());
         g.fillPolygon(tri.xList, tri.yList,tri.numPoints);
      }

      // Draw the output sites.
      it = geometry.outputSites();
      while( it.hasNext() )
      {
         outputSite site = (outputSite)it.next();

         double Px = site.getX();
         double Py = site.getY();

         // If the site is highlighted, draw the highlight first.
         if( site.isHighlighted() )
         {
            g.setColor( SiteHighlightColor );
            triangle tri = new triangle();
            tri.loadOutputHighlight(site.getX(), site.getY());
            g.fillPolygon(tri.xList, tri.yList,tri.numPoints);

            // Bump over to the left, if are highlighting
            Px -= HighlightOffset;
         }

         // Set the color
         if( site.isHidden() )
            g.setColor( SiteHiddenColor);
         else if( baseRecord.isOutputSlotConnected(site.getSlot()) )
            g.setColor( SiteGoodColor );
         else
            g.setColor( SiteBadColor );

         // Draw the shape.
         triangle tri = new triangle();
         tri.loadOutput(Px, Py);
         g.fillPolygon(tri.xList, tri.yList,tri.numPoints);
      }
  }

   /// Update the geometry
   public void transform (AffineTransform at)
   {
      super.transform(at);
      geometry.setShape(getShape());
   }

   /// Get the site for the specified input slot.
   public Site getInputSite (int slot)
   {
      return geometry.getInputSite(slot);
   }

   /// Get the site for the specified output slot.
   public Site getOutputSite (int slot)
   {
      return geometry.getOutputSite(slot);
   }

   /// Get the number of inputs to this figure
   public int getNumInputs()
   {
      return geometry.getNumInputs();
   }

   /// Get the number of inputs to this figure
   public int getNumOutputs()
   {
      return geometry.getNumOutputs();
   }

   /// Check if the mouse click location is on one of our sites.
   /// If so, return the site, else null;
   public samBaseSite checkSiteHit(double x, double y)
   {
      // Check the input sites.
      Iterator it = geometry.inputSites();
      while( it.hasNext() )
      {
         samBaseSite site = (samBaseSite)it.next();

         // Compute the shape.
         triangle tri = new triangle();
         tri.loadInput(site.getX(), site.getY());

         // Check if the point is within the enclosing rectangle.
         // Should fix this to be the polygon!!!
         if( x <= tri.xList[0] && x >= tri.xList[1] &&
             y <= tri.yList[1] && y >= tri.yList[2] )
         {
            return site;
         }
      }

      // Check the output sites.
      it = geometry.outputSites();
      while( it.hasNext() )
      {
         samBaseSite site = (samBaseSite)it.next();

         // Compute the shape.
         triangle tri = new triangle();
         tri.loadOutput(site.getX(), site.getY());

         // Check if the point is within the enclosing rectangle.
         // Should fix this to be the polygon!!!
         if( x <= tri.xList[0] && x >= tri.xList[1] &&
             y <= tri.yList[1] && y >= tri.yList[2] )
         {
            return site;
         }
      }

      // Nope.
      return null;
   }

   // Return the specified site's contract as an XML string
   public String getContract(boolean isInput, int slot)
   {
      return baseRecord.getContract(isInput, slot);
   }

 // Return the specified site's contract
   public Operator getContractOperator(boolean isInput, int slot)
   {
      return baseRecord.getContractOperator(isInput, slot);
   }


    /** Handle our own AWT events.
     *  Used to drill down into components based on double clicks or button 2 clicks.
     */

    public void dispatchEvent (AWTEvent event)
    {
       LayerEvent e = (LayerEvent)event;

       // Is it our double click event?
       if (e.getID() == MouseEvent.MOUSE_CLICKED)
       {
         if( baseRecord.hasGraph() )
         {
            // clear any outstanding selections.
            ((samGraphController)MainWindow.theMainWindow.theSociety.thePane.getGraphController()).clearSelections(false);

            // Update the history record
            MainWindow.theMainWindow.backandforward.addtoHistory( baseRecord );

            // Render the new graph
            baseRecord.renderGraph( MainWindow.theMainWindow.theSociety.theJGraph );
          }

          // The buck stops here.
          e.consume();
       }
    }

    /** Set the enabled flag of this layer. If the flag is false,
     * then the layer will not respond to user input events.
     */
    public void setEnabled (boolean flag) {
        _enabled = flag;
    }
    /** Test the enabled flag of this layer. Note that this flag
     *  does not indicate whether the layer is actually enabled,
     * as its pane or one if its ancestors may not be enabled.
     */
    public boolean isEnabled () {
        return _enabled;
    }
}

