package sam;

import sam.display.samGraphController;
import sam.display.samRenderer;
import sam.display.samBaseSite;
import sam.display.samEdgeRenderer;
import diva.graph.model.GraphModel;
import diva.graph.model.Graph;
import diva.graph.model.BasicNode;
import diva.graph.model.Node;
import sam.display.samJGraph;
import sam.display.samTarget;
import diva.canvas.selection.SelectionDragger;
import diva.util.UserObjectContainer;
import sam.LoadContractFiles.contractOwnerBase;
import sam.cluster;
import sam.plugin;
import sam.society;
import sam.PrintingDocs;
import sam.SamProperties;
import sam.graphPlanner.graphPlanner;
import sam.graphPlanner.graphNode;
import sam.prettyprint;
import sam.display.samLayout;
import sam.BackAndForward;
import sam.LoadIniFiles.iniParser;
import sam.societyXmlLoader;

import diva.canvas.*;
import diva.graph.*;
import diva.graph.layout.GlobalLayout;
import diva.graph.layout.LayoutTarget;

import java.io.*;
import java.awt.*;
import java.awt.print.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.event.*;
import javax.swing.tree.TreeNode;
import javax.swing.border.*;
import java.net.URL;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeSelectionModel;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.*;
import java.util.Collection;
import java.awt.geom.Point2D;
import java.util.Properties;
import org.cougaar.core.util.Operator;
import org.cougaar.core.util.OperatorFactory;
import javax.swing.UIManager;

/**
 * This Class creates the user Interface called the "Sam User Interface"<p>
 * Title:        Sam <p>
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


public class MainWindow extends JFrame
{
   // Used to set the name in the main window title bar.
   final String programName = "Sam 2.0";

   Properties systemProperties = System.getProperties();
   String version = systemProperties.getProperty("java.version");
   StringTokenizer stringtoken = new StringTokenizer(version, ".");
   int first = Integer.parseInt(stringtoken.nextToken());
   int middle = Integer.parseInt(stringtoken.nextToken());
   int last = Integer.parseInt(stringtoken.nextToken());

   public static MainWindow theMainWindow;
   public BackAndForward backandforward = new BackAndForward();
   public SamProperties properties;

   JPanel contentPane;
   JMenuBar menuBar1 = new JMenuBar();  //menubar for the main window.
   JToolBar toolBar2 = new JToolBar(); //for the editor pane

   //menu items for the editor pane
   public JButton menuedit2 = new JButton();
   public JButton menusave2 = new JButton();
   public JButton menucancel2 = new JButton();
   public JButton menuprint2 = new JButton();

   JPanel editor_panel = new JPanel(); //a panel to which the toolbar and the editor pane will be added

   JMenu menuFile = new JMenu(); //file menu for the main window.
   JMenuItem menuFileExit = new JMenuItem();
   JMenu menuFilePrint = new JMenu();
   JMenuItem canvasPrint = new JMenuItem("Canvas");
   JMenuItem screenPrint = new JMenuItem("Screen Snap Shot");
   JToolBar toolBar = new JToolBar(); //tool bar to add to the main window.

   public JButton jButton_up = new JButton();   //for going back
   public JButton jButton_forward = new JButton();  //for going forward
   JButton jButton_print = new JButton(); //for printing the displayed field
   JButton jButton_suggest = new JButton();
   public JTabbedPane tabbedpane = new JTabbedPane();
   public JButton jButton_parent = new JButton();//for diaplyaing the parent of the displayed field
   JButton jButton1 = new JButton(); //for open file.gif
   JButton jButton2 = new JButton(); //for save file.gif
   JButton jButton3 = new JButton(); //for help

   /**
    * For the open.gif
    */
   ImageIcon image1;
   /**
    * For the close.gif
    */
   ImageIcon image2;
   /**
    * For the Help.gif
    */
   ImageIcon image3;
   ImageIcon print_image;  //for printing the displayed page
   ImageIcon back_image;   //for the back arrow
   ImageIcon forward_image; //for the forward arrow image
   ImageIcon analyze_image; //for analyze
   ImageIcon parent_image; //for diaplaying the parent or the home
   ImageIcon suggest_image; //for suggest

   JLabel statusBar = new JLabel();
   /**
    * Label to display the displayed component
    */
   public JLabel fieldname = new JLabel();
   JLabel fieldname2 = new JLabel("          ");  //trying to declare two new lables
   JLabel fieldname3 = new JLabel("          ");  //to set thegap to display, needs to be changed to more refined code

   /**
    * For setting the Border Layout for the Main Window
    */
   BorderLayout borderLayout1 = new BorderLayout();
   /**
    * For setting the border layout for the Panel to which the EditorPane and the MenuBar have been added
    */
   BorderLayout borderLayout2 = new BorderLayout();
   Border border1;
   /**
    * Menu Item
    */
   JMenuItem menuFileSaveSociety = new JMenuItem();
   SamFileFilter  samfilefilter = new SamFileFilter("soc","Society");
   SamFileFilter  newsamfilefilter = new SamFileFilter("xml","Society");
   ClipBoard cb = new ClipBoard();

   // My data structures
   public society theSociety;
   public society  newSociety;
   public world theWorld = new world("default");

   JMenuItem menuEditPrefs = new JMenuItem();
   JMenuItem menu_insert = new JMenuItem();
   JMenu menuEdit = new JMenu();
   JMenuItem menuViewLayout = new JMenuItem();
   JMenu menuView = new JMenu();
   JMenuItem menuHelpAbout = new JMenuItem();
   JMenuItem menuHelpDocument = new JMenuItem();
   JMenuItem menuUserManual = new JMenuItem();
   JMenu menuHelp = new JMenu();

   //insert stuff
   JMenuItem pastemenuitem = new JMenuItem("Paste",'p');
   JMenu inserteditsubmenu = new JMenu("Insert");
   JMenuItem cuteditsubmenu = new JMenuItem("Cut",'c');
   JMenuItem copymenuitem = new JMenuItem("Copy");
   JMenuItem retainsuggestmenuitem = new JMenuItem("Retain");
   JMenuItem make_communitiesmenuitem = new JMenuItem("Make Communitites");

   //split pane stuff
   JSplitPane jSplitPane1 = new JSplitPane();
   JSplitPane jSplitPane2 = new JSplitPane();
   JSplitPane jSplitPane3 = new JSplitPane();
   JScrollPane jScrollPane1 = new JScrollPane();
   JScrollPane jScrollPane3 = new JScrollPane();
   JScrollPane jScrollPane4 = new JScrollPane();
   public static JEditorPane jEditorPane1 = new JEditorPane();

   JTree jTree1 = new JTree();
   JTree jTree2 = new JTree();
   JButton jButton_analyze = new JButton();

   JPopupMenu jPopupMenu1 = new JPopupMenu(); //for closing the error pane
   JMenuItem closeItem = new JMenuItem("Close");
   JPopupMenu tabbedpane_popup = new JPopupMenu();
   JMenuItem tabbedpane_save = new JMenuItem("Save");

   JMenuItem menuFileLoadSociety = new JMenuItem();
   JMenuItem menuclosetab = new JMenuItem();
   JMenuItem menucloseall = new JMenuItem();
   JMenuItem menuimportfromini = new JMenuItem();
   JMenuItem menuexporttoini = new JMenuItem();

   // Reference to the layout engine.
   samLayout LayoutEngine;
   /**
   * MainWindow default constructor intializes each
   * instance variable  takes system properties file name as an argument
   */

   //Construct the frame
   public MainWindow(final String propertiesFilename)
   {
      // Singleton pattern: Remember our instance.
      theMainWindow = this;
      if((propertiesFilename)!= null)
      {
         properties = new SamProperties(propertiesFilename);
         properties.propRead();
      }

      enableEvents(AWTEvent.WINDOW_EVENT_MASK);
      try
      {
         jbInit();
      }
      catch(Exception e)
      {
         e.printStackTrace();
      }
   }

