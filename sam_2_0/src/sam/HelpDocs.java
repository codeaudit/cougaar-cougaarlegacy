package sam;

import java.awt.*;
import java.io.*;
import java.awt.print.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.event.*;
import javax.swing.border.*;
import java.net.*;
import sam.printHelpDocs;
import java.awt.geom.*;
import java.awt.font.*;
import sam.printHelpDocs;
import java.util.*;
/**
 * class HelpDocs, to load the system help documentation and the User Manual.
 * Title:        Sam
 * Description:  ALP Business Process User Interface
 * <copyright>
 *    Copyright (c) 2000-2001 Defense Advanced Research Projects
 *    Agency (DARPA) and Mobile Intelligence Corporation.
 *    This software to be used only in accordance with the
 *    COUGAAR license agreement.
 * </copyright>
 * Company:      Mobile Intelligence Corp.
 * @author: Sridevi Salagrama
 * @version 1.0
 */

public class HelpDocs extends JFrame implements Printable
{
   public static HelpDocs thehelpdoc;

   //variable of component
   private Component componentToBePrinted;

   //to  print only for the help documents
   boolean isHelp;

   printHelpDocs printhelpdocs = new printHelpDocs();

   // for the help back and forward
   HelpBackAndForward helpbackandforward = new HelpBackAndForward();

   //Initializing the textlayout
   TextLayout layout;

   double scale;

   //URL
   URL url;

   //graphics variable

   public Graphics g;
   public PageFormat pf;
   public int pageIndex;
   public JPanel contentPane;
   BorderLayout bl1 = new BorderLayout();
   Border border1;
   JMenuBar  menubar1 = new JMenuBar();
   JToolBar  toolbar1 = new JToolBar();

   //menus
   JMenu menuFile = new JMenu();

   //images
   ImageIcon back_image;
   ImageIcon forward_image;
   ImageIcon home_image;

   //buttons
   JButton back_button = new JButton();
   JButton forward_button = new JButton();
   JButton print_button = new JButton();
   //JButton find_button = new JButton();
   JButton home_button = new JButton();

   //menuitems added to the
   public  JMenuItem menuManual = new JMenuItem();
   public JMenuItem menuPrint = new JMenuItem();
   public JScrollPane jscrollpane5 = new JScrollPane();

   JTabbedPane tabbedpane = new JTabbedPane();
   String[] items = {"Contents","Index","Find"};

   JButton temporary = new JButton("Temporary");
   String urlname;
   public JEditorPane editorpane = new JEditorPane();

   /**
   * Constructor.<p>
   */
   public HelpDocs()
   {
      thehelpdoc = this;
      enableEvents(AWTEvent.WINDOW_EVENT_MASK);
      try
      {
         jbInit();
      }
      catch (Exception e)
      {
         e.printStackTrace();
      }
   }

