/* $RCSfile$    
 * $Author$    
 * $Date$    
 * $Revision$
 * 
 * Copyright (C) 1997-2003  The Chemistry Development Kit (CDK) project
 * 
 * Contact: cdk-devel@lists.sourceforge.net
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
 *  
 */
package org.openscience.cdk.layout;

import org.openscience.cdk.*;
import org.openscience.cdk.ringsearch.*;
import org.openscience.cdk.geometry.*;
import org.openscience.cdk.tools.*;
import javax.vecmath.*;
import java.util.Vector;
import java.lang.Math;
import java.awt.*;

/**
 * Class providing methods for generating coordinates for ring atoms.
 * Various situations are supported, like condensation, spiro-attachment, etc.
 * They can be used for Automated Structure Diagram Generation or in the interactive
 * buildup of ringsystems by the user. 
 **/

public class RingPlacer 
{
	static boolean debug = false;
	private org.openscience.cdk.tools.LoggingTool logger;
	
	private Molecule molecule; 
	
	private AtomPlacer atomPlacer = new AtomPlacer();
	
	static int FUSED = 0;
	static int BRIDGED = 1;		
	static int SPIRO = 2;

	/**
	 * The empty constructor.
	 */
	public RingPlacer() 
	{
		logger = new org.openscience.cdk.tools.LoggingTool(this.getClass().getName());
	}


