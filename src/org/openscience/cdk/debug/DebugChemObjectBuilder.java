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
package org.openscience.cdk.debug;

import javax.vecmath.Point2d;
import javax.vecmath.Point3d;

import org.openscience.cdk.AtomParity;
import org.openscience.cdk.ChemFile;
import org.openscience.cdk.ChemModel;
import org.openscience.cdk.ChemSequence;
import org.openscience.cdk.Ring;
import org.openscience.cdk.RingSet;
import org.openscience.cdk.SetOfReactions;
import org.openscience.cdk.interfaces.ChemObjectBuilder;

/**
 * A helper class to instantiate a ChemObject for the debug implementation.
 *
 * @author        egonw
 * @cdk.module    data-debug
 */
public class DebugChemObjectBuilder implements ChemObjectBuilder {

	private static DebugChemObjectBuilder instance = null;
	
	private DebugChemObjectBuilder() {}

	public static DebugChemObjectBuilder getInstance() {
		if (instance == null) {
			instance = new DebugChemObjectBuilder();
		}
		return instance;
	}
	
	public org.openscience.cdk.interfaces.AminoAcid newAminoAcid() {
		return new DebugAminoAcid();
	}
	
	public org.openscience.cdk.interfaces.Atom newAtom() {
		return new DebugAtom();
	}
	
    public org.openscience.cdk.interfaces.Atom newAtom(String elementSymbol) {
    	return new DebugAtom(elementSymbol);
    }
    
    public org.openscience.cdk.interfaces.Atom newAtom(String elementSymbol, javax.vecmath.Point2d point2d) {
    	return new DebugAtom(elementSymbol, point2d);
    }

    public org.openscience.cdk.interfaces.Atom newAtom(String elementSymbol, javax.vecmath.Point3d point3d) {
    	return new DebugAtom(elementSymbol, point3d);
    }
		
	public org.openscience.cdk.interfaces.AtomContainer newAtomContainer() {
		return new DebugAtomContainer();
	}
    
	public org.openscience.cdk.interfaces.AtomContainer newAtomContainer(int atomCount, int electronContainerCount) {
		return new DebugAtomContainer(atomCount, electronContainerCount);
	}
    
	public org.openscience.cdk.interfaces.AtomContainer newAtomContainer(org.openscience.cdk.interfaces.AtomContainer container) {
		return new DebugAtomContainer(container);
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
		return new DebugAtomType(elementSymbol);
	}

	public org.openscience.cdk.interfaces.AtomType newAtomType(String identifier, String elementSymbol) {
		return new DebugAtomType(identifier, elementSymbol);
	}

	public org.openscience.cdk.interfaces.BioPolymer newBioPolymer(){
		return new DebugBioPolymer();
	}

	public org.openscience.cdk.interfaces.Bond newBond() {
		return new DebugBond();
	}
	
	public org.openscience.cdk.interfaces.Bond newBond(org.openscience.cdk.interfaces.Atom atom1, org.openscience.cdk.interfaces.Atom atom2) {
		return new DebugBond(atom1, atom2);
	}
	
	public org.openscience.cdk.interfaces.Bond newBond(org.openscience.cdk.interfaces.Atom atom1, org.openscience.cdk.interfaces.Atom atom2, double order) {
		return new DebugBond(atom1, atom2, order);
	}
	
	public org.openscience.cdk.interfaces.Bond newBond(org.openscience.cdk.interfaces.Atom atom1, org.openscience.cdk.interfaces.Atom atom2, double order, int stereo) {
		return new DebugBond(atom1, atom2, order, stereo);
	}
	
	public org.openscience.cdk.interfaces.ChemFile newChemFile() {
		return new ChemFile();
	}

	public org.openscience.cdk.interfaces.ChemModel newChemModel() {
		return new ChemModel();
	}
	
	public org.openscience.cdk.interfaces.ChemObject newChemObject() {
		return new DebugChemObject();
	}
	
	public org.openscience.cdk.interfaces.ChemSequence newChemSequence() {
		return new ChemSequence();   
	}
	
    public org.openscience.cdk.interfaces.Crystal newCrystal() {
    	return new DebugCrystal();
    }
    
