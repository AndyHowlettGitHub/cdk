/*
 * MathTools.java
 *
 * $RCSfile$    $Author$    $Date$    $Revision$
 *
 * Copyright (C) 1997-2002  The Chemistry Development Kit (CDK) project
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
 */


package org.openscience.cdk.tools;

public class MathTools
{
	/**
	* Analog of Math.max that returns the largest int value in an array of ints
	**/
	public static int max(int[] values)
	{
		int max = values[0];
		for (int f = 0; f < values.length; f++)
		{
			if (values[f] > max)
			{
				max = values[f];
			}
		}
		return max;
	}

	/**
	* Analog of Math.max that returns the largest int value in an array of ints
	**/
	public static int min(int[] values)
	{
		int min = values[0];
		for (int f = 0; f < values.length; f++)
		{
			if (values[f] < min)
			{
				min = values[f];
			}
		}
		return min;
	}
}
