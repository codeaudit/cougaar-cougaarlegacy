package com.centurylogix.ultralog;

import org.cougaar.core.plugin.SimplePlugIn;
import org.cougaar.core.cluster.IncrementalSubscription;

import java.util.*;

import com.centurylogix.timeSeries.*;
import com.centurylogix.timeSeries.correlationAnalysis.*;
import com.centurylogix.timeSeries.predictiveAnalysis.ForecastDisplayRequest;

public class TimeSeriesBuilderPlugIn extends SimplePlugIn
{
  IncrementalSubscription tsValueSub;   // our subscriptions that are filled wind up here.
  String timeSeriesName = null;         // if this is null, we are monitoring and building
                                        //  time series, if not just the one named here.
  HashMap tsHash = new HashMap (10);    // containst all known TimeSeries, hashed by name

  private DebugFrame df = new DebugFrame (true);
  public TimeSeriesBuilderPlugIn()
  {
    df.setBounds (200, 150, 300, 500);
    df.setTitle("Time Series Builder PlugIn");
    df.printToFile ("TimeSeriesSummary.out");
    df.show();
  }

  public void setupSubscriptions ()
  {
    tsValueSub = (IncrementalSubscription) subscribe (new TimeSeriesValuePredicate());
  }

// TODO: consider removing tsvales from logPlan
  public void execute ()
  {
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
        }
      }
    }

    if (checkTimeSeriesLength())
      reportStatus();

  } // end public void execute()

  private boolean isKnownTimeSeries (String name)
  {
    boolean ret = false;

    if (tsHash.containsKey(name))
        ret = true;

    return ret;
  } // end private boolean isKownTimeSeries (String)


  boolean onePrint = false;
  private boolean checkTimeSeriesLength()
  {
    boolean ret = false;
    TimeSeries ts = (TimeSeries) tsHash.get ("DirectivesIn");

    if (ts != null)
    {
      int size = ts.size();

      df.addText ("size is : " + size);
      if (size > 50 && onePrint == false) // gotta do this b/c 50 is potentially skipped.
      {
        ret =  true;
        onePrint = true;
      }
    }

    return ret;
  }

  // requires that the mapping is checked for validity, param does belong to known time series
  private void addToTimeSeries (TimeSeriesValue tsv)
  {
    String tsName = tsv.getTimeSeriesName ();

    TimeSeries ts = (TimeSeries) tsHash.get (tsName);

    //df.addText ("adding : " + tsv.toString() + " to " + ts.toString());
    try
    {
      ts.addElement (tsv);
    }
    catch (IncompatibleTimeSeriesException e)
    {
      df.addText ("Error adding new element to time series.");
    }
  } // end private void addToTimeSeries (TimeSeriesValue)


  private void reportStatus ()
  {

    // ******************** test out cross-correlaation of a couple of these time series ***/
    TimeSeries directivesIn = (TimeSeries) tsHash.get ("DirectivesIn");
    TimeSeries directivesOut = (TimeSeries) tsHash.get ("DirectivesOut");

    TimeSeriesUtilities.replaceNulls (directivesIn);
    TimeSeriesUtilities.replaceNulls (directivesOut);

    TimeSeries diffedIn = TimeSeriesUtilities.getDifferencedTimeSeries (directivesIn);
    TimeSeries diffedOut = TimeSeriesUtilities.getDifferencedTimeSeries (directivesOut);


    CrossCorrelationAnalysis cca = new CrossCorrelationAnalysis (.05, 20);
    if (cca.correlationExists(diffedIn, diffedOut))
    {
      int lag = cca.getOptimalTimeLag();
      double coeff = cca.getCorrelation();
      df.addText ("Optimal Time Lag is : " + lag);
      df.addText ("Optimal Correlation Coefficient is " + coeff);

      TimeSeries correlation = cca.getCorrelationTimeSeries();

      df.addText (correlation.toString());

      // publish a request to display the corrolation
      CorrelationDisplayRequest cdr = new CorrelationDisplayRequest (diffedIn, diffedOut);
      cdr.setCorrelationSeries (correlation);
      cdr.setCorrelationResults (lag, coeff);

//      ForecastDisplayRequest fdr = new ForecastDisplayRequest (null, diffedOut, diffedIn);
//      publishAdd (fdr);

      publishAdd (cdr);
    }
    else
      df.addText ("Could not find any meaningful correlatin b/n the time series of interest.");


  }// reportStatus

  public static void main(String[] args)
  {
    TimeSeriesBuilderPlugIn tsbp = new TimeSeriesBuilderPlugIn();
  }
}