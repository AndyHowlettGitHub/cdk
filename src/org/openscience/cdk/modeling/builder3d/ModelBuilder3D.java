/*  $RCSfile$
 *  $Author$
 *  $Date$
 *  $Revision$
 *
 *  Copyright (C) 2004-2005  The Chemistry Development Kit (CDK) project
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
package org.openscience.cdk.modeling.builder3d;

import java.util.Hashtable;

import javax.vecmath.Point3d;
import javax.vecmath.Vector3d;
import java.io.IOException;
import java.util.Vector;

import org.openscience.cdk.Atom;
import org.openscience.cdk.AtomContainer;
import org.openscience.cdk.CDKConstants;
import org.openscience.cdk.Molecule;
import org.openscience.cdk.RingSet;
import org.openscience.cdk.graph.ConnectivityChecker;
import org.openscience.cdk.tools.manipulator.RingSetManipulator;
import org.openscience.cdk.layout.AtomPlacer;
import org.openscience.cdk.ringsearch.RingPartitioner;
import org.openscience.cdk.geometry.GeometryTools;

/**
 *  The main class to generate the 3D coordinates of a molecule ModelBuilder3D.
 *  Its use looks like:
 *  <pre>
 *  ModelBuilder3D mb3d = new ModelBuilder3D();
 *  mb3d.setTemplateHandler();
 *  mb3d.setParameterSet("mm2");
 *  mb3d.setMolecule(molecule, false);
 *  mb3d.generate3DCoordinates();
 *  Molecule molecule = mb3d.getMolecule();
 *  </pre>
 *
 *  <p>Standing problems:
 *  <ul>
 *    <li>condensed ring systems which are unknown for the template class 
 *    <li>vdWaals clashes
 *    <li>stereochemistry
 *    <li>chains running through ring systems
 *  </ul>
 *
 * @author      cho
 * @aythor      steinbeck
 * @cdk.created 2004-09-07
 * @cdk.module  builder3d
 * @cdk.keyword 3D coordinates
 * @cdk.keyword coordinate generation, 3D
 */
public class ModelBuilder3D {

	private static TemplateHandler3D DEFAULT_TEMPLATE_HANDLER = new TemplateHandler3D();
	private TemplateHandler3D templateHandler = null;
	boolean useTemplates = true;
	private Hashtable parameterSet = null;
	private Molecule molecule;
	private AtomPlacer atomPlacer = new AtomPlacer();
	private AtomPlacer3D ap3d = new AtomPlacer3D();
	private AtomTetrahedralLigandPlacer3D atlp3d = new AtomTetrahedralLigandPlacer3D();
	ForceFieldConfigurator ffc = new ForceFieldConfigurator();
	String forceFieldName = "mm2";
	
	
	/**
	 *  Constructor for the ModelBuilder3D object
	 */
	public ModelBuilder3D() { }


	/**
	 *  Constructor for the ModelBuilder3D object
	 *
	 *@param  molecule         Molecule
	 *@param  templateHandler  templateHandler Object
	 *@param  ffname           name of force field
	 */
	public ModelBuilder3D(Molecule molecule, TemplateHandler3D templateHandler, String ffname) {
		setMolecule(molecule, false);
		setTemplateHandler(templateHandler);
		setForceField(ffname);
	}


	/**
	 *  Creates an instance of this class while assigning a molecule to be layed
	 *  out.
	 *
	 *@param  molecule  The molecule to be layed out.
	 */
	public ModelBuilder3D(Molecule molecule) {
		setMolecule(molecule, false);
	}


	/**
	 *  Initilize classes needed by ModelBuilder 3d
	 */
	private void initilizeClassesForModelBuilder3D() {
		ap3d.initilize(parameterSet);
		atlp3d.setParameterSet(parameterSet);
	}


	/**
	 *  Clone molecule? 
	 *
	 *@param  mol    Molecule
	 *@param  clone  boolean
	 */
	public void setMolecule(Molecule mol, boolean clone) {

		if (clone) {
			this.molecule = (Molecule) mol.clone();
		} else {
			this.molecule = mol;
		}
		atomPlacer.setMolecule(molecule);
	}


