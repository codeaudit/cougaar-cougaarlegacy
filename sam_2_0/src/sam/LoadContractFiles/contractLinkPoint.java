

package sam.LoadContractFiles;

import org.cougaar.core.util.Operator;
import org.cougaar.core.util.OperatorFactory;
import sam.graphPlanner.graphLink;

import java.util.*;
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

public class contractLinkPoint implements graphLink
{
   // Is this a publish contract?
   /**
    *  Is this is a publish contract.<p>
    */
   private boolean publish;

   // Our contract.
   /**
    * the contract.<p>
    */
   private Operator contract;

   // Our slot number
   /**
    * The integer number of the slot.<p>
    */
   private int slot;

   // The parent record
   /**
    * The Parent record, a contractOwnerBase.<P>
    *
    */
   private contractOwnerBase parent;

   // list of other "contractLinkPoint" records which either:
   //    If we are publish:   subscriptions we fullfill
   //    If we are !publish:  publications that fullfill our subscription.
   /**
    * The vector that holds the list of all the contractLinkPoint records.<p>
    */
   public Vector links;

   // True if all links into or out of this link point are hidden.
   /**
    * Returns true if  all the links into or out of this link point are hidden.<p>
    */
   private boolean hidden;

   // True if this link should be highlighted in the display
   /**
    * Returns true if this link is highlighted in the display.<p>
    */
   private boolean highlighted;

   // Basic constructor.
   /**
    * Constructor.<p>
    */
   public contractLinkPoint(Operator _contract, boolean _publish, contractOwnerBase ourParent, int _slot)
   {
      contract = _contract;
      publish = _publish;
      slot = _slot;
      parent = ourParent;

      hidden = false;
      highlighted = false;

      links = new Vector();
   }

   // Constructor to also add the link.
   /**
    * constructor.<p>
    */
   public contractLinkPoint(Operator _contract, boolean _publish, contractOwnerBase ourParent,
                            int _slot, contractLinkPoint other)
   {
      this(_contract, _publish, ourParent, _slot);

      addLink(other);
   }

   /**
    * Returns the contractOwnerBase parent.<p>
    */
   public contractOwnerBase getParent()
   {
      return parent;
   }
   /**
    * Returns true if the link is full filled.<p>
    */

   public boolean isFullFilled()
   {
      return !links.isEmpty();
   }

   /**
    * Returns true if the contract link point is connected.<P>
    */

   public boolean isConnected()
   {
      return isFullFilled();
   }

   /**
    * Returns true if the contract is publish else returns false.<p>
    */
   public boolean isPublish()
   {
      return publish;
   }
   /**
    * Returns the Operator.<p>
    */

   public Operator getContract()
   {
      return contract;
   }

   ///Replace the specified slot's contract with the new one.
   /**
    * Returns true if the specified slot's contract has been replaced by new one.<p>
    * @param op Operator.<P>
    */
   public boolean setContract(Operator op)
   {
      contract = op;
      return true;
   }

   // prepare our record for freeing by unhooking any references we hold
   // so the garbage collector will find them.
   /**
    * Prepares the record for freeing by unhooking any references that are held.<P>
    */
   public void prepareForDelete()
   {
      contract = null;
      parent = null;
      clearLinks();
   }
   /**
    *Adds a link to the vector that holds the list of the contract link points.<P>
    */

   public void addLink(contractLinkPoint other)
   {
      links.addElement(other);
   }

   //clears all the links
   /**
    * Clears all the links.<P>
    */
   public void clearLinks()
   {
      links.clear();
   }

   /**
    * Returns the integer number for that slot.<P>
    */
   public int getSlot()
   {
      return slot;
   }

   /// Return the contract as an XML string
   /**
    * Returns the Contract as an XML String.<P>
    */
   public String toXMLString()
   {
      return contract.toString(Operator.XML_FLAG);
   }

   /// Called when user clicks on this site to hide it.
   /**
    * This method is called when the user clicks on the site to hide it.<p>
    */
   public void hide()
   {
      if( !hidden )
      {
         hidden = true;

         // Tell the other end of the links to hide themselves.
         Iterator it = links.iterator();
         while( it.hasNext() )
         {
            contractLinkPoint lp = (contractLinkPoint)it.next();
            lp.propogateHide();
         }

         // If we are an input link, tell the base record to check
         // if all its inputs are now hidden, and if so, hide its output.
         if( !publish )
         {
            // Tell the parent we just hid one of its input links
            parent.checkLinks(true);
         }
      }
   }

