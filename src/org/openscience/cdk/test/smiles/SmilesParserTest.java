/*  $RCSfile$
 *  $Author$
 *  $Date$
 *  $Revision$
 *
 *  Copyright (C) 1997-2005  The Chemistry Development Kit (CDK) project
 *
 *  Contact: cdk-devel@lists.sourceforge.net
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
package org.openscience.cdk.test.smiles;

import java.awt.Dimension;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import org.openscience.cdk.Atom;
import org.openscience.cdk.Bond;
import org.openscience.cdk.CDKConstants;
import org.openscience.cdk.Molecule;
import org.openscience.cdk.PseudoAtom;
import org.openscience.cdk.Reaction;
import org.openscience.cdk.SetOfMolecules;
import org.openscience.cdk.applications.swing.MoleculeListViewer;
import org.openscience.cdk.applications.swing.MoleculeViewer2D;
import org.openscience.cdk.isomorphism.IsomorphismTester;
import org.openscience.cdk.layout.StructureDiagramGenerator;
import org.openscience.cdk.smiles.SmilesParser;
import org.openscience.cdk.graph.ConnectivityChecker;
import org.openscience.cdk.tools.LoggingTool;
import org.openscience.cdk.test.CDKTestCase;

/**
 * @cdk.module test
 *
 *@author      steinbeck
 *@cdk.created 2003-09-19
 */
public class SmilesParserTest extends CDKTestCase
{
	boolean standAlone = false;

    private SmilesParser sp;

	/**
	 *  Constructor for the SmilesParserTest object
	 *
	 *@param  name  Description of the Parameter
	 */
	public SmilesParserTest(String name) {
		super(name);
        sp = new SmilesParser();
	}


	/**
	 *  A unit test suite for JUnit
	 *
	 *@return    The test suite
	 */
	public static Test suite()
	{
		return new TestSuite(SmilesParserTest.class);
	}


	/**
	 *  Sets the standAlone attribute of the SmilesParserTest object
	 *
	 *@param  standAlone  The new standAlone value
	 */
	public void setStandAlone(boolean standAlone)
	{
		this.standAlone = standAlone;
	}


    public void testSmiles1() {
        try {
            String smiles = "C1c2c(c3c(c(O)cnc3)cc2)CC(=O)C1";
            Molecule molecule = sp.parseSmiles(smiles);
            assertEquals(16, molecule.getAtomCount());
        } catch (Exception exception) {
            fail(exception.getMessage());
        }
    }
    
    public void testSmiles2() {
        try {
            String smiles = "O=C(O3)C1=COC(OC4OC(CO)C(O)C(O)C4O)C2C1C3C=C2COC(C)=O";
            Molecule molecule = sp.parseSmiles(smiles);
            assertEquals(29, molecule.getAtomCount());
        } catch (Exception exception) {
            fail(exception.getMessage());
        }
    }
    
    public void testSmiles3() {
        try {
            String smiles = "CN1C=NC2=C1C(N(C)C(N2C)=O)=O";
            Molecule molecule = sp.parseSmiles(smiles);
            assertEquals(14, molecule.getAtomCount());
        } catch (Exception exception) {
            fail(exception.getMessage());
        }
    }
    
    public void testSmiles4() {
        try {
            String smiles = "CN(C)CCC2=CNC1=CC=CC(OP(O)(O)=O)=C12";
            Molecule molecule = sp.parseSmiles(smiles);
            assertEquals(19, molecule.getAtomCount());
        } catch (Exception exception) {
            fail(exception.getMessage());
        }
    }
    
    public void testSmiles5() {
        try {
            String smiles = "O=C(O)C1C(OC(C3=CC=CC=C3)=O)CC2N(C)C1CC2";
            Molecule molecule = sp.parseSmiles(smiles);
            assertEquals(21, molecule.getAtomCount());
        } catch (Exception exception) {
            fail(exception.getMessage());
        }
    }
    
    public void testSmiles6() {
        try {
            String smiles = "C1(C2(C)(C))C(C)=CCC2C1";
            Molecule molecule = sp.parseSmiles(smiles);
            assertEquals(10, molecule.getAtomCount());
        } catch (Exception exception) {
            fail(exception.getMessage());
        }
    }
    
    public void xtestSmiles7() {
        try {
            String smiles = "C1(C=C(C=C(C=C(C=C(C=CC%35=C%36)C%31=C%35C%32=C%33C%36=C%34)C%22=C%31C%23=C%32C%24=C%25C%33=C%26C%34=CC%27=CC%28=CC=C%29)C%14=C%22C%15=C%23C%16=C%24C%17=C%18C%25=C%19C%26=C%27C%20=C%28C%29=C%21)C6=C%14C7=C%15C8=C%16C9=C%17C%12=C%11C%18=C%10C%19=C%20C%21=CC%10=CC%11=CC(C=C%30)=C%12%13)=C(C6=C(C7=C(C8=C(C9=C%13C%30=C5)C5=C4)C4=C3)C3=C2)C2=CC=C1";
            Molecule molecule = sp.parseSmiles(smiles);
            assertNotNull(molecule);
        } catch (Exception exception) {
            fail(exception.getMessage());
        }
    }
    
