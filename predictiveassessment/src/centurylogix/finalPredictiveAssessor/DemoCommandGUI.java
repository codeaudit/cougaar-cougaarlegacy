/**
 *  @file         DemoCommandGUI.java
 *  @copyright    Copyright (c) 2001
 *  @author       Henzil Browne
 *  @company      21st Century Technologies, Inc
 *  @description
 *  @history      Created July 2, 2001.
 *  @todo
 **/

package com.centurylogix.finalPredictiveAssessor;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

public class DemoCommandGUI extends JFrame
{
  JPanel contentPane;

  JButton jButton1 = new JButton();
  JButton jButton2 = new JButton();
  JButton jButton3 = new JButton();
  JButton jButton4 = new JButton();
  JButton jButton5 = new JButton();

  JPanel jPanel1 = new JPanel();
  JPanel jPanel2 = new JPanel();
  JPanel jPanel3 = new JPanel();
  JPanel jPanel4 = new JPanel();
  JPanel jPanel5 = new JPanel();
  JPanel jPanel6 = new JPanel();

  BorderLayout borderLayout1 = new BorderLayout();
  GridBagLayout gridBagLayout1 = new GridBagLayout();

  TimeSeriesDisplayPlugIn tsDisplayPlugIn;

  /**Construct the frame*/
  public DemoCommandGUI(TimeSeriesDisplayPlugIn tsdp)
  {
    this.tsDisplayPlugIn = tsdp;
    enableEvents(AWTEvent.WINDOW_EVENT_MASK);

    try
    {
      jbInit();
    }
    catch(Exception e)
    {
      e.printStackTrace();
    }

    this.show();
  }
  /**Component initialization*/
  private void jbInit() throws Exception
  {
    //setIconImage(Toolkit.getDefaultToolkit().createImage(TimeSeriesDisplayGUI.class.getResource("[Your Icon]")));
    contentPane = (JPanel) this.getContentPane();
    contentPane.setLayout(borderLayout1);
    jPanel1.setLayout(gridBagLayout1);

    //this.setSize(new Dimension(250, 210));
    this.setBounds(775, 20, 250, 210);
  // x, y, width, height
    this.setTitle("21st CT Demo Commands");


    jButton1.setText("Perform Cross Correlation");
    jButton1.setActionCommand("crossCorrelate");
    jButton1.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(ActionEvent e) {
        jButton1_actionPerformed(e);
      }
    });

    jButton2.setText("Begin Moving Avererage Analysis");
    jButton2.setActionCommand("movingAverage");
    jButton2.addActionListener(new java.awt.event.ActionListener()
    {
      public void actionPerformed(ActionEvent e)
      {
        jButton2_actionPerformed(e);
      }
    });


    jButton3.setText("Begin Auto-Correlation Analysis");
    jButton3.setActionCommand("autoCorrelate");
    jButton3.addActionListener(new java.awt.event.ActionListener()
    {
      public void actionPerformed(ActionEvent e)
      {
        jButton3_actionPerformed(e);
      }
    });


    jButton4.setText("Begin Pattern Identification Analysis");
    jButton4.setActionCommand("patternPredict");
    jButton4.addActionListener(new java.awt.event.ActionListener()
    {
      public void actionPerformed(ActionEvent e)
      {
        jButton4_actionPerformed(e);
      }
    });

    jButton5.setText("Begin Mixed Signal Analysis");
    jButton5.setActionCommand("mixedSignal");
    jButton5.addActionListener(new java.awt.event.ActionListener()
    {
      public void actionPerformed(ActionEvent e)
      {
        jButton5_actionPerformed(e);
      }
    });

    contentPane.add(jPanel1, BorderLayout.CENTER);

    jPanel1.add(jPanel2, new GridBagConstraints(0, 0, 1, 1, 1.0, 1.0
            ,GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(2, 0, 0, 0), 50, 24));
    jPanel2.add(jButton1, null);

    jPanel1.add(jPanel3, new GridBagConstraints(0, 1, 1, 1, 1.0, 1.0
            ,GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(0, 0, 0, 0), 125, 29));
    jPanel3.add(jButton2, null);

    jPanel1.add(jPanel4, new GridBagConstraints(0, 2, 1, 1, 1.0, 1.0
            ,GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(0, 0, 0, 0), 125, 28));
    jPanel4.add(jButton3, null);

    jPanel1.add(jPanel5, new GridBagConstraints(0, 3, 1, 1, 1.0, 1.0
            ,GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(0, 0, 4, 0), 121, 28));
    jPanel5.add(jButton4, null);

    jPanel1.add(jPanel6, new GridBagConstraints(0, 4, 1, 1, 1.0, 1.0
            ,GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(0, 0, 4, 0), 121, 28));
    jPanel6.add(jButton5, null);

  }
  /**Overridden so we can exit when window is closed*/
  protected void processWindowEvent(WindowEvent e)
  {
    super.processWindowEvent(e);
    if (e.getID() == WindowEvent.WINDOW_CLOSING)
    {
      System.exit(0);
    }
  }

  void jButton1_actionPerformed(ActionEvent e)
  {
    if( tsDisplayPlugIn.publishMessage(jButton1.getActionCommand()) )
      jButton1.setEnabled (false);
  }

  void jButton2_actionPerformed(ActionEvent e)
  {

    if( tsDisplayPlugIn.publishMessage(jButton2.getActionCommand()) )
      jButton2.setEnabled (false);
  }

  void jButton3_actionPerformed(ActionEvent e)
  {
    if( tsDisplayPlugIn.publishMessage(jButton3.getActionCommand()) )
      jButton3.setEnabled (false);
  }

  void jButton4_actionPerformed(ActionEvent e)
  {
    if( tsDisplayPlugIn.publishMessage(jButton4.getActionCommand()) )
      jButton4.setEnabled (false);
  }

  void jButton5_actionPerformed(ActionEvent e)
  {
    if( tsDisplayPlugIn.publishMessage(jButton5.getActionCommand()) )
      jButton5.setEnabled (false);
  }

}// end class

