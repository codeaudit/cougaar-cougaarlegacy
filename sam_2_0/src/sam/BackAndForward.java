
package sam;

import sam.LoadContractFiles.contractOwnerBase;
import java.util.*;



/**
 * Class: BackAndForward. Used for moving to the previous and the next pages in the file.<p>
 * Theory of operation: The current record matches the one currently being displayed.<p>
 * Title: Sam<p>
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


public class BackAndForward
{

   /**
    * To hold the Previously visited display components in the vector
    */
   ArrayList history;

   final int MaxSize = 100;


   /**
    * Index of the empty record, just before the old record
    */
   int emptySlot;


   /**
    * Index of the newest record
    */
   int newest;
   /**
    * Index of the currently displayed record.<p>
    * If equals emptySlot, then no backwards record available.<p>
    * If equals newest, then no forward record available.<p>
    */
   int current;

   /**
    * Constructor.
    */

   public BackAndForward()
   {
      // Allocate the ring buffer
      history = new ArrayList(MaxSize);
      for(int i=0; i<MaxSize; i++)
         history.add(null);

      clearall();
   }


   /**
    * adds one to the pointer, moving it one slot to the right<p>
    * Takes into account a limited sized buffer<p>
    * @param ptr  integer pointer
    * @return<code>integer</code> specifies the size of the buffer
    */
   private int addOne(int ptr)
   {
      return (ptr + 1) % MaxSize;
   }


   /**
    * Removes one from the pointer, moving it one slot to the left.<p>
    * Takes into account a limited sized buffer.<p>
    */
   private int subOne(int ptr)
   {
      // Using modulo arithmetic, subtract one.
      return (ptr + MaxSize - 1) % MaxSize;
   }

   // Make sure there is at least one empty slot in the buffer.
   // If not, throw away the oldest record.
   /**
    * To check to see if there is atleast one empty slot in the buffer.
    *
    */
   private void checkemptySlot()
   {
      if( newest == emptySlot )
      {
         emptySlot = (emptySlot + 1) % MaxSize;
      }
   }
   /**
    * To add a new record.
    * @param rec contractOwnerBase
    */

   // Add a new record just after the "current" record.
   public void addtoHistory( contractOwnerBase rec)
   {
      // Move to the new record's slot
      current = addOne(current);

      // Put it there
      history.set(current, rec);

      // It is now the newest record (throw away anything they backed over)
      newest = current;

      // Make sure that we don't overfill our buffer.
      checkemptySlot();
   }

   /**
   * @return <code> boolean<\code> true if there is atleast one record to go back to.
   */
   // Returns true if you can go back at least one record.
   public boolean havePast()
   {
      return current != emptySlot && current != addOne(emptySlot);
   }
   /**
    * @return <code> boolean<\code> true if there is atleast one record to go forward to.
    */
   // Returns true if you can go forward at least one record.
   public boolean haveFuture()
   {
      return current != newest;
   }

   // Return the current record and move the current pointer to the next older record.
   // Returns null if there is no going back.
   /**
    * To return the current record and move the pointer to the next older record.
    * @return <code> object <\code> specifies the old record.
    */
   public Object goBack()
   {
       // If they are already looking at the emptySlot record, return null.
      if( !havePast() ) //i.e., current is equal to the emptySlot
      {
          return null;
      }

      // move to the oldest record, just before the current record
      current = subOne(current);

      // Get the record
      Object rec = history.get(current);
      return rec;
   }


   //returning the field ,the user wants to go to
   // Returns null if there is no going forward.
   /**
    * To return the current record and move the pointer to the next new record
    * @return <code> object <\code> specifies the new record
    */
   public Object goForward()
   {
      // If they are already looking at the newest record, return null.
      if( !haveFuture() )
      {
          return null;
      }

      // move to the newer record, just after the current record
      current= addOne(current);


      return history.get(current);

   }
   /**
    * To set all the values to null.
    */

   void clearall()
   {
     emptySlot = 0;
     newest = 0;
     current = 0;
   }
}