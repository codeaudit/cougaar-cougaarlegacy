/**
 *  @file         AlgorithmState.java
 *  @copyright    Copyright (c) 2001
 *  @author       Abraham DeLaO
 *  @company      21st Century Technologies, Inc
 *  @description  This class provide is generic data structure for maintinaing a record
 *                 of an algorithm's past predictions. It's past predictions are used to calculate
 *                 it prediction accuracy.  It's predictions for times still in the future are
 *                 still in an unsteady state and may possibly change as an algorithm updates its
 *                 forward looking forecast based on changes in the time series data.  This class
 *                 provides mechanisms for an algorithm to store it predictions and have it error
 *                 value updated by comparing its past predictions against actual time series values.
 *  @history      Created August 2000.
 *  @todo
 **/

package com.centurylogix.timeSeries.predictiveAnalysis;

import com.centurylogix.timeSeries.*;
import com.centurylogix.ultralog.DebugFrame;

import java.util.Vector;
import java.util.LinkedList;
import java.util.ArrayList;

public class AlgorithmState
{
  // member variables
  private long startTime;           // the beginning time for the algorithm's time series of interest
  private long stepSize;            // the time unit increment for the time seris
  private Double averageError;      // the average error per prediction
  private long lastErrorCalcTime;   // the last time we updated our error calculations
                                    //  we won't update error values for times past this.

  private String algName = null;
  private String predictionTSName = null;

  // data structures
  private LinkedList errorList = new LinkedList ();
  private TimeSeries predictions;

  // MAX_ERROR_WINDOW dictates the number of records we will use in generating error calculations.
  private static int MAX_ERROR_WINDOW = 20;

  private DebugFrame df = new DebugFrame (false);

  public AlgorithmState (long timeStep, long start, String algorithmName)
  {
    this.startTime = start;
    this.stepSize = timeStep;
    this.lastErrorCalcTime = start;
    this.algName = algorithmName;
    this.predictionTSName = new String (this.algName + "->Predictions");
    df.setTitle("Algorithm State Debug Frame");
    df.setBounds(50, 50, 500, 300);
    df.show();
    df.printToFile ("AlgorithmState.out");
  }

  /**
   * Method is used to report and store new predictions.  Predictions that already exist for
   *  <tt> time </tt> are overwritten with <tt> prediction </tt>.
   *  @param      prediction    The predicted value for the time series at <tt> time </tt>
   *  @param      time          The time in milliseconds for the new prediction.
   *  @requires   <tt> time </tt> must fall after <tt> this.startTime </tt>, otherwise
   *              the new prediction will be discarded.
   *  @modifies   The data structure storing the predctions : <tt> this.predictions </tt>
   */
  protected void addPrediction (long time, double prediction)
  {
    // if requested time falls before the time series start time, return
    if (time < this.startTime)
      return;

//    df.addText ("Adding prediction : " + prediction + " for time : " + time + " for "+ predictionTSName);
    // find the appropriate index in which to store the prediction

    Double predictVal = new Double (prediction);

    TimeSeriesValue tsv = new TimeSeriesValue (predictionTSName, predictVal, time, stepSize);

    if (predictions == null)
      predictions = new TimeSeries (tsv);
    else
      predictions.addElement (tsv);

//    df.addText ("All predictions are : " + predictions.toString());
  } // end void addPrediction (long, double)


  /**
   *  Retrieves any predictions associated with <tt> time </tt>.
   *  @param     time    The time in milliseconds for which we want the associated time
   *                     series predictions.
   *  @returns   1. if a prediction exists for the requested time, it is returned
   *             2. Otherwise, if no prediction exists, null is returned.
   */
  protected TimeSeries getPredictions ()
  {
    return this.predictions;  // TODO: should this be cloned ????

  } // end Double getPrediction (long)

  /*
  protected TimeSeries getPredictionsFrom (long time)
  {

  }*/
  /**
   * This method will clear all past predictions and the associated errors. It would be used when
   * analysis is being initiated on a new time series.
   * @modifies    The prediction and error data structures : <tt> this.predictions </tt>
   *              & <tt> this.errorList </tt>
   */
  protected void clearPredictions ()
  {
    if (predictions != null)
      predictions.clear();

    if (errorList != null)
      errorList.clear();
  }

  /**
   * Clears all predictions from <tt> time </tt> forward. When new predictions are generated by
   *  an algorithm, it will usually want to overwrite an pre-existing predictions.  Entering each
   *  new prediction may not overwrite all old predictions, leaving gaps that are usually not
   *  desired.  This method wipes out all predictions forward of <time>, allowing a new set of
   *  predictions to be inserted without the possiblity of mixing new and old predictions.
   *  @param    time    The time in milliseconds from which we want to begin erasing predictions.
   *  @modifies   the predictions data structure : <tt> this.predictions </tt>
   */
  protected void clearPredictionsFrom (long time)
  {
   // int index = findIndex (time);

    if (predictions != null)
      df.addText ("\n\n Old Predictions size : " + predictions.size() + " predictions : " + predictions.toString());

    if (predictions != null && !predictions.isEmpty() && predictions.isValidTime(time))
      predictions.trimToEnd (time);

    if (predictions != null)
      df.addText ("New Predictions size + " + predictions.size()+ " predictions : " + predictions.toString());
  }

