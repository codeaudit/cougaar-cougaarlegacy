/**
 *  @file         PeriodicEvent.java
 *  @copyright    Copyright (c) 2001
 *  @author       Chris Henney & Abraham DeLaO
 *  @company      21st Century Technologies, Inc
 *  @description
 *  @history      Created August 2000.
 *  @todo
 **/

package com.centurylogix.timeSeries.predictiveAnalysis;

import java.util.ArrayList;

// a periodic event should be a list of time-series values. Some elements in the list may be null.
public class PeriodicEvent
{
  private int offset = 0; // time from start to first leading edge.
  private ArrayList event = null;
  private int periodLength = 0; // measured from leading edge to leading edge

  public PeriodicEvent()
  {
  }

  public void setOffset (int inputOffset)
  {
    this.offset = inputOffset;
  }

  public void setEvent (ArrayList inputEvent)
  {
    this.event = inputEvent;
  }

  public void setPeriod (int period)
  {
    this.periodLength = period;
  }

  public int getOffset ()
  {
    return this.offset;
  }

  public ArrayList getEvent ()
  {
    return this.event;
  }

  public int getEventLength ()
  {
    return this.event.size();
  }

  public int getPeriod ()
  {
    return this.periodLength;
  }

  public boolean isValid ()
  {
    if (event != null && periodLength > 0 && offset > 0)
      return true;

    else
      return false;
  } // end boolean isValid()

  public String toString ()
  {
    StringBuffer sb = new StringBuffer (128);
    sb.append ("Periodic Event Data Structure \n");
    sb.append (" Event : " + this.event + "\n");
    sb.append (" First occurence offset from start : " + this.offset + "\n");

    if (event != null)
      sb.append (" Event Length : " + this.event.size() + "\n");
    else
      sb.append (" Event Length : 0 \n");
    sb.append (" Period : " + this.periodLength);

    String s = sb.toString();

    return s;
  }

  public static void main(String[] args)
  {

  }

}