    public void xtestSmiles8() {
        try {
            String smiles = "CC1(C(=C(CC(C1)O)C)C=CC(=CC=CC(=CC=CC=C(C=CC=C(C=CC1=C(CC(CC1(C)C)O)C)C)C)C)C)C";
            Molecule molecule = sp.parseSmiles(smiles);
            assertNotNull(molecule);
        } catch (Exception exception) {
            fail(exception.getMessage());
        }
    }
    
    public void xtestSmiles9() {
        try {
            String smiles = "NC(C(C)C)C(NC(C(C)O)C(NC(C(C)C)C(NC(CCC(N)=O)C(NC(CC([O-])[O-])C(NCC(NC(CC(N)=O)C(NC(Cc1ccccc1)C(NC(CO)C(NC(Cc2ccccc2)C(NC(CO)C(NC(CC(C)C)C(NC(CCC([O-])[O-])C(NC(CO)C(NC(C(C)C)C(NC(CCCC[N+])C(NC(CCCC[N+])C(NC(CC(C)C)C(NC(CCCC[N+])C(NC(CC([O-])[O-])C(NC(CC(C)C)C(NC(CCC(N)=O)C(NC(CCC([O-])[O-])C(N3CCCC3C(NC(CCC(N)=O)C(NC(CCC([O-])[O-])C(N4CCCC4C(NC(CCCNC([N+])[N+])C(NC(C(C)C)C(NCC(NC(CCCC[N+])C(NC(CC(C)C)C(NC(CCCNC([N+])[N+])C(NC(CC(N)=O)C(NC(Cc5ccccc5)C(NC(C)C(N6CCCC6C(NC(C(C)CC)C(N7CCCC7C(NCC(NC(CCC([O-])[O-])C(N8CCCC8C(NC(C(C)C)C(NC(C(C)C)C(N9CCCC9C(NC(C(C)CC)C(NC(CC(C)C)C(NC%19C[S][S]CC(C(NC(CCCC[N+])C(NC(CCC([O-])[O-])C(N%10CCCC%10C(NC(CC(N)=O)C(NC(C)C(NC(CCC(N)=O)C(NC(CCC([O-])[O-])C(NC(C(C)CC)C(NC(CC(C)C)C(NC(CCC(N)=O)C(NC(CCCNC([N+])[N+])C(NC(CC(C)C)C(NC(CCC([O-])[O-])C(NC(CCC([O-])[O-])C(NC(C(C)CC)C(NC(C)C(NC(CCC([O-])[O-])C(NC(CC([O-])[O-])C(N%11CCCC%11C(NCC(NC(C(C)O)C(NC%14C[S][S]CC%13C(NC(C(C)O)C(NCC(NC(C[S][S]CC(C(NC(C)C(NC(Cc%12ccc(O)cc%12)C(NC(C)C(NC(C)C(N%13)=O)=O)=O)=O)=O)NC(=O)C(C(C)CC)NC(=O)C(CCC([O-])[O-])NC%14=O)C(O)=O)=O)=O)=O)=O)=O)=O)=O)=O)=O)=O)=O)=O)=O)=O)=O)=O)=O)=O)=O)=O)=O)=O)=O)=O)=O)NC(=O)C(CC(C)C)NC(=O)C%15CCCN%15C(=O)C(CCCC[N+])NC(=O)C(CC(C)C)NC(=O)C(CCC([O-])[O-])NC(=O)C(CCC([O-])[O-])NC(=O)C%16CCCN%16C(=O)C(Cc%17ccccc%17)NC(=O)C(CC(N)=O)NC(=O)C%18CCCN%18C(=O)C(CC(N)=O)NC(=O)C(CO)NC%19=O)=O)=O)=O)=O)=O)=O)=O)=O)=O)=O)=O)=O)=O)=O)=O)=O)=O)=O)=O)=O)=O)=O)=O)=O)=O)=O)=O)=O)=O)=O)=O)=O)=O)=O)=O)=O)=O)=O)=O)=O)=O)=O)=O)=O)=O)=O)=O";
            Molecule molecule = sp.parseSmiles(smiles);
            assertNotNull(molecule);
        } catch (Exception exception) {
            fail(exception.getMessage());
        }
    }
    
    public void testAromaticSmiles() {
        try {
            String smiles = "c1ccccc1";
            Molecule molecule = sp.parseSmiles(smiles);
            assertTrue(molecule.getAtomAt(0).getFlag(CDKConstants.ISAROMATIC));
            assertTrue(molecule.getBondAt(0).getFlag(CDKConstants.ISAROMATIC));
        } catch (Exception exception) {
            fail(exception.getMessage());
        }
    }
    
