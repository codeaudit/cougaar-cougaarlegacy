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
/** Additional methods for WeatherForecastPG
 * offering mutators (set methods) for the object's owner
 **/

package com.prc.alp.liaison.assets;

import org.cougaar.domain.planning.ldm.measure.*;
import org.cougaar.domain.planning.ldm.asset.*;
import org.cougaar.domain.planning.ldm.plan.*;
import java.util.*;


import java.util.Date;

import java.util.Date;

public interface NewWeatherForecastPG extends WeatherForecastPG, NewPropertyGroup, org.cougaar.domain.planning.ldm.dq.HasDataQuality {
  void setLocation(String location);
  void setDate(Date date);
  void setAsof(Date asof);
  void setCondition(String condition);
}
