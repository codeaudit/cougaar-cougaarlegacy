
package sam;

import java.awt.*;
import javax.swing.*;
import javax.swing.border.*;
import java.awt.event.*;
import sam.MainWindow;

/**
 * class PropertiesDialogBox is for loading the system property options for sam.
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

public class PropertiesDialog extends JDialog
{
   // Interface flags.
   // Set if the results are ok to use (user clicked OK).
   public boolean OK;

   // individual properties.
   public boolean debugParsers;
   public boolean debugContracts;
   public boolean verbose;
   public boolean debugSave;
   public boolean plannerDebug;
   public String pathName;
  // public String Editor_pathName;

   GridBagLayout gridbag = new GridBagLayout();
   GridBagConstraints constraints = new GridBagConstraints();
   TitledBorder titledBorder1;

   JCheckBox debugParserCheckBox;
   JCheckBox debugContractsCheckBox;
   JCheckBox verboseCheckBox;
   JCheckBox debugSaveCheckBox;
   JCheckBox debugPlannerCheckBox;

   JButton jbCancel;
   JButton jbOK;


   JTextField User_Home;
   JTextField Contract_Editor;
   JLabel lable;

   public PropertiesDialog(Frame frame, String title, boolean modal)
   {
      super(frame, title, modal);
      try
      {
         jbInit();
         pack();
      }
      catch(Exception ex)
      {
         ex.printStackTrace();
      }
   }

   public PropertiesDialog()
   {
      this(null, "Edit Properties", false);
   }

   void jbInit() throws Exception
   {
      titledBorder1 = new TitledBorder("");
      this.setSize(500,250);
      this.getContentPane().setLayout(gridbag);

      //settings for all the checkboxes
      constraints.insets = new Insets(5,5,5,5);
      constraints.gridx =0; constraints.gridy = 0;
      constraints.gridwidth =1;constraints.gridheight = 1;
      constraints.weightx = 0.0;constraints.weighty = 0.0;
      makelabel(lable,"Home Directory :",gridbag,constraints);


      constraints.insets = new Insets(5,5,5,5);
      constraints.gridx =1; constraints.gridy = 0;
      constraints.gridwidth = 5 ;constraints.gridheight = 2;
      constraints.weightx = 0.0;constraints.weighty = 0.0;
      User_Home = makeTextfield(gridbag,constraints);

      User_Home.addActionListener(new java.awt.event.ActionListener()
      {
         public void actionPerformed(ActionEvent e)
         {
            User_Home_actionPerformed(e);

         }
      });

      constraints.insets = new Insets(5,5,5,5);
      constraints.gridx =0; constraints.gridy = 6;
      constraints.gridwidth =2;constraints.gridheight = 1;
      constraints.weightx = 0.0;constraints.weighty = 0.0;
      debugSaveCheckBox = makecheckboxes( "Enable file save debug messages",gridbag,constraints);
      debugSaveCheckBox.addActionListener(new java.awt.event.ActionListener()
      {

         public void actionPerformed(ActionEvent e)
         {
            debugSaveCheckBox_actionPerformed(e);
         }
      });

      constraints.insets = new Insets(5,5,5,5);
      constraints.gridx =0; constraints.gridy = 8;
      constraints.gridwidth = 2 ;constraints.gridheight = 1;
      constraints.weightx = 0.0;constraints.weighty = 0.0;
      debugParserCheckBox =  makecheckboxes( "Enable parser debug messages",gridbag,constraints);
      debugParserCheckBox.addActionListener(new java.awt.event.ActionListener()
      {

         public void actionPerformed(ActionEvent e)
         {
            debugParserCheckBox_actionPerformed(e);
         }
      });

      constraints.insets = new Insets(5,5,5,5);
      constraints.gridx =0; constraints.gridy = 10;
      constraints.gridwidth = 2;constraints.gridheight = 1;
      constraints.weightx = 0.0;constraints.weighty = 0.0;
      debugContractsCheckBox  = makecheckboxes("Enable contract debug messages",gridbag,constraints);
      debugContractsCheckBox.setActionCommand("Enable contract debug messages");
      debugContractsCheckBox.addActionListener(new java.awt.event.ActionListener()
      {

         public void actionPerformed(ActionEvent e)
         {
            debugContractsCheckBox_actionPerformed(e);
         }
      });

      constraints.insets = new Insets(5,5,5,5);
      constraints.gridx =0; constraints.gridy = 12;
      constraints.gridwidth = 2 ;constraints.gridheight = 1;
      constraints.weightx = 0.0;constraints.weighty = 0.0;
      verboseCheckBox = makecheckboxes( "Enable verbose messages",gridbag,constraints);
      verboseCheckBox.setActionCommand("Enable verbose messages");
      verboseCheckBox.addActionListener(new java.awt.event.ActionListener()
      {

         public void actionPerformed(ActionEvent e)
         {
            verboseCheckBox_actionPerformed(e);
         }
      });

      constraints.insets = new Insets(5,5,5,5);
      constraints.gridx =0; constraints.gridy = 14;
      constraints.gridwidth = 2 ;constraints.gridheight = 1;
      constraints.weightx = 0.0;constraints.weighty = 0.0;
      debugPlannerCheckBox = makecheckboxes( "Enable Planner save debug messages",gridbag,constraints);
      debugPlannerCheckBox.addActionListener(new java.awt.event.ActionListener()
      {

         public void actionPerformed(ActionEvent e)
         {
            debugPlannerCheckBox_actionPerformed(e);
         }
      });

    /*  constraints.insets = new Insets(5,5,5,5);
      constraints.gridx =0; constraints.gridy = 10;
      constraints.gridwidth = 2 ;constraints.gridheight = 2;
      constraints.weightx = 0.0;constraints.weighty = 0.0;
      makelabel(lable,"Editor Path :",gridbag,constraints);

      constraints.insets = new Insets(5,5,5,5);
      constraints.gridx =2; constraints.gridy = 10;
      constraints.gridwidth = 5 ;constraints.gridheight = 2;
      constraints.weightx = 0.0;constraints.weighty = 0.0;
      Contract_Editor = makeTextfield(gridbag,constraints);
      Contract_Editor.addActionListener(new java.awt.event.ActionListener()
      {
         public void actionPerformed(ActionEvent e)
         {
            Contract_Editor_actionPerformed(e);
         }
      });*/

      constraints.insets = new Insets(5,5,5,5);
      constraints.gridx =0; constraints.gridy = 16;
      constraints.gridwidth = 1 ;constraints.gridheight = 1;
      constraints.weightx = 0.0;constraints.weighty = 0.0;
      jbOK = makebutton( "OK", gridbag, constraints);
      jbOK.setActionCommand("OK");
      jbOK.addActionListener(new java.awt.event.ActionListener()
      {

         public void actionPerformed(ActionEvent e)
         {
            jbOK_actionPerformed(e);
         }
      });

      constraints.insets = new Insets(5,5,5,5);
      constraints.gridx =2; constraints.gridy = 16;
      constraints.gridwidth = 1 ;constraints.gridheight = 1;
      constraints.weightx = 0.0;constraints.weighty = 0.0;
      jbCancel = makebutton( "Cancel", gridbag, constraints);
      jbCancel.setActionCommand("Cancel");
      jbCancel.addActionListener(new java.awt.event.ActionListener()
      {

         public void actionPerformed(ActionEvent e)
         {
            jbCancel_actionPerformed(e);
         }
      });


   }

   public JButton makebutton( String name,GridBagLayout gridbag,GridBagConstraints constraints)
   {
      JButton button = new JButton(name);
      gridbag.setConstraints(button,constraints);
      this.getContentPane().add(button);
      return button;
   }
   public JCheckBox makecheckboxes(String chekboxtext,GridBagLayout gridbag,GridBagConstraints constraints)
   {

      JCheckBox name =  new JCheckBox();
      name.setText(chekboxtext);
      gridbag.setConstraints(name,constraints);
      this.getContentPane().add(name);
      return name;
   }

   public void makelabel(JLabel label,String labeltext,GridBagLayout gridbag,GridBagConstraints constraints)
   {
      label  = new JLabel();
      label.setText(labeltext);
      gridbag.setConstraints(label,constraints);
      this.getContentPane().add(label);

   }
   public JTextField makeTextfield( GridBagLayout gridbag,GridBagConstraints constraints)
   {
      JTextField newtextfield = new JTextField();
      constraints.fill = GridBagConstraints.HORIZONTAL;
      gridbag.setConstraints(newtextfield,constraints);
      this.getContentPane().add(newtextfield);
      return newtextfield;

   }

   void jbCancel_actionPerformed(ActionEvent e)
   {
      OK = false;
      setVisible(false);
   }

   void jbOK_actionPerformed(ActionEvent e)
   {
      OK = true;
      setVisible(false);
   }

   void loadInitialValues()
   {
      // Init the properties before they are displayed.
      debugParserCheckBox.setSelected(debugParsers);
      debugContractsCheckBox.setSelected(debugContracts);
      verboseCheckBox.setSelected(verbose);
      debugSaveCheckBox.setSelected(debugSave);
      debugPlannerCheckBox.setSelected(plannerDebug);

      User_Home.setText(pathName);
      //Contract_Editor.setText(Editor_pathName);
   }


   void debugParserCheckBox_actionPerformed(ActionEvent e)
   {
      debugParsers = debugParserCheckBox.isSelected();
   }

   void debugContractsCheckBox_actionPerformed(ActionEvent e)
   {
      debugContracts = debugContractsCheckBox.isSelected();
   }

   void verboseCheckBox_actionPerformed(ActionEvent e)
   {
      verbose = verboseCheckBox.isSelected();
   }

   void debugSaveCheckBox_actionPerformed(ActionEvent e)
   {
      debugSave = debugSaveCheckBox.isSelected();
   }
   void debugPlannerCheckBox_actionPerformed(ActionEvent e)
   {
      plannerDebug = debugPlannerCheckBox.isSelected();
   }

   void User_Home_actionPerformed(ActionEvent e)
   {
      pathName = User_Home.getText();
   }

  /* void Contract_Editor_actionPerformed(ActionEvent e)
   {
      Editor_pathName = Contract_Editor.getText();
   }*/

}
