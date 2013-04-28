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

import java.util.HashMap;

/**
 * Contains the labels of the parameters of the firms.
 */
public enum InternalLabel {
	GROSS_PROFIT("The gross profit"),
	GROSS_PROFIT_AVERAGE("average gross profit on the last periods"),
	INVENTORY_FG_VALUE("Value of the inventory stock of finished goods"),
	INVENTORY_FG_VOLUME("Volume of the inventory stock of finished goods"),
	INVENTORY_LEVEL_RATIO("inventory level ratio"),
	INVENTORY_UG_VALUE("Value of inventory of unfinished goods"),
	JOBS_OFFERED("Jobs offered"),
	MACHINERY("Number of machines"),
	OFFER_OF_GOODS("The offer on the market of goods"),
	OFFER_OF_JOB("The offer of job on the labor market"),
	PAYROLL("The payroll"),
	PRICE("current price"),
	PRODUCT_FOR_SALES("product for sales"),
	PRODUCTION_LEVEL("production level"),
	PRODUCTION_LEVEL_MAX("maximum production level"),
	PRODUCTION_MAX("the average production volume at full utilization of capacity"),
	PRODUCTION_VALUE("Value of the production"),
	PRODUCTION_VOLUME("Volume of the production"),
	RAW_MATERIALS("The new purchased raw materials"),
	RAW_MATERIALS_BUDGET("raw materials budget"),
	RAW_MATERIALS_NEEDS("raw materials needs"),
	RAW_MATERIALS_VOLUME("raw materials inventory volume"),
	COST_OF_GOODS_SOLD("Sales at cost value"),
	SALES_VALUE("Sales at price value"),
	SALES_VOLUME("Volume of sales"),
	UNIT_COST("unit cost"),
	UTIL_RATE_TARGET("utilization rate targeted"),
	VACANCIES("vacancies"),
	WAGEBILL("effective wage bill"),
	WAGEBILL_BUDGET("wage bill budget"),
	WORKFORCE("effective workforce"),
	WORKFORCE_TARGET("workforce target");
        
    private String description;

    private final static HashMap<String, InternalLabel> hash;
    
    // Static constructor
    static{
      hash = new HashMap<String, InternalLabel>();
      for(InternalLabel l:InternalLabel.values())
      {
        hash.put(l.toString(), l);
      }
    }
        
    InternalLabel(String s){
        description = s;
    }
           
    @Override
    public String toString()    { return description; }

    public static InternalLabel fromString(String s) { return hash.get(s); }

}
