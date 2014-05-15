package com.aa_software.farm_adventure.model.item.tool.irrigate;

import com.aa_software.farm_adventure.model.Inventory;
import com.aa_software.farm_adventure.model.item.tool.AbstractTool;
import com.aa_software.farm_adventure.model.item.worker.DefaultWorker;
import com.aa_software.farm_adventure.model.plot.Irrigation;
import com.aa_software.farm_adventure.model.plot.Plot;
import com.aa_software.farm_adventure.model.plot.TaskType;
import com.badlogic.gdx.utils.Timer;
import com.badlogic.gdx.utils.Timer.Task;

/**
 * Represents the basis of an irrigation tool.
 * 
 * @author Bebop
 * 
 */
public abstract class AbstractIrrigationTool extends AbstractTool {

	/**
	 * Runs for a fraction of the total time it takes to irrigate, then sets
	 * another task to run. The last task completes the irrigation.
	 * 
	 * @author Bebop
	 * 
	 */
	private class IrrigationTask extends Task {

		private Irrigation irrigationChoice;
		private TaskType task;
		private Plot plot;
		private DefaultWorker worker;
		private float delay;

		public IrrigationTask(Plot plot, Irrigation irrigationChoice,
				TaskType task, DefaultWorker worker, float delay) {
			this.irrigationChoice = irrigationChoice;
			this.task = task;
			this.plot = plot;
			this.worker = worker;
			this.delay = delay;
		}

		@Override
		public void run() {
			plot.setTaskTexturePrefix(task);
			if (plot.getTaskTextureIndex() == plot.getWorkStatusTextureLength() - 1) {
				plot.setUsable(true);
				switch (plot.getPlotType()) {
				case PLOWEDUNWATERED:
					plot.setPlotType(Plot.Type.PLOWEDWATERED);
					break;
				case UNPLOWEDUNWATERED:
					plot.setPlotType(Plot.Type.UNPLOWEDWATERED);
					break;
				default:
					break;
				}
				plot.addIrrigation(irrigationChoice);
				plot.setTaskTextureIndex(0);
				worker.addExperience();
				worker.setBusy(false);
			} else {
				plot.incrementTaskTextureIndex();
				Timer.schedule(this, delay);
			}
		}
	}

	private Irrigation irrigationChoice;
	private TaskType task;
	public final static String TYPE = "IRRIGATION TOOLS";

	public AbstractIrrigationTool(int cost, int value, String name,
			String description, float workTime, AbstractTool upgrade) {
		super(cost, value, name, description, workTime, upgrade);
	}

	public Irrigation getIrrigationChoice() {
		return irrigationChoice;
	}

	public TaskType getIrrTaskType() {
		return task;
	}

	@Override
	public String getItemType() {
		return TYPE;
	}

	public void setIrrigationChoice(Irrigation irrigationChoice) {
		this.irrigationChoice = irrigationChoice;
	}

	public void setTaskType(TaskType task) {
		this.task = task;
	}

	/**
	 * Checks whether there is an available worker and if the plot is available
	 * for irrigation. If so, begins an Irrigation Task, which will end in a
	 * successfully irrigated plot.
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
		if (plot.isUsable()) {
			worker.setBusy(true);
			plot.setUsable(false);
			plot.setTaskTexturePrefix(task);
			float delay = workTime * worker.getWorkRate()
					/ (plot.getWorkStatusTextureLength() - 1);
			Timer.schedule(new IrrigationTask(plot, irrigationChoice, task,
					worker, delay), delay);
			worker.resetTexture();
		}
	}

}
