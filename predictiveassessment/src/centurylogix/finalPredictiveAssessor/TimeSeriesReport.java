/**
 *  @file         TimeSeriesReport.java
 *  @copyright    Copyright (c) 2001
 *  @author       Abraham DeLaO
 *  @company      21st Century Technologies, Inc
 *  @history      Created June 20, 2001.
 *  @description
 *  @todo
 *
 **/

package com.centurylogix.finalPredictiveAssessor;

import org.cougaar.domain.planning.ldm.plan.Plan;
import org.cougaar.domain.planning.ldm.plan.Directive;
import org.cougaar.core.cluster.ClusterIdentifier;
import org.cougaar.core.cluster.Subscriber;
import org.cougaar.core.society.UID;
import org.cougaar.core.society.UniqueObject;
//import org.cougaar.core.cluster.ActiveSubscriptionObject;
import org.cougaar.util.TimeSpan;
import org.cougaar.core.util.XMLizable;
import java.io.Serializable;

import com.centurylogix.timeSeries.*;

public class TimeSeriesReport implements Directive, TimeSpan, Serializable, UniqueObject, XMLizable
{
  TimeSeries ts = null;
  Plan plan = null;
  ClusterIdentifier source = null;
  ClusterIdentifier destination = null;

  int count = 0;
  UID uid = null;

  public TimeSeriesReport(TimeSeries tsToRpt, Plan reportPlan,
                          ClusterIdentifier src, ClusterIdentifier dest)
  {
    //super();
    this.ts = tsToRpt;
    this.plan = reportPlan;
    this.source = src;
    this.destination = dest;
  }

  /** Returns an object that represents the plan that this directive is in reference to.  All Tasks
    * are members of a Plan.
    * @return   The Plan to which this Directive pertains.
    **/
  public Plan getPlan()
  {
    return this.plan;
  }

  /**
    * @return ClusterIdentifier Identifies the originator of this message
    */
  public ClusterIdentifier getSource()
  {
    return this.source;
  }

  /*
   *@return ClusterIdentifier Identifies the receiver of the message
   */
  public ClusterIdentifier getDestination()
  {
    return this.destination;
  }

  public long getStartTime ()
  {
    return ts.getStartTime();
  }

  public long getEndTime ()
  {
    return ts.getEndTime();
  }

  public String getTimeSeriesName()
  {
    return ts.getName();
  }

  public TimeSeries getTimeSeries()
  {
    return ts;
  }

  public boolean isPersistable ()
  {
    return true;
  }

  public UID getUID ()
  {
   count++;
    return new UID ("TimeSeriesReport", count);
  }

  public void setUID (UID newUID)
  {
    this.uid = newUID;
  }

  /*
  public boolean addingToLogPlan (Subscriber sub)
  {
    return true;
  }

  public boolean changingInLogPlan (Subscriber sub)
  {
    return true;
  }

  public boolean removingFromLogPlan (Subscriber sub)
  {
    return true;
  }
*/

  public org.w3c.dom.Element getXML(org.w3c.dom.Document doc)
  {
    return org.cougaar.core.util.XMLize.getPlanObjectXML(this,doc);
  }


  public String toXML ()
  {
   StringBuffer sb = new StringBuffer (64);

   sb.append( "<?xml version=\"1.0\" encoding=\"US-ASCII\"?>" + "\n");
   sb.append( "<!DOCTYPE " + "Time Series Value" + ">\n");

    /*
    // cycle through list of pairs, entring each into XML document
    while (iter.hasNext())
    {
      Map.Entry entry = (Map.Entry) iter.next();

      String name = (String) entry.getKey();
      String value = (String) entry.getValue();

      sb.append( "<AssetDetails ");
      sb.append( "Name = \"" + name + "\" ");
      sb.append( "Value = \"" + value + "\"/>\n");
    } // end while
*/
    //sb.append ("</Criteria>\n");
    String stringRep = sb.toString();
    return stringRep;

  }// end toXML ()


  public String toString ()
  {
    StringBuffer buffer = new StringBuffer (64);
    buffer.append ("\n\t Time Series Report ->>");
    buffer.append ("\n\t Time Series Name ->> " + getTimeSeriesName());
    buffer.append ("\n\t Time Series Start Time ->> " + getStartTime());
    buffer.append ("\n\t Time Series End Time ->> " + getEndTime());
    buffer.append ("\n\t Report Source ->> " + getSource().toAddress());
    buffer.append ("\n\t Report Destination ->> " + getDestination().toAddress());
    buffer.append ("\n\t Plan ->> " + plan.getPlanName());

    return buffer.toString();
  }
}// end class