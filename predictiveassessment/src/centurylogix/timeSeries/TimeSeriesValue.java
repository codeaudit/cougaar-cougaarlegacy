/**
 *  @file         TimeSeriesValue.java
 *  @copyright    Copyright (c) 2001
 *  @author       Abraham DeLaO
 *  @company      21st Century Technologies, Inc
 *  @description  Once created, a TimeSeriesValue object is an immutable representation for a
 *                time series element.  There are no restrictions on the type of element that
 *                can be used to populate this object.  A TimeSeriesValue will be linked with
 *                others of the same type to form a TimeSeries. Each time series object must
 *                be linked to a time series name when it is created.  This name will be used to
 *                group related TimeSeriesValues into TimeSeries objects.
 *  @see          TimeSeries
 *  @history      Created June 5, 2001.
 *  @todo         1. add toXML() method and create supporting DTD.
 *
 **/
package com.centurylogix.timeSeries;

import com.centurylogix.finalPredictiveAssessor.DebugFrame;
import org.cougaar.core.cluster.Publishable;
import org.cougaar.core.util.XMLizable;
import java.io.Serializable;

public class TimeSeriesValue implements Publishable, Serializable, XMLizable
{
  private Object value;             // the actual value of the time series element
  private long startTime;           // start time at which this time series value is valid
  private long reportTime;          // the time at which this value was created
  private String tsName;            // name of the time series to which this value belongs
  private long timeIncrement;       // the time increment between time series samplings

  /**
   * Only constructor for this object.
   * @param   reportedValue   This is the value of this time series element.
   * @param   timeSeriesName  The name of the time series to which this value belongs
   * @param   timeOfReport    The time at which this value was generated.
   * @param   increment       The unit of time that time series sampling is occuring at
   */
  public TimeSeriesValue(String seriesName, Object reportValue, long timeOfReport, long increment)
  {
    this.value = reportValue;
    this.tsName = seriesName;
    this.reportTime = timeOfReport;
    this.startTime = timeOfReport;
    this.timeIncrement = increment;
  }

  /**
   *  Returns the Java class of this objects value.
   *  @return  The Java class of this time series element
   */
  public Class getValueClass ()
  {
    return this.value.getClass();
  }

  /**
   * Returns the Java class name for this time series elements value. For instance if the
   *  this time series value would return strings like : "Double", "Integer", "String"
   * @return  The name of the class of this time series element.
   */
  public String getClassName ()
  {
    return this.value.getClass().getName();
  }

  /**
   * Returns the name of the time series with which this value is associated.
   * @return A string representing the name of the time series to which this value belongs.
   */
  public String getTimeSeriesName ()
  {
    return this.tsName;
  }

  /**
   * Changes the time series to which this value belongs. This is useful when merging or
   * combining two time series to create a new one.
   * @param   newName   The name of the new time series to which this value now belongs.
   */
  public void setTimeSeriesName (String newName)
  {
    this.tsName = newName;
  }

  /**
   * Retrievs the value for this time series element. The original Java class of the object has
   *  been lost since all time series elements are represented as objects.
   * @return  The value of this time series element.
   */
  public Object getValue ()
  {
    return this.value;
  }

  /**
   * Returns the time increment between data samples in this time series.
   * @return  The time in milliseconds between time series samples.
   */
   public long getTimeIncrement()
   {
    return this.timeIncrement;
   }

  /**
   * Returns the time at which this time series value was reported.
   * @return  The time in milliseconds this time series value was created.
   */
  public long getReportTime ()
  {
    return this.reportTime;
  }

  /**
   * Returns the start time for this time series element. Implments the method specified in the
   *  TimeSpan interface.
   * @return  The start time in milliseconds for this time series element.
   */
  public long getStartTime ()
  {
    return this.startTime;
  }

  /**
   * Sets the start time for this value to the input time. The start time is the beginning
   *  period of time for which the TimeSeriesValue pertains.  The TimeSeriesValue is no
   *  longer valid at <tt> newStartTime + timeIncrement </tt>.
   *  @param  newStartTime  The first time in milliseconds for which this value is valid.
   */
  public void adjustStartTime (long newStartTime)
  {
    this.startTime = newStartTime;
  }

