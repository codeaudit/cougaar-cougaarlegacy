/**
 *  @file         ChartingFrameDataModel.java
 *  @copyright    Copyright (c) 2001
 *  @author       Abraham DeLaO & Darrin Taylor
 *  @company      21st Century Technologies, Inc
 *  @description
 *  @history      First interface created Fall 1999 for ALP. Heavily modified June 2001 for UltraLog
 *  @todo
 **/

package com.centurylogix.timeSeries.jChartDisplay;

import java.util.HashMap;
import java.util.ArrayList;
import java.util.Iterator;

import com.centurylogix.timeSeries.*;
import com.centurylogix.ultralog.DebugFrame;

import java.util.*;

public class ChartingFrameDataModel
{
  private ChartingFrame myFrame = null;
  private HashMap tsGroups = new HashMap (10);
  private String currentlyShownGroup = null;
  private String currDataString = null;

  private Integer startIndex = new Integer (0);
  private Integer unitsToView = null;
  private long timeIncrement = 0;         //for now assume time increment same for all time series
  private long currStartTime = 0;         // same for start time.

  private static final String emptyDataString = new String ("ARRAY 'No Data' 1 5 \n" +
                                                        "'X-axis Values' 0.0 1.0 2.0 3.0 4.0 5.0 \n" +
                                                        "'Time Series Values' 0 0 0 0 0 0 \n");

  DebugFrame df = new DebugFrame (false);

  /* Initializes the class with the particular chart we are interfacing with */
  public ChartingFrameDataModel(ChartingFrame frame)
  {
    this.myFrame = frame;
    frame.setModel (this);

    df.setTitle("ChartingFrameMode Debug Frame");
    df.setBounds(50, 50, 400, 400);
    df.show();
  }

  public void addTimeSeriesGroup (TimeSeries[] tsArray, String groupName)
  {
    if (tsArray == null || tsArray.length == 0)
      return;

    if (timeIncrement == 0)
    {
      TimeSeries ts = tsArray [0];
      this.timeIncrement = ts.getTimeIncrement();
      df.addText ("setting time increment to :" + this.timeIncrement);
    }
/*
    StringBuffer nameBuff = new StringBuffer (32);
    for (int i = 0; i < tsArray.length; i++)
    {
      TimeSeries ts = tsArray [i];
      nameBuff.append (ts.getName());
    }

    String groupName = nameBuff.toString();
*/
    if (tsGroups.containsKey (groupName))
    {
      TimeSeriesGroup group = (TimeSeriesGroup)tsGroups.get(groupName);
      group.updateTimeSeries (tsArray);

      TimeSeries newTS = (TimeSeries) tsArray [0];
      //df.addText ("This is the new TS : " + newTS.toString());
      tsGroups.put (groupName, group);

      if (groupName.equals (currentlyShownGroup))
      {
        df.addText ("found an update for the currently shown chart ");
        setCurrDataString (getCurrentGroup());
        myFrame.refreshChart();

      }
    }
    else
    {
      df.addText ("is new ts, create group and add to hash : " + groupName);
      TimeSeriesGroup newTSGroup = new TimeSeriesGroup (tsArray, groupName);
      tsGroups.put (groupName, newTSGroup);

      if (this.currentlyShownGroup == null)
      {
        df.addText("no showt chart, make this one the shown one");
        this.currentlyShownGroup = groupName;
        setCurrDataString (newTSGroup);
        myFrame.refreshChart();
        myFrame. createCheckBoxes();
        df.addText ("refreshing chart");
      }
      else if (newTSGroup.groupName == this.currentlyShownGroup)
      {
      df.addText ("don't think this is possible");
        myFrame.refreshChart();
      }
      // add this group to then tree.

      myFrame.addAnalysisGroup (groupName);
    }

  }// end public void addTimeSeriesGroup (TimeSeries[])


  protected void setStartIndex (int index)
  {
    df.addText ("Want to view starting from : " + index);
    this.startIndex = new Integer (index);
     setCurrDataString (getCurrentGroup());
  }

  protected void setUnitsToView(int index)
  {
    df.addText ("Want to view " + index + " number of units");
    this.unitsToView = new Integer (index);
    setCurrDataString (getCurrentGroup());
  }

  protected void setFullChartViewIndices()
  {
    this.startIndex = new Integer (0);
    this.unitsToView = null;
    setCurrDataString (getCurrentGroup());
  }

  // TODO: make sure chartName is valid.
  protected void setCurrentGroup (String chartName)
  {
    df.addText ("Setting current group to : " + chartName);
    if (chartName == this.currentlyShownGroup)
      return;

    this.currentlyShownGroup = chartName;
    this.startIndex = new Integer (0);
    this.unitsToView = null;

    TimeSeriesGroup currGroup = (TimeSeriesGroup) tsGroups.get(chartName);
    if (currGroup == null)
      df.addText ("Cant set new current group, chart name not recognized");
    setCurrDataString (currGroup);
    //myFrame.clearCheckBoxes();
   // myFrame. createCheckBoxes();
  }

  protected long getIncrement()
  {
    return this.timeIncrement;
  }

  protected Integer getStartIndex ()
  {
    return this.startIndex;
  }

  protected Integer getUnitsToView ()
  {
    return this.unitsToView;
  }


  protected int getStartIndexToView ()
  {
    int ret = 0;

    if (this.startIndex != null)
      ret = this.startIndex.intValue();
    else
      ret = 0;

    return ret;
  }

  protected int getEndIndexToView ()
  {
    int ret = 0;

    if (this.unitsToView != null)
      ret = this.unitsToView.intValue();
    else
    {
      TimeSeriesGroup tsg = getCurrentGroup();
      ret = tsg.getMaxSize();
    }

    return ret;
  }

