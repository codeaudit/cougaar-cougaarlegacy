/**
 *  @file         TimeSeriesValue.java
 *  @copyright    Copyright (c) 2001
 *  @author       Abraham DeLaO
 *  @company      21st Century Technologies, Inc
 *  @description
 *  @see          TimeSeries
 *  @history      Created June 13, 2001.
 *  @todo         1. try modularizing the capturing of the correct return value class
 **/

package com.centurylogix.timeSeries;

import java.util.Iterator;
import com.centurylogix.ultralog.DebugFrame;

public final class TimeSeriesUtilities
{
  static DebugFrame df = new DebugFrame (false);
  static {
    df.show();
  }

  // private constructor ensuring class can't be instantiated
  private TimeSeriesUtilities ()
  {
  }

  /**
   * This method takes the input time series and removes all null occurences found in the
   * input. Nulls are replaced with new TimeSeriesValues whose value is the average of the
   * first legitimate TimeSeriesValues that come before and after the null in the time series.
   * If a sequence of nulls is located they are all replaced with the average of the pair of
   * non-null elements the border the sequence. These new TimeSeriesValues that replace the
   * nulls will be compatible with other TimeSeriesValues in the time series. All leading and
   * trailing nulls are discarded.
   * @param   ts  The input time series.
   * @modifies    ts
   * @throws      IncompatibleTimeSeriesException   Thrown if input TimeSeries is not internally
   *                                                consistent and can't correctly replace nulls.
   */
  public static void replaceNulls (TimeSeries ts) throws IncompatibleTimeSeriesException
  {
    if (ts == null)
      return;

    /**** remove the leading elements in the time series that are nulls *********/
    int leadingNullCount = 0;

    boolean nullsStillLead = true;
    // search for the first non-null element in the time series.
    while (nullsStillLead)
    {
      if ( ts.getValueAt (leadingNullCount) == null)
        leadingNullCount++;
      else
        nullsStillLead = false;

      if (leadingNullCount == ts.size())
        nullsStillLead = false;
    } //end while

    // trim the null elements from the time series
    if (leadingNullCount > 0)
      ts.trimFromStart (leadingNullCount);


    /********  begin replacing internal nulls with average of bordering elements ******/
    boolean endNotReached = true;
    TimeSeriesValue holdElt = ts.getValueAt (0); // 1st element will not be null, this is the
                                                    //    inital value we will hold
    int nextIndex = 1;                           // the next index to look for null

    // cycle through time series replacing null elements with average of surroundin non-nulls
    while (endNotReached)
    {
      if (nextIndex >= ts.size())
        endNotReached = false;

      // if next element is not null, hold a copy of it and update index for next null search index
      else if (ts.getValueAt (nextIndex) != null)
      {
        holdElt = (TimeSeriesValue) ts.getValueAt (nextIndex);
        nextIndex++;
      }
      else // o.w. element at <nextIndex> must be null, look for next following non-null
      {
        df.addText ("Found null element in time series at : " + nextIndex);
        int searchIndex = nextIndex + 1;          // begin searching at following index for non-null
        boolean lookingForNextNonNull = true;

        // keep searching in time series for next non null or end of time series
        while (lookingForNextNonNull)
        {
          if (searchIndex == ts.size()) //should only happen in case of trailing nulls
            lookingForNextNonNull = false;

          else if (ts.getValueAt (searchIndex) == null)
            searchIndex++;
          else
            lookingForNextNonNull = false;
        }

        // if search reached end of time series, trim trailing nulls since no non-nulls found
        if (searchIndex == ts.size())
          ts.trimToEnd (nextIndex);
        else // o.w. replace all nulls with the avg. of saved non-null and the one we just found
        {
          df.addText ("Looking to replace internal nulls ");

          // compute the average of the non-nulls that border this sequence of nulls
          Object avgValue = avgTimeSeriesValues (ts.getValueAt (searchIndex), holdElt);

          // prepare TimeSeriesValue information needed for compatibility
          String name = ts.getName();
          long increment = ts.getTimeIncrement ();
          long thisStart = ts.getValueAt (nextIndex - 1).getStartTime() + increment;

          // if avg. is somehow null, we need to throw an exception
          if (avgValue == null)
            throw new IncompatibleTimeSeriesException ("Could not properly replace nulls in " +
                      " time series : " + name);

          // replace all nulls with new TimeSeriesValues whose value is the calculate average
          for (int i = nextIndex; i < searchIndex; i++)
          {
            TimeSeriesValue newTSV = new TimeSeriesValue (name, avgValue, thisStart, increment);
            df.addText ("Replacing null element at :" + i + " with value : " + newTSV.toString());

            // adjust the start time for the nex time series value
            thisStart = thisStart + increment;

            ts.addElement (newTSV); // replace the observed null with this new value
          } // end for

          // hold a copy of the non-null for potentail future use in averaging if nulls follow it
          holdElt = (TimeSeriesValue)ts.getValueAt (searchIndex).clone();
        }// end else

        // update location to resume null search for index after our last found non-null
        nextIndex = searchIndex + 1;

      }// end else
    } // end while (endNotReached)
  } // end public static void replaceNulls (TimeSeries ts)


