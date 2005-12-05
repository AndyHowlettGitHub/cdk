/*  $RCSfile$
 *  $Author$
 *  $Date$
 *  $Revision$
 *
 *  Copyright (C) 1997-2005  The Chemistry Development Kit (CDK) project
 *
 *  This code has been kindly provided by Stephane Werner
 *  and Thierry Hanser from IXELIS mail@ixelis.net
 *
 *  IXELIS sarl - Semantic Information Systems
 *  17 rue des C???res 67200 Strasbourg, France
 *  Tel/Fax : +33(0)3 88 27 81 39 Email: mail@ixelis.net
 *
 *  CDK Contact: cdk-devel@lists.sf.net
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
 *
 */
package org.openscience.cdk.isomorphism;

import java.util.ArrayList;
import java.util.BitSet;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;

import org.openscience.cdk.CDKConstants;
import org.openscience.cdk.exception.CDKException;
import org.openscience.cdk.interfaces.Atom;
import org.openscience.cdk.interfaces.AtomContainer;
import org.openscience.cdk.interfaces.Bond;
import org.openscience.cdk.isomorphism.matchers.QueryAtom;
import org.openscience.cdk.isomorphism.matchers.QueryBond;
import org.openscience.cdk.isomorphism.mcss.RGraph;
import org.openscience.cdk.isomorphism.mcss.RMap;
import org.openscience.cdk.isomorphism.mcss.RNode;

/**
 *  This class implements a multipurpose structure comparison tool.
 *  It allows to find maximal common substructure, find the
 *  mapping of a substructure in another structure, and the mapping of
 *  two isomorphic structures.
 *
 *  <p>Structure comparison may be associated to bond constraints
 *  (mandatory bonds, e.g. scaffolds, reaction cores,...) on each source graph.
 *  The constraint flexibility allows a number of interesting queries.
 *  The substructure analysis relies on the RGraph generic class (see: RGraph)
 *  This class implements the link between the RGraph model and the
 *  the CDK model in this way the RGraph remains independant and may be used
 *  in other contexts.
 *
 *  <p>This algorithm derives from the algorithm described in
 *  {@cdk.cite HAN90} and modified in the thesis of T. Hanser {@cdk.cite Han93}.
 *
 *  <p><font color="#FF0000">
 *     warning :  As a result of the adjacency perception used in this algorithm
 *                there is a single limitation : cyclopropane and isobutane are seen as isomorph
 *                This is due to the fact that these two compounds are the only ones where
 *                each bond is connected two each other bond (bonds are fully conected)
 *                with the same number of bonds and still they have different structures
 *                The algotihm could be easily enhanced with a simple atom mapping manager
 *                to provide an atom level overlap definition that would reveal this case.
 *                We decided not to penalize the whole procedure because of one single
 *                exception query. Furthermore isomorphism may be discarded since  the number of atoms are
 *                not the same (3 != 4) and in most case this will be already
 *                screened out by a fingerprint based filtering.
 *                It is possible to add a special treatment for this special query.
 *                Be reminded that this algorithm matches bonds only. Hence an AtomContainer that
 *                contains no bonds is seen as isomorph to any other AtomContainer.
 *                shk3: Single atom cases now work.
 *   </font>
 *
 *
 * @author      Stephane Werner from IXELIS mail@ixelis.net
 * @cdk.created 2002-07-17
 * @cdk.require java1.4+
 * @cdk.module  standard
 */
public class UniversalIsomorphismTester {

  final static int ID1 = 0;
  final static int ID2 = 1;
  private static long start;
  public static long timeout=-1;

  ///////////////////////////////////////////////////////////////////////////
  //                            Query Methods
  //
  // This methods are simple applications of the RGraph model on atom containers
  // using different constrains and search options. They give an exemple of the
  // most common queries but of course it is possible to define other type of
  // queries exploiting the constrain and option combinations
  //

  ////
  // Isomorphism search

