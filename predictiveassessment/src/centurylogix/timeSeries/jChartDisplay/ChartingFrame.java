/**
 *  @file         ChartingFrame.java
 *  @copyright    Copyright (c) 2001
 *  @author       Nicolas Rundquist, Abraham DeLaO and Henzil Browne
 *  @company      21st Century Technologies, Inc
 *  @description
 *  @history      Created Fall 1999 for ALP. Heavily modified June 2001 for UltraLog
 *  @todo
 **/


package com.centurylogix.timeSeries.jChartDisplay;

import java.awt.*;
import java.awt.event.*;
import jclass.chart.*;
import java.util.*;
import java.util.Properties;
import java.util.Date;
import java.text.DateFormat;
import javax.swing.*;
import javax.swing.border.*;
import javax.swing.tree.*;
import javax.swing.event.*;

import java.io.Serializable;

// this imported simly to assist in test routing listed in main
import com.centurylogix.timeSeries.*;

public class ChartingFrame extends JFrame implements JCPickListener
{

  BorderLayout borderLayout1 = new BorderLayout();
  TitledBorder border1 = new TitledBorder("Predictive Analysis Select");
  TitledBorder border2 = new TitledBorder("Time Series Predictive Analysis Display");
  JPanel panel1 = new JPanel();
  JPanel panel2 = new JPanel();
  //TreeControl treeControl1 = new TreeControl();
  JTree tree1;
  DefaultTreeCellRenderer renderer;
  DefaultTreeModel treeModel1;
  BorderLayout borderLayout2 = new BorderLayout();
  JTabbedPane tabsetPanel1 = new JTabbedPane();
  BorderLayout borderLayout3 = new BorderLayout();
  SimpleChart simpleChart1 = new SimpleChart();
  JSplitPane splitPane1 = new JSplitPane();

  //Added for selected data streams
  JCheckBox[] jCheckBox;
  CheckBoxListener myListener = new CheckBoxListener();


  ChartDataViewSeries cdvs;

//  Properties props = System.getProperties();
  String rootDirectory = "guiIcons";

  private DateFormat dateFormat = DateFormat.getDateInstance(DateFormat.LONG);

  private static final long dayInMillis = 1000 * 60 * 60 * 24;

  private ChartingFrameDataModel dataModel = null;

  public ChartingFrame()
  {
    //System.out.println ("Beginning of constructor in AnalysisClientFrame");

    try
    {
      jbInit();
    }
    catch(Exception e)
    {
      e.printStackTrace();
    }

    //System.out.println ("End of constructor in AnalysisClientFrame");

    this.setTitle("Predictive Time Series Analysis Results");
    this.setBounds(50, 50, 650, 650);
    this.show();
  } // end ChartingFrme ()

  //Component initialization
  private void jbInit() throws Exception
  {
    //System.out.println ("Beginning of jbinit in AnalysisClientFrame");

    try { UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName()); }
    catch (Exception e)	{ System.err.println("Error loading L&F: " + e); }

    this.getContentPane().setLayout(borderLayout1);
    this.setSize(new Dimension(600, 650));

    panel1.setBackground(SystemColor.control);
    panel1.setBorder(border1);
    panel1.setMinimumSize(new Dimension(200, 600));
    panel1.setLayout(borderLayout2);
    panel1.setPreferredSize(new Dimension(200,200));
    panel2.setBorder(border2);
    panel2.setMaximumSize(new Dimension(467, 650));
    panel2.setLayout(borderLayout3);
    panel2.setBackground(SystemColor.control);
    panel2.setPreferredSize(new Dimension(467, 607));

    simpleChart1.setTrigger(0, new EventTrigger(0, EventTrigger.PICK));
    simpleChart1.setBackground(Color.white);
    //This method adds a break/discontinuity for null or negative data
    simpleChart1.getDataView(0).setHoleValue(-1);
    simpleChart1.setTrigger(0, new EventTrigger(0, EventTrigger.PICK));
    simpleChart1.setAllowUserChanges(true);
    simpleChart1.addPickListener(this);

    simpleChart1.setData("ARRAY 'No Data' 0 0 \n");// + "'X-axis Values' 0.0 1.0 2.0 3.0 4.0 5.0 \n" +
                  //"'Time Series Values' 0 0 0 0 0 0 \n");

    simpleChart1.setChartType(jclass.chart.JCChart.AREA);
    simpleChart1.setXAxisTitleText("Time");
    simpleChart1.setYAxisTitleText("Units");
    //Added for selected data streams

    ChartPanel.setMaximumSize(new Dimension(450, 548));
    ChartPanel.setMinimumSize(new Dimension(450, 548));
    ChartPanel.setPreferredSize(new Dimension(450, 548));
    startDayField.setMinimumSize(new Dimension(44, 21));
    dayRangeField.setMinimumSize(new Dimension(44, 21));
    tabsetPanel1.setMaximumSize(new Dimension(455, 579));
    tabsetPanel1.setPreferredSize(new Dimension(455, 579));
    jPanel2.setMaximumSize(new Dimension(440, 540));
    jPanel1.setMaximumSize(new Dimension(250, 40));

    simpleChart1.setLegendIsShowing(true);
    simpleChart1.setLegendAnchor(jclass.chart.JCLegend.SOUTHWEST);

