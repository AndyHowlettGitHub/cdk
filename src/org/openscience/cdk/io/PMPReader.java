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
 * All we ask is that proper credit is given for our work, which includes
 * - but is not limited to - adding the above copyright notice to the beginning
 * of your source code files, and to any copyright notice that you may distribute
 * with programs based on this work.
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
package org.openscience.cdk.io;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;
import java.util.Hashtable;
import java.util.StringTokenizer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.openscience.cdk.Atom;
import org.openscience.cdk.AtomContainer;
import org.openscience.cdk.Bond;
import org.openscience.cdk.ChemFile;
import org.openscience.cdk.ChemModel;
import org.openscience.cdk.ChemObject;
import org.openscience.cdk.ChemSequence;
import org.openscience.cdk.Crystal;
import org.openscience.cdk.Molecule;
import org.openscience.cdk.SetOfMolecules;
import org.openscience.cdk.exception.CDKException;
import org.openscience.cdk.tools.ChemModelManipulator;

/**
 * Reads an frames from a PMP formated input.
 * Both compilation and use of this class requires Java 1.4.
 *
 * @cdk.module io
 *
 * @cdk.keyword file format, Polymorph Predictor (tm)
 *
 * @author E.L. Willighagen
 *
 * @since Java 1.4
 */
public class PMPReader extends DefaultChemObjectReader {

    private BufferedReader input;

    private org.openscience.cdk.tools.LoggingTool logger;

    /* Keep a copy of the PMP model */
    private SetOfMolecules som;
    private ChemModel modelModel;
    private Molecule molecule;
    private ChemObject chemObject;
    /* Keep an index of PMP id -> AtomCountainer id */
    private Hashtable atomids = new Hashtable();
    private Hashtable bondids = new Hashtable();

    /* Often used patterns */
    Pattern objHeader;
    Pattern objCommand;
    Pattern atomTypePattern;

    /*
     * construct a new reader from a Reader type object
     *
     * @param input reader from which input is read
     */
    public PMPReader(Reader input) {
        this.input = new BufferedReader(input);
        logger = new org.openscience.cdk.tools.LoggingTool(this.getClass().getName());
    
        /* compile patterns */
        objHeader = Pattern.compile(".*\\((\\d+)\\s(\\w+)$");
        objCommand = Pattern.compile(".*\\(A\\s(C|F|D|I|O)\\s(\\w+)\\s+\"?(.*?)\"?\\)$");
        atomTypePattern = Pattern.compile("^(\\d+)\\s+(\\w+)$");
    }

    public String getFormatName() {
        return "PolyMorph Predictor (Cerius)";
    }
    
    /**
     * reads the content from a PMP input. It can only return a
     * ChemObject of type ChemFile
     *
     * @param object class must be of type ChemFile
     *
     * @see ChemFile
     */
    public ChemObject read(ChemObject object) throws CDKException {
        if (object instanceof ChemFile) {
            return (ChemObject)readChemFile();
        } else {
            throw new CDKException("Only supported is reading of ChemFile objects.");
        }
    }

    // private procedures

