/* $RCSfile$
 * $Author$
 * $Date$
 * $Revision$
 *
 * Copyright (C) 2004-2005  The Chemistry Development Kit (CDK) project
 *
 * Contact: cdk-devel@lists.sourceforge.net
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 *  This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110-1301 USA.
 */
package org.openscience.cdk.io.formats;

/**
 * @cdk.module io
 * @cdk.set    io-formats
 */
public class SMILESFormat implements ChemFormatMatcher {

    public SMILESFormat() {}
    
    public String getFormatName() {
        return "SMILES";
    }

    public String getReaderClassName() { 
      return "org.openscience.cdk.io.SMILESReader";
    }
    public String getWriterClassName() { 
      return "org.openscience.cdk.io.SMILESWriter";
    }

    public boolean matches(int lineNumber, String line) {
        return false;
        /* IMPORTANT: this should never match because several formats
             allow comment lines on which often SMILES is stored!!! */
    }

}
