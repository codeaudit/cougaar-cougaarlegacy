
package sam.display;


import diva.canvas.connector.ConnectorTarget;
import diva.canvas.connector.Connector;

import diva.canvas.Figure;
import diva.canvas.Site;
import diva.util.Filter;
import sam.display.inputSite;

import java.util.HashMap;

/** A connector target that returns sites on the figures.
 *
 * @version $Revision: 1.1 $
 * @author Douglas MacKenzie
 * $Id: samTarget.java,v 1.1 2003-05-20 15:23:52 tom Exp $
 *
 * Copyright (c) 1998 The Regents of the University of California.
 * All rights reserved.  See the file COPYRIGHT for details.
 *
 */
public class samTarget implements ConnectorTarget {

    /** Accept a figure. A site will only be returned for figures that
     * are accepted by this method. By default, any figure will be accepted
     * except for Connectors.
     */
    public boolean accept (Figure f) {
        return !(f instanceof Connector);
    }

/*
   /// Return the nearest site on the figure if the figure is not a connector
   public Site getHeadSite (samBaseFigure f, int slot, double x, double y)
   {
      if (accept(f))
      {
	 return new inputSite(f, slot);
      }
      else
      {
         return null;
      }
   }

   /// Return the nearest site on the figure if the figure is not a connector
   public Site getTailSite (samBaseFigure f, int slot, double x, double y)
   {
      if (accept(f))
      {
	 return new outputSite(f, slot);
      }
      else
      {
         return null;
      }
   }
*/




///////////// Old calls /////////////
     /**
     * @deprecated
     */

   public Site getHeadSite (Figure f, double x, double y)
   {
      System.err.println("ERROR: Called unsupported getHeadSite in samTarget!");

      // Cause an exception.
      Site a = (Site)f;

      return null;
   }

     /**
     * @deprecated
     */
   public Site getTailSite (Figure f, double x, double y)
   {
      System.err.println("ERROR: Called unsupported getTailSite in samTarget!");

      // Cause an exception.
      Site a = (Site)f;

      return null;
   }





    /** Return the nearest site on the site's figure
         */
    public Site getHeadSite (Site s, double x, double y) {
	//  return getHeadSite(s.getFigure(), x, y);
	return s;
    }

    /** Return the nearest site on the site's figure
         */
    public Site getTailSite (Site s, double x, double y) {
	// return getHeadSite(s, x, y);
	return s;
    }
}
