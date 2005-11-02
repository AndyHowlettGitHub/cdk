/* $RCSfile$
 * $Author$ 
 * $Date$
 * $Revision$
 * 
 * Copyright (C) 2005  The Chemistry Development Kit (CDK) project
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
 * Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110-1301 USA.
 */
package org.openscience.cdk.io;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;

import javax.vecmath.Point3d;

import org.openscience.cdk.CDKConstants;
import org.openscience.cdk.interfaces.Atom;
import org.openscience.cdk.interfaces.Bond;
import org.openscience.cdk.interfaces.ChemObject;
import org.openscience.cdk.interfaces.Molecule;
import org.openscience.cdk.exception.CDKException;
import org.openscience.cdk.io.formats.ChemFormat;
import org.openscience.cdk.io.formats.Mol2Format;

/**
 * An output Writer that writes molecular data into the
 * <a href="http://www.tripos.com/data/support/mol2.pdf">Tripos Mol2 format</a>.
 * Writes the atoms and the bonds only at this moment.
 *
 * @cdk.module io
 *
 * @author  Egon Willighagen
 */
public class Mol2Writer extends DefaultChemObjectWriter {

    static BufferedWriter writer;

    /**
    * Constructs a new Mol2 writer.
    * @param out the stream to write the XYZ file to.
    */
    public Mol2Writer(Writer out) {
        if (out instanceof BufferedWriter) {
            writer = (BufferedWriter)out;
        } else {
            writer = new BufferedWriter(out);
        }
    }

    public Mol2Writer(OutputStream input) {
        this(new OutputStreamWriter(input));
    }

    public ChemFormat getFormat() {
        return new Mol2Format();
    }

    /**
     * Flushes the output and closes this object.
     */
    public void close() throws IOException {
        writer.close();
    }

    public void write(ChemObject object) throws CDKException {
        if (object instanceof Molecule) {
            try {
                writeMolecule((Molecule)object);
            } catch(Exception ex) {
                throw new CDKException("Error while writing Mol2 file: " + ex.getMessage(), ex);
            }
        } else {
            throw new CDKException("Mol2Writer only supports output of Molecule classes.");
        }
    }

    /**
     * Writes a single frame in XYZ format to the Writer.
     *
     * @param mol the Molecule to write
     */
    public void writeMolecule(Molecule mol) throws IOException {

        String st = "";
        boolean writecharge = true;

        try {

/*
#        Name: benzene 
#        Creating user name: tom 
#        Creation time: Wed Dec 28 00:18:30 1988 

#        Modifying user name: tom 
#        Modification time: Wed Dec 28 00:18:30 1988
*/

            if (mol.getProperty(CDKConstants.TITLE) != null) {
                writer.write("#        Name: " + mol.getProperty(CDKConstants.TITLE) + "\n");
            }
            // FIXME: add other types of meta data
            writer.newLine();

/*
@<TRIPOS>MOLECULE 
benzene 
12 12 1  0       0 
SMALL 
NO_CHARGES 
*/

            writer.write("@<TRIPOS>MOLECULE\n");
            writer.write(mol.getID() + "\n");
            writer.write(mol.getAtomCount() + " " + 
                        mol.getBondCount() +
                        "\n"); // that's the minimum amount of info required the format
            writer.write("SMALL\n"); // no biopolymer
            writer.write("NO CHARGES\n"); // other options include Gasteiger charges

/*
@<TRIPOS>ATOM 
1       C1      1.207   2.091   0.000   C.ar    1       BENZENE 0.000 
2       C2      2.414   1.394   0.000   C.ar    1       BENZENE 0.000 
3       C3      2.414   0.000   0.000   C.ar    1       BENZENE 0.000 
4       C4      1.207   -0.697  0.000   C.ar    1       BENZENE 0.000 
5       C5      0.000   0.000   0.000   C.ar    1       BENZENE 0.000 
6       C6      0.000   1.394   0.000   C.ar    1       BENZENE 0.000 
7       H1      1.207   3.175   0.000   H       1       BENZENE 0.000 
8       H2      3.353   1.936   0.000   H       1       BENZENE 0.000 
9       H3      3.353   -0.542  0.000   H       1       BENZENE 0.000 
10      H4      1.207   -1.781  0.000   H       1       BENZENE 0.000 
11      H5      -0.939  -0.542  0.000   H       1       BENZENE 0.000 
12      H6      -0.939  1.936   0.000   H       1       BENZENE 0.000 
*/

            // write atom block
            writer.write("@<TRIPOS>ATOM\n");
            Atom[] atoms = mol.getAtoms();
            for (int i=0; i<atoms.length; i++) {
                writer.write(i + " " +
                             atoms[i].getID() + " ");
                if (atoms[i].getPoint3d() != null) {
                    writer.write(atoms[i].getX3d() + " ");
                    writer.write(atoms[i].getY3d() + " ");
                    writer.write(atoms[i].getZ3d() + " ");
                } else if (atoms[i].getPoint2d() != null) {
                    writer.write(atoms[i].getX2d() + " ");
                    writer.write(atoms[i].getY2d() + " ");
                    writer.write(" 0.000 ");
                } else {
                    writer.write("0.000 0.000 0.000 ");
                }
                writer.write(atoms[i].getSymbol()); // FIXME: should use perceived Mol2 Atom Types!
            }

/*
@<TRIPOS>BOND 
1       1       2       ar 
2       1       6       ar 
3       2       3       ar 
4       3       4       ar 
5       4       5       ar 
6       5       6       ar 
7       1       7       1 
8       2       8       1 
9       3       9       1 
10      4       10      1 
11      5       11      1 
12      6       12      1
*/

            // write bond block
            writer.write("@<TRIPOS>BOND\n");
            Bond[] bonds = mol.getBonds();
            for (int i=0; i<bonds.length; i++) {
                writer.write(i + " " +
                             mol.getAtomNumber(bonds[i].getAtomAt(0)) + " " +
                             mol.getAtomNumber(bonds[i].getAtomAt(1)) + " " +
                             ((int)bonds[i].getOrder()) + 
                             "\n");
            } 

        } catch (IOException e) {
            throw e;
        }
    }
}


