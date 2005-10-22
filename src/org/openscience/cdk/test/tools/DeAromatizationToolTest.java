/*  $RCSfile$
 *  $Author$
 *  $Date$
 *  $Revision$
 *
 *  Copyright (C) 1997-2005  The Chemistry Development Kit (CKD) project
 *
 *  Contact: cdk-devel@lists.sourceforge.net
 *
 *  This program is free software; you can redistribute it and/or
 *  modify it under the terms of the GNU Lesser General Public License
 *  as published by the Free Software Foundation; either version 2.1
 *  of the License, or (at your option) any later version.
 *  All I ask is that proper credit is given for my work, which includes
 *  - but is not limited to - adding the above copyright notice to the beginning
 *  of your source code files, and to any copyright notice that you may distribute
 *  with programs based on this work.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU Lesser General Public License for more details.
 *
 *  You should have received a copy of the GNU Lesser General Public License
 *  along with this program; if not, write to the Free Software
 *  Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110-1301 USA.
 *
 */
package org.openscience.cdk.test.tools;

import junit.framework.Test;
import junit.framework.TestSuite;

import org.openscience.cdk.Ring;
import org.openscience.cdk.test.CDKTestCase;
import org.openscience.cdk.tools.DeAromatizationTool;

/**
 * Tests the DeAromatizationTool.
 *
 * @cdk.module test
 */
public class DeAromatizationToolTest extends CDKTestCase {
	
	public DeAromatizationToolTest(String name) {
		super(name);
	}

	public static Test suite() {
		return new TestSuite(DeAromatizationToolTest.class);
	}

	public void testBezene() {
		Ring benzene = new Ring(6, "C");
		boolean success = DeAromatizationTool.deAromatize(benzene);
		assertTrue(success);
	}
	
	public void testPyridine() {
		Ring pyridine = new Ring(6, "C");
		pyridine.getAtomAt(0).setSymbol("N");
		boolean success = DeAromatizationTool.deAromatize(pyridine);
		assertTrue(success);
	}
	
	public void test() {
		Ring benzene = new Ring(4, "C");
		boolean success = DeAromatizationTool.deAromatize(benzene);
		assertFalse(success);
	}
	
}

