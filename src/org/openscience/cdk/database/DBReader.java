/* $RCSfile$    
 * $Author$    
 * $Date$    
 * $Revision$
 * 
 * Copyright (C) 1997-2005  The Chemistry Development Kit (CDK) project
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
package org.openscience.cdk.database;

import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;

import org.openscience.cdk.CDKConstants;
import org.openscience.cdk.ChemFile;
import org.openscience.cdk.ChemModel;
import org.openscience.cdk.ChemObject;
import org.openscience.cdk.ChemSequence;
import org.openscience.cdk.Molecule;
import org.openscience.cdk.SetOfMolecules;
import org.openscience.cdk.exception.CDKException;
import org.openscience.cdk.io.CMLReader;
import org.openscience.cdk.io.formats.ChemFormat;

/**
 * Reader that can read from a relational database that can be
 * accessed through JDBC.
 *
 * @cdk.keyword database
 * @cdk.keyword JDBC
 *
 * @cdk.module orphaned
 */
public class DBReader {

	Connection con;
	String query = null;
	
	public DBReader(Connection con) {
		this.con = con;
	}

    public ChemFormat getFormat() {
        return new ChemFormat() {
            public String getFormatName() {
                return "JDBC database";
            }
            public String getReaderClassName() { return null; };
            public String getWriterClassName() { return null; };
        };
    }
    
    public void setReader(Reader input) throws CDKException {
        throw new CDKException("This Reader does not read from a Reader but from a JDBC database");
    }

    public ChemObject read(ChemObject object) throws CDKException {
	    if (object instanceof Molecule) {
	        return (ChemObject)readMolecule();
	    } else {
	        throw new CDKException("ChemObject is not supported Molecule.");
	    }
    }
	
	private org.openscience.cdk.interfaces.ChemObject readMolecule() {
		org.openscience.cdk.interfaces.Molecule mol = null;
		CMLReader cmlr;
		StringReader reader;
		Statement st;
		ResultSet rs;
		try {
			con.setAutoCommit(false);
			st = con.createStatement();
			System.out.println(query);
			rs = st.executeQuery(query);
			while (rs.next()) {
				byte[] bytes = rs.getBytes(14);
                String CMLString = new String(bytes);
				reader = new StringReader(CMLString);
				cmlr = new CMLReader(reader);
				mol = getMolecule((ChemFile)cmlr.read(new ChemFile()));
				
				mol.setProperty(CDKConstants.AUTONOMNAME, rs.getString(1));
				mol.setProperty(CDKConstants.CASRN, rs.getString(2));
				mol.setProperty(CDKConstants.BEILSTEINRN, rs.getString(3));
			}
			rs.close();
			st.close();
			con.commit();
			con.setAutoCommit(true);
		} catch (Exception exc) {
	    	exc.printStackTrace();
	    }
		return mol;
    }

	private org.openscience.cdk.interfaces.Molecule getMolecule(ChemFile cf) {		
		org.openscience.cdk.interfaces.ChemSequence cs = cf.getChemSequence(0);
		org.openscience.cdk.interfaces.ChemModel cm = cs.getChemModel(0);
		org.openscience.cdk.interfaces.SetOfMolecules som = cm.getSetOfMolecules();
		return som.getMolecule(0);
	}

	public void setQuery(String query) {
		this.query = query;
	}
	
    public void close() throws IOException {
    }
}
