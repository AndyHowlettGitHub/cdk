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
 *
 */
package org.openscience.cdk.io.cml;

import java.util.Enumeration;
import java.util.StringTokenizer;
import java.util.Vector;

import org.openscience.cdk.geometry.CrystalGeometryTools;
import org.openscience.cdk.io.cml.cdopi.CDOInterface;
import org.xml.sax.Attributes;

/**
 * Core CML 1.x and 2.0 elements are parsed by this class.
 *
 * <p>Please file a bug report if this parser fails to parse
 * a certain element or attribute value in a valid CML document.
 *
 * @cdk.module io
 *
 * @author Egon Willighagen <egonw@sci.kun.nl>
 **/
public class CMLCoreModule implements ModuleInterface {

    protected org.openscience.cdk.tools.LoggingTool logger;
    protected final String SYSTEMID = "CML-1999-05-15";
    protected CDOInterface cdo;
    
    protected int atomCounter;
    protected Vector elsym;
    protected Vector eltitles;
    protected Vector elid;
    protected Vector formalCharges;
    protected Vector partialCharges;
    protected Vector isotope;
    protected Vector x3;
    protected Vector y3;
    protected Vector z3;
    protected Vector x2;
    protected Vector y2;
    protected Vector xfract;
    protected Vector yfract;
    protected Vector zfract;
    protected Vector hCounts;
    protected Vector atomParities;
    protected Vector atomDictRefs;

    protected int bondCounter;
    protected Vector bondid;
    protected Vector bondARef1;
    protected Vector bondARef2;
    protected Vector order;
    protected Vector bondStereo;
    protected Vector bondDictRefs;
    protected Vector bondElid;
    protected boolean stereoGiven;
    protected int curRef;
    protected int CurrentElement;
    protected String BUILTIN;
    protected String DICTREF;
    protected String elementTitle;
    protected String currentChars;
    
    protected double[] unitcellparams;
    protected int crystalScalar;
    
    private double[] a;
    private double[] b;
    private double[] c;
    boolean cartesianAxesSet = false;
    
    public CMLCoreModule(CDOInterface cdo) {
        logger = new org.openscience.cdk.tools.LoggingTool(this.getClass().getName());
        this.cdo = cdo;
    }
    
    public CMLCoreModule(ModuleInterface conv) {
        inherit(conv);
    }

    public void inherit(ModuleInterface convention) {
        if (convention instanceof CMLCoreModule) {
            CMLCoreModule conv = (CMLCoreModule)convention;
            this.logger = conv.logger;
            this.cdo = conv.returnCDO();
            this.BUILTIN = conv.BUILTIN;
            this.atomCounter = conv.atomCounter;
            this.elsym = conv.elsym;
            this.eltitles = conv.eltitles;
            this.elid = conv.elid;
            this.formalCharges = conv.formalCharges;
            this.partialCharges = conv.partialCharges;
            this.isotope = conv.isotope;
            this.x3 = conv.x3;
            this.y3 = conv.y3;
            this.z3 = conv.z3;
            this.x2 = conv.x2;
            this.y2 = conv.y2;
            this.xfract = conv.xfract;
            this.yfract = conv.yfract;
            this.zfract = conv.zfract;
            this.hCounts = conv.hCounts;
            this.atomParities = conv.atomParities;
            this.atomDictRefs = conv.atomDictRefs;
            this.bondCounter = conv.bondCounter;
            this.bondid = conv.bondid;
            this.bondARef1 = conv.bondARef1;
            this.bondARef2 = conv.bondARef2;
            this.order = conv.order;
            this.bondStereo = conv.bondStereo;
            this.bondDictRefs = conv.bondDictRefs;
            this.curRef = conv.curRef;
            this.unitcellparams = conv.unitcellparams;
        } else {
            logger.warn("Cannot inherit information from module: " + convention.getClass().getName());
        }
    }

    public CDOInterface returnCDO() {
        return (CDOInterface)this.cdo;
    }
    
    /**
     * Clean all data about parsed data.
     */
    protected void newMolecule() {
        newAtomData();
        newBondData();
        newCrystalData();
    }
    
    /**
     * Clean all data about read atoms.
     */
    protected void newAtomData() {
        atomCounter = 0;
        elsym = new Vector();
        elid = new Vector();
        eltitles = new Vector();
        formalCharges = new Vector();
        partialCharges = new Vector();
        isotope = new Vector();
        x3 = new Vector();
        y3 = new Vector();
        z3 = new Vector();
        x2 = new Vector();
        y2 = new Vector();
        xfract = new Vector();
        yfract = new Vector();
        zfract = new Vector();
        hCounts = new Vector();
        atomParities = new Vector();
        atomDictRefs = new Vector();
    }

