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

package com.prc.alp.liaison.test;

import org.cougaar.core.plugin.SimplePlugIn;
import org.cougaar.core.cluster.IncrementalSubscription;
import java.util.*;
import org.cougaar.util.UnaryPredicate;
import org.cougaar.domain.planning.ldm.plan.*;
import org.cougaar.domain.planning.ldm.asset.Asset;
import org.cougaar.domain.planning.ldm.asset.ItemIdentificationPG;
import org.cougaar.domain.planning.ldm.asset.NewItemIdentificationPG;
import org.cougaar.domain.glm.ldm.asset.Organization;
import org.cougaar.domain.glm.ldm.asset.OrganizationPG;
import com.globalinfotek.coabsgrid.*;
import com.prc.alp.liaison.assets.*;
import com.prc.alp.liaison.plugin.*;
import com.prc.alp.liaison.interact.*;
import com.prc.alp.liaison.admin.*;
import com.prc.alp.liaison.LConstants;
import com.prc.alp.liaison.weather.*;
import net.jini.space.JavaSpace;

/**
 * A <CODE>SimpleLiaisonPlugIn</CODE> that can test performance of a RETSINA
 * weather InfoAgent.  It uses the PlugIn parameter "testCount" to determine how
 * many times to request weather information from the external agent, and the
 * parameter "pause" to determine how long (in seconds) to wait in between
 * requests.
 *
 * @author Brandon L. Buteau
 * @version 1.1
 * @since 1.0
 */
public class LiaisonPlugIn extends SimpleLiaisonPlugIn {

  private Asset what_to_code, what_else_to_code;
  private Asset weather;
  
  /**
   * Does all the test work at the time subscriptions are set up.
   */
  protected void setupSubscriptions() {
  
  System.out.println("Starting liaison test with CoABS Grid");
  
  // Test LiaisonManager
  LiaisonManager mgr = createLiaisonManager();
  CoABSLiaisonDeputy dep = (CoABSLiaisonDeputy) 
                                mgr.createLiaisonDeputy(LConstants.COABS_SOCIETY,
                                                        getDelegate(),
                                                        getParameters());
  mgr.start(true);
  dep.start("ALP_Society", null, true);
  //dep.DEBUG = true;
  
  // go register
  CoABSRegistryInteractor reg = new CoABSRegistryInteractor(dep);
  Interaction regInt = new Interaction(dep, reg, 
                                       ((LiaisonDeputy) dep).externalManager());
  //regInt.setDebug(true);
  regInt.start(true);
  
  // find a weather agent
  CoABSAgentDescription desc = new CoABSAgentDescription();
  desc.name = WConstants.AGENT_NAMES[WConstants.RETSINA_WCN_FORECAST_AGENT];
  CoABSLookupInteractor lookup = new CoABSLookupInteractor(dep, desc);
  Interaction lookInt = new Interaction(dep, lookup,
                                        ((LiaisonDeputy) dep).externalManager());
  //lookInt.setDebug(true);
  System.out.println("Going to try finding " + desc.name);
  Vector result = (Vector) lookInt.start(true);
  if ( result == null || result.isEmpty() )
    System.out.println("No weather agent found");
  else
    System.out.println("Found " + ((AgentRep) result.firstElement()).getName());
  
  // go get the weather
  GregorianCalendar cal = new GregorianCalendar(2000,11,11);
  String limit = LiaisonManager.findLiaisonParameter(getParameters(),"testCount");
  if ( limit == null )
    limit = "10";
  int queryLimit = (new Integer(limit)).intValue();
  String pause = LiaisonManager.findLiaisonParameter(getParameters(),"pause");
  if ( pause == null )
    pause = "5"; // seconds
  int testPause = (new Integer(pause)).intValue() * 1000;
  long totalTime = 0;
  long startTime;
  for ( int i = 0; i < queryLimit; i++ ) {
    RETSINAWeatherInteractor wxagent =
      new RETSINAWeatherInteractor(dep, ((AgentRep) result.firstElement()),
                                   cal.getTime().getTime(), cal.getTime().getTime()+1000*60*60*24);
    int index = (int) (Math.random() * WConstants.RETSINAWCNLocationMap.length);
    String place = WConstants.RETSINAWCNLocationMap[index][1];
    Interaction wxInt = new Interaction(dep, wxagent, place);
    //wxInt.setDebug(true);
    startTime = System.currentTimeMillis();
    ForecastConditions forecast = (ForecastConditions) wxInt.start(true);
    totalTime += System.currentTimeMillis() - startTime;
    System.out.println(forecast.place + ": " + forecast.conditions);
    try {
      Thread.sleep(testPause);
    } catch (InterruptedException ex) { /* don't care why */ }
  }
  System.out.println("\n" + queryLimit + " total queries @ " +
                     (totalTime / (1000.0 * queryLimit)) + " sec/query");
}


/**
 * Does nothing because this PlugIn has no subscriptions.
 */
protected void execute () {
}


}
