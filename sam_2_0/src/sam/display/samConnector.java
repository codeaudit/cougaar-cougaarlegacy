package sam.display;

import diva.canvas.CanvasUtilities;
import diva.canvas.Figure;
import diva.canvas.Site;
import diva.canvas.TransformContext;
import diva.canvas.connector.AbstractConnector;
import diva.canvas.toolbox.PaintedShape;
import diva.canvas.connector.ConnectorEnd;

import java.awt.Shape;
import java.awt.geom.AffineTransform;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.awt.Graphics2D;
import java.awt.Color;

/** A Connector that draws itself in a straight line. The connector
 * uses an instance of PaintedShape to draw itself, so see that class
 * for a more detailed description of the paint- and stroke-related
 * methods.
 */
public class samConnector extends AbstractConnector
{
   final float lightGreyHSB[] = {0.0f, 0.0f, 0.9f};
   final Color hideColor = Color.getHSBColor(lightGreyHSB[0], lightGreyHSB[1], lightGreyHSB[2]);

    /** Create a new sam connector between the given
     * sites. The connector is drawn with a width of one
     * and in black.
     */
    public samConnector (Site tail, Site head)
    {
        super(tail, head);
        getPaintedShape().shape = new Line2D.Double();
        route();
    }

    /** Tell the connector to route itself between the
     * current positions of the head and tail sites.
     */
    public void route ()
    {
        repaint();

        TransformContext currentContext = getTransformContext();
        Site headSite = getHeadSite();
        Site tailSite = getTailSite();
        Point2D headPt, tailPt;

        // Get the transformed head and tail points. Sometimes
        // people will call this before the connector is added
        // to a container, so deal with it
        if (currentContext != null) {
            tailPt = tailSite.getPoint(currentContext);
            headPt = headSite.getPoint(currentContext);
        } else {
            tailPt = tailSite.getPoint();
            headPt = headSite.getPoint();
        }

        // Figure out the centers of the attached figures
        Point2D tailCenter, headCenter;
        if (tailSite.getFigure() != null) {
            tailCenter = CanvasUtilities.getCenterPoint(tailSite.getFigure());
        } else {
            tailCenter = tailPt;
        }
        if (headSite.getFigure() != null) {
            headCenter = CanvasUtilities.getCenterPoint(headSite.getFigure());
        } else {
            headCenter = headPt;
        }

        // Figure out the normal at the line ends. NOTE that we
        // use upside-down geometry, so that pi/2 points SOUTH.
        double x = headCenter.getX() - tailCenter.getX();
        double y = headCenter.getY() - tailCenter.getY();
        double angle = Math.atan2(y, x);

        // Tell the sites to adjust their positions
        tailSite.setNormal(angle);
        headSite.setNormal(angle - Math.PI);

        // Recompute the head and tail points
        if (currentContext != null) {
            tailPt = tailSite.getPoint(currentContext);
            headPt = headSite.getPoint(currentContext);
        } else {
            tailPt = tailSite.getPoint();
            headPt = headSite.getPoint();
        }

        // Figure out the normal again
        x = headPt.getX() - tailPt.getX();
        y = headPt.getY() - tailPt.getY();
        angle = Math.atan2(y, x);

        // Adjust for decorations on the ends
        if (getHeadEnd() != null) {
            getHeadEnd().setNormal(angle+Math.PI);
            getHeadEnd().setOrigin(headPt.getX(), headPt.getY());
            getHeadEnd().getConnection(headPt);
        }
        if (getTailEnd() != null) {
            getTailEnd().setNormal(angle);
            getTailEnd().setOrigin(tailPt.getX(), tailPt.getY());
            getTailEnd().getConnection(tailPt);
        }
        // Change the line shape
        ((Line2D)getPaintedShape().shape).setLine(tailPt, headPt);
        repaint();
    }

    /** Translate the connector. This method is implemented, since
     * controllers may wish to translate connectors when the
     * sites at both ends are moved the same distance.
     */
    public void translate (double x, double y) {
        repaint();
        Line2D line = (Line2D) getPaintedShape().shape;
        line.setLine(
                line.getX1()+x, line.getY1()+y,
                line.getX2()+x, line.getY2()+y);
        repaint();
    }

   /////////////////////////////////////////////////////////////////////
   // Paint the connector.
   // This call is forwarded to the internal PaintedShape object,
   // and then the connector ends are drawn, if they exist.
   //
   public void paint (Graphics2D g)
   {
      // Set the draw color, based on rather this edge is hidden or not.
      samBaseSite headSite = (samBaseSite)getHeadSite();
      samBaseSite tailSite = (samBaseSite)getTailSite();

      Color color = Color.black;
      if( (tailSite != null && tailSite.isHidden()) ||
          (headSite != null && headSite.isHidden()) )
      {
         color = hideColor;
      }

      // Paint the line in the desired color.
      PaintedShape line = getPaintedShape();
      line.outlinePaint = color;
      line.paint(g);

      // Paint the arrow head
      ConnectorEnd ce = getHeadEnd();
      if ( ce != null)
      {
         ce.paint(g);
      }

      // Paint the tail.
      ConnectorEnd ct = getTailEnd();
      if (ct != null)
      {
         ct.paint(g);
      }
   }
}
