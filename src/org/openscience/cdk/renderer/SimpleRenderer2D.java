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
 *  Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110-1301 USA.
 *
 */
package org.openscience.cdk.renderer;

import java.awt.Graphics2D;

import org.openscience.cdk.RingSet;
import org.openscience.cdk.graph.ConnectivityChecker;
import org.openscience.cdk.ringsearch.SSSRFinder;

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
public class SimpleRenderer2D extends AbstractRenderer2D
{

	/**
	 *  Constructs a Renderer2D with a default settings model.
	 */
	public SimpleRenderer2D()
	{
		super(new Renderer2DModel());
	}


	/**
	 *  Constructs a Renderer2D.
	 *
	 *@param  r2dm  The settings model to use for rendering.
	 */
	public SimpleRenderer2D(Renderer2DModel r2dm)
	{
		super(r2dm);
	}


	/**
	 *  Triggers the methods to make the molecule fit into the frame and to paint
	 *  it.
	 *
	 *@param  atomCon   Description of the Parameter
	 *@param  graphics  Description of the Parameter
	 */
	public void paintMolecule(org.openscience.cdk.interfaces.AtomContainer atomCon, Graphics2D graphics) {
        logger.debug("inside paintMolecule()");
		customizeRendering(graphics);
		RingSet ringSet = new RingSet();
		org.openscience.cdk.interfaces.Molecule[] molecules = null;
		try
		{
			molecules = ConnectivityChecker.partitionIntoMolecules(atomCon).getMolecules();
		} catch (Exception exception)
		{
			logger.warn("Could not partition molecule: ", exception.getMessage());
			logger.debug(exception);
			return;
		}
		for (int i = 0; i < molecules.length; i++)
		{
			SSSRFinder sssrf = new SSSRFinder(molecules[i]);
			ringSet.add(sssrf.findSSSR());
		}
        paintBonds(atomCon, ringSet, graphics);
		paintAtoms(atomCon, graphics);
		if (r2dm.getSelectRect() != null)
		{
			graphics.setColor(r2dm.getHighlightColor());
			graphics.drawPolygon(r2dm.getSelectRect());
		}
		paintLassoLines(graphics);
	
	}

}

