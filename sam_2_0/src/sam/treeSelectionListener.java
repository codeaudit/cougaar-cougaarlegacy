

package sam;

import sam.LoadContractFiles.contractOwnerBase;
import sam.display.samGraphController;
import diva.canvas.selection.SelectionDragger;

import javax.swing.JTree;
import javax.swing.event.TreeSelectionListener;
import javax.swing.event.TreeSelectionEvent;
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
public class treeSelectionListener implements TreeSelectionListener
{

   public treeSelectionListener()
   {
   }

   public void valueChanged(TreeSelectionEvent e)
   {
      // Get the source
      JTree tree = (JTree)e.getSource();

      // Get the selected node.
      contractOwnerBase rec = (contractOwnerBase)tree.getLastSelectedPathComponent();
      if( rec != null )
      {
         // clear any outstanding selections.
         ((samGraphController)MainWindow.theMainWindow.theSociety.thePane.getGraphController()).clearSelections(false);

         // Can we show it as a graph?
         if( rec.hasGraph() )
         {
            //to display the selected node's graph
            rec.renderGraph( MainWindow.theMainWindow.theSociety.theJGraph );

            //adding the displayed graph to the history
            MainWindow.theMainWindow.backandforward.addtoHistory(MainWindow.theMainWindow.theSociety.displayedComponent);

            //updating the back and forward buttons
//            MainWindow.theMainWindow.update_BackandForward();

         }
         else
         {
            // How about it's parent?
            rec = rec.getParentNode();
            if( rec != null && rec.hasGraph() )
            {
               //to display the selected node's graph
               rec.renderGraph( MainWindow.theMainWindow.theSociety.theJGraph );

               //adding the displayed graph to the history
               MainWindow.theMainWindow.backandforward.addtoHistory(MainWindow.theMainWindow.theSociety.displayedComponent);

               //updating the back and forward buttons
//               MainWindow.theMainWindow.update_BackandForward();
            }
         }
      }
   }
}