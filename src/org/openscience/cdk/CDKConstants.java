/* $RCSfile$
 * $Author$    
 * $Date$    
 * $Revision$
 *
 * Copyright (C) 1997-2004  The Chemistry Development Kit (CDK) project
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
 * 
 */

package org.openscience.cdk;

/**
 * An interface providing predefined values for a number of
 * constants used throughout the CDK. Classes using these constants should
 * <b>not</b> implement this interface, but use it like:
 * <pre>
 *   double singleBondOrder = CDKConstants.BONDORDER_SINGLE;
 * </pre>
 *
 * <p>The lazyCreation patch has been applied to this class.
 *
 * @cdk.module core
 *
 * @cdk.keyword bond order
 * @cdk.keyword stereochemistry
 */
public class CDKConstants {

        /** A bond of degree 1.0. */
        public final static double BONDORDER_SINGLE = 1.0;

        /** A bond of degree 1.5. */
        public final static double BONDORDER_AROMATIC = 1.5;

        /** A bond of degree 2.0. */
        public final static double BONDORDER_DOUBLE = 2.0;

        /** A bond of degree 3.0. */
        public final static double BONDORDER_TRIPLE = 3.0;

        /** A bonds which end is above the drawing plane. */
        public final static int STEREO_BOND_UP = 1;
        /** A bonds which start is above the drawing plane. */
        public final static int STEREO_BOND_UP_INV = 2;

        /** A bonds for which the stereochemistry is undefined. */
        public final static int STEREO_BOND_UNDEFINED = 4;

        /** A bonds for which there is no stereochemistry. */
        public final static int STEREO_BOND_NONE = 0;

        /** A bonds which end is below the drawing plane.
         *  The bond is draw from the first to the second bond atom.
         */
        public final static int STEREO_BOND_DOWN = -1;
        /** A bonds which end is below the drawing plane.
         *  The bond is draw from the second to the first bond atom.
         */
        public final static int STEREO_BOND_DOWN_INV = -2;

        /** A positive atom parity. */
        public final static int STEREO_ATOM_PARITY_PLUS = 1;
        /** A negative atom parity. */
        public final static int STEREO_ATOM_PARITY_MINUS = -1;
        /** A undefined atom parity. */
        public final static int STEREO_ATOM_PARITY_UNDEFINED = 0;

        /** A undefined hybridization. */
        public final static int HYBRIDIZATION_UNSET = 0;
        /** A geometry of neighboring atoms when an s orbital is hybridized
         *  with one p orbital. */
        public final static int HYBRIDIZATION_SP1 = 1;
        /** A geometry of neighboring atoms when an s orbital is hybridized
         *  with two p orbitals. */
        public final static int HYBRIDIZATION_SP2 = 2;
        /** A geometry of neighboring atoms when an s orbital is hybridized
         *  with three p orbitals. */
        public final static int HYBRIDIZATION_SP3 = 3;

        /** 
         * Carbon NMR shift contant for use as a key in the
         * ChemObject.physicalProperties hashtable.
         * @see org.openscience.cdk.ChemObject
         */
        public final static String NMRSHIFT_CARBON = "carbon nmr shift";

        /** Hydrogen NMR shift contant for use as a key in the
          * ChemObject.physicalProperties hashtable.
          * @see org.openscience.cdk.ChemObject
          */
        public final static String NMRSHIFT_HYDROGEN = "hydrogen nmr shift";

        /** Nitrogen NMR shift contant for use as a key in the
          * ChemObject.physicalProperties hashtable.
          * @see org.openscience.cdk.ChemObject
          */
        public final static String NMRSHIFT_NITROGEN = "nitrogen nmr shift";

        /** Phosphorus NMR shift contant for use as a key in the
          * ChemObject.physicalProperties hashtable.
          * @see org.openscience.cdk.ChemObject
          */
        public final static String NMRSHIFT_PHOSPORUS = "phosphorus nmr shift";

        /** Fluorine NMR shift contant for use as a key in the
          * ChemObject.physicalProperties hashtable.
          * @see org.openscience.cdk.ChemObject
          */
        public final static String NMRSHIFT_FLUORINE = "fluorine nmr shift";