	/**
	 * Standalone method only! For JUnit testing, see the
     * testSmilesX() methods.
	 */
	public void viewSmilesStructures()
	{
		MoleculeListViewer mlv = new MoleculeListViewer();
		String[] smiles =
				{
		"C1c2c(c3c(c(O)cnc3)cc2)CC(=O)C1",
		"O=C(O3)C1=COC(OC4OC(CO)C(O)C(O)C4O)C2C1C3C=C2COC(C)=O",
		"CN1C=NC2=C1C(N(C)C(N2C)=O)=O",
		"CN(C)CCC2=CNC1=CC=CC(OP(O)(O)=O)=C12",
		"O=C(O)C1C(OC(C3=CC=CC=C3)=O)CC2N(C)C1CC2",
		"C1(C2(C)(C))C(C)=CCC2C1",
		"C1(C=C(C=C(C=C(C=C(C=CC%35=C%36)C%31=C%35C%32=C%33C%36=C%34)C%22=C%31C%23=C%32C%24=C%25C%33=C%26C%34=CC%27=CC%28=CC=C%29)C%14=C%22C%15=C%23C%16=C%24C%17=C%18C%25=C%19C%26=C%27C%20=C%28C%29=C%21)C6=C%14C7=C%15C8=C%16C9=C%17C%12=C%11C%18=C%10C%19=C%20C%21=CC%10=CC%11=CC(C=C%30)=C%12%13)=C(C6=C(C7=C(C8=C(C9=C%13C%30=C5)C5=C4)C4=C3)C3=C2)C2=CC=C1",
		"CC1(C(=C(CC(C1)O)C)C=CC(=CC=CC(=CC=CC=C(C=CC=C(C=CC1=C(CC(CC1(C)C)O)C)C)C)C)C)C",
        "NC(C(C)C)C(NC(C(C)O)C(NC(C(C)C)C(NC(CCC(N)=O)C(NC(CC([O-])[O-])C(NCC(NC(CC(N)=O)C(NC(Cc1ccccc1)C(NC(CO)C(NC(Cc2ccccc2)C(NC(CO)C(NC(CC(C)C)C(NC(CCC([O-])[O-])C(NC(CO)C(NC(C(C)C)C(NC(CCCC[N+])C(NC(CCCC[N+])C(NC(CC(C)C)C(NC(CCCC[N+])C(NC(CC([O-])[O-])C(NC(CC(C)C)C(NC(CCC(N)=O)C(NC(CCC([O-])[O-])C(N3CCCC3C(NC(CCC(N)=O)C(NC(CCC([O-])[O-])C(N4CCCC4C(NC(CCCNC([N+])[N+])C(NC(C(C)C)C(NCC(NC(CCCC[N+])C(NC(CC(C)C)C(NC(CCCNC([N+])[N+])C(NC(CC(N)=O)C(NC(Cc5ccccc5)C(NC(C)C(N6CCCC6C(NC(C(C)CC)C(N7CCCC7C(NCC(NC(CCC([O-])[O-])C(N8CCCC8C(NC(C(C)C)C(NC(C(C)C)C(N9CCCC9C(NC(C(C)CC)C(NC(CC(C)C)C(NC%19C[S][S]CC(C(NC(CCCC[N+])C(NC(CCC([O-])[O-])C(N%10CCCC%10C(NC(CC(N)=O)C(NC(C)C(NC(CCC(N)=O)C(NC(CCC([O-])[O-])C(NC(C(C)CC)C(NC(CC(C)C)C(NC(CCC(N)=O)C(NC(CCCNC([N+])[N+])C(NC(CC(C)C)C(NC(CCC([O-])[O-])C(NC(CCC([O-])[O-])C(NC(C(C)CC)C(NC(C)C(NC(CCC([O-])[O-])C(NC(CC([O-])[O-])C(N%11CCCC%11C(NCC(NC(C(C)O)C(NC%14C[S][S]CC%13C(NC(C(C)O)C(NCC(NC(C[S][S]CC(C(NC(C)C(NC(Cc%12ccc(O)cc%12)C(NC(C)C(NC(C)C(N%13)=O)=O)=O)=O)=O)NC(=O)C(C(C)CC)NC(=O)C(CCC([O-])[O-])NC%14=O)C(O)=O)=O)=O)=O)=O)=O)=O)=O)=O)=O)=O)=O)=O)=O)=O)=O)=O)=O)=O)=O)=O)=O)=O)=O)=O)=O)NC(=O)C(CC(C)C)NC(=O)C%15CCCN%15C(=O)C(CCCC[N+])NC(=O)C(CC(C)C)NC(=O)C(CCC([O-])[O-])NC(=O)C(CCC([O-])[O-])NC(=O)C%16CCCN%16C(=O)C(Cc%17ccccc%17)NC(=O)C(CC(N)=O)NC(=O)C%18CCCN%18C(=O)C(CC(N)=O)NC(=O)C(CO)NC%19=O)=O)=O)=O)=O)=O)=O)=O)=O)=O)=O)=O)=O)=O)=O)=O)=O)=O)=O)=O)=O)=O)=O)=O)=O)=O)=O)=O)=O)=O)=O)=O)=O)=O)=O)=O)=O)=O)=O)=O)=O)=O)=O)=O)=O)=O)=O)=O"
				};
		for (int f = 0; f < smiles.length; f++)
		{
			try
			{
				Molecule mol = sp.parseSmiles(smiles[f]);
                StructureDiagramGenerator sdg = new StructureDiagramGenerator();
                MoleculeViewer2D mv = new MoleculeViewer2D();
                sdg.setMolecule((Molecule) mol.clone());
                sdg.generateCoordinates();
                mv.setAtomContainer(sdg.getMolecule());
                mlv.addStructure(mv, "Structure " + (f + 1));
			} catch (Exception exc) {
				exc.printStackTrace();
				fail(exc.toString());
			}
		}
        // long l1 = System.currentTimeMillis();
        try
        {
            Molecule mol = sp.parseSmiles(smiles[6]);
            StructureDiagramGenerator sdg = new StructureDiagramGenerator();
            MoleculeViewer2D mv = new MoleculeViewer2D();
            sdg.setMolecule((Molecule) mol.clone());
            sdg.generateCoordinates();
            mv.setAtomContainer(sdg.getMolecule());
            mv.display();
            //mlv.addStructure(mv, "Structure " + (f + 1));
        } catch (Exception exc)
        {
            exc.printStackTrace();
        }
        // long l2 = System.currentTimeMillis();
        //System.out.println(l2-l1);
	}


