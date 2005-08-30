/*  $RCSfile$
 *  $Author$
 *  $Date$
 *  $Revision$
 *
 *  Copyright (C) 2003-2005  The Chemistry Development Kit (CDK) project
 *
 *  Contact: cdk-devel@lists.sourceforge.net
 *
 *  This program is free software; you can redistribute it and/or
 *  modify it under the terms of the GNU Lesser General Public License
 *  as published by the Free Software Foundation; either version 2.1
 *  of the License, or (at your option) any later version.
 *  All we ask is that proper credit is given for our work, which includes
 *  - but is not limited to - adding the above copyright notice to the beginning
 *  of your source code files, and to any copyright notice that you may distribute
 *  with programs based on this work.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU Lesser General Public License for more details.
 *
 *  You should have received a copy of the GNU Lesser General Public License
 *  along with this program; if not, write to the Free Software
 *  Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 *
 */
package org.openscience.cdk.tools;

import java.io.IOException;
import java.util.HashMap;

import org.openscience.cdk.Atom;
import org.openscience.cdk.interfaces.AtomContainer;
import org.openscience.cdk.Bond;
import org.openscience.cdk.interfaces.Isotope;
import org.openscience.cdk.Molecule;
import org.openscience.cdk.SetOfMolecules;
import org.openscience.cdk.config.IsotopeFactory;
import org.openscience.cdk.exception.CDKException;
import org.openscience.cdk.geometry.GeometryTools;
import org.openscience.cdk.graph.ConnectivityChecker;

/**
 * Provides methods for adding missing hydrogen atoms.
 *
 * <p>An example:
 * <pre>
 *   Molecule methane = new Molecule();
 *   Atom carbon = new Atom("C");
 *   methane.addAtom(carbon);
 *   HydrogenAdder adder = new HydrogenAdder();
 *   adder.addImplicitHydrogensToSatisfyValency(methane);
 *   int atomCount = methane.getAtomCount(); // = 1
 * </pre>
 * As the example shows, this only adjusts the hydrogenCount
 * on the carbon.
 *
 * <p>If you want to add the hydrogens as separate atoms, you
 * need to do:
 * <pre>
 *   Molecule methane = new Molecule();
 *   Atom carbon = new Atom("C");
 *   methane.addAtom(carbon);
 *   HydrogenAdder adder = new HydrogenAdder();
 *   adder.addExplicitHydrogensToSatisfyValency(methane);
 *   int atomCount = methane.getAtomCount(); // = 5
 * </pre>
 *
 * <p>If you want to add the hydrogens to a specific atom only,
 * use this example:
 * <pre>
 *   Molecule ethane = new Molecule();
 *   Atom carbon1 = new Atom("C");
 *   Atom carbon2 = new Atom("C");
 *   ethane.addAtom(carbon1);
 *   ethane.addAtom(carbon2);
 *   HydrogenAdder adder = new HydrogenAdder();
 *   adder.addExplicitHydrogensToSatisfyValency(ethane, carbon1);
 *   int atomCount = ethane.getAtomCount(); // = 5
 * </pre>
 *
 * @cdk.keyword    hydrogen, adding
 */
public class HydrogenAdder {

    private LoggingTool logger;
    private ValencyCheckerInterface valencyChecker;

    /**
     * Creates a tool to add missing hydrogens using the SaturationChecker class.
     * 
     * @see org.openscience.cdk.tools.SaturationChecker
     */
    public HydrogenAdder() {
        this("org.openscience.cdk.tools.SaturationChecker");
    }
    
    /**
     * Creates a tool to add missing hydrogens using a ValencyCheckerInterface.
     * 
     * @see org.openscience.cdk.tools.ValencyCheckerInterface
     */
    public HydrogenAdder(String valencyCheckerInterfaceClassName) {
        logger = new LoggingTool(this);
        try {
            if (valencyCheckerInterfaceClassName.equals("org.openscience.cdk.tools.ValencyChecker")) {
                valencyChecker = new ValencyChecker();
            } else if (valencyCheckerInterfaceClassName.equals("org.openscience.cdk.tools.SaturationChecker")) {
                valencyChecker = new SaturationChecker();
            } else {
                logger.error("Cannot instantiate unknown ValencyCheckerInterface; using SaturationChecker");
                valencyChecker = new SaturationChecker();
            }
        } catch (Exception exception) {
            logger.error("Could not intantiate a SaturationChecker.");
            logger.debug(exception);
        }
    }
    
    /**
     * Creates a tool to add missing hydrogens using a ValencyCheckerInterface.
     * 
     * @see org.openscience.cdk.tools.ValencyCheckerInterface
     */
    public HydrogenAdder(ValencyCheckerInterface valencyChecker) {
        logger = new LoggingTool(this);
        this.valencyChecker = valencyChecker;
    }