  /**
   * Creates a differenced version of the input time series.  The new differenced time series
   * represents the difference measured between each pair of neighboring TimeSeriesValues that
   * comprise the input TimeSeries.  The new time series is exactly one element shorter than
   * the input time series. Only numeric time series can be differenced.
   * @param   ts  The time series to be analyzed and differenced
   * @return      A new TimeSeries that is a differenced version of the input
   * @requires    All elements in the input must be non-null
   * @throws      IllegalArgumentException    Thrown if input TimeSeries is not numeric in nature.
   */
  public static TimeSeries getDifferencedTimeSeries (TimeSeries ts) throws IllegalArgumentException
  {
    df.addText ("Differencing time series : " + ts.toString());

    TimeSeries retTS = null;
    Class tsClass = ts.getValueClass();

    if (isClassNumeric (tsClass))
    {
      Iterator tsIter = ts.iterator();
      TimeSeriesValue tsv = null;         // these two variables will hold the pair of elts. to be
      TimeSeriesValue lastTSV = null;             // differenced

      // create a new name for the new time series we are about to create.
      String newTSName = new String (ts.getName() + "-->Differenced");

      // cycle through the input subracting each element from the previous one.
      while (tsIter.hasNext())
      {
        try {
          tsv = (TimeSeriesValue) tsIter.next();

          if (lastTSV != null) // this will only be null during initial pass
          {
            // get the difference between the current elt. and the last one
            Object diff = TimeSeriesUtilities.subtractTimeSeriesValues(tsv, lastTSV);

            long start = tsv.getStartTime();            //the start time for this new element

            df.addText ("new diff is : " + diff.toString() + " start : " + start );

            TimeSeriesValue newTSVal = new TimeSeriesValue (newTSName, diff, start, tsv.getTimeIncrement());

            // if the differenced time series has been created, add to it; o.w. create a new one
            if (retTS == null)
              retTS = new TimeSeries (newTSVal);
            else
              retTS.addElement (newTSVal);
          }

          // save the current elt. for use in the next differencing iteration
          lastTSV = (TimeSeriesValue)tsv.clone();

        }// end try
        catch (Exception e) // will catch any null, casting or Incomapitibiltiy exceptions and return null
        {
          df.addText ("Exception in differencening " + e.getMessage());
          return null;
        }

      } // end while
    }// end if (isClassNumeric())
    else
      throw new IllegalArgumentException ("Can not difference non-numeric time series : " + ts.getName());

    return retTS;
  }// end public static TimeSeries getDiffernencedTimeSeries (TimeSeries)


  /**
   * Determines if the input class is numeric in nature and can be used in time series
   * calculations. To be considered numberic, the input class must implement the Java Number
   * interface.
   * @param   classToTest   The class to be tested in order to determine if it is numeric.
   * @return  Returns true if input class is numeric, and false otherwise.
   */
  public static boolean isClassNumeric (Class classToTest)
  {
    boolean ret = false;

    try
    {
      Class numberClass = Class.forName("java.lang.Number");

      if (numberClass.isAssignableFrom (classToTest))
        return true;
    }
    catch (ClassNotFoundException e)
    { ; }// if this exception is thrown do nothing and just return false

    return ret;
  }


