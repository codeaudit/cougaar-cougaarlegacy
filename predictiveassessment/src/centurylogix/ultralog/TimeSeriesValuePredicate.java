/**
 *  @file         TimeSeriesValuePredicate.java
 *  @copyright    Copyright (c) 2001
 *  @author       Abraham DeLaO
 *  @company      21st Century Technologies, Inc
 *  @history      Created June 8, 2001.
 *  @description  This Predicate allows us to identify TimeSeriesValue objects in the LogPlan.
 *                We can use this to capture all TimeSeriesValues that exist on the
 *                Blackboard, or just the ones that belong to a particular TimeSeries.
 *  @todo
 *
 **/

package com.centurylogix.ultralog;

import org.cougaar.util.UnaryPredicate;
import com.centurylogix.timeSeries.*;

public class TimeSeriesValuePredicate implements UnaryPredicate
{
  private String timeSeriesName = null;

  /**
   * This constructor is used to allow this Predicate instance to caputure all TimeSeriesValues.
   */
  public TimeSeriesValuePredicate()
  {
  }

  /**
   * This constructor is used to specify that this Predicate instance should identify
   * TimeSeriesValues that belong the a specific TimeSeries.
   * @param   name    The name of the TimeSeries that we want the TimeSeriesValues for.
   */
  public TimeSeriesValuePredicate(String name)
  {
    this.timeSeriesName = name;
  }

  /**
   * Satisfies the UrnaryPredicate Interface. Called to identify the input parameter
   * as an instance of a TimeSeriesValue of interest to us. If this Predicate instance is
   * only selecting TimeSeriesValues of a specific TimeSeries, then the input object must
   * be a TimeSeriesValue from that TimeSeries.
   * @param   o   Object to be checked against our interests.
   * @return  This returns true if the input parmeter meets our criteria. False is returned
   *          otherwise.
   */
  public boolean execute (Object o)
  {
    boolean ret = false;

    // if object is a TimeSeriesValue investigate it futther
    if (o instanceof TimeSeriesValue)
    {
      // if we have narrowed our criteria to a specific TimeSeries, check the input closer
      if (timeSeriesName != null)
      {
        TimeSeriesValue tsv = (TimeSeriesValue)o;
        String tsvName = tsv.getTimeSeriesName();

        // if the input is from our TimeSeries, return false.
        if (this.timeSeriesName.equals (tsvName))
          ret = true;
        else
          ret = false;
      }
      else //o.w. the input is not a TimeSeriesValue, return false.
        ret = true;
    }

    return ret;
  } // end public boolean execute

  public static void main(String[] args)
  {
    TimeSeriesValuePredicate tsvp = new TimeSeriesValuePredicate();
  }

} // end class