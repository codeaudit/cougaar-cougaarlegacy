package sam;

 import java.awt.*;
 import javax.swing.*;
 import java.awt.print.*;
 import java.awt.event.*;
 import java.util.*;
 import sam.HelpDocs;

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
 * @author: Sridevi Salagrama
 * @version 1.0<p>
 */

public class printHelpDocs extends JFrame
{
   private PageFormat pageformat;
     //variable of component
  //private Component componentToBePrinted;

  //to  print only for the help documents
 // boolean isHelp;
 HelpDocs hds;
 public static printHelpDocs phd;

 PageFormat selectedFormat;
 PrinterJob printjob;

   public printHelpDocs()
   {
   //empty constructor
   }

   public Book makeBook()
   {
      if(pageformat == null)
      {
         printjob = PrinterJob.getPrinterJob();
         pageformat =  printjob.defaultPage();

         selectedFormat = printjob.pageDialog(pageformat);
      }

//set the printable to the selected format
      //printjob.setPrintable(hds.thehelpdoc,selectedFormat);

      Book book = new Book();
      int pagecount = hds.thehelpdoc.getNumberOfPages((Graphics2D)hds.thehelpdoc.getGraphics(), pageformat);
      System.out.println(" The Page count is : " + pagecount);
     // book.append(hds.thehelpdoc, selectedFormat);
      book.append(hds.thehelpdoc, selectedFormat, pagecount);
      return book;
   }

   public void actionperformed( ActionEvent e)
   {

      Object source = e.getSource();
      if(source == hds.thehelpdoc.menuPrint)
      {
         printjob = PrinterJob.getPrinterJob();
         printjob.setPageable(makeBook());

         if( printjob.printDialog())
         {
            try
            {
               printjob.print();
              // hds.thehelpdoc.print();
            }
            catch (Exception ex)
            {
               ex.printStackTrace();
               JOptionPane.showMessageDialog(this,ex);
            }
         }
      }
   }




}