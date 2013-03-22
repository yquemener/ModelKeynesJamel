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

package jamel.spheres.monetarySphere;

import jamel.agents.roles.AccountHolder;

/**
 * The base interface for accounts.
 */
public interface Account {

	/**
	 * Deposits a check on this account.
	 * @param check the check to deposit.
	 */
	void deposit(Check check);

	/**
	 * Returns the available balance of the account.
	 * @return the available balance.
	 */
	long getAmount();

	/**
	 * Returns the amount of debt for this account.
	 * @return the outstanding debt.
	 */
	long getDebt();

	/**
	 * Returns the quality of the debt associated to this account.
	 * @return the quality.
	 */
	Quality getDebtorStatus();

	/**
	 * Returns the holder of the account.
	 * @return the holder of the account
	 */
	AccountHolder getHolder();

	/**
	 * Lends the given amount.
	 * @param money - the amount of loan.
	 */
	void lend(long money);

	/**
	 * Returns a new check from this account.
	 * @param amount the amount of the check
	 * @param payee the payee
	 * @return the new check
	 */
	Check newCheck(long amount, AccountHolder payee);

}
