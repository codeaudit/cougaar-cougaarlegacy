/**
 *  @file         TimeSeriesReportPublisherPlugIn.java
 *  @copyright    Copyright (c) 2001
 *  @author       Abraham DeLaO
 *  @company      21st Century Technologies, Inc
 *  @history      Created June 26, 2001.
 *  @description  This PlugIn aggregates TimeSeriesValues that are found on the BlackBoard and
 *                collects them into TimeSeriesReports (these are just short TimeSeries segments).
 *                These reports are periodically forwarded to other plugins.
 *  @todo
 *
 **/

package com.centurylogix.finalPredictiveAssessor;

import org.cougaar.core.plugin.SimplePlugIn;
import org.cougaar.core.cluster.IncrementalSubscription;
import org.cougaar.core.cluster.ClusterIdentifier;
import org.cougaar.core.cluster.DirectiveMessage;
import org.cougaar.core.cluster.MessageManager;
import org.cougaar.core.society.MessageAddress;
import org.cougaar.domain.planning.ldm.plan.Plan;

import org.cougaar.domain.glm.ldm.asset.Organization;

import org.cougaar.domain.planning.ldm.asset.Asset;
import org.cougaar.util.UnaryPredicate;
import org.cougaar.domain.planning.ldm.plan.Allocation;
import org.cougaar.domain.planning.ldm.plan.NewTask;
import org.cougaar.domain.planning.ldm.plan.Role;

import java.util.*;

import com.centurylogix.timeSeries.*;

public class TimeSeriesReportPublisherPlugIn extends SimplePlugIn
{
  private IncrementalSubscription tsValueSub;   // our subscriptions that are filled wind up here.
  private String timeSeriesName = null;         // if this is null, we are monitoring and building
                                                  //  all time series, if not just the one named here.
  private HashMap tsHash = new HashMap (10);    // containst all known TimeSeries, hashed by name
  private HashMap lastReportHash = new HashMap (10); // contains the end times for all know time series

  private int reportSize = 1;              // this is the report size, each one has this many values

  private ClusterIdentifier sourceCluster = null;
  private ClusterIdentifier destCluster = new ClusterIdentifier ("TimeSeriesAnalysis");
  private Plan plan = null;

  private IncrementalSubscription orgSub = null;
  private Asset orgAsset = null;

  private DebugFrame df = new DebugFrame (true);


  class orgPredicate implements UnaryPredicate
  {
    public boolean execute (Object o)
    {
      boolean ret = false;

      if (o instanceof Organization)
      {
        Organization org = (Organization) o;
        org.cougaar.domain.planning.ldm.asset.ClusterPG pg = org.getClusterPG();
        ClusterIdentifier ci = pg.getClusterIdentifier();
        String name = ci.toAddress ();

        if (name.equals ("TimeSeriesAnalysis"))
          ret = true;
      }

      return ret;
    } // end public boolean execute
  }// end class Tim


  public TimeSeriesReportPublisherPlugIn()
  {
    df.setBounds (100, 150, 250, 400);
    df.setTitle("Time Series Report Publisher PlugIn");
    df.show();
  }

  /**
   * Constructor specifing the desired size of the published time series reports
   * @param  inputReportSize  The size of the time series reports.
   */
  public TimeSeriesReportPublisherPlugIn(int inputReportSize)
  {
    this.reportSize = inputReportSize;
  }

  /**
   * Constuction specifying a particular time series to be tracked and reported upon.
   * @param   tsToTrack   The name of the time series to be tracked
   */
  public TimeSeriesReportPublisherPlugIn(String tsToTrack)
  {
    this.timeSeriesName = tsToTrack;
  }

  /**
   * Sets up subscriptions for Time Series objects that are to be compiled & published as reports.
   */
  public void setupSubscriptions ()
  {
    tsValueSub = (IncrementalSubscription) subscribe (new TimeSeriesValuePredicate());
    orgSub = (IncrementalSubscription) subscribe (new orgPredicate());

    sourceCluster = getClusterIdentifier();
    plan = theLDMF.getRealityPlan();

  }

// TODO: consider removing tsvales from logPlan
  /**
   * Called when new TimeSeriesValue of interest to us are recieved.  It collects them
   * into TimeSeriesReports and publishes them when they reach our predesignated size.
   */
  public void execute ()
  {
    if (orgSub != null && !orgSub.isEmpty())
    {
      Organization org = (Organization)orgSub.first();
      orgAsset = (Asset) org;

    }

    if (tsValueSub != null && !tsValueSub.isEmpty())
    {
      Collection newTSValues = tsValueSub.getAddedCollection();
      Iterator tsValueIter = newTSValues.iterator();

      while (tsValueIter.hasNext())
      {
        TimeSeriesValue tsv = (TimeSeriesValue) tsValueIter.next();

        // if it's a known time series, append to the time series to which it belongs.
        if (isKnownTimeSeries (tsv.getTimeSeriesName()))
        {
          addToTimeSeries (tsv);

        }
        else // o.w. create a new time series starting with this value.
        {
          TimeSeries newTS = new TimeSeries (tsv);
          String newTSName = newTS.getName();

          this.tsHash.put (newTSName, newTS);

          this.lastReportHash.put (newTSName, new Integer (0));
        }

        publishRemove(tsv);
      }// end while

    }

    // publish all time series reports that have reached the publishable size
    publishTimeSeriesReports ();

  } // end public void execute()


