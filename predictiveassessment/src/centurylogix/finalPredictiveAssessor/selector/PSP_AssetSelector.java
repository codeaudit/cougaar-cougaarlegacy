/*  Title:        AssetSelectorMediator.java
 *  Version:      v 2.0
 *  Copyright:    Copyright (c) 2000
 *  Author:       Abraham DeLaO
 *  Company:      21st Century Technologies, Inc
 *  Description:  This PSP provides ALP society access to the client-side asset selection GUI.
 *                Requests from the GUI are forwarded to this PSP by the AssetSelectionMediator.
 *                These requests are in the form of specifically formatted text strings that are
 *                parsed here. The appropriate response is then determined and the requested
 *                information is encoded in XML format and returned to the calling method in
 *                the Mediator. An instance of this PSP is loaded on each cluster running our Plugin
 *                and requests can be directed to specific instances by quering the PSP by directly
 *                referring to the cluster of interest in the URL query. Otherwise, queries are
 *                handled by some default cluster when ambiguous URL queries are made. This strategy
 *                is used to make general society-wide queries where the particulars of the
 *                answering cluster is unimportant.
 * Future:        1. Create a new Success XML document that is returned to the Mediator when logPlan
 *                publishes were successful. Currently, we assume success if no know error was found.
 */
package com.centurylogix.finalPredictiveAssessor.selector;

import com.centurylogix.finalPredictiveAssessor.*;

import org.cougaar.lib.planserver.*;

import org.cougaar.domain.planning.ldm.asset.Asset;
import org.cougaar.domain.planning.ldm.asset.ItemIdentificationPG;

import org.cougaar.util.UnaryPredicate;

import org.cougaar.core.cluster.IncrementalSubscription;
import org.cougaar.core.cluster.Subscription;

import java.io.PrintStream;
import java.util.*;
//added
//import org.cougaar.lib.planserver.psp.PSP_PlugInLoader.*;

public class PSP_AssetSelector extends PSP_BaseAdapter implements PlanServiceProvider, UISubscriber
{
  // standard XML document declaration
  private static String XMLDeclaration = "<?xml version=\"1.0\" encoding=\"US-ASCII\"?>";

  // PSP_id is simply the name of the psp
  private String PSP_id = "PREDICTION_INFO.PSP";
  private PSPState state;

  String [] NSNString;
  String [] deetailsString;
  //static int valueCount = 0;
  //static int cycleCount;

  // this list of asset properties is the only state maintained by this class. It holds the list of
  // asset detials most recently requested by the GUI. When a request submission is recieved these
  // asset properties will pertain to the asset being designated for analysis.
  HashMap assetProperties = null;

  private static DebugFrame df = new DebugFrame (false);

  public PSP_AssetSelector()
  {
    super();
    // todo: once this class is finished eliminate this debugging stuff
    df.setTitle("PSP_AssetSelector Debug Frame");
    df.setBounds(200, 200, 500, 400);
    df.show();
    df.addText ("Path is : " + this.getPath());
    df.addText ("String Rep : " + this.toString());
  }

  /* This constructor is required, but never used. */
  public PSP_AssetSelector( String pkg, String id) throws RuntimePSPException
  {
    setResourceLocation( pkg, id);
    PSP_id = id;
  }


  /* This PSP is referenced directly (in the URL from the client)
   * and hence this should not be called
   */
  public boolean test(HttpInput queryParameters, PlanServiceContext psc)
  {
    super.initializeTest();
    return false;
  }

  /* Called when a request is received from a client. It gets the POST data; parse the request;
   * get the log plan objects that match the request; encode these in XML document and
   * send the document to the client. The POST requests must be of a previously expected form
   * and all other requests result in failure.
   */

   class myState extends PSPState
   {
      public myState(UISubscriber xsubscriber, HttpInput query_parameters, PlanServiceContext xpsc)
      {
          super(xsubscriber, query_parameters, xpsc);
      }
      public void setParam(String name, String value)
      {
      }
   }

