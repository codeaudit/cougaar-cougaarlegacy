

package sam;

import javax.swing.tree.TreeNode;
import java.util.Enumeration;
import java.util.ArrayList;
import java.util.Vector;
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

public class ResultsNode implements TreeNode
{
   // Our parent, or null if we are the root.
   ResultsNode parent;

   // list of ResultsNode records, denoting our children.
   ArrayList children;

   // The text we display
   String text;

   // A user object hanging off
   Object obj;

   public ResultsNode()
   {
      children = new ArrayList();
      text = "Default";
   }

   public ResultsNode(ResultsNode ourParent, final String displayText, Object userobject)
   {
      this();

      parent = ourParent;
      text = displayText;
      obj = userobject;
   }

   public ResultsNode addChildResult(final String displayText, Object obj)
   {
      // Create the child node.
      ResultsNode child = new ResultsNode(this, displayText, obj);

      // Append it to our list of kids.
      children.add(child);

      // Give the caller a handle on the new child node.
      return child;
   }

   public Object getUserObject()
   {
      return obj;
   }

   public TreeNode getChildAt(int childIndex)
   {
      return (TreeNode)children.get(childIndex);
   }

   public int getChildCount()
   {
      return children.size();
   }

   public TreeNode getParent()
   {
      return parent;
   }

   public int getIndex(TreeNode node)
   {
      return children.indexOf(node);
   }

   public boolean getAllowsChildren()
   {
      return children.size() > 0;
   }

   public boolean isLeaf()
   {
      return children.size() == 0;
   }

   public Enumeration children()
   {
      Vector v = new Vector(children);
      return v.elements();
   }

   /// Return our display text for the jTree
   public String toString()
   {
      return text;
   }
}