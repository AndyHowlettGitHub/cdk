/* $Revision$ $Author$ $Date$
 * 
 * Copyright (C) 1997-2007  The Chemistry Development Kit (CDK) project
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

import java.util.HashMap;
import java.util.Map;

import junit.framework.JUnit4TestAdapter;

import org.junit.Assert;
import org.junit.Test;
import org.openscience.cdk.Atom;
import org.openscience.cdk.Bond;
import org.openscience.cdk.DefaultChemObjectBuilder;
import org.openscience.cdk.Molecule;
import org.openscience.cdk.config.AtomTypeFactory;
import org.openscience.cdk.exception.CDKException;
import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IAtomType;
import org.openscience.cdk.interfaces.IBond;
import org.openscience.cdk.interfaces.IMolecule;
import org.openscience.cdk.nonotify.NNMolecule;
import org.openscience.cdk.nonotify.NoNotificationChemObjectBuilder;

/**
 * @cdk.module test-extra
 */
public class HybridizationMatcherTest extends AbstractAtomTypeTest {

    private static Map<String, Integer> testedAtomTypes = new HashMap<String, Integer>();

    public static junit.framework.Test suite() {
        return new JUnit4TestAdapter(HybridizationMatcherTest.class);
    }
    
    @Test public void testHybridizationMatcher() throws CDKException {
        HybridizationMatcher matcher = new HybridizationMatcher();
        Assert.assertNotNull(matcher);
    }
    
    @Test public void testFindMatchingAtomType_IAtomContainer_IAtom() throws ClassNotFoundException, CDKException, java.lang.Exception {
        Molecule mol = new Molecule();
        Atom atom = new Atom("C");
        final IAtomType.Hybridization thisHybridization = IAtomType.Hybridization.SP1;
        atom.setHybridization(thisHybridization);
        mol.addAtom(atom);

        HybridizationMatcher atm = new HybridizationMatcher();
        IAtomType matched = atm.findMatchingAtomType(mol, atom);
        Assert.assertNotNull(matched);
        Assert.assertEquals("C", matched.getSymbol());
        
        Assert.assertEquals(thisHybridization, matched.getHybridization());
    }
    
    @Test public void testN3() throws CDKException {
        Molecule mol = new Molecule();

        Atom atom = new Atom("N");
        for (int i = 0; i < 3; i++) {
            Atom h = new Atom("H");
            mol.addAtom(h);
            mol.addBond(new Bond(atom, h, IBond.Order.SINGLE)) ;
        }
                       

        HybridizationMatcher atm = new HybridizationMatcher();
        IAtomType matched = atm.findMatchingAtomType(mol, atom);
        Assert.assertNotNull(matched);
        Assert.assertEquals("N", matched.getSymbol());
    }


    @Test public void testFlourine() throws Exception {
        IMolecule mol = DefaultChemObjectBuilder.getInstance().newMolecule();
        IAtom atom1 = DefaultChemObjectBuilder.getInstance().newAtom("C");
        atom1.setHydrogenCount(0);        
        mol.addAtom(atom1);
        for (int i = 0; i < 4; i++) {
            IAtom floruineAtom = DefaultChemObjectBuilder.getInstance().newAtom("F");
            mol.addAtom(floruineAtom);
            IBond bond = DefaultChemObjectBuilder.getInstance().newBond(floruineAtom, atom1);
            mol.addBond(bond);
        }

        HybridizationMatcher matcher = new HybridizationMatcher();
        IAtomType matched = matcher.findMatchingAtomType(mol, mol.getAtom(0));
        Assert.assertNotNull(matched);
        assertAtomType(testedAtomTypes, "C.sp3", matched);

        for (int i = 1; i < mol.getAtomCount(); i++) {
            IAtom atom = mol.getAtom(i);
            matched = matcher.findMatchingAtomType(mol, atom);
            assertAtomType(testedAtomTypes, "atom " + i + " failed to match", "F", matched);
        }
    }

