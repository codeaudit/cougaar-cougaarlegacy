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

package com.prc.alp.liaison.acl;

import EDU.cmu.softagents.misc.KQMLParser.KQMLmessage;
import java.util.Vector;

/**
 * The RETSINAWeatherMessageParser class extracts weather information from
 * a RETSINA weather info agent message.
 *
 * @author Brandon L. Buteau
 * @version 1.1
 * @since 1.0
 */

public class RETSINAWeatherMessageParser  {
  
  private boolean debug;
  
  /**
   * A default no-arg constructor that leaves debug output disabled.
   */
  public RETSINAWeatherMessageParser () {
    debug = false;
  }
  
  /** 
   * Retrieves the current debug status for this parser.
   *
   * @return A value of <CODE>true</CODE> indicating that debug output is
   * enabled or<CODE>false</CODE> indicating that debug output is
   * disabled.
   */
  public boolean getDebug () {
    return debug;
  }
  
  /**
   * Sets the current debug status for this parser.
   *
   * @param flag A value of <CODE>true</CODE> means to enable debug output,
   * while a <CODE>false</CODE> means to disable debug output.
   */
  public void setDebug (boolean flag) {
    debug = flag;
  }
  
  /**
   * Extracts current weather information from a RETSINA weather InfoAgent reply.
   *
   * @param content The raw text of the complete reply returned by the RETSINA agent
   * @return Current weather conditions at a previously requested location
   * as reported by the RETSINA agent
   */
  public String parseCurrent (String content) {
    if ( debug )
      System.out.println("RETSINA weather message raw text:\n" + content);
    KQMLmessage kmsg = new KQMLmessage(content);
    String reply = kmsg.getValue("content");
    KQMLmessage rmsg = new KQMLmessage(reply);
    String city = clean(rmsg.getValue("city"));
    String time = clean(rmsg.getValue("time"));
    int pos = time.indexOf(" GMT");
    if (pos > 4 )
      time = time.substring(pos - 4, pos);
    else
      time = "<unknown time>";
    String weather = rmsg.getValue("weather");
    KQMLmessage wmsg = new KQMLmessage(weather);
    String conditions = clean(wmsg.getValue("conditions"));
    String temp = clean(wmsg.getValue("temperature"));
    pos = temp.indexOf(" C");
    if (pos > 3)
      temp = temp.substring(pos - 3, pos);
    else
      temp = "<unknown temp>";
    String hum = clean(wmsg.getValue("humidity"));
    hum = hum.substring(0, hum.length() - 1);
    String wind = clean(wmsg.getValue("wind"));
    String windir = wind.substring(0, wind.indexOf(' '));
    pos = wind.indexOf(" kph");
    if (pos > 3)
      wind = wind.substring(pos - 3, pos);
    else
      wind = "<unknown wind>";
    wind = windir + " " + wind;
    System.out.println("Weather in " + city + " at " + time + " GMT:\n" +
                       "Conditions: " + conditions + "\n" +
                       "Temperature: " + temp + "C\n" +
                       "Humidity: " + hum + "%\n" +
                       "Wind: " + wind + "kph");  
    return "";
  }
  
  /**
   * Extracts weather forecast information from a RETSINA weather InfoAgent reply.
   *
   * @param content The raw text of the complete reply returned by the RETSINA agent
   * @return Weather forecast conditions at a previously requested location
   * as reported by the RETSINA agent; currently structured as a <CODE>Vector</CODE>
   */
  public Object parseForecast (String content) {
    if ( debug )
      System.out.println("RETSINA weather message raw text:\n" + content);
    KQMLmessage kmsg = new KQMLmessage(content);
    String reply = kmsg.getValue("content");
    KQMLmessage rmsg = new KQMLmessage(reply);
    if ( rmsg.getValue("city") == null ) {
      if ( debug )
        System.out.println("RETSINA weather message was an error result:\n" +
                           reply);
      return reply;
    }
    String city = clean(rmsg.getValue("city"));
    String weather = rmsg.getValue("weather");
    //System.out.println("Weather forecast for " + city + ":\n" +
    //                   weather);
    KQMLmessage cmsg = new KQMLmessage(weather);
    String conditions  = clean(cmsg.getValue("conditions"));
    Vector result = new Vector();
    int pos = city.indexOf("/");
    result.add(city.substring(pos + 1));
    pos = 0;
    int nextpos = 0;
    while ( nextpos >= 0 ) {
      nextpos = conditions.indexOf(' ', pos);
      if ( nextpos > 0)
        result.add(conditions.substring(pos, nextpos));
      else
        result.add(conditions.substring(pos));
      pos = nextpos + 1;
    }
    //System.out.println("parseForecast returning Vector : " + result);
    return result;
  }
  
  /**
   * Extracts an identification code from a RETSINA weather InfoAgent reply.  This
   * allows the reply to be matched to a prior request.
   *
   * @param content The raw text of the complete reply returned by the RETSINA agent
   * @return The reply-with ID sent in the original request to the RETSINA agent
   */
  public String extractReplyID (String content) {
    KQMLmessage kmsg = new KQMLmessage(content);
    return kmsg.getValue("in-reply-to");
  }
  
  /**
   * Extracts an identification code from a RETSINA weather InfoAgent reply.  This
   * allows the reply itself to be replied to in case of error.
   *
   * @param content The raw text of the complete reply returned by the RETSINA agent
   * @return The reply-with ID sent the RETSINA agent's reply
   */
  public String extractReplyWith (String content) {
    KQMLmessage kmsg = new KQMLmessage(content);
    return kmsg.getValue("reply-with");
  }
  
  /**
   * Extracts the content field from a RETSINA weather InfoAgent reply.  This
   * contains the substantive weather information (either a forecast or current data).
   *
   * @param content The raw text of the complete reply returned by the RETSINA agent
   * @return The weather forecast or current weather conditions returned by
   * the RETSINA agent
   */
  public String extractContent (String content) {
    KQMLmessage kmsg = new KQMLmessage(content);
    return kmsg.getValue("content");
  }
  
  private String clean (String s) {
    StringBuffer result = new StringBuffer();
    for (int i = 0; i < s.length(); i++ ) {
      char c = s.charAt(i);
      if ( c != '(' && c != ')' && c != '\n' && c != '\r' && c != '\t' )
        result.append(c);
    }
    return result.toString();
  }
      
}