///////////////////////////////////////////////////////////////////////////
   /**
   * jbInit method that sets the user interface
   */
   //Component initialization
   private void jbInit() throws Exception
   {
      image1 = new ImageIcon(MainWindow.class.getResource("openFile.gif"));
      image2 = new ImageIcon(MainWindow.class.getResource("closeFile.gif"));
      image3 = new ImageIcon(MainWindow.class.getResource("help.gif"));
      //image4 = new ImageIcon(MainWindow.class.getResource("closeFile.gif")); //for saving in the editor pane

   ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
       //setting the icon for the print button

      URL printURL = MainWindow.class.getResource("print.gif");
      if( printURL == null )
      {
         System.err.println("Installation Error: Unable to find icon file sam/classes/sam/print.gif");
         System.exit(0);
      }
      print_image = new ImageIcon(printURL);
   /////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

      //setting the icon for the suggest button

      URL suggestURL = MainWindow.class.getResource("suggest.gif");
      if( suggestURL == null )
      {
         System.err.println("Installation Error: Unable to find icon file sam/classes/sam/suggest.gif");
         System.exit(0);
      }
      suggest_image = new ImageIcon(suggestURL);
   /////////////////////////////////////////////////////////////////////////////////////////////////////////////////

      //setting the icon for the back button

      URL backURL = MainWindow.class.getResource("back.gif");
      if( backURL == null )
      {
         System.err.println("Installation Error: Unable to find icon file sam/classes/sam/back.gif");
         System.exit(0);
      }
      back_image = new ImageIcon(backURL);
   ////////////////////////////////////////////////////////////////////////////////////////////////////////////////
      //setting the icon for the forward button

      URL forwardURL = MainWindow.class.getResource("forward2_new.gif");
      if( forwardURL == null)
      {
         System.err.println("Installation Error: Unable to find icon file sam/classes/sam/forward2_new.gif");
         System.exit(0);
      }
      forward_image = new ImageIcon(forwardURL);
   ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////
      //setting the icon for the analyze button

      URL analyzeURL = MainWindow.class.getResource("analyze.gif");
      if( analyzeURL == null )
      {
         System.err.println("Installation Error: Unable to find icon file sam/classes/sam/analyze.gif");
         System.exit(0);
      }
      analyze_image = new ImageIcon(analyzeURL);
   //////////////////////////////////////////////////////////////////////////////////////////////////////////////////

      //setting the icon for the parent button

      URL parentURL = MainWindow.class.getResource("parent.gif");
      if( parentURL == null )
      {
         System.err.println("Installation Error: Unable to find icon file sam/classes/sam/parent.gif");
         System.exit(0);
      }
      parent_image = new ImageIcon(parentURL);
   ///////////////////////////////////////////////////////////////////////////////////////////////////////////////

      contentPane = (JPanel) this.getContentPane();
      border1 = BorderFactory.createLineBorder(Color.black,2);
      contentPane.setLayout(borderLayout1);
      this.setSize(new Dimension(1000, 800));
      this.setTitle(programName + " - " + "Default");

      editor_panel.setLayout(borderLayout2); //setting  border lay out for the editor panel

      jButton_print.setIcon(print_image);
      jButton_print.setMaximumSize(new Dimension(25,25));
      jButton_print.setMinimumSize(new Dimension(75,25));
      jButton_print.setToolTipText("print page");
      jButton_print.addActionListener(new java.awt.event.ActionListener()
      {
         public void actionPerformed(ActionEvent e)
         {
            jButton_print_actionPerformed(e);
         }
      });
      print_image.setDescription("vfs://host:0/file:////shares/projects/alp/sam/classes/sam/print.gif");

      jButton_suggest.setIcon(suggest_image);
      jButton_suggest.setMaximumSize(new Dimension(25,25));
      jButton_suggest.setMinimumSize(new Dimension(75,25));
      jButton_suggest.setToolTipText("Suggest");
      jButton_suggest.addActionListener(new java.awt.event.ActionListener()
      {
         public void actionPerformed(ActionEvent e)
         {
            jButton_suggest_actionPerformed(e);
         }
      });
      suggest_image.setDescription("vfs://host:0/file:////shares/projects/alp/sam/classes/sam/suggest.gif");

      jButton_forward.setIcon(forward_image);
      jButton_forward.setMaximumSize(new Dimension(25,25));
      jButton_forward.setMinimumSize(new Dimension(100,25));
      jButton_forward.setToolTipText("Go to next page");
      jButton_forward.setEnabled(false);
      jButton_forward.addActionListener(new java.awt.event.ActionListener()
      {
         public void actionPerformed(ActionEvent e)
         {
            jButton_forward_actionPerformed(e);
         }
      });

      forward_image.setDescription("vfs://host:0/file:////shares/projects/alp/sam/classes/sam/forward2_new.gif");
      jButton_up.setMaximumSize(new Dimension(25, 25));
      jButton_up.setMinimumSize(new Dimension(67, 25));
      jButton_up.setToolTipText("Go to previous page");
      jButton_up.setIcon(back_image);
      jButton_up.setEnabled(false);
      jButton_up.addActionListener(new java.awt.event.ActionListener()
      {
         public void actionPerformed(ActionEvent e)
         {
            jButton_up_actionPerformed(e);
         }
      });

      back_image.setDescription("vfs://host:0/file:////shares/projects/alp/sam/classes/sam/back.gif");
      jButton_parent.setToolTipText("Go to the Parent field");
      jButton_parent.setIcon(parent_image);
      jButton_parent.setEnabled(false);
      jButton_parent.setMaximumSize(new Dimension(25,25));
      jButton_parent.setMinimumSize(new Dimension(100,25));

      jButton_parent.addActionListener(new java.awt.event.ActionListener()
      {
         public void actionPerformed(ActionEvent e)
         {
            jButton_parent_actionPerformed(e);
         }
      });

      parent_image.setDescription("vfs://host:0/file:////shares/projects/alp/sam/classes/sam/parent.gif");
      jEditorPane1.setText("jEditorPane1");
      jEditorPane1.setEditable(false); //setting the editing capability of the editor pane to false
      statusBar.setText(" ");

      menuFile.setText("File");
      menuFile.setMnemonic('f');
      menuFileExit.setText("Exit");
      menuFilePrint.setText("Print");
      menuFileExit.setMnemonic('x');
      menuFilePrint.setMnemonic('i');
      menuFilePrint.add(canvasPrint);
      menuFilePrint.add(screenPrint);
   ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

      menuFileExit.addActionListener(new ActionListener()
      {
         public void actionPerformed(ActionEvent e)
         {
            fileExit_actionPerformed(e);
         }
      });

      canvasPrint.addActionListener(new ActionListener()
      {
         public void actionPerformed(ActionEvent e)
         {
            jButton_print_actionPerformed(e);
         }
      });
      screenPrint.addActionListener(new ActionListener()
      {
         public void actionPerformed(ActionEvent e)
         {
            screenPrint_actionPerformed(e);
         }
      });
   /////////////////////////////////for the editor pane buttons////////////////////////////////////////////////

      menusave2.setText("Save");
      menusave2.setMaximumSize(new Dimension(60, 25));
      menusave2.setMinimumSize(new Dimension(75, 25));
      menusave2.setToolTipText("Save File");
      menusave2.setEnabled(false);
      menusave2.addActionListener(new java.awt.event.ActionListener()
      {
         public void actionPerformed(ActionEvent e)
         {
            saveContractsEditor(e, "Tempcontracts.xml");
         }
      });

      menuprint2.setMaximumSize(new Dimension(60,25));
      menuprint2.setMinimumSize(new Dimension(75,25));
      menuprint2.setToolTipText("print page");
      menuprint2.setText("Print");
      menuprint2.setEnabled(true);
      menuprint2.addActionListener(new java.awt.event.ActionListener()
      {
         public void actionPerformed(ActionEvent e)
         {
            PrintingDocs.printComponent(MainWindow.theMainWindow.jEditorPane1, false, true);
         }
      });

      menuedit2.setMaximumSize(new Dimension(60,25));
      menuedit2.setMinimumSize(new Dimension(75,25));
      menuedit2.setToolTipText("Edit Contracts");
      menuedit2.setText("Edit");
      menuedit2.setEnabled(true);
      menuedit2.addActionListener(new java.awt.event.ActionListener()
      {
         public void actionPerformed(ActionEvent e)
         {
            editcontractmenu(e);
         }
      });

      menucancel2.setMaximumSize(new Dimension(60,25));
      menucancel2.setMinimumSize(new Dimension(75,25));
      menucancel2.setToolTipText("Cancel");
      menucancel2.setText("Cancel");
      menucancel2.setEnabled(false);
      menucancel2.addActionListener(new java.awt.event.ActionListener()
      {
         public void actionPerformed(ActionEvent e)
         {
            contractcancel(e);
         }
      });
   ///////////////////////////////////////////////////////////////////////////////////////////////////////////////
   ////////////////////////// For the Open File Button//////////////////////////////////////
      jButton1.setIcon(image1);
      jButton1.setMaximumSize(new Dimension(25, 25));
      jButton1.setMinimumSize(new Dimension(75, 25));
      jButton1.setToolTipText("Open File");
      jButton1.addActionListener(new java.awt.event.ActionListener()
      {
         public void actionPerformed(ActionEvent e)
         {
            menuFileLoadSociety_actionPerformed(e);
         }
      });
   ////////////////////////// For the Save File Button//////////////////////////////////////

      jButton2.setIcon(image2);
      jButton2.setMaximumSize(new Dimension(25, 25));
      jButton2.setMinimumSize(new Dimension(75, 25));
      jButton2.setToolTipText("Save File");
      jButton2.addActionListener(new java.awt.event.ActionListener()
      {
         public void actionPerformed(ActionEvent e)
         {
            //will call the method that will load the file save dialog box for the xml.
            menuFileSaveSociety_actionPerformed(e);
         }
      });
   ////////////////////////////////////////////////////////////////////////////////////////

      jButton3.setIcon(image3);
      jButton3.setMaximumSize(new Dimension(23, 25));
      jButton3.setMinimumSize(new Dimension(23, 25));
      jButton3.setToolTipText("Help");

      menuFileSaveSociety.setActionCommand("Save Society");
      menuFileSaveSociety.setMnemonic('s');
      menuFileSaveSociety.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_S,InputEvent.CTRL_MASK));
      menuFileSaveSociety.setText("Save Society");
      menuFileSaveSociety.addActionListener(new java.awt.event.ActionListener()
      {
         public void actionPerformed(ActionEvent e)
         {
            menuFileSaveSociety_actionPerformed(e);
         }
      });

   //////////////////////////////////Method to export to ini/////////////////////////
      menuexporttoini.addActionListener(new java.awt.event.ActionListener()
      {
         public void actionPerformed(ActionEvent e)
         {
            menuexporttoini_actionPerformed(e);
         }
      });
      menuexporttoini.setText("Export to ini");
      menuexporttoini.setMnemonic('i');
      menuEditPrefs.addActionListener(new java.awt.event.ActionListener()
      {
         public void actionPerformed(ActionEvent e)
         {
            menuEditPrefs_actionPerformed(e);
         }
      });
      menuEditPrefs.setText("System Properties");
      menuEditPrefs.setMnemonic('m');
      menuEdit.setActionCommand("Edit");
      menuEdit.setText("Edit");
      cuteditsubmenu.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_X,InputEvent.CTRL_MASK));
      menuViewLayout.addActionListener(new java.awt.event.ActionListener()
      {
         public void actionPerformed(ActionEvent e)
         {
            menuViewLayout_actionPerformed(e);
         }
      });
      menuViewLayout.setText("Layout");
      menuViewLayout.setMnemonic('y');
      menuView.setActionCommand("View");
      menuView.setText("View");
      menuView.setMnemonic('v');
      menuHelpAbout.setText("About");
      menuHelpAbout.setMnemonic('a');
      menuHelpAbout.addActionListener(new ActionListener()
      {
         public void actionPerformed(ActionEvent e)
         {
            helpAbout_actionPerformed(e);
         }
      });
      menuHelpDocument.setText("Java Docs");
      menuHelpDocument.setMnemonic('d');
      menuHelpDocument.addActionListener(new ActionListener()
      {
         public void actionPerformed(ActionEvent e)
         {
            menuHelpDocument_actionPerformed(e);
         }
      });

      menuUserManual.setText("User Manual");
      menuUserManual.setMnemonic('u');
      menuUserManual.addActionListener(new ActionListener()
      {
         public void actionPerformed(ActionEvent e)
         {
            menuUserManual_actionPerformed(e);
         }
      });

      menuHelp.setText("Help");
      menuHelp.setMnemonic('h');
      jButton_analyze.setMaximumSize(new Dimension(25, 25));
      jButton_analyze.setMinimumSize(new Dimension(100, 25));
      jButton_analyze.setToolTipText("Check Configuration Consistency");
      jButton_analyze.setIcon(analyze_image);
      jButton_analyze.addActionListener(new java.awt.event.ActionListener()
      {
         public void actionPerformed(ActionEvent e)
         {
            jButton_analyze_actionPerformed(e);
         }
      });
      jSplitPane2.setOrientation(JSplitPane.VERTICAL_SPLIT);
      jSplitPane3.setOrientation(JSplitPane.VERTICAL_SPLIT);
      jTree2.setRootVisible(false);
      jTree2.addMouseListener(new java.awt.event.MouseAdapter()
      {
         public void mousePressed(MouseEvent e)
         {
            jTree2_mousePressed(e);
         }
         public void mouseReleased(MouseEvent e)
         {
            jTree2_mouseReleased(e);
         }
      });

      closeItem.addActionListener(new java.awt.event.ActionListener()
      {
         public void actionPerformed(ActionEvent e)
         {
            closeItem_actionPerformed(e);
         }
      });

      tabbedpane_save.addActionListener(new java.awt.event.ActionListener()
      {
         public void actionPerformed(ActionEvent e)
         {
            //to do add the save dialog
         }
      });
      menuclosetab.addActionListener(new java.awt.event.ActionListener()
      {
         public void actionPerformed(ActionEvent e)
         {
            menuclosetab_actionPerformed(e);
         }
      });

      menuclosetab.setText("Close Society");
      menuclosetab.setMnemonic('t');
      menuclosetab.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_DELETE,InputEvent.CTRL_MASK));
      menucloseall.addActionListener(new java.awt.event.ActionListener()
      {
         public void actionPerformed(ActionEvent e)
         {
            menucloseall_actionPerformed(e);
         }
      });
      menucloseall.setText("Close all Societies");
      menucloseall.setMnemonic('a');
      menuFileLoadSociety.addActionListener(new java.awt.event.ActionListener()
      {
         public void actionPerformed(ActionEvent e)
         {
            menuFileLoadSociety_actionPerformed(e);
         }
      });
      menuFileLoadSociety.setText("Load Society");
      menuFileLoadSociety.setMnemonic('l');
      menuFileLoadSociety.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_O,InputEvent.CTRL_MASK));
      menuimportfromini.addActionListener(new java.awt.event.ActionListener()
      {
         public void actionPerformed(ActionEvent e)
         {
            menuimportfromini_actionPerformed(e);
         }
      });
      menuimportfromini.setText("Import from ini");
      menuimportfromini.setMnemonic('i');
      cuteditsubmenu.addActionListener(new java.awt.event.ActionListener()
      {
         public void actionPerformed(ActionEvent e)
         {
            cuteditsubmenu_actionPerformed(e);
         }
      });

      toolBar.add(jButton1);
      toolBar.add(jButton2);
      toolBar.add(jButton_print);
      toolBar.addSeparator();

      menuFile.add(menuFileLoadSociety);
      menuFile.add(menuFileSaveSociety);
      menuFile.add(menuclosetab);
      menuFile.add(menucloseall);
      menuFile.add(menuFilePrint);
      menuFile.add(menuimportfromini);
      menuFile.add(menuexporttoini);
      menuFile.add(menuFileExit);
      menuBar1.add(menuFile);
      menuBar1.add(menuEdit);
      menuBar1.add(menuView);
      menuBar1.add(menuHelp);

      this.setJMenuBar(menuBar1);
      contentPane.add(toolBar, BorderLayout.NORTH);
      toolBar.add(jButton_up, null);
      toolBar.add(jButton_parent,null);
      toolBar.add(jButton_forward,null);
      toolBar.addSeparator();
      toolBar.add(jButton_suggest,null);
      toolBar.add(jButton_analyze, null);
      toolBar.addSeparator();
      toolBar.add(jButton3);
      toolBar.add(fieldname2);
      toolBar.add(fieldname3);
      toolBar.add(fieldname);

      contentPane.add(statusBar, BorderLayout.SOUTH);
      contentPane.add(jSplitPane1, BorderLayout.CENTER);

      //for the editor pane
      toolBar2.add(menuedit2);
      toolBar2.add(menuprint2);
      toolBar2.add(menusave2);
      toolBar2.add(menucancel2);

      // Set the divider sizes
      jSplitPane1.setDividerSize(2);
      jSplitPane2.setDividerSize(2);
      jSplitPane3.setDividerSize(2);
      jSplitPane1.add(jSplitPane2, JSplitPane.LEFT);
      jSplitPane1.add(jSplitPane3, JSplitPane.RIGHT);
      jSplitPane2.add(jScrollPane1, JSplitPane.TOP);
      jSplitPane2.add(jScrollPane4, JSplitPane.BOTTOM);
      jSplitPane3.add(tabbedpane, JSplitPane.TOP);
      jSplitPane3.add(jScrollPane3, JSplitPane.BOTTOM);
      editor_panel.add(toolBar2,BorderLayout.NORTH);
      editor_panel.add(jEditorPane1,BorderLayout.CENTER);
      jScrollPane3.getViewport().add(jTree2, null);
      jScrollPane1.getViewport().add(jTree1, null);
      jScrollPane4.getViewport().add(editor_panel, null);

      // Set the selection model to only allow one node selected at a time
      TreeSelectionModel selectionModel = jTree1.getSelectionModel();
      selectionModel.setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);
      selectionModel = jTree2.getSelectionModel();
      selectionModel.setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);
      jTree2.setShowsRootHandles(true);

      // Register a tree selection listener to get selection events.
      jTree1.addTreeSelectionListener( new treeSelectionListener() );
      jTree2.addTreeSelectionListener( new resultsTreeSelectionListener() );

      menuEdit.add(menuEditPrefs);
      menuEdit.addSeparator();
      menuEdit.setMnemonic('e');
      menuEdit.add(inserteditsubmenu);
      inserteditsubmenu.setMnemonic('i');
      menuEdit.addSeparator();
      menuEdit.add(cuteditsubmenu);
      menuEdit.add(pastemenuitem);
      menuEdit.add(copymenuitem);
      menuEdit.add(retainsuggestmenuitem);
      menuEdit.add(make_communitiesmenuitem);
   //////////////////////////////////////////////////////////////////////////////////////////////////////////

      copymenuitem.addActionListener(new java.awt.event.ActionListener()
      {
         public void actionPerformed(ActionEvent e)
         {
            copymenuitem_actionPerformed(e);
         }
      });
   ////////////////////////////////////////////////////////////////////////////////////
      pastemenuitem.addActionListener(new java.awt.event.ActionListener()
      {
         public void actionPerformed(ActionEvent e)
         {
            pastemenuitem_actionPerformed(e);
         }
      });
   ///////////////////////////////////////////////////////////////////////////////////

      retainsuggestmenuitem.addActionListener(new java.awt.event.ActionListener()
      {
         public void actionPerformed(ActionEvent e)
         {
            retainsuggestmenuitem_actionPerformed(e);
         }
      });
   ///////////////////////////////////////////////////////////////////////////////////

       make_communitiesmenuitem.addActionListener(new java.awt.event.ActionListener()
      {
        public void actionPerformed(ActionEvent e)
        {
          //make_communitiesmenuitem_actionPerformed(e);
        }

      });
   //////////////////////////////////////////////////////////////////////////////////
      JMenu communitiesmenu = new JMenu("Communities");
      inserteditsubmenu.add(communitiesmenu);
      JMenuItem newcommunity = new JMenuItem("New Community");
      communitiesmenu.add(newcommunity);
      newcommunity.addActionListener(new java.awt.event.ActionListener()
      {
        public void actionPerformed(ActionEvent e)
        {
          newCommunitiesmenuitem_actionPerformed(e);

        }

      });

      JMenuItem existingCommunity = new JMenuItem("Existing Community");
      communitiesmenu.add(existingCommunity);
      existingCommunity.addActionListener(new java.awt.event.ActionListener()
      {
         public void actionPerformed(ActionEvent e)
         {
            communitiesmenuitem_actionPerformed(e);
         }

      });


   ////////////////////////////////////////////////////////////////////////////
      JMenu clustersmenu = new JMenu("Cluster");
      inserteditsubmenu.add(clustersmenu);
      JMenuItem newCluster = new JMenuItem("New Cluster");
      clustersmenu.add(newCluster);
      newCluster.addActionListener(new java.awt.event.ActionListener()
      {
         public void actionPerformed(ActionEvent e)
         {
            newclustersmenuitem_actionPerformed(e);
         }

      });

      JMenuItem existingCluster = new JMenuItem("Existing Cluster");
      clustersmenu.add(existingCluster);
      existingCluster.addActionListener(new java.awt.event.ActionListener()
      {
         public void actionPerformed(ActionEvent e)
         {
            clustersmenuitem_actionPerformed(e);
         }

      });

   /////////////////////////////////////////////////////////////////////////////
      JMenuItem pluginmenuitem = new JMenuItem("Plugin");
      pluginmenuitem.setActionCommand("plugin");
      inserteditsubmenu.add(pluginmenuitem);
      pluginmenuitem.addActionListener(new java.awt.event.ActionListener()
      {
        public void actionPerformed(ActionEvent e)
        {
          pluginmenuitem_actionPerformed(e);
        }

      });
      pluginmenuitem.setMnemonic('p');
      inserteditsubmenu.addSeparator();
   ////////////////////////////////////////////////////////////////////////////////////////

      JMenu contractmenuitem = new JMenu("Contracts");
      inserteditsubmenu.add(contractmenuitem);
      contractmenuitem.setMnemonic('t');
      JMenuItem newInputContract = new JMenuItem("New Input contract",'n');
      contractmenuitem.add(newInputContract);
      newInputContract.setActionCommand("New Input contract");

      newInputContract.addActionListener(new java.awt.event.ActionListener()
      {
         public void actionPerformed(ActionEvent e)
         {
            contractmenuitem_actionPerformed(e);
         }

      });

      JMenuItem newOutputContract = new JMenuItem("New Output contract",'w');
      contractmenuitem.add(newOutputContract);
      newOutputContract.setActionCommand("New Output contract");
      newOutputContract.addActionListener(new java.awt.event.ActionListener()
      {
         public void actionPerformed(ActionEvent e)
         {
            contractmenuitem_actionPerformed(e);
         }

      });


   //////////////////////////////////////////////////////////////////////////////////////////////////

      menuView.add(menuViewLayout);
      menuHelp.add(menuHelpAbout);
      menuHelp.add(menuHelpDocument);
      menuHelp.add(menuUserManual);

      // Make the popup menu.
      jPopupMenu1.add(closeItem);
      tabbedpane_popup.add(tabbedpane_save);

      // Create a default, empty society.
      theSociety = new society("default", theWorld);
      theSociety.tabbedpane_filename = "Default";

      //attach the contracts for the society
      theWorld.attachSociety( theSociety, theSociety.getName() );

      tabbedpane.addChangeListener(new javax.swing.event.ChangeListener()
      {
         public void stateChanged(ChangeEvent ce)
         {
            //call the activate tab method
            activate_Tab(ce);
         }
      });
   ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
      // Create and configure the layout engine.
      LayoutEngine = new samLayout();
      LayoutEngine.setOrientation(samLayout.HORIZONTAL);

      jSplitPane1.setDividerLocation(225);
      jSplitPane2.setDividerLocation(400);
      jSplitPane3.setDividerLocation(600);
      menuView.add(menuViewLayout);

      // Start out with the default tab.
      activateTab(0);

      statusBar.setText("OK");
   }//ends jbinit

