package com.aa_software.farm_adventure.model.item;

import com.aa_software.farm_adventure.model.Inventory;
import com.aa_software.farm_adventure.model.audio.Sounds;
import com.aa_software.farm_adventure.model.plot.Plot;

/**
 * Represents the basis of an individual item.
 * 
 * @author Bebop
 * 
 */
public abstract class AbstractItem implements Comparable<AbstractItem> {

	/* Sound */
	public static final Sounds SOUNDS = Sounds.getInstance();
	protected int cost;
	protected int value;
	protected String name;
	protected String description;

	public AbstractItem() {

	}

	public AbstractItem(int cost, int value, String name, String description) {
		this.cost = cost;
		this.value = value;
		this.name = name;
		this.description = description;
	}

	/**
	 * Compare items based on their name
	 */
	@Override
	public int compareTo(AbstractItem item) {
		return getName().compareTo(item.getName());
	}

	/**
	 * Rerun the item's buying cost
	 * 
	 * @return cost
	 */
	public int getCost() {
		return cost;
	}

	public String getDescription() {
		return description;
	}

	/**
	 * Returns the itemType as a String
	 * 
	 * @return
	 */
	public abstract String getItemType();

	public String getName() {
		return name;
	}

	public abstract String getTextureName();

	/**
	 * Return the item's selling value
	 * 
	 * @return
	 */
	public int getValue() {
		return this.value;
	}

	/**
	 * Sets the item's buying cost to the provided amount
	 * 
	 * @param cost
	 */
	public void setCost(int cost) {
		this.cost = cost;
	}

	/**
	 * Sets the item's selling value to the provided amount
	 * 
	 * @param value
	 */
	public void setValue(int value) {
		this.value = value;
	}

	/**
	 * Returns item's name as its string representation
	 */
	@Override
	public String toString() {
		return name;
	}

	public abstract void update(AbstractItem item);

	/**
	 * Changes the status of the given plot and inventory
	 * 
	 * @param plot
	 */
	public abstract void update(Plot plot, Inventory inventory);
}