    /**
     * Clean all data about read bonds.
     */
    protected void newBondData() {
        bondCounter = 0;
        bondid = new Vector();
        bondARef1 = new Vector();
        bondARef2 = new Vector();
        order = new Vector();
        bondStereo = new Vector();
        bondDictRefs = new Vector();
        bondElid = new Vector();
    }

    /**
     * Clean all data about read bonds.
     */
    protected void newCrystalData() {
        unitcellparams = new double[6];
        cartesianAxesSet = false;
        crystalScalar = 0;
        a = new double[3];
        b = new double[3];
        c = new double[3];
    }

    public void startDocument() {
        logger.info("Start XML Doc");
        cdo.startDocument();
        newMolecule();
        BUILTIN = "";
        curRef = 0;
    }
    
    public void endDocument() {
        cdo.endDocument();
        logger.info("End XML Doc");
    }
    
    public void startElement(CMLStack xpath, String uri, String local, String raw, 
                              Attributes atts) {

        String name = local;
        logger.debug("StartElement");
        currentChars = "";
        
        BUILTIN = "";
        DICTREF = "";

        for (int i=0; i<atts.getLength(); i++) {
            String qname = atts.getQName(i);
            if (qname.equals("builtin")) {
                BUILTIN = atts.getValue(i);
                logger.debug(name, "->BUILTIN found: ", atts.getValue(i));
            } else if (qname.equals("dictRef")) {
                DICTREF = atts.getValue(i);
                logger.debug(name, "->DICTREF found: ", atts.getValue(i));
            } else if (qname.equals("title")) {
                elementTitle = atts.getValue(i);
                logger.debug(name, "->TITLE found: ", atts.getValue(i));
            } else {
                logger.debug("Qname: ", qname);
            }
        }
        
        if ("atom".equals(name)) {
            atomCounter++;
            for (int i = 0; i < atts.getLength(); i++) {
                
                String att = atts.getQName(i);
                String value = atts.getValue(i);
                
                if (att.equals("id")) { // this is supported in CML 1.x
                    elid.addElement(value);
                } // this is supported in CML 2.0 
                else if (att.equals("elementType")) {
                    elsym.addElement(value);
                } // this is supported in CML 2.0 
                else if (att.equals("title")) {
                    eltitles.addElement(value);
                } // this is supported in CML 2.0 
                else if (att.equals("x2")) {
                    x2.addElement(value);
                } // this is supported in CML 2.0 
                else if (att.equals("xy2")) {
                    StringTokenizer tokenizer = new StringTokenizer(value);
                    x2.addElement(tokenizer.nextToken());
                    y2.addElement(tokenizer.nextToken());
                } // this is supported in CML 2.0 
                else if (att.equals("xyzFract")) {
                    StringTokenizer tokenizer = new StringTokenizer(value);
                    xfract.addElement(tokenizer.nextToken());
                    yfract.addElement(tokenizer.nextToken());
                    zfract.addElement(tokenizer.nextToken());
                } // this is supported in CML 2.0 
                else if (att.equals("xyz3")) {
                    StringTokenizer tokenizer = new StringTokenizer(value);
                    x3.addElement(tokenizer.nextToken());
                    y3.addElement(tokenizer.nextToken());
                    z3.addElement(tokenizer.nextToken());
                } // this is supported in CML 2.0 
                else if (att.equals("y2")) {
                    y2.addElement(value);
                } // this is supported in CML 2.0 
                else if (att.equals("x3")) {
                    x3.addElement(value);
                } // this is supported in CML 2.0 
                else if (att.equals("y3")) {
                    y3.addElement(value);
                } // this is supported in CML 2.0 
                else if (att.equals("z3")) {
                    z3.addElement(value);
                } // this is supported in CML 2.0 
                else if (att.equals("xFract")) {
                    xfract.addElement(value);
                } // this is supported in CML 2.0 
                else if (att.equals("yFract")) {
                    yfract.addElement(value);
                } // this is supported in CML 2.0 
                else if (att.equals("zFract")) {
                    zfract.addElement(value);
                } // this is supported in CML 2.0 
                else if (att.equals("formalCharge")) {
                    formalCharges.addElement(value);
                } // this is supported in CML 2.0 
                else if (att.equals("hydrogenCount")) {
                    hCounts.addElement(value);
                }
                else if (att.equals("isotope")) {
                    isotope.addElement(value);
                }
                else if (att.equals("dictRef")) {
                    atomDictRefs.addElement(value);
                } else {
                    logger.warn("Unparsed attribute: " + att);
                }
            }
        } else if ("atomArray".equals(name)) {
            boolean atomsCounted = false;
            for (int i = 0; i < atts.getLength(); i++) {
                String att = atts.getQName(i);
                int count = 0;
                if (att.equals("atomID")) {
                    count = addArrayElementsTo(elid, atts.getValue(i));
                } else if (att.equals("elementType")) {
                    count = addArrayElementsTo(elsym, atts.getValue(i));
                } else if (att.equals("x2")) {
                    count = addArrayElementsTo(x2, atts.getValue(i));
                } else if (att.equals("y2")) {
                    count = addArrayElementsTo(y2, atts.getValue(i));
                } else if (att.equals("x3")) {
                    count = addArrayElementsTo(x3, atts.getValue(i));
                } else if (att.equals("y3")) {
                    count = addArrayElementsTo(y3, atts.getValue(i));
                } else if (att.equals("z3")) {
                    count = addArrayElementsTo(z3, atts.getValue(i));
                } else if (att.equals("xFract")) {
                    count = addArrayElementsTo(xfract, atts.getValue(i));
                } else if (att.equals("yFract")) {
                    count = addArrayElementsTo(yfract, atts.getValue(i));
                } else if (att.equals("zFract")) {
                    count = addArrayElementsTo(zfract, atts.getValue(i));
                } else {
                    logger.warn("Unparsed attribute: " + att);
                }
                if (!atomsCounted) {
                    atomCounter += count;
                    atomsCounted = true;
                }
            }
        } else if ("bond".equals(name)) {
            bondCounter++;
            for (int i = 0; i < atts.getLength(); i++) {
                String att = atts.getQName(i);
                logger.debug("B2 ", att, "=", atts.getValue(i));
                
                if (att.equals("id")) {
                    bondid.addElement(atts.getValue(i));
                    logger.debug("B3 ", bondid);
                } else if (att.equals("atomRefs") || // this is CML 1.x support
                           att.equals("atomRefs2")) { // this is CML 2.0 support
                    
                    // expect exactly two references
                    try {
                        StringTokenizer st = new StringTokenizer(atts.getValue(
                        i));
                        bondARef1.addElement((String)st.nextElement());
                        bondARef2.addElement((String)st.nextElement());
                    } catch (Exception e) {
                        logger.error("Error in CML file: ", e.getMessage());
                        logger.debug(e);
                    }
                } else if (att.equals("order")) { // this is CML 2.0 support
                    order.addElement(atts.getValue(i).trim());
                } else if (att.equals("dictRef")) {
                    bondDictRefs.addElement(atts.getValue(i).trim());
                }
            }
            
            stereoGiven = false;
            curRef = 0;
        } else if ("bondArray".equals(name)) {
            boolean bondsCounted = false;
            for (int i = 0; i < atts.getLength(); i++) {
                String att = atts.getQName(i);
                int count = 0;
                if (att.equals("bondID")) {
                    count = addArrayElementsTo(bondid, atts.getValue(i));
                } else if (att.equals("atomRefs1")) {
                    count = addArrayElementsTo(bondARef1, atts.getValue(i));
                } else if (att.equals("atomRefs2")) {
                    count = addArrayElementsTo(bondARef2, atts.getValue(i));
                } else if (att.equals("atomRef1")) {
                    count = addArrayElementsTo(bondARef1, atts.getValue(i));
                } else if (att.equals("atomRef2")) {
                    count = addArrayElementsTo(bondARef2, atts.getValue(i));
                } else if (att.equals("order")) {
                    count = addArrayElementsTo(order, atts.getValue(i));
                } else {
                    logger.warn("Unparsed attribute: " + att);
                }
                if (!bondsCounted) {
                    bondCounter += count;
                    bondsCounted = true;
                }
            }
            curRef = 0;
        } else if ("molecule".equals(name)) {
            newMolecule();
            BUILTIN = "";
            cdo.startObject("Molecule");
            for (int i = 0; i < atts.getLength(); i++) {
                
                String att = atts.getQName(i);
                String value = atts.getValue(i);
                
                if (att.equals("id")) {
                    cdo.setObjectProperty("Molecule", "id", atts.getValue(i));
                }
            }
        } else if ("crystal".equals(name)) {
            newCrystalData();
            cdo.startObject("Crystal");
            for (int i = 0; i < atts.getLength(); i++) {
                String att = atts.getQName(i);
                if (att.equals("z")) {
                    cdo.setObjectProperty("Crystal", "z", atts.getValue(i));
                }
            }
        } else if ("symmetry".equals(name)) {
            for (int i = 0; i < atts.getLength(); i++) {
                String att = atts.getQName(i);
                if (att.equals("spaceGroup")) {
                    cdo.setObjectProperty("Crystal", "spacegroup", atts.getValue(i));
                }
            }
        } else if ("scalar".equals(name)) {
            if (xpath.toString().endsWith("crystal/scalar/"))
                crystalScalar++;
        } else if ("list".equals(name)) {
            cdo.startObject("SetOfMolecules");
        }
    }

