

package sam.LoadContractFiles;

import org.cougaar.core.util.Operator;
import org.cougaar.core.util.OperatorFactory;
//import alp.util.SetRelationship;

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
public class contractRecord
{
   // Is this a publish contract?

   boolean publish;

   // Our contract.
   Operator contract;

   /**
    * Returns a contractRecord.<p>
    * @param isPublish boolean, theContract Operator.<p>
    */
   public contractRecord(boolean isPublish, Operator theContract)
   {
      publish = isPublish;
      contract = theContract;
   }
   /**
    * Returns true if the contract is a publish contract else false.<p>
    */

   public boolean isPublish()
   {
      return publish;
   }

   /**
    * Returns Operator.<p>
    */
   public Operator getContract()
   {
      return contract;
   }

}
