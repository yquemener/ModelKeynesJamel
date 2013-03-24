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
import jamel.agents.roles.Employer;

import java.util.TreeSet;

/**
 * Un ménage qui résiste à changer de secteur pour trouver un emploi. 
 */
public class HouseholdTypeC extends HouseholdTypeB {
	
	/** The mobility on the labor market. 
	 * O : the household is not mobile.
	 * 1 : the household is perfectly mobile.
	 */
	protected float mobility;
	
	/**
	 * Creates a new household.
	 * @param aName  the name.
	 * @param parameters  a string that contains some parameters.
	 */
	public HouseholdTypeC(String aName, String parameters) {
		super(aName, parameters);
		this.mobility = 0.5f; // TODO should be a parameter.
	}

	
	/**
	 * Updates the list of employers, mixing
	 */
	@Override
	protected void updateEmployersList() {
		final int mobSize = (int) (maxEmployers*mobility);
		final TreeSet<Employer> newEmployers = new TreeSet<Employer>(EMPLOYER_COMPARATOR);
		newEmployers.addAll(Circuit.getEmployerCollection(mobSize,null));
		newEmployers.addAll(Circuit.getEmployerCollection(maxEmployers-mobSize,this.sector));
		this.employers.clear();
		this.employers.addAll(newEmployers);
	}
	
}
