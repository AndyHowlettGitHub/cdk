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
import org.openscience.cdk.interfaces.AtomContainer;
import org.openscience.cdk.exception.CDKException;
import org.openscience.cdk.qsar.result.IntegerResult;

/**
 * Descriptor based on the number of atoms of a certain element type.
 *
 * It is
 * possible to use the wild card symbol '*' as element type to get the count of
 * all atoms.
 * <p>This descriptor uses these parameters:
 * <table border="1">
 *   <tr>
 *     <td>Name</td>
 *     <td>Default</td>
 *     <td>Description</td>
 *   </tr>
 *   <tr>
 *     <td>elementName</td>
 *     <td>*</td>
 *     <td>Symbol of the element we want to count</td>
 *   </tr>
 * </table>
 *
 *
 * @author      mfe4
 * @cdk.created 2004-11-13
 * @cdk.module  qsar
 * @cdk.set     qsar-descriptors
 * @cdk.dictref qsar-descriptors:atomCount
 */
public class AtomCountDescriptor implements Descriptor {

    private String elementName = "*";


    /**
     *  Constructor for the AtomCountDescriptor object.
     */
    public AtomCountDescriptor() { 
        elementName = "*";
    }

    /**
     * Returns a <code>Map</code> which specifies which descriptor
     * is implemented by this class. 
     *
     * These fields are used in the map:
     * <ul>
     * <li>Specification-Reference: refers to an entry in a unique dictionary
     * <li>Implementation-Title: anything
     * <li>Implementation-Identifier: a unique identifier for this version of
     *  this class
     * <li>Implementation-Vendor: CDK, JOELib, or anything else
     * </ul>
     *
     * @return An object containing the descriptor specification
     */
    public DescriptorSpecification getSpecification() {
        return new DescriptorSpecification(
                "http://qsar.sourceforge.net/dicts/qsar-descriptors:atomCount",
                this.getClass().getName(),
                "$Id$",
                "The Chemistry Development Kit");
    };

    /**
     *  Sets the parameters attribute of the AtomCountDescriptor object.
     *
     *@param  params            The new parameters value
     *@throws  CDKException  if the number of parameters is greater than 1
     *or else the parameter is not of type String
     *@see #getParameters
     */
    public void setParameters(Object[] params) throws CDKException {
        if (params.length > 1) {
            throw new CDKException("AtomCount only expects one parameter");
        }
        if (!(params[0] instanceof String)) {
            throw new CDKException("The parameter must be of type String");
        }
        // ok, all should be fine
        elementName = (String) params[0];
    }


    /**
     *  Gets the parameters attribute of the AtomCountDescriptor object.
     *
     *@return    The parameters value
     *@see #setParameters
     */
    public Object[] getParameters() {
        // return the parameters as used for the descriptor calculation
        Object[] params = new Object[1];
        params[0] = elementName;
        return params;
    }


    /**
     *  This method calculate the number of atoms of a given type in an {@link AtomContainer}.
     *
     *@param  container  The atom container for which this descriptor is to be calculated
     *@return            Number of atoms of a certain type is returned.
     *@throws CDKException currently nothing will cause an exception to be thrown
     */

    // it could be interesting to accept as elementName a SMARTS atom, to get the frequency of this atom
    // this could be useful for other descriptors like polar surface area...
    public DescriptorValue calculate(AtomContainer container) throws CDKException {
        int atomCount = 0;

        if (container == null) throw new CDKException("The supplied AtomContainer was NULL");

        Atom[] atoms = container.getAtoms();

        if (atoms == null) throw new CDKException("There were no atoms in the supplied AtomContainer");

        if (elementName.equals("*")) {
            for (int i = 0; i < atoms.length; i++) {
                atomCount += container.getAtomAt(i).getHydrogenCount();
            }			
            atomCount += atoms.length;
        } 
        else if (elementName.equals("H")) {
            for (int i = 0; i < atoms.length; i++) {
                if (container.getAtomAt(i).getSymbol().equals(elementName)) {
                    atomCount += 1;
                }
                else {
                    atomCount += container.getAtomAt(i).getHydrogenCount();
                }
            }
        }
        else {
            for (int i = 0; i < atoms.length; i++) {
                if (container.getAtomAt(i).getSymbol().equals(elementName)) {
                    atomCount += 1;
                }
            }			
        }
        return new DescriptorValue(getSpecification(), getParameterNames(), getParameters(), new IntegerResult(atomCount));
    }


    /**
     *  Gets the parameterNames attribute of the AtomCountDescriptor object.
     *
     *@return    The parameterNames value
     */
    public String[] getParameterNames() {
        String[] params = new String[1];
        params[0] = "elementName";
        return params;
    }


    /**
     *  Gets the parameterType attribute of the AtomCountDescriptor object.
     *
     *@param  name  Description of the Parameter
     *@return       An Object whose class is that of the parameter requested
     */
    public Object getParameterType(String name) {
        Object[] paramTypes = new Object[1];
        paramTypes[0] = new String();
        return paramTypes;
    }
}

