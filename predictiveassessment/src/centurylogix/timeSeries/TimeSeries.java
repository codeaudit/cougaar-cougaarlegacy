/**
 *  @file         TimeSeries.java
 *  @copyright    Copyright (c) 2001
 *  @author       Abraham DeLaO
 *  @company      21st Century Technologies, Inc
 *  @description  This is the description.
 *  @history      Created June 5, 2001.
 *  @todo
 *
 **/

package com.centurylogix.timeSeries;

import java.util.ArrayList;
import java.util.Iterator;
import com.centurylogix.finalPredictiveAssessor.DebugFrame;

public class TimeSeries implements ITimeSeries
{
  private String name;
  private long startTime;
  private long endTime;
  private long timeIncrement;
  private Class valueClass;

  private ArrayList timeSeries = new ArrayList (256);

  public TimeSeries (TimeSeriesValue tsv)
  {
    this.name = tsv.getTimeSeriesName();
    this.startTime = tsv.getStartTime();
    this.timeIncrement = tsv.getTimeIncrement();
    this.endTime = this.startTime + this.timeIncrement;
    this.valueClass = tsv.getValueClass();

    timeSeries.add (tsv);
  }

  // private constructor used for easier cloning
  private TimeSeries ()
  {}

  public Object clone()
  {

    TimeSeries newTS = new TimeSeries ();
    newTS.name = this.name;
    newTS.valueClass = this.valueClass;
    newTS.startTime = this.startTime;
    newTS.endTime = this.endTime;
    newTS.timeIncrement = this.timeIncrement;
    newTS.timeSeries = (ArrayList) this.timeSeries.clone();

    return newTS;
  }

  /**
   * Returns the start time for the time series.  This is the time of our earliest TimeSeriesValue.
   * @return  Time in milliseconds of the earliest time series element.
   */
  public long getStartTime ()
  {
    return this.startTime;
  }

  /**
   * Returns the end time for the time series.  This time is exclusive in that it is the time
   * where our last TimeSeriesValue is no longer valid.
   * @return  Time first time in milliseconds that is no longer valid.
   */
  public long getEndTime ()
  {
    return this.endTime;
  }

  /**
   * Returns the time increment between successive time series elements.
   * @return  The time in milliseconds between time series elements.
   */
  public long getTimeIncrement ()
  {
    return this.timeIncrement;
  }

  /**
   * Returns the name associated with this TimeSeries instatiation.
   * @return  A String representing the TimeSeries name.
   */
  public String getName ()
  {
    return this.name;
  }

  /**
   * Sets the name associated with this time series. Does not affect the time series names
   *  associated that are part of any of the underlying TimeSeriesValues.
   * @param   newName   The new name for the this time series.
   */
  public void setName (String newName)
  {
    this.name = newName;
  }

  /**
   * Tests if an time is valid with respect to the bounds of this time series. If it falls
   *  before the valid start time or after the valid end time, it is invalid.
   *  @param    timeToTest  The time in milliseconds to be tested for validity
   *  @return   True if the input time is valid, false otherwise.
   */
   public boolean isValidTime (long timeToTest)
   {
    if (timeToTest < this.startTime || timeToTest >= this.endTime)
      return false;
    else
      return true;
   }


  /**
   *
   */
   public long getLastStartTime ()
   {
    int lastIndex = timeSeries.size() - 1;
    TimeSeriesValue tsv = (TimeSeriesValue)timeSeries.get(lastIndex);
    long lastStart = tsv.getStartTime();
    return lastStart;

   }
  /**
   * Returns the TimeSeriesValue at <tt> index </tt>. The index is zero-based.
   * @throws  IndexOutOfBoundsException   If <tt> index </tt> is out of range (less than
   *                                      zero or greater than the number of time series elements).
   * @param   The index to retrieve a time series element from.
   * @return  The (<tt>index</tt> + 1)th TimeSeriesValue from the beginnig.
   *          If no value exist at this index, <tt>null</tt> is returned.
   */
  public TimeSeriesValue getValueAt (int index) throws IndexOutOfBoundsException
  {
    TimeSeriesValue tsv = (TimeSeriesValue) timeSeries.get (index);
    return tsv;
  }

