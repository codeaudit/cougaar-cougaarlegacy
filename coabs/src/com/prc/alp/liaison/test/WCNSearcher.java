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

package com.prc.alp.liaison.test;

import java.io.*;
import java.net.*;
import java.util.*;

/**
 * The WCN searcher class is for finding particular pages on the Weather
 * Channel web site.  It functions as a very limited kind of web crawler,
 * and is rapidly outdated by changes to the web site.
 *
 * @author Brandon L. Buteau
 * @version 1.1
 * @since 1.0
 */

public class WCNSearcher {
  private static String WCN_SITE = "http://www.weather.com/";
  private static String REGIONS_INDEX = WCN_SITE + "intl/regions_index/";
  private static String[] REGIONS = { "Africa", "Asia", "Australia_New_Zealand",
                                      "Caribbean_Central_America", "Europe", 
                                      "Indian_Ocean_Islands", "Middle_East",
                                      "North_America", "Pacific_Islands", "South_America" };
  private static String REGIONS_SUFFIX = "_region.html";
  private static String COUNTRY_FIND = "<OPTION VALUE=\"/intl/countries_index/";
  private static String COUNTRY_PREFIX = "<OPTION VALUE=\"/";
  private static String COUNTRY_SUFFIX = "\">";
  private static String CITY_FIND = "<A HREF=\"/weather/cities/";
  private static String CITY_PREFIX = "<A HREF=\"/";
  private static String CITY_SUFFIX = "\">";
  private static String US_INDEX = "weather/us/states/";
  private static String[] US_STATES = { "AL", "AK", "AZ", "AR", "CA", "CO", "CT",
                                        "DE", "FL", "GA", "GU", "HI", "ID", "IL",
                                        "IN", "IA", "KS", "KY", "LA", "ME", "MD",
                                        "MA", "MI", "MN", "MS", "MO", "MT", "NE",
                                        "NV", "NH", "NJ", "NM", "NY", "NC", "ND",
                                        "OH", "OK", "OR", "PA", "PR", "RI", "SC",
                                        "SD", "TN", "TX", "UT", "VT", "VA", "WA",
                                        "WV", "WI", "WY" };
  private static String[] US_SPECIAL = { "DC-W", "FM-Y",  "VI-C", "VI-F", "VI-K",
                                         "VI-S" };
  private static String[] ALPHABET = { "A", "B", "C", "D", "E", "F",
                                       "G", "H", "I", "J", "K",
                                       "L", "M", "N", "O", "P",
                                       "Q", "R", "S", "T", "U",
                                       "V", "W", "X", "Y", "Z" };
  private static String US_SUFFIX = ".html";
  
  public String mainURL = null;
  public boolean us = false;
  public String cityString = null;
  public ArrayList searchStrings = new ArrayList();
  
  public static void main (String[] args) {
    WCNSearcher searcher = 
      new WCNSearcher ();
    //searcher.dumpArgs(args);
    searcher.parseArgs(args);
    if ( searcher.us )
      searcher.extractUSCities(US_INDEX);
    else if ( searcher.mainURL != null ) {
       String page = searcher.loadURL(searcher.mainURL);
       searcher.extractInternationalCities(page);
    } else
      searcher.findCityContaining(searcher.cityString, searcher.searchStrings);
    System.exit(0);
  }
  
  private void parseArgs (String[] args) {
    if ( args.length < 1 ) {
      System.err.println("Usage:\n" +
                         "\tjava WCNSearcher [-intl] [-us] [city-string search-strings... < city-file]");
      System.exit(-1);
    }
    if ( args[0].equals("-intl") )
      mainURL = REGIONS_INDEX + "Asia" + REGIONS_SUFFIX;
    else if ( args[0].equals("-us") ) {
      mainURL = US_INDEX;
      us = true;
    } else {
      cityString = args[0];
      for (int i = 1; i < args.length; i++)
        searchStrings.add(args[i]);     
    }
  }
  
  public WCNSearcher () {  }
  
  private void dumpArgs (String[] args) {
    System.out.println("Arguments:");
    for ( int i = 0; i < args.length; i++)
      System.out.println(args[i]);
  }
  
