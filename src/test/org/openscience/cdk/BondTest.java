/* $RCSfile$
 * $Author$
 * $Date$
 * $Revision$
 * 
 * Copyright (C) 2002-2007  The Chemistry Development Kit (CDK) project
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
package org.openscience.cdk;

import java.util.Iterator;

import javax.vecmath.Point2d;
import javax.vecmath.Point3d;

import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IBond;

/**
 * Checks the functionality of the Bond class.
 *
 * @cdk.module test-data
 * @see org.openscience.cdk.Bond
 */
public class BondTest extends ChemObjectTest {

    @BeforeClass
    public static void setUp() {
        setBuilder(DefaultChemObjectBuilder.getInstance());
    }

    @Test
    public void testBond() {
        IBond bond = getBuilder().newBond();
        Assert.assertEquals(2, bond.getAtomCount());
        Assert.assertNull(bond.getAtom(0));
        Assert.assertNull(bond.getAtom(1));
        Assert.assertNull(bond.getOrder());
        Assert.assertEquals(CDKConstants.STEREO_BOND_NONE, bond.getStereo());
    }

    @Test
    public void testBond_arrayIAtom() {
        IAtom atom1 = getBuilder().newAtom("C");
        IAtom atom2 = getBuilder().newAtom("O");
        IAtom atom3 = getBuilder().newAtom("C");
        IAtom atom4 = getBuilder().newAtom("C");
        IAtom atom5 = getBuilder().newAtom("C");

        IBond bond1 = getBuilder().newBond(new IAtom[]{atom1, atom2, atom3, atom4, atom5});
        Assert.assertEquals(5, bond1.getAtomCount());
        Assert.assertEquals(atom1, bond1.getAtom(0));
        Assert.assertEquals(atom2, bond1.getAtom(1));
    }

    @Test
    public void testBond_arrayIAtom_IBond_Order() {
        IAtom atom1 = getBuilder().newAtom("C");
        IAtom atom2 = getBuilder().newAtom("O");
        IAtom atom3 = getBuilder().newAtom("C");
        IAtom atom4 = getBuilder().newAtom("C");
        IAtom atom5 = getBuilder().newAtom("C");

        IBond bond1 = getBuilder().newBond(new IAtom[]{atom1, atom2, atom3, atom4, atom5}, IBond.Order.SINGLE);
        Assert.assertEquals(5, bond1.getAtomCount());
        Assert.assertEquals(atom1, bond1.getAtom(0));
        Assert.assertEquals(atom2, bond1.getAtom(1));
        Assert.assertEquals(IBond.Order.SINGLE, bond1.getOrder());
    }

    @Test
    public void testBond_IAtom_IAtom() {
        IAtom c = getBuilder().newAtom("C");
        IAtom o = getBuilder().newAtom("O");
        IBond bond = getBuilder().newBond(c, o);

        Assert.assertEquals(2, bond.getAtomCount());
        Assert.assertEquals(c, bond.getAtom(0));
        Assert.assertEquals(o, bond.getAtom(1));
        Assert.assertEquals(IBond.Order.SINGLE, bond.getOrder());
        Assert.assertEquals(CDKConstants.STEREO_BOND_NONE, bond.getStereo());
    }

    @Test
    public void testBond_IAtom_IAtom_IBond_Order() {
        IAtom c = getBuilder().newAtom("C");
        IAtom o = getBuilder().newAtom("O");
        IBond bond = getBuilder().newBond(c, o, IBond.Order.DOUBLE);

        Assert.assertEquals(2, bond.getAtomCount());
        Assert.assertEquals(c, bond.getAtom(0));
        Assert.assertEquals(o, bond.getAtom(1));
        Assert.assertTrue(bond.getOrder() == IBond.Order.DOUBLE);
        Assert.assertEquals(CDKConstants.STEREO_BOND_NONE, bond.getStereo());
    }

