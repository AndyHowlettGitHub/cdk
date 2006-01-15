/* $RCSfile$ 
 * $Author$
 * $Date$
 * $Revision$
 * 
 * Copyright (C) 1997-2005  The Chemistry Development Kit (CDK) project
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
package org.openscience.cdk.test.graph;

import junit.framework.Assert;
import junit.framework.Test;
import junit.framework.TestSuite;
import org.openscience.cdk.Molecule;
import org.openscience.cdk.exception.InvalidSmilesException;
import org.openscience.cdk.graph.PathTools;
import org.openscience.cdk.interfaces.Atom;
import org.openscience.cdk.interfaces.AtomContainer;
import org.openscience.cdk.smiles.SmilesParser;
import org.openscience.cdk.templates.MoleculeFactory;
import org.openscience.cdk.test.CDKTestCase;

import java.util.List;
import java.util.Vector;

/**
 * @cdk.module test
 */
public class PathToolsTest extends CDKTestCase {
    private Molecule molecule;

    public PathToolsTest(String name) {
        super(name);
    }

    public void setUp() {
        molecule = MoleculeFactory.makeAlphaPinene();
    }

    public static Test suite() {
        return new TestSuite(PathToolsTest.class);
    }

    public void testBreadthFirstTargetSearch() {
        org.openscience.cdk.interfaces.Atom atom1 = molecule.getAtomAt(0);
        org.openscience.cdk.interfaces.Atom atom2 = molecule.getAtomAt(8);
        Vector sphere = new Vector();
        sphere.addElement(atom1);
        int length = PathTools.breadthFirstTargetSearch(molecule, sphere, atom2, 0, 3);
        //System.out.println("PathLengthTest->length: " + length);
        assertEquals(3, length);
    }


    public void testGetPath() {
        AtomContainer atomContainer = null;
        Atom start = null;
        List paths = null;
        SmilesParser sp = new SmilesParser();
        try {
            atomContainer = sp.parseSmiles("c1cc2ccccc2cc1");
            start = atomContainer.getAtomAt(0);
            paths = PathTools.getPathsOfLength(atomContainer, start, 1);
            Assert.assertEquals(2, paths.size());

            atomContainer = sp.parseSmiles("Cc1cc2ccccc2cc1");
            start = atomContainer.getAtomAt(0);
            paths = PathTools.getPathsOfLength(atomContainer, start, 1);
            Assert.assertEquals(1, paths.size());

        } catch (InvalidSmilesException e) {
            e.printStackTrace();
        }
    }

    public void testGetAllPaths1() {
        SmilesParser sp = new SmilesParser();
        try {
            AtomContainer atomContainer = sp.parseSmiles("c12ccccc1cccc2");

            Atom start = atomContainer.getAtomAt(0);
            Atom end = atomContainer.getAtomAt(2);
            List paths = PathTools.getAllPaths(atomContainer, start, end);

            Assert.assertEquals(3, paths.size());

            List path1 = (List) paths.get(0);
            List path2 = (List) paths.get(1);
            List path3 = (List) paths.get(2);

            Assert.assertEquals(start, path1.get(0));
            Assert.assertEquals(atomContainer.getAtomAt(1), path1.get(1));
            Assert.assertEquals(end, path1.get(2));

            Assert.assertEquals(start, path2.get(0));
            Assert.assertEquals(atomContainer.getAtomAt(5), path2.get(1));
            Assert.assertEquals(atomContainer.getAtomAt(4), path2.get(2));
            Assert.assertEquals(atomContainer.getAtomAt(3), path2.get(3));
            Assert.assertEquals(end, path2.get(4));


        } catch (InvalidSmilesException e) {
            e.printStackTrace();
        }
    }

    public void testGetNumberOfVertices() {
        SmilesParser sp = new SmilesParser();
        try {
            AtomContainer atomContainer = sp.parseSmiles("c12ccccc1cccc2");
            Assert.assertEquals(11, PathTools.getVertexCountAtDistance(atomContainer, 1));
            Assert.assertEquals(14, PathTools.getVertexCountAtDistance(atomContainer, 2));
        } catch (InvalidSmilesException e) {
            e.printStackTrace();
        }
    }

}