	/**
	 *  A unit test for JUnit
	 */
	public void testSFBug630475()
	{
		MoleculeListViewer mlv = null;
		if (standAlone)
		{
			mlv = new MoleculeListViewer();
			mlv.setMolViewDim(new Dimension(400, 600));
		}
		String[] smiles =
				{
				"CC1(C(=C(CC(C1)O)C)C=CC(=CC=CC(=CC=CC=C(C=CC=C(C=CC1=C(CC(CC1(C)C)O)C)C)C)C)C)C"
				};
		for (int f = 0; f < smiles.length; f++)
		{
			try
			{
				Molecule mol = sp.parseSmiles(smiles[f]);
				if (standAlone)
				{
					StructureDiagramGenerator sdg = new StructureDiagramGenerator();
					MoleculeViewer2D mv = new MoleculeViewer2D();
					//mv.getRenderer2DModel().setDrawNumbers(true);
					sdg.setMolecule((Molecule) mol.clone());
					sdg.generateCoordinates();
					mv.setAtomContainer(sdg.getMolecule());
					mlv.addStructure(mv, "Structure " + (f + 1));
				}
			} catch (Exception exc)
			{
				exc.printStackTrace();
				fail(exc.toString());
			}
		}
	}


	/**
	 *  A unit test for JUnit
	 */
	public void testSFBug585811()
	{
		String[] smiles =
				{
				"CC(C(C8CCC(CC8)=O)C3C4C(CC5(CCC(C9=CC(C=CN%10)=C%10C=C9)CCCC5)C4)C2CCC1CCC7(CCC7)C6(CC6)C1C2C3)=O"
				};
		MoleculeListViewer mlv = null;
		if (standAlone)
		{
			mlv = new MoleculeListViewer();
			mlv.setMolViewDim(new Dimension(400, 600));
		}
		for (int f = 0; f < smiles.length; f++)
		{
			try
			{
				Molecule mol = sp.parseSmiles(smiles[f]);
				if (standAlone)
				{
					StructureDiagramGenerator sdg = new StructureDiagramGenerator();
					MoleculeViewer2D mv = new MoleculeViewer2D();
					//mv.getRenderer2DModel().setDrawNumbers(true);
					sdg.setMolecule((Molecule) mol.clone());
					sdg.generateCoordinates();
					mv.setAtomContainer(sdg.getMolecule());
					mlv.addStructure(mv, "Structure " + (f + 1));
				}
			} catch (Exception exc)
			{
				exc.printStackTrace();
				fail(exc.toString());
			}
		}
	}


