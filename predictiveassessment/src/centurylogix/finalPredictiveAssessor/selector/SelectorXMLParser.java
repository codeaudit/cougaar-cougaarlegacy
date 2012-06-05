package com.centurylogix.finalPredictiveAssessor.selector;

import java.util.ArrayList;
import java.io.ByteArrayInputStream;

import com.ibm.xml.parser.Parser;
import com.ibm.xml.parser.*;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.NamedNodeMap;

public class SelectorXMLParser
{

  public SelectorXMLParser()
  {
  }

  protected static ArrayList parseClusterList (ByteArrayInputStream dataFromPSP)
  {
    ArrayList clusters = new ArrayList (10);
    String clusterID = null;

    try {
      Parser parser = new Parser("Cluster List");
      parser.setExpandEntityReferences(true);
      Document doc = null;
      doc = parser.readStream(dataFromPSP);

      // if we obtained a non-empty, valid XML doc, commence parsing
      if ( doc != null && doc.getDoctype() != null)
      {
        // get the 'doctype' of the XML stream
        String documentName = doc.getDoctype().getName();

        // if the "doctype' is "Error", there is not valid analysis data logPlan element
          // set flag in <info> stating that no valid data was found. This should only
          // occur when the display is waiting for anlysis to begin on some asset an a
          // trend analysis data structure to be published
        if (documentName.equals("Error"))
          clusters = null;

        else if (documentName.equals("ClusterList"))
        {
          // get the root node and then its attributes
          Node rootNode = (Node)doc.getDocumentElement();

          // cycle throught the list of child nodes, and get the values for each day
    		  NodeList theNodes = ((Element)rootNode).getChildNodes();
  		    if (theNodes != null)
          {
            int len = theNodes.getLength();
            String tailNumber = null;
            for ( int i = 0; i < len; i++ )
            {
              // re-initialize place holder variable to hold parsed info
              clusterID = null;

      				String nodeName = theNodes.item(i).getNodeName();

              //identify 'DailyInfo' nodes (this should be the only kind found)
              if ( nodeName.equals("ClusterName"))
              {
                // get the attribute for this node
                NamedNodeMap attributes = theNodes.item(i).getAttributes();

                if (attributes.getNamedItem("Name") != null)
                  clusterID = new String (attributes.getNamedItem("Name").getNodeValue().toString());

                // If all fields were successfully obtained, add them as a group to <info>
                if (clusterID != null)
                {
                  clusters.add (clusterID);
                }
              }// end if (nodeName.equals ("ClusterName");
            }// end if
          } // end for (int i=0,...)
        }//end if (theNodes != null)
      } // end if (documentName.equals ("ClusterList")

    } // end try
    catch ( Exception e)
    {  //todo: determine more appropriage error handling
      e.printStackTrace();
      System.out.println ("problem parsing data : " + e.getMessage());
    }
    return clusters;
  }

  protected static ArrayList parseAssetList (ByteArrayInputStream dataFromPSP, String requestedCluster)
  {
    ArrayList assets = new ArrayList (10);
    String clusterID = null;
    String assetName = null;

    try {
      Parser parser = new Parser("Asset List");
      parser.setExpandEntityReferences(true);
      Document doc = null;
      doc = parser.readStream(dataFromPSP);

      // if we obtained a non-empty, valid XML doc, commence parsing
      if ( doc != null && doc.getDoctype() != null)
      {
        // get the 'doctype' of the XML stream
        String documentName = doc.getDoctype().getName();

        // if the "doctype' is "Error", PSP could not resolve request properly for some reason
        if (documentName.equals("Error"))
          assets = null;

        else if (documentName.equals("AssetList"))
        {
          // get the root node and then its attributes
          Node rootNode = (Node)doc.getDocumentElement();
          NamedNodeMap rootAttributes = rootNode.getAttributes();

           // extract the clusterID for this group of assets
          if (rootAttributes.getNamedItem("ClusterID") != null)
            clusterID = new String (rootAttributes.getNamedItem("ClusterID").toString());

          // perform check to see that this cluster ID matches the
          if (clusterID != null)
            if (!clusterID.equals (requestedCluster))
              return null;

          // cycle throught the list of child nodes, and get the values for each
    		  NodeList theNodes = ((Element)rootNode).getChildNodes();
  		    if (theNodes != null)
          {
            int len = theNodes.getLength();
            String tailNumber = null;
            for ( int i = 0; i < len; i++ )
            {
              // re-initialize place holder variable to hold parsed info
              assetName = null;

      				String nodeName = theNodes.item(i).getNodeName();

              //identify 'AssetName' nodes (this should be the only kind found)
              if ( nodeName.equals("AssetName"))
              {
                // get the attribute for this node
                NamedNodeMap attributes = theNodes.item(i).getAttributes();

                if (attributes.getNamedItem("Name") != null)
                  assetName = new String (attributes.getNamedItem("Name").getNodeValue().toString());

                // If all fields were successfully obtained, add them as a group to <info>
                if (assetName != null)
                {
                  assets.add (assetName);
                }
              }// end if (nodeName.equals ("AssetName");
            }// end if
          } // end for (int i=0,...)
        }//end if (theNodes != null)
      } // end if (documentName.equals ("AssetList")

    } // end try
    catch ( Exception e)
    {  //todo: determine more appropriage error handling
      e.printStackTrace();
      System.out.println ("problem parsing data : " + e.getMessage());
    }
    return assets;
  }

