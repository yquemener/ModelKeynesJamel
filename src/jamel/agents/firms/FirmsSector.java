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

import jamel.Circuit;
import jamel.JamelObject;
import jamel.agents.roles.CapitalOwner;
import jamel.agents.roles.Employer;
import jamel.agents.roles.Provider;
import jamel.util.data.PeriodDataset;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

/**
 * Represents the firms sector.
 * <p>
 * Encapsulates a collection of {@link Firm} agents.
 */
public class FirmsSector extends JamelObject {

	/**
	 * Returns a hash map that contains a list of parameters and their values.
	 * @param paramString - a string that contains parameters and their values.
	 * @return a hash map.
	 */
	private static Map<String,String> getParamHashMap(String paramString) {
		final Map<String,String> parametersMap = new HashMap<String,String>();
		final String[] parameters = paramString.split(",");
		for(final String line:parameters) {
			final String[] parameter = line.split("=",2);
			parametersMap.put(parameter[0], parameter[1]);
		}
		return parametersMap;
	}

	/** The counter of firms created. */
	private int countFirms ;

	/** The list of the providers of final goods. */
	private final LinkedList<Firm> finalGoodsProvidersList ;

	/** The list of the firms. */
	private final LinkedList<Firm> firmsList ;

	/** The list of the providers of intermediate goods. */
	private final LinkedList<Firm> intermediateGoodsProvidersList ;

	/** The scenario. */
	private final LinkedList<String> delayedActions;

	/**
	 * Creates a new firms sector.
	 * @param aScenario - a list of strings that contain the description of the events.
	 */
	public FirmsSector(LinkedList<String> aScenario) {
		this.firmsList = new LinkedList<Firm>() ;
		this.finalGoodsProvidersList = new LinkedList<Firm>() ;
		this.intermediateGoodsProvidersList = new LinkedList<Firm>() ;
		this.delayedActions = new LinkedList<String>(); 
        //this.scenario = aScenario;
		//JamelSimulator.println("Firms sector: ok.");
	}

	/**
	 * Receive notification of a bankruptcy.<br>
	 * A new firm will be created 12 months later.
	 * @param aBankruptFirm the bankrupt firm.
	 */
	private void failure(Firm aBankruptFirm) {
		if (!aBankruptFirm.isBankrupt())
			throw new RuntimeException("Not bankrupt.");
		final String currentDate = getCurrentPeriod().toString();
		Circuit.println(currentDate+" Firm Failure ("+aBankruptFirm.getName()+")");
		final String someMonthsLater = getCurrentPeriod().getNewPeriod(12+getRandom().nextInt(12)).toString();
		//final String instructions = aBankruptFirm.getParametersString();
		this.delayedActions.add(someMonthsLater+".new(firms=1,type="+aBankruptFirm.getClass().getName()+")");
		/*final String type = aBankruptFirm.getClass().getName();
		final String production = aBankruptFirm.getProduction().name();
		this.scenario.add(someMonthsLater+".new(firms=1,type="+type+",production="+production+")");*/
		return ;
	}

