/*
 *  $RCSfile$
 *  $Author$
 *  $Date$
 *  $Revision$
 *
 *  Copyright (C) 2002-2003  The Jmol Development Team
 *  Copyright (C) 2003-2005  The Chemistry Development Kit (CDK) project
 *
 *  Contact: cdk-devel@lists.sourceforge.net
 *
 *  This library is free software; you can redistribute it and/or
 *  modify it under the terms of the GNU Lesser General Public
 *  License as published by the Free Software Foundation; either
 *  version 2.1 of the License, or (at your option) any later version.
 *
 *  This library is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *  Lesser General Public License for more details.
 *
 *  You should have received a copy of the GNU Lesser General Public
 *  License along with this library; if not, write to the Free Software
 *  Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA
 *  02111-1307  USA.
 */
package org.openscience.cdk.io;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StreamTokenizer;
import java.io.StringReader;
import java.util.StringTokenizer;

import javax.vecmath.Point3d;

import org.openscience.cdk.interfaces.Atom;
import org.openscience.cdk.interfaces.AtomContainer;
import org.openscience.cdk.CDKConstants;
import org.openscience.cdk.interfaces.ChemFile;
import org.openscience.cdk.interfaces.ChemModel;
import org.openscience.cdk.interfaces.ChemObject;
import org.openscience.cdk.interfaces.ChemSequence;
import org.openscience.cdk.interfaces.Molecule;
import org.openscience.cdk.interfaces.SetOfMolecules;
import org.openscience.cdk.config.IsotopeFactory;
import org.openscience.cdk.exception.CDKException;
import org.openscience.cdk.io.formats.ChemFormat;
import org.openscience.cdk.io.formats.Gaussian98Format;
import org.openscience.cdk.io.setting.BooleanIOSetting;
import org.openscience.cdk.io.setting.IOSetting;
import org.openscience.cdk.tools.LoggingTool;
import org.openscience.cdk.tools.manipulator.ChemModelManipulator;

/**
 *  A reader for Gaussian98 output. Gaussian 98 is a quantum chemistry program
 *  by Gaussian, Inc. (http://www.gaussian.com/).
 *
 *  <p>Molecular coordinates, energies, and normal coordinates of vibrations are
 *  read. Each set of coordinates is added to the ChemFile in the order they are
 *  found. Energies and vibrations are associated with the previously read set
 *  of coordinates.
 *
 *  <p>This reader was developed from a small set of example output files, and
 *  therefore, is not guaranteed to properly read all Gaussian98 output. If you
 *  have problems, please contact the author of this code, not the developers of
 *  Gaussian98.
 *
 * @author        Bradley A. Smith <yeldar@home.com>
 * @author        Egon Willighagen
 * @author        Christoph Steinbeck
 * @cdk.module    io
 */
public class Gaussian98Reader extends DefaultChemObjectReader
{

	private IsotopeFactory isotopeFactory;
	private BufferedReader input;
	private LoggingTool logger;
	private int atomCount = 0;
	private String lastRoute = "";

	/**
	 *  Customizable setting
	 */
	private BooleanIOSetting readOptimizedStructureOnly;


	/**
	 *  Constructor for the Gaussian98Reader object
	 */
	public Gaussian98Reader()
	{
		this(new StringReader(""));
	}

    public Gaussian98Reader(InputStream input) {
        this(new InputStreamReader(input));
    }
    
    public ChemFormat getFormat() {
        return new Gaussian98Format();
    }

	/**
	 *  Sets the reader attribute of the Gaussian98Reader object
	 *
	 *@param  input             The new reader value
	 *@exception  CDKException  Description of the Exception
	 */
	public void setReader(Reader input) throws CDKException
	{
		if (input instanceof BufferedReader)
		{
			this.input = (BufferedReader) input;
		} else
		{
			this.input = new BufferedReader(input);
		}
	}

    public void setReader(InputStream input) throws CDKException {
        setReader(new InputStreamReader(input));
    }


	/**
	 *  Create an Gaussian98 output reader.
	 *
	 *@param  input  source of Gaussian98 data
	 */
	public Gaussian98Reader(Reader input)
	{
		logger = new LoggingTool(this);
		if (input instanceof BufferedReader)
		{
			this.input = (BufferedReader) input;
		} else
		{
			this.input = new BufferedReader(input);
		}
		try
		{
			isotopeFactory = IsotopeFactory.getInstance();
			logger.info("IsotopeFactory instantiated: " + isotopeFactory);
			logger.info(" #isotopes defined: " + isotopeFactory.getSize());
		} catch (Exception exception)
		{
			// should not happen
			logger.error("Could not instantiate IsotopeFactory");
			logger.debug(exception);
		}
		initIOSettings();
	}


