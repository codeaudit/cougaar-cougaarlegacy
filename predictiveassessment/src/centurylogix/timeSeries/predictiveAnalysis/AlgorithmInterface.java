/**
 *  @file         AlgorithmInterface.java
 *  @copyright    Copyright (c) 2001
 *  @author       Abraham DeLaO
 *  @company      21st Century Technologies, Inc
 *  @description  This Java Interface defines the minimum requirement of an Algorithm.  The
 *                AlgorithmManager requires that each algorithm implement the methods specified
 *                in this interface.
 *  @history      Created August 2000.
 *  @todo
 **/

package com.centurylogix.timeSeries.predictiveAnalysis;

import com.centurylogix.timeSeries.*;
import java.util.ArrayList;

public interface AlgorithmInterface
{
  public String getAlgorithmName ();

  public void initialize (long timeIncrement, long startTime);
/*
  public abstract void updatePredictions (PredictiveAssessorInfo info);

  // this might be parameterless, but for Chris' alg, need this stuff since he's not using state
  public abstract Double getError (PredictiveAssessorInfo info, long currTime);

  public abstract PredictedData getPrediction (long currentTime);
*/
} // end class