    treeModel1 = new DefaultTreeModel(new DefaultMutableTreeNode("Select"));
    tree1 = new JTree(treeModel1);

    tree1.addTreeSelectionListener(new TreeSelectionListener()
    {
      public void valueChanged(TreeSelectionEvent tse)
      {
        TreePath tp = tse.getNewLeadSelectionPath();
        String chartName = tp.getLastPathComponent().toString();

        //System.out.println ("chart name is : " + chartName);

        DefaultMutableTreeNode root = (DefaultMutableTreeNode)treeModel1.getRoot();
        DefaultMutableTreeNode node;
        DefaultMutableTreeNode save_node = null;
        Enumeration e1 = root.breadthFirstEnumeration();
        while (e1.hasMoreElements())
        {
          node = (DefaultMutableTreeNode)e1.nextElement();
          //System.out.println ("node is : " + node.toString());
          if ((node.toString()).compareTo(chartName) == 0)
            save_node = node;
        }

        if (save_node.getLevel() == 1)
        {
          //System.out.println ("Setting current chart to : " + chartName);
          dataModel.setCurrentGroup (chartName);
          refreshChart ();
          //clearCheckBoxes();
        }

        //dataModel.setCurrentGroup (chartName);
        //refreshChart ();
      }
    });

    renderer = new DefaultTreeCellRenderer();
    renderer.setLeafIcon(new ImageIcon(rootDirectory + "/right_arrow.gif"));
    renderer.setOpenIcon(new ImageIcon(rootDirectory + "/down_arrow.gif"));
    renderer.setClosedIcon(new ImageIcon(rootDirectory + "/right_arrow.gif"));
    tree1.setCellRenderer(renderer);

    splitPane1.setBackground(Color.lightGray);
    splitPane1.setToolTipText("Adjust Size");
    splitPane1.setDividerSize(3);
    ChartPanel.setLayout(gridBagLayout1);
    fullGraphButton.setText("Show Full Graph");
    fullGraphButton.setActionCommand("full_graph");

    fullGraphButton.addActionListener(new java.awt.event.ActionListener()
    {
      public void actionPerformed(ActionEvent e)
      {
        fullGraphButton_actionPerformed(e);
      }
    });

    fullGraphButton.setSelected (true);
    cropGraphButton.setText("Crop Graph");
    cropGraphButton.setActionCommand("crop_graph");

    cropGraphButton.addActionListener(new java.awt.event.ActionListener()
    {
      public void actionPerformed(ActionEvent e)
      {
        cropGraphButton_actionPerformed(e);
      }
    });

    ButtonGroup group1 = new ButtonGroup();
    showCurrentButton.setSelected(true);
    showCurrentButton.setEnabled(false);
    showCurrentButton.setText("Show Current Information");
    showCurrentButton.setActionCommand("showCurrent");
    showCurrentButton.addActionListener(new java.awt.event.ActionListener()
    {
      public void actionPerformed(ActionEvent e)
      {
        showCurrentButton_actionPerformed(e);
      }
    });

    showOldButton.setEnabled(false);
    showOldButton.setText("Set Start Unit");
    showOldButton.setActionCommand("showOld");
    showOldButton.addActionListener(new java.awt.event.ActionListener()
    {
      public void actionPerformed(ActionEvent e)
      {
        showOldButton_actionPerformed(e);
      }
    });

    rangeLabel.setEnabled(false);
    rangeLabel.setMinimumSize(new Dimension(100, 17));
    rangeLabel.setPreferredSize(new Dimension(100, 17));
    rangeLabel.setText("Number of Time Units to Display");
    startDayLabel.setEnabled(false);
    startDayLabel.setText("Start Unit");
    startDayField.setEnabled(false);
    startDayField.setColumns(4);
    startDayField.addFocusListener(new java.awt.event.FocusAdapter()
    {
      public void focusLost(FocusEvent e)
      {
        readStartIndex(e);
      }
    });
    startDayField.addKeyListener(new java.awt.event.KeyAdapter()
    {
      public void keyTyped(KeyEvent e)
      {
        readStartIndex(e);
      }
    });

    dayRangeField.setEnabled(false);
    dayRangeField.setColumns(4);
    dayRangeField.addFocusListener(new java.awt.event.FocusAdapter()
    {
      public void focusLost(FocusEvent e)
      {
        readRange(e);
      }
    });
    dayRangeField.addKeyListener(new java.awt.event.KeyAdapter()
    {
      public void keyTyped(KeyEvent e)
      {
        readRange(e);
      }
    });


    displayButton.setToolTipText("Change Chart Display Format");
    displayButton.setText("Display");
    displayButton.addActionListener(new java.awt.event.ActionListener()
    {
      public void actionPerformed(ActionEvent e)
      {
        displayButton_actionPerformed(e);
      }
    });

    infoPanel.setLayout(flowLayout1);
    //infoPanel.setLayout(verticalFlowLayout1);

