/* $RCSfile$
 * $Author$
 * $Date$
 * $Revision$
 *
 * Copyright (C) 2002-2003  The Jmol Development Team
 * Copyright (C) 2003-2005  The Chemistry Development Kit (CDK) project
 *
 * Contact: cdk-devel@lists.sourceforge.net
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 *  This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA
 * 02111-1307  USA.
 */
package org.openscience.cdk.io;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StreamTokenizer;
import java.io.StringReader;
import java.util.StringTokenizer;

import javax.vecmath.Point3d;

import org.openscience.cdk.Atom;
import org.openscience.cdk.AtomContainer;
import org.openscience.cdk.ChemFile;
import org.openscience.cdk.ChemModel;
import org.openscience.cdk.interfaces.ChemObject;
import org.openscience.cdk.ChemSequence;
import org.openscience.cdk.Molecule;
import org.openscience.cdk.SetOfMolecules;
import org.openscience.cdk.config.IsotopeFactory;
import org.openscience.cdk.exception.CDKException;
import org.openscience.cdk.io.formats.ChemFormat;
import org.openscience.cdk.io.formats.Gaussian03Format;
import org.openscience.cdk.tools.LoggingTool;

/**
 * A reader for Gaussian03 output.
 * Gaussian 03 is a quantum chemistry program
 * by Gaussian, Inc. (http://www.gaussian.com/).
 *
 * <p>Molecular coordinates, energies, and normal coordinates of
 * vibrations are read. Each set of coordinates is added to the
 * ChemFile in the order they are found. Energies and vibrations
 * are associated with the previously read set of coordinates.
 *
 * <p>This reader was developed from a small set of
 * example output files, and therefore, is not guaranteed to
 * properly read all Gaussian03 output. If you have problems,
 * please contact the author of this code, not the developers
 * of Gaussian03.
 *
 * <p>This code was adaptated by Jonathan from Gaussian98Reader written by
 * Bradley, and ported to CDK by Egon.
 *
 * @cdk.module io
 *
 * @author Jonathan C. Rienstra-Kiracofe <jrienst@emory.edu>
 * @author Bradley A. Smith <yeldar@home.com>
 * @author Egon Willighagen
 */
public class Gaussian03Reader extends DefaultChemObjectReader {

    private IsotopeFactory isotopeFactory;
    private BufferedReader input;
    private LoggingTool logger;
    
    public Gaussian03Reader(Reader reader) {
        input = new BufferedReader(reader);
        logger = new LoggingTool(this);
        try {
            isotopeFactory = IsotopeFactory.getInstance();
        } catch (Exception exception) {
            // should not happen
        }
    }
    
    public Gaussian03Reader(InputStream input) {
        this(new InputStreamReader(input));
    }
    
    public Gaussian03Reader() {
        this(new StringReader(""));
    }
    
    public ChemFormat getFormat() {
        return new Gaussian03Format();
    }
    
    public void setReader(Reader reader) throws CDKException {
        this.input = new BufferedReader(input);
    }

    public void setReader(InputStream input) throws CDKException {
        setReader(new InputStreamReader(input));
    }

    public boolean accepts(ChemObject object) {
        if (object instanceof ChemSequence) {
            return true;
        } else if (object instanceof ChemFile) {
            return true;
        } else {
            return false;
        }
    }
    
    public ChemObject read(ChemObject object) throws CDKException {
        if (object instanceof ChemSequence) {
            return readChemSequence();
        } else if (object instanceof ChemFile) {
            return readChemFile();
        } else {
            throw new CDKException("Object " + object.getClass().getName() + " is not supported");
        }
    }
    
    public void close() throws IOException {
        input.close();
    }
    
    private ChemFile readChemFile() throws CDKException {
        ChemFile chemFile = new ChemFile();
        ChemSequence sequence = readChemSequence();
        chemFile.addChemSequence(sequence);
        return chemFile;
    }
    