    @Test public void testChlorine() throws Exception {
        IMolecule mol = DefaultChemObjectBuilder.getInstance().newMolecule();
        IAtom atom1 = DefaultChemObjectBuilder.getInstance().newAtom("C");
        atom1.setHydrogenCount(0);
        mol.addAtom(atom1);
        for (int i = 0; i < 4; i++) {
            IAtom floruineAtom = DefaultChemObjectBuilder.getInstance().newAtom("Cl");
            mol.addAtom(floruineAtom);
            IBond bond = DefaultChemObjectBuilder.getInstance().newBond(floruineAtom, atom1);
            mol.addBond(bond);
        }

        HybridizationMatcher matcher = new HybridizationMatcher();
        IAtomType matched = matcher.findMatchingAtomType(mol, mol.getAtom(0));
        assertAtomType(testedAtomTypes, "C.sp3", matched);

        for (int i = 1; i < mol.getAtomCount(); i++) {
            IAtom atom = mol.getAtom(i);
            matched = matcher.findMatchingAtomType(mol, atom);
            assertAtomType(testedAtomTypes, "atom " + i + " failed to match", "Cl", matched);
        }
    }

    @Test public void testBromine() throws Exception {
        IMolecule mol = DefaultChemObjectBuilder.getInstance().newMolecule();
        IAtom atom1 = DefaultChemObjectBuilder.getInstance().newAtom("C");
        atom1.setHydrogenCount(0);
        mol.addAtom(atom1);
        for (int i = 0; i < 4; i++) {
            IAtom floruineAtom = DefaultChemObjectBuilder.getInstance().newAtom("Br");
            mol.addAtom(floruineAtom);
            IBond bond = DefaultChemObjectBuilder.getInstance().newBond(floruineAtom, atom1);
            mol.addBond(bond);
        }

        HybridizationMatcher matcher = new HybridizationMatcher();
        IAtomType matched = matcher.findMatchingAtomType(mol, mol.getAtom(0));
        assertAtomType(testedAtomTypes, "C.sp3", matched);

        for (int i = 1; i < mol.getAtomCount(); i++) {
            IAtom atom = mol.getAtom(i);
            matched = matcher.findMatchingAtomType(mol, atom);
            assertAtomType(testedAtomTypes, "atom " + i + " failed to match", "Br", matched);
        }
    }

    @Test public void testIodine() throws Exception {
        IMolecule mol = DefaultChemObjectBuilder.getInstance().newMolecule();
        IAtom atom1 = DefaultChemObjectBuilder.getInstance().newAtom("C");
        atom1.setHydrogenCount(0);
        mol.addAtom(atom1);
        for (int i = 0; i < 4; i++) {
            IAtom floruineAtom = DefaultChemObjectBuilder.getInstance().newAtom("I");
            mol.addAtom(floruineAtom);
            IBond bond = DefaultChemObjectBuilder.getInstance().newBond(floruineAtom, atom1);
            mol.addBond(bond);
        }

        HybridizationMatcher matcher = new HybridizationMatcher();
        IAtomType matched = matcher.findMatchingAtomType(mol, mol.getAtom(0));
        assertAtomType(testedAtomTypes, "C.sp3", matched);

        for (int i = 1; i < mol.getAtomCount(); i++) {
            IAtom atom = mol.getAtom(i);
            matched = matcher.findMatchingAtomType(mol, atom);
            assertAtomType(testedAtomTypes, "atom " + i + " failed to match", "I", matched);
        }
    }

    /*
    Tests C4, O2
     */
    @Test public void testOxygen1() throws CDKException {
        IMolecule mol = DefaultChemObjectBuilder.getInstance().newMolecule();
        IAtom carbon = DefaultChemObjectBuilder.getInstance().newAtom("C");
        IAtom o1 = DefaultChemObjectBuilder.getInstance().newAtom("O");
        IAtom o2 = DefaultChemObjectBuilder.getInstance().newAtom("O");

        IAtom h1 = DefaultChemObjectBuilder.getInstance().newAtom("H");
        IAtom h2 = DefaultChemObjectBuilder.getInstance().newAtom("H");

        IBond bond1 = DefaultChemObjectBuilder.getInstance().newBond(carbon, o1, IBond.Order.SINGLE);
        IBond bond2 = DefaultChemObjectBuilder.getInstance().newBond(carbon, o2, IBond.Order.DOUBLE);

        IBond bond3 = DefaultChemObjectBuilder.getInstance().newBond(carbon, h1, IBond.Order.SINGLE);
        IBond bond4 = DefaultChemObjectBuilder.getInstance().newBond(o1, h2, IBond.Order.SINGLE);

        mol.addAtom(carbon);
        mol.addAtom(o1);
        mol.addAtom(o2);
        mol.addAtom(h1);
        mol.addAtom(h2);
        mol.addBond(bond1);
        mol.addBond(bond2);
        mol.addBond(bond3);
        mol.addBond(bond4);

        HybridizationMatcher matcher = new HybridizationMatcher();

        // look at the sp2 O first
        IAtomType matched = matcher.findMatchingAtomType(mol, mol.getAtom(2));
        assertAtomType(testedAtomTypes, "O.sp2", matched);

        matched = matcher.findMatchingAtomType(mol, mol.getAtom(0));
        assertAtomType(testedAtomTypes, "C.sp2", matched);

        matched = matcher.findMatchingAtomType(mol, mol.getAtom(1));
        assertAtomType(testedAtomTypes, "O.sp3", matched);
    }

