/**
 *  @file         TimeSeriesReportPredicate.java
 *  @copyright    Copyright (c) 2001
 *  @author       Abraham DeLaO
 *  @company      21st Century Technologies, Inc
 *  @history      Created June 20, 2001.
 *  @description  This Predicate allows us to identify TimeSeriesReport objects in the LogPlan.
 *                We can use this to capture all TimeSeriesReports.
 *  @todo
 *
 **/

package com.centurylogix.ultralog;

import org.cougaar.util.UnaryPredicate;
import com.centurylogix.timeSeries.*;

public class TimeSeriesReportPredicate implements UnaryPredicate
{
  private String timeSeriesName = null;

  /**
   * Default constructor used to generate an instance that will identify TimeSeriesReports.
   */
  public TimeSeriesReportPredicate ()
  {
  }


  /**
   * This constructor is used to specify that this Predicate instance should identify
   * TimeSeriesReport that belong the a specific TimeSeries.
   * @param   name    The name of the TimeSeries that we want the TimeSeriesValues for.
   */
  public TimeSeriesReportPredicate(String name)
  {
    this.timeSeriesName = name;
  }


  /**
   * Satisfies the UrnaryPredicate Interface. Called to identify the input parameter
   * as an instance of a TimeSeriesReport of interest to us. If this Predicate instance is
   * only selecting specific TimeSeries, then the input object must an instance of that timeSeries.
   * @param   o   Object to be checked against our interests.
   * @return  This returns true if the input parmeter meets our criteria. False is returned
   *          otherwise.
   */
  public boolean execute (Object o)
  {
    boolean ret = false;

    // if object is a TimeSeriesReport investigate it futther
    if (o instanceof TimeSeriesReport)
    {
      // if we have narrowed our criteria to a specific TimeSeries, check the input close
      if (timeSeriesName != null)
      {
        TimeSeriesReport tsr = (TimeSeriesReport)o;
        String tsvName = tsr.getTimeSeriesName();

        // if the input is from our TimeSeries, return false.
        if (this.timeSeriesName.equals (tsvName))
          ret = true;
        else
          ret = false;
      }
      else //o.w. we have not specified a particular time series to monitor, return true
        ret = true;
    }

    return ret;
  } // end public boolean execute

  public static void main(String[] args)
  {
    TimeSeriesReportPredicate tsrp = new TimeSeriesReportPredicate();
  }

} // end class