  protected long getStartTime ()
  {
    TimeSeriesGroup tsg = getCurrentGroup();
    long start = tsg.getStartTime();
    return start;
  }

  protected String getChartString()
  {
    //df.addText (this.currDataString);
    return this.currDataString;
  }

  protected void clear ()
  {
    // this sets current graph data to view be empty.
    // this done by setting Curr String dat ver so empty data, if/when new data is recieved it will
      // replace this empty data string. some refreshing mechanism will be necessary to
        // let the frame know that new data has arrived and that it should request to
        // be refreshed. or we can just call for the refresh ourselves....BUT, we must remember
        // to update the current graph data when new data is recieved for the currently
        // shown graph.
  }

  protected void pause ()
  {}

  protected void restart ()
  {}

  private TimeSeriesGroup getCurrentGroup ()
  {
    TimeSeriesGroup currGroup = (TimeSeriesGroup) tsGroups.get(this.currentlyShownGroup);
    return currGroup;
  }

  private int getViewSize (TimeSeriesGroup tsg)
  {
    int size = 0;

    if (this.unitsToView == null)
      size = tsg.getFullSpanSize();
    else
      size = this.unitsToView.intValue();

    return size;
  }

  private void setCurrDataString (TimeSeriesGroup tsg)
  {
    String myTitle = tsg.groupName;
    int timeStreams = tsg.tsList.size();
    int viewSize = getViewSize(tsg); // this takes care of null possiblity and returns a valid value

    df.addText ("new view size is : " + viewSize);
    if(viewSize == 0)
    {
      this.currDataString = this.emptyDataString;
      return;
    }

    StringBuffer myData = new StringBuffer();

    myData.append("ARRAY ").append("'").append(myTitle).append("' ").append(timeStreams);
    myData.append(" ").append(viewSize);

    myData.append("\n").append("'").append( new String ("X-axis Values") ).append("' ");

    int startIndex = this.startIndex.intValue();

    long startGraphTime = tsg.getStartGraphTime (startIndex);
    long timeIncrement = tsg.getIncrement();

    /// these indices may need to be zero based
    for(int j= startIndex ; j < (viewSize + startIndex); j++)
    {
          String index = (new Integer(j)).toString();
          myData.append(index).append(" ");
    }

    for(int i=0; i < tsg.tsList.size() ; i++)
    {
      TimeSeries ts = (TimeSeries)tsg.tsList.get(i);
      myData.append("\n").append("'").append( ts.getName() ).append("' ");

      for(int j=0; j < viewSize; j++)
      {
        try {
          TimeSeriesValue tsv = ts.getValueAt(startGraphTime + (timeIncrement * j));

          if (tsv.getValue() !=  null)
          {
            Number val = (Number) tsv.getValue();
            myData.append(val.toString()).append(" ");
          }
        }
        catch (Exception e)
        {
          myData.append("-1 ");
        }
      }
    } // end for (i)

    String dataString = myData.toString();
    this.currDataString = new String (dataString);;

  } // end setCurrDataString


  class TimeSeriesGroup
  {
    ArrayList tsList = new ArrayList ();
    String groupName;

    protected TimeSeriesGroup (TimeSeries[] tsArray, String name)
    {
      this.groupName = name;

      for (int i = 0; i < tsArray.length; i++)
      {
        TimeSeries ts = (TimeSeries) tsArray [i];
        tsList.add (ts);
      }
    }

    protected void updateTimeSeries (TimeSeries[] tsArray)
    {
      tsList.clear();

      for (int i = 0; i < tsArray.length; i++)
      {
        TimeSeries ts = (TimeSeries) tsArray [i];
        tsList.add (ts);
      }
    }

    protected long getIncrement()
    {
      long timeIncrement = ((TimeSeries)tsList.get(0)).getTimeIncrement();
      return timeIncrement;
    }

  // return the earliest start time
    protected long getStartTime ()
    {
      long bestStart = Long.MAX_VALUE;
      long thisStart = 0;

      for (int i = 0; i < tsList.size(); i++)
      {
        TimeSeries ts = (TimeSeries) tsList.get(i);
        thisStart = ts.getStartTime();

        if (thisStart < bestStart)
          bestStart = thisStart;
      }

      return bestStart;

    }

    /**
     * Returns the latest end time for all time series in the group.
     */
    protected long getEndTime ()
    {
      long bestEnd = 0;
      long thisEnd = 0;

      for (int i = 0; i < tsList.size(); i++)
      {
        TimeSeries ts = (TimeSeries) tsList.get(i);
        thisEnd = ts.getEndTime();

        if (thisEnd > bestEnd)
          bestEnd = thisEnd;
      }

      return bestEnd;
    }

    protected int getFullSpanSize ()
    {
      long timeIncrement = ((TimeSeries)tsList.get(0)).getTimeIncrement();
      long firstStart = getStartTime ();
      long lastValueTime = getEndTime () - timeIncrement;

      int span = (int)( (lastValueTime - firstStart) / timeIncrement);

      return span;
    }

    protected long getStartGraphTime (int startIndex)
    {
      long timeIncrement = ((TimeSeries)tsList.get(0)).getTimeIncrement();
      long bestStart = getStartTime ();

      long startGraphTime = bestStart + (timeIncrement * startIndex);

      return startGraphTime;
    }

    protected int getMaxSize ()
    {
      int bestLength = 0;
      int thisLength = 0;

      for (int i = 0; i < tsList.size(); i++)
      {
        TimeSeries ts = (TimeSeries) tsList.get(i);
        thisLength = ts.size();

        if (thisLength > bestLength)
          bestLength = thisLength;
      }

      return bestLength;
    }
  } // end inner class TimeSeriesGroup

}// end class ChartingFrameDataModel

