/*  Title:        AssetSelectorModel.java
 *  Version:      v 1.0
 *  Copyright:    Copyright (c) 2000
 *  Author:       Henzil Browne
 *  Company:      21st Century Technologies, Inc
 *  Description:
 *  Future:
 */

package com.centurylogix.finalPredictiveAssessor.selector;

import com.centurylogix.finalPredictiveAssessor.PredictionCriteria;
import java.util.*;
import java.io.*;

public class AssetSelectorModel
{
  AssetSelectorMediator mediator;
  ArrayList clusters = new ArrayList ();
  ArrayList assets = new ArrayList ();
  ArrayList details = new ArrayList ();
  static String clusterName;
  static String assetName;
  public static boolean assetShortfalls = true;
  public static boolean assetSurpluses = true;
  static String Sensitivity;
  static boolean logPlanAlert;
  static boolean dialogWindow;
  static boolean logToFile;
  long timeUnit;
  static boolean remote = false;

  public AssetSelectorModel ()
  {
    this.mediator = new AssetSelectorMediator ();
  }

  //Populate GUI's ComboBox with Clusters
  ArrayList getClusters()
  {
    clusters = mediator.getClusterNames();

    return clusters;
  }

  //Populate GUI's ListBox with Assets
  ArrayList getAssets(String cluster)
  {
    assets = mediator.getTrackableAssets(cluster);
    return assets;
  }

  //Returns Asset Details
  ArrayList assetDetails(String asset)
  {
    details = mediator.getAssetDetails(clusterName, asset);
    return details;
  }

  //Set Cluster and Asset
  public void setSelectedCluster(String selectedCluster)
  {
    clusterName = selectedCluster;
  }

  //Set the Selected Asset
  void setSelectedAsset(String selectedAsset)
  {
    assetName = selectedAsset;
  }

  //Set Time Units
  public void setTimeUnits(String time)
  {
      if (time == "Hours")
        timeUnit = (1000 * 60  * 60);

      else if(time == "Days")
        timeUnit = (1000 * 60 *  60 * 24);

      else if(time == "Weeks")
        timeUnit = (1000 * 60 * 60 * 24 * 7);

      else if(time == "Months")
        timeUnit = (1000 * 60 * 60 * 24 * 30);

      else if(time == "Years")
        timeUnit = (1000 * 60 * 60 * 24 * 365);

      else
        System.out.println("Invalid Time Unit Entered! \n");

      timeUnit = 5000;

  }

  //Set Events
  public void setShortfalls(boolean Shortfalls)
  {
    assetShortfalls = Shortfalls;
  }

  public void setSurpluses(boolean Surpluses)
  {
    assetSurpluses = Surpluses;
  }

  //Set Sensitivity Levels
  public void setSensitivityLevels(String level)
  {
    Sensitivity = level;
  }

  //Set Notification Actions
  static void setNotificationActions(boolean LogPlan, boolean Dialog, boolean File)
  {
    logPlanAlert = LogPlan;
    dialogWindow = Dialog;
    logToFile = File;
  }

  public void isRemote()
  {
    remote = true;
  }

  /* Submits the requested properties entered using the GUI */
  boolean submitRequest()
  {

  PredictionCriteria pc1 = null;

  try
  {
    pc1 = new PredictionCriteria(assetName, clusterName);
    pc1.setTrackShortFalls(assetShortfalls);
    pc1.setTrackSurpluses(assetSurpluses);
    pc1.setSensitivityLevel(Sensitivity);
    pc1.setNotificationProtocols(logPlanAlert, dialogWindow, logToFile);
    pc1.setTimeUnit(timeUnit);
   }
   catch (Exception e)
   { System.out.println("Error in creation of prediction criteria object") ;}
   return mediator.publishSelection(pc1, remote);
  }

}