/* $RCSfile$
 * $Author$
 * $Date$
 * $Revision$
 *
 * Copyright (C) 2003-2007  The Chemistry Development Kit (CDK) project
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
 * Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110-1301 USA.
 *  */
package org.openscience.cdk.io;

import java.io.InputStream;

import org.junit.Assert;
import org.junit.Test;
import org.openscience.cdk.CDKTestCase;
import org.openscience.cdk.ChemFile;
import org.openscience.cdk.ChemModel;
import org.openscience.cdk.ChemObject;
import org.openscience.cdk.Molecule;
import org.openscience.cdk.Reaction;
import org.openscience.cdk.interfaces.IMolecule;
import org.openscience.cdk.io.formats.CMLFormat;
import org.openscience.cdk.io.formats.CTXFormat;
import org.openscience.cdk.io.formats.GamessFormat;
import org.openscience.cdk.io.formats.Gaussian98Format;
import org.openscience.cdk.io.formats.GhemicalSPMFormat;
import org.openscience.cdk.io.formats.IChemFormat;
import org.openscience.cdk.io.formats.IChemFormatMatcher;
import org.openscience.cdk.io.formats.INChIFormat;
import org.openscience.cdk.io.formats.INChIPlainTextFormat;
import org.openscience.cdk.io.formats.IResourceFormat;
import org.openscience.cdk.io.formats.MDLFormat;
import org.openscience.cdk.io.formats.MDLV2000Format;
import org.openscience.cdk.io.formats.MDLV3000Format;
import org.openscience.cdk.io.formats.Mol2Format;
import org.openscience.cdk.io.formats.PDBFormat;
import org.openscience.cdk.io.formats.PubChemASNFormat;
import org.openscience.cdk.io.formats.PubChemCompoundXMLFormat;
import org.openscience.cdk.io.formats.PubChemSubstanceXMLFormat;
import org.openscience.cdk.io.formats.ShelXFormat;
import org.openscience.cdk.io.formats.VASPFormat;
import org.openscience.cdk.io.formats.XYZFormat;

/**
 * TestCase for the instantiation and functionality of the {@link ReaderFactory}.
 *
 * @cdk.module test-io
 */
public class ReaderFactoryTest extends CDKTestCase {

    private ReaderFactory factory = new ReaderFactory();
    
    @Test public void testCreateReader_IChemFormat() {
    	IChemFormat format = (IChemFormat)XYZFormat.getInstance();
        ISimpleChemObjectReader reader = factory.createReader(format);
        Assert.assertNotNull(reader);
        Assert.assertEquals(format.getFormatName(), reader.getFormat().getFormatName());
    }
    @Test public void testGaussian98() throws Exception {
        expectReader("data/gaussian/g98.out", Gaussian98Format.getInstance());
    }

    @Test public void testGhemical() throws Exception {
        expectReader("data/ghemical/ethene.mm1gp", GhemicalSPMFormat.getInstance());
    }

    @Test public void testINChI() throws Exception {
        expectReader("data/inchi/guanine.inchi.xml", INChIFormat.getInstance());
    }

    @Test public void testINChIPlainText() throws Exception {
        expectReader("data/inchi/guanine.inchi", INChIPlainTextFormat.getInstance());
    }

    @Test public void testVASP() throws Exception {
        expectReader("data/vasp/LiMoS2_optimisation_ISIF3.vasp", VASPFormat.getInstance());
    }

    @Test public void testGamess() throws Exception {
        expectReader("data/gamess/ch3oh_gam.out", GamessFormat.getInstance());
    }

    @Test public void testCML() throws Exception {
        expectReader("data/cml/estron.cml", CMLFormat.getInstance());
    }

    @Test public void testXYZ() throws Exception {
        expectReader("data/xyz/bf3.xyz", XYZFormat.getInstance());
    }

    @Test public void testShelX() throws Exception {
        expectReader("data/shelx/frame_1.res", ShelXFormat.getInstance());
    }
    
    @Test public void testMDLMol() throws Exception {
        expectReader("data/mdl/bug1014344-1.mol", MDLFormat.getInstance());
    }

