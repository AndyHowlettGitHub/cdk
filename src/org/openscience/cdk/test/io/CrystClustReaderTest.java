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
import java.io.StringReader;
import javax.vecmath.Vector3d;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import org.openscience.cdk.Atom;
import org.openscience.cdk.ChemFile;
import org.openscience.cdk.ChemModel;
import org.openscience.cdk.ChemObject;
import org.openscience.cdk.ChemSequence;
import org.openscience.cdk.Crystal;
import org.openscience.cdk.Molecule;
import org.openscience.cdk.SetOfMolecules;
import org.openscience.cdk.io.CrystClustReader;
import org.openscience.cdk.tools.LoggingTool;

/**
 * TestCase for the reading MDL mol files using one test file.
 *
 * @cdk.module test
 *
 * @see org.openscience.cdk.io.CrystClustReader
 */
public class CrystClustReaderTest extends TestCase {

    private org.openscience.cdk.tools.LoggingTool logger;

    public CrystClustReaderTest(String name) {
        super(name);
        logger = new LoggingTool(this);
    }

    public static Test suite() {
        return new TestSuite(CrystClustReaderTest.class);
    }

    public void testEstrone() {
        String filename = "data/estron.crystclust";
        logger.info("Testing: " + filename);
        InputStream ins = this.getClass().getClassLoader().getResourceAsStream(filename);
        try {
            CrystClustReader reader = new CrystClustReader(new InputStreamReader(ins));
            ChemFile chemFile = (ChemFile)reader.read((ChemObject)new ChemFile());
            
            assertNotNull(chemFile);
            assertEquals(1, chemFile.getChemSequenceCount());
            ChemSequence seq = chemFile.getChemSequence(0);
            assertNotNull(seq);
            assertEquals(2, seq.getChemModelCount());
            ChemModel model = seq.getChemModel(0);
            assertNotNull(model);
            
            Crystal crystal = model.getCrystal();
            assertNotNull(crystal);
            assertEquals(42, crystal.getAtomCount());
            
            // test reading of partial charges
            Atom atom = crystal.getAtomAt(0);
            assertNotNull(atom);
            assertEquals("O", atom.getSymbol());
            assertEquals(-0.68264902, atom.getCharge(), 0.00000001);
            
            // test unit cell axes
            Vector3d a = crystal.getA();
            assertEquals(7.971030, a.x, 0.000001);
            assertEquals(0.0, a.y, 0.000001);
            assertEquals(0.0, a.z, 0.000001);
            Vector3d b = crystal.getB();
            assertEquals(0.0, b.x, 0.000001);
            assertEquals(18.772200, b.y, 0.000001);
            assertEquals(0.0, b.z, 0.000001);
            Vector3d c = crystal.getC();
            assertEquals(0.0, c.x, 0.000001);
            assertEquals(0.0, c.y, 0.000001);
            assertEquals(10.262220, c.z, 0.000001);
        } catch (Exception exception) {
            System.out.println("Error while reading file: " + exception.getMessage());
            exception.printStackTrace();
            fail(exception.toString());
        }
    }
}
