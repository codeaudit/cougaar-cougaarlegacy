/**
 *  @file         TimeSeriesDisplayPlugIn.java
 *  @copyright    Copyright (c) 2001
 *  @author       Abraham DeLaO
 *  @company      21st Century Technologies, Inc
 *  @description
 *  @history      Created June 21, 2001.
 *  @todo
 **/

package com.centurylogix.finalPredictiveAssessor;

import org.cougaar.util.UnaryPredicate;
import org.cougaar.core.plugin.SimplePlugIn;
import org.cougaar.core.cluster.IncrementalSubscription;

import java.util.Collection;
import java.util.Iterator;

import com.centurylogix.timeSeries.*;
import com.centurylogix.timeSeries.correlationAnalysis.*;
import com.centurylogix.timeSeries.predictiveAnalysis.*;
import com.centurylogix.timeSeries.jChartDisplay.*;

class ForecastDisplayRequestPred implements UnaryPredicate
  {
    public boolean execute (Object o)
    {
      boolean ret = false;

      if (o instanceof ForecastDisplayRequest)
        ret = true;

      return ret;
    } // end public boolean execute
  }// end class ForecastDisplayRequestPred

class CorrelationDisplayRequestPred implements UnaryPredicate
  {
    public boolean execute (Object o)
    {
      boolean ret = false;

      if (o instanceof CorrelationDisplayRequest)
        ret = true;

      return ret;
    } // end public boolean execute
  }// end class CorrelationDisplayRequestPred


class TimeSeriesDisplayRequestPred implements UnaryPredicate
  {
    public boolean execute (Object o)
    {
      boolean ret = false;

      if (o instanceof TimeSeriesDisplayRequest)
        ret = true;

      return ret;
    } // end public boolean execute
  }// end class TimeSeriesDisplayRequestPred


class predictionCriteriaPred implements UnaryPredicate
{
    public boolean execute (Object o)
    {
      boolean ret = false;

      if (o instanceof PredictionCriteria)
        ret = true;

      return ret;
    } // end public boolean execute
}


public class TimeSeriesDisplayPlugIn extends SimplePlugIn
{
  IncrementalSubscription forecastRequestSub;
  IncrementalSubscription correlationRequestSub;
  IncrementalSubscription timeSeriesRequestSub;
  IncrementalSubscription predictionCriteriaSub;

  DebugFrame df = new DebugFrame (true);

  ChartingFrame cf;
  ChartingFrameDataModel dm;

  DemoCommandGUI commandGUI;

  public TimeSeriesDisplayPlugIn()
  {
    df.setBounds (200, 650, 500, 600);
    df.setTitle("Time Series Display PlugIn");
    df.show();

  }

  public void setupSubscriptions ()
  {
    forecastRequestSub = (IncrementalSubscription) subscribe (new ForecastDisplayRequestPred());
    correlationRequestSub = (IncrementalSubscription) subscribe (new CorrelationDisplayRequestPred());
    timeSeriesRequestSub = (IncrementalSubscription) subscribe (new TimeSeriesDisplayRequestPred());
    predictionCriteriaSub = (IncrementalSubscription) subscribe (new predictionCriteriaPred());

  }


  public void execute ()
  {
    df.addText ("execute called");

    if (predictionCriteriaSub != null && !predictionCriteriaSub.isEmpty())
    {
      if (this.commandGUI == null)
        this.commandGUI = new DemoCommandGUI (this);

    }

    if (correlationRequestSub != null && !correlationRequestSub.isEmpty())
    {
      df.addText ("Subscriptions filled for correlation analysis display ");
      Collection newDisplayRequests = correlationRequestSub.getAddedCollection();
      Iterator requestIter = newDisplayRequests.iterator();

      CorrelationDisplayRequest cdr = null;

      while (requestIter.hasNext())
      {
        cdr = (CorrelationDisplayRequest) requestIter.next();
      }

      try {
        df.addText ("attempting to start a display");
        CorrelationAnalysisDisplay displ = new CorrelationAnalysisDisplay (cdr);
      }
      catch (Exception e)
      {
        df.addText ("Unable to start window : " + e.getMessage());
        e.printStackTrace();
      }

      newDisplayRequests.clear();
      publishRemove (cdr);
    } // end if

    if (forecastRequestSub != null && !forecastRequestSub.isEmpty())
    {
      Collection displayRequests = forecastRequestSub.getAddedCollection();
      Iterator requestIter = displayRequests.iterator();

      while (requestIter.hasNext())
      {
        ForecastDisplayRequest fdr = (ForecastDisplayRequest)requestIter.next();

        TimeSeries obsvTS = fdr.getObservedTS();
        //TimeSeries corrTS = fdr.getCorrelatedTS();
        TimeSeries forecastTS = fdr.getForecastTS();

        //df.addText(forecastTS.toString());

        if (cf == null)
        {
          cf = new ChartingFrame();
          dm = new ChartingFrameDataModel (cf);
        }

        TimeSeries[] tsArray;

        if (forecastTS == null)
          tsArray = new TimeSeries[] {obsvTS};
        else
          tsArray = new TimeSeries [] {obsvTS, forecastTS};

        String groupName = fdr.getName();
        dm.addTimeSeriesGroup(tsArray, groupName);
        df.addText ("Adding time series grouop : " + groupName);
      }// end while (requestIter.hasNext())
    }

    if (timeSeriesRequestSub != null && !timeSeriesRequestSub.isEmpty())
    {
      Collection added = timeSeriesRequestSub.getAddedCollection();
      Iterator addedIter = added.iterator();

      TimeSeriesDisplayRequest tsdr = null;

      while (addedIter.hasNext())
      {
        tsdr = (TimeSeriesDisplayRequest) addedIter.next();

        TimeSeries ts = tsdr.getTSToDisplay();
        TimeSeriesUtilities.replaceNulls(ts);
        df.addText ("ts to add to display : " + ts.toString());
        TimeSeries[] tsArray = new TimeSeries [] {ts};

        if (cf == null)
        {
          cf = new ChartingFrame();
          dm = new ChartingFrameDataModel (cf);
        }

      dm.addTimeSeriesGroup (tsArray, ts.getName());
      }
    }// if
  } // end public void execute ()

  protected boolean publishMessage(String message)
  {
      //Temp
      //System.out.println("Message about to be published = " + message);
      DemoCommand dc = new DemoCommand(message);
      openTransaction();
      publishAdd(dc);
      closeTransaction();
      return true;
  }

} // end class
