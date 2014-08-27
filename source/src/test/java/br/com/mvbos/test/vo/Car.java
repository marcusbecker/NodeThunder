package br.com.mvbos.test.vo;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Calendar;

import br.com.mvbos.nodethunder.annotation.ThunderEntity;
import br.com.mvbos.nodethunder.annotation.ThunderField;

/**
 * 
 * @author Marcus Becker
 * 
 */

@ThunderEntity(propertyName = "name")
public class Car {

    @ThunderField
    private String name;

    @ThunderField
    private String[] models;

    @ThunderField
    private int[] doors;

    @ThunderField
    private BigDecimal[] prices;

    @ThunderField
    private double[] offers;

    @ThunderField
    private long[] ids;

    @ThunderField
    private Calendar[] years;

    @ThunderField
    private boolean[] optionals;

    public Car() {
	super();
    }

    public Car(String name, String[] models, int[] doors, BigDecimal[] prices, double[] offers, long[] ids, Calendar[] years,
	    boolean[] optionals) {
	super();
	this.name = name;
	this.models = models;
	this.doors = doors;
	this.prices = prices;
	this.offers = offers;
	this.ids = ids;
	this.years = years;
	this.optionals = optionals;
    }

    public String getName() {
	return name;
    }

    public void setName(String name) {
	this.name = name;
    }

    public String[] getModels() {
	return models;
    }

    public void setModels(String[] models) {
	this.models = models;
    }

    public int[] getDoors() {
	return doors;
    }

    public void setDoors(int[] doors) {
	this.doors = doors;
    }

    public BigDecimal[] getPrices() {
	return prices;
    }

    public void setPrices(BigDecimal[] prices) {
	this.prices = prices;
    }

    public double[] getOffers() {
	return offers;
    }

    public void setOffers(double[] offers) {
	this.offers = offers;
    }

    public long[] getIds() {
	return ids;
    }

    public void setIds(long[] ids) {
	this.ids = ids;
    }

    public Calendar[] getYears() {
	return years;
    }

    public void setYears(Calendar[] years) {
	this.years = years;
    }

    public boolean[] getOptionals() {
	return optionals;
    }

    public void setOptionals(boolean[] optionals) {
	this.optionals = optionals;
    }

    @Override
    public String toString() {
	return "Car [name=" + name + ", models=" + Arrays.toString(models) + ", doors=" + Arrays.toString(doors) + ", prices="
		+ Arrays.toString(prices) + ", offers=" + Arrays.toString(offers) + ", ids=" + Arrays.toString(ids) + ", years="
		+ Arrays.toString(years) + ", optionals=" + Arrays.toString(optionals) + "]";
    }

    @Override
    public int hashCode() {
	final int prime = 31;
	int result = 1;
	result = prime * result + Arrays.hashCode(doors);
	result = prime * result + Arrays.hashCode(ids);
	result = prime * result + Arrays.hashCode(models);
	result = prime * result + ((name == null) ? 0 : name.hashCode());
	result = prime * result + Arrays.hashCode(offers);
	result = prime * result + Arrays.hashCode(optionals);
	result = prime * result + Arrays.hashCode(prices);
	return result;
    }

    @Override
    public boolean equals(Object obj) {
	if (this == obj)
	    return true;
	if (obj == null)
	    return false;
	if (getClass() != obj.getClass())
	    return false;
	Car other = (Car) obj;
	if (!Arrays.equals(doors, other.doors))
	    return false;
	if (!Arrays.equals(ids, other.ids))
	    return false;
	if (!Arrays.equals(models, other.models))
	    return false;
	if (name == null) {
	    if (other.name != null)
		return false;
	} else if (!name.equals(other.name))
	    return false;
	if (!Arrays.equals(offers, other.offers))
	    return false;
	if (!Arrays.equals(optionals, other.optionals))
	    return false;
	if (!Arrays.equals(prices, other.prices))
	    return false;
	return true;
    }

}
