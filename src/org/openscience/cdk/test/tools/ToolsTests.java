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
package org.openscience.cdk.test.tools;

import junit.framework.Test;
import junit.framework.TestSuite;

import org.openscience.cdk.test.tools.manipulator.ManipulatorsTests;

/**
 * TestSuite that runs all the sample tests in the
 * org.openscience.cdk.tools package.
 *
 * @cdk.module test
 */
public class ToolsTests {

    public static Test suite () {
        TestSuite suite= new TestSuite("The cdk.tools Tests");
        suite.addTest(BremserPredictorTest.suite());
        suite.addTest(DeAromatizationToolTest.suite());
        suite.addTest(HOSECodeTest.suite());
        suite.addTest(HydrogenAdderTest.suite());
        suite.addTest(HydrogenAdder2Test.suite());
        suite.addTest(HydrogenAdder3Test.suite());
        suite.addTest(IDCreatorTest.suite());
        suite.addTest(LoggingToolTest.suite());
        suite.addTest(MFAnalyserTest.suite());
        suite.addTest(SaturationCheckerTest.suite());
        suite.addTest(ValencyCheckerTest.suite());
        suite.addTest(ValencyHybridCheckerTest.suite());
        suite.addTest(NormalizerTest.suite());
        suite.addTest(ProteinBuilderToolTest.suite());
        // cdk.test.tools.manipulator
        suite.addTest(ManipulatorsTests.suite());
        return suite;
    }

}