  /**
   *  Tests if  g1 and g2 are isomorph
   *
   * @param  g1  first molecule
   * @param  g2  second molecule
   * @return     true if the 2 molecule are isomorph
   */
  public static boolean isIsomorph(AtomContainer g1, AtomContainer g2)  throws CDKException{
    return (getIsomorphMap(g1, g2) != null);
  }


  /**
   *  Returns the first isomorph mapping found or null
   *
   * @param  g1  first molecule
   * @param  g2  second molecule
   * @return     the first isomorph mapping found projected of g1. This is a List of RMap objects containing Ids of matching bonds.
   */
  public static List getIsomorphMap(AtomContainer g1, AtomContainer g2)  throws CDKException{
    List result = null;

    List rMapsList = search(g1, g2, getBitSet(g1),
        getBitSet(g2), false, false);

    if (!rMapsList.isEmpty()) {
      result = (List) rMapsList.get(0);
    }

    return result;
  }


  /**
   *  Returns the first isomorph 'atom mapping' found for g2 in g1.
   *
   * @param  g1  first molecule
   * @param  g2  second molecule
   * @return     the first isomorph atom mapping found projected on g1. This is a List of RMap objects containing Ids of matching atoms.
   */
  public static List getIsomorphAtomsMap(AtomContainer g1, AtomContainer g2)  throws CDKException{
    return (makeAtomsMapOfBondsMap(UniversalIsomorphismTester.getIsomorphMap(g1, g2), g1, g2));
  }


  /**
   *  Returns all the isomorph 'mappings' found between two
   *  atom containers.
   *
   * @param  g1  first molecule
   * @param  g2  second molecule
   * @return     the list of all the 'mappings'
   */
  public static List getIsomorphMaps(AtomContainer g1, AtomContainer g2)  throws CDKException{
    return search(g1, g2, getBitSet(g1),
        getBitSet(g2), true, true);
  }


  /////
  // Subgraph search

  /**
   *  Returns all the subgraph 'bond mappings' found for g2 in g1.
   *  This is an ArrayList of ArrayLists of RMap objects.
   *
   * @param  g1  first molecule
   * @param  g2  second molecule
   * @return     the list of all the 'mappings' found projected of g1
   *
   */
  public static List getSubgraphMaps(AtomContainer g1, AtomContainer g2)  throws CDKException{
    return search(g1, g2, new BitSet(), getBitSet(g2), true, true);
  }


  /**
   *  Returns the first subgraph 'bond mapping' found for g2 in g1.
   *
   * @param  g1  first molecule
   * @param  g2  second molecule
   * @return     the first subgraph bond mapping found projected on g1. This is a List of RMap objects containing Ids of matching bonds.
   */
  public static List getSubgraphMap(AtomContainer g1, AtomContainer g2)  throws CDKException{
    List result = null;
    List rMapsList = search(g1, g2, new BitSet(),
        getBitSet(g2), false, false);

    if (!rMapsList.isEmpty()) {
      result = (List) rMapsList.get(0);
    }

    return result;
  }


  /**
   *  Returns all subgraph 'atom mappings' found for g2 in g1.
   *  This is an ArrayList of ArrayLists of RMap objects.
   *
   * @param  g1  first AtomContainer
   * @param  g2  second AtomContainer
   * @return     all subgraph atom mappings found projected on g1. This is a List of RMap objects containing Ids of matching atoms.
   */
  public static List getSubgraphAtomsMaps(AtomContainer g1, AtomContainer g2)  throws CDKException{
    return (makeAtomsMapsOfBondsMaps(UniversalIsomorphismTester.getSubgraphMaps(g1, g2), g1, g2));
  }
  
  /**
   *  Returns the first subgraph 'atom mapping' found for g2 in g1.
   *
   * @param  g1  first molecule
   * @param  g2  second molecule
   * @return     the first subgraph atom mapping found projected on g1. This is a List of RMap objects containing Ids of matching atoms.
   */
  public static List getSubgraphAtomsMap(AtomContainer g1, AtomContainer g2)  throws CDKException{
    return (makeAtomsMapOfBondsMap(UniversalIsomorphismTester.getSubgraphMap(g1, g2), g1, g2));
  }


