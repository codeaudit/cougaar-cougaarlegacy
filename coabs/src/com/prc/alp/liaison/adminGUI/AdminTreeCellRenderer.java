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
import com.prc.alp.liaison.admin.ALPAgentReference;
import com.prc.alp.liaison.admin.LiaisonStatusReference;
import com.prc.alp.liaison.admin.ExternalAgentReference;
import javax.swing.JTree;
import javax.swing.JPanel;
import javax.swing.tree.TreeCellRenderer;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.JLabel;
import javax.swing.UIManager;
import javax.swing.Icon;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.FlowLayout;
import java.awt.Point;
import java.awt.Font;

/**
 * AdminTreeCellRender is the renderer for cells in both tree displays.
 * For anybody digging into the code, please note that this originally used
 * two labels in a panel, but only effectively uses one now.
 *
 * @author John Page
 * @version 1.1
 * @since 1.0
 */
public class AdminTreeCellRenderer extends JPanel implements TreeCellRenderer{
   protected JLabel leftLabel;
   protected JLabel rightLabel;
   private   DisplayModelServer    dispServer;  // keep a pointer to reach icons
   private   LiaisonAdminToolController  theApplication;  // looks like I'll need a ptr to the 
                                               // application shell as well. 
   protected Color d_textSelectionColor;
   protected Color d_textNonSelectionColor;
   protected Color d_bkSelectionColor;
   protected Color d_bkNonSelectionColor;
   protected Color d_borderSelectionColor;
   protected boolean d_selected;  // Holds whether selection is made for repaint
   protected boolean d_underControl;  // if true, the current agent is not under its 
                                     // own control


    /** 
     * Creates new TestTreeCellRenderer
     * @param app Pointer to the LiaisonAdminToolController (application shell) that is
     * creating it.  This provides access to all public data structures.
     * @param disp A displayModelServer that holds all of the icons and other resources needed for rendering.
     */
    public AdminTreeCellRenderer(LiaisonAdminToolController  app, DisplayModelServer disp) {
        super();
        this.setLayout( new FlowLayout());
        leftLabel = new JLabel();
        rightLabel = new JLabel();
        this.add(leftLabel);
        this.add(rightLabel);
        
        dispServer = disp;
        theApplication = app;
        d_textSelectionColor = UIManager.getColor("Tree.selectionForeground");
        d_textNonSelectionColor = UIManager.getColor("Tree.textForeground");
        d_bkSelectionColor = UIManager.getColor("Tree.selectionBackground");
        d_bkNonSelectionColor = UIManager.getColor("Tree.textBackground");
        d_borderSelectionColor = UIManager.getColor("Tree.selectionBorderColor");
        setOpaque(false);  
    }

