/* $RCSfile$
 * $Author$
 * $Date$
 * $Revision$
 *
 * Copyright (C) 2003  The Chemistry Development Kit (CDK) project
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
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 */
package org.openscience.cdk.test.reaction;

import java.io.*;
import java.util.*;
import junit.framework.*;
import org.openscience.cdk.*;
import org.openscience.cdk.exception.*;
import org.openscience.cdk.io.*;
import org.openscience.cdk.tools.*;
import org.openscience.cdk.tools.manipulator.*;
import org.openscience.cdk.isomorphism.*;
import org.openscience.cdk.reaction.ReactionBalancer;

/**
 * @author      Kai Hartmann
 * @cdk.created 2004-02-20
 * @cdk.module  test
 */
public class ReactionBalancerTest extends TestCase {

	protected Reaction reaction1 = new Reaction();
	protected Reaction reaction3 = new Reaction();
	protected Reaction reaction4 = new Reaction();
	
	public ReactionBalancerTest(String name) {
		super(name);
	}
	
    public void setUp() {
		// Read reaction1
		String filename1 = "data/mdl/reaction-1.rxn";
		InputStream ins1 = this.getClass().getClassLoader().getResourceAsStream(filename1);
		MDLRXNReader reader1 = new MDLRXNReader(new InputStreamReader(ins1));
		try {
			this.reaction1 = (Reaction)reader1.read(reaction1);
		} catch (CDKException ex) {
			System.err.println("Could not set up reaction1 in test/tools/ReactionBalancerTest");
		}
		
		// Read reaction3
		String filename3 = "data/mdl/reaction-3.rxn";
		InputStream ins3 = this.getClass().getClassLoader().getResourceAsStream(filename3);
		MDLRXNReader reader3 = new MDLRXNReader(new InputStreamReader(ins3));
		try {
			this.reaction3 = (Reaction)reader3.read(reaction3);
		} catch (CDKException ex) {
			System.err.println("Could not set up reaction3 in test/tools/ReactionBalancerTest");
		}
		
		// Read reaction4
		String filename4 = "data/mdl/reaction-4.rxn";
		InputStream ins4 = this.getClass().getClassLoader().getResourceAsStream(filename4);
		MDLRXNReader reader4 = new MDLRXNReader(new InputStreamReader(ins4));
		try {
			this.reaction4 = (Reaction)reader4.read(reaction4);
		} catch (CDKException ex) {
			System.err.println("Could not set up reaction4 in test/tools/ReactionBalancerTest");
		}
	}
	
    public static Test suite() {
        TestSuite suite = new TestSuite(ReactionBalancerTest.class);
        return suite;
	}
	
    public void testMakeDiffHashtable() {
        ReactionBalancer rb = new ReactionBalancer(reaction1);
		
		Hashtable hash = rb.getDiffHashtable();
		
		assertFalse(hash.containsKey("O"));
		assertFalse(hash.containsKey("C"));
		assertEquals(-1, ((Double)hash.get("Cl")).intValue());
    }
	
	public void testGetMoleculePosition() {
        Molecule mol = new Molecule();
		mol.addAtom(new Atom("H"));
		ReactionBalancer rb = new ReactionBalancer(reaction4);
		int notFound = rb.getMoleculePosition(reaction4.getProducts(), mol);
		assertEquals(-1, notFound);
		
		mol.addAtom(new Atom("H"));
		mol.addBond(0, 1, 1.0);
		int found = rb.getMoleculePosition(reaction4.getProducts(), mol);
		assertEquals(0, found);
    }
	
	public void testGetMoleculePosition2() {
		Molecule hydrogen = new Molecule();
		Molecule proton = new Molecule();
		hydrogen.addAtom(new Atom("H"));
		hydrogen.addAtom(new Atom("H"));
		hydrogen.addBond(0, 1, 1.0);
		proton.addAtom(new Atom("H"));
		proton.getAtomAt(0).setFormalCharge(1);
		//Reaction r = new Reaction();
		//r.getProducts().addAtomContainer(proton, 2.0);
		//ReactionBalancer rb = new ReactionBalancer(r);
		//assertFalse(UniversalIsomorphismTester.isIsomorph(proton, hydrogen));
		//assertFalse(UniversalIsomorphismTester.isIsomorph(r.getProducts().getAtomContainer(0), hydrogen));
		//int notFound = rb.getMoleculePosition(r.getProducts(), hydrogen);
		//assertEquals(-1, notFound);
    }
	
