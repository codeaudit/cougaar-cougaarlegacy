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
 * Author:  John Page
 *
 */

package com.prc.alp.liaison.adminGUI;
import javax.swing.DefaultListModel;
import javax.swing.ComboBoxModel;
import java.util.ArrayList;
import java.util.Iterator;


/**
 * Provides the Swing Combo Box model for the AlpSocietyListComboBox and the
 *  ExternalSocietyListComboBox.
 *
 * @author John Page
 * @version 1.1
 * @since 1.0
 */
public class SocComboBoxModel extends DefaultListModel implements ComboBoxModel {
    protected SocietyDescriptor    currentValue; 
    public  boolean  fireChanges = true;  // Set to false if you wish to set the value
                                          // without a callback

    /** Creates new SocComboBoxModel takes an Array List of Society descriptors */
    public SocComboBoxModel(ArrayList  descList) {
        Iterator  it;
        SocietyDescriptor  thisDesc;
        
        it = descList.iterator();
        
        while(it.hasNext()){
            thisDesc = (SocietyDescriptor) it.next();
            this.addElement(thisDesc);
        }
        
        
        //currentValue = (SocietyDescriptor) descList.get(0);
        
 
    }

    /** Set the value of the model to a specified object(SocietyDescriptor) */
    public void setSelectedItem(final java.lang.Object obj) {
        if(!(obj instanceof SocietyDescriptor))
            return;
        currentValue = (SocietyDescriptor) obj;
        if(fireChanges)
            fireContentsChanged(this, -1, -1);
    }
    
    /** Return the currenly selected SocietyDescriptor */
    
    public java.lang.Object getSelectedItem() {
        return currentValue;
        
    }
    
    /** Set the current selection to the first item */
    public void setFirst(){
        Object obj;
        
        obj = firstElement();
        setSelectedItem(obj);
    }

    
    /* I am using the default implementations of the following, and therefore will 
     * not be needing to override them.
     
    public void removeListDataListener(final javax.swing.event.ListDataListener p1) {
    }
    public int getSize() {
    }
    public void addListDataListener(final javax.swing.event.ListDataListener p1) {
    }
    public java.lang.Object getElementAt(int p1) {
    }
    */
    
}