	/**
	 *  Sets the forceField attribute of the ModelBuilder3D object
	 *
	 *@param  ffname  forceField name
	 */
	public void setForceField(String ffname) {
		if (ffname == null) {
			ffname = "mm2";
		}
		try {
			forceFieldName = ffname;
			ffc.setForceFieldConfigurator(ffname);
			parameterSet = ffc.getParameterSet();
			//ReadForceField
			initilizeClassesForModelBuilder3D();
		} catch (Exception ex1) {
			System.out.println("Problem with ForceField configuration due to>" + ex1.toString());
		}
	}


	/**
	 *  generate 3D coordinates with force field information
	 *
	 *@return                int
	 *@exception  Exception  Description of the Exception
	 */
	public int generate3DCoordinates() throws Exception {
		//System.out.println("******** GENERATE COORDINATES ********");
		//CHECK FOR CONNECTIVITY!
		//System.out.println("#atoms>"+molecule.getAtomCount());
		ConnectivityChecker cc = new ConnectivityChecker();
		if (!cc.isConnected(molecule)) {
			throw new Exception("CDKError: Molecule is NOT connected,could not layout.");
		}
		if (ap3d.numberOfUnplacedHeavyAtoms(molecule) == 1) {
			System.out.println("Only one Heavy Atom");
			molecule.getAtomAt(0).setX3d(0);
			molecule.getAtomAt(0).setY3d(0);
			molecule.getAtomAt(0).setZ3d(0);
			try {
				atlp3d.add3DCoordinatesForSinglyBondedLigands(molecule);
			} catch (Exception ex3) {
				System.out.println("PlaceSubstitutensERROR: Cannot place substitutents due to:" + ex3.toString());
			}
			return 1;
		}
		//Assing Atoms to Rings,Aliphatic and Atomtype
		RingSet ringSetMolecule = ffc.assignAtomTyps(molecule);
		Vector ringSystems = null;
		RingSet largestRingSet = null;
		double NumberOfRingAtoms = 0;

		if (ringSetMolecule.size() > 0) {
			ringSystems = RingPartitioner.partitionRings(ringSetMolecule);
			largestRingSet = getLargestRingSet(ringSystems);
			NumberOfRingAtoms = (double) ((AtomContainer) RingSetManipulator.getAllInOneContainer(largestRingSet)).getAtomCount();
			templateHandler.mapTemplates(RingSetManipulator.getAllInOneContainer(largestRingSet), NumberOfRingAtoms);
			if (!checkAllRingAtomsHasCoordinates(RingSetManipulator.getAllInOneContainer(largestRingSet))) {
				throw new IOException("RingAtomLayoutError: Not every ring atom is placed! Molecule cannot be layout.Sorry");
			}

			setAtomsToPlace(RingSetManipulator.getAllInOneContainer(largestRingSet));
			searchAndPlaceBranches(RingSetManipulator.getAllInOneContainer(largestRingSet));
			largestRingSet = null;
		} else {
			//System.out.println("****** Start of handling aliphatic molecule ******");
			AtomContainer ac = null;

			try {
				ac = atomPlacer.getInitialLongestChain(molecule);
				setAtomsToUnVisited();
				setAtomsToUnPlaced();
				ap3d.placeAliphaticHeavyChain(molecule, ac);
				//ZMatrixApproach
				ap3d.zmatrixChainToCartesian(molecule, false);
				searchAndPlaceBranches(ac);
			} catch (Exception ex1) {
				System.out.println("AliphaticChainError: Problem with finding longest chain due to>" + ex1.toString());
				throw new IOException("AliphaticChainError: Problem with finding longest chain");
			}
		}
		layoutMolecule(ringSystems);
		//System.out.println("******* PLACE SUBSTITUENTS ******");
		try {
			atlp3d.add3DCoordinatesForSinglyBondedLigands(molecule);
		} catch (Exception ex3) {
			System.out.println("PlaceSubstitutensERROR: Cannot place substitutents due to:" + ex3.toString());
		}
		return 1;
	}



	/**
	 *  Gets the ringSetOfAtom attribute of the ModelBuilder3D object
	 *
	 *@param  ringSystems  Description of the Parameter
	 *@param  atom         Description of the Parameter
	 *@return              The ringSetOfAtom value
	 */
	public RingSet getRingSetOfAtom(Vector ringSystems, Atom atom) {
		RingSet ringSetOfAtom = null;
		for (int i = 0; i < ringSystems.size(); i++) {
			if (((RingSet) ringSystems.get(i)).contains(atom)) {
				return (RingSet) ringSystems.get(i);
			}
		}
		return ringSetOfAtom;
	}


