/* $RCSfile$
 * $Author$
 * $Date$
 * $Revision$
 *
 * Copyright (C) 2005  The Chemistry Development Kit (CDK) project
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
 * Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110-1301 USA.
 */
package org.openscience.cdk.debug;

import java.util.Hashtable;

import javax.vecmath.Point2d;
import javax.vecmath.Point3d;

import org.openscience.cdk.interfaces.Atom;
import org.openscience.cdk.interfaces.Bond;
import org.openscience.cdk.interfaces.ChemObjectBuilder;
import org.openscience.cdk.interfaces.ChemObjectChangeEvent;
import org.openscience.cdk.interfaces.ChemObjectListener;
import org.openscience.cdk.interfaces.ElectronContainer;
import org.openscience.cdk.tools.LoggingTool;

/**
 * Debugging data class.
 * 
 * @author     egonw
 * @cdk.module data-debug
 */
public class DebugBond extends org.openscience.cdk.Bond
    implements Bond {

	LoggingTool logger = new LoggingTool();
	
	public int getElectronCount() {
		logger.debug("Getting electron count: ", super.getElectronCount());
		return super.getElectronCount();
	}

	public void setElectronCount(int electronCount) {
		logger.debug("Setting electron count: ", electronCount);
		super.setElectronCount(electronCount);
	}

	public void addListener(ChemObjectListener col) {
		logger.debug("Adding listener: ", col);
		super.addListener(col);
	}

	public int getListenerCount() {
		logger.debug("Getting listener count: ", super.getListenerCount());
		return super.getListenerCount();
	}

	public void removeListener(ChemObjectListener col) {
		logger.debug("Removing listener: ", col);
		super.removeListener(col);
	}

	public void notifyChanged() {
		logger.debug("Notifying changed");
		super.notifyChanged();
	}

	public void notifyChanged(ChemObjectChangeEvent evt) {
		logger.debug("Notifying changed event: ", evt);
		super.notifyChanged(evt);
	}

	public void setProperty(Object description, Object property) {
		logger.debug("Setting property: ", description + "=" + property);
		super.setProperty(description, property);
	}

	public void removeProperty(Object description) {
		logger.debug("Removing property: ", description);
		super.removeProperty(description);
	}

	public Object getProperty(Object description) {
		logger.debug("Getting property: ", description + "=" + super.getProperty(description));
		return super.getProperty(description);
	}

	public Hashtable getProperties() {
		logger.debug("Getting properties");
		return super.getProperties();
	}

	public String getID() {
		logger.debug("Getting ID: ", super.getID());
		return super.getID();
	}

	public void setID(String identifier) {
		logger.debug("Setting ID: ", identifier);
		super.setID(identifier);
	}

	public void setFlag(int flag_type, boolean flag_value) {
		logger.debug("Setting flag: ", flag_type + "=" + flag_value);
		super.setFlag(flag_type, flag_value);
	}

	public boolean getFlag(int flag_type) {
		logger.debug("Setting flag: ", flag_type + "=" + super.getFlag(flag_type));
		return super.getFlag(flag_type);
	}

	public void setProperties(Hashtable properties) {
		logger.debug("Setting properties: ", properties);
		super.setProperties(properties);
	}

	public void setFlags(boolean[] flagsNew) {
		logger.debug("Setting flags:", flagsNew.length);
		super.setFlags(flagsNew);
	}

	public boolean[] getFlags() {
		logger.debug("Getting flags:", super.getFlags().length);
		return super.getFlags();
	}

	public Object clone() {
        Object clone = null;
        try {
        	clone = super.clone();
        } catch (Exception exception) {
        	logger.error("Could not clone DebugAtom: " + exception.getMessage(), exception);
        	logger.debug(exception);
        }
        return clone;
	}

	public ChemObjectBuilder getBuilder() {
		return DebugChemObjectBuilder.getInstance();
	}

	public Atom[] getAtoms() {
		logger.debug("Getting atoms: ", super.getAtoms().length);
		return super.getAtoms();
	}

	public void setAtoms(Atom[] atoms) {
		logger.debug("Setting atoms: ", atoms.length);
		super.setAtoms(atoms);
	}

	public int getAtomCount() {
		logger.debug("Getting atom count: ", super.getAtomCount());
		return super.getAtomCount();
	}

	public Atom getAtomAt(int position) {
		logger.debug("Getting atom at position: ", position);
		return super.getAtomAt(position);
	}

	public Atom getConnectedAtom(Atom atom) {
		logger.debug("Getting connected atom to atom: ", atom);
		return super.getConnectedAtom(atom);
	}

	public boolean contains(Atom atom) {
		logger.debug("Contains atom: ", atom);
		return super.contains(atom);
	}

	public void setAtomAt(Atom atom, int position) {
		logger.debug("Setting atom at position: ", atom);
		super.setAtomAt(atom, position);
	}

	public double getOrder() {
		logger.debug("Getting order: ", super.getOrder());
		return super.getOrder();
	}

	public void setOrder(double order) {
		logger.debug("Setting order: ", order);
		super.setOrder(order);
	}

	public int getStereo() {
		logger.debug("Getting stereo: ", super.getStereo());
		return super.getStereo();
	}

	public void setStereo(int stereo) {
		logger.debug("Setting stereo: ", stereo);
		super.setStereo(stereo);
	}

	public Point2d get2DCenter() {
		logger.debug("Getting 2d center: ", super.get2DCenter());
		return super.get2DCenter();
	}

	public Point3d get3DCenter() {
		logger.debug("Getting 3d center: ", super.get3DCenter());
		return super.get3DCenter();
	}

	public boolean compare(Object object) {
		logger.debug("Comparing to object: ", object);
		return super.compare(object);
	}

	public boolean isConnectedTo(Bond bond) {
		logger.debug("Is connected to bond: ", bond);
		return super.isConnectedTo(bond);
	}

}
