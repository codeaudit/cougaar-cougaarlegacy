
package sam.LoadContractFiles;

import java.util.*;
import org.cougaar.core.util.Operator;
import java.io.*;
import sam.contractInfo;

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

public class setOfContractOperators
{
   // A list of contracts as "Operator" records.
   public Vector publish;
   public Vector subscribe;

   // Is this a system object which we should ignore?
   public boolean systemObject;

   public setOfContractOperators()
   {
      publish = new Vector();
      subscribe = new Vector();
      systemObject = false;
   }

   public void addPublishContract( Operator new_Contract)
   {
      publish.addElement(new_Contract);
   }

   public void addSubscribeContract( Operator new_Contract)
   {
      subscribe.addElement(new_Contract);
   }

   public boolean writeOutContracts( PrintWriter pw, boolean debug)
   {
      //get all the contracts for publish,then write the xml for the publish
      Iterator pub_it = publish.iterator();

      //write the opening tag for the publish contracts
      pw.println("<publish>");
      while(pub_it.hasNext())
      {
         //get the objects in the iterator as the operator objects
         Operator op = (Operator)pub_it.next();

         // Get the contract as an XML string
         String str = op.toString();

         //pass that string as parameter to the printwriter
         pw.println(str);
      }

      //write the closing tag for the publish contracts
      pw.println("</publish>");

     //get all the contracts for the subscribe, then write the xml for subscribe
      Iterator sub_it = subscribe.iterator();

      //write the opening tag for the subscribe contracts
      pw.println("<subscribe>");
      while (sub_it.hasNext())
      {
         // get the objects in the iterator as the operators objects
         Operator op = (Operator)sub_it.next();

         //get the contract as an xml string
         String str = op.toString();

         //pass the string as parameter to the printwriter
         pw.println(str);

      }
      //write the closing tag for the subscribe contracts
      pw.println("</subscribe>");


      return true;

   }

}//ends class set of contract operators.
