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
 * Note:  Heavily adapted from example InfoAgent external query
 *        functions supplied by the CMU RETSINA support organization.
 *
 */
//package EDU.cmu.softagents.infoagents.ExternalQueryFn;

import java.util.* ;
import java.net.* ;
import java.io.* ;

import EDU.cmu.softagents.infoagents.ExternalQueryFn.ExternalQueryInterface;
import EDU.cmu.softagents.infoagents.ExternalQueryFn.QueryConfigurationObject;
import EDU.cmu.softagents.infoagents.ExternalQueryFn.Parser;
import EDU.cmu.softagents.retsina.TaskStructures.DisplayBehavior ;
import EDU.cmu.softagents.retsina.TaskStructures.GUI ;
import EDU.cmu.softagents.retsina.Communicator.*;

/**
 * An external query function capable of extracting current weather condition
 * information from CNN Web site weather pages.  Note that this is heavily
 * adapted from example InfoAgent external query functions supplied by the
 * CMU RETSINA support organization.
 *
 * @author Brandon L. Buteau
 * @version 1.1
 * @since 1.0
 */
public class CNNCurrentWeatherQueryFn implements ExternalQueryInterface {

  // Some class variables the BasicAgent can set to perform voodoo.
  // File names will be BASE_NAME.EXT_NUM and we increment the EXT_NUM
  // to create the next file name. FILE_READ on says we are reading the
  // web page content from a file not a URL. FILE_WRITE on says to write
  // the weg page content to a file.
  public static /*final*/ boolean DEBUG = true;
  public static /*final*/ boolean FILE_READ = false;
  public static /*final*/ String  FILE_READ_PATH = "";
  public static /*final*/ String  FILE_READ_BASE_NAME = "WeatherCNN";
  public static           int     FILE_READ_EXT_NUM = 0;
  public static /*final*/ boolean FILE_WRITE = false;
  public static /*final*/ String  FILE_WRITE_PATH = "";
  public static /*final*/ String  FILE_WRITE_BASE_NAME = "WeatherCNN";
  public static           int     FILE_WRITE_EXT_NUM = 0;
  
  
  public String siteURLString = null ;

  public static final String CITY_STRING = "city";
  public static final String TIME_STRING = "time";
  public static final String WEATHER_STRING = "weather";
  public static final String WEATHER_URL_STRING = "weather-url";

  // Components of the weather.
  public static final String WEATHER_PERF_STRING = "weather";
  public static final String CONDITION_STRING = "conditions";
  public static final String TEMPERATURE_STRING = "temperature";
  public static final String HUMIDITY_STRING = "humidity";
  public static final String WIND_STRING = "wind";

  public static final String BASE_URL_STRING = "http://cnn.com/WEATHER/html/";
  
  public static final String ALL_FIELDS = "ALL";
  private DisplayBehavior display = null;
  private Hashtable fieldsTable = null ;

  // We get the agent-specific args from this object.
  //
  private QueryConfigurationObject queryConfigObj = null;

  // Access to Communicator should be through the method,
  // getCommunicator(), and not through the variable, comm,
  // to ensure that the reference is not null. InfoAgent will
  // instantiate the CNNCurrentWeatherQueryFn, then the BasicAgent, get
  // the Communicator, and call setCommunicator(). If by chance
  // a query interface method here (that used the Communicator)
  // was called before the Communicator reference was supplied,
  // then we'd get a null reference if the call accessed the
  // variable, comm. By having the method use getCommunicator()
  // instead, we cause it to block until the reference has been
  // supplied. We don't have any easy fix for this chicken and
  // egg problem right now, maybe later after the demo. -dirk 5/1/98
  //
  // NOTE: Have changed this to go through the queryConfigObj to
  //       get and set the Communicator. Now just provide a convenience
  //       method here for Communicator accesses inside this module.
  //       -dirk 5/2/98
  //
  private RetsinaCommunicator comm = null;
  /****************************************************************
  public synchronized void setCommunicator(Communicator comm) {
    if (comm == null) {
      displayMessage("ERROR", "CNNCurrentWeatherQueryFn::setCommunicator " +
		     "Null arg for Communicator was supplied");
    }
    this.comm = comm;
    notifyAll();
  }
  private synchronized Communicator getCommunicator() {
    while (comm == null) {try {wait();} catch (Exception e) {}}
    return comm;
  }
  ****************************************************************/
  private RetsinaCommunicator getCommunicator() {
    if (comm == null)
      comm = queryConfigObj.getCommunicator();  // may block waiting
    return comm;
  }
  

