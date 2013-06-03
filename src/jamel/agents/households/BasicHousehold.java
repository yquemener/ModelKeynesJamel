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

import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

import org.jfree.data.time.TimeSeries;
import org.jfree.data.time.TimeSeriesDataItem;

import jamel.Circuit;
import jamel.JamelObject;
import jamel.agents.firms.ProductionType;
import jamel.agents.roles.Employer;
import jamel.agents.roles.Provider;
import jamel.spheres.monetarySphere.Account;
import jamel.spheres.monetarySphere.Check;
import jamel.spheres.realSphere.FinalGoods;
import jamel.spheres.realSphere.Machine;
import jamel.util.data.Labels;
import jamel.util.markets.EmploymentContract;
import jamel.util.markets.GoodsOffer;
import jamel.util.markets.JobOffer;
import jamel.util.markets.ProviderComparator;

/**
 * An implementation of the household interface.
 * <p>
 * A household has two main functions : labor and consumption.
 */
class BasicHousehold extends AbstractHousehold {

	/** The max number of employers in the memory of the household. */
	protected static final int maxEmployers = 10; // TODO should be a parameter.

	/** 
	 * The employer comparator.<p>
	 * To compare employers according to the wage they offer on the labor market.
	 */
	public static Comparator<Employer>
	EMPLOYER_COMPARATOR =
	new Comparator<Employer>() {
		public int compare(Employer e1, Employer e2) {
			final JobOffer offer1 = e1.getJobOffer();
			final JobOffer offer2 = e2.getJobOffer();
			if ((offer1 == null) & (offer2 == null)) return 0;
			if (offer1 == null) return 1;
			if (offer2 == null) return -1;
			return (new Long(offer2.getWage()).compareTo(offer1.getWage()));
		}
	};

	/** 
	 * The provider comparator.
	 */
	public static final ProviderComparator PROVIDER_COMPARATOR = new ProviderComparator();

	/** The bank account. */
	private final Account bankAccount ;

	/** The data. */
	private final HouseholdDataset data ;

	/** The flexibility of the reservation wage. */
	private float flexibility;

	/** The income time series. */
	private final TimeSeries incomeTimeSeries = new TimeSeries("Income");

	/** The job contract. */
	private EmploymentContract jobContract;

	/** The job offer that the household is applying for.*/
	private JobOffer jobOffer;

	/** The name of the household. */
	private String name;

	/** The resistance to a cut of the reservation wage. */
	private int resistance;

	/** The saving propensity. */
	private float savingPropensity;

	/** The unemployment duration. */
	private float unemploymentDuration;

	/** The employers. */
	protected final LinkedList<Employer> employers ;

	/** The maximum size of a list of providers or employers. */
	protected int maxSize = 10; // TODO should be a parameter.

	/** A map that contains the specific parameters of the firm. */
	protected final HashMap<String,String> parametersMap = new HashMap<String,String>();

	/** The list of usual providers. */
	protected LinkedList<Provider> providers ;

	/** The reservation wage. */
	protected float reservationWage;

	/** The preferred sector of the household. */
	protected ProductionType sector = null;

	/**
	 * Creates a new household.
	 * @param aName - the name.
	 * @param parameters - a string that contains some parameters.
	 */
	public BasicHousehold(String aName, String parameters) {
		this.name = aName;
		this.bankAccount = Circuit.getNewAccount(this);
		this.data = new HouseholdDataset();
		this.incomeTimeSeries.setMaximumItemAge(12);
		this.providers = new LinkedList<Provider>();
		this.employers = new LinkedList<Employer>();
		this.defaultSettings();
		this.setParameters(parameters);
		this.updateParameters();
	}

	/**
	 * Adds a new income to the income of the period.
	 * @param newIncome - the new income.
	 */
	private void addToIncomeTimeSeries(long newIncome) {
		this.incomeTimeSeries.addOrUpdate(getCurrentPeriod().getMonth(), this.incomeTimeSeries.getValue(getCurrentPeriod().getMonth()).longValue()+newIncome);
	}