  /**
   * Returns the TimeSeriesValue at <tt> time </tt>.
   * @throws  IllegalArguementException   If <tt> time </tt> is out of range (less than the
   *                                      TimeSeries startTime or greater than endTime).
   * @param   The time in milliseconds to retrieve a time series elemetn from.
   * @return  The TimeSeriesValue value associated with the time interval starting at <tt>time</tt>.
   *          If no value exist at this time, <tt>null</tt> is returned.
   */
  public TimeSeriesValue getValueAt (long time) throws IndexOutOfBoundsException
  {
    TimeSeriesValue tsv = null;
    int index = translateTimeToIndex (time);
/*
    if (index >= timeSeries.size())
    {
      System.out.println ("NNNNNNNNNNNNNNNNN About to throw exception NNNNNNNNNNN");
      System.out.println ("request time :" + time);
      System.out.println ("Our End Time :" + this.endTime);
      System.out.println ("Our Start Time :" + this.startTime);
      System.out.println ("calc index :" + index);
      System.out.println ("Our   size :" + timeSeries.size());

    } */
    if (index < 0)
      throw new IndexOutOfBoundsException ("Illegal Time Arguement Paratmer for method getValueAt(long)");
    else
      tsv = (TimeSeriesValue) timeSeries.get (index);

    return tsv;
  }

  /**
   * This method returns the Java class of the underlying elements that make up the time
   * series.
   * @returns  The Java class of the time series values.
   */
  public Class getValueClass ()
  {
    return this.valueClass;
  }

  /**
   * Determines if a TimeSeriesValue is compatible with this time series.  Compatiblity allows
   * the <tt> tsv </tt> to be compared against the elements of this time series.  It
   * also means that <tt> tsv </tt> could potentially be added to this time series.  In order to
   * be compatible, <tt> tsv </tt> must be of the same Java class as the elements of this
   * TimeSeries. In addition, <tt> tsv </tt> must also have the same time series name given
   * to it at its instantiation as the elements of this TimeSeries and have the same timeIncrement.
   * @return  Returns true if <tt> tsv </tt> can be compared to the elements of or added to this
   *          TimeSeries, false otherwise.
   * @param   tsv   The TimeSeriesValue to be checked for compatiblity.
   */
  public boolean areCompatible (TimeSeriesValue tsv)
  {
    boolean ret = false;
    Class inputClass = tsv.getValueClass();
    long inputIncrement = tsv.getTimeIncrement();
    String inputName = tsv.getTimeSeriesName ();

    if (this.valueClass.equals (inputClass) && inputIncrement == this.timeIncrement &&
        this.name.equals (inputName))
    {
      ret = true;
    }
    // this should also require that the time increments be the same.
    return ret;
  } // end public boolean areCompatible (TimeSeriesValue)


  /**
   * Determines if the TimesSeries, <ts>, is compatible with this time series.  Compatiblity allows
   * the elements of <tt> ts </tt> to be compared against the elements of this time series.  It
   * also means that <tt> ts </tt> could potentially be appended to this time series.  In order to
   * be compatible, the TimeSeriesValues of <tt> ts </tt> must be of the same Java class as
   * the elements of this TimeSeries. In addition, <tt> ts </tt> must also have the same
   * time series name as this one and have the same timeIncrement.
   * @return  Returns true if <tt> ts </tt> can be added and compared to this
   *          TimeSeries, false otherwise.
   * @param   ts    The TimeSeries to check for compatiblity.
   */
  public boolean areCompatible (TimeSeries ts)
  {
    boolean ret = false;
    Class inputClass = ts.getValueClass();
    long inputIncrement = ts.getTimeIncrement();
    String inputName = ts.getName ();

//    System.out.println ("testing for compatiblity");
 //   System.out.println ("input name is " + inputName + ". This name is : " + this.name);
  //  System.out.println ("input increment is : "+ inputIncrement + " should be : " + this.timeIncrement);
//    System.out.println ("
    if (this.valueClass.equals (inputClass) && inputIncrement == this.timeIncrement &&
        this.name.equals (inputName))
    {
      ret = true;
    }
    // this should also require that the time increments be the same.
    return ret;
  } // end boolean areCompatible (TimeSeries)