	/**
	 *  Layout the molecule, starts with ring systems and than aliphatic chains
	 *
	 *@param  ringSetMolecule  ringSystems of the molecule
	 *@exception  Exception    Description of the Exception
	 */
	public void layoutMolecule(Vector ringSetMolecule) throws Exception {
		//System.out.println("****** LAYOUT MOLECULE MAIN *******");
		AtomContainer ac = null;
		int safetyCounter = 0;
		Atom atom = null;
		//Place rest Chains/Atoms
		do {
			safetyCounter++;
			atom = ap3d.getNextPlacedHeavyAtomWithUnplacedRingNeighbour(molecule);
			if (atom != null) {
				//System.out.println("layout RingSystem...");
				Atom unplacedAtom = ap3d.getUnplacedRingHeavyAtom(molecule, atom);
				RingSet ringSetA = getRingSetOfAtom(ringSetMolecule, unplacedAtom);
				templateHandler.mapTemplates(RingSetManipulator.getAllInOneContainer(ringSetA), (double) ((AtomContainer) RingSetManipulator.getAllInOneContainer(ringSetA)).getAtomCount());

				if (checkAllRingAtomsHasCoordinates(RingSetManipulator.getAllInOneContainer(ringSetA))) {
				} else {
					throw new IOException("RingAtomLayoutError: Not every ring atom is placed! Molecule cannot be layout.Sorry");
				}

				Point3d firstAtomOriginalCoord = unplacedAtom.getPoint3d();
				Point3d centerPlacedMolecule = ap3d.geometricCenterAllPlacedAtoms(molecule);

				setBranchAtom(unplacedAtom, atom, ap3d.getPlacedHeavyAtoms(molecule, atom));
				layoutRingSystem(firstAtomOriginalCoord, unplacedAtom, ringSetA, centerPlacedMolecule, atom);
				searchAndPlaceBranches(RingSetManipulator.getAllInOneContainer(ringSetA));
				//System.out.println("Ready layout Ring System");
				ringSetA = null;
				unplacedAtom = null;
				firstAtomOriginalCoord = null;
				centerPlacedMolecule = null;
			} else {
				//System.out.println("layout chains...");
				setAtomsToUnVisited();
				atom = ap3d.getNextPlacedHeavyAtomWithUnplacedAliphaticNeighbour(molecule);
				if (atom != null) {
					ac = new AtomContainer();
					ac.addAtom(atom);
					searchAndPlaceBranches(ac);
					ac = null;
				}
			}
		} while (!ap3d.allHeavyAtomsPlaced(molecule) || safetyCounter > molecule.getAtomCount());
	}


