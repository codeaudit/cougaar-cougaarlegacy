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

import java.text.SimpleDateFormat;
import org.cougaar.domain.planning.ldm.plan.*;

/**
 * A convenience class for organizing all of the configurable constants
 * and translation functions associated with ALP/Cougaar society use of
 * weather forecast conditions supplied by external RETSINA agents.
 *
 * @author  Brandon L. Buteau
 * @version 1.1
 * @since   1.0 
 */
public class WConstants extends Object {

  public WConstants() {
  }

  // Weather agent types and names
  public static final int UNKNOWN_FORECAST_AGENT = 0;
  public static final int RETSINA_CNN_FORECAST_AGENT = 1;
  public static final int RETSINA_WCN_FORECAST_AGENT = 2;
  public static final String[] AGENT_NAMES = {
    "<unknown agent>",
    "CNNForecastWeatherAgent",
    "WCNForecastWeatherAgent"
  };

  // Weather aspect types -- hacked for now
  public static final int CONDITION = 1000; // used to retrieve original string
  public static final int VISIBILITY = 1001; // measured in kilometers?
  public static final int PRECIPITATION = 1002; // measured in cm/day?
  public static final int TRACTION = 1003; // 0.0 = ice, 1.0 = dry asphalt
  public static final int TURBULENCE = 1004; // minutes delay ?
  public static final int HIGHTEMP = 1005;
  public static final int LOWTEMP = 1006;

  public static final String FORECAST_VERB = "ForecastWeather";
  public static final String GET_FORECAST_VERB = "GetWeatherForecast";
  public static final String FORECAST_DATE_PREPOSITION = "OnDate";
  public static final String ORIGINAL_ITINERARY_PREPOSITION = "OriginalItineraryOf";
  public static final String FORECAST_ASSET_TYPE = "WeatherForecaster";

  public static final SimpleDateFormat DATE_FORMAT = 
    new SimpleDateFormat("yyyy_MM_dd");

  public static String translateALPToRemote(String locName, int agentType) {
    String[][] map = null;
    switch (agentType) {
      case UNKNOWN_FORECAST_AGENT:
        return null;
      case RETSINA_CNN_FORECAST_AGENT:
        map = RETSINACNNLocationMap;
        break;
      case RETSINA_WCN_FORECAST_AGENT:
        map = RETSINAWCNLocationMap;
        break;
      default:
        return null;
      }
    for ( int i = 0; i < map.length; i++ )
      if ( locName.equals(map[i][0]) )
        return map[i][1];
    return null;
  }

  public static String translateRemoteToALP(String locName, int agentType) {
    String[][] map = null;
    switch (agentType) {
      case UNKNOWN_FORECAST_AGENT:
        return null;
      case RETSINA_CNN_FORECAST_AGENT:
        map = RETSINACNNLocationMap;
        break;
      case RETSINA_WCN_FORECAST_AGENT:
        map = RETSINAWCNLocationMap;
        break;
      default:
        return null;
      }
    for ( int i = 0; i < map.length; i++ )
      if ( locName.equals(map[i][1]) )
        return map[i][0];
    return null;
  }

  public static String[][] RETSINACNNLocationMap = {
    { "AL Kharj", "me/SaudiArabia/RiyadhOERY" },
    { "Aviano AFB", "eu/Italy/AvianoPNLIYW" },
    { "Eglin AFB", "se/FL/ValparaisoVPS" },
    { "Hill AFB", "sw/UT/OgdenOGD" },
    { "Nellis AFB", "sw/NV/NellisAFBLSV" },
    { "Ramstein AFB", "eu/Germany/HeidelbergEDHX" },
    { "Spangdahlem AFB", "eu/Germany/BonnBONN" }
  };

  public static String[][] RETSINAWCNLocationMap = {
    { "Asmara", "er_asmara" }
  };

  public static String[] RETSINAConditions = {
    "unknown",
    "sunny",
    "p/cloudy",
    "cloudy",
    "showers",
    "rain",
    "t-storms",
    "snow",
    "icy"
  };
    
  public static double[][] RETSINAConditionValues = {
    { 0.0, 30.0, 0.0, 1.0, 0.0  }, // unknown (assume same as clear)
    { 1.0, 30.0, 0.0, 1.0, 0.0  }, // clear
    { 2.0, 20.0, 0.0, 1.0, 0.0  }, // partly cloudy`
    { 3.0, 10.0, 0.0, 1.0, 0.0  }, // cloudy
    { 4.0,  5.0, 0.2, 0.8, 5.0  }, // showers
    { 5.0,  3.0, 1.0, 0.6, 10.0 }, // rain
    { 6.0,  1.5, 2.0, 0.6, 20.0 }, // thunderstorms
    { 7.0,  3.0, 0.2, 0.3, 10.0 }, // snow
    { 8.0,  3.0, 0.3, 0.0, 10.0 }  // ice
  };
    
  public static double[] getRETSINAConditionValues (String condition) {
    double[] result = null;
    for ( int i = 0; i < RETSINAConditions.length; i++ )
      if ( condition.equals(RETSINAConditions[i]) )
        result = RETSINAConditionValues[i];
    return result;
  }
    
  public static long weatherImpact(AllocationResult ar) {
    long impact = 0;
    long mSecPerHour = 60 * 60 * 1000;
    double visibility = ar.getValue(WConstants.VISIBILITY);
    double precipitation = ar.getValue(WConstants.PRECIPITATION);
    double traction = ar.getValue(WConstants.TRACTION);
    double turbulence = ar.getValue(WConstants.TURBULENCE);
    if ( visibility < 5 ) {
      impact = impact + (long) (0.25 * mSecPerHour);
      if ( visibility < 2 )
        impact = impact + (long) (0.25 * mSecPerHour);
    }
    if ( precipitation > 0.0 ) {
      impact = impact + (long) (0.15 * mSecPerHour);
      if ( precipitation > 0.5 ) {
        impact = impact + (long) (0.15 * mSecPerHour);
        if ( precipitation > 1 )
          impact = impact + (long) (0.25 * mSecPerHour);
      }
    }
    if ( traction < 1.0 )
      impact = impact + (long) ((1.0 - traction) * mSecPerHour);
    if ( turbulence > 0.0  )
      impact = impact + (long) ((turbulence / 60.0) * mSecPerHour);
    return impact;
  }
  
}