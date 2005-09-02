package org.openscience.cdk.test.applications.undoredo;

import java.util.HashMap;

import javax.vecmath.Point2d;

import org.openscience.cdk.Atom;
import org.openscience.cdk.Molecule;
import org.openscience.cdk.applications.undoredo.ChangeCoordsEdit;
import org.openscience.cdk.test.CDKTestCase;

/**
 * Junit test for the ChangeCoordsEdit class
 * 
 * @author tohel
 * @cdk.module test
 * 
 */
public class ChangeCoordsEditTest extends CDKTestCase {

	private Molecule mol;

	private HashMap atomCoordsMap;

	/**
	 * @param map
	 * @param mol
	 */
	public ChangeCoordsEditTest(HashMap map, Molecule mol) {
		this.mol = mol;
		this.atomCoordsMap = map;
	}

	/*
	 * Test method for
	 * 'org.openscience.cdk.applications.undoredo.ChangeCoordsEdit.redo()'
	 */
	public void testRedo() throws Exception {
		ChangeCoordsEdit edit = new ChangeCoordsEdit(atomCoordsMap);
		edit.undo();
		edit.redo();
		for (int i = 0; i < mol.getAtomCount(); i++) {
			org.openscience.cdk.interfaces.Atom atom = mol.getAtomAt(i);
			if (atomCoordsMap.containsKey(atom)) {
				assertTrue(atom.getPoint2d().equals(
						((Point2d[]) atomCoordsMap.get(atom))[0]));
			}
		}
	}

	/*
	 * Test method for
	 * 'org.openscience.cdk.applications.undoredo.ChangeCoordsEdit.undo()'
	 */
	public void testUndo() throws Exception {
		ChangeCoordsEdit edit = new ChangeCoordsEdit(atomCoordsMap);
		edit.undo();
		for (int i = 0; i < mol.getAtomCount(); i++) {
			org.openscience.cdk.interfaces.Atom atom = mol.getAtomAt(i);
			if (atomCoordsMap.containsKey(atom)) {
				assertTrue(atom.getPoint2d().equals(
						((Point2d[]) atomCoordsMap.get(atom))[1]));
			}
		}
	}

}