  /**
   * This is a more relaxed measure of comparison between time series than that done by the
   * <tt>areCompatible</tt> method.  In this case, two time series can be merged if they
   * have the same time increment and contain values of the same type.
   * @return  Returns true if <tt> ts </tt> can be added and compared to this
   *          TimeSeries, false otherwise.
   * @param   ts    The TimeSeries to check for compatiblity.
   */
  public boolean areMergable (TimeSeries ts)
  {
    boolean ret = false;
    Class inputClass = ts.getValueClass();
    long inputIncrement = ts.getTimeIncrement();

//    System.out.println ("testing for mergability");
 //   System.out.println ("input increment is : "+ inputIncrement + " should be : " + this.timeIncrement);

    if (this.valueClass.equals (inputClass) && inputIncrement == this.timeIncrement)
      ret = true;

    return ret;
  }// end public boolean areMergable (TimeSeries)

  /**
   * Adds <tt> tsv </tt> to the time series.  If a time series element already exists for the
   * time associated with <tt> tsv </tt>, the present value is overwritten with the value of the
   * input parameter. As TimeSeriesValues are added, their
   * @param   tsv   The TimeSeriesValue to be inserted into the TimeSeries.
   * @throws  IncompatibleTimeSeriesException   If <tt> tsv </tt> is incompabitible with this
   *                                            time series and cannot be added.
   */
  public void addElement (TimeSeriesValue tsv) throws IncompatibleTimeSeriesException
  {
    if (!this.areCompatible (tsv))
    {
      throw new IncompatibleTimeSeriesException ("Incompatible TimeSeriesValue cannot be added " +
                                                 "to this TimeSeries. ");
    }

    // determine this element's index in the the time series.
    long report = tsv.getReportTime();
    int index = translateTimeToIndex (report);

    // adjust the time series values start time. When it is created the start and report time
      // are the same,
    long newStart = this.startTime + (index * this.timeIncrement);
    tsv.adjustStartTime (newStart);

    // if <index> is >= 0, we will be replacing existing element or appending new item
    if (index >= 0)
    {
      //if <index> is less than time series size, replace existing element.
      if (index < timeSeries.size())
        timeSeries.set (index, tsv);

      else // o.w. we must append the new value to the end of the time series
      {
        // if the new value does not follow immediately after the current trailing elt, add spacing
        int placeholdersToAdd = index - timeSeries.size();

        for (int i = 0; i < placeholdersToAdd; i++)
        {
          timeSeries.add (null);
        }

        // once we have added any necessary spacing, the new value is added
        timeSeries.add (tsv);

        // reset the timeSeries end time.
        this.endTime = tsv.getStartTime() + tsv.getTimeIncrement();
      }
    } // end if (index >= 0)

    else // if <index> is less than zero ,we must insert elements at the start of the time series
    {
      // insert our new element at the beginning of the time series.
      timeSeries.add (0, tsv);

      // if this addition does not belong direclty ahead of the previous first element, add spacing
      int placeholdersToAdd = Math.abs (index) - 1;

      for (int i = 0; i < placeholdersToAdd; i++)
      {
        timeSeries.add (1, null);
      }

      this.startTime = tsv.getStartTime();
    }// end else

  }// end public void addElement (TimeSeriesValue)

