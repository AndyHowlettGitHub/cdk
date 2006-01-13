/* $RCSfile$
 * $Author$
 * $Date$
 * $Revision$
 * 
 * Copyright (C) 2002-2005  The Chemistry Development Kit (CDK) project
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
 * 
 */

package org.openscience.cdk.test;

import javax.vecmath.Point2d;
import javax.vecmath.Point3d;

import junit.framework.Test;
import junit.framework.TestSuite;

import org.openscience.cdk.DefaultChemObjectBuilder;
import org.openscience.cdk.interfaces.AminoAcid;
import org.openscience.cdk.interfaces.Atom;
import org.openscience.cdk.interfaces.AtomContainer;
import org.openscience.cdk.interfaces.AtomParity;
import org.openscience.cdk.interfaces.AtomType;
import org.openscience.cdk.interfaces.BioPolymer;
import org.openscience.cdk.interfaces.Bond;
import org.openscience.cdk.interfaces.ChemFile;
import org.openscience.cdk.interfaces.ChemModel;
import org.openscience.cdk.interfaces.ChemObjectBuilder;
import org.openscience.cdk.interfaces.ChemSequence;
import org.openscience.cdk.interfaces.Crystal;
import org.openscience.cdk.interfaces.ElectronContainer;
import org.openscience.cdk.interfaces.Element;
import org.openscience.cdk.interfaces.Isotope;
import org.openscience.cdk.interfaces.LonePair;
import org.openscience.cdk.interfaces.Molecule;
import org.openscience.cdk.interfaces.Monomer;
import org.openscience.cdk.interfaces.Polymer;
import org.openscience.cdk.interfaces.PseudoAtom;
import org.openscience.cdk.interfaces.Reaction;
import org.openscience.cdk.interfaces.Ring;
import org.openscience.cdk.interfaces.RingSet;
import org.openscience.cdk.interfaces.SetOfAtomContainers;
import org.openscience.cdk.interfaces.SetOfMolecules;
import org.openscience.cdk.interfaces.SetOfReactions;
import org.openscience.cdk.interfaces.SingleElectron;
import org.openscience.cdk.interfaces.Strand;

/**
 * Checks the functionality of the Crystal.
 *
 * @cdk.module test
 */
public class DefaultChemObjectBuilderTest extends CDKTestCase {

	protected org.openscience.cdk.ChemObject rootObject;
	
    public DefaultChemObjectBuilderTest(String name) {
        super(name);
    }

    public void setUp() {
        rootObject = new org.openscience.cdk.ChemObject();
    }

    public static Test suite() {
        return new TestSuite(DefaultChemObjectBuilderTest.class);
    }

    public void testGetInstance() {
    	Object builder = DefaultChemObjectBuilder.getInstance();
    	assertNotNull(builder);
    	assertTrue(builder instanceof ChemObjectBuilder);
        assertTrue(builder instanceof DefaultChemObjectBuilder);
    }
    
	public void testNewAminoAcid() {
		Object object = rootObject.getBuilder().newAminoAcid();
		assertNotNull(object);
		assertTrue(object instanceof org.openscience.cdk.ChemObject);

		assertTrue(object instanceof AminoAcid);
	}
	 
	public void testNewAtom() {
		Object object = rootObject.getBuilder().newAtom();
		assertNotNull(object);
		assertTrue(object instanceof org.openscience.cdk.ChemObject);

		assertTrue(object instanceof Atom);
	}
	 
	public void testNewAtom_String() {
		Object object = rootObject.getBuilder().newAtom("C");
		assertNotNull(object);
		assertTrue(object instanceof org.openscience.cdk.ChemObject);

		assertTrue(object instanceof Atom);
	}
	
	public void testNewAtom_String_Point2d() {
		Object object = rootObject.getBuilder().newAtom("C", new Point2d(1.0, 2.0));
		assertNotNull(object);
		assertTrue(object instanceof org.openscience.cdk.ChemObject);

		assertTrue(object instanceof Atom);
	}
	
	public void testNewAtom_String_Point3d() {
		Object object = rootObject.getBuilder().newAtom("C", new Point3d(1.0, 2.0, 3.0));
		assertNotNull(object);
		assertTrue(object instanceof org.openscience.cdk.ChemObject);

		assertTrue(object instanceof Atom);
	}
	
	public void testNewAtomContainer() {
		Object object = rootObject.getBuilder().newAtomContainer();
		assertNotNull(object);
		assertTrue(object instanceof org.openscience.cdk.ChemObject);

		assertTrue(object instanceof AtomContainer);
	}
	
