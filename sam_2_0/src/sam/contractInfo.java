
package sam;

import java.util.Vector;
import org.cougaar.core.util.Operator;

/**
 * class: contractInfo holds the information for a single contract for the plugin.<p>
 * Title:        Sam<p>
 * Description:  ALP Business Process User Interface<p>
 * <copyright>
 *    Copyright (c) 2000-2001 Defense Advanced Research Projects
 *    Agency (DARPA) and Mobile Intelligence Corporation.
 *    This software to be used only in accordance with the
 *    COUGAAR license agreement.
 * </copyright>
 * Company:      Mobile Intelligence Corp.<p>
 * @author Doug MacKenzie
 * @version 1.0
 */

/**********************************************************/
// A container to hold the info for a single contract for plugins

public class contractInfo
{
   // Is this a publish (true) or a subscribe (false) contract?
   /**
    * Returns true if the contract is publish else returns false.<p>
    */
   public boolean isPublish;

   // Our parent plugin making this contract
   /**
    * Returns the plugin information about the plugin that makes this contract.<P>
    */
   public pluginInfo thePlugin;

   // The contract
   /**
    * The Contract.<P>
    */
   public Operator theContract;

   // A list of contractInfo records for related contracts.
   // If this is a publish, then these are subscriptions we fulfill.
   // If this is a subscribe, then these are publications fulfilling us.
   /**
    * A vector that holds the list of all the contractInfo records for the related contracts.<p>
    */
   public Vector relatedContracts;

   // A constructor
   /**
    * Constructor.
    */
   public contractInfo(boolean is_publish, Operator the_contract, pluginInfo the_plugin)
   {
      isPublish = is_publish;
      theContract = the_contract;
      thePlugin = the_plugin;
   }
}