   ////////////////////////////////////////////////////////////////////////
   /* jbInit*/
   private void jbInit() throws Exception
   {
      contentPane = (JPanel) this.getContentPane();
      border1 = BorderFactory.createLineBorder(Color.black,2);
      contentPane.setLayout(bl1);
      this.setSize(new Dimension (1000,800));
      this.setTitle("Help");
      this.setJMenuBar(menubar1);

      //for the imgaes
      URL backURL = HelpDocs.class.getResource("back.gif");
      if( backURL == null )
      {
         System.err.println("Installation Error: Unable to find icon file sam/classes/sam/back.gif");
         System.exit(0);
      }
      back_image = new ImageIcon(backURL);
      back_button.setMaximumSize(new Dimension(25, 25));
      back_button.setMinimumSize(new Dimension(67, 25));
      back_button.setToolTipText("Go to previous page");
      back_button.setIcon(back_image);
      back_button.addActionListener(new java.awt.event.ActionListener()
      {
         public void actionPerformed(ActionEvent e)
         {
            back_button_actionPerformed(e); //to be written
         }
      });
      back_image.setDescription("vfs://host:0/file:////shares/projects/alp/sam/classes/sam/back.gif");


      //setting the icon for the forward button
      URL forwardURL = HelpDocs.class.getResource("forward2_new.gif");
      if( forwardURL == null)
      {
         System.err.println("Installation Error: Unable to find icon file sam/classes/sam/forward2_new.gif");
         System.exit(0);
      }
      forward_image = new ImageIcon(forwardURL);
      forward_button.setMaximumSize(new Dimension(25, 25));
      forward_button.setMinimumSize(new Dimension(67, 25));
      forward_button.setToolTipText("Go to next page");
      forward_button.setIcon(forward_image);
      forward_button.addActionListener(new java.awt.event.ActionListener()
      {
         public void actionPerformed(ActionEvent e)
         {
            forward_button_actionPerformed(e);
         }
      });

      //setting the icon for the home button.
      URL homeURL = HelpDocs.class.getResource("home.gif");
      if( homeURL == null)
      {
         System.err.println("Installation Error: Unable to find icon file sam/classes/sam/home.gif");
         System.exit(0);
      }
      home_image = new ImageIcon(homeURL);
      home_button.setMaximumSize(new Dimension(25, 25));
      home_button.setMinimumSize(new Dimension(67, 25));
      home_button.setToolTipText("Go to home");
      home_button.setIcon(home_image);
      home_button.addActionListener(new java.awt.event.ActionListener()
      {
         public void actionPerformed(ActionEvent e)
         {
            home_button_actionPerformed(e);
         }
      });

      toolbar1.add(home_button);
      toolbar1.add(back_button);
      toolbar1.add(forward_button);
////////////////////////////////////////////////////////////////////////////////////////////////
      menubar1.add(menuFile);
      menuFile.setText("File");
      menuFile.add(menuManual);
      menuFile.add(menuPrint);

      menuManual.addActionListener(new ActionListener()
      {
         public void actionPerformed(ActionEvent e)
         {
            toLoadManual(e);
         }
      });
      menuManual.setText("Manual");
      menuManual.setActionCommand("Manual");
      menuPrint.addActionListener(new ActionListener()
      {
         public void actionPerformed(ActionEvent e)
         {
            printDocs(e);
         }
      });
      menuPrint.setText("Print");
      menuPrint.setActionCommand("Print");
      contentPane.add(jscrollpane5,BorderLayout.CENTER);
      contentPane.add(toolbar1,BorderLayout.NORTH);
      jscrollpane5.getViewport().add(editorpane,BorderLayout.CENTER);

      urlname = "file:/shares/ssalag/jbproject/sam/docs/javadoc/index.html";
      url = new URL(urlname);

      //setting the forward and the back and parent buttons to grey out, depending on if there  is a page to go to.
      update_helpBackandForward();

      //for closing the help window.
      addWindowListener( new WindowAdapter()
      {
         public void windowClosing(WindowEvent e)
         {
            dispose();
         }
      });
      editorpane.setEditable(false);
      editorpane.setPage(url);
      helpbackandforward.addtoHistory(url);
      editorpane.addHyperlinkListener( new HyperlinkListener()
      {
         public void hyperlinkUpdate(HyperlinkEvent event)
         {
            if(event.getEventType()==HyperlinkEvent.EventType.ACTIVATED)
            {
               try
               {
                  editorpane.setPage(event.getURL());
                  helpbackandforward.addtoHistory(event.getURL());

                  //setting the forward and the back and parent buttons to grey out, depending on if there  is a page to go to.
                  update_helpBackandForward();
               }
               catch (IOException e)
               {
                  editorpane.setText(" Error: " + e);
               }
             }
         }
      });
   }
//////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
   /**
    * To go  to the previous page.<p>
    * @param e ActionEvent.<p>
    *
    */
   void back_button_actionPerformed(ActionEvent e)
   {
      url = (URL)helpbackandforward.goBack();
      if( url == null )
      {
         // Display an error messsage, there is no history.
         System.err.println("Error:  There is no page/record to go to.");
      }
      else
      {
         try
         {
            editorpane.setPage(url);
         }
         catch(Exception ef)
         {
            ef.printStackTrace();
         }

      }
        //setting the forward and the back and parent buttons to grey out, depending on if there is a page to go to.
        update_helpBackandForward();
    }
/////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
   /**
    * To go to the next page.<p>
    * @param e ActionEvent.<p>
    */
   void forward_button_actionPerformed(ActionEvent e)
   {
      url = (URL)helpbackandforward.goForward();
      if( url == null )
      {
         // Display an error messsage, there is no history.
         System.err.println("Error:  There is no page/record to go forward to.");
      }
      else
      {
         try
         {
            editorpane.setPage(url);
         }
         catch ( Exception ef)
         {
            ef.printStackTrace();
         }
       }

      //setting the forward and the back and parent buttons to grey out, depending on if there  is a page to go to.
      update_helpBackandForward();
    }

