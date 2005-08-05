/* $RCSfile$    
 * $Author$    
 * $Date$    
 * $Revision$
 * 
 * Copyright (C) 2005  The Chemistry Development Kit (CDK) project
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
package org.openscience.cdk.test.config.isotopes;

import java.io.StringReader;
import java.util.Vector;

import junit.framework.Test;
import junit.framework.TestSuite;

import org.openscience.cdk.config.isotopes.IsotopeReader;
import org.openscience.cdk.test.CDKTestCase;

/**
 * Checks the funcitonality of the IsotopeFactory
 *
 * @cdk.module test
 */
public class IsotopeReaderTest extends CDKTestCase {
     
	public IsotopeReaderTest(String name) {
		super(name);
	}
	
	public void setUp() {}
	
	public static Test suite() {
		return new TestSuite(IsotopeReaderTest.class);
	}

    public void testIsotopeReader_Reader() {
        IsotopeReader reader = new IsotopeReader(
            new StringReader("")
        );
        assertNotNull(reader);
    }
    
    public void testReadIsotopes() {
        IsotopeReader reader = new IsotopeReader(
            new StringReader("")
        );
        assertNotNull(reader);
        Vector isotopes = reader.readIsotopes();
        assertNotNull(isotopes);
        assertEquals(0, isotopes.size());
    }
    
    public void testReadIsotopes2() {
        String isotopeData = 
            "<?xml version=\"1.0\"?>" +
            "<list xmlns=\"http://www.xml-cml.org/schema/cml2/core\"" +
            "    xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\"" +
            "    xsi:schemaLocation=\"http://www.xml-cml.org/schema/cml2/core ../../io/cml/data/cmlCore.xsd\">" +
            "" +
            "    <isotopeList id=\"H\">" +
            "        <isotope id=\"H1\" isotopeNumber=\"1\" elementType=\"H\">" +
            "            <abundance dictRef=\"cdk:relativeAbundance\">100.0</abundance>" +
            "            <scalar dictRef=\"cdk:exactMass\">1.00782504</scalar>" +
            "            <scalar dictRef=\"cdk:atomicNumber\">1</scalar>" +
            "        </isotope>" +
            "        <isotope id=\"H2\" isotopeNumber=\"2\" elementType=\"H\">" +
            "            <abundance dictRef=\"cdk:relativeAbundance\">0.015</abundance>" +
            "            <scalar dictRef=\"cdk:exactMass\">2.01410179</scalar>" +
            "            <scalar dictRef=\"cdk:atomicNumber\">1</scalar>" +
            "        </isotope>" +
            "        <isotope id=\"D2\" isotopeNumber=\"2\" elementType=\"D\">" +
            "            <abundance dictRef=\"cdk:relativeAbundance\">0.015</abundance>" +
            "            <scalar dictRef=\"cdk:exactMass\">2.01410179</scalar>" +
            "            <scalar dictRef=\"cdk:atomicNumber\">1</scalar>" +
            "        </isotope>" +
            "    </isotopeList>" +
            "</list>";
        
        IsotopeReader reader = new IsotopeReader(
            new StringReader(isotopeData)
        );
        assertNotNull(reader);
        Vector isotopes = reader.readIsotopes();
        assertNotNull(isotopes);
        assertEquals(3, isotopes.size());
    }
    
}
