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

import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.HashMap;

import jamel.agents.firms.BasicFirm;
import jamel.agents.firms.InternalLabel;
import jamel.agents.firms.ExternalLabel;
import jamel.agents.roles.Worker;
import jamel.util.markets.EmploymentContract;

/**
 * Represents a factory.
 * <p>
 * A factory encapsulates a machinery (= a collection of {@link Machine} objects).
 */
abstract class AbstractFactory implements jamel.spheres.realSphere.Factory{

	/** The inventory stock target (in number of month of production). */
	private final static int inventoryStockTarget = 4; // TODO Should be a parameter.

	/** 
	 * The machine comparator.<p>
	 * To compare machines and sort them according to the progress of their process of production.
	 */
	public static Comparator<Machine>
	MACHINE_COMPARATOR =
		new Comparator<Machine>() {
		@Override
		public int compare(Machine m1, Machine m2) {
			if (m1.getProgress()>m2.getProgress()) return -1;
			if (m1.getProgress()<m2.getProgress()) return 1;
			if (m1.getProductivity()>m2.getProductivity()) return -1;			
			if (m1.getProductivity()<m2.getProductivity()) return 1;
			return 0;
		}
	};

	/** The average monthly volume that the factory produces when working at its full capacity. */
	private int maxProduction;

	/** The value of the production of the current period. */
	private long productionValue;

	/** The volume of the production of the current period. */
	private int productionVolume ;

	/** The black board of the firm, used for internal communication between
         *  managers.
         */
	final protected HashMap<InternalLabel,Object> blackboard;

	/** The external repository of parameters. */
	final protected HashMap<ExternalLabel,Object> externalParams;
        

	/** The inventory where the production of the firm is stored. */
	protected Goods finishedGoodsInventory;
	
	/** The list of machines. */
	protected final LinkedList<Machine> machinery = new LinkedList<Machine>() ;

	/**
	 * Creates a new factory.
	 * @param blackBoard  the blackboard.
	 */
	public AbstractFactory(BasicFirm parent) {
		this.blackboard = parent.blackboard;
                this.externalParams = parent.externalParams;
		setDefaultParameters();		
		this.toolUp();
	}
	
	/**
	 * Adds some default parameters to blackboard.
	 */
	private void setDefaultParameters() {
		final Map<ExternalLabel, Object> defaultParameters = this.getDefaultParameters();
		for(Entry<ExternalLabel, Object> entry : defaultParameters.entrySet()) {
			final ExternalLabel key = entry.getKey();
			if (!this.externalParams.containsKey(key)) {
				this.externalParams.put(key, entry.getValue());
			}
		}
	}
	
	/**
	 * Changes the productivity of all machines.
	 * @param ratio the change ratio.
	 */
	@SuppressWarnings("unused")
	private void changeProductivity(float ratio) {
		for (Machine machine : machinery) machine.changeProductivity(ratio);
	}

	/**
	 * Returns a fraction of the inventory.
	 * @return a heap of goods.
	 */
	private Goods getProductForSale() {
		final int volume = Math.min(
				this.finishedGoodsInventory.getVolume()/2,
				this.maxProduction*2
				) ;
		if (volume==0)
			return null;
		return this.finishedGoodsInventory.newGoods(volume);
	}

	/**
	 * Returns the average theoretical productivity of the factory.
	 * @return a double that represents the average theoretical productivity.
	 */
	@SuppressWarnings("unused")
	private double getProductivity() {
		int count = 0;
		long sum = 0;
		for (Machine machine : machinery) {
			count++;
			sum += machine.getProductivity();
		}
		return sum/count; 
	}

	/**
	 * Sets the production cycle time.<br>
	 * The production cycle time of each machine within the factory is modified.
	 * @param prodTime - the production cycle time to set.
	 */
	@SuppressWarnings("unused")
	private void setProdTime(int prodTime) {
		for (Machine machine : machinery) {
			machine.setProdTime(prodTime);
		}
	}

	/**
	 * Tools up the factory with new machines.
	 */
	public void toolUp() {
		final int machines = (Integer) this.externalParams.get(ExternalLabel.PARAM_FACTORY_MACHINES);
		final int productivityMin = (Integer) this.externalParams.get(ExternalLabel.PARAM_FACTORY_PROD_MIN);
		final int productivitymax =  (Integer) this.externalParams.get(ExternalLabel.PARAM_FACTORY_PROD_MAX);
		final int productionTime =  (Integer) this.externalParams.get(ExternalLabel.PARAM_FACTORY_PRODUCTION_TIME);
        machinery.clear();
		if (machines == 0) new RuntimeException("The number of machines can't be nul.");
		if (machines == 1) {
			machinery.add(newMachine((productivityMin+productivitymax)/2, productionTime));
			return;
		}
		for (int i = 0; i<machines; i++) {
			int prod = productivityMin+(i*(productivitymax-productivityMin))/(machines-1);
			machinery.add(newMachine(prod, productionTime));
		}
	}

	/**
	 * Updates the average monthly volume that the factory produces when working at its full capacity.  
	 */
	private void updateMaxProduction() {
		int production = 0;				
		for (Machine thisMachine : machinery) 
			production += thisMachine.getProductivity();
		this.maxProduction = production;
		this.blackboard.put(InternalLabel.PRODUCTION_MAX,this.maxProduction);
	}