    /*
    Tests O2, H1
     */
    @Test public void testOxygen2() throws CDKException {
        IMolecule mol = DefaultChemObjectBuilder.getInstance().newMolecule();
        IAtom o1 = DefaultChemObjectBuilder.getInstance().newAtom("O");
        IAtom o2 = DefaultChemObjectBuilder.getInstance().newAtom("O");
        IAtom h1 = DefaultChemObjectBuilder.getInstance().newAtom("H");
        IAtom h2 = DefaultChemObjectBuilder.getInstance().newAtom("H");

        IBond bond1 = DefaultChemObjectBuilder.getInstance().newBond(h1, o1, IBond.Order.SINGLE);
        IBond bond2 = DefaultChemObjectBuilder.getInstance().newBond(o1, o2, IBond.Order.SINGLE);
        IBond bond3 = DefaultChemObjectBuilder.getInstance().newBond(o2, h2, IBond.Order.SINGLE);

        mol.addAtom(o1);
        mol.addAtom(o2);
        mol.addAtom(h1);
        mol.addAtom(h2);

        mol.addBond(bond1);
        mol.addBond(bond2);
        mol.addBond(bond3);

        HybridizationMatcher matcher = new HybridizationMatcher();
        IAtomType matched;

        matched = matcher.findMatchingAtomType(mol, mol.getAtom(0));
        assertAtomType(testedAtomTypes, "O.sp3", matched);

        matched = matcher.findMatchingAtomType(mol, mol.getAtom(1));
        assertAtomType(testedAtomTypes, "O.sp3", matched);

        matched = matcher.findMatchingAtomType(mol, mol.getAtom(2));
        assertAtomType(testedAtomTypes, "H", matched);

        matched = matcher.findMatchingAtomType(mol, mol.getAtom(3));
        assertAtomType(testedAtomTypes, "H", matched);
    }

    /*
    Tests P4, S2, Cl1
     */
    @Test public void testP4() throws CDKException {
        IMolecule mol = DefaultChemObjectBuilder.getInstance().newMolecule();
        IAtom p = DefaultChemObjectBuilder.getInstance().newAtom("P");
        IAtom cl1 = DefaultChemObjectBuilder.getInstance().newAtom("Cl");
        IAtom cl2 = DefaultChemObjectBuilder.getInstance().newAtom("Cl");
        IAtom cl3 = DefaultChemObjectBuilder.getInstance().newAtom("Cl");
        IAtom s = DefaultChemObjectBuilder.getInstance().newAtom("S");

        IBond bond1 = DefaultChemObjectBuilder.getInstance().newBond(p, cl1, IBond.Order.SINGLE);
        IBond bond2 = DefaultChemObjectBuilder.getInstance().newBond(p, cl2, IBond.Order.SINGLE);
        IBond bond3 = DefaultChemObjectBuilder.getInstance().newBond(p, cl3, IBond.Order.SINGLE);
        IBond bond4 = DefaultChemObjectBuilder.getInstance().newBond(p, s, IBond.Order.DOUBLE);

        mol.addAtom(p);
        mol.addAtom(cl1);
        mol.addAtom(cl2);
        mol.addAtom(cl3);
        mol.addAtom(s);

        mol.addBond(bond1);
        mol.addBond(bond2);
        mol.addBond(bond3);
        mol.addBond(bond4);

        HybridizationMatcher matcher = new HybridizationMatcher();
        IAtomType matched;

        matched = matcher.findMatchingAtomType(mol, mol.getAtom(0));
        assertAtomType(testedAtomTypes, "P4", matched);


        matched = matcher.findMatchingAtomType(mol, mol.getAtom(4));
        assertAtomType(testedAtomTypes, "S2", matched);

        for (int i = 1; i < 4; i++) {
            matched = matcher.findMatchingAtomType(mol, mol.getAtom(i));
            assertAtomType(testedAtomTypes, "atom " + i + " failed to match", "Cl", matched);
        }
    }