    c.gridx = 0;
    c.gridy = 0;
    c.anchor = GridBagConstraints.NORTHWEST;
    c.weightx = 1.0;
    c.weighty = 0.0;
    iconLabel1.setIcon(imageIcon1);
    iconLabel1.setPreferredSize(new Dimension(imageIcon1.getIconWidth(),
                              imageIcon1.getIconHeight()));
    //iconPanel.setMaximumSize(new Dimension(100, 100));
    //iconPanel.setMinimumSize(new Dimension(40, 40));
    //iconPanel.setPreferredSize(new Dimension(80, 80));
    iconPanel.setPreferredSize(new Dimension(imageIcon1.getIconWidth(),
                              imageIcon1.getIconHeight()));
    iconPanel.setOpaque(false);
    iconPanel.setLayout(borderLayout4);

    c.gridy = 1;
    aboutLabel1.setFont(new java.awt.Font("Dialog", 0, 12));
    aboutLabel1.setText("Trend Analysis");

    c.gridy = 2;
    aboutLabel2.setFont(new java.awt.Font("Dialog", 0, 12));
    aboutLabel2.setText("21st Century Technologies");

    c.gridy = 3;
    c.weighty = 1.0;
    aboutLabel3.setFont(new java.awt.Font("Dialog", 0, 12));
    aboutLabel3.setText("Copyright (c) 2001");

    infoPanel.setBackground(Color.lightGray);
    infoPanel.setMaximumSize(new Dimension(100, 100));
    infoPanel.setPreferredSize(new Dimension(100, 100));
    infoPanel.setOpaque(false);

    dayRangeLabel.setText("Demo not started");

    resetButton.setToolTipText("Reset the Display");
    resetButton.setActionCommand("Reset");
    resetButton.setText("Reset");
    resetButton.addActionListener(new java.awt.event.ActionListener()
    {
      public void actionPerformed(ActionEvent e) {
        resetButton_actionPerformed(e);
      }
    });

    pauseButton.setToolTipText("Pause the Collection of Data");
    pauseButton.setText("  Pause  ");
    pauseButton.addActionListener(new java.awt.event.ActionListener()
    {
      public void actionPerformed(ActionEvent e) {
        pauseButton_actionPerformed(e);
      }
    });


    jEditorPane1.setText("jEditorPane1");
    group1.add (fullGraphButton);
    group1.add (cropGraphButton);
    ButtonGroup group2 = new ButtonGroup();
    group2.add (showCurrentButton);
    group2.add (showOldButton);
    this.getContentPane().add(splitPane1, BorderLayout.CENTER);
    splitPane1.add(panel1, JSplitPane.LEFT);
    panel1.add(tree1, BorderLayout.CENTER);
    splitPane1.add(panel2, JSplitPane.RIGHT);
    panel2.add(tabsetPanel1, BorderLayout.CENTER);
    tabsetPanel1.addTab("About", infoPanel);
    infoPanel.add(iconPanel, null);
    iconPanel.add(iconLabel1, BorderLayout.CENTER);
    infoPanel.add(aboutLabel1, null);
    infoPanel.add(aboutLabel2, null);
    infoPanel.add(aboutLabel3, null);
    tabsetPanel1.addTab("Analysis", ChartPanel);
    ChartPanel.add(dayRangeLabel, new GridBagConstraints(0, 0, 3, 1, 0.0, 0.0
            ,GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(5, 5, 0, 12), 451, 0));
    ChartPanel.add(simpleChart1, new GridBagConstraints(0, 1, 3, 1, 1.0, 1.0
            ,GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(0, 5, 0, 5), 95, 182));
    ChartPanel.add(fullGraphButton, new GridBagConstraints(0, 4, 2, 1, 0.0, 0.0
            ,GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(0, 5, 0, 0), 101, 0));
    ChartPanel.add(cropGraphButton, new GridBagConstraints(0, 5, 2, 1, 0.0, 0.0
            ,GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(0, 5, 0, 10), 140, 0));
    ChartPanel.add(showCurrentButton, new GridBagConstraints(2, 4, 1, 1, 0.0, 0.0
            ,GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 46, 0));
    ChartPanel.add(showOldButton, new GridBagConstraints(2, 5, 1, 1, 0.0, 0.0
            ,GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 113, 0));
    ChartPanel.add(startDayLabel, new GridBagConstraints(2, 6, 1, 1, 0.0, 0.0
            ,GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(0, 18, 0, 0), 222, 0));
    ChartPanel.add(startDayField, new GridBagConstraints(2, 7, 1, 1, 1.0, 0.0
            ,GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(0, 19, 5, 0), -10, 0));
    ChartPanel.add(dayRangeField, new GridBagConstraints(0, 7, 1, 1, 1.0, 0.0
            ,GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(0, 23, 5, 0), -10, 0));
    ChartPanel.add(jPanel1, new GridBagConstraints(0, 3, 3, 1, 1.0, 1.0
            ,GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(0, 87, 0, 67), 429, 0));
    jPanel1.add(displayButton, null);
    jPanel1.add(pauseButton, null);
    jPanel1.add(resetButton, null);
    ChartPanel.add(rangeLabel, new GridBagConstraints(0, 6, 2, 1, 0.0, 0.0
            ,GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(0, 23, 0, 0), 195, 0));
    ChartPanel.add(jPanel2, new GridBagConstraints(0, 2, 3, 1, 1.0, 1.0
            ,GridBagConstraints.EAST, GridBagConstraints.BOTH, new Insets(0, 100, 0, 70), 376, 19));
    /*
    assetDetailPane1.setLayout(gridBagLayout2);
    analysisDetailPane1.setLayout(gridBagLayout3);
    tabsetPanel1.addTab("Asset Details", assetDetailPane1);
    assetDetailPane1.add(jScrollPane1, new GridBagConstraints(0, 0, 1, 1, 1.0, 1.0
            ,GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(16, 10, 16, 9), 0, 503));
    jScrollPane1.getViewport().add(jEditorPane1, null);
    tabsetPanel1.addTab("Analysis Details", analysisDetailPane1);
    analysisDetailPane1.add(jScrollPane2, new GridBagConstraints(0, 0, 1, 1, 1.0, 1.0
            ,GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(17, 10, 16, 10), 0, 502));
    */