	/**
	 * Generated coordinates for a given ring. Multiplexes to special handlers 
	 * for the different possible situations (spiro-, fusion-, bridged attachement)
	 *
	 * @param   ring  The ring to be placed
	 * @param   sharedAtoms  The atoms of this ring, also members of another ring, which are already placed
	 * @param   sharedAtomsCenter  The geometric center of these atoms
	 * @param   ringCenterVector  A vector pointing the the center of the new ring
	 * @param   bondLength  The standard bondlength
	 */
	public void placeRing(Ring ring, AtomContainer sharedAtoms, Point2d sharedAtomsCenter, Vector2d ringCenterVector, double bondLength)
	{
		int sharedAtomCount = sharedAtoms.getAtomCount();
		logger.debug("placeRing -> sharedAtomCount: " + sharedAtomCount);
		if (sharedAtomCount > 2) 
		{
			placeBridgedRing(ring, sharedAtoms, sharedAtomsCenter, ringCenterVector, bondLength);
		}
		else if (sharedAtomCount == 2)
		{
			placeFusedRing(ring, sharedAtoms, sharedAtomsCenter, ringCenterVector, bondLength);
		}
		else if (sharedAtomCount == 1)
		{
			placeSpiroRing(ring, sharedAtoms, sharedAtomsCenter, ringCenterVector, bondLength);
		}

	}
	
	
	/**
	 * Positions the aliphatic substituents of a ring system
	 *
	 * @param   rs The RingSystem for which the substituents are to be laid out 
	 * @return  A list of atoms that where laid out   
	 */
	public AtomContainer placeRingSubstituents(RingSet rs, double bondLength)
	{
		logger.debug("RingPlacer.placeRingSubstituents() start");
		Ring ring = null;
		Atom atom = null;
		RingSet rings = null;
		AtomContainer unplacedPartners = new AtomContainer();;
		AtomContainer sharedAtoms = new AtomContainer();
		AtomContainer primaryAtoms = new AtomContainer();
		AtomContainer treatedAtoms = new AtomContainer();
		Point2d centerOfRingGravity = null;
		for (int j = 0; j < rs.size(); j++)
		{
			ring = (Ring)rs.elementAt(j); /* Get the j-th Ring in RingSet rs */
			for (int k = 0; k < ring.getAtomCount(); k++)
			{
				unplacedPartners.removeAllElements();
				sharedAtoms.removeAllElements();
				primaryAtoms.removeAllElements();
				atom = ring.getAtomAt(k);
				rings = rs.getRings(atom);
				centerOfRingGravity = rings.get2DCenter();
				atomPlacer.partitionPartners(atom, unplacedPartners, sharedAtoms);
				atomPlacer.markNotPlaced(unplacedPartners);
				try
				{
						for (int f = 0; f < unplacedPartners.getAtomCount(); f++)
						{
							logger.debug("placeRingSubstituents->unplacedPartners: " + (molecule.getAtomNumber(unplacedPartners.getAtomAt(f)) + 1));
						}
				}
				catch(Exception exc)
				{
				}
				
				treatedAtoms.add(unplacedPartners);
				if (unplacedPartners.getAtomCount() > 0)
				{
					atomPlacer.distributePartners(atom, sharedAtoms, centerOfRingGravity, unplacedPartners, bondLength);
				}
			}
		}
		logger.debug("RingPlacer.placeRingSubstituents() end");
		return treatedAtoms;
	}
	
	
	/**
	 * Generated coordinates for a given ring, which is connected to another ring a bridged ring, 
	 * i.e. it shares more than two atoms with another ring.
	 *
	 * @param   ring  The ring to be placed
	 * @param   sharedAtoms  The atoms of this ring, also members of another ring, which are already placed
	 * @param   sharedAtomsCenter  The geometric center of these atoms
	 * @param   ringCenterVector  A vector pointing the the center of the new ring
	 * @param   bondLength  The standard bondlength
	 */
	private  void placeBridgedRing(Ring ring, AtomContainer sharedAtoms, Point2d sharedAtomsCenter, Vector2d ringCenterVector, double bondLength )
	{
		double radius = getNativeRingRadius(ring, bondLength);
		Point2d ringCenter = new Point2d(sharedAtomsCenter);
		ringCenterVector.normalize();
		logger.debug("placeBridgedRing->: ringCenterVector.length()" + ringCenterVector.length());	
		ringCenterVector.scale(radius);
		ringCenter.add(ringCenterVector);


		Atom[] bridgeAtoms = getBridgeAtoms(sharedAtoms);
		Atom bondAtom1 = bridgeAtoms[0];
		Atom bondAtom2 = bridgeAtoms[1];

		Vector2d bondAtom1Vector = new Vector2d(bondAtom1.getPoint2D());
		Vector2d bondAtom2Vector = new Vector2d(bondAtom2.getPoint2D());		
		Vector2d originRingCenterVector = new Vector2d(ringCenter);		

		bondAtom1Vector.sub(originRingCenterVector);
		bondAtom2Vector.sub(originRingCenterVector);		

		double occupiedAngle = bondAtom1Vector.angle(bondAtom2Vector);		
		
		double remainingAngle = (2 * Math.PI) - occupiedAngle;
		double addAngle = remainingAngle / (ring.getRingSize() - sharedAtoms.getAtomCount() + 1);

		logger.debug("placeBridgedRing->occupiedAngle: " + Math.toDegrees(occupiedAngle));
		logger.debug("placeBridgedRing->remainingAngle: " + Math.toDegrees(remainingAngle));

		logger.debug("placeBridgedRing->addAngle: " + Math.toDegrees(addAngle));				


		Atom startAtom;

		double centerX = ringCenter.x;
		double centerY = ringCenter.y;
		
		double xDiff = bondAtom1.getX2D() - bondAtom2.getX2D();
		double yDiff = bondAtom1.getY2D() - bondAtom2.getY2D();
		
		double startAngle;;	
		
		int direction = 1;
		// if bond is vertical
		if (xDiff == 0)
		{
			logger.debug("placeBridgedRing->Bond is vertical");
			//starts with the lower Atom
			if (bondAtom1.getY2D() > bondAtom2.getY2D())
			{
				startAtom = bondAtom1;
			}
			else
			{
				startAtom = bondAtom2;
			}
			
			//changes the drawing direction
			if (centerX < bondAtom1.getX2D())
			{
				direction = 1;
			}
			else
			{
				direction = -1;
			}
		}

		  // if bond is not vertical
		else
		{
			//starts with the left Atom
			if (bondAtom1.getX2D() > bondAtom2.getX2D())
			{
				startAtom = bondAtom1;
			}
			else
			{
				startAtom = bondAtom2;
			}
			
			//changes the drawing direction
			if (centerY - bondAtom1.getY2D() > (centerX - bondAtom1.getX2D()) * yDiff / xDiff)
			{
				direction = 1;
			}
			else
			{
				direction = -1;
			}
		}
		startAngle = GeometryTools.getAngle(startAtom.getX2D() - ringCenter.x, startAtom.getY2D() - ringCenter.y);

		Atom currentAtom = startAtom;
        // determine first bond in Ring
        int k = 0;
        for (k = 0; k < ring.getElectronContainerCount(); k++) {
            if (ring.getElectronContainerAt(k) instanceof Bond) break;
        }
        Bond currentBond = (Bond)sharedAtoms.getElectronContainerAt(k);
		Vector atomsToDraw = new Vector();
		for (int i = 0; i < ring.getBondCount() - 2; i++)
		{
			currentBond = ring.getNextBond(currentBond, currentAtom);
			currentAtom = currentBond.getConnectedAtom(currentAtom);
			if (!sharedAtoms.contains(currentAtom))
			{
				atomsToDraw.addElement(currentAtom);
			}
		}
			try
			{
				logger.debug("placeBridgedRing->atomsToPlace: " + atomPlacer.listNumbers(molecule, atomsToDraw));
				logger.debug("placeBridgedRing->startAtom is: " + (molecule.getAtomNumber(startAtom) + 1));
				logger.debug("placeBridgedRing->startAngle: " + Math.toDegrees(startAngle));
				logger.debug("placeBridgedRing->addAngle: " + Math.toDegrees(addAngle));		
			}
			catch(Exception exc)
			{
				logger.debug("Caught an exception while logging in RingPlacer");
			}
		
		addAngle = addAngle * direction;
		atomPlacer.populatePolygonCorners(atomsToDraw, ringCenter, startAngle, addAngle, radius, false);
	}
	
