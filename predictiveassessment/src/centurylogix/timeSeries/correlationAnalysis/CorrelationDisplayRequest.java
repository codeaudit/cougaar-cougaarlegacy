/**
 *  @file         CorrolationDisplayRequest.java
 *  @copyright    Copyright (c) 2001
 *  @author       Abraham DeLaO
 *  @company      21st Century Technologies, Inc
 *  @history      Created June 20, 2001.
 *  @description
 *  @todo
 *
 **/

package com.centurylogix.timeSeries.correlationAnalysis;

import java.util.ArrayList;

import com.centurylogix.timeSeries.*;

public class CorrelationDisplayRequest
{
  TimeSeries dependentTS;
  TimeSeries independentTS;
  TimeSeries correlationTS;

  int optimalLag = 0;
  double optimalCoefficient = 0;

  public CorrelationDisplayRequest(TimeSeries independent, TimeSeries dependent)
  {
    this.independentTS = independent;
    this.dependentTS = dependent;
  }

  public void setCorrelationSeries (TimeSeries corrTs)
  {
    this.correlationTS = corrTs;
  }

  public void setCorrelationResults (int bestLag, double coefficient)
  {
    this.optimalLag = bestLag;
    this.optimalCoefficient = coefficient;
  }


  public TimeSeries getCorrelationTimeSeries ()
  {
    return this.correlationTS;
  }

  public TimeSeries getIndependentTimeSeries ()
  {
    return this.independentTS;
  }

  public TimeSeries getDependentTimeSeries ()
  {
    return this.dependentTS;
  }

  public int getOptimalTimeLag ()
  {
    return this.optimalLag;
  }

  public double getOptimalCoefficient ()
  {
    return this.optimalCoefficient;
  }

  // need more than an equals, need to know if should replace any existing versions.
  public boolean equals (CorrelationDisplayRequest cdr)
  {
    TimeSeries inputDepTS = cdr.getDependentTimeSeries();
    TimeSeries inputIndepTS = cdr.getIndependentTimeSeries();

    if (this.independentTS.equals (inputIndepTS) && this.dependentTS.equals(inputDepTS))
      ;
    return false;
  }


}// end class