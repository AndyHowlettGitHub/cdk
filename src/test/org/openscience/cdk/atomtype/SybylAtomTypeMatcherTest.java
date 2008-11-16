/* $Revision: 11293 $ $Author: rajarshi $ $Date: 2008-06-06 22:46:01 +0200 (Fri, 06 Jun 2008) $
 * 
 * Copyright (C) 2008  Egon Willighagen <egonw@users.sf.net>
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
package org.openscience.cdk.atomtype;

import java.io.InputStream;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.junit.Assert;
import org.junit.Test;
import org.openscience.cdk.Atom;
import org.openscience.cdk.CDKConstants;
import org.openscience.cdk.DefaultChemObjectBuilder;
import org.openscience.cdk.Molecule;
import org.openscience.cdk.PseudoAtom;
import org.openscience.cdk.exception.CDKException;
import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IAtomType;
import org.openscience.cdk.interfaces.IBond;
import org.openscience.cdk.interfaces.IMolecule;
import org.openscience.cdk.io.Mol2Reader;
import org.openscience.cdk.nonotify.NoNotificationChemObjectBuilder;
import org.openscience.cdk.templates.MoleculeFactory;
import org.openscience.cdk.tools.manipulator.AtomTypeManipulator;

/**
 * This class tests the perception of Sybyl atom types, which uses
 * CDK atom type perception and mapping of CDK atom types to Sybyl
 * atom types.
 *
 * @cdk.module test-atomtype
 */
public class SybylAtomTypeMatcherTest extends AbstractSybylAtomTypeTest {

    private static Map<String, Integer> testedAtomTypes = new HashMap<String, Integer>();

    static {
        // do not complain about a few non-tested atom types
        // so, just mark them as tested
        testedAtomTypes.put("LP", 1);
        testedAtomTypes.put("Du", 1);
        testedAtomTypes.put("Any", 1);
        testedAtomTypes.put("Hal", 1);
        testedAtomTypes.put("Het", 1);
        testedAtomTypes.put("Hev", 1);
        testedAtomTypes.put("X", 1);
        testedAtomTypes.put("Het", 1);
    }

	@Test public void testGetInstance_IChemObjectBuilder() {
		IAtomTypeMatcher matcher = SybylAtomTypeMatcher.getInstance(NoNotificationChemObjectBuilder.getInstance());
		Assert.assertNotNull(matcher);
	}
	
	@Test public void testFindMatchingAtomType_IAtomContainer_IAtom() throws Exception {
		IAtomTypeMatcher matcher = SybylAtomTypeMatcher.getInstance(NoNotificationChemObjectBuilder.getInstance());
		Assert.assertNotNull(matcher);
		Molecule ethane = MoleculeFactory.makeAlkane(2);
		String[] expectedTypes = {"C.3", "C.3"};
        assertAtomTypes(testedAtomTypes, expectedTypes, ethane);
	}

  @Test public void testFindMatchingAtomType_IAtomContainer() throws Exception {
      String filename = "data/mol2/atomtyping.mol2";
      InputStream ins = this.getClass().getClassLoader().getResourceAsStream(filename);
      Mol2Reader reader = new Mol2Reader(ins);
      IMolecule mol = (IMolecule)reader.read(new Molecule());

      // just check consistency; other methods do perception testing
      SybylAtomTypeMatcher matcher = SybylAtomTypeMatcher.getInstance(DefaultChemObjectBuilder.getInstance());
      IAtomType[] types = matcher.findMatchingAtomType(mol);
      for (int i=0; i<types.length; i++) {
          IAtomType type = matcher.findMatchingAtomType(mol, mol.getAtom(i));
          Assert.assertEquals(type.getAtomTypeName(), types[i].getAtomTypeName());
      }
  }