    private ChemSequence readChemSequence() throws CDKException {
        ChemSequence sequence = new ChemSequence();
        ChemModel model = null;
        
        try {
            String line = input.readLine();
            String levelOfTheory = null;
            
            // Find first set of coordinates
            while (input.ready() && (line != null)) {
                if (line.indexOf("Standard orientation:") >= 0) {
                    
                    // Found a set of coordinates
                    model = new ChemModel();
                    try {
                        readCoordinates(model);
                    } catch (IOException exception) {
                        throw new CDKException("Error while reading coordinates: " + exception.toString());
                    }
                    break;
                }
                line = input.readLine();
            }
            if (model != null) {
                // Read all other data
                line = input.readLine();
                while (input.ready() && (line != null)) {
                    if (line.indexOf("Standard orientation:") >= 0) {
                        // Found a set of coordinates
                        // Add current frame to file and create a new one.
                        sequence.addChemModel(model);
                        fireFrameRead();
                        model = new ChemModel();
                        readCoordinates(model);
                    } else if (line.indexOf("SCF Done:") >= 0) {
                        // Found an energy
                        model.setProperty("org.openscience.cdk.io.Gaussian03Reaer:SCF Done", line.trim());
                    } else if (line.indexOf("Harmonic frequencies") >= 0) {
                        // Found a set of vibrations
                        try {
                            readFrequencies(model);
                        } catch (IOException exception) {
                            throw new CDKException("Error while reading frequencies: " + exception.toString());
                        }
                    } else if (line.indexOf("Mulliken atomic charges") >= 0) {
                        readPartialCharges(model);
                    } else if (line.indexOf("Magnetic shielding") >= 0) {
                        // Found NMR data
                        try {
                            readNMRData(model, line);
                        } catch (IOException exception) {
                            throw new CDKException("Error while reading NMR data: " + exception.toString());
                        }
                    } else if (line.indexOf("GINC") >= 0) {
                        // Found calculation level of theory
                        levelOfTheory = parseLevelOfTheory(line);
                        // FIXME: is doing anything with it?
                    }
                    line = input.readLine();
                }
                
                // Add current frame to file
                sequence.addChemModel(model);
                fireFrameRead();
            }
        } catch (IOException exception) {
            throw new CDKException("Error while reading general structure: " + exception.toString());
        }
        return sequence;
    }
    
    /**
     * Reads a set of coordinates into ChemModel.
     *
     * @param     model        the destination ChemModel
     * @exception IOException  if an I/O error occurs
     */
    private void readCoordinates(ChemModel model) throws CDKException, IOException {
        AtomContainer container = new org.openscience.cdk.AtomContainer();
        String line = input.readLine();
        line = input.readLine();
        line = input.readLine();
        line = input.readLine();
        while (input.ready()) {
            line = input.readLine();
            if ((line == null) || (line.indexOf("-----") >= 0)) {
                break;
            }
            int atomicNumber = 0;
            StringReader sr = new StringReader(line);
            StreamTokenizer token = new StreamTokenizer(sr);
            token.nextToken();
            
            // ignore first token
            if (token.nextToken() == StreamTokenizer.TT_NUMBER) {
                atomicNumber = (int) token.nval;
                if (atomicNumber == 0) {
                    
                    // Skip dummy atoms. Dummy atoms must be skipped
                    // if frequencies are to be read because Gaussian
                    // does not report dummy atoms in frequencies, and
                    // the number of atoms is used for reading frequencies.
                    continue;
                }
            } else {
                throw new IOException("Error reading coordinates");
            }
            token.nextToken();
            
            // ignore third token
            double x = 0.0;
            double y = 0.0;
            double z = 0.0;
            if (token.nextToken() == StreamTokenizer.TT_NUMBER) {
                x = token.nval;
            } else {
                throw new IOException("Error reading coordinates");
            }
            if (token.nextToken() == StreamTokenizer.TT_NUMBER) {
                y = token.nval;
            } else {
                throw new IOException("Error reading coordinates");
            }
            if (token.nextToken() == StreamTokenizer.TT_NUMBER) {
                z = token.nval;
            } else {
                throw new IOException("Error reading coordinates");
            }
            Atom atom = new Atom(isotopeFactory.getElementSymbol(atomicNumber));
            atom.setPoint3d(new Point3d(x, y, z));
            container.addAtom(atom);
        }
        SetOfMolecules moleculeSet = new SetOfMolecules();
        moleculeSet.addMolecule(new Molecule(container));
        model.setSetOfMolecules(moleculeSet);
    }

    /**
     * Reads partial atomic charges and add the to the given ChemModel.
     */
    private void readPartialCharges(ChemModel model) throws CDKException, IOException {
        logger.info("Reading partial atomic charges");
        SetOfMolecules moleculeSet = model.getSetOfMolecules();
        org.openscience.cdk.interfaces.Molecule molecule = moleculeSet.getMolecule(0);
        String line = input.readLine(); // skip first line after "Total atomic charges"
        while (input.ready()) {
            line = input.readLine();
            logger.debug("Read charge block line: " + line);
            if ((line == null) || (line.indexOf("Sum of Mulliken charges") >= 0)) {
                logger.debug("End of charge block found");
                break;
            }
            StringReader sr = new StringReader(line);
            StreamTokenizer tokenizer = new StreamTokenizer(sr);
            if (tokenizer.nextToken() == StreamTokenizer.TT_NUMBER) {
                int atomCounter = (int) tokenizer.nval;

                tokenizer.nextToken(); // ignore the symbol
                
                double charge = 0.0;
                if (tokenizer.nextToken() == StreamTokenizer.TT_NUMBER) {
                    charge = (double)tokenizer.nval;
                    logger.debug("Found charge for atom " + atomCounter + 
                                 ": " + charge);
                } else {
                    throw new CDKException("Error while reading charge: expected double.");
                }
                org.openscience.cdk.interfaces.Atom atom = molecule.getAtomAt(atomCounter-1);
                atom.setCharge(charge);
            }
        }
    }

