/* samRenderer.java -- By Douglas MacKenzie */

package sam.display;

import diva.graph.model.CompositeNode;
import diva.graph.CompositeNodeFigure;
import diva.graph.model.Node;
import diva.graph.NodeRenderer;
import diva.canvas.Figure;
import diva.canvas.CompositeFigure;
import diva.canvas.toolbox.BasicFigure;
import sam.display.samBaseFigure;

import sam.LoadContractFiles.contractOwnerBase;

import sam.plugin;
import sam.cluster;
import sam.society;
import sam.world;
import sam.inputBar;
import sam.outputBar;
import sam.community;

import java.awt.Shape;
import java.awt.Paint;
import java.awt.Color;
import java.awt.geom.Rectangle2D;
import java.awt.geom.RectangularShape;
import java.awt.geom.GeneralPath;
import javax.swing.*;

/**
 * A factory which creates and returns a NodeFigure given a plugin node
 * to render.
 *
 */
public class samRenderer implements NodeRenderer
{
   // Some colors in the HSB color scheme.
   final float LightBlueHSB[] = {0.67f, 0.2f, 1.0f};
   final float MediumBlueHSB[] = {0.67f, 0.5f, 1.0f};


    /**
     * The shape for nodes.
     */
    private Shape _nodeShape = null;

    /**
     * The shape for composite nodes.
     */
    private Shape _compositeShape = null;

    /**
     * The scaling factor for composite nodes.
     *
     * @see setCompositeScale(double)
     */
    private double _compositeScale = 0;

    /**
     * The fill paint for nodes.
     */
    private Paint _nodeFill = null;

    /**
     * The fill paint for composite nodes.
     */
    private Paint _compositeFill = null;


   // Helper function to make polygon shapes
   private Shape makePoly(int num, double r)
   {
      GeneralPath p = new GeneralPath();

      p.moveTo((float)r,0);
      for(int i = 1; i < num; i++)
      {
         double theta = i*Math.PI*2.0/num;
         double x = r*Math.cos(theta);
         double y = r*Math.sin(theta);
         p.lineTo((float)x, (float)y);
      }
      p.closePath();

      return p;
   }



   /// Create a renderer which renders plugin nodes.
   public samRenderer()
   {
/*
      Shape nodeShape = new Rectangle2D.Double(0.0,0.0,40.0,40.0);
      Shape compositeShape = new Rectangle2D.Double(0.0,0.0,600.0,600.0);
      Paint nodeFill = Color.orange;
      Paint compositeFill = Color.red;
      double compositeScale = .3;

      setNodeShape(nodeShape);
      setNodeFill(nodeFill);
      setCompositeShape(compositeShape);
      setCompositeFill(compositeFill);
      setCompositeScale(compositeScale);
*/
   }

    /**
     * Return the fill that composites are painted with.
     */
    public Paint getCompositeFill() {
        return _compositeFill;
    }

    /**
     * Return the scaling factor for the composite nodes
     *
     * @see setCompositeScale(double)
     */
    public double getCompositeScale() {
        return _compositeScale;
    }

    /**
     * Return the shape that composites are rendered in.
     */
    public Shape getCompositeShape() {
        return _compositeShape;
    }

    /**
     * Return the fill that nodes are painted with.
     */
    public Paint getNodeFill() {
        return _nodeFill;
    }

    /**
     * Return the shape that nodes are rendered in.
     */
    public Shape getNodeShape() {
        return _nodeShape;
    }


   /// This is the only call into this class by the graph display package

