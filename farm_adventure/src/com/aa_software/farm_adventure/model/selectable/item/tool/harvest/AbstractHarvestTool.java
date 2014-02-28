package com.aa_software.farm_adventure.model.selectable.item.tool.harvest;

import com.aa_software.farm_adventure.model.Inventory;
import com.aa_software.farm_adventure.model.selectable.item.crop.AbstractCrop;
import com.aa_software.farm_adventure.model.selectable.item.tool.AbstractTool;
import com.aa_software.farm_adventure.model.selectable.plot.Plot;
import com.aa_software.farm_adventure.model.selectable.plot.PlotType;

public class AbstractHarvestTool extends AbstractTool {
	@Override
	public void update(Plot plot) {
		AbstractCrop crop = plot.getCrop();
		if (crop != null) {
			// TODO: how to add the crop to inventory?
			plot.setCrop(null);
			plot.setPlotType(PlotType.UNPLOWEDWATERED);
		}
	}
	
	/**
	 * Removes a crop from the plot iff it currently has a crop and moves
	 * the crop to the inventory
	 */
	public void update(Plot plot, Inventory inventory) {
		AbstractCrop crop = plot.getCrop();
		if (crop != null) {
			// TODO: how to add the crop to inventory?
			plot.setCrop(null);
			plot.setPlotType(PlotType.UNPLOWEDWATERED);
			inventory.addItem(crop);
		}
	}
	
	public String getTextureName() {
		return "arbitrary";
	}
	
	public String getItemType() {
		return "HARVEST TOOL";
	}	
	
}
