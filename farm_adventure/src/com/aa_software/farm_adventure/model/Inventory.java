package com.aa_software.farm_adventure.model;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.aa_software.farm_adventure.model.item.AbstractItem;
import com.aa_software.farm_adventure.model.item.seed.BeetSeed;
import com.aa_software.farm_adventure.model.item.seed.CabbageSeed;
import com.aa_software.farm_adventure.model.item.seed.CarrotSeed;
import com.aa_software.farm_adventure.model.item.seed.PotatoSeed;
import com.aa_software.farm_adventure.model.item.tool.harvest.ScytheTool;
import com.aa_software.farm_adventure.model.item.tool.irrigate.ShovelTool;
import com.aa_software.farm_adventure.model.item.tool.plant.SeedPackTool;
import com.aa_software.farm_adventure.model.item.tool.plow.HandPlowTool;
import com.aa_software.farm_adventure.model.item.worker.DefaultWorker;

//import com.aa_software.farm_adventure.model.item.worker.AbstractWorker;

/**
 * Represents the items that the farm has on hand.
 */
public class Inventory {

	private List<AbstractItem> defaultItems = new ArrayList<AbstractItem>(
			Arrays.asList(new AbstractItem[] { new PotatoSeed(),
					new BeetSeed(), new CarrotSeed(), new CabbageSeed(),
					new HandPlowTool(), new ShovelTool(), new SeedPackTool(),
					new ScytheTool(), new DefaultWorker()

			}));

	private Map<String, ArrayList<AbstractItem>> inventoryItems = new HashMap<String, ArrayList<AbstractItem>>();

	/**
	 * Create inventory from default items
	 */
	public Inventory() {
		int itemCount = this.defaultItems.size();
		for (int i = 0; i < itemCount; i++) {
			addItem(this.defaultItems.get(i));
		}
	}

	/**
	 * Create inventory from provided list of AbstractItems
	 * 
	 * @param items
	 */
	public Inventory(List<AbstractItem> items) {
		int itemCount = items.size();
		for (int i = 0; i < itemCount; i++) {
			addItem(items.get(i));
		}
	}

	/**
	 * Add AbstractItem to the inventory based on its type (e.g. plow tool,
	 * irrigation tool, ...)
	 * 
	 * @param item
	 */
	public void addItem(AbstractItem item) {
		if (inventoryItems.size() == 0) {
			String itemType = item.getItemType();
			ArrayList<AbstractItem> items = new ArrayList<AbstractItem>();
			items.add(item);
			inventoryItems.put(itemType, items);
		} else {
			String itemType = item.getItemType();
			if (inventoryItems.containsKey(itemType)) {
				inventoryItems.get(itemType).add(item);
			} else {
				ArrayList<AbstractItem> items = new ArrayList<AbstractItem>();
				items.add(item);
				inventoryItems.put(itemType, items);
			}
		}
	}

	public ArrayList<AbstractItem> getAllWorkers() {
		return inventoryItems.get("WORKERS");
	}

	/**
	 * Searches through the inventory to get and return the count of the
	 * specific item It first searches for the item type and then iterates
	 * through the list of that type of items to get the count
	 * 
	 * @param item
	 * @return number of items in the inventory
	 */
	public int getCount(AbstractItem item) {
		if (inventoryItems.size() == 0) {
			return 0;
		} else {
			String itemType = item.getItemType();
			if (inventoryItems.containsKey(itemType)) {
				ArrayList<AbstractItem> typeList = inventoryItems.get(itemType);
				int typeCount = typeList.size();
				int itemCount = 0;
				for (int i = 0; i < typeCount; i++) {
					if (typeList.get(i).compareTo(item) == 0) {
						itemCount++;
					}
				}
				return itemCount;
			} else {
				return 0;
			}
		}
	}

	public DefaultWorker getFreeWorker() {
		String itemType = "WORKERS";
		if (inventoryItems.containsKey(itemType)) {
			for (int i = 0; i < getWorkerCount(); i++) {
				DefaultWorker worker = (DefaultWorker) inventoryItems.get(
						itemType).get(i);
				if (!worker.isBusy()) {
					return worker;
				}
			}
		}
		return null;
	}

	/**
	 * Returns a hashMap of all items in the inventory (the key is the itemType)
	 * 
	 * @return inventoryItems
	 */
	public Map<String, ArrayList<AbstractItem>> getItems() {
		return this.inventoryItems;
	}

	/**
	 * Returns the number of all items in the inventory
	 * 
	 * @return count of all items in the inventory
	 */
	public int getItemsCount() {
		if (inventoryItems.size() == 0) {
			return 0;
		} else {
			int count = 0;
			Object[] keyset = inventoryItems.keySet().toArray();
			int countItemType = keyset.length;
			for (int i = 0; i < countItemType; i++) {
				count += inventoryItems.get(keyset[i]).size();
			}
			return count;
		}
	}

	/**
	 * Searches through the inventory to get and return the count of
	 * AbstractWorkers It first searches for the key of type "WORKERS" that
	 * returns the number of items in the mapped list
	 * 
	 * @return
	 */
	public int getWorkerCount() {
		String itemType = "WORKERS";
		if (inventoryItems.containsKey(itemType)) {
			return inventoryItems.get(itemType).size();
		} else {
			return 0;
		}
	}

	/**
	 * Searches through the inventory to get and return the count of
	 * AbstractWorkers It first searches for the key of type "WORKERS" that
	 * returns the number of items in the mapped list
	 * 
	 * @return
	 */
	public ArrayList<AbstractItem> getWorkers() {
		String itemType = "WORKERS";
		ArrayList<AbstractItem> workers = null;
		if (inventoryItems.containsKey(itemType)) {
			workers = inventoryItems.get(itemType);
		}
		return workers;
	}

	/**
	 * Removes item from the inventory
	 * 
	 * @param item
	 * @return true if item was deleted and false if the item was not found
	 */
	public boolean removeItem(AbstractItem item) {
		if (inventoryItems.size() != 0) {
			String itemType = item.getItemType();
			if (inventoryItems.containsKey(itemType)) {
				int del = 0;
				int itemsCount = inventoryItems.get(itemType).size();
				for (int i = 0; i < itemsCount; i++) {
					if (inventoryItems.get(itemType).get(i).compareTo(item) == 0) {
						inventoryItems.get(itemType).remove(i);
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