  /** Constructor takes a QueryConfigurationObject from which it
      gets the args it needs supplied through agent's configuration
      file by InfoAgent.
  */
  public CNNCurrentWeatherQueryFn(QueryConfigurationObject queryConfigObj) {
    this.queryConfigObj = queryConfigObj;
    this.siteURLString = queryConfigObj.getInfoSourceURL();
    this.display = queryConfigObj.getExtGUI();

    // I should now get the agentDebugLevel and options and
    // do some setup with them. Just do very simple for now.
    int debugLevel = queryConfigObj.getAgentDebugLevel();
    if (debugLevel == 0)
      setNoDebugFlags();
    else
      setDebugFlags();
    int options = queryConfigObj.getAgentOptionsSelect();
    if (options == 3) {
      Parser.DEBUG = true;
    } else if (options == 0) {
      CNNCurrentWeatherQueryFn.FILE_READ = false;
      CNNCurrentWeatherQueryFn.FILE_WRITE = false;
    } else if (options == 1) {
      CNNCurrentWeatherQueryFn.FILE_READ = false;
      CNNCurrentWeatherQueryFn.FILE_WRITE = true;
    } else {
      CNNCurrentWeatherQueryFn.FILE_READ = true;
      CNNCurrentWeatherQueryFn.FILE_WRITE = false;
    }

  }

  
  public String fetchPage(String pageURLString) {
    URL pageURL = null;
    try {
      pageURL = new URL(pageURLString);
    } catch (MalformedURLException e) {
      displayMessage("ERROR", "CNNCurrentWeatherQueryFn::fetchPage " + 
		     "Malformed URL " + pageURLString);
      return null ;
    }
        
    Parser pageParser = new Parser(pageURL, true, display);
    return pageParser.getContent();
  }


  /// Convenience function to get entire record
  public synchronized String getRecord(String key)     {
    return getRecordFields(key, ALL_FIELDS);
  }

