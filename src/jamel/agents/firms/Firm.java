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

package jamel.agents.firms;

import java.util.LinkedList;
import java.util.Map;

import jamel.agents.roles.Employer;
import jamel.agents.roles.Provider;
import jamel.agents.firms.ExternalLabel;
import jamel.util.Blackboard;

/**
 * An interface for the firms.
 */
public interface Firm extends Employer, Provider {

  	/** Returns the current values of the exogeneous params. */
	Blackboard<ExternalLabel> getExternalParams();
    
    /** Sets a new value for an exogeneous param */
    void setParam(ExternalLabel label, Object value);
  
	/**
	 * Buys the raw materials.
	 */
	void buyRawMaterials();

	/**
	 * Closes the firm.<br>
	 * Completes some technical operations at the end of the period.
	 */
	void close();

	/**
	 * Returns the data.
	 * @return the data.
	 */
	FirmDataset getData();

	/**
	 * Kills the firm.
	 */
	void kill();

	/** 
	 * Opens the household for a new period.<br>
	 * Initializes data and executes events.
	 */
	void open();

	/**
	 * Pays the dividend.
	 */
	void payDividend();

	/**
	 * Prepares the production.
	 */
	void prepareProduction();

	/**
	 * Produces.
	 */
	void production();

}
