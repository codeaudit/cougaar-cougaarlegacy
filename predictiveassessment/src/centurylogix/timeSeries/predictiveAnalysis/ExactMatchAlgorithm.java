/*  Title:        ExactMatchAlgorithm.java
 *  Version:      v 1.0
 *  Copyright:    Copyright (c) 2000
 *  Author:       Henzil Browne
 *  Company:      21st Century Technologies, Inc
 *  Description:  Exact Match Algorithm
 *  Future:
 */


package com.centurylogix.timeSeries.predictiveAnalysis;

import com.centurylogix.timeSeries.*;
import com.centurylogix.ultralog.DebugFrame;
import java.util.*;

public class ExactMatchAlgorithm implements AlgorithmInterface
{
  private String algName = "Exact Match Algorithm";
  private String inputStreamName = null;
  private AlgorithmState state = null;
  private long stepSize;
  private int predictionWindow = 5;

  private DebugFrame df = new DebugFrame (false);

  public ExactMatchAlgorithm(String streamName)
  {
    df.setTitle("ExactMatchAlgorithm Debug Frame");
    df.setBounds(150, 150, 500, 300);
    df.show();

    this.inputStreamName = ("\t-> Input Stream : " + streamName);
  }

  public void initialize (long step, long startTime)
  {
    this.stepSize = step;
    this.state = new AlgorithmState (step, startTime, algName);
  }

  public String getAlgorithmName()
  {
    return (algName);
  }

  public TimeSeries getPredictions (long currTime)
  {
   TimeSeries predictions = state.getPredictions ();

    return predictions;
  }// end TimeSeries getPredictions (currTime)


  public void updatePredictions (long currTime, TimeSeries inputSeries)
  {
     long timeToCopyFrom;
     long timeToPredictAt;

     double predictedUsage;

     for (int j = 0; j <= predictionWindow; j++)
     {
        timeToCopyFrom = currTime - (j * stepSize);
        timeToPredictAt = currTime + ((j +1) * stepSize);

        if (inputSeries.isValidTime(timeToCopyFrom))
        {
          TimeSeriesValue tsv = inputSeries.getValueAt(timeToCopyFrom);

          predictedUsage = ((Number)tsv.getValue()).doubleValue();
          state.addPrediction (timeToPredictAt, predictedUsage);
        }
     }

     this.state.updateError(inputSeries, currTime);
  }

   public Double getError (long time)
  {
    if (this.state == null)
      return null;

    return (this.state.getError());
  } // end public double getError (PredictedAssessorInfo, long)

}

