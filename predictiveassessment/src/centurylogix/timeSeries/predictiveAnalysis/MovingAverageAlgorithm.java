/**
 *  @file         MovingAverageAlgorithm.java
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

import java.util.LinkedList;
import java.util.ArrayList;

public class MovingAverageAlgorithm implements AlgorithmInterface
{
  private String name = "MovingAverageAlgorithm";
  private String inputStreamName;
  private AlgorithmState state = null;

  private LinkedList window = null; //the current set of data considered in the average
  private ArrayList averages = new ArrayList (20);

  private long stepSize;
  private int windowSize = 20;
  private double windowSum = 0;
  private long lastUpdateTime = 0;
  private int defaultForecastSize = 10;
  private int minForecastSize = 5;
  private int maxForecastSize = 10;

  DebugFrame df = new DebugFrame (false);

  public MovingAverageAlgorithm (String inputName)
  {
    this.inputStreamName = inputName;
    window = new LinkedList ();
    df.setTitle("Moving Avarage Algorithm Debug Frame");
    df.setBounds(100, 100, 500, 300);
    df.show();
  }

  /**
   *  Returns the name of this algorithm.
   *  @return A String representation of the name of this algorithm.
   */
  public String getAlgorithmName ()
  {
    return name; //return (new String (name + " --> Input Stream : " + this.inputStreamName));
  }


 /**
   * Method serves as a one-time setup for this class. It uses the input info. to
   *  set member variables that direct the behavior of the algorithm.
   * @param   pc          Data structure containing sensitiviy level information and other
   *                      user defined parameters.
   * @param   startTime   Scenario time at which analysis was initiated.
   * @requires pc != null
   */
  public void initialize (long step, long startTime)
  {
    this.stepSize = step;
    this.state = new AlgorithmState (step, startTime, getAlgorithmName());
    this.lastUpdateTime = startTime; // this is to give this value some valid non-zero start time.
    this.windowSum = 0;
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
  public Double getError (long currTime)
  {
    if (this.state == null)
      return null;

    return (this.state.getError());
  } // end public double getError (long)

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
  }// end TimeSeries getPrediction (long)

 /**
   *  Main prediction method called by the Algorithm Manager to signal that new
   *  data was recieved and for this algorithm to use that new data to try and form a prediction.
   *  The method updates its internal window of data to included the x most recent number of
   *  consumption reports. It then generates an average value for this window and adds that
   *  value to its history of averages.  The trendline of these averages is then the moving
   *  averagae which is extapolated to for predictions for future time series values. The length of
   *  our prediction depends on the volatility measured in the time series data stream.
   * @param   info  The data structure contining time-series data needed for making predictions
   * @requires  info != null
   */
  public void updatePredictions (long currTime, TimeSeries ts)
  {
    // update the window to include only the most recent set of data, includes potential
          //shifting and trimming
    updateWindow (currTime, ts);

    // if we have not filled our default window yet, don't bother making any extrapolations.
    if (window.size() < this.windowSize)
      return;

    // determine appropirate prediction window size based on sensitivity and data volitility
    //double sensitivityDeviation = info.getCriteria().getSensitivityDeviation();

    double volatility = calcWindowVolatility ();

    // determine the size of the forecaast window based on the volatility
    int forecastWindowSize = Math.round ((float) (maxForecastSize * ( 1 - volatility)));

    if (forecastWindowSize < minForecastSize)
      forecastWindowSize = minForecastSize;

    df.addText ("Forecast Window size is : " + forecastWindowSize);

    // calculate the average for our current window, then add it to our history of averages.
    double avg = calcWindowAvg ();
    df.addText ("Most Recent Average is : " + avg);

    averages.add (new Double (avg));

    // trim our history of averages to be the same size as our moving average window
    if (averages.size() > window.size())
      averages.remove (0);

    // determine the slope of our moving average trendline
    double slope = 0;
    if (window.size() > 0)
      slope =(avg - ( (Double) averages.get (0)).doubleValue() )/ window.size();

    // extrapolate upon this moving average curve
    for (int j = 1; j <= forecastWindowSize; j++)
    {
      double predictedUsage = avg + slope;

      df.addText ("new prediction : " + predictedUsage);
      state.addPrediction (currTime + (j * stepSize), predictedUsage);

      avg = predictedUsage;
    }

    this.state.updateError(ts, currTime);

  } // end void updatePredictions (TimeSeries)


  /**
   * This method updates our window of data to include the most recent data currently available.
   *  It adds new data and removes older data in order to limit the window size to remain within
   *  our predefined limits. It also updates the sum of the elements in the window which is
   *  used to calculate the moving averages.
   *  @param    currTime   The current scenario time.
   *  @param    info       Data structure contining consumption data.
   *  @modifies            window
   *  @requires            window != null and no nulls exist in the input time series
   */
  private void updateWindow (long currTime, TimeSeries ts)
  {
    int unitsSinceLastUpdate =  ts.getIntervalUnits (this.lastUpdateTime, currTime);

    df.addText ("Time units since last update : " + unitsSinceLastUpdate);
    df.addText ("Old Window before update is : " + (window != null ? window.toString() : " null." ));

    if (unitsSinceLastUpdate >= ts.size())
      unitsSinceLastUpdate = ts.size() - 1;

    // get the last <unitsSinceLastUpdate> number of days and add them to the window
    for (int i = unitsSinceLastUpdate; i >= 0; i--)
    {
      // get the time for which we are going to get the new window data
      long lookupTime = currTime - (i * this.stepSize);

//      if (lookupTime <= ts.getStartTime() || lookupTime > ts.getEndTime())
 //       continue;

      TimeSeriesValue newValue = ts.getValueAt(lookupTime);
      Number val = (Number)newValue.getValue();

      // if we retrieve a valid value, augment window with it.
      if (val != null)
      {
        df.addText ("adding value " + val);
        window.addLast (val);
        windowSum = windowSum + val.doubleValue();
      }

      // for each element we add, remove oldest elements in window if this puts us past window size
      if (window.size() > windowSize)
      {
        double removeValue = ((Number) window.removeFirst()).doubleValue();
        windowSum = windowSum - removeValue;
      }

      df.addText ("window sum is : " + windowSum);
    }// end for (i)

    df.addText ("New Window after update is : " + window.toString());

    this.lastUpdateTime = currTime;
  }// end private void updateWindow (long, PredictiveAssessorInfo)


  /**
   * Calculates the average of the values that currently comprise our window of time series data.
   * @return    the average for the time-series values in our current window of interest
   * @requires  this.window != null
   */
  private double calcWindowAvg ()
  {
    double avg;

    if (window.size () > 0)
      avg = windowSum / window.size();
    else
      avg = 0;

    return avg;
  } // end private double calcWindowAvg()

  /**
   * Method calculates the amount of volatility witnessed in the window time-series based on
   * our own definition of volatility.  We are defining volatility simply as the percentage
   * of possible direction shifts that are present in the time-series when it is graphed.
   * For instance, a uniform, straight time-series would have no direction changes and would have
   * a volatility of zero. A time-series that is oscillating up and down with each time-series
   * value would have a maximum volatility of one.
   * @return    the volatility witness in the current time-series window. This value varies
   *            between zero (no variations in data stream) and one (data stream consists of
   *            consecutive oscillating values.)
   * @requires  this.window != null
   */
  private double calcWindowVolatility ()
  {
    int directionShifts = 0;
    double pointA, pointB, pointC, diffBA, diffCB;
    int elts = window.size();

    df.addText ("number of window elements in volatility calculation : " + elts);

    // cycle through our window three units at a time looking for direction shifts b/n the 2 segments
    for (int i = 0; i < (elts - 2); i++)
    {
      // get three elements
      pointA =  ( (Number) window.get(i) ). doubleValue();
      pointB =  ( (Number) window.get(i+1) ). doubleValue();
      pointC =  ( (Number) window.get(i+2) ). doubleValue();

      diffBA = pointB - pointA;
      diffCB = pointC - pointB;

      // if first two are same, any change in slope considered a direction shift
      if (diffBA == 0 && diffCB != 0)
        directionShifts++;

      // if first two elts. are increasing, the a decrease in the third is a direction shift
      else if (diffBA > 0 && diffCB < 0)
        directionShifts++;

      // if first two are decreaseing, an increase in third elt. signals a direction shift.
      else if (diffBA < 0 && diffCB > 0)
        directionShifts++;
    } // end for (i)

    df.addText ("Number of direction shifts : " + directionShifts + " in window : " + window.toString());

    double volatility = 0;

    // calculate the volatility as the ratio of possible direction shifts that were actually present
    if (elts >= 3)
     volatility = (double) directionShifts / (elts - 2) ;

    df.addText ("Volatility is : " + volatility);
    return volatility;
  }

  public static void main (String args[])
  {
    DebugFrame df = new DebugFrame (true);
    df.setTitle ("Moving Average Debug Frame");
    df.show();

    MovingAverageAlgorithm alg = new MovingAverageAlgorithm ("BlackBoxTesting");
    // test for proper initialization
    /*********** Testing of updateWindow (long currTime, PredictiveAssessorInfo info) ***/
    TimeSeriesValue tsv0_one = new TimeSeriesValue ("Test1", new Double (5), 0, 5);
    TimeSeriesValue tsv1_one = new TimeSeriesValue ("Test1", new Double (6), 5, 5);
    TimeSeriesValue tsv2_one = new TimeSeriesValue ("Test1", new Double (10), 10, 5);
    TimeSeriesValue tsv3_one = new TimeSeriesValue ("Test1", new Double (12), 15, 5);
    TimeSeriesValue tsv4_one = new TimeSeriesValue ("Test1", new Double (16), 20, 5);
    TimeSeriesValue tsv5_one = new TimeSeriesValue ("Test1", new Double (22), 25, 5);
    TimeSeriesValue tsv6_one = new TimeSeriesValue ("Test1", new Double (27), 30, 5);
    TimeSeriesValue tsv7_one = new TimeSeriesValue ("Test1", new Double (30), 35, 5);
    TimeSeriesValue tsv8_one = new TimeSeriesValue ("Test1", new Double (25), 40, 5);
    TimeSeriesValue tsv9_one = new TimeSeriesValue ("Test1", new Double (20), 45, 5);
    TimeSeriesValue tsv10_one = new TimeSeriesValue ("Test1", new Double (16), 50, 5);
    TimeSeriesValue tsv11_one = new TimeSeriesValue ("Test1", new Double (14), 55, 5);
    TimeSeriesValue tsv12_one = new TimeSeriesValue ("Test1", new Double (11), 60, 5);
    TimeSeriesValue tsv13_one = new TimeSeriesValue ("Test1", new Double (9), 65, 5);
    TimeSeriesValue tsv14_one = new TimeSeriesValue ("Test1", new Double (7), 70, 5);

    // add these values to a time series
    TimeSeries ts = new TimeSeries (tsv0_one);
    ts.addElement(tsv1_one);
    ts.addElement(tsv2_one);
    ts.addElement(tsv3_one);
    ts.addElement(tsv4_one);
    ts.addElement(tsv5_one);
    ts.addElement(tsv6_one);
    ts.addElement(tsv7_one);
    ts.addElement(tsv8_one);
    ts.addElement(tsv9_one);
    ts.addElement(tsv10_one);
    ts.addElement(tsv11_one);
    ts.addElement(tsv12_one);
    ts.addElement(tsv13_one);
    ts.addElement(tsv14_one);

    alg.window.clear();
    alg.windowSize = 3;
//    alg.initialize(pc, 0); // where 0 is the startTime and intially <this.lastUpdateTime>
    alg.lastUpdateTime = 0;
    long currTime = 15;
    long startTime = 0;
    alg.initialize(ts.getTimeIncrement(), startTime);

    alg.updateWindow (currTime, ts);

//    df.addText (alg.window.toString());
    Number firstVal = (Number) alg.window.get (0);
    Number secondVal = (Number) alg.window.get (1);
    Number thirdVal = (Number) alg.window.get (2);

    if (firstVal.doubleValue() == 6 && secondVal.doubleValue() == 10 && thirdVal.doubleValue() == 12)
      df.addText ("1. Pass : Empty window sucessfully intialized \n");
    else
      df.addText ("1. Fail : Empty window NOT successfully intialized \n");

    /////// test for correct updating
    currTime =  25;
    alg.updateWindow (currTime, ts);

    firstVal = (Number) alg.window.get (0);
    secondVal = (Number) alg.window.get (1);
    thirdVal = (Number) alg.window.get (2);

    if (firstVal.doubleValue() == 12 && secondVal.doubleValue() == 16 && thirdVal.doubleValue() == 22)
      df.addText ("2. Pass : Window sucessfully updated \n");
    else
      df.addText ("2. Fail : Window NOT successfully updated \n");

    // test for shifting and subsequent trimming of time window, since max size is 3, all pervious
      // elements should be dropped
    currTime =  40;
    alg.updateWindow (currTime, ts);

    firstVal = (Number) alg.window.get (0);
    secondVal = (Number) alg.window.get (1);
    thirdVal = (Number) alg.window.get (2);

    if (firstVal.doubleValue() == 27 && secondVal.doubleValue() == 30 && thirdVal.doubleValue() == 25)
      df.addText ("3. Pass : Window sucessfully shifted and trimmed \n");
    else
      df.addText ("3. Fail : Window NOT successfully shifted and trimmed \n");

    /*********** Testing of calcWindowAvg () ***/
    //test with an an empty window
    alg.window.clear ();

    double avg = alg.calcWindowAvg();
    alg.df.addText ("Window is : " + alg.window.toString() + " with average of : " + avg);

    if (avg == 0)
        df.addText ("4. Pass : Empyt window average sucessfully calculated \n");
    else
        df.addText ("4. Fail : Empty window average NOT successfully calculated \n");

    // test for real with a few elements
    currTime = 70;
    alg.initialize (ts.getTimeIncrement(), 0);
    alg.updateWindow (currTime, ts);

    avg = alg.calcWindowAvg();
    alg.df.addText ("Window is : " + alg.window.toString() + " with average of : " + avg);
    if (avg == 9)
      alg.df.addText ("5. Pass : Window average sucessfully calculated \n");
    else
      alg.df.addText ("5. Fail : Window average NOT successfully calculated \n");

    /***************** testing calcWindowVolatitlity **************/
  /*  // test with an empty window
    alg.window.clear();
    double volatility = alg.calcWindowVolatility ();

    alg.df.addText ("Window is : " + alg.window.toString() + " with volatility of : " + volatility);
    if (volatility == 0)
        alg.df.addText ("Pass : Empty window volatility sucessfully calculated \n");
    else
        alg.df.addText ("Fail : Empty window volatility NOT successfully calculated \n");

    // test with a few elements, with no direction shifts
    currTime = 5000;
    alg.initialize (pc, 0);
    alg.updateWindow (currTime, info);

    volatility = alg.calcWindowVolatility ();

    alg.df.addText ("Window is : " + alg.window.toString() + " with volatility of : " + volatility);
    if (volatility == 0)
        alg.df.addText ("Pass : Window volatility sucessfully calculated with no direction shifts\n");
    else
        alg.df.addText ("Fail : Window volatility NOT successfully calculated with no direction shifts\n");

    //test with a single direction shift
    alg.windowSize = 4;
    alg.initialize (pc, 0);
    alg.updateWindow (6000, info);

    volatility = alg.calcWindowVolatility ();

    alg.df.addText ("Window is : " + alg.window.toString() + " with volatility of : " + volatility);
    if (volatility == .5)
        alg.df.addText ("Pass : Window volatility sucessfully calculated with 1 direction shift\n");
    else
        alg.df.addText ("Fail : Window volatility NOT successfully calculated with 1 direction shift\n");

    //test with all direction shifts
    alg.windowSize = 5;
    alg.updateWindow (9000, info);

    volatility = alg.calcWindowVolatility ();

    alg.df.addText ("Window is : " + alg.window.toString() + " with volatility of : " + volatility);
    if (volatility == 1)
        alg.df.addText ("Pass : Window volatility sucessfully calculated with all direction shifts\n");
    else
        alg.df.addText ("Fail : Window volatility NOT successfully calculated with all direction shifts\n");
*/
  } // end main

} // end class
