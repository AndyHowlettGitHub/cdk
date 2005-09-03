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
 *  Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 */
package org.openscience.cdk.qsar;

import java.util.Hashtable;

import org.openscience.cdk.Atom;
import org.openscience.cdk.interfaces.AtomContainer;
import org.openscience.cdk.Element;
import org.openscience.cdk.config.IsotopeFactory;
import org.openscience.cdk.exception.CDKException;
import org.openscience.cdk.qsar.result.DoubleResult;
import org.openscience.cdk.tools.LoggingTool;


/**
 *  Atomic valence connectivity index (order 0). See
 *  <a href="http://www.edusoft-lc.com/molconn/manuals/400/chaptwo.html">http://www.edusoft-lc.com/molconn/manuals/400/chaptwo.html</a> and
 *  <a href="http://www.chemcomp.com/Journal_of_CCG/Features/descr.htm#KH">http://www.chemcomp.com/Journal_of_CCG/Features/descr.htm#KH</a>.
 *
 *  <p>Returned value is:
 *  <ul>
 *    <li>chi0vC is the Carbon valence connectivity index (order 0);
 *  </ul>
 *  where the valence is the number of s and p valence electrons of atom.
 *
 * @author      mfe4
 * @cdk.created 2004-11-03
 * @cdk.module	qsar
 * @cdk.set     qsar-descriptors
 * @cdk.dictref qsar-descriptors:chi0vC
 */
public class ValenceCarbonConnectivityOrderZeroDescriptor implements Descriptor {

    private LoggingTool logger;
    private static Hashtable valences;
    private AtomValenceDescriptor avd = null;
	/**
	 *  Constructor for the ValenceCarbonConnectivityOrderZeroDescriptor object
	 */
	public ValenceCarbonConnectivityOrderZeroDescriptor() { 
            logger = new LoggingTool(this);
	    if (valences == null) { 
		avd = new AtomValenceDescriptor();
		valences = avd.valencesTable;
	    }
        }


	/**
	 *  Gets the specification attribute of the
	 *  ValenceCarbonConnectivityOrderZeroDescriptor object
	 *
	 *@return    The specification value
	 */
	public DescriptorSpecification getSpecification() {
        return new DescriptorSpecification(
            "http://qsar.sourceforge.net/dicts/qsar-descriptors:chi0vC",
		    this.getClass().getName(),
		    "$Id$",
            "The Chemistry Development Kit");
	}


	/**
	 *  Sets the parameters attribute of the
	 *  ValenceCarbonConnectivityOrderZeroDescriptor object
	 *
	 *@param  params            The new parameters value
	 *@exception  CDKException  Description of the Exception
	 */
	public void setParameters(Object[] params) throws CDKException {
		// no parameters for this descriptor
	}


	/**
	 *  Gets the parameters attribute of the
	 *  ValenceCarbonConnectivityOrderZeroDescriptor object
	 *
	 *@return    The parameters value
	 */
	public Object[] getParameters() {
		// no parameters to return
		return (null);
	}


	/**
	 *  calculates the Carbon valence connectivity index (order 0) descriptor for an atom container
	 *
	 *@param  atomContainer                AtomContainer
	 *@return                   Carbon valence connectivity index (order 0)
	 *@exception  CDKException  Possible Exceptions
	 */
	public DescriptorValue calculate(AtomContainer atomContainer) throws CDKException {
		int valence = 0;
		int atomicNumber = 0;
		int hcount = 0;
		int atomValue = 0;
		double chi0vC = 0;
		org.openscience.cdk.interfaces.Atom[] atoms = atomContainer.getAtoms();
		Atom[] neighatoms = null;
		Element element = null;
		IsotopeFactory elfac = null;
		String symbol = null;
                for (int i = 0; i < atoms.length; i++) {
                    symbol = atoms[i].getSymbol();
                    if(!symbol.equals("H")) {
                        try {
                            elfac = IsotopeFactory.getInstance();
                        } catch (Exception exc) {
                            logger.debug(exc);
                            throw new CDKException("Problem instantiating IsotopeFactory: " + exc.toString());
                        }
                        try {
                            element = elfac.getElement(symbol);
                        } catch (Exception exc) {
                            logger.debug(exc);
                            throw new CDKException("Problem getting isotope " + symbol + " from ElementFactory: " + exc.toString());
                        }
                        atomicNumber = element.getAtomicNumber();
                        valence = ((Integer)valences.get(symbol)).intValue();
                        hcount = 0;
                        atomValue = 0;
                        neighatoms = atomContainer.getConnectedAtoms(atoms[i]);
                        for (int a = 0; a < neighatoms.length; a++) {
                            if (neighatoms[a].getSymbol().equals("H")) {
                                hcount += 1;
                            }
                        }
                        hcount += atomContainer.getAtomAt(i).getHydrogenCount();
                        atomValue = (valence - hcount) / (atomicNumber - valence - 1);
                        if (atomValue > 0) {
                            if(symbol.equals("C")) {
                                chi0vC  += (1/(Math.sqrt(atomValue))); // chi0vC
                            }
                        }
                    }
                }
                return new DescriptorValue(getSpecification(), getParameterNames(), getParameters(), new DoubleResult(chi0vC));
        }


	/**
	 *  Gets the parameterNames attribute of the
	 *  ValenceCarbonConnectivityOrderZeroDescriptor object
	 *
	 *@return    The parameterNames value
	 */
	public String[] getParameterNames() {
		// no param names to return
		return (null);
	}



	/**
	 *  Gets the parameterType attribute of the
	 *  ValenceCarbonConnectivityOrderZeroDescriptor object
	 *
	 *@param  name  Description of the Parameter
	 *@return       The parameterType value
	 */
	public Object getParameterType(String name) {
		return (null);
	}
}

