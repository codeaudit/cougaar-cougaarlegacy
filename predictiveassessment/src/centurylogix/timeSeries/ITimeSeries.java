/**
 *  @file         ITimeSeries.java
 *  @copyright    Copyright (c) 2001
 *  @author       Abraham DeLaO
 *  @company      21st Century Technologies, Inc
 *  @description  This is the description.
 *  @history      Created June 5, 2001.
 *  @todo
 *
 **/
package com.centurylogix.timeSeries;

import java.util.Iterator;

public interface ITimeSeries
{

  /**
   * Returns the start time for the time series.  This is the time of our earliest TimeSeriesValue.
   * @return  Time in milliseconds of the earliest time series element.
   */
  public long getStartTime ();

  /**
   * Returns the end time for the time series.  This is the time of our last TimeSeriesValue.
   * @return  Time in milliseconds of the last time series element.
   */
  public long getEndTime ();

  /**
   * Returns the time increment between successive time series elements.
   * @return  The time in milliseconds between time series elements.
   */
  public long getTimeIncrement ();

  /**
   * Returns the TimeSeriesValue at <tt> index </tt>. The index is zero-based.
   * @throws  IndexOutOfBoundsException   If <tt> index </tt> is out of range (less than
   *                                      zero or greater than the number of time series elements).
   * @param   The index to retrieve a time series element from.
   * @return  The (<tt>index</tt> + 1)th TimeSeriesValue from the beginnig.
   *          If no value exist at this index, <tt>null</tt> is returned.
   */
  public TimeSeriesValue getValueAt (int index) throws IndexOutOfBoundsException;

  /**
   * Returns the TimeSeriesValue at <tt> time </tt>.
   * @throws  IllegalArguementException   If <tt> time </tt> is out of range (less than the
   *                                      TimeSeries startTime or greater than endTime).
   * @param   The time in milliseconds to retrieve a time series elemetn from.
   * @return  The TimeSeriesValue value associated with the time interval starting at <tt>time</tt>.
   *          If no value exist at this time, <tt>null</tt> is returned.
   */
  public TimeSeriesValue getValueAt (long time) throws IllegalArgumentException;

  /**
   * This method returns the Java class of the underlying elements that make up the time
   * series.
   * @returns  The Java class of the time series values.
   */
  public Class getValueClass ();

  /**
   * Determines if a TimeSeriesValue is compatible with this time series.  Compatiblity allows
   * the <tt> tsv </tt> to be compared against the elements of this time series.  It
   * also means that <tt> tsv </tt> could potentially be added to this time series.  In order to
   * be compatible, <tt> tsv </tt> must be of the same Java class as the elements of this
   * TimeSeries. In addition, <tt> tsv </tt> must also have the same time series name given
   * to it at its instantiation as the elements of this TimeSeries.
   * @return  Returns true if <tt> tsv </tt> can be compared to the elements of or added to this
   *          TimeSeries, false otherwise.
   * @param   tsv   The TimeSeriesValue to be checked for compatiblity.
   */
  public boolean areCompatible (TimeSeriesValue tsv);

  /**
   * Determines if the TimesSeries, <ts>, is compatible with this time series.  Compatiblity allows
   * the elements of <tt> ts </tt> to be compared against the elements of this time series.  It
   * also means that <tt> ts </tt> could potentially be appended to this time series.  In order to
   * be compatible, the TimeSeriesValues of <tt> ts </tt> must be of the same Java class as
   * the elements of this TimeSeries. In addition, <tt> ts </tt> must also have the same
   * time series name as this one.
   * @return  Returns true if <tt> ts </tt> can be added and compared to this
   *          TimeSeries, false otherwise.
   * @param   ts    The TimeSeries to check for compatiblity.
   */
  public boolean areCompatible (TimeSeries ts);