/////////////////////////////////////////////////////////////Methods of MainWindow.java///////////////////////////////////////////////////////////////////////////////

   /**
   * Saves the Contracts that are displayed in the Editor pane of the
   * Sam User Interface
   * @param e Actionevent
   * @param name  filename
   */
   void saveContractsEditor(ActionEvent e, String name)
   {
      // Retrieve their modified contract
      String text = MainWindow.theMainWindow.jEditorPane1.getText();

      // Now parse it.
      OperatorFactory opFactory = OperatorFactory.getInstance();
      StringReader sr = new StringReader(text);
      Operator op = null;
      boolean goodParse = true;
      try
      {
         op = opFactory.create(sr);
      }
      catch (Exception ex)
      {
         // Pop up a window to show the error.
         JOptionPane.showMessageDialog(this,"Error: Unable to parse contract:\n" + text,"Error",JOptionPane.ERROR_MESSAGE);
         goodParse = false;
      }

      // If good one, save the operator
      if( goodParse )
      {
         // save their changes
         samGraphController gc = (samGraphController)theSociety.thePane.getGraphController();
         gc.selectedSitesDisplayedComponent.setContract(gc.selectedSite.isInput(), gc.selectedSite.getSlot(), op);

         String filename = systemProperties.getProperty("java.io.tmpdir") + systemProperties.getProperty("file.separator")+ name;
         OutputStream out = null;
         OutputStreamWriter osw =null;
         BufferedWriter bw = null;
         String contracts = prettyprint.XML( gc.selectedSite.getContract() );
         try
         {
            if(filename.equals("-"))
            {
               System.err.println("writing to standard output.");
               out = new java.io.DataOutputStream(System.out);
            }
            else
            {
              out = new FileOutputStream(filename);
              osw = new OutputStreamWriter(out);
              bw = new BufferedWriter(osw);
              bw.write(contracts);
            }
            bw.close();
         }
         catch (IOException ex)
         {
            System.err.println("Error:" + ex);
         }

         JOptionPane.showMessageDialog(this,"Message: Completed parsing the contracts:\n","Message",JOptionPane.INFORMATION_MESSAGE);

         // Take them out of edit mode.
         menuedit2.setEnabled(true);
         menuprint2.setEnabled(true);
         menusave2.setEnabled(false);
         menucancel2.setEnabled(true);

       }
   } //ends save contracts edit method
/////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
   /**
   * Set the Contracts displyed in the Editor pane to <p>
   * editable.
   * @param e ActionEvent.
   */
   void editcontractmenu(ActionEvent e)
   {
      jEditorPane1.setEditable(true);
      menuedit2.setEnabled(false);
      menusave2.setEnabled(true);
      menucancel2.setEnabled(true);
      menuprint2.setEnabled(true);

      //now must set the view contracts to null
      samGraphController gc = (samGraphController)theSociety.thePane.getGraphController();
      gc.item1.setEnabled(false);

   }
/////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
   /**
   * Clear the selection and the xml contract string in the Editor pane.
   */
   void contractcancel(ActionEvent e)
   {
      samGraphController gc = (samGraphController)theSociety.thePane.getGraphController();
      gc.clearSelections(true);
      gc.upDatesite();
      gc.item1.setEnabled(true);
      menuprint2.setEnabled(false);
      menusave2.setEnabled(false);
      menuedit2.setEnabled(false);
      menucancel2.setEnabled(false);
   }
/////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
   /**
   * Activates the tab in the case of shifting between the tabs
   */
   void  activate_Tab( ChangeEvent ce)
   {
      //activate the tabbedpane for the selected tab
      int selectedTab = tabbedpane.getSelectedIndex();
      if( selectedTab < 0 )
      {
         System.err.println("Internal Error: MainWindow::activate_Tab callback - Invalid selected tab: " + selectedTab);
         return;
      }
      activateTab( selectedTab );

      //to set the title of the mainwindow to the name of the file when ever there is a change
      //in the selected tab.
      this.setTitle(programName + " - " + MainWindow.theMainWindow.theSociety.tabbedpane_filename);
   }