	/**
	 *  Layout the ring system, rotate and translate the template
	 *
	 *@param  originalCoord         coordinates of the placedRingAtom from the template 
	 *@param  placedRingAtom        placedRingAtom
	 *@param  ringSet               ring system which placedRingAtom is part of
	 *@param  centerPlacedMolecule  the geometric center of the already placed molecule
	 *@param  atomB                 placed neighbour atom of  placedRingAtom
	 */
	private void layoutRingSystem(Point3d originalCoord, Atom placedRingAtom, RingSet ringSet, Point3d centerPlacedMolecule, Atom atomB) {
		//System.out.print("****** Layout ring System ******");System.out.println(">around atom:"+molecule.getAtomNumber(placedRingAtom));
		AtomContainer ac = RingSetManipulator.getAllInOneContainer(ringSet);
		Point3d newCoord = placedRingAtom.getPoint3d();
		Vector3d axis = new Vector3d(atomB.getX3d() - newCoord.x, atomB.getY3d() - newCoord.y, atomB.getZ3d() - newCoord.z);
		translateStructure(originalCoord, newCoord, ac);
		//Rotate Ringsystem to farthest possible point
		Vector3d startAtomVector = new Vector3d(newCoord.x - atomB.getX3d(), newCoord.y - atomB.getY3d(), newCoord.z - atomB.getZ3d());
		Atom farthestAtom = ap3d.getFarthestAtom(placedRingAtom.getPoint3d(), ac);
		Vector3d farthestAtomVector = new Vector3d(farthestAtom.getX3d() - newCoord.x, farthestAtom.getY3d() - newCoord.y, farthestAtom.getZ3d() - newCoord.z);
		Vector3d n1 = new Vector3d();
		n1.cross(axis, farthestAtomVector);
		n1.normalize();
		double lengthFarthestAtomVector = farthestAtomVector.length();
		Vector3d farthestVector = new Vector3d(startAtomVector);
		farthestVector.normalize();
		farthestVector.scale((startAtomVector.length() + lengthFarthestAtomVector));
		double dotProduct = farthestAtomVector.dot(farthestVector);
		double angle = Math.acos(dotProduct / (farthestAtomVector.length() * farthestVector.length()));
		Vector3d ringCenter = new Vector3d();

		for (int i = 0; i < ac.getAtomCount(); i++) {
			if (!(ac.getAtomAt(i).getFlag(CDKConstants.ISPLACED))) {
				ringCenter.x = (ac.getAtomAt(i).getPoint3d()).x - newCoord.x;
				ringCenter.y = (ac.getAtomAt(i).getPoint3d()).y - newCoord.y;
				ringCenter.z = (ac.getAtomAt(i).getPoint3d()).z - newCoord.z;
				ringCenter = atlp3d.rotate(ringCenter, n1, angle);
				ac.getAtomAt(i).setX3d((ringCenter.x + newCoord.x));
				ac.getAtomAt(i).setY3d((ringCenter.y + newCoord.y));
				ac.getAtomAt(i).setZ3d((ringCenter.z + newCoord.z));
				//ac.getAtomAt(i).setFlag(CDKConstants.ISPLACED, true);
			}
		}

		//Rotate Ring so that geometric center is max from placed center
		//System.out.println("Rotate RINGSYSTEM");
		Point3d pointRingCenter = GeometryTools.get3DCenter(ac);
		double distance = 0;
		double rotAngleMax = 0;
		angle = 1 / 180 * Math.PI;
		ringCenter = new Vector3d(pointRingCenter.x, pointRingCenter.y, pointRingCenter.z);
		ringCenter.x = ringCenter.x - newCoord.x;
		ringCenter.y = ringCenter.y - newCoord.y;
		ringCenter.z = ringCenter.z - newCoord.z;
		for (int i = 1; i < 360; i++) {
			ringCenter = atlp3d.rotate(ringCenter, axis, angle);
			if (centerPlacedMolecule.distance(new Point3d(ringCenter.x, ringCenter.y, ringCenter.z)) > distance) {
				rotAngleMax = i;
				distance = centerPlacedMolecule.distance(new Point3d(ringCenter.x, ringCenter.y, ringCenter.z));
			}
		}

		//rotate ring around axis with best angle
		rotAngleMax = (rotAngleMax / 180) * Math.PI;
		for (int i = 0; i < ac.getAtomCount(); i++) {
			if (!(ac.getAtomAt(i).getFlag(CDKConstants.ISPLACED))) {
				ringCenter.x = (ac.getAtomAt(i).getPoint3d()).x;
				ringCenter.y = (ac.getAtomAt(i).getPoint3d()).y;
				ringCenter.z = (ac.getAtomAt(i).getPoint3d()).z;
				ringCenter = atlp3d.rotate(ringCenter, axis, rotAngleMax);
				ac.getAtomAt(i).setX3d(ringCenter.x);
				ac.getAtomAt(i).setY3d(ringCenter.y);
				ac.getAtomAt(i).setZ3d(ringCenter.z);
				ac.getAtomAt(i).setFlag(CDKConstants.ISPLACED, true);
			}
		}
	}