  /**
   * Determines if two TimeSeriesValues have the same underlying value. Their values must be of the
   *  same class and be equal in magnitude.
   *  @param  tsv   The object whose value is to be compared against this one.
   *  @returns      True if the two elements have the same value, false otherwise
   */
  public boolean sameValueAs (TimeSeriesValue tsv)
  {
    Class inputClass = tsv.getValueClass();
    Class thisClass = this.value.getClass();

    //if the values are of the same class and equal in value, we will consider them equal
    if (thisClass.equals (inputClass))
    {
      if (this.value.equals (tsv.getValue()))
        return true;
    }
    return false;
  } //end sameValueAs (TimeSeriesValue)


  /**
   * Determines if two TimeSeriesValues are equal. In order to be equal the time series elememnts
   *  must be of the same class and have identical values and startTime and timeIncrement.
   *  @param  tsv   The object to be compared against this one.
   *  @returns      True if the two elements are the same, false otherwise
   */
  public boolean equals (TimeSeriesValue tsv)
  {
    Class inputClass = tsv.getValueClass();
    Class thisClass = this.value.getClass();

    //if the values are of the same class and equal in value, we will consider them equal
    if (thisClass.equals (inputClass))
    {
      if (this.value.equals (tsv.getValue()) &&
         (this.startTime == tsv.getStartTime()) &&
         (this.timeIncrement == tsv.getTimeIncrement()) &&
         (this.tsName == tsv.getTimeSeriesName()))
        return true;
    }

    return false;
  } //end equals ()


  /**
   * This method is included in order to satisify the Publishabe interface. Implementing this
   *  interface is required for this object to be published to the Blackboard.
   *  TODO: Figure out what persistable means?, this object is proabably not persistable since
   *    it is likely not worth trying to save whatever values exist in the Blackboard.
   */
  public boolean isPersistable ()
  {
    return false;
  }


  public org.w3c.dom.Element getXML(org.w3c.dom.Document doc)
  {
    return org.cougaar.core.util.XMLize.getPlanObjectXML(this,doc);
  }

  /**
   * This method returns a new instance of TimeSeriesValue with all properties identical to this one.
   * @return  The new TimeSeriesValue instance.
   */
  public Object clone ()
  {
    TimeSeriesValue ret = new TimeSeriesValue (this.tsName, this.value,
                                               this.reportTime, this.timeIncrement);
    ret.adjustStartTime(this.startTime);
    return ret;
  }

  /**
   * Returns a string representation of this time series value.
   * @return  A String rep. of this object.
   */
  public String toString ()
  {
    StringBuffer buffer = new StringBuffer (128);
    buffer.append ("\n\t Time Series Value String Rep -------->>");
    buffer.append ("\n\t\t Time series name : " + this.getTimeSeriesName());
    buffer.append ("\n\t\t Java Class name of time series value : " + this.getClassName());
    buffer.append ("\n\t\t Time Series Value : " + this.value.toString());
    buffer.append ("\n\t\t Start Time of Value : " + this.startTime);
    buffer.append ("\n\t\t Report Time for Value : " + this.reportTime);
    buffer.append ("\n\t\t Time Increment :" + this.timeIncrement +"\n");

    String stringRep = buffer.toString();
    return stringRep;
  }