    /**
     * Method that saturates a molecule by adding explicit hydrogens.
     * In order to get coordinates for these Hydrogens, you need to 
     * remember the average bondlength of you molecule (coordinates for 
     * all atoms should be available) by using
     * double bondLength = GeometryTools.getBondLengthAverage(atomContainer);
     * and then use this method here and then use
     * org.openscience.cdk.HydrogenPlacer(atomContainer, bondLength);
     *
     * @param  molecule  Molecule to saturate
     * @return 
     * @cdk.keyword          hydrogen, adding
     * @cdk.keyword          explicit hydrogen
     */
    public AtomContainer addHydrogensToSatisfyValency(Molecule molecule) throws IOException, ClassNotFoundException, CDKException
    {
	    logger.debug("Start of addHydrogensToSatisfyValency");
        AtomContainer changedAtomsAndBonds = addExplicitHydrogensToSatisfyValency(molecule);
	logger.debug("End of addHydrogensToSatisfyValency");
    return changedAtomsAndBonds;
    }

    /**
     * Method that saturates a molecule by adding explicit hydrogens.
     * In order to get coordinates for these Hydrogens, you need to 
     * remember the average bondlength of you molecule (coordinates for 
     * all atoms should be available) by using
     * double bondLength = GeometryTools.getBondLengthAverage(atomContainer);
     * and then use this method here and then use
     * org.openscience.cdk.HydrogenPlacer(atomContainer, bondLength);
     *
     * @param  molecule  Molecule to saturate
     * @return 
     * @cdk.keyword          hydrogen, adding
     * @cdk.keyword          explicit hydrogen
     */
    public AtomContainer addExplicitHydrogensToSatisfyValency(Molecule molecule) throws IOException, ClassNotFoundException, CDKException
    {
	    logger.debug("Start of addExplicitHydrogensToSatisfyValency");
      SetOfMolecules moleculeSet = ConnectivityChecker.partitionIntoMolecules(molecule);
      Molecule[] molecules = moleculeSet.getMolecules();
      AtomContainer changedAtomsAndBonds = new org.openscience.cdk.AtomContainer();
      AtomContainer intermediateContainer= null;
      for (int k = 0; k < molecules.length; k++) {
        Molecule molPart = molecules[k];
        Atom[] atoms = molPart.getAtoms();
         for (int i = 0; i < atoms.length; i++) {
            intermediateContainer = addHydrogensToSatisfyValency(molPart, atoms[i], molecule);
            changedAtomsAndBonds.add(intermediateContainer);
        }
       
      }
      logger.debug("End of addExplicitHydrogensToSatisfyValency");
      return changedAtomsAndBonds;
    }

    /**
     * Method that saturates an atom in a molecule by adding explicit hydrogens.
     * In order to get coordinates for these Hydrogens, you need to 
     * remember the average bondlength of you molecule (coordinates for 
     * all atoms should be available) by using
     * double bondLength = GeometryTools.getBondLengthAverage(atomContainer);
     * and then use this method here and then use
     * org.openscience.cdk.HydrogenPlacer(atomContainer, bondLength);
     *
     * @param  atom      Atom to saturate
     * @param  container AtomContainer containing the atom
     * @param  totalContainer In case you have a container containing multiple structures, this is the total container, whereas container is a partial structure
     * @return 
     *
     * @cdk.keyword          hydrogen, adding
     * @cdk.keyword          explicit hydrogen
     *
     * @deprecated
     */
    public AtomContainer addHydrogensToSatisfyValency(AtomContainer container, org.openscience.cdk.interfaces.Atom atom, AtomContainer totalContainer) 
        throws IOException, ClassNotFoundException, CDKException
    {
	logger.debug("Start of addHydrogensToSatisfyValency(AtomContainer container, Atom atom)");
    AtomContainer changedAtomsAndBonds = addExplicitHydrogensToSatisfyValency(container, atom, totalContainer);
	logger.debug("End of addHydrogensToSatisfyValency(AtomContainer container, Atom atom)");
    return changedAtomsAndBonds;
    }

