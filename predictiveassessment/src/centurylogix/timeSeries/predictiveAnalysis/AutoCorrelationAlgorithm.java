/**
 *  @file         AutoCorrelationAlgorithm.java
 *  @copyright    Copyright (c) 2001
 *  @author       Darrin Taylor & Abraham DeLaO
 *  @company      21st Century Technologies, Inc
 *  @description
 *  @history      Created August 2000.
 *  @todo
 **/


package com.centurylogix.timeSeries.predictiveAnalysis;

import com.centurylogix.timeSeries.*;
import com.centurylogix.ultralog.DebugFrame;

import java.util.ArrayList;
import java.util.Iterator;

public class AutoCorrelationAlgorithm implements AlgorithmInterface
{
  private ArrayList myData;

  private String algName = "Auto Correlation Algorithm";
  private String inputName;
  private AlgorithmState state;
  private long stepSize;

  private int minPeriodLength = 15;              // the minimum length of a period
  private int maxPeriodLength = 40;             // maximum period length
  private int minStreamLength = 2 * maxPeriodLength;  //begin analysis will wait for this much data
  private double epsilon = .25;

  private DebugFrame df = new DebugFrame (false);

  public AutoCorrelationAlgorithm (String input)
  {
    this.inputName = input;

    df.setTitle(algName + " Debug Frame");
    df.setBounds(100, 100, 500, 300);
    df.show();
    df.printToFile ("AutoCorrelation.out");
  }

  /**
   * Returns the name of this algorithm.
   * @return  A String representing this algorithm's name.
   */
  public String getAlgorithmName ()
  {
    return algName; //(new String (algName + " --> Input Stream : " + this.inputName));
  }

 /**
   * Method serves as a one-time setup for this class. It uses the input info. to
   *  set member variables that direct the behavior of the algorithm.
   * @param   pc          Data structure containing sensitiviy level information and other
   *                      user defined parameters.
   * @param   startTime   Scenario time at which analysis was initiated.
   * @requires pc != null
   **/
  public void initialize (long step, long startTime)
  {
    this.stepSize = step;
    this.state = new AlgorithmState (step, startTime, getAlgorithmName());
  }

  /**
   *  Returns a weighted error value reflecting the accuracy of past predictions.
   *  If we have made predictions and are able to determine their accuracy, we compute their
   *  weighted error value. This error value places a higher weight on more recent predictions and
   *  gradually phases out the importance of past discrepancies between our predictions and the
   *  actual data.
   * @param     info        Data structure containing consumption information.
   * @param     currTime    The current scenario time.
   * @return    1. if we have not made any predictions yet -> return null. <br>
   *            2. otherwise we return a weighted error value
   * @requires  this.state != null
   */
  public Double getError (long time)
  {
    if (this.state == null)
      return null;

    return (this.state.getError());
  } // end public double getError (PredictedAssessorInfo, long)


  /**
   * If we have any predictions for dates beyond <tt> currentTime </tt>, we return a data
   *    structure populated with them.
   * @param   currentTime   The current scenario time.
   * @return  1. if we have no predictions beyond <currentTime> -> return null. <br>
   *          2. Otherwise we return a populated instance of PredictedData.
   * @requires  this.state != null
   * @modifies  this.state
   */
  public TimeSeries getPredictions (long currTime)
  {
   TimeSeries predictions = state.getPredictions ();

    return predictions;
  }// end PredictionData getPrediction (long, long)


