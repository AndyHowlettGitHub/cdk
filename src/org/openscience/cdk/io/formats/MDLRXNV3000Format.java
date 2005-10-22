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
public class MDLRXNV3000Format implements ChemFormatMatcher {

    public MDLRXNV3000Format() {}
    
    public String getFormatName() {
        return "MDL RXN V3000";
    }

    public String getReaderClassName() { 
      return "org.openscience.cdk.io.MDLRXNV3000Reader";
    }
    public String getWriterClassName() { return null; }

    public boolean matches(int lineNumber, String line) {
        if (lineNumber == 4 && 
            (line.indexOf("v3000") >= 0 ||
             line.indexOf("V3000") >= 0)) {
            return true;
        }
        return false;
    }

}
