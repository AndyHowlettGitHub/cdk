/* $Revision: 5922 $ $Author: egonw $ $Date: 2006-04-12 11:20:15 +0200 (Wed, 12 Apr 2006) $
 *
 * Copyright (C) 1997-2007  The Chemistry Development Kit (CDK) project
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
package org.openscience.cdk.test;

import junit.framework.Test;
import junit.framework.TestSuite;

import org.openscience.cdk.test.tools.CDKHydrogenAdderTest;
import org.openscience.cdk.test.tools.CDKValencyCheckerTest;
import org.openscience.cdk.test.tools.DeduceBondOrderTestFromExplicitHydrogens;
import org.openscience.cdk.test.tools.DeduceBondOrderTestFromExplicitHydrogensAndCharges;
import org.openscience.cdk.test.tools.DeduceBondOrderTestFromHybridization;
import org.openscience.cdk.test.tools.SaturationCheckerTest;
import org.openscience.cdk.test.tools.ValencyCheckerTest;
import org.openscience.cdk.test.tools.ValencyHybridCheckerTest;

/**
 * TestSuite that runs all the tests for the valency module.
 *
 * @cdk.module  test-valencycheck
 * @cdk.depends log4j.jar
 * @cdk.depends junit.jar
 */
public class MvalencycheckTests {
    
    public static Test suite( ) {
        TestSuite suite= new TestSuite("All valencycheck Tests");

        suite.addTest(SaturationCheckerTest.suite());
        suite.addTest(ValencyCheckerTest.suite());
        suite.addTest(ValencyHybridCheckerTest.suite());
        suite.addTest(DeduceBondOrderTestFromHybridization.suite());
        suite.addTest(DeduceBondOrderTestFromExplicitHydrogens.suite());
        suite.addTest(DeduceBondOrderTestFromExplicitHydrogensAndCharges.suite());
        
        // the next generation valency tools that rely on CDKAtomTypeMatcher
        suite.addTest(CDKHydrogenAdderTest.suite());
        suite.addTest(CDKValencyCheckerTest.suite());
        
        return suite;
    }
    
}