  /**
   * Adds <tt> tsv </tt> to the time series.  If a time series element already exists for the
   * time associated with <tt> tsv </tt>, the present value is overwritten with the value of the
   * input parameter.
   * @param   tsv   The TimeSeriesValue to be inserted into the TimeSeries.
   * @throws  IncompatibleTimeSeriesException   If <tt> tsv </tt> is incompabitible with this
   *                                            time series and cannot be added.
   */
  public void addElement (TimeSeriesValue tsv) throws IncompatibleTimeSeriesException;

  /**
   * Appends <tt> ts </tt> to the time series.  If overlapping time series elements exists between
   * the two time series, the existing values overwritten with the values of the input time series.
   * @param   ts    The TimeSeries to be appended.
   * @throws  IncompatibleTimeSeriesException   If <tt> ts </tt> is incompabitible with this
   *                                            time series and cannot be appended.
   */
  public void addElements (TimeSeries ts) throws IncompatibleTimeSeriesException;

  /**
   * Adds <tt> tsv </tt> to the time series.  If a time series element already exists for the
   * time associated with <tt> tsv </tt>, the present value added to the value of the input
   * parameter.
   * @param   tsv   The TimeSeriesValue to be included into the TimeSeries.
   * @throws  IncompatibleTimeSeriesException   If <tt> tsv </tt> is incompabitible with this
   *                                            time series and cannot be added.
   */
  public void augmentElement (TimeSeriesValue tsv) throws IncompatibleTimeSeriesException;

  /**
   * Clears the element at <tt> index</tt>. The TimeSeries will not shrink in size since a
   * <tt> null </tt> will be inserted into this index location as a placeholder.
   * @throws  IndexOutOfBoundsException   If <tt> index </tt> is less than zero or greater
   *                                      than the TimeSeries size, an exception is thrown.
   * @param   index   The index to be cleared.
   */
  public void clearElement (int index) throws IndexOutOfBoundsException;

  /**
   * Clears the element at <tt> time</tt>. The TimeSeries will not shrink in size since a
   * <tt> null </tt> will be inserted at this time location as a placeholder.
   * @throws  IllegalArguementException   If <tt> time </tt> is less than the TimeSeries
   *                                      startTime or greater than the endTime.
   * @param   time   The time in the TimeSeries to be cleared.
   */
  public void clearElement (long time) throws IllegalArgumentException;

  /**
   * Clears the elements from <tt> startIndex</tt> (inclusive) to <tt> endIndex </tt> (exlusive).
   * The TimeSeries will not shrink in size since <tt> null </tt> will be inserted into these index
   * locations as a placeholders.
   * @throws  IndexOutOfBoundsException   If either paramter is less than zero or greater than the
   *                                      TimeSeries size  or if <tt> endIndex </tt> is less than
   *                                      <tt> startIndex </tt>.
   * @param   startIndex   The first index of the TimeSeries to be cleared.
   *          endIndex     The first index of after <tt> startIndex</tt> to NOT be left uncleared.
   */
  public void clearElements (int startIndex, int endIndex) throws IndexOutOfBoundsException;

  /**
   * Clears the elements from <tt> startTime</tt> (inclusive) to <tt> endTime </tt> (exlusive).
   * The TimeSeries will not shrink in size since <tt> null </tt> will be inserted into these
   * locations as a placeholders.
   * @throws  IllegalArguementExeption    If either paramter is less than TimeSeries startTime
   *                                      or greater than the endTime or if <tt> endTime </tt>
   *                                      is less than <tt> startTime </tt>.
   * @param   startTime    The time of the beginning TimeSeries element to be cleared.
   *          endTime      The first time after <tt>startTime</tt> in TimeSeries that is NOT uncleared.
   */
  public void clearElements (long startTime, long endTime) throws IllegalArgumentException;

  /**
   * Removes all elements of the TimeSeries beginning with the one at <tt> index </tt>.
   * @param  throws   IntexOutOfBoundsException     If <tt> index </tt> is less than zero or greater
   *                                                than size of TimeSeries.
   * @param  index    The starting index from which all others following will be removed.
   */
  public void trimToEnd (int index) throws IndexOutOfBoundsException;

