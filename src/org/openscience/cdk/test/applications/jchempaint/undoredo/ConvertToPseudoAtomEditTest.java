package org.openscience.cdk.test.applications.jchempaint.undoredo;

import junit.framework.Test;
import junit.framework.TestSuite;

import org.openscience.cdk.Atom;
import org.openscience.cdk.Molecule;
import org.openscience.cdk.PseudoAtom;
import org.openscience.cdk.applications.jchempaint.undoredo.ConvertToPseudoAtomEdit;
import org.openscience.cdk.templates.MoleculeFactory;
import org.openscience.cdk.test.CDKTestCase;
import org.openscience.cdk.tools.manipulator.AtomContainerManipulator;

/**
 * Junit test for the ConvertToPseudoAtomEdit class
 * 
 * @author tohel
 * @cdk.module test
 */
public class ConvertToPseudoAtomEditTest extends CDKTestCase {

    private Molecule mol = null;

    public ConvertToPseudoAtomEditTest() {

    }

    /**
     * @return
     */
    public static Test suite() {
        return new TestSuite(ConvertToPseudoAtomEditTest.class);
    }

    /*
     * Test method for
     * 'org.openscience.cdk.applications.jchempaint.undoredo.ConvertToPseudoAtomEdit.redo()'
     */
    public void testRedo() {
        createPseudoAtomMolecule();
        for (int i = 0; i < mol.getAtomCount(); i++) {
            ConvertToPseudoAtomEdit edit = new ConvertToPseudoAtomEdit(mol, mol
                    .getAtomAt(i), new PseudoAtom(mol.getAtomAt(i)));
            edit.undo();
            edit.redo();
        }
        for (int i = 0; i < mol.getAtomCount(); i++) {
            assertTrue(mol.getAtomAt(i) instanceof PseudoAtom);
        }
    }

    /*
     * Test method for
     * 'org.openscience.cdk.applications.jchempaint.undoredo.ConvertToPseudoAtomEdit.undo()'
     */
    public void testUndo() {
        createPseudoAtomMolecule();
        for (int i = 0; i < mol.getAtomCount(); i++) {
            ConvertToPseudoAtomEdit edit = new ConvertToPseudoAtomEdit(mol, mol
                    .getAtomAt(i), (PseudoAtom) mol.getAtomAt(i));
            edit.undo();
        }
        for (int i = 0; i < mol.getAtomCount(); i++) {
            assertTrue(mol.getAtomAt(i) instanceof Atom);
        }
    }

    /**
     * 
     */
    private void createPseudoAtomMolecule() {
        mol = MoleculeFactory.makeAlphaPinene();
        for (int i = 0; i < mol.getAtomCount(); i++) {
            Atom atom = mol.getAtomAt(i);
            PseudoAtom pseudoAtom = new PseudoAtom(atom);
            AtomContainerManipulator.replaceAtomByAtom(mol, atom, pseudoAtom);
        }
    }

}
