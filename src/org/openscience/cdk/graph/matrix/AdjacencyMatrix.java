/*  $RCSfile$
 *  $Author$
 *  $Date$
 *  $Revision$
 *
 *  Copyright (C) 2004  The Chemistry Development Kit (CDK) project
 *
 *  Contact: cdk-devel@lists.sourceforge.net
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
package org.openscience.cdk.graph.matrix;

import org.openscience.cdk.Atom;
import org.openscience.cdk.AtomContainer;
import org.openscience.cdk.Bond;
import org.openscience.cdk.ElectronContainer;

/**
 * Calculator for a adjacency matrix representation of this AtomContainer. An
 * adjacency matrix is a matrix of quare NxN matrix, where N is the number of
 * atoms in the AtomContainer. The element i,j of the matrix is 1, if the i-th
 * and the j-th atom in the atomcontainer share a bond. Otherwise it is zero.
 * See {@cdk.cite TRI1992}.
 *
 * @cdk.module  standard
 * @cdk.keyword adjacency matrix
 *
 * @author      steinbeck
 * @cdk.created 2004-07-04
 */
public class AdjacencyMatrix implements GraphMatrix {

	/**
	 * Returns the adjacency matrix for the given AtomContainer.
	 *
     * @param  container The AtomContainer for which the matrix is calculated
	 * @return           A adjacency matrix representating this AtomContainer
	 */
	public static int[][] getMatrix(AtomContainer container) {
		ElectronContainer electronContainer = null;
		int indexAtom1;
		int indexAtom2;
		int[][] conMat = new int[container.getAtomCount()][container.getAtomCount()];
		for (int f = 0; f < container.getElectronContainerCount(); f++){
            electronContainer = container.getElectronContainerAt(f);
			if (electronContainer instanceof Bond)
			{
				Bond bond = (Bond) electronContainer;
				indexAtom1 = container.getAtomNumber(bond.getAtomAt(0));
				indexAtom2 = container.getAtomNumber(bond.getAtomAt(1));
				conMat[indexAtom1][indexAtom2] = 1;
				conMat[indexAtom2][indexAtom1] = 1;
			}
		}
		return conMat;
	}

}

