
package sam.LoadContractFiles;

import java.util.Vector;
import java.util.Iterator;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Collection;
import java.util.Set;


/**
* use "Property" get/test methods where applicable.
*      getProperty()
*      equalsProperty()
*
* "Properties" are an abstraction over "attribute" and "sub-element"
* semantics...
*
* The "property" of an element can be an attribute or sub-element.
* example:
*      1. <foo bar="xxx">...</foo>
*      2. <foo>
*            <bar>xxx</bar>
*         </foo>
*  for either case (above), getProperty("bar") returns "xxx"
*
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
public class BElement {

  protected String myLocalName = "";
  protected String myURI = "";
  protected String myRawName = "";
  protected Vector mySubElements = new Vector();
  protected String myData = null;
  //protected org.xml.sax.Attributes myAttributes = null;
  protected HashMap myAttributes = new HashMap();

  // protected BElement myParent = null;

  //--------------------------------------------------------------------
  public BElement(String localName, String uri,  String rawname, org.xml.sax.Attributes attr) {
      myLocalName = localName;
      myURI = uri;
      myRawName = rawname;
      myAttributes = attributesAsHashMap(attr);
  }

  //--------------------------------------------------------------------
  // equalsProperty() returns TRUE IFF (1.) OR (2.) is true:
  //
  // 1. element has 'property' attribute defined
  //      AND it is equal to 'value' parameter
  // 2. contains subelement whose getLocalName() == 'property'
  //      AND whose getMyData() == 'value' parameter
  //
  public boolean equalsProperty( String property, String value ){
         String p = getProperty(property);
         if( p != null )
         {   return p.equals(value);
         }
         return false;
  }

  //--------------------------------------------------------------------
  //  returns "property" associated with this element.
  //  Property can be expressed as attribute or subelement
  //  See equalsProperty()
  //
  public String getProperty(String property) {

     String a = null;
     BElement be = null;

     // ~~~~~~~ 1.
     if( (a = getAttribute(property)) != null){
        return a;
     }
     // ~~~~~~~ 2.
     else if ( (be = findSubElementByLocalName(property)) != null ){
        return be.getMyData();
     }
     return null;
  }

  //--------------------------------------------------------------------
  //  removes "property" associated with this element.
  //  Property can be expressed as attribute or subelement
  //  See equalsProperty()
  //  returns true if property found and successful else false.
  //
  public boolean removeProperty(String property) {

     String a = null;
     BElement be = null;

     // ~~~~~~~ 1.
     if( (a = getAttribute(property)) != null){
        this.getMyAttributes().remove(property);
        System.out.println("%%%%%%%%%%%%%%%%%%%%%%%%%%%%%% removing attribute " + property);
        return true;
     }
     // ~~~~~~~ 2.
     else if ( (be = findSubElementByLocalName(property)) != null ){
        System.out.println("%%%%%%%%%%%%%%%%%%%%%%%%%%%%%% removing subelement " + property);
        this.getMySubElements().remove(be);
        return true;
     }
     //else {
     //   System.out.println("%%%%%%%%%%%%%%%%%%%%%%%%%%%%%% unable to remove property " + property);
     //}
     return false;
  }

  //--------------------------------------------------------------------
  //  finds sub-element by "property"
  //  Property can be expressed as attribute or subelement on subelement
  //  See equalsProperty()
  //
  public BElement findSubElementByProperty( String property )
  {
      Iterator it = mySubElements.iterator();
      while( it.hasNext() )
      {
         BElement be = (BElement)it.next();
         if( be.getProperty(property) != null ) return be;
      }
      return null;
  }




  public String getLocalName(){
      return myLocalName;
  }

  public String getAttribute(String attributekey) {
      return (String)myAttributes.get(attributekey);
  }

  public void addChild( BElement c ){
       mySubElements.addElement(c);
  }

  public int getNumberChildren() {
      return mySubElements.size();
  }

  public Vector getMySubElements() {
      return mySubElements;
  }

  public HashMap getMyAttributes() {
      return myAttributes;
  }

  public BElement findSubElementByLocalName( String localname )
  {
      Iterator it = mySubElements.iterator();
      while( it.hasNext() )
      {
         BElement be = (BElement)it.next();
         if( be.getLocalName().equals(localname) ) return be;
      }
      return null;
  }

  public void setMyData(String data){
      myData = data;
  }

  // returns null if no data set
  public String getMyData() {
      return myData;
  }

  /**
  public void addParent(BElement p) {
      myParent = p;
  }
  **/

  //
  // recursively applied -- remove properties from object tree
  // used to clean-up after language extension features
  //
  public void stripProperty(String property)
  {
     _strip(this, property);
  }

  private static void _strip(BElement obj, String property)
  {
       obj.removeProperty(property);

       int num_subelems = obj.getMySubElements().size();
       if( (num_subelems > 0) ) {
             Iterator it = obj.mySubElements.iterator();
             while(it.hasNext()){
                 _strip((BElement)it.next(), property);
             }
       }
  }



  public void print( PrintWriter pw )
  {
      _print(pw, this, "");
  }

  //
  // recursively applied -- pretty print object tree
  //
  private static void _print(PrintWriter pw, BElement obj, String prefix )
  {
       // String of all attributes
       String all_attribs = (String)attributesAsString(obj.myAttributes );
       //
       // data_character:  <foo>data</foo>
       // may be null if no data (eg. only attributes or subelements)
       //
       String data_character = obj.getMyData();
       int num_subelems = obj.getMySubElements().size();

       //
       // We represent element within single tag "<foo/>" IFF
       // there are no sub-elements or data.  Assume existance of
       // sub-elemetns and data are mutually exclusive
       //

       // CASE: SUB-ELEMENTS %%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
       if( (num_subelems > 0) ) {
             pw.println(prefix + "<" + obj.myLocalName + " " + all_attribs + ">" );
             Iterator it = obj.mySubElements.iterator();
             while(it.hasNext()){
                 _print(pw,((BElement)it.next()),"   " + prefix);
             }
             pw.println(prefix + "</" + obj.myLocalName + ">");
       }
       // CASE: DATA %%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
       else if((data_character!=null) ) {
             pw.print(prefix + "<" + obj.myLocalName + " " + all_attribs + ">" );
             pw.print(data_character);
             pw.println("</" + obj.myLocalName + ">");
       }
       // CASE: fit everything into single TAG %%%%%%%%
       else {
             pw.println(prefix + "<" + obj.myLocalName + " " + all_attribs + "/>" );
       }

       pw.flush();
  }

  private static String  attributesAsString( HashMap attribs )
  {
       String all = new String();
       Set keys = (Set)attribs.keySet();
       Iterator it = keys.iterator();
       while(it.hasNext() )
       {
          String key = (String)it.next();
          String value = (String)attribs.get(key);
          all += " " + key + "=\"" + value + "\"";
       }
       return all;
  }

  private static HashMap attributesAsHashMap(org.xml.sax.Attributes attributes)
  {
      HashMap map = new HashMap();
      int sz = attributes.getLength();
      int i;
      for(i=0; i<sz; i++) {
          map.put(attributes.getLocalName(i), attributes.getValue(i));
      }
      return map;
  }
}