    /**
     *  Private method that actually parses the input to read a ChemFile
     *  object.
     *
     *  Each PMP frame is stored as a Crystal in a ChemModel. The PMP
     *  file is stored as a ChemSequence of ChemModels.
     *
     * @return A ChemFile containing the data parsed from input.
     */
    private ChemFile readChemFile() {
        ChemFile chemFile = new ChemFile();
        ChemSequence chemSequence = new ChemSequence();
        ChemModel chemModel = new ChemModel();
        Crystal crystal = new Crystal();

        StringTokenizer tokenizer;

        try {
            String line = input.readLine();
            while (input.ready() && line != null) {
                if (line.startsWith("%%Header Start")) {
                    // parse Header section
                    while (input.ready() && line != null && !(line.startsWith("%%Header End"))) {
                        if (line.startsWith("%%Version Number")) {
                            String version = input.readLine().trim();
                            if (!version.equals("3.00")) {
                                logger.error("The PMPReader only supports PMP files with version 3.00");
                                return null;
                            }
                        }
                        line = input.readLine();
                    }
                } else if (line.startsWith("%%Model Start")) {
                    // parse Model section
                    while (input.ready() && line != null && !(line.startsWith("%%Model End"))) {
                        Matcher objHeaderMatcher = objHeader.matcher(line);
                        if (objHeaderMatcher.matches()) {
                            String object = objHeaderMatcher.group(2);
                            constructObject(object);
                            int id = Integer.parseInt(objHeaderMatcher.group(1));
                            // System.out.println(object + " id: " + id);
                            line = input.readLine();
                            while (input.ready() && line != null && !(line.trim().equals(")"))) {
                                // parse object command (or new object header)
                                Matcher objCommandMatcher = objCommand.matcher(line);
                                objHeaderMatcher = objHeader.matcher(line);
                                if (objHeaderMatcher.matches()) {
                                    // ok, forget about nesting and hope for the best
                                    object = objHeaderMatcher.group(2);
                                    id = Integer.parseInt(objHeaderMatcher.group(1));
                                    constructObject(object);
                                } else if (objCommandMatcher.matches()) {
                                    String format = objCommandMatcher.group(1);
                                    String command = objCommandMatcher.group(2);
                                    String field = objCommandMatcher.group(3);
                                    processModelCommand(object, command, format, field);
                                } else {
                                    logger.warn("Skipping line: " + line);
                                }
                                line = input.readLine();
                            }
                            if (chemObject instanceof Atom) {
                                atomids.put(new Integer(id), new Integer(molecule.getAtomCount()));
                                molecule.addAtom((Atom)chemObject);
                            } else if (chemObject instanceof Bond) {
                                bondids.put(new Integer(id), new Integer(molecule.getAtomCount()));
                                molecule.addBond((Bond)chemObject);
                            } else {
                                logger.error("chemObject is not initialized or of bad class type");
                            }
                            // System.out.println(molecule.toString());
                        }
                        line = input.readLine();
                    }
                    som.addMolecule(molecule);
                    modelModel.setSetOfMolecules(som);
                } else if (line.startsWith("%%Traj Start")) {
                    chemSequence = new ChemSequence();
                    while (input.ready() && line != null && !(line.startsWith("%%Traj End"))) {
                        if (line.startsWith("%%Start Frame")) {
                            chemModel = new ChemModel();
                            crystal = new Crystal();
                            AtomContainer atomC = ChemModelManipulator.getAllInOneContainer(modelModel);
                            while (input.ready() && line != null && !(line.startsWith("%%End Frame"))) {
                                // process frame data
                                if (line.startsWith("%%Atom Coords")) {
                                    // add atomC as atoms to crystal
                                    crystal.add((AtomContainer)atomC.clone());
                                    int expatoms = atomC.getAtomCount();
                                    // exception
                                    for (int i=0; i < expatoms; i++) {
                                        line = input.readLine();
                                        Atom a = crystal.getAtomAt(i);
                                        StringTokenizer st = new StringTokenizer(line, " ");
                                        a.setX3D(Double.parseDouble(st.nextToken()));
                                        a.setY3D(Double.parseDouble(st.nextToken()));
                                        a.setZ3D(Double.parseDouble(st.nextToken()));
                                    }
                                } else if (line.startsWith("%%Lat Vects")) {
                                    StringTokenizer st;
                                    line = input.readLine();
                                    st = new StringTokenizer(line, " ");
                                    crystal.setA(
                                        Double.parseDouble(st.nextToken()),
                                        Double.parseDouble(st.nextToken()),
                                        Double.parseDouble(st.nextToken())
                                    );
                                    line = input.readLine();
                                    st = new StringTokenizer(line, " ");
                                    crystal.setB(
                                        Double.parseDouble(st.nextToken()),
                                        Double.parseDouble(st.nextToken()),
                                        Double.parseDouble(st.nextToken())
                                    );
                                    line = input.readLine();
                                    st = new StringTokenizer(line, " ");
                                    crystal.setC(
                                        Double.parseDouble(st.nextToken()),
                                        Double.parseDouble(st.nextToken()),
                                        Double.parseDouble(st.nextToken())
                                    );
                                } else if (line.startsWith("%%Space Group")) {
                                    line = input.readLine().trim();
                                    /* standardize space group name.
                                       See Crystal.setSpaceGroup() */
                                    if ("P 21 21 21 (1)".equals(line)) {
                                        crystal.setSpaceGroup("P 2_1 2_1 2_1");
                                    } else {
                                        crystal.setSpaceGroup("P1");
                                    }
                                } else {
                                }
                                line = input.readLine();
                            }
                            chemModel.setCrystal(crystal);
                            chemSequence.addChemModel(chemModel);
                        }
                        line = input.readLine();
                    }
                    chemFile.addChemSequence(chemSequence);
                } else {
                    // disregard line
                }
                // read next line
                line = input.readLine();
            }
        } catch (IOException e) {
            // should make some noise now
            chemFile = null;
        }

        return chemFile;
    }