	/**
	 * Returns a HashMap that contains the default parameters for this factory.
	 * @return a HashMap.
	 */
	abstract protected Map<ExternalLabel, Object> getDefaultParameters();
	
	/**
	 * Adds a new stock of commodity to the current stock of product.<br>
	 * The new product value is its production cost.
	 * @param product - the commodity to add.
	 */
	protected void addProduct(Goods product) {
		this.productionVolume += product.getVolume();
		this.productionValue += product.getValue();
		this.finishedGoodsInventory.add(product);				
	}
	
	/**
	 * Returns the max level of production according to the current resources of the factory. 
	 * @return a float between 0 and 100.
	 */
	abstract protected double getMaxLevelOfProduction();

	/**
	 * Returns the value of the inventory of raw materials.
	 * Used to calculate the total value of the factory.
	 * @return the value of the inventory of raw materials.
	 */
	protected abstract long getRawMaterialsInventoryValue();

	/**
	 * Returns the volume of the need of raw materials.
	 * @return a volume.
	 */
	abstract protected int getRawMaterialsNeedVolume();

	/**
	 * Return the value of unfinished goods stock within the factory.
	 * @return the value of the unfinished goods.
	 */
	protected long getUnfinishedGoodsValue() {
		long value = 0;
		for (Machine machine : machinery) value += machine.getProductionProcessValue();
		return value; 
	}

	/**
	 * Returns a new machine with the given parameters.
	 * @param productivity - the productivity.
	 * @param productionTime - the production time.
	 * @return a new machine.
	 */
	abstract protected Machine newMachine(int productivity, int productionTime);

	@Override 
	public void close() {
		final Goods unsoldGoods = (Goods) this.blackboard.remove(InternalLabel.PRODUCT_FOR_SALES);
		if (unsoldGoods !=null)
			this.finishedGoodsInventory.add(unsoldGoods);
		this.blackboard.put(InternalLabel.INVENTORY_FG_VALUE, this.finishedGoodsInventory.getValue());
		this.blackboard.put(InternalLabel.INVENTORY_FG_VOLUME, this.finishedGoodsInventory.getVolume());
		this.blackboard.put(InternalLabel.INVENTORY_UG_VALUE, this.getUnfinishedGoodsValue());
	}
	
	/**
	 * Returns the total value of the factory.
	 * This total value is the sum of the value of the inventory of finished goods
	 * plus the value of unfinished goods embedded in the processes of production
	 * plus the value of the inventory of raw materials. 
	 * @return a value.
	 */
	@Override
	public long getWorth() {
		return this.finishedGoodsInventory.getValue()+this.getUnfinishedGoodsValue()+this.getRawMaterialsInventoryValue();
	}

	/**
	 * Kills the factory.
	 */
	@Override
	public void kill() {
		for (Machine machine : machinery) machine.kill();
		this.machinery.clear();
	}
	
	/**
	 * Completes some technical operations at the beginning of the period.
	 */
	@Override
	public void open() {
		this.productionVolume = 0;
		this.productionValue = 0;
		updateMaxProduction();
		final double normalInventoryStockLevel = inventoryStockTarget *this.maxProduction;
		final double currentInventoryStockLevel = this.finishedGoodsInventory.getVolume();
		this.blackboard.put(InternalLabel.INVENTORY_LEVEL_RATIO, currentInventoryStockLevel/normalInventoryStockLevel);
		this.blackboard.put(InternalLabel.UNIT_COST,this.finishedGoodsInventory.getUnitCost());
		this.blackboard.put(InternalLabel.PRODUCTION_LEVEL_MAX, (double)this.getMaxLevelOfProduction());
		this.blackboard.put(InternalLabel.MACHINERY, this.machinery.size());
		this.blackboard.put(InternalLabel.RAW_MATERIALS_NEEDS, this.getRawMaterialsNeedVolume());
	}

	/**
	 * Production function of the factory.<br>
	 * Summons each employee and makes him work on a machine.
	 */
	@Override
	public void production() {
		@SuppressWarnings("unchecked")
		final List<EmploymentContract> payroll = (List<EmploymentContract>) this.blackboard.remove(InternalLabel.PAYROLL);
		Collections.sort(this.machinery,MACHINE_COMPARATOR);
		Iterator<Machine> machineIterator=this.machinery.iterator();
		for (EmploymentContract contract : payroll) {
			Worker worker = contract.getEmployee();
			long wage = contract.getWage();
			Machine machine = machineIterator.next();
			machine.work(worker,wage);
		}
		this.blackboard.put(InternalLabel.PRODUCT_FOR_SALES, this.getProductForSale());
		this.blackboard.put(InternalLabel.PRODUCTION_VALUE, this.productionValue);
		this.blackboard.put(InternalLabel.PRODUCTION_VOLUME, this.productionVolume);
	}

	/**
	 * Sets the productivity of the machines.
	 * @param newProductivity - the productivity to set.
	 */
	@Override
	public void setProductivity(int newProductivity) {
		for (Machine machine : machinery) machine.setProductivity(newProductivity);
	}
	
}










