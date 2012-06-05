/*  Title:        AssetSelectorMediator.java
 *  Version:      v 1.0
 *  Copyright:    Copyright (c) 2000
 *  Author:       Abraham DeLaO
 *  Company:      21st Century Technologies, Inc
 *  Description:  This class serves as the sole intermediary between our client-side asset
 *                selection GUI and the running ALP society infrastructure. It provided a means
 *                of synchronous communication with the Asset Selctor PSP that resides on cluster
 *                that are running our PlugIn group and give us access to each cluster's LogPLan.
 *                All requests from the GUI for cluster information are transmitted to the PSP
 *                in the form of simple text messages formatted in a manner that is expected by
 *                the PSP. The structure of these messages is arbitrary but must be maintained
 *                consistently between these two modules. The replies recieved from the PSP are
 *                XML formatted messages that are dispatched for parsing and passed back to the GUI.
 *  Future :      1. Possibly try to write more rigourous test methods in main
 */

package com.centurylogix.finalPredictiveAssessor.selector;

import com.centurylogix.finalPredictiveAssessor.DebugFrame;
import com.centurylogix.finalPredictiveAssessor.PredictionCriteria;

import java.util.ArrayList;
import java.net.*;
import java.io.*;

public class AssetSelectorMediator
{
  private AssetSelectorModel model;
  //private AssetSelectionGUIFrame gui;

  // data used to connect to our PSP in running ALP society
  private final static String PSP_Package = "/alpine/demo/";
  private final static String PSP_Name = "PREDICTION_INFO.PSP";
  private final static String host = "localhost";
  private final static String rHost = "65.197.68.40";
  private final static String port = "5555";
  private static String urlName;

  private ByteArrayInputStream dataFromPSP; //stream of data from PSP
  private URL clusterURL; // the URL of the PSP of interest to us
  private static boolean isRemote = false;
  private static DebugFrame df = new DebugFrame(false);

  public AssetSelectorMediator ()
  {
    df.setTitle("AssetSelectorMediator Debug Frame");
    df.setBounds(200, 200, 500, 400);
    df.show();
  }

  /* This method returns an ArrayList of String objects that represent the names of all clusters currently
   * registered with the ALP Name Server. These names are retrieve by sending a "clusters" request to
   * the Asset Selector PSP, which replies with an XML document encoding the list of known clusters.
   */
  protected ArrayList getClusterNames ()
  {
    ArrayList names;

    // set the URL we will query to the default cluster that has loaded our PSP. This default cluster answers
      // all vaugely specified PSP queries such as this one.
    setURL (null);

    // try quering PSP for cluster info, if data is returned, attempt to parse it as XML stream.
    if (getPredictionData (new String("clusters")) && dataFromPSP != null)
    {
      // if error occurs during parsing return null
      try {
        df.addText ("beginning  SelectorXMLParser.parseClusterList");
        names = SelectorXMLParser.parseClusterList (dataFromPSP);
        df.addText ("End  SelectorXMLParser.parseClusterList");
      }
      catch (Exception e) {
        df.addText ("exception at  SelectorXMLParser.parseClusterList");
        names = null;
      }
    } // end if
    else // if PSP does not return data, return null to caller
      names = null;

    dataFromPSP = null;
    df.addText ("End getClusterNames");
    return names;
  } // end protected ArrayList getClusterModel()

  /* Returns an ArrayList of Strings that are 9-digit NSN identifiers that represent trackable assets
   * which are owned by the cluster organization selected by the user in the GUI and currently
   * represented in the AssetSelectionModel. The PSP returns a XML formatted list of assets that
   * is parsed to form an ArrayList to be returned to the caller.
   */
  protected ArrayList getTrackableAssets (String clusterID)
  {
    ArrayList assetNSNs;

    if (clusterID == null)
      return null;

    // set the URL we will query to point to the PSP instance loaded by <clusterID>
    setURL (clusterID);

    // set the parameter we will use to query the PSP for the list of assts
    String queryParam = new String ("Assets:"+clusterID);

    // try quering PSP, if valid data is returned try parsing it for asset data
    if (getPredictionData (queryParam) && dataFromPSP !=null)
      assetNSNs = SelectorXMLParser.parseAssetList (dataFromPSP, clusterID);
    else
      assetNSNs = null;

    // reset holdor of data retrieved from PSP
    dataFromPSP = null;
    return assetNSNs;

  } // end protected ArrayList getTrackableAssets (String)


