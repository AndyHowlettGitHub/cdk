/* $RCSfile$
 * $Author$
 * $Date$
 * $Revision$
 * 
 * Copyright (C) 2004-2005  The Chemistry Development Kit (CDK) project
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
 */
package org.openscience.cdk.interfaces;

import java.util.Collection;
import java.util.Hashtable;

/**
 * A Strand is an AtomContainer which stores additional strand specific
 * informations for a group of Atoms.
 *
 * @cdk.module  data
 * @cdk.created 2004-12-20
 * @author      Martin Eklund <martin.eklund@farmbio.uu.se>
 */
public interface Strand extends AtomContainer {

	/**
	 * Retrieve the strand name.
	 *
	 * @return The name of the Strand object
	 */
	public String getStrandName();
	
	/**
	 * Retrieve the strand type.
	 *
	 * @return The type of the Strand object
	 */
	public String getStrandType();
	
	/**
	 * Set the name of the Strand object.
	 *
	 * @param cStrandName  The new name for this strand
	 */
	public void setStrandName(String cStrandName);
	
	/**
	 * Set the type of the Strand object.
	 *
	 * @param cStrandType  The new type for this strand
	 */
	public void setStrandType(String cStrandType);
	
	/**
	 * Adds the atom oAtom without specifying a Monomer or a Strand. Therefore the
	 * atom gets added to a Monomer of type UNKNOWN in a Strand of type UNKNOWN.
	 *
	 * @param oAtom  The atom to add
	 */
	public void addAtom(Atom oAtom);
	
	/**
	 * Adds the atom oAtom to a specific Monomer.
	 *
	 * @param oAtom  The atom to add
	 * @param oMonomer  The monomer the atom belongs to
	 */
	public void addAtom(Atom oAtom, Monomer oMonomer);
	
	/**
	 * Return the number of monomers present in the Strand.
	 *
	 * @return number of monomers
	 */
	public int getMonomerCount();
	
	/**
	 * Retrieve a Monomer object by specifying its name.
	 *
	 * @param cName  The name of the monomer to look for
	 * @return The Monomer object which was asked for
	 */
	public org.openscience.cdk.Monomer getMonomer(String cName);
	
	/**
	 * Returns a collection of the names of all <code>Monomer</code>s in this
	 * polymer.
	 *
	 * @return a <code>Collection</code> of all the monomer names.
	 */
	public Collection getMonomerNames();
	
	/**
	 * Removes a particular monomer, specified by its name.
	 * 
	 * @param name
	 */
	public void removeMonomer(String name);
	
	/**
	 * Returns the monomers in this strand. 
	 * 
	 * @return hashtable containing the monomers in the strand.
	 */
	public Hashtable getMonomers();
}
