/*  Title:        AssetSelectionGUIAppt.java
 *  Version:      v 1.0
 *  Copyright:    Copyright (c) 2001
 *  Author:       Henzil Browne
 *  Company:      21st Century Technologies, Inc
 *  Description:  This class represents the Asset Selection GUI Applet.
 *  Future:       Complete work on communication between applets on separate
                  web pages.
 */

package com.centurylogix.finalPredictiveAssessor.selector;

import java.awt.*;
import java.awt.event.*;
import java.applet.*;
import javax.swing.*;
import javax.swing.event.*;
import java.util.*;
import java.io.*;
import java.net.URL;
import java.net.MalformedURLException;
//import com.borland.jbcl.layout.*;
import javax.swing.border.*;

import netscape.javascript.*;

public class AssetSelectionGUIAppt extends JApplet
{
  boolean isStandalone = false;
  JPanel jPanel1 = new JPanel();
  JPanel jPanel2 = new JPanel();
  JPanel jPanel3 = new JPanel();

  //URL strings
  private static String string1;
  private static String urlString1;
  private static URL urlA;
  //Applet context variable
  private static AppletContext appletContext1;
  //Selected item: cluster and asset
  String selectedCluster;
  String selectedAsset;
  //Asset Selector Model class
  AssetSelectorModel ASM1 = new AssetSelectorModel ();
  //Vectors for holding the asset names and asset details
  private static Vector assetNames = new Vector();
  private static Vector assetDetails = new Vector();
  //Array lists for selection methods
  ArrayList A1 = ASM1.getClusters();
  ArrayList A2;
  ArrayList A3;
  Vector vec = new Vector(A1);
  //Button group for JRadioButtons
  ButtonGroup group1 = new ButtonGroup();
  //Time Unit Strings
  String [] timeUnits = new String [] {"Hours", "Days", "Weeks", "Months", "Years"};
  String selectedTimeUnit;
  //Selected Radio Button
  ButtonModel selectedButton;

  JComboBox jComboBox1 = new JComboBox(vec);
  JList jList1 = new JList();
  JEditorPane jEditorPane1 = new JEditorPane();
  JPanel jPanel4 = new JPanel();
  JPanel jPanel8 = new JPanel();
  JComboBox jComboBox2 = new JComboBox(timeUnits);
  JCheckBox jCheckBox1 = new JCheckBox();
  JCheckBox jCheckBox2 = new JCheckBox();
  JCheckBox jCheckBox3 = new JCheckBox();
  JCheckBox jCheckBox4 = new JCheckBox();
  JCheckBox jCheckBox5 = new JCheckBox();
  JPanel jPanel10 = new JPanel();
  JRadioButton jRadioButton1 = new JRadioButton();
  JRadioButton jRadioButton2 = new JRadioButton();
  JRadioButton jRadioButton3 = new JRadioButton();
  JRadioButton jRadioButton4 = new JRadioButton();
  JRadioButton jRadioButton5 = new JRadioButton();
  JPanel jPanel11 = new JPanel();
  JPanel jPanel12 = new JPanel();
  JPanel jPanel13 = new JPanel();
  JPanel jPanel14 = new JPanel();
  JPanel jPanel5 = new JPanel();
  JPanel jPanel6 = new JPanel();
  JPanel jPanel7 = new JPanel();
  TitledBorder titledBorder1;
  TitledBorder titledBorder2;
  TitledBorder titledBorder3;
  TitledBorder titledBorder4;
  TitledBorder titledBorder5;
  JButton jButton1 = new JButton();
  JButton jButton3 = new JButton();
  GridBagLayout gridBagLayout1 = new GridBagLayout();
  GridBagLayout gridBagLayout2 = new GridBagLayout();
  GridBagLayout gridBagLayout3 = new GridBagLayout();
  GridBagLayout gridBagLayout4 = new GridBagLayout();
  GridBagLayout gridBagLayout5 = new GridBagLayout();
  GridBagLayout gridBagLayout6 = new GridBagLayout();
  GridBagLayout gridBagLayout7 = new GridBagLayout();
  GridBagLayout gridBagLayout8 = new GridBagLayout();
  GridBagLayout gridBagLayout9 = new GridBagLayout();
  GridBagLayout gridBagLayout10 = new GridBagLayout();
  GridBagLayout gridBagLayout11 = new GridBagLayout();
  GridBagLayout gridBagLayout12 = new GridBagLayout();
  GridBagLayout gridBagLayout13 = new GridBagLayout();
  FlowLayout flowLayout1 = new FlowLayout();
  //Get a parameter value
  public String getParameter(String key, String def)
  {
    return isStandalone ? System.getProperty(key, def) :
      (getParameter(key) != null ? getParameter(key) : def);
  }