  public void execute(PrintStream out, HttpInput queryParameters,
		       PlanServiceContext psc, PlanServiceUtilities psu) throws Exception
  {
    state = new myState(this, queryParameters, psc);
    df.addText ("cluster psp URL : " + state.cluster_psp_url + "\n");

    String postData = null; // string rep. of the URL post data

    try {
      // if postData is not null, try parsing it
      if (queryParameters.getPostData () != null)
      {

        String postDataRaw = new String (queryParameters.getPostData ());
        postData = postDataRaw.trim();

        df.addText ("Serialized stuff : " + postData);
        // if postData is "clusters", this constitutes a request for a list of clusters
        if (postData.equals("clusters") && psc != null)
        {
          Vector names = new Vector ();

          // get list of names of all cluster registered w/ ALP name server
          psc.getAllNames (names);

          // format this list as XML doc and send it as reply
          generateXMLClusterList(out, names);

          df.addText ("Returned list of names : " + names.toString());
        }

        // if postData begins w/ "Assets:", this constitutes a request for a particular list of assets
        else if (postData.startsWith ("Assets:"))
        {
          // parse the name of the specific cluster we are interested in.
          // (This should be unnecessary since the request should have been directed to the particular
          // cluster PSP we are interested in)
          String clusterID = postData.substring (7);
          // todo: possibly add a check to make sure clusterID is the name of this cluster

          // just generate a canned list of asstets for now
          ArrayList assetList = getAssetNames (clusterID);

          // format this list of assets in an XML doc and send it as reply
          generateXMLAssetList (out, clusterID, assetList);
        }

        // if postData begins w/ "Properties:", this is a request of a specific asstets details
        else if (postData.startsWith("Properties:"))
        {
          // extract the asset ID from the post data
          String assetNSN = postData.substring (11,24);

          // parse the cluster ID, (as above, this is probably unnecesary other than for performing
          //  some form of double-check to make sure correct cluster is involved
          String clusterID = postData.substring (24);

          // create structure containing asset details
          assetProperties = getAssetProperties (clusterID, assetNSN);

          // format this list of asset details in an XML doc and send it as reply
          generateXMLAssetProperties (out, clusterID, assetNSN, assetProperties);
        }

        // if postData begins with "NSN", this request is a prediction submission
        else if (postData.startsWith ("NSN"))
        {
          df.addText ("processing request to submit prediction request : " + postData.toString());
          //wait (10000);
          // in this case, PostData is a serialized PredictionCriteria data struct, re-construct
            // the data structure by using special constuctor
          PredictionCriteria pc = new PredictionCriteria (postData);

          if (pc != null)
            df.addText ("Re-generated Predcition Criteria object : " + pc.toString());
          else
            df.addText ("Could not regenerate pc");

          // if deSerialization completed properly, proceed to publish it to LogPlan
          if (pc != null && pc.wasDeserializedProperly ())
          {
            // set the asset detials that we most recently sent to the GUI
            pc.setAssetDetails (assetProperties);

            df.addText ("Trying to publish; de-serialized properly : " + pc.toString());
            //get plugIn-like logPlan access to the cluster
            ServerPlugInSupport sps = psc.getServerPlugInSupport();

//            PredictiveAssessorTask pat = new PredictiveAssessorTask ();
//            pat.setVerb ("InitiatePredictiveAssessment");
//            pat.setDirectObject (pc);

            sps.publishAddForSubscriber (pc);

//            df.addText (pat.toString());
            df.addText ("Published PredictionCriteria to logPlan ");

            generateXMLConfirmation (out);
          }
          else // if postData not recoginized reply with an XML error document
          {
            df.addText ("post data could not be parsed properly for prediction data");
            generateErrorXML (out);

          }
        }

      }// end if (queryParameters.getPostData != null)
    } // end try

    catch (Exception ex)
    { // if we found some error, signal that the submission has failed
      generateErrorXML (out);
      out.flush();
    }
  }//end public void execute()

  /* This method takes the list of cluster names listed in <clusterNames> and uses it to
   * format an XML document with this info. This document is then placed on the waiting
   * output stream <out> that is to be returned to the caller of the PSP. The DTD of the
   * XML documented created here is as follows :
   *
   *  <!ELEMENT ClusterList (ClusterName+)>
   *  <!ELEMENT ClusterName EMPTY>
   *  <!ATTLIST ClusterName
	 *       Name				CDATA #REQUIRED>
   *
   *  REQUIRES : <out> and <clusterNames> are not null
   */
  private void generateXMLClusterList (PrintStream out, Vector clusterNames)
  {
    StringBuffer buffer = new StringBuffer(1024);

    //XML basic document tags and precursor info
    buffer.append( XMLDeclaration + "\n");
    buffer.append( "<!DOCTYPE " + "ClusterList" + ">\n");
    buffer.append( "<" + "ClusterList" + ">\n");

    Enumeration names = clusterNames.elements ();

    // generate XML entry for each cluster name
    while (names.hasMoreElements())
    {
      String cluster = (String) names.nextElement();

      buffer.append( "<ClusterName ");
      buffer.append( "Name=\"" + cluster+ "\"/>");
    }

    buffer.append( "</" + "ClusterList" + ">\n");

    String XMLString = buffer.toString();

    out.print(XMLString);
    out.close();
  }// end private void genarateXMLClusterList (PrintStream)