    /*
    Tests P3, O2, C4
     */
    @Test public void testP3() throws CDKException {
        IMolecule mol = DefaultChemObjectBuilder.getInstance().newMolecule();
        IAtom p = DefaultChemObjectBuilder.getInstance().newAtom("P");
        IAtom o1 = DefaultChemObjectBuilder.getInstance().newAtom("O");
        IAtom o2 = DefaultChemObjectBuilder.getInstance().newAtom("O");
        IAtom o3 = DefaultChemObjectBuilder.getInstance().newAtom("O");
        IAtom c1 = DefaultChemObjectBuilder.getInstance().newAtom("C");
        IAtom c2 = DefaultChemObjectBuilder.getInstance().newAtom("C");
        IAtom c3 = DefaultChemObjectBuilder.getInstance().newAtom("C");

        c1.setHydrogenCount(3);
        c2.setHydrogenCount(3);
        c3.setHydrogenCount(3);

        IBond bond1 = DefaultChemObjectBuilder.getInstance().newBond(p, o1, IBond.Order.SINGLE);
        IBond bond2 = DefaultChemObjectBuilder.getInstance().newBond(p, o2, IBond.Order.SINGLE);
        IBond bond3 = DefaultChemObjectBuilder.getInstance().newBond(p, o3, IBond.Order.SINGLE);
        IBond bond4 = DefaultChemObjectBuilder.getInstance().newBond(c1, o1, IBond.Order.SINGLE);
        IBond bond5 = DefaultChemObjectBuilder.getInstance().newBond(c2, o2, IBond.Order.SINGLE);
        IBond bond6 = DefaultChemObjectBuilder.getInstance().newBond(c3, o3, IBond.Order.SINGLE);

        mol.addAtom(p);
        mol.addAtom(o1);
        mol.addAtom(o2);
        mol.addAtom(o3);
        mol.addAtom(c1);
        mol.addAtom(c2);
        mol.addAtom(c3);

        mol.addBond(bond1);
        mol.addBond(bond2);
        mol.addBond(bond3);
        mol.addBond(bond4);
        mol.addBond(bond5);
        mol.addBond(bond6);

        HybridizationMatcher matcher = new HybridizationMatcher();
        IAtomType matched;

        String[] atomTypes = {"Pacid", "O.sp3", "O.sp3", "O.sp3", "C.sp3", "C.sp3", "C.sp3"};
        for (int i = 0; i < mol.getAtomCount(); i++) {
            matched = matcher.findMatchingAtomType(mol, mol.getAtom(i));
            assertAtomType(testedAtomTypes, "atom " + i + " failed to match.", 
            	atomTypes[i], matched
            );
        }
    }