  /**
   *  Tests if g2 a subgraph of g1
   *
   * @param  g1  first molecule
   * @param  g2  second molecule
   * @return     true if g2 a subgraph on g1
   */
  public static boolean isSubgraph(AtomContainer g1, AtomContainer g2)  throws CDKException{
    return (getSubgraphMap(g1, g2) != null);
  }


  ////
  // Maximum common substructure search

  /**
   *  Returns all the maximal common substructure between 2 atom containers
   *
   * @param  g1  first molecule
   * @param  g2  second molecule
   * @return     the list of all the maximal common substructure
   *             found projected of g1 (list of AtomContainer )
   */
  public static List getOverlaps(AtomContainer g1, AtomContainer g2) throws CDKException{
    start=System.currentTimeMillis();
    List rMapsList = search(g1, g2, new BitSet(),
        new BitSet(), true, false);

    // projection on G1
    ArrayList graphList = projectList(rMapsList, g1, ID1);

    // reduction of set of solution (isomorphism and substructure
    // with different 'mappings'
    ArrayList reducedGraphList = getMaximum(graphList);

    return reducedGraphList;
  }


  /**
   *  Transforms an AtomContainer into a BitSet (which's size = number of bond
   *  in the atomContainer, all the bit are set to true)
   *
   * @param  ac  AtomContainer to transform
   * @return     The bitSet
   */
  public static BitSet getBitSet(AtomContainer ac) {
    BitSet bs = null;
    int n = ac.getBondCount();

    if (n != 0) {
      bs = new BitSet(n);
      for (int i = 0; i < n; i++) { bs.set(i); };
    } else {
      bs = new BitSet();
    }

    return bs;
  }


  //////////////////////////////////////////////////
  //          Internal methods

  /**
   *  Builds the RGraph ( resolution graph ), from two atomContainer
   * (description of the two molecules to compare)
   * This is the interface point between the CDK model and
   * the generic MCSS algorithm based on the RGRaph.
   *
   * @param  g1  Description of the first molecule
   * @param  g2  Description of the second molecule
   * @return     the rGraph
   */
  public static RGraph buildRGraph(AtomContainer g1, AtomContainer g2)  throws CDKException{
    RGraph rGraph = new RGraph();
    nodeConstructor(rGraph, g1, g2);
    arcConstructor(rGraph, g1, g2);
    return rGraph;
  }


  /**
   *  General Rgraph parsing method (usually not used directly)
   *  This method is the entry point for the recursive search
   *  adapted to the atom container input.
   *
   * @param  g1                first molecule
   * @param  g2                second molecule
   * @param  c1                initial condition ( bonds from g1 that
   *                           must be contains in the solution )
   * @param  c2                initial condition ( bonds from g2 that
   *                           must be contains in the solution )
   * @param  findAllStructure  if false stop at the first structure found
   * @param  findAllMap        if true search all the 'mappings' for one same
   *                           structure
   * @return                   a list of rMapList that represent the search solutions
   */
  public static List search(AtomContainer g1, AtomContainer g2, BitSet c1,
		  BitSet c2, boolean findAllStructure, boolean findAllMap)  throws CDKException{
	  
	  //Test for single atom cases
	  if (g2.getAtomCount() == 1) {
		  List arrayList = new ArrayList();
		  Atom atom = g2.getAtomAt(0);
		  
		  if (atom instanceof QueryAtom) {
			  QueryAtom qAtom = (QueryAtom)atom;
			  for (int i=0; i<g1.getAtomCount(); i++){
				  if(qAtom.matches(g1.getAtomAt(i)))
					  arrayList.add(new RMap(i,0));
			  }
		  } else {
			  String atomSymbol = atom.getSymbol();
			  for(int i=0; i<g1.getAtomCount(); i++){
				  if(g1.getAtomAt(i).getSymbol().equals(atomSymbol))
					  arrayList.add(new RMap(i,0));
			  }
		  }
		  return arrayList;
	  } else if (g1.getAtomCount() == 1) {
		  List arrayList = new ArrayList();
		  
		  Atom atom = g1.getAtomAt(0);
		  for (int i=0; i<g2.getAtomCount(); i++) {
		      Atom atom2 = g2.getAtomAt(i);
		      if (atom2 instanceof QueryAtom) {
		    	  QueryAtom qAtom = (QueryAtom)atom2;
		    	  if (qAtom.matches(atom))
		    		  arrayList.add(new RMap(0,i));
		      } else {
		    	  if(atom2.getSymbol().equals(atom.getSymbol()))
					  arrayList.add(new RMap(0,i));
		      }
		  }
		  return arrayList;
	  }
	  
	  // reset result
	  ArrayList rMapsList = new ArrayList();
	  
	  // build the RGraph corresponding to this problem
	  RGraph rGraph = buildRGraph(g1, g2);
	  // parse the RGraph with the given constrains and options
	  rGraph.parse(c1, c2, findAllStructure, findAllMap);
	  List solutionList = rGraph.getSolutions();
	  
	  // convertions of RGraph's internal solutions to G1/G2 mappings
	  for (Iterator i = solutionList.iterator(); i.hasNext(); ) {
		  BitSet set = (BitSet) i.next();
		  rMapsList.add(rGraph.bitSetToRMap(set));
	  }
	  
	  return rMapsList;
  }
  
