/**
 *  @file         ForecastDisplayRequest.java
 *  @copyright    Copyright (c) 2001
 *  @author       Abraham DeLaO
 *  @company      21st Century Technologies, Inc
 *  @history      Created June 27, 2001.
 *  @description
 *  @todo
 *
 **/

package com.centurylogix.timeSeries.predictiveAnalysis;

import com.centurylogix.timeSeries.*;

public class ForecastDisplayRequest
{

  private String groupName;
  private TimeSeries forecastTS;
  private TimeSeries observedTS;
  private TimeSeries correlatedTS; // this should maybe be an array or something since there could be
    // several correlated time series.

  public ForecastDisplayRequest(TimeSeries forecast, TimeSeries observed, TimeSeries corr)
  {
    this.forecastTS = forecast;
    this.observedTS = observed;
    this.correlatedTS = corr;
  }

  public void setName (String name)
  {
    this.groupName = name;
  }

  public String getName ()
  {
    return this.groupName;
  }

  public TimeSeries getForecastTS ()
  {
    return this.forecastTS;
  }

  public TimeSeries getObservedTS()
  {
    return this.observedTS;
  }

  public TimeSeries getCorrelatedTS()
  {
    return this.correlatedTS;
  }

} // end class