	/**
	 *  Sets a branch atom to a ring or aliphatic chain
	 *
	 *@param  unplacedAtom    The new branchAtom 
	 *@param  atomA           placed atom to which the unplaced satom is connected
	 *@param  atomNeighbours  placed atomNeighbours of atomA
	 *@exception  Exception   Description of the Exception
	 */
	public void setBranchAtom(Atom unplacedAtom, Atom atomA, AtomContainer atomNeighbours) throws Exception {
		//System.out.println("****** SET Branch Atom ****** >"+molecule.getAtomNumber(unplacedAtom));
		AtomContainer noCoords = new AtomContainer();
		noCoords.addAtom(unplacedAtom);
		Point3d centerPlacedMolecule = ap3d.geometricCenterAllPlacedAtoms(molecule);
		Atom atomB = atomNeighbours.getAtomAt(0);
		double length = ap3d.getBondLengthValue(atomA.getID(), unplacedAtom.getID());
		double angle = (ap3d.getAngleValue(atomB.getID(), atomA.getID(), unplacedAtom.getID())) * Math.PI / 180;
		/*
		 *  System.out.println("A:"+atomA.getSymbol()+" "+atomA.getID()+" B:"+atomB.getSymbol()+" "+atomB.getID()
		 *  +" unplaced Atom:"+unplacedAtom.getID()+" BL:"+length+" Angle:"+angle
		 *  +" FormalNeighbour:"+atomA.getFormalNeighbourCount()+" HYB:"+atomA.getFlag(CDKConstants.HYBRIDIZATION_SP2)
		 *  +" #Neigbhours:"+atomNeighbours.getAtomCount());
		 */
		Atom atomC = ap3d.getPlacedHeavyAtom(molecule, atomB, atomA);

		Point3d[] branchPoints = atlp3d.get3DCoordinatesForLigands(atomA, noCoords, atomNeighbours, atomC
				, (atomA.getFormalNeighbourCount() - atomNeighbours.getAtomCount())
				, length, angle);
		double distance = 0;
		int farthestPoint = 0;
		try {
			for (int i = 0; i < branchPoints.length; i++) {
				if (Math.abs(branchPoints[i].distance(centerPlacedMolecule)) > Math.abs(distance)) {
					distance = branchPoints[i].distance(centerPlacedMolecule);
					farthestPoint = i;
				}
			}
		} catch (Exception ex2) {
			System.out.println("SetBranchAtomERROR: Not enough branch Points due to:" + ex2.toString() + " #branchpoints:" + branchPoints.length);
			throw new IOException("SetBranchAtomERROR: Not enough branch Points");
		}

		int stereo = -1;
		if (atomA.getStereoParity() != 0 ||
				(Math.abs((molecule.getBond(atomA, unplacedAtom)).getStereo()) < 2
				 && Math.abs((molecule.getBond(atomA, unplacedAtom)).getStereo()) != 0)
				 && molecule.getMaximumBondOrder(atomA) < 1.5) {
			if (atomNeighbours.getAtomCount() > 1) {
				stereo = atlp3d.makeStereocenter(atomA.getPoint3d(), molecule.getBond(atomA, unplacedAtom), (atomNeighbours.getAtomAt(0)).getPoint3d(), (atomNeighbours.getAtomAt(1)).getPoint3d(), branchPoints);
			}
		}
		if (stereo != -1) {
			farthestPoint = stereo;
		}
		unplacedAtom.setPoint3d(branchPoints[farthestPoint]);
		unplacedAtom.setFlag(CDKConstants.ISPLACED, true);
	}


	/**
	 *  Search and place branches of a chain or ring
	 *
	 *@param  chain          AtomContainer if atoms in an aliphatic chain or ring system 
	 *@exception  Exception  Description of the Exception
	 */
	public void searchAndPlaceBranches(AtomContainer chain) throws Exception {
		//System.out.println("****** SEARCH AND PLACE ****** Chain length: "+chain.getAtomCount());
		Atom[] atoms = null;
		AtomContainer branchAtoms = new AtomContainer();
		AtomContainer connectedAtoms = new AtomContainer();
		for (int i = 0; i < chain.getAtomCount(); i++) {
			atoms = molecule.getConnectedAtoms(chain.getAtomAt(i));
			for (int j = 0; j < atoms.length; j++) {
				if (!(atoms[j].getSymbol()).equals("H") & !(atoms[j].getFlag(CDKConstants.ISPLACED)) & !(atoms[j].getFlag(CDKConstants.ISINRING))) {
					//System.out.println("SEARCH PLACE AND FOUND Branch Atom "+molecule.getAtomNumber(chain.getAtomAt(i))+
					//			" New Atom:"+molecule.getAtomNumber(atoms[j])+" -> STORE");
					try {
						connectedAtoms.add(ap3d.getPlacedHeavyAtoms(molecule, chain.getAtomAt(i)));
						//System.out.println("Connected atom1:"+molecule.getAtomNumber(connectedAtoms.getAtomAt(0))+" atom2:"+
						//molecule.getAtomNumber(connectedAtoms.getAtomAt(1))+ " Length:"+connectedAtoms.getAtomCount());
					} catch (Exception ex1) {
						System.out.println("SearchAndPlaceBranchERROR: Cannot find connected placed atoms due to" + ex1.toString());
						throw new IOException("SearchAndPlaceBranchERROR: Cannot find connected placed atoms");
					}
					try {
						setBranchAtom(atoms[j], chain.getAtomAt(i), connectedAtoms);
					} catch (Exception ex2) {
						System.out.println("SearchAndPlaceBranchERROR: Cannot find enough neighbour atoms due to" + ex2.toString());
						throw new IOException("SearchAndPlaceBranchERROR: Cannot find enough neighbour atoms");
					}
					branchAtoms.addAtom(atoms[j]);
					connectedAtoms.removeAllElements();
				}
			}

		}//for ac.getAtomCount
		placeLinearChains3D(branchAtoms);
	}


