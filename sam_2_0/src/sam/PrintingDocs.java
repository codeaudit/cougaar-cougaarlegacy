package sam;
import sam.HelpDocs;


 import java.awt.*;
 import javax.swing.*;
 import java.awt.print.*;
 import java.awt.geom.*;
 import java.awt.font.*;

/**
 * class PrintingDocs is mainly to print the graphical representations.
 * Title:        Sam
 * Description:  ALP Business Process User Interface
 * <copyright>
 *    Copyright (c) 2000-2001 Defense Advanced Research Projects
 *    Agency (DARPA) and Mobile Intelligence Corporation.
 *    This software to be used only in accordance with the
 *    COUGAAR license agreement.
 * </copyright>
 * Company:      Mobile Intelligence Corp.
 * @author: Sridevi Salagrama
 * @version 1.0
 */

public class PrintingDocs implements Printable {

  //variable of component
  private Component componentToBePrinted;
  boolean flag;
  boolean isContract;

  //Static Method that takes Component as an parameter and prints
  //for that component
  public static void printComponent(Component c, boolean isScreenPrint, boolean isContract) {
    new PrintingDocs(c,isScreenPrint,isContract).print();

 }

   //contructor that sets the component to be printed
  public PrintingDocs(Component componentToBePrinted, boolean flag, boolean isContract) {

    this.componentToBePrinted = componentToBePrinted;
    this.flag = flag;
    this.isContract = isContract;
  }


  //overwriting the print method so that a dialog box is displayed
  public void print() {
      //creating the object of PrinterJob
      PrinterJob printjob = PrinterJob.getPrinterJob();

      //to let the user select if he wants to select landscape or potrait form of printout
      PageFormat defaultFormat = printjob.defaultPage();
      PageFormat selectedFormat = printjob.pageDialog(defaultFormat);

      printjob.setPrintable(this, selectedFormat);


     //Wait for the user to decide rather to print or not.
     // returns true if they click "print", false otherwise.
    if( printjob.printDialog())
    {

      try
      {

        printjob.setJobName( MainWindow.theMainWindow.theSociety.displayedComponent.getLongName());
        printjob.print();

      }
      catch (PrinterException pe)
      {
        System.out.println("Error printing: "+ pe);
      }//ends catch
    }

  } //ends method print

  public int print(Graphics g, PageFormat pf, int pageIndex) {
    if (pageIndex >0)
    {
      return (NO_SUCH_PAGE);
    }
    else
    {
      Graphics2D g2d = (Graphics2D) g;

      g2d.translate(pf.getImageableX(),pf.getImageableY());

      if(isContract)
      {
        g2d.translate(pf.getImageableX()+40,pf.getImageableY()+30);
      }

      //tring to draw perfect components
      //subtracting 18 from the height of the component so that there would be no
      //gap which is formed due to the missing of the "sam" title bar
      double iw = pf.getImageableWidth();
      double ih = pf.getImageableHeight();
      double gw = componentToBePrinted.getWidth();
      double gh = componentToBePrinted.getHeight();


     if((flag) ||(isContract))
      {
       gh-=18;
      }

      //trying to scale the components
      double scaleWidth  = iw / gw;
      double scaleHeight = ih / gh;


      double scale = Math.min(scaleWidth, scaleHeight);
      if(isContract)
      {

      scale = Math.min((scaleWidth/scaleHeight)+.5, (scaleHeight/scaleWidth) +.5);
      }

      g2d.scale(scale, scale);

      disableDoubleBuffering(componentToBePrinted);
      componentToBePrinted.paint(g2d);
      enableDoubleBuffering(componentToBePrinted);


      //trying to set margins
      double ix = 0;
      double iy = 0;
      if(flag)
      {
        iy +=18;

      }

      Rectangle2D page = new Rectangle2D.Double(ix,iy,gw,gh);

      g2d.setPaint(Color.black);
      g2d.draw(page);
      return(PAGE_EXISTS);
      }


  }

  public static void disableDoubleBuffering(Component c) {
    RepaintManager currentManager = RepaintManager.currentManager(c);
    currentManager.setDoubleBufferingEnabled(false);
  }

  public static void enableDoubleBuffering(Component c) {
    RepaintManager currentManager = RepaintManager.currentManager(c);
    currentManager.setDoubleBufferingEnabled(true);
  }




}//ends class printing docs