    @Test
    public void testBond_IAtom_IAtom_IBond_Order_int() {
        IAtom c = getBuilder().newAtom("C");
        IAtom o = getBuilder().newAtom("O");
        IBond bond = getBuilder().newBond(c, o, IBond.Order.SINGLE, CDKConstants.STEREO_BOND_UP);

        Assert.assertEquals(2, bond.getAtomCount());
        Assert.assertEquals(c, bond.getAtom(0));
        Assert.assertEquals(o, bond.getAtom(1));
        Assert.assertTrue(bond.getOrder() == IBond.Order.SINGLE);
        Assert.assertEquals(CDKConstants.STEREO_BOND_UP, bond.getStereo());
    }

    @Test
    public void testCompare_Object() {
        IAtom c = getBuilder().newAtom("C");
        IAtom o = getBuilder().newAtom("O");

        IBond b = getBuilder().newBond(c, o, IBond.Order.DOUBLE); // C=O bond
        IBond b2 = getBuilder().newBond(c, o, IBond.Order.DOUBLE); // same C=O bond

        Assert.assertTrue(b.compare(b2));
    }

    @Test
    public void testContains_IAtom() {
        IAtom c = getBuilder().newAtom("C");
        IAtom o = getBuilder().newAtom("O");

        IBond b = getBuilder().newBond(c, o, IBond.Order.DOUBLE); // C=O bond

        Assert.assertTrue(b.contains(c));
        Assert.assertTrue(b.contains(o));
    }

    @Test
    public void testGetAtomCount() {
        IAtom c = getBuilder().newAtom("C");
        IAtom o = getBuilder().newAtom("O");

        IBond b = getBuilder().newBond(c, o, IBond.Order.DOUBLE); // C=O bond

        Assert.assertEquals(2.0, b.getAtomCount(), 0.001);
    }

    @Test
    public void testSetAtoms_arrayIAtom() {
        IAtom[] atomsToAdd = new IAtom[2];
        atomsToAdd[0] = getBuilder().newAtom("C");
        atomsToAdd[1] = getBuilder().newAtom("O");

        IBond b = getBuilder().newBond();
        b.setAtoms(atomsToAdd);

        Assert.assertEquals(2, b.getAtomCount());
        Assert.assertEquals(atomsToAdd[0], b.getAtom(0));
        Assert.assertEquals(atomsToAdd[1], b.getAtom(1));
    }

    @Test
    public void testAtoms() {
        IAtom c = getBuilder().newAtom("C");
        IAtom o = getBuilder().newAtom("O");

        IBond b = getBuilder().newBond(c, o, IBond.Order.DOUBLE); // C=O bond

        java.util.Iterator atoms = b.atoms().iterator();
        Assert.assertEquals(2, b.getAtomCount());
        Assert.assertTrue(atoms.hasNext());
        Assert.assertEquals(c, atoms.next());
        Assert.assertTrue(atoms.hasNext());
        Assert.assertEquals(o, atoms.next());
        Assert.assertFalse(atoms.hasNext());
    }

    @Test
    public void testGetAtom_int() {
        IAtom c = getBuilder().newAtom("C");
        IAtom o = getBuilder().newAtom("O");

        IBond b = getBuilder().newBond(c, o, IBond.Order.DOUBLE); // C=O bond

        Assert.assertEquals(c, b.getAtom(0));
        Assert.assertEquals(o, b.getAtom(1));
    }

    @Test
    public void testSetAtom_IAtom_int() {
        IAtom c = getBuilder().newAtom("C");
        IAtom o = getBuilder().newAtom("O");

        IBond b = getBuilder().newBond();
        b.setAtom(c, 0);
        b.setAtom(o, 1);

        Assert.assertEquals(c, b.getAtom(0));
        Assert.assertEquals(o, b.getAtom(1));
    }

