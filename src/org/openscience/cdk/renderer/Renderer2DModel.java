/* $RCSfile$
 * $Author$
 * $Date$
 * $Revision$
 * 
 * Copyright (C) 1997-2004  The Chemistry Development Kit (CDK) project
 * 
 * Contact: cdk-devel@lists.sourceforge.net
 * 
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public License
 * as published by the Free Software Foundation; either version 2.1
 * of the License, or (at your option) any later version.
 * All we ask is that proper credit is given for our work, which includes
 * - but is not limited to - adding the above copyright notice to the beginning
 * of your source code files, and to any copyright notice that you may distribute
 * with programs based on this work.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 *  */
package org.openscience.cdk.renderer;

import org.openscience.cdk.renderer.color.CDK2DAtomColors;
import org.openscience.cdk.tools.LoggingTool;
import java.awt.*;
import java.awt.Polygon;
import org.openscience.cdk.*;
import org.openscience.cdk.event.*;
import java.util.*;

/**
 * Model for Renderer2D that contains settings for drawing objects.
 *
 * @cdkPackage render
 */
public class Renderer2DModel implements java.io.Serializable, Cloneable
{
    
    // private LoggingTool logger = new LoggingTool("org.openscience.cdk.render.Renderer2DModel");
    
    private double scaleFactor = 60.0;
    
    /** Determines how much the image is zoomed into on. */
    private double zoomFactor = 1.0;
	
	private double bondWidth = 2.0;

	private double bondDistance = 6.0;

	private double bondLength = 36.0;

	private Color backColor = Color.white;
	private Color foreColor = Color.black;
	private Color mappingColor = Color.gray;
    
	private Color highlightColor = Color.lightGray;
	
	private double highlightRadius = 10.0;

	private boolean drawNumbers = false;

    private boolean showAtomAtomMapping = false;

	private int atomRadius = 8;
	
	private Atom highlightedAtom = null;
	
	private Bond highlightedBond = null;
	
    /** 
     * The color hash is used to color substructures.
     * @see #getColorHash()
     */
	private Hashtable colorHash = new Hashtable();
    private CDK2DAtomColors colorer = new CDK2DAtomColors();
	
	private transient Vector listeners = new Vector();
	
	private Point pointerVectorStart = null;
	
	private Point pointerVectorEnd = null;
	
	private Polygon selectRect = null;
	
	private AtomContainer selectedPart = null;
	
	private Vector lassoPoints = new Vector();
    
    /** Determines wether structures should be drawn as Kekule structures,
     *  thus giving each carbon element explicitely, instead of not displaying
     *  the element symbol. Example C-C-C instead of /\.
     */
    private boolean kekuleStructure = false;

    /** Determines wether methyl carbons' symbols should be drawn explicit
     *  for methyl carbons. Example C/\C instead of /\. 
     */
    private boolean showEndCarbons = false;

    /** Determines wether implicit hydrogens should be drawn. */
    private boolean showImplicitHydrogens = true;

    /** Determines wether rings should be drawn with a circle if they are aromatic. */
    private boolean showAromaticity = true;
    private boolean showAromaticityInCDKStyle = false;

    /** Determines wether atoms are colored by type. */
    private boolean colorAtomsByType = true;

    private Dimension backgroundDimension = new Dimension(500,1200);
    
    private boolean showTooltip = false;
    
    private HashMap toolTipTextMap = new HashMap();
    
    /**
     * Returns the active background dimensions, thus applying the zoom
     * factor.
     *
     * @see #getUnzoomedBackgroundDimension
     */
    public Dimension getBackgroundDimension() {
        return new Dimension((int)((double)backgroundDimension.getWidth() * zoomFactor),
                             (int)((double)backgroundDimension.getHeight() * zoomFactor));
    }
    
    /**
     * Returns the unzoomed background dimensions.
     *
     * @see #getBackgroundDimension
     */
    public Dimension getUnzoomedBackgroundDimension() {
        return backgroundDimension;
    }
    
    /**
     * Sets the background dimensions in an unzoomed state.
     */
    public void setBackgroundDimension(Dimension dim) {
        this.backgroundDimension = dim;
    }
    
	/**
	 * Returns the distance between two lines in a double or triple bond
	 *
	 * @return     the distance between two lines in a double or triple bond
	 */
	public double getBondDistance() {
		return this.bondDistance;
	}


	/**
	 * Sets the distance between two lines in a double or triple bond
	 *
	 * @param   bondDistance  the distance between two lines in a double or triple bond
	 */
	public void setBondDistance(double bondDistance) {
		this.bondDistance = bondDistance;
	}

	

