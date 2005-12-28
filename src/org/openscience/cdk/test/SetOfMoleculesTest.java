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
 * 
 */
package org.openscience.cdk.test;

import junit.framework.Test;
import junit.framework.TestSuite;

import org.openscience.cdk.interfaces.ChemObjectBuilder;
import org.openscience.cdk.interfaces.ChemObjectListener;
import org.openscience.cdk.DefaultChemObjectBuilder;
import org.openscience.cdk.interfaces.Molecule;
import org.openscience.cdk.interfaces.SetOfMolecules;
import org.openscience.cdk.interfaces.ChemObjectChangeEvent;

/**
 * Checks the funcitonality of the SetOfMolecules class.
 *
 * @cdk.module test
 *
 * @see org.openscience.cdk.SetOfMolecules
 */
public class SetOfMoleculesTest extends CDKTestCase {

	protected ChemObjectBuilder builder;
	
    public SetOfMoleculesTest(String name) {
        super(name);
    }

    public void setUp() {
       	builder = DefaultChemObjectBuilder.getInstance();
    }

    public static Test suite() {
        return new TestSuite(SetOfMoleculesTest.class);
    }
    
    public void testGetMoleculeCount() {
        SetOfMolecules som = builder.newSetOfMolecules();
        som.addMolecule(builder.newMolecule());
        som.addMolecule(builder.newMolecule());
        som.addMolecule(builder.newMolecule());
        
        assertEquals(3, som.getMoleculeCount());
    }
    
    public void testGetMolecule_int() {
        SetOfMolecules som = builder.newSetOfMolecules();
        som.addMolecule(builder.newMolecule());
        som.addMolecule(builder.newMolecule());
        som.addMolecule(builder.newMolecule());

        assertNotNull(som.getMolecule(2)); // third molecule should exist
        assertNull(som.getMolecule(3)); // fourth molecule must not exist
    }
    
    public void testAddMolecule_Molecule() {
        SetOfMolecules som = builder.newSetOfMolecules();
        som.addMolecule(builder.newMolecule());
        som.addMolecule(builder.newMolecule());
        som.addMolecule(builder.newMolecule());
        som.addMolecule(builder.newMolecule());
        som.addMolecule(builder.newMolecule());

        assertEquals(5, som.getMoleculeCount());
        
        // now test it to make sure it properly grows the array
        som.addMolecule(builder.newMolecule());
        som.addMolecule(builder.newMolecule());

        assertEquals(7, som.getMoleculeCount());        
    }
    
    public void testAdd_SetOfMolecules() {
        SetOfMolecules som = builder.newSetOfMolecules();
        som.addMolecule(builder.newMolecule());
        som.addMolecule(builder.newMolecule());
        som.addMolecule(builder.newMolecule());
        som.addMolecule(builder.newMolecule());
        som.addMolecule(builder.newMolecule());

        SetOfMolecules som2 = builder.newSetOfMolecules();
        som2.add(som);
        
        assertEquals(5, som2.getMoleculeCount());
    }
    
    public void testSetMolecules_arrayMolecule() {
        SetOfMolecules som = builder.newSetOfMolecules();
        
        Molecule[] set = new Molecule[5];
        set[0] = builder.newMolecule();
        set[1] = builder.newMolecule();
        set[2] = builder.newMolecule();
        set[3] = builder.newMolecule();
        set[4] = builder.newMolecule();
        
        assertEquals(0, som.getMoleculeCount());
        som.setMolecules(set);
        assertEquals(5, som.getMoleculeCount());
    }
    
    public void testGrowMoleculeArray() {
        // this test assumes that the growSize = 5 !
        // if not, there is need for the array to grow
        SetOfMolecules som = builder.newSetOfMolecules();
        
        som.addMolecule(builder.newMolecule());
        som.addMolecule(builder.newMolecule());
        som.addMolecule(builder.newMolecule());
        som.addMolecule(builder.newMolecule());
        som.addMolecule(builder.newMolecule());
        som.addMolecule(builder.newMolecule());
        som.addMolecule(builder.newMolecule());

        org.openscience.cdk.interfaces.Molecule[] mols = som.getMolecules();
        assertEquals(7, mols.length);
    }
    
    public void testSetOfMolecules() {
        SetOfMolecules som = builder.newSetOfMolecules();
        assertNotNull(som);
        assertEquals(0, som.getMoleculeCount());
    }
    
    public void testGetMolecules() {
        SetOfMolecules som = builder.newSetOfMolecules();
        
        org.openscience.cdk.interfaces.Molecule[] mols = som.getMolecules();
        assertEquals(0, mols.length);
        
        som.addMolecule(builder.newMolecule());
        som.addMolecule(builder.newMolecule());
        som.addMolecule(builder.newMolecule());

        mols = som.getMolecules();
        assertEquals(3, mols.length);
        assertNotNull(mols[0]);
        assertNotNull(mols[1]);
        assertNotNull(mols[2]);
    }

    public void testToString() {
        SetOfMolecules som = builder.newSetOfMolecules();
        String description = som.toString();
        for (int i=0; i< description.length(); i++) {
            assertTrue(description.charAt(i) != '\n');
            assertTrue(description.charAt(i) != '\r');
        }
    }

    public void testClone() {
        SetOfMolecules som = builder.newSetOfMolecules();
        Object clone = som.clone();
        assertTrue(clone instanceof SetOfMolecules);
	assertNotSame(som, clone);
    }   
    
    public void testStateChanged_ChemObjectChangeEvent() {
        ChemObjectListenerImpl listener = new ChemObjectListenerImpl();
        SetOfMolecules chemObject = builder.newSetOfMolecules();
        chemObject.addListener(listener);
        
        chemObject.addMolecule(builder.newMolecule());
        assertTrue(listener.changed);
    }

    private class ChemObjectListenerImpl implements ChemObjectListener {
        private boolean changed;
        
        private ChemObjectListenerImpl() {
            changed = false;
        }
        
        public void stateChanged(ChemObjectChangeEvent e) {
            changed = true;
        }
        
        public void reset() {
            changed = false;
        }
    }

}