  public static void makeSameLength (TimeSeries tsOne, TimeSeries tsTwo)
  {
    if (tsOne.size() == tsTwo.size() && tsOne.getStartTime() == tsTwo.getStartTime())
      return;


  }

  /**
   * Returns the maximum value found in the time series. This is useful for graphing
   * purposes in order to determine the appropriate scaling.
   */
  public static double getMaxValue (TimeSeries ts)
  {
    double maxVal = Double.MIN_VALUE;
    double thisVal = 0;

    Iterator tsIter = ts.iterator();

    while (tsIter.hasNext())
    {
      TimeSeriesValue tsv = (TimeSeriesValue) tsIter.next();
      thisVal = ((Number) tsv.getValue()).doubleValue();

      if (thisVal > maxVal)
        maxVal = thisVal;
    }

    return maxVal;
  } // end public static Number getMaxValue (TimeSeries)

  /**
   * Returns the minumum value found in the time series. This is useful for graphing
   * purposes in order to determine the appropriate scaling.
   */
  public static double getMinValue (TimeSeries ts)
  {
    double minVal = Double.MAX_VALUE;
    double thisVal = 0;

    Iterator tsIter = ts.iterator();

    while (tsIter.hasNext())
    {
      TimeSeriesValue tsv = (TimeSeriesValue) tsIter.next();
      thisVal = ((Number) tsv.getValue()).doubleValue();

      if (thisVal < minVal)
        minVal = thisVal;
    }

    return minVal;
  } // end public static Number getMinValue (TimeSeries)

  /**
   *
   */
  public static Double calcTimeSeriesMean (TimeSeries ts)
  {
    Double mean = null;

    if (ts == null)
      return mean;

    Class tsClass = ts.getValueClass();

    if (isClassNumeric (tsClass))
    {
      double sum = 0;
      int size = ts.size();

      Iterator tsIter = ts.iterator();

      while (tsIter.hasNext())
      {
        TimeSeriesValue tsv = (TimeSeriesValue) tsIter.next();
        double val = ((Number) tsv.getValue()).doubleValue();
        sum = sum + val;
      }

      mean = new Double (sum / size);
    }// end if (classIsNumeric (ts))

    return mean;
  } // end public static Double calcTimeSeriesMean (TimeSeries)


  /**
   * Adds two TimeSeriesValues together and returns an Number of the same class as the values
   * represented by the two input time series values.  For instance, if both inputs represent
   * Double time series values, the returned sum will be a Double as well and can be casted
   * as such.  Thus, both inputs must be numeric and of the same class or null will be returned.
   * @param   tsvOne    The first of of the two TimeSeriesValues to have their values summed.
   * @param   tsvTwo    The second of of the two TimeSeriesValues to have their values summed.
   * @return  An instance of a Java Number that is the sum of the two inputs.
   */
  public static Number addTimeSeriesValues (TimeSeriesValue tsvOne, TimeSeriesValue tsvTwo)
  {
    Number ret = null;

    // get the values of the two input objects
    Object valOneObj = tsvOne.getValue();
    Object valTwoObj = tsvTwo.getValue();

    // get the Java classes for the time series values.
    Class classOne = valOneObj.getClass();
    Class classTwo = valTwoObj.getClass();

    if (classOne.equals (classTwo))
    {
      if (isClassNumeric (classOne) && isClassNumeric (classTwo))
      {

        // if the input time series values are Doubles, return a Double
        if (classOne.isInstance (new Double (0)))
        {
          double valOne = ((Double) tsvOne.getValue()).doubleValue();
          double valTwo = ((Double) tsvTwo.getValue()).doubleValue();

          Double sum = new Double (valOne + valTwo);
          return sum;
        }
        else if (classOne.isInstance (new Integer (0)))
        {
          int valOne = ((Integer) tsvOne.getValue()).intValue();
          int valTwo = ((Integer) tsvTwo.getValue()).intValue();

          Integer sum = new Integer (valOne + valTwo);
          return sum;
        }
        else if (classOne.isInstance (new Long (0)))
        {
          long valOne = ((Long) tsvOne.getValue()).longValue();
          long valTwo = ((Long) tsvTwo.getValue()).longValue();

          Long sum = new Long (valOne + valTwo);
          return sum;
        }
      }
    }
    else // if time series values were not of the same class or are not numeric, return null
      ret = null;

    return ret;
  } // end public static Number addTimeSeriesValues ()