	/**
	 * Returns the thickness of a bond line.
	 *
	 * @return     the thickness of a bond line
	 */
	public double getBondWidth()
	{
		return this.bondWidth;
	}


	/**
	 * Sets the thickness of a bond line.
	 *
	 * @param   bondWidth  the thickness of a bond line
	 */
	public void setBondWidth(double bondWidth)
	{
		this.bondWidth = bondWidth;
	}


	/**
	 * Returns the length of a bond line.
	 *
	 * @return     the length of a bond line
	 */
	public double getBondLength()
	{
		return this.bondLength;
	}


	/**
	 * Sets the length of a bond line.
	 *
	 * @param   bondLength  the length of a bond line
	 */
	public void setBondLength(double bondLength)
	{
		this.bondLength = bondLength;
	}
	

	/**
	 * A scale factor for the drawing.
	 *
	 * @return a scale factor for the drawing
	 */
	public double getScaleFactor()
	{
		return this.scaleFactor;
	}


	/**
	 * Returns the scale factor for the drawing
	 *
	 * @param   scaleFactor  the scale factor for the drawing
	 */
	public void setScaleFactor(double scaleFactor)
	{
		this.scaleFactor = scaleFactor;
	}

	/**
	 * A zoom factor for the drawing.
	 *
	 * @return a zoom factor for the drawing
	 */
	public double getZoomFactor() {
		return this.zoomFactor;
	}


	/**
	 * Returns the zoom factor for the drawing.
	 *
	 * @param   zoomFactor  the zoom factor for the drawing
	 */
	public void setZoomFactor(double zoomFactor) {
		this.zoomFactor = zoomFactor;
	}	

	/**
	 * Returns the foreground color for the drawing.
	 *
	 * @return the foreground color for the drawing    
	 */
	public Color getForeColor()
	{
		return this.foreColor;
	}


	/**
	 * Sets the foreground color with which bonds and atoms are drawn
	 *
	 * @param   foreColor  the foreground color with which bonds and atoms are drawn
	 */
	public void setForeColor(Color foreColor)
	{
		this.foreColor = foreColor;
	}

	

	/**
	 * Returns the background color 
	 *
	 * @return the background color     
	 */
	public Color getBackColor()
	{
		return this.backColor;
	}


	/**
	 * Sets the background color 
	 *
	 * @param   backColor the background color  
	 */
	public void setBackColor(Color backColor)
	{
		this.backColor = backColor;
	}

    /**
     * Returns the atom-atom mapping line color 
     *
     * @return the atom-atom mapping line color     
     */
    public Color getAtomAtomMappingLineColor() {
        return this.backColor;
    }

    /**
     * Sets the atom-atom mapping line color 
     *
     * @param   mappingColor the atom-atom mapping line color  
     */
    public void setAtomAtomMappingLineColor(Color mappingColor) {
        this.mappingColor = mappingColor;
    }

	
	/**
	 * Returns if the drawing of atom numbers is switched on for this model
	 *
	 * @return  true if the drawing of atom numbers is switched on for this model   
	 */
	public boolean drawNumbers()
	{
		return this.drawNumbers;
	}

	public boolean getKekuleStructure() {
		return this.kekuleStructure;
	}

	public void setKekuleStructure(boolean kekule) {
		this.kekuleStructure = kekule;
	}

    public boolean getColorAtomsByType() {
        return this.colorAtomsByType;
    }
    
    public void setColorAtomsByType(boolean bool) {
        this.colorAtomsByType = bool;
    }

    public boolean getShowEndCarbons() {
        return this.showEndCarbons;
    }
    
    public void setShowEndCarbons(boolean showThem) {
        this.showEndCarbons = showThem;
    }
    
    public boolean getShowImplicitHydrogens() {
        return this.showImplicitHydrogens;
    }
    
    public void setShowImplicitHydrogens(boolean showThem) {
        this.showImplicitHydrogens = showThem;
    }
    
    public boolean getShowAromaticity() {
        return this.showAromaticity;
    }
    
    public void setShowAromaticity(boolean showIt) {
        this.showAromaticity = showIt;
    }
    
    public boolean getShowAromaticityInCDKStyle() {
        return this.showAromaticityInCDKStyle;
    }
    
    public void setShowAromaticityInCDKStyle(boolean showIt) {
        this.showAromaticityInCDKStyle = showIt;
    }
    
	/**
	 * Sets if the drawing of atom numbers is switched on for this model
	 *
	 * @param   drawNumbers  true if the drawing of atom numbers is to be switched on for this model
	 */
	public void setDrawNumbers(boolean drawNumbers)
	{
		this.drawNumbers = drawNumbers;
	}


	/**
	 * Returns the color used for highlighting things in this model 
	 *
	 * @return     the color used for highlighting things in this model 
	 */
	public Color getHighlightColor()
	{
		return this.highlightColor;
	}