	/**
	 *  Description of the Method
	 *
	 *@param  object  Description of the Parameter
	 *@return         Description of the Return Value
	 */
	public boolean accepts(ChemObject object)
	{
		if (object instanceof ChemFile)
		{
			return true;
		} else
		{
			return false;
		}
	}

	/**
	 *  Description of the Method
	 *
	 *@param  object            Description of the Parameter
	 *@return                   Description of the Return Value
	 *@exception  CDKException  Description of the Exception
	 */
	public ChemObject read(ChemObject object) throws CDKException
	{
		customizeJob();

		if (object instanceof ChemFile)
		{
			ChemFile file = (ChemFile)object;
			try
			{
				file = readChemFile(file);
			} catch (IOException exception)
			{
				throw new CDKException(
						"Error while reading file: " + exception.toString()
						);
			}
			return file;
		} else
		{
			throw new CDKException("Reading of a " + object.getClass().getName() +
					" is not supported.");
		}
	}


	/**
	 *  Description of the Method
	 *
	 *@exception  IOException  Description of the Exception
	 */
	public void close() throws IOException
	{
		input.close();
	}


	/**
	 *  Read the Gaussian98 output.
	 *
	 *@return                   a ChemFile with the coordinates, energies, and
	 *      vibrations.
	 *@exception  IOException   if an I/O error occurs
	 *@exception  CDKException  Description of the Exception
	 */
	private ChemFile readChemFile(ChemFile chemFile) throws CDKException, IOException
	{
		ChemSequence sequence = chemFile.getBuilder().newChemSequence();
		ChemModel model = null;
		String line = input.readLine();
		String levelOfTheory = "";
		String description = "";
		int modelCounter = 0;

		// Find first set of coordinates by skipping all before "Standard orientation"
		while (input.ready() && (line != null))
		{
			if (line.indexOf("Standard orientation:") >= 0)
			{

				// Found a set of coordinates
				model = chemFile.getBuilder().newChemModel();
				readCoordinates(model);
				break;
			}
			line = input.readLine();
		}
		if (model != null)
		{

			// Read all other data
			line = input.readLine().trim();
			while (input.ready() && (line != null))
			{
				if(line.indexOf("#") == 0)
				{
					// Found the route section
					// Memorizing this for the description of the chemmodel
					lastRoute = line;
					modelCounter = 0;
					
				}
				else if (line.indexOf("Standard orientation:") >= 0)
				{

					// Found a set of coordinates
					// Add current frame to file and create a new one.
					if (!readOptimizedStructureOnly.isSet())
					{
						sequence.addChemModel(model);
					} else
					{
						logger.info("Skipping frame, because I was told to do");
					}
					fireFrameRead();
					model = chemFile.getBuilder().newChemModel();
					modelCounter++;
					readCoordinates(model);
				} else if (line.indexOf("SCF Done:") >= 0)
				{

					// Found an energy
					model.setProperty(CDKConstants.REMARK, line.trim());
				} else if (line.indexOf("Harmonic frequencies") >= 0)
				{

					// Found a set of vibrations
					// readFrequencies(frame);
				} else if (line.indexOf("Total atomic charges") >= 0)
				{
					readPartialCharges(model);
				} else if (line.indexOf("Magnetic shielding") >= 0)
				{

					// Found NMR data
					readNMRData(model, line);

				} else if (line.indexOf("GINC") >= 0)
				{

					// Found calculation level of theory
					levelOfTheory = parseLevelOfTheory(line);
					logger.debug("Level of Theory for this model: " + levelOfTheory);
					description = lastRoute + ", model no. " + modelCounter;
					model.setProperty(CDKConstants.DESCRIPTION, description);
				}else
				{
					//logger.debug("Skipping line: " + line);
				}
				line = input.readLine();
			}

			// Add last frame to file
			sequence.addChemModel(model);
			fireFrameRead();
		}
		chemFile.addChemSequence(sequence);

		return chemFile;
	}