    /* Test Si4, C4, Cl1 */
    @Test public void testSi4() throws CDKException {
        IMolecule mol = DefaultChemObjectBuilder.getInstance().newMolecule();
        IAtom si = DefaultChemObjectBuilder.getInstance().newAtom("Si");
        IAtom c1 = DefaultChemObjectBuilder.getInstance().newAtom("C");
        IAtom cl1 = DefaultChemObjectBuilder.getInstance().newAtom("Cl");
        IAtom cl2 = DefaultChemObjectBuilder.getInstance().newAtom("Cl");
        IAtom cl3 = DefaultChemObjectBuilder.getInstance().newAtom("Cl");

        c1.setHydrogenCount(3);

        IBond bond1 = DefaultChemObjectBuilder.getInstance().newBond(si, c1, IBond.Order.SINGLE);
        IBond bond2 = DefaultChemObjectBuilder.getInstance().newBond(si, cl1, IBond.Order.SINGLE);
        IBond bond3 = DefaultChemObjectBuilder.getInstance().newBond(si, cl2, IBond.Order.SINGLE);
        IBond bond4 = DefaultChemObjectBuilder.getInstance().newBond(si, cl3, IBond.Order.SINGLE);

        mol.addAtom(si);
        mol.addAtom(c1);
        mol.addAtom(cl1);
        mol.addAtom(cl2);
        mol.addAtom(cl3);

        mol.addBond(bond1);
        mol.addBond(bond2);
        mol.addBond(bond3);
        mol.addBond(bond4);

        HybridizationMatcher matcher = new HybridizationMatcher();
        IAtomType matched;

        matched = matcher.findMatchingAtomType(mol, mol.getAtom(0));
        assertAtomType(testedAtomTypes, "Si.sp3", matched);

        matched = matcher.findMatchingAtomType(mol, mol.getAtom(1));
        assertAtomType(testedAtomTypes, "C.sp3", matched);

        for (int i = 3; i < mol.getAtomCount(); i++) {
            matched = matcher.findMatchingAtomType(mol, mol.getAtom(i));
            assertAtomType(testedAtomTypes, "atom " + i + " failed to match", "Cl", matched);
        }
    }

    /* Tests S2, H1 */
    @Test public void testS2() throws CDKException {
        IMolecule mol = DefaultChemObjectBuilder.getInstance().newMolecule();
        IAtom s = DefaultChemObjectBuilder.getInstance().newAtom("S");
        s.setHydrogenCount(2);

        mol.addAtom(s);

        HybridizationMatcher matcher = new HybridizationMatcher();
        IAtomType matched;

        matched = matcher.findMatchingAtomType(mol, mol.getAtom(0));
        assertAtomType(testedAtomTypes, "S", matched);

        mol = DefaultChemObjectBuilder.getInstance().newMolecule();
        s = DefaultChemObjectBuilder.getInstance().newAtom("S");
        IAtom h1 = DefaultChemObjectBuilder.getInstance().newAtom("H");
        IAtom h2 = DefaultChemObjectBuilder.getInstance().newAtom("H");
        IBond b1 = DefaultChemObjectBuilder.getInstance().newBond(s, h1, IBond.Order.SINGLE);
        IBond b2 = DefaultChemObjectBuilder.getInstance().newBond(s, h2, IBond.Order.SINGLE);

        mol.addAtom(s);
        mol.addAtom(h1);
        mol.addAtom(h2);

        mol.addBond(b1);
        mol.addBond(b2);

        matched = matcher.findMatchingAtomType(mol, mol.getAtom(0));
        assertAtomType(testedAtomTypes, "S", matched);

        matched = matcher.findMatchingAtomType(mol, mol.getAtom(1));
        assertAtomType(testedAtomTypes, "H", matched);

        matched = matcher.findMatchingAtomType(mol, mol.getAtom(2));
        assertAtomType(testedAtomTypes, "H", matched);
    }

    /* Tests S3, O2 */
    @Test public void testS3() throws CDKException {
        IMolecule mol = DefaultChemObjectBuilder.getInstance().newMolecule();
        IAtom s = DefaultChemObjectBuilder.getInstance().newAtom("S");
        IAtom o1 = DefaultChemObjectBuilder.getInstance().newAtom("O");
        IAtom o2 = DefaultChemObjectBuilder.getInstance().newAtom("O");

        IBond b1 = DefaultChemObjectBuilder.getInstance().newBond(s, o1, IBond.Order.DOUBLE);
        IBond b2 = DefaultChemObjectBuilder.getInstance().newBond(s, o2, IBond.Order.DOUBLE);

        mol.addAtom(s);
        mol.addAtom(o1);
        mol.addAtom(o2);

        mol.addBond(b1);
        mol.addBond(b2);

        HybridizationMatcher matcher = new HybridizationMatcher();
        IAtomType matched;


        matched = matcher.findMatchingAtomType(mol, mol.getAtom(0));
        assertAtomType(testedAtomTypes, "S3", matched);


        matched = matcher.findMatchingAtomType(mol, mol.getAtom(1));
        assertAtomType(testedAtomTypes, "O.sp2", matched);


        matched = matcher.findMatchingAtomType(mol, mol.getAtom(2));
        assertAtomType(testedAtomTypes, "O.sp2", matched);
    }


