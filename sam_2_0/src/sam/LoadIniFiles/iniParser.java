package sam.LoadIniFiles;

import java.util.*;
import java.io.*;

/** Utility for reading MSWindows-style .ini files.
 *  Provided by Nathan at BBN.
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


public class iniParser
{

   public iniParser() //Constructor
   {
   }

   private boolean isVerbose = false;
   public boolean isVerbose() { return isVerbose; }
   public void setVerbose(boolean v) { isVerbose = v; }

   private char commentChar = '#';
   public char getCommentChar() { return commentChar; }
   public void setCommentChar(char c) { commentChar = c; }

   private String filename = "<stream>";
   public String getFilename() { return filename; }
   public void setFilename(final String name) { filename = name; }

  //////////////////////////////////////////////CLASS SLOT///////////////////////////////////////
  public static class Slot
  {
    private String name;

    //Method to get the name of the slot,something like plugin or cluster
    /**
     * Returns the name of the slot as a String.<p>
     */
    public String getName() { return name; }

    //Vector to hold the values ,something like ExternalDatasource
    private Vector values = new Vector();

    //Method that returns the Vector
    /**
     * Returns a vector of all the values.<P>
     */
    public Vector getValues() { return values; }

    //Method to return the name of the value,it returns something like ExternalDatasource
    /**
     * Returns the value as a String.<p>
     */
    public String getValue()
    {
      if (values.size() < 1)
       return null;

      return (String) values.elementAt(0);
    }


    /**
     * Sets the value.<p>
     */
    public void setValues(Vector v)
    {
       values = v;
    }

    //Method to add new elements to the Vector values
    /**
     * Adds new elements to the vector values.<p>
     * @param v String.<p>
     */
    public void addValue(String v)
    {
      values.addElement(v);

    }

    /**
     * Constructor.<p>
     */
    public Slot(String name)
    {
      this.name = name;
    }

    public Slot(String name, Object val)
    {
      this(name);
      values.add(val);
    }

    /**
     * Returns the name as a string as "slot xyz"
     */
    public String toString()
    {
      return "Slot "+name;
    }

    /**
     * Compares the instance of the object and returns true if it matches or else returns false.<p>
     * @param obj Object.<p>
     */
    public boolean equals(Object obj)
     {
      if (obj instanceof String)
      {
	return name.equals(obj);
      }

      if (obj instanceof Slot)
      {
	return name.equals(((Slot) obj).name);
      }
      return false;
     }


      /**
       * Returns the independent copy of this record.<p>
       */
      public Slot deepCopy()
      {
         Slot newOne = new Slot( getName() );
         newOne.setValues( getValues() );

         return newOne;
      }

  }//ends class Slot
  ////////////////////////////////////////////////////////////////////////////////////////////////////////


  ////////////////////////////////////CLASS SLOTHOLDER////////////////////////////////////////
  public static class SlotHolder
  {
    /*
    * vector of Slot
    *
    */
    private Vector slots = new Vector();

    //Method to get slots
    /**
     * Returns the Vector of the slots.<p>
     */
    public Vector getSlots()
    {
      return slots;
    }

    //Method to add new slots i.e., name and value
    /**
     * Adds new slots with the key and the value.<p>
     * @param s Slot.<p>
     */
    public void addSlot(Slot s)
    {
       slots.addElement(s);
    }

    //Method to add new slots i.e., name and value
    /**
     * Adds new slots with name and value.<p>
     * @param name String.<p>
     * @param val Object.<p>
     */
    public void addSlot(String name, Object val)
    {
      addSlot( new Slot(name, val) );
    }


    /**
     * Delets the Particular slot for the given name and value.<p>
     * @param name String.<p>
     * @param value String.<p>
     */
    public void deleteSlot(String name,String value)
    {
      //method to delete the slot from the group
      int index;

      for(index=0; index<slots.size(); index++)
      {
         //searching the vector for the matching name and the value of the cluster that is to be deleted
         Slot thisSlot = (Slot)slots.elementAt(index);

         //taking the name and the value of the cluster into different strings
         String name1 = thisSlot.getName();
         String val2 = thisSlot.getValue();

         if( (name1.equals(name)) && ( val2.equals(value)))
         {
            //remove that particular element that matches
            slots.removeElementAt(index);

               return;
         }

      }

            System.err.println("Internal Error: Slot " + name + ":" + value + " not found in iniParser:deleteSlot");
    }



    /**
     * Gets the slot given the name of the slot.<p>
     * @param name String.<p>
     */
    public Slot getSlot(String name)
    {

      int ix = slots.indexOf(name);
      if (ix < 0)
	return null;
      return (Slot) slots.elementAt(ix);
    }
  }//ends class Slotholder

  ////////////////////////////////////////////////////////////////////////////////////////////////

  /////////////////////////////////////////CLASS GROUP///////////////////////////////////////////
  public static class Group extends SlotHolder
  {
      //Anew Vector that holds the Sections
      private Vector sections = new Vector();

      //Method that returns the Sections
      /**
       * Returns the vector of Sections.<p>
       */
      public Vector getSections()
      {
         return sections;
      }


      /**
       * Adds new elements to the vector sections.<p>
       */
      public void addSection(Section s)
      {
         sections.addElement(s);
      }


      /**
       * Gets the matching Name of the Section.<p>
       */
      public Section getSection(String name)
      {
         Iterator it = sections.iterator();
         while( it.hasNext())
         {
            Section sec = (Section)it.next();
            String name1 = sec.getName();

            if( name1.equals(name))
            {
               return sec;
            }
         }

         return null;
      }


      /**
       * Returns an independent copy of this record
       */
      public Group deepCopy()
      {
         Group newOne = new Group();

         Iterator it = getSlots().iterator();
         while( it.hasNext() )
         {
            Slot slot = (Slot)it.next();

            newOne.addSlot( slot.deepCopy() );
         }

         it = getSections().iterator();
         while( it.hasNext() )
         {
            Section section = (Section)it.next();

            newOne.addSection( section.deepCopy() );
         }

         return newOne;
      }
   }//ends Group

   /////////////////////////////////////////////////////////////////////////////////////////////////

  ////////////////////////////////////CLASS SECTION////////////////////////////////////////////////////////////

  public static class Section extends SlotHolder
  {
    /** name of Section **/
    private String name = null;

    public String getName()
    {
      return name;
    }

    /** Vector of String **/
    private Vector parameters = new Vector();

    /**
     * Returns the vector of parameters.<p>
     */
    public Vector getParameters()
    {
      return parameters;
    }

    /**
     * Adds a Parameter with the specific name.<p>
     * @param p String.<p>
     */
    public void addParameter(String p)
    {
      parameters.addElement(p);
    }

    /**
     * constructor.<p>
     */
    public Section(String name)
    {
      this.name = name;
    }

    /**
     * Gets the name of the Section as the String as Section XXX.<p>
     */
    public String toString()
    {
      return "Section "+name;
    }


      /**
       * Returns an independent copy of this record
       */
      public Section deepCopy()
      {
         Section newOne = new Section( getName() );

         Iterator it = getParameters().iterator();
         while( it.hasNext() )
         {
            String parm = (String)it.next();

            newOne.addParameter( parm );
         }

         it = getSlots().iterator();
         while( it.hasNext() )
         {
            Slot slot = (Slot)it.next();

            newOne.addSlot( slot.deepCopy() );
         }

         return newOne;
      }
  }//ends class section

  ////////////////////////////////////////////////////////////////////////////////////////////////

  //////////////////////////////METHODS OF CLASS INIPARSER////////////////////////////////////////

   // Returns null if unable to open file.
   /**
    * Returns a Group on parsing the file else returns null.<p>
    * @param file, file to which the detials need to be parsed.<p>
    */
  public Group parse(File file) throws IOException
  {
     filename = file.toString();
     InputStream stream = null;

    if (filename.equals("-"))
    {
      if (isVerbose)
        System.err.println("Reading from standard input.");
      stream = new java.io.DataInputStream(System.in);
    } else
    {
      if (isVerbose)
        System.err.println("Reading \""+filename+"\".");
      try
      {
         stream = new FileInputStream(filename);
      }
      catch (Exception ex )
      {
         System.err.println("Error reading file " + ex.getLocalizedMessage() );
         return null;
      }
    }

    Group g = parse(stream);
    stream.close();
    return g;
  }

  /**
   * Returns a Section on parsing the file.<p>
   * @param file file to which the details need to be parsed.<p>
   * @param Section the Particular Section whose details need to be parsed.<p>
   */
  public Section parse(File file, String section) throws IOException
  {
    return parse(file.toString(), section);
  }

  /**
   * Returns the section on parsing the file successfully else returns false.<p>
   * @param filename the name of the file to which the details of the section need to be parsed.<p>
   * @param section the name of the section.<p>
   */

  public Section parse(String filename, String section) throws IOException {
    this.filename = filename;
    InputStream stream = null;

    if (filename.equals("-")) {
      if (isVerbose)
        System.err.println("Reading from standard input.");
      stream = new java.io.DataInputStream(System.in);
    } else {
      if (isVerbose)
        System.err.println("Reading \""+filename+"\".");
      stream = new FileInputStream(filename);
    }

    Section s = parse(stream, section);
    stream.close();
    return s;
  }


  /**
   * Returns the section on parsing the file successfully else returns false.<p>
   * @param in inputStream.<p>
   * @param section String.<P>
   */
  public Section parse(InputStream in, String section) throws IOException {
    InputStreamReader isr = new InputStreamReader(in);
    BufferedReader br = new BufferedReader(isr);

    Group g = runParser(br);
    if (g != null) {
      Enumeration sections = g.getSections().elements();
      while (sections.hasMoreElements()) {
        Section s = (Section) sections.nextElement();
        if (section.equals(s.getName())) {

          return s;
        }
      }
    }

    return null;
  }

  /**
   * returns the Gropu after parsing the details else returns null.<p>
   * @param in Inputstream.<p>
   */
  public Group parse(InputStream in) throws IOException
  {
    InputStreamReader isr = new InputStreamReader(in);
    BufferedReader br = new BufferedReader(isr);

    return runParser(br);
  }

  /**
   * Returns the group after writing out the group, else returns null.<p>
   * @param br Buffered reader.<p>
   */
  private Group runParser(BufferedReader br) throws IOException {
    Group g = new Group();
    Section section = null;

    String line;
    int ln = 0;

    if (isVerbose)
        System.err.println("Starting parsing");


    for (line = br.readLine(); line != null; line=br.readLine())
	 {
      int i, j;

      ln++;

        // ignore comments
      i = line.indexOf(commentChar);
      if (i >= 0)
        line = line.substring(0,i);

        // zap extra whitespace
      line = line.trim();

      // ignore empty lines
      if (line.length() <= 0)
        continue;

      int l;
      while (line.length() > 0 && line.charAt((l=line.length())-1) == '\\')
      {
        line = line.substring(0,l-1) + br.readLine().trim();
        ln++;
      }

      // ignore empty lines
      if (line.length() <= 0)
        continue;

      if (line.charAt(0) == '[')
      {
        // classd line
        j = line.indexOf(']');

        // Check for not found.
        if (j < 0)
	{
          throw new RuntimeException("Missing close bracket ']' at line " + ln);
        }

        String stuff = line.substring(1, j).trim();

        Vector v = StringUtility.explode(stuff);

        if (v.size() < 1) {
          throw new RuntimeException("Empty Section at line "+ln);
        }

        section = new Section((String) v.elementAt(0));

    if (isVerbose)
        System.err.println("Added section '" + section.getName() + "'");



        for (i=1; i<v.size(); i++) {
          section.addParameter((String) v.elementAt(i));
        }

        g.addSection(section);
      }
		else if ( (j = line.indexOf('=')) >= 0)
		{
        // a param line

        String name = line.substring(0, j).trim();
        Vector v = StringUtility.parseCSV(line, j+1);

	Slot slot = new Slot(name);
	slot.setValues(v);
        if (section == null) {
          g.addSlot(slot);
        } else {
	  section.addSlot(slot);
	}
      } else {
        System.err.println("Bad line " + ln + " in " + filename);
      }
       }

    if (isVerbose)
        System.err.println("Done parsing");

       return g;
     }
