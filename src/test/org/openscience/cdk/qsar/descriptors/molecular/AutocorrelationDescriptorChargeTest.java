package org.openscience.cdk.qsar.descriptors.molecular;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.openscience.cdk.ChemObject;
import org.openscience.cdk.Molecule;
import org.openscience.cdk.interfaces.IMolecule;
import org.openscience.cdk.io.MDLV2000Reader;
import org.openscience.cdk.qsar.DescriptorValue;

import java.io.InputStream;

/**
 * @cdk.module test-qsarmolecular
 */
public class AutocorrelationDescriptorChargeTest extends MolecularDescriptorTest {

	public AutocorrelationDescriptorChargeTest() {
		super();
	}


	@Before
    public void setUp() throws Exception {
		setDescriptor(AutocorrelationDescriptorCharge.class);
	}

    @Test
    public void test1() throws Exception {
		String filename = "data/mdl/clorobenzene.mol";
		InputStream ins = this.getClass().getClassLoader().getResourceAsStream(
				filename);
		MDLV2000Reader reader = new MDLV2000Reader(ins);
		IMolecule container = (Molecule) reader.read((ChemObject) new Molecule());
		DescriptorValue count = descriptor.calculate(container);
		System.out.println(count.getValue());
		Assert.fail("Not validated yet");
	}

}