	/**
	 *  Layout all aliphatic chains with ZMatrix
	 *
	 *@param  startAtoms     AtomContainer of possible start atoms for a chain
	 *@exception  Exception  Description of the Exception
	 */
	public void placeLinearChains3D(AtomContainer startAtoms) throws Exception {
		//System.out.println("****** PLACE LINEAR CHAINS ******");
		Atom dihPlacedAtom = null;
		Atom thirdPlacedAtom = null;
		AtomContainer longestUnplacedChain = new AtomContainer();
		if (startAtoms.getAtomCount() == 0) {
			//no branch points ->linear chain
			//System.out.println("------ LINEAR CHAIN - FINISH ------");
		} else {
			for (int i = 0; i < startAtoms.getAtomCount(); i++) {
				//System.out.println("FOUND BRANCHED ALKAN");
				//System.out.println("Atom NOT NULL:" + molecule.getAtomNumber(startAtoms.getAtomAt(i)));
				thirdPlacedAtom = ap3d.getPlacedHeavyAtom(molecule, startAtoms.getAtomAt(i));
				dihPlacedAtom = ap3d.getPlacedHeavyAtom(molecule, thirdPlacedAtom, startAtoms.getAtomAt(i));
				longestUnplacedChain.addAtom(dihPlacedAtom);
				longestUnplacedChain.addAtom(thirdPlacedAtom);
				longestUnplacedChain.addAtom(startAtoms.getAtomAt(i));

				longestUnplacedChain.add(atomPlacer.getLongestUnplacedChain(molecule, startAtoms.getAtomAt(i)));
				setAtomsToUnVisited();
				
				if (longestUnplacedChain.getAtomCount() < 4) {
					//di,third,sec
					//System.out.println("------ SINGLE BRANCH METHYLTYP ------");
					//break;
				} else {
					//System.out.println("LongestUnchainLength:"+longestUnplacedChain.getAtomCount());
					ap3d.placeAliphaticHeavyChain(molecule, longestUnplacedChain);
					ap3d.zmatrixChainToCartesian(molecule, true);
					searchAndPlaceBranches(longestUnplacedChain);
				}
				longestUnplacedChain.removeAllElements();
			}//for

		}
		//System.out.println("****** HANDLE ALIPHATICS END ******");
	}


	/**
	 *  Translates the template ring system to new coordinates
	 *
	 *@param  originalCoord  original coordinates of the placed ring atom from template
	 *@param  newCoord       new coordinates from branch placement
	 *@param  ac             AtomContainer contains atoms of ring system 
	 */
	public void translateStructure(Point3d originalCoord, Point3d newCoord, AtomContainer ac) {
		Point3d transVector = new Point3d(originalCoord);
		transVector.x = transVector.x - newCoord.x;
		transVector.y = transVector.y - newCoord.y;
		transVector.z = transVector.z - newCoord.z;
		for (int i = 0; i < ac.getAtomCount(); i++) {
			if (!(ac.getAtomAt(i).getFlag(CDKConstants.ISPLACED))) {
				ac.getAtomAt(i).setX3d(ac.getAtomAt(i).getX3d() - transVector.x);
				ac.getAtomAt(i).setY3d(ac.getAtomAt(i).getY3d() - transVector.y);
				ac.getAtomAt(i).setZ3d(ac.getAtomAt(i).getZ3d() - transVector.z);
				//ac.getAtomAt(i).setFlag(CDKConstants.ISPLACED, true);
			}
		}
	}