  /* Returns an ArrayList of alternating asset property names and asset property values represented
   * as Strings.  This list of details petains to the cluster and asset specified in the GUI and
   * reflected in the AssetSelectionModel.  The returned data is formated as an ArrayList of
   * consecutive pairs of these property names and values. For instance, the ArrayList might
   * be popluated with data as follows : [Weight, 30kg, Width, 10in, Capacity, 10 persons,...]
   */
  protected ArrayList getAssetDetails (String clusterID, String assetNSN)
  {
    df.addText ("Beginning getAssetDetails");
    ArrayList properties;

    // set the URL we will query to point to the PSP instance loaded by <clusterID>
    setURL (clusterID);

    // form the query that will be sent to the PSP.
    String queryParam = new String ("Properties:"+assetNSN+clusterID);

    // try quering PSP, if valid data is returned try parsing it for asset data
    if (getPredictionData (queryParam) && dataFromPSP != null)
      properties = SelectorXMLParser.parseAssetProperties (dataFromPSP, clusterID, assetNSN);
    else
      properties = null;

    // reset holder of PSP data
    dataFromPSP = null;

    df.addText ("End getAssetDetails");

    return properties;
  } // end protected ArrayList getAssetProperties (String, String)

  /* Once a PredictionCriteria data structure has been instantiated and populated with verified
   * data originiating from the asset selection GUI, it is passed as a parameter during the
   * invokation of this method. The PredictionCriteria data structure is then trasformed into
   * a string representation for transmission to the PSP.  It is there re-assembled and
   * published to the logPlan. If any errors occur during this process, an XML message is
   * returned indicating failure.
   */
  protected boolean publishSelection (PredictionCriteria pc, boolean remote)
  {
    df.addText ("Beginning publishSelection");
    boolean ret = false;
    isRemote = remote;
    if (pc == null)
      return false;

    //retrieve the name of the cluster of interest from the data structure
    String clusterID = pc.getClusterID ();

    // set the URL we will query to point to the PSP instance loaded by <clusterID>
    setURL (clusterID);

    //try quering PSP, if valid data is returned try parsing it for asset data
    if (getPredictionData (pc.serialize()) && dataFromPSP != null)
    {
      // if returned data is XML Error doc, signal that the process failed
      if (SelectorXMLParser.isErrorDoc (dataFromPSP))
        ret = false;
      else //o.w. assume everything worked out
        ret = true;
    }
    df.addText ("End of publishSelection");
    return ret;
  } //end protected boolean publishSelection (PredictionCriteria)

  /* The method sets the URL which will be used to query the particular PSP which we are
   * interested in. If <clusterName> is null, we are only interested in quering the default cluster
   * for general ALP society information. Otherwise, <clusterName> will specify the cluster with
   * which we wish to communicate for publishing and querying data from its LogPlan
   * MODIFIES : <this.clusterURL>
   */

  private void setURL (String clusterName)
  {

    if (clusterName != null)
    {
      if(!isRemote)
      {
        urlName = "http://" + host + ":" + port + "/$" + clusterName + PSP_Package + PSP_Name;
      }
/*      else
      {
        urlName = "http://" + rHost + ":" + port + "/$" + clusterName + PSP_Package + PSP_Name;
      }*/
    }

    else
    {
      if(!isRemote)
      {
        urlName = "http://" + host + ":" + port + PSP_Package + PSP_Name;
      }

/*      else
      {
        urlName = "http://" + rHost + ":" + port + PSP_Package + PSP_Name;
      }*/
    }

     try
     {
        clusterURL = new URL(urlName);
     }
     catch (MalformedURLException e)
     {
      // todo: need better error handling should pass message back to gui for dialog pop-up
      // box in the future
      //gui.errorInfo ();
      System.out.println(e);
      return;
     }
  } // end void setURL (String)


