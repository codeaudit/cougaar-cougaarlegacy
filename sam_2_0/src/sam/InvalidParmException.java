package sam;
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
public class InvalidParmException extends Exception
{
   String message;

   /**
    * Throws an invalid Exception.<p>
    * @param msg String.<p>
    */
   public InvalidParmException(String msg)
   {
      message = msg;
   }
/*
   public String toString()
   {
      return "Invalid Parameter Execption: " + message;
   }
*/
}