  /**
   *  Main prediction method called by the Algorithm Manager to signal that new
   *  data was recieved and for this algorithm to use that new data to try and form a prediction.
   *  This method analyzes the time series in search for periodically repeating patterns
   *  that can be extrapolated. If they exist, the algorithm's predictions are updated with
   *  forecasts of additional occurences the identified periodic elements.
   *  @param   info  The data structure contining time-series data needed for making predictions
   *  @requires  info != null
   */
  public void updatePredictions (long currTime, TimeSeries inputSeries)
  {

// TODO: decide if this is necessary, it's in PPALg >>
    this.state.clearPredictionsFrom (currTime + stepSize);
//this.state.clearPredictions();

    TimeSeries timeSeries = (TimeSeries) inputSeries.clone();

    // if timeSeries is shorter than the minimum stream do nothing.
    if (timeSeries.size() < minStreamLength)
      return;

    // trim the first <minPeriod> number of elements from the timeSeries.
      // this removes non-periodicity that is seen in simulators first few elements.

      timeSeries.trimFromStart(minPeriodLength);

    df.addText ("First trim of time series : " + timeSeries.shortToString());

    // find the best shift of the time series against iteself that leads to the most matching
    ArrayList shiftGraph = findBestShiftGraph (timeSeries);

    // trim the shifted graph to make sure we aren't starting in the middle of an event
    int amountTrimmed = trimShiftGraph (shiftGraph);

    df.addText ("Trim amount : " + amountTrimmed);
    df.addText ("final, trimmed shift graph : " + shiftGraph.toString());

    // the offset is the total amount of the time series we have discarded, it is the offset
      // to the leading edge of a periodic repeating occurence
    int offset = amountTrimmed + minPeriodLength;

    // create a new periodic event object
    PeriodicEvent pe = getPeriod (shiftGraph, offset);

    if (pe == null || pe.getEvent().isEmpty())
    {
      df.addText (" Periodic event is null ");
      return;
    }

    df.addText (" Periodic Event is : " + pe.toString());

    // get the current time from which we will extrapolate
    long time = currTime;

    int period = pe.getPeriod();
    ArrayList event = pe.getEvent();

    // if we divide the timeSeries into portions the size of the time period, how much is left over?
      // for this we do not consider the trimmed amount.
    int timeSeriesSize = timeSeries.size() - amountTrimmed;

    if (timeSeriesSize <= period)
      return;
    int remainder = getRemainder (timeSeriesSize, period);
    df.addText ("remainder is : " + remainder);

    // cycle through the remaining portion of the last period adding any predictions based on
      // our periodic event that we have identified.
      // must subtract 1 since index is zero-base, & reaminder is one-based
    for (int i = remainder -1 ; i < event.size(); i++)
    {
      double prediction;

      time = time + timeSeries.getTimeIncrement();

      if (i >= 0 && event.get (i) != null)
      {
        prediction = ((Double) event.get(i)).doubleValue();

        state.addPrediction ( time, prediction);
        df.addText ("Adding prediction : " + prediction + " for time : " + time);
      }
    } // end for (i)

    int extrapolationCount = 2;

    // We make <j> additional extapolation into the future beyond the last period fragment
    while (extrapolationCount > 0)
    {
      for (int j = 0; j < event.size(); j++)
      {
        double prediction;

        time = time + timeSeries.getTimeIncrement();

        if (event.get (j) != null)
        {
          prediction = ((Double) event.get(j)).doubleValue();

          state.addPrediction ( time, prediction);
          df.addText ("Adding prediction : " + prediction + " for time : " + time);
        }
      } // end for (j)

      extrapolationCount--;
    } // end while (extapoliationCount)


    state.updateError (timeSeries, currTime);
  } // end public void updatePredictions (PredictiveAssessorInfo)


  /**
   * This method takes a timeSeries and determines the optimal auto-correlation with itself. An
   * auto-correlation is the iterative process of comparing a time series against various shifted
   * versions of itself.  This will eventually reveal any repeating, periodic elements in the
   * time series.  We define best auto-correlation attempt to be the one with the most number
   * of matches made in comparison of a time series with its shifted counterpart.  This method
   * returns an time-series-like listing of the results of the best correlation attempt. This
   * 'shift graph' contains a null for all locations where the two time series did not match
   * and contains the matching value when it is found in both time series locations.
   * @param   timeSeries    The time series in which we are searching for peridicity.
   * @return  An ArrayList that represents the best correlation matching run of the time series.
   */
  private ArrayList findBestShiftGraph (TimeSeries timeSeries)
  {
    ArrayList candidateShiftGraph = new ArrayList (timeSeries.size() / 2);
    ArrayList bestShiftGraph = new ArrayList ();

    int matchCount = 0;
    int bestMatchCount = 0;
    int shiftOffset = 0;
    int bestShiftOffset = 0;

    // we can stop correlation runs when we get to point where remaining portion is too small
    int lastShiftOffset = timeSeries.size() - this.minPeriodLength;

    // successively shift to a starting point further to the right in the time series.
    for (shiftOffset = minPeriodLength; shiftOffset < lastShiftOffset; shiftOffset++)
    {
      matchCount = 0;
      candidateShiftGraph.clear();

      // compare the shifted version against as much of a non-shifted version as possible
        //the number of elements to compare decreases by one each time as shifting occurs
      int lastComparisonIndex = timeSeries.size() - shiftOffset;
      for (int j = 0 ; j < lastComparisonIndex; j++)
      {
        Double staticElement = null;
        Double shiftElement = null;

        // get the shifted element and the static one and comparet them for equality
        try
        {
          TimeSeriesValue staticVal = timeSeries.getValueAt (j);
          TimeSeriesValue shiftVal = timeSeries.getValueAt (j + shiftOffset);

          staticElement = new Double (((Number)staticVal.getValue()).doubleValue());
          shiftElement = new Double (((Number)shiftVal.getValue()).doubleValue());
        }
        catch (Exception e)
        {
          candidateShiftGraph.add (null);
        }

        // if elements are equal add that value to the shift graph, otherwise add null
        if (elementsAreEqual (staticElement, shiftElement))
        {
          candidateShiftGraph.add (staticElement);
          matchCount++;
        }
        else
          candidateShiftGraph.add (null);

      } // end for (j)

      // determine if this shift attempt resulted in more matches that anything previously tried
        // if so make this our 'best' attempt.
      if (matchCount > bestMatchCount)
      {
        bestMatchCount = matchCount;
        bestShiftOffset = shiftOffset;

        bestShiftGraph.clear();
        bestShiftGraph.addAll (candidateShiftGraph);
        candidateShiftGraph.clear();

        df.addText ("Found new best shift graph : " + bestShiftGraph.toString());
      } // end if
    } // end for (shiftOffset)

    return bestShiftGraph;
  } // end   private ArrayList findBestShiftGraph (ArrayList timeSeries)


