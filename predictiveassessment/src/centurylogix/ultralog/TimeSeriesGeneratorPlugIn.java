/**
 *  @file         TimeSeriesGeneratorPlugIn.java
 *  @copyright    Copyright (c) 2001
 *  @author       Abraham DeLaO
 *  @company      21st Century Technologies, Inc
 *  @description  This PlugIn is awoken at regugular intervals to produce time series values.  It
 *                has the ablity to capture the data for TimeSeriesValues from the Cougaar
 *                infrastrucure metrics API. For the July deadline, we manually decide the
 *                value for each time series element since the Mini-test-config society does not
 *                produce varied enough time series patterns. The PlugIn awaits messages from
 *                the command dialog to begin generating values for each of the five different
 *                time series it is generating.
 *  @history      Created July 4, 2001.
 *  @todo
 **/

package com.centurylogix.ultralog;

import org.cougaar.core.plugin.SimplePlugIn;
import org.cougaar.core.plugin.PlugInDelegate;
import org.cougaar.core.cluster.IncrementalSubscription;
import org.cougaar.core.cluster.MetricsSnapshot;
import org.cougaar.util.UnaryPredicate;

import java.util.*;

import com.centurylogix.timeSeries.*;


/**
 * This Inner class is a Predicate object that identifies Blackboard elements that are commands
 * from the DemoCommand GUI.
 */
class DemoCommandPred implements UnaryPredicate
{
  public boolean execute (Object o)
  {
    boolean ret = false;

    if (o instanceof DemoCommand)
      ret = true;

    return ret;
  } // end public boolean execute
}// end class DemoCommand


public class TimeSeriesGeneratorPlugIn extends SimplePlugIn
{
  private final static long TIME_STEP = 4000;   //this is the time increment for all time series
  private long lastWakeUpTime;           // this is the last time we were last woken to generate a value
  private long nextWake;                 // the next scheduled wakup-time
  private long lastReportTime;
  private IncrementalSubscription demoCommandSub;

  private int autoCorrCount = 0;      // these member vars. are used strictly in the genertion
  private int patternCount = 0;         // patterns and tendencies in the time series.
  private int crossCount = 0;
  private int movAvgCount = 0;
  private Random rand = new Random ();

  // thes signal that the time series for each time series application are being generated
  private boolean crossCorrelateStarted = false;
  private boolean movingAverageStarted = false;
  private boolean patternPredictStarted = false;
  private boolean autoCorrelateStarted = false;

  private DebugFrame df = new DebugFrame ();

  public TimeSeriesGeneratorPlugIn()
  {
    df.setBounds (200, 150, 300, 500);
    df.setTitle ("Time Series Generator PlugIn");
    df.show();
  }

  /**
   * Sets up an intial alarm for the plugin to be continually awokan at regular intevals. Also
   * sets up a subscription for commands from the DemoCommand GUI.
   */
  public void setupSubscriptions ()
  {
    demoCommandSub = (IncrementalSubscription) subscribe (new DemoCommandPred());

    // get the current scenario time
    long currentTime = currentTimeMillis();

    this.lastReportTime = currentTimeMillis();

   //need lastWakupTime to determing how many days were missed (if any)
    this.lastWakeUpTime = currentTime;

    // set next wake-up time to be one day in the future
    nextWake = currentTime + TIME_STEP;

    // check to make sure scenario time has not advanced past one day in the future
    // somehow and keep advancing wake-up time one day further in the future until a
    // valid one is discovered.
    // TODO: should probably be super-safe and throw and exception if this goes on for too long
    boolean foundValidWakupTime = false;

    while (!foundValidWakupTime)
    {
      try
      {
        wakeAt(nextWake);//RealTime (nextWake);
        foundValidWakupTime = true;
      }
      catch (Exception e)
      {
        // foundValidWakupTime still equal to false, need to advance next wakup schedule further
        nextWake = nextWake + TIME_STEP;
      }
    }// end while
  }// end public void setupSubscriptions ()