    @Test public void testAtomTyping() throws Exception {
        String filename = "data/mol2/atomtyping.mol2";
        InputStream ins = this.getClass().getClassLoader().getResourceAsStream(filename);
        Mol2Reader reader = new Mol2Reader(ins);
        IMolecule molecule = (IMolecule)reader.read(new Molecule());
        Assert.assertNotNull(molecule);
        IMolecule reference = (IMolecule)molecule.clone();
        
        // test if the perceived atom types match that
        percieveAtomTypesAndConfigureAtoms(molecule);
        Iterator<IAtom> refAtoms = reference.atoms().iterator();
        Iterator<IAtom> atoms = molecule.atoms().iterator();
        while (atoms.hasNext() && refAtoms.hasNext()) {
        	// work around aromaticity, which we skipped for now
        	Assert.assertEquals(
        		"Perceived atom type does not match atom type in file",
        		refAtoms.next().getAtomTypeName(),
        		atoms.next().getAtomTypeName()
        	);
        }
    }

    /**
     * Uses findMatchingAtomType(IAtomContainer, IAtom) type.
     */
    @Test public void testBenzene() throws Exception {
        IMolecule benzene = MoleculeFactory.makeBenzene();

        // test if the perceived atom types match that
        SybylAtomTypeMatcher matcher = SybylAtomTypeMatcher.getInstance(benzene.getBuilder());
        for (IAtom atom : benzene.atoms()) {
          atom.setAtomTypeName(null);
          IAtomType matched = matcher.findMatchingAtomType(benzene, atom);
          Assert.assertEquals("C.2", matched.getAtomTypeName());
        }
    }

    /**
     * Uses findMatchingAtomType(IAtomContainer) type.
     */
    @Test public void testBenzene_AtomContainer() throws Exception {
        IMolecule benzene = MoleculeFactory.makeBenzene();

        // test if the perceived atom types match that
        SybylAtomTypeMatcher matcher = SybylAtomTypeMatcher.getInstance(benzene.getBuilder());
        IAtomType[] types = matcher.findMatchingAtomType(benzene);
        for (IAtomType type : types) {
          Assert.assertEquals("C.ar", type.getAtomTypeName());
        }
    }

	@Test public void testAtomTyping4() throws Exception {
        String filename = "data/mol2/atomtyping4.mol2";
        InputStream ins = this.getClass().getClassLoader().getResourceAsStream(filename);
        Mol2Reader reader = new Mol2Reader(ins);
        IMolecule molecule = (IMolecule)reader.read(new Molecule());
        Assert.assertNotNull(molecule);
        IMolecule reference = (IMolecule)molecule.clone();
        
        // test if the perceived atom types match that
        percieveAtomTypesAndConfigureAtoms(molecule);
        Iterator<IAtom> refAtoms = reference.atoms().iterator();
        Iterator<IAtom> atoms = molecule.atoms().iterator();
        while (atoms.hasNext() && refAtoms.hasNext()) {
            // work around aromaticity, which we skipped for now
            IAtom refAtom = refAtoms.next();
            String refName = refAtom.getAtomTypeName();
            if (refName.endsWith(".ar")) {
                refName = refName.substring(0, refName.indexOf(".")) + ".2";
                refAtom.setAtomTypeName(refName);
            }
        	Assert.assertEquals(
        		"Perceived atom type does not match atom type in file",
        		refAtom.getAtomTypeName(),
        		atoms.next().getAtomTypeName()
        	);
        }
    }

    @Test public void testAtomTyping3() throws Exception {
        String filename = "data/mol2/atomtyping3.mol2";
        InputStream ins = this.getClass().getClassLoader().getResourceAsStream(filename);
        Mol2Reader reader = new Mol2Reader(ins);
        IMolecule molecule = (IMolecule)reader.read(new Molecule());
        Assert.assertNotNull(molecule);
        IMolecule reference = (IMolecule)molecule.clone();
        
        // test if the perceived atom types match that
        percieveAtomTypesAndConfigureAtoms(molecule);
        Iterator<IAtom> refAtoms = reference.atoms().iterator();
        Iterator<IAtom> atoms = molecule.atoms().iterator();
        while (atoms.hasNext() && refAtoms.hasNext()) {
        	// work around aromaticity, which we skipped for now
        	IAtom refAtom = refAtoms.next();
          String refName = refAtom.getAtomTypeName();
        	if (refName.endsWith(".ar")) {
        	    refName = refName.substring(0, refName.indexOf(".")) + ".2";
        	    refAtom.setAtomTypeName(refName);
        	}
        	Assert.assertEquals(
        		"Perceived atom type does not match atom type in file",
        		refAtom.getAtomTypeName(),
        		atoms.next().getAtomTypeName()
        	);
        }
    }

