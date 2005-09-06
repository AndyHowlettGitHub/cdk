/*
 *  $RCSfile$
 *  $Author$
 *  $Date$
 *  $Revision$
 *
 *  Copyright (C) 1997-2005  The Chemistry Development Kit (CDK) project
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
package org.openscience.cdk.fingerprint;

import java.util.BitSet;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Vector;

import org.openscience.cdk.Atom;
import org.openscience.cdk.interfaces.AtomContainer;
import org.openscience.cdk.Bond;
import org.openscience.cdk.CDKConstants;
import org.openscience.cdk.aromaticity.HueckelAromaticityDetector;
import org.openscience.cdk.ringsearch.AllRingsFinder;
import org.openscience.cdk.tools.LoggingTool;

/**
 *  Generates a Fingerprint for a given AtomContainer. Fingerprints are
 *  one-dimensional bit arrays, where bits are set according to a the occurence
 *  of a particular structural feature (See for example the Daylight inc. theory
 *  manual for more information) Fingerprints allow for a fast screening step to
 *  excluded candidates for a substructure search in a database. They are also a
 *  means for determining the similarity of chemical structures. <p>
 *
 *  A fingerprint is generated for an AtomContainer with this code: <pre>
 *   Molecule molecule = new Molecule();
 *   BitSet fingerprint = Fingerprinter.getFingerprint(molecule);s
 * </pre> <p>
 *
 *  The FingerPrinter assumes that hydrogens are explicitely given! <p>
 *
 *  <font color="#FF0000">Warning: The aromaticity detection for this
 *  SmilesGenerator relies on AllRingsFinder, which is known to take very long
 *  for some molecules with many cycles or special cyclic topologies. Thus, the
 *  AllRingsFinder has a built-in timeout of 5 seconds after which it aborts and
 *  throws an Exception. If you want your SMILES generated at any expense, you
 *  need to create your own AllRingsFinder, set the timeout to a higher value,
 *  and assign it to this SmilesGenerator. In the vast majority of cases,
 *  however, the defaults will be fine. </font> <p>
 *
 *  <font color="#FF0000">Another Warning : The daylight manual says:
 *  "Fingerprints are not so definite: if a fingerprint indicates a pattern is
 *  missing then it certainly is, but it can only indicate a pattern's presence
 *  with some probability." In the case of very small molecules, the probability
 *  that you get the same fingerprint for different molecules is high. </font>
 *  </p>
 *
 *@author         steinbeck
 *@cdk.created    2002-02-24
 *@cdk.keyword    fingerprint
 *@cdk.keyword    similarity
 */
public class Fingerprinter
{
	static int defaultSize = 1024;
	static int defaultSearchDepth = 6;
	static Hashtable pathes;
	final static boolean debug = true;
	static int debugCounter = 0;

	private static LoggingTool logger = new LoggingTool(Fingerprinter.class);


	/**
	 *  Generates a fingerprint of the default size for the given AtomContainer
	 *
	 *@param  ac                       The AtomContainer for which a Fingerprint is
	 *      generated default size of the fingerprint= 1024 default depth of
	 *      search= 6
	 *@return                          The Fingerprint (A one-dimensional bit
	 *      array)
	 *@exception  Exception            Description of the Exception
	 */
	public static BitSet getFingerprint(AtomContainer ac) throws Exception
	{
		return getFingerprint(ac, defaultSize, defaultSearchDepth, null);
	}


	/**
	 *  Generates a fingerprint of a given size for the given AtomContainer
	 *
	 *@param  ac                       The AtomContainer for which a Fingerprint is
	 *      generated
	 *@param  size                     The desired size of the fingerprint
	 *@return                          The Fingerprint (A one-dimensional bit
	 *      array)
	 *@exception  Exception            Description of the Exception
	 */
	public static BitSet getFingerprint(AtomContainer ac, int size) throws Exception
	{
		return getFingerprint(ac, size, defaultSearchDepth, null);
	}

	/**
	 *  Generates a fingerprint of a given size for the given AtomContainer
	 *
	 *@param  ac		The AtomContainer for which a Fingerprint is generated
	 *@param  size          The desired size of the fingerprint
	 *@param  searchDepth   The desired depth of search
	 *@return               The Fingerprint (A one-dimensional bit array)
	 *@exception  Exception thrown if something goes wrong
	 */
	public static BitSet getFingerprint(AtomContainer ac, int size, int searchDepth) throws Exception
	{
		return getFingerprint(ac, size, searchDepth, null);

	}
		