	/**
	 * Generated coordinates for a given ring, which is connected to a spiro ring.
	 * The rings share exactly one atom.
	 *
	 * @param   ring  The ring to be placed
	 * @param   sharedAtoms  The atoms of this ring, also members of another ring, which are already placed
	 * @param   sharedAtomsCenter  The geometric center of these atoms
	 * @param   ringCenterVector  A vector pointing the the center of the new ring
	 * @param   bondLength  The standard bondlength
	 */
	public void placeSpiroRing(Ring ring, AtomContainer sharedAtoms, Point2d sharedAtomsCenter, Vector2d ringCenterVector, double bondLength)
	{

		logger.debug("placeSpiroRing");
		double radius = getNativeRingRadius(ring, bondLength);
		Point2d ringCenter = new Point2d(sharedAtomsCenter);
		ringCenterVector.normalize();
		ringCenterVector.scale(radius);
		ringCenter.add(ringCenterVector);
		double addAngle = 2 * Math.PI / ring.getRingSize();

		Atom startAtom = sharedAtoms.getAtomAt(0);

		double centerX = ringCenter.x;
		double centerY = ringCenter.y;
		
		int direction = 1;

		Atom currentAtom = startAtom;
		double startAngle = GeometryTools.getAngle(startAtom.getX2D() - ringCenter.x, startAtom.getY2D() - ringCenter.y);
		/* 
		 * Get one bond connected to the spiro bridge atom.
		 * It doesn't matter in which direction we draw.
		 */ 
		Bond[] bonds = ring.getConnectedBonds(startAtom);
		
		Bond currentBond = bonds[0];
		
		Vector atomsToDraw = new Vector();
		/* 
		 * Store all atoms to draw in consequtive order relative to the 
		 * chosen bond.
		 */ 
		for (int i = 0; i < ring.getBondCount(); i++)
		{
			currentBond = ring.getNextBond(currentBond, currentAtom);
			currentAtom = currentBond.getConnectedAtom(currentAtom);
			atomsToDraw.addElement(currentAtom);
		}
		logger.debug("currentAtom  "+currentAtom);
		logger.debug("startAtom  "+startAtom);

		atomPlacer.populatePolygonCorners(atomsToDraw, ringCenter, startAngle, addAngle, radius, false);
	
	}


