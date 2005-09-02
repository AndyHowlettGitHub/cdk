package org.openscience.cdk.test.applications.undoredo;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;

import javax.swing.undo.UndoableEdit;

import junit.framework.Test;
import junit.framework.TestSuite;

import org.openscience.cdk.Atom;
import org.openscience.cdk.Molecule;
import org.openscience.cdk.applications.undoredo.ChangeAtomSymbolEdit;
import org.openscience.cdk.templates.MoleculeFactory;
import org.openscience.cdk.test.CDKTestCase;

/**
 * Junit test for the ChangeAtomSymbolEdit class
 * 
 * @author tohel
 * @cdk.module test
 */
public class ChangeAtomSymbolEditTest extends CDKTestCase {

	/**
	 * 
	 */
	public ChangeAtomSymbolEditTest() {
	}

	/**
	 * @return
	 */
	public static Test suite() {
		return new TestSuite(ChangeAtomSymbolEditTest.class);
	}

	/*
	 * Test method for
	 * 'org.openscience.cdk.applications.undoredo.ChangeAtomSymbolEdit.redo()'
	 */
	public void testRedo() {
		HashMap atomSymbolMap = createAllNitrogenMol();
		Set atoms = atomSymbolMap.keySet();
		Iterator it = atoms.iterator();
		while (it.hasNext()) {
			Atom atom = (Atom) it.next();
			String[] symbols = (String[]) atomSymbolMap.get(atom);
			UndoableEdit edit = new ChangeAtomSymbolEdit(atom, symbols[0],
					symbols[1]);
			edit.undo();
			edit.redo();
			assertTrue(atom.getSymbol().equals(symbols[1]));
		}
	}

	/*
	 * Test method for
	 * 'org.openscience.cdk.applications.undoredo.ChangeAtomSymbolEdit.undo()'
	 */
	public void testUndo() {
		HashMap atomSymbolMap = createAllNitrogenMol();
		Set atoms = atomSymbolMap.keySet();
		Iterator it = atoms.iterator();
		while (it.hasNext()) {
			Atom atom = (Atom) it.next();
			String[] symbols = (String[]) atomSymbolMap.get(atom);
			UndoableEdit edit = new ChangeAtomSymbolEdit(atom, symbols[0],
					symbols[1]);
			edit.undo();
			assertTrue(atom.getSymbol().equals(symbols[0]));
		}
	}

	/**
	 * @return
	 */
	private HashMap createAllNitrogenMol() {
		HashMap atomSymbolMap = new HashMap();
		Molecule mol = MoleculeFactory.makeAlphaPinene();
		for (int i = 0; i < mol.getAtomCount(); i++) {
			org.openscience.cdk.interfaces.Atom atom = mol.getAtomAt(i);
			String formerSymbol = atom.getSymbol();
			String[] symbols = new String[2];
			symbols[0] = formerSymbol;
			symbols[1] = "N";
			atom.setSymbol("N");
			atomSymbolMap.put(atom, symbols);
		}
		return atomSymbolMap;
	}

}
