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
import org.openscience.cdk.Bond;
import org.openscience.cdk.CDKConstants;
import org.openscience.cdk.exception.CDKException;
import org.openscience.cdk.RingSet;
import org.openscience.cdk.ringsearch.AllRingsFinder;
import org.openscience.cdk.qsar.result.*;
import java.util.Vector;
import java.util.Map;
import java.util.Hashtable;

/**
 *  The number of rotatable bonds is given by the SMARTS specified by Daylight on
 *  <a href="http://www.daylight.com/dayhtml_tutorials/languages/smarts/smarts_examples.html#EXMPL">SMARTS tutorial</a>
 *
 * @author      mfe4
 * @cdk.created 2004-11-03
 * @cdk.module  qsar
 * @cdk.set     qsar-descriptors
 */
public class RotatableBondsCountDescriptor implements Descriptor {
	private boolean includeTerminals = false;


	/**
	 *  Constructor for the RotatableBondsCountDescriptor object
	 */
	public RotatableBondsCountDescriptor() { }


	/**
	 *  Gets the specification attribute of the RotatableBondsCountDescriptor
	 *  object
	 *
	 *@return    The specification value
	 */
	public DescriptorSpecification getSpecification() {
        return new DescriptorSpecification(
            "http://qsar.sourceforge.net/dicts/qsar-descriptors:rotatableBondsCount",
		    this.getClass().getName(),
		    "$Id$",
            "The Chemistry Development Kit");
	}


	/**
	 *  Sets the parameters attribute of the RotatableBondsCountDescriptor object
	 *
	 *@param  params            a boolean true means that terminal atoms must be included in the count
	 *@exception  CDKException  Description of the Exception
	 */
	public void setParameters(Object[] params) throws CDKException {
		if (params.length > 1) {
			throw new CDKException("RotatableBondsCount only expects less than two parameters");
		}
		if (!(params[0] instanceof Boolean)) {
			throw new CDKException("The parameter must be of type Boolean");
		}
		// ok, all should be fine
		includeTerminals = ((Boolean) params[0]).booleanValue();
	}


	/**
	 *  Gets the parameters attribute of the RotatableBondsCountDescriptor object
	 *
	 *@return    The parameters value
	 */
	public Object[] getParameters() {
		// return the parameters as used for the descriptor calculation
		Object[] params = new Object[1];
		params[0] = new Boolean(includeTerminals);
		return params;
	}


	/**
	 *  The method calculates the number of rotatable bonds of an atom container.
	 *  If the boolean parameter is set to true, terminal bonds are included.
	 *
	 *@param  ac                AtomContainer
	 *@return                   number of rotatable bonds
	 *@exception  CDKException  Possible Exceptions
	 */
	public DescriptorValue calculate(AtomContainer ac) throws CDKException {
		int rotatableBondsCount = 0;
		Bond[] bonds = ac.getBonds();
		int degree0 = 0;
		int degree1 = 0;
		RingSet ringSet = null;
		AllRingsFinder arf = new AllRingsFinder();
		ringSet = arf.findAllRings(ac);
		Vector ringsWithThisBond = null;
		for (int f = 0; f < bonds.length; f++) {
			ringsWithThisBond = ringSet.getRings(bonds[f]);
			if (ringsWithThisBond.size() > 0) {
				bonds[f].setFlag(CDKConstants.ISINRING, true);
			}
		}
		for (int i = 0; i < bonds.length; i++) {

			Atom[] atoms = ac.getBondAt(i).getAtoms();
			if (bonds[i].getOrder() == CDKConstants.BONDORDER_SINGLE) {
				if ((ac.getMaximumBondOrder(atoms[0]) < 3.0) && (ac.getMaximumBondOrder(atoms[1]) < 3.0)) {
					if (bonds[i].getFlag(CDKConstants.ISINRING) == false) {
						degree0 = ac.getBondCount(atoms[0]);
						degree1 = ac.getBondCount(atoms[1]);
						if ((degree0 == 1) || (degree1 == 1)) {
							if (includeTerminals == true) {
								rotatableBondsCount += 1;
							}
							if (includeTerminals == false) {
								rotatableBondsCount += 0;
							}
						} else {
							rotatableBondsCount += 1;
						}
					}
				}
			}
		}
		return new DescriptorValue(getSpecification(), getParameterNames(), getParameters(), new IntegerResult(rotatableBondsCount));
	}


	/**
	 *  Gets the parameterNames attribute of the RotatableBondsCountDescriptor
	 *  object
	 *
	 *@return    The parameterNames value
	 */
	public String[] getParameterNames() {
		String[] params = new String[1];
		params[0] = "Include Terminal Bonds in the count";
		return params;
	}



	/**
	 *  Gets the parameterType attribute of the RotatableBondsCountDescriptor
	 *  object
	 *
	 *@param  name  Description of the Parameter
	 *@return       The parameterType value
	 */
	public Object getParameterType(String name) {
		Object[] paramTypes = new Object[1];
		paramTypes[0] = new Boolean(true);
		return paramTypes;
	}
}

