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
package org.openscience.cdk.applications;

import java.io.*;
import java.util.*;

import org.apache.commons.cli.*;

import org.openscience.cdk.Molecule;
import org.openscience.cdk.SetOfMolecules;
import org.openscience.cdk.io.*;
import org.openscience.cdk.io.iterator.IteratingMDLReader;
import org.openscience.cdk.io.listener.PropertiesListener;
import org.openscience.cdk.tools.LoggingTool;

import org.openscience.cdk.qsar.DescriptorEngine;
import org.openscience.cdk.qsar.DescriptorSpecification;
import org.openscience.cdk.qsar.DescriptorValue;
import org.openscience.cdk.qsar.result.*;


/**
 * Command line utility that calculates QSAR descriptor values.
 *
 * @cdk.module applications
 *
 * @author      Egon Willighagen
 * @cdk.require java1.4
 * @cdk.created 2004-12-02
 * @cdk.keyword descriptor calculation
 */
public class DescriptorCalculator {

    private LoggingTool logger;
    
    private static boolean firstTime;
    private boolean inputIsSMILES;
    private String outputFormat = null;
    private String suffix = null;
    private String[] descTypes = null;
    private PropertiesListener propsListener;
    private DescriptorEngine engine;

    public DescriptorCalculator() {
        logger = new LoggingTool();
        LoggingTool.configureLog4j();
        logger.dumpSystemProperties();

        inputIsSMILES = false;
        outputFormat = "cml";
        suffix = ".cml";
        firstTime = true;
        
        Properties props = new Properties();
        props.setProperty("CMLIDs", "false");
        props.setProperty("NamespacedOutput", "false");
        props.setProperty("XMLDeclaration", "false");
        propsListener = new PropertiesListener(props);
    }

    private void initEngine() {
        if (descTypes != null) {
            engine = new DescriptorEngine(descTypes);
        } else {
            engine = new DescriptorEngine();
        }
    }

    public static void main(String[] args) {
        DescriptorCalculator calculator = new DescriptorCalculator();

        // process options
        String fileToProcess = calculator.parseCommandLineOptions(args);
        
        // create the engine specifying which descriptors to calculate
        calculator.initEngine();

        // calculate descriptors
        calculator.process(fileToProcess);
    }

    private void printCMLHeader(Writer writer) throws IOException {
        writer.write("<?xml version=\"1.0\"?>\n");
        writer.write("<list\n");
        writer.write("  xmlns=\"http://www.xml-cml.org/schema/cml2/core\"\n");
        writer.write("  xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\"\n");
        writer.write("  xsi:schemaLocation=\"http://www.xml-cml.org/schema/cml2/core cmlAll4.4.xsd\">\n");
        writer.flush();
    }

    private void printCMLMolecule(Writer writer, Molecule molecule) throws Exception {
        logger.info("Writing output in CML format");
        StringWriter stringWriter = new StringWriter();
        CMLWriter cmlWriter = new CMLWriter(stringWriter);
        cmlWriter.addChemObjectIOListener(propsListener);
        cmlWriter.write(molecule);
        cmlWriter.close();
        writer.write(stringWriter.toString());
        writer.flush();
    }
    private void printTXTMolecule(Writer writer, Molecule molecule) throws Exception {
        logger.info("Writing output in TXT format");
        String headerLine = "";
        StringWriter stringWriter = new StringWriter();
        List specList = engine.getDescriptorSpecifications();

        for (Iterator it = specList.iterator(); it.hasNext();) {
            DescriptorSpecification spec = (DescriptorSpecification)it.next();
            String title = spec.getImplementationTitle();

            // the title contains the full class path. We just need
            // the last component
            String[] comps = title.split("\\.");
            title = comps[ comps.length-1 ];

            DescriptorValue value = (DescriptorValue)molecule.getProperty(spec);
            DescriptorResult result = value.getValue();
            if (result instanceof DoubleResult) {
                stringWriter.write(((DoubleResult)result).doubleValue()+" ");
                if (firstTime) headerLine = headerLine + title + " ";
            } else if (result instanceof IntegerResult) {
                stringWriter.write(((IntegerResult)result).intValue()+" ");
                if (firstTime) headerLine = headerLine + title + " ";
            } else if (result instanceof BooleanResult) {
                stringWriter.write(((BooleanResult)result).booleanValue()+" ");
                if (firstTime) headerLine = headerLine + title + " ";
            } else if (result instanceof DoubleArrayResult) {
                for (int i = 0; i < ((DoubleArrayResult)result).size(); i++) {
                    stringWriter.write(((DoubleArrayResult)result).get(i)+" ");
                    if (firstTime) headerLine = headerLine + title + "." + i + " ";
                }
            } else if (result instanceof IntegerArrayResult) {
                for (int i = 0; i < ((IntegerArrayResult)result).size(); i++) {
                    stringWriter.write(((IntegerArrayResult)result).get(i)+" ");
                    if (firstTime) headerLine = headerLine + title + "." + i + " ";
                }
            }
        }
        if (firstTime) {
            writer.write(headerLine+"\n");
            firstTime = false;
        }
        writer.write(stringWriter.toString()+"\n");
        writer.flush();
    }
    
