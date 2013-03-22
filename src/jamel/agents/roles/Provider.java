/* =========================================================
 * JAMEL : a Java (tm) Agent-based MacroEconomic Laboratory.
 * =========================================================
 *
 * (C) Copyright 2007-2012, Pascal Seppecher.
 * 
 * Project Info <http://p.seppecher.free.fr/jamel/javadoc/index.html>. 
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
 * [Java is a trademark or registered trademark of Sun Microsystems, Inc.
 * in the United States and other countries.]
 */

package jamel.agents.roles;

import jamel.agents.firms.ProductionType;
import jamel.spheres.monetarySphere.Check;
import jamel.spheres.realSphere.Goods;
import jamel.util.markets.GoodsOffer;

/**
 * A provider is an agent who can post an offer on a goods market, sell goods 
 * in response to customers demand, and receive check in payment.
 */
public interface Provider extends Offerer {

	/**
	 * Returns the offer of the provider on the goods market.
	 * @return the offer.
	 */
	GoodsOffer getGoodsOffer();

	/**
	 * Returns the name of the agent.
	 * @return a string that represents the name.
	 */
	String getName();

	/**
	 * Returns the type of production of this provider.
	 * @return a type of production.
	 */
	ProductionType getType();

	/**
	 * Returns a flag that indicates if the provider is bankrupt or not.
	 * @return <code>true</code> if the provider is bankrupt.
	 */
	boolean isBankrupt();

	/**
	 * Sells some commodities to an other agent.
	 * @param offer - the offer to which the buyer responds.
	 * @param volume - the volume of goods the buyer wants to buy.
	 * @param cheque - the payment.
	 * @return the goods sold.
	 */
	Goods sell(
			GoodsOffer offer, 
			int volume,
			Check cheque
	);

}
