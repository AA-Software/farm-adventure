package com.aa_software.farm_adventure.model.item.tool.plant;

import com.aa_software.farm_adventure.model.Inventory;
import com.aa_software.farm_adventure.model.item.crop.AbstractCrop;
import com.aa_software.farm_adventure.model.item.seed.AbstractSeed;
import com.aa_software.farm_adventure.model.item.tool.AbstractTool;
import com.aa_software.farm_adventure.model.item.worker.AbstractWorker;
import com.aa_software.farm_adventure.model.plot.Plot;
import com.aa_software.farm_adventure.presenter.PlantTask;
import com.badlogic.gdx.utils.Timer;

public abstract class AbstractPlantTool extends AbstractTool {
	protected AbstractSeed seed;
	protected AbstractCrop crop;

	public AbstractSeed getSeed() {
		return this.seed;
	}

	public void setSeed(AbstractSeed seed) {
		// TODO: we'll have to decide if we're making a distinction between
		// produce and seeds. If so, change crop to seed.
		this.seed = seed;
		this.crop = seed.getCrop();
	}
	
	@Override
	public void update(final Plot plot, Inventory inventory) {
		final AbstractWorker worker = inventory.getFreeWorker();
		if(worker == null) {
			return;
		}
		if(!plot.isGrass() && !plot.isUnplowed() && plot.isIrrigated() && 
			!plot.hasCrop() && plot.isUsable() && inventory.removeItem(seed)) {
			worker.setBusy(true);
			plot.setUsable(false);
			float delay = workTime * worker.getWorkRate()/(Plot.WORK_STATUS_TEXTURES.length - 1);
			Timer.schedule(new PlantTask(plot, crop, worker, delay), delay);
			sounds.playClick();
		}
	}
	
	public void update(final Plot plot) {
	}
	
	public String getItemType() {
		return "PLANT TOOLS";
	}
}