  // Convenience function to get 
  public synchronized String getField(String key, String fieldName)     {
    return getRecordFields(key, fieldName);
  }
  


  
  // This uses the key to get the entire record from the web page 
  // For now the fields that compose the record will be hardcoded
  // but these fields need to be checked with the advertisement 
  // in future releases. It then uses the parameter passed to it 
  // to decide which fields to return 
  // Also put's the field values in a Hashtable that can be later accessed
  private synchronized String getRecordFields(String key, String param)     {

    fieldsTable = null;

    String city = new String(key);
    displayMessage("DEBUG", "CNNCurrentWeatherQueryFn::getRecordFields " + 
		   "city = " + city);

    // Add the URL extension corresponding to the city.
    StringBuffer sbuf = new StringBuffer();
    for (int i = 0; i < city.length(); i++) {
      char ch = city.charAt(i);
      if (ch != ' ' && ch != '\t' && ch != '\n')
	sbuf.append(ch);
    }
    String formURLString = siteURLString + new String(sbuf) + ".html";

    String weatherURL = formURLString;

    URL infoURL = null;
    try {
      infoURL = new URL(formURLString);
    } catch (MalformedURLException e) {
      displayMessage("ERROR", "CNNCurrentWeatherQueryFn::getRecordFields " + 
		     "Malformed URL " + formURLString);
      return null ;
    }
    displayMessage("DEBUG", "CNNCurrentWeatherQueryFn::getRecordFields " + 
		   "Got URL for site: " + formURLString);


    //Get the parser corresponding to the initial URL
    Parser infoParser = new Parser(infoURL,true,display);
    if (infoParser == null)     {
      displayMessage("ERROR",  "CNNCurrentWeatherQueryFn::getRecordFields " + 
		     "Cannot initialize parser for page " + formURLString );
      return null ;
    }

    if (FILE_WRITE) {
      displayMessage("DEBUG", "CNNCurrentWeatherQueryFn::getRecordFields " + 
		     "Site URL: " + formURLString +
		     " Calling writeContent()." + FILE_WRITE_EXT_NUM);
      writeContent(infoParser.getContent());
    }


    displayMessage("DEBUG", "CNNCurrentWeatherQueryFn::getRecordFields " + 
		   "ready to return record fields: " + param);

    displayMessage("DEBUG", "CNNCurrentWeatherQueryFn::getRecordFields " + 
		   "city is: " + key);
  


    String ct = null;
    String condition = null;
    boolean isCondition = false;
    String time = null;
    boolean isTime = false;
    String temperature = null;
    boolean isTemperature = false;
    String humidity = null; 
    boolean isHumidity = false;
    String wind = null;
    boolean isWind = false;
    String tag = null;

    while ((tag = infoParser.nextTag("NONE")) != null) {
      ct = infoParser.getContentBetween ();
      if ((ct == null) || (ct.length() == 0)) continue;
      ct = ct.replace('\n', ' ');
      ct = ct.replace('\r', ' ');
      if (ct.toLowerCase().indexOf("conditions") != -1) {
	tag = infoParser.nextTag("NONE");
	while (tag != null && !tag.equalsIgnoreCase("table"))
	  tag = infoParser.nextTag("NONE");
	while (tag != null && !tag.equalsIgnoreCase("table"))
	  tag = infoParser.nextTag("NONE");
	while (tag != null && !tag.equalsIgnoreCase("span"))
	  tag = infoParser.nextTag("NONE");
        if (tag != null) {
	  infoParser.removeContentBetween();
	  tag = infoParser.nextTag("NONE");
  	  while (tag != null && !tag.equalsIgnoreCase("/span"))
	    tag = infoParser.nextTag("NONE");
          if (tag != null) {
	    time = infoParser.getContentBetween();

	    // Get conditions.
	    tag = infoParser.nextTag("NONE");
	    while (tag != null && !tag.equalsIgnoreCase("span"))
	      tag = infoParser.nextTag("NONE");
	    if (tag != null) {
	      infoParser.removeContentBetween();
	      tag = infoParser.nextTag("NONE");
	      while (tag != null && !tag.equalsIgnoreCase("/span"))
		tag = infoParser.nextTag("NONE");
	      if (tag != null) {
		condition = infoParser.getContentBetween();

		// Get temp.
		tag = infoParser.nextTag("NONE");
		while (tag != null && !tag.equalsIgnoreCase("span"))
		  tag = infoParser.nextTag("NONE");
		tag = infoParser.nextTag("NONE");
		while (tag != null && !tag.equalsIgnoreCase("span"))
		  tag = infoParser.nextTag("NONE");
		if (tag != null) {
                  infoParser.removeContentBetween();
		  tag = infoParser.nextTag("NONE");
		  while (tag != null && !tag.equalsIgnoreCase("/span"))
		    tag = infoParser.nextTag("NONE");
		  if (tag != null) {
                    temperature = infoParser.getContentBetween();

                    // Get humidity.
                    tag = infoParser.nextTag("NONE");
                    while (tag != null && !tag.equalsIgnoreCase("span"))
                      tag = infoParser.nextTag("NONE");
                    tag = infoParser.nextTag("NONE");
                    while (tag != null && !tag.equalsIgnoreCase("span"))
                      tag = infoParser.nextTag("NONE");
                    if (tag != null) {
                      infoParser.removeContentBetween();
                      tag = infoParser.nextTag("NONE");
                      while (tag != null && !tag.equalsIgnoreCase("/span"))
                        tag = infoParser.nextTag("NONE");
                      if (tag != null) {
                        humidity = infoParser.getContentBetween();

                        // Get wind.
                        tag = infoParser.nextTag("NONE");
                        while (tag != null && !tag.equalsIgnoreCase("span"))
                          tag = infoParser.nextTag("NONE");
                        tag = infoParser.nextTag("NONE");
                        while (tag != null && !tag.equalsIgnoreCase("span"))
                          tag = infoParser.nextTag("NONE");
                        if (tag != null) {
                          infoParser.removeContentBetween();
                          tag = infoParser.nextTag("NONE");
                          while (tag != null && !tag.equalsIgnoreCase("/span"))
                            tag = infoParser.nextTag("NONE");
                          if (tag != null) {
                            wind = infoParser.getContentBetween();
                          }
                        }
                      }
                    }

		  }
		}

	      }
	    }
	  }

	}
      }
      infoParser.removeContentBetween();
    }

    // If we couldn't get at least a temperature from the page, it must
    // have been an invalid URL, ie.e, a city not in CNN database or
    // incorrectly spelled or designated.
    if (temperature == null)     {
      displayMessage("ERROR",  "CNNCurrentWeatherQueryFn::getRecordFields " + 
		     "URL " +  formURLString + " is not a valid URL. " +
		     "The \"CityCountry\" or \"CityState\" key may be wrong.");
      return null ;
    }

    String weather = WEATHER_PERF_STRING + " " +
                     ":" + CONDITION_STRING + " (" + condition + ") " +
	             ":" + TEMPERATURE_STRING + " (" + temperature + ") " +
	             ":" + HUMIDITY_STRING + " (" + humidity + ") " +
                     ":" + WIND_STRING + " (" + wind + ")";

    if (param.equalsIgnoreCase(ALL_FIELDS)) {

      fieldsTable = new Hashtable();
      
      if (key != null) {
	fieldsTable.put(CITY_STRING, key);
      }
      if (time !=null) {
	fieldsTable.put(TIME_STRING, time);
      }
      if (condition != null) {
	fieldsTable.put(CONDITION_STRING, condition);
      }
      if (temperature != null) {
	fieldsTable.put(TEMPERATURE_STRING, temperature);
      }

      if (humidity != null) {
	fieldsTable.put(HUMIDITY_STRING, humidity);
      }

      if (wind != null) {
	fieldsTable.put(WIND_STRING, wind);
      }

      if (weatherURL != null) {
	fieldsTable.put(WEATHER_URL_STRING, weatherURL);
      }

      if (key == null) {
	key = " ";
      }
      if (time == null) {
        time =  " ";
      }
      if (condition == null) {
	condition = " ";
      }

      if (temperature == null) {
	temperature = " ";
      }

      if (humidity == null) {
	humidity = " ";
      }

      if (wind == null) {
	wind = " ";
      }

      if (weatherURL == null) {
	weatherURL = " ";
      }

      String reply = "(reply :" + CITY_STRING + " (" + key + ") " + 
	             ":" + TIME_STRING + " (" + time + ") " +
	             ":" + WEATHER_STRING + " (" + weather + ") " +
	             ":" + WEATHER_URL_STRING + " (" + weatherURL + "))";
      displayMessage("DEBUG", "CNNCurrentWeatherQueryFn::getRecordFields " + 
		     "Weather is: " + reply);
      return reply;
    } else if (param.equalsIgnoreCase(CITY_STRING)) {
      fieldsTable = new Hashtable();
      fieldsTable.put(CITY_STRING, key);
      return key ;
    } else if (param.equalsIgnoreCase(TIME_STRING)) {
      fieldsTable = new Hashtable();
      fieldsTable.put(TIME_STRING, time);
      return time ;
    } else if (param.equalsIgnoreCase(WEATHER_STRING)) {
      fieldsTable = new Hashtable();
      fieldsTable.put(WEATHER_STRING, weather);
      return weather;
    } else if (param.equalsIgnoreCase(WEATHER_URL_STRING)) {
      fieldsTable = new Hashtable();
      fieldsTable.put(WEATHER_URL_STRING, weatherURL);
      return weatherURL;
    } else {
      return null;
    }
    
    }


