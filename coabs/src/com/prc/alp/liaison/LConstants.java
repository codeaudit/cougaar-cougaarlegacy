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

package com.prc.alp.liaison;


/**
 * A convenience class for organizing all of the configurable constants
 * associated with the ALP/Cougaar external agent liaison capability.
 *
 * @author  Brandon L. Buteau
 * @version 1.1
 * @since   1.0 
 */
public final class LConstants extends Object {

    public LConstants() {
    }

    // Liaison management constants
    public static final int ALP_SOCIETY = 1;
    public static final int COABS_SOCIETY = 2;
    public static final String [] JINI_GROUPS = { "public" };
    public static final boolean DEFAULT_LIAISON_STATUS = false;
    public static final long DEFAULT_INTERACTION_TIME_LIMIT = 1000 * 60 * 5;
    public static final long FOREVER = -1;
    
  /**
   * A convenience interface for organizing all of the configurable constants
   * associated with the ALP/Cougaar external agent liaisons, including
   * those related to liaison administration, {@link com.prc.alp.liaison.plugin.LiaisonManager}, and
   * {@link com.prc.alp.liaison.plugin.LiaisonDeputy} functions.
   */
    public static interface ALP {
      
      public static final String DEFAULT_SOCIETY_NAME = "ALP_Society";
      public static final String DEFAULT_COMMUNITY_NAME = "Community";
      public static final String DEFAULT_LOCATOR = "jini://localhost:4160/";

      public static final String SERVICE_ID_FILE_PREFIX = "Liaison_";

      public static final String CONFIG_PATH_PROPERTY = "org.cougaar.config.liaison.path";
      public static final String LOCATOR_PROPERTY = "com.prc.alp.liaison.locator";
      public static final String WAIT_PROPERTY = "com.prc.alp.liaison.wait";
      public static final String TIME_LIMIT_PROPERTY = "com.prc.alp.liaison.timeLimit";
      public static final String AGENT_PROPERTY = "com.prc.alp.liaison.agent";
      public static final String COMMUNITY_PROPERTY = "com.prc.alp.liaison.community";
      public static final String SOCIETY_PROPERTY = "com.prc.alp.liaison.society";

      public static final String LOCATOR_PARAMETER = "myLocator";
      public static final String WAIT_PARAMETER = "wait";
      public static final String WAIT_ALLOWED = "true";
      public static final String TIME_LIMIT_PARAMETER = "timeLimit";
      public static final String AGENT_PARAMETER = "myName";
      public static final String COMMUNITY_PARAMETER = "myCommunity";
      public static final String SOCIETY_PARAMETER = "mySociety";

    }
    
  /**
   * A convenience interface for organizing all of the configurable constants
   * associated with the ALP/Cougaar external agent liaisons related to the
   * CoABS Grid, including {@link com.prc.alp.liaison.plugin.CoABSLiaisonDeputy} functions.
   */
    public static interface CoABS {

      public static final String DEFAULT_GRID_NAME = "CoABSGrid";
      public static final String DEFAULT_REGISTRY_NAME = "Registry (LookupDiscovery)";
      
      public static final String SERVICE_ID_FILE_PREFIX = "CoABS_";
      
      public static final String LOCATOR_PROPERTY = "com.prc.alp.liaison.coabs.locator";
      public static final String SOCIETY_NAME_PROPERTY = "com.prc.alp.liaison.coabs.society";
      public static final String LOG_PROPERTY = "com.prc.alp.liaison.coabs.useLogger";
      
      public static final String LOCATOR_PARAMETER = "coabsLocator";
      public static final String SOCIETY_PARAMETER = "coabsSociety";
      public static final String LOG_PARAMETER = "useLogger";
      public static final String USE_LOGGER = "true";
    
    }
    
    
}