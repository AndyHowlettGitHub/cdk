/*
 *  $RCSfile$
 *  $Author$
 *  $Date$
 *  $Revision$
 *
 *  Copyright (C) 1997-2005  The JChemPaint project
 *
 *  Contact: jchempaint-devel@lists.sourceforge.net
 *
 *  This program is free software; you can redistribute it and/or
 *  modify it under the terms of the GNU Lesser General Public License
 *  as published by the Free Software Foundation; either version 2.1
 *  of the License, or (at your option) any later version.
 *  All we ask is that proper credit is given for our work, which includes
 *  - but is not limited to - adding the above copyright notice to the beginning
 *  of your source code files, and to any copyright notice that you may distribute
 *  with programs based on this work.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU Lesser General Public License for more details.
 *
 *  You should have received a copy of the GNU Lesser General Public License
 *  along with this program; if not, write to the Free Software
 *  Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 */
package org.openscience.cdk.applications.jchempaint.application;

import java.io.File;
import java.io.FileReader;

import javax.swing.JFrame;
import javax.swing.SwingConstants;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.OptionBuilder;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.apache.commons.cli.PosixParser;
import org.apache.commons.cli.UnrecognizedOptionException;
import org.openscience.cdk.applications.jchempaint.JChemPaintEditorPanel;
import org.openscience.cdk.tools.LoggingTool;

/**
 *  JChemPaint main class.
 *
 * @cdk.module jchempaint.application
 * @author     steinbeck
 * @author     egonw
 * @cdk.created    a long time ago
 */
public class JChemPaint implements SwingConstants
{

	private static JChemPaint jchempaintInstance = null;
	private LoggingTool logger;

	/**
	 *  The main program for the JChemPaint class
	 *
	 *@param  args  The command line arguments
	 */
	public static void main(String[] args)
	{
		try
		{
			String vers = System.getProperty("java.version");
			String requiredJVM = "1.4.0";
			if (vers.compareTo(requiredJVM) < 0)
			{
				System.err.println("WARNING: JChemPaint 2.0 must be run with a Java VM version " +
						requiredJVM + " or higher.");
				System.err.println("Your JVM version: " + vers);
				System.exit(1);
			}

			Options options = new Options();
			options.addOption("h", "help", false, "give this help page");
			options.addOption("v", "version", false, "gives JChemPaints version number");
			options.addOption(
					OptionBuilder.withArgName("property=value").
					hasArg().
					withValueSeparator().
					withDescription("supported options are given below").
					create("D")
					);

			CommandLine line = null;
			try
			{
				CommandLineParser parser = new PosixParser();
				line = parser.parse(options, args);
			} catch (UnrecognizedOptionException exception)
			{
				System.err.println(exception.getMessage());
				System.exit(-1);
			} catch (ParseException exception)
			{
				System.err.println("Unexpected exception: " + exception.toString());
			}

			if (line.hasOption("v"))
			{
				Package self = Package.getPackage("org.openscience.cdk.applications.jchempaint");
				String version = self.getImplementationVersion();

				System.out.println("JChemPaint v." + version + "\n");
				System.exit(0);
			}

			if (line.hasOption("h"))
			{
				Package self = Package.getPackage("org.openscience.cdk.applications.jchempaint");
				String version = self.getImplementationVersion();

				System.out.println("JChemPaint v." + version + "\n");

				HelpFormatter formatter = new HelpFormatter();
				formatter.printHelp("JChemPaint", options);

				// now report on the -D options
				System.out.println();
				System.out.println("The -D options are as follows (defaults in parathesis):");
				System.out.println("  cdk.debugging     [true|false] (false)");
				System.out.println("  cdk.debug.stdout  [true|false] (false)");
				System.out.println("  devel.gui         [true|false] (false)");
				System.out.println("  gui               [stable|experimental] (stable)");
				System.out.println("  plugin.dir        (unset)");
				System.out.println("  user.language     [DE|EN|NL|PL] (EN)");

				System.exit(0);
			}

			// Process command line arguments
			String modelFilename = "";
			FileReader contentToOpen = null;
			args = line.getArgs();
			if (args.length > 0)
			{
				modelFilename = args[0];
				File file = new File(modelFilename);
				if (!file.exists())
				{
					System.err.println("File does not exist: " + modelFilename);
					System.exit(-1);
				}
				// ok, file exists
				contentToOpen = new FileReader(file);
			}

			JChemPaint jcp = JChemPaint.getInstance();

		} catch (Throwable t)
		{
			System.err.println("uncaught exception: " + t);
			t.printStackTrace(System.err);
		}
	}


	/**
	 *  Constructor for the JChemPaint object
	 */
	private JChemPaint()
	{
		logger = new LoggingTool(this);
		logger.dumpSystemProperties();
		JFrame frame = JChemPaintEditorPanel.getEmptyFrameWithModel();
		//for testing the ViewerOnlyPanel
		/*JFrame frame = new JFrame();
		frame.addWindowListener(new JChemPaintPanel.AppCloser());
		frame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		JChemPaintViewerOnlyPanel jpvop = new JChemPaintViewerOnlyPanel(new Dimension(150,250));
		frame.getContentPane().add(jpvop);*/
		frame.show();
		frame.pack();
		logger.debug("End of JCP constructor");
	}


	/**
	 *  Gets the instance attribute of the JChemPaint class
	 *
	 *@return    The instance value
	 */
	public static JChemPaint getInstance()
	{
		if (jchempaintInstance == null)
		{
			jchempaintInstance = new JChemPaint();
		}
		return jchempaintInstance;
	}
}