/////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
   /**
    * Adds tab to the main panel.
    * The new tab is added with the name of the new society that is loaded
    * @param society society
    * @param label the tab name that is to be displyed which is same as the name of the society.
    * @return <code>integer</code> specifying the number of tabs
    */
   public int addtab( society soc, final String label )
   {
      soc.theJGraph = new samJGraph();

      // Add a resize listener
      soc.theJGraph.addComponentListener(new getResizeEvents());
      tabbedpane.addTab(label, soc.theJGraph);

      //trying to remove the default society tab as soon as a new society has been loaded
      //getting the name of the loaded society
      String sec =tabbedpane.getTitleAt(0);
      if((theMainWindow.theWorld.isNewSociety) && (sec =="default"))
      {
         removetab( 0 );
      }
      soc.thePane = (GraphPane)soc.theJGraph.getCanvasPane();
      LayoutTarget _target = soc.thePane.getGraphView();

      // Set the connector to our class.
      samGraphController theGraphController = (samGraphController)soc.thePane.getGraphController();

      theGraphController.setConnectorTarget(new samTarget());

      // Set display features on the graph view.
      GraphView view = soc.thePane.getGraphView();

      // Set the node renderer.
      samRenderer nodeRender = new samRenderer();
      view.setNodeRenderer(nodeRender);

      // Set the edge renderer.
      samEdgeRenderer edgeRender = new samEdgeRenderer();
      view.setEdgeRenderer(edgeRender);

      // Set the percentage of the pane that should be used for layout.
      view.setLayoutPercentage(1.0);

      return tabbedpane.getTabCount() - 1;
   }
   /**
    * Activates the tab
    * @param i The tab number , that should be activated<p>
    * @return <code>boolean</code> will return true if the tab is activated<p>
    */

   // Returns true on success, false on failure.
   public boolean activateTab(int i)
   {
      if( i < 0 || i >= tabbedpane.getTabCount() )
      {
         System.err.println("Internal Error: illegal tab in activateTab(" + i + ") num tabs=" + tabbedpane.getTabCount() );
         return false;
      }
      if( tabbedpane.getSelectedIndex() != i )
      {
         tabbedpane.setSelectedIndex(i);
      }
      // Activate it.
      society soc = theWorld.getSocietyByNumber(i);
      if( soc == null )
      {
         System.err.println("Internal Error: null society record in activateTab(" + i + ")");
         return false;
      }
      theSociety = soc;

      //trying to set the tool tip for the tab
      //DCM: Not in jdk 1.2.2?
      boolean old  = (first == 1 && middle < 3);
      if(old)
      {
         //for versions not same as 1.3.0
         int selectd_tab = tabbedpane.getSelectedIndex();
         String title = tabbedpane.getTitleAt(selectd_tab);
         tabbedpane.setToolTipText(title);
      }
      else
      {
         //set it according to jdk 1.3.0 standards
         tabbedpane.setToolTipTextAt(tabbedpane.getSelectedIndex(), soc.getName());
      }
      if( theSociety == null )
      {
         System.err.println("Internal error: MainWindow::activateTab - theSociety is null!");
         return false;
      }
      if( theSociety.thePane == null )
      {
         System.err.println("Internal error: MainWindow::activateTab - thePane is null!");
         return false;
      }
      samGraphController gc = (samGraphController)theSociety.thePane.getGraphController();
      if (!(gc.savedSite == null))
      {
         //if there is saved site.
         //then get the contracts for that highlighted site.
         MainWindow.theMainWindow.setDisplayedText(prettyprint.XML(gc.savedSite.getContract()));
      }
      else
      {
         //if no triangle is highlighted ,
         //then show nothing
         MainWindow.theMainWindow.setDisplayedText(" ");
      }
      // Reload the tree navigator.
      jTree1.setModel(theSociety.theTreeDisplay);

      // Reload the analysis navigator.
      jTree2.setModel(theSociety.theErrorDisplay);

      //trying to set the back and forward, so rightnow
      //when you click a new tab everything is being cleared out irrespective of where we were before with in the tab.
      MainWindow.theMainWindow.backandforward.clearall();

      // Render the graph
      MainWindow.theMainWindow.theSociety.displayedComponent.renderGraph(theSociety.theJGraph);

      //to display the errors
      ResultsNode root = new ResultsNode();
      int warningLevel = 10;  //  for all
      boolean rtn = theWorld.analyze(root, warningLevel);

      //if there are no problems to report then set the split pane to the lowest positon
      //so as not to display the error pane.
      if( !rtn )
      {
         theMainWindow.jSplitPane3.setDividerLocation(700);
      }
      else
      {
         //if you find errors in the society then display them.
         // theSociety.theErrorDisplay = new DefaultTreeModel(root);
         displayResults( );
      }

      return true;
   }

   /**
   * Removes the tab, and moves the next tab to the left <p>
   */

   public void removetab()
   {
      int i = tabbedpane.getSelectedIndex();
      removetab(i);

      // Activate the next tab to the right of the one we just deleted.
      // ( or the new right most one, if this was the right most one)
      if( i >= tabbedpane.getTabCount() )
      {
         int newTab = tabbedpane.getTabCount() - 1;

         // Only try to activate a tab if there is one left to activate.
         if( newTab >= 0 )
         {
            activateTab(newTab);
         }
      }
      else
      {
         // There was a tab to the right of the one we just deleted,
         // so activate that one, since it will appear in the same screen
         // position as the one we just deleted.
         activateTab(i);
      }
   }

   /**
   * Removes the tab and also removes the society by number.<p>
   * @param i integer value of the tab , that is to be removed<p>
   */

   public void removetab(int i)
   {
      //DCM: Changed from tabbedpane.remove(i); because didn't work with jdk 1.2.2
      // Double check, this one doesn't delete the component.
      boolean old  = (first == 1 && middle < 3);
      if(old)
      {
         tabbedpane.removeTabAt(i);
      }
      else
      {
         //set it according to jdk 1.3 standards
         tabbedpane.remove(i);
      }
      // delete the society.
      theWorld.removeSocietyByNumber(i);
   }

   /**
    * Draws the graph
    */

   public void layoutJGraph()
   {
      LayoutTarget _target = theSociety.thePane.getGraphView();
      try
      {
         GraphModel model = theSociety.theJGraph.getGraphModel();
         if( model == null )
         {
            System.err.println("Internal error: null graph model in layoutJGraph");
            return;
         }

         Graph graph = model.getGraph();

         if( graph == null )
         {
            System.err.println("Internal error: null graph in layoutJGraph");
            return;
         }

         LayoutEngine.layout(_target, graph);

      }
      catch(Exception ex)
      {
         ex.printStackTrace();
         statusBar.setText(ex.getMessage());
      }

   }

   /**
   * Exits from the system.
   */

   public void fileExit_actionPerformed(ActionEvent e)
   {
      properties.propWrite();
      System.exit(0);
   }

   /**
    * Loads the information about the user inter facel<p>
    * @param e ActionEvent<p>
    */

   public void helpAbout_actionPerformed(ActionEvent e)
   {
      MainWindow_AboutBox dlg = new MainWindow_AboutBox(this);
      Dimension dlgSize = dlg.getPreferredSize();
      Dimension frmSize = getSize();
      Point loc = getLocation();
      dlg.setLocation((frmSize.width - dlgSize.width) / 2 + loc.x, (frmSize.height - dlgSize.height) / 2 + loc.y);
      dlg.setModal(true);
      dlg.show();
   }
   /**
    * Loads the information about the java documentation
    * and the User Manual <p>
    * @param e ActionEvenet<p>
    */
   public void menuHelpDocument_actionPerformed(ActionEvent e)
   {
      boolean packframe = false;
      HelpDocs help = new HelpDocs();
      if(packframe)
      {
         help.pack();
      }
      else
      {
        help.validate();
      }

      Dimension screensize = Toolkit.getDefaultToolkit().getScreenSize();
      Dimension framesize = help.getSize();

      if(framesize.height >  screensize.height)
      {
         framesize.height = screensize.height;
      }

      if(framesize.width >  screensize.width)
      {
         framesize.width  = screensize.width;
      }

      help.setVisible(true);

      try
      {
         UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
      }
      catch( Exception ae)
      {
         ae.printStackTrace();
      }

   }

   public void menuUserManual_actionPerformed(ActionEvent e)
   {
      boolean packframe = false;
      userManual manual = new userManual();
      if(packframe)
      {
         manual.pack();
      }
      else
      {
        manual.validate();
      }

      Dimension screensize = Toolkit.getDefaultToolkit().getScreenSize();
      Dimension framesize = manual.getSize();

      if(framesize.height >  screensize.height)
      {
         framesize.height = screensize.height;
      }

      if(framesize.width >  screensize.width)
      {
         framesize.width  = screensize.width;
      }

      manual.setVisible(true);

      try
      {
         UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
      }
      catch( Exception ae)
      {
         ae.printStackTrace();
      }

   }

   /**
    * To Exit when the window is closed
    * @param e WindowEvent
    */

   protected void processWindowEvent(WindowEvent e)
   {
      super.processWindowEvent(e);
      if (e.getID() == WindowEvent.WINDOW_CLOSING)
      {
         fileExit_actionPerformed(null);
      }
   }

   /**
    * @param e ActionEvent
    * To Open the File open Dialog box to load the society from the xml files<p>
    */

   void menuFileLoadSociety_actionPerformed(ActionEvent e)
   {
      popupLoadSocietyFromXmlFileOpenDialog();
   }

   /**
    * @param e ActionEvent
    * To Open the File open Dialog box to load the society from the ".soc" files<p>
    */

   void menuimportfromini_actionPerformed(ActionEvent e)
   {
      popupLoadSocietyFileOpenDialog();
   }
   /**
    * To close the tab i.e., remove the tab.<p>
    * @param e ActionEvent
    */
   void menuclosetab_actionPerformed(ActionEvent e)
   {
      // If that will be the last tab, then make a new default tab first.
      if(tabbedpane.getTabCount() == 1)
      {
         // Create a default, empty society.
         theSociety = new society("default", theWorld);
         theSociety.tabbedpane_filename = "Default";
         setTitle(programName + " - " + theSociety.tabbedpane_filename);

         theWorld.attachSociety( theSociety, theSociety.getName() );
      }

      // now remove the old one.
      removetab( tabbedpane.getSelectedIndex() );

      // Activate the new one
      activateTab(tabbedpane.getSelectedIndex());
   }
///////////////////////////////////////////////////////////////////////
   /**
    * To close all the Societies, and load the default society <p>
    * so that the Main panel is not left empty.
    * @param e ActionEvent
    */
   void menucloseall_actionPerformed(ActionEvent e)
   {
      //remove all the tabs except the last one
      for(int i = 0;i<=tabbedpane.getTabCount()-2;i++)
      {
         removetab(i);
      }

      //call the function to load the default society.
      menuclosetab_actionPerformed(e);

   }
//////////////////////////////////////////////////////////////////////////////
   /**
    * calls the method that prints the canvas mode printing<p>
    * @param e ActionEvent
    */
   void jButton_print_actionPerformed(ActionEvent e)
   {
      printSocietyOpenDialog();
   }
/////////////////////////////////////////////////////////////////////////////

   /**
    * calls the method that prints the screen snap shot print mode<p>
    * @param e ActionEvent
    */

   void screenPrint_actionPerformed(ActionEvent e)
   {
      screenprint();
   }

////////////////////////////////////////////////////////////////////////////////
   /**
    * Is called when the suggest button is clicked<p>
    * calls the method that suggets the solutions<p>
    * @param e ActionEvent
    */
   void jButton_suggest_actionPerformed(ActionEvent e)
   {
      suggest();
   }

