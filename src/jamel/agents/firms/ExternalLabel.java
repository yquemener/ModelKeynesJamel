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
public enum ExternalLabel {
	PARAM_FACTORY_MACHINES("size"),
	PARAM_FACTORY_PROD_MAX("prodMax"),
	PARAM_FACTORY_PROD_MIN("prodMin"),
	PARAM_FACTORY_PRODUCTION_TIME("productionTime"),
	PRICE_FLEXIBILITY("price flexibility"),
	PRODUCTION("production"),
	TECH_COEFF("coefficient"),
	WAGE_DOWN_FLEX("wage downward flex"),
	WAGE_UP_FLEX("wage upward flex");
        
    private String description;
    
    private final static HashMap<String, ExternalLabel> hash;
    
    // Static constructor
    static{
      hash = new HashMap<String, ExternalLabel>();
      for(ExternalLabel l:ExternalLabel.values())
      {
        hash.put(l.toString(), l);
      }
    }
    
    ExternalLabel(String s){
        description = s;
    }
           
    @Override
    public String toString()    { return description; }

    public static ExternalLabel fromString(String s) { return hash.get(s); }

}
