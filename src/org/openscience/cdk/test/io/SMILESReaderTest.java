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

import org.openscience.cdk.Molecule;
import org.openscience.cdk.SetOfMolecules;
import org.openscience.cdk.io.SMILESReader;
import org.openscience.cdk.test.CDKTestCase;
/**
 * TestCase for the reading MDL mol files using one test file.
 *
 * @cdk.module test
 *
 * @see org.openscience.cdk.io.MDLReader
 */
public class SMILESReaderTest extends CDKTestCase {

    private org.openscience.cdk.tools.LoggingTool logger;

    public SMILESReaderTest(String name) {
        super(name);
        logger = new org.openscience.cdk.tools.LoggingTool(this);
    }

    public static Test suite() {
        return new TestSuite(SMILESReaderTest.class);
    }

    public void testReading() {
        String filename = "data/smiles/smiles.smi";
        logger.info("Testing: " + filename);
        InputStream ins = this.getClass().getClassLoader().getResourceAsStream(filename);
        try {
            SMILESReader reader = new SMILESReader(new InputStreamReader(ins));
            SetOfMolecules som = (SetOfMolecules)reader.read(new SetOfMolecules());
            assertEquals(8, som.getMoleculeCount());
        } catch (Exception e) {
            fail(e.toString());
        }
    }
    
    
    public void testReadingSmiFile_1() {
        String filename = "data/smiles/smiles.smi";
        logger.info("Testing: " + filename);
        InputStream ins = this.getClass().getClassLoader().getResourceAsStream(filename);
        try {
            SMILESReader reader = new SMILESReader(new InputStreamReader(ins));
            SetOfMolecules som = (SetOfMolecules)reader.read(new SetOfMolecules());
            String name = null;
            org.openscience.cdk.interfaces.Molecule thisMol = null;
	    
	    thisMol = som.getMolecule(0);
	    name = ( (String)thisMol.getProperty("SMIdbNAME") ).toString();
	    assertEquals("benzene", name);
        } catch (Exception e) {
            fail(e.toString());
        }
    }
    
    public void testReadingSmiFile_2() {
        String filename = "data/smiles/smiles.smi";
        logger.info("Testing: " + filename);
        InputStream ins = this.getClass().getClassLoader().getResourceAsStream(filename);
        try {
            SMILESReader reader = new SMILESReader(new InputStreamReader(ins));
            SetOfMolecules som = (SetOfMolecules)reader.read(new SetOfMolecules());
            org.openscience.cdk.interfaces.Molecule thisMol = som.getMolecule(1);
            assertNull(thisMol.getProperty("SMIdbNAME"));
        } catch (Exception e) {
            e.printStackTrace();
            fail("Error in test: " + e.getMessage());
        }
    }
}