///////////////////////////////////////////method to copy///////////////////////////////
   /**
    * Is called when the copy command is activated<p>
    * calls the copySelecetedObjects Function<p>
    * @param e ActionEvent
    */

   void copymenuitem_actionPerformed(ActionEvent e)
   {
      copySelectedObjects();
   }

//////////////////////////////Method for inserting the deleted objects/////////////////////////////////
   /**
    * Is called when the paste command is activated
    * pastes the component that is stored on the clip board
    * @param e ActionEvent
    */
   void pastemenuitem_actionPerformed(ActionEvent e)
   {
      //getting all the elements that have been deleted, and placed ina vector from the cliboard
      cb.getFromClipboard();

      //looping through the elements
      Iterator it = cb.getFromClipboard().iterator();
      while(it.hasNext())
      {
         //getting the name of the object thta has been deleted and placed in the vector
         contractOwnerBase cob = (contractOwnerBase)it.next();
         String s = cob.getName();

         //if the deleted object is the instance of the class then paste the object in the ralated field
         if((cob instanceof plugin) &&(theSociety.displayedComponent instanceof cluster) ||
         (cob instanceof cluster) && ((theSociety.displayedComponent instanceof society) ||(theSociety.displayedComponent instanceof community)))
         {
            theSociety.displayedComponent.addComponent(s);

            //rendering the graph again
            theSociety.displayedComponent.renderGraph(theSociety.theJGraph);


            // Notify that we have changed the model.
            theSociety.theTreeDisplay.reload();

            //if there is a change/any kind of paste in the society file then set the boolean to true indicating that there has been
            //a change
            MainWindow.theMainWindow.theSociety.files_changed = true;
         }
         else if((cob instanceof community) && ((theSociety.displayedComponent instanceof society) ||(theSociety.displayedComponent instanceof community)))
         {
            theSociety.displayedComponent.addHighLevelComponent(s);

            //rendering the graph again
            theSociety.displayedComponent.renderGraph(theSociety.theJGraph);


            // Notify that we have changed the model.
            theSociety.theTreeDisplay.reload();

            //if there is a change/any kind of paste in the society file then set the boolean to true indicating that there has been
            //a change
            MainWindow.theMainWindow.theSociety.files_changed = true;

         }
         else
         {
            //if not throw an error message
            JOptionPane.showMessageDialog(this,"Error: You are trying to paste the object in an unrelated field ","Error",JOptionPane.ERROR_MESSAGE);
         }
      }
   }

///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
   /**
   * clears all the flags for the retained society
   * @param rec contractOwnerBase
   */

   void clearChildren(contractOwnerBase rec)
   {
      // Clear me.
      rec.clearDifferent();

      // Clear the kids.
      Iterator it = rec.getChildren().iterator();
      while( it.hasNext() )
      {
         contractOwnerBase kid = (contractOwnerBase)it.next();
         //recursive calling itself.
         clearChildren(kid);
      }
   }
//////////////////////////////////////////////////////////////////////////////////////////////////////
   /**
    * Class to get back the integer value
    */
//private class to get back an integer value

   private class Int
   {
      public int value;

      // Constructor
      public Int(int v)
      {
         value = v;
      }
   }
///////////////////////////////////////////////////////Method for retaing the selected tabbedpane//////////////////////////
//this method is being developed for now which will be later replaced by some thing more refined.
/**
 * Retains the user chosen tab and will remove all the other tabs<p>
 * @param e ActionEvent
 */
   void retainsuggestmenuitem_actionPerformed(ActionEvent e)
   {
      int i =tabbedpane.getSelectedIndex();

      // Remember which tabs we want to delete.
      // An array of ints, which are tab indexes.
      Vector markedForDelete = new Vector();

      // replace parent society with the selected society.

      //get the name of the society at the tab name
      String  soc = tabbedpane.getTitleAt(i);
      int periodindex = soc.lastIndexOf('.');
      String root = soc.substring(0,periodindex);
      int l = tabbedpane.getTabCount();
      for( int k = 0; k<=l-1; k++)
      {
         // Skip the one we have selected
         if( k == i )
            continue;

         //looping through all of the tabs
         String title = tabbedpane.getTitleAt(k);
         int thisIndex = title.lastIndexOf('.');
         String thisRoot;

         if(thisIndex >0)
         {
            thisRoot = title.substring(0,thisIndex);
         }
         else
         {
            thisRoot = title;
         }


         // Is this our parent tab or one of our siblings?
         // The first test checks if this tab is the parent
         // The second test checks if this tab is a sibling.
         if( root.equals(title)|| root.equals( thisRoot ) )
         {
            markedForDelete.add( new Int(k) );
         }

      }

      // Now delete them (in right to left order, so the other indexes stay valid.
      for(int tab=markedForDelete.size()-1; tab >=0; tab--)
      {
         Int tabIndex = (Int)markedForDelete.get(tab);
         removetab(tabIndex.value);
      }

      boolean foundIt = false;
      for (int remain=0; remain<=tabbedpane.getTabCount()-1;remain++)
      {
         // Get the title for this tab
         String name = tabbedpane.getTitleAt(remain);

         // Is it the one they originally selected?
         if( name.equals(soc) )
         {
            // Yes, rename it to just its root.
            tabbedpane.setTitleAt(remain,root);
            activateTab(remain);
            theSociety.setName(root);
            foundIt = true;
            break;
         }
      }

      if( !foundIt )
      {
         System.err.println("Internal Error: Didn't find selected tab in retainsuggestmenuitem_actionPerformed");
      }


      // Clear the different flags in the new retained society
      clearChildren( theMainWindow.theSociety );

      //rendering the graph again
      theSociety.displayedComponent.renderGraph(theSociety.theJGraph);

   }

/////////////////////////////////Method for inserting the communities//////////////////////////////////////

   /**
    * Pops up a list of the Communities from which<p>
    * the user can select a desired communitty to insert.<p>
    * @param e ActionEvent
    */
   void communitiesmenuitem_actionPerformed(ActionEvent e)
   {
      showOptionPane rec = new showOptionPane();
      rec.popitup(showOptionPane.theCommunityType);
   }

   /**
    * Pops up a list of the Communities from which<p>
    * the user can select a desired communitty to insert.<p>
    * @param e ActionEvent
    */
   void newCommunitiesmenuitem_actionPerformed(ActionEvent e)
   {
      showOptionPane rec = new showOptionPane();
      rec.popitup(showOptionPane.theNewCommunityType);
   }

//////////////////////////////////method for inserting the clusters/////////////////////////////////////////////
/**
 * Pops up a list of the clusters from which the user <p>
 * can select a desired cluster to insert. <p>
 * @param e ActionEvent.
 */
   void clustersmenuitem_actionPerformed(ActionEvent e)
   {
      showOptionPane rec = new showOptionPane();
      rec.popitup(showOptionPane.theClusterType);
   }


   /**
 * Pops up a list of the clusters from which the user <p>
 * can select a desired cluster to insert. <p>
 * @param e ActionEvent.
 */
   void newclustersmenuitem_actionPerformed(ActionEvent e)
   {
      showOptionPane rec = new showOptionPane();
      rec.popitup(showOptionPane.theNewClusterType);
   }
/////////////////////////////////////////////////////////////////////////////////////
/**
 * Pops up a list of the Plugins from which the user <p>
 * can select a desired plugin to insert.<p>
 * @param e ActionEvent
 */
   void pluginmenuitem_actionPerformed(ActionEvent e)
   {
      showOptionPane rec = new showOptionPane();
      rec.popitup(showOptionPane.thePluginType);
   }
/////////////////////////////////////////////////////////////////////////////
/**
 * Pops up a list of the contracts from which the user <p>
 * can select a desired contract to insert.<p>
 * @param e ActionEvent
 */

   void contractmenuitem_actionPerformed(ActionEvent e)
   {
      String target = e.getActionCommand();
      if(target.equals("New Output contract"))
      {
         // Get the selected objects.
         Collection selections = ((samGraphController)theSociety.thePane.getGraphController()).getSelectedObjects();

         // clear the selections (so they will go away).
         ((samGraphController)theSociety.thePane.getGraphController()).clearSelections(false);

         // Delete each of the selected objects
         Iterator it = selections.iterator();

         if( selections.size()==0)
         {
            JOptionPane.showMessageDialog(this,"Error:Please select an Object for which you wish to insert contracts","Error",JOptionPane.ERROR_MESSAGE);
         }

         while( it.hasNext() )
         {
            UserObjectContainer uoc = (UserObjectContainer)it.next();
            BasicNode node = (BasicNode)uoc.getUserObject();
            contractOwnerBase rec = (contractOwnerBase)node.getSemanticObject();
            MainWindow.theMainWindow.theSociety.displayedComponent.addNewPublishContract();

         }

      // Tell the remaining objects to clear their dependencies.
         theWorld.clearMemberDependencies();

      // And then recompute them.
      theWorld.computeDependencies(null, false);

     // ((samGraphController)theSociety.thePane.getGraphController()).completeRedraw();

      // redisplay the graph after the removal of the selected object from the vector
     // display.displayedComponent.renderGraph(MainWindow.theMainWindow.theSociety.theJGraph);
      MainWindow.theMainWindow.theSociety.displayedComponent.renderGraph(MainWindow.theMainWindow.theSociety.theJGraph);


      }//if ends
      else
      {

         // Get an iterator over the selected objects.
         Collection selections = ((samGraphController)theSociety.thePane.getGraphController()).getSelectedObjects();

         // clear the selections (so they will go away).
         ((samGraphController)theSociety.thePane.getGraphController()).clearSelections(false);

         // Delete each of the selected objects
         Iterator it = selections.iterator();

         if( selections.size()==0)
         {
            JOptionPane.showMessageDialog(this,"Error : Please select an Object for which you wish to insert contracts","Error",JOptionPane.ERROR_MESSAGE);
         }

         while( it.hasNext() )
         {
            UserObjectContainer uoc = (UserObjectContainer)it.next();
            BasicNode node = (BasicNode)uoc.getUserObject();
            contractOwnerBase rec = (contractOwnerBase)node.getSemanticObject();
            MainWindow.theMainWindow.theSociety.displayedComponent.addNewSubscribeContract();
         }
         // Tell the remaining objects to clear their dependencies.
         theWorld.clearMemberDependencies();

         // And then recompute them.
         theWorld.computeDependencies(null, false);

        // ((samGraphController)theSociety.thePane.getGraphController()).completeRedraw();

         // redisplay the graph after the removal of the selected object from the vector
         MainWindow.theMainWindow.theSociety.displayedComponent.renderGraph(MainWindow.theMainWindow.theSociety.theJGraph);


      }
   }

/////////////////////////////////////////////////////////////////////////////////////////
   // Called to popup the file chooser for the main society ini file.
   /**
    * Opens the file open dialog box <p>
    */
   private void popupLoadSocietyFileOpenDialog()
   {
      String lastTime = properties.getPathName();
      if( lastTime == null )
          lastTime = ".";
      File defaultFile = new File(lastTime);

      //creating a new file chosser object
      JFileChooser societyIniFileChooser = new JFileChooser();

      //setting the borders for the file open dialog box
      societyIniFileChooser.setBorder(BorderFactory.createLineBorder(Color.black));

      //setting the current directory  to the file chooser
      societyIniFileChooser.setCurrentDirectory(defaultFile);

      //setting the file filter
      societyIniFileChooser.setFileFilter( samfilefilter);

      //getting the value.
      int returnvalue = societyIniFileChooser.showOpenDialog(this);

     /*//any of these methods can be used
     if( societyIniFileChooser.showOpenDialog(this) == societyIniFileChooser.APPROVE_OPTION )
      {
        loadTheSystem(societyIniFileChooser.getSelectedFile());
      }
      */
      if ( returnvalue == societyIniFileChooser.APPROVE_OPTION )
      {
         loadTheSystem(societyIniFileChooser.getSelectedFile());
      }

   }