  public static void main(String[] args)
  {
    DebugFrame df = new DebugFrame ();
    df.show();
    df.setTitle ("TimeSeriesValue BlackBox testing");

    Double dbl = new Double (5);
    TimeSeriesValue tsv = new TimeSeriesValue("Test", dbl, 100, 50);

    df.addText ("BlackBox Testing using : " + tsv.toString());

    // ********** testing getValueClass() **************************/
    if (tsv.getValueClass().equals (dbl.getClass()))
      df.addText ("Pass : getValueClass() returns Class successfully");
    else
      df.addText ("Fail : getValueClass() does NOT return Class successfully");

    // ********** testing getClassName() ***************************/
    if (tsv.getClassName().equals (dbl.getClass().getName()))
      df.addText ("Pass : getClassName() returns Class name successfully");
    else
      df.addText ("Fail : getClassName() does NOT return Class name successfully");

    // ********** testing getValue() *******************************/
    if (tsv.getValue().equals (dbl))
      df.addText ("Pass : getValue() returns value successfully");
    else
      df.addText ("Fail : getValue() does NOT return value successfully");

    // ********** testing getTimeSeriesName() ***************************/
    if (tsv.getTimeSeriesName().equals ("Test"))
      df.addText ("Pass : getTimeSeriesName() returns name successfully");
    else
      df.addText ("Fail : getTimeSeriesName() does NOT return name successfully");

    // ********** testing getStartTime() ***************************/
    if (tsv.getStartTime() == 100)
      df.addText ("Pass : getStartTime() returns correct start time successfully");
    else
      df.addText ("Fail : getStartTime() does NOT return correct start time successfully");

    // ********** testing getTimeIncrement() ***************************/
    if (tsv.getTimeIncrement() == 50)
      df.addText ("Pass : getTimeIncrement() returns correct time increment successfully");
    else
      df.addText ("Fail : getTimeIncrement() does NOT return correct time increment successfully");

    // ********** testing sameValueAs() ***************************/
    TimeSeriesValue tsv1 = new TimeSeriesValue ("Test1", new Double (10), 100, 50);
    if (!tsv.sameValueAs (tsv1))
      df.addText ("Pass : sameValueAs() identifies different value successfully : " + 10);
    else
      df.addText ("Fail : sameValueAs() does NOT identify different value successfully : " + 10);

    tsv1 = new TimeSeriesValue ("Test1", new String ("String"), 100, 50);
    if (!tsv.sameValueAs (tsv1))
      df.addText ("Pass : sameValueAs() identifies different value successfully : " + "String");
    else
      df.addText ("Fail : sameValueAs() does NOT identify different value successfully : " + "String");

    tsv1 = new TimeSeriesValue ("Test1", new Double (5), 100, 50);
    if (tsv.sameValueAs (tsv1))
      df.addText ("Pass : sameValueAs() identifies same value successfully ");
    else
      df.addText ("Fail : sameValueAs() does NOT identify same value successfully");

    // ********** testing equals() *********************************/
    TimeSeriesValue tsv2 = new TimeSeriesValue ("Test", new Double (5), 100, 50);
    if (tsv.equals (tsv2))
      df.addText ("Pass : equals() compares object successfully with itself");
    else
      df.addText ("Fail : equals() does NOT compare object successfully with itself");

    tsv2 = new TimeSeriesValue ("Test", new Double (20), 100, 50);
    if (!tsv.equals (tsv2))
      df.addText ("Pass : equals() properly compares when values are different: " + new Double (20));
    else
      df.addText ("Fail : equals() does NOT properly compare when values are different : " + new Double (20));

    tsv2 = new TimeSeriesValue ("Test", new String ("a String"), 100, 50);
    if (!tsv.equals (tsv2))
      df.addText ("Pass : equals() properly compares when values are different : " + new String ("a String"));
    else
      df.addText ("Fail : equals() does NOT properly compare when values are different : " + new String ("a String"));

    tsv2 = new TimeSeriesValue ("NOT-Test", new Double (5), 100, 50);
    if (!tsv.equals (tsv2))
      df.addText ("Pass : equals() properly compares when time series are different : " + "NON-Test");
    else
      df.addText ("Fail : equals() does NOT properly compare when time series are different : " + "NON-Test");

    tsv2 = new TimeSeriesValue ("Test", new Double (5), 10, 50);
    if (!tsv.equals (tsv2))
      df.addText ("Pass : equals() properly compares when start times are different : " + 10);
    else
      df.addText ("Fail : equals() does NOT properly compare when start times are different : " + 10);

    tsv2 = new TimeSeriesValue ("Test", new Double (5), 100, 15);
    if (!tsv.equals (tsv2))
      df.addText ("Pass : equals() properly compares when timeIncrements are different : " + 15);
    else
      df.addText ("Fail : equals() does NOT properly compare when timeIncrements are different : " + 15);

  }// end main

} //end class TimeSeriesValue
