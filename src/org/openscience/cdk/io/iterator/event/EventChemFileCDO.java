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
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA. 
 */
package org.openscience.cdk.io.iterator.event;

import java.util.Hashtable;
import org.openscience.cdk.Atom;
import org.openscience.cdk.AtomContainer;
import org.openscience.cdk.Bond;
import org.openscience.cdk.CDKConstants;
import org.openscience.cdk.ChemModel;
import org.openscience.cdk.Crystal;
import org.openscience.cdk.Molecule;
import org.openscience.cdk.PseudoAtom;
import org.openscience.cdk.SetOfMolecules;
import org.openscience.cdk.io.cml.cdopi.CDOAcceptedObjects;
import org.openscience.cdk.io.cml.cdopi.CDOInterface;
import org.openscience.cdk.tools.LoggingTool;

/**
 * CDO object needed as interface with the JCFL library for reading CML
 * in a event based manner.
 *
 * <p>The CDO only takes care about atoms, bonds and molecules.
 *
 * @cdk.module io
 * 
 * @author Egon Willighagen <egonw@sci.kun.nl>
*/ 
public class EventChemFileCDO implements CDOInterface {
    
    private AtomContainer currentMolecule;
    private Atom currentAtom;
    
    private Hashtable atomEnumeration;
    
    private int numberOfAtoms = 0;
    
    private int bond_a1;
    private int bond_a2;
    private double bond_order;
    private int bond_stereo;
    private String bond_id;
    
    protected LoggingTool logger;
    
    private DefaultEventChemObjectReader eventReader;
    
    /**
    * Constructs an iterating-abled CDO. After reading one molecule it
    * fires a frameRead event.
    */
    public EventChemFileCDO(DefaultEventChemObjectReader eventReader) {
        logger = new LoggingTool(this);
        this.eventReader = eventReader;
        clearData();
    }
    
    private void clearData() {
        currentMolecule = null;
        atomEnumeration = null;
        currentAtom = null;
    }
    
    public AtomContainer getAtomContainer() {
        return currentMolecule;
    }
    
    // procedures required by CDOInterface
    
    /**
    * Procedure required by the CDOInterface. This function is only
    * supposed to be called by the JCFL library
    */
    public void startDocument() {
        logger.info("New CDO Object");
    };
    
    /**
    * Procedure required by the CDOInterface. This function is only
    * supposed to be called by the JCFL library
    */
    public void endDocument() {
        logger.debug("Closing document");
        logger.info("End CDO Object");
    };
    
    /**
    * Procedure required by the CDOInterface. This function is only
    * supposed to be called by the JCFL library
    */
    public void setDocumentProperty(String type, String value) {};
    
    /**
    * Procedure required by the CDOInterface. This function is only
    * supposed to be called by the JCFL library
    */
    public void startObject(String objectType) {
        logger.debug("START:" + objectType);
        if (objectType.equals("Molecule")) {
            currentMolecule = new AtomContainer();
            atomEnumeration = new Hashtable();
        } else if (objectType.equals("Atom")) {
            currentAtom = new Atom("H");
            logger.debug("Atom # " + numberOfAtoms);
            numberOfAtoms++;
        } else if (objectType.equals("Bond")) {
            bond_id = null;
            bond_stereo = -99;
        }
    };
    
    /**
    * Procedure required by the CDOInterface. This function is only
    * supposed to be called by the JCFL library
    */
    public void endObject(String objectType) {
        logger.debug("END: " + objectType);
        if (objectType.equals("Molecule")) {
            eventReader.fireFrameRead();
            clearData();
        } else if (objectType.equals("Atom")) {
            currentMolecule.addAtom(currentAtom);
        } else if (objectType.equals("Bond")) {
            logger.debug("Bond(" + bond_id + "): " + bond_a1 + ", " + bond_a2 + ", " + bond_order);
            if (bond_a1 > currentMolecule.getAtomCount() ||
            bond_a2 > currentMolecule.getAtomCount()) {
                logger.error("Cannot add bond between at least one non-existant atom: " + bond_a1 +
                " and " + bond_a2);
            } else {
                Atom a1 = currentMolecule.getAtomAt(bond_a1);
                Atom a2 = currentMolecule.getAtomAt(bond_a2);
                Bond b = new Bond(a1, a2, bond_order);
                if (bond_id != null) b.setID(bond_id);
                if (bond_stereo != -99) {
                    b.setStereo(bond_stereo);
                }
                if (bond_order == CDKConstants.BONDORDER_AROMATIC) {
                    b.setFlag(CDKConstants.ISAROMATIC, true);
                }
                currentMolecule.addBond(b);
            }
        }
    };
    