    private void processModelCommand(String object, String command, String format, String field) {
        logger.debug(object + "->" + command + " (" + format + "): " + field);
        if ("Model".equals(object)) {
            logger.warn("Unkown PMP Model command: " + command);
        } else if ("Atom".equals(object)) {
            if ("ACL".equals(command)) {
                Matcher atomTypeMatcher = atomTypePattern.matcher(field);
                if (atomTypeMatcher.matches()) {
                    int atomicnum = Integer.parseInt(atomTypeMatcher.group(1));
                    String type = atomTypeMatcher.group(2);
                    ((Atom)chemObject).setAtomicNumber(atomicnum);
                    ((Atom)chemObject).setSymbol(type);
                } else {
                    logger.error("Incorrectly formated field value: " + field + ".");
                }
            } else if ("Charge".equals(command)) {
                try {
                    double charge = Double.parseDouble(field);
                    ((Atom)chemObject).setCharge(charge);
                } catch (NumberFormatException e) {
                    logger.error("Incorrectly formated float field: " + field + ".");
                }
            } else if ("CMAPPINGS".equals(command)) {
            } else if ("FFType".equals(command)) {
            } else if ("Id".equals(command)) {
            } else if ("Mass".equals(command)) {
            } else if ("XYZ".equals(command)) {
            } else if ("ZOrder".equals(command)) {
            } else {
                logger.warn("Unkown PMP Atom command: " + command);
            }
        } else if ("Bond".equals(object)) {
            if ("Atom1".equals(command)) {
                int atomid = Integer.parseInt(field);
                // this assumes that the atoms involved in this bond are
                // already added, which seems the case in the PMP files
                int realatomid = ((Integer)atomids.get(new Integer(atomid))).intValue();
                Atom a = molecule.getAtomAt(realatomid);
                ((Bond)chemObject).setAtomAt(a, 0);
            } else if ("Atom2".equals(command)) {
                int atomid = Integer.parseInt(field);
                // this assumes that the atoms involved in this bond are
                // already added, which seems the case in the PMP files
                int realatomid = ((Integer)atomids.get(new Integer(atomid))).intValue();
                Atom a = molecule.getAtomAt(realatomid);
                ((Bond)chemObject).setAtomAt(a, 1);
            } else if ("Order".equals(command)) {
                double order = Double.parseDouble(field);
                ((Bond)chemObject).setOrder(order);
            } else if ("Id".equals(command)) {
            } else if ("Label".equals(command)) {
            } else if ("3DGridOrigin".equals(command)) {
            } else if ("3DGridMatrix".equals(command)) {
            } else if ("3DGridDivision".equals(command)) {
            } else {
                logger.warn("Unkown PMP Bond command: " + command);
            }
        } else {
            logger.warn("Unkown PMP object: " + object);
        }
    }
    
    private void constructObject(String object) {
        if ("Atom".equals(object)) {
            chemObject = new Atom("C");
        } else if ("Bond".equals(object)) {
            chemObject = new Bond();
        } else if ("Model".equals(object)) {
            modelModel = new ChemModel();
            som = new SetOfMolecules();
            molecule = new Molecule();
        } else {
            logger.error("Cannot construct PMP object type: " + object);
        }
    };

    public void close() throws IOException {
        input.close();
    }
}