    public org.openscience.cdk.interfaces.Crystal newCrystal(org.openscience.cdk.interfaces.AtomContainer container) {
    	return new DebugCrystal(container);
    }
    
    public org.openscience.cdk.interfaces.ElectronContainer newElectronContainer() {
    	return new DebugElectronContainer();
    }
    
    public org.openscience.cdk.interfaces.Element newElement() {
    	return new DebugElement();
    }

    public org.openscience.cdk.interfaces.Element newElement(String symbol) {
    	return new DebugElement(symbol);
    }

    public org.openscience.cdk.interfaces.Element newElement(String symbol, int atomicNumber) {
    	return new DebugElement(symbol, atomicNumber);
    }

	public org.openscience.cdk.interfaces.Isotope newIsotope(String elementSymbol) {
		return new DebugIsotope(elementSymbol);
	}
	
	public org.openscience.cdk.interfaces.Isotope newIsotope(int atomicNumber, String elementSymbol, 
			int massNumber, double exactMass, double abundance) {
		return new DebugIsotope(atomicNumber, elementSymbol, massNumber, exactMass, abundance);
	}

	public org.openscience.cdk.interfaces.Isotope newIsotope(int atomicNumber, String elementSymbol, 
			double exactMass, double abundance) {
		return new DebugIsotope(atomicNumber, elementSymbol, exactMass, abundance);
	}

	public org.openscience.cdk.interfaces.Isotope newIsotope(String elementSymbol, int massNumber) {
		return new DebugIsotope(elementSymbol, massNumber);
	}

    public org.openscience.cdk.interfaces.LonePair newLonePair() {
    	return new DebugLonePair();
    }

    public org.openscience.cdk.interfaces.LonePair newLonePair(org.openscience.cdk.interfaces.Atom atom) {
    	return new DebugLonePair(atom);
    }

	public org.openscience.cdk.interfaces.Molecule newMolecule() {
		return new DebugMolecule();
	}

	public org.openscience.cdk.interfaces.Molecule newMolecule(int atomCount, int electronContainerCount) {
		return new DebugMolecule(atomCount, electronContainerCount);
	}

	public org.openscience.cdk.interfaces.Molecule newMolecule(org.openscience.cdk.interfaces.AtomContainer container) {
		return new DebugMolecule(container);
	}

	public org.openscience.cdk.interfaces.Monomer newMonomer () {
		return new DebugMonomer();
	}
	
	public org.openscience.cdk.interfaces.Polymer newPolymer() {
		return new DebugPolymer();
	}

    public org.openscience.cdk.interfaces.Reaction newReaction() {
    	return new DebugReaction();	
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
		return new DebugSetOfAtomContainers();
	}

	public org.openscience.cdk.interfaces.SetOfMolecules newSetOfMolecules() {
		return new DebugSetOfMolecules();
	}

	public org.openscience.cdk.interfaces.SetOfReactions newSetOfReactions() {
		return new SetOfReactions();
	}
	
    public org.openscience.cdk.interfaces.SingleElectron newSingleElectron() {
    	return new DebugSingleElectron();
    }
    
    public org.openscience.cdk.interfaces.SingleElectron newSingleElectron(org.openscience.cdk.interfaces.Atom atom) {
    	return new DebugSingleElectron(atom);   
    }

	public org.openscience.cdk.interfaces.Strand newStrand() {
		return new DebugStrand();
	}

	public org.openscience.cdk.interfaces.PseudoAtom newPseudoAtom() {
		return new DebugPseudoAtom();
	}

	public org.openscience.cdk.interfaces.PseudoAtom newPseudoAtom(String label) {
		return new DebugPseudoAtom(label);
	}

	public org.openscience.cdk.interfaces.PseudoAtom newPseudoAtom(org.openscience.cdk.interfaces.Atom atom) {
		return new DebugPseudoAtom(atom);
	}

	public org.openscience.cdk.interfaces.PseudoAtom newPseudoAtom(String label, Point3d point3d) {
		return new DebugPseudoAtom(label, point3d);
	}

	public org.openscience.cdk.interfaces.PseudoAtom newPseudoAtom(String label, Point2d point2d) {
		return new DebugPseudoAtom(label, point2d);
	}
}