  //////////////////////////////////////
  //    Manipulation tools

  /**
   *  Projects a list of RMap on a molecule
   *
   * @param  rMapList  the list to project
   * @param  g         the molecule on which project
   * @param  id        the id in the RMap of the molecule g
   * @return           an AtomContainer
   */
  public static AtomContainer project(List rMapList, AtomContainer g, int id) {
    AtomContainer ac = g.getBuilder().newAtomContainer();

    Bond[] bondList = g.getBonds();

    Hashtable table = new Hashtable();
    Atom a1 = null;
    Atom a2 = null;
    Atom a = null;
    Bond bond = null;

    for (Iterator i = rMapList.iterator(); i.hasNext(); ) {
      RMap rMap = (RMap) i.next();
      if (id == UniversalIsomorphismTester.ID1) {
        bond = bondList[rMap.getId1()];
      } else {
        bond = bondList[rMap.getId2()];
      }

      a = bond.getAtomAt(0);
      a1 = (Atom) table.get(a);

      if (a1 == null) {
        a1 = (Atom)((Atom)a).clone();
        ac.addAtom(a1);
        table.put(a, a1);
      }

      a = bond.getAtomAt(1);
      a2 = (Atom) table.get(a);

      if (a2 == null) {
        a2 = (Atom)((Atom)a).clone();
        ac.addAtom(a2);
        table.put(a, a2);
      }
      Bond newBond = g.getBuilder().newBond(a1, a2, bond.getOrder());
      newBond.setFlag(
        CDKConstants.ISAROMATIC,
        bond.getFlag(CDKConstants.ISAROMATIC)
      );
      ac.addBond(newBond);
    }
    return ac;
  }


  /**
   *  Project a list of RMapsList on a molecule
   *
   * @param  rMapsList  list of RMapsList to project
   * @param  g          the molecule on which project
   * @param  id         the id in the RMap of the molecule g
   * @return            a list of AtomContainer
   */
  public static ArrayList projectList(List rMapsList, AtomContainer g, int id) {
    ArrayList graphList = new ArrayList();

    for (Iterator i = rMapsList.iterator(); i.hasNext(); ) {
      List rMapList = (List) i.next();
      AtomContainer ac = project(rMapList, g, id);
      graphList.add(ac);
    }
    return graphList;
  }

