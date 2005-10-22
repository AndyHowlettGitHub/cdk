/* $RCSfile$
 * $Author$
 * $Date$
 * $Revision$
 * 
 * Copyright (C) 2004-2005  The Chemistry Development Kit (CDK) project
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
package org.openscience.cdk.test.qsar;

import junit.framework.Test;
import junit.framework.TestSuite;

import org.openscience.cdk.AtomContainer;
import org.openscience.cdk.exception.CDKException;
import org.openscience.cdk.qsar.Descriptor;
import org.openscience.cdk.qsar.ValenceCarbonConnectivityOrderOneDescriptor;
import org.openscience.cdk.qsar.result.DoubleResult;
import org.openscience.cdk.smiles.SmilesParser;
import org.openscience.cdk.test.CDKTestCase;

/**
 * TestSuite that runs all QSAR tests.
 *
 * @cdk.module test
 */

public class ValenceCarbonConnectivityOrderOneDescriptorTest extends CDKTestCase {
	
	public  ValenceCarbonConnectivityOrderOneDescriptorTest() {}
    
	public static Test suite() {
		return new TestSuite(ValenceCarbonConnectivityOrderOneDescriptorTest.class);
	}
    
	public void testValenceCarbonConnectivityOrderOneDescriptor() throws ClassNotFoundException, CDKException, java.lang.Exception {
		double [] testResult={1.4883912,1.0606601};
		Descriptor descriptor = new ValenceCarbonConnectivityOrderOneDescriptor();
		SmilesParser sp = new SmilesParser();
		AtomContainer mol = sp.parseSmiles("O=C(O)CC");
		DoubleResult retval = (DoubleResult)descriptor.calculate(mol).getValue();
		// chi1v_C
		assertEquals(testResult[1], retval.doubleValue(), 0.0001);
	}
}

