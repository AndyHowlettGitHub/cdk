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
 *  Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110-1301 USA.
 */
package org.openscience.cdk.qsar.descriptors.atomic;

import org.openscience.cdk.interfaces.AtomContainer;
import org.openscience.cdk.interfaces.AtomType;
import org.openscience.cdk.CDKConstants;
import org.openscience.cdk.config.AtomTypeFactory;
import org.openscience.cdk.exception.CDKException;
import org.openscience.cdk.qsar.result.IntegerResult;
import org.openscience.cdk.qsar.IDescriptor;
import org.openscience.cdk.qsar.DescriptorSpecification;
import org.openscience.cdk.qsar.DescriptorValue;
import org.openscience.cdk.tools.LoggingTool;

/**
 *  This class returns the hybridization of an atom.
 *
 *  <p>This class try to find a SIMPLE WAY the molecular geometry for wollowing from
 *    Valence Shell ElectronPair Reupulsion or VSEPR model and at the same time its
 *    hybridization of atoms in a molecule.
 *
 *  <p>The basic premise of the model is that the electrons are paired in a molecule 
 *    and that the molecule geometry is determined only by the repulsion between the pairs. 
 *    The geomtry adopted by a molecule is then the one in which the reulsions are minimized.
 *
 *  <p>It counts the number of electron pairs in the lewis dot diagram which
 *   are attached to an atom. Then uses the following table.
 * <pre>
 *    pairs on  	   hybridization 
 *    an atom              of the atom          geomtry             number for CDK.Constants
 *  ******************************************************************************************
 *      2                     sp                  linear                        1
 *      3                     sp^2            trigonal planarb                  2
 *      4                     sp^3              tetrahedral                     3
 *      5                     sp^3d         trigonal bipyramid                  4
 *      6                     sp^3d^2           octahedral                      5
 *      7                     sp^3d^3       pentagonal bipyramid                6
 *      8                     sp^3d^4        square antiprim                    7
 *      9                     sp^3d^5     tricapped trigonal prism              8
 * </pre>
 *
 *  <p>This table only works if the central atom is a p-block element 
 *   (groups IIA through VIIIA), not a transition metal.
 *
 *
 * <p>This descriptor uses these parameters:
 * <table border="1">
 *   <tr>
 *     <td>Name</td>
 *     <td>Default</td>
 *     <td>Description</td>
 *   </tr>
 *   <tr>
 *     <td>targetPosition</td>
 *     <td>0</td>
 *     <td>The position of the target atom</td>
 *   </tr>
 * </table>
 *
 *@author         mrc
 *@cdk.created    2005-03-24
 *@cdk.module     qsar
 *@cdk.set        qsar-descriptors
 * @cdk.dictref qsar-descriptors:atomHybridization
 */
public class AtomHybridizationVSEPRDescriptor implements IDescriptor {

	private int targetPosition = 0;
	org.openscience.cdk.interfaces.Atom atom = null;
	private LoggingTool logger;
  private static AtomTypeFactory atomATF=null;
	
	/**
	 *  Constructor for the AtomHybridizationVSEPRDescriptor object
	 */
	public AtomHybridizationVSEPRDescriptor() {
		logger = new LoggingTool(this);
	}


	/**
	 *  Gets the specification attribute of the AtomHybridizationVSEPRDescriptor object
	 *
	 *@return    The specification value
	 */
	public DescriptorSpecification getSpecification() {
		return new DescriptorSpecification(
				"http://www.blueobelisk.org/ontologies/chemoinformatics-algorithms/#atomHybridization",
				this.getClass().getName(),
				"$Id$",
				"The Chemistry Development Kit");
	}


	/**
	 *  Sets the parameters attribute of the AtomHybridizationVSEPRDescriptor object
	 *
	 *@param  params            The parameter is the atom position
	 *@exception  CDKException  Description of the Exception
	 */
	public void setParameters(Object[] params) throws CDKException {
		if (params.length > 1) {
			throw new CDKException("AtomHybridizationVSEPRDescriptor only expects one parameter");
		}
		if (!(params[0] instanceof Integer)) {
			throw new CDKException("The parameter must be of type Integer");
		}
		targetPosition = ((Integer) params[0]).intValue();
	}


