/*  $RCSfile$
 *  $Author$
 *  $Date$
 *  $Revision$
 *
 *  Copyright (C) 1997-2004  The Chemistry Development Kit (CDK) project
 *
 *  Contact: cdk-devel@list.sourceforge.net
 *
 *  This program is free software; you can redistribute it and/or
 *  modify it under the terms of the GNU Lesser General Public License
 *  as published by the Free Software Foundation; either version 2.1
 *  of the License, or (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU Lesser General Public License for more details.
 *
 *  You should have received a copy of the GNU Lesser General Public License
 *  along with this program; if not, write to the Free Software
 *  Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 */
package org.openscience.cdk.test.aromaticity;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Iterator;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import org.openscience.cdk.CDKConstants;
import org.openscience.cdk.ChemObject;
import org.openscience.cdk.Molecule;
import org.openscience.cdk.Ring;
import org.openscience.cdk.RingSet;
import org.openscience.cdk.applications.swing.MoleculeViewer2D;
import org.openscience.cdk.aromaticity.HueckelAromaticityDetector;
import org.openscience.cdk.io.MDLReader;
import org.openscience.cdk.ringsearch.AllRingsFinder;
import org.openscience.cdk.smiles.SmilesParser;
import org.openscience.cdk.templates.MoleculeFactory;

/**
 *  Description of the Class
 *
 * @cdkPackage test
 *
 *@author     steinbeck
 *@created    2002-10-06
 */
public class HueckelAromaticityDetectorTest extends TestCase
{
	boolean standAlone = false;


	/**
	 *  Constructor for the HueckelAromaticityDetectorTest object
	 *
	 *@param  name  Description of the Parameter
	 */
	public HueckelAromaticityDetectorTest(String name)
	{
		super(name);
	}


	/**
	 *  A unit test suite for JUnit
	 *
	 *@return    The test suite
	 */
	public static Test suite()
	{
		return new TestSuite(HueckelAromaticityDetectorTest.class);
	}


	/**
	 *  Sets the standAlone attribute of the HueckelAromaticityDetectorTest object
	 *
	 *@param  standAlone  The new standAlone value
	 */
	public void setStandAlone(boolean standAlone)
	{
		this.standAlone = standAlone;
	}


	/**
	 *  A unit test for JUnit The special difficulty with Azulene is that only the
	 *  outermost larger 10-ring is aromatic according to Hueckel rule.
	 */
	public void testAzulene()
	{
		boolean[] testResult =
				{true,
				true,
				true,
				true,
				true,
				true,
				true,
				true,
				true,
				true
				};
		Molecule molecule = MoleculeFactory.makeAzulene();
		boolean isAromatic = false;
		boolean result = false;
		try
		{
			isAromatic = HueckelAromaticityDetector.detectAromaticity(molecule);
		} catch (Exception exc)
		{
			if (standAlone)
			{
				exc.printStackTrace();
			}
			fail(exc.toString());
		}
		for (int f = 0; f < molecule.getAtomCount(); f++)
		{
			result = (molecule.getAtomAt(f).getFlag(CDKConstants.ISAROMATIC) == testResult[f]);
			assertTrue(result);
			if (standAlone)
			{
				System.out.println("Result for atom " + f + " is correct?: " + result);
			}
		}
		if (standAlone && isAromatic)
		{
			System.out.println("Azulene is aromatic");
		}
	}


	/**
	 *  A unit test for JUnit. The N has to be counted correctly
	 */
	public void testIndole()
	{
		Molecule molecule = MoleculeFactory.makeIndole();
		boolean testResults[] = {
				true,
				true,
				true,
				true,
				true,
				true,
				true,
				true,
				true
				};
		boolean isAromatic = false;
		try
		{
			isAromatic = HueckelAromaticityDetector.detectAromaticity(molecule);
			for (int f = 0; f < molecule.getAtomCount(); f++)
			{
				assertTrue(molecule.getAtomAt(f).getFlag(CDKConstants.ISAROMATIC) == testResults[f]);
			}
		} catch (Exception exc)
		{
			if (standAlone)
			{
				exc.printStackTrace();
			}
			fail(exc.toString());
		}
	}