	public void testNewAtomContainer_int_int() {
		Object object = rootObject.getBuilder().newAtomContainer(10,10);
		assertNotNull(object);
		assertTrue(object instanceof org.openscience.cdk.ChemObject);

		assertTrue(object instanceof AtomContainer);
	}
	
	public void testNewAtomContainer_AtomContainer() {
		Object object = rootObject.getBuilder().newAtomContainer(rootObject.getBuilder().newAtomContainer());
		assertNotNull(object);
		assertTrue(object instanceof org.openscience.cdk.ChemObject);

		assertTrue(object instanceof AtomContainer);
	}
	
	public void testNewAtomParity_Atom_Atom_Atom_Atom_Atom_int() {
		Object object = rootObject.getBuilder().newAtomParity(
				rootObject.getBuilder().newAtom(),
				rootObject.getBuilder().newAtom(),
				rootObject.getBuilder().newAtom(),
				rootObject.getBuilder().newAtom(),
				rootObject.getBuilder().newAtom(),
				1
		);
		assertNotNull(object);
		assertTrue(object instanceof org.openscience.cdk.ChemObject);

		assertTrue(object instanceof AtomParity);
	}
	
	public void testNewAtomType_String() {
		Object object = rootObject.getBuilder().newAtomType("Carom");
		assertNotNull(object);
		assertTrue(object instanceof org.openscience.cdk.ChemObject);

		assertTrue(object instanceof AtomType);
	}
	
	public void testNewAtomType_String_String() {
		Object object = rootObject.getBuilder().newAtomType("Carom");
		assertNotNull(object);
		assertTrue(object instanceof org.openscience.cdk.ChemObject);

		assertTrue(object instanceof AtomType);
	}
	
	public void testNewBioPolymer() {
		Object object = rootObject.getBuilder().newBioPolymer();
		assertNotNull(object);
		assertTrue(object instanceof org.openscience.cdk.ChemObject);

		assertTrue(object instanceof BioPolymer);
	}
	
	public void testNewBond() {
		Object object = rootObject.getBuilder().newBond();
		assertNotNull(object);
		assertTrue(object instanceof org.openscience.cdk.ChemObject);

		assertTrue(object instanceof Bond);
	}
	
	public void testNewBond_Atom_Atom() {
		Object object = rootObject.getBuilder().newBond(
			rootObject.getBuilder().newAtom(),
			rootObject.getBuilder().newAtom()
		);
		assertNotNull(object);
		assertTrue(object instanceof org.openscience.cdk.ChemObject);

		assertTrue(object instanceof Bond);
	}
	
	public void testNewBond_Atom_Atom_double() {
		Object object = rootObject.getBuilder().newBond(
			rootObject.getBuilder().newAtom(),
			rootObject.getBuilder().newAtom(),
			1.0
		);
		assertNotNull(object);
		assertTrue(object instanceof org.openscience.cdk.ChemObject);

		assertTrue(object instanceof Bond);
	}
	
	public void testNewBond_Atom_Atom_double_int() {
		Object object = rootObject.getBuilder().newBond(
			rootObject.getBuilder().newAtom(),
			rootObject.getBuilder().newAtom(),
			1.0, 1
		);
		assertNotNull(object);
		assertTrue(object instanceof org.openscience.cdk.ChemObject);

		assertTrue(object instanceof Bond);
	}
	
	public void testNewChemFile() {
		Object object = rootObject.getBuilder().newChemFile();
		assertNotNull(object);
		assertTrue(object instanceof org.openscience.cdk.ChemObject);

		assertTrue(object instanceof ChemFile);
	}
	
	public void testNewChemModel() {
		Object object = rootObject.getBuilder().newChemModel();
		assertNotNull(object);
		assertTrue(object instanceof org.openscience.cdk.ChemObject);

		assertTrue(object instanceof ChemModel);
	}

	public void testNewChemObject() {
		Object object = rootObject.getBuilder().newChemObject();
		assertNotNull(object);
		assertTrue(object instanceof org.openscience.cdk.ChemObject);
	}

	public void testNewChemSequence() {
		Object object = rootObject.getBuilder().newChemSequence();
		assertNotNull(object);
		assertTrue(object instanceof org.openscience.cdk.ChemObject);

		assertTrue(object instanceof ChemSequence);
	}
	
	public void testNewCrystal() {
		Object object = rootObject.getBuilder().newCrystal();
		assertNotNull(object);
		assertTrue(object instanceof org.openscience.cdk.ChemObject);

		assertTrue(object instanceof Crystal);
	}
	