    @Test
    public void testGetConnectedAtom_IAtom() {
        IAtom c = getBuilder().newAtom("C");
        IAtom o = getBuilder().newAtom("O");

        IBond b = getBuilder().newBond(c, o, IBond.Order.DOUBLE); // C=O bond

        Assert.assertEquals(c, b.getConnectedAtom(o));
        Assert.assertEquals(o, b.getConnectedAtom(c));

        // test default return value
        Assert.assertNull(b.getConnectedAtom(getBuilder().newAtom()));
    }

    @Test
    public void testGetConnectedAtoms_IAtom() {
    	IAtom[] atoms = new IAtom[3];
        atoms[0] = getBuilder().newAtom("B");
        atoms[1] = getBuilder().newAtom("H");
        atoms[2] = getBuilder().newAtom("B");

        IBond b = getBuilder().newBond();
        b.setAtoms(atoms);
        b.setOrder(IBond.Order.SINGLE); // C=O bond

        IAtom[] connectedAtoms = b.getConnectedAtoms(atoms[1]);
        Assert.assertNotNull(connectedAtoms);
        Assert.assertEquals(2, connectedAtoms.length);
        Assert.assertNotNull(connectedAtoms[0]);
        Assert.assertNotNull(connectedAtoms[1]);

        // test default return value
        connectedAtoms = b.getConnectedAtoms(getBuilder().newAtom());
        Assert.assertNull(connectedAtoms);
    }

    @Test
    public void testIsConnectedTo_IBond() {
        IAtom c1 = getBuilder().newAtom("C");
        IAtom o = getBuilder().newAtom("O");
        IAtom c2 = getBuilder().newAtom("C");
        IAtom c3 = getBuilder().newAtom("C");

        IBond b1 = getBuilder().newBond(c1, o);
        IBond b2 = getBuilder().newBond(o, c2);
        IBond b3 = getBuilder().newBond(c2, c3);

        Assert.assertTrue(b1.isConnectedTo(b2));
        Assert.assertTrue(b2.isConnectedTo(b1));
        Assert.assertTrue(b2.isConnectedTo(b3));
        Assert.assertTrue(b3.isConnectedTo(b2));
        Assert.assertFalse(b1.isConnectedTo(b3));
        Assert.assertFalse(b3.isConnectedTo(b1));
    }

    @Test
    public void testGetOrder() {
        IBond b = getBuilder().newBond(getBuilder().newAtom("C"), getBuilder().newAtom("O"), IBond.Order.DOUBLE); // C=O bond

        Assert.assertEquals(IBond.Order.DOUBLE, b.getOrder());
    }

    @Test
    public void testSetOrder_IBond_Order() {
        IBond b = getBuilder().newBond(getBuilder().newAtom("C"), getBuilder().newAtom("O"), IBond.Order.DOUBLE); // C=O bond

        Assert.assertEquals(IBond.Order.DOUBLE, b.getOrder());

        b.setOrder(IBond.Order.SINGLE);
        Assert.assertEquals(IBond.Order.SINGLE, b.getOrder());
    }

    @Test
    public void testSetStereo_int() {
        IAtom c = getBuilder().newAtom("C");
        IAtom o = getBuilder().newAtom("O");

        IBond b = getBuilder().newBond(c, o, IBond.Order.DOUBLE, CDKConstants.STEREO_BOND_DOWN);

        b.setStereo(CDKConstants.STEREO_BOND_UP);
        Assert.assertEquals(CDKConstants.STEREO_BOND_UP, b.getStereo());
    }

    @Test
    public void testGetStereo() {
        IAtom c = getBuilder().newAtom("C");
        IAtom o = getBuilder().newAtom("O");

        IBond b = getBuilder().newBond(c, o, IBond.Order.DOUBLE, CDKConstants.STEREO_BOND_UP);
        Assert.assertEquals(CDKConstants.STEREO_BOND_UP, b.getStereo());
    }