    /* Tests S4, Cl1 */
    @Test public void testS4() throws CDKException {
        IMolecule mol = DefaultChemObjectBuilder.getInstance().newMolecule();
        IAtom s = DefaultChemObjectBuilder.getInstance().newAtom("S");
        mol.addAtom(s);
        for (int i = 0; i < 6; i++) {
            IAtom f = DefaultChemObjectBuilder.getInstance().newAtom("F");
            mol.addAtom(f);
            IBond bond = DefaultChemObjectBuilder.getInstance().newBond(s, f, IBond.Order.SINGLE);
            mol.addBond(bond);
        }

        HybridizationMatcher matcher = new HybridizationMatcher();
        IAtomType matched;


        matched = matcher.findMatchingAtomType(mol, mol.getAtom(0));
        assertAtomType(testedAtomTypes, "S6", matched);

        for (int i = 1; i < mol.getAtomCount(); i++) {
            matched = matcher.findMatchingAtomType(mol, mol.getAtom(i));
            assertAtomType(testedAtomTypes, "atom " + i + " failed to match", "F", matched);
        }
    }

    /* Tests S4, O2 */
    @Test public void testS4oxide() throws CDKException {
        IMolecule mol = DefaultChemObjectBuilder.getInstance().newMolecule();
        IAtom s = DefaultChemObjectBuilder.getInstance().newAtom("S");
        IAtom o1 = DefaultChemObjectBuilder.getInstance().newAtom("O");
        IAtom o2 = DefaultChemObjectBuilder.getInstance().newAtom("O");
        IAtom o3 = DefaultChemObjectBuilder.getInstance().newAtom("O");

        IBond b1 = DefaultChemObjectBuilder.getInstance().newBond(s, o1, IBond.Order.DOUBLE);
        IBond b2 = DefaultChemObjectBuilder.getInstance().newBond(s, o2, IBond.Order.DOUBLE);
        IBond b3 = DefaultChemObjectBuilder.getInstance().newBond(s, o3, IBond.Order.DOUBLE);

        mol.addAtom(s);
        mol.addAtom(o1);
        mol.addAtom(o2);
        mol.addAtom(o3);

        mol.addBond(b1);
        mol.addBond(b2);
        mol.addBond(b3);

        HybridizationMatcher matcher = new HybridizationMatcher();
        IAtomType matched;


        matched = matcher.findMatchingAtomType(mol, mol.getAtom(0));
        assertAtomType(testedAtomTypes, "S4", matched);


        matched = matcher.findMatchingAtomType(mol, mol.getAtom(1));
        assertAtomType(testedAtomTypes, "O.sp2", matched);


        matched = matcher.findMatchingAtomType(mol, mol.getAtom(2));
        assertAtomType(testedAtomTypes, "O.sp2", matched);

        matched = matcher.findMatchingAtomType(mol, mol.getAtom(3));
        assertAtomType(testedAtomTypes, "O.sp2", matched);
    }

    /* Tests N3, O2 */
    @Test public void testN3acid() throws CDKException {
        IMolecule mol = DefaultChemObjectBuilder.getInstance().newMolecule();
        IAtom n = DefaultChemObjectBuilder.getInstance().newAtom("N");
        IAtom o = DefaultChemObjectBuilder.getInstance().newAtom("O");
        IAtom h = DefaultChemObjectBuilder.getInstance().newAtom("H");


        IBond b1 = DefaultChemObjectBuilder.getInstance().newBond(n, o, IBond.Order.DOUBLE);
        IBond b2 = DefaultChemObjectBuilder.getInstance().newBond(n, h, IBond.Order.SINGLE);


        mol.addAtom(n);
        mol.addAtom(o);
        mol.addAtom(h);

        mol.addBond(b1);
        mol.addBond(b2);

        HybridizationMatcher matcher = new HybridizationMatcher();
        IAtomType matched;

        matched = matcher.findMatchingAtomType(mol, mol.getAtom(0));
        assertAtomType(testedAtomTypes, "N.sp2", matched);


        matched = matcher.findMatchingAtomType(mol, mol.getAtom(1));
        assertAtomType(testedAtomTypes, "O.sp2", matched);


        matched = matcher.findMatchingAtomType(mol, mol.getAtom(2));
        assertAtomType(testedAtomTypes, "H", matched);
    }