    /** The requiered method for a tree cell renderer.  It displays the tree cell based on the semantics of either the ALP Society tree or external society tree.
     * @param tree
     * @param value
     * @param sel
     * @param expanded
     * @param leaf
     * @param row
     * @param hasfocus
     * @return This tree cell renderer 
     */
    public java.awt.Component getTreeCellRendererComponent(final JTree tree,
                                                           final Object value,
                                                           boolean sel,
                                                           boolean expanded,
                                                           boolean leaf,
                                                           int row,
                                                           boolean hasfocus) {
        
        ALPAgentReference  thisAgent; 
        ALPAgentReference  alpRef;
        LiaisonStatusReference thisStatus;
        ExternalAgentReference extRef;
        DefaultMutableTreeNode node = (DefaultMutableTreeNode) value;
        Object  obj = node.getUserObject();
        Font    labelFont;
            
        /* This sort of weird test is valid in cases where the tree is currently
           being expanded, and the listener may not yet have processed it. 
        */
        

           
            if (obj instanceof Boolean)
                rightLabel.setText("Receiving data...");
        
        // set default   this is only relavent to the ALP tree. 
        d_underControl = false;
       
 
        if(obj instanceof ALPAgentReference){

            thisAgent = (ALPAgentReference) obj; 
 
            rightLabel.setText(thisAgent.getName());
            
 
            
            d_underControl = theApplication.alpSet.isAncestorInControl(thisAgent);
            
            // rule of thumb.  Use grey text and disabled icons ONLY when under someone 
            // else;s control.  A society always controls itself-- I hope.
            
            if(thisAgent.isSociety()){
                // Right label is ALWAYS a societyIcon

                rightLabel.setIcon(null);

                // Left label-- Stop sign takes precedence, then 
                // crown, finally blank. 
              
                if (!(thisAgent.isEnabled()))
                    leftLabel.setIcon(dispServer.stopSign);
                else if (!(thisAgent.delegatesAuthority.booleanValue()))
                    leftLabel.setIcon(dispServer.crownIcon);
                else  
                    leftLabel.setIcon(dispServer.blankIcon);
               
                
            } else if (thisAgent.isNeighborhood()){
                // Stop sign has precedence for left icon. 
                // not that, if this agent is being controlled, the disabled versions
                // of other icons are shown. 
                if(!(thisAgent.isEnabled())){
                    if (d_underControl)
                        leftLabel.setIcon(dispServer.disStopSign);
                    else
                        leftLabel.setIcon(dispServer.stopSign);
                } else if (!(thisAgent.delegatesAuthority.booleanValue())){
                    if(d_underControl)
                        leftLabel.setIcon(dispServer.disCrownIcon);
                    else
                        leftLabel.setIcon(dispServer.crownIcon);
                }   else {
                    leftLabel.setIcon(dispServer.blankIcon);
                }
                
                // now select right icon
                
                /*  overriding this for now. 
                if(d_underControl)
                    rightLabel.setIcon(dispServer.disNeighborhoodIcon);
                else
                    rightLabel.setIcon(dispServer.neighborhoodIcon);
                */
                rightLabel.setIcon(null);
                  
            } else if (thisAgent.isAgent()){
                // Set left Icon crown is not a possibility. 
                if(!(thisAgent.isEnabled())){
                    if(d_underControl)
                        leftLabel.setIcon(dispServer.disStopSign);
                    else
                        leftLabel.setIcon(dispServer.stopSign);
                } else {
                    leftLabel.setIcon(dispServer.blankIcon);
                }
                
                // set right icon
                
                /* Override for now 
                if(d_underControl)
                    rightLabel.setIcon(dispServer.disAgentIcon);
                else
                    rightLabel.setIcon(dispServer.agentIcon);
                */
                
              

            } // is ALP Agent
            
                    /* Now steal the display properties from the tree
                       Fonts used are always the same when drawing ALPAgentReferences*/
        
            rightLabel.setFont(tree.getFont());

            // For a LSR, we don't care about the "control" issues, so we never show 
            // any greyed out icons.  The only "wrinkle" is that if we are showing the 
            // permissions for an ALP Ref that has delegated permissions to alp refs 
            // underneath it, the cells in the external tree are not showing "active"
            // permissions, but rather "virtual" permissions which will be enforced
            // if the current ALP reference resumes control of its subordinates. When
            // in this "virtual" mode, we will be printing the name of the external 
            // agent ref in italics.  
       } else if(obj instanceof LiaisonStatusReference){
            thisStatus = (LiaisonStatusReference) obj; 
            extRef = (ExternalAgentReference) theApplication.extSet.getAgentRefByID(thisStatus.externalID);
            if(extRef == null){
                System.err.println("AdminTreeCellRenderer: ExtREF is NULL!! AHHH!!!");
                System.exit(0);
            }
            
            alpRef = theApplication.currentAlpRef;
            
            // set text for right label 
            rightLabel.setText(extRef.getName());
            labelFont = tree.getFont();
            
                // Are the permissions shown "virtual?" 
            if( (!(alpRef.isAgent())) & alpRef.delegatesAuthority.booleanValue()){
                labelFont = labelFont.deriveFont(Font.ITALIC);
                rightLabel.setFont(labelFont);
            } else /* use regular */{
                rightLabel.setFont(labelFont);
            }
                      
            // Choose icon for right label
            /* Override for now
           
            if(extRef.isSociety()){
                rightLabel.setIcon(dispServer.societyIcon);
            } else if (extRef.isNeighborhood()){
                rightLabel.setIcon(dispServer.neighborhoodIcon);
            } else if (extRef.isAgent()){
                rightLabel.setIcon(dispServer.agentIcon);
            }
            */
            
            rightLabel.setIcon(null);
            
            // choose icon for left label
            
            if((thisStatus.ALPCanInitiate.booleanValue()) &
               (thisStatus.ALPCanRespond.booleanValue())){
                leftLabel.setIcon(dispServer.bothWaysIcon);              
            } else if (thisStatus.ALPCanInitiate.booleanValue()){
                leftLabel.setIcon(dispServer.initIcon);               
            } else if (thisStatus.ALPCanRespond.booleanValue()){
                leftLabel.setIcon(dispServer.respIcon);            
            } else {
                // no interactions allowed 
                leftLabel.setIcon(dispServer.stopSign);
            }
            
            
            
            
       } else {  // Don't know what type of object is attached.
                leftLabel.setIcon(dispServer.blankIcon);
                rightLabel.setIcon(null);
                rightLabel.setText("BAD TREE NODE!");
       }
        

        
        if(sel)
        {
            if(d_underControl)
                rightLabel.setForeground(Color.darkGray);
            else
                rightLabel.setForeground(d_textSelectionColor);
            rightLabel.setBackground(d_bkSelectionColor);
        } else {
            if(d_underControl)
                rightLabel.setForeground(Color.gray);
            else
                rightLabel.setForeground(d_textNonSelectionColor);
            rightLabel.setBackground(d_bkNonSelectionColor);
        }
        
        /* Holds value for repaint call */
        d_selected = sel;
        
        return this;     
       
    }
    
    /** Overrides the component repaint method to draw a selection box around the text if the cell is selected.
     * @param g
     */
    public void paintComponent(Graphics g) {
        Point panelOrigin;
        Point rightLabelOrigin;
        Color bColor = rightLabel.getBackground();
        Color fColor = rightLabel.getForeground();
        Icon icon = rightLabel.getIcon();
        int offset = 0;  // This is from the code I'm borrowing.  It oringally
                         // was using a simple label as a renderer. 

        

        rightLabelOrigin = rightLabel.getLocation(null);

        g.setColor(bColor);
        
        /* Calculate size of text area and fill with background */
        if(icon != null && rightLabel.getText() != null)
            offset = (icon.getIconWidth() + rightLabel.getIconTextGap());
        g.fillRect(offset + rightLabelOrigin.x, 0 + rightLabelOrigin.y, rightLabel.getWidth() - 1 - offset, rightLabel.getHeight() -1); 
        
        /* Draw border if selected */
        
        if(d_selected) {
            g.setColor(d_borderSelectionColor);
            g.drawRect(offset + rightLabelOrigin.x, 0 + rightLabelOrigin.y, rightLabel.getWidth() -1 - offset, rightLabel.getHeight()-1);
        }
        
        /* now invoke base class to paint text */
        
 
        super.paintComponent(g);

                                                            
    }
}