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
 */
package org.openscience.cdk.test.io;

import java.io.StringWriter;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import org.openscience.cdk.Atom;
import org.openscience.cdk.Molecule;
import org.openscience.cdk.PseudoAtom;
import org.openscience.cdk.io.MDLWriter;
import org.openscience.cdk.tools.LoggingTool;
import org.openscience.cdk.test.CDKTestCase;

/**
 * TestCase for the writer MDL mol files using one test file.
 *
 * @cdk.module test
 *
 * @see org.openscience.cdk.io.MDLWriter
 */
public class MDLWriterTest extends CDKTestCase {

    private org.openscience.cdk.tools.LoggingTool logger;

    public MDLWriterTest(String name) {
        super(name);
        logger = new LoggingTool(this);
    }

    public static Test suite() {
        return new TestSuite(MDLWriterTest.class);
    }

    public void testBug890456() {
        StringWriter writer = new StringWriter();
        Molecule molecule = new Molecule();
        molecule.addAtom(new PseudoAtom("*"));
        molecule.addAtom(new Atom("C"));
        molecule.addAtom(new Atom("C"));
        
        try {
            MDLWriter mdlWriter = new MDLWriter(writer);
            mdlWriter.write(molecule);
        } catch (Exception exception) {
            logger.error("Error while creating an MDL file");
            logger.debug(exception);
            fail(exception.getMessage());
        }
        
        assertTrue(writer.toString().indexOf("M  END") != -1);
    }

    public void testBug1212219() {
        StringWriter writer = new StringWriter();
        Molecule molecule = new Molecule();
        Atom atom = new Atom("C");
        atom.setMassNumber(14);
        molecule.addAtom(atom);
        
        try {
            MDLWriter mdlWriter = new MDLWriter(writer);
            mdlWriter.write(molecule);
        } catch (Exception exception) {
            logger.error("Error while creating an MDL file");
            logger.debug(exception);
            fail(exception.getMessage());
        }
        
        String output = writer.toString();
        System.out.println("MDL output for testBug1212219: " + output);
        assertTrue(output.indexOf("M  ISO  1   7   14") != -1);
    }
}