//////////////////////////////////////////////////////////////////////////////////////////
//called to popup the filechooser for the main society xml file.
/**
 * Opens the file dialog box with the options to load the society from the xml file
 */

   private void popupLoadSocietyFromXmlFileOpenDialog()
   {
      String lastTime = properties.getPathName();
      if( lastTime == null )
          lastTime = ".";
      File defaultFile = new File(lastTime);
      //creating a file chooser object
      JFileChooser societyXmlFileChooser = new JFileChooser();

      //setting the open dialog box's border
      societyXmlFileChooser.setBorder(BorderFactory.createLineBorder(Color.black));

      //setting the current directory for the filechooser's display
      societyXmlFileChooser.setCurrentDirectory(defaultFile);

      //setting the file filter value.
      societyXmlFileChooser.setFileFilter( newsamfilefilter);

      //taking the value of the option.
      int returnvalue = societyXmlFileChooser.showOpenDialog(this);

      /*if( societyXmlFileChooser.showOpenDialog(this) == societyXmlFileChooser.APPROVE_OPTION )
      {
          loadTheSystemFromXml(societyXmlFileChooser.getSelectedFile());
      }*/
      if( returnvalue == societyXmlFileChooser.APPROVE_OPTION )
      {
         loadTheSystemFromXml(societyXmlFileChooser.getSelectedFile());
      }
   }
//////////////////////////////////////////////////////////////////////////////////////
/**
 * Prints as a Canvas printout
 */
   private void printSocietyOpenDialog()
   {
      PrintingDocs.printComponent(MainWindow.theMainWindow.theSociety.theJGraph.getCanvasPane().getCanvas(), false, false);
   }
/**
 * Prints as a Screen snap shot
 */
   private void screenprint()
   {
      PrintingDocs.printComponent(this, true, false);
    }
//////////////////////////////////////////////////////////////////////////////////////
/**
 * Creates all the solutions in the form of new socities<p>
 * as new tabs<p>
 */
   private void suggest()
   {
      boolean debug = properties.getDebugPlanner();

      // Create the planner
      graphPlanner p = new graphPlanner(debug);

      // Build the list of available nodes.
      ArrayList availableNodes = new ArrayList();
      String names[] = theSociety.getAvailablePlugins();
      //String names[] = theWorld.getAvailablePlugins();
      for(int i=0; i<names.length; i++)
      {
         plugin plug = new plugin(names[i], null);
         plug.attachContracts( theWorld.getSystemContract(), false );
         availableNodes.add( plug );
      }

      // Generate the solutions.
      ArrayList results = new ArrayList();
      boolean rtn = p.plan(theSociety.displayedComponent, availableNodes, results);

      // Examine the results.
      int count = results.size();
      String msg = "";
      if( rtn && count == 0 )
      {
         msg = "Configuration is already complete";
      }
      else if( !rtn && count == 0 )
      {
         msg = "No useful components available";
      }
      else if( rtn && count == 1 )
      {
         msg = "Found " + count + " complete solution";
      }
      else if( !rtn && count == 1 )
      {
         msg = "Found " + count + " partial solution";
      }
      else if( rtn )
      {
         msg = "Found " + count + " complete solutions";
      }
      else
      {
         msg = "Found " + count + " partial solutions";
      }

      statusBar.setText( msg );

      // Now add the suggested configurations.
      if( count > 0 )
      {
         // build a trail to the current record, where the suggested nodes go.
         ArrayList trail = new ArrayList();

         if( debug )
         {
            System.err.println("Building navigation trail");
         }

         contractOwnerBase rec = theSociety.displayedComponent;
         do
         {
            if( debug )
            {
               System.err.println("   Adding " + rec.getLongName() + " to trail");
            }
            trail.add(rec);
            rec = rec.getParentNode();
         } while( rec != null && rec != theSociety );


         int firstTab = -1;
         int i=1;
         Iterator it = results.iterator();
         while( it.hasNext() )
         {
            Collection solution = (Collection)it.next();

            if( debug )
            {
               System.err.println("Adding solution " + i + " to display.  Nodes:");
               Iterator nIT = solution.iterator();
               while( nIT.hasNext() )
               {
                  contractOwnerBase node = (contractOwnerBase)nIT.next();
                  System.err.println("   " + node);
               }
               System.err.println("");
            }

            // Dup the society
            contractOwnerBase newRec = theSociety.recursiveCopy(theWorld);
            if( !(newRec instanceof society) )
            {
               System.err.println("Internal Error: duplicating society record didn't return a society record: " + newRec);
               continue;
            }
            //society newSociety = (society)newRec;
             newSociety = (society)newRec;

            // Find the node in the new tree which matches the currently displayed record.
            contractOwnerBase point = newSociety;
            for(int step = trail.size()-1; step>=0; step--)
            {
               contractOwnerBase node = (contractOwnerBase)trail.get(step);

               if( debug )
                   System.err.println("node.name=" + node.getLongName() + " point.name=" + point.getLongName() );

               point = point.getChildByName( node.getName() );
               if( point == null )
               {
                  System.err.println("Internal Error: Unable to find path in duplicated society");
                  break;
               }
            }

            // Did we error out?
            if( point == null )
            {
               continue;
            }

            // Add this solution into the tree.
            Iterator nodeIT = solution.iterator();
            while( nodeIT.hasNext() )
            {
               graphNode node = (graphNode)nodeIT.next();

               point.addDifferentComponent( node.getName() );
            }

            // Point the display record to this node
            newSociety.displayedComponent = point;

            // Attach the contracts
            newSociety.attachContracts(theWorld.getSystemContract(), false);

            //if there is a change in the society file then set the boolean to true
            //indicating that there has beena change in the file
            newSociety.files_changed = true;

            // Compute dependencies
            newSociety.computeDependencies(null, false);

            // Attach it to the world.
            String name = newSociety.getName() + "." + i++;
            newSociety.setName( name );

            int tab = theWorld.attachSociety(newSociety, name );
            if( firstTab == -1 )
               firstTab = tab;

         }

         // Activate the left tab.
         if( firstTab >= 0 )
            activateTab(firstTab);

      }
   }


//////////////////////////////////////////////////////////////////////////////////////////////////////////////////
/**
 * Pops up the System properties Dialog box<p>
 * @param e ActionEvent
 */
   void menuEditPrefs_actionPerformed(ActionEvent e)
   {
      properties.popupPropertiesDialog();

   }
   /**
    *
    */
   void menuHelpAbout_actionPerformed(ActionEvent e)
   {

   }
   /**
    * Loads the Society from the ini files which has been seleceted from the file open dialog,<p>
   * @see Method popupLoadSocietyFileOpenDialog
   * @param file File variable
   */

   void loadTheSystem(File file)
   {
      // Load the society from the ini files.
      society newSociety = theWorld.loadSocietyFromIniFiles(file, "contracts.xml", properties.getDebugParsers(), properties.getDebugContracts(), properties.getVerbose() );
      if( newSociety == null )
      {
         System.err.println("Unable to load the society");
         return;
      }

      //setting the title of the sam main window to the path of the file.
      this.setTitle(programName + " - " + file.toString() );

      properties.setPathName(file.toString()); //setting the path for the properties dialog box

      String Long_name = newSociety.displayedComponent.getLongName(); //setting the field name to the name of the displayed component
      MainWindow.theMainWindow.fieldname.setText(Long_name);

      theMainWindow.backandforward.addtoHistory(MainWindow.theMainWindow.theSociety.displayedComponent);

      //trying to fix the closing of the lower frame when yopu load a new society
      theMainWindow.jSplitPane3.setDividerLocation(700);
   }


///////////////////////////////////////USING THE XML TO LOAD THE FILES/////////////////////
   /**
   * Loads the society from the ".soc" file that has been selected from the dialog box<p>
   * @param File file
   */
   void loadTheSystemFromXml(File file)
   {
      // Load the society from the xml files.
      societyXmlLoader thesocietyxmlloader= new societyXmlLoader();

      society newSociety = thesocietyxmlloader.loadXml(file,"contracts.xml",properties.getDebugParsers(), properties.getVerbose());

      if( newSociety == null )
      {
         System.err.println("Unable to load the society");
         return;
      }

      //setting the title of the sam main window to the path of the file.
      this.setTitle(programName + " - " + file.toString() );

      properties.setPathName(file.toString()); //setting the path for the properties dialog box

      String Long_name = newSociety.displayedComponent.getLongName(); //setting the field name to the name of the displayed component
      MainWindow.theMainWindow.fieldname.setText(Long_name);

      theMainWindow.backandforward.addtoHistory(MainWindow.theMainWindow.theSociety.displayedComponent);

      //update_BackandForward();


      //trying to fix the closing of the lower frame when a new society is loaded
      theMainWindow.jSplitPane3.setDividerLocation(700);
   }
////////////////////////////////////////////////////////////////////////////
   /**
    * Calls the method to pop up a file save dialog box to save the society as  xml<p>
    * @param e ActionEvent
    */

   void menuFileSaveSociety_actionPerformed(ActionEvent e)
   {
      popupSaveXmlFileOpenDialog();
   }

///////////////////////////////////////////////////////////////////////////////////

   /**
    * Calls the method to pop up a file save dialog box to save society as ".soc " and the cluster details as ".ini" files<p>
    * @param e ActionEvent
    */

   void menuexporttoini_actionPerformed(ActionEvent e)
   {
      popupsavesocietyFileopenDialog();

   }
/////////////////////////////////////////////////////////////////////////
   /**Pops up a file save dialog box<p>
    *
    */
   private void popupsavesocietyFileopenDialog()
   {
      File defaultFile = new File(properties.getPathName());
      JFileChooser saveSocietyFileChooser = new JFileChooser();
      saveSocietyFileChooser.setBorder(BorderFactory.createLineBorder(Color.black));
      saveSocietyFileChooser.setCurrentDirectory(defaultFile);
      saveSocietyFileChooser.setFileFilter( samfilefilter );

      if(saveSocietyFileChooser.showSaveDialog(this) == saveSocietyFileChooser.APPROVE_OPTION)
      {
         File theirFile = saveSocietyFileChooser.getSelectedFile();
         if( theirFile == null )
         {
            System.err.println("Internal Error: Null file returned from JFileChooser in MainWindow::popupsavesocietyFileopenDialog");
            return;
         }

         savetheSystem( theirFile );
      }

   }//ends void popupsavesocietyfile

////////////////////////////////////////////////////////////////Method for opening the file save dialog box to write the xml/////////////

   private void popupSaveXmlFileOpenDialog()
   {
      File defaultFile = new File(properties.getPathName());
      JFileChooser saveSocietyXmlFileChooser = new JFileChooser();
      saveSocietyXmlFileChooser.setBorder(BorderFactory.createLineBorder(Color.black));
      saveSocietyXmlFileChooser.setCurrentDirectory(defaultFile);
      saveSocietyXmlFileChooser.setFileFilter( newsamfilefilter );


      if(saveSocietyXmlFileChooser.showSaveDialog(this) == saveSocietyXmlFileChooser.APPROVE_OPTION)
      {
         File theirFile = saveSocietyXmlFileChooser.getSelectedFile();

         if( theirFile == null )
         {
            System.err.println("Internal Error: Null file returned from JFileChooser in MainWindow::popupsavesocietyXmlFileopenDialog");
            return;
         }

         savetheXml( theirFile );
      }

   }//ends void popupsavesocietyfile


   /**
   * Saves the society as the society name that has been entered
   * @param file File variable
   */

   void savetheSystem(File file)
   {
      //save the society to the inifiles
     /* if(theWorld.savetofile(file,properties.getDebugSave()))
      {
        statusBar.setText("The society has been saved.");
      }*/
      if(theWorld.writeSocFile(file,properties.getDebugSave()))
      {
        statusBar.setText("The society has been saved.");
      }


   }//ends save the system