	public void testNewCrystal_AtomContainer() {
		Object object = rootObject.getBuilder().newCrystal(
			rootObject.getBuilder().newAtomContainer()
		);
		assertNotNull(object);
		assertTrue(object instanceof org.openscience.cdk.ChemObject);

		assertTrue(object instanceof Crystal);
	}
	
	public void testNewElectronContainer() {
		Object object = rootObject.getBuilder().newElectronContainer();
		assertNotNull(object);
		assertTrue(object instanceof org.openscience.cdk.ChemObject);

		assertTrue(object instanceof ElectronContainer);
	}

	public void testNewElement() {
		Object object = rootObject.getBuilder().newElement();
		assertNotNull(object);
		assertTrue(object instanceof org.openscience.cdk.ChemObject);

		assertTrue(object instanceof Element);
	}
	
	public void testNewElement_String() {
		Object object = rootObject.getBuilder().newElement("C");
		assertNotNull(object);
		assertTrue(object instanceof org.openscience.cdk.ChemObject);

		assertTrue(object instanceof Element);
	}
	
	public void testNewElement_String_int() {
		Object object = rootObject.getBuilder().newElement("C", 6);
		assertNotNull(object);
		assertTrue(object instanceof org.openscience.cdk.ChemObject);

		assertTrue(object instanceof Element);
	}

	public void testNewIsotope_int_String_double_double() {
		Object object = rootObject.getBuilder().newIsotope(
			12, "C", 12.001, 100.0
		);
		assertNotNull(object);
		assertTrue(object instanceof org.openscience.cdk.ChemObject);

		assertTrue(object instanceof Isotope);
	}
	
	public void testNewIsotope_int_String_int_double_double() {
		Object object = rootObject.getBuilder().newIsotope(
			12, "C", 6, 12.001, 100.0
		);
		assertNotNull(object);
		assertTrue(object instanceof org.openscience.cdk.ChemObject);
		
		assertTrue(object instanceof Isotope);
	}
	
	public void testNewIsotope_String() {
		Object object = rootObject.getBuilder().newIsotope("N");
		assertNotNull(object);
		assertTrue(object instanceof org.openscience.cdk.ChemObject);
		
		assertTrue(object instanceof Isotope);
	}
	
	public void testNewIsotope_String_int() {
		Object object = rootObject.getBuilder().newIsotope("N", 5);
		assertNotNull(object);
		assertTrue(object instanceof org.openscience.cdk.ChemObject);
		
		assertTrue(object instanceof Isotope);
	}

	public void testNewLonePair() {
		Object object = rootObject.getBuilder().newLonePair();
		assertNotNull(object);
		assertTrue(object instanceof org.openscience.cdk.ChemObject);

		assertTrue(object instanceof LonePair);
	}	

	public void testNewLonePair_Atom() {
		Object object = rootObject.getBuilder().newLonePair(
			rootObject.getBuilder().newAtom()
		);
		assertNotNull(object);
		assertTrue(object instanceof org.openscience.cdk.ChemObject);

		assertTrue(object instanceof LonePair);
	}	

	public void testNewMolecule() {
		Object object = rootObject.getBuilder().newMolecule();
		assertNotNull(object);
		assertTrue(object instanceof org.openscience.cdk.ChemObject);

		assertTrue(object instanceof Molecule);
	}	

	public void testNewMolecule_int_int() {
		Object object = rootObject.getBuilder().newMolecule(5,5);
		assertNotNull(object);
		assertTrue(object instanceof org.openscience.cdk.ChemObject);

		assertTrue(object instanceof Molecule);
	}	

	public void testNewMolecule_AtomContainer() {
		Object object = rootObject.getBuilder().newMolecule(
			rootObject.getBuilder().newAtomContainer()
		);
		assertNotNull(object);
		assertTrue(object instanceof org.openscience.cdk.ChemObject);

		assertTrue(object instanceof Molecule);
	}	

	public void testNewMonomer() {
		Object object = rootObject.getBuilder().newMonomer();
		assertNotNull(object);
		assertTrue(object instanceof org.openscience.cdk.ChemObject);

		assertTrue(object instanceof Monomer);
	}	

	public void testNewPolymer() {
		Object object = rootObject.getBuilder().newPolymer();
		assertNotNull(object);
		assertTrue(object instanceof org.openscience.cdk.ChemObject);

		assertTrue(object instanceof Polymer);
	}	

	public void testNewPseudoAtom() {
		Object object = rootObject.getBuilder().newPseudoAtom();
		assertNotNull(object);
		assertTrue(object instanceof org.openscience.cdk.ChemObject);

		assertTrue(object instanceof PseudoAtom);
	}	