	/**
	 *  Generates a fingerprint of a given size for the given AtomContainer
	 *
	 *@param  ac		The AtomContainer for which a Fingerprint is generated
	 *@param  size          The desired size of the fingerprint
	 *@param  searchDepth   The desired depth of search
	 *@param  ringFinder           The AllRingsFinder to be used by the aromaticity detection
	 *@return               The Fingerprint (A one-dimensional bit array)
	 *@exception  Exception thrown if something goes wrong
	 */
	public static BitSet getFingerprint(AtomContainer ac, int size, int searchDepth, AllRingsFinder ringFinder) throws Exception
	{
		String path = null;
		int position = -1;
		boolean isAromatic = false;
		// logger.debug("Entering Fingerprinter");
		// logger.debug("Starting Aromaticity Detection");
		//long before = System.currentTimeMillis();
		isAromatic = HueckelAromaticityDetector.detectAromaticity(ac, false, ringFinder);
		//long after = System.currentTimeMillis();
		//logger.debug("time for aromaticity calculation: " + (after - before) + " milliseconds");
		// logger.debug("Finished Aromaticity Detection");
		findPathes(ac, searchDepth);
		BitSet bs = new BitSet(size);
		for (Enumeration e = pathes.elements(); e.hasMoreElements(); )
		{
			path = (String) e.nextElement();
			position = new java.util.Random(path.hashCode()).nextInt(size);
			logger.debug("Setting bit " + position + " for " + path);
			bs.set(position);
		}
		return bs;
	}


	/**
	 *  Checks whether all the positive bits in BitSet bs2 occur in BitSet bs1. If
	 *  so, the molecular structure from which bs2 was generated is a possible
	 *  substructure of bs1. <p>
	 *
	 *  Example: <pre>
	 *  Molecule mol = MoleculeFactory.makeIndole();
	 *  BitSet bs = Fingerprinter.getFingerprint(mol);
	 *  Molecule frag1 = MoleculeFactory.makePyrrole();
	 *  BitSet bs1 = Fingerprinter.getFingerprint(frag1);
	 *  if (Fingerprinter.isSubset(bs, bs1)) {
	 *      System.out.println("Pyrrole is subset of Indole.");
	 *  }
	 *  </pre>
	 *
	 *@param  bs1     The reference BitSet
	 *@param  bs2     The BitSet which is compared with bs1
	 *@return         True, if bs2 is a subset of bs2
	 *@cdk.keyword    substructure search
	 */
	public static boolean isSubset(BitSet bs1, BitSet bs2)
	{
		BitSet clone = (BitSet) bs1.clone();
		clone.and(bs2);
		if (clone.equals(bs2))
		{
			return true;
		}
		return false;
	}



	/**
	 *  Gets all pathes of length 1 up to the length given by the 'searchDepth"
	 *  parameter. The pathes are aquired by a number of depth first searches, one
	 *  for each atom.
	 *
	 *@param  ac           The AtomContainer which is to be searched.
	 *@param  searchDepth  Description of the Parameter
	 */
	static void findPathes(AtomContainer ac, int searchDepth)
	{
		pathes = new Hashtable();
		Vector currentPath = new Vector();
		debugCounter = 0;
		for (int f = 0; f < ac.getAtomCount(); f++)
		{
			currentPath.removeAllElements();
			currentPath.addElement(ac.getAtomAt(f));
			checkAndStore(currentPath);
			logger.info("Starting at atom " + (f + 1) + " with symbol " + ac.getAtomAt(f).getSymbol());
			depthFirstSearch(ac, ac.getAtomAt(f), currentPath, 0, searchDepth);

		}
	}