  /* This method takes the list of asset names listed in <assetNames> and uses it to
   * format an XML document with this info. This document is then placed on the waiting
   * output stream <out> that is to be returned to the caller of the PSP. The DTD of the
   * XML documented created here is as follows :
   *
   *  <!ELEMENT AssetList (AssetName+)>
   *  <!ATTLIST AssetList
   *    ClusterID			CDATA #REQUIRED>
   *
   *  <!ELEMENT AssetName EMPTY>
   *  <!ATTLIST AssetName
   *	  Name				CDATA #REQUIRED>
   *
   *   REQUIRES : <out>, <clusterID> and <assetNames> are not null
   */
  private void generateXMLAssetList(PrintStream out, String clusterID, ArrayList assetNames)
  {
    StringBuffer buffer = new StringBuffer(1024);

    //XML basic document tags
    buffer.append( XMLDeclaration + "\n");
    buffer.append( "<!DOCTYPE " + "AssetList" + ">\n");
    buffer.append( "<" + "AssetList " + "ClusterID = \""+ clusterID + "\">\n");

    Iterator iter = assetNames.iterator();

    // cycle through list of assets, making an entry for each one
    while (iter.hasNext())
    {
      String name = (String) iter.next();

      buffer.append( "<AssetName ");
      buffer.append( "Name=\"" + name + "\"/>");

    } // end while

    // this is the closing xml tag for the document
    buffer.append( "</" + "AssetList" + ">\n");

    String XMLString = buffer.toString();

    out.print(XMLString);
    out.close();

  }// end private void generateEmptyXML ()


  /* This method takes the list of property names/value pairs listed in <properties> and uses it to
   * format an XML document with this info. This document is then placed on the waiting
   * output stream <out> that is to be returned to the caller of the PSP. The DTD of the
   * XML documented created here is as follows :
   *
   *  <!ELEMENT AssetProperties (Property+)>
   *  <!ATTLIST AssetProperties
	 *    ClusterID			CDATA #REQUIRED
   *   	AssetNSN			CDATA #REQUIRED>
   *
   *  <!ELEMENT Property EMPTY>
   *   <!ATTLIST Property
   *   	Name				CDATA #REQUIRED
   *   	Value				CDATA #REQUIRED>
   *
   * REQUIRES : all paramters must be non-null
   */
  private void generateXMLAssetProperties (PrintStream out, String clusterID, String assetNSN, HashMap properties)
  {
    StringBuffer buffer = new StringBuffer(1024);

    //XML basic document tags
    buffer.append( XMLDeclaration + "\n");
    buffer.append( "<!DOCTYPE " + "AssetProperties" + ">\n");
    buffer.append( "<" + "AssetProperties " + "ClusterID = \""+ clusterID + "\" AssetNSN = \"" +assetNSN+ "\">\n");

    Iterator iter = properties.entrySet().iterator();

    // cycle through list of pairs, entring each into XML document
    while (iter.hasNext())
    {
      Map.Entry entry = (Map.Entry) iter.next();

      String name = (String) entry.getKey();
      String value = (String) entry.getValue();

      buffer.append( "<Property ");
      buffer.append( "Name = \"" + name + "\" ");
      buffer.append( "Value = \"" + value + "\"/>");
    } // end while

    // this is the closing xml tag for the document
    buffer.append( "</" + "AssetProperties" + ">\n");

    String XMLString = buffer.toString();

    out.print(XMLString);
    out.close();
  } //end private void generateXMLAssetProperties (PrintStream, String, String, HashMap)


