
package sam.LoadContractFiles;

import org.xml.sax.Locator;
import java.lang.String;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

import org.xml.sax.ContentHandler;
import org.xml.sax.ErrorHandler;
import java.util.Stack;
import java.util.Vector;
import java.util.Iterator;


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
public class BContentHandler implements ContentHandler {

  private Stack myElementStack = new Stack();
  public Vector rootElements = new Vector();  // each elem instanceof binder.BElement
  public Vector allElements = new Vector(); // each elem instanceof binder.Element
  private String currentCharacterData = null;


  public BContentHandler() {
  }

  public Vector getRootElements(){
      return rootElements;
  }

  public Vector getALLElements() {
      //System.out.println("************************ returning ALL elements vec, size=" + allElements.size());
      return allElements;
  }

  public void characters(char[] p0, int p1, int p2) throws SAXException
  {
       String c = new String(p0,p1,p2);
       //System.out.println("-------------------------" + c);
       currentCharacterData = c;
  }

  public void endDocument() throws SAXException
  {
  }

  public void endElement(String p0, String p1, String p2) throws SAXException
  {
      /**
      System.out.print("   <BContentHandler.endElement().  p0="
              + p0 + ", p1=" + p1 + ", p2=" + p2 );
      **/
      try{
         BElement me = (BElement)myElementStack.pop();
         if( currentCharacterData != null ){
            me.setMyData(currentCharacterData);
         }
      } catch (Exception ex ){
         ex.printStackTrace();
      }
      currentCharacterData=null;
  }

  public void endPrefixMapping(String p0) throws SAXException
  {
  }

  public void ignorableWhitespace(char[] p0, int p1, int p2) throws SAXException
  {
  }

  public void processingInstruction(String p0, String p1) throws SAXException
  {
  }

  public void setDocumentLocator(Locator p0)
  {
  }

  public void skippedEntity(String p0) throws SAXException
  {
  }

  public void startDocument() throws SAXException
  {
  }

  // uri, localname, rawname
  public void startElement(String uri, String localname, String rawname, Attributes p3) throws SAXException
  {
      try{
         BElement parent = null;
         if (myElementStack.size() > 0 ) parent = (BElement)myElementStack.peek();
         BElement me = new BElement(localname,uri,rawname, p3);
         allElements.addElement(me);
         myElementStack.push(me);
         if( parent != null ){
            parent.addChild(me);
            // me.addParent(parent);
         } else {
           // must be root element if no parent
           rootElements.addElement(me);
         }
      } catch (Exception ex ){
          ex.printStackTrace();
      }


      /**
      System.out.print(">BContentHandler.startElement().  p0="
              + p0 + ", p1=" + p1 + ", p2=" + p2 );
      System.out.print(" Attributes = " );
      int len = p3.getLength();
      int i;
      for(i=0;i<len; i++)
      {
           System.out.print("(" + p3.getLocalName(i) + ", "
                      + p3.getURI(i) + ", "   // namespace URI
                      + p3.getValue(i) + ")");
      }
      System.out.println("");
      **/
  }

  public void startPrefixMapping(String p0, String p1) throws SAXException
  {
  }
}
