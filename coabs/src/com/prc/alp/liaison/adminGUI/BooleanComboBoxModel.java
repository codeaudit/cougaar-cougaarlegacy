/*
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

/**
 * This Class allows you to define a general purpose Combo Box model that supports toggling
 * between two values that are represented by Strings that are passed in a creation time.
 * You may either get and set using string objects, or by getting a boolean value back if
 * the currently selected object represents the truth string.
 *
 * @author John Page
 * @version 1.1
 * @since 1.0
 */
public class BooleanComboBoxModel extends DefaultListModel implements ComboBoxModel{
    public  String strTruth;
    public  String strFalse;
    private Object  currentValue;
    private boolean currentBoolValue;
    

    /** Creates new BooleanComboBoxModel */
    public BooleanComboBoxModel(String truthString, String falseString) {
        super();
        strTruth = truthString;
        strFalse = falseString;
        currentValue = null;
        currentBoolValue = false;
        this.addElement(strTruth);
        this.addElement(strFalse);      
    }


  
    /** Set selection. Invoked by Swing */
    public void setSelectedItem(final Object obj) {
        String  testString;
        String  listString;
        int i;
        
        // make sure object is a string.  Otherwise, do nothing.
        
        if (!(obj instanceof String))
            return;
        
        testString = (String) obj;
        
        if(testString.equalsIgnoreCase(strTruth)){
            currentValue = strTruth;
            currentBoolValue = true;
        } else if (testString.equalsIgnoreCase(strFalse)){
            currentValue = strFalse;
            currentBoolValue = false;
        }  // if neither, then do nothing. 
        
        // Broadcast a change.  Listeners will be expected to query the ComboBox 
        // for the value. 
        fireContentsChanged(this, -1, -1);
    }
    
    /** determine if the string is the one for "truth" */
    private  boolean  getBoolValueForString(String str){       
        if (str.equalsIgnoreCase(strTruth))
            return true;
        else
            return false;     
    }
    
    
    
    
    /** Swing level version to get current item */
    
    public Object getSelectedItem() {
        return currentValue;
    }
    
    
    /** convenience function that lets value be set by Boolean
     *
     * @param bool  
    */
    public void setValue(boolean bool){
        String setString;
        
        if(bool)
            setString = strTruth;
        else
            setString = strFalse;
        
        
        setSelectedItem(setString);
        
    }
    
    /** Convenience function that returns boolean value */
    public boolean getValue(){
        return currentBoolValue;
        
    } 
    
    
    /* Do not override the Default list model stuff here...
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