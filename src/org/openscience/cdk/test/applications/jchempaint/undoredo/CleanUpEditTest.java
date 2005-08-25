package org.openscience.cdk.test.applications.jchempaint.undoredo;

import java.util.HashMap;

import javax.vecmath.Point2d;
import javax.vecmath.Vector2d;

import junit.framework.Test;
import junit.framework.TestSuite;

import org.openscience.cdk.Atom;
import org.openscience.cdk.Molecule;
import org.openscience.cdk.layout.StructureDiagramGenerator;
import org.openscience.cdk.templates.MoleculeFactory;

/**
 * Junit test for the CleanUpEdit class
 * @author tohel
 *
 */
public class CleanUpEditTest extends ChangeCoordsEditTest {

    private static Molecule mol = MoleculeFactory.makeAlphaPinene();

    private static StructureDiagramGenerator diagramGenerator = new StructureDiagramGenerator();

    /**
     * @throws Exception
     */
    public CleanUpEditTest() throws Exception {
        super(getCleanMolecule(), mol);
    }

    /**
     * @return
     */
    public static Test suite() {
        return new TestSuite(CleanUpEditTest.class);
    }

    /* (non-Javadoc)
     * @see org.openscience.cdk.test.applications.jchempaint.undoredo.ChangeCoordsEditTest#testUndo()
     */
    public void testUndo() throws Exception {
        super.testUndo();
    }

    /* (non-Javadoc)
     * @see org.openscience.cdk.test.applications.jchempaint.undoredo.ChangeCoordsEditTest#testRedo()
     */
    public void testRedo() throws Exception {
        super.testRedo();
    }

    /**
     * @return
     * @throws Exception
     */
    private static HashMap getCleanMolecule() throws Exception {
        HashMap atomCoordsMap = new HashMap();
        StructureDiagramGenerator generator = new StructureDiagramGenerator(mol);
        generator.generateCoordinates();
        mol = relayoutMolecule(mol);
        Atom[] atoms = mol.getAtoms();
        Atom[] newAtoms = mol.getAtoms();
        for (int j = 0; j < atoms.length; j++) {
            Point2d oldCoord = atoms[j].getPoint2d();
            Point2d newCoord = newAtoms[j].getPoint2d();
            if (!oldCoord.equals(newCoord)) {
                Point2d[] coords = new Point2d[2];
                coords[0] = newCoord;
                coords[1] = oldCoord;
                atomCoordsMap.put(newAtoms[j], coords);
            }
        }
        return atomCoordsMap;
    }

    /**
     * @param molecule
     * @return
     * @throws Exception
     */
    private static Molecule relayoutMolecule(Molecule molecule)
            throws Exception {
        Molecule cleanedMol = molecule;
        if (molecule != null) {
            if (molecule.getAtomCount() > 2) {
                diagramGenerator.setMolecule(molecule);
                diagramGenerator.generateExperimentalCoordinates(new Vector2d(
                        0, 1));
                cleanedMol = diagramGenerator.getMolecule();
            }
        }
        return cleanedMol;
    }
}