	/**
	 *  This tests the fix made for bug #593648.
	 */
	public void testSFBug593648()
	{
		try
		{
			String smiles = "CC1=CCC2CC1C(C)2C";
			Molecule mol = sp.parseSmiles(smiles);

            Molecule apinene = new Molecule();
            apinene.addAtom(new Atom("C")); // 1
            apinene.addAtom(new Atom("C")); // 2
            apinene.addAtom(new Atom("C")); // 3
            apinene.addAtom(new Atom("C")); // 4
            apinene.addAtom(new Atom("C")); // 5
            apinene.addAtom(new Atom("C")); // 6
            apinene.addAtom(new Atom("C")); // 7
            apinene.addAtom(new Atom("C")); // 8
            apinene.addAtom(new Atom("C")); // 9
            apinene.addAtom(new Atom("C")); // 10
            
            apinene.addBond(0, 1, 2.0); // 1
            apinene.addBond(1, 2, 1.0); // 2
            apinene.addBond(2, 3, 1.0); // 3
            apinene.addBond(3, 4, 1.0); // 4
            apinene.addBond(4, 5, 1.0); // 5
            apinene.addBond(5, 0, 1.0); // 6
            apinene.addBond(0, 6, 1.0); // 7
            apinene.addBond(3, 7, 1.0); // 8
            apinene.addBond(5, 7, 1.0); // 9
            apinene.addBond(7, 8, 1.0); // 10
            apinene.addBond(7, 9, 1.0); // 11

			IsomorphismTester it = new IsomorphismTester(apinene);
			assertTrue(it.isIsomorphic(mol));
		} catch (Exception e)
		{
			fail(e.toString());
		}
	}


	/**
	 *  A unit test for JUnit
	 */
	public void testReadingOfTwoCharElements()
	{
		try
		{
			String smiles = "[Na]";
			Molecule mol = sp.parseSmiles(smiles);
			assertEquals(1, mol.getAtomCount());
			assertEquals("Na", mol.getAtomAt(0).getSymbol());
		} catch (Exception e)
		{
			fail(e.toString());
		}
	}


	/**
	 *  A unit test for JUnit
	 */
	public void testOrganicSubsetUnderstanding()
	{
		try
		{
			String smiles = "[Ni]";
			SmilesParser sp = new SmilesParser();
			Molecule mol = sp.parseSmiles(smiles);
			assertEquals(1, mol.getAtomCount());
			assertEquals("Ni", mol.getAtomAt(0).getSymbol());
		} catch (Exception e)
		{
			fail(e.toString());
		}
		try
		{
			String smiles = "Ni";
			Molecule mol = sp.parseSmiles(smiles);
			assertEquals(2, mol.getAtomCount());
			assertEquals("N", mol.getAtomAt(0).getSymbol());
			assertEquals("I", mol.getAtomAt(1).getSymbol());
		} catch (Exception e)
		{
			fail(e.toString());
		}
	}


	/**
	 *  A unit test for JUnit
	 */
	public void testMassNumberReading()
	{
		try
		{
			String smiles = "[13C]";
			SmilesParser sp = new SmilesParser();
			Molecule mol = sp.parseSmiles(smiles);
			assertEquals(1, mol.getAtomCount());
			assertEquals("C", mol.getAtomAt(0).getSymbol());
			assertEquals(13, mol.getAtomAt(0).getMassNumber());
		} catch (Exception e)
		{
			fail(e.toString());
		}
	}


	/**
	 *  A unit test for JUnit
	 */
	public void testFormalChargeReading()
	{
		try
		{
			String smiles = "[OH-]";
			Molecule mol = sp.parseSmiles(smiles);
			assertEquals(1, mol.getAtomCount());
			assertEquals("O", mol.getAtomAt(0).getSymbol());
			assertEquals(-1, mol.getAtomAt(0).getFormalCharge());
		} catch (Exception e)
		{
			fail(e.toString());
		}
	}


	/**
	 *  A unit test for JUnit
	 */
	public void testReadingPartionedMolecules()
	{
		try
		{
			String smiles = "[Na+].[OH-]";
			Molecule mol = sp.parseSmiles(smiles);
			assertEquals(2, mol.getAtomCount());
			assertEquals(0, mol.getBondCount());
		} catch (Exception e)
		{
			fail(e.toString());
		}
	}

   public void testExplicitSingleBond() {
       try {
           String smiles = "C-C";
           Molecule mol = sp.parseSmiles(smiles);
           assertEquals(2, mol.getAtomCount());
           assertEquals(1, mol.getBondCount());
           assertEquals(1.0, mol.getBondAt(0).getOrder(), 0.0001);
       } catch (Exception e) {
           fail(e.toString());
       }
   }


	/**
	 *  A unit test for JUnit
	 */
	public void testUnkownAtomType()
	{
		try
		{
			String smiles = "*C";
			Molecule mol = sp.parseSmiles(smiles);
			assertEquals(2, mol.getAtomCount());
			assertEquals(1, mol.getBondCount());
			assertTrue(mol.getAtomAt(0) instanceof PseudoAtom);
			assertFalse(mol.getAtomAt(1) instanceof PseudoAtom);
		} catch (Exception e)
		{
			fail(e.toString());
		}
		try
		{
			String smiles = "[*]C";
			Molecule mol = sp.parseSmiles(smiles);
			assertEquals(2, mol.getAtomCount());
			assertEquals(1, mol.getBondCount());
			assertTrue(mol.getAtomAt(0) instanceof PseudoAtom);
			assertFalse(mol.getAtomAt(1) instanceof PseudoAtom);
		} catch (Exception e)
		{
			fail(e.toString());
		}
	}