  /**
   *  remove all redundant solution
   *
   * @param  graphList  the list of structure to clean
   * @return            the list cleaned
   */
  private static ArrayList getMaximum(ArrayList graphList)  throws CDKException{
    ArrayList reducedGraphList = (ArrayList) graphList.clone();

    for (int i = 0; i < graphList.size(); i++) {
      AtomContainer gi = (AtomContainer) graphList.get(i);

      for (int j = i + 1; j < graphList.size(); j++) {
        AtomContainer gj = (AtomContainer) graphList.get(j);

        // Gi included in Gj or Gj included in Gi then
        // reduce the irrelevant solution
        if (isSubgraph(gj, gi)) {
            reducedGraphList.remove(gi);
        } else if (isSubgraph(gi, gj)) {
            reducedGraphList.remove(gj);
        }
      }
    }
    return reducedGraphList;
  }

  /**
   *  This makes maps of matching atoms out of a maps of matching bonds as produced by the get(Subgraph|Ismorphism)Maps methods.
   *
   * @param  l   The list produced by the getMap method.
   * @param  g1  The first atom container.
   * @param  g2  The second one (first and second as in getMap)
   * @return     A Vector of Vectors of RMap objects of matching Atoms.
   */
   public static List makeAtomsMapsOfBondsMaps(List l, AtomContainer g1, AtomContainer g2) {
	   if(l==null) {
		   return l;
	   }
	   Vector result = new Vector();
	   for (int i = 0; i < l.size(); i++) {
		   ArrayList l2 = (ArrayList)l.get(i);
		   if (l!=null) {
			   result.add((Vector)makeAtomsMapOfBondsMap(l2, g1, g2));
		   }
	   }
	   return result;
   }
  
  /**
   *  This makes a map of matching atoms out of a map of matching bonds as produced by the get(Subgraph|Ismorphism)Map methods.
   *
   * @param  l   The list produced by the getMap method.
   * @param  g1  The first atom container.
   * @param  g2  The second one (first and second as in getMap)
   * @return     The mapping found projected on g1. This is a List of RMap objects containing Ids of matching atoms.
   */
  public static List makeAtomsMapOfBondsMap(List l, AtomContainer g1, AtomContainer g2) {
    if(l==null)
      return(l);
    Bond[] bonds1 = g1.getBonds();
    Bond[] bonds2 = g2.getBonds();
    List result = new Vector();
    for (int i = 0; i < l.size(); i++) {
    	Bond bond1 = bonds1[((RMap) l.get(i)).getId1()];
    	Bond bond2 = bonds2[((RMap) l.get(i)).getId2()];
      Atom[] atom1 = bond1.getAtoms();
      Atom[] atom2 = bond2.getAtoms();
      for (int j = 0; j < 2; j++) {
    	  Bond[] bondsConnectedToAtom1j = g1.getConnectedBonds(atom1[j]);
        for (int k = 0; k < bondsConnectedToAtom1j.length; k++) {
          if (bondsConnectedToAtom1j[k] != bond1) {
        	  Bond testBond = bondsConnectedToAtom1j[k];
            for (int m = 0; m < l.size(); m++) {
            	Bond testBond2 = null;
              if (((RMap) l.get(m)).getId1() == g1.getBondNumber(testBond)) {
                testBond2 = bonds2[((RMap) l.get(m)).getId2()];
                for (int n = 0; n < 2; n++) {
                  Vector bondsToTest = g2.getConnectedBondsVector(atom2[n]);
                  if (bondsToTest.contains(testBond2)) {
                    RMap map;
                    if (j == n) {
                      map = new RMap(g1.getAtomNumber(atom1[0]), g2.getAtomNumber(atom2[0]));
                    } else {
                      map = new RMap(g1.getAtomNumber(atom1[1]), g2.getAtomNumber(atom2[0]));
                    }
                    if (!result.contains(map)) {
                      result.add(map);
                    }
                    RMap map2;
                    if (j == n) {
                      map2 = new RMap(g1.getAtomNumber(atom1[1]), g2.getAtomNumber(atom2[1]));
                    } else {
                      map2 = new RMap(g1.getAtomNumber(atom1[0]), g2.getAtomNumber(atom2[1]));
                    }
                    if (!result.contains(map2)) {
                      result.add(map2);
                    }
                  }
                }
              }
            }
          }
        }
      }
    }
    return (result);
  }