  /**
   * Appends <tt> ts </tt> to the time series.  If overlapping time series elements exists between
   * the two time series, the existing values overwritten with the values of the input time series.
   * The time series to be appended may contain null elements. But, by definition a all leading
   * and trailing nulls will be ignored since a TimeSeries must start and end with non-nulls.
   * @param   ts    The TimeSeries to be appended.
   * @throws  IncompatibleTimeSeriesException   If <tt> ts </tt> is incompabitible with this
   *                                            time series and cannot be appended.
   */
  public void addElements (TimeSeries ts) throws IncompatibleTimeSeriesException
  {
    Iterator tsIter = ts.iterator();

    while (tsIter.hasNext())
    {
      TimeSeriesValue tsv = (TimeSeriesValue) tsIter.next();

      // if null, don't bother trying to add.
      if (tsv == null)
        continue;

      this.addElement(tsv);
    }
  } //end void addElements (TimeSeries)

  /**
   * Adds <tt> tsv </tt> to the time series.  If a time series element already exists for the
   * time associated with <tt> tsv </tt>, the present value added to the value of the input
   * parameter.
   * @param   tsv   The TimeSeriesValue to be included into the TimeSeries.
   * @throws  IncompatibleTimeSeriesException   If <tt> tsv </tt> is incompabitible with this
   *                                            time series and cannot be added.
   */
  public void augmentElement (TimeSeriesValue tsv) throws IncompatibleTimeSeriesException
  {
    ;
  }

  /**
   * Clears the element at <tt> index</tt>. The TimeSeries will not shrink in size since a
   * <tt> null </tt> will be inserted into this index location as a placeholder.
   * @throws  IndexOutOfBoundsException   If <tt> index </tt> is less than zero or greater
   *                                      than the TimeSeries size, an exception is thrown.
   * @param   index   The index to be cleared.
   */
  public void clearElement (int index) throws IndexOutOfBoundsException
  {
    if (index < 0 || index >= timeSeries.size())
      throw new IndexOutOfBoundsException ("Illegal index in method clearElement (int)");
    else
      timeSeries.set(index, null);
  }

  /**
   * Clears the element at <tt> time</tt>. The TimeSeries will not shrink in size since a
   * <tt> null </tt> will be inserted at this time location as a placeholder.
   * @throws  IllegalArguementException   If <tt> time </tt> is less than the TimeSeries
   *                                      startTime or greater than the endTime.
   * @param   time   The time in the TimeSeries to be cleared.
   */
  public void clearElement (long time) throws IllegalArgumentException
  {
    ;
  }

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
  public void clearElements (int startIndex, int endIndex) throws IndexOutOfBoundsException
  {
    ;
  }

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
  public void clearElements (long startTime, long endTime) throws IllegalArgumentException
  {
    ;
  }

  /**
   * Removes all elements of the TimeSeries beginning with the one at <tt> index </tt>.
   * @param  throws   IntexOutOfBoundsException     If <tt> index </tt> is less than zero or greater
   *                                                than size of TimeSeries.
   * @param  index    The starting index from which all others following will be removed.
   */
  public void trimToEnd (int index) throws IndexOutOfBoundsException
  {
    if (index >= timeSeries.size() || index < 0)
      throw new IndexOutOfBoundsException ("Invalid Index in method : trimToEnd (int). Index is : "+
                                            index + ". Size is : " + timeSeries.size());

    int numToRemove = this.size() - index;
    for (int i = index; i < (numToRemove + index); i++)
      timeSeries.remove (index);

    this.endTime = this.endTime - (numToRemove * this.timeIncrement);
  }


  /**
   * Removes all elements of the TimeSeries from <tt> time </tt> to the end of the TimeSeries.
   * @throws  IllegalArgumentException    If time is before TimeSeries startTime or after
   *                                      endTime.
   * @param time   The startTime (inclusively) from which to begin removing elements.
   */
  public void trimToEnd (long time) throws IllegalArgumentException
  {
    int index = translateTimeToIndex (time);
    trimToEnd (index);
  }

