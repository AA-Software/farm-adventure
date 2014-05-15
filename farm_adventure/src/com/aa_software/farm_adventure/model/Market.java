package com.aa_software.farm_adventure.model;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import com.aa_software.farm_adventure.model.item.AbstractItem;
import com.aa_software.farm_adventure.model.item.crop.BeetCrop;
import com.aa_software.farm_adventure.model.item.crop.CabbageCrop;
import com.aa_software.farm_adventure.model.item.crop.CarrotCrop;
import com.aa_software.farm_adventure.model.item.crop.PotatoCrop;
import com.aa_software.farm_adventure.model.item.seed.BeetSeed;
import com.aa_software.farm_adventure.model.item.seed.CabbageSeed;
import com.aa_software.farm_adventure.model.item.seed.CarrotSeed;
import com.aa_software.farm_adventure.model.item.seed.PotatoSeed;
import com.aa_software.farm_adventure.model.item.tool.harvest.ScytheTool;
import com.aa_software.farm_adventure.model.item.tool.irrigate.ShovelTool;
import com.aa_software.farm_adventure.model.item.tool.plant.SeedPackTool;
import com.aa_software.farm_adventure.model.item.tool.plow.HandPlowTool;
import com.aa_software.farm_adventure.model.item.worker.DefaultWorker;

/**
 * Represents a market, where the player can buy, sell, and upgrade items.
 * 
 * @author Bebop
 * 
 */
public class Market {

	private ArrayList<AbstractItem> defaultItems = new ArrayList<AbstractItem>(
			Arrays.asList(new AbstractItem[] { new DefaultWorker(),
					new PotatoCrop(), new BeetCrop(), new CarrotCrop(),
					new CabbageCrop(), new HandPlowTool(), new ShovelTool(),
					new SeedPackTool(), new ScytheTool(), new PotatoSeed(),
					new BeetSeed(), new CarrotSeed(), new CabbageSeed() }));

	private Map<String, ArrayList<AbstractItem>> marketItems = new HashMap<String, ArrayList<AbstractItem>>();

	/**
	 * Create market from default items
	 */
	public Market() {
		int itemCount = this.defaultItems.size();
		for (int i = 0; i < itemCount; i++) {
			addItem(this.defaultItems.get(i));
		}
	}

	/**
	 * Create market from provided list of AbstractItems
	 * 
	 * @param items
	 */
	public Market(ArrayList<AbstractItem> items) {
		int itemCount = items.size();
		for (int i = 0; i < itemCount; i++) {
			addItem(items.get(i));
		}
	}

	/**
	 * Add AbstractItem to the market based on its type (e.g. plow tool,
	 * irrigation tool, ...)
	 * 
	 * @param item
	 */
	public void addItem(AbstractItem item) {
		if (marketItems.size() == 0) {
			String itemType = item.getItemType();
			ArrayList<AbstractItem> items = new ArrayList<AbstractItem>();
			items.add(item);
			marketItems.put(itemType, items);
		} else {
			String itemType = item.getItemType();
			if (marketItems.containsKey(itemType)) {
				marketItems.get(itemType).add(item);
			} else {
				ArrayList<AbstractItem> items = new ArrayList<AbstractItem>();
				items.add(item);
				marketItems.put(itemType, items);
			}
		}
	}

	/**
	 * Returns a hashMap of all items in the market (the key is the itemType)
	 * 
	 * @return marketItems
	 */
	public Map<String, ArrayList<AbstractItem>> getItems() {
		return this.marketItems;
	}

	/**
	 * Returns the number of all items in the market
	 * 
	 * @return count of all items in the market
	 */
	public int getItemsCount() {
		if (marketItems.size() == 0) {
			return 0;
		} else {
			int count = 0;
			Object[] keyset = marketItems.keySet().toArray();
			int countItemType = keyset.length;
			for (int i = 0; i < countItemType; i++) {
				count = marketItems.get(keyset[i]).size();
			}
			return count;
		}
	}

	/**
	 * Removes item from the inventory
	 * 
	 * @param item
	 * @return true if item was deleted and false if the item was not found
	 */
	public boolean removeItem(AbstractItem item) {
		if (marketItems.size() != 0) {
			String itemType = item.getItemType();
			if (marketItems.containsKey(itemType)) {
				int del = 0;
				int itemsCount = marketItems.get(itemType).size();
				for (int i = 0; i < itemsCount; i++) {
					if (marketItems.get(itemType).get(i).compareTo(item) == 0) {
						marketItems.get(itemType).remove(i);
						del++;
					}
					if (del > 0) {
						return true;
					}
				}
			}
		}
		return false;
	}
}