  // todo: provide overview for this method
  private void generateErrorXML (PrintStream out)
  {
    String XMLErrorDeclaration = "<!DOCTYPE ERROR>";
    // todo: determine what this base case XML doc should be
    StringBuffer buffer = new StringBuffer(64);

    buffer.append (XMLDeclaration + "\n");
    buffer.append (XMLErrorDeclaration + "\n");
    buffer.append ("<ERROR> \n <Promblems in Asset Selection PSP> \n </ERROR>");

    String bufferString = buffer.toString();
    out.print (bufferString);
    out.close();
    //df.addText ("No assets found.");
  }


   // todo: provide overview for this method
  private void generateXMLConfirmation (PrintStream out)
  {
    // todo: determine what this base case XML doc should be
    StringBuffer buffer = new StringBuffer(64);

    buffer.append (XMLDeclaration + "\n");
    buffer.append ( "<!DOCTYPE " + "Confirmation" + ">\n");
    buffer.append ("<Confirm> </Confirm>");

    String bufferString = buffer.toString();
    out.print (bufferString);
    out.close();
  }

  /* Used for testing purposes to generate a list of asset NSN values */
  private ArrayList getAssetNames (String clusterID)
  {
    // for now just return identical list for all clusters.
    ArrayList assetNames = new ArrayList ();
    String asset;

    if (clusterID.compareTo("MCCGlobalMode") == 0)
    {
      for (int i=0, numNSN = 15; i < numNSN; i++)
      {
        asset = createAssetNSN(i);
        assetNames.add (asset);
      }
    }

    else if (clusterID.compareTo("3ID") == 0)
    {
      for (int i=15, numNSN = 30; i < numNSN; i++)
      {
        asset = createAssetNSN(i);
        assetNames.add (asset);
      }
    }

    else if (clusterID.compareTo("1BDE") == 0)
    {
      for (int i=30, numNSN = 45; i < numNSN; i++)
      {
        asset = createAssetNSN(i);
        assetNames.add (asset);
      }
    }

    else if (clusterID.compareTo("3-69-ARBN") == 0)
    {
      for (int i=45, numNSN = 60; i < numNSN; i++)
      {
        asset = createAssetNSN(i);
        assetNames.add (asset);
      }
    }

    else
    {
        System.out.println("Error:  No such cluster exists.");
    }
    return assetNames;
  } // end private ArrayList getAssetNames (String)

  /* Used for testing to generate a HashMap of asset property details */
  private HashMap getAssetProperties (String clusterID, String assetNSN)
  {
    // for now don't do anything fancy, just return a default hashmap

    HashMap properties = new HashMap (10);

    int valueCount = 0;
    int NSNStep = 15;
    int detailsStep = 7;

    for (int i = 0; i < NSNStep; i++)
    {
      if ( assetNSN.endsWith(createAssetNSN(i)) || assetNSN.endsWith(createAssetNSN(NSNStep + i)) ||
        assetNSN.endsWith(createAssetNSN((2 * NSNStep) + i)) || assetNSN.endsWith(createAssetNSN((3 * NSNStep) + i)) )
      {
        valueCount = i * detailsStep;
      }

    }//end for loop
    //System.out.println("Asset ID = " + createAssetDetails(valueCount) + "ValueCount =" + valueCount);
    String name = "Asset ID"; String value = createAssetDetails(valueCount++);
    properties.put (name, value);
    //System.out.println("Weight = " + createAssetDetails(valueCount) + "ValueCount =" + valueCount);
    name = "Weight"; value = createAssetDetails(valueCount++);
    properties.put (name, value);
    //System.out.println("Lot Size = " + createAssetDetails(valueCount) + "ValueCount =" + valueCount);
    name = "Lot Size"; value = createAssetDetails(valueCount++);
    properties.put (name, value);
    //System.out.println("Manufacturer = " + createAssetDetails(valueCount) + "ValueCount =" + valueCount);
    name = "Manufacturer"; value = createAssetDetails(valueCount++);
    properties.put (name, value);
    //System.out.println("Supplier = " + createAssetDetails(valueCount) + "ValueCount =" + valueCount);
    name = "Supplier"; value = createAssetDetails(valueCount++);
    properties.put (name, value);
    //System.out.println("HazMat Classification = " + createAssetDetails(valueCount) + "ValueCount =" + valueCount);
    name = "HazMat Classification"; value = createAssetDetails(valueCount++);
    properties.put (name, value);
    //System.out.println("Order Latency = " + createAssetDetails(valueCount) + "ValueCount =" + valueCount+ "\n\n");
    name = "Order Latency"; value = createAssetDetails(valueCount++);
    properties.put (name, value);
    return properties;
  } // end private HashMap getAssetProperties (String, String)

