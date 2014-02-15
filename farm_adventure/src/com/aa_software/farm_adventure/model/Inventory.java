package com.aa_software.farm_adventure.model;


import com.aa_software.farm_adventure.model.selectable.item.AbstractItem;
import com.aa_software.farm_adventure.model.selectable.item.worker.AbstractWorker;
import com.aa_software.farm_adventure.model.selectable.item.worker.DefaultWorker;

/*
 * Inventory: the items that the farm has on hand.
 */
public class Inventory {
	private AbstractItem[] items;
	private AbstractWorker[] workers;

	public Inventory() {
		//TODO: REPLACE THIS!
		workers = new AbstractWorker[4];
		for(int i = 0; i < 4; i++)
			workers[i] = new DefaultWorker();
	}
	
	public int getWorkerCount() {
		return workers.length;
	}

	public Inventory(AbstractItem[] items) {
		this.items = items;
	}

	public AbstractItem[] getItems() {
		return items;
	}

	public void setItems(AbstractItem[] items) {
		this.items = items;
	}

}
