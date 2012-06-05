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

import net.jini.core.entry.Entry;
import java.util.GregorianCalendar;

/**
 * Describes forecasted weather conditions at a specified place and time as of
 * a particular time when the forecast was made.
 *
 * @author Brandon L. Buteau
 * @version 1.1
 * @since 1.0
 */
public class ForecastConditions implements Entry {
  
    public String place = null;
    public GregorianCalendar asOfDate = null;
    public GregorianCalendar onDate = null;
    public String conditions = null;

    public ForecastConditions () {
    }
    
    public static ForecastConditions create (String place,
                                             GregorianCalendar asOfDate,
                                             GregorianCalendar onDate,
                                             String conditions) {
      ForecastConditions result = new ForecastConditions();
      result.place = place;
      result.asOfDate = asOfDate;
      result.onDate = onDate;
      result.conditions = conditions;
      return result;
    }
    
    public String toString() {
      return 
        "<ForecastConditions at:" + place +
        " onDate:" + WConstants.DATE_FORMAT.format(onDate.getTime()) +
        " asOfDate:" + WConstants.DATE_FORMAT.format(asOfDate.getTime()) +
        " conditions:" + conditions + ">";
    }

}