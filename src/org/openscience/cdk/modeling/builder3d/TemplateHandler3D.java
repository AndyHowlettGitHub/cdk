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
 */
package org.openscience.cdk.modeling.builder3d;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.InputStream;
import java.util.List;
import java.util.Vector;
import java.util.BitSet;
import java.util.StringTokenizer;

import org.openscience.cdk.SetOfMolecules;
import org.openscience.cdk.Atom;
import org.openscience.cdk.AtomContainer;
import org.openscience.cdk.CDKConstants;
import org.openscience.cdk.Molecule;
import org.openscience.cdk.RingSet;
import org.openscience.cdk.exception.*;
import org.openscience.cdk.isomorphism.UniversalIsomorphismTester;
import org.openscience.cdk.isomorphism.mcss.RMap;
import org.openscience.cdk.io.iterator.IteratingMDLReader;
import org.openscience.cdk.isomorphism.matchers.QueryAtomContainer;
import org.openscience.cdk.isomorphism.matchers.QueryAtomContainerCreator;
import org.openscience.cdk.fingerprint.Fingerprinter;

/**
 *  Helper class for ModelBuilder3D. Handles templates. This is
 *  our layout solution for 3D ring systems 
 *
 *@author     cho,steinbeck
 *@cdk.created    2004-09-21
 *@cdk.module     builder3d
 */
public class TemplateHandler3D{
	Molecule molecule;
	RingSet sssr;
	SetOfMolecules templates=null;
	Vector fingerprintData=null;
	Vector ringTemplates=null;
	/**
	 *  The empty constructor.
	 */
	public TemplateHandler3D() {
		templates = new SetOfMolecules();
		fingerprintData=new Vector();
		ringTemplates=new Vector(75);
	}


	/**
	 *  Loads all existing templates into memory To add templates to be used in
	 *  Template file is a mdl file. Creates a Object Set of Molecules
	 */
	public void loadTemplates() throws CDKException{
		//System.out.println("TEMPLATE START");
		IteratingMDLReader imdl=null;
		int [] statistics=new int [13];
		//int maxRingSize=0;
		InputStream ins = null;
		BufferedReader fin =null;
			
		try{	
			ins = this.getClass().getClassLoader().getResourceAsStream("org/openscience/cdk/modeling/builder3d/data/ringTemplateStructures.sdf.gz");
			fin = new BufferedReader(new InputStreamReader(ins));
			imdl=new IteratingMDLReader(fin);
		}catch (Exception exc1){
			throw new CDKException("Problems loading file ringTemplateStructures.sdf.gz");
		}
		//System.out.println("TEMPLATE addMolecule");
		Molecule molecule=null;
		while (imdl.hasNext()){
			molecule=(Molecule) imdl.next();
			templates.addMolecule(molecule);
		}
		molecule=null;
		try{	
			imdl.close();
		}catch (Exception exc2){
			System.out.println("Could not close Reader due to: "+exc2.getMessage());
		}
		//System.out.println("TEMPLATE Finger");
		try{
			
			ins = this.getClass().getClassLoader().getResourceAsStream("org/openscience/cdk/modeling/builder3d/data/ringTemplateFingerprints.txt.gz");
			fin = new BufferedReader(new InputStreamReader(ins));
		}catch (Exception exc3){
			System.out.println("Could not read Fingerprints from FingerprintFile due to: "+exc3.getMessage());
		}
		String s=null;
		while (true) {
			try{
				s = fin.readLine();
			}catch(Exception exc4){}
			
			if (s == null) {
				break;
			}
			fingerprintData.add((BitSet)getBitSetFromFile(new StringTokenizer(s,"\t ;{, }")));
		}
		//System.out.println("Fingerprints are read in:"+fingerprintData.size());
	}
	
	private BitSet getBitSetFromFile(StringTokenizer st){
		BitSet bitSet=new BitSet(1024);
		for (int i=0;i<st.countTokens();i++){
			
			try{
			bitSet.set(Integer.parseInt(st.nextToken()));
			}catch (NumberFormatException nfe){}
		}
		return bitSet;
	}
	
	/**
	 *  Checks if one of the loaded templates is a substructure in the given
	 *  Molecule. If so, it assigns the coordinates from the template to the
	 *  respective atoms in the Molecule.
	 *
	 *@param  ringSystems AtomContainer from the ring systems
	 *@param  NumberOfRingAtoms double
	 */
	public void mapTemplates(AtomContainer ringSystems, double NumberOfRingAtoms) throws Exception{
		System.out.println("Map Template...START---Number of Ring Atoms:"+NumberOfRingAtoms);
		int mapped = 0;
		AtomContainer template=null;
		QueryAtomContainer queryRingSystem=QueryAtomContainerCreator.createAnyAtomContainer(ringSystems,false);
		QueryAtomContainer query=null;
		BitSet ringSystemFingerprint=Fingerprinter.getFingerprint(queryRingSystem);
		RMap map = null;
		Atom atom1 = null;
		Atom atom2 = null;
		boolean flagMaxSubstructure=false;
		for (int i = 0; i < fingerprintData.size(); i++){
			template=(AtomContainer)templates.getMolecule(i);
			if (template.getAtomCount()!=ringSystems.getAtomCount()){
					continue;
			}
			if (Fingerprinter.isSubset(ringSystemFingerprint,(BitSet)fingerprintData.get(i))){
				query=QueryAtomContainerCreator.createAnyAtomContainer(template,true);
				if (UniversalIsomorphismTester.isSubgraph(ringSystems,query)){
					List list = UniversalIsomorphismTester.getSubgraphAtomsMap(ringSystems,query);
					//System.out.println("Found a subgraph mapping of size " + list.size()+" Position:"+i+" RingSize:"+NumberOfRingAtoms);
					if ((NumberOfRingAtoms)/list.size()==1){
						flagMaxSubstructure=true;
					}
					
					for (int j = 0; j < list.size(); j++){
						map = (RMap) list.get(j);
						atom1 = ringSystems.getAtomAt(map.getId1());
						atom2 = template.getAtomAt(map.getId2());
						if (atom1.getFlag(CDKConstants.ISINRING)){
							atom1.setX3d(atom2.getX3d());
							atom1.setY3d(atom2.getY3d());
							atom1.setZ3d(atom2.getZ3d());
							mapped++;
						}
					}//for j
				
					if (flagMaxSubstructure){
						break;
					}
				
				}//if subgraph
			}//if fingerprint
		}//for i
		if (!flagMaxSubstructure){
			System.out.println("WARNING:Maybe RingTemplateError");		
		}
	}
	
	/**
	 *  Gets the templateCount attribute of the TemplateHandler object
	 *
	 *@return    The templateCount value
	 */
	public int getTemplateCount()
	{
		return templates.getMoleculeCount();
	}


	/**
	 *  Gets the templateAt attribute of the TemplateHandler object
	 *
	 *@param  position  Description of the Parameter
	 *@return           The templateAt value
	 */
	public AtomContainer getTemplateAt(int position)
	{
		return (AtomContainer) templates.getMolecule(position);
	}
}