   /// Propogate hiding.
   /**
    * Propogates hiding, i.e., the corresponding sites will be hidden.<p>
    */
   public void propogateHide()
   {
      if( !hidden )
      {
         // Check if all our links are now hidden.
         boolean allHidden = true;
         Iterator it = links.iterator();
         while( it.hasNext() )
         {
            contractLinkPoint lp = (contractLinkPoint)it.next();
            if( !lp.isHidden() )
            {
               allHidden = false;
               break;
            }
         }

         // If all are now hidden, then we are hidden.
         if( allHidden )
         {
            hidden = true;

            // If we are an output, let the other end of the links check their state.
            if( publish )
            {
               it = links.iterator();
               while( it.hasNext() )
               {
                  contractLinkPoint lp = (contractLinkPoint)it.next();
                  lp.propogateHide();
               }
            }
            else
            {
               // If we are an input link, tell the base record to check
               // if all its inputs are now hidden, and if so, hide its output.
               parent.checkLinks(true);
            }
         }
      }
   }

   //do not hide the site
   /**
    * Show the site.<P>
    */
   public void show()
   {
      if( hidden )
      {
         hidden = false;

         // Tell the other end of the links to show themselves.
         Iterator it = links.iterator();
         while( it.hasNext() )
         {
            contractLinkPoint lp = (contractLinkPoint)it.next();
            lp.propogateShow();
         }

         // If we are an input link, tell the base record to check
         // if all its inputs are still hidden, and if not, show its output.
         if( !publish )
         {
            // the the parent we just showed one if its inputs.
            parent.checkLinks(false);
         }
      }
   }

   //do not hide the site
   /**
    * Shows all the corresponding sites when requested to show deopndencies.<P>
    */
   public void propogateShow()
   {
      if( hidden )
      {
         hidden = false;
/*
         // Tell the other end of the links to show themselves.
         Iterator it = links.iterator();
         while( it.hasNext() )
         {
            contractLinkPoint lp = (contractLinkPoint)it.next();
            lp.propogateShow();
         }
*/
         // If we are an input link, tell the base record to check
         // if all its inputs are still hidden, and if not, show its output.
         if( !publish )
         {
            // the the parent we just showed one if its inputs.
            parent.checkLinks(false);
         }
      }
   }


   //is this site hidden?
   /**
    * Returns true if the site is hidden.<p>
    */
   public boolean isHidden()
   {
      return hidden;
   }

   // Can this link be connected to the specified link?
   /**
    * Returns true if this link can be connected to the spcified link, else returns false.<p>
    */
   public boolean isCompatible(graphLink link)
   {
      // Safe upcast.
      if( !(link instanceof contractLinkPoint) )
      {
         System.err.println("Error: contractLinkPoint::isCompatible passed unknown record: " + link);
         return false;
      }
      contractLinkPoint otherLink = (contractLinkPoint)link;

      // Compare them, if they are comparable.

      // Init to false.
      boolean isGood = false;

      // Case where we are publish and otherlink is subscribe.
      if( publish && !otherLink.isPublish() )
         isGood = contract.implies( otherLink.getContract() );

      // Case where we are subscribe and otherlink is publish
      else if( !publish && otherLink.isPublish() )
         isGood = otherLink.getContract().implies( contract );

      // Return true iff they are composable.
      return isGood;
   }

   // Is this link equivalent to the specified link?
   /**
    * Returns true if the link is equivalent to the specified link.<p>
    */
   public boolean isEqual(graphLink link)
   {
      // Safe upcast.
      if( !(link instanceof contractLinkPoint) )
      {
         System.err.println("Error: contractLinkPoint::isCompatible passed unknown record: " + link);
         return false;
      }
      contractLinkPoint otherLink = (contractLinkPoint)link;
/*
System.err.println("\nChecking for equality.  Contract A= " + contract.toString() );
System.err.println("Contract B= " + otherLink.getContract().toString() );
System.err.println("Contract A: publish=" + publish + " Contract B: publish=" + otherLink.isPublish());
System.err.println("Contract A implies Contract B = " + contract.implies( otherLink.getContract() ));
System.err.println("Contract A impliedBy Contract B = " + contract.impliedBy( otherLink.getContract()) + "\n");
*/
      //Return true iff they are both subscribe or both publish and they imply each other.
      return ((publish && otherLink.isPublish()) || (!publish && !otherLink.isPublish())) &&
              contract.implies( otherLink.getContract() ) &&
              contract.impliedBy( otherLink.getContract() );
   }
   /**
    * Returns the parent's long name as a String.<p>
    */
   public String parentName()
   {
      return parent.getLongName();
   }

   ///////////////////////////////For highlighted stuff////////////////////////////////////
   /**
    *sets that the highlight of the site to true.<P>
    */
   public void highlight()
   {
      highlighted = true;
   }

   /**
    * Sets the normal of the  site to false.<p>
    */
   public void normal()
   {
      highlighted = false;
   }

   /**
    * Returns true if the site is highlighted and false if it is not.<p>
    */
   public boolean isHighlighted()
   {
      return highlighted;
   }
} //ends the contrcatLinkPoint class
