/* * * * * * * * * * * * * * * * * * * * * * * * * * * * *
 *
 * Copyright (c) 2000-2001 PRC Inc., a wholly-owned
 *   subsidiary of Northrop Grumman Corporation.
 *
 *   This software may be used only in accordance
 *   with the Cougaar Open Source License Agreement. 
 *   See http://www.cougaar.org/documents/license.html
 *   or the www.cougaar.org Web site for more information.
 *   All other rights reserved to PRC Inc.
 *
 * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
 *
 * Author:  Brandon L. Buteau
 *
 */

package com.prc.alp.liaison.weather;

import java.io.File;
import java.io.FileInputStream;
import java.io.PrintStream;
import java.util.*;
import java.text.SimpleDateFormat;

import org.cougaar.core.cluster.Subscription;
import org.cougaar.domain.planning.ldm.plan.*;
import org.cougaar.lib.planserver.*;
import org.cougaar.util.*;
import org.cougaar.domain.glm.ldm.*;
import org.cougaar.domain.glm.ldm.plan.*;


/**
 * An ALP/Cougaar PSP (Plan Service Provider) that provides an HTML-based GUI
 * display of the impact of weather forecasts on planned transportation tasks.
 *
 * @author Brandon L. Buteau
 * @version 1.1
 * @since 1.0
 */
