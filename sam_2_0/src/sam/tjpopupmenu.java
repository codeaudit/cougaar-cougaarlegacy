
package sam;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

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
 * @author Sridevi Salagrama
 * @version 1.0
 */
public class tjpopupmenu
{

   JPopupMenu popupmenu;
   JMenuItem edsItem,cutitem,copyitem,pasteitem,helpitem;

  public tjpopupmenu()
  {
   popupmenu = new JPopupMenu("Insert Popup Menu");

   //create the popupmenu items and save them

  edsItem = new JMenuItem("External Data source ");
  popupmenu.add(edsItem);
  popupmenu.addSeparator();

  cutitem = new JMenuItem("cut ");
  popupmenu.add(cutitem);
  popupmenu.addSeparator();

  copyitem = new JMenuItem("copy ");
  popupmenu.add(copyitem);
  popupmenu.addSeparator();

  pasteitem = new JMenuItem("paste ");
  popupmenu.add(pasteitem);
  popupmenu.addSeparator();

  helpitem = new JMenuItem("help");
  popupmenu.add(helpitem);
  popupmenu.addSeparator();

  popupmenuListener pml = new popupmenuListener();
//  MainWindow.theMainWindow.jComboBox1.addMouseListener(pml);
  popupmenu.setVisible(true
  );


  }  //ends constructor tjpopupmenu

  class popupmenuListener extends MouseAdapter
  {
    public void mousePressed(MouseEvent me)
    {
      showPopup(me);
    }

    public void mouseReleased(MouseEvent me)
    {
      showPopup(me);
    }
    private void showPopup(MouseEvent me)
    {
      if(me.isPopupTrigger())

    {
      popupmenu.show(me.getComponent(),me.getX(),me.getY());
     }
    }
  }//ends class popupmenuListener
}