	/**
	 * Returns the largest (number of atoms) ring set in a molecule
	 *
	 *@param  ringSystems  RingSystems of a molecule 
	 *@return              The largestRingSet 
	 */
	private RingSet getLargestRingSet(Vector ringSystems) {
		RingSet largestRingSet = null;
		int atomNumber = 0;
		AtomContainer container = null;
		for (int i = 0; i < ringSystems.size(); i++) {
			container = RingSetManipulator.getAllInOneContainer((RingSet) ringSystems.get(i));
			if (atomNumber < container.getAtomCount()) {
				atomNumber = container.getAtomCount();
				largestRingSet = (RingSet) ringSystems.get(i);
			}
		}
		return largestRingSet;
	}


	/**
	 *  Returns true if all atoms in an AtomContainer have coordinates
	 *
	 *@param  ac  AtomContainer
	 *@return     boolean
	 */
	private boolean checkAllRingAtomsHasCoordinates(AtomContainer ac) {
		for (int i = 0; i < ac.getAtomCount(); i++) {
			if (ac.getAtomAt(i).getPoint3d() != null && ac.getAtomAt(i).getFlag(CDKConstants.ISINRING)) {
			} else if (!ac.getAtomAt(i).getFlag(CDKConstants.ISINRING)) {
			} else {
				return false;
			}
		}
		return true;
	}


	/**
	 *  Sets the atomsToPlace attribute of the ModelBuilder3D object
	 *
	 *@param  ac  The new atomsToPlace value
	 */
	private void setAtomsToPlace(AtomContainer ac) {
		for (int i = 0; i < ac.getAtomCount(); i++) {
			ac.getAtomAt(i).setFlag(CDKConstants.ISPLACED, true);
		}
	}


	/**
	 *  Sets the atomsToUnPlaced attribute of the ModelBuilder3D object
	 */
	private void setAtomsToUnPlaced() {
		for (int i = 0; i < molecule.getAtomCount(); i++) {
			molecule.getAtomAt(i).setFlag(CDKConstants.ISPLACED, false);
		}
	}


	/**
	 *  Sets the atomsToUnVisited attribute of the ModelBuilder3D object
	 */
	private void setAtomsToUnVisited() {
		for (int i = 0; i < molecule.getAtomCount(); i++) {
			molecule.getAtomAt(i).setFlag(CDKConstants.VISITED, false);
		}
	}


	/**
	 *  Sets the useTemplates attribute of the ModelBuilder3D object
	 *
	 *@param  useTemplates  The new useTemplates value
	 */
	public void setUseTemplates(boolean useTemplates) {
		this.useTemplates = useTemplates;
	}


	/**
	 *  Gets the useTemplates attribute of the ModelBuilder3D object
	 *
	 *@return    The useTemplates value
	 */
	public boolean getUseTemplates() {
		return useTemplates;
	}


	/**
	 *  Sets the templateHandler attribute of the ModelBuilder3D object
	 *
	 *@param  templateHandler  The new templateHandler value
	 */
	public void setTemplateHandler(TemplateHandler3D templateHandler) {
		this.templateHandler = templateHandler;
		this.templateHandler.loadTemplates();
	}


	/**
	 *  Sets the templateHandler attribute of the ModelBuilder3D object
	 */
	public void setTemplateHandler() {
		this.templateHandler = DEFAULT_TEMPLATE_HANDLER;
		this.templateHandler.loadTemplates();
	}


	/**
	 *  Sets the molecule attribute of the ModelBuilder3D object
	 *
	 *@param  molecule  The new molecule value
	 */
	public void setMolecule(Molecule molecule) {
		setMolecule(molecule, true);
	}


	/**
	 *  Gets the molecule attribute of the ModelBuilder3D object
	 *
	 *@return    The molecule value
	 */
	public Molecule getMolecule() {
		return this.molecule;
	}
}

