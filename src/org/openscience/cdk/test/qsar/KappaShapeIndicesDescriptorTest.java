/* $RCSfile$
 * $Author$
 * $Date$
 * $Revision$
 * 
 * Copyright (C) 2004  The Chemistry Development Kit (CDK) project
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
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA. 
 */
package org.openscience.cdk.test.qsar;

import org.openscience.cdk.qsar.*;
import org.openscience.cdk.qsar.result.*;
import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;
import org.openscience.cdk.AtomContainer;
import org.openscience.cdk.tools.manipulator.AtomContainerManipulator;
import org.openscience.cdk.smiles.SmilesParser;
import org.openscience.cdk.exception.CDKException;
import java.util.ArrayList;
import java.io.*;

/**
 * TestSuite that runs all QSAR tests.
 *
 * @cdk.module test
 */

 public class KappaShapeIndicesDescriptorTest extends TestCase {
	
	public  KappaShapeIndicesDescriptorTest() {}
    
	public static Test suite() {
		return new TestSuite(KappaShapeIndicesDescriptorTest.class);
	}
    
	public void testKappaShapeIndicesDescriptor() throws ClassNotFoundException, CDKException, java.lang.Exception {
		double [] testResult={ 5,2.25, 4};
		Descriptor descriptor = new KappaShapeIndicesDescriptor();
		SmilesParser sp = new SmilesParser();
		AtomContainer mol = sp.parseSmiles("O=C(O)CC");
		AtomContainerManipulator acm = new AtomContainerManipulator();
		acm.removeHydrogens(mol);
		DoubleArrayResult retval = (DoubleArrayResult)descriptor.calculate(mol);
		// position 0 =  kier1
		// positions 1 = kier2
		// THIS IS OK: assertEquals(testResult[1], ((Double)retval.get(1)).doubleValue(), 0.0001);
		// THIS IS OK: assertEquals(testResult[0], ((Double)retval.get(0)).doubleValue(), 0.0001);
		assertEquals(testResult[2], retval.get(2), 0.0001);
	}
}