///////////////////////////////////////////////////////////////////////////////////////////////////////////////////
//to generate the details
public void parsegenerator(File file, Group g) throws IOException
   {
        parsegenerator(file.toString(),g);
   }


public void parsegenerator(String filename, Group g) throws IOException
   {
        this.filename = filename;
        OutputStream stream = null;

        if(filename.equals("-"))
        {
            if(isVerbose)
               System.err.println("writing to standard output.");
            stream = new java.io.DataOutputStream(System.out);
         }
        else
        {
            if(isVerbose)
              System.err.println("writing to " + filename);
            stream = new FileOutputStream(filename);
         }

      parsegenerator(stream, g);

      stream.close();
   }



public void parsegenerator(OutputStream out, Group g) throws IOException
   {
      OutputStreamWriter osw = new OutputStreamWriter(out);
      BufferedWriter bw = new BufferedWriter(osw);

      runparsegenerator(bw,g);

      bw.close(); //to stop writing to the file
   }



private void runparsegenerator(BufferedWriter bw, Group g) throws IOException
{
      Section section = null;
      String line = null;

      Vector groups = g.getSections();
      Iterator it1 = groups.iterator();
      while( it1.hasNext() )
            {
               Section sec = (Section)it1.next();

               // Writing the section heading
               bw.newLine();
               bw.newLine();
               bw.write("[" + sec.getName() + "]");

               // Write the parameters

              Vector parms = sec.getParameters();
              Iterator it = parms.iterator();
              while( it.hasNext() )
                  {
                     String thisParm = (String)it.next();

                     // Write a parameter on its own line.
                     bw.write( thisParm );

                  }

               // Write the slot values.
              Vector slots = sec.getSlots();
              Iterator it2 = slots.iterator();
              while( it2.hasNext() )
                  {
                     // Write a line with the name and its value from the slot.
                     iniParser.Slot thisSlot = (iniParser.Slot)it2.next();
                     bw.newLine();
                     bw.write(thisSlot.getName() + " = " + thisSlot.getValue());

                   }



            }

     }

}