	/**
	 *  A unit test for JUnit
	 */
	public void testThiazole()
	{
		Molecule molecule = MoleculeFactory.makeThiazole();
		boolean isAromatic = false;
		boolean[] testResults = {true, true, true, true, true};
		try
		{
			isAromatic = HueckelAromaticityDetector.detectAromaticity(molecule);
			for (int f = 0; f < molecule.getAtomCount(); f++)
			{
				assertTrue(molecule.getAtomAt(f).getFlag(CDKConstants.ISAROMATIC) == testResults[f]);
			}

		} catch (Exception exc)
		{
			if (standAlone)
			{
				exc.printStackTrace();
			}
			fail(exc.toString());
		}

	}


	/**
	 *  A unit test for JUnit
	 */
	public void testTetraDehydroDecaline()
	{
		boolean isAromatic = false;
		boolean testResults[] = {false, true, false};
		try
		{
			SmilesParser sp = new SmilesParser();

			Molecule mol = sp.parseSmiles("C1CCCc2c1cccc2");
			RingSet rs = (new AllRingsFinder()).findAllRings(mol);
			(new HueckelAromaticityDetector()).detectAromaticity(mol, rs, true);
			Iterator iter = rs.iterator();
			Ring r = null;
			int i = 0;
			while (iter.hasNext())
			{
				r = (Ring) iter.next();
				isAromatic = r.getFlag(CDKConstants.ISAROMATIC);
				
				if (standAlone && isAromatic)
				{
					System.out.println("Ring " + i + " in test molecule is aromatic.");
				} else if (standAlone && !isAromatic)
				{
					System.out.println("Ring " + i + " in test molecule is not aromatic.");
				}
				assertTrue(testResults[i] == isAromatic);
				i++;
			}
		} catch (Exception exc)
		{
			if (standAlone)
			{
				exc.printStackTrace();
			}
			fail(exc.toString());
		}
	}


	/**
	 *  A unit test for JUnit
	 */
	public void testPorphyrine()
	{
		Molecule molecule = null;
		boolean isAromatic = false;
		boolean testResults[] = {
				false,
				false,
				false,
				false,
				false,
				true,
				true,
				true,
				true,
				true,
				false,
				true,
				true,
				true,
				false,
				true,
				true,
				false,
				false,
				true,
				true,
				false,
				false,
				false,
				true,
				true,
				false,
				false,
				false,
				true,
				true,
				false,
				false,
				false,
				false,
				true,
				true,
				true,
				true,
				false,
				false,
				false
				};
		try
		{
			String filename = "data/mdl/porphyrin.mol";
			InputStream ins = this.getClass().getClassLoader().getResourceAsStream(filename);
			MDLReader reader = new MDLReader(new InputStreamReader(ins));
			molecule = (Molecule) reader.read((ChemObject) new Molecule());

			isAromatic = HueckelAromaticityDetector.detectAromaticity(molecule);
			for (int f = 0; f < molecule.getAtomCount(); f++)
			{
				assertTrue(molecule.getAtomAt(f).getFlag(CDKConstants.ISAROMATIC) == testResults[f]);
			}
		} catch (Exception exc)
		{
			exc.printStackTrace();
			fail();
		}
		assertTrue(isAromatic);
	}