  /**
   * Removes all elements of the TimeSeries from the begining to <tt>index</tt>. This is
   * inclusive of the element at the zero-based <tt>index</tt>. it IS removed.
   * @param  throws   IntexOutOfBoundsException     If <tt> index </tt> is less than zero or greater
   *                                                than size of TimeSeries.
   * @param index   The index at which we are to stop removing elements.
   */
  public void trimFromStart (int index) throws IndexOutOfBoundsException
  {
    if (index >= timeSeries.size() || index < 0)
      throw new IndexOutOfBoundsException ("Invalid Index in method : trimFromStart (int)");

    for (int i = 0; i < index; i++)
      timeSeries.remove (0);

    this.startTime = this.startTime + ((index + 1 ) * this.timeIncrement);
  }

  /**
   * Removes all elements of the TimeSeries from the begining to elements at <tt>time</tt>.
   * This is inclusive of the element at <tt>time</tt>, it IS removed.
   * @throws  IllegalArgumentException    If time is before TimeSeries startTime or after
   *                                      endTime.
   * @param time   The time in the TimeSeries at which we are to stop removing elements.
   */
  public void trimFromStart (long time) throws IllegalArgumentException
  {
    ;
  }

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
  public TimeSeries subSeries (int startIndex, int endIndex) throws IndexOutOfBoundsException
  {
    return new TimeSeries (new TimeSeriesValue ("Test", new Double (10), 10, 20));
  }

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
  public TimeSeries subSeries (long startTime, long endTime)
  {
    return new TimeSeries (new TimeSeriesValue ("Test", new Double (10), 10, 20));
  }

  /**
   * Removes all values in the TimeSeries.  It is now empty.
   */
  public void clear()
  {
    timeSeries.clear();
  }

  /**
   * Test to see if there are any TimeSeriesValues that are part of this TimeSeries.
   * @return    Returns true if there is at least one element in the TimeSeries, false otherwise.
   */
  public boolean isEmpty ()
  {
    return timeSeries.isEmpty();
  }

  /**
   * Returns the number of TimeSeriesValues that are currently part of this TimeSeries.
   * @return  The size of this TimeSeries.
   */
  public int size()
  {
    return timeSeries.size();
  }

  /**
   * A Java Iterator that cycles through the TimeSeriesValues that make up this TimeSeries.
   * @return  A Java Iterator of TimeSeriesValue objects.
   */
  public Iterator iterator ()
  {
    return timeSeries.iterator();
  }

  /**
   * A String representation of this TimeSeries.
   * @return  A String containg the String Repsentatino.
   */
  public String toString ()
  {
    return timeSeries.toString();
  }

  public String shortToString ()
  {
    StringBuffer buffer = new StringBuffer (128);
    buffer.append ("\n\t Time Series Short Rep -------->>");
    buffer.append ("\n\t\t Time series name : " + this.getName());
    buffer.append ("\n\t\t Time series size : " + this.size());
    buffer.append ("\n\t\t Java Class name of time series value : " + getValueClass().toString());
    buffer.append ("\n\t\t Start Time of Value : " + this.startTime);
    buffer.append ("\n\t\t End Time for Value : " + this.endTime);
    buffer.append ("\n\t\t Time Increment :" + this.timeIncrement +"\n");

    String stringRep = buffer.toString();
    return stringRep;

  }
  /**
   * A String that conatins an XML version of this object.
   * @return  An XML String that represents this object and its state.
   */
  public String toXML ()
  {
    return new String ("");
  }

  /**
   * Determines if the input parameter is identical to this TimeSeries. For equality, two
   * TimeSeries must contain elements of the same Java class, have the same time series name
   * all TimeSeriesValues must also be equal.
   * @param   ts    The TimeSeries to comparare with this one.
   * @return  True, if the two TimeSeries are the same; false, otherwise.
   */
  public boolean equals (TimeSeries ts)
  {
    boolean ret = false;

    if (this.name.equals (ts.getName()) && this.timeIncrement == ts.getTimeIncrement() &&
        this.startTime == ts.getStartTime() && this.endTime == ts.getEndTime() &&
        this.valueClass.equals (ts.getValueClass()))
    {
      ret = true;
    }
    return ret;
  }

