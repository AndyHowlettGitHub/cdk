/* $RCSfile$
 * $Author$    
 * $Date$    
 * $Revision$
 * 
 * Copyright (C) 2002-2004  The Chemistry Development Kit (CDK) project
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
 * 
 */
package org.openscience.cdk.test.applications;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import org.openscience.cdk.applications.APIVersionTester;

/**
 * Checks the functionality of the APIVersionTester.
 *
 * @cdk.module test
 */
public class APIVersionTesterTest extends TestCase {

    public APIVersionTesterTest(String name) {
        super(name);
    }

    public void setUp() {}

    public static Test suite() {
        return new TestSuite(APIVersionTesterTest.class);
    }

    public void testIsBiggerOrEqual() {
        assertTrue(APIVersionTester.isBiggerOrEqual("1.6", "1.6"));
        assertTrue(APIVersionTester.isBiggerOrEqual("1.6", "1.12"));
        assertFalse(APIVersionTester.isBiggerOrEqual("1.12", "1.7"));
    }

    public void testIsSmaller() {
        assertFalse(APIVersionTester.isSmaller("1.6", "1.6"));
        assertFalse(APIVersionTester.isSmaller("1.6", "1.12"));
        assertTrue(APIVersionTester.isSmaller("1.12", "1.7"));
    }

}
