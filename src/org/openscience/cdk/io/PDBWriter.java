/* $RCSfile$
 * $Author$
 * $Date$
 * $Revision$
 *
 * Copyright (C) 2000-2003  The Jmol Development Team
 * Copyright (C) 2003-2005  The CDK Project
 *
 * Contact: cdk-devel@lists.sf.net
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

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;

import javax.vecmath.Point3d;
import javax.vecmath.Vector3d;

import org.openscience.cdk.Atom;
import org.openscience.cdk.AtomContainer;
import org.openscience.cdk.ChemFile;
import org.openscience.cdk.ChemModel;
import org.openscience.cdk.interfaces.ChemObject;
import org.openscience.cdk.ChemSequence;
import org.openscience.cdk.Crystal;
import org.openscience.cdk.Molecule;
import org.openscience.cdk.exception.CDKException;
import org.openscience.cdk.geometry.CrystalGeometryTools;
import org.openscience.cdk.io.formats.ChemFormat;
import org.openscience.cdk.io.formats.PDBFormat;
import org.openscience.cdk.tools.manipulator.ChemModelManipulator;

import freeware.PrintfFormat;

/**
 * Saves molecules in a rudimentary PDB format.
 *
 * @cdk.module io
 */
public class PDBWriter extends DefaultChemObjectWriter {

    static BufferedWriter writer;

    /**
     * Creates a PDB writer.
     */
    public PDBWriter (Writer out) {
        writer = new BufferedWriter(out);
    }

    public PDBWriter(OutputStream input) {
        this(new OutputStreamWriter(input));
    }
    
    public ChemFormat getFormat() {
        return new PDBFormat();
    }
    
    public void write(ChemObject object) throws CDKException {
        if (object instanceof Molecule){
            writeMolecule((Molecule)object);
        } else if (object instanceof Crystal){
            writeCrystal((Crystal)object);
        } else if (object instanceof ChemFile){
            ChemFile chemFile = (ChemFile)object;
            org.openscience.cdk.interfaces.ChemSequence sequence = chemFile.getChemSequence(0);
            if (sequence != null) {
            	org.openscience.cdk.interfaces.ChemModel model = sequence.getChemModel(0);
                if (model != null) {
                	org.openscience.cdk.interfaces.Crystal crystal = model.getCrystal();
                    if (crystal != null) {
                        write(crystal);
                    } else {
                        writeMolecule(new Molecule(
                            (AtomContainer)ChemModelManipulator.getAllInOneContainer(model)
                        ));
                    }
                }
            }
        } else {
            throw new CDKException("Only supported is writing of Molecule, Crystal and ChemFile objects.");
        }
    }
    
    public ChemObject highestSupportedChemObject() {
        return new ChemFile();
    }
    
   /**
    * Writes a single frame in PDB format to the Writer.
    *
    * @param molecule the Molecule to write
    */
   public void writeMolecule(Molecule molecule) throws CDKException {
       
       try {
           int na = 0;
           int atomNumber = 1;
           String info = "";
           String st = "";
           String tab = "\t";
           boolean writecharge = false;
           boolean writevect = false;
           
           String hetatmRecordName = "HETATM";
           String terRecordName = "TER";
           PrintfFormat serialFormat = new PrintfFormat("%5d");
           PrintfFormat atomNameFormat = new PrintfFormat("%-4s");
           PrintfFormat positionFormat = new PrintfFormat("%8.3f");
           
           // Loop through the atoms and write them out:
           StringBuffer buffer = new StringBuffer();
           org.openscience.cdk.interfaces.Atom[] atoms = molecule.getAtoms();
           for (int i = 0; i < atoms.length; i++) {
               buffer.setLength(0);
               buffer.append(hetatmRecordName);
               buffer.append(serialFormat.sprintf(atomNumber));
               buffer.append(' ');
               org.openscience.cdk.interfaces.Atom atom = atoms[i];
               buffer.append(atomNameFormat.sprintf(atom.getSymbol()));
               buffer.append(" MOL          ");
               Point3d position = atom.getPoint3d();
               buffer.append(positionFormat.sprintf(position.x));
               buffer.append(positionFormat.sprintf(position.y));
               buffer.append(positionFormat.sprintf(position.z));
               
               writer.write(buffer.toString(), 0, buffer.length());
               writer.newLine();
               ++atomNumber;
           }
           writer.write(terRecordName, 0, terRecordName.length());
           writer.newLine();
       } catch (IOException exception) {
           throw new CDKException("Error while writing file: " + exception.getMessage());
       }
   }
   
   public void writeCrystal(Crystal crystal) throws CDKException {
       try {
           writer.write("HEADER created with CDK fileconvertot\n");
           Vector3d a = crystal.getA();
           Vector3d b = crystal.getB();
           Vector3d c = crystal.getC();
           double[] ucParams = CrystalGeometryTools.cartesianToNotional(a,b,c);
           PrintfFormat lengthFormat = new PrintfFormat("%4.3f");
           PrintfFormat angleFormat = new PrintfFormat("%3.3f");
           writer.write("CRYST1 " + lengthFormat.sprintf(ucParams[0])
                                                   + lengthFormat.sprintf(ucParams[1])
                                                   + lengthFormat.sprintf(ucParams[2])
                                                   + angleFormat.sprintf(ucParams[3])
                                                   + angleFormat.sprintf(ucParams[4])
                                                   + angleFormat.sprintf(ucParams[5]) + "\n");
                                                   
           // before saving the atoms, we need to create cartesian coordinates
           org.openscience.cdk.interfaces.Atom[] atoms = crystal.getAtoms();
            for (int i=0; i<atoms.length; i++) {
            	org.openscience.cdk.interfaces.Atom atom = atoms[i];
                Point3d frac = new Point3d();
                frac.x = atom.getFractX3d();
                frac.y = atom.getFractY3d();
                frac.z = atom.getFractZ3d();
                Point3d cart = CrystalGeometryTools.fractionalToCartesian(a,b,c, frac);
                atom.setPoint3d(cart);
            }
           writeMolecule(new Molecule(crystal));
       } catch (IOException exception) {
           throw new CDKException("Error while writing file: " + exception.getMessage());
       }
   }

   /**
     * Flushes the output and closes this object.
     */
    public void close() throws IOException {
        writer.close();
    }

}