  /**
   *  Builds  the nodes of the RGraph ( resolution graph ), from
   *  two atom containers (description of the two molecules to compare)
   *
   * @param  gr   the target RGraph
   * @param  ac1  description of the first molecule
   * @param  ac2  description of the second molecule
   */
  private static void nodeConstructor(RGraph gr, AtomContainer ac1, AtomContainer ac2)  throws CDKException{
    // resets the target graph.
    gr.clear();
    Bond[] bondsA1 = ac1.getBonds();
    Bond[] bondsA2 = ac2.getBonds();
    int k = 0;

    // compares each bond of G1 to each bond of G2
    for (int i = 0; i < bondsA1.length; i++) {
      for (int j = 0; j < bondsA2.length; j++) {
          if(timeout>-1 && (System.currentTimeMillis()-start)>timeout)
        	  throw new CDKException("Timeout exceeded in getOverlaps");
          Bond bondA2 = bondsA2[j];
          if (bondA2 instanceof QueryBond) {
              QueryBond queryBond = (QueryBond)bondA2;
              QueryAtom atom1 = (QueryAtom)(bondA2.getAtomAt(0));
              QueryAtom atom2 = (QueryAtom)(bondA2.getAtomAt(1));
              Bond bond = bondsA1[i];
              if (queryBond.matches(bond)) {
                  // ok, bonds match
                  if (atom1.matches(bond.getAtomAt(0)) && atom2.matches(bond.getAtomAt(1)) ||
                      atom1.matches(bond.getAtomAt(1)) && atom2.matches(bond.getAtomAt(0))) {
                      // ok, atoms match in either order
                      gr.addNode(new RNode(i,j));
                  }
              }
          } else {
              // if both bonds are compatible then create an association node
              // in the resolution graph
              if (
                ( // bond type conditions
                  ( // same bond order and same aromaticity flag (either both on or off)
                    bondsA1[i].getOrder() == bondsA2[j].getOrder() &&
                    bondsA1[i].getFlag(CDKConstants.ISAROMATIC) ==
                    bondsA2[j].getFlag(CDKConstants.ISAROMATIC)
                  )
                  ||
                  ( // both bond are aromatic
                    bondsA1[i].getFlag(CDKConstants.ISAROMATIC) &&
                    bondsA2[j].getFlag(CDKConstants.ISAROMATIC)
                  )
                )
                &&
                ( // atome type conditions 
                  ( // a1 = a2 && b1 = b2 
                    bondsA1[i].getAtomAt(0).getSymbol().equals(bondsA2[j].getAtomAt(0).getSymbol()) &&
                    bondsA1[i].getAtomAt(1).getSymbol().equals(bondsA2[j].getAtomAt(1).getSymbol())
                  )
                  ||
                  ( // a1 = b2 && b1 = a2
                    bondsA1[i].getAtomAt(0).getSymbol().equals(bondsA2[j].getAtomAt(1).getSymbol()) &&
                    bondsA1[i].getAtomAt(1).getSymbol().equals(bondsA2[j].getAtomAt(0).getSymbol())
                  )
                )
              ) {
                  gr.addNode(new RNode(i, j));
              }
          }
      }
    }
  }