    public void endElement(CMLStack xpath, String uri, String name, String raw) {
        logger.debug("EndElement: ", name);

        String cData = currentChars;
        
        if ("bond".equals(name)) {
            if (!stereoGiven)
                bondStereo.addElement("");
            if (bondStereo.size() > bondDictRefs.size())
                bondDictRefs.addElement(null);
        } else if ("atom".equals(name)) {
            if (atomCounter > eltitles.size()) {
                eltitles.addElement(null);
            }
            if (atomCounter > hCounts.size()) {
                /* while strictly undefined, assume zero 
                implicit hydrogens when no number is given */
                hCounts.addElement("0");
            }
            if (atomCounter > atomDictRefs.size()) {
                atomDictRefs.addElement(null);
            }
            if (atomCounter > isotope.size()) {
                isotope.addElement(null);
            }
            if (atomCounter > formalCharges.size()) {
                /* while strictly undefined, assume zero 
                implicit hydrogens when no number is given */
                formalCharges.addElement("0");
            }
            /* It may happen that not all atoms have
            associated 2D or 3D coordinates. accept that */
            if (atomCounter > x2.size() && x2.size() != 0) {
                /* apparently, the previous atoms had atomic
                coordinates, add 'null' for this atom */
                x2.addElement(null);
                y2.addElement(null);
            }
            if (atomCounter > x3.size() && x3.size() != 0) {
                /* apparently, the previous atoms had atomic
                coordinates, add 'null' for this atom */
                x3.addElement(null);
                y3.addElement(null);
                z3.addElement(null);
            }
            
            if (atomCounter > xfract.size() && xfract.size() != 0) {
                /* apparently, the previous atoms had atomic
                coordinates, add 'null' for this atom */
                xfract.addElement(null);
                yfract.addElement(null);
                zfract.addElement(null);
            }
        } else if ("molecule".equals(name)) {
            storeData();
            cdo.endObject("Molecule");
        } else if ("crystal".equals(name)) {
            if (crystalScalar > 0) {
                // convert unit cell parameters to cartesians
                double[][] axes = CrystalGeometryTools.notionalToCartesian(
                unitcellparams[0], unitcellparams[1], unitcellparams[2],
                unitcellparams[3], unitcellparams[4], unitcellparams[5]
                );
                a[0] = axes[0][0];
                a[1] = axes[0][1];
                a[2] = axes[0][2];
                b[0] = axes[1][0];
                b[1] = axes[1][1];
                b[2] = axes[1][2];
                c[0] = axes[2][0];
                c[1] = axes[2][1];
                c[2] = axes[2][2];
                cartesianAxesSet = true;
                cdo.startObject("a-axis");
                cdo.setObjectProperty("a-axis", "x", new Double(a[0]).toString());
                cdo.setObjectProperty("a-axis", "y", new Double(a[1]).toString());
                cdo.setObjectProperty("a-axis", "z", new Double(a[2]).toString());
                cdo.endObject("a-axis");
                cdo.startObject("b-axis");
                cdo.setObjectProperty("b-axis", "x", new Double(b[0]).toString());
                cdo.setObjectProperty("b-axis", "y", new Double(b[1]).toString());
                cdo.setObjectProperty("b-axis", "z", new Double(b[2]).toString());
                cdo.endObject("b-axis");
                cdo.startObject("c-axis");
                cdo.setObjectProperty("c-axis", "x", new Double(c[0]).toString());
                cdo.setObjectProperty("c-axis", "y", new Double(c[1]).toString());
                cdo.setObjectProperty("c-axis", "z", new Double(c[2]).toString());
                cdo.endObject("c-axis");
            } else {
                logger.error("Could not find crystal unit cell parameters");
            }
            cdo.endObject("Crystal");
        } else if ("list".equals(name)) {
            cdo.endObject("SetOfMolecules");
        } else if ("coordinate3".equals(name)) {
            if (BUILTIN.equals("xyz3")) {
                logger.debug("New coord3 xyz3 found: ", currentChars);
                
                try {
                    
                    StringTokenizer st = new StringTokenizer(currentChars);
                    x3.addElement(st.nextToken());
                    y3.addElement(st.nextToken());
                    z3.addElement(st.nextToken());
                    logger.debug("coord3 x3.length: ", x3.size());
                    logger.debug("coord3 y3.length: ", y3.size());
                    logger.debug("coord3 z3.length: ", z3.size());
                } catch (Exception exception) {
                    logger.error(
                    "CMLParsing error while setting coordinate3!");
                    logger.debug(exception);
                }
            } else {
                logger.warn("Unknown coordinate3 BUILTIN: " + BUILTIN);
            }
        } else if ("string".equals(name)) {
            if (BUILTIN.equals("elementType")) {
                logger.debug("Element: ", cData.trim());
                elsym.addElement(cData);
            } else if (BUILTIN.equals("atomRef")) {
                curRef++;
                logger.debug("Bond: ref #", curRef);
                
                if (curRef == 1) {
                    bondARef1.addElement(cData.trim());
                } else if (curRef == 2) {
                    bondARef2.addElement(cData.trim());
                }
            } else if (BUILTIN.equals("order")) {
                logger.debug("Bond: order ", cData.trim());
                order.addElement(cData.trim());
            } else if (BUILTIN.equals("formalCharge")) {
                // NOTE: this combination is in violation of the CML DTD!!!
                logger.warn("formalCharge BUILTIN accepted but violating CML DTD");
                logger.debug("Charge: ", cData.trim());
                String charge = cData.trim();
                if (charge.startsWith("+") && charge.length() > 1) {
                    charge = charge.substring(1);
                }
                formalCharges.addElement(charge);
            }
        } else if ("float".equals(name)) {
            if (BUILTIN.equals("x3")) {
                x3.addElement(cData.trim());
            } else if (BUILTIN.equals("y3")) {
                y3.addElement(cData.trim());
            } else if (BUILTIN.equals("z3")) {
                z3.addElement(cData.trim());
            } else if (BUILTIN.equals("x2")) {
                x2.addElement(cData.trim());
            } else if (BUILTIN.equals("y2")) {
                y2.addElement(cData.trim());
            } else if (BUILTIN.equals("order")) {
                // NOTE: this combination is in violation of the CML DTD!!!
                order.addElement(cData.trim());
            } else if (BUILTIN.equals("charge") || BUILTIN.equals("partialCharge")) {
                partialCharges.addElement(cData.trim());
            }
        } else if ("integer".equals(name)) {
            if (BUILTIN.equals("formalCharge")) {
                formalCharges.addElement(cData.trim());
            }
        } else if ("coordinate2".equals(name)) {
            if (BUILTIN.equals("xy2")) {
                logger.debug("New coord2 xy2 found.", cData);
                
                try {
                    
                    StringTokenizer st = new StringTokenizer(cData);
                    x2.addElement(st.nextToken());
                    y2.addElement(st.nextToken());
                } catch (Exception e) {
                    notify("CMLParsing error: " + e, SYSTEMID, 175, 1);
                }
            }
        } else if ("stringArray".equals(name)) {
            if (BUILTIN.equals("id") || BUILTIN.equals("atomId")
                || BUILTIN.equals("atomID")) { // invalid according to CML1 DTD but found in OpenBabel 1.x output
                
                try {
                    boolean countAtoms = (atomCounter == 0) ? true : false;
                    StringTokenizer st = new StringTokenizer(cData);
                    
                    while (st.hasMoreTokens()) {
                        if (countAtoms) { atomCounter++; }
                        String token = st.nextToken();
                        logger.debug("StringArray (Token): ", token);
                        elid.addElement(token);
                    }
                } catch (Exception e) {
                    notify("CMLParsing error: " + e, SYSTEMID, 186, 1);
                }
            } else if (BUILTIN.equals("elementType")) {
                
                try {
                    boolean countAtoms = (atomCounter == 0) ? true : false;
                    StringTokenizer st = new StringTokenizer(cData);
                    
                    while (st.hasMoreTokens()) {
                        if (countAtoms) { atomCounter++; }
                        elsym.addElement(st.nextToken());
                    }
                } catch (Exception e) {
                    notify("CMLParsing error: " + e, SYSTEMID, 194, 1);
                }
            } else if (BUILTIN.equals("atomRefs")) {
                curRef++;
                logger.debug("New atomRefs found: ", curRef);
                
                try {
                    boolean countBonds = (bondCounter == 0) ? true : false;
                    StringTokenizer st = new StringTokenizer(cData);
                    
                    while (st.hasMoreTokens()) {
                        if (countBonds) { bondCounter++; }
                        String token = st.nextToken();
                        logger.debug("Token: ", token);
                        
                        if (curRef == 1) {
                            bondARef1.addElement(token);
                        } else if (curRef == 2) {
                            bondARef2.addElement(token);
                        }
                    }
                } catch (Exception e) {
                    notify("CMLParsing error: " + e, SYSTEMID, 194, 1);
                }
            } else if (BUILTIN.equals("atomRef")) {
                curRef++;
                logger.debug("New atomRef found: ", curRef); // this is CML1 stuff, we get things like:
                /*
                  <bondArray>
                  <stringArray builtin="atomRef">a2 a2 a2 a2 a3 a3 a4 a4 a5 a6 a7 a9</stringArray>
                  <stringArray builtin="atomRef">a9 a11 a12 a13 a5 a4 a6 a9 a7 a8 a8 a10</stringArray>
                  <stringArray builtin="order">1 1 1 1 2 1 2 1 1 1 2 2</stringArray>
                  </bondArray>
                */
                
                try {
                    boolean countBonds = (bondCounter == 0) ? true : false;
                    StringTokenizer st = new StringTokenizer(cData);
                    
                    while (st.hasMoreTokens()) {
                        if (countBonds) { bondCounter++; }
                        String token = st.nextToken();
                        logger.debug("Token: ", token);
                        
                        if (curRef == 1) {
                            bondARef1.addElement(token);
                        } else if (curRef == 2) {
                            bondARef2.addElement(token);
                        }
                    }
                } catch (Exception e) {
                    notify("CMLParsing error: " + e, SYSTEMID, 194, 1);
                }
            } else if (BUILTIN.equals("order")) {
                logger.debug("New bond order found.");
                
                try {
                    
                    StringTokenizer st = new StringTokenizer(cData);
                    
                    while (st.hasMoreTokens()) {
                        
                        String token = st.nextToken();
                        logger.debug("Token: ", token);
                        order.addElement(token);
                    }
                } catch (Exception e) {
                    notify("CMLParsing error: " + e, SYSTEMID, 194, 1);
                }
            }
        } else if ("integerArray".equals(name)) {
            logger.debug("IntegerArray: builtin = ", BUILTIN);
            
            if (BUILTIN.equals("formalCharge")) {
                
                try {
                    
                    StringTokenizer st = new StringTokenizer(cData);
                    
                    while (st.hasMoreTokens()) {
                        
                        String token = st.nextToken();
                        logger.debug("Charge added: ", token);
                        formalCharges.addElement(token);
                    }
                } catch (Exception e) {
                    notify("CMLParsing error: " + e, SYSTEMID, 205, 1);
                }
            }
        } else if ("scalar".equals(name)) {
            if (xpath.toString().endsWith("crystal/scalar/")) {
                logger.debug("Going to set a crystal parameter: " + crystalScalar, 
                    " to ", cData);
                try {
                    unitcellparams[crystalScalar-1] = Double.parseDouble(cData.trim());
                } catch (NumberFormatException exception) {
                    logger.error("Content must a float: " + cData);
                }
            } else if (xpath.toString().endsWith("bond/scalar/")) {
                if (DICTREF.equals("mdl:stereo")) {
                    bondStereo.addElement(cData.trim());
                }
            } else {
                logger.warn("Ignoring scaler: " + xpath);
            }
        } else if ("floatArray".equals(name)) {
            if (BUILTIN.equals("x3")) {
                
                try {
                    
                    StringTokenizer st = new StringTokenizer(cData);
                    
                    while (st.hasMoreTokens())
                        x3.addElement(st.nextToken());
                } catch (Exception e) {
                    notify("CMLParsing error: " + e, SYSTEMID, 205, 1);
                }
            } else if (BUILTIN.equals("y3")) {
                
                try {
                    
                    StringTokenizer st = new StringTokenizer(cData);
                    
                    while (st.hasMoreTokens())
                        y3.addElement(st.nextToken());
                } catch (Exception e) {
                    notify("CMLParsing error: " + e, SYSTEMID, 213, 1);
                }
            } else if (BUILTIN.equals("z3")) {
                
                try {
                    
                    StringTokenizer st = new StringTokenizer(cData);
                    
                    while (st.hasMoreTokens())
                        z3.addElement(st.nextToken());
                } catch (Exception e) {
                    notify("CMLParsing error: " + e, SYSTEMID, 221, 1);
                }
            } else if (BUILTIN.equals("x2")) {
                logger.debug("New floatArray found.");
                
                try {
                    
                    StringTokenizer st = new StringTokenizer(cData);
                    
                    while (st.hasMoreTokens())
                        x2.addElement(st.nextToken());
                } catch (Exception e) {
                    notify("CMLParsing error: " + e, SYSTEMID, 205, 1);
                }
            } else if (BUILTIN.equals("y2")) {
                logger.debug("New floatArray found.");
                
                try {
                    
                    StringTokenizer st = new StringTokenizer(cData);
                    
                    while (st.hasMoreTokens())
                        y2.addElement(st.nextToken());
                } catch (Exception e) {
                    notify("CMLParsing error: " + e, SYSTEMID, 454, 1);
                }
            } else if (BUILTIN.equals("partialCharge")) {
                logger.debug("New floatArray with partial charges found.");
                
                try {
                    
                    StringTokenizer st = new StringTokenizer(cData);
                    
                    while (st.hasMoreTokens())
                        partialCharges.addElement(st.nextToken());
                } catch (Exception e) {
                    notify("CMLParsing error: " + e, SYSTEMID, 462, 1);
                }
            }
        } else {
            logger.warn("Skipping element: " + name);
        }

        currentChars = "";
        BUILTIN = "";
        elementTitle = "";
    }

