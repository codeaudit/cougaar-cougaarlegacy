/*  Title:        PredictionCriteria.java
 *  Version:      v 1.0
 *  Copyright:    Copyright (c) 2000
 *  Author:       Abraham DeLaO
 *  Company:      21st Century Technologies, Inc
 *  Description:
 *  Future:
 */
package com.centurylogix.finalPredictiveAssessor;

import java.io.Serializable;
import java.util.*;

import org.cougaar.domain.planning.ldm.asset.*;

public class PredictionCriteria implements Serializable
{
  private Asset assetToMonitor = null;
  private String assetName = null;
  private HashMap assetDetails = null;
  private String clusterID;
  private long timeUnit = 0;
  private boolean trackShortFalls = true;
  private boolean trackSurpluses = true;
  private boolean logPlanAlerts = true;
  private boolean dialogBoxes = true;
  private boolean logToFile = false;
  private double sensitivity = .7;

  private static double veryHigh = .9;
  private static double high = .8;
  private static double avg = .7;
  private static double low = .6;
  private static double veryLow = .5;

  // error indicator used to ensure data was transfered properly to logPlan cluster
  private boolean deSerializedProperly = true;

  public PredictionCriteria (String asset, String cluster)
  {
    clusterID = cluster;
    assetName = asset;
  }

  public PredictionCriteria (Asset asset, String cluster)
  {
    clusterID = cluster;
    assetToMonitor = asset;
  }

  public PredictionCriteria (String serialization)
  {
    if (this.deSerialize (serialization))
      deSerializedProperly = true;
    else
      deSerializedProperly = false;
  }

  /**************** MUTATORS *********************/
  public void setTimeUnit (long time)
  {
    this.timeUnit = time;
  }

  public void setTrackShortFalls (boolean input)
  {
    this.trackShortFalls = input;
  }

  public void setTrackSurpluses (boolean input)
  {
    this.trackSurpluses = input;
  }

  public void setAssetDetails (HashMap details)
  {
    this.assetDetails = details;
  }

  // requires <newLevel> to be exactly one of 5 possibilities
  public void setSensitivityLevel (String newLevel)
  {
    if (newLevel == null)
      this.sensitivity = avg;
    else if (newLevel.equals ("VeryHigh"))
      this.sensitivity = veryHigh;
    else if (newLevel.equals ("High"))
      this.sensitivity = high;
    else if (newLevel.equals ("Average"))
      this.sensitivity = avg;
    else if (newLevel.equals ("Low"))
      this.sensitivity = low;
    else if (newLevel.equals ("VeryLow"))
      this.sensitivity = veryLow;
    else
      this.sensitivity = avg;
  }

  public void setNotificationProtocols (boolean alerts, boolean dialogs, boolean file)
  {
    this.logPlanAlerts = alerts;
    this.dialogBoxes = dialogs;
    this.logToFile = file;
  }

  /*************** OBSERVERS ****************/
  public String getClusterID ()
  {
    return clusterID;
  }

  public Asset getAsset ()
  {
    return assetToMonitor;
  }

  public String getAssetName ()
  {
    if (assetName != null)
      return assetName;

    TypeIdentificationPG tipg = assetToMonitor.getTypeIdentificationPG();
    String assetTypeID = tipg.getTypeIdentification();

    if (assetTypeID.startsWith("<") )
      assetTypeID = assetTypeID.substring (1);

    if (assetTypeID.endsWith(">"))
      assetTypeID = assetTypeID.substring (0, assetTypeID.length());

    return assetTypeID;
  }

  public long getTimeUnit ()
  {
    return timeUnit;
  }

  public boolean getTrackShortfalls ()
  {
    return trackShortFalls;
  }

  public boolean getTrackSurpluses ()
  {
    return trackSurpluses;
  }

  public boolean getLogPlanNotify()
  {
    return logPlanAlerts;
  }

  public boolean getDialogAlerts()
  {
    return dialogBoxes;
  }

  public boolean getLogToFile ()
  {
    return logToFile;
  }

  public double getSensitivityLevel ()
  {
    return sensitivity;
  }

  // document how this can be used, as in PatternPredictionAlg
  public double getSensitivityDeviation ()
  {
    return (sensitivity - avg);
  }

  public boolean wasDeserializedProperly ()
  {
    return deSerializedProperly;
  }

 /************* *************************/


  public boolean areEqual (PredictionCriteria pc)
  {
    boolean ret = false;

    if (this.assetName.equals(pc.getAssetName()))
      if (this.clusterID.equals(pc.getClusterID ()))
        ret = true;

    return ret;
  }

  public String serialize ()
  {
    StringBuffer buffer = new StringBuffer(64);

    buffer.append(this.getAssetName() + " ");

    if (this.trackShortFalls)
      buffer.append("T");
    else
      buffer.append("F");

    if (this.trackSurpluses)
      buffer.append("T");
    else
      buffer.append("F");

    if (this.logPlanAlerts)
      buffer.append("T");
    else
      buffer.append("F");

    if (this.dialogBoxes)
      buffer.append("T");
    else
      buffer.append("F");

    if (this.logToFile)
      buffer.append("T");
    else
      buffer.append("F");

    buffer.append (" " + this.sensitivity + " ");

    buffer.append (this.clusterID + " ");

    buffer.append (this.timeUnit);

    String stringRep = buffer.toString();

    return stringRep;
  } // end public String serialize ()