	/**
	 * Creates new firms according to the parameters.
	 * @param parametersMap - a map of parameters (key of the parameter, value of the parameter).
	 */
	public void newFirms(Map<String, String> parametersMap) {
		Integer newFirms = null;
		String className = null;
        System.out.println("Creating new firms "+parametersMap);
		for(Entry<String, String> entry : parametersMap.entrySet()) {
			final String key = entry.getKey();
			final String value = entry.getValue();
			if (key.equals("firms")) {
				if (newFirms != null) throw new RuntimeException("Event new firms : Duplicate parameter : firms.");
				newFirms = Integer.parseInt(value);
			}
			else if (key.equals("type")) {
				if (className != null) throw new RuntimeException("Event new firms : Duplicate parameter : type.");
				className = value;				
			}
		}
		if (newFirms==null) 
			throw new RuntimeException("Missing parameter: firms.");
		if (className==null) 
			throw new RuntimeException("Missing parameter: type.");
		parametersMap.remove("firms");
		parametersMap.remove("type");
		for (int count = 0 ; count<newFirms ; count++){
			countFirms ++ ;
			final CapitalOwner owner = Circuit.getRandomCapitalOwner();
			try {
				final String name = "Company "+countFirms;
                HashMap<ExternalLabel,Object> eParams = 
                        (HashMap<ExternalLabel,Object>)Circuit.getCircuit().firmsParams.clone();
                for(String label:parametersMap.keySet())
                {
                  Object o;
                  String s = parametersMap.get(label);
                  if(label.equals("production"))
                  {
                    if(s.equals("intermediateProduction"))
                        o = ProductionType.intermediateProduction;
                    else if(s.equals("finalProduction"))
                        o = ProductionType.finalProduction;
                    else if(s.equals("integratedProduction"))
                        o = ProductionType.integratedProduction;
                    else
                      throw new IllegalArgumentException();
                  }
                  else
                  {
                    try{ o = Integer.parseInt(s);}
                    catch(NumberFormatException e) {
                      try{ o = Float.parseFloat(s);}
                      catch(NumberFormatException e2) {
                        o = s;
                      }}
                  }
                  eParams.put(ExternalLabel.fromString(label), o);
                }
				final Firm newFirm = (Firm) Class.forName(className,false,ClassLoader.getSystemClassLoader())
                        .getConstructor(String.class,CapitalOwner.class,HashMap.class).newInstance(name,owner,eParams);
                
				firmsList.add(newFirm) ;
			} catch (ClassNotFoundException e) {
				e.printStackTrace();
				throw new RuntimeException("Firm creation failure"); 
			} catch (IllegalArgumentException e) {
				e.printStackTrace();
				throw new RuntimeException("Firm creation failure"); 
			} catch (SecurityException e) {
				e.printStackTrace();
				throw new RuntimeException("Firm creation failure"); 
			} catch (InstantiationException e) {
				e.printStackTrace();
				throw new RuntimeException("Firm creation failure"); 
			} catch (IllegalAccessException e) {
				e.printStackTrace();
				throw new RuntimeException("Firm creation failure"); 
			} catch (InvocationTargetException e) {
				e.printStackTrace();
				throw new RuntimeException("Firm creation failure"); 
			} catch (NoSuchMethodException e) {
				e.printStackTrace();
				throw new RuntimeException("Firm creation failure"); 
			}
		}
	}

	/**
	 * Updates the list of providers of final goods.
	 */
	private void updateFinalGoodsProvidersList() {
		this.finalGoodsProvidersList.clear();
		for (Firm aProvider : firmsList) {
			if ((aProvider.getType()==ProductionType.finalProduction)||(aProvider.getType()==ProductionType.integratedProduction))
				this.finalGoodsProvidersList.add(aProvider);
		}
	}

	/**
	 * Updates the list of providers of intermediate goods.
	 */
	private void updateIntermediateGoodsProvidersList() {
		this.intermediateGoodsProvidersList.clear();
		for (Firm aProvider : firmsList) {
			if (aProvider.getType()==ProductionType.intermediateProduction)
				this.intermediateGoodsProvidersList.add(aProvider);
		}
	}

	/**
	 * Buys the raw materials.<br>
	 * Each firm is called to buy the raw materials it needs.
	 */
	public void buyRawMaterials() {
		for (Firm selectedFirm : firmsList) {
			selectedFirm.buyRawMaterials() ; 
		}
	}

	/**
	 * Closes the sector.<br>
	 * Each household is closed. 
	 * The sector data are updated.
	 * @param macroDataset - the macro dataset. 
	 */
	public void close(PeriodDataset macroDataset) {
		for (Firm selectedFirm : firmsList)
			selectedFirm.close() ;
		macroDataset.compileFirmsData(this.firmsList);
		final Collection<Firm> newList = new LinkedList<Firm>();
		for (Firm selectedFirm : firmsList) {
			if (!selectedFirm.isBankrupt()) {
				newList.add(selectedFirm);
			}
			else {
				this.failure(selectedFirm);
			}
		}
		firmsList.clear();
		firmsList.addAll(newList);
	}

	/**
	 * Returns an employer selected at random.
	 * @return a employer.
	 */
	public Employer getRandomEmployer() {
		final int size = this.firmsList.size();
		if (size>0)
			return this.firmsList.get(getRandom().nextInt(size));
		return null;
	}

	/**
	 * Returns a firm selected at random.
	 * @return a firm.
	 */
	public Firm getRandomFirm() {
		final int size = this.firmsList.size();
		if (size>0) {
			final Firm firm =  this.firmsList.get(getRandom().nextInt(this.firmsList.size()));
			return firm;
			}
		return null;
	}

	/**
	 * Returns a firm selected at random.
	 * @param type  the type of the production of the firm to select. 
	 * @return a firm selected at random.
	 */
	public Firm getRandomFirm(ProductionType type) {
		final List<? extends Firm> list = this.getRandomFirms(1, type);
		final Firm firm;
		if (list.isEmpty()) {
			firm = null;
		}
		else {
			firm = list.get(0);
		}
		return firm;
	}