  /**
   * Subtracts two TimeSeriesValues and returns an Number of the same class as the values
   * represented by the two input time series values.  For instance, if both inputs represent
   * Double time series values, the returned difference will be a Double as well and can be casted
   * as such.  Thus, both inputs must be numeric and of the same class or null will be returned.
   * @param   tsvOne    The first of of the two TimeSeriesValues in the subtraction.
   * @param   tsvTwo    The second TimeSeriesValues to be subtracted from the first.
   * @return  An instance of a Java Number that is the difference of the two inputs.
   */
  public static Number subtractTimeSeriesValues (TimeSeriesValue tsvOne, TimeSeriesValue tsvTwo)
  {
    Number ret = null;

    Object valOneObj = tsvOne.getValue();
    Object valTwoObj = tsvTwo.getValue();

    Class classOne = valOneObj.getClass();
    Class classTwo = valTwoObj.getClass();

    if (classOne.equals (classTwo))
    {
      if (isClassNumeric (classOne) && isClassNumeric (classTwo))
      {
        if (classOne.isInstance (new Double (0)))
        {
          double valOne = ((Double) tsvOne.getValue()).doubleValue();
          double valTwo = ((Double) tsvTwo.getValue()).doubleValue();

          Double diff = new Double (valOne - valTwo);
          return diff;
        }
        else if (classOne.isInstance (new Integer (0)))
        {
          int valOne = ((Integer) tsvOne.getValue()).intValue();
          int valTwo = ((Integer) tsvTwo.getValue()).intValue();

          Integer diff = new Integer (valOne - valTwo);
          return diff;
        }
        else if (classOne.isInstance (new Long (0)))
        {
          long valOne = ((Long) tsvOne.getValue()).longValue();
          long valTwo = ((Long) tsvTwo.getValue()).longValue();

          Long diff = new Long (valOne - valTwo);
          return diff;
        }
      }// end if both classes are numeric
    }// end if (classOne.equals (classTwo))
    else
      ret = null;

    return ret;
  } // end public static Number subtractTimeSeriesValues ()


  /**
   * Averages two TimeSeriesValues and returns an Number of the same class as the values
   * represented by the two input time series values.  For instance, if both inputs represent
   * Double time series values, the returned average will be a Double as well and can be casted
   * as such.  Thus, both inputs must be numeric and of the same class or null will be returned.
   * @param   tsvOne    The first of of the two TimeSeriesValues to be averaged.
   * @param   tsvTwo    The second TimeSeriesValues to be averaged.
   * @return  An instance of a Java Number that is the average value of the two inputs.
   */
  public static Number avgTimeSeriesValues (TimeSeriesValue tsvOne, TimeSeriesValue tsvTwo)
  {
    Number ret = null;

    Object valOneObj = tsvOne.getValue();
    Object valTwoObj = tsvTwo.getValue();

    Class classOne = valOneObj.getClass();
    Class classTwo = valTwoObj.getClass();

    if (classOne.equals (classTwo))
    {
      if (isClassNumeric (classOne) && isClassNumeric (classTwo))
      {
        if (classOne.isInstance (new Double (0)))
        {
          double valOne = ((Double) tsvOne.getValue()).doubleValue();
          double valTwo = ((Double) tsvTwo.getValue()).doubleValue();

          Double avg = new Double ((valOne + valTwo) / 2);
          return avg;
        }
        else if (classOne.isInstance (new Integer (0)))
        {
          int valOne = ((Integer) tsvOne.getValue()).intValue();
          int valTwo = ((Integer) tsvTwo.getValue()).intValue();

          Integer avg = new Integer ((valOne + valTwo) / 2);
          return avg;
        }
        else if (classOne.isInstance (new Long (0)))
        {
          long valOne = ((Long) tsvOne.getValue()).longValue();
          long valTwo = ((Long) tsvTwo.getValue()).longValue();

          Long avg = new Long ((valOne + valTwo) / 2);
          return avg;
        }
      }
    }
    else
      ret = null;

    return ret;
  } // end public static Number addTimeSeriesValues ()

