/* $RCSfile$
 * $Author$
 * $Date$
 * $Revision$
 *
 * Copyright (C) 2003-2004 The Chemistry Development Kit (CDK) project
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
package org.openscience.cdk.applications;

import java.io.File;
import java.io.FileReader;

import org.openscience.cdk.Molecule;
import org.openscience.cdk.io.ChemObjectReader;
import org.openscience.cdk.io.ReaderFactory;
import org.openscience.cdk.isomorphism.matchers.QueryAtomContainer;
import org.openscience.cdk.isomorphism.UniversalIsomorphismTester;
import org.openscience.cdk.smiles.smarts.SMARTSParser;
import org.openscience.cdk.tools.LoggingTool;

/**
 * Command line utility that matches the given SMARTS against the given set of 
 * files.
 *
 * @cdk.module  experimental
 *
 * @author      Egon Willighagen
 * @cdk.created 2003-08-14
 * @cdk.require java1.4
 * @cdk.require ant1.6
 */
public class SubstructureFinder {

	/*
	 *  This is a command line application            *
	 *  Do not convert these System.out/err.println() *
	 *  to logger statements
	 */
	
    public static void main(String[] args) {
        if (args.length < 2) {
            System.err.println("syntax: SubstructureFinder <SMARTS> <file> <file2> ...");
            System.exit(0);
        }

        // to make sure the CDK LoggingTool is configured
        LoggingTool logger = new LoggingTool(true);

        QueryAtomContainer substructure = null;
        String smarts = args[0];
        try {
            substructure = SMARTSParser.parse(smarts);
        } catch (Exception exc) {
            System.err.println("Problem parsing SMARTS: " + smarts); 
            System.err.println("Error: " + exc.toString());
            System.exit(-1);
        }
        if (substructure != null) {
        
            // loop over all files
            for (int i=1; i<args.length; i++) {
                String ifilename = args[i];
                try {
                    File input = new File(ifilename);
                    if (!input.isDirectory()) {
                        ChemObjectReader reader = new ReaderFactory().createReader(new FileReader(input));
                        if (reader.accepts(new Molecule())) {
                            Molecule molecule = (Molecule)reader.read(new Molecule());
                            if (molecule != null) {
                                boolean matches = UniversalIsomorphismTester.isSubgraph(molecule, substructure);
                                if (matches) {
                                    System.out.println(ifilename + ": matches!");
                                }
                            }
                        }
                    }
                } catch (Exception exception) {
                    System.err.println(ifilename + ": error=");
                    exception.printStackTrace();
                }
            }
        } else {
            System.err.println("No SMARTS given!"); 
            System.exit(-1);
        }
    }

}
