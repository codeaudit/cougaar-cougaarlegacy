
package sam;

import javax.swing.filechooser.*;
import java.io.File;

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

public class SamFileFilter extends FileFilter
{
   String ext;
   String desc;

   public SamFileFilter(String extension, String description)
   {
     ext = extension;
     desc = description;
   }

   public String getDescription()
   {
      //TODO: implement this javax.swing.filechooser.FileFilter abstract method
      return desc + "( *. " + ext + ")";
   }

   public boolean accept(File file)
   {
      //TODO: implement this javax.swing.filechooser.FileFilter abstract method
      if(file.isDirectory())
      {
         return true;
      }

      String filename = file.getName();
      int periodIndex = filename.lastIndexOf('.');
      boolean accepted = false;

      if (periodIndex>0 && periodIndex<filename.length()-1)
      {

         String Extension = filename.substring(periodIndex+1).toLowerCase();
         if(Extension.equals(ext))
         accepted = true;

      }
      return accepted;

   }
}