	/**
	 * Returns the average income of the household.
	 * @return the average income.
	 */
	private long getAverageIncome() {
		@SuppressWarnings("unchecked") List<TimeSeriesDataItem> data = this.incomeTimeSeries.getItems();
		long annualIncome = 0;
		if (data.size()==0) return 0;
		for(TimeSeriesDataItem item:data)
			annualIncome += item.getValue().longValue();
		return annualIncome/data.size();
	}

	/**
	 * Purchases.
	 * @param provider - the provider.
	 * @param aBudget - the budget.
	 * @return the value of the purchase.
	 */
	private long purchase(final Provider provider, final long aBudget) {
		GoodsOffer offer = provider.getGoodsOffer() ;
		if (offer == null) throw new RuntimeException("Offer is null.");
		if (offer.getVolume()==0) throw new RuntimeException("Volume is zero.");
		int dealVolume = (int) (aBudget/offer.getPrice());
		if (dealVolume==0) {
			return 0; // No transaction, cause the volume I can purchase with my budget is 0.
		}
		if (dealVolume>offer.getVolume()) dealVolume = offer.getVolume();
		long dealValue = (long) (dealVolume*offer.getPrice());
		FinalGoods purchase = (FinalGoods) provider.sell(offer,dealVolume,this.bankAccount.newCheck(dealValue, provider)) ;
		if (purchase.getVolume()!=dealVolume) throw new RuntimeException();
		this.data.addToConsumptionValue(dealValue);
		this.data.addToConsumptionVolume(dealVolume);
		purchase.consumption() ;
		return dealValue ;
	}
	
	/**
	 * Sets the parameters of the household.
	 * @param string  a string that contain parameters separated by commas.
	 */
	private void setParameters(String string) {
		final String[] rows = string.split(",");
		for(final String row:rows) {
			final String[] data = row.split("=",2);
			this.parametersMap.put(data[0].trim(), data[1].trim());
		}
	}

	/**
	 * Updates the reservation wage.<br>
	 * The level of the reservation wage depends on the number of periods spent in a state of unemployment.
	 * After a certain time, the unemployed worker accepts to lower its reservation wage.
	 */
	private void updateReservationWage() {
		if (this.jobContract!=null) {							
			this.unemploymentDuration=0;					 
			this.reservationWage=this.jobContract.getWage();
		}
		else {
			if (this.unemploymentDuration == 0) this.unemploymentDuration = getRandom().nextFloat() ;
			else this.unemploymentDuration += 1 ;
			final float alpha1 = getRandom().nextFloat();
			final float alpha2 = getRandom().nextFloat();
			if (alpha1*this.resistance < this.unemploymentDuration)
				this.reservationWage=(this.reservationWage*(1f-this.flexibility*alpha2));
		}
		this.data.setReservationWage(this.reservationWage);
		this.data.setUnemploymentDuration(this.unemploymentDuration);
	}

	/**
	 * Initializes the defaults parameters.
	 */
	protected void defaultSettings() {
		this.parametersMap.put("savingPropensity", "0.05");
		this.parametersMap.put("resistance", "12");
		this.parametersMap.put("flexibility", "0.05");
	}

	/**
	 * Checks if the given offer is acceptable. 
	 * @param jobOffer  the offer to check.
	 * @return <code>true</code> if the wage of the given offer is equal or higher than the reservation wage.
	 */
	protected boolean isAcceptable(JobOffer jobOffer) {
		return jobOffer.getWage()>=this.reservationWage;
	}

	/**
	 * Updates the list of employers.
	 */
	protected void updateEmployersList() {
		final LinkedList<Employer> newListOfEmployers = new LinkedList<Employer>();
		for (Employer employer: this.employers){
			if (!employer.isBankrupt())
				newListOfEmployers.add(employer);
		}
		this.employers.clear();
		for (int count = 0; count<maxEmployers; count++){
			final Employer employer = Circuit.getRandomEmployer();
			if (employer.isBankrupt())
				throw new RuntimeException("This employer is bankrupt.");
			if ((employer.getJobOffer()!=null)&&(this.employers.contains(employer)==false)) {
				newListOfEmployers.add(employer);
			}
		}
		if (newListOfEmployers.size()>0) {
			Collections.sort(newListOfEmployers, EMPLOYER_COMPARATOR);
			while (newListOfEmployers.size()>maxEmployers)
				newListOfEmployers.removeLast();
		}
		this.employers.addAll(newListOfEmployers);
	}