public class PSP_WeatherImpact 
  extends PSP_BaseAdapter implements PlanServiceProvider, UISubscriber {
    
  private PSPState myState;
  private String rqmtTaskUID;
  private Collection forecastTasks;
  private Collection forecastAllocations;
 
  public PSP_WeatherImpact() {
    super();
  }

  public PSP_WeatherImpact (String pkg, String id) 
    throws RuntimePSPException {
    setResourceLocation(pkg, id);
  }

  public boolean test(HttpInput query_parameters, PlanServiceContext sc) {
    super.initializeTest(); // IF subclass off of PSP_BaseAdapter.java
    return false;  // Only accessed by direct reference.
  }

  public void execute (PrintStream out, HttpInput query_parameters,
                       PlanServiceContext psc, PlanServiceUtilities psu ) 
    throws Exception
  {

    myState = new PSPState(this, query_parameters, psc);
    String rqmt = (String) query_parameters.getFirstParameterToken("RQMT",'=');
    
    forecastTasks = search(getWeatherForecastTasks());
    forecastAllocations = search(getWeatherForecastAllocations());
    
    if ( rqmt == null )
      displaySummaryPage(out);
    else
      displayTaskPage(out, rqmt);
    return;
    
  }

  public boolean returnsXML() {
    return false;
  }

  public boolean returnsHTML() {
    return true;
  }

  public String getDTD()  {
    return null;
  }
  
  public void subscriptionChanged (Subscription subscription) {
    // don't care!
  }
  
  private void displaySummaryPage (PrintStream out) {
    out.print(
      "<HTML><BODY>\n" +
      "<H1 align=\"center\">Weather Impact Summary</H1>\n");
    displaySummaryTableContents(out);
    out.print("</BODY></HTML>\n");
  }

  private void displaySummaryTableContents (PrintStream out) {
    out.print("<TABLE border=1 cellPadding=1 cellSpacing=1 align=center" +
              " width=85% bordercolordark=#660000 bordercolorlight=#cc9966>\n");
    displaySummaryTableHeader(out);
    Iterator it = forecastAllocations.iterator();
    while ( it.hasNext() ) {
      Task t = ((Allocation) it.next()).getTask();
      displaySummaryTableRow(out, (MPTask) t);
    }
    out.print("</TABLE>\n");
  }
  
  private void displaySummaryTableHeader (PrintStream out) {
    out.print("<TR>\n<TD align=center width=\"25%\"><B>Forecast Location</B></TD>\n");
    out.print("<TD align=center width=\"20%\"><B>Forecast Date</B></TD>\n");
    out.print("<TD align=center width=\"13%\"><B>Conditions</B></TD>\n");
    out.print("<TD align=center width=\"12%\"><B>Impact (minutes)</B></TD>\n");
    out.print("<TD align=center><B>Prepare for Transport Tasks</B></TD>\n</TR>\n");
  }
  
  private void displaySummaryTableRow (PrintStream out, MPTask t) {
    int pcount = getParentCount(t);
    out.print("<TR>\n<TD align=center width=\"25%\" rowspan=\"" + pcount + "\">");
    displayForecastTaskLocation(out, t);
    out.print("</TD>\n<TD align=center width=\"20%\" rowspan=\"" + pcount + "\">");
    displayForecastTaskDate(out, t);
    out.print("</TD>\n<TD align=center width=\"13%\" rowspan=\"" + pcount + "\">");
    displayForecastTaskCondition(out, t);
    out.print("</TD>\n<TD align=center width=\"12%\" rowspan=\"" + pcount + "\">");
    displayForecastTaskImpact(out, t);
    out.print("</TD>\n");
    displayTransportTasks(out, t);
  }
  
  private void displayForecastTaskLocation (PrintStream out, MPTask t) {
    PrepositionalPhrase pp = t.getPrepositionalPhrase(Constants.Preposition.FOR);
    out.print(((GeolocLocationImpl) pp.getIndirectObject()).getName());
  }
  
  private void displayForecastTaskDate (PrintStream out, MPTask t) {
    PrepositionalPhrase pp = t.getPrepositionalPhrase(WConstants.FORECAST_DATE_PREPOSITION);
    out.print((String) pp.getIndirectObject());
  }
  
  private void displayForecastTaskCondition (PrintStream out, MPTask t) {
    
    Iterator allocs = forecastAllocations.iterator();
    while ( allocs.hasNext() ) {
      Allocation alloc = (Allocation) allocs.next();
      if ( alloc.getTask() == t ) {
        AllocationResult result = alloc.getReportedResult();
        if ( result != null ) {
          int condition = (int) result.getValue(WConstants.CONDITION);
          out.print(WConstants.RETSINAConditions[condition]);
          return;
        }
      }
    }    
  }
  
  private void displayForecastTaskImpact (PrintStream out, MPTask t) {
    
    Iterator allocs = forecastAllocations.iterator();
    while ( allocs.hasNext() ) {
      Allocation alloc = (Allocation) allocs.next();
      if ( alloc.getTask() == t ) {
        AllocationResult result = alloc.getReportedResult();
        if ( result != null ) {
          out.print(Math.round(WConstants.weatherImpact(result) / (60.0 * 1000)));
          return;
        }
      }
    }    
  }
  
  private void displayTransportTasks (PrintStream out, MPTask t) {
    Enumeration parents = t.getParentTasks();
    boolean first = true;
    while ( parents.hasMoreElements() ) {
      Task parent = (Task) parents.nextElement();
      PrepositionalPhrase pp = parent.getPrepositionalPhrase(Constants.Preposition.FROMTASK);
      Task pfTransport = (Task) pp.getIndirectObject();
      out.print( (first ? "" : "<TR>\n") + "<TD align=center>" );
      String UID = parent.getUID().toString();
      int pos = UID.indexOf('/') + 1;
      out.print("<A href=\"WEATHER_IMPACT.PSP?RQMT=" + UID.substring(pos) + "\">");
      out.print("<FONT size=\"2\">" + pfTransport.getUID());
      out.print("</FONT></A></TD>\n</TR>\n");
      first = false;
    }
  }
  
  private void displayTaskPage (PrintStream out, String rqmt) {
    rqmtTaskUID = myState.clusterID + "/" + rqmt;
    Task rqmtTask = (Task) search(getWeatherRequirementTask()).iterator().next();
    PrepositionalPhrase pp = rqmtTask.getPrepositionalPhrase(Constants.Preposition.FROMTASK);
    Task pfTransport = (Task) pp.getIndirectObject();
    out.print(
      "<HTML><BODY>\n" +
      "<H1 align=\"center\">Weather Impact on Task</H1>\n" +
      "<H2 align=\"center\">" + pfTransport.getUID() + "</H2>\n");
    displayRequirementData(out, rqmtTask);
    out.print("<HR noshade color=\"#FFFFFF\"></HR>\n");
    displayScheduleTableContents(out, pfTransport);
    out.print("<P><A href=\"WEATHER_IMPACT.PSP\">");
    out.print("Return to summary");
    out.print("</A></P>\n");
    out.print("</BODY></HTML>\n");
  }
  
  private void displayRequirementData (PrintStream out, Task rqmtTask) {
    PrepositionalPhrase pp = rqmtTask.getPrepositionalPhrase(Constants.Preposition.FOR);
    GeolocLocationImpl loc = (GeolocLocationImpl) pp.getIndirectObject();
    pp = rqmtTask.getPrepositionalPhrase(WConstants.FORECAST_DATE_PREPOSITION);
    String onDate = (String) pp.getIndirectObject();
    out.print("<TABLE border=1 cellPadding=1 cellSpacing=1 align=center" +
              " width=75% bordercolordark=#660000 bordercolorlight=#cc9966>\n");
    out.print("<TR>\n<TD align=center width=\"25%\"><B>Forecast Location</B></TD>\n");
    out.print("<TD align=center width=\"25%\"><B>Forecast Date</B></TD>\n");
    out.print("<TD align=center width=\"25%\"><B>Conditions</B></TD>\n");
    out.print("<TD align=center width=\"25%\"><B>Impact (each stop)</B></TD></TR>\n");
    out.print("<TR><TD align=center>" + loc.getName() + "</TD>\n");
    Date d = null;
    try {
      d = WConstants.DATE_FORMAT.parse(onDate);
    } catch (Exception ex) { /* don't care why */ }
    SimpleDateFormat fmt = new SimpleDateFormat("MM/dd/yy");
    if ( d != null )
      out.print("<TD align=center>" + fmt.format(d) + "</TD>\n");
    else
      out.print("<TD align=center>??/??/??</TD>\n");
    Aggregation agg = (Aggregation) rqmtTask.getPlanElement();
    AllocationResult ar = agg.getReportedResult();
    if ( ar != null ) {
      out.print("<TD align=center>");
      int condition = (int) ar.getValue(WConstants.CONDITION);
      out.print(WConstants.RETSINAConditions[condition] + "</TD>\n");      
    }
    MPTask fTask = agg.getComposition().getCombinedTask();
    out.print("<TD align=center>");
    displayForecastTaskImpact(out, fTask);
    out.print(" minutes</TD></TR>\n");
    out.print("</TABLE>\n");    
  }

  private void displayScheduleTableContents (PrintStream out, Task pfTask) {
    out.print("<TABLE border=1 cellPadding=1 cellSpacing=1 align=center" +
              " width=85% bordercolordark=#660000 bordercolorlight=#cc9966>\n");
    displayScheduleTableHeader(out);
    PrepositionalPhrase pp = pfTask.getPrepositionalPhrase(Constants.Preposition.ITINERARYOF);
    Schedule ns = (Schedule) pp.getIndirectObject();
    pp = pfTask.getPrepositionalPhrase(WConstants.ORIGINAL_ITINERARY_PREPOSITION);
    Schedule os = null;
    if (pp == null)
      os = ns;
    else
      os = (Schedule) pp.getIndirectObject();
    Enumeration newSched = ns.getAllScheduleElements();
    Enumeration oldSched = os.getAllScheduleElements();
    while ( oldSched.hasMoreElements() ) {
      ItineraryElement oldElem = (ItineraryElement) oldSched.nextElement();
      ItineraryElement newElem = (ItineraryElement) newSched.nextElement();
      displayScheduleTableRow(out, oldElem, newElem);
    }
    out.print("</TABLE>\n");
  }
  
  private void displayScheduleTableHeader (PrintStream out) {
    out.print("<TR>\n<TD align=center width=\"12%\"><B>Action</B></TD>\n");
    out.print("<TD align=center width=\"15%\"><B>Start Location</B></TD>\n");
    out.print("<TD align=center width=\"15%\"><B>End Location</B></TD>\n");
    out.print("<TD align=center width=\"12%\"><B>Original Start Time</B></TD>\n");
    out.print("<TD align=center width=\"12%\"><B>Revised Start Time</B></TD>\n");
    out.print("<TD align=center width=\"12%\"><B>Original End Time</B></TD>\n");
    out.print("<TD align=center width=\"12%\"><B>Revised End Time</B></TD>\n");
  }
  
  private void displayScheduleTableRow (PrintStream out,
                                        ItineraryElement oldElem,
                                        ItineraryElement newElem) {
    SimpleDateFormat fmt = new SimpleDateFormat("HH:mm:ss MM/dd/yy");
    out.print("<TR>\n<TD align=center width=\"12%\">");
    out.print(oldElem.getRole().toString());
    out.print("</TD>\n<TD align=center width=\"15%\">");
    out.print(((GeolocLocationImpl) oldElem.getStartLocation()).getName());
    out.print("</TD>\n<TD align=center width=\"15%\">");
    out.print(((GeolocLocationImpl) oldElem.getEndLocation()).getName());
    out.print("</TD>\n<TD align=center width=\"12%\">");
    out.print(fmt.format(oldElem.getStartDate()));
    out.print("</TD>\n<TD align=center width=\"12%\">");
    out.print(fmt.format(newElem.getStartDate()));
    out.print("</TD>\n<TD align=center width=\"12%\">");
    out.print(fmt.format(oldElem.getEndDate()));
    out.print("</TD>\n<TD align=center width=\"12%\">");
    out.print(fmt.format(newElem.getEndDate()));
    out.print("</TD>\n</TR>\n");
  }
  
  private int getParentCount (MPTask t) {
    int count = 0;
    Enumeration parents = t.getParentTasks();
    while ( parents.hasMoreElements() ) {
      parents.nextElement();
      count++;
    }
    return count;
  }
  
  private Collection search (UnaryPredicate pred) {
    return myState.sps.queryForSubscriber(pred);
  }
  
  private UnaryPredicate getWeatherForecastTasks () {
    return new UnaryPredicate() {
      public boolean execute(Object o) {
        if ( o instanceof Task)
          return ((Task)o).getVerb().toString().equals(WConstants.GET_FORECAST_VERB);
        else
          return false;
      }
    };
  }
  
  private UnaryPredicate getWeatherForecastAllocations () {
    return new UnaryPredicate() {
      public boolean execute(Object o) {
        if ( o instanceof Allocation ) {
          Allocation alloc = (Allocation) o;
          Task t = alloc.getTask();
          return (t.getVerb().toString().equals(WConstants.GET_FORECAST_VERB));
        } else
          return false;
      }
    };
  }

  private UnaryPredicate getWeatherRequirementTasks () {
    return new UnaryPredicate() {
      public boolean execute(Object o) {
        if ( o instanceof Task)
          return ((Task)o).getVerb().toString().equals(WConstants.FORECAST_VERB);
        else
          return false;
      }
    };
  }

  private UnaryPredicate getWeatherRequirementTask () {
    return new UnaryPredicate() {
      public boolean execute(Object o) {
        if ( o instanceof Task)
          return ((Task)o).getUID().toString().equals(rqmtTaskUID);
        else
          return false;
      }
    };
  }

}
