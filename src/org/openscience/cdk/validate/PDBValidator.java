/* $RCSfile$
 * $Author$
 * $Date$
 * $Revision$
 * 
 * Copyright (C) 2003-2005  The Chemistry Development Kit (CDK) project
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
package org.openscience.cdk.validate;

import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.Vector;

import org.openscience.cdk.Atom;
import org.openscience.cdk.interfaces.AtomContainer;
import org.openscience.cdk.ChemFile;
import org.openscience.cdk.ChemModel;
import org.openscience.cdk.EnzymeResidueLocator;
import org.openscience.cdk.PseudoAtom;
import org.openscience.cdk.io.MACiEReader;
import org.openscience.cdk.io.PDBReader;
import org.openscience.cdk.tools.LoggingTool;
import org.openscience.cdk.tools.manipulator.ChemFileManipulator;
import org.openscience.cdk.tools.manipulator.ChemModelManipulator;

/**
 * Validates the existence of references to dictionaries.
 *
 * @cdk.module experimental
 *
 * @author   Egon Willighagen
 * @cdk.created  2003-08-27
 * @cdk.require java1.4+
 */ 
public class PDBValidator extends AbstractValidator {

    private static LoggingTool logger;
    
    static {
        logger = new LoggingTool(PDBValidator.class);
    }

    private static String prefix = "http://www.rcsb.org/pdb/cgi/export.cgi/?format=PDB&pdbId=";
    private static String postfix = "&compression=None";

    public PDBValidator() {}

    public ValidationReport validateChemModel(ChemModel subject) {
        ValidationReport report = new ValidationReport();
        logger.debug("Starting to validate against PDB entry...");
        Object PDBcodeObject = subject.getProperty(MACiEReader.PDBCode);
        if (PDBcodeObject != null) {
            String PDB = PDBcodeObject.toString();
            logger.info("Validating against PDB code: " + PDB);
            ChemFile file = null;
            try {
                URL pdbQuery = new URL(prefix + PDB + postfix);
                logger.info("Downloading PDB file from: " + pdbQuery.toString());
                URLConnection connection = pdbQuery.openConnection();
                PDBReader reader = new PDBReader(new InputStreamReader(connection.getInputStream()));
                file = (ChemFile)reader.read(new ChemFile());
            } catch (Exception exception) {
                logger.error("Could not download or parse PDB entry");
                logger.debug(exception);
                return report;
            }
            logger.info("Successvully download PDB entry");
            
            // ok, now make a hash with all residueLocator in the PDB file
            Vector residues = new Vector();
            AtomContainer allPDBAtoms = ChemFileManipulator.getAllInOneContainer(file);
            org.openscience.cdk.interfaces.Atom[] atoms = allPDBAtoms.getAtoms();
            logger.info("Found in PDB file, #atoms: " + atoms.length);
            for (int i=0; i< atoms.length; i++) {
                String resName = (String)atoms[i].getProperty("pdb.resName");
                String resSeq = (String)atoms[i].getProperty("pdb.resSeq");
                String resLocator = resName + resSeq;
                if (!residues.contains(resLocator.toLowerCase())) {
                    logger.debug("Found new residueLocator: " + resLocator);
                    residues.add(resLocator.toLowerCase());
                }
            }
            
            // now see if the model undergoing validation has bad locators
            AtomContainer allAtoms = ChemModelManipulator.getAllInOneContainer(subject);
            org.openscience.cdk.interfaces.Atom[] validateAtoms = allAtoms.getAtoms();
            for (int i=0; i<validateAtoms.length; i++) {
                // only testing PseudoAtom's
            	org.openscience.cdk.interfaces.Atom validateAtom = validateAtoms[i];
                if (validateAtom instanceof EnzymeResidueLocator) {
                    ValidationTest badResidueLocator = new ValidationTest(validateAtom,
                        "ResidueLocator does not exist in PDB entry."
                    );
                    String label = ((PseudoAtom)validateAtom).getLabel();
                    if (residues.contains(label.toLowerCase())) {
                        // yes, not problem
                        report.addOK(badResidueLocator);
                    } else {
                        badResidueLocator.setDetails(
                            "Could not find " + label + " in PDB entry for " + PDB
                        );
                        report.addError(badResidueLocator);
                    }
                } else {
                    // ok, then don't test
                }
            }
        }
        return report;
    }
    
}