  /* This method simply converts an InpustStream of data into a an array of byte representations of
   * the data.
   */
  private byte[] getResponse(InputStream is) throws IOException
  {
    ByteArrayOutputStream os = new ByteArrayOutputStream();
    byte b[] = new byte[512];
    int len;
    while( (len = is.read( b,0,512)) > -1 )
      os.write( b,0,len);
    return os.toByteArray();
  } // end public byte[] getResponse (InputStream)


  /* The actual querying of the PSP designated by <clusterURL> for specific data and retrieves any
   * returned information. Thus all communication with the PSP via this method is synchronous. The
   * data in <queryParameters> encodes the type of data requested and is transmitted to the cluster
   * PSP. The returned data is translated to an array of byte encoded data which can then be parsed
   * for our requested data
   * MODIFIES : <this.dataFromPSP>
   */
  private boolean getPredictionData(String queryParameter)
  {
    URLConnection uc = null;
    int numberOfAttempts = 0;
    boolean establishedConnection = false;

    // attempt to connect to the PSP
    while (establishedConnection == false && numberOfAttempts < 10)
    {
      // if we're retrying this, stall to see if problem clears up
      if (numberOfAttempts > 0)
      {
        // Sleep for 5 seconds before attempting to connect again
        try {
          this.wait(5000);
        }
        catch (InterruptedException e) {
          // Do nothing, this is normal
        }
      }

      try {
        uc = clusterURL.openConnection();
        establishedConnection = true;
      }
      catch (IOException e) {
        // Failed to establish connectino to server. This could be a result of
        // the server not running or an IP address or Port problem in the URL
        numberOfAttempts++;
      }
    } // end while (...)

    // continue communication with server if connection was established
    if (establishedConnection == true)
    {
      uc.setDoInput(true); // will be recieving input from connection
      uc.setDoOutput(true); // will not be returning info. on connection

      // get the input associated with the connection
      try {
        OutputStream os = uc.getOutputStream();
        uc.connect();
        PrintStream outPrint = new PrintStream (os);

        // print to output stream that is sent to PSP
        outPrint.print (queryParameter);

        // get the returned input from the PSP
        InputStream is = uc.getInputStream();

        // encode data as byte stream
        byte[] response = getResponse(is);
        dataFromPSP = new ByteArrayInputStream (response);

      }
      catch (IOException e) {
        //System.out.println ("Error connection to server in : getPredictionData");
        // todo: throw gui error msg.
      }
    }// end if (establishedConnection == true)
    df.addText ("End of getPredictionData");
    return establishedConnection;
  } // end public boolean getAnalysisData()


  /*Thes test methods require society support (a running PSP)*/
  public static void main(String[] args)
  {
    df.addText ("Beginning Main");
    AssetSelectorMediator asm = new AssetSelectorMediator();

    asm.getClusterNames();

    asm.getTrackableAssets ("3ID");

    asm.getAssetDetails ("3ID", "NSN/123456789");
    df.addText ("Beginning TIPG");
    org.cougaar.domain.planning.ldm.asset.TypeIdentificationPGImpl tipgi =
            new org.cougaar.domain.planning.ldm.asset.TypeIdentificationPGImpl();
    tipgi.setTypeIdentification ("NSN/987654321");
    df.addText ("Beginning dummyAsset");
    org.cougaar.domain.planning.ldm.asset.Asset dummyAsset = new org.cougaar.domain.planning.ldm.asset.Asset();
    dummyAsset.setTypeIdentificationPG (tipgi);
    df.addText ("Beginning prediction Criteria");
    PredictionCriteria pc = new PredictionCriteria (dummyAsset, "3ID");
    pc.setTimeUnit (5000);
    pc.setNotificationProtocols (false,false,true);
    pc.setSensitivityLevel ("Average");
    df.addText ("About to publish in main");
    asm.publishSelection (pc, isRemote);
    df.addText ("End main");
  } // end main

}// end class