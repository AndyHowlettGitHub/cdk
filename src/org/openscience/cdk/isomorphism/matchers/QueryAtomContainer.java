/* $RCSfile$
 * $Author$
 * $Date$
 * $Revision$
 * 
 * Copyright (C) 2004-2005  The Chemistry Development Kit (CDK) project
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
package org.openscience.cdk.isomorphism.matchers;

import org.openscience.cdk.Atom;
import org.openscience.cdk.AtomContainer;
import org.openscience.cdk.Bond;
import org.openscience.cdk.ElectronContainer;

/**
 * @cdk.module standard
 */
public class QueryAtomContainer extends AtomContainer {

    public QueryAtomContainer() {
    };
    
    public void add(AtomContainer container) {
        if (container instanceof QueryAtomContainer) {
            super.add(container);
        } else {
            throw new IllegalArgumentException("AtomContainer is not of type QueryAtomContainer");
        }
    }
    
    public void addAtom(Atom atom) {
        if (atom instanceof QueryAtom) {
            super.addAtom(atom);
        } else {
            throw new IllegalArgumentException("Atom is not of type QueryAtom");
        }
    }

    public void addBond(Bond bond) {
        if (bond instanceof QueryBond) {
            super.addBond(bond);
        } else {
            throw new IllegalArgumentException("Bond is not of type QueryBond");
        }
    }
    
	public String toString() {
		ElectronContainer ec;
		StringBuffer s = new StringBuffer();
		s.append("QueryAtomContainer(");
		s.append(this.hashCode() + ", ");
		s.append("#A:" + getAtomCount() + ", ");
		s.append("#EC:" + getElectronContainerCount() + ", ");
		for (int i = 0; i < getAtomCount(); i++) {
			s.append(getAtomAt(i).toString() + ", ");
		}
		for (int i = 0; i < getElectronContainerCount(); i++) {
			s.append(getElectronContainerAt(i).toString() + ", ");
		}
		s.append(")");
		return s.toString();
	}
    
}