    /**
    * Procedure required by the CDOInterface. This function is only
    * supposed to be called by the JCFL library
    */
    public void setObjectProperty(String objectType, String propertyType,
    String propertyValue) {
        logger.debug("objectType: " + objectType);
        logger.debug("propType: " + propertyType);
        logger.debug("property: " + propertyValue);
        
        if (objectType == null) {
            logger.error("Cannot add property for null object");
            return;
        }
        if (propertyType == null) {
            logger.error("Cannot add property for null property type");
            return;
        }
        if (propertyValue == null) {
            logger.warn("Will not add null property");
            return;
        }
        
        if (objectType.equals("Molecule")) {
            if (propertyType.equals("id")) {
                currentMolecule.setID(propertyValue);
            } else if (propertyType.equals("inchi")) {
                currentMolecule.setProperty("iupac.nist.chemical.identifier", propertyValue);
            }
        } else if (objectType.equals("PseudoAtom")) {
            if (propertyType.equals("label")) {
                if (!(currentAtom instanceof PseudoAtom)) {
                    currentAtom = new PseudoAtom(currentAtom);
                }
                ((PseudoAtom)currentAtom).setLabel(propertyValue);
            }
        } else if (objectType.equals("Atom")) {
            if (propertyType.equals("type")) {
                if (propertyValue.equals("R") && !(currentAtom instanceof PseudoAtom)) {
                    currentAtom = new PseudoAtom(currentAtom);
                }
                currentAtom.setSymbol(propertyValue);
            } else if (propertyType.equals("x2")) {
                currentAtom.setX2d(new Double(propertyValue).doubleValue());
            } else if (propertyType.equals("y2")) {
                currentAtom.setY2d(new Double(propertyValue).doubleValue());
            } else if (propertyType.equals("x3")) {
                currentAtom.setX3d(new Double(propertyValue).doubleValue());
            } else if (propertyType.equals("y3")) {
                currentAtom.setY3d(new Double(propertyValue).doubleValue());
            } else if (propertyType.equals("z3")) {
                currentAtom.setZ3d(new Double(propertyValue).doubleValue());
            } else if (propertyType.equals("xFract")) {
                currentAtom.setFractX3d(new Double(propertyValue).doubleValue());
            } else if (propertyType.equals("yFract")) {
                currentAtom.setFractY3d(new Double(propertyValue).doubleValue());
            } else if (propertyType.equals("zFract")) {
                currentAtom.setFractZ3d(new Double(propertyValue).doubleValue());
            } else if (propertyType.equals("formalCharge")) {
                currentAtom.setFormalCharge(new Integer(propertyValue).intValue());
            } else if (propertyType.equals("charge") ||
            propertyType.equals("partialCharge")) {
                currentAtom.setCharge(new Double(propertyValue).doubleValue());
            } else if (propertyType.equals("hydrogenCount")) {
                currentAtom.setHydrogenCount(new Integer(propertyValue).intValue());
            } else if (propertyType.equals("dictRef")) {
                currentAtom.setProperty("org.openscience.cdk.dict", propertyValue);
            } else if (propertyType.equals("atomicNumber")) {
                currentAtom.setAtomicNumber(Integer.parseInt(propertyValue));
            } else if (propertyType.equals("massNumber")) {
                currentAtom.setMassNumber((new Double(propertyValue)).intValue());
            } else if (propertyType.equals("id")) {
                logger.debug("id: ", propertyValue);
                currentAtom.setID(propertyValue);
                atomEnumeration.put(propertyValue, new Integer(numberOfAtoms));
            }
        } else if (objectType.equals("Bond")) {
            if (propertyType.equals("atom1")) {
                bond_a1 = new Integer(propertyValue).intValue();
            } else if (propertyType.equals("atom2")) {
                bond_a2 = new Integer(propertyValue).intValue();
            } else if (propertyType.equals("id")) {
                logger.debug("id: " + propertyValue);
                bond_id = propertyValue;
            } else if (propertyType.equals("order")) {
                try {
                    bond_order = Double.parseDouble(propertyValue);
                } catch (Exception e) {
                    logger.error("Cannot convert to double: " + propertyValue);
                    bond_order = 1.0;
                }
            } else if (propertyType.equals("stereo")) {
                if (propertyValue.equals("H")) {
                    bond_stereo = CDKConstants.STEREO_BOND_DOWN;
                } else if (propertyValue.equals("W")) {
                    bond_stereo = CDKConstants.STEREO_BOND_UP;
                }
            }
        };
        logger.debug("Object property set...");
    };
    
    /**
    * Procedure required by the CDOInterface. This function is only
    * supposed to be called by the JCFL library
    */
    public CDOAcceptedObjects acceptObjects() {
        CDOAcceptedObjects objects = new CDOAcceptedObjects();
        objects.add("Molecule");
        objects.add("PsuedoAtom");
        objects.add("Atom");
        objects.add("Bond");
        return objects;
    };
}

