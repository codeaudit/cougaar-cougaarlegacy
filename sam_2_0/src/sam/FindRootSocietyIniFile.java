
package sam;

import java.awt.*;
import javax.swing.*;
import javax.swing.border.*;

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
public class FindRootSocietyIniFile extends JDialog
{
   /**
    * Constructor
    */

   public FindRootSocietyIniFile(Frame frame, String title, boolean modal)
   {
      super(frame, title, modal);
      try
      {
         jbInit();
         pack();
      }
      catch(Exception ex)
      {
         ex.printStackTrace();
      }
   }

   public FindRootSocietyIniFile()
   {
      this(null, "", false);
   }

   void jbInit() throws Exception
   {
   }
}