  private Parser readContent() {
    Parser infoParser;
    String filename = FILE_READ_PATH + FILE_READ_BASE_NAME + "." + FILE_READ_EXT_NUM;
    FILE_READ_EXT_NUM++;
    try {
      File file = new File(filename);
      if (DEBUG) displayMessage("DEBUG", "CNNCurrentWeatherQueryFn.readContent:: File " +
				filename + " file.length=" + file.length());
      byte bytes[] = new byte[(int) (file.length())];
      FileInputStream fis = new FileInputStream(file);
      int cnt = fis.read(bytes, 0, bytes.length);
      if (DEBUG) displayMessage("DEBUG", "CNNCurrentWeatherQueryFn.readContent:: File " +
				filename + " read cnt=" + cnt);
      fis.close();
      if (cnt != bytes.length) return null;
      String content = new String(bytes);
      infoParser = new Parser(content, true, display);
    }
    catch (FileNotFoundException e) {
      if (DEBUG) displayMessage("ERROR", "File " + filename + " not found.");
      return null;
    }
    catch (IOException e) {
      if (DEBUG) displayMessage("ERROR", "Read failed on file " + filename);
      return null;
    }
    return infoParser;
  }

  private void writeContent(String content) {
    String filename = FILE_WRITE_PATH + FILE_WRITE_BASE_NAME + "." + FILE_WRITE_EXT_NUM;
    FILE_WRITE_EXT_NUM++;
    try {
      FileWriter file = new FileWriter(filename);
      file.write(content, 0, content.length());
      if (DEBUG) displayMessage("DEBUG", "CNNCurrentWeatherQueryFn.writeContent:: File " +
				filename + " closed with bytes=" + content.length());
      file.close();
    }
    catch (IOException e) {
      if (DEBUG) displayMessage("ERROR", "Could not write file " + filename);
    }
  }
    
