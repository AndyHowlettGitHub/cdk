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
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringReader;
import java.util.StringTokenizer;

import org.openscience.cdk.interfaces.AtomContainer;
import org.openscience.cdk.interfaces.ChemFile;
import org.openscience.cdk.interfaces.ChemModel;
import org.openscience.cdk.interfaces.ChemObject;
import org.openscience.cdk.interfaces.ChemSequence;
import org.openscience.cdk.interfaces.Molecule;
import org.openscience.cdk.interfaces.SetOfMolecules;
import org.openscience.cdk.exception.CDKException;
import org.openscience.cdk.io.formats.ChemFormat;
import org.openscience.cdk.io.formats.INChIPlainTextFormat;
import org.openscience.cdk.io.inchi.INChIContentProcessorTool;

/**
 * Reads the content of a IUPAC/NIST Chemical Identifier (INChI) plain text 
 * document. This reader parses output generated with INChI 1.12beta like:
 * <pre>
 * 
 * Input_File: "E:\Program Files\INChI\inchi-samples\Figure04.mol"
 * 
 * Structure: 1
 * INChI=1.12Beta/C6H6/c1-2-4-6-5-3-1/h1-6H
 * AuxInfo=1.12Beta/0/N:1,2,3,4,5,6/E:(1,2,3,4,5,6)/rA:6CCCCCC/rB:s1;d1;d2;s3;s4d5;/rC:5.6378,-4.0013,0;5.6378,-5.3313,0;4.4859,-3.3363,0;4.4859,-5.9963,0;3.3341,-4.0013,0;3.3341,-5.3313,0;
 * </pre>
 *
 * @cdk.module experimental
 *
 * @author      Egon Willighagen <egonw@sci.kun.nl>
 * @cdk.created 2004-08-01
 *
 * @cdk.keyword file format, INChI
 * @cdk.keyword chemical identifier
 * @cdk.require java1.4+
 *
 * @see     org.openscience.cdk.io.INChIReader
 */
public class INChIPlainTextReader extends DefaultChemObjectReader {

    private BufferedReader input;
    private INChIContentProcessorTool inchiTool;

    /**
     * Construct a INChI reader from a Reader object.
     *
     * @param input the Reader with the content
     */
    public INChIPlainTextReader(Reader input) {
        this.init();
        setReader(input);
        inchiTool = new INChIContentProcessorTool();
    }

    public INChIPlainTextReader(InputStream input) {
        this(new InputStreamReader(input));
    }
    
    public INChIPlainTextReader() {
        this(new StringReader(""));
    }
    
    public ChemFormat getFormat() {
        return new INChIPlainTextFormat();
    }
    
    public void setReader(Reader input) {
        if (input instanceof BufferedReader) {
            this.input = (BufferedReader)input;
        } else {
            this.input = new BufferedReader(input);
        }
    }

    public void setReader(InputStream input) throws CDKException {
        setReader(new InputStreamReader(input));
    }

    /**
     * Initializes this reader.
     */
    private void init() {}

    /**
     * Reads a ChemObject of type object from input.
     * Supported types are: ChemFile.
     *
     * @param  object type of requested ChemObject
     * @return the content in a ChemFile object
     */
    public ChemObject read(ChemObject object) throws CDKException {
        if (object instanceof ChemFile) {
            return (ChemObject)readChemFile((ChemFile)object);
        } else {
            throw new CDKException("Only supported is reading of ChemFile objects.");
        }
    }

    // private functions

    /**
     * Reads a ChemFile object from input.
     *
     * @return ChemFile with the content read from the input
     */
    private ChemFile readChemFile(ChemFile cf) throws CDKException {
        // have to do stuff here
        try {
            String line = input.readLine();
            while (line != null) {
                if (line.startsWith("INChI=")) {
                    // ok, the fun starts
                    cf = cf.getBuilder().newChemFile();
                    // ok, we need to parse things like:
                    // INChI=1.12Beta/C6H6/c1-2-4-6-5-3-1/h1-6H
                    final String INChI = line.substring(6);
                    StringTokenizer tokenizer = new StringTokenizer(INChI, "/");
                    // ok, we expect 4 tokens
                    final String version = tokenizer.nextToken(); // 1.12Beta
                    final String formula = tokenizer.nextToken(); // C6H6
                    final String connections = tokenizer.nextToken().substring(1); // 1-2-4-6-5-3-1
                    final String hydrogens = tokenizer.nextToken().substring(1); // 1-6H
                    
                    AtomContainer parsedContent = inchiTool.processFormula(
                    		cf.getBuilder().newAtomContainer(), formula
                    );
                    inchiTool.processConnections(connections, parsedContent, -1);
                    
                    SetOfMolecules moleculeSet = cf.getBuilder().newSetOfMolecules();
                    moleculeSet.addMolecule(cf.getBuilder().newMolecule(parsedContent));
                    ChemModel model = cf.getBuilder().newChemModel();
                    model.setSetOfMolecules(moleculeSet);
                    ChemSequence sequence = cf.getBuilder().newChemSequence();
                    sequence.addChemModel(model);
                    cf.addChemSequence(sequence);
                }
                line = input.readLine();
            }
        } catch (Exception exception) {
            throw new CDKException("Error while reading INChI file: " + exception.getMessage());
        }
        return cf;
    }

    public void close() throws IOException {
        input.close();
    }
}

