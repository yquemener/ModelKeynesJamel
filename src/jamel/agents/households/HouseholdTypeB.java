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

package jamel.agents.households;

import jamel.Circuit;
import jamel.agents.roles.Provider;

import java.util.TreeSet;

/**
 * An implementation of the household interface.
 * <p>
 * A household has two main functions : labor and consumption.
 */
class HouseholdTypeB extends BasicHousehold {

	/**
	 * Creates a new household.
	 * @param aName - the name.
	 * @param parameters - a string that contains some parameters.
	 */
	public HouseholdTypeB(String aName, String parameters) {
		super(aName,parameters);
	}

	/**
	 * Updates the list of the providers.
	 */
	@Override
	protected void updateProvidersList() {
		final TreeSet<Provider> newSet = new TreeSet<Provider>(PROVIDER_COMPARATOR);
		for (Provider provider: this.providers) {
			if ((!provider.isBankrupt())&&(provider.getGoodsOffer()!=null))
				newSet.add(provider);
		}
		for (int count = 0; count<maxSize; count++){
			final Provider provider = Circuit.getRandomProviderOfFinalGoods();
			if ((provider!=null)&&(provider.getGoodsOffer()!=null)) {
				newSet.add(provider);
			}
		}
		this.providers.clear();
		this.providers.addAll(newSet);
		while (this.providers.size()>maxSize)
			this.providers.removeLast();
	}
	
}