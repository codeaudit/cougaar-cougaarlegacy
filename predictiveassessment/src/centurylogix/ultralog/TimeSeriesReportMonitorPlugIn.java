/**
 *  @file         TimeSeriesReportMonitorPlugIn.java
 *  @copyright    Copyright (c) 2001
 *  @author       Abraham DeLaO
 *  @company      21st Century Technologies, Inc
 *  @description
 *  @history      Created June 21, 2001.
 *  @todo
 **/

package com.centurylogix.ultralog;

import org.cougaar.util.UnaryPredicate;
import org.cougaar.core.plugin.SimplePlugIn;
import org.cougaar.core.plugin.PluginBinder;
import org.cougaar.core.plugin.PluginBindingSite;
import org.cougaar.core.plugin.PlugInDelegate;
import org.cougaar.core.component.Component;
import org.cougaar.core.component.BindingSite;
import org.cougaar.core.cluster.IncrementalSubscription;

import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;

import com.centurylogix.timeSeries.*;
import com.centurylogix.timeSeries.correlationAnalysis.*;
import com.centurylogix.timeSeries.predictiveAnalysis.*;


/**
 * A Predicate that allows to idenitify TimeSeriesReport objects on the Blackboard.
 */
class TsReportPred implements UnaryPredicate
  {

    public boolean execute (Object o)
    {
       //  System.out.println ("________________Looking for a time series report________ ");
      boolean ret = false;

      if (o instanceof TimeSeriesReport)
      {
  //   System.out.println ("________________Found a time series report________ ");
        ret = true;
      }
      return ret;
    } // end public boolean execute
  }// end class CorrelationDisplayRequestPred


public class TimeSeriesReportMonitorPlugIn extends SimplePlugIn
{
  IncrementalSubscription tsReportSub;  // subscription for TimeSeriesReports
  HashMap tsHash = new HashMap (10);    // containst all known TimeSeries, hashed by name

  DebugFrame df = new DebugFrame (true);

  public TimeSeriesReportMonitorPlugIn()
  {
    df.setBounds (200, 350, 200, 300);
    df.setTitle("Time Series Report Monitor PlugIn");
    df.show();
    df.printToFile("ReportMonitor");
  }

  /**
   * Sets up subsciptions for TimeSeriesReport objects.
   */
  public void setupSubscriptions ()
  {
//    PlugInAdapter pia = new PlugIn

    //PlugInDelegate delegate = getDelegate();
    //PluginBindingSite pbs = delegate.getBindingSite();
    //pbs.requestStop();


    //PluginBinder binder = new PluginBinder (new Double(10), (Component)this);
    //setBindingSite ((BindingSite)binder);

    tsReportSub = (IncrementalSubscription) subscribe (new TsReportPred());
    df.addText ("subscriptions have been set up");
    df.addText ("this cluster address : " + getClusterIdentifier().toAddress() );
    df.addText ("this cluster name : " + getClusterIdentifier().toString());

  }


  /**
   *
   */
  public void execute ()
  {
//    df.addText ("execute called");

    // cycle through the reports, adding them to our TimeSeries records and requesting displays
    if (tsReportSub != null && !tsReportSub.isEmpty())
    {
      Collection reports = tsReportSub.getAddedCollection();
      Iterator reportIter = reports.iterator();

      while (reportIter.hasNext())
      {
        TimeSeriesReport tsr = (TimeSeriesReport) reportIter.next();
//        df.addText (tsr.toString());

        TimeSeries ts = tsr.getTimeSeries ();

        // replace any nulls found in the time series with the average of bordering elements.
        TimeSeriesUtilities.replaceNulls (ts);

        // if it's a known time series, append to the time series to which it belongs.
        if (isKnownTimeSeries (ts.getName()))
          addToTimeSeries (ts);
        else // o.w. create a new time series entry
          this.tsHash.put (ts.getName(), ts);

        String tsName = ts.getName();

        if (tsName.equals("LeadingTS"))
        {
          this.leadTS = ts;
          testCrossCorrelation ();
        }
        else if (tsName.equals("LaggingTS"))
        {
          this.lagTS = ts;
          testCrossCorrelation ();
        }

        else if (tsName.equals("MovingAvgData"))
          testMovingAvgAlg (ts);

        else if (tsName.equals("AutoCorrelationData"))
          testAutoCorrelationAlg (ts);

        else if (tsName.equals ("PatternIdentificationData"))
          testPatternPredictAlg (ts);
      }
    } // end if
  } // end public void execute ()