	/**
	 * Generated coordinates for a given ring, which is fused to another ring.
	 * The rings share exactly one bond.
	 *
	 * @param   ring  The ring to be placed
	 * @param   sharedAtoms  The atoms of this ring, also members of another ring, which are already placed
	 * @param   sharedAtomsCenter  The geometric center of these atoms
	 * @param   ringCenterVector  A vector pointing the the center of the new ring
	 * @param   bondLength  The standard bondlength
	 */
	public  void placeFusedRing(Ring ring, AtomContainer sharedAtoms, Point2d sharedAtomsCenter, Vector2d ringCenterVector, double bondLength )
	{
		logger.debug("RingPlacer.placeFusedRing() start");
		Point2d ringCenter = new Point2d(sharedAtomsCenter);
		double radius = getNativeRingRadius(ring, bondLength);
		double newRingPerpendicular = Math.sqrt(Math.pow(radius, 2) - Math.pow(bondLength/2, 2));
		ringCenterVector.normalize();
		logger.debug("placeFusedRing->: ringCenterVector.length()" + ringCenterVector.length());	
		ringCenterVector.scale(newRingPerpendicular);
		ringCenter.add(ringCenterVector);

		Atom bondAtom1 = sharedAtoms.getAtomAt(0);
		Atom bondAtom2 = sharedAtoms.getAtomAt(1);

		Vector2d bondAtom1Vector = new Vector2d(bondAtom1.getPoint2D());
		Vector2d bondAtom2Vector = new Vector2d(bondAtom2.getPoint2D());		
		Vector2d originRingCenterVector = new Vector2d(ringCenter);		

		bondAtom1Vector.sub(originRingCenterVector);
		bondAtom2Vector.sub(originRingCenterVector);		

		double occupiedAngle = bondAtom1Vector.angle(bondAtom2Vector);		
		
		double remainingAngle = (2 * Math.PI) - occupiedAngle;
		double addAngle = remainingAngle / (ring.getRingSize()-1);
	
		logger.debug("placeFusedRing->occupiedAngle: " + Math.toDegrees(occupiedAngle));
		logger.debug("placeFusedRing->remainingAngle: " + Math.toDegrees(remainingAngle));
		logger.debug("placeFusedRing->addAngle: " + Math.toDegrees(addAngle));				


		Atom startAtom;

		double centerX = ringCenter.x;
		double centerY = ringCenter.y;
		
		double xDiff = bondAtom1.getX2D() - bondAtom2.getX2D();
		double yDiff = bondAtom1.getY2D() - bondAtom2.getY2D();
		
		double startAngle;;	
		
		int direction = 1;
		// if bond is vertical
     	if (xDiff == 0)
		{
			logger.debug("placeFusedRing->Bond is vertical");
			//starts with the lower Atom
			if (bondAtom1.getY2D() > bondAtom2.getY2D())
			{
				startAtom = bondAtom1;
			}
			else
			{
				startAtom = bondAtom2;
			}
			
			//changes the drawing direction
			if (centerX < bondAtom1.getX2D())
			{
				direction = 1;
			}
			else
			{
				direction = -1;
			}
		}

		  // if bond is not vertical
		else
		{
			//starts with the left Atom
			if (bondAtom1.getX2D() > bondAtom2.getX2D())
			{
				startAtom = bondAtom1;
			}
			else
			{
				startAtom = bondAtom2;
			}
			
			//changes the drawing direction
			if (centerY - bondAtom1.getY2D() > (centerX - bondAtom1.getX2D()) * yDiff / xDiff)
			{
				direction = 1;
			}
			else
			{
				direction = -1;
			}
		}
		startAngle = GeometryTools.getAngle(startAtom.getX2D() - ringCenter.x, startAtom.getY2D() - ringCenter.y);
	
		Atom currentAtom = startAtom;
        // determine first bond in Ring
        int k = 0;
        for (k = 0; k < ring.getElectronContainerCount(); k++) {
            if (ring.getElectronContainerAt(k) instanceof Bond) break;
        }
        Bond currentBond = (Bond)sharedAtoms.getElectronContainerAt(k);
		Vector atomsToDraw = new Vector();
		for (int i = 0; i < ring.getBondCount() - 2; i++)
		{
			currentBond = ring.getNextBond(currentBond, currentAtom);
			currentAtom = currentBond.getConnectedAtom(currentAtom);
			atomsToDraw.addElement(currentAtom);
		}
		addAngle = addAngle * direction;
			try
			{
				logger.debug("placeFusedRing->startAngle: " + Math.toDegrees(startAngle));
				logger.debug("placeFusedRing->addAngle: " + Math.toDegrees(addAngle));		
				logger.debug("placeFusedRing->startAtom is: " + (molecule.getAtomNumber(startAtom) + 1));
				logger.debug("AtomsToDraw: " + atomPlacer.listNumbers(molecule, atomsToDraw));
			}
			catch(Exception exc)
			{
				logger.debug("Caught an exception while logging in RingPlacer");
			}
		atomPlacer.populatePolygonCorners(atomsToDraw, ringCenter, startAngle, addAngle, radius, false);
	}
	

