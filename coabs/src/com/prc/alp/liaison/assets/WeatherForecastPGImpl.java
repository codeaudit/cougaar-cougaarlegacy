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
/** Implementation of WeatherForecastPG.
 *  @see WeatherForecastPG
 *  @see NewWeatherForecastPG
 **/

package com.prc.alp.liaison.assets;

import org.cougaar.domain.planning.ldm.measure.*;
import org.cougaar.domain.planning.ldm.asset.*;
import org.cougaar.domain.planning.ldm.plan.*;
import java.util.*;


import java.util.Date;

import java.io.ObjectOutputStream;
import java.io.ObjectInputStream;
import java.io.IOException;
import java.beans.PropertyDescriptor;
import java.beans.IndexedPropertyDescriptor;
import java.util.Date;

public class WeatherForecastPGImpl extends java.beans.SimpleBeanInfo
  implements NewWeatherForecastPG, Cloneable
{
  public WeatherForecastPGImpl() {
  };

  // Slots

  private String theLocation;
  public String getLocation(){ return theLocation; }
  public void setLocation(String location) {
    if (location!=null) location=location.intern();
    theLocation=location;
  }
  private Date theDate;
  public Date getDate(){ return theDate; }
  public void setDate(Date date) {
    theDate=date;
  }
  private Date theAsof;
  public Date getAsof(){ return theAsof; }
  public void setAsof(Date asof) {
    theAsof=asof;
  }
  private String theCondition;
  public String getCondition(){ return theCondition; }
  public void setCondition(String condition) {
    if (condition!=null) condition=condition.intern();
    theCondition=condition;
  }


  public WeatherForecastPGImpl(WeatherForecastPG original) {
    theLocation = original.getLocation();
    theDate = original.getDate();
    theAsof = original.getAsof();
    theCondition = original.getCondition();
  }

  public boolean hasDataQuality() { return false; }
  public org.cougaar.domain.planning.ldm.dq.DataQuality getDataQuality() { return null; }

  // static inner extension class for real DataQuality Support
  public final static class DQ extends WeatherForecastPGImpl implements org.cougaar.domain.planning.ldm.dq.NewHasDataQuality {
   public DQ() {
    super();
   }
   public DQ(WeatherForecastPG original) {
    super(original);
   }
   public Object clone() { return new DQ(this); }
   private transient org.cougaar.domain.planning.ldm.dq.DataQuality _dq = null;
   public boolean hasDataQuality() { return (_dq!=null); }
   public org.cougaar.domain.planning.ldm.dq.DataQuality getDataQuality() { return _dq; }
   public void setDataQuality(org.cougaar.domain.planning.ldm.dq.DataQuality dq) { _dq=dq; }
   private void writeObject(ObjectOutputStream out) throws IOException {
    out.defaultWriteObject();
    if (out instanceof org.cougaar.core.cluster.persist.PersistenceOutputStream) out.writeObject(_dq);
   }
   private void readObject(ObjectInputStream in) throws ClassNotFoundException, IOException {
    in.defaultReadObject();
    if (in instanceof org.cougaar.core.cluster.persist.PersistenceInputStream) _dq=(org.cougaar.domain.planning.ldm.dq.DataQuality)in.readObject();
   }
    
    private final static PropertyDescriptor properties[]=new PropertyDescriptor[1];
    static {
      try {
        properties[0]= new PropertyDescriptor("dataQuality", DQ.class, "getDataQuality", null);
      } catch (Exception e) { e.printStackTrace(); }
    }
    public PropertyDescriptor[] getPropertyDescriptors() {
      PropertyDescriptor[] pds = super.properties;
      PropertyDescriptor[] ps = new PropertyDescriptor[pds.length+properties.length];
      System.arraycopy(pds, 0, ps, 0, pds.length);
      System.arraycopy(properties, 0, ps, pds.length, properties.length);
      return ps;
    }
  }


  private transient WeatherForecastPG _locked = null;
  public PropertyGroup lock(Object key) {
    if (_locked == null)
      _locked = new _Locked(key);
    return _locked; }
  public PropertyGroup lock() { return lock(null); }
  public NewPropertyGroup unlock(Object key) { return this; }

  public Object clone() throws CloneNotSupportedException {
    WeatherForecastPGImpl _tmp = new WeatherForecastPGImpl(this);
    return _tmp;
  }

  public PropertyGroup copy() {
    try {
      return (PropertyGroup) clone();
    } catch (CloneNotSupportedException cnse) { return null;}
  }

  public Class getPrimaryClass() {
    return primaryClass;
  }
  public String getAssetGetMethod() {
    return assetGetter;
  }
  public String getAssetSetMethod() {
    return assetSetter;
  }

  private void readObject(ObjectInputStream in) throws ClassNotFoundException, IOException {
    in.defaultReadObject();
    if (theLocation!= null) theLocation=theLocation.intern();
    if (theCondition!= null) theCondition=theCondition.intern();
  }

  private final static PropertyDescriptor properties[] = new PropertyDescriptor[4];
  static {
    try {
      properties[0]= new PropertyDescriptor("location", WeatherForecastPG.class, "getLocation", null);
      properties[1]= new PropertyDescriptor("date", WeatherForecastPG.class, "getDate", null);
      properties[2]= new PropertyDescriptor("asof", WeatherForecastPG.class, "getAsof", null);
      properties[3]= new PropertyDescriptor("condition", WeatherForecastPG.class, "getCondition", null);
    } catch (Exception e) { System.err.println("Caught: "+e); e.printStackTrace(); }
  };

  public PropertyDescriptor[] getPropertyDescriptors() {
    return properties;
  }
  private final class _Locked extends java.beans.SimpleBeanInfo
    implements WeatherForecastPG, Cloneable, LockedPG
  {
    private transient Object theKey = null;
    _Locked(Object key) { 
      if (this.theKey == null){  
        this.theKey = key; 
      } 
    }  

    /** public constructor for beaninfo - probably wont work**/
    public _Locked() {}

    public PropertyGroup lock() { return this; }
    public PropertyGroup lock(Object o) { return this; }

    public NewPropertyGroup unlock(Object key) throws IllegalAccessException {
       if( theKey.equals(key) )
         return WeatherForecastPGImpl.this;
       else 
         throw new IllegalAccessException("unlock: mismatched internal and provided keys!");
    }

    public PropertyGroup copy() {
      return new WeatherForecastPGImpl(WeatherForecastPGImpl.this);
    }

    public Object clone() throws CloneNotSupportedException {
      return new WeatherForecastPGImpl(WeatherForecastPGImpl.this);
    }

    public String getLocation() { return WeatherForecastPGImpl.this.getLocation(); }
    public Date getDate() { return WeatherForecastPGImpl.this.getDate(); }
    public Date getAsof() { return WeatherForecastPGImpl.this.getAsof(); }
    public String getCondition() { return WeatherForecastPGImpl.this.getCondition(); }
  public final boolean hasDataQuality() { return WeatherForecastPGImpl.this.hasDataQuality(); }
  public final org.cougaar.domain.planning.ldm.dq.DataQuality getDataQuality() { return WeatherForecastPGImpl.this.getDataQuality(); }
    public Class getPrimaryClass() {
      return primaryClass;
    }
    public String getAssetGetMethod() {
      return assetGetter;
    }
    public String getAssetSetMethod() {
      return assetSetter;
    }

    public PropertyDescriptor[] getPropertyDescriptors() {
      return properties;
    }

    public Class getIntrospectionClass() {
      return WeatherForecastPGImpl.class;
    }

  }

}