    /**
     * Method that saturates an atom in a molecule by adding explicit hydrogens.
     * In order to get coordinates for these Hydrogens, you need to 
     * remember the average bondlength of you molecule (coordinates for 
     * all atoms should be available) by using
     * double bondLength = GeometryTools.getBondLengthAverage(atomContainer);
     * and then use this method here and then use
     * org.openscience.cdk.HydrogenPlacer(atomContainer, bondLength);
     *
     * @param  atom      Atom to saturate
     * @param  container AtomContainer containing the atom
     * @param  totalContainer In case you have a container containing multiple structures, this is the total container, whereas container is a partial structure
     * @return 
     *
     * @cdk.keyword          hydrogen, adding
     * @cdk.keyword          explicit hydrogen
     */
    public AtomContainer addExplicitHydrogensToSatisfyValency(AtomContainer container, org.openscience.cdk.interfaces.Atom atom, AtomContainer totalContainer) 
        throws IOException, ClassNotFoundException, CDKException
    {
        // set number of implicit hydrogens to zero
        // add explicit hydrogens
	logger.debug("Start of addExplicitHydrogensToSatisfyValency(AtomContainer container, org.openscience.cdk.interfaces.Atom atom)");
        int missingHydrogens = valencyChecker.calculateNumberOfImplicitHydrogens(atom, container);
	logger.debug("According to valencyChecker, " + missingHydrogens + " are missing");
        AtomContainer changedAtomsAndBonds = addExplicitHydrogensToSatisfyValency(container, atom, missingHydrogens, totalContainer);
	logger.debug("End of addExplicitHydrogensToSatisfyValency(AtomContainer container, Atom atom)");
    return changedAtomsAndBonds;
    }
    
    /**
     * Method that saturates an atom in a molecule by adding explicit hydrogens.
     *
     * @param  atom      Atom to saturate
     * @param  container AtomContainer containing the atom
     * @param  count     Number of hydrogens to add
     * @param  totalContainer In case you have a container containing multiple structures, this is the total container, whereas container is a partial structure
     * @return 
     *
     * @cdk.keyword          hydrogen, adding
     * @cdk.keyword          explicit hydrogen
     */
    public AtomContainer addExplicitHydrogensToSatisfyValency(AtomContainer container, org.openscience.cdk.interfaces.Atom atom, int count, AtomContainer totalContainer) 
        throws IOException, ClassNotFoundException
    {
        boolean create2DCoordinates = GeometryTools.has2DCoordinates(container);
        
        Isotope isotope = IsotopeFactory.getInstance().getMajorIsotope("H");
        atom.setHydrogenCount(0);
        AtomContainer changedAtomsAndBonds = new org.openscience.cdk.AtomContainer();
        for (int i = 1; i <= count; i++) {
            Atom hydrogen = new Atom("H");
            IsotopeFactory.getInstance().configure(hydrogen, isotope);
            totalContainer.addAtom(hydrogen);
            Bond newBond = new Bond((Atom)atom, hydrogen, 1.0);
            totalContainer.addBond(newBond);
            changedAtomsAndBonds.addAtom(hydrogen);
            changedAtomsAndBonds.addBond(newBond);
        }
        return changedAtomsAndBonds;
    }
    
    /**
     *  Method that saturates a molecule by adding implicit hydrogens.
     *
     *@param  container  Molecule to saturate
     * @return 
     *@cdk.keyword          hydrogen, adding
     *@cdk.keyword          implicit hydrogen
     */
    public HashMap addImplicitHydrogensToSatisfyValency(AtomContainer container) throws CDKException {
      SetOfMolecules moleculeSet = ConnectivityChecker.partitionIntoMolecules(container);
      Molecule[] molecules = moleculeSet.getMolecules();
      HashMap hydrogenAtomMap = new HashMap();
      for (int k = 0; k < molecules.length; k++) {
        Molecule molPart = molecules[k];
        Atom[] atoms = molPart.getAtoms();
        for (int f = 0; f < atoms.length; f++) {
            int[] hydrogens = addImplicitHydrogensToSatisfyValency(molPart, atoms[f]);
            hydrogenAtomMap.put(atoms[f], hydrogens);
        }
      }
      return hydrogenAtomMap;
    }
    
    /**
     * Method that saturates an atom in a molecule by adding implicit hydrogens.
     *
     * @param  container  Molecule to saturate
     * @param  atom      Atom to satureate.
     * @return 
     * @cdk.keyword          hydrogen, adding
     * @cdk.keyword          implicit hydrogen
     */
    public int[] addImplicitHydrogensToSatisfyValency(AtomContainer container, org.openscience.cdk.interfaces.Atom atom) throws CDKException
    {
        int formerHydrogens = atom.getHydrogenCount();
        int missingHydrogens = valencyChecker.calculateNumberOfImplicitHydrogens(atom, container);
        atom.setHydrogenCount(missingHydrogens);
        int[] hydrogens = new int[2];
        hydrogens[0] = formerHydrogens;
        hydrogens[1] = missingHydrogens;
        return hydrogens;
    }

    /*
     * Method that saturates an atom by adding implicit hydrogens.
     *
     * @param  atom      Atom to satureate.
     *
    public void addImplicitHydrogensToSatisfyValency(Atom atom) throws CDKException
    {
        int missingHydrogens = valencyChecker.calculateNumberOfImplicitHydrogens(atom);
        atom.setHydrogenCount(missingHydrogens);
    } */
}

