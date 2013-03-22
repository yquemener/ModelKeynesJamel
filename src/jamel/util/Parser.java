/* =========================================================
 * JAMEL : a Java (tm) Agent-based MacroEconomic Laboratory.
 * =========================================================
 *
 * (C) Copyright 2007-2013, Pascal Seppecher.
 * 
 * Project Info <http://p.seppecher.free.fr/jamel/>. 
 *
 * This file is a part of JAMEL (Java Agent-based MacroEconomic Laboratory).
 * 
 * JAMEL is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * JAMEL is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with JAMEL. If not, see <http://www.gnu.org/licenses/>.
 *
 * [Oracle and Java are registered trademarks of Oracle and/or its affiliates.]
 */

package jamel.util;

import java.util.Random;

/**
 * A stupid object.
 */
public class Parser {
	
	/**
	 * Returns a value for the given parameters.
	 * @param string a string that contains parameters.
	 * @param random the random.
	 * @return a value.
	 */
	public static float parseFloat(String string, Random random) {
		final String[] param = string.split("\\-",2);
		final float target;
		if (param.length==1) {
			target = Float.parseFloat(param[0]);
		}
		else if (param.length==2) {
			final float min = Float.parseFloat(param[0]);
			final float max = Float.parseFloat(param[1]);
			target = min+random.nextFloat()*(max-min);
		}
		else {
			throw new RuntimeException("Incredible error.");
		}		
		return target;
	}

}