	/**
	 * Updates the parameters of the household
	 * using the values recorded in the parameter map.
	 */
	protected void updateParameters() {
		this.savingPropensity = Float.parseFloat(this.parametersMap.get("savingPropensity"));
		this.flexibility = Float.parseFloat(this.parametersMap.get("flexibility"));
		this.resistance = Integer.parseInt(this.parametersMap.get("resistance"));
	}

	/**
	 * Updates the list of the providers.
	 */
	protected void updateProvidersList() {
		while (providers.size()<maxSize){
			final Provider provider = Circuit.getRandomProviderOfFinalGoods();
			if (provider==null) break;
			if (!this.providers.contains(provider)) {
				this.providers.add(provider);
			}
		}
		Collections.sort(providers, PROVIDER_COMPARATOR);
		if (this.providers.size()>maxSize-1)
			this.providers.removeLast();
	}

	/**
	 * Closes the period.
	 */
	@Override
	public void close() {
		this.data.setDeposits(this.bankAccount.getAmount());
		if ((data.getEmploymentStatus()==Labels.STATUS_EMPLOYED)&&(this.isLaborPowerAvailable()))
			throw new RuntimeException("Employed but the labor power is not exhausted.");
		if ((data.getWage()==0)&&(data.getEmploymentStatus()==Labels.STATUS_EMPLOYED))
			throw new RuntimeException("Employed but the wage equals 0.");
	}

	/**
	 * Spends his money in consumption.
	 */
	@Override
	public void consume() {
		final long averageIncome = getAverageIncome();
		final long savingsTarget = (long) (12*averageIncome*this.savingPropensity);
		final long savings = this.bankAccount.getAmount()-averageIncome;
		long consumptionTarget;
		if (savings<savingsTarget) 
			consumptionTarget = (long) ((1.-this.savingPropensity)*averageIncome);
		else
			consumptionTarget = averageIncome + (savings-savingsTarget)/2;
		long budget = Math.min(this.bankAccount.getAmount(),consumptionTarget);
		this.data.setConsumptionBudget(budget);
		updateProvidersList();
		final LinkedList<Provider> newList = new LinkedList<Provider>(providers);
		while (newList.size()>0) {
			final Provider provider = newList.removeFirst();
			final GoodsOffer offer = provider.getGoodsOffer();
			if (offer!=null) {
				if (offer.getVolume()==0) throw new RuntimeException("Volume is zero.");
				if (budget>offer.getPrice()){
					budget-=purchase(provider,budget);
					if (budget<0) throw new RuntimeException("Budget overrun.");
					if (budget==0) break;
				}
			}
			else {
				providers.remove(provider);
			}
		}
		if ((JamelObject.getCurrentPeriod().getYear().getYear()>2002) & (budget>this.data.getConsumptionBudget()/10)) {
			for (int i = 0; i<maxSize; i++) {
				final Provider aProvider = Circuit.getRandomProviderOfFinalGoods();
				if (aProvider==null) break;
				final GoodsOffer offer = aProvider.getGoodsOffer();
				if (offer!=null) {
					if (offer.getVolume()==0) throw new RuntimeException("Volume is zero.");
					if (budget>offer.getPrice()){
						budget-=purchase(aProvider,budget);
						providers.add(aProvider);
						if (budget<0) throw new RuntimeException("Budget overrun.");
						if (budget==0) break;
					}
				}
			}
		}
		this.data.setForcedSavings(budget);
	}

	/**
	 * Returns the data.
	 * @return the data.
	 */
	@Override
	public HouseholdDatasetInterface getData() {
		return data;
	}

	/**
	 * Returns the name of the household.
	 * @return the name.
	 */
	@Override
	public String getName() {
		return this.name;
	}