    @Test public void testMDLMolV2000() throws Exception {
        expectReader("data/mdl/methylbenzol.mol", MDLV2000Format.getInstance());
    }
    
    @Test public void testDetection() throws Exception {
    	expectReader("data/mdl/withcharges.mol", MDLV2000Format.getInstance());
    }

    @Test public void testMDLMolV3000() throws Exception {
        expectReader("data/mdl/molV3000.mol", MDLV3000Format.getInstance());
    }

    @Test public void testPDB() throws Exception {
        expectReader("data/pdb/coffeine.pdb", PDBFormat.getInstance());
    }
    
    @Test public void testMol2() throws Exception {
    	expectReader("data/mol2/fromWebsite.mol2", Mol2Format.getInstance());
    }
    
    @Test public void testCTX() throws Exception {
    	expectReader("data/ctx/methanol_with_descriptors.ctx", CTXFormat.getInstance());
    }
    
    @Test public void testPubChemCompoundASN() throws Exception {
        expectReader("data/asn/pubchem/cid1.asn", PubChemASNFormat.getInstance());
    }

    @Test public void testPubChemSubstanceXML() throws Exception {
        expectReader("data/asn/pubchem/sid577309.xml", PubChemSubstanceXMLFormat.getInstance());
    }
    
    @Test public void testPubChemCompoundXML() throws Exception {
        expectReader("data/asn/pubchem/cid1145.xml", PubChemCompoundXMLFormat.getInstance());
    }
    
    @Test public void testSmiles() throws Exception {
    	InputStream is=this.getClass().getClassLoader().getResourceAsStream("data/smiles/drugs.smi");
        Object reader = factory.createReader( is );
        Assert.assertNull(reader);
    }

    private void expectReader(String filename, IResourceFormat expectedFormat) throws Exception {
        InputStream ins = this.getClass().getClassLoader().getResourceAsStream(filename);
        Assert.assertNotNull("Cannot find file: " + filename, ins);
        if (expectedFormat instanceof IChemFormatMatcher) {
        	factory.registerFormat((IChemFormatMatcher)expectedFormat);
        }
        ISimpleChemObjectReader reader = factory.createReader(ins);
        reader.setReader(ins);
        Assert.assertNotNull(reader);
        Assert.assertEquals(
            ((IChemFormat)expectedFormat).getReaderClassName(),
            reader.getClass().getName()
        );
        // now try reading something from it
        ChemObject[] objects = { 
        		new ChemFile(), new ChemModel(), new Molecule(),
        		new Reaction()
        };
        boolean read = false;
        for (int i=0; (i<objects.length && !read); i++) {
        	if (reader.accepts(objects[i].getClass())) {
        		reader.read(objects[i]);
        		read = true;
        	}
        }
        if (read) {
        	// ok, reseting worked
        } else {
        	Assert.fail("Reading an IChemObject from the Reader did not work properly.");
        }
    }
    
    /**
     * @cdk.bug 2153298
     */
    @Test public void testBug2153298() throws Exception {
        String filename = "data/asn/pubchem/cid1145.xml";
        InputStream ins = this.getClass().getClassLoader().getResourceAsStream(filename);
        Assert.assertNotNull("Cannot find file: " + filename, ins);
        IChemFormatMatcher realFormat = (IChemFormatMatcher)PubChemCompoundXMLFormat.getInstance();
        factory.registerFormat(realFormat);
        // ok, if format ok, try instantiating a reader
        ins = this.getClass().getClassLoader().getResourceAsStream(filename);
        ISimpleChemObjectReader reader = factory.createReader(ins);
        Assert.assertNotNull(reader);
        Assert.assertEquals(
            ((IChemFormat)PubChemCompoundXMLFormat.getInstance()).getReaderClassName(),
            reader.getClass().getName()
        );
        // now try reading something from it
        IMolecule molecule = (IMolecule)reader.read(new Molecule());
        Assert.assertNotNull(molecule);
        Assert.assertNotSame(0, molecule.getAtomCount());
        Assert.assertNotSame(0, molecule.getBondCount());
    }
}
