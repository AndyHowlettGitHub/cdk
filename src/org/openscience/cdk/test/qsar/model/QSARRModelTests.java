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
package org.openscience.cdk.test.qsar.model;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;
import org.openscience.cdk.test.qsar.model.*;

/**
 * TestSuite that runs all Model tests.
 *
 * @author     Rajarshi Guha
 * @cdk.module test
 */
public class QSARRModelTests {
    
    public static Test suite() {
        TestSuite suite = new TestSuite("All QSAR R Based Modeling Tests");
        try {
            Class testClass = suite.getClass().getClassLoader().loadClass("org.openscience.cdk.test.qsar.model.SJavaEnvironmentTest");
            suite.addTest(new TestSuite(testClass));
            testClass = suite.getClass().getClassLoader().loadClass("org.openscience.cdk.test.qsar.model.LinearRegressionModelTest");
            suite.addTest(new TestSuite(testClass));
            testClass = suite.getClass().getClassLoader().loadClass("org.openscience.cdk.test.qsar.model.CNNRegressionModelTest");
            suite.addTest(new TestSuite(testClass));
            testClass = suite.getClass().getClassLoader().loadClass("org.openscience.cdk.test.qsar.model.ModelLoadSaveTest");
            suite.addTest(new TestSuite(testClass));
        } catch (ClassNotFoundException exception) {
            System.out.println("SJava is not found, skipping R tests...");
            exception.printStackTrace();
        } catch (Exception exception) {
            System.out.println("Could not load an R model test: " + exception.getMessage());
            exception.printStackTrace();
        }
        return suite;
    }
}