    @Test public void testN3cyanide() throws CDKException {
        IMolecule mol = DefaultChemObjectBuilder.getInstance().newMolecule();
        IAtom n = DefaultChemObjectBuilder.getInstance().newAtom("N");
        IAtom c1 = DefaultChemObjectBuilder.getInstance().newAtom("C");
        IAtom c2 = DefaultChemObjectBuilder.getInstance().newAtom("C");


        c1.setHydrogenCount(0);
        c2.setHydrogenCount(3);

        IBond b1 = DefaultChemObjectBuilder.getInstance().newBond(n, c1, IBond.Order.TRIPLE);
        IBond b2 = DefaultChemObjectBuilder.getInstance().newBond(c1, c2, IBond.Order.SINGLE);


        mol.addAtom(n);
        mol.addAtom(c1);
        mol.addAtom(c2);

        mol.addBond(b1);
        mol.addBond(b2);

        HybridizationMatcher matcher = new HybridizationMatcher();
        IAtomType matched;

        matched = matcher.findMatchingAtomType(mol, mol.getAtom(0));
        assertAtomType(testedAtomTypes, "N.sp", matched);


        matched = matcher.findMatchingAtomType(mol, mol.getAtom(1));
        assertAtomType(testedAtomTypes, "C.sp", matched);


        matched = matcher.findMatchingAtomType(mol, mol.getAtom(2));
        assertAtomType(testedAtomTypes, "C.sp3", matched);
    }


    /* Tests N5, O2, C4 */
    @Test public void testN5() throws CDKException {
        IMolecule mol = DefaultChemObjectBuilder.getInstance().newMolecule();
        IAtom n = DefaultChemObjectBuilder.getInstance().newAtom("N");
        IAtom o1 = DefaultChemObjectBuilder.getInstance().newAtom("O");
        IAtom o2 = DefaultChemObjectBuilder.getInstance().newAtom("O");
        IAtom c = DefaultChemObjectBuilder.getInstance().newAtom("C");

        c.setHydrogenCount(3);

        IBond b1 = DefaultChemObjectBuilder.getInstance().newBond(n, o1, IBond.Order.DOUBLE);
        IBond b2 = DefaultChemObjectBuilder.getInstance().newBond(n, o2, IBond.Order.DOUBLE);
        IBond b3 = DefaultChemObjectBuilder.getInstance().newBond(n, c, IBond.Order.SINGLE);

        mol.addAtom(n);
        mol.addAtom(o1);
        mol.addAtom(o2);
        mol.addAtom(c);

        mol.addBond(b1);
        mol.addBond(b2);
        mol.addBond(b3);

        HybridizationMatcher matcher = new HybridizationMatcher();
        IAtomType matched;

        matched = matcher.findMatchingAtomType(mol, mol.getAtom(0));
        assertAtomType(testedAtomTypes, "N5", matched);


        matched = matcher.findMatchingAtomType(mol, mol.getAtom(1));
        assertAtomType(testedAtomTypes, "O.sp2", matched);

        matched = matcher.findMatchingAtomType(mol, mol.getAtom(2));
        assertAtomType(testedAtomTypes, "O.sp2", matched);

        matched = matcher.findMatchingAtomType(mol, mol.getAtom(3));
        assertAtomType(testedAtomTypes, "C.sp3", matched);
    }
    
    /**
     * The test seems to be run by JUnit in order in which they found
     * in the source. Ugly, but @AfterClass does not work because that
     * methods does cannot Assert.assert anything.
     */
    @Test public void countTestedAtomTypes() {
    	AtomTypeFactory factory = AtomTypeFactory.getInstance(
    		"org/openscience/cdk/config/data/hybridization_atomtypes.xml",
            NoNotificationChemObjectBuilder.getInstance()
        );
    	
   	    IAtomType[] expectedTypes = factory.getAllAtomTypes();
    	if (expectedTypes.length != testedAtomTypes.size()) {
       	    String errorMessage = "Atom types not tested:";
       	    for (int i=0; i<expectedTypes.length; i++) {
       	    	if (!testedAtomTypes.containsKey(expectedTypes[i].getAtomTypeName()))
       	    		errorMessage += " " + expectedTypes[i].getAtomTypeName();
       	    }
    		Assert.assertEquals(errorMessage,
    			factory.getAllAtomTypes().length, 
    			testedAtomTypes.size()
    		);
    	}
    }
    