    private void processMolecule(Writer writer, Molecule molecule) throws Exception {
        boolean engineError = false;
        try {
            engine.process(molecule);
        } catch (Exception exception) {
            logger.error("Exception while generating descriptors for molecule: ", exception.getMessage());
            logger.debug(exception);
            engineError = true;
        }
        if (!engineError) {
            if (outputFormat.equals("cml")) printCMLMolecule(writer, molecule);
            else printTXTMolecule(writer,molecule);
            if (!inputIsSMILES) System.out.print(".");
        } else {
            if (!inputIsSMILES) System.out.print("x");
        }
    }
    
    public void process(String toProcess) {
        try {
            Writer writer;
            if (inputIsSMILES) {
                writer = new OutputStreamWriter(System.out);
            } else {
                writer = new FileWriter(new File(toProcess + suffix));
            }

            if (outputFormat.equals("cml")) printCMLHeader(writer);
            
            if (inputIsSMILES) {
                SMILESReader reader = new SMILESReader(
                    new StringReader(toProcess)
                );
                SetOfMolecules moleculeSet = (SetOfMolecules)reader.read(new SetOfMolecules());
                Molecule[] molecules = moleculeSet.getMolecules();
                for (int i=0; i<molecules.length; i++) {
                    processMolecule(writer, molecules[i]);
                }
            } else {
                IteratingMDLReader reader = new IteratingMDLReader(
                    new FileReader(new File(toProcess))
                );
                while (reader.hasNext()) {
                    Molecule molecule = (Molecule)reader.next();
                    processMolecule(writer, molecule);
                }
            }
            
            if (outputFormat.equals("cml")) printCMLFooter(writer);
            if (!inputIsSMILES) System.out.println("\n");
        } catch (FileNotFoundException exception) {
            logger.debug(exception);
            System.err.println("File not found: " + toProcess);
            System.exit(-1);
        } catch (IOException exception) {
            logger.debug(exception);
            System.err.println("IO exception: " + exception.getMessage());
            exception.printStackTrace();
            System.exit(-1);
        } catch (Exception exception) {
            logger.debug(exception);
            System.err.println("Some exception: " + exception.getMessage());
            exception.printStackTrace();
            System.exit(-1);
        }
    }
        
    private void printCMLFooter(Writer writer) throws IOException {
        writer.write("</list>\n");
        writer.close();
    }

    /**
     * Parses the options in the command line arguments and returns
     * the index of the first non-option argument.
     */
    private String parseCommandLineOptions(String[] args) {

        Options options = new Options();
        options.addOption("h", "help", false, "give this help page");
        options.addOption("s","smiles", false, "input one SMILES string");
        options.addOption("t","type",true,
                "specify which types of descriptor to calculate (comma seperated list). "+
                "Possible values are: constitutional, molecular, topological, geometrical, electronic");
        options.addOption("o","output",true,
                "Format in which to output descriptors. Options are 'cml' or'txt' to"+
                "indicate CML output or comma seperated text");
 
        CommandLine line = null;
        try {
            CommandLineParser parser = new PosixParser();
            line = parser.parse(options, args);
        } catch (ParseException exception) {
            System.err.println("Unexpected exception: " + exception.toString());
        }

        if (line.hasOption("s") || line.hasOption("smiles")) {
            inputIsSMILES = true;
        } 
        if (line.hasOption("t") || line.hasOption("type")) {
            String optvalue = line.getOptionValue("t");
            descTypes = optvalue.split(",");
        }
        if (line.hasOption("o") || line.hasOption("output")) {
            String optvalue = line.getOptionValue("o");
            if (!optvalue.equals("cml") && !optvalue.equals("txt")) {
                System.out.println("Invalid output format");
                printHelp(options);
            }
            outputFormat = optvalue;
            if (outputFormat.equals("txt")) suffix = ".txt";
        }
    
        String[] filesToConvert = line.getArgs();
        if (filesToConvert.length != 1 || line.hasOption("h") || line.hasOption("help")) {
            printHelp(options);
        }         
        return filesToConvert[0];
    }

    private void printHelp(Options options) {
        HelpFormatter formatter = new HelpFormatter();
        formatter.printHelp("DescriptorCalculator", options);
        
        // now report on the supported formats
        System.out.println();
        System.out.println(" OUTPUT FORMATS:");
        System.out.println("  cml    Chemical Markup Language (the default)");
        System.out.println("  txt    Space seperated text");
        
        System.exit(0);
    }
}