  /**
   * Removes all elements of the TimeSeries from <tt> time </tt> to the end of the TimeSeries.
   * @throws  IllegalArgumentException    If time is before TimeSeries startTime or after
   *                                      endTime.
   * @param time   The startTime (inclusively) from which to begin removing elements.
   */
  public void trimToEnd (long time) throws IllegalArgumentException;

  /**
   * Removes all elements of the TimeSeries from the begining to <tt>index</tt>. This is
   * inclusive of the element at <tt>index</tt>, it IS removed.
   * @param  throws   IntexOutOfBoundsException     If <tt> index </tt> is less than zero or greater
   *                                                than size of TimeSeries.
   * @param index   The index at which we are to stop removing elements.
   */
  public void trimFromStart (int index) throws IndexOutOfBoundsException;

  /**
   * Removes all elements of the TimeSeries from the begining to elements at <tt>time</tt>.
   * This is inclusive of the element at <tt>time</tt>, it IS removed.
   * @throws  IllegalArgumentException    If time is before TimeSeries startTime or after
   *                                      endTime.
   * @param time   The time in the TimeSeries at which we are to stop removing elements.
   */
  public void trimFromStart (long time) throws IllegalArgumentException;

  /**
   * Returns a TimeSeries objects that contain the TimeSeriesValues contained in this
   * TimeSeries, from the one at <tt>startIndex</tt> (inclusively)
   * to the one at <tt> endIndex </tt> (exclusively).
   * @param   startIndex    The first index of this TimeSeries in the new TimeSeries copy.
   * @param   endIndex      The firstIndex after <tt> startIndex </tt> that is not copied.
   * @return  A new TimeSeries containing values from <>startIndex</tt> to <tt><endIndex/tt>.
   * @param  throws   IntexOutOfBoundsException     If <tt> startIndex</tt> or <tt>endIndex</tt>
   *                                                is less than zero or greater than TimeSeries size.
   */
  public TimeSeries subSeries (int startIndex, int endIndex) throws IndexOutOfBoundsException;

  /**
   * Returns a TimeSeries objects that contains the TimeSeriesValues in this
   * TimeSeries, from the one at <tt> startTime </tt> (inclusively)
   * to the one at <tt> endTime </tt> (exclusively).
   * @param   startTime    The time of the first element copied to the new TimeSeries.
   * @param   endTime      The first time after <tt> startTime </tt> that is not included.
   * @return  A new TimeSeries containing values from <>startIndex</tt> to <tt><endIndex/tt>.
   * @param  throws   IllegalArgumentException     If <tt> startIndex</tt> or <tt>endIndex</tt>
   *                                                is less than zero or greater than TimeSeries size.
   */
  public TimeSeries subSeries (long startTime, long endTime);

  /**
   * Removes all values in the TimeSeries.  It is now empty.
   */
  public void clear();

  /**
   * Test to see if there are any TimeSeriesValues that are part of this TimeSeries.
   * @return    Returns true if there is at least one element in the TimeSeries, false otherwise.
   */
  public boolean isEmpty ();

  /**
   * Returns the number of TimeSeriesValues that are currently part of this TimeSeries.
   * @return  The size of this TimeSeries.
   */
  public int size();

  /**
   * A Java Iterator that cycles through the TimeSeriesValues that make up this TimeSeries.
   * @return  A Java Iterator of TimeSeriesValue objects.
   */
  public Iterator iterator ();

  /**
   * A String representation of this TimeSeries.
   * @return  A String containg the String Repsentatino.
   */
  public String toString ();

  /**
   * A String that conatins an XML version of this object.
   * @return  An XML String that represents this object and its state.
   */
  public String toXML ();

  /**
   * Determines if the input parameter is identical to this TimeSeries. For equality, two
   * TimeSeries must contain elements of the same Java class, have the same time series name
   * all TimeSeriesValues must also be equal.
   * @param   ts    The TimeSeries to comparare with this one.
   * @return  True, if the two TimeSeries are the same; false, otherwise.
   */
  public boolean equals (TimeSeries ts);


}// end class ITimeSeries