  /* This method signifies that the PSP output is in XML form */
  public boolean returnsXML(){return true;}

  /* Signals that PSP output is not an HTML document */
  public boolean returnsHTML(){return false;}


  /* This method is necessary to satisfy the interface spec. but thankfully is not used since
   * this PSP returns several differnt XML formated docs with different DTD's.
   */
  public String getDTD(){return null;}


  /* The UISubscriber interface. This PSP doesn't care if subscriptions change
   *  because it treats each request as a new request.
   */
  public void subscriptionChanged(Subscription subscription) {}

  /* Currently unused, but returns the name by which this PSP is registred and located */
  public String getID(){ return PSP_id;}

  String createAssetNSN(int index)
  {
      String [] NSNString = {"NSN/564256253","NSN/621235344","NSN/623659566","NSN/032642659",
      "NSN/659734561","NSN/542312458","NSN/235647885","NSN/643522300","NSN/895623457",
      "NSN/613254451","NSN/298557389","NSN/735418744","NSN/646352928","NSN/457827877",
      "NSN/466235125","NSN/115642789","NSN/172348931","NSN/224424343","NSN/236832932",
      "NSN/289928324","NSN/332849323","NSN/468732331","NSN/500483983","NSN/552383320",
      "NSN/594831578","NSN/624842931","NSN/633839239","NSN/712232932","NSN/733332954",
      "NSN/882139311","NSN/364985792","NSN/987632549","NSN/326794815","NSN/796324519",
      "NSN/134564124","NSN/649837259","NSN/987635614","NSN/649852912","NSN/653978152",
      "NSN/632159844","NSN/786512356","NSN/569845568","NSN/193655486","NSN/751698435",
      "NSN/984631578","NSN/965423143","NSN/789548632","NSN/563654121","NSN/966987431",
      "NSN/852631547","NSN/756985631","NSN/563211456","NSN/478745620","NSN/362514784",
      "NSN/986314454","NSN/236551237","NSN/654432115","NSN/643522627","NSN/265342156",
      "NSN/545456581"};
      return NSNString[index];
  }

  String createAssetDetails(int index)
  {
      String [] detailsString = {"20mm Munitions", "10bls", "20 Rounds", "Remington", "125th Quartermaster", "Level 3", "2 weeks",
        "AMRAAM", "335lbs", "10", "Hughes/Raytheon", "Air Combat Command", "level 2", "5 days",
        "Harpoon", "150lbs", "20", "Boeing", "Air Combat Command", "level 2", "1 week",
        "Harm", "100lbs", "30", "Boeing", "Air Combat Command", "level 3", "1 week",
        "Maverick", "126lbs", "15", "Raytheon", "Air Combat Command", "level 2", "2 weeks",
        "Penguin", "181lbs", "25", "Hughes Aircraft Co", "Air Combat Command", "level 2", "1 week",
        "Phoenix", "268lbs", "10", "Kongsberg Vaapenfabrikk", "PEO (W)", "level 2", "7 days",
        "Sea Sparrow", "198lbs", "15", "Hughes Aircraft Co", "Air Combat Command", "level 2", "2 days",
        "Sidewinder", "146lbs", "10", "Raytheon", "Air Combat Command", "level 2", "6 days",
        "AIM-7 Sparrow", "331lbs", "10", "General Dynamics", "PEO (W)", "level 2", "3 week",
        "Harpoon", "319lbs", "20", "Ford Aerospace and Communications Corp.", "Air Combat Command", "level 2", "3 days",
        "AGM-130 Missile", "147lbs", "20", "Boeing", "Air Combat Command", "level 2", "8 days",
        "GBU-15", "256lbs", "35", "Atlantic Research", "PEO (W)", "level 2", "9 days",
        "Standard", "200lbs", "25", "Raytheon", "Air Combat Command", "level 2", "7 days",
        "SLAM-ER", "175lbs", "15", "Hughes", "PEO (W)", "level 2", "9 days",
        "GBU 31/32", "214lbs", "10", "Lockheed Martin", "Air Combat Command", "level 2", "3 days"};

        return detailsString[index];
  }

}
