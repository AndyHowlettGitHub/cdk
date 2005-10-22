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
 * Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110-1301 USA.
 */
package org.openscience.cdk;

import javax.vecmath.Point2d;
import javax.vecmath.Point3d;

import org.openscience.cdk.Atom;
import org.openscience.cdk.interfaces.ChemObjectBuilder;
import org.openscience.cdk.PseudoAtom;

/**
 * A helper class to instantiate a ChemObject for a specific implementation.
 *
 * @author        egonw
 * @cdk.module    data
 */
public class DefaultChemObjectBuilder implements ChemObjectBuilder {

	private static DefaultChemObjectBuilder instance = null;
	
	private DefaultChemObjectBuilder() {}

	public static DefaultChemObjectBuilder getInstance() {
		if (instance == null) {
			instance = new DefaultChemObjectBuilder();
		}
		return instance;
	}
	
	public org.openscience.cdk.interfaces.Atom newAtom() {
		return new Atom();
	}
	
    public org.openscience.cdk.interfaces.Atom newAtom(String elementSymbol) {
    	return new Atom(elementSymbol);
    }
    
    public org.openscience.cdk.interfaces.Atom newAtom(String elementSymbol, javax.vecmath.Point2d point2d) {
    	return new Atom(elementSymbol, point2d);
    }

    public org.openscience.cdk.interfaces.Atom newAtom(String elementSymbol, javax.vecmath.Point3d point3d) {
    	return new Atom(elementSymbol, point3d);
    }
		
	public org.openscience.cdk.interfaces.AtomContainer newAtomContainer() {
		return new AtomContainer();
	}
    
	public org.openscience.cdk.interfaces.AtomContainer newAtomContainer(int atomCount, int electronContainerCount) {
		return new AtomContainer(atomCount, electronContainerCount);
	}
    
	public org.openscience.cdk.interfaces.AtomContainer newAtomContainer(org.openscience.cdk.interfaces.AtomContainer container) {
		return new AtomContainer(container);
	}
	
    public org.openscience.cdk.interfaces.AtomParity newAtomParity(
    		org.openscience.cdk.interfaces.Atom centralAtom, 
    		org.openscience.cdk.interfaces.Atom first, 
    		org.openscience.cdk.interfaces.Atom second, 
    		org.openscience.cdk.interfaces.Atom third, 
    		org.openscience.cdk.interfaces.Atom fourth,
            int parity) {
    	return new AtomParity(centralAtom, first, second, third, fourth, parity);
    }

	public org.openscience.cdk.interfaces.AtomType newAtomType(String elementSymbol) {
		return new AtomType(elementSymbol);
	}

	public org.openscience.cdk.interfaces.AtomType newAtomType(String identifier, String elementSymbol) {
		return new AtomType(identifier, elementSymbol);
	}

	public org.openscience.cdk.interfaces.BioPolymer newBioPolymer(){
		return new BioPolymer();
	}

	public org.openscience.cdk.interfaces.Bond newBond() {
		return new Bond();
	}
	
	public org.openscience.cdk.interfaces.Bond newBond(org.openscience.cdk.interfaces.Atom atom1, org.openscience.cdk.interfaces.Atom atom2) {
		return new Bond(atom1, atom2);
	}
	
	public org.openscience.cdk.interfaces.Bond newBond(org.openscience.cdk.interfaces.Atom atom1, org.openscience.cdk.interfaces.Atom atom2, double order) {
		return new Bond(atom1, atom2, order);
	}
	
	public org.openscience.cdk.interfaces.Bond newBond(org.openscience.cdk.interfaces.Atom atom1, org.openscience.cdk.interfaces.Atom atom2, double order, int stereo) {
		return new Bond(atom1, atom2, order, stereo);
	}
	
	public org.openscience.cdk.interfaces.ChemFile newChemFile() {
		return new ChemFile();
	}

	public org.openscience.cdk.interfaces.ChemModel newChemModel() {
		return new ChemModel();
	}
	
	public org.openscience.cdk.interfaces.ChemSequence newChemSequence() {
		return new ChemSequence();   
	}
	
    public org.openscience.cdk.interfaces.Crystal newCrystal() {
    	return new Crystal();
    }
    
    public org.openscience.cdk.interfaces.Crystal newCrystal(org.openscience.cdk.interfaces.AtomContainer container) {
    	return new Crystal(container);
    }
    
    public org.openscience.cdk.interfaces.ElectronContainer newElectronContainer() {
    	return new ElectronContainer();
    }
    
