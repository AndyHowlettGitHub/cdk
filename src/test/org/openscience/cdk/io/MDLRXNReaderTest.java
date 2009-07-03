/* $RCSfile$
 * $Author$
 * $Date$
 * $Revision$
 *
 * Copyright (C) 2003-2007  The Chemistry Development Kit (CDK) project
 * 
 * Contact: cdk-devel@slists.sourceforge.net
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
 * Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110-1301 USA.
 *
 */
package org.openscience.cdk.io;

import java.io.InputStream;

import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.openscience.cdk.ChemFile;
import org.openscience.cdk.ChemModel;
import org.openscience.cdk.Molecule;
import org.openscience.cdk.MoleculeSet;
import org.openscience.cdk.Reaction;
import org.openscience.cdk.ReactionSet;
import org.openscience.cdk.interfaces.IChemFile;
import org.openscience.cdk.interfaces.IChemModel;
import org.openscience.cdk.interfaces.IMoleculeSet;
import org.openscience.cdk.interfaces.IReaction;
import org.openscience.cdk.interfaces.IReactionSet;
import org.openscience.cdk.nonotify.NNChemFile;
import org.openscience.cdk.nonotify.NNChemModel;
import org.openscience.cdk.nonotify.NNReaction;
import org.openscience.cdk.nonotify.NNReactionSet;
import org.openscience.cdk.tools.ILoggingTool;
import org.openscience.cdk.tools.LoggingToolFactory;

/**
 * TestCase for the reading MDL RXN files using one test file.
 *
 * @cdk.module test-io
 *
 * @see org.openscience.cdk.io.MDLRXNReader
 */
public class MDLRXNReaderTest extends SimpleChemObjectReaderTest {

    private static ILoggingTool logger =
        LoggingToolFactory.createLoggingTool(MDLRXNReaderTest.class);

    @BeforeClass public static void setup() {
        setSimpleChemObjectReader(new MDLRXNReader(), "data/mdl/reaction-1.rxn");
    }

    @Test public void testAccepts() {
    	MDLRXNReader reader = new MDLRXNReader();
    	Assert.assertTrue(reader.accepts(ChemFile.class));
    	Assert.assertTrue(reader.accepts(ChemModel.class));
    	Assert.assertTrue(reader.accepts(Reaction.class));
		Assert.assertTrue(reader.accepts(ReactionSet.class));
		Assert.assertFalse(reader.accepts(MoleculeSet.class));
		Assert.assertFalse(reader.accepts(Molecule.class));
    }

    @Test public void testReadReactions1() throws Exception {
        String filename1 = "data/mdl/reaction-1.rxn";
        logger.info("Testing: " + filename1);
        InputStream ins1 = this.getClass().getClassLoader().getResourceAsStream(filename1);
        MDLRXNReader reader1 = new MDLRXNReader(ins1);
        IReaction reaction1 = new NNReaction();
        reaction1 = (IReaction)reader1.read(reaction1);
        reader1.close();

        Assert.assertNotNull(reaction1);
        Assert.assertEquals(2, reaction1.getReactantCount());
        Assert.assertEquals(1, reaction1.getProductCount());

        IMoleculeSet educts = reaction1.getReactants();
        // Check Atom symbols of first educt
        String[] atomSymbolsOfEduct1 = { "C", "C", "O", "Cl"};
        for (int i = 0; i < educts.getMolecule(0).getAtomCount(); i++) {
        	Assert.assertEquals(atomSymbolsOfEduct1[i], educts.getMolecule(0).getAtom(i).getSymbol());
        }

        // Check Atom symbols of second educt
        for (int i = 0; i < educts.getMolecule(1).getAtomCount(); i++) {
        	Assert.assertEquals("C", educts.getMolecule(1).getAtom(i).getSymbol());
        }

        // Check Atom symbols of first product
        IMoleculeSet products = reaction1.getProducts();
        String[] atomSymbolsOfProduct1 = { 
        		"C",
        		"C",
        		"C",
        		"C",
        		"C",
        		"C",
        		"C",
        		"O",
        		"C"
        };
        for (int i = 0; i < products.getMolecule(0).getAtomCount(); i++) {
        	Assert.assertEquals(atomSymbolsOfProduct1[i], products.getMolecule(0).getAtom(i).getSymbol());
        }
    }

    @Test public void testReadReactions2() throws Exception {
		String filename2 = "data/mdl/reaction-2.rxn";
		logger.info("Testing: " + filename2);
		InputStream ins2 = this.getClass().getClassLoader().getResourceAsStream(filename2);
		MDLRXNReader reader2 = new MDLRXNReader(ins2);
		IReaction reaction2 = new NNReaction();
		reaction2 = (IReaction)reader2.read(reaction2);
		reader2.close();

		Assert.assertNotNull(reaction2);
		Assert.assertEquals(2, reaction2.getReactantCount());
		Assert.assertEquals(2, reaction2.getProductCount());
    }
    
