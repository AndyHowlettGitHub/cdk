/* $RCSfile$
 * $Author$
 * $Date$
 * $Revision$
 *
 * Copyright (C) 2002-2004  The Jmol Development Team
 *
 * Contact: cdk-devel@lists.sourceforge.net
 *
 *  This library is free software; you can redistribute it and/or
 *  modify it under the terms of the GNU Lesser General Public
 *  License as published by the Free Software Foundation; either
 *  version 2.1 of the License, or (at your option) any later version.
 *
 *  This library is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *  Lesser General Public License for more details.
 *
 *  You should have received a copy of the GNU Lesser General Public
 *  License along with this library; if not, write to the Free Software
 *  Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA
 *  02111-1307  USA.
 */
package org.openscience.cdk.io;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.util.StringTokenizer;

import org.openscience.cdk.ChemModel;
import org.openscience.cdk.ChemObject;
import org.openscience.cdk.Molecule;
import org.openscience.cdk.Reaction;
import org.openscience.cdk.SetOfReactions;
import org.openscience.cdk.exception.CDKException;
import org.openscience.cdk.io.formats.*;
import org.openscience.cdk.io.setting.IOSetting;
import org.openscience.cdk.tools.LoggingTool;

/**
 * Class that implements the new MDL mol format introduced in August 2002.
 * The overall syntax is compatible with the old format, but I consider
 * the format completely different, and thus implemented a separate Reader
 * for it.
 *
 * @cdk.module io
 *
 * @author  Egon Willighagen <egonw@sci.kun.nl>
 * @cdk.created 2003-10-05
 * 
 * @cdk.keyword MDL V3000
 * @cdk.require java1.4
 */
public class MDLRXNV3000Reader extends DefaultChemObjectReader {

    BufferedReader input = null;
    private LoggingTool logger = null;

    public MDLRXNV3000Reader(Reader in) {
        logger = new org.openscience.cdk.tools.LoggingTool(this.getClass().getName());
        input = new BufferedReader(in);
        initIOSettings();
    }

    public MDLRXNV3000Reader() {
        this(new StringReader(""));
    }
    
    public ChemFormat getFormat() {
        return new MDLRXNV3000Format();
    }

    public void setReader(Reader input) throws CDKException {
        if (input instanceof BufferedReader) {
            this.input = (BufferedReader)input;
        } else {
            this.input = new BufferedReader(input);
        }
    }

    public boolean matches(int lineNumber, String line) {
        if (line.startsWith("$RXN V3000")) {
            return true;
        }
        return false;
    }
    
    public ChemObject read(ChemObject object) throws CDKException {
         if (object instanceof Reaction) {
             return (ChemObject) readReaction();
         } else if (object instanceof ChemModel) {
             ChemModel model = new ChemModel();
             SetOfReactions reactionSet = new SetOfReactions();
             reactionSet.addReaction(readReaction());
             model.setSetOfReactions(reactionSet);
             return model;
         } else {
             throw new CDKException("Only supported are Reaction and ChemModel, and not " +
                 object.getClass().getName() + "."
             );
         }
     }
    
    /**
     * Reads the command on this line. If the line is continued on the next, that
     * part is added.
     *
     * @return Returns the command on this line.
     */
    public String readCommand() throws CDKException {
        String line = readLine();
        if (line.startsWith("M  V30 ")) {
            String command =  line.substring(7);
            if (command.endsWith("-")) {
                command = command.substring(0, command.length()-1);
                command += readCommand();
            }
            return command;
        } else {
            throw new CDKException("Could not read MDL file: unexpected line: " + line);
        }
    }
    
    public String readLine() throws CDKException {
        String line = null;
        try {
            line = input.readLine();
            logger.debug("read line: " + line);
        } catch (Exception exception) {
            String error = "Unexpected error while reading file: " + exception.getMessage();
            logger.error(error);
            logger.debug(exception);
            throw new CDKException(error);
        }
        return line;
    }
    
