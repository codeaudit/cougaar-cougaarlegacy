
package sam;

import diva.graph.model.Node;

/**
 * Title:        Sam<p>
 * Description:  ALP Business Process User Interface<p>
 * <copyright>
 *    Copyright (c) 2000-2001 Defense Advanced Research Projects
 *    Agency (DARPA) and Mobile Intelligence Corporation.
 *    This software to be used only in accordance with the
 *    COUGAAR license agreement.
 * </copyright>
 * Company:      Mobile Intelligence Corp.<p>
 * @author Doug MacKenzie
 * @version 1.0
 */
public class EdgeData
{
   // The head of the edge.
   public Node srcNode;
   public int  srcSlot;

   // The tail of the edge.
   public Node dstNode;
   public int  dstSlot;

   public EdgeData()
   {
   }

   /**
    * Returns the data as a String.<p>
    */
   public String toString()
   {
      return "EdgeData[" + srcNode + ", srcSlot[" + srcSlot + "], " +
                           dstNode + ", dstSlot[" + dstSlot + "]";
   }

}