/*
 *  $RCSfile: Polarizability
 *  $Author$
 *  $Date$
 *  *
 *  Copyright (C) 1997-2004  The Chemistry Development Kit (CDK) project
 *
 *  Contact: cdk-devel@list.sourceforge.net
 *
 *  This program is free software; you can redistribute it and/or
 *  modify it under the terms of the GNU Lesser General Public License
 *  as published by the Free Software Foundation; either version 2.1
 *  of the License, or (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU Lesser General Public License for more details.
 *
 *  You should have received a copy of the GNU Lesser General Public License
 *  along with this program; if not, write to the Free Software
 *  Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 */
package org.openscience.cdk.charges;

import java.util.Vector;
import org.openscience.cdk.Atom;
import org.openscience.cdk.Bond;
import org.openscience.cdk.AtomContainer;
import org.openscience.cdk.tools.HydrogenAdder;
import org.openscience.cdk.Molecule;
import org.openscience.cdk.graph.PathTools;

/**
 *  Calculation of the polarizability of a molecule by the method of Kang and
 *  Jhon and Gasteiger based on {@cdk.cite KJ81} and {@cdk.cite GH82}
 *  Limitations in parameterization of atoms:
 *  H,Csp3,Csp2,Csp2arom,Csp3,Nsp3,Nsp2,Nsp3,P,Osp3,Osp2 Aromaticity must be
 *  calculated before hand
 *
 *@author         chhoppe
 *@cdk.created    2004-11-03
 */
public class Polarizability {

	/**
	 *  Constructor for the Polarizability object
	 */
	public Polarizability() { }


	/**
	 *  Gets the polarizabilitiyFactorForAtom 
	 *
	 *@param  ac    AtomContainer
	 *@param  atom  atom for which the factor should become known
	 *@return       The polarizabilitiyFactorForAtom value
	 */
	public double getPolarizabilitiyFactorForAtom(AtomContainer ac, Atom atom) {
		AtomContainer acH = new AtomContainer(ac);
		try {
			HydrogenAdder hAdder = new HydrogenAdder();
			hAdder.addExplicitHydrogensToSatisfyValency((Molecule) acH);
		} catch (Exception ex1) {
		}
		return getKJPolarizabilityFactor(acH, atom);
	}


	/**
	 *  calculates the mean molecular polarizability as described in paper of Kang and Jhorn
	 *
	 *@param  ac  AtomContainer
	 *@return     polarizabilitiy
	 */
	public double calculateKJMeanMolecularPolarizability(AtomContainer ac) {
		double polarizabilitiy = 0;
		Molecule acH = new Molecule(ac);
		try {
			HydrogenAdder hAdder = new HydrogenAdder();
			hAdder.addExplicitHydrogensToSatisfyValency(acH);
		} catch (Exception ex1) {
		}
		for (int i = 0; i < acH.getAtomCount(); i++) {
			polarizabilitiy += getKJPolarizabilityFactor(acH, acH.getAtomAt(i));
		}
		return polarizabilitiy;
	}


	/**
	 *  calculate effective atom polarizability
	 *
	 *@param  ac                     AtomContainer
	 *@param  atom                   atom for which effective atom polarizability should be calculated
	 *@param  influenceSphereCutOff  cut off for spheres whoch should taken into account for calculation
	 *@return                        polarizabilitiy
	 */
	public double calculateGHEffectiveAtomPolarizability(AtomContainer ac, Atom atom, int influenceSphereCutOff) {
		double polarizabilitiy = 0;
		Molecule acH = new Molecule(ac);
		Vector startAtom = new Vector(1);
		startAtom.add(0, (Atom) atom);
		double bond = 0;
		try {
			HydrogenAdder hAdder = new HydrogenAdder();
			hAdder.addExplicitHydrogensToSatisfyValency(acH);
		} catch (Exception ex1) {
		}
		polarizabilitiy += getKJPolarizabilityFactor(acH, atom);
		for (int i = 0; i < acH.getAtomCount(); i++) {
			if (acH.getAtomAt(i) != atom) {
				bond = PathTools.breadthFirstTargetSearch(acH,
						startAtom, acH.getAtomAt(i), 0, influenceSphereCutOff);
				if (bond == 1) {
					polarizabilitiy += getKJPolarizabilityFactor(acH, acH.getAtomAt(i));
				} else {
					polarizabilitiy += (Math.pow(0.5, bond - 1) * getKJPolarizabilityFactor(acH, acH.getAtomAt(i)));
				}//if bond==0
			}//if !=atom
		}//for
		return polarizabilitiy;
	}