  //Construct the applet
  public AssetSelectionGUIAppt()
  {
  }

  //Populate ListBox with assets
  public void setListBox()
  {
    selectedCluster = (String)jComboBox1.getSelectedItem();
    ASM1.setSelectedCluster(selectedCluster);
    A2 = ASM1.getAssets(selectedCluster);
    String assetNamesString;
    assetNamesString = A2.toString();
    assetNames = parseList(assetNamesString);
  }

  //Asset details for editor panel
  public void setEditorPane1()
  {
    int index = jList1.getSelectedIndex();
    selectedAsset = (String)assetNames.elementAt(index);
    A3 = ASM1.assetDetails(selectedAsset);
    ASM1.setSelectedAsset(selectedAsset);
    String assetDetailsString;
    assetDetailsString = A3.toString();
    assetDetails = parseListAD(assetDetailsString);
  }


  //Initialize the applet
  public void init()
  {
    try
    {
      jbInit();
    }
    catch(Exception e)
    {
      e.printStackTrace();
    }
  }

  //Component initialization
  private void jbInit() throws Exception
  {
    titledBorder1 = new TitledBorder("Asset Details:");
    titledBorder2 = new TitledBorder("Events to be tracked:");
    titledBorder3 = new TitledBorder("Sensitivity Levels:");
    titledBorder4 = new TitledBorder("Time Units:");
    titledBorder5 = new TitledBorder("Notification Actions:");
    //this.setSize(new Dimension(580,320));
    this.setSize(new Dimension(470, 608));
    this.getContentPane().setLayout(gridBagLayout1);
    jPanel1.setLayout(gridBagLayout2);
    jPanel2.setLayout(gridBagLayout3);
    jPanel3.setLayout(gridBagLayout4);

    jList1.setBorder(BorderFactory.createLoweredBevelBorder());
    jList1.setToolTipText("Select an asset to view its details");
    jList1.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
    jList1.setVisibleRowCount(10);
    jList1.addMouseListener(new java.awt.event.MouseAdapter()
    {
      public void mouseClicked(MouseEvent e)
      {
        jList1_mouseClicked(e);
      }
    });
    jComboBox1.setBorder(BorderFactory.createLoweredBevelBorder());
    jComboBox1.setToolTipText("Select a cluster to view available assets");
    jComboBox1.setSelectedIndex(-1);
    jComboBox1.addActionListener(new java.awt.event.ActionListener()
    {
      public void actionPerformed(ActionEvent e)
      {
        jComboBox1_actionPerformed(e);
      }
    });
    jEditorPane1.setCaretColor(Color.black);
    jEditorPane1.setBackground(Color.lightGray);
    jEditorPane1.setBorder(BorderFactory.createLoweredBevelBorder());
    jEditorPane1.setEditable(false);
    JScrollPane listScrollPane = new JScrollPane(jList1);
    JScrollPane textScrollPane = new JScrollPane(jEditorPane1);
    jPanel1.setBorder(BorderFactory.createLoweredBevelBorder());
    jPanel1.setMaximumSize(new Dimension(467, 650));
    jPanel1.setMinimumSize(new Dimension(467, 614));
    jPanel1.setPreferredSize(new Dimension(467, 614));
    jPanel8.setLayout(gridBagLayout12);
    jPanel8.setBorder(titledBorder5);
    jCheckBox1.setSelected(true);
    jCheckBox1.setText("Asset Shortfalls");
    jCheckBox2.setSelected(true);
    jCheckBox2.setText("Asset Surpluses");
    jCheckBox3.setSelected(true);
    jCheckBox3.setText("Notify via LogPlan Alerts");
    jCheckBox4.setText("Notify via pop dialog windows");
    jCheckBox5.setText("Log Threshold violations to file \\ database");
    jPanel10.setLayout(gridBagLayout8);
    jRadioButton1.setToolTipText("Warn me only when sure about predictions concerning above events");
    jRadioButton1.setSelected(true);
    jRadioButton1.setText("Very Low");
    jRadioButton1.setActionCommand("VeryLow");
    jRadioButton2.setText("Low");
    jRadioButton2.setActionCommand("Low");
    jRadioButton3.setText("Average");
    jRadioButton3.setActionCommand("Average");
    jRadioButton4.setText("High");
    jRadioButton4.setActionCommand("High");
    jRadioButton5.setToolTipText("Warn me whenever you slightly suspect one of above events will occur");
    jRadioButton5.setText("Very High");
    jRadioButton5.setActionCommand("VeryHigh");

    //Group the radio buttons
    textScrollPane.setBorder(titledBorder1);
    jComboBox2.setBackground(Color.lightGray);
    jComboBox2.setBorder(titledBorder4);
    jButton1.setText("Close");
    jButton1.addActionListener(new java.awt.event.ActionListener()
    {
       public void actionPerformed(ActionEvent e)
      {
        jButton1_actionPerformed(e);
      }
    });
    jButton3.setText("Submit");
    jButton3.addActionListener(new java.awt.event.ActionListener()
    {

      public void actionPerformed(ActionEvent e)
      {
        jButton3_actionPerformed(e);
      }
    });
    flowLayout1.setHgap(50);
    group1.add(jRadioButton1);
    group1.add(jRadioButton2);
    group1.add(jRadioButton3);
    group1.add(jRadioButton4);
    group1.add(jRadioButton5);

    jPanel11.setLayout(gridBagLayout6);
    jPanel12.setLayout(gridBagLayout10);
    jPanel13.setLayout(gridBagLayout7);
    jPanel14.setLayout(gridBagLayout9);
    jPanel5.setLayout(flowLayout1);
    jPanel6.setBorder(titledBorder2);
    jPanel6.setLayout(gridBagLayout11);
    jPanel7.setBorder(titledBorder3);
    jPanel7.setLayout(gridBagLayout13);
    jPanel4.setLayout(gridBagLayout5);
    this.getContentPane().add(jPanel1, new GridBagConstraints(0, 0, 1, 1, 1.0, 1.0
            ,GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(0, 0, 0, 0), 3, -29));
    jPanel1.add(jPanel2, new GridBagConstraints(0, 0, 1, 1, 1.0, 1.0
            ,GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(5, 1, 0, 0), 2, 15));
    jPanel2.add(jComboBox1, new GridBagConstraints(0, 0, 1, 1, 1.0, 0.0
            ,GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, new Insets(10, 8, 0, 2), 93, 1));
    jPanel2.add(listScrollPane, new GridBagConstraints(0, 1, 1, 1, 1.0, 1.0
            ,GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(9, 8, 16, 2), -42, 14));
    jPanel1.add(jPanel3, new GridBagConstraints(1, 0, 1, 1, 3.0, 3.0
            ,GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(5, 0, 0, 2), 100, 14));
    jPanel3.add(textScrollPane, new GridBagConstraints(0, 0, 1, 1, 1.0, 1.0
            ,GridBagConstraints.EAST, GridBagConstraints.BOTH, new Insets(5, 7, 15, 7), 100, 166));
    jPanel1.add(jPanel4, new GridBagConstraints(0, 1, 2, 1, 1.0, 1.0
            ,GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(0, 1, 0, 2), 0, -4));
    jPanel4.add(jPanel11, new GridBagConstraints(0, 0, 1, 1, 1.0, 1.0
            ,GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(5, 0, 0, 0), 2, 0));
    jPanel11.add(jPanel13, new GridBagConstraints(0, 0, 1, 1, 1.0, 1.0
            ,GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(8, 4, 0, 0), 9, 9));
    jPanel13.add(jPanel6, new GridBagConstraints(0, 0, 1, 1, 1.0, 1.0
            ,GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(0, 9, 8, 10), 62, -2));
    jPanel6.add(jCheckBox1, new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0
            ,GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(0, 11, 0, 60), 0, 0));
    jPanel6.add(jCheckBox2, new GridBagConstraints(0, 1, 1, 1, 0.0, 0.0
            ,GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(0, 11, 5, 60), 0, 0));
    jPanel11.add(jPanel10, new GridBagConstraints(0, 1, 1, 1, 1.0, 1.0
            ,GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(2, 12, 0, 0), 7, 2));
    jPanel10.add(jComboBox2, new GridBagConstraints(0, 0, 1, 1, 1.0, 0.0
            ,GridBagConstraints.SOUTH, GridBagConstraints.HORIZONTAL, new Insets(1, 0, 1, 9), 152, 0));
    jPanel11.add(jPanel14, new GridBagConstraints(0, 2, 2, 1, 1.0, 1.0
            ,GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(12, 4, 0, 2), 10, 8));
    jPanel14.add(jPanel8, new GridBagConstraints(0, 0, 1, 1, 1.0, 1.0
            ,GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(4, 8, 7, 14), 155, -1));
    jPanel8.add(jCheckBox3, new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0
            ,GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(5, 11, 0, 250), 7, -3));
    jPanel8.add(jCheckBox4, new GridBagConstraints(0, 1, 1, 1, 0.0, 0.0
            ,GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(0, 11, 0, 220), 7, -3));
    jPanel8.add(jCheckBox5, new GridBagConstraints(0, 2, 1, 1, 0.0, 0.0
            ,GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(0, 11, 5, 155), 5, -3));
    jPanel11.add(jPanel12, new GridBagConstraints(1, 0, 1, 2, 1.0, 1.0
            ,GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(8, 17, 0, 2), 13, 3));
    jPanel12.add(jPanel7, new GridBagConstraints(0, 0, 1, 1, 1.0, 1.0
            ,GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(0, 11, 1, 14), 94, 2));
    jPanel7.add(jRadioButton1, new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0
            ,GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(0, 13, 0, 96), 0, 0));
    jPanel7.add(jRadioButton2, new GridBagConstraints(0, 1, 1, 1, 0.0, 0.0
            ,GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(0, 13, 0, 117), 0, 0));
    jPanel7.add(jRadioButton3, new GridBagConstraints(0, 2, 1, 1, 0.0, 0.0
            ,GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(0, 13, 0, 96), 0, 0));
    jPanel7.add(jRadioButton4, new GridBagConstraints(0, 3, 1, 1, 0.0, 0.0
            ,GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(0, 13, 0, 114), 0, 0));
    jPanel7.add(jRadioButton5, new GridBagConstraints(0, 4, 1, 1, 0.0, 0.0
            ,GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(0, 13, 9, 96), 0, 0));
    jPanel1.add(jPanel5, new GridBagConstraints(0, 2, 2, 1, 1.0, 1.0
            ,GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, new Insets(22, 1, 7, 2), 174, 7));
    jPanel5.add(jButton1, null);
    jPanel5.add(jButton3, null);
  }


  //Get Applet information
  public String getAppletInfo()
  {
    return "Applet Information";
  }

  //Get parameter info
  public String[][] getParameterInfo()
  {
    return null;
  }

  /* Parses the asset names string list */
  protected static Vector parseList(String theStringList)
  {
    String stringList;
    int i;
    char cH1 =']';
    i = theStringList.lastIndexOf(cH1);
    stringList = theStringList.substring(1, i);
    Vector v = new Vector(10);
    StringTokenizer tokenizer = new StringTokenizer(stringList, ", ");
    while (tokenizer.hasMoreTokens())
    {
      String image = tokenizer.nextToken();
      v.addElement(image);
    }
    return v;
  }

  /* Parse asset details string list */
  protected static Vector parseListAD(String theStringList)
  {
    String stringList;
    int i;
    char cH1 =']';
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

  //Main method
  public static void main(String[] args)
  {
    AssetSelectionGUIAppt applet = new AssetSelectionGUIAppt();
    applet.isStandalone = true;
    JFrame frame = new JFrame();
    frame.setTitle("TrendAnalysis PlugIn Asset Selection");
    frame.getContentPane().add(applet, BorderLayout.CENTER);
    applet.init();
    applet.start();
    frame.setSize(470, 625);
    Dimension d = Toolkit.getDefaultToolkit().getScreenSize();
    frame.setLocation((d.width - frame.getSize().width) / 2, (d.height - frame.getSize().height) / 2);
    frame.setVisible(true);
  }

  void jComboBox1_actionPerformed(ActionEvent e)
  {
      if (jComboBox1.getSelectedIndex() == -1)
      {
          return;
      }
      setListBox();
      jList1.setFixedCellWidth(0);
      jList1.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
      jList1.setListData(assetNames);
  }

  void jList1_mouseClicked(MouseEvent e)
  {
    if (jList1.getSelectedIndex() == -1 )
    {
          return;
    }
    jEditorPane1.setText("");
    setEditorPane1();
    StringBuffer details = new StringBuffer();
    for (int c = 0; c < (assetDetails.size()); c += 2)
    {
      details.append ((String)assetDetails.get(c) + ":" + "  ");
      details.append ((String)assetDetails.get(c + 1) + "\n");
    }
    jEditorPane1.setText(details.toString());
  }

  /* Close button */
  void jButton1_actionPerformed(ActionEvent e)
  {
    JSObject win = JSObject.getWindow(this);
    win.call("windowClose", null);
  }
  /* Done button */
  void jButton3_actionPerformed(ActionEvent e)
  {
    //Return control and do nothing if no asset is selected
    if (jList1.getSelectedIndex() == -1 )
    {
          return;
    }

    //Set Events to be Tracked
    ASM1.setShortfalls((jCheckBox1.isSelected()));
    ASM1.setSurpluses((jCheckBox2.isSelected()));

    //Set Notification Protocols
    boolean snp1 = (jCheckBox3.isSelected());
    boolean snp2 = (jCheckBox4.isSelected());
    boolean snp3 = (jCheckBox5.isSelected());
    ASM1.setNotificationActions(snp1, snp2, snp3);
    selectedButton = group1.getSelection();
    //Set Sensitivity Levels
    ASM1.setSensitivityLevels((selectedButton.getActionCommand()));
    //Set Time Units
    selectedTimeUnit = (String)jComboBox2.getSelectedItem().toString().trim();
    ASM1.setTimeUnits(selectedTimeUnit);

    //Test if Request is Valid
    if (ASM1.submitRequest())
    {
    }

    //Request is not Valid:  Dialog Window
    else
    {
      JOptionPane.showMessageDialog(null, "Selected Cluster or Asset Unavailable!",
      "Inane error", JOptionPane.ERROR_MESSAGE);
    }

  }
}