  /**
   * Method returns the number or prediction units that we have values for past <tt> time </tt>.
   *  This value is the number of time units from <tt> time </tt> until the farthest reaching
   *  prediction. Since there may not be a prediction for every time increment, this does not
   *  correspond to the number of actual predictions that exist in this time range.
   *  @param    time    The time in milliseconds that defines the beginning of the time range. This
   *                    will usually be the current time.
   *  @return   The number time units from <tt> time </tt> to our most forward-looking prediction.
   *            If <tt> time </tt> is less than <tt> this.startTime </tt> , zero is returned.
   */
/*  protected int getNumberOfPredictionDays (long time)
  {
    // get the offset from the beginning
    int index = findIndex (time);

    // if this is equal to or less than the start time, or we have no predictions return 0.
    if (index == 0 || predictions.isEmpty())
      return 0;

    // get the differnce between the index of the input time and the total number of prediction
      // records, this will be the number of prediction records past the input time.
    int diff = predictions.size() - index;

    if (diff < 0)
      diff = 0;

    return diff;
  }// end protocted int getNumberOfPredicionDays (long)
*/

  /**
   *  Method returns an algorithm's error rating. This error rating is linearly weighted to
   *  give more influence to the most recent prediction errors and less importance to errors
   *  from the past.  The total number of prediction errors considered in this calculation
   *  is limited by <tt> MAX_ERROR_WINDOW </tt>.  This error rating is updated each time new time
   *  series data is recieved which can be used to judge the accuracy of past predictions.
   *  @return   The error rating for this algorithm. If no error rating has been calculated, null
   *            is returned.
   */
  protected Double getError ()
  {
    double totalError = 0;
    double error;

    // if no calculated error, return null.
    if (errorList.isEmpty())
    {
      df.addText ("error list is empty : no error information.");
      return null;
    }

    // most recent errors listed are last, give those the most wieght.
    for (int i = 0; i < errorList.size(); i++)
    {
      error = ( (Double)errorList.get(i)).doubleValue();
      totalError += (error * ((i + 1)/ MAX_ERROR_WINDOW));
    }

    df.addText ("calculated error is : " + (totalError / errorList.size()));

    averageError = new Double (totalError / errorList.size ());
    return averageError;
  }// end protected Double getError()


  /**
   * This method determines the accuracy of past predictions for the algorithm.  It does so by
   * looking for actual time series values contained in <tt> info </tt> that can be compared against
   * past predictions. The record of errors is limited in size to the size specified by
   * <tt> MAX_ERROR_WINDOW </tt>.  Once the list of errors has been calculated, the error is
   * retrieved using the <tt> getError() </tt> method.
   * @requieres   info != null
   */
  protected void updateError (TimeSeries ts, long currTime)
  {
    long lastPredictionTime = currTime;

  //  df.addText ("Predictions : " + predictions.toString());

    // if curr time is greater than our last error calc time, look for new actual time series values
    if (currTime > this.lastErrorCalcTime)
    {
      df.addText ("CurrTime : " + currTime + "\n lastErrorCalcTime : " + lastErrorCalcTime);

      int timeUnitsToCheck = (int) Math.floor ((currTime - this.lastErrorCalcTime) / this.stepSize);
      df.addText ("Time units to check : " + timeUnitsToCheck);

      // check each actual, observed value from last error calc time fwd.
      for (int i = 1; i <= timeUnitsToCheck; i++)
      {
        // this is the time of the next observed value we expect to find
        long nextTime = this.lastErrorCalcTime + (i * this.stepSize);

         df.addText ("Looking for prediction at " + nextTime);

        //get our predicted value for this date
//        Double prediction = getPrediction (nextTime);

        if (predictions!= null && predictions.isValidTime (nextTime))
        {
          TimeSeriesValue predictedTSV = predictions.getValueAt (nextTime);

          if (predictedTSV != null)
          {
           Number predictedVal = (Number) predictedTSV.getValue();

            // if prediction was made for this data, compare it to actual consumption
            if (predictedVal != null)
            {
              double predVal_double = predictedVal.doubleValue();

              df.addText ("Prediction for this time : " + predVal_double);

              TimeSeriesValue observedTSV = null;
              Number observedVal = null;

              if (ts.isValidTime (nextTime))
              {
               // get the observed time series value for this date
                observedTSV = ts.getValueAt (nextTime);
                observedVal = (Number) observedTSV.getValue();
              }

              if (observedVal != null);
              {
                double obsVal_double = observedVal.doubleValue();
                // compare this to predicted value, if one exists
                double diff = Math.abs (obsVal_double - predVal_double);

                if (errorList.size() >= MAX_ERROR_WINDOW)
                  errorList.removeFirst();

                df.addText ("Adding " + diff + " to error List ");
                errorList.addLast (new Double (diff));
              }

              lastPredictionTime = nextTime;
            } // end if (predictedVal != null)
          }/// end if (predictedTSV != null)
        }// end if (nextTime)
      }// end for (i)

      // reset last error calc time
      lastErrorCalcTime = lastPredictionTime;
    } // end if (currTime > lastErrorCalcTime)
  } // end protected void updateError()

  /**
   * Thie method returns an integer offset indicating the number of whole time units from the
   *  first time series value to <tt> time </tt>.
   *  @param  time  The time in milliseconds that we want to get the offset for.
   *  @return   The integer offset of the number of whole time units between our first time series
   *            instance and the input value. If <tt> time </tt> falls before our first time series
   *            value, zero is returned.
   */
  private int findIndex (long time)
  {
    if (time < startTime)
      return 0;

    long indexedTime = time - this.startTime;

    int index =  (int) Math.floor (indexedTime / this.stepSize);

    return index;
  }// end private int findindex

} // end class AlgorithmState