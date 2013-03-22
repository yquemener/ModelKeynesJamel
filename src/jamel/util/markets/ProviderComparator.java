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

package jamel.util.markets;

import jamel.agents.roles.Provider;

import java.util.Comparator;

/** 
 * The provider comparator.<p>
 * To compare providers according to the price they offer on the goods market.
 */
public class ProviderComparator implements Comparator<Provider> {

	public int compare(Provider p1, Provider p2) {
		final GoodsOffer offer1  = p1.getGoodsOffer();
		final GoodsOffer offer2  = p2.getGoodsOffer();
		if ((offer1 == null) & (offer2 == null)) return 0;
		if (offer1 == null) return 1;
		if (offer2 == null) return -1;
		if ((offer1.getVolume() == 0) | (offer2.getVolume() == 0)) new RuntimeException();
		if ((offer1.getPrice() == 0) | (offer2.getPrice() == 0)) new RuntimeException();
		return (new Double(offer1.getPrice()).compareTo(offer2.getPrice()));
	}

}