	/**
	 * Sets the color used for highlighting things in this model 
	 *
	 * @param   highlightColor  the color to be used for highlighting things in this model 
	 */
	public void setHighlightColor(Color highlightColor)
	{
		this.highlightColor = highlightColor;
	}


	/**
	 * Returns the radius around an atoms, for which the atom is 
	 * marked highlighted if a pointer device is placed within this radius
	 * 
	 * @return The highlight radius for all atoms   
	 */
	public double getHighlightRadius()
	{
		return this.highlightRadius;
	}


	/**
	 * Sets the radius around an atoms, for which the atom is 
	 * marked highlighted if a pointer device is placed within this radius
	 *
	 * @param   highlightRadius  the highlight radius of all atoms
	 */
	public void setHighlightRadius(double highlightRadius)
	{
		this.highlightRadius = highlightRadius;
	}

    /**
     * Returns whether Atom-Atom mapping must be shown.
     */
     public boolean getShowAtomAtomMapping() {
         return this.showAtomAtomMapping;
     }


    /**
     * Sets wether Atom-Atom mapping must be shown.
     */
    public void setShowAtomAtomMapping(boolean value) {
        this.showAtomAtomMapping = value;
    }

    /**
     * XXX No idea what this is about.
     */
    public int getAtomRadius() {
        return this.atomRadius;
    }


	/**
	 * XXX No idea what this is about
	 *
	 * @param   atomRadius   XXX No idea what this is about
	 */
	public void setAtomRadius(int atomRadius)
	{
		this.atomRadius = atomRadius;
	}

	

	/**
	 * Returns the atom currently highlighted
	 *
	 * @return the atom currently highlighted    
	 */
	public Atom getHighlightedAtom()
	{
		return this.highlightedAtom;
	}


	/**
	 * Sets the atom currently highlighted
	 *
	 * @param   highlightedAtom The atom to be highlighted  
	 */
	public void setHighlightedAtom(Atom highlightedAtom)
	{
		if ((this.highlightedAtom == null) &&
            (highlightedAtom == null)) {
            // do not do anything, nothing has changed
        } else {
            this.highlightedAtom = highlightedAtom;
            fireChange();
        }
	}

	

	/**
	 * Returns the Bond currently highlighted
	 *
	 * @return the Bond currently highlighted    
	 */
	public Bond getHighlightedBond()
	{
		return this.highlightedBond;
	}


	/**
	 * Sets the Bond currently highlighted
	 *
	 * @param   highlightedBond  The Bond to be currently highlighted
	 */
	public void setHighlightedBond(Bond highlightedBond)
	{
		if ((this.highlightedBond == null) &&
            (highlightedBond == null)) {
            // do not do anything, nothing has changed
        } else {
            this.highlightedBond = highlightedBond;
            fireChange();
        }
	}

	

	/**
	 * Returns the hashtable used for coloring substructures 
	 *
	 * @return the hashtable used for coloring substructures     
	 */
	public Hashtable getColorHash()
	{
		return this.colorHash;
	}
    
    /**
     * Returns the drawing color of the given atom.
     * An atom is colored as highlighted if hightlighted.
     * The atom is color marked if in a substructure.
     * If not, the color from the CDK2DAtomColor is used
     * (if selected). Otherwise, the atom is colored black.
     */
    public Color getAtomColor(Atom atom) {
        // logger.debug("Getting atom front color for " + atom.toString());
        Color atomColor = getForeColor();
        if (colorAtomsByType) {
            // logger.debug("Coloring atom by type");
            atomColor = colorer.getAtomColor(atom);
        }
        // logger.debug("Color: " + atomColor.toString());
        return atomColor;
    }

    /**
     * Returns the background color of the given atom.
     */
    public Color getAtomBackgroundColor(Atom atom) {
        // logger.debug("Getting atom back color for " + atom.toString());
        Color atomColor = getBackColor();
        // logger.debug("  BackColor: " + atomColor.toString());
        Color hashColor = (Color) this.getColorHash().get(atom);
        if (hashColor != null) {
            // logger.debug("Background color atom according to hashing (substructure)");
            atomColor = hashColor;
        }
        if (atom == this.getHighlightedAtom()) {
            // logger.debug("Background color atom according to highlighting");
            atomColor = this.getHighlightColor();
        }
        // logger.debug("Color: " + atomColor.toString());
        return atomColor;
    }

	/**
	 * Sets the hashtable used for coloring substructures 
	 *
	 * @param   colorHash  the hashtable used for coloring substructures 
	 */
	public void setColorHash(Hashtable colorHash)
	{
		this.colorHash = colorHash;
	}
	