    public org.openscience.cdk.interfaces.Element newElement() {
    	return new Element();
    }

    public org.openscience.cdk.interfaces.Element newElement(String symbol) {
    	return new Element(symbol);
    }

    public org.openscience.cdk.interfaces.Element newElement(String symbol, int atomicNumber) {
    	return new Element(symbol, atomicNumber);
    }

	public org.openscience.cdk.interfaces.Isotope newIsotope(String elementSymbol) {
		return new Isotope(elementSymbol);
	}
	
	public org.openscience.cdk.interfaces.Isotope newIsotope(int atomicNumber, String elementSymbol, 
			int massNumber, double exactMass, double abundance) {
		return new Isotope(atomicNumber, elementSymbol, massNumber, exactMass, abundance);
	}

	public org.openscience.cdk.interfaces.Isotope newIsotope(int atomicNumber, String elementSymbol, 
			double exactMass, double abundance) {
		return new Isotope(atomicNumber, elementSymbol, exactMass, abundance);
	}

	public org.openscience.cdk.interfaces.Isotope newIsotope(String elementSymbol, int massNumber) {
		return new Isotope(elementSymbol, massNumber);
	}

    public org.openscience.cdk.interfaces.LonePair newLonePair() {
    	return new LonePair();
    }

    public org.openscience.cdk.interfaces.LonePair newLonePair(org.openscience.cdk.interfaces.Atom atom) {
    	return new LonePair(atom);
    }

	public org.openscience.cdk.interfaces.Molecule newMolecule() {
		return new Molecule();
	}

	public org.openscience.cdk.interfaces.Molecule newMolecule(int atomCount, int electronContainerCount) {
		return new Molecule(atomCount, electronContainerCount);
	}

	public org.openscience.cdk.interfaces.Molecule newMolecule(org.openscience.cdk.interfaces.AtomContainer container) {
		return new Molecule(container);
	}

	public org.openscience.cdk.interfaces.Monomer newMonomer () {
		return new Monomer();
	}
	
	public org.openscience.cdk.interfaces.Polymer newPolymer() {
		return new Polymer();
	}

    public org.openscience.cdk.interfaces.Reaction newReaction() {
    	return new Reaction();	
    }
	
	public org.openscience.cdk.interfaces.Ring newRing() {
		return new Ring();
	}
	
	public org.openscience.cdk.interfaces.Ring newRing(org.openscience.cdk.interfaces.AtomContainer container) {
		return new Ring(container);
	}
	
	public org.openscience.cdk.interfaces.Ring newRing(int ringSize, String elementSymbol) {
		return new Ring(ringSize, elementSymbol);
	}
	
	public org.openscience.cdk.interfaces.Ring newRing(int ringSize) {
		return new Ring(ringSize);
	}

	public org.openscience.cdk.interfaces.RingSet newRingSet() {
		return new RingSet();
	}

	public org.openscience.cdk.interfaces.SetOfAtomContainers newSetOfAtomContainers() {
		return new SetOfAtomContainers();
	}

	public org.openscience.cdk.interfaces.SetOfMolecules newSetOfMolecules() {
		return new SetOfMolecules();
	}

	public org.openscience.cdk.interfaces.SetOfReactions newSetOfReactions() {
		return new SetOfReactions();
	}
	
    public org.openscience.cdk.interfaces.SingleElectron newSingleElectron() {
    	return new SingleElectron();
    }
    
    public org.openscience.cdk.interfaces.SingleElectron newSingleElectron(org.openscience.cdk.interfaces.Atom atom) {
    	return new SingleElectron(atom);   
    }

	public org.openscience.cdk.interfaces.Strand newStrand() {
		return new Strand();
	}

	public org.openscience.cdk.interfaces.PseudoAtom newPseudoAtom() {
		return new PseudoAtom();
	}

	public org.openscience.cdk.interfaces.PseudoAtom newPseudoAtom(String label) {
		return new PseudoAtom(label);
	}

	public org.openscience.cdk.interfaces.PseudoAtom newPseudoAtom(org.openscience.cdk.interfaces.Atom atom) {
		return new PseudoAtom(atom);
	}

	public org.openscience.cdk.interfaces.PseudoAtom newPseudoAtom(String label, Point3d point3d) {
		return new PseudoAtom(label, point3d);
	}

	public org.openscience.cdk.interfaces.PseudoAtom newPseudoAtom(String label, Point2d point2d) {
		return new PseudoAtom(label, point2d);
	}
}


