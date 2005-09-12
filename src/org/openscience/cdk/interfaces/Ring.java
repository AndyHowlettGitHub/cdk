/* $RCSfile$
 * $Author$    
 * $Date$    
 * $Revision$
 *
 * Copyright (C) 1997-2005  The Chemistry Development Kit (CDK) project
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
 */
package org.openscience.cdk.interfaces;

/** 
 * Class representing a ring structure in a molecule.
 * A ring is a linear sequence of
 * N atoms interconnected to each other by covalent bonds,
 * such that atom i (1 < i < N) is bonded to
 * atom i-1 and atom i + 1 and atom 1 is bonded to atom N and atom 2.
 *
 * @cdk.module  interfaces
 *
 * @cdk.keyword ring
 */
public interface Ring extends AtomContainer {

	/**
	 * Returns the number of atoms/bonds in this ring.
	 *
	 * @return   The number of atoms/bonds in this ring   
	 */
	public int getRingSize();	

	/**
	 * Returns the next bond in order, relative to a given bond and atom.
	 * Example: Let the ring be composed of 0-1, 1-2, 2-3 and 3-0. 
	 * A request getNextBond(1-2, 2) will return Bond 2-3.
	 *
	 * @param   bond  A bond for which an atom from a consecutive bond is sought
	 * @param   atom  A atom from the bond above to assign a search direction
	 * @return  The next bond in the order given by the above assignment   
	 */
	public Bond getNextBond(Bond bond, Atom atom);
	
	/**
	 * Returns the sum of all bond orders in the ring.
	 *
	 * @return the sum of all bond orders in the ring
	 */
	public int getOrderSum();
	
}