   /**
   * checks if there is a page to go to and sets the setEnable to true for the  back button <p>
   * if there is a page to go to,if there is no page to go to then sets it to false
   * Similar check is done for the back and the forward buttons
   */
   public void update_helpBackandForward()
   {
      //to check if there is a record to go forward to.
      if(helpbackandforward.haveFuture())
      {
         forward_button.setEnabled(true);
      }
      else
      {
         forward_button.setEnabled(false);
      }
      //to check if there is a record to go back to.
      if(helpbackandforward.havePast())
      {
         back_button.setEnabled(true);
      }
      else
      {
         back_button.setEnabled(false);
      }
   }

   /**
    * Loads the user manual.<p>
    * @param e ActionEvent.<p>
    */
   void toLoadManual( ActionEvent e)
   {
      urlname = "file:/shares/ssalag/jbproject/sam/docs/manual/manual.html";
      try
      {
         url = new URL(urlname);
         editorpane.setPage(url);
         helpbackandforward.addtoHistory(url);

      }
      catch(IOException f)
      {
         f.printStackTrace();
      }
      //setting the forward and the back and parent buttons to grey out, depending on if there  is a page to go to.
      update_helpBackandForward();
   }

   /**
    * To Print the help documentation STILL UNDER CONSTRUCTION.<p>
    * @param e ActionEvent.<p>
    */
   void printDocs(ActionEvent e)
   {
      //To do: write code for the printing stuff
      printhelpdocs.actionperformed(e);
   }

    //loading the index if the users hits the home button
    /**
     * Loads the Index.<p>
     * @param e ActionEvent.<p>
     */
   void home_button_actionPerformed(ActionEvent e)
   {
      urlname = "file:/shares/ssalag/jbproject/sam/docs/javadoc/index.html";
      try
      {
         editorpane.setPage(urlname);
      }
      catch(IOException f)
      {
         f.printStackTrace();
      }
        //setting the forward and the back and parent buttons to grey out, depending on if there  is a page to go to.
        update_helpBackandForward();
   }



   //overwriting the print method so that a dialog box is displayed
   public void print()
   {
      System.out.println( " HelpDocs::Print:: In the Print ");
      PrinterJob printjob = PrinterJob.getPrinterJob();
      try
      {
         printjob.print();
      }
      catch (Exception e)
      {
         e.printStackTrace();
      }
   } //ends method print

   public int print( Graphics g, PageFormat pf,  int pageIndex)// throws PrinterException
   {
      System.err.println("In HelpDocs::print( g=" + g + " pf=" + pf + " pageIndex=" + pageIndex);
      Graphics2D g2 = (Graphics2D)g;

      if(pageIndex > getNumberOfPages(g2,pf))
      {
         return Printable.NO_SUCH_PAGE;
      }
      g2.translate(pf.getImageableX(), pf.getImageableY());


      double gw =/*pf.getImageableWidth(); //*/componentToBePrinted.getWidth();
      double gh =/* pf.getImageableHeight();//*/componentToBePrinted.getHeight();

      disableDoubleBuffering(componentToBePrinted);
      componentToBePrinted.paint(g2);
      enableDoubleBuffering(componentToBePrinted);
      Rectangle2D page = new Rectangle2D.Double(0,0,gw,gh);
      g2.setPaint(Color.black);
      drawPage(g2,pf,pageIndex);


      return(PAGE_EXISTS);
   }

   public static void disableDoubleBuffering(Component c)
   {
      RepaintManager currentManager = RepaintManager.currentManager(c);
      currentManager.setDoubleBufferingEnabled(false);
   }

   public static void enableDoubleBuffering(Component c)
   {
      RepaintManager currentManager = RepaintManager.currentManager(c);
      currentManager.setDoubleBufferingEnabled(true);
   }
   public int getNumberOfPages(Graphics2D g2,PageFormat pf)
   {
      if(editorpane.getText().equals(" ")) return 0;
         layoutpages(g2,pf);

      //double Height = scale; //* layout.getAdvance();
      System.out.println( " The advance is : " + layout.getAdvance());

      int pages = (int)Math.ceil((double)layout.getAdvance()/ (pf.getImageableHeight()*scale) );
      return pages;
   }