    /**
     * Reads a set of vibrations into ChemModel.
     *
     * @param frame  the destination ChemModel
     * @exception IOException  if an I/O error occurs
     */
    private void readFrequencies(ChemModel model) throws IOException {
        /* This is yet to be ported. Vibrations don't exist yet in CDK.
        String line = input.readLine();
        line = input.readLine();
        line = input.readLine();
        line = input.readLine();
        line = input.readLine();
        while ((line != null) && line.startsWith(" Frequencies --")) {
            Vector currentVibs = new Vector();
            StringReader vibValRead = new StringReader(line.substring(15));
            StreamTokenizer token = new StreamTokenizer(vibValRead);
            while (token.nextToken() != StreamTokenizer.TT_EOF) {
                Vibration vib = new Vibration(Double.toString(token.nval));
                currentVibs.addElement(vib);
            }
            line = input.readLine(); // skip "Red. masses"
            line = input.readLine(); // skip "Rfc consts"
            line = input.readLine(); // skip "IR Inten"
            while (!line.startsWith(" Atom AN")) {
                // skip all lines upto and including the " Atom AN" line
                line = input.readLine(); // skip
            }
            for (int i = 0; i < frame.getAtomCount(); ++i) {
                line = input.readLine();
                StringReader vectorRead = new StringReader(line);
                token = new StreamTokenizer(vectorRead);
                token.nextToken();
                
                // ignore first token
                token.nextToken();
                
                // ignore second token
                for (int j = 0; j < currentVibs.size(); ++j) {
                    double[] v = new double[3];
                    if (token.nextToken() == StreamTokenizer.TT_NUMBER) {
                        v[0] = token.nval;
                    } else {
                        throw new IOException("Error reading frequency");
                    }
                    if (token.nextToken() == StreamTokenizer.TT_NUMBER) {
                        v[1] = token.nval;
                    } else {
                        throw new IOException("Error reading frequency");
                    }
                    if (token.nextToken() == StreamTokenizer.TT_NUMBER) {
                        v[2] = token.nval;
                    } else {
                        throw new IOException("Error reading frequency");
                    }
                    ((Vibration) currentVibs.elementAt(j)).addAtomVector(v);
                }
            }
            for (int i = 0; i < currentVibs.size(); ++i) {
                frame.addVibration((Vibration) currentVibs.elementAt(i));
            }
            line = input.readLine();
            line = input.readLine();
            line = input.readLine();
        } */
    }

    /**
     * Reads NMR nuclear shieldings.
     */
    private void readNMRData(ChemModel model, String labelLine) throws IOException {
        /* FIXME: this is yet to be ported. CDK does not have shielding stuff.
        // Determine label for properties
        String label;
        if (labelLine.indexOf("Diamagnetic") >= 0) {
            label = "Diamagnetic Magnetic shielding (Isotropic)";
        } else if (labelLine.indexOf("Paramagnetic") >= 0) {
            label = "Paramagnetic Magnetic shielding (Isotropic)";
        } else {
            label = "Magnetic shielding (Isotropic)";
        }
        int atomIndex = 0;
        for (int i = 0; i < frame.getAtomCount(); ++i) {
            String line = input.readLine().trim();
            while (line.indexOf("Isotropic") < 0) {
                if (line == null) {
                    return;
                }
                line = input.readLine().trim();
            }
            StringTokenizer st1 = new StringTokenizer(line);
            
            // Find Isotropic label
            while (st1.hasMoreTokens()) {
                if (st1.nextToken().equals("Isotropic")) {
                    break;
                }
            }
            
            // Find Isotropic value
            while (st1.hasMoreTokens()) {
                if (st1.nextToken().equals("=")) {
                    break;
                }
            }
            double shielding = Double.valueOf(st1.nextToken()).doubleValue();
            NMRShielding ns1 = new NMRShielding(label, shielding);
            ((org.openscience.jmol.Atom)frame.getAtomAt(atomIndex)).addProperty(ns1);
            ++atomIndex;
        } */
    }
    
    /**
     * Select the theory and basis set from the first archive line.
     */
    private String parseLevelOfTheory(String line) {
        
        StringTokenizer st1 = new StringTokenizer(line, "\\");
        
        // Must contain at least 6 tokens
        if (st1.countTokens() < 6) {
            return null;
        }
        
        // Skip first four tokens
        for (int i = 0; i < 4; ++i) {
            st1.nextToken();
        }
        return st1.nextToken() + "/" + st1.nextToken();
    }
    
}