/////////////////////////////////////////Method for saving/writing the xml file///////////////////////////////

   /**
   * Saves/ writes out the society's XML
   * @param file File variable
   */

   void savetheXml(File file)
   {
      //get the path name for this file so that the contracts.xml can also be written to the same directory.
      String path = file.getParent();

      //save the society to the inifiles
      if(theSociety.societyXml(file, properties.getDebugSave()))
      {
        statusBar.setText("The society's XML has been written to the file:" + file.toString());
      }

      if(theSociety.theSystemContracts.saveContracts(path +"/" +"contracts.xml", properties.getDebugSave()))
      {
        statusBar.setText("The contracts's XML has been written to the file: " + path + "/" + "contracts.xml");
      }

    }//ends save the system


///////////////////////////////////////////////////////////////////////////////////////////////////////////////
   /**
   * Takes the user to a previous page<p>
   * @param e ActionEvent
   */
   void jButton_up_actionPerformed(ActionEvent e)
   {
      //trying to set the selected triangles/contracts
      samGraphController gc = (samGraphController)MainWindow.theMainWindow.theSociety.thePane.getGraphController();

      // clear any outstanding selections.There will only be one selection per society.
      ((samGraphController)theSociety.thePane.getGraphController()).clearSelections(false);

      if(!(gc.savedSite == null))
      { //if there is saved site
         if (!(gc.savedSite.isHighlighted()))
         {
            //if there is no highlighted site
            MainWindow.theMainWindow.setDisplayedText(" ");
         }
         else
         {
            //if there is a triangle/contract that is highlighted, then
            //get the contracts for the highlighted triangle
            MainWindow.theMainWindow.setDisplayedText(prettyprint.XML(gc.savedSite.getContract()));
         }
      }
      //to display the errors
      ResultsNode root = new ResultsNode();
      int warningLevel = 10;  //  for all
      boolean rtn = theWorld.analyze(root, warningLevel);


      //if there are no problems to report then set the split pane to the lowest positon
      //so as not to display the error pane.
      if( !rtn )
      {
         theMainWindow.jSplitPane3.setDividerLocation(700);
      }
      else
      {
         //if you find errors in the society then display them.
         // theSociety.theErrorDisplay = new DefaultTreeModel(root);
         displayResults( );
      }

      // Render the new graph.
      contractOwnerBase cob = (contractOwnerBase)backandforward.goBack();
      if( cob == null )
      {
         // Display an error messsage, there is no history and setting the button
         System.err.println("Error:  There is no page/record to go back to.");
      }
      else
      {
         //System.out.println(cob.getName());
         cob.renderGraph(MainWindow.theMainWindow.theSociety.theJGraph);

         //setting the lable name of the currently shown field
         String Long_name =MainWindow.theMainWindow.theSociety.displayedComponent.getLongName();
         fieldname.setText(Long_name);
      }

      //setting the forward and the back and parent buttons to grey out, depending on if there  is a page to go to.
      update_BackandForward();
   }

/////////////////////////////////////////////////////////////////////////////////////////////
   /**
    * Takes the user to the next page if available
    * @param e ActionEvent
    */

   void jButton_forward_actionPerformed(ActionEvent e)
   {
      //trying to set the selected triangles/contracts
      samGraphController gc = (samGraphController)MainWindow.theMainWindow.theSociety.thePane.getGraphController();

      // clear any outstanding selections.There will only be one selection per society.
      ((samGraphController)theSociety.thePane.getGraphController()).clearSelections(false);

      if(!(gc.savedSite == null))
      {
         //if there is no saved site
         if (!(gc.savedSite.isHighlighted()))
         {
            //if there is no highlighted site
            MainWindow.theMainWindow.setDisplayedText(" ");
         }
         else
         {
            //if there is a triangle/contract that is highlighted, then
            //get the contracts for the highlighted triangle
            MainWindow.theMainWindow.setDisplayedText(prettyprint.XML(gc.savedSite.getContract()));
         }

      }

      //to display the errors
      ResultsNode root = new ResultsNode();
      int warningLevel = 10;  //  for all
      boolean rtn = theWorld.analyze(root, warningLevel);


      //if there are no problems to report then set the split pane to the lowest positon
      //so as not to display the error pane.
      if( !rtn )
      {
         theMainWindow.jSplitPane3.setDividerLocation(700);
      }
      else
      {
         //if you find errors in the society then display them.
         // theSociety.theErrorDisplay = new DefaultTreeModel(root);
         displayResults( );
       }

      // Render the new graph.
      //display.displayedComponent.moveUp();
      contractOwnerBase cob = (contractOwnerBase)backandforward.goForward();
      if( cob == null )
      {
         // Display an error messsage, there is no history.
         System.err.println("Error:  There is no page/record to go forward to.");
      }
      else
      {
         cob.renderGraph(MainWindow.theMainWindow.theSociety.theJGraph);
      }

      //setting the forward and the back and parent buttons to grey out, depending on if there  is a page to go to.
      update_BackandForward();
   }

   //Method to display the parent of the displayed field
   /**
    * Displays the parant field of the displayed field<p>
    * @param e ActionEvent
    */

   void jButton_parent_actionPerformed(ActionEvent e)
   {
      ((samGraphController)theSociety.thePane.getGraphController()).clearSelections(false);
      MainWindow.theMainWindow.theSociety.displayedComponent.moveUp();
      backandforward.addtoHistory(theSociety.displayedComponent);

      //setting the lable name of the currently shown field
      String Long_name = MainWindow.theMainWindow.theSociety.displayedComponent.getLongName();
      fieldname.setText(Long_name);

      //setting the forward and the back and parent buttons to grey out, depending on if there  is a page to go to.
      update_BackandForward();

   }
   /**
   * checks if there is a page to go to and sets the setEnable to true fot the  back button <p>
   * if there is a page to go to,if there is no page to go to then sets it to false
   * Similar check is done for the back and the forward buttons
   */
   public void update_BackandForward()
   {
      //to check if there is a record to go forward to.
      if(theMainWindow.backandforward.haveFuture())
      {
         MainWindow.theMainWindow.jButton_forward.setEnabled(true);
      }
      else
      {
         MainWindow.theMainWindow.jButton_forward.setEnabled(false);
      }

      //to check if there is a record to go back to.
      if(theMainWindow.backandforward.havePast())
      {
         MainWindow.theMainWindow.jButton_up.setEnabled(true);
      }
      else
      {
         MainWindow.theMainWindow.jButton_up.setEnabled(false);
      }

      //to check if the record  has a parent.
      if(!(theMainWindow.theSociety.displayedComponent.getParent() == null) )
      {
         MainWindow.theMainWindow.jButton_parent.setEnabled(true);
      }
      else
      {
         MainWindow.theMainWindow.jButton_parent.setEnabled(false);
      }

   }
////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

   /// Write text into the lower left display window.
   /**
    * <B>static</B> Method
    * Displays the Contracts in the Editor pane.<p>
    * @param page String Variable
    */
   public static void setDisplayedText(String page)
   {
      jEditorPane1.setText(page);
      jEditorPane1.setDisabledTextColor(Color.black);

   }
////////////////////////////////////////////////////////////////////////////////////////////////////////
   /**
    * Checks to see if the Society has any associated problems<p>
    * @param e ActionEvent
    */

   void jButton_analyze_actionPerformed(ActionEvent e)
   {
      ResultsNode root = new ResultsNode();
      int warningLevel = 10;  // All
      boolean rtn = theWorld.analyze(root, warningLevel);

      // Everything OK?
      if( !rtn )
      {
         ResultsNode rec = new ResultsNode(root, "No problems found", null);
         root.children.add(rec);
      }

      theSociety.theErrorDisplay = new DefaultTreeModel(root);
      displayResults( );

   }
////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

   // Display the results of the analysis.
   /**
    * Displays the results from the analysis in the error pane<p>
    * @see jButton_analyze_actionPerformed(ActionEvent e)<p>
    */
   public void displayResults()
   {
      if( theSociety.theErrorDisplay != null )
      {
         jTree2.setModel( theSociety.theErrorDisplay );
         jScrollPane3.setVisible(true);
         jSplitPane3.setDividerLocation(0.6);
         jSplitPane3.setVisible(true);
      }
   }
//////////////////////////////////////////////////////////////////////////////////////////////////////////
   /**
    * Closes the results window
    */

   public void deactivateResultsWindow()
   {
      jScrollPane3.setVisible(false);
      jSplitPane3.setDividerLocation(1.0);
   }
/////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
   /**
    *Will call the method to Pop up a menu to close the Error pane when the mouse is pressed
    *@param e MouseEvent
    *@see showPopup(MouseEvent e)
    */

   void jTree2_mousePressed(MouseEvent e)
   {
      showPopup(e);
   }
   /**
    * Will call the method to Pop up a menu to close the error pane when the mouse is pressed
    * @param e MouseEvent
    * @see showPopup(MouseEvent e)
    */

   void jTree2_mouseReleased(MouseEvent e)
   {
      showPopup(e);
   }
   /**
    * will Popup the close menu to close the Error pane
    * @param e MouseEvent
    */

   private void showPopup(MouseEvent e)
   {
      if( e.isPopupTrigger())
      {
         jPopupMenu1.show(e.getComponent(), e.getX(), e.getY());
         theMainWindow.backandforward.addtoHistory(MainWindow.theMainWindow.theSociety.displayedComponent);

      }
   }


   /**
   * Will call the method to close the Error pane
   * @param e ActionEvent
   */

   void closeItem_actionPerformed(ActionEvent e)
   {
      deactivateResultsWindow();
   }

   /**
    * Calls the method that will draw the graph.
    * @param e ActionEvent
    */
   void menuViewLayout_actionPerformed(ActionEvent e)
   {
      MainWindow.theMainWindow.layoutJGraph();
   }


   // A private class to get resize events in the JGraph so we can do a new layout.
   /**
    * Class getResizeEvents will get the resize events in the Jgraph, so that a new lay out can be done.
    *
    */
   private class getResizeEvents implements ComponentListener
   {
      public void componentResized(ComponentEvent e)
      {
         // Re-layout the display
         MainWindow.theMainWindow.layoutJGraph();
      }

      public void componentMoved(ComponentEvent e)
      {
         //TODO: Implement this java.awt.event.ComponentListener method
      }

      public void componentShown(ComponentEvent e)
      {
         //TODO: Implement this java.awt.event.ComponentListener method
      }

      public void componentHidden(ComponentEvent e)
      {
         //TODO: Implement this java.awt.event.ComponentListener method
      }
   } //ends class getResizeEvents

   /**
    * Deletes the Sleceted object from the Graphical  representation
   * @return <code>boolean </code> true if the object is deleted<p>
   */

   // Delete the selected objects from the system.
   boolean deleteSelectedObjects()
   {
      // Get an iterator over the selected objects.
      Collection selections = ((samGraphController)theSociety.thePane.getGraphController()).getSelectedObjects();

      // clear the selections (so they will go away).
      ((samGraphController)theSociety.thePane.getGraphController()).clearSelections(false);

      // Delete each of the selected objects
      if( selections.size()==0)
      {
         JOptionPane.showMessageDialog(this,"Error:Please select the Object that you wish to delete ","Error",JOptionPane.ERROR_MESSAGE);
      }
      //clear all objects that are present in the clipboard
      cb.clear();

      Iterator it = selections.iterator();
      //looping through
      while( it.hasNext() )
      {
         UserObjectContainer uoc = (UserObjectContainer)it.next();
         BasicNode node = (BasicNode)uoc.getUserObject();
         contractOwnerBase rec = (contractOwnerBase)node.getSemanticObject();

         //take the object that has been deleted
         contractOwnerBase obj = MainWindow.theMainWindow.theSociety.displayedComponent.cutComponent(rec);

         //add each of these objects to the clipboard
         cb.addToClipboard(obj);

         //if there is a change in the society file then set the boolean to true indicating that there has been
         //a change
         MainWindow.theMainWindow.theSociety.files_changed = true;
      }


      // Tell the remaining objects to clear their dependencies.
      theWorld.clearMemberDependencies();

      // And then recompute them.
      theWorld.computeDependencies(null, false);

      // redisplay the graph after the removal of the selected object from the vector
      MainWindow.theMainWindow.theSociety.displayedComponent.renderGraph(MainWindow.theMainWindow.theSociety.theJGraph);

      return true;
   }
