package sam.LoadContractFiles;

import org.cougaar.core.util.Operator;
import org.cougaar.core.util.OperatorFactory;
//import alp.util.SetRelationship;

import java.util.Vector;
import java.util.Iterator;
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

public class setOfContractLinkPoints
{
   // A list of "contractLinkPoint" records.
   public Vector publish;
   public Vector subscribe;

   // What type of contract is this?
   public static final int InvalidContract = 0;
   public static final int PluginContract = 1;
   public static final int ClusterContract = 2;
   public static final int CommunityContract = 3;
   public static final int SocietyContract = 4;
   public int contractType;

   // Is this a system object which we should ignore?
   public boolean systemObject;

   public setOfContractLinkPoints()
   {
      publish = new Vector();
      subscribe = new Vector();
      systemObject = false;
   }

   // Build the object from a set of contract operators.
   public setOfContractLinkPoints(setOfContractOperators ops, contractOwnerBase baseRec)
   {
      // Create an empty record
      this();

      // Set the systemPlugin flag
      systemObject = ops.systemObject;

      // Now fill it.
      Iterator it = ops.publish.iterator();
      int slot = 0;
      while(it.hasNext() )
      {
         Operator op = (Operator)it.next();
         contractLinkPoint point = new contractLinkPoint(op, true, baseRec, slot);
         publish.addElement(point);

         // next one.
         slot++;
      }

      it = ops.subscribe.iterator();
      slot = 0;
      while(it.hasNext() )
      {
         Operator op = (Operator)it.next();
         contractLinkPoint point = new contractLinkPoint(op, false, baseRec, slot);
         subscribe.addElement(point);

         // next one.
         slot++;
      }
   }
///////////////////////////////////////////////////////////////////////////////////////////////

   // Adds a new publish contract.
   public boolean addPublishContract(contractLinkPoint theNewContract)
   {
      publish.addElement(theNewContract);
      return true;
   }//ends the method add publish contract


   public boolean addSubscribeContract(contractLinkPoint theNewContract)
   {
      subscribe.addElement(theNewContract);
      return true;

   }//ends the method add publish contract

   //////////////////////////////////////////////////////////////////////////////////////////

   // Special constructor used by inputBar and outputBar.
   // They only need one of the contracts (publish or subscribe), but they need it
   // in the opposite sense.  So inputBar gets its parents subscribe contract and uses it as a publish contract.
   public setOfContractLinkPoints(setOfContractOperators ops, contractOwnerBase baseRec, boolean usePublish)
   {
      // Create an empty record
      this();

      // Set the systemPlugin flag
      systemObject = ops.systemObject;

      if( usePublish )
      {
         // Now fill it.
         Iterator it = ops.publish.iterator();
         int slot = 0;
         while(it.hasNext() )
         {
            Operator op = (Operator)it.next();

            // NOTE: Remember we swapped this to be a subscribe link point.
            contractLinkPoint point = new contractLinkPoint(op, false, baseRec, slot);
            subscribe.addElement(point);

            // next one.
            slot++;
         }
      }
      else
      {
         Iterator it = ops.subscribe.iterator();
         int slot = 0;
         while(it.hasNext() )
         {
            Operator op = (Operator)it.next();

            // Remember we swapped this to be a publish link point.
            contractLinkPoint point = new contractLinkPoint(op, true, baseRec, slot);
            publish.addElement(point);

            // next one.
            slot++;
         }
      }
   }

   // prepare our record for freeing by unhooking any references we hold
   public void prepareForDelete()
   {
      // Clear the dependency links
      clearLinks();

      // Clear the contracts themselves.
      publish.clear();
      subscribe.clear();
   }

//////////////////////////////////////////////////////////////////////////////////////////
   //trying to clear all the links

   public void clearLinks()
   {
      // Clear the dependency links for our publish contracts
      Iterator it = publish.iterator();
      while(it.hasNext())
      {
         ((contractLinkPoint)it.next()).clearLinks();
      }

      // Clear the dependency links for our subscribe contracts
      it = subscribe.iterator();
      while(it.hasNext())
      {
         ((contractLinkPoint)it.next()).clearLinks();
      }
   }
   //////////////////////////////////////////////////////////////////////////////////////


   // Add a link
   public void addPublishLink(Operator contract, contractOwnerBase ourParent, int slot, contractLinkPoint other)
   {
      publish.addElement( new contractLinkPoint(contract, true, ourParent, slot, other) );
   }
   public void addSubscribeLink(Operator contract, contractOwnerBase ourParent, int slot, contractLinkPoint other)
   {
      subscribe.addElement( new contractLinkPoint(contract, false, ourParent, slot, other) );
   }

   /// Get the number of subscriptions.
   public int getNumSubscriptions()
   {
      return subscribe.size();
   }

   /// Get the number of publications.
   public int getNumPublications()
   {
      return publish.size();
   }

   // does the specified input slot have a connection?
   public boolean isInputSlotConnected(int slot)
   {
      Iterator it = subscribe.iterator();
      while(it.hasNext() )
      {
         contractLinkPoint lp = (contractLinkPoint)it.next();
         if( lp.getSlot() == slot )
         {
            return lp.isFullFilled();
         }
      }

      System.err.println("Internal Error: setOfContractLinkPoints::isInputSlotConnected(" + slot + ") - slot not found");
      return false;
   }

   // does the specified output slot have a connection?
   public boolean isOutputSlotConnected(int slot)
   {
      Iterator it = publish.iterator();
      while(it.hasNext() )
      {
         contractLinkPoint lp = (contractLinkPoint)it.next();
         if( lp.getSlot() == slot )
         {
            return lp.isFullFilled();
         }
      }

      System.err.println("Internal Error: setOfContractLinkPoints::isOutputSlotConnected(" + slot + ") - slot not found");
      return false;
   }