    private Reaction readReaction() throws CDKException {
        Reaction reaction = new Reaction();
        String firstLine = readLine(); // should be $RXN
        String secondLine = readLine();
        String thirdLine = readLine();
        String fourthLine = readLine();

        int reactantCount = 0;
        int productCount = 0;
        boolean foundCOUNTS = false;
        while (isReady() && !foundCOUNTS) {
            String command = readCommand();
            if (command.startsWith("COUNTS")) {
                StringTokenizer tokenizer = new StringTokenizer(command);
                try {
                    String dummy = tokenizer.nextToken();
                    reactantCount = Integer.valueOf(tokenizer.nextToken()).intValue();
                    logger.info("Expecting " + reactantCount + " reactants in file");
                    productCount = Integer.valueOf(tokenizer.nextToken()).intValue();
                    logger.info("Expecting " + productCount + " products in file");
                } catch (Exception exception) {
                    logger.debug(exception);
                    throw new CDKException("Error while counts line of RXN file");
                }
                foundCOUNTS = true;
            } else {
                logger.warn("Waiting for COUNTS line, but found: " + command);
            }
        }
        
        // now read the reactants
        for (int i=1; i<=reactantCount; i++) {
            StringBuffer molFile = new StringBuffer();
            String announceMDLFileLine = readCommand();
            if (!announceMDLFileLine.equals("BEGIN REACTANT")) {
                String error = "Excepted start of reactant, but found: " + announceMDLFileLine;
                logger.error(error);
                throw new CDKException(error);
            }
            String molFileLine = "";
            while (!molFileLine.endsWith("END REACTANT")) {
                molFileLine = readLine();
                molFile.append(molFileLine);
                molFile.append("\n");
            };
            
            try {
                // read MDL molfile content
                MDLV3000Reader reader = new MDLV3000Reader(
                  new StringReader(molFile.toString()));
                Molecule reactant = (Molecule)reader.read(
                  new Molecule());
                  
                // add reactant
                reaction.addReactant(reactant);
            } catch (Exception exception) {
                String error = "Error while reading reactant: " + exception.getMessage();
                logger.error(error);
                logger.debug(exception);
                throw new CDKException(error);
            }
        }
        
        // now read the products
        for (int i=1; i<=productCount; i++) {
            StringBuffer molFile = new StringBuffer();
            String announceMDLFileLine = readCommand();
            if (!announceMDLFileLine.equals("BEGIN PRODUCT")) {
                String error = "Excepted start of product, but found: " + announceMDLFileLine;
                logger.error(error);
                throw new CDKException(error);
            }
            String molFileLine = "";
            while (!molFileLine.endsWith("END PRODUCT")) {
                molFileLine = readLine();
                molFile.append(molFileLine);
                molFile.append("\n");
            };
            
            try {
                // read MDL molfile content
                MDLV3000Reader reader = new MDLV3000Reader(
                  new StringReader(molFile.toString()));
                Molecule product = (Molecule)reader.read(
                  new Molecule());
                  
                // add product
                reaction.addProduct(product);
            } catch (Exception exception) {
                String error = "Error while reading product: " + exception.getMessage();
                logger.error(error);
                logger.debug(exception);
                throw new CDKException(error);
            }
        }
        
        return reaction;
    }

    public boolean isReady() throws CDKException {
        try {
            return input.ready();
        } catch (Exception exception) {
            String error = "Unexpected error while reading file: " + exception.getMessage();
            logger.error(error);
            logger.debug(exception);
            throw new CDKException(error);
        }
    }

    public boolean accepts(ChemObject object) {
        if (object instanceof Reaction) {
            return true;
        } else if (object instanceof ChemModel) {
            return true;
        }
        return false;
    }

    public void close() throws IOException {
        input.close();
    }
    
    private void initIOSettings() {
    }
    
    private void customizeJob() {
    }

    public IOSetting[] getIOSettings() {
        return new IOSetting[0];
    }
    
}
