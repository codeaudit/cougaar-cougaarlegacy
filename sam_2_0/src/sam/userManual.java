package sam;


import java.awt.*;
import java.io.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.event.*;
import javax.swing.border.*;
import java.net.*;
import java.awt.geom.*;
import java.awt.font.*;
import java.util.*;

/**
 * Title:        Sam
 * Description:  ALP Business Process User Interface
 * Copyright:    Copyright (c) Doug MacKenzie
 * Company:      Mobile Intelligence Corp.
 * @author
 * @version 1.0
 */

//////////////////////Class to load the User Manual///////////////////////////////////////////////////////////////////////////

/**
 * Class userManual :  To load the user manual from the
 */
public class userManual extends JFrame
{
   public static userManual theuserManual;

   //for the loading of the url
   URL url;

   //for the String value
   String urlname;

   //for the content pane
   public JPanel contentpane;

   //for the border layout
   BorderLayout borderlayout = new BorderLayout();
   Border border1;

   //for setting the editor pane.
   public JEditorPane editorpane = new JEditorPane();

   //for the scrollpane
   public JScrollPane jscrollpane = new JScrollPane();

   /**
    * Constructor for the user manual class.
    */
   public userManual()
   {
      theuserManual = this;
      enableEvents(AWTEvent.WINDOW_EVENT_MASK);
      try
      {
         jbInit();
      }
      catch(Exception e)
      {
         System.out.println(" Exception occured while trying to load jbInit for the user Manual::UserManual constructor." );
         e.printStackTrace();
      }
   } //ends the constructor
    /**
     * jbIni     */
   private void jbInit () throws Exception
   {
      contentpane = (JPanel) this.getContentPane();
      border1 = BorderFactory.createLineBorder(Color.black,2);
      contentpane.setLayout(borderlayout);

      //set the size of the frame.
      this.setSize(new Dimension (1000, 800));

      //set the title of the frame
      this.setTitle("User Manual");

      //adding the editor pane to the content pane.
      contentpane.add(jscrollpane,BorderLayout.CENTER);
      jscrollpane.getViewport().add(editorpane,BorderLayout.CENTER);;

      //setting the file name to the string.
      urlname = "file:/shares/ssalag/jbproject/sam/docs/manual/manual.html";

      //setting the url to the string name
      url = new URL(urlname);

      editorpane.setEditable(false);
      editorpane.setPage(url);

      //for closing the window.
      addWindowListener( new WindowAdapter()
      {
         public void windowClosing (WindowEvent e)
         {
            dispose();
         }
      });

   }

}