  public static TimeSeries mergeTimeSeries (String newTSName, TimeSeries tsOne, TimeSeries tsTwo)
  {
    TimeSeries earlierTS;
    TimeSeries laterTS;
    long startTime = 0;
    long endTime = 0;

    if (!tsOne.areMergable(tsTwo))
    {
      df.addText ("Can not merge incomapatible time series .");
      return null;
    }
    else
      df.addText ("Time Series to be merged are compatible");

    long interval = tsOne.getTimeIncrement();

    if (tsOne.getStartTime() <= tsTwo.getStartTime())
    {
      earlierTS = tsOne;
      laterTS = tsTwo;
      startTime = tsOne.getStartTime();
    }
    else
    {
      earlierTS = tsTwo;
      laterTS = tsOne;
    }

    if (tsOne.getEndTime() >= tsTwo.getEndTime())
      endTime = tsOne.getEndTime();
    else
      endTime = tsTwo.getEndTime();

    TimeSeriesValue firstTSV = tsOne.getValueAt(0);
    firstTSV.setTimeSeriesName ("Merged->" + tsOne.getName() + " + " + tsTwo.getName());
    TimeSeries merged = new TimeSeries (firstTSV);

    int units = merged.getIntervalUnits (startTime, endTime);

    long nextTime = startTime;

    for (int i = 0; i < units; i++)
    {
      nextTime = nextTime + interval;


      //TODO: may want to consider cloning the values here.
      if (tsOne.isValidTime(nextTime) && tsOne.getValueAt(nextTime) != null)
      {
        TimeSeriesValue nextTSV = tsOne.getValueAt(nextTime);
        nextTSV.setTimeSeriesName ("Merged->" + tsOne.getName() + " + " + tsTwo.getName());
        df.addText ("new TSV to add to merged " + nextTSV.toString());
        merged.addElement(nextTSV);
      }
      else if (tsTwo.isValidTime(nextTime) && tsTwo.getValueAt(nextTime) != null)
      {
        TimeSeriesValue nextTSV = tsTwo.getValueAt(nextTime);
        nextTSV.setTimeSeriesName ("Merged->" + tsOne.getName() + " + " + tsTwo.getName());
        df.addText ("new TSV to add to merged " + nextTSV.toString());
        merged.addElement(nextTSV);
      }
    }

    merged.setName (newTSName);
    return merged;
  }