	public void testBalance_water() {
		HydrogenAdder ha = new HydrogenAdder();
		for (int i = 0; i < reaction3.getReactants().getAtomContainerCount(); i++) {
			Molecule mol = reaction3.getReactants().getMolecule(i);
			try {
				ha.addExplicitHydrogensToSatisfyValency(mol);
			} catch(Exception ex) {
				
			}
		}
		for (int i = 0; i < reaction3.getProducts().getAtomContainerCount(); i++) {
			Molecule mol = reaction3.getProducts().getMolecule(i);
			try {
				ha.addExplicitHydrogensToSatisfyValency(mol);
			} catch(Exception ex) {
				
			}
		}
		ReactionBalancer rb = new ReactionBalancer(reaction3);
		boolean success = rb.balance();
		Reaction newReaction = rb.getReaction();
		
		assertTrue(success);
		assertEquals(1, newReaction.getReactants().getAtomContainerCount());
		assertEquals(2, newReaction.getProducts().getAtomContainerCount());
		assertTrue(rb.getDiffHashtable().isEmpty());
	}
	
	public void testBalance_charge() {
		ReactionBalancer rb = new ReactionBalancer(reaction4);
		boolean success = rb.balance();
		Reaction newReaction = rb.getReaction();
		
		assertTrue(success);
		assertEquals(3, newReaction.getReactants().getAtomContainerCount());
		assertEquals(1.0, newReaction.getReactants().getMultiplier(2), 0.00001);
		assertEquals(0.0, SetOfMoleculesManipulator.getTotalFormalCharge(newReaction.getReactants()), 0.000001);
		assertEquals(0.0, SetOfMoleculesManipulator.getTotalFormalCharge(newReaction.getProducts()), 0.000001);
	}
	
	public void testBalance_hydrogen() {
		ReactionBalancer rb = new ReactionBalancer(reaction4);
		boolean success = rb.balance();
		Reaction newReaction = rb.getReaction();
		
		assertTrue(success);
		assertEquals(1.0, newReaction.getReactants().getMultiplier(2), 0.000001);
		assertEquals(2, newReaction.getProducts().getAtomContainerCount());
		assertEquals(3, newReaction.getReactants().getAtomContainerCount());
		assertEquals(2.0, newReaction.getProducts().getMultiplier(0), 0.000001);
	}
	
	public void testBalance_emptyProducts() {
		Reaction r = new Reaction();
		Molecule mol = new Molecule();
		Atom h = new Atom("H");
		Atom o = new Atom("O");
		mol.addAtom(h);
		mol.addAtom(h);
		mol.addAtom(o);
		r.addReactant(mol);
		ReactionBalancer rb = new ReactionBalancer(r);
		boolean success = rb.balance();
		assertTrue(success);
		assertEquals(0, r.getProductCount());
		assertEquals(1, rb.getReaction().getProductCount());
	}
	
	public void testBalance_wrongstoichiometry() {
		// uncorrected: C2O2 -> CO
		// corrected: C2O2 -> 2 CO
		Reaction r = new Reaction();
		Atom c1 = new Atom("C");
		Atom c2 = new Atom("C");
		Atom c3 = new Atom("C");
		Atom o1 = new Atom("O");
		Atom o2 = new Atom("O");
		Atom o3 = new Atom("O");
		Molecule mol1 = new Molecule();
		mol1.addAtom(c1);
		mol1.addAtom(c2);
		mol1.addAtom(o1);
		mol1.addAtom(o2);
		Molecule mol2 = new Molecule();
		mol2.addAtom(c3);
		mol2.addAtom(o3);
		r.addReactant(mol1);
		r.addProduct(mol2);
		ReactionBalancer rb = new ReactionBalancer(r);
		boolean success = rb.balance();
		assertTrue(success);
		//assertEquals(1.0, r.getProductCoefficient(mol2), 0.000001);
		assertEquals(2.0, rb.getReaction().getProductCoefficients()[0], 0.000001);
		assertEquals(2.0, rb.getReaction().getProductCoefficient(mol2), 0.000001);
		
	}
	
	
}

