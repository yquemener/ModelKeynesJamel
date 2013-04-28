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

import jamel.JamelObject;
import jamel.agents.firms.InternalLabel;
import jamel.agents.firms.ExternalLabel;
import jamel.agents.firms.BasicFirm;
import java.util.HashMap;

/**
 * A basic production manager.
 */
public class ProductionManager extends JamelObject{

	/** The flexibility of the capacity utilization rate. */
	protected float utilizationRateFlexibility = 10;

	/** The current (targeted) capacity utilization rate. */
	protected float utilizationRateTargeted=75+getRandom().nextInt(25);

	/** The black board of the firm, used for internal communication between
         *  managers.
         */
	final private HashMap<InternalLabel,Object> blackboard;

	/** The external repository of parameters. */
	final private HashMap<ExternalLabel,Object> externalParams;

	/**
	 * Creates a new manager.
	 * @param blackboard2  the black board.
	 */
	public ProductionManager(BasicFirm parent) {
		this.blackboard = parent.blackboard;
                this.externalParams = parent.externalParams;
	}

	/**
	 * Updates the level of production targeted.
	 */
	public void updateProductionLevel() {
		final float alpha1 = getRandom().nextFloat();
		final float alpha2 = getRandom().nextFloat();
		final float inventoryRatio = (Float)this.blackboard.get(InternalLabel.INVENTORY_LEVEL_RATIO);
		if (inventoryRatio<1-alpha1*alpha2) {// Low level
			final float delta = (alpha1*this.utilizationRateFlexibility);
			this.utilizationRateTargeted += delta;
			if (this.utilizationRateTargeted>100) {
				this.utilizationRateTargeted = 100;
			}
		}
		else if (inventoryRatio>1+alpha1*alpha2) {// High level
			final float delta = (alpha1*this.utilizationRateFlexibility);
			this.utilizationRateTargeted -= delta;
			if (this.utilizationRateTargeted<0) {
				this.utilizationRateTargeted = 0;
			}
		}
		final float maxUtilization = (Float) this.blackboard.get(InternalLabel.PRODUCTION_LEVEL_MAX);
		final float rectifiedTarget = Math.min(this.utilizationRateTargeted, maxUtilization);
		this.blackboard.put(InternalLabel.PRODUCTION_LEVEL, rectifiedTarget);
	}

}