//////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

   //method to copy the selected objects
   /**
    * copies the selected object in the Graphical representation to the clip board
    * @return <code> boolean<\code> true if copied to the clip board
    */
   boolean copySelectedObjects()
   {
      // Get an iterator over the selected objects.
      Collection selections = ((samGraphController)theSociety.thePane.getGraphController()).getSelectedObjects();

      // clear the selections (so they will go away).
      ((samGraphController)theSociety.thePane.getGraphController()).clearSelections(false);

      // Delete each of the selected objects
      if( selections.size()==0)
      {
         JOptionPane.showMessageDialog(this,"Error:Please select the Object that you wish to copy ","Error",JOptionPane.ERROR_MESSAGE);
      }
      //clear all objects that are present in the clipboard
      cb.clear();

      Iterator it = selections.iterator();
      while( it.hasNext() )
      {
         UserObjectContainer uoc = (UserObjectContainer)it.next();
         BasicNode node = (BasicNode)uoc.getUserObject();
         contractOwnerBase rec = (contractOwnerBase)node.getSemanticObject();

         //add each of these objects to the clipboard
         cb.addToClipboard(rec);
      }


      // Tell the remaining objects to clear their dependencies.
      theWorld.clearMemberDependencies();

      // And then recompute them.
      theWorld.computeDependencies(null, false);

      // redisplay the graph after the removal of the selected object from the vector
      MainWindow.theMainWindow.theSociety.displayedComponent.renderGraph(MainWindow.theMainWindow.theSociety.theJGraph);

      return true;
   }

/**
 * Class showOptionpane
 * Shows the optionpane dialog box from which user needs <p>
 * to select the component that he desires to insert.
 */

class showOptionPane extends Component
{
   public static final int theContractType = 1;
   public static final int thePluginType = 2;
  // public static final int theNewPluginType = 3;
   public static final int theClusterType = 4;
   public static final int theNewClusterType = 5;
   public static final int theCommunityType = 6;
   public static final int theNewCommunityType = 7;
   public static final int theSaveType =8;


   showOptionPane()
   {
      //constructor
   }

   /**
    * Method  that will pop up the appropriate insert menu.
    * @param int  the type of the object that needs to be inserted.
    */
   public void popitup(int theTypeOfWindow)
   {
      boolean made_change = false;

      //if the type of the object desired to be inserted is Contract type
      if( theTypeOfWindow == theContractType )
      {
         //this should be changed later as soon as we have some kind of xml/contract editor.

         Object input =
         JOptionPane.showInputDialog(this,"Please select a contract",
         "choose a contract to insert",JOptionPane.QUESTION_MESSAGE,null,
         new String[] {"Contract 1","Contract 2","Contract 3","Conrtact 4","Contract 5",
         "contract 6","contract 7","Contract 8","Contract9","contract 10","Contract 11",
         "contract 12","contract 13","contract 14","contract 15","contract 16","contract 17",
         "contract 18","contract 19","contract 20","contract 21","contract 22"},"contract 10");

          System.out.println("the contract that has been selected is:"+ input);
      }

      //if not the contrscts then
      else if( theTypeOfWindow == thePluginType )
      {
         //trying to get the Optionpane so that the user can select the type of the plugin that needs to be inserted
         Object input =
         JOptionPane.showInputDialog(this,"Please select a Plugin", "choose a Plugin to insert",JOptionPane.QUESTION_MESSAGE,null,
         theSociety.getAvailablePlugins()," ");
         if(!(input == null))
         {
            //so that if the  user selects nothing and chooses cancel nothing is done
            //trying to insert objects in related fields only
            if((input instanceof Object) && (MainWindow.theMainWindow.theSociety.displayedComponent  instanceof cluster))
            {
               MainWindow.theMainWindow.theSociety.displayedComponent.addComponent((String)input);
               made_change = true;
               MainWindow.theMainWindow.theSociety.files_changed = true;
            }
            //if not throw an error message
            else
            {
               JOptionPane.showMessageDialog(this,"Error: You are trying to insert the object in an unrelated field ","Error",JOptionPane.ERROR_MESSAGE);

            }
         }

         //trying to set the lable in the main window
         MainWindow.theMainWindow.statusBar.setText(((String)input)+ " is being inserted into : "+ MainWindow.theMainWindow.theSociety.displayedComponent.getLongName());

      }

      //if not the plugin then
      else if( theTypeOfWindow == theNewClusterType )
      {
         boolean isInput = true;
         Object input = JOptionPane.showInputDialog(this, "Enter the name of the Cluster:");
         if(input != null && input instanceof String && ((String)input).length() > 0)
         {
            String newname = (String)input;

            //get all the availabel children in the present society
            Collection vec = theSociety.displayedComponent.getChildren();
            Iterator it = vec.iterator();
            while(it.hasNext())
            {
               //get the name of each of the object and compare it with the input name.
               contractOwnerBase cob = (contractOwnerBase)it.next();
               if(input.equals(cob.getName()))
               {
                  JOptionPane.showMessageDialog(this,"Error: An object with the same name already exists " + "\n Names of the objects must be unique across the society."
                  ,"Error",JOptionPane.ERROR_MESSAGE);
                  isInput = false;
                  break;
               }

            }
            if(isInput)
            {
                  if((input instanceof Object) && ((MainWindow.theMainWindow.theSociety.displayedComponent instanceof society) ||
                     (MainWindow.theMainWindow.theSociety.displayedComponent  instanceof community)))
                  {
                     //adding the components contracts to the sytem contracts list.
                     MainWindow.theMainWindow.theSociety.theSystemContracts.addNewClusterContracts(newname);

                     //adding the component to the society.
                     MainWindow.theMainWindow.theSociety.displayedComponent.addComponent(newname);

                     //getting the cluster by name
                     cluster clus = (cluster)MainWindow.theMainWindow.theSociety.getChildByName(newname);

                     //cretaing the new input bar and adding the input bars to the cluster
                     inputBar theInputBar = new inputBar(newname, clus);
                     clus.thePlugins.add(theInputBar);
                     outputBar theOutputBar = new outputBar(newname, clus);
                     clus.thePlugins.add(theOutputBar);

                     made_change = true;
                     MainWindow.theMainWindow.statusBar.setText(((String)input)+ " is being inserted into : "+ MainWindow.theMainWindow.theSociety.displayedComponent.getLongName());
                  }
                  else
                  {
                     JOptionPane.showMessageDialog(this,"Error: You are trying to insert the object in an unrelated field.  ","Error",JOptionPane.ERROR_MESSAGE);
                  }

            }
         }
         else
         {
         MainWindow.theMainWindow.statusBar.setText("Operation cancelled");
         }

      }
////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

      //if not the newcluster then
      else if(theTypeOfWindow ==theClusterType)
      {
         Object input =
         JOptionPane.showInputDialog(this,"Please select a Cluster",
         "choose a Cluster to insert",JOptionPane.QUESTION_MESSAGE,null,
         theSociety.getAvailableclusters()," ");
         if(!(input ==null) )
         {
            //trying to insert the objects in the related fields only
            if(( input instanceof Object) && ((MainWindow.theMainWindow.theSociety.displayedComponent  instanceof society) ||
                                             (MainWindow.theMainWindow.theSociety.displayedComponent  instanceof community)))
            {
               MainWindow.theMainWindow.theSociety.displayedComponent.addComponent((String)input);
               made_change = true;
            }
            else
            {
               //if not throw an error message
               JOptionPane.showMessageDialog(this,"Error: You are trying to insert the object in an unrelated field ","Error",JOptionPane.ERROR_MESSAGE);
            }
         }
       }



      //if not the cluster then
      else if(theTypeOfWindow ==theCommunityType)
      {
         Object input =
         JOptionPane.showInputDialog(this,"Please select a community",
         "choose a Community to insert",JOptionPane.QUESTION_MESSAGE,null,
         theSociety.getAvailableCommunities()," ");
         if(!(input ==null) )
         {
            //trying to insert the objects in the related fields only
            if(( input instanceof Object) && ((MainWindow.theMainWindow.theSociety.displayedComponent  instanceof society) ||
                                             (MainWindow.theMainWindow.theSociety.displayedComponent  instanceof community)))
            {
               MainWindow.theMainWindow.theSociety.displayedComponent.addHighLevelComponent((String)input);
               made_change = true;
            }
            else
            {
               //if not throw an error message
               JOptionPane.showMessageDialog(this,"Error: You are trying to insert the object in an unrelated field ","Error",JOptionPane.ERROR_MESSAGE);
            }
         }
       }


       //if not the plugin then
      else if( theTypeOfWindow == theNewCommunityType )
      {
         boolean isInput = true;
         Object input = JOptionPane.showInputDialog(this, "Enter the name of the Community:");
         if(input != null && input instanceof String && ((String)input).length() > 0)
         {
            String newname = (String)input;

            //get all the availabel children in the present society
            Collection vec = theSociety.displayedComponent.getChildren();
            Iterator it = vec.iterator();
            while(it.hasNext())
            {
               //get the name of each of the object and compare it with the input name.
               contractOwnerBase cob = (contractOwnerBase)it.next();
               if(input.equals(cob.getName()))
               {
                  JOptionPane.showMessageDialog(this,"Error: An object with the same name already exists " +"\n Names of the objects must be unique across the society.","Error",JOptionPane.ERROR_MESSAGE);
                  isInput = false;
                  break;
               }

            }
            if(isInput)
            {
                  if((input instanceof Object) && ((MainWindow.theMainWindow.theSociety.displayedComponent instanceof society) ||
                     (MainWindow.theMainWindow.theSociety.displayedComponent  instanceof community)))
                  {
                     //adding the contrcat sfor the component.
                     MainWindow.theMainWindow.theSociety.theSystemContracts.addNewCommunityContracts(newname);

                     //adding the community to the displayed component.
                     MainWindow.theMainWindow.theSociety.displayedComponent.addHighLevelComponent(newname);

                     //getting the cluster by name
                     community newCommunity = (community)MainWindow.theMainWindow.theSociety.getChildByName(newname);

                     //cretaing the new input bar and adding the input bars to the community
                     inputBar theInputBar = new inputBar(newname, newCommunity);
                     newCommunity.theChildren.add(theInputBar);
                     outputBar theOutputBar = new outputBar(newname, newCommunity);
                     newCommunity.theChildren.add(theOutputBar);



                     made_change = true;
                     MainWindow.theMainWindow.statusBar.setText(((String)input)+ " is being inserted into : "+ MainWindow.theMainWindow.theSociety.displayedComponent.getLongName());
                  }
                  else
                  {
                     JOptionPane.showMessageDialog(this,"Error: You are trying to insert the object in an unrelated field.  ","Error",JOptionPane.ERROR_MESSAGE);
                  }

            }
         }
         else
         {
         MainWindow.theMainWindow.statusBar.setText("Operation cancelled");
         }

      }
       //if not the community type then for the save type box for the closing of the tabbed pane.
      else if(theTypeOfWindow ==theSaveType)
      {
         int input =
         JOptionPane.showConfirmDialog(this,theWorld.theSocieties.toString(),
         "Save Modified Socities",JOptionPane.YES_NO_CANCEL_OPTION,JOptionPane.QUESTION_MESSAGE);
      }
      if( made_change )
      {
         // Now redisplay the graph.
         theSociety.displayedComponent.renderGraph(theSociety.theJGraph);

         // Notify that we have changed the model.
         theSociety.theTreeDisplay.reload();

         //if there is a change/any kind of insert in the society file then set the boolean to true indicating that there has been
         //a change
         MainWindow.theMainWindow.theSociety.files_changed = true;
      }
   }//ends method

} //ends class showoptionpane.

   /**
   * Calls the method to delete the selected object.<p>
   * @param e ActionEvent.
   */

   void cuteditsubmenu_actionPerformed(ActionEvent e)
   {
      // Delete the object which is currently selected.
      deleteSelectedObjects();
   }
} //ends class main window
////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
