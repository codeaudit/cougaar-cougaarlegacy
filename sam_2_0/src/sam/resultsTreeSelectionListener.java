
package sam;

import sam.LoadContractFiles.contractOwnerBase;
import sam.LoadContractFiles.contractLinkPoint;

import sam.display.samGraphController;
import sam.display.samBaseFigure;
import sam.display.samBaseSite;

import diva.canvas.Site;
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

public class resultsTreeSelectionListener implements TreeSelectionListener
{

   public resultsTreeSelectionListener()
   {
   }

   public void valueChanged(TreeSelectionEvent e)
   {
      // Get the source
      JTree tree = (JTree)e.getSource();

      // clear any outstanding selections.
      samGraphController sgc = (samGraphController)MainWindow.theMainWindow.theSociety.thePane.getGraphController();
      sgc.clearSelections(false);

      // Get the selected node.
      ResultsNode node = (ResultsNode)tree.getLastSelectedPathComponent();

      if( node == null )
         return;

      Object obj = node.getUserObject();
      if( obj != null )
      {
         // Is it a node record?
         if( obj instanceof contractOwnerBase )
         {
            contractOwnerBase rec = (contractOwnerBase)obj;

            // Can we show it as a graph?
            if( rec.hasGraph() )
            {
               rec.renderGraph( MainWindow.theMainWindow.theSociety.theJGraph );
            }
            else
            {
               // How about it's parent?
               contractOwnerBase p = rec.getParentNode();
               if( p != null && p.hasGraph() )
               {
                  p.renderGraph( MainWindow.theMainWindow.theSociety.theJGraph );
               }
            }

            // Show it selected.
            rec.select();
         }
         // Is it a link point?
         else if( obj instanceof contractLinkPoint )
         {
            contractLinkPoint lp = (contractLinkPoint)obj;
            contractOwnerBase rec = lp.getParent();

            // Can we show it as a graph?
            if( rec.hasGraph() )
            {
               rec.renderGraph( MainWindow.theMainWindow.theSociety.theJGraph );
            }
            else
            {
               // How about it's parent?
               contractOwnerBase p = rec.getParentNode();

               if( p != null && p.hasGraph() )
               {
                  p.renderGraph( MainWindow.theMainWindow.theSociety.theJGraph );
               }
            }

            // Show it selected.
            samBaseFigure fig = rec.getFigure();
            Site sysSite = lp.isPublish() ? fig.getOutputSite(lp.getSlot()) : fig.getInputSite(lp.getSlot());
            samBaseSite site = (samBaseSite)sysSite;

            // Select this site.
            sgc.addSelectedSite(site);
            site.highlightSite();

            // Get the site's contract as an XML string
            String contract = prettyprint.XML(site.getContract());

            // Display the contract in the text window.
            MainWindow.setDisplayedText(contract);
         }
         else
         {
            // Hmm, what is it?
            System.err.println("Internal Error: Unknown record type passed to resultsTreeSelectionListener: " + obj);
         }
      }
   }
}