    private void percieveAtomTypesAndConfigureAtoms(IAtomContainer container) throws CDKException {
    	SybylAtomTypeMatcher matcher = SybylAtomTypeMatcher.getInstance(container.getBuilder());
        Iterator<IAtom> atoms = container.atoms().iterator();
        while (atoms.hasNext()) {
        	IAtom atom = atoms.next();
        	atom.setAtomTypeName(null);
        	IAtomType matched = matcher.findMatchingAtomType(container, atom);
        	if (matched != null) AtomTypeManipulator.configure(atom, matched);
        }
	}

    @Test public void countTestedAtomTypes() {
    	super.countTestedAtomTypes(testedAtomTypes);
    }
    
    @Test public void testForDuplicateDefinitions() {
    	super.testForDuplicateDefinitions();
    }

    @Test public void testDummy() throws Exception {
        IMolecule mol = new Molecule();
        IAtom atom = new PseudoAtom("R");
        mol.addAtom(atom);

        String[] expectedTypes = {"X"};
        assertAtomTypeNames(testedAtomTypes, expectedTypes, mol);
    }

    @Test public void testEthene() throws Exception {
    	IMolecule mol = new Molecule();
        IAtom atom = new Atom("C");
        IAtom atom2 = new Atom("C");
        mol.addAtom(atom);
        mol.addAtom(atom2);
        mol.addBond(0,1,CDKConstants.BONDORDER_DOUBLE);

        String[] expectedTypes = {"C.2", "C.2"};
        assertAtomTypeNames(testedAtomTypes, expectedTypes, mol);
    }

    @Test public void testImine() throws Exception {
    	IMolecule mol = new Molecule();
        IAtom atom = new Atom("C");
        IAtom atom2 = new Atom("N");
        mol.addAtom(atom);
        mol.addAtom(atom2);
        mol.addBond(0,1,CDKConstants.BONDORDER_DOUBLE);

        String[] expectedTypes = {"C.2", "N.2"};
        assertAtomTypeNames(testedAtomTypes, expectedTypes, mol);
    }

    @Test public void testPropyne() throws Exception {
    	IMolecule mol = new Molecule();
        IAtom atom = new Atom("C");
        IAtom atom2 = new Atom("C");
        IAtom atom3 = new Atom("C");
        mol.addAtom(atom);
        mol.addAtom(atom2);
        mol.addAtom(atom3);
        mol.addBond(0,1,CDKConstants.BONDORDER_TRIPLE);
        mol.addBond(2,1,CDKConstants.BONDORDER_SINGLE);

        String[] expectedTypes = {"C.1", "C.1", "C.3"};
        assertAtomTypeNames(testedAtomTypes, expectedTypes, mol);
    }

    @Test public void testHalogenatedMethane() throws Exception {
        IMolecule mol = new Molecule();
        mol.addAtom(new Atom("C"));
        mol.addAtom(new Atom("F"));
        mol.addAtom(new Atom("Cl"));
        mol.addAtom(new Atom("I"));
        mol.addAtom(new Atom("Br"));
        mol.addBond(0,1,IBond.Order.SINGLE);
        mol.addBond(0,2,IBond.Order.SINGLE);
        mol.addBond(0,3,IBond.Order.SINGLE);
        mol.addBond(0,4,IBond.Order.SINGLE);

        String[] expectedTypes = {"C.3", "F", "Cl", "I", "Br"};
        assertAtomTypeNames(testedAtomTypes, expectedTypes, mol);
    }

}
