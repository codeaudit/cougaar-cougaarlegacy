/**
 *  @file         IncompatibleTimeSeriesException.java
 *  @copyright    Copyright (c) 2001
 *  @author       Abraham DeLaO
 *  @company      21st Century Technologies, Inc
 *  @description  This is the description.
 *  @history      Created June 6, 2001.
 *  @todo
 *
 **/

package com.centurylogix.timeSeries;

public class IncompatibleTimeSeriesException extends RuntimeException
{
  public IncompatibleTimeSeriesException()
  {
    super ();
  }

  public IncompatibleTimeSeriesException(String s)
  {
    super (s);
  }
} // end class