/*  $RCSfile$    
 *  $Author$    
 *  $Date$    
 *  $Revision$
 *
 *  Copyright (C) 2002-2005  The Chemistry Development Kit (CDK) project
 *
 *  Contact: cdk-devel@lists.sourceforge.net
 *
 *  This program is free software; you can redistribute it and/or
 *  modify it under the terms of the GNU Lesser General Public License
 *  as published by the Free Software Foundation; either version 2.1
 *  of the License, or (at your option) any later version.
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
package org.openscience.cdk.applications.swing;

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.io.FileInputStream;
import java.util.EventObject;

import javax.swing.JFrame;
import javax.swing.JPanel;

import org.openscience.cdk.AtomContainer;
import org.openscience.cdk.ChemFile;
import org.openscience.cdk.Molecule;
import org.openscience.cdk.event.CDKChangeListener;
import org.openscience.cdk.geometry.GeometryTools;
import org.openscience.cdk.io.MDLReader;
import org.openscience.cdk.layout.StructureDiagramGenerator;
import org.openscience.cdk.renderer.Renderer2D;
import org.openscience.cdk.renderer.Renderer2DModel;
import org.openscience.cdk.tools.LoggingTool;


/**
 * A Swing-based implementation of Renderer2D for viewing molecules.
 *
 * @cdk.module applications
 * @cdk.require swing
 *
 * @author     steinbeck
 * @cdk.created    2002-05-30
 */
public class MoleculeViewer2D extends JPanel implements CDKChangeListener
{
    public AtomContainer atomContainer;
    public Renderer2DModel r2dm;
    public Renderer2D renderer;
    public String title = "Molecule Viewer";

    private JFrame frame = null;
    private Dimension preferredSize;

    private static LoggingTool logger;


    /**
     *  Constructs a MoleculeViewer with a molecule to display and a Renderer2DModel containing the information on how to display it.
     *
     * @param  r2dm           The rendere settings determining how the molecule is displayed
     */
    public MoleculeViewer2D(AtomContainer atomContainer, Renderer2DModel r2dm)
    {
        logger = new LoggingTool(this);
        this.atomContainer = atomContainer;
        preferredSize = new Dimension(500, 500);
        this.r2dm = r2dm;
        r2dm.setBackgroundDimension(preferredSize);
        r2dm.addCDKChangeListener(this);
        renderer = new Renderer2D(r2dm);
        frame = new JFrame();
    }


    /**
     *  Constructs a MoleculeViewer with a molecule to display
     */
    public MoleculeViewer2D(AtomContainer atomContainer)
    {
        this(atomContainer, new Renderer2DModel());
    }


    /**
     *  Constructs a MoleculeViewer with a molecule to display
     */
    public MoleculeViewer2D()
    {
        this(null, new Renderer2DModel());
    }


    /**
     *  Sets the Frame attribute of the MoleculeViewer2D object
     *
     * @param  frame  The new Frame value
     */
    public void setFrame(JFrame frame)
    {
        this.frame = frame;
    }


    /**
     *  Sets a Renderer2DModel which determins the way a molecule is displayed
     *
     * @param  r2dm  The Renderer2DModel
     */
    public void setRenderer2DModel(Renderer2DModel r2dm)
    {
        this.r2dm = r2dm;
        r2dm.addCDKChangeListener(this);
        renderer = new Renderer2D(r2dm);
    }


    /**
     *  Sets the AtomContainer to be displayed
     *
     * @param  atomContainer  The AtomContainer to be displayed
     */
    public void setAtomContainer(AtomContainer atomContainer)
    {
        this.atomContainer = atomContainer;
    }


    /**
     *  Gets the Frame attribute of the MoleculeViewer2D object
     *
     * @return    The Frame value
     */
    public JFrame getFrame()
    {
        return frame;
    }


    /**
     *  Gets the Renderer2DModel which determins the way a molecule is displayed
     *
     * @return    The Renderer2DModel value
     */
    public Renderer2DModel getRenderer2DModel()
    {
        return renderer.getRenderer2DModel();
    }


    /**
     *  Returns the AtomContainer which is being displayed
     *
     * @return    The AtomContainer which is being displayed
     */
    public AtomContainer getAtomContainer()
    {
        return this.atomContainer;
    }



    /**
     *  Contructs a JFrame into which this JPanel is put and displays the frame with 
     *  the molecule.
     */
    public void display() {
        setPreferredSize(preferredSize);
        frame.setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
        frame.getContentPane().add(this);
        frame.setTitle(title);
        frame.pack();
        frame.setVisible(true);
    }


    public static void display(Molecule molecule, boolean generateCoordinates)
    {
        display(molecule, generateCoordinates, false);
    }

    public static void display(Molecule molecule, boolean generateCoordinates, boolean drawNumbers)
    {	
        StructureDiagramGenerator sdg = new StructureDiagramGenerator();
        MoleculeViewer2D moleculeViewer = new MoleculeViewer2D();
        moleculeViewer.getFrame().setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        Renderer2DModel r2dm = moleculeViewer.getRenderer2DModel();
        r2dm.setDrawNumbers(drawNumbers);

        try
        {
            if (generateCoordinates)
            {
                sdg.setMolecule((Molecule)molecule.clone());
                sdg.generateCoordinates();
                molecule = sdg.getMolecule();
            }
            moleculeViewer.setAtomContainer(molecule);
            moleculeViewer.display();
        }
        catch(Exception exc)
        {
            logger.debug(exc);
            System.out.println("*** Exit due to an unexpected error during coordinate generation ***");
            exc.printStackTrace();
        }
    }

    /**
     *  Paints the molecule onto the JPanel
     *
     * @param  graphics  The graphics used to paint with.
     */
    public void paint(Graphics graphics)
    {
        super.paint(graphics);
        if (atomContainer != null) {
            setBackground(r2dm.getBackColor());
            GeometryTools.translateAllPositive(atomContainer);
            GeometryTools.scaleMolecule(atomContainer, r2dm.getBackgroundDimension(), 0.8);
            GeometryTools.center(atomContainer, r2dm.getBackgroundDimension());
            renderer.paintMolecule(atomContainer, (Graphics2D)graphics);
        }
    }



    /**
     *  Method to notify this CDKChangeListener if something has changed in another object
     *
     * @param  eventObject  The EventObject containing information on the nature and source of the event
     */
    public void stateChanged(EventObject eventObject)
    {
        repaint();
    }


    /**
     *  The main method.
     *
     * @param  args  An MDL molfile
     */

    public static void main(String[] args)
    {
        AtomContainer atomContainer = null;
        try
        {
            FileInputStream fis = new FileInputStream(args[0]);
            MDLReader mr = new MDLReader(fis);
            atomContainer = ((ChemFile) mr.read(new ChemFile())).getChemSequence(0).getChemModel(0).getSetOfMolecules().getMolecule(0);
            fis.close();
        }
        catch (Exception exc)
        {
            logger.debug(exc);
            exc.printStackTrace();
        }

        new MoleculeViewer2D(atomContainer, new Renderer2DModel());
    }
}


