/* $RCSfile$
 * $Author$
 * $Date$
 * $Revision$
 * 
 * Copyright (C) 2003-2004  The Chemistry Development Kit (CDK) project
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
package org.openscience.cdk.validate;

import java.util.Enumeration;
import java.util.Hashtable;

import org.openscience.cdk.Atom;
import org.openscience.cdk.AtomContainer;
import org.openscience.cdk.AtomType;
import org.openscience.cdk.Bond;
import org.openscience.cdk.ChemFile;
import org.openscience.cdk.ChemModel;
import org.openscience.cdk.ChemObject;
import org.openscience.cdk.ChemSequence;
import org.openscience.cdk.Crystal;
import org.openscience.cdk.ElectronContainer;
import org.openscience.cdk.Element;
import org.openscience.cdk.Isotope;
import org.openscience.cdk.Molecule;
import org.openscience.cdk.Reaction;
import org.openscience.cdk.SetOfMolecules;
import org.openscience.cdk.SetOfReactions;
import org.openscience.cdk.tools.LoggingTool;

/**
 * Engine that performs the validation by traversing the ChemObject
 * hierarchy. Basic use of the ValidatorEngine is:
 * <pre>
 * ValidatorEngine engine = new ValidatorEngine();
 * engine.addValidator(new BasicValidator());
 * ValidationReport report = engine.validateMolecule(new Molecule());
 * </pre>
 *
 * @author   Egon Willighagen <egonw@sci.kun.nl>
 * @cdk.created  2003-08-22
 */ 
public class ValidatorEngine implements ValidatorInterface {
    
    private Hashtable validators;
    private LoggingTool logger;
    
    public ValidatorEngine() {
        validators = new Hashtable();
        logger = new LoggingTool(this);
    };
    
    public void addValidator(ValidatorInterface validator) {
        logger.info("Registering validator: " + validator.getClass().getName());
        String validatorName = validator.getClass().getName();
        if (validators.containsKey(validatorName)) {
            logger.warn("  already registered.");
        } else {
            validators.put(validatorName, validator);
        }
    }
    
    public void removeValidator(ValidatorInterface validator) {
        logger.info("Removing validator: " + validator.getClass().getName());
        String validatorName = validator.getClass().getName();
        if (!validators.containsKey(validatorName)) {
            logger.warn("  not in list.");
        } else {
            validators.remove(validatorName);
        }
    }
    