    public void characterData(CMLStack xpath, char[] ch, int start, int length) {
        currentChars = currentChars + new String(ch, start, length);
        logger.debug("CD: ", currentChars);
    }

    protected void notify(String message, String systemId, int line, 
                          int column) {
        logger.debug("Message: ", message);
        logger.debug("SystemId: ", systemId);
        logger.debug("Line: ", line);
        logger.debug("Column: ", column);
    }

    protected void storeData() {
        storeAtomData();
        storeBondData();
    }

    protected void storeAtomData() {
        logger.debug("No atoms: ", atomCounter);
        if (atomCounter == 0) {
            return;
        }

        boolean hasID = false;
        boolean has3D = false;
        boolean has3Dfract = false;
        boolean has2D = false;
        boolean hasFormalCharge = false;
        boolean hasPartialCharge = false;
        boolean hasHCounts = false;
        boolean hasSymbols = false;
        boolean hasTitles = false;
        boolean hasIsotopes = false;
        boolean hasDictRefs = false;

        if (elid.size() == atomCounter) {
            hasID = true;
        } else {
            logger.debug("No atom ids: " + elid.size(), " != " + atomCounter);
        }

        if (elsym.size() == atomCounter) {
            hasSymbols = true;
        } else {
            logger.debug(
                    "No atom symbols: " + elsym.size(), " != " + atomCounter);
        }

        if (eltitles.size() == atomCounter) {
            hasTitles = true;
        } else {
            logger.debug(
                    "No atom titles: " + eltitles.size(), " != " + atomCounter);
        }

        if ((x3.size() == atomCounter) && (y3.size() == atomCounter) && 
            (z3.size() == atomCounter)) {
            has3D = true;
        } else {
            logger.debug(
                    "No 3D info: " + x3.size(), " " + y3.size(), " " + 
                    z3.size(), " != " + atomCounter);
        }

        if ((xfract.size() == atomCounter) && (yfract.size() == atomCounter) && 
            (zfract.size() == atomCounter)) {
            has3Dfract = true;
        } else {
            logger.debug(
                    "No 3D fractional info: " + xfract.size(), " " + yfract.size(), " " + 
                    zfract.size(), " != " + atomCounter);
        }

        if ((x2.size() == atomCounter) && (y2.size() == atomCounter)) {
            has2D = true;
        } else {
            logger.debug(
                    "No 2D info: " + x2.size(), " " + y2.size(), " != " + 
                    atomCounter);
        }

        if (formalCharges.size() == atomCounter) {
            hasFormalCharge = true;
        } else {
            logger.debug(
                    "No formal Charge info: " + formalCharges.size(), 
                    " != " + atomCounter);
        }

        if (partialCharges.size() == atomCounter) {
            hasPartialCharge = true;
        } else {
            logger.debug(
                    "No partial Charge info: " + partialCharges.size(),
                    " != " + atomCounter);
        }

        if (hCounts.size() == atomCounter) {
            hasHCounts = true;
        } else {
            logger.debug(
                    "No hydrogen Count info: " + hCounts.size(), 
                    " != " + atomCounter);
        }

        if (atomDictRefs.size() == atomCounter) {
            hasDictRefs = true;
        } else {
            logger.debug(
                    "No dictRef info: " + atomDictRefs.size(),
                    " != " + atomCounter);
        }

        if (isotope.size() == atomCounter) {
            hasIsotopes = true;
        } else {
            logger.debug(
                    "No isotope info: " + isotope.size(),
                    " != " + atomCounter);
        }

        for (int i = 0; i < atomCounter; i++) {
            logger.info("Storing atom: ", i);
            cdo.startObject("Atom");
            if (hasID) {
                cdo.setObjectProperty("Atom", "id", (String)elid.elementAt(i));
            }
            if (hasTitles) {
                if (hasSymbols) {
                    String symbol = (String)elsym.elementAt(i);
                    if (symbol.equals("Du") || symbol.equals("Dummy")) {
                        cdo.setObjectProperty("PseudoAtom", "label", (String)eltitles.elementAt(i));
                    } else {
                        cdo.setObjectProperty("Atom", "title", (String)eltitles.elementAt(i));
                    }
                } else {
                    cdo.setObjectProperty("Atom", "title", (String)eltitles.elementAt(i));
                }
            }

            // store optional atom properties
            if (hasSymbols) {
                String symbol = (String)elsym.elementAt(i);
                if (symbol.equals("Du") || symbol.equals("Dummy")) {
                    symbol = "R";
                }
                cdo.setObjectProperty("Atom", "type", symbol);
            }

            if (has3D) {
                cdo.setObjectProperty("Atom", "x3", (String)x3.elementAt(i));
                cdo.setObjectProperty("Atom", "y3", (String)y3.elementAt(i));
                cdo.setObjectProperty("Atom", "z3", (String)z3.elementAt(i));
            }

            if (has3Dfract) {
                // ok, need to convert fractional into eucledian coordinates
                cdo.setObjectProperty("Atom", "xFract", (String)xfract.elementAt(i));
                cdo.setObjectProperty("Atom", "yFract", (String)yfract.elementAt(i));
                cdo.setObjectProperty("Atom", "zFract", (String)zfract.elementAt(i));
            }

            if (hasFormalCharge) {
                cdo.setObjectProperty("Atom", "formalCharge", 
                                      (String)formalCharges.elementAt(i));
            }

            if (hasPartialCharge) {
                logger.debug("Storing partial atomic charge...");
                cdo.setObjectProperty("Atom", "partialCharge", 
                                      (String)partialCharges.elementAt(i));
            }

            if (hasHCounts) {
                cdo.setObjectProperty("Atom", "hydrogenCount", (String)hCounts.elementAt(i));
            }

            if (has2D) {
                if (x2.elementAt(i) != null)
                    cdo.setObjectProperty("Atom", "x2", (String)x2.elementAt(i));
                if (y2.elementAt(i) != null)
                    cdo.setObjectProperty("Atom", "y2", (String)y2.elementAt(i));
            }
            
            if (hasDictRefs) {
                cdo.setObjectProperty("Atom", "dictRef", (String)atomDictRefs.elementAt(i));
            }

            if (hasIsotopes) {
                cdo.setObjectProperty("Atom", "massNumber", (String)isotope.elementAt(i));
            }

            cdo.endObject("Atom");
        }
        if (elid.size() > 0) {
            // assume this is the current working list
            bondElid = elid;
        }
        newAtomData();
    }
    
