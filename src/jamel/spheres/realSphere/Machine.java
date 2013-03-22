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

package jamel.spheres.realSphere;

import jamel.agents.roles.Worker;

/**
 * The interface for machines.
 */
public interface Machine {

	/**
	 * Changes the productivity of the machine.
	 * @param ratio the change ratio.
	 */
	public abstract void changeProductivity(float ratio);

	/**
	 * Returns the value of the current production process (= the sum of wages payed).
	 * @return the value of the current production process.
	 */
	public abstract long getProductionProcessValue();

	/**
	 * Returns an integer that represents the productivity of the machine.
	 * @return an integer.
	 */
	public abstract int getProductivity();

	/**
	 * Returns an integer that represents the progress of the production process.
	 * @return an integer.
	 */
	public abstract int getProgress();

	/**
	 * 
	 */
	public abstract void kill();

	/**
	 * Sets the production cycle time.
	 * @param time the production cycle time.
	 */
	public abstract void setProdTime(int time);

	/**
	 * Sets the productivity.
	 * @param newProductivity - the productivity to set.
	 */
	public abstract void setProductivity(int newProductivity);

	/**
	 * Expends the given labor power in the production-process progress.
	 * @param labourPower the labor power to expend.
	 */
	public abstract void work(LaborPower labourPower);

	/**
	 * Increments the production-process by the expenditure of a labor power.<br>
	 * Can be called only once in a period (else an exception is generated).
	 * @param worker the worker.
	 * @param wage the wage payed.
	 */
	public abstract void work(Worker worker, long wage);
	
}