    jScrollPane2.getViewport().add(jEditorPane2, null);
    tabsetPanel1.setSelectedComponent(ChartPanel);
    //aboutPanel.add(imageControl1, null);
    //imageControl1.setImageName( rootDirectory + "/bluer.gif");
    splitPane1.setTopComponent(panel1);
    splitPane1.setBottomComponent(panel2);
    splitPane1.setDividerLocation(175);

  } // end private void jbinit ()

  /********************************************************************************/
  /*************** end private void jbinit() ***************************************/
  /********************************************************************************/

  void treeControl1_actionPerformed(ActionEvent e)
  {
  }

  // These are just member variables in a wierd place
  JPanel ChartPanel = new JPanel();
  JRadioButton fullGraphButton = new JRadioButton();
  JRadioButton cropGraphButton = new JRadioButton();
  JTextField dayRangeField = new JTextField();
  JRadioButton showCurrentButton = new JRadioButton();
  JRadioButton showOldButton = new JRadioButton();
  JTextField startDayField = new JTextField();
  JLabel rangeLabel = new JLabel();
  JLabel startDayLabel = new JLabel();
  JButton displayButton = new JButton();

  GridBagConstraints c = new GridBagConstraints();

  //JPanel aboutPanel = new JPanel();
  JPanel infoPanel = new JPanel();
  JPanel iconPanel = new JPanel();
  JLabel iconLabel1 = new JLabel();
  ImageIcon imageIcon1 = new ImageIcon(rootDirectory + "/21st_Century_Final_logo.jpg");
  JLabel aboutLabel1 = new JLabel();
  BorderLayout borderLayout4 = new BorderLayout();
  JLabel aboutLabel2 = new JLabel();
  JLabel aboutLabel3 = new JLabel();

 // JPanel assetDetailPane1 = new JPanel();
 // JPanel analysisDetailPane1 = new JPanel();

  //ImageControl imageControl1 = new ImageControl();
  JLabel dayRangeLabel = new JLabel();
  JPanel jPanel1 = new JPanel();
  JButton resetButton = new JButton();
  JButton pauseButton = new JButton();
  JPanel jPanel2 = new JPanel();
  GridBagLayout gridBagLayout1 = new GridBagLayout();
  //VerticalFlowLayout verticalFlowLayout1 = new VerticalFlowLayout();
  JScrollPane jScrollPane1 = new JScrollPane();
  JEditorPane jEditorPane1 = new JEditorPane();
  JScrollPane jScrollPane2 = new JScrollPane();
  JEditorPane jEditorPane2 = new JEditorPane();
  GridBagLayout gridBagLayout2 = new GridBagLayout();
  GridBagLayout gridBagLayout3 = new GridBagLayout();
  FlowLayout flowLayout1 = new FlowLayout();

  /**
   * Captures events that involve the button that commands for the entire time series span
   * be shown. All other graphic field relating to the cropping of the viewable time series
   * are cleared and diabled.  They must be enabled again via radio buttions to accept
   * cropping commands. The current shown chart is refreshed to show all time series points.
   */
  void fullGraphButton_actionPerformed(ActionEvent e)
  {
    if (fullGraphButton.isSelected())
    {
      rangeLabel.setEnabled (false);
      dayRangeField.setEnabled (false);
      showCurrentButton.setEnabled (false);
      showOldButton.setEnabled (false);
      startDayLabel.setEnabled (false);
      startDayField.setEnabled (false);

      this.dataModel.setFullChartViewIndices();
      refreshChart();
    }
  }

  /** TODO: really unsure about the behavior of this stuff and if refreshing is needed
   * Captures events that involve the button that commands for the entire time series span
   * be shown. All other graphic field relating to the cropping of the viewable time series
   * are cleared and diabled.  They must be enabled again via radio buttions to accept
   * cropping commands. The current shown chart is refreshed to show all time series points.
   */
  void cropGraphButton_actionPerformed(ActionEvent e)
  {
    if (cropGraphButton.isSelected ())
    {
      rangeLabel.setEnabled (true);
      dayRangeField.setEnabled (true);
      showCurrentButton.setEnabled (true);
      showOldButton.setEnabled (true);
      if (showOldButton.isSelected())
      {
        startDayLabel.setEnabled (true);
        startDayField.setEnabled (true);
      }
      else
      {
        startDayLabel.setEnabled (false);
        startDayField.setEnabled(false);
      }
    }
  }

  // TODO: again unsure about behavior
  void showCurrentButton_actionPerformed(ActionEvent e)
  {
    if (showCurrentButton.isSelected())
    {
      startDayLabel.setEnabled (false);
      startDayField.setEnabled (false);
    }
    //myOwner.displayChart(currentChart);
    //refreshChart();
  }

  // TODO: again unsure about behavior
  void showOldButton_actionPerformed(ActionEvent e)
  {
    if (showOldButton.isSelected())
    {
      startDayLabel.setEnabled (true);
      startDayField.setEnabled (true);
   }
    //myOwner.displayChart(currentChart);
//    myOwner.displayChart();
  }

  ///////////////////////////////////////////////////////////////

  // todo: this obviously must change alot
  public void addAnalysisGroup (String groupName)
  {
    //System.out.println ("Adding tree element " + groupName);
    DefaultMutableTreeNode root = (DefaultMutableTreeNode)treeModel1.getRoot();
    DefaultMutableTreeNode node, groupNode;
    DefaultMutableTreeNode save_node = null;

    Enumeration e1 = root.breadthFirstEnumeration();
    while (e1.hasMoreElements())
    {
      node = (DefaultMutableTreeNode)e1.nextElement();
      if ((node.toString()).compareTo(groupName) == 0)
        save_node = node;
    }

    if(save_node == null)
    {
      save_node = new DefaultMutableTreeNode(groupName);
      treeModel1.insertNodeInto(save_node,root,root.getChildCount());
    }

    // since only have one layer for now, this seems unnecessary, will need later
        // for supplemental info. ie. thresholds, actions

    //squadron_node = new DefaultMutableTreeNode(squadron);
    //treeModel1.insertNodeInto(squadron_node, save_node,save_node.getChildCount());

    tree1.makeVisible(new TreePath(save_node.getPath()));

    //System.out.println ("finished adding new asset to analysis frame");
    /* -- already commented out
    GraphLocation aefRoot = treeControl1.find(aef);
    if (aefRoot == null)
    {
      aefRoot = treeControl1.addChild(root, aef);
    }
    treeControl1.addChild(aefRoot, squadron);
  */
  } // end void addAsset



  /*Returns the range of days entered by user
  private Integer getDisplayedTimeUnits ()
  {
    if (cropGraphButton.isSelected())
    {
      return new Integer (currentRange);
    }
    else
    {
      return null;
    }
  }
*/

  void displayButton_actionPerformed(ActionEvent e)
  {
    if(simpleChart1.getChartType()!=jclass.chart.JCChart.AREA)
    {
      simpleChart1.setChartType(jclass.chart.JCChart.AREA);
    }
    else
    {
      simpleChart1.setChartType(jclass.chart.JCChart.PLOT);
    }
  }

  /**
   * When cropping their view of the time series chart, a user can set a number of time units to view.
   * When a value is entered into text field range value, this method
   * is called. It retrieves the entered range and stores it to the data model. It then
   * requests for the chart to be refreshed according to the cropping input.
   */
  private void readRange(KeyEvent e)
  {
    if (e.getKeyChar() == '\n')
    {
      try
      {
        String fieldText = dayRangeField.getText();
        int range = Integer.parseInt(fieldText);

        this.dataModel.setUnitsToView(range);
        refreshChart();
      }
      catch (NumberFormatException ex)
      {
        JOptionPane.showMessageDialog (new JFrame (), "The given range is not valid");

        Integer unitsToView = dataModel.getUnitsToView();
        if (unitsToView != null)
          dayRangeField.setText(unitsToView.toString());
        else
          dayRangeField.setText ("");
      }
    } // if
  } // end private void readRange (KeyEvent)


  /**
   * When cropping their view of the time series chart, a user can set a number of units to view.
   * When the mouse focus leaves the text field where the start index is entered, this method
   * is called. It retrieves the entered range and stores it to the data model. It then
   * requests for the chart to be refreshed according to the cropping input.
   */
  void readRange(FocusEvent e)
  {
    if (e.getID() == FocusEvent.FOCUS_LOST && !e.isTemporary())
    {
      try
      {
        String fieldText = dayRangeField.getText();
        int range = Integer.parseInt(fieldText);
        dataModel.setUnitsToView(range);
        refreshChart();
      }
      catch (NumberFormatException ex)
      {
        JOptionPane.showMessageDialog (new JFrame (), "The given range is not valid.");

        Integer unitsToView = dataModel.getUnitsToView();
        if (unitsToView != null)
          dayRangeField.setText(unitsToView.toString());
        else
          dayRangeField.setText ("");
      }
    } // if
  } // readRange

  //This method sets the date range displayed at the top of the graph
  private void updateDayRange (int firstIndex, int lastIndex, long startTime)
  {
     //Current time unit being used
     long timeStep = dataModel.getIncrement();

     long firstDate_L = startTime + timeStep * firstIndex;
     long lastDate_L = startTime + timeStep * lastIndex;

     String startDate_Str = dateFormat.format (new Date (firstDate_L));
     String endDate_Str = dateFormat.format (new Date (lastDate_L));

     dayRangeLabel.setText ("Displaying from " + startDate_Str + " to " + endDate_Str);

  }

  /*
  //This method displays the asset details
  void setAssetDetails(HashMap AssetDetailsHM)
  {
    String assetDetailsString = AssetDetailsHM.toString();
    Vector assetDetails = new Vector();
    assetDetails = parseListAD(assetDetailsString);
    jEditorPane1.setText("");
    StringBuffer details = new StringBuffer();
 //
    for (int c = 0; c < (assetDetails.size()); c++)
    {
      details.append ((String)assetDetails.get(c) + "\n");
    }
    jEditorPane1.setText(details.toString());
  }*/

  /**
   * When cropping their view of the time series chart, a user sets a starting viewing index.
   * When the mouse focus leaves the text field where the start index is entered, this method
   * is called. It retrieves the entered start index and stores it to the data model. It then
   * requests for the chart to be refreshed according to the cropping input.
   */
  private void readStartIndex (FocusEvent e)
  {
    if (e.getID() == FocusEvent.FOCUS_LOST && !e.isTemporary())
    {
      try
      {
        String fieldText = startDayField.getText();
        int startIndex = Integer.parseInt(fieldText);

        this.dataModel.setStartIndex (startIndex);
        refreshChart();
      }
      catch (NumberFormatException ex)
      {
        JOptionPane.showMessageDialog (new JFrame (), "The given start offset is not valid.");
        Integer startIndex = dataModel.getStartIndex();
        if (startIndex != null)
          startDayField.setText(startIndex.toString());
        else
          startDayField.setText("");
      }
    } // if
  } // end privat int readStartIndex (FocusEvent)

  /**
   * When cropping their view of the time series chart, a user sets a starting viewing index.
   * When a value is entered into the text field of the start index, this method
   * is called. It retrieves the entered start index and stores it to the data model. It then
   * requests for the chart to be refreshed according to the cropping input.
   */
  private void readStartIndex(KeyEvent e)
  {
    if (e.getKeyChar() == '\n')
    {
      try
      {
        String fieldText = startDayField.getText();
        int startIndex = Integer.parseInt(fieldText);

        this.dataModel.setStartIndex (startIndex);
        refreshChart();

        int startDay = Integer.parseInt(fieldText);
      }
      catch (NumberFormatException ex)
      {
        JOptionPane.showMessageDialog (new JFrame (), "The given start index is not valid.");
        Integer startIndex = dataModel.getStartIndex();
        if (startIndex != null)
          startDayField.setText(startIndex.toString());
        else
          startDayField.setText("");
      }
    } // if
  } // end private int readStartIndex (KeyEvent)

  // TODO : decide if this should be a CLEAR button, this for later
  void resetButton_actionPerformed(ActionEvent e)
  {
    dayRangeLabel.setText("No data to view");
    DefaultMutableTreeNode root = (DefaultMutableTreeNode)treeModel1.getRoot();
    root.removeAllChildren();

    dataModel.clear ();
    refreshChart ();
  }

  void pauseButton_actionPerformed(ActionEvent e)
  {
    dataModel.pause();

    if (pauseButton.getText().trim().equals("Pause"))
    {
      pauseButton.setText("Resume");
      pauseButton.setToolTipText("Resume the Collection of Data");
    }
    else
    {
      pauseButton.setText ("Pause");
      pauseButton.setToolTipText("Pause the Collection of Data");
    }
  }

  //Pick listener which restricts charting view to an individual stream selected
  public void pick (JCPickEvent e)
  {
     JCDataIndex di = e.getPickResult();
     int srs = 0;
     if (di != null)
     {
        Object obj = di.getObject();
        ChartDataView vw = di.getDataView();
        srs = di.getSeriesIndex();
        //ChartDataViewSeries cdvs;
        int dist = di.getDistance();
        int numSeries =  vw.getNumSeries();

        if (vw != null && srs != -1)
        {
          if (srs >= 0)
          {
            if ((obj instanceof JCLegend) || (obj instanceof JCChartArea && dist == 0))
            {
              for (int i=0; i < numSeries; i++)
              {
                if (i == srs)
                  continue;
                cdvs = vw.getSeries(i);
                cdvs.setIsShowing(false);
                cdvs.setIsShowingInLegend(true);
                jCheckBox[i].setSelected(false);
              }
            }
            else
            {
              for (int j=0; j < numSeries; j++)
              {
                if (j == srs)
                  continue;
                cdvs = vw.getSeries(j);
                cdvs.setIsShowing(true);
                cdvs.setIsShowingInLegend(true);
                jCheckBox[j].setSelected(true);
              }
            } // end else
          } //end if (srs)
        } // end if (vw != null && srs != -1)
     }// end if (di)
  } //end public void pick ()

  //This method dynamically creates the checkboxes for individual series
  public void createCheckBoxes()
  {
     int numSeries = simpleChart1.getDataView(0).getNumSeries();
     jCheckBox = new JCheckBox[numSeries];

     for (int i=0, j=1; i < numSeries; i++, j++)
     {
       jCheckBox[i] = new JCheckBox();
       jCheckBox[i].setSelected(true);
       jCheckBox[i].setText("Series" + j);
       jCheckBox[i].addItemListener(myListener);
       jPanel2.add(jCheckBox[i]);
     }

      //jPanel2.validate();
  }

  public void clearCheckBoxes()
  {
    jPanel2.removeAll();
    jPanel2.validate();

    /*
    int numSeries = simpleChart1.getDataView(0).getNumSeries();

    for (int i = 0; i < numSeries; i++)
    {
      cdvs = simpleChart1.getDataView(0).getSeries(i);
      cdvs.setIsShowing(false);
      cdvs.setIsShowingInLegend(true);
    }*/
  }

  class CheckBoxListener implements ItemListener
  {
        public void itemStateChanged(ItemEvent e)
        {
            //Integer integer;
            //char ch = new char();
            //String buttonIndex;
            JCheckBox jcb = new JCheckBox();
            jcb = (JCheckBox)e.getItem();
            String buttonIndex = jcb.getText();
            //char ch = buttonIndex.charAt(6);
            Character ch = new Character(buttonIndex.charAt(6));
            Integer integer = new Integer(ch.toString());

            if (e.getStateChange() == ItemEvent.DESELECTED)
            {
             hideSeries(integer.intValue() - 1);
            }

            else if(e.getStateChange() == ItemEvent.SELECTED)
            {
             showSeries(integer.intValue() - 1);
            }
        }
    }

    //This method displays the series represented by the series number
    void showSeries(int seriesNumber)
    {
       cdvs = simpleChart1.getDataView(0).getSeries(seriesNumber);
       cdvs.setIsShowing(true);
       cdvs.setIsShowingInLegend(true);
    }

    //This method hides the series represented by the series number
    void hideSeries(int seriesNumber)
    {
       cdvs = simpleChart1.getDataView(0).getSeries(seriesNumber);
       cdvs.setIsShowing(false);
       cdvs.setIsShowingInLegend(true);
    }

    /*
    //This method parses the list of asset details
    protected static Vector parseListAD(String theStringList)
    {
       String stringList;
       int i;
       char cH1 ='}';
       i = theStringList.lastIndexOf(cH1);
       stringList = theStringList.substring(1, i);
       Vector v = new Vector(10);
       StringTokenizer tokenizer = new StringTokenizer(stringList, ",");
       while (tokenizer.hasMoreTokens())
       {
          String image = tokenizer.nextToken();
          image.trim();
          v.addElement(image);
       }
       return v;
    }
*/
  protected void setModel (ChartingFrameDataModel cfdm)
  {
    this.dataModel = cfdm;
  }

  protected void refreshChart ()
  {
    // get the String data representation for the current chart with appropriate croppings
    String chartString = dataModel.getChartString();

    // update the viewed chart
    this.simpleChart1.setData(chartString);

    int start = dataModel.getStartIndexToView();
    int end = dataModel.getEndIndexToView();
    long startTime = dataModel.getStartTime();

    updateDayRange (start, end, startTime);

    clearCheckBoxes();
    createCheckBoxes();
  }


  //Temp for Debug
  public static void main (String[] args)
  {
    ChartingFrame cf = new ChartingFrame ();

    TimeSeriesValue tsv0_one = new TimeSeriesValue ("Test1", new Double (5), 0, 5);
    TimeSeriesValue tsv1_one = new TimeSeriesValue ("Test1", new Double (6), 5, 5);
    TimeSeriesValue tsv2_one = new TimeSeriesValue ("Test1", new Double (10), 10, 5);
    TimeSeriesValue tsv3_one = new TimeSeriesValue ("Test1", new Double (12), 15, 5);
    TimeSeriesValue tsv4_one = new TimeSeriesValue ("Test1", new Double (16), 20, 5);
    TimeSeriesValue tsv5_one = new TimeSeriesValue ("Test1", new Double (22), 25, 5);
    TimeSeriesValue tsv6_one = new TimeSeriesValue ("Test1", new Double (27), 30, 5);
    TimeSeriesValue tsv7_one = new TimeSeriesValue ("Test1", new Double (30), 35, 5);
    TimeSeriesValue tsv8_one = new TimeSeriesValue ("Test1", new Double (25), 40, 5);
    TimeSeriesValue tsv9_one = new TimeSeriesValue ("Test1", new Double (20), 45, 5);
    TimeSeriesValue tsv10_one = new TimeSeriesValue ("Test1", new Double (16), 50, 5);
    TimeSeriesValue tsv11_one = new TimeSeriesValue ("Test1", new Double (14), 55, 5);
    TimeSeriesValue tsv12_one = new TimeSeriesValue ("Test1", new Double (11), 60, 5);
    TimeSeriesValue tsv13_one = new TimeSeriesValue ("Test1", new Double (9), 65, 5);
    TimeSeriesValue tsv14_one = new TimeSeriesValue ("Test1", new Double (6), 70, 5);

    // add these values to a time series
    TimeSeries ts = new TimeSeries (tsv0_one);
    ts.addElement(tsv1_one);
    ts.addElement(tsv2_one);
    ts.addElement(tsv3_one);
    ts.addElement(tsv4_one);
    ts.addElement(tsv5_one);
    ts.addElement(tsv6_one);
    ts.addElement(tsv7_one);
    ts.addElement(tsv8_one);
    ts.addElement(tsv9_one);
    ts.addElement(tsv10_one);
    ts.addElement(tsv11_one);
    ts.addElement(tsv12_one);
    ts.addElement(tsv13_one);
    ts.addElement(tsv14_one);

    TimeSeriesValue tsv0_two = new TimeSeriesValue ("Test2", new Double (15), 0, 5);
    TimeSeriesValue tsv1_two = new TimeSeriesValue ("Test2", new Double (12), 5, 5);
    TimeSeriesValue tsv2_two = new TimeSeriesValue ("Test2", new Double (11), 10, 5);
    TimeSeriesValue tsv3_two = new TimeSeriesValue ("Test2", new Double (10), 15, 5);
    TimeSeriesValue tsv4_two = new TimeSeriesValue ("Test2", new Double (6), 20, 5);
    TimeSeriesValue tsv5_two = new TimeSeriesValue ("Test2", new Double (2), 25, 5);
    TimeSeriesValue tsv6_two = new TimeSeriesValue ("Test2", new Double (1), 30, 5);
    TimeSeriesValue tsv7_two = new TimeSeriesValue ("Test2", new Double (3), 35, 5);
    TimeSeriesValue tsv8_two = new TimeSeriesValue ("Test2", new Double (4), 40, 5);
    TimeSeriesValue tsv9_two = new TimeSeriesValue ("Test2", new Double (6), 45, 5);
    TimeSeriesValue tsv10_two = new TimeSeriesValue ("Test2", new Double (6), 50, 5);
    TimeSeriesValue tsv11_two = new TimeSeriesValue ("Test2", new Double (8), 55, 5);
    TimeSeriesValue tsv12_two = new TimeSeriesValue ("Test2", new Double (9), 60, 5);
    TimeSeriesValue tsv13_two = new TimeSeriesValue ("Test2", new Double (13), 65, 5);
    TimeSeriesValue tsv14_two = new TimeSeriesValue ("Test2", new Double (16), 70, 5);

    // add these values to a time series
    TimeSeries ts2 = new TimeSeries (tsv0_two);
    ts2.addElement(tsv1_two);
    ts2.addElement(tsv2_two);
    ts2.addElement(tsv3_two);
    ts2.addElement(tsv4_two);
    ts2.addElement(tsv5_two);
    ts2.addElement(tsv6_two);
    ts2.addElement(tsv7_two);
    ts2.addElement(tsv8_two);
    ts2.addElement(tsv9_two);
    ts2.addElement(tsv10_two);
    ts2.addElement(tsv11_two);
    ts2.addElement(tsv12_two);
    ts2.addElement(tsv13_two);
    ts2.addElement(tsv14_two);

    TimeSeries[] tsArray = new TimeSeries [] {ts, ts2};

    ChartingFrameDataModel dm = new ChartingFrameDataModel (cf);

    dm.addTimeSeriesGroup(tsArray, "Test");


    TimeSeriesValue tsv0_three = new TimeSeriesValue ("Test3", new Double (27), 0, 5);
    TimeSeriesValue tsv1_three = new TimeSeriesValue ("Test3", new Double (12), 5, 5);
    TimeSeriesValue tsv2_three = new TimeSeriesValue ("Test3", new Double (11), 10, 5);
    TimeSeriesValue tsv3_three = new TimeSeriesValue ("Test3", new Double (10), 15, 5);
    TimeSeriesValue tsv4_three = new TimeSeriesValue ("Test3", new Double (2), 20, 5);
    TimeSeriesValue tsv5_three = new TimeSeriesValue ("Test3", new Double (31), 25, 5);
    TimeSeriesValue tsv6_three = new TimeSeriesValue ("Test3", new Double (21), 30, 5);
    TimeSeriesValue tsv7_three = new TimeSeriesValue ("Test3", new Double (23), 35, 5);
    TimeSeriesValue tsv8_three = new TimeSeriesValue ("Test3", new Double (24), 40, 5);
    TimeSeriesValue tsv9_three = new TimeSeriesValue ("Test3", new Double (13), 45, 5);
    TimeSeriesValue tsv10_three = new TimeSeriesValue ("Test3", new Double (6), 50, 5);
    TimeSeriesValue tsv11_three = new TimeSeriesValue ("Test3", new Double (2), 55, 5);
    TimeSeriesValue tsv12_three = new TimeSeriesValue ("Test3", new Double (3), 60, 5);
    TimeSeriesValue tsv13_three = new TimeSeriesValue ("Test3", new Double (3), 65, 5);
    TimeSeriesValue tsv14_three = new TimeSeriesValue ("Test3", new Double (6), 70, 5);

    // add these values to a time series
    TimeSeries ts3 = new TimeSeries (tsv0_three);
    ts3.addElement(tsv1_three);
    ts3.addElement(tsv2_three);
    ts3.addElement(tsv3_three);
    ts3.addElement(tsv4_three);
    ts3.addElement(tsv5_three);
    ts3.addElement(tsv6_three);
    ts3.addElement(tsv7_three);
    ts3.addElement(tsv8_three);
    ts3.addElement(tsv9_three);
    ts3.addElement(tsv10_three);
    ts3.addElement(tsv11_three);
    ts3.addElement(tsv12_three);
    ts3.addElement(tsv13_three);
    ts3.addElement(tsv14_three);

    TimeSeries[] tsArray2 = new TimeSeries [] {ts3};

    dm.addTimeSeriesGroup(tsArray2, "Test");

  } // end main

} // end class

