package sam;


 import java.io.*;
 import java.util.*;
 import java.lang.Boolean;
 import sam.PropertiesDialog;
 /**
 * class SamProperties is for setting the properties of the system which can be loaded<p>
 *  using the properties dialog box<p>
 * Title:        Sam
 * Description:  ALP Business Process User Interface
 * <copyright>
 *    Copyright (c) 2000-2001 Defense Advanced Research Projects
 *    Agency (DARPA) and Mobile Intelligence Corporation.
 *    This software to be used only in accordance with the
 *    COUGAAR license agreement.
 * </copyright>
 * Company:      Mobile Intelligence Corp.
 * @author
 * @version 1.0
 */

public class SamProperties extends Properties
{
   private String filename;
   private Properties systemProperties;
   private Properties programproperties;
   //properties dialog
   PropertiesDialog propertiesDialog;
   private String nothing = "                               ";

  //contrsuctor for SamProperties.
  public SamProperties(final String name)
  {
    systemProperties = System.getProperties();  //will extract the system propperties
    //systemProperties.list(System.out);
    programproperties  = new Properties();
    filename = systemProperties.getProperty("user.home")+systemProperties.getProperty("file.separator") + name;

//    System.out.println("The Path seperator is :" + systemProperties.getProperty("file.separator"));


      // Create the properties dialog and make it modal
      propertiesDialog = new PropertiesDialog();
      propertiesDialog.setModal(true);
  }

      // Method where you can give the name of the file and load the properties
      public void propRead()
      {
         FileInputStream in = null;
         boolean success = true;

         try
         {
            in = new FileInputStream(filename);
            programproperties.load(in); //this will read the program properties
            success = true;
//   System.err.println("Loaded program properties:");
//   programproperties.list(System.err);
         }

         catch (IOException ex)
         {
             System.err.println("Error:" + ex);
         }

      } //ends method to load system properties from the file


      //Method to write the system Properties to a file

      public void propWrite ()
      {
//         System.err.println("Writing the program properties");
//         programproperties.list(System.err);
         FileOutputStream out = null;
         boolean success = true;
         try
         {
            out = new FileOutputStream(filename);
            programproperties.store(out,filename);  //will write the program properties to the file
            success = true;

         }
         catch (IOException ex)
         {
            System.err.println("Error:" + ex);
         }


      }//ends method to write the system Properties to a file


      public boolean getDebugParsers()
      {
       String value = programproperties.getProperty("debugParsers");
       if( value != null)
       {
         return Boolean.valueOf( value ).booleanValue();
       }
        return false;
      }

      public void setDebugParsers(boolean _flag)
      {
         Boolean flag = new Boolean(_flag);
         programproperties.setProperty("debugParsers", flag.toString() );

      }

////////////////////////////////////////////////////////////////////////////////////
      public boolean getDebugContracts()
      {
        String value = programproperties.getProperty("debugContracts");

        if( value != null)
        {
          return Boolean.valueOf( value ).booleanValue();
        }
         return false;
      }

      public void setDebugContracts(boolean _flag)
      {
         Boolean flag = new Boolean(_flag);
         programproperties.setProperty("debugContracts",flag.toString() );

      }
/////////////////////////////////////////////////////////////////////////////////////

      public boolean getVerbose()
      {
        String value = programproperties.getProperty("verbose");

        if( value != null)
        {
          return Boolean.valueOf( value ).booleanValue();
        }
         return false;
      }

      public void setVerbose(boolean _flag)
      {
         Boolean flag = new Boolean(_flag);
         programproperties.setProperty("verbose", flag.toString() );
      }
/////////////////////////////////////////////////////////////////////////////////////

      public boolean getDebugSave()
      {
        String value = programproperties.getProperty("debugSave");

        if( value != null)
        {
          return Boolean.valueOf( value ).booleanValue();
        }
         return false;
      }

      public void setDebugSave(boolean _flag)
      {
         Boolean flag = new Boolean(_flag);
         programproperties.setProperty("debugSave", flag.toString() );
      }

////////////////////////////////////////////////////////////////////////////////////////
 public boolean getDebugPlanner()
      {
        String value = programproperties.getProperty("plannerDebug");

        if( value != null)
        {
          return Boolean.valueOf( value ).booleanValue();
        }
         return false;
      }

      public void setDebugPlanner(boolean _flag)
      {
         Boolean flag = new Boolean(_flag);
         programproperties.setProperty("plannerDebug", flag.toString() );
      }
///////////////////////////////////////////////////////////////////////////////////////

      public String getPathName()
      {
         String value =  programproperties.getProperty("User_Home");

         if(value != null)
         {
            return value;

         }
         return null;
      }

      public void setPathName(String name)
      {
         //you have to load the society for the properties,else you will have a null pointer error.
         programproperties.setProperty("User_Home",name);
      }
////////////////////////////////////////////////////////////////////////////////////////////////////
public String getEditorPathName()
      {
         String value =  programproperties.getProperty("Editor");

         if(value != null)
         {
            return value;

         }
         return nothing;
      }

      public void setEditorPathName(String name)
      {
         //you have to load the society for the properties,else you will have a null pointer error.
         programproperties.setProperty("EDITOR",name);
      }
////////////////////////////////////////////////////////////////////////////////////////////
// Called to popup the dialog for the user preferences.
   public  void popupPropertiesDialog()
   {
      // Set the initial values.
      propertiesDialog.debugParsers = getDebugParsers();
      propertiesDialog.debugContracts = getDebugContracts();
      propertiesDialog.verbose = getVerbose();
      propertiesDialog.debugSave = getDebugSave();
      propertiesDialog.plannerDebug = getDebugPlanner();
      propertiesDialog.pathName = getPathName();
     // propertiesDialog.Editor_pathName = getEditorPathName();

      propertiesDialog.loadInitialValues();

      // Pop it up.
      // blocks in setVisible until they click OK or CANCEL
      propertiesDialog.setVisible(true);

      // Get the results.
      if( propertiesDialog.OK )
      {
         setDebugParsers( propertiesDialog.debugParsers);
         setDebugContracts( propertiesDialog.debugContracts );
         setVerbose (propertiesDialog.verbose);
         setDebugSave(propertiesDialog.debugSave);
         setDebugPlanner(propertiesDialog.plannerDebug);
         setPathName(propertiesDialog.pathName);
        // setEditorPathName(propertiesDialog.Editor_pathName);
      }
   }

} //ends class SamProperties