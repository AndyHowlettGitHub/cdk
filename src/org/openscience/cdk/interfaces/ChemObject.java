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
 */
package org.openscience.cdk.interfaces;

import java.util.Hashtable;
import org.openscience.cdk.ChemObjectListener;
import org.openscience.cdk.event.ChemObjectChangeEvent;

/**
 * The base class for all chemical objects in this cdk. It provides methods for
 * adding listeners and for their notification of events, as well a a hash
 * table for administration of physical or chemical properties
 *
 *@author        egonw
 *@cdk.module    data
 */
public interface ChemObject extends Cloneable {

	/**
	 * Use this to add yourself to this ChemObject as a listener. In order to do
	 * so, you must implement the ChemObjectListener Interface.
	 *
	 * @param  col  the ChemObjectListener
	 * @see         #removeListener
	 */
	public void addListener(ChemObjectListener col);

	/**
	 * Returns the number of ChemObjectListeners registered with this object.
	 *
	 * @return    the number of registered listeners.
	 */
	public int getListenerCount();

	/**
	 * Use this to remove a ChemObjectListener from the ListenerList of this
	 * ChemObject. It will then not be notified of change in this object anymore.
	 *
	 * @param  col  The ChemObjectListener to be removed
	 * @see         #addListener
	 */
	public void removeListener(ChemObjectListener col);


	/**
	 * This should be triggered by an method that changes the content of an object
	 * to that the registered listeners can react to it.
	 */
	public void notifyChanged();

	/**
	 * This should be triggered by an method that changes the content of an object
	 * to that the registered listeners can react to it. This is a version of
	 * notifyChanged() which allows to propagate a change event while preserving
	 * the original origin.
	 *
	 * @param  evt  A ChemObjectChangeEvent pointing to the source of where
	 *		        the change happend
	 */
	public void notifyChanged(ChemObjectChangeEvent evt);

	/**
	 * Sets a property for a ChemObject.
	 *
	 * @param  description  An object description of the property (most likely a
	 *                      unique string)
	 * @param  property     An object with the property itself
	 * @see                 #getProperty
	 * @see                 #removeProperty
	 */
	public void setProperty(Object description, Object property);
	
	/**
	 * Removes a property for a ChemObject.
	 *
	 * @param  description  The object description of the property (most likely a
	 *                      unique string)
	 * @see                 #setProperty
	 * @see                 #getProperty
	 */
	public void removeProperty(Object description);

	/**
	 * Returns a property for the ChemObject.
	 *
	 * @param  description  An object description of the property (most likely a
	 *                      unique string)
	 * @return              The object containing the property. Returns null if
	 *                      propert is not set.
	 * @see                 #setProperty
	 * @see                 #removeProperty
	 */
	public Object getProperty(Object description);

	/**
	 *  Returns a Map with the ChemObject's properties.
	 *
	 *@return    The object's properties as an Hashtable
	 *@see       #setProperties
	 */
	public Hashtable getProperties();

	/**
	 * Returns the identifier (ID) of this object.
	 *
	 * @return    a String representing the ID value
	 * @see       #setID
	 */
	public String getID();

	/**
	 * Sets the identifier (ID) of this object.
	 *
	 * @param  identifier  a String representing the ID value
	 * @see                #getID
	 */
	public void setID(String identifier);

	/**
	 * Sets the value of some flag.
	 *
	 * @param  flag_type   Flag to set
	 * @param  flag_value  Value to assign to flag
	 * @see                #getFlag
	 */
	public void setFlag(int flag_type, boolean flag_value);


	/**
	 * Returns the value of some flag.
	 *
	 * @param  flag_type  Flag to retrieve the value of
	 * @return            true if the flag <code>flag_type</code> is set
	 * @see               #setFlag
	 */
	public boolean getFlag(int flag_type);

	/**
	 * Sets the properties of this object.
	 *
	 * @param  properties  a Hashtable specifying the property values
	 * @see                #getProperties
	 */
	public void setProperties(Hashtable properties);
    
	/**
	 * Sets the whole set of flags.
	 *
	 * @param  flagsNew    the new flags.
	 * @see                #getFlags
	 */
    public void setFlags(boolean[] flagsNew);

	/**
	 * Returns the whole set of flags.
	 *
	 * @return    the flags.
	 * @see       #setFlags
	 */
    public boolean[] getFlags();

    /**
     * Returns a one line description of this ChemObject.
     *
     * @return a String representation of this object
     */
    public String toString();
    
    /**
     * Returns a deep clone of this ChemObject.
     *
     * @return Object the clone of this ChemObject.
     */
    public Object clone();
}