	/**
	 *  Gets the parameters attribute of the AtomHybridizationVSEPRDescriptor object
	 *
	 *@return    The parameters value
	 */
	public Object[] getParameters() {
		Object[] params = new Object[1];
		params[0] = new Integer(targetPosition);
		return params;
	}


	/**
	 *  This method calculates the hybridization of an atom.
	 *
	 *@param  container         Parameter is the atom container.
	 *@return                   The hybridization
	 *@exception  CDKException  Description of the Exception
	 */

	public DescriptorValue calculate(AtomContainer container) throws CDKException
	{
		atom = container.getAtomAt(targetPosition);
		
		AtomType atomType = findMatchingAtomType(container, atom);
		
		double bondOrderSum = container.getBondOrderSum(atom);
		int charge = atom.getFormalCharge();
		int hcount = atom.getHydrogenCount();
		int valency = atomType.getValency();
		double nLonePair = (valency - ( hcount + bondOrderSum ) - charge) / 2;
		
		int hybridization = (int)nLonePair + ( hcount + container.getConnectedAtoms(atom).length );
		
		logger.debug("ATOM : bondOrderSum " + bondOrderSum + ", charge " + charge + ", hcount " + hcount + 
		             ", valency "  + valency + ", nLonePair " + nLonePair + ", hybridization "  + hybridization);
		
		int hybridizationCDK = 0;
		switch (hybridization) 
		{
			case 2:
				hybridizationCDK = CDKConstants.HYBRIDIZATION_SP1;break;
			case 3:
				hybridizationCDK = CDKConstants.HYBRIDIZATION_SP2;break;
			case 4:
				hybridizationCDK = CDKConstants.HYBRIDIZATION_SP3;break;
			case 5:
				hybridizationCDK = CDKConstants.HYBRIDIZATION_SP3D1;break;
			case 6:
				hybridizationCDK = CDKConstants.HYBRIDIZATION_SP3D2;break;
			case 7:
				hybridizationCDK = CDKConstants.HYBRIDIZATION_SP3D3;break;
			case 8:
				hybridizationCDK = CDKConstants.HYBRIDIZATION_SP3D4;break;
			case 9:
				hybridizationCDK = CDKConstants.HYBRIDIZATION_SP3D5;break;
			default:
				hybridizationCDK = CDKConstants.HYBRIDIZATION_UNSET;break;
		}

		return new DescriptorValue(getSpecification(), getParameterNames(), getParameters(), new IntegerResult(hybridizationCDK));
	}


	
	private AtomType findMatchingAtomType(AtomContainer container, org.openscience.cdk.interfaces.Atom atom) throws CDKException 
	{
		try {
      if(atomATF==null)
        atomATF = AtomTypeFactory.getInstance("org/openscience/cdk/config/data/valency2_atomtypes.xml", 
            container.getBuilder());

			// take atomtype for the given element...
			AtomType atomType = atomATF.getAtomType(atom.getSymbol());
			return atomType;
			
		} catch (Exception ex1) 
		{
			logger.error(ex1.getMessage());
			logger.debug(ex1);
			throw new CDKException("Problems with AtomTypeFactory due to " + ex1.toString(), ex1);
		}
	}
	
	
	
	/**
	 *  Gets the parameterNames attribute of the AtomHybridizationVSEPRDescriptor object
	 *
	 *@return    The parameterNames value
	 */
	public String[] getParameterNames() {
		String[] params = new String[1];
		params[0] = "targetPosition";
		return params;
	}


	/**
	 *  Gets the parameterType attribute of the AtomHybridizationVSEPRDescriptor object
	 *
	 *@param  name  Description of the Parameter
	 *@return       The parameterType value
	 */
	public Object getParameterType(String name) {
		Object[] paramTypes = new Object[1];
		paramTypes[0] = new Integer(1);
		return paramTypes;
	}
}