    @Test
    public void testGet2DCenter() {
        IAtom o = getBuilder().newAtom("O", new Point2d(0.0, 0.0));
        IAtom c = getBuilder().newAtom("C", new Point2d(1.0, 1.0));
        IBond b = getBuilder().newBond(c, o);

        Assert.assertEquals(0.5, b.get2DCenter().x, 0.001);
        Assert.assertEquals(0.5, b.get2DCenter().y, 0.001);
    }

    @Test
    public void testGet3DCenter() {
        IAtom o = getBuilder().newAtom("O", new Point3d(0.0, 0.0, 0.0));
        IAtom c = getBuilder().newAtom("C", new Point3d(1.0, 1.0, 1.0));
        IBond b = getBuilder().newBond(c, o);

        Assert.assertEquals(0.5, b.get3DCenter().x, 0.001);
        Assert.assertEquals(0.5, b.get3DCenter().y, 0.001);
        Assert.assertEquals(0.5, b.get3DCenter().z, 0.001);
    }

    @Test
    public void testClone() throws Exception {
        IBond bond = getBuilder().newBond();
        Object clone = bond.clone();
        Assert.assertNotNull(clone);
        Assert.assertTrue(clone instanceof org.openscience.cdk.interfaces.IBond);
    }

    @Test
    public void testClone_IAtom() throws Exception {
        IAtom atom1 = getBuilder().newAtom("C");
        IAtom atom2 = getBuilder().newAtom("O");
        IBond bond = getBuilder().newBond(atom1, atom2);
        IBond clone = (IBond) bond.clone();

        // test cloning of atoms
        Assert.assertNotSame(atom1, clone.getAtom(0));
        Assert.assertNotSame(atom2, clone.getAtom(1));
    }

    @Test
    public void testClone_Order() throws Exception {
        IAtom atom1 = getBuilder().newAtom("C");
        IAtom atom2 = getBuilder().newAtom("O");
        IBond bond = getBuilder().newBond(atom1, atom2, IBond.Order.SINGLE);
        IBond clone = (IBond) bond.clone();

        // test cloning of bond order
        bond.setOrder(IBond.Order.DOUBLE);
        Assert.assertEquals(IBond.Order.SINGLE, clone.getOrder());
    }

    @Test
    public void testClone_Stereo() throws Exception {
        IAtom atom1 = getBuilder().newAtom("C");
        IAtom atom2 = getBuilder().newAtom("O");
        IBond bond = getBuilder().newBond(atom1, atom2, IBond.Order.SINGLE, 1);
        IBond clone = (IBond) bond.clone();

        // test cloning of bond order
        bond.setStereo(2);
        Assert.assertEquals(1, clone.getStereo());
    }

    /**
     * Test for RFC #9
     */
    @Test
    public void testToString() {
        IBond bond = getBuilder().newBond();
        String description = bond.toString();
        for (int i = 0; i < description.length(); i++) {
            Assert.assertTrue(description.charAt(i) != '\n');
            Assert.assertTrue(description.charAt(i) != '\r');
        }
    }

    @Test
    public void testMultiCenter1() {
        IAtom atom1 = getBuilder().newAtom("C");
        IAtom atom2 = getBuilder().newAtom("O");
        IAtom atom3 = getBuilder().newAtom("C");

        IBond bond = getBuilder().newBond(new IAtom[]{atom1, atom2, atom3});
        Assert.assertEquals(3, bond.getAtomCount());
        Assert.assertEquals(atom1, bond.getAtom(0));
        Assert.assertEquals(atom2, bond.getAtom(1));
        Assert.assertEquals(atom3, bond.getAtom(2));

        Assert.assertEquals(bond.getOrder(), CDKConstants.UNSET);
    }