	/**
	 *  Reads a set of coordinates into ChemFrame.
	 *
	 *@param  model             Description of the Parameter
	 *@exception  IOException   if an I/O error occurs
	 *@exception  CDKException  Description of the Exception
	 */
	private void readCoordinates(ChemModel model) throws CDKException, IOException
	{
		SetOfMolecules moleculeSet = model.getBuilder().newSetOfMolecules();
		Molecule molecule = model.getBuilder().newMolecule();
		String line = input.readLine();
		line = input.readLine();
		line = input.readLine();
		line = input.readLine();
		while (input.ready())
		{
			line = input.readLine();
			if ((line == null) || (line.indexOf("-----") >= 0))
			{
				break;
			}
			int atomicNumber = 0;
			StringReader sr = new StringReader(line);
			StreamTokenizer token = new StreamTokenizer(sr);
			token.nextToken();

			// ignore first token
			if (token.nextToken() == StreamTokenizer.TT_NUMBER)
			{
				atomicNumber = (int) token.nval;
				if (atomicNumber == 0)
				{

					// Skip dummy atoms. Dummy atoms must be skipped
					// if frequencies are to be read because Gaussian
					// does not report dummy atoms in frequencies, and
					// the number of atoms is used for reading frequencies.
					continue;
				}
			} else
			{
				throw new CDKException("Error while reading coordinates: expected integer.");
			}
			token.nextToken();

			// ignore third token
			double x = 0.0;
			double y = 0.0;
			double z = 0.0;
			if (token.nextToken() == StreamTokenizer.TT_NUMBER)
			{
				x = token.nval;
			} else
			{
				throw new IOException("Error reading x coordinate");
			}
			if (token.nextToken() == StreamTokenizer.TT_NUMBER)
			{
				y = token.nval;
			} else
			{
				throw new IOException("Error reading y coordinate");
			}
			if (token.nextToken() == StreamTokenizer.TT_NUMBER)
			{
				z = token.nval;
			} else
			{
				throw new IOException("Error reading z coordinate");
			}
			Atom atom = model.getBuilder().newAtom(isotopeFactory.getElementSymbol(atomicNumber));
			atom.setPoint3d(new Point3d(x, y, z));
			molecule.addAtom(atom);
		}
		/*
		 *  this is the place where we store the atomcount to
		 *  be used as a counter in the nmr reading
		 */
		atomCount = molecule.getAtomCount();
		moleculeSet.addMolecule(molecule);
		model.setSetOfMolecules(moleculeSet);
	}


	/**
	 *  Reads partial atomic charges and add the to the given ChemModel.
	 *
	 *@param  model             Description of the Parameter
	 *@exception  CDKException  Description of the Exception
	 *@exception  IOException   Description of the Exception
	 */
	private void readPartialCharges(ChemModel model) throws CDKException, IOException
	{
		logger.info("Reading partial atomic charges");
		org.openscience.cdk.interfaces.SetOfMolecules moleculeSet = model.getSetOfMolecules();
		org.openscience.cdk.interfaces.Molecule molecule = moleculeSet.getMolecule(0);
		String line = input.readLine();
		// skip first line after "Total atomic charges"
		while (input.ready())
		{
			line = input.readLine();
			logger.debug("Read charge block line: " + line);
			if ((line == null) || (line.indexOf("Sum of Mulliken charges") >= 0))
			{
				logger.debug("End of charge block found");
				break;
			}
			StringReader sr = new StringReader(line);
			StreamTokenizer tokenizer = new StreamTokenizer(sr);
			if (tokenizer.nextToken() == StreamTokenizer.TT_NUMBER)
			{
				int atomCounter = (int) tokenizer.nval;

				tokenizer.nextToken();
				// ignore the symbol

				double charge = 0.0;
				if (tokenizer.nextToken() == StreamTokenizer.TT_NUMBER)
				{
					charge = (double) tokenizer.nval;
					logger.debug("Found charge for atom " + atomCounter +
							": " + charge);
				} else
				{
					throw new CDKException("Error while reading charge: expected double.");
				}
				org.openscience.cdk.interfaces.Atom atom = molecule.getAtomAt(atomCounter - 1);
				atom.setCharge(charge);
			}
		}
	}


	/**
	 *  Reads a set of vibrations into ChemFrame.
	 *
	 *@param  model            Description of the Parameter
	 *@exception  IOException  if an I/O error occurs
	 */
	private void readFrequencies(ChemModel model) throws IOException
	{
		/*
		 *  FIXME: this is yet to be ported
		 *  String line;
		 *  line = input.readLine();
		 *  line = input.readLine();
		 *  line = input.readLine();
		 *  line = input.readLine();
		 *  line = input.readLine();
		 *  while ((line != null) && line.startsWith(" Frequencies --")) {
		 *  Vector currentVibs = new Vector();
		 *  StringReader vibValRead = new StringReader(line.substring(15));
		 *  StreamTokenizer token = new StreamTokenizer(vibValRead);
		 *  while (token.nextToken() != StreamTokenizer.TT_EOF) {
		 *  Vibration vib = new Vibration(Double.toString(token.nval));
		 *  currentVibs.addElement(vib);
		 *  }
		 *  line = input.readLine();
		 *  line = input.readLine();
		 *  line = input.readLine();
		 *  line = input.readLine();
		 *  line = input.readLine();
		 *  line = input.readLine();
		 *  for (int i = 0; i < frame.getAtomCount(); ++i) {
		 *  line = input.readLine();
		 *  StringReader vectorRead = new StringReader(line);
		 *  token = new StreamTokenizer(vectorRead);
		 *  token.nextToken();
		 *  / ignore first token
		 *  token.nextToken();
		 *  / ignore second token
		 *  for (int j = 0; j < currentVibs.size(); ++j) {
		 *  double[] v = new double[3];
		 *  if (token.nextToken() == StreamTokenizer.TT_NUMBER) {
		 *  v[0] = token.nval;
		 *  } else {
		 *  throw new IOException("Error reading frequency");
		 *  }
		 *  if (token.nextToken() == StreamTokenizer.TT_NUMBER) {
		 *  v[1] = token.nval;
		 *  } else {
		 *  throw new IOException("Error reading frequency");
		 *  }
		 *  if (token.nextToken() == StreamTokenizer.TT_NUMBER) {
		 *  v[2] = token.nval;
		 *  } else {
		 *  throw new IOException("Error reading frequency");
		 *  }
		 *  ((Vibration) currentVibs.elementAt(j)).addAtomVector(v);
		 *  }
		 *  }
		 *  for (int i = 0; i < currentVibs.size(); ++i) {
		 *  frame.addVibration((Vibration) currentVibs.elementAt(i));
		 *  }
		 *  line = input.readLine();
		 *  line = input.readLine();
		 *  line = input.readLine();
		 *  }
		 */
	}


