/* $RCSfile$
 * $Author$
 * $Date$
 * $Revision$
 *
 * Copyright (C) 2005  The Chemistry Development Kit (CDK) project
 *
 * Contact: cdk-devel@lists.sourceforge.net
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public License
 * as published by the Free Software Foundation; either version 2.1
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110-1301 USA.
 */
package org.openscience.cdk.applications.undoredo;

import javax.swing.undo.AbstractUndoableEdit;
import javax.swing.undo.CannotRedoException;
import javax.swing.undo.CannotUndoException;

import org.openscience.cdk.interfaces.Atom;
import org.openscience.cdk.interfaces.AtomContainer;
import org.openscience.cdk.interfaces.Bond;
import org.openscience.cdk.interfaces.ChemModel;
import org.openscience.cdk.interfaces.Molecule;
import org.openscience.cdk.interfaces.SetOfMolecules;
import org.openscience.cdk.graph.ConnectivityChecker;
import org.openscience.cdk.tools.manipulator.ChemModelManipulator;

public class RemoveAtomsAndBondsEdit extends AbstractUndoableEdit {

	private String type;

	private AtomContainer undoRedoContainer;

	private ChemModel chemModel;

	private AtomContainer container;

	public RemoveAtomsAndBondsEdit(ChemModel chemModel,
			AtomContainer undoRedoContainer, String type) {
		this.chemModel = chemModel;
		this.undoRedoContainer = undoRedoContainer;
		this.container = ChemModelManipulator.getAllInOneContainer(chemModel);
		this.type = type;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.swing.undo.UndoableEdit#redo()
	 */
	public void redo() throws CannotRedoException {
		for (int i = 0; i < undoRedoContainer.getBondCount(); i++) {
			Bond bond = undoRedoContainer.getBondAt(i);
			container.removeElectronContainer(bond);
		}
		for (int i = 0; i < undoRedoContainer.getAtomCount(); i++) {
			Atom atom = undoRedoContainer.getAtomAt(i);
			container.removeAtom(atom);
		}
		Molecule molecule = new org.openscience.cdk.Molecule(container);
		SetOfMolecules moleculeSet = ConnectivityChecker
				.partitionIntoMolecules(molecule);
		chemModel.setSetOfMolecules(moleculeSet);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.swing.undo.UndoableEdit#undo()
	 */
	public void undo() throws CannotUndoException {
		for (int i = 0; i < undoRedoContainer.getBondCount(); i++) {
			Bond bond = undoRedoContainer.getBondAt(i);
			container.addBond(bond);
		}
		for (int i = 0; i < undoRedoContainer.getAtomCount(); i++) {
			Atom atom = undoRedoContainer.getAtomAt(i);
			container.addAtom(atom);
		}
		Molecule molecule = new org.openscience.cdk.Molecule(container);
		SetOfMolecules moleculeSet = ConnectivityChecker
				.partitionIntoMolecules(molecule);
		chemModel.setSetOfMolecules(moleculeSet);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.swing.undo.UndoableEdit#canRedo()
	 */
	public boolean canRedo() {
		return true;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.swing.undo.UndoableEdit#canUndo()
	 */
	public boolean canUndo() {
		return true;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.swing.undo.UndoableEdit#getPresentationName()
	 */
	public String getPresentationName() {
		return type;
	}

}
