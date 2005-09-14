/*
 *  $RCSfile$
 *  $Author$
 *  $Date$
 *  $Revision$
 *
 *  Copyright (C) 2002-2005  The Chemistry Development Kit (CDK) project
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
 *
 */
package org.openscience.cdk.renderer;

import java.awt.Graphics2D;

import org.openscience.cdk.interfaces.Atom;
import org.openscience.cdk.interfaces.AtomContainer;
import org.openscience.cdk.CDKConstants;
import org.openscience.cdk.ChemModel;
import org.openscience.cdk.ChemObject;
import org.openscience.cdk.Mapping;
import org.openscience.cdk.interfaces.Molecule;
import org.openscience.cdk.interfaces.Reaction;
import org.openscience.cdk.SetOfMolecules;
import org.openscience.cdk.SetOfReactions;
import org.openscience.cdk.geometry.GeometryTools;
import org.openscience.cdk.graph.ConnectivityChecker;
import org.openscience.cdk.tools.manipulator.SetOfMoleculesManipulator;

/**
 *  A Renderer class which draws 2D representations of molecules onto a given
 *  graphics objects using information from a Renderer2DModel. <p>
 *
 *  This renderer uses two coordinate systems. One that is a world coordinates
 *  system which is generated from the document coordinates. Additionally, the
 *  screen coordinates make up the second system, and are calculated by applying
 *  a zoom factor to the world coordinates. <p>
 *
 *  The coordinate system used for display has its origin in the left-bottom
 *  corner, with the x axis to the right, and the y axis towards the top of the
 *  screen. The system is thus right handed. <p>
 *
 *  The two main methods are paintMolecule() and paintChemModel(). Others might
 *  not show full rendering, e.g. anti-aliasing. <p>
 *
 *  This modules tries to adhere to guidelines being developed by the IUPAC
 *  which results can be found at <a href="http://www.angelfire.com/sc3/iupacstructures/">
 *  http://www.angelfire.com/sc3/iupacstructures/</a> .
 *
 *@author         steinbeck
 *@author         egonw
 *@cdk.module     render
 *@cdk.created    2002-10-03
 *@cdk.keyword    viewer, 2D-viewer
 *@cdk.bug        834515
 *@see            org.openscience.cdk.renderer.Renderer2DModel
 */
public class Renderer2D extends SimpleRenderer2D
{

	/**
	 *  Constructs a Renderer2D with a default settings model.
	 */
	public Renderer2D()
	{
		super(new Renderer2DModel());
	}


	/**
	 *  Constructs a Renderer2D.
	 *
	 *@param  r2dm  The settings model to use for rendering.
	 */
	public Renderer2D(Renderer2DModel r2dm)
	{
		super(r2dm);
	}


	/**
	 *  Description of the Method
	 *
	 *@param  model     Description of the Parameter
	 *@param  graphics  Description of the Parameter
	 */
	public void paintChemModel(ChemModel model, Graphics2D graphics)
	{
		customizeRendering(graphics);
		tooltiparea = null;
		paintPointerVector(graphics);
		if (model.getSetOfReactions() != null)
		{
			paintSetOfReactions(model.getSetOfReactions(), graphics);
		} else
		{
			logger.debug("setOfReactions is null");
		}
		if (model.getSetOfMolecules() != null)
		{
			paintSetOfMolecules(model.getSetOfMolecules(), graphics);
		} else
		{
			logger.debug("setOfMolecules is null");
		}
	
	}


	/**
	 *  Description of the Method
	 *
	 *@param  reactionSet  Description of the Parameter
	 *@param  graphics     Description of the Parameter
	 */
	public void paintSetOfReactions(org.openscience.cdk.interfaces.SetOfReactions reactionSet, Graphics2D graphics) {
		Reaction[] reactions = reactionSet.getReactions();
		for (int i = 0; i < reactions.length; i++)
		{
			paintReaction(reactions[i], graphics);
		}
	}


	/**
	 *  Description of the Method
	 *
	 *@param  moleculeSet  Description of the Parameter
	 *@param  graphics     Description of the Parameter
	 */
	public void paintSetOfMolecules(org.openscience.cdk.interfaces.SetOfMolecules moleculeSet, Graphics2D graphics) {
		logger.debug("painting set of molecules");
		org.openscience.cdk.interfaces.AtomContainer atomContainer = SetOfMoleculesManipulator.getAllInOneContainer(moleculeSet);
		Molecule[] molecules = null;
		try
		{
			molecules = ConnectivityChecker.partitionIntoMolecules(atomContainer).getMolecules();
			logger.debug("We have " + molecules.length + " molecules on screen");
		} catch (Exception exception)
		{
			logger.warn("Could not partition molecule: ", exception.getMessage());
			logger.debug(exception);
			return;
		}
		for (int i = 0; i < molecules.length; i++)
		{
			logger.debug("painting molecule " + i);
			paintMolecule(molecules[i], graphics);
		}
	}