  /**
   *  This methods transforms a time into the corresponding index for that time series information.
   *  The resulting index may be negative if <tt> time </tt> falls before the startTime for this
   *  TimeSeries. There is not guaranteed to be an element at this index, this is merely a way
   *  to find out how many timeIncrement units from the origin a given time is.
   *  @param  time    The time to translate to an index.
   *  @return   The index in the TimeSeries associated with <tt> time </tt>.
   */
  public int translateTimeToIndex (long time)
  {
    // if the timeIncrement is somhow zero, return zero
    if (this.timeIncrement <= 0)
      return 0;

    int diffCount = 0;
    long diff = 0 ;

    // if the input time is less than start time, answer will be negative.  The magnitude is the
      // number of whole timeIncrement units <time> falls before the TimeSeries origin.
    if (time < this.startTime)
    {
      diffCount--;    // this adjustment is necessary since index immediate before start is -1.
      diff = this.startTime - time;

      // iterativey, deccrease the difference by timeIncrement
      while (diff > this.timeIncrement)
      {
        diff = diff - this.timeIncrement;
        diffCount--;
      }
    }
    else //o.w. if <time> is greater than startTime, just find the how many timeIncrement units
          // lie between the origin and the input
    {
      diff = time - this.startTime;

      while (diff >= this.timeIncrement)
      {
        diff = diff - this.timeIncrement;
        diffCount++;
      }
    }

    return diffCount;
 }// end translateTimeToIndex (long)


  public int getIntervalUnits (long start, long end)
  {
    int count = 0;
    long diff = end - start;

    while (diff >= this.timeIncrement)
    {
      diff = diff - this.timeIncrement;
      count++;
    }

    return count;
  }// end getIntervalUnits (long, long)