	/**
	 * Looks for a job.
	 */
	@Override
	public void jobSearch() {
		if ((this.jobContract!=null)&&(!this.jobContract.isValid())){
			this.jobContract = null; // Erase the job contract if not valid.} 
		}
		updateReservationWage() ;
		if (this.jobContract!=null) { // The household is already employed.
			this.data.setEmploymentStatus(Labels.STATUS_EMPLOYED);
		}
		else {
			this.data.setEmploymentStatus(Labels.STATUS_INVOLUNTARY_UNEMPLOYED);		
			updateEmployersList();
			if (employers.size()>0) {
				this.jobOffer = employers.getFirst().getJobOffer();
				if (this.jobOffer != null) {
					if (isAcceptable(this.jobOffer)) {
						//if ((long)this.jobOffer.getWage()>=this.reservationWage) {
						this.jobOffer.getEmployer().jobApplication(this,jobOffer) ;
						if (this.jobOffer == null) 
							throw new RuntimeException("No job contract.");
						if (this.jobOffer.getEmployer().isBankrupt())
							throw new RuntimeException("Employer is bankrupt.");
					}
					else {
						this.jobOffer = null;
						this.data.setEmploymentStatus(Labels.STATUS_VOLUNTARY_UNEMPLOYED);
					}
				}
			}
		}
	}

	/**
	 * Receives notification of his hiring.
	 * @param contract - the employment contract.
	 */
	@Override
	public void notifyHiring(EmploymentContract contract) {
		if (this.jobContract != null) throw new RuntimeException("I've already a job.") ;
		if (this.jobOffer == null) throw new RuntimeException("I'm no applying for a job.") ;
		if ((long)this.jobOffer.getWage() != contract.getWage()) throw new RuntimeException("Bad wage.") ;
		this.jobContract = contract;
		this.sector=contract.getEmployer().getType();
		this.data.setEmploymentStatus(Labels.STATUS_EMPLOYED);
		this.data.setSector(sector);
	}

	/**
	 * Receives notification of his layoff.
	 */
	@Override
	public void notifyLayoff() {
		if (this.jobContract == null) throw new RuntimeException("I'm already without job.") ;
		this.jobContract = null;
		this.data.setEmploymentStatus(Labels.STATUS_INVOLUNTARY_UNEMPLOYED);
	}

	/** 
	 * Opens the household for a new period.<br>
	 * Initializes data and executes events.
	 * @param eList - a list of strings that describes the events for the current period. 
	 */
	@Override
	public void open() {
		updateLaborPower();
		data.clear() ;
		this.incomeTimeSeries.add(getCurrentPeriod().getMonth(), 0);
		/*for (String string: eList){
			String[] word = string.split("\\)",2);
			String[] event = word[0].split("\\(",2);
			if (event[0].equals("set")) {
				this.setParameters(event[1]);
				this.updateParameters();
			}
			else
				throw new RuntimeException("Unknown event \""+event[0]+"\".");
		}*/
	}

	/**
	 * Receives a dividend from the bank.
	 * @param check - the check.
	 */
	@Override
	public void receiveDividend(Check check) {
		final long dividend = check.getAmount();
		this.data.addDividend(dividend) ;
		addToIncomeTimeSeries(dividend);
		this.bankAccount.deposit(check);
	}

	/**
	 * Receives a wage from an employer.
	 * @param check - the check.
	 */
	@Override
	public void receiveWage(Check check) {
		final long wage = check.getAmount();
		if ( this.jobContract == null ) throw new RuntimeException("Contract is null.") ;
		if ( wage != this.jobContract.getWage() ) throw new RuntimeException("Bad Wage.") ;
		this.data.setWage(wage) ;
		addToIncomeTimeSeries(wage);
		this.bankAccount.deposit(check) ;
	}

	/**
	 * Works on the specified machine.
	 * @param machine - the machine to work on.
	 */
	@Override
	public void work(Machine machine) {
		if (this.data.getWage() != this.jobContract.getWage()) 
			throw new RuntimeException("Wage not payed.");
		super.work(machine);
	}

}