    @Test
    public void testMultiCenterCompare() {
        IAtom atom1 = getBuilder().newAtom("C");
        IAtom atom2 = getBuilder().newAtom("O");
        IAtom atom3 = getBuilder().newAtom("C");

        IBond bond1 = getBuilder().newBond(new IAtom[]{atom1, atom2, atom3});
        IBond bond2 = getBuilder().newBond(new IAtom[]{atom1, atom2, atom3});

        Assert.assertTrue(bond1.compare(bond2));

        IAtom atom4 = getBuilder().newAtom("C");
        IBond bond3 = getBuilder().newBond(new IAtom[]{atom1, atom2, atom4});
        Assert.assertFalse(bond1.compare(bond3));
    }

    @Test
    public void testMltiCenterContains() {
        IAtom atom1 = getBuilder().newAtom("C");
        IAtom atom2 = getBuilder().newAtom("O");
        IAtom atom3 = getBuilder().newAtom("C");
        IAtom atom4 = getBuilder().newAtom("C");

        IBond bond1 = getBuilder().newBond(new IAtom[]{atom1, atom2, atom3});
        Assert.assertTrue(bond1.contains(atom1));
        Assert.assertTrue(bond1.contains(atom2));
        Assert.assertTrue(bond1.contains(atom3));
        Assert.assertFalse(bond1.contains(atom4));
    }

    @Test
    public void testMultiCenterIterator() {
        IAtom atom1 = getBuilder().newAtom("C");
        IAtom atom2 = getBuilder().newAtom("O");
        IAtom atom3 = getBuilder().newAtom("C");
        IAtom atom4 = getBuilder().newAtom("C");

        IBond bond1 = getBuilder().newBond(new IAtom[]{atom1, atom2, atom3, atom4});
        Iterator<IAtom> atoms = bond1.atoms().iterator();
        int natom = 0;
        while (atoms.hasNext()) {
            IAtom atom = atoms.next();
            natom++;
        }
        Assert.assertEquals(4, natom);
    }

    @Test
    public void testMultiCenterConnectedAtoms() {
        IAtom atom1 = getBuilder().newAtom("C");
        IAtom atom2 = getBuilder().newAtom("O");
        IAtom atom3 = getBuilder().newAtom("C");
        IAtom atom4 = getBuilder().newAtom("C");

        IBond bond1 = getBuilder().newBond(new IAtom[]{atom1, atom2, atom3, atom4});
        Assert.assertEquals(atom2, bond1.getConnectedAtom(atom1));
        Assert.assertNull(bond1.getConnectedAtom(getBuilder().newAtom()));

        IAtom[] conAtoms = bond1.getConnectedAtoms(atom1);
        boolean correct = true;
        for (IAtom atom : conAtoms) {
            if (atom == atom1) {
                correct = false;
                break;
            }
        }
        Assert.assertTrue(correct);


        conAtoms = bond1.getConnectedAtoms(atom3);
        correct = true;
        for (IAtom atom : conAtoms) {
            if (atom == atom3) {
                correct = false;
                break;
            }
        }
        Assert.assertTrue(correct);
    }

    @Test
    public void testMultiCenterIsConnectedTo() {
        IAtom atom1 = getBuilder().newAtom("C");
        IAtom atom2 = getBuilder().newAtom("O");
        IAtom atom3 = getBuilder().newAtom("C");
        IAtom atom4 = getBuilder().newAtom("C");
        IAtom atom5 = getBuilder().newAtom("C");

        IBond bond1 = getBuilder().newBond(new IAtom[]{atom1, atom2, atom3});
        IBond bond2 = getBuilder().newBond(new IAtom[]{atom2, atom3, atom4});
        IBond bond3 = getBuilder().newBond(new IAtom[]{atom2, atom4});
        IBond bond4 = getBuilder().newBond(new IAtom[]{atom5, atom4});

        Assert.assertTrue(bond1.isConnectedTo(bond2));
        Assert.assertTrue(bond2.isConnectedTo(bond1));
        Assert.assertTrue(bond1.isConnectedTo(bond3));
        Assert.assertFalse(bond4.isConnectedTo(bond1));
    }
}