	/**
	 * True if coordinates have been assigned to all atoms in all rings. 
	 *
	 * @param   rs  The ringset to be checked
	 * @return  True if coordinates have been assigned to all atoms in all rings.    
	 */

	public  boolean allPlaced(RingSet rs)
	{
		for (int i = 0; i < rs.size(); i++)
		{
			if (!((Ring)rs.elementAt(i)).getFlag(CDKConstants.ISPLACED)) 
			{
				return false;
			}
		}
		return true;
	}
	
	/**
	 * Walks throught the atoms of each ring in a ring set and marks 
	 * a ring as PLACED if all of its atoms have been placed.
	 *
	 * @param   rs  The ringset to be checked
	 * @return  True if coordinates have been assigned to all atoms in all rings.    
	 */

	public void checkAndMarkPlaced(RingSet rs)
	{
		Ring ring = null;
		boolean allPlaced = true;
		for (int i = 0; i < rs.size(); i++)
		{
			ring = (Ring)rs.elementAt(i);
			allPlaced = true;
			for (int j = 0; j < ring.getAtomCount(); j++)
			{
				if (!((Atom)ring.getAtomAt(j)).getFlag(CDKConstants.ISPLACED))
				{
					allPlaced = false; 
					break;
				}
			}
			ring.setFlag(CDKConstants.ISPLACED, allPlaced);
		}
	}


	/**
	 * Returns the bridge atoms, that is the outermost atoms in
	 * the chain of more than two atoms which are shared by two rings
	 *
	 * @param   sharedAtoms  The atoms (n > 2) which are shared by two rings
	 * @return  The bridge atoms, i.e. the outermost atoms in the chain of more than two atoms which are shared by two rings  
	 */
	private  Atom[] getBridgeAtoms(AtomContainer sharedAtoms)
	{
		Atom[] bridgeAtoms = new Atom[2];
		Atom atom;
		int counter = 0; 
		for (int f = 0; f < sharedAtoms.getAtomCount(); f++)
		{
			atom = sharedAtoms.getAtomAt(f);	
			if (sharedAtoms.getConnectedAtoms(atom).length == 1)
			{
				bridgeAtoms[counter] = atom;
				counter ++;
			}
		}
		return bridgeAtoms;
	}

	/**
	 * Partition the bonding partners of a given atom into ring atoms and non-ring atoms
	 *
	 * @param   atom  The atom whose bonding partners are to be partitioned
	 * @param   ring  The ring against which the bonding partners are checked
	 * @param   ringAtoms  An AtomContainer to store the ring bonding partners
	 * @param   nonRingAtoms  An AtomContainer to store the non-ring bonding partners
	 */
	public void partitionNonRingPartners(Atom atom, Ring ring, AtomContainer ringAtoms, AtomContainer nonRingAtoms)
	{
		Atom[] atoms = molecule.getConnectedAtoms(atom);
		for (int i = 0; i < atoms.length; i++)
		{
			if (!ring.contains(atoms[i]))
			{
				nonRingAtoms.addAtom(atoms[i]);
			}
			else
			{
				ringAtoms.addAtom(atoms[i]);
			}
		}
	}



	/**
	 * Returns the ring radius of a perfect polygons of size ring.getAtomCount()
	 * The ring radius is the distance of each atom to the ringcenter.
	 *
	 * @param   ring  The ring for which the radius is to calculated
	 * @param   bondLength  The bond length for each bond in the ring
	 * @return  The radius of the ring.   
	 */
	public  double getNativeRingRadius(Ring ring, double bondLength)
	{
		int size = ring.getAtomCount();
		double radius = bondLength / (2 * Math.sin((Math.PI) / size));
		return radius;
	}