        /** Deuterium NMR shift contant for use as a key in the
          * ChemObject.physicalProperties hashtable.
          * @see org.openscience.cdk.ChemObject
          */
        public final static String NMRSHIFT_DEUTERIUM = "deuterium nmr shift";


        /****************************************
         * Some predefined flags - keep the     *
         * numbers below 50 free for other      *
         * purposes                             *
         ****************************************/

        /** Flag that is set if the chemobject is placed (somewhere).
         */
        public final static int ISPLACED = 0;
        /** Flag that is set when the chemobject is part of a ring.
         */
        public final static int ISINRING = 1;
        /** Flag that is set if a chemobject is part of an alipahtic chain.
         */
        public final static int ISALIPHATIC = 2;
        /** Flag is set if chemobject has been visited.
         */
        public final static int VISITED = 3; // Use in tree searches
        /** Flag is set if chemobject is part of an aromatic system. */
        public final static int ISAROMATIC = 4;
        /** Flag is set if a chemobject is mapped to another chemobject.
         *  It is used for example in subgraph isomorphism search.
         */
        public final static int MAPPED = 5;

    /**
     * Maximum flags array index.
     */
    public final static int MAX_FLAG_INDEX = 5;

    /**
     * Flag used for JUnit testing the pointer functionality.
     */
    public final static int DUMMY_POINTER = 1;

    /**
     * Maximum pointers array index.
     */
    public final static int MAX_POINTER_INDEX = 1;
    
    /****************************************
    * Some predefined property names for    *
    * ChemObjects                           *
    ****************************************/

    /** The title for a ChemObject. */
    public static final String TITLE = "Title";

    /** A remark for a ChemObject.*/
    public static final String REMARK = "Remark";

    /** A String comment. */
    public static final String COMMENT = "Comment";

    /** A List of names. */
    public static final String NAMES = "Names";

    /** A List of annotation remarks. */
    public static final String ANNOTATIONS = "Annotations";

    /** A description for a ChemObject. */
    public static final String DESCRIPTION = "Description";


    /****************************************
    * Some predefined property names for    *
    * Molecules                             *
    ****************************************/

    /** The IUPAC compatible name generated with AutoNom. */
    public static final String AUTONOMNAME = "AutonomName";

    /** The Beilstein Registry Number. */
    public static final String BEILSTEINRN = "BeilsteinRN";

    /** The CAS Registry Number. */
    public static final String CASRN = "CasRN";

    /****************************************
    * Some predefined property names for    *
    * Atoms                                 *
    ****************************************/

    /** The Isotropic Shielding, usually calculated by
      * a quantum chemistry program like Gaussian.
      * This is a property used for calculating NMR chemical
      * shifts by subtracting the value from the 
      * isotropic shielding value of a standard (e.g. TMS).
      */
    public static final String ISOTROPIC_SHIELDING = "IsotropicShielding";

    /* The next statics are used to store PDB information as Atom properties. */
    
    /** Full record from PDB file. **/
    public static final String PDB_RECORD     = "pdb.record";
    /** PDB field for serial number?. **/
    public static final String PDB_SERIAL     = "pdb.serial";
    /** PDB field for the name of what?. **/
    public static final String PDB_NAME       = "pdb.name";
    /** PDB field for alternative location. **/
    public static final String PDB_ALTLOC     = "pdb.altLoc";
    /** PDB field for the residue name of the atom. **/
    public static final String PDB_RESNAME    = "pdb.resName";
    /** PDB field for the chain ID. **/
    public static final String PDB_CHAINID    = "pdb.chainID";
    /** PDB field for the residue sequence number. **/
    public static final String PDB_RESSEQ     = "pdb.resSeq";
    /** PDB field for the insertion code. **/
    public static final String PDB_ICODE      = "pdb.iCode";
    /** PDB field for the atomic occupancy. **/
    public static final String PDB_OCCOPANCY  = "pdb.occupancy";
    /** PDB field for the atomic temperature factor. **/
    public static final String PDB_TEMPFACTOR = "pdb.tempFactor";
    /** PDB field for the segment ID. **/
    public static final String PDB_SEGID      = "pdb.segID";
    /** PDB field for the element symbol?. **/
    public static final String PDB_ELEMENT    = "pdb.element";
    /** PDB field for the atomic partial? charge. **/
    public static final String PDB_CHARGE     = "pdb.charge";
}


