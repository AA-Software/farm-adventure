package com.aa_software.farm_adventure.model.item.tool.plow;

import com.aa_software.farm_adventure.model.Inventory;
import com.aa_software.farm_adventure.model.item.tool.AbstractTool;
import com.aa_software.farm_adventure.model.item.worker.DefaultWorker;
import com.aa_software.farm_adventure.model.plot.Plot;
import com.aa_software.farm_adventure.model.plot.TaskType;
import com.badlogic.gdx.utils.Timer;
import com.badlogic.gdx.utils.Timer.Task;

public abstract class AbstractPlowTool extends AbstractTool {

	public static final String TYPE = "PLOW TOOLS";

	public AbstractPlowTool(int cost, int value, String name,
			String description, float workTime, AbstractTool upgrade) {
		super(cost, value, name, description, workTime, upgrade);
	}

	@Override
	public String getItemType() {
		return TYPE;
	}

	/**
	 * Checks whether there is an available worker and if the plot is available for plowing. If so, begins a Task, which will end in a successfully plowed plot.
	 * 
	 * @author Bebop
	 *
	 */
	@Override
	public void update(final Plot plot, final Inventory inventory) {
		final DefaultWorker worker;
		if (workerIndex < 0
				|| ((DefaultWorker) inventory.getItems()
						.get(DefaultWorker.TYPE).get(workerIndex)).isBusy()) {
			return;
		} else {
			worker = (DefaultWorker) inventory.getItems()
					.get(DefaultWorker.TYPE).get(workerIndex);
		}
		if (plot.isUsable() && (plot.isGrass() || plot.isUnplowed())) {
			worker.setBusy(true);
			plot.setUsable(false);
			sounds.playClick();
			Timer.schedule(
					new Task() {
						@Override
						public void run() {
							if (plot.isIrrigated()) {
								plot.setTaskTexturePrefix(TaskType.PLOW_W);
							} else {
								plot.setTaskTexturePrefix(TaskType.PLOW_UW);
							}
							if (plot.getTaskTextureIndex() == plot
									.getWorkStatusTextureLength() - 1) {
								plot.setUsable(true);
								if (plot.isIrrigated()) {
									plot.setPlotType(Plot.Type.PLOWEDWATERED);
								} else {
									plot.setPlotType(Plot.Type.PLOWEDUNWATERED);
								}
								plot.setTaskTextureIndex(0);
								worker.addExperience();
								worker.setBusy(false);
							} else {
								plot.incrementTaskTextureIndex();
								Timer.schedule(
										this,
										(workTime * worker.getWorkRate())
												/ (plot.getWorkStatusTextureLength() - 1));
							}
							worker.resetTexture();
						}
					},
					workTime * worker.getWorkRate()
							/ (plot.getWorkStatusTextureLength() - 1));
		}
	}
}
