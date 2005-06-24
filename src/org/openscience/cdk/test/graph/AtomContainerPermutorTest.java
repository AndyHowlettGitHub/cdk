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
 * You should have received a copy of the GNU Lesserf General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA. 
 */
package org.openscience.cdk.test.graph;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import org.openscience.cdk.Atom;
import org.openscience.cdk.ChemObject;
import org.openscience.cdk.Molecule;
import org.openscience.cdk.test.CDKTestCase;
import org.openscience.cdk.graph.*;
import org.openscience.cdk.AtomContainer;
import org.openscience.cdk.Bond;

/**
 * @cdk.module test
 */
public class AtomContainerPermutorTest extends CDKTestCase
{
	public AtomContainerPermutorTest(String name) {
		super(name);
	}

	public void setUp() {
		
	}

	public static Test suite() {
		return new TestSuite(AtomContainerPermutorTest.class);
	}

	public void testAtomPermutation() 
	{
		AtomContainer ac = new AtomContainer();
		AtomContainer result;
		String atoms = new String("");
		ac.addAtom(new Atom("C"));
		ac.addAtom(new Atom("N"));
		ac.addAtom(new Atom("P"));
		ac.addAtom(new Atom("O"));
		ac.addAtom(new Atom("S"));
		ac.addAtom(new Atom("Br"));
		ac.addBond(0, 1, 1.0);
		ac.addBond(1, 2, 1.0);
		ac.addBond(2, 3, 1.0);
		ac.addBond(3, 4, 1.0);
		ac.addBond(4, 5, 1.0);
		AtomContainerAtomPermutor acap = new
		AtomContainerAtomPermutor(ac);
		int counter = 0;
		while(acap.hasNext())
		{
			counter ++;
			atoms = "";
			result = (AtomContainer)acap.next();
			for (int f = 0; f < result.getAtomCount(); f++)
			{
				atoms += result.getAtomAt(f).getSymbol(); 
			}
		}
		assertEquals(719, counter);
	}

	public void testBondPermutation() 
	{
		AtomContainer ac = new AtomContainer();
		AtomContainer result;
		String bonds = new String("");
		ac.addAtom(new Atom("C"));
		ac.addAtom(new Atom("N"));
		ac.addAtom(new Atom("P"));
		ac.addAtom(new Atom("O"));
		ac.addAtom(new Atom("S"));
		ac.addAtom(new Atom("Br"));
		ac.addBond(0, 1, 1.0);
		ac.addBond(1, 2, 2.0);
		ac.addBond(2, 3, 3.0);
		ac.addBond(3, 4, 4.0);
		ac.addBond(4, 5, 5.0);
		AtomContainerBondPermutor acap = new
		AtomContainerBondPermutor(ac);
		int counter = 0;
		while(acap.hasNext())
		{
			counter ++;
			bonds = "";
			result = (AtomContainer)acap.next();
			for (int f = 0; f < result.getBondCount(); f++)
			{
				bonds += result.getBondAt(f).getOrder(); 
			}
			//System.out.println(bonds);
		}
		assertEquals(119, counter);
	}

	
	public static void main(String[] args)
	{
		AtomContainerPermutorTest acpt = new
		AtomContainerPermutorTest("AtomContainerPermutorTest");
		//acpt.testAtomPermutation();
		acpt.testBondPermutation();
	}
}