  /**
   * This method is called each time one of the PlugIn's subsriptions is filled and when each
   *  of our regular Alarm wakup-times has been reached. It is also called when our subscription
   *  for DemoCommand request are found on the LogPlan.
   */
  public void execute ()
  {
    // look through the demo commands, start genearating any of the time series streams that
      // are not already being produced. ignore all other requests.
    if (demoCommandSub != null && !demoCommandSub.isEmpty())
    {
      df.addText ("Demo command Subscriptions filled ");
      Collection commands = demoCommandSub.getAddedCollection();
      Iterator commandIter = commands.iterator();

      DemoCommand dc = null;

      while (commandIter.hasNext())
      {
        dc = (DemoCommand) commandIter.next();
        String command = dc.getCommand ();

        df.addText ("Demo command string : " + command);
        if (command.equals("crossCorrelate") && crossCorrelateStarted == false)
          crossCorrelateStarted = true;
        else if (command.equals("movingAverage") && movingAverageStarted == false)
          movingAverageStarted = true;
        else if (command.equals("patternPredict") && patternPredictStarted == false)
          patternPredictStarted = true;
        else if (command.equals("autoCorrelate") && autoCorrelateStarted == false)
          autoCorrelateStarted = true;

        publishRemove (dc);
      }

      commands.clear();
    }// end if

    // take care of creating and publishing appropriate time series values
    if (wasAwakened())
    {
      long reportTime = currentTimeMillis();

      // getting the delegate allows us access to the metrics API
      PlugInDelegate delegate = getDelegate();
      MetricsSnapshot metrics = delegate.getMetricsSnapshot();

      df.addText ("Generator PlugIn awoken at time : " + reportTime +
                  " Metric snapshot time is : " + reportTime);

      /**************** create TimeSeriesValues and publish them *****************************/

      reportTime = this.lastReportTime + TIME_STEP;
      this.lastReportTime += TIME_STEP;

      // create time series values for cross-correlation
      if (this.crossCorrelateStarted)
      {
        Integer leadingTS = null;
        if (crossCount >= 10 && crossCount < 15)
          leadingTS = new Integer (crossCount);
        else if (crossCount >= 25 && crossCount < 35)
          leadingTS = new Integer (crossCount);
        else
          leadingTS = new Integer (5);

        TimeSeriesValue leadTSV = new TimeSeriesValue ("LeadingTS", leadingTS, reportTime, TIME_STEP);
        publishAdd (leadTSV);


        Integer laggingTS = null;
        if (crossCount >= 15 && crossCount < 20)
          laggingTS = new Integer (crossCount + 5);
        else if (crossCount >= 30 && crossCount < 40)
          laggingTS = new Integer (crossCount + 5);
        else
          laggingTS = new Integer (5);

        TimeSeriesValue laggingTSV = new TimeSeriesValue ("LaggingTS", laggingTS, reportTime, TIME_STEP);
        publishAdd (laggingTSV);

        crossCount++;
      }

      // create time series value for our moving average algorithm
      if (this.movingAverageStarted)
      {
        movAvgCount++;
        int val = 10;
        int randVal = (int) (rand.nextGaussian() * 1.35);

        if (movAvgCount < 15)
          val = movAvgCount + 10;
        else if (movAvgCount >= 15 && movAvgCount <= 30)
          val = -movAvgCount + 40;
        if (movAvgCount >= 30)
          movAvgCount = 0;

        Integer movingAvg = new Integer (randVal + val);

        TimeSeriesValue movingAvgTSV = new TimeSeriesValue ("MovingAvgData", movingAvg, reportTime, TIME_STEP);
        publishAdd (movingAvgTSV);
      }

      // create values that exhibit a temporal pattern for auto-correlation
      if (this.autoCorrelateStarted)
      {
        Integer autoCorr = null;
        if (autoCorrCount >= 0 && autoCorrCount < 10)
        {
          autoCorr = new Integer (rand.nextInt (100));
        }
        else
          autoCorr = new Integer (50);

        autoCorrCount++;

        if (autoCorrCount == 15)
          autoCorrCount = 0;

        TimeSeriesValue autoCorrTSV = new TimeSeriesValue
                                    ("AutoCorrelationData", autoCorr, reportTime, TIME_STEP);
        publishAdd (autoCorrTSV);
      }


      if (this.patternPredictStarted)
      {
        patternCount++;
        Integer patternTS = null;

        if (patternCount >= 5 && patternCount < 10)
          patternTS = new Integer (patternCount + 10);
        else if (patternCount >= 10 && patternCount <= 15)
          patternTS = new Integer (patternCount + 25);
        else
          patternTS = new Integer (rand.nextInt (20));;

        if (patternCount == 25)
          patternCount = 0;

        TimeSeriesValue patternTSV = new TimeSeriesValue
                    ("PatternIdentificationData", patternTS, reportTime, TIME_STEP);
        publishAdd (patternTSV);

      }
//      genList.add (directivesIn);

  //    if (genList.size() > DELAY)
    //    genList.removeFirst();
/*
      Integer directivesIn = new Integer (metrics.directivesIn);
      TimeSeriesValue tsv3 = new TimeSeriesValue ("DirectivesIn", directivesIn, reportTime, TIME_STEP);
      publishAdd (tsv3);

      Integer directivesOut = new Integer (metrics.directivesOut);
      Integer directivesOut = new Integer (((Integer)genList.getFirst ()).intValue() + OFFSET);
      TimeSeriesValue tsv4 = new TimeSeriesValue ("DirectivesOut", directivesOut, reportTime, TIME_STEP);
      publishAdd (tsv4);

      Long idleTime = new Long (metrics.idleTime);
      TimeSeriesValue tsv6 = new TimeSeriesValue ("IdleTime", idleTime, reportTime, TIME_STEP);
      publishAdd (tsv6);

      Integer planElements = new Integer (metrics.planelements);
      TimeSeriesValue tsv9 = new TimeSeriesValue ("PlanElements", planElements, reportTime, TIME_STEP);
      publishAdd (tsv9);

      Integer tasks = new Integer (metrics.tasks);
      TimeSeriesValue tsv11 = new TimeSeriesValue ("Tasks", tasks, reportTime, TIME_STEP);
      publishAdd (tsv11);

      Integer workflows = new Integer (metrics.workflows);
      TimeSeriesValue tsv14 = new TimeSeriesValue ("WorkFlows", workflows, reportTime, TIME_STEP);
      publishAdd (tsv14);

      Integer assets = new Integer (metrics.assets);
      TimeSeriesValue tsv1 = new TimeSeriesValue ("ClusterAssets", assets, reportTime, TIME_STEP);
      publishAdd (tsv1);

      Integer notifyIn = new Integer (metrics.notificationsIn);
      TimeSeriesValue tsv7 = new TimeSeriesValue ("NotificationsIn", notifyIn, reportTime, TIME_STEP);
      publishAdd (tsv7);

      Integer notifyOut = new Integer (metrics.notificationsOut);
      TimeSeriesValue tsv8 = new TimeSeriesValue ("NotificationsOut", notifyOut, reportTime, TIME_STEP);
      publishAdd (tsv8);

      Long memory = new Long (metrics.freeMemory);
      TimeSeriesValue tsv5 = new TimeSeriesValue ("FreeMemory", memory, reportTime, TIME_STEP);
      publishAdd (tsv5);

      Integer prototypes = new Integer (metrics.cachedPrototypeCount);
      TimeSeriesValue tsv2 = new TimeSeriesValue ("CachedPrototypes", prototypes, reportTime, TIME_STEP);
      publishAdd (tsv2);

      Integer threads = new Integer (metrics.threadCount);
      TimeSeriesValue tsv12 = new TimeSeriesValue ("ThreadCount", threads, reportTime, TIME_STEP);
      publishAdd (tsv12);

      Long totalMem = new Long (metrics.totalMemory);
      TimeSeriesValue tsv13 = new TimeSeriesValue ("TotalMemory", totalMem, reportTime, TIME_STEP);
      publishAdd (tsv13);

      Integer plugins = new Integer (metrics.pluginCount);
      TimeSeriesValue tsv10 = new TimeSeriesValue ("PlugInCount", plugins, reportTime, TIME_STEP);
      publishAdd (tsv10);
      */

      /********************* set next wakup time ****************************************/

      lastWakeUpTime = nextWake;
      nextWake = nextWake + TIME_STEP;

      // check to make sure cenario time has not advanced past one day in the future
      // somehow and keep advancing wake-up time one day further in the future until a
      // still valid one is discovered
      boolean foundValidWakupTime = false;

      while (!foundValidWakupTime)
      {
        try
        {
          while (nextWake <= currentTimeMillis())
            nextWake += TIME_STEP;

          //Alarm alarm = super.wakeAt (nextWake);
          super.wakeAt(nextWake);//RealTime (nextWake);
          foundValidWakupTime = true;
        }
        // if alarm at <nextWake> has already expired, catch the thrown exception and try advancing
          // clock to some date further in the future
        catch (Exception e)
        {
          nextWake = nextWake + TIME_STEP;
          //foundValidWakupTime is still false;
        }
      }// end while
    } // end if (wasAwakened)}
  } // end public void execute()

} // end class