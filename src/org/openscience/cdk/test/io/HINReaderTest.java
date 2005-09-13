/* $RCSfile$
 * $Author$
 * $Date$
 * $Revision$
 *
 * Copyright (C) 2004-2005  The Chemistry Development Kit (CDK) project
 * 
 * Contact: cdk-devel@slists.sourceforge.net
 * 
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public License
 * as published by the Free Software Foundation; either version 2.1
 * of the License, or (at your option) any later version.
 * All we ask is that proper credit is given for our work, which includes
 * - but is not limited to - adding the above copyright notice to the beginning
 * of your source code files, and to any copyright notice that you may distribute
 * with programs based on this work.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 *  */
package org.openscience.cdk.test.io;

import java.io.InputStream;
import java.io.InputStreamReader;

import junit.framework.Test;
import junit.framework.TestSuite;

import org.openscience.cdk.ChemFile;
import org.openscience.cdk.ChemModel;
import org.openscience.cdk.ChemObject;
import org.openscience.cdk.ChemSequence;
import org.openscience.cdk.Molecule;
import org.openscience.cdk.SetOfMolecules;
import org.openscience.cdk.io.HINReader;
import org.openscience.cdk.test.CDKTestCase;

/**
 * TestCase for the reading HIN mol files using one test file.
 *
 * @cdk.module test
 *
 * @see org.openscience.cdk.io.HINReader
 */
public class HINReaderTest extends CDKTestCase {

    private org.openscience.cdk.tools.LoggingTool logger;

    public HINReaderTest(String name) {
        super(name);
        logger = new org.openscience.cdk.tools.LoggingTool(this);
    }

    public static Test suite() {
        return new TestSuite(HINReaderTest.class);
    }

    public void testBenzene() {
        String filename = "data/hin/benzene.hin";
        logger.info("Testing: " + filename);
        InputStream ins = this.getClass().getClassLoader().getResourceAsStream(filename);
        try {
            HINReader reader = new HINReader(new InputStreamReader(ins));
            ChemFile chemFile = (ChemFile)reader.read((ChemObject)new ChemFile());
            
            assertNotNull(chemFile);
            assertEquals(1, chemFile.getChemSequenceCount());
            ChemSequence seq = chemFile.getChemSequence(0);
            assertNotNull(seq);
            assertEquals(1, seq.getChemModelCount());
            ChemModel model = seq.getChemModel(0);
            assertNotNull(model);
            
            org.openscience.cdk.interfaces.SetOfMolecules som = model.getSetOfMolecules();
            assertNotNull(som);
            assertEquals(1, som.getMoleculeCount());
            org.openscience.cdk.interfaces.Molecule m = som.getMolecule(0);
            assertNotNull(m);
            assertEquals(12, m.getAtomCount());
            // assertEquals(?, m.getBondCount());
            
        } catch (Exception e) {
            fail(e.toString());
        }
    }

    public void testMoleculeTwo() {
        String filename = "data/hin/molecule2.hin";
        logger.info("Testing: " + filename);
        InputStream ins = this.getClass().getClassLoader().getResourceAsStream(filename);
        try {
            HINReader reader = new HINReader(new InputStreamReader(ins));
            ChemFile chemFile = (ChemFile)reader.read((ChemObject)new ChemFile());
            
            assertNotNull(chemFile);
            assertEquals(1, chemFile.getChemSequenceCount());
            ChemSequence seq = chemFile.getChemSequence(0);
            assertNotNull(seq);
            assertEquals(1, seq.getChemModelCount());
            ChemModel model = seq.getChemModel(0);
            assertNotNull(model);
            
            org.openscience.cdk.interfaces.SetOfMolecules som = model.getSetOfMolecules();
            assertNotNull(som);
            assertEquals(1, som.getMoleculeCount());
            org.openscience.cdk.interfaces.Molecule m = som.getMolecule(0);
            assertNotNull(m);
            assertEquals(37, m.getAtomCount());
            // assertEquals(?, m.getBondCount());
            
        } catch (Exception e) {
            fail(e.toString());
        }
    }

    public void testMultiple() {
        String filename = "data/hin/multiple.hin";
        logger.info("Testing: " + filename);
        InputStream ins = this.getClass().getClassLoader().getResourceAsStream(filename);
        try {
            HINReader reader = new HINReader(new InputStreamReader(ins));
            ChemFile chemFile = (ChemFile)reader.read((ChemObject)new ChemFile());
            
            assertNotNull(chemFile);
            assertEquals(1, chemFile.getChemSequenceCount());
            ChemSequence seq = chemFile.getChemSequence(0);
            assertNotNull(seq);
            assertEquals(1, seq.getChemModelCount());
            ChemModel model = seq.getChemModel(0);
            assertNotNull(model);
            
            org.openscience.cdk.interfaces.SetOfMolecules som = model.getSetOfMolecules();
            assertNotNull(som);
            assertEquals(3, som.getMoleculeCount());
        } catch (Exception e) {
            fail(e.toString());
        }
    }

}