	/**
	 * Returns a list of firms selected at random.
	 * @param size the number of firms to select.
	 * @return a list of firms.
	 */
	public Collection<? extends Firm> getRandomFirms(int size) {
		Collections.shuffle(firmsList,getRandom());
		size = Math.min(size, firmsList.size());
		return firmsList.subList(0, size);
	}

	/**
	 * Returns a collection of firms selected at random.
	 * @param size  the number of firms to select.
	 * @param sector  the type of production of the firms to select.
	 * @return a collection of firms.
	 */
	public List<? extends Firm> getRandomFirms(int size,ProductionType sector) {
		final LinkedList<Firm> theList;
		if (sector==null) {
			theList = this.firmsList;
		}
		else if ((sector.equals(ProductionType.finalProduction))||(sector.equals(ProductionType.integratedProduction))) {
			theList = this.finalGoodsProvidersList;
		}
		else if (sector.equals(ProductionType.intermediateProduction)) {
			theList = this.intermediateGoodsProvidersList;
		}
		else
			throw new RuntimeException("Error while searching for firms. Unknown type of production: "+sector);
		Collections.shuffle(theList,getRandom());
		
		final List<Firm> theSubList = theList.subList(0, Math.min(size, theList.size()));
		return theSubList;
	}

	/**
	 * Returns a provider selected at random in the list of providers of final goods.
	 * @return a provider (<code>null</code> if the list is empty).
	 */
	public Provider getRandomProviderOfFinalGoods() {
		final int size = this.finalGoodsProvidersList.size();
		if (size>0)
			return this.finalGoodsProvidersList.get(getRandom().nextInt(size));
		return null;
	}

	/**
	 * Returns a provider selected at random in the list of providers of intermediate goods.
	 * @return a provider (<code>null</code> if the list is empty).
	 */
	public Provider getRandomProviderOfRawMaterials() {
		final int size = this.intermediateGoodsProvidersList.size();
		if (size>0)
			return this.intermediateGoodsProvidersList.get(getRandom().nextInt(size));
		return null;
	}							

	/**
	 * Kills the sector.
	 */
	public void kill() {
		for (Firm selectedFirm : firmsList) selectedFirm.kill() ;
		this.firmsList.clear();
	}

	/**
	 * Opens the firms sector.<br>
	 * Each firm is opened.
	 */
	public void open() {
		Collections.shuffle(firmsList,getRandom());
		updateFinalGoodsProvidersList();
		updateIntermediateGoodsProvidersList();
		final String date = getCurrentPeriod().toString();
		final LinkedList<String> eList = Circuit.getParametersList(this.delayedActions, date, "\\.");
		if (!eList.isEmpty()) {
			for (String string: eList){
				String[] word = string.split("\\)",2);
				String[] event = word[0].split("\\(",2);
				if (event[0].equals("new"))
					newFirms(getParamHashMap(event[1]));
			}
		}
		for (Firm selectedFirm : firmsList) {
			selectedFirm.open() ;
		}
	}

	/**
	 * Pays the dividend.<br>
	 * Each firm is called to pay the dividend.
	 */
	public void payDividend() {
		for (Firm selectedFirm : firmsList) {			
			if (!selectedFirm.isBankrupt()) {
				selectedFirm.payDividend() ;			
			}
		}
	}

	/**
	 * Plans the production.<br>
	 * Each firm is called to prepare the production.
	 */
	public void planProduction() {
		for (Firm selectedFirm : firmsList) {
			if (!selectedFirm.isBankrupt()) {
				selectedFirm.prepareProduction() ;
			}
		}
	}

	/**
	 * Prints the data of each firm.
	 * @param outputFile  the output file.
	 * @param keys  an array of strings with the name of the fields to be printed; 
	 */
	public void printEach(File outputFile, String[] keys) {
		try {
			final FileWriter writer = new FileWriter(outputFile,true);
			for(Firm firm:this.firmsList){
				firm.getData().write(writer,keys);
			}
			writer.close();
		} catch (IOException e) {
			e.printStackTrace();
			throw new RuntimeException("Error while writing data in the output file.");
		}		
	}

	/**
	 * Executes the production.<br>
	 * Each firm is called to executes the production.
	 */
	public void production() {
		for (Firm selectedFirm : firmsList) {
			if (!selectedFirm.isBankrupt()) {
				selectedFirm.production() ; 
			}
		}
	}							


}