	/**
	 *  A unit test for JUnit
	 */
	public void testBondCreation()
	{
		try
		{
			String smiles = "CC";
			Molecule mol = sp.parseSmiles(smiles);
			assertEquals(2, mol.getAtomCount());
			assertEquals(1, mol.getBondCount());
		} catch (Exception e)
		{
			fail(e.toString());
		}
		try
		{
			String smiles = "cc";
			Molecule mol = sp.parseSmiles(smiles);
			assertEquals(2, mol.getAtomCount());
			assertEquals(1, mol.getBondCount());
		} catch (Exception e)
		{
			fail(e.toString());
		}
	}


	/**
	 *  A unit test for JUnit
	 */
	public void testSFBug784433()
	{
		try
		{
			String smiles = "c1cScc1";
			Molecule mol = sp.parseSmiles(smiles);
			assertEquals(5, mol.getAtomCount());
			assertEquals(5, mol.getBondCount());
		} catch (Exception e)
		{
			fail(e.toString());
		}
	}

	/**
	 * See SF bug #873783.
	 */
	public void testProton() {
		try {
			String smiles = "[H+]";
			Molecule mol = sp.parseSmiles(smiles);
			assertEquals(1, mol.getAtomCount());
			assertEquals(1, mol.getAtomAt(0).getFormalCharge());
		} catch (Exception e) {
			fail(e.toString());
		}
	}
    
	/**
	 * See SF bug #881330.
	 */
	public void testSMILESFromXYZ() {
		try {
			String smiles = "C.C.N.[Co].C.C.C.[H].[He].[H].[H].[H].[H].C.C.[H].[H].[H].[H].[H]";
			Molecule mol = sp.parseSmiles(smiles);
			assertEquals(20, mol.getAtomCount());
		} catch (Exception e) {
			e.printStackTrace();
			fail(e.toString());
		}
	}

	public void testSingleBracketH() {
		try {
			String smiles = "[H]";
			Molecule mol = sp.parseSmiles(smiles);
			assertEquals(1, mol.getAtomCount());
		} catch (Exception e) {
			fail(e.toString());
		}
	}

	public void testSingleH() {
		try {
			String smiles = "H";
            sp.parseSmiles(smiles);
			fail("The SMILES string 'H' is not valid: H is not in the organic element subset");
		} catch (Exception e) {
            // yes! it should fail
		}
	}
    
	/**
	 * See SF bug #862930.
	 */
	public void testHydroxonium() {
		try {
			String smiles = "[H][O+]([H])[H]";
			Molecule mol = sp.parseSmiles(smiles);
			assertEquals(4, mol.getAtomCount());
		} catch (Exception e) {
			fail(e.toString());
		}
	}

    public void testSFBug809412() {
		try {
			String smiles = "Nc4cc3[n+](c2c(c1c(cccc1)cc2)nc3c5c4cccc5)c6c7c(ccc6)cccc7";
			Molecule mol = sp.parseSmiles(smiles);
			assertEquals(33, mol.getAtomCount());
		} catch (Exception e) {
			fail(e.toString());
		}
    }
    
    /**
     * A bug found with JCP.
     */
    public void testSFBug956926() {
		try {
			String smiles = "[c+]1ccccc1"; // C6H5+, phenyl cation
			Molecule mol = sp.parseSmiles(smiles);
			assertEquals(6, mol.getAtomCount());
            // it's a bit hard to detect three double bonds in the phenyl ring
            // but I do can check the total order in the whole molecule
            double totalBondOrder = 0.0;
            Bond[] bonds = mol.getBonds();
            for (int i=0; i<bonds.length; i++) {
                totalBondOrder += bonds[i].getOrder();
            }
            assertEquals(9.0, totalBondOrder, 0.001);
            // I can also check wether all carbons have exact two neighbors
            Atom[] atoms = mol.getAtoms();
            for (int i=0; i<atoms.length; i++) {
                assertEquals(2, mol.getConnectedAtoms(atoms[i]).length);
            }
            // and the number of implicit hydrogens
            int hCount = 0;
            for (int i=0; i<atoms.length; i++) {
                hCount += atoms[i].getHydrogenCount();
            }
            assertEquals(5, hCount);
		} catch (Exception e) {
			fail(e.toString());
		}
    }
    