	/**
	 *  A unit test for JUnit
	 */
	public void testBug698152()
	{
		Molecule molecule = null;
		boolean isAromatic = false;
		boolean[] testResults = {true,
				true,
				true,
				true,
				true,
				true,
				false,
				false,
				false,
				false,
				false,
				false,
				false,
				false,
				false,
				false,
				false,
				false};
		try
		{
			String filename = "data/mdl/bug698152.mol";
			InputStream ins = this.getClass().getClassLoader().getResourceAsStream(filename);
			MDLReader reader = new MDLReader(new InputStreamReader(ins));
			molecule = (Molecule) reader.read((ChemObject) new Molecule());

			isAromatic = HueckelAromaticityDetector.detectAromaticity(molecule);
			for (int f = 0; f < molecule.getAtomCount(); f++)
			{
				assertTrue(molecule.getAtomAt(f).getFlag(CDKConstants.ISAROMATIC) == testResults[f]);
			}
		} catch (Exception exc)
		{
			exc.printStackTrace();
			fail();
		}
	}

	/**
	 *  A test for the fix of bug #716259, where a quinone ring 
	 *  was falsely detected as aromatic
	 */
	public void testBug716259()
	{
		Molecule molecule = null;
		boolean isAromatic = false;
		boolean[] testResults = {
			true,
			true,
			true,
			true,
			true,
			true,
			true,
			true,
			true,
			true,
			false,
			false,
			false,
			false,
			false,
			false,
			false,
			false,
			false
		};
		try
		{
			String filename = "data/mdl/bug716259.mol";
			InputStream ins = this.getClass().getClassLoader().getResourceAsStream(filename);
			MDLReader reader = new MDLReader(new InputStreamReader(ins));
			molecule = (Molecule) reader.read((ChemObject) new Molecule());

			isAromatic = HueckelAromaticityDetector.detectAromaticity(molecule);
			for (int f = 0; f < molecule.getAtomCount(); f++)
			{
				assertTrue(molecule.getAtomAt(f).getFlag(CDKConstants.ISAROMATIC) == testResults[f]);
			}

		} catch (Exception exc)
		{
			exc.printStackTrace();
			fail();
		}
	}
	
	
	/**
	 *  A unit test for JUnit
	 */
	public void testQuinone()
	{
		Molecule molecule = MoleculeFactory.makeQuinone();
		boolean isAromatic = false;
		boolean[] testResults = {false, false, false, false, false, false, false, false};
		try
		{
			isAromatic = HueckelAromaticityDetector.detectAromaticity(molecule);
			for (int f = 0; f < molecule.getAtomCount(); f++)
			{
				assertTrue(molecule.getAtomAt(f).getFlag(CDKConstants.ISAROMATIC) == testResults[f]);
			}

		} catch (Exception exc)
		{
			if (standAlone)
			{
				exc.printStackTrace();
			}
			fail(exc.toString());
		}

	}

	/**
	 *  A unit test for JUnit
	 */
	public void testBenzene()
	{
		Molecule molecule = MoleculeFactory.makeBenzene();
		boolean isAromatic = false;
		boolean[] testResults = {true,true,true,true,true,true};
		try
		{
			isAromatic = HueckelAromaticityDetector.detectAromaticity(molecule);
			for (int f = 0; f < molecule.getAtomCount(); f++)
			{
				assertTrue(molecule.getAtomAt(f).getFlag(CDKConstants.ISAROMATIC) == testResults[f]);
			}

		} catch (Exception exc)
		{
			if (standAlone)
			{
				exc.printStackTrace();
			}
			fail(exc.toString());
		}
		
		if (standAlone) MoleculeViewer2D.display(molecule, true);

	}
	
	

	/**
	 *  The main program for the HueckelAromaticityDetectorTest class
	 *
	 *@param  args  The command line arguments
	 */
	public static void main(String[] args)
	{
		HueckelAromaticityDetectorTest hadt = new HueckelAromaticityDetectorTest("HueckelAromaticityDetectorTest");
		hadt.setStandAlone(true);
		//hadt.testAzulene();
		//hadt.testTetraDehydroDecaline();
		//hadt.testIndole();
		//hadt.testThiazole();
		//hadt.testBug698152();
		//hadt.testPorphyrine();
		//hadt.testQuinone();
		//hadt.testBenzene();
		hadt.testBug716259();
	}
}