   public void layoutpages(Graphics2D g2, PageFormat pf)
   {
      FontRenderContext context = g2.getFontRenderContext();
      Font f = new Font("Serif",Font.PLAIN,12);
      layout = new TextLayout(editorpane.getText(),f,context);
      double ascent = (double)layout.getAscent();
      System.out.println( "The value of the ascent is " + layout.getAscent());
      double descent =(double) layout.getDescent();
      System.out.println( "The value of the descent is " + descent);
      double height = ascent + descent;
      // scale = pf.getImageableHeight()/height;
      scale = height;
      // System.out.println( "The value of the scale is " + scale);
   }
   public void drawPage( Graphics2D g2,PageFormat pf, int page)
   {
      System.err.println("starting Helpdocs::drawPage ");
      if(editorpane.getText().equals(" "))
      {
         System.err.println("Helpdocs::drawPage - ducking out");
         return;
      }
      // page --;
      layoutpages(g2,pf);
      g2.clip(new Rectangle2D.Double(0,0,pf.getImageableWidth(),pf.getImageableHeight()));
      g2.translate(-page * pf.getImageableWidth(), 0);
      g2.scale(scale,scale);
      AffineTransform transform = AffineTransform.getTranslateInstance(0,layout.getAscent());
      Shape outline = layout.getOutline(transform);
      g2.draw(outline);
   }

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////


/**
 * Class: HelpBackAndForward. Used for moving to the previous and the next pages in the help file.<p>
 * Theory of operation: The current record matches the one currently being displayed.<p>
 * Title: Sam<p>
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
 *

 *
 */

public class HelpBackAndForward
{
   //To hold the previously visited display components in a vector
   ArrayList history;
   final int MaxSize = 100;

   // Index of empty record, just before the oldest record
   int emptySlot;

   // Index of Newest record
   int newest;

   // Index of the currently displayed record.
   // If equals emptySlot, then no backwards record available.
   // If equals newest, then no forward record available.
   int current;
   /**
    * Constructor, Initializes the variables.
    */

   public HelpBackAndForward()
   {
      // Allocate the ring buffer
      history = new ArrayList(MaxSize);
      for(int i=0; i<MaxSize; i++)
         history.add(null);

      clearall();
   }

   // Add one to a pointer, moving it one slot to the right,
   // taking into account a limited sized buffer.
   /**
    * adds one to the pointer
    * @param ptr  integer pointer
    * @return<code>integer</code> specifies the size of the buffer
    */
   private int addOne(int ptr)
   {
      return (ptr + 1) % MaxSize;
   }

   // Subtract one from a pointer, moving it one slot to the left,
   // taking into account a limited sized buffer.
   /**
    * removes one from the pointer
    */
   private int subOne(int ptr)
   {
      // Using modulo arithmetic, subtract one.
      return (ptr + MaxSize - 1) % MaxSize;
   }

   // Make sure there is at least one empty slot in the buffer.
   // If not, throw away the oldest record.
   /**
    * To check to see if there is atleast one empty slot in the buffer.
    *
    */
   private void checkemptySlot()
   {
      if( newest == emptySlot )
      {
         emptySlot = (emptySlot + 1) % MaxSize;
      }
   }
   /**
    * To add a new record.
    * @param rec contractOwnerBase
    */

   // Add a new record just after the "current" record.
   public void addtoHistory( URL url)
   {
      // Move to the new record's slot
      current = addOne(current);

      // Put it there
      history.set(current, url);

      // It is now the newest record (throw away anything they backed over)
      newest = current;

      // Make sure that we don't overfill our buffer.
      checkemptySlot();
   }
/**
 * @return <code> boolean<\code> true if there is atleast one record to go back to.
 */
   // Returns true if you can go back at least one record.
   public boolean havePast()
   {
      return current != emptySlot && current != addOne(emptySlot);
   }
   /**
    * @return <code> boolean<\code> true if there is atleast one record to go forward to.
    */
   // Returns true if you can go forward at least one record.
   public boolean haveFuture()
   {
      return current != newest;
   }

   // Return the current record and move the current pointer to the next older record.
   // Returns null if there is no going back.
   /**
    * To return the current record and move the pointer to the next older record.
    * @return <code> object <\code> specifies the old record.
    */
   public Object goBack()
   {
       // If they are already looking at the emptySlot record, return null.
      if( !havePast() ) //i.e., current is equal to the emptySlot
      {
          return null;
      }

      // move to the oldest record, just before the current record
      current = subOne(current);

      // Get the record
      Object rec = history.get(current);
      return rec;
   }


   //returning the field ,the user wants to go to
   // Returns null if there is no going forward.
   /**
    * To return the current record and move the pointer to the next new record
    * @return <code> object <\code> specifies the new record
    */
   public Object goForward()
   {
      // If they are already looking at the newest record, return null.
      if( !haveFuture() )
      {
          return null;
      }

      // move to the newer record, just after the current record
      current= addOne(current);
      return history.get(current);

   }
   /**
    * To set all the values to null.
    */

   void clearall()
   {
     emptySlot = 0;
     newest = 0;
     current = 0;
   }
} //ends class help backand forward


} //ends class help docs