	public void testNewPseudoAtom_Atom() {
		Object object = rootObject.getBuilder().newPseudoAtom(
			rootObject.getBuilder().newAtom()
		);
		assertNotNull(object);
		assertTrue(object instanceof org.openscience.cdk.ChemObject);

		assertTrue(object instanceof PseudoAtom);
	}	

	public void testNewPseudoAtom_String() {
		Object object = rootObject.getBuilder().newPseudoAtom("Glu178");
		assertNotNull(object);
		assertTrue(object instanceof org.openscience.cdk.ChemObject);

		assertTrue(object instanceof PseudoAtom);
	}	

	public void testNewPseudoAtom_String_Point2d() {
		Object object = rootObject.getBuilder().newPseudoAtom("Glue178", new Point2d(1.0,2.0));
		assertNotNull(object);
		assertTrue(object instanceof org.openscience.cdk.ChemObject);

		assertTrue(object instanceof PseudoAtom);
	}	

	public void testNewPseudoAtom_String_Point3d() {
		Object object = rootObject.getBuilder().newPseudoAtom("Glue178", new Point3d(1.0,2.0,3.0));
		assertNotNull(object);
		assertTrue(object instanceof org.openscience.cdk.ChemObject);

		assertTrue(object instanceof PseudoAtom);
	}	

	public void testNewReaction() {
		Object object = rootObject.getBuilder().newReaction();
		assertNotNull(object);
		assertTrue(object instanceof org.openscience.cdk.ChemObject);

		assertTrue(object instanceof Reaction);
	}

	public void testNewRing() {
		Object object = rootObject.getBuilder().newRing();
		assertNotNull(object);
		assertTrue(object instanceof org.openscience.cdk.ChemObject);

		assertTrue(object instanceof Ring);
	}	

	public void testNewRing_int() {
		Object object = rootObject.getBuilder().newRing(5);
		assertNotNull(object);
		assertTrue(object instanceof org.openscience.cdk.ChemObject);

		assertTrue(object instanceof Ring);
	}	

	public void testNewRing_int_String() {
		Object object = rootObject.getBuilder().newRing(5,"C");
		assertNotNull(object);
		assertTrue(object instanceof org.openscience.cdk.ChemObject);

		assertTrue(object instanceof Ring);
	}	

	public void testNewRing_AtomContainer() {
		Object object = rootObject.getBuilder().newRing(
			rootObject.getBuilder().newAtomContainer()
		);
		assertNotNull(object);
		assertTrue(object instanceof org.openscience.cdk.ChemObject);

		assertTrue(object instanceof Ring);
	}	

	public void testNewRingSet() {
		Object object = rootObject.getBuilder().newRingSet();
		assertNotNull(object);
        // FIXME: apparently RingSet does not extend IChemObject !
		// assertTrue(object instanceof org.openscience.cdk.interfaces.IChemObject);

		assertTrue(object instanceof RingSet);
	}

	public void testNewSetOfAtomContainers() {
		Object object = rootObject.getBuilder().newSetOfAtomContainers();
		assertNotNull(object);
		assertTrue(object instanceof org.openscience.cdk.ChemObject);

		assertTrue(object instanceof SetOfAtomContainers);
	}

	public void testNewSetOfMolecules() {
		Object object = rootObject.getBuilder().newSetOfMolecules();
		assertNotNull(object);
		assertTrue(object instanceof org.openscience.cdk.ChemObject);

		assertTrue(object instanceof SetOfMolecules);
	}

	public void testNewSetOfReactions() {
		Object object = rootObject.getBuilder().newSetOfReactions();
		assertNotNull(object);
		assertTrue(object instanceof org.openscience.cdk.ChemObject);

		assertTrue(object instanceof SetOfReactions);
	}

	public void testNewSingleElectron() {
		Object object = rootObject.getBuilder().newSingleElectron();
		assertNotNull(object);
		assertTrue(object instanceof org.openscience.cdk.ChemObject);

		assertTrue(object instanceof SingleElectron);
	}

	public void testNewSingleElectron_Atom() {
		Object object = rootObject.getBuilder().newSingleElectron(
			rootObject.getBuilder().newAtom()
		);
		assertNotNull(object);
		assertTrue(object instanceof org.openscience.cdk.ChemObject);

		assertTrue(object instanceof SingleElectron);
	}
	
	public void testNewStrand() {
		Object object = rootObject.getBuilder().newStrand();
		assertNotNull(object);
		assertTrue(object instanceof org.openscience.cdk.ChemObject);

		assertTrue(object instanceof Strand);
	}

}
