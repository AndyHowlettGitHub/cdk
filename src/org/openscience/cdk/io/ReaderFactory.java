/* $RCSfile$
 * $Author$
 * $Date$
 * $Revision$
 *
 * Copyright (C) 2001-2003  Jmol Project
 * Copyright (C) 2003-2005  The Chemistry Development Kit (CDK) project
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

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.CharArrayReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringReader;
import java.util.Enumeration;
import java.util.StringTokenizer;
import java.util.Vector;
import java.util.zip.GZIPInputStream;

import org.openscience.cdk.Molecule;
import org.openscience.cdk.exception.InvalidSmilesException;
import org.openscience.cdk.io.formats.*;
import org.openscience.cdk.smiles.SmilesParser;
import org.openscience.cdk.tools.LoggingTool;

/**
 * A factory for creating ChemObjectReaders. The type of reader
 * created is determined from the content of the input. Formats
 * of GZiped files can be detected too.
 *
 * A typical example is:
 * <pre>
 *   StringReader stringReader = "&lt;molecule/>";
 *   ChemObjectReader reader = new ReaderFactory().createReader(stringReader);
 * </pre>
 *
 * @cdk.module io
 *
 * @author  Egon Willighagen <egonw@sci.kun.nl>
 * @author  Bradley A. Smith <bradley@baysmith.com>
 */
public class ReaderFactory {
    
    private final static String IO_FORMATS_LIST = "io-formats.set";

    private int headerLength;
    private LoggingTool logger;

    private static Vector formats = null;

    /**
     * Constructs a ReaderFactory which tries to detect the format in the
     * first 65536 chars.
     */
    public ReaderFactory() {
        this(65536);
    }

    /**
     * Constructs a ReaderFactory which tries to detect the format in the
     * first given number of chars.
     *
     * @param headerLength length of the header in number of chars
     */
    public ReaderFactory(int headerLength) {
        logger = new LoggingTool(this);
        this.headerLength = headerLength;
        loadReaders();
    }

    /**
     * Registers a format for detection.
     */
    public void registerFormat(ChemFormatMatcher format) {
        formats.addElement(format);
    }

    private void loadReaders() {
        if (formats == null) {
            formats = new Vector();
            try {
                logger.debug("Starting loading Readers...");
                BufferedReader reader = new BufferedReader(new InputStreamReader(
                    this.getClass().getClassLoader().getResourceAsStream(IO_FORMATS_LIST)
                ));
                int formatCount = 0;
                while (reader.ready()) {
                    // load them one by one
                    String formatName = reader.readLine();
                    formatCount++;
                    try {
                        ChemFormatMatcher format = (ChemFormatMatcher)this.getClass().getClassLoader().
                            loadClass(formatName).newInstance();
                        formats.addElement(format);
                        logger.info("Loaded IO format: " + format.getClass().getName());
                    } catch (ClassNotFoundException exception) {
                        logger.error("Could not find this ChemObjectReader: ", formatName);
                        logger.debug(exception);
                    } catch (Exception exception) {
                        logger.error("Could not load this ChemObjectReader: ", formatName);
                        logger.debug(exception);
                    }
                }
                logger.info("Number of loaded formats used in detection: ", formatCount);
            } catch (Exception exception) {
                logger.error("Could not load this io format list: ", IO_FORMATS_LIST);
                logger.debug(exception);
            }
        }
    }

