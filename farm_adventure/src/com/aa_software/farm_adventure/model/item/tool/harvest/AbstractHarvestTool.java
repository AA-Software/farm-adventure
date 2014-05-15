package com.aa_software.farm_adventure.model.item.tool.harvest;

import com.aa_software.farm_adventure.model.Inventory;
import com.aa_software.farm_adventure.model.item.crop.AbstractCrop;
import com.aa_software.farm_adventure.model.item.tool.AbstractTool;
import com.aa_software.farm_adventure.model.item.worker.DefaultWorker;
import com.aa_software.farm_adventure.model.plot.Plot;
import com.aa_software.farm_adventure.presenter.utility.TextureHelper;
import com.badlogic.gdx.utils.Timer;
import com.badlogic.gdx.utils.Timer.Task;

/**
 * Represents the basis of a harvesting tool.
 * 
 * @author Bebop
 * 
 */
public abstract class AbstractHarvestTool extends AbstractTool {

	public final static String TYPE = "HARVEST TOOLS";

	public AbstractHarvestTool(int cost, int value, String name,
			String description, float workTime, AbstractTool upgrade) {
		super(cost, value, name, description, workTime, upgrade);
	}

	@Override
	public String getItemType() {
		return TYPE;
	}

	/**
	 * Checks whether there is an available worker and if the plot is available
	 * for harvesting. If so, begins a Task, which will end in a successfully
	 * harvested crop.
	 * 
	 * @author Bebop
	 * 
	 */
	@SuppressWarnings("static-access")
	@Override
	public void update(final Plot plot, final Inventory inventory) {
		if (plot.isUsable() && plot.hasCrop()) {
			SOUNDS.playClick();
			final DefaultWorker worker = (DefaultWorker) inventory.getItems()
					.get(DefaultWorker.TYPE).get(workerIndex);
			if (worker == null || worker.isBusy()) {
				return;
			}
			worker.setBusy(true);
			plot.setUsable(false);
			// TODO: not the best way to do this, but to get the animation for
			// removing the crops, this
			// had to happen. Need a better solution here!
			AbstractCrop crop = plot.getCrop();
			plot.setTaskTexturePrefix(TextureHelper.getTaskTypeValue("h"
					+ crop.getTextureName()));
			// TODO: We should be adding crops to the inventory AFTER the task
			// is finished.

			inventory.addItem(crop);
			Timer.schedule(
					new Task() {
						@Override
						public void run() {
							if (plot.getTaskTextureIndex() == plot
									.getWorkStatusTextureLength() - 1) {
								plot.setUsable(true);
								plot.setPlotType(Plot.Type.UNPLOWEDWATERED);
								plot.setTaskTextureIndex(0);
								worker.addExperience();
								worker.setBusy(false);
								SOUNDS.playClick();
							} else {
								plot.incrementTaskTextureIndex();
								plot.removeCrop();
								Timer.schedule(
										this,
										(workTime * worker.getWorkRate())
												/ (plot.getWorkStatusTextureLength() - 1));
								worker.resetTexture();
							}
						}
					},
					(workTime * worker.getWorkRate())
							/ (plot.getWorkStatusTextureLength() - 1));
		}
	}
}
