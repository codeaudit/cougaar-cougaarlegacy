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

package com.prc.alp.liaison.interact;

import com.globalinfotek.coabsgrid.*;
import com.prc.alp.liaison.admin.*;
import com.prc.alp.liaison.plugin.*;
import net.jini.core.entry.Entry;
import net.jini.core.lookup.ServiceMatches;
import java.util.Vector;


/**
 * A manager of interactions involving the CoABS Grid Jini registry lookup service.
 * It supports the lookup of CoABS Grid agents based upon matching a 
 * CoABS-defined agent description.  Because the lookup service operates
 * synchronously (no message passing is involved), most of the functionality
 * of this interactor is contained in its <code>send</code> method, where the
 * lookup request is made and the results are returned.
 *
 * @author Brandon L. Buteau
 * @version 1.1
 * @since 1.0
 */
public class CoABSLookupInteractor implements Interactor {
  
  private CoABSLiaisonDeputy deputy = null;
  private ExternalAgentReference regRef = null;
  private CoABSAgentDescription description = null;
  private String state = null;
  
  public CoABSLookupInteractor (CoABSLiaisonDeputy dep, CoABSAgentDescription desc) {
    deputy = dep;
    description = desc;
  }

  public synchronized Object begin(Object o, Interaction iaction)
    throws ActionTimeoutException, InteractionTimeoutException {
    regRef = (ExternalAgentReference) o;
    state = "SEND";
    if ( !deputy.canInitiateTo(regRef) ) {
      if ( !deputy.waitForPermission() )
        state = "ABORT";
      else {
        do {
          System.out.println("CoABSLookupInteractor waiting for permission");
          iaction.sleep(10000);
        } while ( !deputy.canInitiateTo(regRef) );
      }
    }
    return state;
  }
  
  public synchronized Object send(Object o, Interaction iaction)
    throws ActionTimeoutException, InteractionTimeoutException {
    state = (String) o;
    if ( !state.equals("SEND") )
      state = "ABORT";
    else {
      ServiceMatches sm;
      Vector agentReps = new Vector();
      Entry[] searchTemplate = new Entry[1];
      searchTemplate[0] = description;
      try {
        sm = deputy.getRegistry().lookup(searchTemplate);
        if (sm != null && sm.items != null  && sm.items.length > 0) {
          if ( iaction.getDebug() )
            System.out.println("CoABSLookupInteractor.send():\n" +
                                "Found " + sm.totalMatches + " matches.");
          for (int i=0; i<sm.totalMatches; i++) {
            agentReps.add(sm.items[i].service);
          }
        }
        else if ( iaction.getDebug() )
          System.out.println("CoABSLookupInteractor.send():\n" +
                                "Found no matches");
        deputy.saveReply(iaction.getID(), agentReps);
        state = "RECEIVE";
      }
      catch (Exception ex) {
        iaction.sleep(0);
        System.err.println("CoABSLookupInteractor.send():\n" +
                           "Error when looking for agents\n" + ex.getMessage());
        state = "ABORT";
      }
    }
    return state;
  }
  
  public synchronized Object receive(Object o, Interaction iaction)
    throws ActionTimeoutException, InteractionTimeoutException {
    state = (String) o;
    if ( !state.equals("RECEIVE") )
      state = "ABORT";
    else {
      while ( deputy.getLastReply(iaction.getID()) == null )
        iaction.sleep(100);
      state = "END";
    }
    return state;
  }
  
  public synchronized Object normalEnd(Object o, Interaction iaction)
    throws ActionTimeoutException, InteractionTimeoutException {
    iaction.sleep(0);
    state = (String) o;
    if ( !state.equals("END") )
      return InteractionResult.ERROR;
    else
      return deputy.getLastReply(iaction.getID());
  }
  
  public synchronized void abnormalEnd(Object o, Interaction iaction)
    throws ActionTimeoutException, InteractionTimeoutException {
    iaction.sleep(0);
    System.err.println("CoABSLookupInteractor reached an abnormal state");
  }
  
  public synchronized ActionType nextAction(Object o, Interaction iaction, boolean priorActExpired) {
    if ( o == null )
      return new ActionType("BEGIN");
    else
      return new ActionType((String) o);
  }
    
}