    /**
     * Creates a String of the Class name of the <code>ChemObject</code> reader
     * for this file format. The input is read line-by-line
     * until a line containing an identifying string is
     * found.
     *
     * <p>The ReaderFactory detects more formats than the CDK
     * has Readers for.
     *
     * <p>This method is not able to detect the format of gziped files.
     * Use <code>guessFormat(InputStream)</code> instead for such files.
     *
     * @throws IOException  if an I/O error occurs
     * @throws IllegalArgumentException if the input is null
     *
     * @see #guessFormat(InputStream)
     */
    public ChemFormat guessFormat(BufferedReader input) throws IOException {
        if (input == null) {
            throw new IllegalArgumentException("input cannot be null");
        }

        // make a copy of the header
        char[] header = new char[this.headerLength];
        if (!input.markSupported()) {
            logger.error("Mark not supported");
            throw new IllegalArgumentException("input must support mark");
        }
        input.mark(this.headerLength);
        input.read(header, 0, this.headerLength);
        input.reset();
        
        BufferedReader buffer = new BufferedReader(new CharArrayReader(header));
        
        /* Search file for a line containing an identifying keyword */
        String line = buffer.readLine();
        int lineNumber = 1;
        while (buffer.ready() && (line != null)) {
            logger.debug(lineNumber + ": ", line);
            for (int i=0; i<formats.size(); i++) {
                ChemFormatMatcher cfMatcher = (ChemFormatMatcher)formats.elementAt(i);
                if (cfMatcher.matches(lineNumber, line)) {
                    logger.info("Detected format: ", cfMatcher.getFormatName());
                    return cfMatcher;
                }
            }
            line = buffer.readLine();
            lineNumber++;
        }
        
        logger.warn("Now comes the tricky and more difficult ones....");
        buffer = new BufferedReader(new CharArrayReader(header));
        
        line = buffer.readLine();
        // is it a XYZ file?
        StringTokenizer tokenizer = new StringTokenizer(line.trim());
        try {
            int tokenCount = tokenizer.countTokens();
            if (tokenCount == 1) {
                new Integer(tokenizer.nextToken());
                // if not failed, then it is a XYZ file
                return new org.openscience.cdk.io.formats.XYZFormat();
            } else if (tokenCount == 2) {
                new Integer(tokenizer.nextToken());
                if ("Bohr".equalsIgnoreCase(tokenizer.nextToken())) {
                    return new org.openscience.cdk.io.formats.XYZFormat();
                }
            }
        } catch (NumberFormatException exception) {
            logger.info("No, it's not a XYZ file");
        }
        // is it a SMILES file?
        try {
            SmilesParser sp = new SmilesParser();
            Molecule m = sp.parseSmiles(line);
            return new org.openscience.cdk.io.formats.SMILESFormat();
        } catch (Exception ise) {
            // no, it is not
            logger.info("No, it's not a SMILES file");
        }

        logger.warn("File format undetermined");
        return null;
    }
    
    public ChemFormat guessFormat(InputStream input) throws IOException {
        BufferedInputStream bistream = new BufferedInputStream(input, 8192);
        InputStream istreamToRead = bistream; // if gzip test fails, then take default
        bistream.mark(5);
        int countRead = 0;
        try {
            byte[] abMagic = new byte[4];
            countRead = bistream.read(abMagic, 0, 4);
            bistream.reset();
            if (countRead == 4) {
                if (abMagic[0] == (byte)0x1F && abMagic[1] == (byte)0x8B) {
                    istreamToRead = new GZIPInputStream(bistream);
                }
            }
        } catch (IOException exception) {
            logger.error(exception.getMessage());
            logger.debug(exception);
        }
        return guessFormat(new BufferedReader(new InputStreamReader(istreamToRead)));
    }
    
    /**
     * Detects the format of the Reader input, and if known, it will return
     * a CDK Reader to read the format, or null when the reader is not
     * implemented.
     *
     * @return null if CDK does not contain a reader for the detected format.
     *
     * @see #createReader(Reader)
     */
    public ChemObjectReader createReader(InputStream input) throws IOException {
        BufferedInputStream bistream = new BufferedInputStream(input, 8192);
        InputStream istreamToRead = bistream; // if gzip test fails, then take default
        bistream.mark(5);
        int countRead = 0;
        try {
            byte[] abMagic = new byte[4];
            countRead = bistream.read(abMagic, 0, 4);
            bistream.reset();
            if (countRead == 4) {
                if (abMagic[0] == (byte)0x1F && abMagic[1] == (byte)0x8B) {
                    istreamToRead = new GZIPInputStream(bistream);
                }
            }
        } catch (IOException exception) {
            logger.error(exception.getMessage());
            logger.debug(exception);
        }
        return createReader(new InputStreamReader(istreamToRead));
    }
    
    /**
     * Detects the format of the Reader input, and if known, it will return
     * a CDK Reader to read the format. This method is not able to detect the 
     * format of gziped files. Use createReader(InputStream) instead for such 
     * files.
     *
     * @see #createReader(InputStream)
     */
    public ChemObjectReader createReader(Reader input) throws IOException {
        if (!(input instanceof BufferedReader)) {
            input = new BufferedReader(input);
        }
        ChemFormat chemFormat = guessFormat((BufferedReader)input);
        if (chemFormat != null) {
            String readerClassName = chemFormat.getReaderClassName();
            if (readerClassName != null) {
                try {
                    // make a new instance of this class
                    ChemObjectReader coReader = (ChemObjectReader)this.getClass().getClassLoader().
                        loadClass(readerClassName).newInstance();
                    coReader.setReader(input);
                    return coReader;
                } catch (ClassNotFoundException exception) {
                    logger.error("Could not find this ChemObjectReader: ", readerClassName);
                    logger.debug(exception);
                } catch (Exception exception) {
                    logger.error("Could not create this ChemObjectReader: ", readerClassName);
                    logger.debug(exception);
                }
            } else {
                logger.warn("ChemFormat is recognized, but no reader is available.");
            }
        } else {
            logger.warn("ChemFormat is not recognized.");
        }
        return null;
    }

}
