package sam.graphPlanner;
import java.util.List;
/**
 * Title:        Sam
 * Description:  ALP Business Process User Interface
 * <copyright>
 *    Copyright (c) 2000-2001 Defense Advanced Research Projects
 *    Agency (DARPA) and Mobile Intelligence Corporation.
 *    This software to be used only in accordance with the
 *    COUGAAR license agreement.
 * </copyright>
 * Company:      Mobile Intelligence Corp.
 * @author Doug MacKenzie
 * @version 1.0
 */
public interface graphLink
{
   // Is this link connected?
   /**
    * Returns true if the Link is connected.<p>
    */
   abstract public boolean isConnected();

   // Can this link be connected to the specified link?
   /**
    * Returns true if the Link can be coonected to a specified link.<p>
    * @param link graphLink.<p>
    */
   abstract public boolean isCompatible(graphLink link);

   // Are the two links equivalent?
   /**
    * Returns true if the two links are equivalent.<p>
    * @param link graphLink.<p>
    */
   abstract public boolean isEqual(graphLink link);

   // get the parent node's name, for debug messages.
   /**
    * Returns the Parent's name as a String.<p>
    */
   abstract public String parentName();
}