    /**
     * A bug found with JCP.
     */
    public void testSFBug956929() {
		try {
			String smiles = "Cn1cccc1";
			Molecule mol = sp.parseSmiles(smiles);
			assertEquals(6, mol.getAtomCount());
            // it's a bit hard to detect two double bonds in the pyrrole ring
            // but I do can check the total order in the whole molecule
            double totalBondOrder = 0.0;
            Bond[] bonds = mol.getBonds();
            for (int i=0; i<bonds.length; i++) {
                totalBondOrder += bonds[i].getOrder();
            }
            assertEquals(8.0, totalBondOrder, 0.001);
            // I can also check wether the total neighbor count around the 
            // nitrogen is 3, all single bonded
            Atom nitrogen = mol.getAtomAt(1); // the second atom
            assertEquals("N", nitrogen.getSymbol());
            totalBondOrder = 0.0;
            bonds = mol.getConnectedBonds(nitrogen);
            assertEquals(3, bonds.length);
            for (int i=0; i<bonds.length; i++) {
                totalBondOrder += bonds[i].getOrder();
            }
            assertEquals(3.0, totalBondOrder, 0.001);
		} catch (Exception e) {
			fail(e.toString());
		}
    }
    
    /**
     * A bug found with JCP.
     */
    public void testSFBug956921() {
		try {
			String smiles = "[cH-]1cccc1";
			Molecule mol = sp.parseSmiles(smiles);
			assertEquals(5, mol.getAtomCount());
            // each atom should have 1 implicit hydrogen, and two neighbors
            Atom[] atoms = mol.getAtoms();
            for (int i=0; i<atoms.length; i++) {
                assertEquals(1, atoms[i].getHydrogenCount());
                assertEquals(2, mol.getConnectedAtoms(atoms[i]).length);
            }
            // and the first atom should have a negative charge
            assertEquals(-1, mol.getAtomAt(0).getFormalCharge());
		} catch (Exception e) {
			fail(e.toString());
		}
    }

    public void testSFBug1095696() {
		try {
			String smiles = "Nc1ncnc2[nH]cnc12";
			Molecule mol = sp.parseSmiles(smiles);
			assertEquals(10, mol.getAtomCount());
            assertEquals(1, mol.getAtomAt(6).getHydrogenCount());
		} catch (Exception e) {
			fail(e.toString());
		}
    }

    /**
     * Example taken from 'Handbook of Chemoinformatics', Gasteiger, 2003,
     * page 89 (Part I).
     */
    public void testNonBond() {
        try {
            String sodiumPhenoxide = "c1cc([O-].[Na+])ccc1";
            Molecule mol = sp.parseSmiles(sodiumPhenoxide);
            assertEquals(8, mol.getAtomCount());
            assertEquals(7, mol.getBondCount());

            ConnectivityChecker connChecker = new ConnectivityChecker();
            SetOfMolecules fragments = connChecker.partitionIntoMolecules(mol);
            int fragmentCount = fragments.getMoleculeCount();
            assertEquals(2, fragmentCount);
            Molecule mol1 = fragments.getMolecule(0);
            Molecule mol2 = fragments.getMolecule(1);
            // one should have one atom, the other seven atoms
            // in any order, so just test the difference
            assertEquals(6, Math.abs(mol1.getAtomCount() - mol2.getAtomCount()));
        } catch (Exception e) {
            fail(e.toString());
        }
    }
    
    /**
     * Example taken from 'Handbook of Chemoinformatics', Gasteiger, 2003,
     * page 89 (Part I).
     */
    public void testConnectedByRingClosure() {
        try {
            String sodiumPhenoxide = "C1.O2.C12";
            Molecule mol = sp.parseSmiles(sodiumPhenoxide);
            assertEquals(3, mol.getAtomCount());
            assertEquals(2, mol.getBondCount());

            ConnectivityChecker connChecker = new ConnectivityChecker();
            SetOfMolecules fragments = connChecker.partitionIntoMolecules(mol);
            int fragmentCount = fragments.getMoleculeCount();
            assertEquals(1, fragmentCount);
            Molecule mol1 = fragments.getMolecule(0);
            assertEquals(3, mol1.getAtomCount());
        } catch (Exception e) {
            fail(e.toString());
        }
    }
    
    /**
     * Example taken from 'Handbook of Chemoinformatics', Gasteiger, 2003,
     * page 89 (Part I).
     */
    public void testReaction() {
        try {
            String reactionSmiles = "O>>[H+].[OH-]";
            Reaction reaction = sp.parseReactionSmiles(reactionSmiles);
            assertEquals(1, reaction.getReactantCount());
            assertEquals(2, reaction.getProductCount());
        } catch (Exception e) {
            e.printStackTrace();
            fail(e.toString());
        }
    }
    
    /**
     * Example taken from 'Handbook of Chemoinformatics', Gasteiger, 2003,
     * page 90 (Part I).
     */
    public void testReactionWithAgents() {
        try {
            String reactionSmiles = "CCO.CC(=O)O>[H+]>CC(=O)OCC.O";
            Reaction reaction = sp.parseReactionSmiles(reactionSmiles);
            assertEquals(2, reaction.getReactantCount());
            assertEquals(2, reaction.getProductCount());
            assertEquals(1, reaction.getAgents().getMoleculeCount());
            
            assertEquals(1, reaction.getAgents().getMolecule(0).getAtomCount());
        } catch (Exception e) {
            e.printStackTrace();
            fail(e.toString());
        }
    }
    
