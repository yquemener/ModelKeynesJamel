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

package jamel.agents.firms.managers;

import java.util.HashMap;
import jamel.JamelObject;
import jamel.agents.firms.BasicFirm;
import jamel.agents.firms.InternalLabel;
import jamel.agents.firms.ExternalLabel;


/**
 * A basic pricing manager.
 */
public class PricingManager extends JamelObject {

	/** The black board of the firm, used for internal communication between
         *  managers.
         */
	final private HashMap<InternalLabel,Object> blackboard;

	/** The external repository of parameters. */
	final private HashMap<ExternalLabel,Object> externalParams;


	/** The current unit price.*/
	protected double currentPrice;

	/**
	 * Creates a new pricing manager.
	 * @param blackboard  the blackboard.
	 */
	public PricingManager(BasicFirm parent) {
		this.blackboard = parent.blackboard;
                this.externalParams = parent.externalParams;
	}

	/**
	 * Updates the unit price.
	 */
	public void updatePrice() {
		final Float priceFlexibility = (Float)this.externalParams.get(ExternalLabel.PRICE_FLEXIBILITY);
		final Float inventoryRatio = (Float)this.blackboard.get(InternalLabel.INVENTORY_LEVEL_RATIO);
		final Double unitCost = (Double)this.blackboard.get(InternalLabel.UNIT_COST);
		if (this.currentPrice==0) {
			this.currentPrice = (1.+getRandom().nextFloat()/2.)*unitCost;
			if ( Double.isNaN(currentPrice) )
				throw new RuntimeException("This price is NaN.") ;
		}
		else {
			final float alpha1 = getRandom().nextFloat();
			final float alpha2 = getRandom().nextFloat();
			if (inventoryRatio<1-alpha1*alpha2) {
				this.currentPrice = this.currentPrice*(1f+alpha1*priceFlexibility);
			}
			else if (inventoryRatio>1+alpha1*alpha2) {
				this.currentPrice = this.currentPrice*(1f-alpha1*priceFlexibility);
				if (this.currentPrice<1) this.currentPrice = 1;
			}
		}
		this.blackboard.put(InternalLabel.PRICE, this.currentPrice);
	}

}




