	/**
	 *  Performs a recursive depth first search
	 *
	 *@param  ac            The AtomContainer to be searched
	 *@param  root          The Atom to start the search at
	 *@param  currentPath   The Path that has been generated so far
	 *@param  currentDepth  The current depth in this recursive search
	 *@param  searchDepth   Description of the Parameter
	 */
	static void depthFirstSearch(AtomContainer ac, org.openscience.cdk.interfaces.Atom root, Vector currentPath, int currentDepth, int searchDepth)
	{
		org.openscience.cdk.interfaces.Bond[] bonds = ac.getConnectedBonds(root);

		/*
		 *  try
		 *  {
		 *  logger.debug("Currently at atom no. " + (ac.getAtomNumber(root)  + 1) + " with symbol "  + root.getSymbol());
		 *  }
		 *  catch(Exception exc){}
		 */
		org.openscience.cdk.interfaces.Atom nextAtom = null;

		/*
		 *  try
		 *  {
		 *  logger.debug("Currently at atom no. " + (ac.getAtomNumber(root)  + 1) + " with symbol "  + root.getSymbol());
		 *  }
		 *  catch(Exception exc){}
		 */
		Atom tempAtom = null;
		Vector newPath = null;
		String symbol = null;
		String bondSymbol = null;
		currentDepth++;
		//logger.info("New incremented searchDepth " + currentDepth);
		//logger.info("Current Path is: " + currentPath);
		for (int f = 0; f < bonds.length; f++)
		{
			nextAtom = bonds[f].getConnectedAtom(root);

			/*
			 *  try
			 *  {
			 *  logger.debug("Found connected atom no. " + (ac.getAtomNumber(nextAtom) + 1) + " with symbol "  + nextAtom.getSymbol() + "...");
			 *  }
			 *  catch(Exception exc){}
			 */
			if (!currentPath.contains(nextAtom))
			{
				newPath = new Vector(currentPath);
				bondSymbol = getBondSymbol(bonds[f]);
				newPath.addElement(bondSymbol);
				//logger.debug("Bond has symbol " + bondSymbol);
				newPath.addElement(nextAtom);
				checkAndStore(newPath);

				if (currentDepth < searchDepth)
				{
					depthFirstSearch(ac, nextAtom, newPath, currentDepth, searchDepth);
					//logger.debug("DepthFirstSearch Fallback to searchDepth " + currentDepth);
				}
			} else
			{
				//logger.debug("... already visited!");
			}
		}
	}


	/**
	 *  Description of the Method
	 *
	 *@param  newPath  Description of the Parameter
	 */
	private static void checkAndStore(Vector newPath)
	{
		String newPathString = "";
		for (int f = 0; f < newPath.size(); f++)
		{
			if ((newPath.elementAt(f)) instanceof org.openscience.cdk.Atom)
			{
				newPathString += convertSymbol(((Atom) newPath.elementAt(f)).getSymbol());
			} else
			{
				newPathString += (String) newPath.elementAt(f);
			}
		}
		//logger.debug("Checking for existence of Path " +  newPathString);
		String storePath = new String(newPathString);
		String reversePath = new StringBuffer(storePath).reverse().toString();
		/*
		 *  Pathes can be found twice (search from one or the other side)
		 *  so they will occur in reversed order. We only handle the
		 *  lexicographically smaller path (This is an arbitrary choice)
		 */
		if (reversePath.compareTo(newPathString) < 0)
		{
			/*
			 *  reversePath is smaller than newPath
			 *  so we keep reversePath
			 */
			storePath = reversePath;
		}
		if (!pathes.containsKey(storePath))
		{
			pathes.put(storePath, storePath);
			if (debug)
			{
				debugCounter++;
				//logger.debug("Storing path no. " + debugCounter + ": " +  storePath + ", Hash: " + storePath.hashCode());
			}
		} else
		{
			//logger.debug("Path " + storePath + " already contained");
		}
	}


	/**
	 *  Description of the Method
	 *
	 *@param  symbol  Description of the Parameter
	 *@return         Description of the Return Value
	 */
	private static String convertSymbol(String symbol)
	{
		String returnSymbol = symbol;
		if (symbol.equals("Cl"))
		{
			symbol = "X";
		} else if (symbol.equals("Si"))
		{
			symbol = "Y";
		} else if (symbol.equals("Br"))
		{
			symbol = "Z";
		}
		return returnSymbol;
	}


	/**
	 *  Gets the bondSymbol attribute of the Fingerprinter class
	 *
	 *@param  bond  Description of the Parameter
	 *@return       The bondSymbol value
	 */
	private static String getBondSymbol(org.openscience.cdk.interfaces.Bond bond)
	{
		String bondSymbol = "";
		if (bond.getFlag(CDKConstants.ISAROMATIC))
		{
			bondSymbol = ":";
		} else if (bond.getOrder() == 1)
		{
			bondSymbol = "-";
		} else if (bond.getOrder() == 2)
		{
			bondSymbol = "=";
		} else if (bond.getOrder() == 3)
		{
			bondSymbol = "#";
		}
		return bondSymbol;
	}


	/**
	 *  Description of the Method
	 *
	 *@param  bs1  Description of the Parameter
	 *@param  bs2  Description of the Parameter
	 */
	public static void listDifferences(BitSet bs1, BitSet bs2)
	{
		logger.debug("Listing bit positions set in bs2 but not in bs1");
		for (int f = 0; f < bs2.size(); f++)
		{
			if (bs2.get(f) && !bs1.get(f))
			{
				logger.debug("Bit " + f + " not set in bs1");
			}
		}
	}

}