  public static void main(String[] args)
  {
    DebugFrame df = new DebugFrame ();
    df.show();
    df.setTitle ("TimeSeriesUtilities BlackBox testing");

    /****************** testing isClassNumeric (Class) *************************************/
    Double dbl = new Double (5);

    if (isClassNumeric(dbl.getClass()))
      df.addText ("1. Passed : isClassNumeric() identifies a numeric class");
    else
      df.addText ("1. Fail : isClassNumeric() does NOT identify a numeric class");

    String strg = new String ("string");
    if (!isClassNumeric(strg.getClass()))
      df.addText ("2. Passed : isClassNumeric() identifies a non-numeric class");
    else
      df.addText ("2. Fail : isClassNumeric() does NOT identify a non numeric class");

    /****************** testing addTimeSeriesValue (TimeSeriesValue, TimeSeriesValue) *******/
    TimeSeriesValue tsv1 = new TimeSeriesValue ("Test", new Double (5), 0 , 50);
    TimeSeriesValue tsv2 = new TimeSeriesValue ("Test", new Double (15), 0 , 50);

    try
    {
      Double sum = (Double) addTimeSeriesValues(tsv1, tsv2);

      if (sum == null)
        df.addText ("3. Failed : addTimeSeriesValues() failed to sum values; sum is null");

      if (sum.doubleValue() == 20)
        df.addText ("3. Passed : addTimeSeriesValues() correctly summed two elements");
      else
        df.addText ("3. Failed : addTimeSeriesValues() failed to sum values " );
    }
    catch (Exception e)
    {
      df.addText ("3. Failed : addTimeSeriesValues() --> exception " + e.getMessage() );
    } // end try


    try
    {
      Long sum = (Long) addTimeSeriesValues(tsv1, tsv2);

      df.addText ("4. Failed : addTimeSeriesValues() allowed miscasting " );
    }
    catch (ClassCastException e)
    {
      df.addText ("4. Passed : addTimeSeriesValues() --> Miscasting exception caught");
    } // end try

    tsv2 = new TimeSeriesValue ("Test", new String(""), 0,1);
    Double sum = (Double) addTimeSeriesValues(tsv1, tsv2);

    if (sum == null)
      df.addText ("5. Passed : addTimeSeriesValues() identified incompatible values; sum is null");
    else
      df.addText ("5. Failed : addTimeSeriesValues() did not identify incompatible values " );


    tsv2 = new TimeSeriesValue ("Test", new Long(4), 0,1);
    sum = (Double) addTimeSeriesValues(tsv1, tsv2);

    if (sum == null)
      df.addText ("6. Passed : addTimeSeriesValues() identified incompatible values; sum is null");
    else
      df.addText ("6. Failed : addTimeSeriesValues() did not identify incompatible values " );

    /******************* testing of replaceNulls (TimeSeries)*********************/
    TimeSeriesValue tsvn0 = new TimeSeriesValue ("Test", new Double (1), 0, 5);
    TimeSeriesValue tsvn1 = new TimeSeriesValue ("Test", new Double (2), 5, 5);
    TimeSeriesValue tsvn2 = new TimeSeriesValue ("Test", new Double (3), 10, 5);
    TimeSeriesValue tsvn3 = new TimeSeriesValue ("Test", new Double (4), 15, 5);

    // add these values to a time series
    TimeSeries tsn = new TimeSeries (tsvn0);
    tsn.addElement(tsvn1);
    tsn.addElement(tsvn2);
    tsn.addElement(tsvn3);

    // make the initial two elements null
    tsn.clearElement(0);
    tsn.clearElement(1);

    replaceNulls (tsn);

    if (tsn.size() == 2)
    {
      if ( ((Double)tsn.getValueAt(0).getValue()).doubleValue() == 3 &&
           ((Double)tsn.getValueAt (1).getValue()).doubleValue() == 4)
            df.addText ("7. Passed : replaceNulls() properly trimmed leading nulls from time series");
      else
        df.addText ("7. Failed : replaceNulls() did not trim leading nulls " );
    }
    else
      df.addText ("7. Failed : replaceNulls() did not trim leading nulls " );


    tsvn0 = new TimeSeriesValue ("Test", new Double (0), 0, 5);
    tsvn1 = new TimeSeriesValue ("Test", new Double (1), 5, 5);
    tsvn2 = new TimeSeriesValue ("Test", new Double (2), 10, 5);
    tsvn3 = new TimeSeriesValue ("Test", new Double (3), 15, 5);

    // add these values to a time series
    tsn = new TimeSeries (tsvn0);
    tsn.addElement(tsvn1);
    tsn.addElement(tsvn2);
    tsn.addElement(tsvn3);

    // make the final two elements null
    tsn.clearElement(2);
    tsn.clearElement(3);

    replaceNulls (tsn);

    if (tsn.size() == 2)
    {
      if ( ((Double)tsn.getValueAt(0).getValue()).doubleValue() == 0 &&
           ((Double)tsn.getValueAt (1).getValue()).doubleValue() == 1)
            df.addText ("8. Passed : replaceNulls() properly trimmed trailing nulls from time series");
      else
        df.addText ("8. Failed : replaceNulls() did not trim trailing nulls " );
    }
    else
      df.addText ("8. Failed : replaceNulls() did not trim trailing nulls " );


    tsvn0 = new TimeSeriesValue ("Test", new Double (1), 0, 5);
    tsvn1 = new TimeSeriesValue ("Test", new Double (2), 5, 5);
    tsvn2 = new TimeSeriesValue ("Test", new Double (11), 10, 5);
    tsvn3 = new TimeSeriesValue ("Test", new Double (4), 15, 5);

    // add these values to a time series
    tsn = new TimeSeries (tsvn0);
    tsn.addElement(tsvn1);
    tsn.addElement(tsvn2);
    tsn.addElement(tsvn3);

    // make the seceond element null
    tsn.clearElement(1);

    replaceNulls (tsn);

    if (tsn.size() == 4)
    {
      if ( ((Double)tsn.getValueAt(1).getValue()).doubleValue() == 6)
        df.addText ("9. Passed : replaceNulls() properly replaced a single null element");
      else
        df.addText ("9. Failed : replaceNulls() did not replace a null value " );
    }
    else
      df.addText ("9. Failed : replaceNulls() did not replace a null value " );


    tsvn0 = new TimeSeriesValue ("Test", new Double (1), 0, 5);
    tsvn1 = new TimeSeriesValue ("Test", new Double (2), 5, 5);
    tsvn2 = new TimeSeriesValue ("Test", new Double (11), 10, 5);
    tsvn3 = new TimeSeriesValue ("Test", new Double (19), 15, 5);

    // add these values to a time series
    tsn = new TimeSeries (tsvn0);
    tsn.addElement(tsvn1);
    tsn.addElement(tsvn2);
    tsn.addElement(tsvn3);

    // make the second and third element null
    tsn.clearElement(1);
    tsn.clearElement(2);

    replaceNulls (tsn);

    if (tsn.size() == 4)
    {
      if ( ((Double)tsn.getValueAt(1).getValue()).doubleValue() == 10 &&
           ((Double)tsn.getValueAt(2).getValue()).doubleValue() == 10)
        df.addText ("10. Passed : replaceNulls() properly replaced multiple nulls");
      else
        df.addText ("10. Failed : replaceNulls() did not replaced multiple null values " );
    }
    else
      df.addText ("10. Failed : replaceNulls() did not replaced multiple null values " );


    tsvn0 = new TimeSeriesValue ("Test", new Double (1), 0, 5);
    tsvn1 = new TimeSeriesValue ("Test", new Double (10), 5, 5);
    tsvn2 = new TimeSeriesValue ("Test", new Double (11), 10, 5);
    tsvn3 = new TimeSeriesValue ("Test", new Double (15), 15, 5);
    TimeSeriesValue tsvn4 = new TimeSeriesValue ("Test", new Double (21), 20, 5);

    // add these values to a time series
    tsn = new TimeSeries (tsvn0);
    tsn.addElement(tsvn1);
    tsn.addElement(tsvn2);
    tsn.addElement(tsvn3);
    tsn.addElement(tsvn4);

    tsn.clearElement(1);
    tsn.clearElement(3);

    replaceNulls(tsn);

//    df.addText (tsn.toString());
    if (tsn.size() == 5)
    {
      if ( ((Double)tsn.getValueAt(1).getValue()).doubleValue() == 6 &&
           ((Double)tsn.getValueAt(3).getValue()).doubleValue() == 16)
        df.addText ("11. Passed : getDifferencedTimeSeries() properly differenced input");
      else
        df.addText ("11. Failed : getDifferencedTimeSeries() did not difference properly " );
    }
    else
      df.addText ("11. Failed : getDifferencedTimeSeries() did not difference properly" );

    /*********************** testing getDifferencedTimeSeries (TimeSeries) *****************/
    tsvn0 = new TimeSeriesValue ("Test", new Double (1), 0, 5);
    tsvn1 = new TimeSeriesValue ("Test", new Double (10), 5, 5);
    tsvn2 = new TimeSeriesValue ("Test", new Double (11), 10, 5);
    tsvn3 = new TimeSeriesValue ("Test", new Double (15), 15, 5);

    // add these values to a time series
    tsn = new TimeSeries (tsvn0);
    tsn.addElement(tsvn1);
    tsn.addElement(tsvn2);
    tsn.addElement(tsvn3);

    TimeSeries diffTS = getDifferencedTimeSeries (tsn);

    if (diffTS.size() == 3)
    {
      if ( ((Double)diffTS.getValueAt(0).getValue()).doubleValue() == 9 &&
           ((Double)diffTS.getValueAt(1).getValue()).doubleValue() == 1 &&
           ((Double)diffTS.getValueAt(2).getValue()).doubleValue() == 4)
        df.addText ("12. Passed : getDifferencedTimeSeries() properly differenced input");
      else
        df.addText ("12. Failed : getDifferencedTimeSeries() did not difference properly " );
    }
    else
      df.addText ("12. Failed : getDifferencedTimeSeries() did not difference properly" );


   /******************** testing of calcTimeSeriesMean (TimeSeries) ***************/
    tsvn0 = new TimeSeriesValue ("Test", new Double (1), 0, 5);
    tsvn1 = new TimeSeriesValue ("Test", new Double (5), 5, 5);
    tsvn2 = new TimeSeriesValue ("Test", new Double (6), 10, 5);
    tsvn3 = new TimeSeriesValue ("Test", new Double (10), 15, 5);

    double actual = 5.5;

    // add these values to a time series
    tsn = new TimeSeries (tsvn0);
    tsn.addElement(tsvn1);
    tsn.addElement(tsvn2);
    tsn.addElement(tsvn3);

    Double meanDbl = calcTimeSeriesMean (tsn);
    double mean = meanDbl.doubleValue();
    double epsilon = .005;

//    df.addText("mean is :" + mean);

    if (mean <= (actual + epsilon) && mean >= (actual - epsilon))
      df.addText ("13. Passed : calcTimeSeriesMean() correctly calculated the time series mean");
    else
      df.addText ("13. Failed : calcTimeSeriesMean() did not correctly calculate the mean " );


    tsvn0 = new TimeSeriesValue ("Test", new Float (7.256), 0, 5);
    tsvn1 = new TimeSeriesValue ("Test", new Float (3.588), 5, 5);
    tsvn2 = new TimeSeriesValue ("Test", new Float (2.896), 10, 5);
    tsvn3 = new TimeSeriesValue ("Test", new Float (100.987), 15, 5);

    actual = 28.68175;

    // add these values to a time series
    tsn = new TimeSeries (tsvn0);
    tsn.addElement(tsvn1);
    tsn.addElement(tsvn2);
    tsn.addElement(tsvn3);

    meanDbl = calcTimeSeriesMean (tsn);
    mean = meanDbl.doubleValue();
    epsilon = .005;

 //   df.addText("mean is :" + mean);

    if (mean <= (actual + epsilon) && mean >= (actual - epsilon))
      df.addText ("14. Passed : calcTimeSeriesMean() correctly calculated the time series mean");
    else
      df.addText ("14. Failed : calcTimeSeriesMean() did not correctly calculate the mean " );


    /************************** testing of mergeTimeSeires (String, TimeSeries, TimeSeries) */
    tsvn0 = new TimeSeriesValue ("TestOne", new Double (10), 0, 5);
    tsvn1 = new TimeSeriesValue ("TestOne", new Double (10), 5, 5);
    tsvn2 = new TimeSeriesValue ("TestOne", new Double (10), 10, 5);
    tsvn3 = new TimeSeriesValue ("TestOne", new Double (10), 15, 5);

    TimeSeries tsOne = new TimeSeries (tsvn0);
    tsOne.addElement(tsvn1);
    tsOne.addElement(tsvn2);
    tsOne.addElement(tsvn3);

    tsvn4 = new TimeSeriesValue ("TestTwo", new Double (20), 15, 5);
    TimeSeriesValue tsvn5 = new TimeSeriesValue ("TestTwo", new Double (20), 20, 5);
    TimeSeriesValue tsvn6 = new TimeSeriesValue ("TestTwo", new Double (20), 25, 5);
    TimeSeriesValue tsvn7 = new TimeSeriesValue ("TestTwo", new Double (20), 30, 5);

    TimeSeries tsTwo = new TimeSeries (tsvn4);
    tsTwo.addElement(tsvn5);
    tsTwo.addElement(tsvn6);
    tsTwo.addElement(tsvn7);

    TimeSeries merged = mergeTimeSeries ("mergedTS", tsOne, tsTwo);

    if (merged == null || merged.isEmpty())
      df.addText ("15. Failed : Merged Time Series is Null or empty.");

    df.addText (merged.toString());

  } // end main
} // end class