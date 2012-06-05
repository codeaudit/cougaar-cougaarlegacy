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

// source machine generated at Mon Apr 30 14:11:10 EDT 2001 - Do not edit
/* @generated */
package com.prc.alp.liaison.assets;
import org.cougaar.domain.planning.ldm.asset.*;
import java.io.ObjectOutputStream;
import java.io.ObjectInputStream;
import java.io.IOException;
import java.util.Vector;
import java.beans.PropertyDescriptor;
import java.beans.IndexedPropertyDescriptor;
import java.beans.IntrospectionException;
/** Representation of the state of weather at a named location, sometime **/

public class WeatherAsset extends org.cougaar.domain.planning.ldm.asset.Asset {

  public WeatherAsset() {
    myItemIdentificationPG = null;
    myWeatherForecastPG = null;
  }

  public WeatherAsset(WeatherAsset prototype) {
    super(prototype);
    myItemIdentificationPG=null;
    myWeatherForecastPG=null;
  }

  /** For infrastructure only - use org.cougaar.domain.planning.ldm.Factory.copyInstance instead. **/
  public Object clone() throws CloneNotSupportedException {
    WeatherAsset _thing = (WeatherAsset) super.clone();
    if (myItemIdentificationPG!=null) _thing.setItemIdentificationPG(myItemIdentificationPG.lock());
    if (myWeatherForecastPG!=null) _thing.setWeatherForecastPG(myWeatherForecastPG.lock());
    return _thing;
  }

  /** create an instance of the right class for copy operations **/
  public Asset instanceForCopy() {
    return new WeatherAsset();
  }

  /** create an instance of this prototype **/
  public Asset createInstance() {
    return new WeatherAsset(this);
  }

  protected void fillAllPropertyGroups(Vector v) {
    super.fillAllPropertyGroups(v);
    { Object _tmp = getItemIdentificationPG();
    if (_tmp != null && !(_tmp instanceof Null_PG)) {
      v.addElement(_tmp);
    } }
    { Object _tmp = getWeatherForecastPG();
    if (_tmp != null && !(_tmp instanceof Null_PG)) {
      v.addElement(_tmp);
    } }
  }

  private transient ItemIdentificationPG myItemIdentificationPG;

  public ItemIdentificationPG getItemIdentificationPG() {
    ItemIdentificationPG _tmp = (myItemIdentificationPG != null) ?
      myItemIdentificationPG : (ItemIdentificationPG)resolvePG(ItemIdentificationPG.class);
    return (_tmp == ItemIdentificationPG.nullPG)?null:_tmp;
  }
  public void setItemIdentificationPG(PropertyGroup arg_ItemIdentificationPG) {
    if (!(arg_ItemIdentificationPG instanceof ItemIdentificationPG))
      throw new IllegalArgumentException("setItemIdentificationPG requires a ItemIdentificationPG argument.");
    myItemIdentificationPG = (ItemIdentificationPG) arg_ItemIdentificationPG;
  }

  private transient WeatherForecastPG myWeatherForecastPG;

  public WeatherForecastPG getWeatherForecastPG() {
    WeatherForecastPG _tmp = (myWeatherForecastPG != null) ?
      myWeatherForecastPG : (WeatherForecastPG)resolvePG(WeatherForecastPG.class);
    return (_tmp == WeatherForecastPG.nullPG)?null:_tmp;
  }
  public void setWeatherForecastPG(PropertyGroup arg_WeatherForecastPG) {
    if (!(arg_WeatherForecastPG instanceof WeatherForecastPG))
      throw new IllegalArgumentException("setWeatherForecastPG requires a WeatherForecastPG argument.");
    myWeatherForecastPG = (WeatherForecastPG) arg_WeatherForecastPG;
  }

  // generic search methods
  public PropertyGroupSchedule searchForPropertyGroupSchedule(Class c) {
    return super.searchForPropertyGroupSchedule(c);
  }

  public PropertyGroup getLocalPG(Class c, long t) {
    if (ItemIdentificationPG.class.equals(c)) {
      return (myItemIdentificationPG==ItemIdentificationPG.nullPG)?null:myItemIdentificationPG;
    }
    if (WeatherForecastPG.class.equals(c)) {
      return (myWeatherForecastPG==WeatherForecastPG.nullPG)?null:myWeatherForecastPG;
    }
    return super.getLocalPG(c,t);
  }

  public void setLocalPG(Class c, PropertyGroup pg) {
    if (ItemIdentificationPG.class.equals(c)) {
      myItemIdentificationPG=(ItemIdentificationPG)pg;
    } else
    if (WeatherForecastPG.class.equals(c)) {
      myWeatherForecastPG=(WeatherForecastPG)pg;
    } else
      super.setLocalPG(c,pg);
  }

  public PropertyGroup generateDefaultPG(Class c) {
    if (ItemIdentificationPG.class.equals(c)) {
      return (myItemIdentificationPG= new ItemIdentificationPGImpl());
    } else
    if (WeatherForecastPG.class.equals(c)) {
      return (myWeatherForecastPG= new WeatherForecastPGImpl());
    } else
      return super.generateDefaultPG(c);
  }

  // dumb serialization methods

  private void writeObject(ObjectOutputStream out) throws IOException {
    out.defaultWriteObject();
      if (myItemIdentificationPG instanceof Null_PG || myItemIdentificationPG instanceof Future_PG) {
        out.writeObject(null);
      } else {
        out.writeObject(myItemIdentificationPG);
      }
      if (myWeatherForecastPG instanceof Null_PG || myWeatherForecastPG instanceof Future_PG) {
        out.writeObject(null);
      } else {
        out.writeObject(myWeatherForecastPG);
      }
  }

  private void readObject(ObjectInputStream in) throws ClassNotFoundException, IOException {
    in.defaultReadObject();
      myItemIdentificationPG=(ItemIdentificationPG)in.readObject();
      myWeatherForecastPG=(WeatherForecastPG)in.readObject();
  }
  // beaninfo support
  private static PropertyDescriptor properties[];
  static {
    try {
      properties = new PropertyDescriptor[2];
      properties[0] = new PropertyDescriptor("ItemIdentificationPG", WeatherAsset.class, "getItemIdentificationPG", null);
      properties[1] = new PropertyDescriptor("WeatherForecastPG", WeatherAsset.class, "getWeatherForecastPG", null);
    } catch (IntrospectionException ie) {}
  }

  public PropertyDescriptor[] getPropertyDescriptors() {
    PropertyDescriptor[] pds = super.getPropertyDescriptors();
    PropertyDescriptor[] ps = new PropertyDescriptor[pds.length+2];
    System.arraycopy(pds, 0, ps, 0, pds.length);
    System.arraycopy(properties, 0, ps, pds.length, 2);
    return ps;
  }
}
