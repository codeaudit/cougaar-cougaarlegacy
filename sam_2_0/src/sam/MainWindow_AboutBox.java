
package sam;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.border.*;

/**
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

public class MainWindow_AboutBox extends JDialog implements ActionListener
{

   JPanel panel1 = new JPanel();
   JPanel panel2 = new JPanel();
   JPanel insetsPanel1 = new JPanel();
   JPanel insetsPanel2 = new JPanel();
   JPanel insetsPanel3 = new JPanel();
   JButton button1 = new JButton();
   JLabel imageControl1 = new JLabel();
   ImageIcon imageIcon;
   JLabel label1 = new JLabel();
   JLabel label2 = new JLabel();
   JTextArea label3 = new JTextArea();
   JLabel label4 = new JLabel();
   BorderLayout borderLayout1 = new BorderLayout();
   BorderLayout borderLayout2 = new BorderLayout();
   FlowLayout flowLayout1 = new FlowLayout();
   FlowLayout flowLayout2 = new FlowLayout();
   GridLayout gridLayout1 = new GridLayout();
   String product = "Sam: The Cougaar Configuration Editor";
   String version = "Version 2.0       February 22, 2001";
   String copyright = "Copyright (c) 2000-2001 Defense Advanced Research Projects Agency (DARPA)\nand Mobile Intelligence Corporation. This software to be used only in accordance\nwith the COUGAAR license agreement.";
   String comments = "See http://www.cougaar.org";
   /**
    * Constructor.<p>
    */
   public MainWindow_AboutBox(Frame parent)
   {
      super(parent);
      enableEvents(AWTEvent.WINDOW_EVENT_MASK);
      try
      {
         jbInit();
      }
      catch(Exception e)
      {
         e.printStackTrace();
      }
      //imageControl1.setIcon(imageIcon);
      pack();
   }

   private void jbInit() throws Exception
   {
      //imageIcon = new ImageIcon(getClass().getResource("[Your Image]"));
      this.setTitle("About");
      setResizable(false);
      panel1.setLayout(borderLayout1);
      panel2.setLayout(borderLayout2);
      insetsPanel1.setLayout(flowLayout1);
      insetsPanel2.setLayout(flowLayout1);
      insetsPanel2.setBorder(new EmptyBorder(10, 10, 10, 10));
      gridLayout1.setRows(4);
      gridLayout1.setColumns(1);
      label1.setText(product);
      label2.setText(version);
      label3.append(copyright);
      label4.setText(comments);
      insetsPanel3.setLayout(gridLayout1);
      insetsPanel3.setBorder(new EmptyBorder(10, 60, 10, 10));
      button1.setText("Ok");
      button1.addActionListener(this);
      insetsPanel2.add(imageControl1, null);
      panel2.add(insetsPanel2, BorderLayout.WEST);
      this.getContentPane().add(panel1, null);
      insetsPanel3.add(label1, null);
      insetsPanel3.add(label2, null);
      insetsPanel3.add(label3, null);
      label3.setEditable(false);
      insetsPanel3.add(label4, null);
      panel2.add(insetsPanel3, BorderLayout.CENTER);
      insetsPanel1.add(button1, null);
      panel1.add(insetsPanel1, BorderLayout.SOUTH);
      panel1.add(panel2, BorderLayout.NORTH);
   }

   protected void processWindowEvent(WindowEvent e)
   {
      if (e.getID() == WindowEvent.WINDOW_CLOSING)
      {
         cancel();
      }
      super.processWindowEvent(e);
   }

   /**
    * Disposes the about window box.<p>
    */
   void cancel()
   {
      dispose();
   }

   public void actionPerformed(ActionEvent e)
   {
      if (e.getSource() == button1)
      {
         cancel();
      }
   }
}
