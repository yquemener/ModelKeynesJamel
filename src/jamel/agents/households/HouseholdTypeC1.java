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

import jamel.util.markets.EmploymentContract;

import java.util.Map;

/**
 * Un ménage dont la mobilité sectorielle augmente avec la durée du chômage.
 */
public class HouseholdTypeC1 extends HouseholdTypeC {
	
	/** The speed at which the mobility of the household increases. */
	private static final float mSpeed = 0.03f;
	
	/**
	 * Creates a new household.
	 * @param aName  the name.
	 * @param parameters  a string that contains some parameters.
	 */
	public HouseholdTypeC1(String aName, Map<String, String> parameters) {
		super(aName, parameters);
		this.mobility=1f;
	}

	
	/**
	 * Updates the list of employers.
	 */
	@Override
	protected void updateEmployersList() {
		super.updateEmployersList();
		if (this.mobility<1)
			this.mobility+=mSpeed;
	}
	
	/**
	 * Receives notification of his hiring.
	 * @param contract  the employment contract.
	 */
	@Override
	public void notifyHiring(EmploymentContract contract) {
		super.notifyHiring(contract);
		this.mobility=0;
	}	
	
}
