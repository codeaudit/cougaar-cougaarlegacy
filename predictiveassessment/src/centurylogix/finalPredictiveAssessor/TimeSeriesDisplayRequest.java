/**
 *  @file         TimeSeriesDisplayRequest.java
 *  @copyright    Copyright (c) 2001
 *  @author       Abraham DeLaO
 *  @company      21st Century Technologies, Inc
 *  @history      Created July 2, 2001.
 *  @description
 *  @todo
 *
 **/

package com.centurylogix.finalPredictiveAssessor;

import com.centurylogix.timeSeries.*;

public class TimeSeriesDisplayRequest
{

  private TimeSeries tsToDisplay;

  public TimeSeriesDisplayRequest(TimeSeries ts)
  {
    this.tsToDisplay = ts;
  }

  public TimeSeries getTSToDisplay ()
  {
    return this.tsToDisplay;
  }

} // end class

