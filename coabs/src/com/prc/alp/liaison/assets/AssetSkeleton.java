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
/** Abstract Asset Skeleton implementation
 * Implements default property getters, and additional property
 * lists.
 * Intended to be extended by org.cougaar.domain.planning.ldm.asset.Asset
 **/

package com.prc.alp.liaison.assets;

import org.cougaar.domain.planning.ldm.measure.*;
import org.cougaar.domain.planning.ldm.asset.*;
import org.cougaar.domain.planning.ldm.plan.*;
import java.util.*;


import java.io.Serializable;
import java.beans.PropertyDescriptor;
import java.beans.IndexedPropertyDescriptor;

public abstract class AssetSkeleton extends org.cougaar.domain.planning.ldm.asset.Asset {

  protected AssetSkeleton() {}

  protected AssetSkeleton(AssetSkeleton prototype) {
    super(prototype);
  }

  /**                 Default PG accessors               **/

  /** Search additional properties for a WeatherForecastPG instance.
   * @return instance of WeatherForecastPG or null.
   **/
  public WeatherForecastPG getWeatherForecastPG()
  {
    WeatherForecastPG _tmp = (WeatherForecastPG) resolvePG(WeatherForecastPG.class);
    return (_tmp==WeatherForecastPG.nullPG)?null:_tmp;
  }

  /** Test for existence of a WeatherForecastPG
   **/
  public boolean hasWeatherForecastPG() {

    return (getWeatherForecastPG() != null);
  }

  /** Set the WeatherForecastPG property.
   * The default implementation will create a new WeatherForecastPG
   * property and add it to the otherPropertyGroup list.
   * Many subclasses override with local slots.
   **/
  public void setWeatherForecastPG(PropertyGroup aWeatherForecastPG) {
    if (aWeatherForecastPG == null) {
      removeOtherPropertyGroup(WeatherForecastPG.class);
    } else {
      addOtherPropertyGroup(aWeatherForecastPG);
    }
  }

}
