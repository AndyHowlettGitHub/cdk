/*  $RCSfile$
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
package org.openscience.cdk.qsar;

import java.util.ArrayList;
import java.util.Hashtable;

import org.openscience.cdk.interfaces.Atom;
import org.openscience.cdk.interfaces.AtomContainer;
import org.openscience.cdk.Element;
import org.openscience.cdk.config.IsotopeFactory;
import org.openscience.cdk.exception.CDKException;
import org.openscience.cdk.qsar.result.DoubleResult;
import org.openscience.cdk.tools.LoggingTool;


/**
 *  Atomic valence connectivity index (order 1). See
 *  <a href="http://www.edusoft-lc.com/molconn/manuals/400/chaptwo.html">http://www.edusoft-lc.com/molconn/manuals/400/chaptwo.html</a> and
 *  <a href="http://www.chemcomp.com/Journal_of_CCG/Features/descr.htm#KH">http://www.chemcomp.com/Journal_of_CCG/Features/descr.htm#KH</a>.
 *
 *  <p>Returned value is:
 *  <ul>
 *    <li>chi0vC is the Carbon valence connectivity index (order 1),
 *  </ul>
 *  where valence is the number of s and p valence electrons of atom.
 * 
 * @author      mfe4
 * @cdk.created 2004-11-03
 * @cdk.module	qsar
 * @cdk.set     qsar-descriptors
 * @cdk.dictref qsar-descriptors:chi1vC
 */
public class ValenceCarbonConnectivityOrderOneDescriptor implements Descriptor {

    private LoggingTool logger;
    private static Hashtable valences;
    private AtomValenceDescriptor avd = null;
	/**
	 *  Constructor for the ValenceCarbonConnectivityOrderOneDescriptor object
	 */
	public ValenceCarbonConnectivityOrderOneDescriptor() { 
            logger = new LoggingTool(this);
	    if (valences == null) { 
		avd = new AtomValenceDescriptor();
		valences = avd.valencesTable;
	    }
        }


	/**
	 *  Gets the specification attribute of the
	 *  ValenceCarbonConnectivityOrderOneDescriptor object
	 *
	 *@return    The specification value
	 */
	public DescriptorSpecification getSpecification() {
        return new DescriptorSpecification(
            "http://qsar.sourceforge.net/dicts/qsar-descriptors:chi1vC",
		    this.getClass().getName(),
		    "$Id$",
            "The Chemistry Development Kit");
	}


	/**
	 *  Sets the parameters attribute of the
	 *  ValenceCarbonConnectivityOrderOneDescriptor object
	 *
	 *@param  params            The new parameters value
	 *@exception  CDKException  Description of the Exception
	 */
	public void setParameters(Object[] params) throws CDKException {
		// no parameters for this descriptor
	}


	/**
	 *  Gets the parameters attribute of the
	 *  ValenceCarbonConnectivityOrderOneDescriptor object
	 *
	 *@return    The parameters value
	 */
	public Object[] getParameters() {
		// no parameters to return
		return (null);
	}


	/**
	 *  calculates the Carbon valence connectivity index (order 1) descriptor for an atom container
	 *
	 *@param  atomContainer                AtomContainer
	 *@return                   Carbon valence connectivity index (order 1)
	 *@exception  CDKException  Possible Exceptions
	 */
	public DescriptorValue calculate(AtomContainer atomContainer) throws CDKException {
		int valence = 0;
		int atomicNumber = 0;
		int hcount = 0;
		int atomValue = 0;
		double val0 = 0;
		double val1 = 0;
		ArrayList chiAtom = new ArrayList(2);
		double chi1v = 0;
		double chi1vC = 0;
		Atom[] atoms = null;
		Atom[] neighatoms = null;
		Element element = null;
		IsotopeFactory elfac = null;
		String symbol = null;
		org.openscience.cdk.interfaces.Bond[] bonds = atomContainer.getBonds();
		for (int b = 0; b < bonds.length; b++) {
			atoms = bonds[b].getAtoms();
			if ((!atoms[0].getSymbol().equals("H")) && (!atoms[1].getSymbol().equals("H"))) {
				val0 = 0;
				val1 = 0;
				chiAtom.clear();
				for (int a = 0; a < atoms.length; a++) {
					symbol = atoms[a].getSymbol();
					try {
						elfac = IsotopeFactory.getInstance();
					} catch (Exception exc) {
                                            logger.debug(exc);
						throw new CDKException("Problem instantiating IsotopeFactory: " + exc.toString(), exc);
					}
					try {
						element = elfac.getElement(symbol);
					} catch (Exception exc) {
                                            logger.debug(exc);
						throw new CDKException("Problem getting isotope " + symbol + " from ElementFactory: " + exc.toString(), exc);
					}
					atomicNumber = element.getAtomicNumber();
					valence = ((Integer)valences.get(symbol)).intValue();
					hcount = 0;
					atomValue = 0;
					neighatoms = atomContainer.getConnectedAtoms(atoms[a]);
					for (int n = 0; n < neighatoms.length; n++) {
						if (neighatoms[n].getSymbol().equals("H")) {
							hcount += 1;
						}
					}
					hcount += atoms[a].getHydrogenCount();
					atomValue = (valence - hcount) / (atomicNumber - valence - 1);
					//if(atomValue > 0) {
						chiAtom.add(new Double(atomValue));
						//System.out.println(symbol+"= atomvalue: "+atomValue+",val: "+valence);
					//}
				}
				val0 = ( (Double)chiAtom.get(0) ).doubleValue();
				val1 = ( (Double)chiAtom.get(1) ).doubleValue();
				if(val0 > 0 && val1 >0) {
					if((atoms[0].getSymbol().equals("C")) && (atoms[1].getSymbol().equals("C"))) {
						chi1vC += 1/(Math.sqrt(val0 * val1));
					}
				}
			}
		}		
		return new DescriptorValue(getSpecification(), getParameterNames(), getParameters(), new DoubleResult(chi1vC));
	}


	/**
	 *  Gets the parameterNames attribute of the
	 *  ValenceCarbonConnectivityOrderOneDescriptor object
	 *
	 *@return    The parameterNames value
	 */
	public String[] getParameterNames() {
		// no param names to return
		return (null);
	}



	/**
	 *  Gets the parameterType attribute of the
	 *  ValenceCarbonConnectivityOrderOneDescriptor object
	 *
	 *@param  name  Description of the Parameter
	 *@return       The parameterType value
	 */
	public Object getParameterType(String name) {
		return (null);
	}
}

