

package sam;

import javax.swing.UIManager;
import java.awt.*;
import java.io.ByteArrayOutputStream;
/**
 * Class main_program to load the user Interface at a proper position.
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

public class main_program
{
   boolean packFrame = false;

   //Construct the application
   /**
    * Constructor.<p>
    */
   public main_program()
   {
      MainWindow frame = new MainWindow("samproperties.txt");

      //Validate frames that have preset sizes
      //Pack frames that have useful preferred size info, e.g. from their layout
      if (packFrame)
      {
         frame.pack();
      }
      else
      {
         frame.validate();
      }
      //Center the window
      Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
      Dimension frameSize = frame.getSize();
      if (frameSize.height > screenSize.height)
      {
         frameSize.height = screenSize.height;
      }
      if (frameSize.width > screenSize.width)
      {
         frameSize.width = screenSize.width;
      }
      frame.setLocation((screenSize.width - frameSize.width) / 2, (screenSize.height - frameSize.height) / 2);
      frame.setVisible(true);
   }

   //Main method
   /**
    * Main
    */
   public static void main(String[] args)
   {
      try
      {
         UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
      }
      catch(Exception e)
      {
         e.printStackTrace();
      }
      new main_program();
   }
}
