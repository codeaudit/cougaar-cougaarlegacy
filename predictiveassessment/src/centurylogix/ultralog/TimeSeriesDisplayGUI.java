package com.centurylogix.ultralog;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import com.borland.jbcl.layout.*;
//import com.borland.jbcl.layout.*;

public class TimeSeriesDisplayGUI extends JFrame {
  JPanel contentPane;
  JPanel jPanel1 = new JPanel();
  JButton jButton1 = new JButton();
  JButton jButton2 = new JButton();
  JButton jButton3 = new JButton();
  JButton jButton4 = new JButton();
  TimeSeriesDisplayPlugIn tsDisplayPlugIn;
  JPanel jPanel2 = new JPanel();
  JPanel jPanel3 = new JPanel();
  JPanel jPanel4 = new JPanel();
  JPanel jPanel5 = new JPanel();
  BorderLayout borderLayout1 = new BorderLayout();
  GridBagLayout gridBagLayout1 = new GridBagLayout();

  /**Construct the frame*/
  public TimeSeriesDisplayGUI(TimeSeriesDisplayPlugIn tsdp)
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
    System.out.println ("About to show new TimeSeriesDisplayGUI");
    this.show();
  }
  /**Component initialization*/
  private void jbInit() throws Exception  {
    //setIconImage(Toolkit.getDefaultToolkit().createImage(TimeSeriesDisplayGUI.class.getResource("[Your Icon]")));
    contentPane = (JPanel) this.getContentPane();
    contentPane.setLayout(borderLayout1);
    this.setSize(new Dimension(217, 274));
    this.setTitle("Time Series Action");
    jPanel1.setLayout(gridBagLayout1);
    jButton1.setText("Perform Time Series");
    jButton1.setActionCommand("Perform Time Series");
    jButton1.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(ActionEvent e) {
        jButton1_actionPerformed(e);
      }
    });
    jButton2.setText("jButton2");
    jButton2.addActionListener(new java.awt.event.ActionListener()
    {
      public void actionPerformed(ActionEvent e)
      {
        jButton2_actionPerformed(e);
      }
    });
    jButton2.setActionCommand("jButton2");
    jButton3.setText("jButton3");
    jButton3.addActionListener(new java.awt.event.ActionListener()
    {
      public void actionPerformed(ActionEvent e)
      {
        jButton3_actionPerformed(e);
      }
    });
    jButton3.setActionCommand("jButton3");
    jButton4.setText("jButton4");
    jButton4.addActionListener(new java.awt.event.ActionListener()
    {
      public void actionPerformed(ActionEvent e)
      {
        jButton4_actionPerformed(e);
      }
    });
    jButton4.setActionCommand("jButton4");
    contentPane.add(jPanel1, BorderLayout.CENTER);
    jPanel1.add(jPanel2, new GridBagConstraints(0, 0, 1, 1, 1.0, 1.0
            ,GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(2, 0, 0, 0), 50, 24));
    jPanel2.add(jButton1, null);
    jPanel1.add(jPanel3, new GridBagConstraints(0, 1, 1, 1, 1.0, 1.0
            ,GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(0, 0, 0, 0), 125, 29));
    jPanel3.add(jButton2, null);
    jPanel1.add(jPanel5, new GridBagConstraints(0, 3, 1, 1, 1.0, 1.0
            ,GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(0, 0, 4, 0), 121, 28));
    jPanel5.add(jButton4, null);
    jPanel1.add(jPanel4, new GridBagConstraints(0, 2, 1, 1, 1.0, 1.0
            ,GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(0, 0, 0, 0), 125, 28));
    jPanel4.add(jButton3, null);
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
    System.out.println("Message published successfully from TimeSeriesDisplayGUI");
  }

  void jButton2_actionPerformed(ActionEvent e)
  {

    if( tsDisplayPlugIn.publishMessage(jButton2.getActionCommand()) )
    System.out.println("Message published successfully from TimeSeriesDisplayGUI");
  }

  void jButton3_actionPerformed(ActionEvent e)
  {
    if( tsDisplayPlugIn.publishMessage(jButton3.getActionCommand()) )
    System.out.println("Message published successfully from TimeSeriesDisplayGUI");
  }

  void jButton4_actionPerformed(ActionEvent e)
  {
    if( tsDisplayPlugIn.publishMessage(jButton1.getActionCommand()) )
    System.out.println("Message published successfully from TimeSeriesDisplayGUI");
  }
}





/*
package com.centurylogix.dynamics;

import javax.swing.UIManager;
import java.awt.*;

public class TimeSeriesDisplayGUI {
  boolean packFrame = false;

  //Construct the application
  public TimeSeriesDisplayGUI() {
    Frame1 frame = new Frame1();
    //Validate frames that have preset sizes
    //Pack frames that have useful preferred size info, e.g. from their layout
    if (packFrame) {
      frame.pack();
    }
    else {
      frame.validate();
    }
    //Center the window
    Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
    Dimension frameSize = frame.getSize();
    if (frameSize.height > screenSize.height) {
      frameSize.height = screenSize.height;
    }
    if (frameSize.width > screenSize.width) {
      frameSize.width = screenSize.width;
    }
    frame.setLocation((screenSize.width - frameSize.width) / 2, (screenSize.height - frameSize.height) / 2);
    frame.setVisible(true);
  }
  //Main method
  public static void main(String[] args) {
    try {
      UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
    }
    catch(Exception e) {
      e.printStackTrace();
    }
    new TimeSeriesDisplayGUI();
  }
}*/