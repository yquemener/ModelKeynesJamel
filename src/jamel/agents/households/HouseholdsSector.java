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
import jamel.JamelObject;
import jamel.agents.roles.CapitalOwner;
import jamel.agents.roles.Consumer;
import jamel.agents.roles.Worker;
import jamel.util.data.PeriodDataset;

import java.util.Collections;
import java.util.LinkedList;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

/**
 * Represents the sector of the households.
 * <p>
 * Encapsulates a collection of {@link Household} agents.
 */
public class HouseholdsSector extends JamelObject {
  

	/** The number of households created. */
	private int countHouseholds = 0 ;

	/** The list of the households. */
	private final LinkedList<Household> householdsList ;

	/**
	 * Creates a new households sector.
	 * @param aScenario - a list of strings that contain the description of the events.
	 */
	public HouseholdsSector() { 
		this.householdsList = new LinkedList<Household>() ;
		//JamelSimulator.println("Households sector: ok.");
	}

	/**
	 * Creates new households.
	 * @param parameters - an array of strings that must contain :
	 * the number of households to create, 
	 * the type of household to create,
	 * and possibly other parameters.
	 */
	public void newHouseholds(Map<String, String> parametersMap) {
		Integer newHouseholds = null;
		String householdClassName = null;
		HashMap<String,String> moreParameter = new HashMap<String, String>();
		for(Entry<String, String> entry : parametersMap.entrySet()) {
            final String key = entry.getKey();
			final String value = entry.getValue();
			if (key.equals("households")) {
				if (newHouseholds != null) throw new RuntimeException("Event new households : Duplicate parameter : households.");
				newHouseholds = Integer.parseInt(value);
			}
			else if (key.equals("type")) {
				if (householdClassName != null) throw new RuntimeException("Event new households : Duplicate parameter : type.");
				householdClassName = value;				
			}
			else {
              moreParameter.put(key, value);
			}
		}
		if (newHouseholds==null) 
			throw new RuntimeException("Missing parameter: households.");
		if (householdClassName==null) 
			throw new RuntimeException("Missing parameter: type.");
		for (int count = 0 ; count<newHouseholds ; count++){
			countHouseholds ++ ;
			try {
				final String name = "Household "+countHouseholds;
				Household newHousehold;
				newHousehold = (Household) Class.forName(householdClassName,false,ClassLoader.getSystemClassLoader()).getConstructor(String.class,Map.class).newInstance(name,moreParameter);
				householdsList.add(newHousehold) ;
			} catch (Exception e) {
				e.printStackTrace();
				throw new RuntimeException("Household creation failure"); 
			}
		}
	}

	/**
	 * Closes the sector.<br>
	 * Each household is closed. 
	 * The macro data are updated.
	 * @param macroData - the macro dataset.
	 */
	public void close(PeriodDataset macroData) {
		for (Household aHousehold : householdsList) 
			aHousehold.close() ;
		macroData.compileHouseholdsData(householdsList);
	}

	/**
	 * Calls up each consumer to consume. 
	 */
	public void consume() {
		for (Consumer selectedConsumer : householdsList)
			selectedConsumer.consume() ;		
	}

	/**
	 * Calls up each worker to job search. 
	 */
	public void jobSearch() {
		for (Worker selectedWorker : householdsList)
			selectedWorker.jobSearch() ;		
	}

	/**
	 * Opens the firms sector.<br>
	 * Each firm is opened.
	 */
	public void open() {
		Collections.shuffle(householdsList, getRandom());
		/*final String date = getCurrentPeriod().toString();
		final LinkedList<String> eList = Circuit.getParametersList(this.scenario, date, "\\.");
		final LinkedList<String> eList2 = new LinkedList<String>();
		if (!eList.isEmpty()) {
			for (String string: eList){
				String[] word = string.split("\\)",2);
				String[] event = word[0].split("\\(",2);
				if (event[0].equals("new"))
					newHouseholds(event[1].split(","));
				else 
					eList2.add(string);
			}
		}*/
		for (Household selectedHousehold : householdsList) {
			selectedHousehold.open() ;
		}
	}

	/**
	 * Selects a capital owner at random.
	 * @return the selected capital owner.
	 */
	public CapitalOwner selectRandomCapitalOwner() {
		final int size = householdsList.size();
		if (size==0) return null;//throw new RuntimeException("The list of households is empty.");
		return householdsList.get( getRandom().nextInt( size ) ) ;
	}

}