  public boolean deSerialize (String input)
  {
    boolean ret = true;

    String stringRep = input.trim ();

    String assetName = stringRep.substring (0,13);

    // for now, we will create a new Asset, in the future this assets info. would be retrieved
    TypeIdentificationPGImpl tipgi = new TypeIdentificationPGImpl();
    tipgi.setTypeIdentification (assetName);

    assetToMonitor = new Asset();
    assetToMonitor.setTypeIdentificationPG (tipgi);

    String shortfalls = stringRep.substring (14,15);
    if (shortfalls.equals ("T"))
      this.trackShortFalls = true;
    else if (shortfalls.equals ("F"))
      this.trackShortFalls = false;
    else
      ret = false;

    String surpluses = stringRep.substring (15,16);
    if (surpluses.equals ("T"))
      this.trackSurpluses = true;
    else if (surpluses.equals ("F"))
      this.trackSurpluses = false;
    else
      ret = false;


    String alerts = stringRep.substring (16,17);
    if (alerts.equals ("T"))
      this.logPlanAlerts = true;
    else if (alerts.equals ("F"))
      this.logPlanAlerts = false;
    else
      ret = false;

    String dialogs = stringRep.substring (17,18);
    if (dialogs.equals ("T"))
      this.dialogBoxes = true;
    else if (dialogs.equals ("F"))
      this.dialogBoxes = false;
    else
      ret = false;

    String toFile = stringRep.substring (18,19);
    if (toFile.equals ("T"))
      this.logToFile = true;
    else if (toFile.equals ("F"))
      this.logToFile = false;
    else
      ret = false;


    int spaceIndex = stringRep.indexOf (" ", 20);
    if (spaceIndex > 20)
    {
      String sensitivityStr = stringRep.substring (20, spaceIndex);

      try {
        this.sensitivity = (new Double (sensitivityStr)).doubleValue();
      }
      catch (NumberFormatException e) {
        ret = false;
      }
    }
    else
      return false;

    int nextSpaceIndex = stringRep.indexOf (" ",spaceIndex + 1);
    if (nextSpaceIndex > spaceIndex)
      this.clusterID = stringRep.substring (spaceIndex + 1, nextSpaceIndex);
    else
      return false;
    // could maybedo a check here to make sure cluster is valid

    String timeStr = stringRep.substring (nextSpaceIndex + 1);
    try {
      this.timeUnit = (new Long (timeStr)).longValue();
    }
    catch (NumberFormatException e)
    {
      ret = false;
    }


    return ret;
  }

  public String shortToString ()
  {
    StringBuffer buffer = new StringBuffer (64);
    buffer.append ("\t Cluster ID : " + this.clusterID + "\n");
    buffer.append ("\t Asset NSN : " + this.getAssetName() + "\n");

    String stringRep = buffer.toString();
    return stringRep;
  }

 // if <fullDoc> is true we return a fully-formatted XML doc is returned, o/w we just return
  // an XML fragment to be inserted within a larger portion of XML code. Such an XML fragment
  // is used when formating an XML doc of the AssessorState.
  public String toXML (boolean fullDoc)
  {
    StringBuffer sb = new StringBuffer (512);

    if (fullDoc)
    {
       //XML basic document tags
      sb.append( "<?xml version=\"1.0\" encoding=\"US-ASCII\"?>" + "\n");
      sb.append( "<!DOCTYPE " + "Prediction Criteria" + ">\n");
    }

    sb.append ("<Criteria  ClusterID = \"" + this.clusterID + "\"" +
                          " AssetNSN = \"" + getAssetName() + "\"" +
                          " TimeUnit = \"" + this.timeUnit + "\" >\n");

    Iterator iter = assetDetails.entrySet().iterator();

    // cycle through list of pairs, entring each into XML document
    while (iter.hasNext())
    {
      Map.Entry entry = (Map.Entry) iter.next();

      String name = (String) entry.getKey();
      String value = (String) entry.getValue();

      sb.append( "<AssetDetails ");
      sb.append( "Name = \"" + name + "\" ");
      sb.append( "Value = \"" + value + "\"/>\n");
    } // end while

    sb.append ("</Criteria>\n");
    String stringRep = sb.toString();
    return stringRep;

  }// end toXML (boolean)

  public String toString ()
  {
    StringBuffer buffer = new StringBuffer (256);

    buffer.append ("\t\t\t Prediction Critera \n");
    buffer.append ("Cluster ID : " + this.clusterID + "\n");
    buffer.append ("Asset NSN : " + this.getAssetName() + "\n");
    buffer.append ("Track Shortfalls : " + this.trackShortFalls + "\n");
    buffer.append ("Track Surpluses : " + this.trackSurpluses + "\n");
    buffer.append ("Sensitivity : " + this.sensitivity + "\n");
    buffer.append ("Alerts to LogPlan : " + this.logPlanAlerts + "\n");
    buffer.append ("Notify via dialog boxes : " + this.dialogBoxes + "\n");
    buffer.append ("Log to File : " + this.logToFile + "\n");
    buffer.append ("Time Unit : " + this.timeUnit + "\n");

    if (assetDetails != null)
      buffer.append ("Asset Properties : " + this.assetDetails.toString());

    String stringRep = buffer.toString();
    return stringRep;
  }//end public void toString ()

  public static void main(String[] args)
  {
    TypeIdentificationPGImpl tipgi = new TypeIdentificationPGImpl();
    tipgi.setTypeIdentification ("NSN/000000000");

    Asset dummyAsset = new Asset();
    dummyAsset.setTypeIdentificationPG (tipgi);

    String clusterID = "SomeCluster";

    PredictionCriteria predictionCriteria = new PredictionCriteria(dummyAsset, clusterID);

  }

}