
package sam;

import java.util.*;
import java.io.*;

import sam.LoadContractFiles.contractOwnerBase;

/**
 * class: clipBoard, to save all the copied and cut components in a vector from where they can be pasted back<p>
 * it back to where the user wants to paste them<p>
 * Title:        Sam<p>
 * Description:  ALP Business Process User Interface<p>
 * <copyright>
 *    Copyright (c) 2000-2001 Defense Advanced Research Projects
 *    Agency (DARPA) and Mobile Intelligence Corporation.
 *    This software to be used only in accordance with the
 *    COUGAAR license agreement.
 * </copyright>
 * Company:      Mobile Intelligence Corp.<p>
 * @author Sridevi Salagrama
 * @version 1.0
 */

public class ClipBoard
{
   //vector of all the deleted Contract Owner based objects
   Vector selections = new Vector();

   /**
    * Constructor
    */

   public ClipBoard()
   {
      //constructor
   }

   /**
    * Add the Selected object to the clipBoard<p>
    * @param obj contractOwnerBase
    */
   public void addToClipboard(contractOwnerBase obj)
   {
      //adding the deleted objects to the clipboard
      selections.add(obj);
   }

   /**
    * Get the object that is in the vector of the deleted objects. <p>
    * Returns the vector that holds the deleted objects
    */

   public Vector getFromClipboard()
   {
      //getting the objects that are in the vector of the deleted objects

      return selections;
   }

   /**
    * Removes all the objects that are present in the vector that holds the deleted objects.<p>
    */

   public void clear()
   {
      selections.clear();
   }
}