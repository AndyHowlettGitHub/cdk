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
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA. 
 */
package org.openscience.cdk.test.qsar;

import junit.framework.Test;
import junit.framework.TestSuite;

import org.openscience.cdk.Molecule;
import org.openscience.cdk.exception.CDKException;
import org.openscience.cdk.qsar.Descriptor;
import org.openscience.cdk.qsar.PiContactDetectionDescriptor;
import org.openscience.cdk.qsar.result.BooleanResult;
import org.openscience.cdk.smiles.SmilesParser;
import org.openscience.cdk.test.CDKTestCase;
import org.openscience.cdk.tools.HydrogenAdder;


/**
 * TestSuite that runs all QSAR tests.
 *
 * @cdk.module test
 */
 
public class PiContactDetectionDescriptorTest extends CDKTestCase {
	
	public  PiContactDetectionDescriptorTest() {}
    
	public static Test suite() {
		return new TestSuite(PiContactDetectionDescriptorTest.class);
	}
	
	public void testPiContactDetectionDescriptorOut() throws ClassNotFoundException, CDKException, java.lang.Exception {
		Descriptor descriptor = new PiContactDetectionDescriptor();
		Object[] params = {new Integer(1), new Integer(6), new Boolean(true)};
		descriptor.setParameters(params);
		SmilesParser sp = new SmilesParser();
		Molecule mol = sp.parseSmiles("CNC=CC=CN"); 
		HydrogenAdder hAdder = new HydrogenAdder();
		hAdder.addExplicitHydrogensToSatisfyValency(mol);
		assertTrue(((BooleanResult)descriptor.calculate(mol).getValue()).booleanValue());
	}
	
	public void testPiContactDetectionDescriptorInside() throws ClassNotFoundException, CDKException, java.lang.Exception {
		Descriptor descriptor = new PiContactDetectionDescriptor();
		Object[] params = {new Integer(2), new Integer(4), new Boolean(true)};
		descriptor.setParameters(params);
		SmilesParser sp = new SmilesParser();
		Molecule mol = sp.parseSmiles("CNC=CC=CN"); 
		HydrogenAdder hAdder = new HydrogenAdder();
		hAdder.addExplicitHydrogensToSatisfyValency(mol);
		assertTrue(((BooleanResult)descriptor.calculate(mol).getValue()).booleanValue());
	}
	
	public void testPiContactDetectionDescriptorFalse() throws ClassNotFoundException, CDKException, java.lang.Exception {
		Descriptor descriptor = new PiContactDetectionDescriptor();
		Object[] params = {new Integer(3), new Integer(9), new Boolean(true)};
		descriptor.setParameters(params);
		SmilesParser sp = new SmilesParser();
		Molecule mol = sp.parseSmiles("CNC=CC=CNCCCCCC"); 
		HydrogenAdder hAdder = new HydrogenAdder();
		hAdder.addExplicitHydrogensToSatisfyValency(mol);
		assertFalse(((BooleanResult)descriptor.calculate(mol).getValue()).booleanValue());
	}
}

