/**
 *  @file         DebugFrame.java
 *  @copyright    Copyright (c) 2001
 *  @author       Abraham DeLaO
 *  @company      21st Century Technologies, Inc
 *  @description
 *  @history      Created August 1, 2000.
 *  @todo
 **/

package com.centurylogix.finalPredictiveAssessor;

import java.awt.*;
import java.awt.event.*;
import java.io.FileWriter;
import java.io.IOException;

public class DebugFrame extends Frame
{
  private static final boolean IN_DEBUG_MODE = false; //system-wide debug mode flag.
  private static final int ROWS = 15;
  private static final int COLUMNS = 60;

  private FileWriter outputFile = null;     // for output file
  private String outputFileName = null;     // output file name
  private boolean outputToFrame = true;     // do we write debug output to frame ?
  private boolean outputToFile = false;     // do we write debug output to file ?

  private boolean inQuietMode = false;      // the level of output vebosity is dictated
  private boolean inNormalMode = true;      //  by which of these is set to TRUE.
  private boolean inVerboseMode = false;    //  default is for Normal Mode.

  private BorderLayout borderLayout = new BorderLayout();
  private TextArea textArea = new TextArea(ROWS, COLUMNS);

  /*
  public void setBounds(int x,
                      int y,
                      int width,
                      int height)
    */


  public DebugFrame()
  {
    this(true); // is this ok to do. nested constructors ? look into it.
  }

  /**
   * Constructor creates a new debug frame.
   */
  public DebugFrame(boolean toFrame)
  {
    super();
    initFrame();

    // are we printing to Frame?
    outputToFrame = toFrame;

    if (outputToFrame) // is this ok, can you add window listner and write text through
      initFrame();

    // listener for window closing event. Need to catch this for proper ouput file closure.
    addWindowListener(new WindowAdapter()
      {
        public void windowClosing(WindowEvent e)
          {
            // if we are closing frame and printing to frame, close ouput file
            try {
              if (outputFile != null)
                outputFile.close();
            }
            catch (IOException io)
            {
              ;
            }

            System.exit(0);
          }
      });

    this.setBounds (100, 100, 550 , 650);
  }// end public DebugFrame (boolean)


  /******************* public methods *******************************************/

  /**
   * Sets the level of output verbosity to its lowest level : Quiet. Only ouput
   *  statements specified for quite mode will be printed. Suitable for lowest level
   *  of debugging ouput.
   */
  public void setQuietMode ()
  {
    this.inQuietMode = true;
    this.inNormalMode = false;
    this.inVerboseMode = false;
  }

  /**
   * Sets the level of output verbosity to its medium level : Normal. Ouput
   *  statements specified for normal and quiet mode will be printed.
   */
  public void setNormalMode ()
  {
    this.inQuietMode = false;
    this.inNormalMode = true;
    this.inVerboseMode = false;

  }

  /**
   * Sets the level of output verbosity to its highest level : Verbose. All debugging
   * statements will be printed.
   */
  public void setVerboseMode ()
  {
    this.inQuietMode = false;
    this.inNormalMode = false;
    this.inVerboseMode = true;

  }


  public void addText (String text)
  {
    if (IN_DEBUG_MODE && outputToFrame)
      textArea.append (text + "\n");

    if (IN_DEBUG_MODE && outputToFile)
    {
      //System.out.println ("Attempting to write to output file : " + text);
      try {
        outputFile.write(text + "\n");
      }
      catch (IOException e)
      {
        //System.out.println ("error writing to output file :" + outputFileName + "\n");
        if (IN_DEBUG_MODE && outputToFrame)
          textArea.append ("Error writing to output file : " + outputFileName + "\n");
      }
    }
    else
      ; // printNothing
  }

  public void printToFile (String fileName)
  {
    outputToFile = true;
    outputFileName = fileName;
    try {
      outputFile = new FileWriter (fileName);
    }
    catch (IOException e)
    {
      if (IN_DEBUG_MODE && outputToFrame)
        textArea.append ("Error opening output file for debug info : " + fileName);
    }
  }

  public void show ()
  {
    if (IN_DEBUG_MODE)
    {
      if (this.outputToFrame)
        super.show();
    }
    else
      return;
  }


 /********************** private methods ************************************/

 /**
   * Initialization of frame. This is here to satisfty extension of Frame class.
   */
  private void initFrame()
  {
    setLayout(borderLayout);
    this.add(textArea, BorderLayout.CENTER);
  }

}