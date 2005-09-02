/* $RCSfile$
 * $Author$
 * $Date$
 * $Revision$
 *
 * Copyright (C) 2001-2005  The Chemistry Development Kit (CDK) project
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
 */
package org.openscience.cdk.io;

import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.Enumeration;
import java.util.Vector;

import javax.vecmath.Point3d;
import javax.vecmath.Vector3d;

import org.openscience.cdk.Atom;
import org.openscience.cdk.interfaces.ChemObject;
import org.openscience.cdk.Crystal;
import org.openscience.cdk.exception.CDKException;
import org.openscience.cdk.geometry.CrystalGeometryTools;
import org.openscience.cdk.io.formats.ChemFormat;
import org.openscience.cdk.io.formats.ShelXFormat;
import org.openscience.cdk.tools.MFAnalyser;

import freeware.PrintfFormat;

/**
 * <p>Serializes a SetOfMolecules or a Molecule object to ShelX code.
 * The output can be read with Platon.
 *
 * @cdk.module io
 *
 * @author Egon Willighagen
 *
 * @cdk.keyword file format, ShelX
 */
public class ShelXWriter extends DefaultChemObjectWriter {

    private Writer output;

    /**
     * Constructs a new ShelXWriter class. Output will be stored in the Writer
     * class given as parameter.
     *
     * @param out Writer to redirect the output to.
     */
    public ShelXWriter(Writer out) {
        output = out;
    }

    public ShelXWriter(OutputStream input) {
        this(new OutputStreamWriter(input));
    }
    
    public ChemFormat getFormat() {
        return new ShelXFormat();
    }
    
    /**
     * Flushes the output and closes this object
     */
    public void close() throws IOException {
        output.close();
    }

    /**
     * Serializes the ChemObject to ShelX and redirects it to the output Writer.
     *
     * @param object A Molecule of SetOfMolecules object
     */
    public void write(ChemObject object) throws CDKException {
        if (object instanceof Crystal) {
            write((Crystal)object);
        } else {
            throw new CDKException("Only Crystal objects can be read.");
        }
    };

    public ChemObject highestSupportedChemObject() {
        return new Crystal();
    }

    // Private procedures

    private void write(Crystal crystal) {
        write("TITLE Produced with CDK (http://cdk.sf.net/)\n");
        Vector3d a = crystal.getA();
        Vector3d b = crystal.getB();
        Vector3d c = crystal.getC();
        double alength = a.length();
        double blength = b.length();
        double clength = c.length();
        double alpha = b.angle(c);
        double beta  = a.angle(c);
        double gamma = a.angle(b);
        PrintfFormat format = new PrintfFormat("%7.5lf");
        write("CELL " + format.sprintf(1.54184) + "   ");
        format = new PrintfFormat("%8.5lf");
        write(format.sprintf(alength) + "  ");
        write(format.sprintf(blength) + "  ");
        write(format.sprintf(clength) + " ");
        format = new PrintfFormat("%8.4lf");
        write(format.sprintf(alpha) + " ");
        write(format.sprintf(beta) + " ");
        write(format.sprintf(gamma) + "\n");
        format = new PrintfFormat("%1.5lf");
        write("ZERR " + format.sprintf((double)crystal.getZ()) +
              "    0.01000  0.01000   0.01000   0.0100   0.0100   0.0100\n");
        String spaceGroup = crystal.getSpaceGroup();
        if ("P1".equals(spaceGroup)) {
            write("LATT  -1\n");
        } else if ("P 2_1 2_1 2_1".equals(spaceGroup)) {
            write("LATT  -1\n");
            write("SYMM  1/2+X   , 1/2-Y   ,    -Z\n");
            write("SYMM     -X   , 1/2+Y   , 1/2-Z\n");
            write("SYMM  1/2-X   ,    -Y   , 1/2+Z\n");
        }
        MFAnalyser mfa = new MFAnalyser(crystal);
        String elemNames = "";
        String elemCounts = "";
        Vector asortedElements = mfa.getElements();
        Enumeration elements = asortedElements.elements();
        while (elements.hasMoreElements()) {
            String symbol = (String)elements.nextElement();
            elemNames += symbol + "    ".substring(symbol.length());
            String countS = new Integer(mfa.getAtomCount(symbol)).toString();
            elemCounts += countS + "    ".substring(countS.length());
        }
        write("SFAC  " + elemNames + "\n");
        write("UNIT  " + elemCounts + "\n");
        /* write atoms */
        format = new PrintfFormat("%7.5lf");
        for (int i = 0; i < crystal.getAtomCount(); i++) {
        	org.openscience.cdk.interfaces.Atom atom = crystal.getAtomAt(i);
            Point3d cartCoord = atom.getPoint3d();
            Point3d fracCoord = CrystalGeometryTools.cartesianToFractional(a, b, c, cartCoord);
            String symbol = atom.getSymbol();
            String output = symbol + (i+1);
            write(output);
            for (int j=1; j<5 - output.length(); j++) {
                write(" ");
            }
            write("     ");
            String elemID = new Integer(asortedElements.indexOf(symbol)+1).toString();
            write(elemID);
            write("    ".substring(elemID.length()));
            write(format.sprintf(fracCoord.x) + "   ");
            write(format.sprintf(fracCoord.y) + "   ");
            write(format.sprintf(fracCoord.z) + "    11.00000    0.05000\n");
        }
        write("END\n");
    }

    private void write(String s) {
        try {
            output.write(s);
        } catch (IOException e) {
            System.err.println("CMLWriter IOException while printing \"" +
                                s + "\":\n" + e.toString());
        }
    }

}