	/**
	 * Calculated the center for the first ring so that it can
	 * layed out. Only then, all other rings can be assigned
	 * coordinates relative to it. 
	 *
	 * @param   ring  The ring for which the center is to be calculated
	 * @return  A Vector2d pointing to the new ringcenter   
	 */
	Vector2d getRingCenterOfFirstRing(Ring ring, Vector2d bondVector, double bondLength)
	{
		int size = ring.getAtomCount();
		double radius = bondLength / (2 * Math.sin((Math.PI) / size));
		double newRingPerpendicular = Math.sqrt(Math.pow(radius, 2) - Math.pow(bondLength/2, 2));		
		/* get the angle between the x axis and the bond vector */
		double rotangle = GeometryTools.getAngle(bondVector.x, bondVector.y);
		/* Add 90 Degrees to this angle, this is supposed to be the new ringcenter vector */
		rotangle += Math.PI / 2;
		return new Vector2d(Math.cos(rotangle) * newRingPerpendicular, Math.sin(rotangle) * newRingPerpendicular);
	}


	/**
	 * Layout all rings in the given RingSet that are connected to a given Ring
	 *
	 * @param   rs  The RingSet to be searched for rings connected to Ring
	 * @param   ring  The Ring for which all connected rings in RingSet are to be layed out. 
	 */
	void placeConnectedRings(RingSet rs, Ring ring, int handleType, double bondLength)
	{
		Vector connectedRings = rs.getConnectedRings(ring);
		Ring connectedRing;
		AtomContainer sharedAtoms;
		int sac;
		Point2d oldRingCenter, newRingCenter, sharedAtomsCenter, tempPoint;
		Vector2d tempVector, oldRingCenterVector, newRingCenterVector;
		Bond bond;

//		logger.debug(rs.reportRingList(molecule)); 
		for (int i = 0; i < connectedRings.size(); i++)
		{
			connectedRing = (Ring)connectedRings.elementAt(i);
			if (!connectedRing.getFlag(CDKConstants.ISPLACED))
			{
//				logger.debug(ring.toString(molecule));
//				logger.debug(connectedRing.toString(molecule));				
				sharedAtoms = ring.getIntersection(connectedRing);
				sac = sharedAtoms.getAtomCount();
				logger.debug("placeConnectedRings-> connectedRing: " + (ring.toString(molecule)));
				if ((sac == 2 && handleType == FUSED) ||(sac == 1 && handleType == SPIRO)||(sac > 2 && handleType == BRIDGED))
				{
					sharedAtomsCenter = sharedAtoms.get2DCenter();
					oldRingCenter = ring.get2DCenter();
					tempVector = (new Vector2d(sharedAtomsCenter));
					newRingCenterVector = new Vector2d(tempVector);
					newRingCenterVector.sub(new Vector2d(oldRingCenter));
					oldRingCenterVector = new Vector2d(newRingCenterVector);
					logger.debug("placeConnectedRing -> tempVector: " + tempVector + ", tempVector.length: " + tempVector.length()); 
					logger.debug("placeConnectedRing -> bondCenter: " + sharedAtomsCenter);
					logger.debug("placeConnectedRing -> oldRingCenterVector.length(): " + oldRingCenterVector.length());
					logger.debug("placeConnectedRing -> newRingCenterVector.length(): " + newRingCenterVector.length());					
					tempPoint = new Point2d(sharedAtomsCenter);
					tempPoint.add(newRingCenterVector);
					placeRing(connectedRing, sharedAtoms, sharedAtomsCenter, newRingCenterVector, bondLength);
					connectedRing.setFlag(CDKConstants.ISPLACED, true);
					placeConnectedRings(rs, connectedRing, handleType, bondLength);
				}
			}
		}
	}

	public Molecule getMolecule()
	{
		return this.molecule;
	}

	public void setMolecule(Molecule molecule)
	{
		this.molecule = molecule;
	}

	
	public AtomPlacer getAtomPlacer()
	{
		return this.atomPlacer;
	}

	public void setAtomPlacer(AtomPlacer atomPlacer)
	{
		this.atomPlacer = atomPlacer;
	}
}