	/**
	 *  Description of the Method
	 *
	 *@param  reaction  Description of the Parameter
	 *@param  graphics  Description of the Parameter
	 */
	public void paintReaction(Reaction reaction, Graphics2D graphics) {
		// calculate some boundaries
		AtomContainer reactantContainer = new org.openscience.cdk.AtomContainer();
		Molecule[] reactants = reaction.getReactants().getMolecules();
		for (int i = 0; i < reactants.length; i++)
		{
			reactantContainer.add(reactants[i]);
		}
		double[] minmaxReactants = GeometryTools.getMinMax(reactantContainer);
		AtomContainer productContainer = new org.openscience.cdk.AtomContainer();
		Molecule[] products = reaction.getProducts().getMolecules();
		for (int i = 0; i < products.length; i++)
		{
			productContainer.add(products[i]);
		}
		double[] minmaxProducts = GeometryTools.getMinMax(productContainer);
	
		// paint atom atom mappings
		Atom highlighted = r2dm.getHighlightedAtom();
		if (r2dm.getShowAtomAtomMapping() && highlighted != null && 
		    reaction instanceof org.openscience.cdk.Reaction)
		{
			logger.debug("Showing atom-atom mapping");
			Mapping[] mappings = ((org.openscience.cdk.Reaction)reaction).getMappings();
			logger.debug(" #mappings: ", mappings.length);
			for (int i = 0; i < mappings.length; i++)
			{
				ChemObject[] objects = mappings[i].getRelatedChemObjects();
				// only draw mapping when one of the mapped atoms
				// is highlighted
				if (objects[0] instanceof Atom && objects[1] instanceof Atom)
				{
					logger.debug("    atom1: ", objects[0]);
					logger.debug("    atom1: ", objects[1]);
					logger.debug("    highlighted: ", highlighted);
					if (highlighted == objects[0] || highlighted == objects[1])
					{
						Atom atom1 = (Atom) objects[0];
						Atom atom2 = (Atom) objects[1];
						int[] ints = new int[4];
						ints[0] = (int) (atom1.getPoint2d().x);
						ints[1] = (int) (atom1.getPoint2d().y);
						ints[2] = (int) (atom2.getPoint2d().x);
						ints[3] = (int) (atom2.getPoint2d().y);
						int[] screenCoords = getScreenCoordinates(ints);
						graphics.setColor(r2dm.getAtomAtomMappingLineColor());
						logger.debug("Mapping line color", r2dm.getAtomAtomMappingLineColor());
						logger.debug("Mapping line coords atom1.x: ", screenCoords[0]);
						logger.debug("Mapping line coords atom1.y: ", screenCoords[1]);
						logger.debug("Mapping line coords atom2.x: ", screenCoords[2]);
						logger.debug("Mapping line coords atom2.y: ", screenCoords[3]);
						graphics.drawLine(screenCoords[0], screenCoords[1],
								screenCoords[2], screenCoords[3]);
						graphics.setColor(r2dm.getForeColor());
					} else
					{
						logger.debug("  skipping this mapping. Not of hightlighted atom");
					}
				} else
				{
					logger.debug("Not showing a non atom-atom mapping");
				}
			}
		} else
		{
			logger.debug("Not showing atom-atom mapping");
		}
	
		// paint box around total
		int width = 13;
		double[] minmaxReaction = new double[4];
		minmaxReaction[0] = Math.min(minmaxReactants[0], minmaxProducts[0]);
		minmaxReaction[1] = Math.min(minmaxReactants[1], minmaxProducts[1]);
		minmaxReaction[2] = Math.max(minmaxReactants[2], minmaxProducts[2]);
		minmaxReaction[3] = Math.max(minmaxReactants[3], minmaxProducts[3]);
		String caption = reaction.getID();
		if (reaction.getProperty(CDKConstants.TITLE) != null)
		{
			caption = reaction.getProperty(CDKConstants.TITLE) +
					" (" + caption + ")";
		} else if (caption == null)
		{
			caption = "r" + reaction.hashCode();
		}
		paintBoundingBox(minmaxReaction, caption, 2 * width, graphics);
	
		// paint reactants content
		paintBoundingBox(minmaxReactants, "Reactants", width, graphics);
		paintMolecule(reactantContainer, graphics);
	
		// paint products content
		paintBoundingBox(minmaxProducts, "Products", width, graphics);
		paintMolecule(productContainer, graphics);
	
		if (productContainer.getAtomCount() > 0 && reactantContainer.getAtomCount() > 0)
		{
			// paint arrow
			int[] ints = new int[4];
			ints[0] = (int) (minmaxReactants[2]) + width + 5;
			ints[1] = (int) (minmaxReactants[1] + (minmaxReactants[3] - minmaxReactants[1]) / 2);
			ints[2] = (int) (minmaxProducts[0]) - (width + 5);
			ints[3] = ints[1];
			int[] screenCoords = getScreenCoordinates(ints);
			int direction = reaction.getDirection();
			if (direction == org.openscience.cdk.Reaction.FORWARD)
			{
				graphics.drawLine(screenCoords[0], screenCoords[1],
						screenCoords[2], screenCoords[3]);
				graphics.drawLine(screenCoords[2], screenCoords[3],
						screenCoords[2] - 7, screenCoords[3] - 7);
				graphics.drawLine(screenCoords[2], screenCoords[3],
						screenCoords[2] - 7, screenCoords[3] + 7);
			} else if (direction == org.openscience.cdk.Reaction.BACKWARD)
			{
				graphics.drawLine(screenCoords[0], screenCoords[1],
						screenCoords[2], screenCoords[3]);
				graphics.drawLine(screenCoords[0], screenCoords[1],
						screenCoords[0] + 7, screenCoords[1] - 7);
				graphics.drawLine(screenCoords[0], screenCoords[1],
						screenCoords[0] + 7, screenCoords[1] + 7);
			} else if (direction == org.openscience.cdk.Reaction.BIDIRECTIONAL)
			{
				graphics.drawLine(screenCoords[0], screenCoords[1] - 3,
						screenCoords[2], screenCoords[3] - 3);
				graphics.drawLine(screenCoords[0], screenCoords[1] - 3,
						screenCoords[0] + 7, screenCoords[1] - 3 - 7);
				graphics.drawLine(screenCoords[0], screenCoords[1] + 3,
						screenCoords[2], screenCoords[3] + 3);
				graphics.drawLine(screenCoords[2], screenCoords[3] + 3,
						screenCoords[2] - 7, screenCoords[3] + 3 + 7);
			}
		}
	}
}