   ///////////////////////////////////////////////////////////////////////////////////////////////////
   /// Return the specified slot's contract.
   /// returns null if the slot is not found.
   public Operator getContractOperator(boolean isInput, int slot)
   {
      // Get the correct iterator.
      Iterator it = null;
      if( isInput )
         it = subscribe.iterator();
      else
         it = publish.iterator();

      // Find the slot
      while(it.hasNext() )
      {
         contractLinkPoint lp = (contractLinkPoint)it.next();
         if( lp.getSlot() == slot )
         {
            return lp.getContract();
         }
      }

      System.err.println("Internal Error: setOfContractLinkPoints::getContractOperator(" + isInput + ", " + slot + ") - slot not found");
      return null;
   }

   ////////////////////////////////////////////////////////////////////////////////////////
   /// Return the specified slot's contract as an XML string.
   public String getContract(boolean isInput, int slot)
   {
      // Get the correct iterator.
      Iterator it = null;
      if( isInput )
         it = subscribe.iterator();
      else
         it = publish.iterator();

      // Find the slot
      while(it.hasNext() )
      {
         contractLinkPoint lp = (contractLinkPoint)it.next();
         if( lp.getSlot() == slot )
         {
            return lp.toXMLString();
         }
      }

      System.err.println("Internal Error: setOfContractLinkPoints::getContract(" + isInput + ", " + slot + ") - slot not found");
      return "Internal Error";
   }

   ///Replace the specified slot's contract with the new one.
   public boolean setContract(boolean isInput, int slot, Operator op)
   {
      // Get the correct iterator.
      Iterator it = null;
      if( isInput )
         it = subscribe.iterator();
      else
         it = publish.iterator();

      // Find the slot
      while(it.hasNext() )
      {
         contractLinkPoint lp = (contractLinkPoint)it.next();
         if( lp.getSlot() == slot )
         {
            lp.setContract(op);
            return true;
         }
      }

      System.err.println("Internal Error: setOfContractLinkPoints::getContract(" + isInput + ", " + slot + ") - slot not found");
      return false;
   }

   /////////////////////////////////////////////////////////////////////
   // Is the specified slot hidden?
   public boolean isSlotHidden(boolean isInput, int slot)
   {
      Iterator it = isInput ? subscribe.iterator() : publish.iterator();
      while(it.hasNext() )
      {
         contractLinkPoint lp = (contractLinkPoint)it.next();
         if( lp.getSlot() == slot )
         {
            return lp.isHidden();
         }
      }

      System.err.println("Internal Error: setOfContractLinkPoints::isInputSlotHidden(" + slot + ") - slot not found");
      return false;
   }

   /////////////////////////////////////////////////////////////////////
   // Hide the specified slot.
   // propogating should be false for the slot the users click on, and false for
   // calls propogated by checkLinks
   public boolean hideSlot(boolean isInput, int slot)
   {
      Iterator it = isInput ? subscribe.iterator() : publish.iterator();
      while(it.hasNext() )
      {
         contractLinkPoint lp = (contractLinkPoint)it.next();
         if( lp.getSlot() == slot )
         {
            lp.hide();
            return true;
         }
      }

      System.err.println("Internal Error: setOfContractLinkPoints::HideSlot(" + slot + ") - slot not found");
      return false;
   }

   /////////////////////////////////////////////////////////////////////
   // Show the specified slot.
   // propogating should be false for the slot the users click on, and false for
   // calls propogated by checkLinks
   public boolean showSlot(boolean isInput, int slot)
   {
      Iterator it = isInput ? subscribe.iterator() : publish.iterator();
      while(it.hasNext() )
      {
         contractLinkPoint lp = (contractLinkPoint)it.next();
         if( lp.getSlot() == slot )
         {
            lp.show();
            return true;
         }
      }

      System.err.println("Internal Error: setOfContractLinkPoints::showSlot(" + slot + ") - slot not found");
      return false;
   }
   ////////////////////////////////////////////////////////////////////////
   /////////////Highlight this slot
   public boolean highlightSlot( boolean areHighlighted, int slot)
   {
      Iterator it = areHighlighted ? subscribe.iterator() : publish.iterator();
      while(it.hasNext() )
      {
         contractLinkPoint lp = (contractLinkPoint)it.next();
         if(lp.getSlot() == slot)
         {
            lp.highlight();
            return true;
         }
      }
         System.err.println("Internal Error: setOfContractLinkPoints::showSlot(" + slot + ") - slot not found");
         return false;
   }

   ////////////////////////////////////////////////////////////////////////
   ////////////show the normal slot
   public boolean normalSlot( boolean areHighlighted, int slot)
   {
      Iterator it = areHighlighted ? subscribe.iterator() : publish.iterator();
      while(it.hasNext() )
      {
         contractLinkPoint lp = (contractLinkPoint)it.next();
            if(lp.getSlot() == slot)
         {
            lp.normal();
            return true;
         }
      }
       System.err.println("Internal Error: setOfContractLinkPoints::showSlot(" + slot + ") - slot not found");
      return false;
   }

   ////////////////////////////////////////////////////////////////////////
   ////////////Isthis slot Highlighted?
   public boolean isSlotHighlighted( boolean areHighlighted, int slot)
   {
      Iterator it = areHighlighted ? subscribe.iterator() : publish.iterator();
      while(it.hasNext() )
      {
         contractLinkPoint lp = (contractLinkPoint)it.next();
         if(lp.getSlot() == slot)
         {
            return lp.isHighlighted();
         }
      }
         System.err.println("Internal Error: setOfContractLinkPoints::showSlot(" + slot + ") - slot not found");
         return false;
   }
}
