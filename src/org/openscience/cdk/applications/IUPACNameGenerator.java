/*
 *  $RCSfile$
 *  $Author$
 *  $Date$
 *  $Revision$
 *
 *  Copyright (C) 2002-2003  The Chemistry Development Kit (CDK) project
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
package org.openscience.cdk.applications;

import org.openscience.cdk.Molecule;
import org.openscience.cdk.smiles.*;
import org.openscience.cdk.iupac.generator.*;
import org.openscience.cdk.tools.LoggingTool;
import freeware.PrintfFormat;
import java.util.Enumeration;
import java.util.Locale;
import java.util.Vector;

/**
 *  This class implements a IUPAC name generator.
 *
 *@author     Egon Willighagen
 *@created    October 21, 2003
 *@keyword    IUPAC name
 */
public class IUPACNameGenerator
{

	/**
	 *  The main program for the IUPACNameGenerator class
	 *
	 *@param  args  The command line arguments
	 */
	public static void main(String[] args)
	{

		// to make sure the CDK LoggingTool is configured
		LoggingTool logger = new LoggingTool(true);

		Locale l = new Locale("en", "US");
		String smiles = "";
		if (args.length == 1)
		{
			smiles = args[0];
		} else if (args.length > 1)
		{
			// parse options
			for (int i = 0; i < args.length - 1; i++)
			{
				String opt = args[i];
				if ("--dutch".equalsIgnoreCase(opt))
				{
					l = new Locale("nl", "NL");
				} else if ("--german".equalsIgnoreCase(opt))
				{
					l = new Locale("de", "DE");
				} else
				{
					/*
					 *  This is a command line application            *
					 *  Do not convert these System.out/err.println() *
					 *  to logger statements
					 */
					System.err.println("Unknown option: " + opt);
					System.exit(1);
				}
			}

			smiles = args[args.length - 1];
		} else
		{
			/*
			 *  This is a command line application            *
			 *  Do not convert these System.out/err.println() *
			 *  to logger statements
			 */
			System.out.println("Syntax : java org.openscience.cdk.applications.IUPACNameGenerator <SMILES>");
			System.exit(0);
		}

		SmilesParser sp = new SmilesParser();
		Molecule mol = null;
		try
		{
			mol = sp.parseSmiles(smiles);
		} catch (Exception exc)
		{
			/*
			 *  This is a command line application            *
			 *  Do not convert these System.out/err.println() *
			 *  to logger statements
			 */
			System.out.println("Problem parsing SMILES: " + exc.toString());
			exc.printStackTrace(System.out);
		}
		if (mol != null)
		{
			org.openscience.cdk.iupac.generator.IUPACNameGenerator gen =
					new org.openscience.cdk.iupac.generator.IUPACNameGenerator(l);
			int atomCount = mol.getAtomCount();
			gen.generateName(mol);
			IUPACName name = (IUPACName) gen.getName();
			/*
			 *  This is a command line application            *
			 *  Do not convert these System.out/err.println() *
			 *  to logger statements
			 */
			System.out.println("IUPAC name: " + name.getName());
			System.out.println("Full (" + name.size() + " parts):");
			System.out.print(name.toString());
			int namedAtomCount = atomCount - mol.getAtomCount();
			PrintfFormat format = new PrintfFormat("%3.1lf");
			/*
			 *  This is a command line application            *
			 *  Do not convert these System.out/err.println() *
			 *  to logger statements
			 */
			System.out.println("Ratio named: " +
					format.sprintf(100.0 * (double) namedAtomCount / (double) atomCount) +
					"% (" + namedAtomCount + "/" + atomCount + ")");
		}
	}

}