  // Stores the fields returned from the last getRecord or getField action 
  // Probably should be synchronized!
  public synchronized String getLastFieldValue(String fieldName)     {
    if (fieldsTable == null) {
      return null ;
    }
    
    return (String) fieldsTable.get(fieldName);
  }

  // returns the non key field names
  public String [] getNonKeyFieldNames()     {
    String[] fieldNames = new String[4];
    fieldNames[0] = CITY_STRING ;
    fieldNames[1] = TIME_STRING ;
    fieldNames[2] = WEATHER_STRING ;
    // Changed to fold these into a single weather string.
    //fieldNames[2] = CONDITION_STRING ;
    //fieldNames[3] = TEMPERATURE_STRING ;
    //fieldNames[4] = HUMIDITY_STRING ;
    fieldNames[3] = WEATHER_URL_STRING ;
    return fieldNames;
  }

  // returns the key field names
  public String [] getKeyFieldNames()     {
    String[] fieldNames = new String[1];
    fieldNames[0] = CITY_STRING ;
    return fieldNames;
  }
        
  // Interface to the GUI
  private void displayMessage(String pattern, String message)     {
    if (display != null)
      display.displayMessage(pattern, message);
    else
      System.out.println(pattern + ": " + message);
  }
  



  //--------------------------------------------
  //--------------------------------------------
  // Set your agent-specific DEBUG flags here
  // along with any other agent-specific stuff.
  //--------------------------------------------
  //--------------------------------------------

  private static void setDebugFlags() {

    Parser.DEBUG = false;  // turn Parser debug off now so tasks don't timeout
    CNNCurrentWeatherQueryFn.DEBUG = true;

    // These flags below are used to set up and turn on
    // the CNNCurrentWeatherQueryFn to operate with files on disk instead
    // of actual web pages over the internet. WRITE is used to
    // collect the actual web pages obtained on WeatherCNN queuries
    // into files, 1 per file. READ is used when we want to
    // use those files to simulate reading pages from the web.
    //
    // String filename = path + base + "." + num;
    CNNCurrentWeatherQueryFn.FILE_READ = false;
    CNNCurrentWeatherQueryFn.FILE_READ_PATH = "";
    CNNCurrentWeatherQueryFn.FILE_READ_BASE_NAME = "weatherCNN";
    CNNCurrentWeatherQueryFn.FILE_READ_EXT_NUM = 0;
    CNNCurrentWeatherQueryFn.FILE_WRITE = false;
    CNNCurrentWeatherQueryFn.FILE_WRITE_PATH = "";
    CNNCurrentWeatherQueryFn.FILE_WRITE_BASE_NAME = "weatherCNN";
    CNNCurrentWeatherQueryFn.FILE_WRITE_EXT_NUM = 0;
  }

  private static void setNoDebugFlags() {

    Parser.DEBUG = false;  // turn Parser debug off now so tasks don't timeout
    CNNCurrentWeatherQueryFn.DEBUG = false;

    // These flags below are used to set up and turn on
    // the CNNCurrentWeatherQueryFn to operate with files on disk instead
    // of actual web pages over the internet. WRITE is used to
    // collect the actual web pages obtained on WeatherCNN queuries
    // into files, 1 per file. READ is used when we want to
    // use those files to simulate reading pages from the web.
    //
    // String filename = path + base + "." + num;
    CNNCurrentWeatherQueryFn.FILE_READ = false;
    CNNCurrentWeatherQueryFn.FILE_READ_PATH = "";
    CNNCurrentWeatherQueryFn.FILE_READ_BASE_NAME = "weatherCNN";
    CNNCurrentWeatherQueryFn.FILE_READ_EXT_NUM = 0;
    CNNCurrentWeatherQueryFn.FILE_WRITE = false;
    CNNCurrentWeatherQueryFn.FILE_WRITE_PATH = "";
    CNNCurrentWeatherQueryFn.FILE_WRITE_BASE_NAME = "weatherCNN";
    CNNCurrentWeatherQueryFn.FILE_WRITE_EXT_NUM = 0;
  }
  
}

  
