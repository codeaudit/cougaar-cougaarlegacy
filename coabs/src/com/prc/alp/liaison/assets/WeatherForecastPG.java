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
/** Primary client interface for WeatherForecastPG.
 *  @see NewWeatherForecastPG
 *  @see WeatherForecastPGImpl
 **/

package com.prc.alp.liaison.assets;

import org.cougaar.domain.planning.ldm.measure.*;
import org.cougaar.domain.planning.ldm.asset.*;
import org.cougaar.domain.planning.ldm.plan.*;
import java.util.*;


import java.util.Date;

public interface WeatherForecastPG extends PropertyGroup, org.cougaar.domain.planning.ldm.dq.HasDataQuality {
  /** A place name **/
  String getLocation();
  /** The date for which the weather state is forecast **/
  Date getDate();
  /** The date when the forecast was generated **/
  Date getAsof();
  /** Description of general state of weather **/
  String getCondition();

  // introspection and construction
  /** the method of factoryClass that creates this type **/
  public static final String factoryMethod = "newWeatherForecastPG";
  /** the (mutable) class type returned by factoryMethod **/
  public static final String mutableClass = "com.prc.alp.liaison.assets.NewWeatherForecastPG";
  /** the factory class **/
  public static final Class factoryClass = com.prc.alp.liaison.assets.PropertyGroupFactory.class;
  /** the (immutable) class type returned by domain factory **/
  public static final Class primaryClass = com.prc.alp.liaison.assets.WeatherForecastPG.class;
  public static final String assetSetter = "setWeatherForecastPG";
  public static final String assetGetter = "getWeatherForecastPG";
  /** The Null instance for indicating that the PG definitely has no value **/
  public static final WeatherForecastPG nullPG = new Null_WeatherForecastPG();

/** Null_PG implementation for WeatherForecastPG **/
static final class Null_WeatherForecastPG
  implements WeatherForecastPG, Null_PG
{
  public String getLocation() { throw new UndefinedValueException(); }
  public Date getDate() { throw new UndefinedValueException(); }
  public Date getAsof() { throw new UndefinedValueException(); }
  public String getCondition() { throw new UndefinedValueException(); }
  public Object clone() throws CloneNotSupportedException {
    throw new CloneNotSupportedException();
  }
  public NewPropertyGroup unlock(Object key) { return null; }
  public PropertyGroup lock(Object key) { return null; }
  public PropertyGroup lock() { return null; }
  public PropertyGroup copy() { return null; }
  public Class getPrimaryClass(){return primaryClass;}
  public String getAssetGetMethod() {return assetGetter;}
  public String getAssetSetMethod() {return assetSetter;}
  public Class getIntrospectionClass() {
    return WeatherForecastPGImpl.class;
  }

  public boolean hasDataQuality() { return false; }
  public org.cougaar.domain.planning.ldm.dq.DataQuality getDataQuality() { return null; }
}

/** Future PG implementation for WeatherForecastPG **/
public final static class Future
  implements WeatherForecastPG, Future_PG
{
  public String getLocation() {
    waitForFinalize();
    return _real.getLocation();
  }
  public Date getDate() {
    waitForFinalize();
    return _real.getDate();
  }
  public Date getAsof() {
    waitForFinalize();
    return _real.getAsof();
  }
  public String getCondition() {
    waitForFinalize();
    return _real.getCondition();
  }
  public Object clone() throws CloneNotSupportedException {
    throw new CloneNotSupportedException();
  }
  public NewPropertyGroup unlock(Object key) { return null; }
  public PropertyGroup lock(Object key) { return null; }
  public PropertyGroup lock() { return null; }
  public PropertyGroup copy() { return null; }
  public Class getPrimaryClass(){return primaryClass;}
  public String getAssetGetMethod() {return assetGetter;}
  public String getAssetSetMethod() {return assetSetter;}
  public Class getIntrospectionClass() {
    return WeatherForecastPGImpl.class;
  }
  public synchronized boolean hasDataQuality() {
    return (_real!=null) && _real.hasDataQuality();
  }
  public synchronized org.cougaar.domain.planning.ldm.dq.DataQuality getDataQuality() {
    return (_real==null)?null:(_real.getDataQuality());
  }

  // Finalization support
  private WeatherForecastPG _real = null;
  public synchronized void finalize(PropertyGroup real) {
    if (real instanceof WeatherForecastPG) {
      _real=(WeatherForecastPG) real;
      notifyAll();
    } else {
      throw new IllegalArgumentException("Finalization with wrong class: "+real);
    }
  }
  private synchronized void waitForFinalize() {
    while (_real == null) {
      try {
        wait();
      } catch (InterruptedException _ie) {}
    }
  }
}
}