	/**
	 *  calculate bond polarizability
	 *
	 *@param  ac    AtomContainer
	 *@param  bond  Bond bond for which the polarizabilitiy should be calculated
	 *@return       polarizabilitiy
	 */
	public double calculateBondPolarizability(AtomContainer ac, Bond bond) {
		double polarizabilitiy = 0;
		Molecule acH = new Molecule(ac);
		Atom[] atoms = bond.getAtoms();
		try {
			HydrogenAdder hAdder = new HydrogenAdder();
			hAdder.addExplicitHydrogensToSatisfyValency(acH);
		} catch (Exception ex1) {
		}
		if (atoms.length == 2) {
			polarizabilitiy += getKJPolarizabilityFactor(acH, atoms[0]);
			polarizabilitiy += getKJPolarizabilityFactor(acH, atoms[1]);
		}
		return (polarizabilitiy / 2);
	}


	/**
	 *  Method which assigns the polarizabilitiyFactors
	 *
	 *@param  ac    AtomContainer
	 *@param  atom  Atom
	 *@return       double polarizabilitiyFactor
	 */
	private double getKJPolarizabilityFactor(AtomContainer ac, Atom atom) {
		double polarizabilitiyFactor = 0;
		String AtomSymbol = "";
		AtomSymbol = atom.getSymbol();
		if (AtomSymbol.equals("H")) {
			polarizabilitiyFactor = 0.386;
		} else if (AtomSymbol.equals("C")) {
			if (ac.getMaximumBondOrder(atom) == 1) {
				polarizabilitiyFactor = 1.064;
			} else if (ac.getMaximumBondOrder(atom) == 1.5) {
				polarizabilitiyFactor = 1.230;
			} else if (ac.getMaximumBondOrder(atom) == 2) {
				if (getNumberOfHydrogen(ac, atom) == 0) {
					polarizabilitiyFactor = 1.382;
				} else {
					polarizabilitiyFactor = 1.529;
				}
			} else if (ac.getMaximumBondOrder(atom) >= 3) {
				polarizabilitiyFactor = 1.279;
			}
		} else if (AtomSymbol.equals("N")) {
			if (atom.getCharge() < 0) {
				polarizabilitiyFactor = 1.090;
			} else if (ac.getMaximumBondOrder(atom) == 1) {
				polarizabilitiyFactor = 1.094;
			} else if (ac.getMaximumBondOrder(atom) > 1 && ac.getMaximumBondOrder(atom) < 3) {
				polarizabilitiyFactor = 1.030;
			} else if (ac.getMaximumBondOrder(atom) >= 3) {
				polarizabilitiyFactor = 0.852;
			}
		} else if (AtomSymbol.equals("O")) {
			if (atom.getCharge() == -1) {
				polarizabilitiyFactor = 1.791;
			} else if (atom.getCharge() == 1) {
				polarizabilitiyFactor = 0.422;
			} else if (ac.getMaximumBondOrder(atom) == 1) {
				polarizabilitiyFactor = 0.664;
			} else if (ac.getMaximumBondOrder(atom) == 2) {
				polarizabilitiyFactor = 0.460;
			}
		} else if (AtomSymbol.equals("F")) {
			polarizabilitiyFactor = 0;
		} else if (AtomSymbol.equals("P")) {
			if (ac.getBondCount(atom) == 4 && ac.getMaximumBondOrder(atom) == 2) {
				polarizabilitiyFactor = 0;
			}
		} else if (AtomSymbol.equals("S")) {
			polarizabilitiyFactor = 0;
		} else if (AtomSymbol.equals("Cl")) {
			polarizabilitiyFactor = 0;
		} else if (AtomSymbol.equals("Br")) {
			polarizabilitiyFactor = 0;
		} else if (AtomSymbol.equals("I")) {
			polarizabilitiyFactor = 0;
		}
		return polarizabilitiyFactor;
	}


	/**
	 *  Gets the numberOfHydrogen attribute of the Polarizability object
	 *
	 *@param  ac    Description of the Parameter
	 *@param  atom  Description of the Parameter
	 *@return       The numberOfHydrogen value
	 */
	private int getNumberOfHydrogen(AtomContainer ac, Atom atom) {
		Bond[] bonds = ac.getConnectedBonds(atom);
		Atom connectedAtom = null;
		int hCounter = 0;
		for (int i = 0; i < bonds.length; i++) {
			connectedAtom = bonds[i].getConnectedAtom(atom);
			if (connectedAtom.getSymbol().equals("H")) {
				hCounter += 1;
			}
		}
		return hCounter;
	}
}

