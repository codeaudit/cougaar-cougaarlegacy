/*
 * <copyright>
 * Copyright 1997-2000 Defense Advanced Research Projects Agency (DARPA)
 * and ALPINE (A BBN Technologies (BBN) and Raytheon Systems Company
 * (RSC) Consortium). This software to be used in accordance with the
 * COUGAAR license agreement.  The license agreement and other
 * information on the Cognitive Agent Architecture (COUGAAR) Project can
 * be found at http://www.cougaar.org or email: info@cougaar.org.
 * </copyright>
 */

// source machine generated at Mon Apr 30 14:11:09 EDT 2001 - Do not edit
/* @generated */
/** AbstractFactory implementation for Properties.
 * Prevents clients from needing to know the implementation
 * class(es) of any of the properties.
 **/

package com.prc.alp.liaison.assets;

import org.cougaar.domain.planning.ldm.measure.*;
import org.cougaar.domain.planning.ldm.asset.*;
import org.cougaar.domain.planning.ldm.plan.*;
import java.util.*;



public class PropertyGroupFactory {
  // brand-new instance factory
  public static NewWeatherForecastPG newWeatherForecastPG() {
    return new WeatherForecastPGImpl();
  }
  // instance from prototype factory
  public static NewWeatherForecastPG newWeatherForecastPG(WeatherForecastPG prototype) {
    return new WeatherForecastPGImpl(prototype);
  }

  /** Abstract introspection information.
   * Tuples are {<classname>, <factorymethodname>}
   * return value of <factorymethodname> is <classname>.
   * <factorymethodname> takes zero or one (prototype) argument.
   **/
  public static String properties[][]={
    {"com.prc.alp.liaison.assets.WeatherForecastPG", "newWeatherForecastPG"},
  };
}
