package com.aa_software.farm_adventure.model.item.tool.plant;

import com.aa_software.farm_adventure.model.Inventory;
import com.aa_software.farm_adventure.model.item.crop.AbstractCrop;
import com.aa_software.farm_adventure.model.item.seed.AbstractSeed;
import com.aa_software.farm_adventure.model.item.tool.AbstractTool;
import com.aa_software.farm_adventure.model.item.worker.DefaultWorker;
import com.aa_software.farm_adventure.model.plot.Plot;
import com.aa_software.farm_adventure.presenter.utility.TextureHelper;
import com.badlogic.gdx.utils.Timer;
import com.badlogic.gdx.utils.Timer.Task;

/**
 * Represents the basis of a planting tool.
 * 
 * @author Bebop
 * 
 */
public abstract class AbstractPlantTool extends AbstractTool {

	/**
	 * Runs for a fraction of the total time it takes to plant, then sets
	 * another task to run. The last task completes the planting.
	 * 
	 * @author Bebop
	 * 
	 */
	private class PlantTask extends Task {

		private Plot plot;
		private AbstractSeed seed;
		private DefaultWorker worker;
		private float delay;

		public PlantTask(Plot plot, AbstractSeed seed, DefaultWorker worker,
				float delay) {
			this.plot = plot;
			this.seed = seed;
			this.worker = worker;
			this.delay = delay;
		}

		@Override
		public void run() {
			// TODO "p" is kind of magic numbery and out of place here.
			plot.setTaskTexturePrefix(TextureHelper.getTaskTypeValue("p"
					+ seed.getCrop().getTextureName()));
			if (plot.getTaskTextureIndex() == plot.getWorkStatusTextureLength() - 1) {
				plot.setUsable(true);
				plot.setTaskTextureIndex(0);
				plot.setCrop(seed.getCrop());
				worker.addExperience();
				worker.setBusy(false);
			} else {
				plot.incrementTaskTextureIndex();
				Timer.schedule(this, delay);
			}
		}
	}

	public static final String TYPE = "PLANT TOOLS";

	protected AbstractSeed seed = null;

	protected AbstractCrop crop = null;

	public AbstractPlantTool(int cost, int value, String name,
			String description, float workTime, AbstractTool upgrade) {
		super(cost, value, name, description, workTime, upgrade);
	}

	@Override
	public String getItemType() {
		return TYPE;
	}

	public AbstractSeed getSeed() {
		return this.seed;
	}

	public void setSeed(AbstractSeed seed) {
		this.seed = seed;
		this.crop = seed.getCrop();
	}

	public void update(final Plot plot) {
	}

	/**
	 * Checks whether there is an available worker and if the plot is available
	 * for planting. If so, begins a Plant Task, which will end in a
	 * successfully grown crop waiting to be harvested.
	 * 
	 * @author Bebop
	 * 
	 */
	@SuppressWarnings("static-access")
	@Override
	public void update(final Plot plot, final Inventory inventory) {
		final DefaultWorker worker = (DefaultWorker) inventory.getItems()
				.get(DefaultWorker.TYPE).get(workerIndex);
		if (worker == null || worker.isBusy()) {
			return;
		}

		if (!plot.isGrass() && !plot.isUnplowed() && plot.isIrrigated()
				&& !plot.hasCrop() && plot.isUsable() && this.seed != null
				&& inventory.removeItem(seed)) {
			plot.setTaskTexturePrefix(TextureHelper.getTaskTypeValue("p"
					+ seed.getCrop().getTextureName()));
			worker.setBusy(true);
			plot.setUsable(false);
			float delay = (workTime * worker.getWorkRate() + seed
					.getGrowthTime()) / (plot.getWorkStatusTextureLength() - 1);
			Timer.schedule(new PlantTask(plot, seed, worker, delay), delay);
			SOUNDS.playClick();
			this.seed = null;
			worker.resetTexture();
		}
	}
}