    public ValidationReport validateAtom(Atom subject) {
        logger.info("Validating org.openscience.cdk.Atom");
        ValidationReport report = new ValidationReport();
        // apply validators
        Enumeration tests = validators.elements();
        while (tests.hasMoreElements()) {
            ValidatorInterface test = (ValidatorInterface)tests.nextElement();
            report.addReport(test.validateAtom(subject));
        }
        // traverse into super class
        report.addReport(validateAtomType(subject));
        // traverse into hierarchy
        return report;
    };
    public ValidationReport validateAtomContainer(AtomContainer subject) {
        logger.info("Validating org.openscience.cdk.AtomContainer");
        ValidationReport report = new ValidationReport();
        // apply validators
        Enumeration tests = validators.elements();
        while (tests.hasMoreElements()) {
            ValidatorInterface test = (ValidatorInterface)tests.nextElement();
            report.addReport(test.validateAtomContainer(subject));
        }
        // traverse into super class
        report.addReport(validateChemObject(subject));
        // traverse into hierarchy
        Atom[] atoms = subject.getAtoms();
        for (int i=0; i<atoms.length; i++) {
            report.addReport(validateAtom(atoms[i]));
        }
        Bond[] bonds = subject.getBonds();
        for (int i=0; i<bonds.length; i++) {
            report.addReport(validateBond(bonds[i]));
        }
        return report;
    };
    public ValidationReport validateAtomType(AtomType subject) {
        logger.info("Validating org.openscience.cdk.AtomType");
        ValidationReport report = new ValidationReport();
        // apply validators
        Enumeration tests = validators.elements();
        while (tests.hasMoreElements()) {
            ValidatorInterface test = (ValidatorInterface)tests.nextElement();
            report.addReport(test.validateAtomType(subject));
        }
        // traverse into super class
        report.addReport(validateIsotope(subject));
        // traverse into hierarchy
        return report;
    };
    public ValidationReport validateBond(Bond subject) {
        logger.info("Validating org.openscience.cdk.Bond");
        ValidationReport report = new ValidationReport();
        // apply validators
        Enumeration tests = validators.elements();
        while (tests.hasMoreElements()) {
            ValidatorInterface test = (ValidatorInterface)tests.nextElement();
            report.addReport(test.validateBond(subject));
        }
        // traverse into super class
        report.addReport(validateElectronContainer(subject));
        // traverse into hierarchy
        Atom[] atoms = subject.getAtoms();
        for (int i=0; i<atoms.length; i++) {
            report.addReport(validateAtom(atoms[i]));
        }
        return report;
    };
    public ValidationReport validateChemFile(ChemFile subject) {
        logger.info("Validating org.openscience.cdk.ChemFile");
        ValidationReport report = new ValidationReport();
        // apply validators
        Enumeration tests = validators.elements();
        while (tests.hasMoreElements()) {
            ValidatorInterface test = (ValidatorInterface)tests.nextElement();
            report.addReport(test.validateChemFile(subject));
        }
        // traverse into super class
        report.addReport(validateChemObject(subject));
        // traverse into hierarchy
        ChemSequence[] sequences = subject.getChemSequences();
        for (int i=0; i< sequences.length; i++) {
            report.addReport(validateChemSequence(sequences[i]));
        }
        return report;
    };
    public ValidationReport validateChemModel(ChemModel subject) {
        logger.info("Validating org.openscience.cdk.ChemModel");
        ValidationReport report = new ValidationReport();
        // apply validators
        Enumeration tests = validators.elements();
        while (tests.hasMoreElements()) {
            ValidatorInterface test = (ValidatorInterface)tests.nextElement();
            report.addReport(test.validateChemModel(subject));
        }
        // traverse into super class
        report.addReport(validateChemObject(subject));
        // traverse into hierarchy
        Crystal crystal = subject.getCrystal();
        if (crystal != null) {
            report.addReport(validateCrystal(crystal));
        }
        SetOfReactions reactionSet = subject.getSetOfReactions();
        if (reactionSet != null) {
            report.addReport(validateSetOfReactions(reactionSet));
        }
        SetOfMolecules moleculeSet = subject.getSetOfMolecules();
        if (moleculeSet != null) {
            report.addReport(validateSetOfMolecules(moleculeSet));
        }
        return report;
    };
    public ValidationReport validateChemObject(ChemObject subject) {
        logger.info("Validating org.openscience.cdk.ChemObject");
        ValidationReport report = new ValidationReport();
        // apply validators
        Enumeration tests = validators.elements();
        while (tests.hasMoreElements()) {
            ValidatorInterface test = (ValidatorInterface)tests.nextElement();
            report.addReport(test.validateChemObject(subject));
        }
        // traverse into super class
        // traverse into hierarchy
        return report;
    };
    public ValidationReport validateChemSequence(ChemSequence subject) {
        logger.info("Validating org.openscience.cdk.ChemSequence");
        ValidationReport report = new ValidationReport();
        // apply validators
        Enumeration tests = validators.elements();
        while (tests.hasMoreElements()) {
            ValidatorInterface test = (ValidatorInterface)tests.nextElement();
            report.addReport(test.validateChemSequence(subject));
        }
        // traverse into super class
        report.addReport(validateChemObject(subject));
        // traverse into hierarchy
        ChemModel[] models = subject.getChemModels();
        for (int i=0; i<models.length; i++) {
            report.addReport(validateChemModel(models[i]));
        }
        return report;
    };
    public ValidationReport validateCrystal(Crystal subject) {
        logger.info("Validating org.openscience.cdk.Crystal");
        ValidationReport report = new ValidationReport();
        // apply validators
        Enumeration tests = validators.elements();
        while (tests.hasMoreElements()) {
            ValidatorInterface test = (ValidatorInterface)tests.nextElement();
            report.addReport(test.validateCrystal(subject));
        }
        // traverse into super class
        report.addReport(validateAtomContainer(subject));
        // traverse into hierarchy
        return report;
    };
    public ValidationReport validateElectronContainer(ElectronContainer subject) {
        logger.info("Validating org.openscience.cdk.ElectronContainer");
        ValidationReport report = new ValidationReport();
        // apply validators
        Enumeration tests = validators.elements();
        while (tests.hasMoreElements()) {
            ValidatorInterface test = (ValidatorInterface)tests.nextElement();
            report.addReport(test.validateElectronContainer(subject));
        }
        // traverse into super class
        report.addReport(validateChemObject(subject));
        // traverse into hierarchy
        return report;
    };
    public ValidationReport validateElement(Element subject) {
        logger.info("Validating org.openscience.cdk.Element");
        ValidationReport report = new ValidationReport();
        // apply validators
        Enumeration tests = validators.elements();
        while (tests.hasMoreElements()) {
            ValidatorInterface test = (ValidatorInterface)tests.nextElement();
            report.addReport(test.validateElement(subject));
        }
        // traverse into super class
        report.addReport(validateChemObject(subject));
        // traverse into hierarchy
        return report;
    };
    public ValidationReport validateIsotope(Isotope subject) {
        logger.info("Validating org.openscience.cdk.Isotope");
        ValidationReport report = new ValidationReport();
        // apply validators
        Enumeration tests = validators.elements();
        while (tests.hasMoreElements()) {
            ValidatorInterface test = (ValidatorInterface)tests.nextElement();
            report.addReport(test.validateIsotope(subject));
        }
        // traverse into super class
        report.addReport(validateElement(subject));
        // traverse into hierarchy
        return report;
    };
    public ValidationReport validateMolecule(Molecule subject) {
        logger.info("Validating org.openscience.cdk.Molecule");
        ValidationReport report = new ValidationReport();
        // apply validators
        Enumeration tests = validators.elements();
        while (tests.hasMoreElements()) {
            ValidatorInterface test = (ValidatorInterface)tests.nextElement();
            report.addReport(test.validateMolecule(subject));
        }
        // traverse into super class
        report.addReport(validateAtomContainer(subject));
        // traverse into hierarchy
        return report;
    };
    public ValidationReport validateReaction(Reaction subject) {
        logger.info("Validating org.openscience.cdk.Reaction");
        ValidationReport report = new ValidationReport();
        // apply validators
        Enumeration tests = validators.elements();
        while (tests.hasMoreElements()) {
            ValidatorInterface test = (ValidatorInterface)tests.nextElement();
            report.addReport(test.validateReaction(subject));
        }
        // traverse into super class
        report.addReport(validateChemObject(subject));
        // traverse into hierarchy
        Molecule[] reactants = subject.getReactants().getMolecules();
        for (int i=0; i<reactants.length; i++) {
            report.addReport(validateMolecule(reactants[i]));
        }
        Molecule[] products = subject.getProducts().getMolecules();
        for (int i=0; i<products.length; i++) {
            report.addReport(validateMolecule(products[i]));
        }
        return report;
    };
    public ValidationReport validateSetOfMolecules(SetOfMolecules subject) {
        logger.info("Validating org.openscience.cdk.SetOfMolecules");
        ValidationReport report = new ValidationReport();
        // apply validators
        Enumeration tests = validators.elements();
        while (tests.hasMoreElements()) {
            ValidatorInterface test = (ValidatorInterface)tests.nextElement();
            report.addReport(test.validateSetOfMolecules(subject));
        }
        // traverse into super class
        report.addReport(validateChemObject(subject));
        // traverse into hierarchy
        Molecule[] molecules = subject.getMolecules();
        for (int i=0; i<molecules.length; i++) {
            report.addReport(validateMolecule(molecules[i]));
        }
        return report;
    };
    public ValidationReport validateSetOfReactions(SetOfReactions subject) {
        logger.info("Validating org.openscience.cdk.SetOfReactions");
        ValidationReport report = new ValidationReport();
        // apply validators
        Enumeration tests = validators.elements();
        while (tests.hasMoreElements()) {
            ValidatorInterface test = (ValidatorInterface)tests.nextElement();
            report.addReport(test.validateSetOfReactions(subject));
        }
        // traverse into super class
        report.addReport(validateChemObject(subject));
        // traverse into hierarchy
        Reaction[] reactions = subject.getReactions();
        for (int i=0; i<reactions.length; i++) {
            report.addReport(validateReaction(reactions[i]));
        }
        return report;
    };
    
}
