/* $RCSfile$
 * $Author$
 * $Date$
 * $Revision$
 *
 * Copyright (C) 2003-2005  The JChemPaint project
 *
 * Contact: jchempaint-devel@lists.sourceforge.net
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public License
 * as published by the Free Software Foundation; either version 2.1
 * of the License, or (at your option) any later version.
 * All we ask is that proper credit is given for our work, which includes
 * - but is not limited to - adding the above copyright notice to the beginning
 * of your source code files, and to any copyright notice that you may distribute
 * with programs based on this work.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 */
package org.openscience.cdk.applications.jchempaint.dialogs;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.util.Properties;

import javax.swing.AbstractAction;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import org.openscience.cdk.applications.jchempaint.*;

/**
 * Internal frame to allow for changing the properties.
 *
 * @cdk.module jchempaint
 */
public class GeneralFieldEditor extends JFrame {
    
    Properties props;
    JChemPaintModel jcpm;

    String[] defaults;
    String[] currentValues;
    JTextField[] fields;
    JChemPaintPanel jcp;
    /**
     * IMPORTANT: the fieldTitles.length and defaults.length *must* be equal.
     */
    public GeneralFieldEditor(JChemPaintPanel jcp, String title,
        String[] fieldTitles, String[] defaults) {
        super(title);
        this.jcp = jcp;
        jcpm = jcp.getJChemPaintModel();

        getContentPane().setLayout(new BorderLayout());
        JPanel southPanel = new JPanel();
        JButton cancelButton = new JButton("Cancel");
        JButton openButton = new JButton("OK");
        openButton.addActionListener(new UpdateAction());
        cancelButton.addActionListener(new CancelAction());
        southPanel.add(openButton);
        southPanel.add(cancelButton);
        
        JPanel centerPanel = new JPanel();
        centerPanel.setLayout(new GridLayout(5,2));
        
        this.defaults = defaults;
        this.currentValues = this.defaults;
        fields = new JTextField[fieldTitles.length];
        for (int i=0; i<fieldTitles.length; i++) {
            JLabel label = new JLabel(fieldTitles[i]);
            JTextField textField = new JTextField("", 20);
            centerPanel.add(label);
            centerPanel.add(textField);
            fields[i] = textField;
        }
        
        setSize(100*fields.length, 500);
        
        getContentPane().setLayout(new BorderLayout());
        getContentPane().add("Center", centerPanel);
        getContentPane().add("South", southPanel);
    }
    
    public String[] getFieldValues() {
        return this.currentValues;
    }
    
    public void closeFrame(){
        dispose();
    }

    class UpdateAction extends AbstractAction {
        UpdateAction() {
            super("Update");
        }
        
        public void actionPerformed(ActionEvent event) {
            // update the current values
            for (int i=0; i< fields.length; i++) {
                JTextField field = fields[i];
                currentValues[i] = field.getText();
            }
            closeFrame();
        }
    }

    class CancelAction extends AbstractAction {
        CancelAction() {
            super("Cancel");
        }
        
        public void actionPerformed(ActionEvent e) {
            currentValues = defaults;
            closeFrame();
        }
    }

    class EditAction extends AbstractAction {
        private String prop = "";
   
        EditAction(String prop) {
            super("Edit");
            this.prop = prop;
        }

        public void actionPerformed(ActionEvent e) {
            // do not validate content
        }
    }
 }
