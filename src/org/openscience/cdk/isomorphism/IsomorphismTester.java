/*  $RCSfile$
 *  $Author$
 *  $Date$
 *  $Revision$
 *
 *  Copyright (C) 2001-2004  The Chemistry Development Kit (CDK) project
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
 *
 */
package org.openscience.cdk.isomorphism;

import java.util.Arrays;

import org.openscience.cdk.Atom;
import org.openscience.cdk.Molecule;
import org.openscience.cdk.exception.NoSuchAtomException;
import org.openscience.cdk.graph.invariant.MorganNumbersTools;

/**
 * A too simplistic implementation of an isomorphism test for chemical graphs.
 *
 * <p><b>Important:</b> as it uses the MorganNumbersTools it does not take bond
 * order into account.
 *
 * <p>Alternatively, you can use the UniversalIsomorphismTester.
 *
 * @cdk.module standard
 *
 * @author     steinbeck
 * @cdk.created    2001-09-10
 *
 * @cdk.keyword    isomorphism
 *
 * @see        org.openscience.cdk.graph.invariant.MorganNumbersTools
 * @see        org.openscience.cdk.isomorphism.UniversalIsomorphismTester
 */
public class IsomorphismTester implements java.io.Serializable
{

	int[] baseTable;
	int[] sortedBaseTable;
	int[] compareTable;
	int[] sortedCompareTable;
	Molecule base = null;
	Molecule compare = null;


	/**
	 *  Constructor for the IsomorphismTester object
	 */
	public IsomorphismTester() { }


	/**
	 *  Constructor for the IsomorphismTester object
	 */
	public IsomorphismTester(Molecule mol) throws NoSuchAtomException
	{
		setBaseTable(mol);
	}


	/**
	 *  Checks whether a given molecule is isomorphic with the one
	 *  that has been assigned to this IsomorphismTester at contruction time
	 *
	 * @param  mol1                     A first molecule to check against the second one
	 * @param  mol2                     A second molecule to check against the first
	 * @return                          True, if the two molecules are isomorphic
	 */
	public boolean isIsomorphic(Molecule mol1, Molecule mol2) {
		setBaseTable(mol1);
		return isIsomorphic(mol2);
	}


	/**
	 *  Checks whether a given molecule is isomorphic with the one 
	 *  that has been assigned to this IsomorphismTester at contruction time
	 *
	 * @param  mol2                     A molecule to check 
	 * @return                          True, if the two molecules are isomorphic 
	 * @exception  NoSuchAtomException  A problem with the structures
	 */
	public boolean isIsomorphic(Molecule mol2) {
		boolean found;
		Atom atom1 = null;
		Atom atom2 = null;
		setCompareTable(mol2);
		for (int f = 0; f < sortedBaseTable.length; f++)
		{
			if (sortedBaseTable[f] != sortedCompareTable[f])
			{
				return false;
			}
		}

		for (int f = 0; f < baseTable.length; f++)
		{
			found = false;
			for (int g = 0; g < compareTable.length; g++)
			{
				if (baseTable[f] == compareTable[g])
				{
					atom1 = base.getAtomAt(f);
					atom2 = compare.getAtomAt(g);
					if (!(atom1.getSymbol().equals(atom2.getSymbol())) && 
                          atom1.getHydrogenCount() == atom2.getHydrogenCount())
					{
						return false;
					}
					found = true;
				}
			}
			if (!found)
			{
				return false;
			}
		}
		return true;
	}

	public void report()
	{
		String s = "";
		for (int f = 0; f < sortedBaseTable.length; f++)
		{
			s += sortedBaseTable[f] + " ";
		}
		System.out.println(s);
		s = "";
		for (int f = 0; f < sortedCompareTable.length; f++)
		{
			s += sortedCompareTable[f] + " ";
		}
		System.out.println(s);

	}


	/**
	 *  Sets the BaseTable attribute of the IsomorphismTester object
	 *
	 * @param  mol                      The new BaseTable value
	 */
	private void setBaseTable(Molecule mol) {
		this.base = mol;
		this.baseTable = MorganNumbersTools.getMorganNumbers(base);
		sortedBaseTable = new int[baseTable.length];
		System.arraycopy(baseTable, 0, sortedBaseTable, 0, baseTable.length);
		Arrays.sort(sortedBaseTable);
	}


	/**
	 *  Sets the CompareTable attribute of the IsomorphismTester object
	 *
	 * @param  mol                      The new CompareTable value
	 */
	private void setCompareTable(Molecule mol) {
		this.compare = mol;
		this.compareTable = MorganNumbersTools.getMorganNumbers(compare);
		sortedCompareTable = new int[compareTable.length];
		System.arraycopy(compareTable, 0, sortedCompareTable, 0, compareTable.length);
		Arrays.sort(sortedCompareTable);

	}
}

