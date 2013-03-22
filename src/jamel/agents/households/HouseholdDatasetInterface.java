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

import jamel.agents.firms.ProductionType;

/**
 *
 */
public interface HouseholdDatasetInterface {

	/**
	 * 
	 */
	public abstract void clear();

	/**
	 * @return the consumption budget.
	 */
	public abstract long getConsumptionBudget();

	/**
	 * @return the consumption value.
	 */
	public abstract long getConsumptionValue();

	/**
	 * @return the deposits.
	 */
	public abstract long getDeposits();

	/**
	 * @return the dividend.
	 */
	public abstract long getDividend();

	/**
	 * @return the employment status.
	 */
	public abstract Integer getEmploymentStatus();

	/**
	 * @return the forced savings.
	 */
	public abstract long getForcedSavings();

	/**
	 * @return the income.
	 */
	public abstract long getIncome();

	/**
	 * @return the reservation wage.
	 */
	public abstract float getReservationWage();

	/**
	 * @return the unemployment duration.
	 */
	public abstract double getUnemploymentDuration();

	/**
	 * @return the wage.
	 */
	public abstract long getWage();

	/**
	 * @return the volume consumed.
	 */
	public abstract int getConsumptionVolume();

	/**
	 * @return the sector where the household was employed the last time.
	 */
	public ProductionType getSector();

}