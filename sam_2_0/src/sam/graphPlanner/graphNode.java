package sam.graphPlanner;



import java.util.List;
import java.util.Collection;
import java.lang.Object;
import java.lang.String;
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
 * @author       Doug MacKenzie
 * @version      1.0
 */
public interface graphNode
{
   // Get this node's parent.
   // returns the parent of this node, or null if none exists.
   /**
    * Returns the Parent of this node as agraph node or nullif it doesnot exist.<p>
    */
   abstract public graphNode getParentGraphNode();

   // Get this node's children.
   // Returns this node's child graphNodes.  It will be empty if none exist.
   /**
    * Returns child graph nodes of this node as a collection.<p>
    */
   abstract public Collection getChildren();

   // Get this node's input links.
   /**
    * Returns a list of graphLinks which may be empty if none exist.
    */
   abstract public List getInputs();

   // Get this node's output links.
    /**
    * Returns a collection of graphLinks which may be empty if none exist.
    */
   abstract public List getOutputs();


   /**
    * Return something like "Cluster xxx".
    */
   public abstract String getLongName();


   /**
    * Return the basic name
    */
   public abstract String getName();
}