  /**
   *
   * @modifies shiftGraph
   */
  private int trimShiftGraph (ArrayList shiftGraph)
  {
    int lastNullIndex = 0;
    boolean foundEndingNonNull =false; // this will be index of first non-null to trim to

    for (int i = 0; i < maxPeriodLength; i++)
    {
      if (i >= shiftGraph.size())
        lastNullIndex = i;

      // mark the index of the last null we've encounterd as long as we haven't reached trim
        // end point.
      else if ( shiftGraph.get (i) == null && foundEndingNonNull == false)
        lastNullIndex = i;

      // if we've found a null already, then the next non null ends our search for trim point
      else if (shiftGraph.get (i) != null && lastNullIndex > 0)
        foundEndingNonNull = true;
    }

    // if no nulls, cannot trim return.
    if (lastNullIndex == 0)
      return 0;
    else if (lastNullIndex >= shiftGraph.size() - 1)
      shiftGraph.clear();
    else
    {
      for (int j = 0; j <= lastNullIndex; j++)
        shiftGraph.remove (0);
    }

    return lastNullIndex;
  }

  /**
   * at this point shift graph should start with non-null
   */
  private PeriodicEvent getPeriod (ArrayList shiftGraph, int eventOffset)
  {
    int matchCount = 0;
    int bestMatchCount = 0;
    int bestPeriod = 0;

    int lastOffset = shiftGraph.size() - this.minPeriodLength;
    int periodOffset = 0;

    for (periodOffset = minPeriodLength; periodOffset < lastOffset; periodOffset++)
    {
      matchCount = 0;

      int lastComparisonIndex = shiftGraph.size() - periodOffset;
      for (int j = 0 ; j < lastComparisonIndex; j++)
      {
        Double staticElement = null;
        Double shiftElement = null;

        try
        {
          staticElement = (Double) shiftGraph.get (j);
          shiftElement = (Double) shiftGraph.get (j + periodOffset);
        }
        catch (Exception e)
        { ; }

        if (elementsAreEqual (staticElement, shiftElement))
          matchCount++;
      } // end for (j)

      if (matchCount > bestMatchCount)
      {
        bestMatchCount = matchCount;
        bestPeriod = periodOffset;
      } // end if

    } // end for (offset)

    ArrayList event = new ArrayList (bestPeriod);

    for (int i = 0; i < bestPeriod; i++)
    {
      Double elt = (Double) shiftGraph.get (i);
      event.add (i, elt);
    }

    PeriodicEvent pe = new PeriodicEvent ();
    pe.setOffset (eventOffset);
    pe.setPeriod (bestPeriod);
    pe.setEvent (event);

    return pe;
  }


  // what is the remainder of <numerator> / <denominator>
  private int getRemainder (int numerator, int denominator)
  {
    if (denominator >= numerator)
      return 0;

    int diff = numerator;
    while (diff >= denominator)
    {
      diff = diff - denominator;
    }

    return diff;
  }


  private boolean elementsAreEqual (Double value1, Double value2)
  {
    if (value1 == null || value2 == null)
      return false;

    double v1 = value1.doubleValue();
    double v2 = value2.doubleValue();

    double diff = Math.abs(v1 - v2);

    if (diff <= epsilon)
      return true;

    return false;
  } // end private boolean elementsAreEqual (Double, Double)

  public static void main(String[] args)
  {
  }
}