	/**
	 *  Reads NMR nuclear shieldings.
	 *
	 *@param  model             Description of the Parameter
	 *@param  labelLine         Description of the Parameter
	 *@exception  CDKException  Description of the Exception
	 */
	private void readNMRData(ChemModel model, String labelLine) throws CDKException
	{
		AtomContainer ac = ChemModelManipulator.getAllInOneContainer(model);
		// Determine label for properties
		String label;
		if (labelLine.indexOf("Diamagnetic") >= 0)
		{
			label = "Diamagnetic Magnetic shielding (Isotropic)";
		} else if (labelLine.indexOf("Paramagnetic") >= 0)
		{
			label = "Paramagnetic Magnetic shielding (Isotropic)";
		} else
		{
			label = "Magnetic shielding (Isotropic)";
		}
		int atomIndex = 0;
		for (int i = 0; i < atomCount; ++i)
		{
			try
			{
				String line = input.readLine().trim();
				while (line.indexOf("Isotropic") < 0)
				{
					if (line == null)
					{
						return;
					}
					line = input.readLine().trim();
				}
				StringTokenizer st1 = new StringTokenizer(line);

				// Find Isotropic label
				while (st1.hasMoreTokens())
				{
					if (st1.nextToken().equals("Isotropic"))
					{
						break;
					}
				}

				// Find Isotropic value
				while (st1.hasMoreTokens())
				{
					if (st1.nextToken().equals("="))
					{
						break;
					}
				}
				double shielding = Double.valueOf(st1.nextToken()).doubleValue();
				//System.out.println("Shielding: " + shielding);
				ac.getAtomAt(atomIndex).setProperty(CDKConstants.ISOTROPIC_SHIELDING, new Double(shielding));
				++atomIndex;
			} catch (Exception exc)
			{
				logger.debug("failed to read line from gaussian98 file where I expected one.");
			}
		}
	}


	/**
	 *  Select the theory and basis set from the first archive line.
	 *
	 *@param  line  Description of the Parameter
	 *@return       Description of the Return Value
	 */
	private String parseLevelOfTheory(String line)
	{
		StringBuffer summary  = new StringBuffer();
		summary.append(line);
		try
		{
				
			do
			{
				line = input.readLine().trim();
				summary.append(line);
			}while(!(line.indexOf("@") >= 0));
		}
		catch(Exception exc)
		{
			logger.debug("syntax problem while parsing summary of g98 section: ");
			logger.debug(exc);
		}
		logger.debug("parseLoT(): " + summary.toString());
		StringTokenizer st1 = new StringTokenizer(summary.toString(), "\\");

		// Must contain at least 6 tokens
		if (st1.countTokens() < 6)
		{
			return null;
		}

		// Skip first four tokens
		for (int i = 0; i < 4; ++i)
		{
			st1.nextToken();
		}
		
		return st1.nextToken() + "/" + st1.nextToken();
	}


	/**
	 *  Description of the Method
	 */
	private void initIOSettings()
	{
		readOptimizedStructureOnly = new BooleanIOSetting("ReadOptimizedStructureOnly", IOSetting.LOW,
				"Should I only read the optimized structure from a geometry optimization?",
				"false");
	}


	/**
	 *  Description of the Method
	 */
	private void customizeJob()
	{
		fireIOSettingQuestion(readOptimizedStructureOnly);
	}


	/**
	 *  Gets the iOSettings attribute of the Gaussian98Reader object
	 *
	 *@return    The iOSettings value
	 */
	public IOSetting[] getIOSettings()
	{
		IOSetting[] settings = new IOSetting[1];
		settings[0] = readOptimizedStructureOnly;
		return settings;
	}

}

