/*
 *  $RCSfile$
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
package org.openscience.cdk.qsar;

import org.openscience.cdk.Atom;
import org.openscience.cdk.AtomContainer;
import org.openscience.cdk.tools.HydrogenAdder;
import org.openscience.cdk.exception.CDKException;
import org.openscience.cdk.graph.invariant.ConjugatedPiSystemsDetector;
import org.openscience.cdk.aromaticity.HueckelAromaticityDetector;
import org.openscience.cdk.SetOfAtomContainers;
import java.util.Map;
import java.util.Hashtable;
import org.openscience.cdk.Molecule;
import org.openscience.cdk.qsar.result.*;

/**
 *  This class checks if two atoms have pi-contact (this is true when there is
 *  one and the same conjugated pi-system which contains both atoms, or directly
 *  linked neighboors of the atoms).
 *
 * <p>This descriptor uses these parameters:
 * <table border="1">
 *   <tr>
 *     <td>Name</td>
 *     <td>Default</td>
 *     <td>Description</td>
 *   </tr>
 *   <tr>
 *     <td>firstAtom</td>
 *     <td>0</td>
 *     <td>The position of the first atom</td>
 *   </tr>
 *   <tr>
 *     <td>secondAtom</td>
 *     <td>0</td>
 *     <td>The position of the second atom</td>
 *   </tr>
 *   <tr>
 *     <td>checkAromaticity</td>
 *     <td>false</td>
 *     <td>True is the aromaticity has to be checked</td>
 *   </tr>
 * </table>
 *
 *@author         mfe4
 *@cdk.created    2004-11-03
 *@cdk.module     qsar
 *@cdk.set        qsar-descriptors
 */
public class PiContactDetectionDescriptor implements Descriptor {

	private int firstAtom = 0;
	private int secondAtom = 0;
	private boolean checkAromaticity = false;
	SetOfAtomContainers acSet = null;


	/**
	 *  Constructor for the PiContactDetectionDescriptor object
	 */
	public PiContactDetectionDescriptor() { }


	/**
	 *  Gets the specification attribute of the PiContactDetectionDescriptor object
	 *
	 *@return    The specification value
	 */
	public DescriptorSpecification getSpecification() {
		return new DescriptorSpecification(
				"http://qsar.sourceforge.net/dicts/qsar-descriptors:piContact",
				this.getClass().getName(),
				"$Id$",
				"The Chemistry Development Kit");
	}


	/**
	 *  Sets the parameters attribute of the PiContactDetectionDescriptor object
	 *
	 *@param  params            Parameters are 2 integers (atoms positions) and a
	 *      boolean (true if is needed a checkAromaticity)
	 *@exception  CDKException  Description of the Exception
	 */
	public void setParameters(Object[] params) throws CDKException {
		if (params.length > 3) {
			throw new CDKException("PiContactDetectionDescriptor only expects 3 parameters");
		}
		if (!(params[0] instanceof Integer)) {
			throw new CDKException("The first parameter must be of type Integer");
		}
		if (!(params[1] instanceof Integer)) {
			throw new CDKException("The second parameter must be of type Integer");
		}
		if (!(params[2] instanceof Boolean)) {
			throw new CDKException("The third parameter must be of type Boolean");
		}
		firstAtom = ((Integer) params[0]).intValue();
		secondAtom = ((Integer) params[1]).intValue();
		checkAromaticity = ((Boolean) params[2]).booleanValue();
	}


	/**
	 *  Gets the parameters attribute of the PiContactDetectionDescriptor object
	 *
	 *@return    The parameters value
	 */
	public Object[] getParameters() {
		// return the parameters as used for the descriptor calculation
		Object[] params = new Object[3];
		params[0] = new Integer(firstAtom);
		params[1] = new Integer(secondAtom);
		params[2] = new Boolean(checkAromaticity);
		return params;
	}


	/**
	 *  The method returns if two atoms have pi-contact
	 *
	 *@param  ac                AtomContainer
	 *@return                   true if the atoms have pi-contact
	 *@exception  CDKException  Possible Exceptions
	 */
	public DescriptorValue calculate(AtomContainer ac) throws CDKException {
		Molecule mol = new Molecule(ac);
		if (checkAromaticity) {
			HueckelAromaticityDetector.detectAromaticity(mol);
		}
		boolean piContact = false;
		int counter = 0;

		Atom first = ac.getAtomAt(firstAtom);
		Atom second = ac.getAtomAt(secondAtom);
		acSet = ConjugatedPiSystemsDetector.detect(mol);
		AtomContainer[] detected = acSet.getAtomContainers();

		Atom[] neighboorsFirst = mol.getConnectedAtoms(first);
		Atom[] neighboorsSecond = mol.getConnectedAtoms(second);

		for (int i = 0; i < detected.length; i++) {
			if (detected[i].contains(first) && detected[i].contains(second)) {
				counter += 1;
				break;
			}
			if (isANeighboorsInAnAtomContainer(neighboorsFirst, detected[i]) && isANeighboorsInAnAtomContainer(neighboorsSecond, detected[i])) {
				counter += 1;
				break;
			}
		}

		if (counter > 0) {
			piContact = true;
		}
		return new DescriptorValue(getSpecification(), getParameterNames(), getParameters(), new BooleanResult(piContact));
	}


	/**
	 *  Gets if neighboors of an atom are in an atom container
	 *
	 *@param  neighs  array of atoms
	 *@param  ac      AtomContainer
	 *@return         The boolean result
	 */
	private boolean isANeighboorsInAnAtomContainer(Atom[] neighs, AtomContainer ac) {
		boolean isIn = false;
		int count = 0;
		for (int i = 0; i < neighs.length; i++) {
			if (ac.contains(neighs[i])) {
				count += 1;
			}
		}
		if (count > 0) {
			isIn = true;
		}
		return isIn;
	}


	/**
	 *  Gets the parameterNames attribute of the PiContactDetectionDescriptor
	 *  object
	 *
	 *@return    The parameterNames value
	 */
	public String[] getParameterNames() {
		String[] params = new String[3];
		params[0] = "firstAtom";
		params[1] = "secondAtom";
		params[2] = "checkAromaticity";
		return params;
	}


	/**
	 *  Gets the parameterType attribute of the PiContactDetectionDescriptor object
	 *
	 *@param  name  Description of the Parameter
	 *@return       The parameterType value
	 */
	public Object getParameterType(String name) {
		Object[] paramTypes = new Object[3];
		paramTypes[0] = new Integer(1);
		paramTypes[1] = new Integer(1);
		paramTypes[2] = new Boolean(true);
		return paramTypes;
	}
}