  protected static ArrayList parseAssetProperties
          (ByteArrayInputStream dataFromPSP, String requestedCluster, String requestNSN)
  {
    ArrayList properties = new ArrayList (20);
    String clusterID = null;
    String assetNSN = null;
    String propertyName = null;
    String propertyValue = null;

    try {
      Parser parser = new Parser("Property List");
      parser.setExpandEntityReferences(true);
      Document doc = null;
      doc = parser.readStream(dataFromPSP);

      // if we obtained a non-empty, valid XML doc, commence parsing
      if ( doc != null && doc.getDoctype() != null)
      {
        // get the 'doctype' of the XML stream
        String documentName = doc.getDoctype().getName();

        // if the "doctype' is "Error", PSP could not resolve request properly for some reason
        if (documentName.equals("Error"))
          properties = null;

        else if (documentName.equals("AssetProperties"))
        {
          // get the root node and then its attributes
          Node rootNode = (Node)doc.getDocumentElement();
          NamedNodeMap rootAttributes = rootNode.getAttributes();

           // extract the clusterID for this set of asset details
          if (rootAttributes.getNamedItem("ClusterID") != null)
            clusterID = new String (rootAttributes.getNamedItem("ClusterID").toString());

          // extract the asset NSN for these properties
          if (rootAttributes.getNamedItem("AssetNSN") != null)
            assetNSN = new String (rootAttributes.getNamedItem("AssetNSN").toString());

          // make sure valid values returned
          if (clusterID == null || assetNSN == null)
            return null;
          else if (!clusterID.equals(requestedCluster) || !assetNSN.equals(requestNSN))
              return null;

          // cycle throught the list of child nodes, and get the values for each
    		  NodeList theNodes = ((Element)rootNode).getChildNodes();
  		    if (theNodes != null)
          {
            int len = theNodes.getLength();
            String tailNumber = null;
            for ( int i = 0; i < len; i++ )
            {
              // re-initialize place holder variable to hold parsed info
              propertyName = null;
              propertyValue = null;

      				String nodeName = theNodes.item(i).getNodeName();

              //identify 'AssetName' nodes (this should be the only kind found)
              if (nodeName.equals("Property"))
              {
                // get the attribute for this node
                NamedNodeMap attributes = theNodes.item(i).getAttributes();

                if (attributes.getNamedItem("Name") != null)
                  propertyName = new String (attributes.getNamedItem("Name").getNodeValue().toString());

                if (attributes.getNamedItem("Value") != null)
                  propertyValue = new String (attributes.getNamedItem("Value").getNodeValue().toString());

                // If all fields were successfully obtained, add them as a group to <info>
                if (propertyName != null && propertyValue != null)
                {
                  properties.add (propertyName);
                  properties.add (propertyValue);
                }
                else
                  ;//throw exception ??
              }// end if (nodeName.equals ("AssetName");
            }// end if
          } // end for (int i=0,...)
        }//end if (theNodes != null)
      } // end if (documentName.equals ("AssetList")

    } // end try
    catch ( Exception e)
    {  //todo: determine more appropriage error handling
      e.printStackTrace();
      System.out.println ("XMLParser --> problem parsing data : " + e.getMessage());
    }
    return properties;
  }

  public static boolean isErrorDoc (ByteArrayInputStream dataFromPSP)
  {
    boolean ret = false;
    try {
      Parser parser = new Parser("Error Doc");
      parser.setExpandEntityReferences(true);
      Document doc = null;
      doc = parser.readStream(dataFromPSP);

      //System.out.println ("parsed the doc, about to look for doc. type");

      // if we obtained a non-empty, valid XML doc, commence parsing
      if (doc != null && doc.getDoctype() != null)
      {
        // get the 'doctype' of the XML stream
        String documentName = doc.getDoctype().getName();

        // if the "doctype' is "Error", PSP could not resolve request properly for some reason
        if (documentName.equals("Error"))
          ret = true;
      }
   } // end try
   catch (Exception e)
   {
    System.out.println ("SelectorXMLParser : Exeception in <isErrorDoc> method : " + e.toString());
    ret = false;
   }

   return ret;
  }

  public static void main(String[] args)
  {
    SelectorXMLParser sxp = new SelectorXMLParser();

  }

}