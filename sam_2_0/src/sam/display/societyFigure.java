/* societyFigure.java  */

package sam.display;

import diva.canvas.Figure;
import diva.canvas.AbstractFigure;
import diva.canvas.CanvasUtilities;
import diva.canvas.manipulator.ShapedFigure;
import diva.canvas.toolbox.PaintedShape;

import java.awt.AlphaComposite;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Composite;
import java.awt.Graphics2D;
import java.awt.Paint;
import java.awt.Shape;
import java.awt.Stroke;
import java.awt.geom.AffineTransform;
import java.awt.geom.RectangularShape;
import java.awt.geom.Rectangle2D;

/** A societyFigure is one that contains a single instance of
 *  Shape. The figure can have a fill with optional compositing (for
 *  translucency), and a stroke with a different fill. With this
 *  class, simple objects can be created on-the-fly simply by passing
 *  an instance of java.awt.Shape to the constructor.
 *
 */
public class societyFigure extends AbstractFigure implements ShapedFigure {

     /** The painted shape that we use to draw the connector.
     */
    private PaintedShape _paintedShape;

    // The node's name
    String shortName;

    /** Create a new figure with the given shape. The figure, by
     *  default, has a unit-width continuous black outline and no fill.
     */
    public societyFigure (Shape shape, String nodeName)
    {
        super();
        _paintedShape = new PaintedShape(shape);
        _paintedShape.setStroke(new BasicStroke());
        _paintedShape.setLineWidth(1.0f);

        // Save just the last name after the final "."
        shortName = nodeName.substring(nodeName.lastIndexOf('.')+1);
    }

    /** Create a new figure with the given shape and outline width.
     * It has no fill. The default outline paint is black.
     */
    public societyFigure (Shape shape, float lineWidth) {
        super();
        _paintedShape = new PaintedShape(shape);
        _paintedShape.setStroke(new BasicStroke());
        _paintedShape.setLineWidth(lineWidth);
    }

    /** Create a new figure with the given paint pattern. The figure,
     *  by default, has no stroke.
     */
    public societyFigure (Shape shape, Paint fill) {
        super();
        _paintedShape = new PaintedShape(shape);
        _paintedShape.fillPaint = fill;
    }

    /** Create a new figure with the given paint pattern and outline width.
     * The default outline paint is black.
     */
    public societyFigure (Shape shape, Paint fill, float lineWidth) {
        super();
        _paintedShape = new PaintedShape(shape);
        _paintedShape.setStroke(new BasicStroke());
        _paintedShape.setLineWidth(lineWidth);
        _paintedShape.fillPaint = fill;
    }

    /** Get the bounding box of this figure. This method overrides
     * the inherited method to take account of the thickness of
     * the stroke, if there is one.
     */
    public Rectangle2D getBounds () {
        return _paintedShape.getBounds();
    }

    /** Get the color composition operator of this figure.
     */
    public Composite getComposite () {
        return _paintedShape.composite;
    }

    /** Get the fill paint pattern of this figure.
     */
    public Paint getFillPaint () {
        return _paintedShape.fillPaint;
    }

   /** Get the line width of this figure.
    */
    public float getLineWidth () {
      return _paintedShape.getLineWidth();
    }

    /** Get the shape of this figure.
     */
    public Shape getShape () {
        return _paintedShape.shape;
    }

    /** Get the stroke of this figure.
     */
    public Stroke getStroke () {
        return _paintedShape.getStroke();
    }

    /** Get the stroke paint pattern of this figure.
     */
    public Paint getStrokePaint () {
        return _paintedShape.outlinePaint;
    }

    /** Test if this figure intersects the given rectangle. If there
     * is a fill but no outline, then there is a hit if the shape
     * is intersected. If there is an outline but no fill, then the
     * area covered by the outline stroke is tested. If there
     * is both a fill and a stroke, the region bounded by the outside
     * edge of the stroke is tested. If there is neither a fill nor
     * a stroke, then return false. If the figure is not visible,
     * always return false.
     */
    public boolean hit (Rectangle2D r) {
        if (!isVisible()) {
             return false;
        }
        return _paintedShape.hit(r);
    }

    /** Paint the figure. The figure is redrawn with the current
     *  shape, fill, and outline.
     */
    public void paint (Graphics2D g)
    {
        if (!isVisible()) {
             return;
        }
        _paintedShape.paint(g);

        // Write the text
        Rectangle2D rec = _paintedShape.getBounds();
        float x = (float)rec.getX();
        float y = (float)(rec.getY() + rec.getHeight()/2);
        g.drawString(shortName, x, y);
    }

    /** Set the color composition operator of this figure. The
     *  compositioning applies to the fill only.
     */
    public void setComposite (Composite c) {
        _paintedShape.composite = c;
    }

   /** Set the dash array of this figure. If the current stroke
    * is an instance of BasicStroke, then its dash array will be
    * set to the passed array; otherwise, the old stroke will be
    * deleted and replaced with a new one with the current line
    * width.
    */
    public void setDashArray (float dashArray[]) {
        _paintedShape.setDashArray(dashArray);
        repaint();
    }

    /** Set the fill paint pattern of this figure. The figure will be
     *  filled with this paint pattern. If no pattern is given, do not
     *  fill it.
     */
    public void setFillPaint (Paint p) {
        _paintedShape.fillPaint = p;
	repaint();
    }

   /** Set the line width of this figure. If the current stroke
    * is an instance of BasicStroke, them its width will be
    * set to the passed width; otherwise, the old stroke will be
    * deleted and replaced with a new one with the given
    * line width. If the width is zero, then the stroke will
    * be removed.
    */
    public void setLineWidth (float lineWidth) {
      repaint();
      _paintedShape.setLineWidth(lineWidth);
      repaint();
    }

    /** Set the shape of this figure. We damage the region
     *  covered by the figure before changing the shape.
     *  The newly covered region will be marked damaged
     *  when the client calls repaint();
     */
    public void setShape (Shape s) {
        repaint();
        _paintedShape.shape = s;
        repaint();
    }

    /** Set the stroke of this figure.
     */
    public void setStroke (Stroke s) {
        repaint();
        _paintedShape.setStroke(s);
        repaint();
    }

    /** Set the stroke paint pattern of this figure.
     */
    public void setStrokePaint (Paint p) {
        _paintedShape.outlinePaint = p;
	repaint();
    }

    /** Transform the figure with the supplied transform. This can be
     * used to perform arbitrary translation, scaling, shearing, and
     * rotation operations. As much as possible, this method attempts
     * to preserve the type of the shape: if the shape of this figure
     * is an of RectangularShape or Polyline, then the shape may be
     * modified directly. Otherwise, a general transformation is used
     * that loses the type of the shape, converting it into a
     * GeneralPath.
     */
    public void transform (AffineTransform at) {
        repaint();
        _paintedShape.shape = CanvasUtilities.transform(
                _paintedShape.shape, at);
	repaint();
    }

    /** Translate the figure by the given distance.
     * As much as possible, this method attempts
     * to preserve the type of the shape: if the shape of this figure
     * is an of RectangularShape or Polyline, then the shape may be
     * modified directly. Otherwise, a general transformation is used
     * that loses the type of the shape, converting it into a
     * GeneralPath.
     */
    public void translate (double x, double y) {
	repaint();
        _paintedShape.shape = CanvasUtilities.translate(
                _paintedShape.shape, x, y);
	repaint();
    }
}