  private TimeSeries leadTS = null;
  private TimeSeries lagTS = null;
  private boolean crossCorrDone = false;
  private void testCrossCorrelation ()
  {
    if (crossCorrDone == false && leadTS != null && lagTS != null && leadTS.size() >= 50 && lagTS.size() >= 50)
    {

      CrossCorrelationAnalysis cca = new CrossCorrelationAnalysis (.05, 20);
      if (cca.correlationExists(leadTS, lagTS))
      {
        int lag = cca.getOptimalTimeLag();
        double coeff = cca.getCorrelation();
        df.addText ("Optimal Time Lag is : " + lag);
        df.addText ("Optimal Correlation Coefficient is " + coeff);

        TimeSeries correlation = cca.getCorrelationTimeSeries();

        df.addText (correlation.shortToString());

        // publish a request to display the corrolation
        CorrelationDisplayRequest cdr = new CorrelationDisplayRequest (leadTS, lagTS);
        cdr.setCorrelationSeries (correlation);
        cdr.setCorrelationResults (lag, coeff);

        publishAdd (cdr);
        crossCorrDone = true;
      }
    }
  }// end testCrossCorrelation()


  private MovingAverageAlgorithm maa = null;
  private void testMovingAvgAlg (TimeSeries ts)
  {
    if (maa == null)
    {
      String testName = ts.getName();
      maa = new MovingAverageAlgorithm (testName);
      maa.initialize (ts.getTimeIncrement(),ts.getStartTime());
    }
    else
    {
      long recentReportTime = ts.getLastStartTime();
      maa.updatePredictions (recentReportTime , ts);

      TimeSeries predictions = maa.getPredictions(0);

      if (predictions != null)
      {
  //      df.addText (predictions.shortToString());
   //     df.addText (ts.shortToString());

        ForecastDisplayRequest fdr = new ForecastDisplayRequest (predictions, ts, null);
        fdr.setName ("MovingAverage");
        publishAdd (fdr);
      }
    }
  }

  private AutoCorrelationAlgorithm aca = null;
  private void testAutoCorrelationAlg (TimeSeries ts)
  {
    if (aca == null)
    {
      String testName = ts.getName();
      aca = new AutoCorrelationAlgorithm (testName);
      aca.initialize (ts.getTimeIncrement(),ts.getStartTime());

      df.addText ("Autocorrelation started ");
    }
    else
    {
      long recentReportTime = ts.getLastStartTime();
      aca.updatePredictions (recentReportTime , ts);

      long getFrom = 0;
      TimeSeries predictions = aca.getPredictions(getFrom);

      if (predictions != null)
        df.addText ("predictions updated for chart : " + predictions.toString());
      else
        df.addText ("predictions are null");

      ForecastDisplayRequest fdr = null;

      if (predictions != null)
        fdr = new ForecastDisplayRequest (predictions, ts, null);
      else
        fdr = new ForecastDisplayRequest (null, ts, null);

      fdr.setName ("AutoCorrelation");
      publishAdd (fdr);
    }
  }

  private PatternPredictionAlgorithm ppa = null;
  private void testPatternPredictAlg (TimeSeries ts)
  {
    if (ppa == null)
    {
      String testName = ts.getName();
      ppa = new PatternPredictionAlgorithm (testName);
      ppa.initialize (ts.getTimeIncrement(),ts.getStartTime());

      df.addText ("Pattern Prediction started ");
    }
    else
    {
      long recentReportTime = ts.getLastStartTime();
      ppa.updatePredictions (recentReportTime , ts);

      long getFrom = 0;
      TimeSeries predictions = ppa.getPredictions(getFrom);

      if (predictions != null)
        df.addText (predictions.shortToString());
      else
        df.addText ("pattern predictions are null");

      df.addText (ts.toString());
      ForecastDisplayRequest fdr;

      if (predictions != null)
         fdr = new ForecastDisplayRequest (predictions, ts, null);
      else
        fdr = new ForecastDisplayRequest (null, ts, null);

      fdr.setName ("PatternPrediction");
      publishAdd (fdr);
    }
  }


  private boolean isKnownTimeSeries (String name)
  {
    boolean ret = false;

    if (tsHash.containsKey(name))
        ret = true;

    return ret;
  } // end private boolean isKownTimeSeries (String)

  // requires that the mapping is checked for validity, param does belong to known time series
  private void addToTimeSeries (TimeSeries newTSReport)
  {
    if (newTSReport == null)
      return;

    String tsName = newTSReport.getName ();

    TimeSeries ts = (TimeSeries) tsHash.get (tsName);

    //df.addText ("adding : " + tsv.toString() + " to " + ts.toString());
    try {
      ts.addElements (newTSReport);
    }
    catch (IncompatibleTimeSeriesException e)
    {
      df.addText ("Error adding new elements to time series.");
    }
  } // end private void addToTimeSeries (TimeSeries)

} // end class