  private String loadURL (String s) {
    URL wcnURL = null;
    try {
      wcnURL = new URL(s);
    } catch (MalformedURLException e) {
      System.err.println("Malformed URL " + s);
      return null ;
    }
    Object content = null;
    try {
      content = wcnURL.getContent();
    } catch (IOException e) {
      System.err.println("Can't load content from " + s);
      return null;
    }
    InputStreamReader input = new InputStreamReader((InputStream) content);
    BufferedReader reader = new BufferedReader(input);
    StringBuffer result = new StringBuffer();
    boolean stillReading = true;
    char[] cbuf = new char[100000];
    int ccount = 0;
    while ( stillReading ) {
      try {
        ccount = reader.read(cbuf);
      } catch (IOException e) {
        System.err.println("IO error getting content from " + s);
        return null;
      }
      if ( ccount > 0 )
        result.append(cbuf, 0, ccount);
      if ( ccount < 0 )
        stillReading = false;
    }
    return result.toString();    
  }

  private void extractUSCities (String page) {
    ArrayList states = extractStates(page);
    Iterator it = states.iterator();
    while ( it.hasNext() ) {
      String stateURL = WCN_SITE + (String) it.next();
      try {
        Thread.sleep(4000);
      } catch (InterruptedException ex) { }
      System.err.println("Getting " + stateURL + "...");
      //System.out.println(stateURL);
      String cpage = loadURL(stateURL);
      ArrayList cities = extractCities(cpage);
      Iterator cit = cities.iterator();
      while ( cit.hasNext() ) {
        String cityURL = WCN_SITE + (String) cit.next();
        System.out.println(cityURL);
      }
    }
  }
  
  private void extractInternationalCities (String page) {
    ArrayList countries = extractCountries(page);
    Iterator it = countries.iterator();
    while ( it.hasNext() ) {
      String countryURL = WCN_SITE + (String) it.next();
      try {
        Thread.sleep(5000);
      } catch (InterruptedException ex) { }
      System.err.println("Getting " + countryURL + "...");
      System.out.println(countryURL);
      String cpage = loadURL(countryURL);
      ArrayList cities = extractCities(cpage);
      Iterator cit = cities.iterator();
      while ( cit.hasNext() ) {
        String cityURL = WCN_SITE + (String) cit.next();
        System.out.println(cityURL);
      }
    }
  }
  
  private ArrayList extractStates (String stateURL) {
    ArrayList states = new ArrayList();
    for ( int i = 0; i < US_STATES.length; i++ )
      for ( int j = 0; j < ALPHABET.length; j++ )
        states.add(stateURL + US_STATES[i] + "-" + ALPHABET[j] + US_SUFFIX);
    for ( int i = 0; i < US_SPECIAL.length; i++ )
      states.add(stateURL + US_SPECIAL[i] + US_SUFFIX);
    return states;
  }

  private ArrayList extractCountries (String page) {
    int pos = 0;
    int delimit = 0;
    ArrayList countries = new ArrayList();
    while ( true ) {
      pos = page.indexOf(COUNTRY_FIND, delimit) + COUNTRY_PREFIX.length();
      if ( pos < COUNTRY_PREFIX.length() )
        return countries;
      delimit = page.indexOf(COUNTRY_SUFFIX, pos);
      countries.add(page.substring(pos, delimit));
    }
  }

  private ArrayList extractCities (String page) {
    int pos = 0;
    int delimit = 0;
    ArrayList cities = new ArrayList();
    while ( true ) {
      pos = page.indexOf(CITY_FIND, delimit) + CITY_PREFIX.length();
      if ( pos < CITY_PREFIX.length() )
        return cities;
      delimit = page.indexOf(CITY_SUFFIX, pos);
      cities.add(page.substring(pos, delimit));
    }
  }
  
  private void findCityContaining (String city, ArrayList sch) {
    String line = null;
    BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
    try {
      while ( (line = reader.readLine()) != null ) 
        if ( line.indexOf(city) >= 0 ) {
          String cityURL = WCN_SITE + "weather/cities/" + line;
          String page = loadURL(cityURL);
          Iterator it = sch.iterator();
          while ( it.hasNext() ) {
            String s = (String) it.next();
            if ( page.indexOf(s) >= 0 )
              System.out.println(line + ":" + s);
          }
        }
    } catch (IOException ex) {
      System.err.println("IO Error reading input file: " + ex.getMessage());
    }
    return;
  }
}