    @Test public void testReadMapping() throws Exception {
		String filename2 = "data/mdl/mappingTest.rxn";
		logger.info("Testing: " + filename2);
		InputStream ins2 = this.getClass().getClassLoader().getResourceAsStream(filename2);
		MDLRXNReader reader2 = new MDLRXNReader(ins2);
		IReaction reaction2 = new NNReaction();
		reaction2 = (IReaction)reader2.read(reaction2);
		reader2.close();

		Assert.assertNotNull(reaction2);
		java.util.Iterator maps = reaction2.mappings().iterator();
		maps.next();
		Assert.assertTrue(maps.hasNext());
    }
    /**
     * 
     */
    @Test public void testRDFChemFile() throws Exception {
        String filename = "data/mdl/qsar-reaction-test.rdf";
        logger.info("Testing: " + filename);
        InputStream ins = this.getClass().getClassLoader().getResourceAsStream(filename);
        MDLRXNReader reader = new MDLRXNReader(ins);
        IChemFile chemFile = (IChemFile)reader.read(new NNChemFile());
        Assert.assertNotNull(chemFile);
        
        
		Assert.assertEquals(2, chemFile.getChemSequence(0).getChemModel(0).getReactionSet().getReactionCount());
        Assert.assertEquals(2, chemFile.getChemSequence(0).getChemModel(0).getReactionSet().getReaction(0).getReactantCount());
        Assert.assertEquals(3, chemFile.getChemSequence(0).getChemModel(0).getReactionSet().getReaction(0).getReactants().getMolecule(0).getAtomCount());
        Assert.assertEquals(2, chemFile.getChemSequence(0).getChemModel(0).getReactionSet().getReaction(0).getReactants().getMolecule(1).getAtomCount());
        Assert.assertEquals(2, chemFile.getChemSequence(0).getChemModel(0).getReactionSet().getReaction(0).getProductCount());
        Assert.assertEquals(2, chemFile.getChemSequence(0).getChemModel(0).getReactionSet().getReaction(0).getProducts().getMolecule(0).getAtomCount());
        Assert.assertEquals(2, chemFile.getChemSequence(0).getChemModel(0).getReactionSet().getReaction(0).getProducts().getMolecule(1).getAtomCount());
        

        Assert.assertEquals(1, chemFile.getChemSequence(0).getChemModel(0).getReactionSet().getReaction(1).getReactantCount());
        Assert.assertEquals(3, chemFile.getChemSequence(0).getChemModel(0).getReactionSet().getReaction(1).getReactants().getMolecule(0).getAtomCount());
        Assert.assertEquals(1, chemFile.getChemSequence(0).getChemModel(0).getReactionSet().getReaction(1).getProductCount());
        Assert.assertEquals(2, chemFile.getChemSequence(0).getChemModel(0).getReactionSet().getReaction(1).getProducts().getMolecule(0).getAtomCount());
        
    }

    /**
     * 
     */
    @Test public void testRDFModel() throws Exception {
        String filename = "data/mdl/qsar-reaction-test.rdf";
        logger.info("Testing: " + filename);
        InputStream ins = this.getClass().getClassLoader().getResourceAsStream(filename);
        MDLRXNReader reader = new MDLRXNReader(ins);
        IChemModel chemModel = (IChemModel)reader.read(new NNChemModel());
        Assert.assertNotNull(chemModel);
        
        
		Assert.assertEquals(2, chemModel.getReactionSet().getReactionCount());
        Assert.assertEquals(2, chemModel.getReactionSet().getReaction(0).getReactantCount());
        Assert.assertEquals(3, chemModel.getReactionSet().getReaction(0).getReactants().getMolecule(0).getAtomCount());
        Assert.assertEquals(2, chemModel.getReactionSet().getReaction(0).getReactants().getMolecule(1).getAtomCount());
        Assert.assertEquals(2, chemModel.getReactionSet().getReaction(0).getProductCount());
        Assert.assertEquals(2, chemModel.getReactionSet().getReaction(0).getProducts().getMolecule(0).getAtomCount());
        Assert.assertEquals(2, chemModel.getReactionSet().getReaction(0).getProducts().getMolecule(1).getAtomCount());
        

        Assert.assertEquals(1, chemModel.getReactionSet().getReaction(1).getReactantCount());
        Assert.assertEquals(3, chemModel.getReactionSet().getReaction(1).getReactants().getMolecule(0).getAtomCount());
        Assert.assertEquals(1, chemModel.getReactionSet().getReaction(1).getProductCount());
        Assert.assertEquals(2, chemModel.getReactionSet().getReaction(1).getProducts().getMolecule(0).getAtomCount());
        
    }
    /**
     * 
     */
    @Test public void testRDFReactioniSet() throws Exception {
        String filename = "data/mdl/qsar-reaction-test.rdf";
        logger.info("Testing: " + filename);
        InputStream ins = this.getClass().getClassLoader().getResourceAsStream(filename);
        MDLRXNReader reader = new MDLRXNReader(ins);
        IReactionSet reactionSet = (IReactionSet)reader.read(new NNReactionSet());
        Assert.assertNotNull(reactionSet);
        
        
		Assert.assertEquals(2, reactionSet.getReactionCount());
        Assert.assertEquals(2, reactionSet.getReaction(0).getReactantCount());
        Assert.assertEquals(3, reactionSet.getReaction(0).getReactants().getMolecule(0).getAtomCount());
        Assert.assertEquals(2, reactionSet.getReaction(0).getReactants().getMolecule(1).getAtomCount());
        Assert.assertEquals(2, reactionSet.getReaction(0).getProductCount());
        Assert.assertEquals(2, reactionSet.getReaction(0).getProducts().getMolecule(0).getAtomCount());
        Assert.assertEquals(2, reactionSet.getReaction(0).getProducts().getMolecule(1).getAtomCount());
        

        Assert.assertEquals(1, reactionSet.getReaction(1).getReactantCount());
        Assert.assertEquals(3, reactionSet.getReaction(1).getReactants().getMolecule(0).getAtomCount());
        Assert.assertEquals(1, reactionSet.getReaction(1).getProductCount());
        Assert.assertEquals(2, reactionSet.getReaction(1).getProducts().getMolecule(0).getAtomCount());
        
    }
}