    protected void storeBondData() {
        logger.debug(
                "Testing a1,a2,stereo,order = count: " + bondARef1.size(), "," + 
                bondARef2.size(), "," + bondStereo.size(), "," + order.size(), "=" +
                bondCounter);

        if ((bondARef1.size() == bondCounter) && 
            (bondARef2.size() == bondCounter)) {
            logger.debug("About to add bond info to ", cdo.getClass().getName());

            Enumeration orders = order.elements();
            Enumeration ids = bondid.elements();
            Enumeration bar1s = bondARef1.elements();
            Enumeration bar2s = bondARef2.elements();
            Enumeration stereos = bondStereo.elements();

            while (bar1s.hasMoreElements()) {
                cdo.startObject("Bond");
                if (ids.hasMoreElements()) {
                    cdo.setObjectProperty("Bond", "id", (String)ids.nextElement());
                }
                cdo.setObjectProperty("Bond", "atom1", 
                                      new Integer(bondElid.indexOf(
                                                          (String)bar1s.nextElement())).toString());
                cdo.setObjectProperty("Bond", "atom2", 
                                      new Integer(bondElid.indexOf(
                                                          (String)bar2s.nextElement())).toString());

                if (orders.hasMoreElements()) {
                    String bondOrder = (String)orders.nextElement();
                    
                    if ("S".equals(bondOrder)) {
                        cdo.setObjectProperty("Bond", "order", "1");
                    } else if ("D".equals(bondOrder)) {
                        cdo.setObjectProperty("Bond", "order", "2");
                    } else if ("T".equals(bondOrder)) {
                        cdo.setObjectProperty("Bond", "order", "3");
                    } else if ("A".equals(bondOrder)) {
                        cdo.setObjectProperty("Bond", "order", "1.5");
                    } else {
                        cdo.setObjectProperty("Bond", "order", bondOrder);
                    }
                }

                if (stereos.hasMoreElements()) {
                    cdo.setObjectProperty("Bond", "stereo", 
                                          (String)stereos.nextElement());
                }

                cdo.endObject("Bond");
            }
        }
        newBondData();
    }

    private int addArrayElementsTo(Vector toAddto, String array) {
        StringTokenizer tokenizer = new StringTokenizer(array);
        int i = 0;
        while (tokenizer.hasMoreElements()) {
            toAddto.addElement(tokenizer.nextToken());
            i++;
        }
        return i;
    }
}