  /**
   * Checks each the collection of TimeSeriesValues for each time series and publishes a report
   * with those values once a collection is large enough.
   */
  private void publishTimeSeriesReports ()
  {
    Collection tsCollection = tsHash.values();
    Iterator tsIter = tsCollection.iterator();

    while (tsIter.hasNext())
    {
      TimeSeries ts = (TimeSeries) tsIter.next();

      int size = ts.size ();
      String tsName = ts.getName();
      int lastPublish = ( (Integer)lastReportHash.get (tsName)).intValue();

      // if the current report size is larger than our <reportSize>, publish it
      if (size >= (lastPublish + reportSize))
      {
        df.addText ("Publishing time series report: " + ts.getName() + " of size " + size);

        TimeSeriesReport tsr = new TimeSeriesReport (ts, plan, sourceCluster, sourceCluster);//destCluster);

        publishAdd (tsr);
/*
        TimeSeriesReport[] tsrArray = new TimeSeriesReport [] {tsr};
        DirectiveMessage dm = new DirectiveMessage (tsrArray);

        dm.setTarget(new MessageAddress(sourceCluster.toAddress())); // usu. destCluster
        dm.setOriginator(new MessageAddress(sourceCluster.toAddress()));

        ArrayList dmAL = new ArrayList (1);
        dmAL.add (dm);
        Iterator dmIter = dmAL.iterator();

        MessageManager mm = getDistributor().getMessageManager();
        mm.sendMessages(dmIter);
        //mm.advanceEpoch();
*/
//        publishAdd (dm);
  //      publishAdd ((org.cougaar.domain.planning.ldm.plan.Directive)tsr);
/*
        df.addText ("Froming allocation to publish to the log plan ");
        if (orgAsset!= null && plan != null)
          df.addText ("Things look ok");
        NewTask task = theLDMF.newTask();
        task.setVerb(new org.cougaar.domain.planning.ldm.plan.Verb("CODE"));
        task.setPlan(theLDMF.getRealityPlan());
        task.setDirectObject(new Asset());

        Allocation tmAllocate = theLDMF.createAllocation(plan, task, orgAsset, null, Role.BOGUS);
        publishAdd (tmAllocate);
*/
   //     df.addText ("Time Series Report has been published : " + dm.toString());

        lastPublish = lastPublish + reportSize;
        lastReportHash.put (tsName, new Integer (lastPublish));
      }

    } // end while
  } // end private void publishTimeSeriesReports()


  /**
   * Deterimines if a particular time series is currenlty being tracked by the PlugIn.
   * @param  name   The name of the time series that we are inquring about.
   * @return        Returns true if the time series is a known one, false otherwise.
   */
  private boolean isKnownTimeSeries (String name)
  {
    boolean ret = false;

    if (tsHash.containsKey(name))
        ret = true;

    return ret;
  } // end private boolean isKownTimeSeries (String)


  /**
   * Adds a TimeSeriesValue to a TimeSeries segment that will be included in a TimeSeriesReport.
   * Compatibility should be insured before attempting to inserting the new value.
   * @param   tsv   The new TimeSeriesValue to add to our TimeSeries segment.
   */
  private void addToTimeSeries (TimeSeriesValue tsv)
  {
    String tsName = tsv.getTimeSeriesName ();

    TimeSeries ts = (TimeSeries) tsHash.get (tsName);

    //df.addText ("adding : " + tsv.toString() + " to " + ts.toString());
    try {
      ts.addElement (tsv);
    }
    catch (IncompatibleTimeSeriesException e)
    {
      df.addText ("Error adding new element to time series.");
    }
  } // end private void addToTimeSeries (TimeSeriesValue)



  public static void main(String[] args)
  {
    TimeSeriesReportPublisherPlugIn tsrp = new TimeSeriesReportPublisherPlugIn();
  }
}