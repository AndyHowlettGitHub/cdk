/* $RCSfile$
 * $Author$
 * $Date$
 * $Revision$
 *
 * Copyright (C) 1997-2005  The JChemPaint project
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
import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.DefaultCellEditor;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.TableColumn;

import org.openscience.cdk.ChemObject;
import org.openscience.cdk.applications.swing.DictRefEditorTableModel;

import org.openscience.jchempaint.application.JChemPaint;
import org.openscience.jchempaint.JChemPaintEditorPanel;

/**
 * Frame to allows editing of dictionary references of 
 * ChemObjects.
 *
 * @cdk.module jchempaint
 */
public class EditDictRefs extends JFrame  {
    
    DictRefEditorTableModel tableModel;
    
    public EditDictRefs(JChemPaint jcp) {
        super("Edit Dictionary References");
        getContentPane().setLayout(new BorderLayout());
        JPanel southPanel = new JPanel();
        JButton cancelButton = new JButton("Cancel");
        JButton applyButton = new JButton("Apply");
        applyButton.addActionListener(new ApplyAction());
        cancelButton.addActionListener(new CancelAction());
        southPanel.add(applyButton);
        southPanel.add(cancelButton);
        
        tableModel = new DictRefEditorTableModel();
        JTable table = new JTable(tableModel);
        table.setPreferredSize(new Dimension(500, 300));
        
        // setup dictionary list
        TableColumn dictColumn = table.getColumnModel().getColumn(1);
        JComboBox comboBox = new JComboBox();
        String[] dicts = JChemPaintEditorPanel.getDictionaryDatabase().getDictionaryNames();
        for (int i=0; i<dicts.length; i++) {
            comboBox.addItem(dicts[i]);
        }
        dictColumn.setCellEditor(new DefaultCellEditor(comboBox));
        
        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setPreferredSize(new Dimension(500, 100));
        
        getContentPane().setLayout(new BorderLayout());
        getContentPane().add("Center", scrollPane);
        getContentPane().add("South", southPanel);
    }
    
    /**
     * Sets the ChemObject that is being edited.
     */
    public void setChemObject(ChemObject object) {
        tableModel.setChemObject(object);
    }
    
    public void closeFrame() {
        dispose();
    }
    
    class ApplyAction extends AbstractAction {
        
        ApplyAction() {
            super("Apply");
        }
        
        public void actionPerformed(ActionEvent e) {
        }
        
    }
    
    class CancelAction extends AbstractAction {
        CancelAction() {
            super("Cancel");
        }
        
        public void actionPerformed(ActionEvent e) {
            closeFrame();
        }
    }
}