   /// Return the rendered visual representation of this node.
   public Figure render(Node n)
   {
      // Get our base record.
      contractOwnerBase rec = (contractOwnerBase)n.getSemanticObject();

      // Get the data from the base record.
      String name = rec.getName();
      int inputs = rec.getNumInputs();
      int outputs = rec.getNumOutputs();

      // Switch on the type of record and build the correct shape.
      Shape nodeShape;
      Paint nodeColor;

      if( rec instanceof plugin )
      {
         /// Create a matching shape
         final double width  = 100;
         final double height = 40;
         nodeShape = new Rectangle2D.Double(0,0, width, height*Math.max(1,Math.max(inputs, outputs)) );

         // Set the color.
         nodeColor = rec.isDifferent() ? Color.yellow : Color.orange;
       }
      else if( rec instanceof inputBar )
      {
         /// Create a matching shape
         final double width  = 40;
         final double height = 40;
         nodeShape = new Rectangle2D.Double(0,0, width, height*Math.max(Math.max(1,Math.max(inputs, outputs)), 15));

         // Set the color.
         nodeColor = Color.getHSBColor(LightBlueHSB[0], LightBlueHSB[1], LightBlueHSB[2]);
      }
      else if( rec instanceof outputBar )
      {
         /// Create a matching shape
         final double width  = 40;
         final double height = 40;
         nodeShape = new Rectangle2D.Double(0,0, width, height*Math.max(Math.max(1,Math.max(inputs, outputs)), 15));

         // Set the color.
         nodeColor = Color.getHSBColor(LightBlueHSB[0], LightBlueHSB[1], LightBlueHSB[2]);
      }
      else if( rec instanceof cluster )
      {
         // Make the clusters green polygons.
         nodeShape = makePoly(8, 40);
         nodeColor = rec.isDifferent() ? Color.yellow : Color.green;

      }
      else if( rec instanceof society )
      {
        // Make the societies blue polygons.
        nodeShape = makePoly(16, 60);
        nodeColor = Color.getHSBColor(MediumBlueHSB[0], MediumBlueHSB[1], MediumBlueHSB[2]);
      }
      else if( rec instanceof community )
      {
        // Make the communities orange polygons.
        nodeShape = makePoly(8,40);
        nodeColor = Color.getHSBColor(MediumBlueHSB[0], MediumBlueHSB[1], MediumBlueHSB[2]);
      }
      else
      {
         System.err.println("Internal Error: samRenderer::render called with unknown record type - " + rec);
         nodeShape = new Rectangle2D.Double(0, 0, 40, 40);

         // Flag it by color.
         nodeColor = Color.red;
      }


      // Trim the name for displaying.  Just the last name after the final "."
      // Take care to include any parms such as yyy(xxxx.cfg)
      String displayName;
      int pos = name.lastIndexOf('(');
      if( pos > 0)
      {
         displayName = name.substring(name.lastIndexOf('.', pos)+1);
      }
      else
      {
         displayName = name.substring(name.lastIndexOf('.')+1);
         if( displayName.equals("ini") )
         {
            displayName = name.substring(0, name.lastIndexOf('.'));
         }
      }


      // Create the new plugin figure.
      samBaseFigure fig = new samBaseFigure(nodeShape, displayName,inputs, outputs, rec);

      // Set the color
      fig.setFillPaint(nodeColor);

      // Attach the node to the figure
      fig.setUserObject(n);

      // Attach the figure to the base record.
      rec.setFigure(fig);

      // return it.
      return fig;
    }



    /**
     * Set the fill to paint the composites with.
     */
    public void setCompositeFill(Paint p) {
        _compositeFill = p;
    }

    /**
     * Set the scaling factor for the composite nodes.
     * Given factor must be greater than 0 and less than
     * or equal to 1.
     *
     * (XXX document this).
     */
    public void setCompositeScale(double scale) {
        if((scale <= 0) || (scale > 1)) {
            String err = "Scale must be between > 0 and <= 1.";
            throw new IllegalArgumentException(err);
        }
        _compositeScale = scale;
    }

    /**
     * Set the shape for composites to be rendered in.  The
     * shape must implement Cloneable.
     */
    public void setCompositeShape(Shape s) {
        _compositeShape = s;
    }

    /**
     * Set the fill to paint the nodes with.
     */
    public void setNodeFill(Paint p) {
        _nodeFill = p;
    }

    /**
     * Set the shape for nodes to be rendered in.  The
     * shape must implement Cloneable.
     */
    public void setNodeShape(Shape s) {
        _nodeShape = s;
    }

}
