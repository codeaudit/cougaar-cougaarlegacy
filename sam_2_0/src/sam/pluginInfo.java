

package sam;

import java.util.*;
import java.util.Vector;
import org.cougaar.core.util.Operator;
/**
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
// A container to hold info about a specific plugin.

public class pluginInfo
{
   // A list of contractInfo records for our subscriptions
   public Vector subscribeContracts;

   // A list of contractInfo records for our publications
   public Vector publishContracts;

   // This plugin's unique name
   public String pluginName;

   // A constructor
   // subscribe is a list of Operator records describing the contract subscriptions.
   // publish is a list of Operator records describing the contract publications.
   // name is the (hopefully unique) name of this plugin
   public pluginInfo(Vector subscribe, Vector publish, String name)
   {
      // Add the subscriptions
      Iterator it = subscribe.iterator();
      while(it.hasNext())
          subscribeContracts.add(new contractInfo(false, (Operator)it.next(), this));

      // Add the publications
      it = publish.iterator();
      while(it.hasNext())
          publishContracts.add(new contractInfo(true, (Operator)it.next(), this));

      pluginName = name;
   }
}
