/*  $RCSfile$
 *  $Author$
 *  $Date$
 *  $Revision$
 *
 *  Copyright (C) 2004-2005  The Chemistry Development Kit (CDK) project
 *
 *  Contact: cdk-devel@list.sourceforge.net
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
 
package org.openscience.cdk.charges;

import java.util.regex.*;

import org.openscience.cdk.AtomContainer;
import org.openscience.cdk.exception.CDKException;
import org.openscience.cdk.tools.HOSECodeGenerator;

/**
 *  Assigns charges to atom types
 *  Both compilation and use of this class requires Java 1.4.
 *
 *@author     chhoppe
 *@cdk.created    2004-11-03
 */
public class AtomTypeCharges {
	HOSECodeGenerator hcg = new HOSECodeGenerator();
	Pattern pOC = Pattern.compile("O-[1][-];=?+C[(]=?+O.*+");
	Pattern pOP = Pattern.compile("O-[1][-];=?+P.*+");
	Pattern pOS = Pattern.compile("O-[1][-];=?+S.*+");
	Pattern p_p = Pattern.compile("[A-Za-z]{1,2}+[-][0-6].?+[+].*+");
	Pattern p_n = Pattern.compile("[A-Za-z]{1,2}+[-][0-6].?+[-].*+");


	/**
	 *  Constructor for the AtomTypeCharges object
	 */
	AtomTypeCharges() { }


	/**
	 *  Sets initial charges for atom types
	 * +1 for cationic atom types
	 * -1 for anionic atom types
	 * carboxylic oxygen -0.5
	 * phosphorylic oxygen -0.66
	 * sulfanilic oxygen -0.5
	 * or to formal charge (which must be determined elsewhere or set manually)
	 * polycations are not handled by this approach
	 *
	 *@param  atomContainer  AtomContainer
	 *@return                AtomContainer with set charges
	 *@exception  Exception  Description of the Exception
	 */
	public AtomContainer setCharges(AtomContainer atomContainer) throws Exception {
		
		atomContainer = setInitialCharges(atomContainer);
		return atomContainer;
	}


	private String removeAromaticityFlagsFromHoseCode(String hoseCode){
		//clean hosecode
		String hosecode="";
		for (int i=0;i<hoseCode.length();i++){
			if (hoseCode.charAt(i)== '*'){
			}else{
				hosecode=hosecode+hoseCode.charAt(i);
			}
		}
		return hosecode;
	}
	
	/**
	 *  Sets the initialCharges attribute of the AtomTypeCharges object
	 *
	 *@param  ac                AtomContainer
	 *@return                   AtomContainer with (new) partial charges
	 *@exception  CDKException  Description of the Exception
	 */
	private AtomContainer setInitialCharges(AtomContainer ac) throws CDKException {
		Matcher matOC = null;
		Matcher matOP = null;
		Matcher matOS = null;
		Matcher mat_p = null;
		Matcher mat_n = null;
		String hoseCode = "";

		for (int i = 0; i < ac.getAtomCount(); i++) {
			try {
				hoseCode = hcg.getHOSECode(ac, ac.getAtomAt(i), 3);
			} catch (CDKException ex1) {
				throw new CDKException("Could not build HOSECode from atom " + i + " due to " + ex1.toString());
			}
			hoseCode=removeAromaticityFlagsFromHoseCode(hoseCode);

			matOC = pOC.matcher(hoseCode);
			matOP = pOP.matcher(hoseCode);
			matOS = pOS.matcher(hoseCode);
			mat_p = p_p.matcher(hoseCode);
			mat_n = p_n.matcher(hoseCode);

			if (matOC.matches()) {
				ac.getAtomAt(i).setCharge(-0.500);
			} else if (matOP.matches()) {
				ac.getAtomAt(i).setCharge(-0.666);
			} else if (matOS.matches()) {
				ac.getAtomAt(i).setCharge(-0.500);
			} else if (mat_p.matches()) {
				ac.getAtomAt(i).setCharge(+1.000);
			} else if (mat_n.matches()) {
				ac.getAtomAt(i).setCharge(-1.000);
			} else {
				ac.getAtomAt(i).setCharge(ac.getAtomAt(i).getFormalCharge());
			}
		}
		return ac;
	}
}