	/**
	 * Returns the end of the pointer vector
	 *
	 * @return the end point
	 */
	public Point getPointerVectorEnd()
	{
		return this.pointerVectorEnd;
	}


	/**
	 * Sets the end of a pointer vector
	 *
	 * @param   pointerVectorEnd  
	 */
	public void setPointerVectorEnd(Point pointerVectorEnd)
	{
		this.pointerVectorEnd = pointerVectorEnd;
		fireChange();
	}

	

	/**
	 * Returns the start of a pointer vector
	 *
	 * @return the start point
	 */
	public Point getPointerVectorStart()
	{
		return this.pointerVectorStart;
	}


	/**
	 * Sets the start point of a pointer vector
	 *
	 * @param   pointerVectorStart  
	 */
	public void setPointerVectorStart(Point pointerVectorStart)
	{
		this.pointerVectorStart = pointerVectorStart;
		fireChange();
	}
	
	

	/**
	 * Returns selected rectangular
	 *
	 * @return the selection
	 */
	public Polygon getSelectRect()
	{
		return this.selectRect;
	}


	/**
	 * Sets a selected region
	 *
	 * @param   selectRect  
	 */
	public void setSelectRect(Polygon selectRect)
	{
		this.selectRect = selectRect;
		fireChange();		
	}

	

	/**
	 * Get selected atoms
	 *
	 * @return an atomcontainer with the selected atoms
	 */
	public AtomContainer getSelectedPart()
	{
		return this.selectedPart;
	}

	/**
	 * Sets the selected atoms
	 *
	 * @param   selectedPart  
	 */
	public void setSelectedPart(AtomContainer selectedPart)
	{
		this.selectedPart = selectedPart;
		getColorHash().clear();
		for (int i = 0; i < selectedPart.getAtomCount(); i++)
		{
			getColorHash().put(selectedPart.getAtomAt(i), getHighlightColor());
		}
        Bond[] bonds = selectedPart.getBonds();
		for (int i = 0; i < bonds.length; i++) {
			getColorHash().put(bonds[i], getHighlightColor());
		}		
	}


	/**
	 * Returns a set of points constituating a selected region
	 *
	 * @return a vector with points
	 */
	public Vector getLassoPoints()
	{
		return this.lassoPoints;
	}


	/**
	 * Adds a point to the list of lasso points
	 *
	 * @param   point  Point to add to list
	 */
	public void addLassoPoint(Point point)
	{
		this.lassoPoints.addElement(point);
		fireChange();
	}


	/**
	 * Adds a change listener to the list of listeners
	 *
	 * @param   listener  The listener added to the list 
	 */

	public void addCDKChangeListener(CDKChangeListener listener)
	{
		if (listeners == null)
		{
			listeners = new Vector();	
		}
		if (!listeners.contains(listener))
		{
			listeners.add(listener);
		}
	}
	

	/**
	 * Removes a change listener from the list of listeners
	 *
	 * @param   listener  The listener removed from the list 
	 */
	public void removeCDKChangeListener(CDKChangeListener listener)
	{
		listeners.remove(listener);
	}


	/**
	 * Notifies registered listeners of certain changes
	 * that have occurred in this model.
	 */
	public void fireChange()
	{
		EventObject event = new EventObject(this);
		if (listeners == null)
		{
			listeners = new Vector();	
		}
		
		for (int i = 0; i < listeners.size(); i++)
		{
			((CDKChangeListener)listeners.get(i)).stateChanged(event);
		}
	}
  
  
  /**
   *  Gets the toolTipText for a certain atom.
   *
   * @param  a  The atom.
   * @return    The toolTipText value.
   */
  public String getToolTipText(Atom a) {
    if (toolTipTextMap.get(a) != null) {
      return ((String) toolTipTextMap.get(a));
    } else {
      return (null);
    }
  }
  
  
  /**
   *  Sets the showTooltip attribute.
   *
   * @param  b  The new value.
   */
  public void setShowTooltip(boolean b){
    showTooltip=b;
  }
  
  
  /**
   *  Gets showTooltip attribute.
   *
   * @return    The showTooltip value.
   */
  public boolean getShowTooltip(){
    return(showTooltip);
  }
  
  
  /**
   *  Sets the toolTipTextMap.
   *
   * @param  map  A map containing Atoms of the current molecule as keys and Strings to display as values. A line break will be inserted where a \n is in the string.  
   */
  public void setToolTipTextMap(HashMap map){
    toolTipTextMap=map;
  }


  /**
   *  Gets the toolTipTextMap.
   *
   * @return  The toolTipTextValue.  
   */
  public HashMap getToolTipTextMap(){
    return toolTipTextMap;
  }
}