    @Test public void testAdenine() throws Exception {
    	IMolecule mol = new NNMolecule();
    	IAtom a1 = mol.getBuilder().newAtom("C"); mol.addAtom(a1);
    	IAtom a2 = mol.getBuilder().newAtom("C"); mol.addAtom(a2);
    	IAtom a3 = mol.getBuilder().newAtom("C"); mol.addAtom(a3);
    	IAtom a4 = mol.getBuilder().newAtom("N"); mol.addAtom(a4);
    	IAtom a5 = mol.getBuilder().newAtom("N"); mol.addAtom(a5);
    	IAtom a6 = mol.getBuilder().newAtom("N"); mol.addAtom(a6);
    	a6.setHydrogenCount(1);
    	IAtom a7 = mol.getBuilder().newAtom("N"); mol.addAtom(a7);
    	IAtom a8 = mol.getBuilder().newAtom("N"); mol.addAtom(a8);
    	a8.setHydrogenCount(2);
    	IAtom a9 = mol.getBuilder().newAtom("C"); mol.addAtom(a9);
    	a9.setHydrogenCount(1);
    	IAtom a10 = mol.getBuilder().newAtom("C"); mol.addAtom(a10);
    	a10.setHydrogenCount(1);
    	IBond b1 = mol.getBuilder().newBond(a1, a2, IBond.Order.DOUBLE); mol.addBond(b1);
    	IBond b2 = mol.getBuilder().newBond(a1, a3, IBond.Order.SINGLE); mol.addBond(b2);
    	IBond b3 = mol.getBuilder().newBond(a1, a4, IBond.Order.SINGLE); mol.addBond(b3);
    	IBond b4 = mol.getBuilder().newBond(a2, a5, IBond.Order.SINGLE); mol.addBond(b4);
    	IBond b5 = mol.getBuilder().newBond(a2, a6, IBond.Order.SINGLE); mol.addBond(b5);
    	IBond b6 = mol.getBuilder().newBond(a3, a7, IBond.Order.DOUBLE); mol.addBond(b6);
    	IBond b7 = mol.getBuilder().newBond(a3, a8, IBond.Order.SINGLE); mol.addBond(b7);
    	IBond b8 = mol.getBuilder().newBond(a4, a9, IBond.Order.DOUBLE); mol.addBond(b8);
    	IBond b9 = mol.getBuilder().newBond(a5, a10, IBond.Order.DOUBLE); mol.addBond(b9);
    	IBond b10 = mol.getBuilder().newBond(a6, a9, IBond.Order.SINGLE); mol.addBond(b10);
    	IBond b11 = mol.getBuilder().newBond(a7, a10, IBond.Order.SINGLE); mol.addBond(b11);
    	HybridizationMatcher matcher = new HybridizationMatcher();
    	assertAtomType(testedAtomTypes, "C.sp2", matcher.findMatchingAtomType(mol, a1));
    	assertAtomType(testedAtomTypes, "C.sp2", matcher.findMatchingAtomType(mol, a2));
    	assertAtomType(testedAtomTypes, "C.sp2", matcher.findMatchingAtomType(mol, a3));
    	assertAtomType(testedAtomTypes, "N.sp2", matcher.findMatchingAtomType(mol, a4));
    	assertAtomType(testedAtomTypes, "N.sp2", matcher.findMatchingAtomType(mol, a5));
    	// the next should really be a N.sp2, but that's impossible to match
    	assertAtomType(testedAtomTypes, "N.sp3", matcher.findMatchingAtomType(mol, a6));
    	assertAtomType(testedAtomTypes, "N.sp2", matcher.findMatchingAtomType(mol, a7));
    	assertAtomType(testedAtomTypes, "N.sp3", matcher.findMatchingAtomType(mol, a8));
    	assertAtomType(testedAtomTypes, "C.sp2", matcher.findMatchingAtomType(mol, a9));
    	assertAtomType(testedAtomTypes, "C.sp2", matcher.findMatchingAtomType(mol, a10));
    }
}
