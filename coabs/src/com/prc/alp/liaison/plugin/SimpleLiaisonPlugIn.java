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

package com.prc.alp.liaison.plugin;

import org.cougaar.core.plugin.PlugInAdapter;
import org.cougaar.core.plugin.SimplePlugIn;
import com.prc.alp.liaison.LConstants;
import java.util.Enumeration;
import java.util.Vector;

/**
 * A SimplePlugIn that runs in its own thread and that implements support for
 * liaisons with external agents through the <CODE>LiaisonManager</CODE>
 * interface.  It also knows how to parse PlugIn parameters that identify it as
 * an agent to external agents.
 *
 * @author Brandon L. Buteau
 * @version 1.1
 * @since 1.0
 */
public abstract class SimpleLiaisonPlugIn
  extends SimplePlugIn
  implements Liaison
{
  protected LiaisonManager manager = null;
  
  private String myName = null;
  private String myCommunity = null;
  private String mySociety = null;
  private String myLocator = null;
  
  public void SimpleLiaisonPlugIn () {
    chooseThreadingModel(PlugInAdapter.SINGLE_THREAD);
  }
  
  public LiaisonManager createLiaisonManager () {
    parseLiaisonManagerParameters(getParameters());
    if ( myName == null )
      manager = new LiaisonManager(this.getCluster(), myCommunity, mySociety,
                                   myLocator);
    else
      manager = new LiaisonManager(myName, myCommunity, mySociety,
                                   myLocator);
    return manager;
  }
  
  public LiaisonManager createLiaisonManager (String mySociety) {
    parseLiaisonManagerParameters(getParameters());
    manager = new LiaisonManager(this.getCluster(), myCommunity, mySociety,
                                 myLocator);
    return manager;
  }
  
  public LiaisonManager createLiaisonManager (String myCommunity,
                                              String mySociety) {
    parseLiaisonManagerParameters(getParameters());
    manager = new LiaisonManager(this.getCluster(), myCommunity, mySociety,
                                 myLocator);
    return manager;
  }
    
  public LiaisonManager createLiaisonManager (String myName,
                                              String myCommunity,
                                              String mySociety) {
    parseLiaisonManagerParameters(getParameters());
    manager = new LiaisonManager(myName, myCommunity, mySociety, myLocator);
    return manager;
  }
  
  public  LiaisonManager getManager () {
    return manager;
  }
  
  private void parseLiaisonManagerParameters(Vector params) {
    myName = LiaisonManager.findLiaisonParameter(params, 
                                                 LConstants.ALP.AGENT_PARAMETER);
    if ( myName == null )
      myName = System.getProperty(LConstants.ALP.AGENT_PROPERTY);
    myCommunity = LiaisonManager.findLiaisonParameter(params,
                                                      LConstants.ALP.COMMUNITY_PARAMETER);
    if ( myCommunity == null )
      myCommunity = System.getProperty(LConstants.ALP.COMMUNITY_PROPERTY,
                                       LConstants.ALP.DEFAULT_COMMUNITY_NAME);
    mySociety = LiaisonManager.findLiaisonParameter(params,
                                                    LConstants.ALP.SOCIETY_PARAMETER);
    if ( mySociety == null )
      mySociety = System.getProperty(LConstants.ALP.SOCIETY_PROPERTY,
                                     LConstants.ALP.DEFAULT_SOCIETY_NAME);
    myLocator = LiaisonManager.findLiaisonParameter(params,
                                                    LConstants.ALP.LOCATOR_PARAMETER);
    if ( myLocator == null )
      myLocator = System.getProperty(LConstants.ALP.LOCATOR_PROPERTY,
                                     LConstants.ALP.DEFAULT_LOCATOR);
  }
        
}

