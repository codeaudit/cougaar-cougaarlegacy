

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

package sam.LoadContractFiles;

import java.util.*;
import java.io.*;

import org.cougaar.core.util.Operator;
import org.cougaar.core.util.OperatorFactory;
//import alp.util.SetRelationship;

import org.w3c.dom.*;
import org.xml.sax.InputSource;
import com.ibm.xml.parsers.*;

/**
 * Load Contract for a PlugIn.
 *  */

public class PluginContractLoader {

   public PluginContractLoader() { }

   /**
    * Load the XML plugin contract file specified by "xmlFile" and
    * set the contracts.
    * <p>
    * @return true on success, false on failure
    */
   public boolean loadContracts(
       File xmlFile,
       HashMap pluginContracts,
       HashMap clusterContracts,
       HashMap communityContracts,
       HashMap societyContracts,
       boolean debug) {

      FileReader freader;
      try {
        freader = new FileReader( xmlFile );

        if (debug) {
          System.out.println(
            "\nStarting to read the contract file " +
            xmlFile.getAbsolutePath());
        }
      } catch (java.io.FileNotFoundException jnfe) {
         System.err.println(
           "XML contract file not found: " +
           xmlFile);
         return false;
      } catch (Exception e) {
         System.err.println(
           "Unable to read XML contract file \"" +
           xmlFile +"\"");
         e.printStackTrace();
         return false;
      }

      // parse the XML document
      Element rootElement;
      try {
        DOMParser parser = new DOMParser();
        parser.parse(new InputSource(freader));
        Document document = parser.getDocument();
        rootElement = document.getDocumentElement();
      } catch (IOException e) {
        System.err.println("I/O exception reading contracts file " + e);
        return false;
      } catch (Exception e) {
         System.err.println("Unable to read XML contracts file " + e);
         e.printStackTrace();
         return false;
      }

      // Now walk through the contract file elements and load the contracts.

      // Should be 1 root element: <contracts>

      // Validate the top level
      if (!(rootElement.getNodeName().equalsIgnoreCase("contracts"))) {
        System.err.println(
          "The root level keyword must be <contracts>, not " +
          rootElement.getNodeName());
        return false;
      }

      // get operator parser
      OperatorFactory opFactory = OperatorFactory.getInstance();

      // get child nodes of root
      NodeList rootChildNodes = rootElement.getChildNodes();
      int nRootChildNodes = rootChildNodes.getLength();

      if (debug) {
        System.out.println("The file contains contracts for " +
            nRootChildNodes + " objects");
      }

      // Step through each object's contracts and build the operator tree
      for (int i = 0; i < nRootChildNodes; i++) {
         Node curNode = (Node)rootChildNodes.item(i);
         if (curNode.getNodeType() != Node.ELEMENT_NODE) {
           continue;
         }
         Element curElement = (Element)curNode;

         // Switch on the type of contract
         HashMap contracts;
         int contractType = setOfContractLinkPoints.InvalidContract;
         String objectDesc = "Unknown";
         String curName = curElement.getNodeName();
         if (curName.equalsIgnoreCase("plugin")) {
            contractType = setOfContractLinkPoints.PluginContract;
            objectDesc = "plugin";
            contracts = pluginContracts;
         } else if (curName.equalsIgnoreCase("cluster")) {
            contractType = setOfContractLinkPoints.ClusterContract;
            objectDesc = "cluster";
            contracts = clusterContracts;
         } else if (curName.equalsIgnoreCase("community")) {
            contractType = setOfContractLinkPoints.CommunityContract;
            objectDesc = "community";
            contracts = communityContracts;
         } else if (curName.equalsIgnoreCase("society")) {
            contractType = setOfContractLinkPoints.SocietyContract;
            objectDesc = "society";
            contracts = societyContracts;
         } else {
            System.err.println(
              "Skipping unknown contract type: <" +
              curElement.getNodeName() +
              ">. Known types are "+
              "<plugin>, <cluster>, <community>, and <society>");
            continue;
         }

         // Get the object's name from the attribute "name=".
         String objectName = getAttribute(curElement, "name");
         if (objectName == null) {
           System.err.println(
             "Skipping contracts for unnammed " + objectDesc +
             ": You must provide the " + objectDesc +
             "'s name.\n   For example: <" + objectDesc +
             " name=\"your." + objectDesc + ".name\">");
            continue;
         }

         // Check if this is a system object from a "system" attribute
         //   (we ignore system objects during closure computation.)
         String objectType = getAttribute(curElement, "type");
         boolean systemObject = false;
         if ((objectType != null) &&
             (objectType.equalsIgnoreCase("system"))) {
           systemObject = true;
         }

         if (debug) {
           System.out.println("Processing " + objectDesc + " " + objectName);
         }

         // See if we already have a record for this object
         setOfContractOperators record =
           (setOfContractOperators)contracts.get(objectName);
         if (record == null) {
           // No, so make a new one.
           record = new setOfContractOperators();
         }

         // Update the systemObject flag.
         record.systemObject = systemObject;

         // Step through each object's contracts and build the operator tree
         NodeList curChildNodes = curElement.getChildNodes();
         int nCurChildNodes = curChildNodes.getLength();
         for (int j = 0; j < nCurChildNodes; j++) {
            // Inside the object, so loop twice,
            //   once for <subscribe>...</subscribe>
            // and
            //   once for <publish>...</publish>
            Node pubsubNode = (Node)curChildNodes.item(j);
            if (pubsubNode.getNodeType() != Node.ELEMENT_NODE) {
              continue;
            }
            Element pubsubElement = (Element)pubsubNode;

            // Are we publish or subscribe?
            boolean isPublish;
            String pubsubName = pubsubElement.getNodeName();
            if (pubsubName.equalsIgnoreCase("publish")) {
              isPublish = true;
            } else if (pubsubName.equalsIgnoreCase("subscribe")) {
               isPublish = false;
            } else {
              System.err.println(
                "Skipping contract for " + objectDesc +
                " " + objectName +
                " with unknown type <" + pubsubElement.getNodeName() +
                ">\n   The keyword must be <subscribe> or <publish>.");
              break;
            }

            NodeList contractITChildNodes = pubsubElement.getChildNodes();
            int nContractITChildNodes = contractITChildNodes.getLength();

            if (debug) {
              System.out.println(
                "Processing "+
                (isPublish ? "publish" : "subscribe")+
                " contracts.  There are " +
                nContractITChildNodes);
            }

            for (int k = 0; k < nContractITChildNodes; k++) {
               Node contractNode = (Node)contractITChildNodes.item(k);
               if (contractNode.getNodeType() != Node.ELEMENT_NODE) {
                 continue;
               }
               Element contractElement = (Element)contractNode;

               // parse the contract
               Operator op;
               try {
                  op = opFactory.create(Operator.XML_FLAG, contractElement);

                  // Save the operator representation of the contract

                  // Now add the contract.
                  if (isPublish) {
                    record.publish.addElement(op);
                  } else {
                    record.subscribe.addElement(op);
                  }
               } catch (Exception e) {
                  System.err.println(
                    "Unable to parse contract:\n" +
                    contractElement +
                    "\nParser stack trace:");
                  e.printStackTrace();
                  System.err.println("   Skipping to next contract");
               }

               if (debug) {
                 System.out.println(
                   "         "+
                   "succeeded in parsing contract:\n" +
                   contractElement +
                   "\n");
               }
            }
         } // End while

         // Save the contract record.
         contracts.put(objectName, record);
      }

      return true;
   }

   private static String getAttribute(Node node, String name) {
     NamedNodeMap attribs = node.getAttributes();
     int nAttribs =
       ((attribs != null) ? attribs.getLength() : 0);
     for (int i = 0; i < nAttribs; i++) {
       Node aNode = attribs.item(i);
       if (aNode.getNodeName().equalsIgnoreCase(name)) {
         return aNode.getNodeValue();
       }
     }
     return null;
   }
/*
   // We need to add an empty operator into the contracts structure
   // when users insert a new contract so they will be able to edit it.
   public Operator getEmptyOperator()
   {

   }

   // Convert an XML contract string into an operator.
   // Used when users edit contract.
   public Operator getEmptyOperator(String contractXML)
   {

   }
*/
}
