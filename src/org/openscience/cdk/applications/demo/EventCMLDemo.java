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
 */
package org.openscience.cdk.applications.demo;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.util.Properties;
import java.util.Vector;

import javax.vecmath.Vector2d;

import org.apache.commons.cli.Options;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.PosixParser;
import org.apache.commons.cli.OptionBuilder;
import org.apache.commons.cli.ParseException;
import org.apache.commons.cli.HelpFormatter;

import org.openscience.cdk.Atom;
import org.openscience.cdk.AtomContainer;
import org.openscience.cdk.ChemFile;
import org.openscience.cdk.ChemModel;
import org.openscience.cdk.ChemObject;
import org.openscience.cdk.ChemSequence;
import org.openscience.cdk.Crystal;
import org.openscience.cdk.Molecule;
import org.openscience.cdk.PseudoAtom;
import org.openscience.cdk.SetOfMolecules;
import org.openscience.cdk.exception.CDKException;
import org.openscience.cdk.graph.rebond.RebondTool;
import org.openscience.cdk.io.*;
import org.openscience.cdk.io.iterator.event.EventCMLReader;
import org.openscience.cdk.io.setting.IOSetting;
import org.openscience.cdk.layout.StructureDiagramGenerator;
import org.openscience.cdk.io.listener.*;
import org.openscience.cdk.config.AtomTypeFactory;
import org.openscience.cdk.tools.manipulator.ChemFileManipulator;
import org.openscience.cdk.tools.HydrogenAdder;
import org.openscience.cdk.tools.IDCreator;
import org.openscience.cdk.tools.LoggingTool;
import org.openscience.cdk.tools.SaturationChecker;
import org.openscience.cdk.test.CDKTestCase;

/**
 * Demo that shows how to use the EvenCMLReader.
 *
 * @cdk.module applications
 *
 * @author Egon Willighagen <egonw@sci.kun.nl>
 *
 * @cdk.keyword      command line util
 * @cdk.keyword      file format
 * @cdk.builddepends commons-cli-1.0.jar
 */
public class EventCMLDemo {

    private LoggingTool logger;
    private EventCMLReader cor;

    public EventCMLDemo() {
        logger = new LoggingTool(this);
        LoggingTool.configureLog4j();
    }

    /**
     * Convert the file <code>ifilename</code>.
     *
     * @param ifilename name of input file
     */
    public boolean process(String ifilename) {
        boolean success = false;
        try {
            File file = new File(ifilename);
            if (file.isFile()) {
                ReaderListener listener = new CMLMolReadListener();
                cor = new EventCMLReader(
                    new FileReader(file), listener
                );
                if (cor == null) {
                    logger.warn("The format of the input file is not recognized or not supported.");
                    System.err.println("The format of the input file is not recognized or not supported.");
                    return false;
                }

                cor.process();
                success = true;
            } else {
                System.err.println("Argument is not a file: " + ifilename);
                return false;
            }
        } catch (FileNotFoundException exception) {
            System.out.println("File " + ifilename + " does not exist!");
        } catch (Exception exception) {
            logger.debug(exception);
        }
        return success;
    }

    /**
     * actual program
     */
    public static void main(String[] args) {
        EventCMLDemo demo = new EventCMLDemo();

        // process options
        String[] filesToConvert = demo.parseCommandLineOptions(args);

        // do conversion(s)
        for (int i=0; i < filesToConvert.length; i++) {
            String inputFilename = filesToConvert[i];
            System.out.print("Processing " + inputFilename + " ... ");
            boolean success = demo.process(inputFilename);
            if (success) {
                System.out.println("succeeded!");
            } else {
                System.out.println("failed!");
            }
        }
    }

    // PRIVATE INTERNAL STUFF

    private void printHelp(Options options) {
        HelpFormatter formatter = new HelpFormatter();
        formatter.printHelp("EventCMLDemo", options);
        
        System.exit(0);
    }
    
    /**
     * Parses the options in the command line arguments and returns
     * the index of the first non-option argument.
     */
    private String[] parseCommandLineOptions(String[] args) {

        Options options = new Options();
        options.addOption("h", "help", false, "give this help page");
        
        CommandLine line = null;
        try {
            CommandLineParser parser = new PosixParser();
            line = parser.parse(options, args);
        } catch (ParseException exception) {
            System.err.println("Unexpected exception: " + exception.toString());
        }
    
        String[] filesToConvert = line.getArgs();
        
        if (filesToConvert.length == 0 || line.hasOption("h")) {
            printHelp(options);
        }
        
        return filesToConvert;
    }

    class CMLMolReadListener implements ReaderListener {
        
        final int LIMIT = 50;
        
        int counter;
        int counter2;
        
        int atomCount;
        
        public CMLMolReadListener() {
            counter = 0;
            counter2 = 0;
            atomCount = 0;
        }
        
        public void frameRead(ReaderEvent event) {
            System.out.print(".");
            counter++;
            AtomContainer mol = ((EventCMLReader)event.getSource()).getAtomContainer();
            atomCount += mol.getAtomCount();
            if (counter == LIMIT) {
                System.out.println(" " + atomCount + " atoms processed");
                counter = 0;
                counter2++;
                System.out.print(counter2*LIMIT + " ");
            }
        }
        
        public void processIOSettingQuestion(IOSetting setting) {};

    }
    
}