	public void testImplicitHydrogenCount() {
		try {
			String smiles = "C";
			Molecule mol = sp.parseSmiles(smiles);
			assertEquals(1, mol.getAtomCount());
			assertEquals(4, mol.getAtomAt(0).getHydrogenCount());
		} catch (Exception e) {
			fail(e.getMessage());
		}
	}
    
	public void testImplicitHydrogenCount2() {
		try {
			String smiles = "CC";
			Molecule mol = sp.parseSmiles(smiles);
			assertEquals(2, mol.getAtomCount());
			assertEquals(3, mol.getAtomAt(0).getHydrogenCount());
		} catch (Exception e) {
			fail(e.getMessage());
		}
	}
    
	public void testImplicitHydrogenCount2b() {
		try {
			String smiles = "C=C";
			Molecule mol = sp.parseSmiles(smiles);
			assertEquals(2, mol.getAtomCount());
			assertEquals(2, mol.getAtomAt(0).getHydrogenCount());
		} catch (Exception e) {
			fail(e.getMessage());
		}
	}
    
	public void testImplicitHydrogenCount2c() {
		try {
			String smiles = "C#C";
			Molecule mol = sp.parseSmiles(smiles);
			assertEquals(2, mol.getAtomCount());
			assertEquals(1, mol.getAtomAt(0).getHydrogenCount());
		} catch (Exception e) {
			fail(e.getMessage());
		}
	}
    
	public void testImplicitHydrogenCount3() {
		try {
			String smiles = "CCC";
			Molecule mol = sp.parseSmiles(smiles);
			assertEquals(3, mol.getAtomCount());
			assertEquals(2, mol.getAtomAt(1).getHydrogenCount());
		} catch (Exception e) {
			fail(e.getMessage());
		}
	}
    
	public void testImplicitHydrogenCount4() {
		try {
			String smiles = "C1CCCCC1";
			Molecule mol = sp.parseSmiles(smiles);
			assertEquals(6, mol.getAtomCount());
			assertEquals(2, mol.getAtomAt(0).getHydrogenCount());
		} catch (Exception e) {
			fail(e.getMessage());
		}
	}
    
	public void testImplicitHydrogenCount4a() {
		try {
			String smiles = "c1=cc=cc=c1";
			Molecule mol = sp.parseSmiles(smiles);
			assertEquals(6, mol.getAtomCount());
			assertEquals(1, mol.getAtomAt(0).getHydrogenCount());
		} catch (Exception e) {
			fail(e.getMessage());
		}
	}
    
	public void testImplicitHydrogenCount4b() {
		try {
			String smiles = "c1ccccc1";
			Molecule mol = sp.parseSmiles(smiles);
			assertEquals(6, mol.getAtomCount());
			assertEquals(1, mol.getAtomAt(0).getHydrogenCount());
		} catch (Exception e) {
			fail(e.getMessage());
		}
	}
    
	public void testHOSECodeProblem() {
		try {
			String smiles = "CC=CBr";
			Molecule mol = sp.parseSmiles(smiles);
			assertEquals(4, mol.getAtomCount());
			assertEquals("Br", mol.getAtomAt(3).getSymbol());
		} catch (Exception e) {
			fail(e.getMessage());
		}
	}
    
	public void testPyridine() {
		try {
			SmilesParser sp = new SmilesParser();

			Molecule mol = sp.parseSmiles("c1ccncc1");
			assertEquals(6, mol.getAtomCount());
            // it's a bit hard to detect two double bonds in the pyrrole ring
            // but I do can check the total order in the whole molecule
            double totalBondOrder = 0.0;
            Bond[] bonds = mol.getBonds();
            for (int i=0; i<bonds.length; i++) {
                totalBondOrder += bonds[i].getOrder();
            }
            assertEquals(9.0, totalBondOrder, 0.001);
            // I can also check wether the total neighbor count around the 
            // nitrogen is 3, all single bonded
            Atom nitrogen = mol.getAtomAt(3); // the second atom
            assertEquals("N", nitrogen.getSymbol());
            totalBondOrder = 0.0;
            bonds = mol.getConnectedBonds(nitrogen);
            assertEquals(2, bonds.length);
            for (int i=0; i<bonds.length; i++) {
                totalBondOrder += bonds[i].getOrder();
            }
            assertEquals(3.0, totalBondOrder, 0.001);
		} catch (Exception exception) {
			fail(exception.toString());
		}
	}

	/**
	 *  The main program for the SmilesParserTest class
	 *
	 *@param  args  The command line arguments
	 */
	public static void main(String[] args) {
        LoggingTool logger = new LoggingTool(SmilesParserTest.class);
        LoggingTool.configureLog4j();
        logger.info("Running the SmilesParserTest tests...");
		SmilesParserTest spt = new SmilesParserTest("SmilesParserTest");
		spt.setStandAlone(true);
		spt.viewSmilesStructures();
	}
}