  public static void main(String[] args)
  {
    DebugFrame df = new DebugFrame ();
    df.show();
    df.setTitle ("TimeSeries BlackBox testing");

    TimeSeriesValue tsv = new TimeSeriesValue ("TestSeries", new Double (10), 20, 20);
    TimeSeries ts = new TimeSeries (tsv);

    /*************** testing translateTimeToIndex ****************************/
    int index = ts.translateTimeToIndex (20);
    if (index == 0)
     df.addText ("1. Pass : translateTimeToIndex() determines index for time  20." );
    else
      df.addText ("1. Fail : translateTimeToIndex() does NOT determine index for time 20");

    index = ts.translateTimeToIndex (39);
    if (index == 0)
     df.addText ("2. Pass : translateTimeToIndex() determines index for time  39." );
    else
      df.addText ("2. Fail : translateTimeToIndex() does NOT determine index for time 39");

    index = ts.translateTimeToIndex (40);
    if (index == 1)
     df.addText ("3. Pass : translateTimeToIndex() determines index for time  40." );
    else
      df.addText ("3. Fail : translateTimeToIndex() does NOT determine index fortime 40.");

    index = ts.translateTimeToIndex (19);
    if (index == -1)
     df.addText ("4. Pass : translateTimeToIndex() determines index for time  19." );
    else
      df.addText ("4. Fail : translateTimeToIndex() does NOT determine index for time 19");

    index = ts.translateTimeToIndex (0);
    if (index == -1)
     df.addText ("5. Pass : translateTimeToIndex() determines index for time  0." );
    else
      df.addText ("5. Fail : translateTimeToIndex() does NOT determine index for time 0");

    index = ts.translateTimeToIndex (-1);
    if (index == -2)
     df.addText ("6. Pass : translateTimeToIndex() determines index for time  -1." );
    else
      df.addText ("6. Fail : translateTimeToIndex() does NOT determine index for time -1.");

    index = ts.translateTimeToIndex (-22);
    if (index == -3)
     df.addText ("7. Pass : translateTimeToIndex() determines index for time  -22." );
    else
      df.addText ("7. Fail : translateTimeToIndex() does NOT determine index for time -22.");

    /************ testing of areCompatible (TimeSeriesValue) ************************/
    df.addText ("\n");

    TimeSeriesValue tsv2 = new TimeSeriesValue ("TestSeries", new Double (10), 20, 20);
    if (ts.areCompatible (tsv2))
     df.addText ("8. Pass : areCompatible() identifies compatible TimeSeriesValue" );
    else
      df.addText ("8. Fail : translateTimeToIndex() does NOT identify compatible TimeSeries Value");

    tsv2 = new TimeSeriesValue ("NOTTestSeries", new Double (10), 20, 20);
    if (!ts.areCompatible (tsv2))
     df.addText ("9. Pass : areCompatible() identifies incompatible TimeSeriesValue names" );
    else
      df.addText ("9. Fail : translateTimeToIndex() does NOT identify incompatible TimeSeries Value names");

    tsv2 = new TimeSeriesValue ("TestSeries", new String ("10"), 20, 20);
    if (!ts.areCompatible (tsv2))
     df.addText ("10. Pass : areCompatible() identifies incompatible TimeSeries types" );
    else
      df.addText ("10. Fail : translateTimeToIndex() does NOT identify incompatible TimeSeries types");

    tsv2 = new TimeSeriesValue ("TestSeries", new Double (2), 20, 30);
    if (!ts.areCompatible (tsv2))
     df.addText ("11. Pass : areCompatible() identifies incompatible timeIncrements" );
    else
      df.addText ("11. Fail : translateTimeToIndex() does NOT identify incompatible timeIncrements");


    /************ testing addElement (TimeSeriesValue) and getValueAt() **********************/
    df.addText ("\n");

    tsv = new TimeSeriesValue ("TestSeries", new Double (10), 20, 20);
    ts = new TimeSeries (tsv);
    tsv2 = new TimeSeriesValue ("TestSeries", new Double (20), 40, 20);

    try { ts.addElement (tsv2); }
    catch (IncompatibleTimeSeriesException e)
    { df.addText ("12. Fail : addElement() fails to add element, IncompatibleException thrown" ); }
    catch (Exception e)
    { df.addText ("12. Fail : addElement() fails to add element, some Exception thrown" );}

    TimeSeriesValue tsv3 = ts.getValueAt(1);
//    df.addText (ts.toString());
    if (tsv3.equals (tsv2))
      df.addText ("12. Pass : addElement() correctly inserts a new element");
    else
      df.addText ("12. Fail : addElement() does NOT correctly inserts a new element");


    tsv2 = new TimeSeriesValue ("TestSeries", new Double (30), 65, 20);
    try { ts.addElement (tsv2); }
    catch (IncompatibleTimeSeriesException e)
    { df.addText ("13. Fail : addElement() fails to add element, IncompatibleException thrown" ); }
    catch (Exception e)
    { df.addText ("13. Fail : addElement() fails to add element, some Exception thrown" );}

    tsv3 = ts.getValueAt(2);
//    df.addText (ts.toString());

    if (tsv3.equals (tsv2))
      df.addText ("13. Pass : addElement() correctly inserts a new element");
    else
      df.addText ("13. Fail : addElement() does NOT correctly inserts a new element");


    tsv2 = new TimeSeriesValue ("TestSeries", new Double (30), 100, 20);
    try { ts.addElement (tsv2); }
    catch (IncompatibleTimeSeriesException e)
    { df.addText ("14. Fail : addElement() fails to add element, IncompatibleException thrown" ); }
    catch (Exception e)
    { df.addText ("14. Fail : addElement() fails to add element, some Exception thrown" );}

    tsv3 = ts.getValueAt(4);
    if (tsv3.equals (tsv2))
      df.addText ("14. Pass : addElement() correctly inserts a new element.");
    else
      df.addText ("14. Fail : addElement() does NOT correctly inserts a new element");


    TimeSeriesValue tsv4 = new TimeSeriesValue ("TestSeries", new Double (30), 0, 20);
    try { ts.addElement (tsv4); }
    catch (IncompatibleTimeSeriesException e)
    { df.addText ("15. Fail : addElement() fails to add element, IncompatibleException thrown" ); }
    catch (Exception e)
    { df.addText ("15. Fail : addElement() fails to add element, some Exception thrown" );}

    tsv3 = ts.getValueAt(0);
    TimeSeriesValue tsv5 = ts.getValueAt (5);
    if (tsv3.equals (tsv4) && tsv5.equals(tsv2))
      df.addText ("15. Pass : addElement() correctly inserts a new element");
    else
      df.addText ("15. Fail : addElement() does NOT correctly inserts a new element");


    tsv4 = new TimeSeriesValue ("TestSeries", new Double (100), 40, 20);
    try { ts.addElement (tsv4); }
    catch (IncompatibleTimeSeriesException e)
    { df.addText ("16. Fail : addElement() fails to add element, IncompatibleException thrown" ); }
    catch (Exception e)
    { df.addText ("16. Fail : addElement() fails to add element, some Exception thrown" );}
//    df.addText (ts.toString());
    tsv3 = ts.getValueAt(2);
    if (tsv3.equals (tsv4))
      df.addText ("16. Pass : addElement() correctly inserts a new element");
    else
      df.addText ("16. Fail : addElement() does NOT correctly inserts a new element");


    tsv5 = new TimeSeriesValue ("NOTTestSeries", new Double (100), 40, 20);
    try {
      ts.addElement (tsv5);
      df.addText ("17. Fail : addElement() adds an incompatible element to TimeSeries.");
    }
    catch (IncompatibleTimeSeriesException e)
    { df.addText ("17. Pass : addElement() does NOT add incompatible, IncompatibleException thrown" ); }
    catch (Exception e)
    { df.addText ("17. Fail : addElement() fails to add element, some Exception thrown" );}

    /************************testing getValueAt (long )*******************************/
    df.addText ("\n");
    try {
      long time = 40;

      tsv5 = ts.getValueAt (time);
      if (tsv5.equals (tsv4))
        df.addText ("18. Pass : getValueAt() correctly retrieves an element");
      else
        df.addText ("18. Fail : addElement() does NOT correctly retrieve an element");
    }
    catch (Exception e)
    { df.addText ("18. Fail : addElement() does NOT correctly retrieve an element - Exception"); }

    try {
      long time = 59;

      tsv5 = ts.getValueAt (time);
      if (tsv5.equals (tsv4))
        df.addText ("19. Pass : getValueAt() correctly retrieves an element");
      else
        df.addText ("19. Fail : addElement() does NOT correctly retrieve an element");
    }
    catch (Exception e)
    { df.addText ("19. Fail : addElement() does NOT correctly retrieve an element - Exception"); }


    /******************* testing trimToEnd (int) **************************************/
    tsv = new TimeSeriesValue ("TestSeries", new Double (10), 20, 20);
    TimeSeriesValue tsv1 = new TimeSeriesValue ("TestSeries", new Double (20), 40, 20);
    tsv2 = new TimeSeriesValue ("TestSeries", new Double (30), 100, 20);

    ts = new TimeSeries (tsv);
    ts.addElement (tsv1);
    ts.addElement (tsv2);

    int indexToTrimFrom = 1;
    ts.trimToEnd (indexToTrimFrom);

    if (ts.size() == 1)
    {
      if ( ((Double) ts.getValueAt(0).getValue()).doubleValue() == 10)
        df.addText ("20. Pass : trimToEnd() correctly trims to end of time series");
      else
        df.addText ("20. Fail : trimToEnd() does NOT correctly trim trailing elements : wrong value");
    }
    else
      df.addText ("20. Fail : trimToEnd() does NOT correctly trim trailing elements : wrong length");

  } // end main

}// end class TimeSeries