  /**
   *  Build edges of the RGraphs
   *  This method create the edge of the RGraph and
   *  calculates the incompatibility and neighbourhood
   *  relationships between RGraph nodes.
   *
   * @param  gr   the rGraph
   * @param  ac1  Description of the first molecule
   * @param  ac2  Description of the second molecule
   */
  private static void arcConstructor(RGraph gr, AtomContainer ac1, AtomContainer ac2) throws CDKException{
    // each node is incompatible with himself
    for (int i = 0; i < gr.getGraph().size(); i++) {
      RNode x = (RNode) gr.getGraph().get(i);
      x.getForbidden().set(i);
    }

    Bond a1 = null;
    Bond a2 = null;
    Bond b1 = null;
    Bond b2 = null;

    Bond[] bondsA1 = ac1.getBonds();
    Bond[] bondsA2 = ac2.getBonds();

    gr.setFirstGraphSize(ac1.getBondCount());
    gr.setSecondGraphSize(ac2.getBondCount());

    for (int i = 0; i < gr.getGraph().size(); i++) {
      RNode x = (RNode) gr.getGraph().get(i);

      // two nodes are neighbours if their adjacency
      // relationship in are equivalent in G1 and G2
      // else they are incompatible.
      for (int j = i + 1; j < gr.getGraph().size(); j++) {
        if(timeout>-1 && (System.currentTimeMillis()-start)>timeout)
          throw new CDKException("Timeout exceeded in getOverlaps");
        RNode y = (RNode) gr.getGraph().get(j);

        a1 = bondsA1[((RNode) gr.getGraph().get(i)).getRMap().getId1()];
        a2 = bondsA2[((RNode) gr.getGraph().get(i)).getRMap().getId2()];
        b1 = bondsA1[((RNode) gr.getGraph().get(j)).getRMap().getId1()];
        b2 = bondsA2[((RNode) gr.getGraph().get(j)).getRMap().getId2()];

        if (a1 instanceof QueryBond) {
            if (a1.equals(b1) || a2.equals(b2) ||
                !queryAdjacency(a1, b1, a2, b2)) {
                x.getForbidden().set(j);
                y.getForbidden().set(i);
            } else if (hasCommonAtom(a1, b1)) {
                x.getExtension().set(j);
                y.getExtension().set(i);
            }
        } else {
            if (a1.equals(b1) || a2.equals(b2) ||
                (!getCommonSymbol(a1, b1).equals(getCommonSymbol(a2, b2)))) {
              x.getForbidden().set(j);
              y.getForbidden().set(i);
            } else if (hasCommonAtom(a1, b1)) {
              x.getExtension().set(j);
              y.getExtension().set(i);
            }
        }
      }
    }
  }

  
    /**
   *  Determines if 2 bond have 1 atom in common
   *
   * @param  a  first bond
   * @param  b  second bond
   * @return    the symbol of the common atom or "" if
   *            the 2 bonds have no common atom
   */
  private static boolean hasCommonAtom(Bond a, Bond b) {
      
    if (a.contains(b.getAtomAt(0))) {
      return true;
    } else if (a.contains(b.getAtomAt(1))) {
      return true;
    }
    
    return false;
  }

  /**
   *  Determines if 2 bond have 1 atom in common and returns the common symbol
   *
   * @param  a  first bond
   * @param  b  second bond
   * @return    the symbol of the common atom or "" if
   *            the 2 bonds have no common atom
   */
  private static String getCommonSymbol(Bond a, Bond b) {
    String symbol = "";
    
    if (a.contains(b.getAtomAt(0))) {
      symbol = b.getAtomAt(0).getSymbol();
    } else if (a.contains(b.getAtomAt(1))) {
      symbol = b.getAtomAt(1).getSymbol();
    }
    
    return symbol;
  }

    /**
   *  Determines if 2 bond have 1 atom in common if second is a query AtomContainer
   *
   * @param  a  first bond
   * @param  b  second bond
   * @return    the symbol of the common atom or "" if
   *            the 2 bonds have no common atom
   */
  private static boolean queryAdjacency(Bond a1, Bond b1, Bond a2, Bond b2) {
      
	  Atom atom1 = null;
	  Atom atom2 = null;
      
      if (a1.contains(b1.getAtomAt(0))) {
          atom1 = b1.getAtomAt(0);
      } else if (a1.contains(b1.getAtomAt(1))) {
          atom1 = b1.getAtomAt(1);
      }
      
      if (a2.contains(b2.getAtomAt(0))) {
          atom2 = b2.getAtomAt(0);
      } else if (a2.contains(b2.getAtomAt(1))) {
          atom2 = b2.getAtomAt(1);
      }
      
      if (atom1 != null && atom2 != null){
          return ((QueryAtom)atom2).matches(atom1);
      } else if (atom1 == null && atom2 == null) {
	      return true;
      } else {
	      return false;
      }
      
  }
  
}

