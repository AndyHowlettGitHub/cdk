/* $RCSfile$
 * $Author$    
 * $Date$    
 * $Revision$
 * 
 * Copyright (C) 1997-2005  The Chemistry Development Kit (CDK) project
 * 
 * Contact: cdk-devel@lists.sourceforge.net
 * 
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public License
 * as published by the Free Software Foundation; either version 2.1
 * of the License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA. 
 * 
 */
package org.openscience.cdk;

/**
 * Implements the idea of an element in the periodic table.
 * 
 * <p>Use the IsotopeFactory to get a ready-to-use elements
 * by symbol or atomic number:
 * <pre>
 *   IsotopeFactory if = IsotopeFactory.getInstance();
 *   Element e1 = if.getElement("C");
 *   Element e2 = if.getElement(12);
 * </pre>
 *
 * @cdk.module data
 *
 * @cdk.keyword element
 *
 * @see org.openscience.cdk.config.IsotopeFactory
 */
public class Element extends ChemObject implements java.io.Serializable, Cloneable
{

    /** The element symbol for this element as listed in the periodic table. */
    protected String symbol;

    /** The atomic number for this element giving their position in the periodic table. */
    protected int atomicNumber = 0;

    /**
     * Constructs an empty Element.
     */
    public Element() {
        super();
        this.symbol = null;
    }

    /**
     * Constructs an Element with a given 
     * element symbol.
     *
     * @param   symbol The element symbol that this element should have.  
     */
    public Element(String symbol) {
        this();
        this.symbol = symbol;
    }

    /**
     * Constructs an Element with a given element symbol, 
     * atomic number and atomic mass.
     *
     * @param   symbol  The element symbol of this element.
     * @param   atomicNumber  The atomicNumber of this element.
     */
    public Element(String symbol, int atomicNumber) {
        this(symbol);
        this.atomicNumber = atomicNumber;
    }

    /**
     * Returns the atomic number of this element.
     *
     * @return The atomic number of this element    
     *
     * @see    #setAtomicNumber
     */
    public int getAtomicNumber() {
        return this.atomicNumber;
    }

    /**
     * Sets the atomic number of this element.
     *
     * @param   atomicNumber The atomic mass to be assigned to this element
     *
     * @see    #getAtomicNumber
     */
    public void setAtomicNumber(int atomicNumber) {
        this.atomicNumber = atomicNumber;
	notifyChanged();
    }

    /**
     * Returns the element symbol of this element.
     *
     * @return The element symbol of this element. Null if unset.
     *
     * @see    #setSymbol
     */
    public String getSymbol() {
        return this.symbol;
    }

    /**
     * Sets the element symbol of this element.
     *
     * @param symbol The element symbol to be assigned to this atom
     *
     * @see    #getSymbol
     */
    public void setSymbol(String symbol) {
        this.symbol = symbol;
	notifyChanged();
    }

    /**
         * Clones this atom object.
         *
         * @return  The cloned object   
         */
    public Object clone() throws CloneNotSupportedException {
        Object clone = null;
        try {
            clone = super.clone();
        } catch (Exception e) {
            e.printStackTrace(System.err);
        }
        return clone;
    }
    
    public String toString() {
        StringBuffer resultString = new StringBuffer();
        resultString.append("Element(");
        resultString.append(getSymbol());
        resultString.append(", ID:"); resultString.append(getID());
        resultString.append(", AN:"); resultString.append(getAtomicNumber());
        resultString.append(")");
        return resultString.toString();
    }
    
    /**
     * Compare an Element with this Element.
     *
     * @param  object Object of type AtomType
     * @return        Return true, if the atomtypes are equal
     */
    public boolean compare(Object object) {
        if (!(object instanceof Element)) {
            return false;
        }
        if (!super.compare(object)) {
            return false;
        }
        Element elem = (Element)object;
        if (atomicNumber == elem.atomicNumber &&
            symbol == elem.symbol) {
            return true;
        }
        return false;
    }
}
