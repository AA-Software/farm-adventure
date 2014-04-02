package com.aa_software.farm_adventure.presenter.screen.farm_screen;

import java.util.EnumSet;
import java.util.Iterator;

import com.aa_software.farm_adventure.model.farm.TutorialFarm;
import com.aa_software.farm_adventure.model.item.crop.BananaCrop;
import com.aa_software.farm_adventure.model.item.crop.BeetCrop;
import com.aa_software.farm_adventure.model.item.crop.CarrotCrop;
import com.aa_software.farm_adventure.model.item.crop.RiceCrop;
import com.aa_software.farm_adventure.model.item.tool.harvest.AbstractHarvestTool;
import com.aa_software.farm_adventure.model.item.tool.irrigate.AbstractIrrigationTool;
import com.aa_software.farm_adventure.model.item.tool.plant.AbstractPlantTool;
import com.aa_software.farm_adventure.model.item.tool.plow.AbstractPlowTool;
import com.aa_software.farm_adventure.model.plot.Irrigation;
import com.aa_software.farm_adventure.model.plot.Plot;
import com.aa_software.farm_adventure.presenter.FarmAdventure;
import com.aa_software.farm_adventure.presenter.IrrigationListener;
import com.aa_software.farm_adventure.presenter.TextureHelper;
import com.aa_software.farm_adventure.presenter.screen.WorldScreen;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.Window;
import com.badlogic.gdx.scenes.scene2d.ui.Label.LabelStyle;

public class TutorialFarmScreen extends AbstractFarmScreen {

	//TODO We need a way to enforce users to plow/irrigate such that they will be able to move
	// forward in the tutorial (or some other way to handle the problem). Basically, if they
	// get to the "plant a crop" part, and do not have anywhere to plant a crop because they
	// plowed a random piece of land, they are stuck.
	
	enum State {
		DESCRIBE_OBJECTIVE, DESCRIBE_FIELD, DESCRIBE_STATUS_BAR, DESCRIBE_TOOL_BAR, DESCRIBE_PLOW, CLICK_PLOW, CLICK_PLOW_PLOT, WAIT_PLOW_PLOT, DESCRIBE_IRRIGATE, CLICK_IRRIGATE, CLICK_IRRIGATE_PLOT, CLICK_IRRIGATE_MENU, WAIT_IRRIGATE_PLOT, DESCRIBE_PLANT, CLICK_PLANT, CLICK_CLICK_PLANT, CLICK_PLANT_MENU, CLICK_PLANT_PLOT, WAIT_PLANT_PLOT, DESCRIBE_HARVEST, CLICK_HARVEST, CLICK_HARVEST_PLOT, WAIT_HARVEST_PLOT, DESCRIBE_INVENTORY, CLICK_INVENTORY, DESCRIBE_INVENTORY_SCREEN, DESCRIBE_QUANTITY, DESCRIBE_BUY_AND_SELL, DESCRIBE_END, END
	}

	/* Font setup */
	final LabelStyle style2 = new LabelStyle(fontType, Color.WHITE);

	private String description;
	private Stage descriptionStage;
	private Window descriptionWindow;
	private float descriptionX, descriptionY;
	private State[] states;
	private int stateIndex;
	private boolean foundClick;
	private int waitingForX;

	/**
	 * Constructs a farm screen using the specifications of TutorialFarm.
	 */
	public TutorialFarmScreen() {
		super();
		farm = new TutorialFarm();

		states = State.values();

		descriptionStage = new Stage(Gdx.graphics.getWidth(),
				Gdx.graphics.getHeight(), true);

		descriptionWindow = new Window("Tutorial Guide:", skin);
		descriptionWindow.setModal(false);
		descriptionWindow.setMovable(false);
		descriptionWindow.defaults().spaceBottom(10);
		descriptionWindow.row().fill().expandX();
		descriptionWindow.setVisible(true);

		descriptionStage.addActor(descriptionWindow);
		getDescription();
		updateDescription();
		disableAllGameClicks();
		disableGameTime = true;
		foundClick = true;
	}

	@Override
	public void resize(int width, int height) {
		super.resize(width, height);
		descriptionStage.setViewport(width, height);
	}

	/**
	 * Acts as our "game loop". Checks for touches (so that they can be
	 * handled), syncs each layer's tiles with their respective model pieces,
	 * and finally updates and draws the stage for the status bar. However, if
	 * the game is over, this method will call for a disposal of the screen.
	 * 
	 * Additionally takes care of the tutorial state.
	 * 
	 */
	@Override
	public void render(float delta) {
		super.render(delta);
		descriptionStage.draw();

		boolean allGameClicksAreDisabled = fieldClicksDisabled
				&& toolBarClicksDisabled && irrigationMenuClicksDisabled
				&& plantMenuClicksDisabled;
		if (allGameClicksAreDisabled) {
			Gdx.input.setInputProcessor(descriptionStage);
		}

		if (states[stateIndex].toString().toLowerCase().contains("click")
				&& foundClick) {
			transitionState();
		}
	}

	public void getDescription() {
		switch (states[stateIndex]) {
		case DESCRIBE_OBJECTIVE:
			description = "Welcome to your first farm!\nYour objective is to make the most\nmoney before time runs out.";
			descriptionX = (float) (Gdx.graphics.getWidth() * .27);
			descriptionY = (float) (Gdx.graphics.getHeight() * .7);
			break;
		case DESCRIBE_FIELD:
			description = "This is your field with plots and water.";
			descriptionX = (float) (Gdx.graphics.getWidth() * .25);
			descriptionY = (float) (Gdx.graphics.getHeight() * .7);
			break;
		case DESCRIBE_STATUS_BAR:
			description = "This is the status bar which displays\nyour money, time left, and workers.";
			descriptionX = (float) (Gdx.graphics.getWidth() * .25);
			descriptionY = (float) (Gdx.graphics.getHeight() * .25);
			break;
		case DESCRIBE_TOOL_BAR:
			description = "This is the tool bar which allows\nyou to perform actions on the farm.";
			descriptionX = (float) (Gdx.graphics.getWidth() * .25);
			descriptionY = (float) (Gdx.graphics.getHeight() * .13);
			break;
		case DESCRIBE_PLOW:
			description = "This is the plow\ntool which plows \n a plot of land.";
			descriptionX = (float) (Gdx.graphics.getWidth() * 0);
			descriptionY = (float) (Gdx.graphics.getHeight() * .13);
			break;
		case CLICK_PLOW:
			description = "Click the plow tool.";
			toolBarClicksDisabled = false;
			foundClick = false;
			waitingForX = 0;
			break;
		case CLICK_PLOW_PLOT:
			// TODO: make sure that the plot actually is plowable.
			description = "Now click a plot to plow it.";
			descriptionX = (float) (Gdx.graphics.getWidth() * .35);
			descriptionY = (float) (Gdx.graphics.getHeight() * .7);
			foundClick = false;
			fieldClicksDisabled = false;
			break;
		case WAIT_PLOW_PLOT:
			description = "One of your workers has taken up the task.";
			descriptionX = (float) (Gdx.graphics.getWidth() * .25);
			break;
		case DESCRIBE_IRRIGATE:
			description = "This is the irrigation\ntool which allows you\nto irrigate your plots.";
			descriptionX = (float) (Gdx.graphics.getWidth() * .15);
			descriptionY = (float) (Gdx.graphics.getHeight() * .13);
			break;
		case CLICK_IRRIGATE:
			description = "Click the irrigation tool.";
			descriptionX = (float) (Gdx.graphics.getWidth() * .18);
			descriptionY = (float) (Gdx.graphics.getHeight() * .13);
			toolBarClicksDisabled = false;
			foundClick = false;
			waitingForX = 1;
			break;
		case CLICK_IRRIGATE_PLOT:
			// TODO: Make sure that it only goes forward if you click a plot
			// open to irrigation!
			description = "Now click a plot to irrigate it.";
			descriptionX = (float) (Gdx.graphics.getWidth() * .35);
			descriptionY = (float) (Gdx.graphics.getHeight() * .7);
			foundClick = false;
			irrigationMenuClicksDisabled = false;
			fieldClicksDisabled = false;
			break;
		case CLICK_IRRIGATE_MENU:
			description = "Now choose a side to irrigate.";
			descriptionX = (float) (Gdx.graphics.getWidth() * .35);
			descriptionY = (float) (Gdx.graphics.getHeight() * .7);
			foundClick = false;
			irrigationMenuClicksDisabled = false;
			break;
		case WAIT_IRRIGATE_PLOT:
			description = "One of your workers has taken up the task!\nIf your plot is already beside water, no change\nto the plot's color is made.";
			descriptionX = (float) (Gdx.graphics.getWidth() * .25);
			break;
		case DESCRIBE_PLANT:
			description = "This is the planting\ntool which plants seeds.";
			descriptionX = (float) (Gdx.graphics.getWidth() * .35);
			descriptionY = (float) (Gdx.graphics.getHeight() * .13);
			break;
		case CLICK_PLANT:
			description = "Click the plant tool.";
			descriptionX = (float) (Gdx.graphics.getWidth() * .40);
			descriptionY = (float) (Gdx.graphics.getHeight() * .13);
			toolBarClicksDisabled = false;
			foundClick = false;
			waitingForX = 2;
			break;
		case CLICK_CLICK_PLANT:
			description = "Click the plant tool\nagain to choose a plant.";
			descriptionX = (float) (Gdx.graphics.getWidth() * .37);
			descriptionY = (float) (Gdx.graphics.getHeight() * .13);
			plantMenuClicksDisabled = false;
			toolBarClicksDisabled = false;
			foundClick = false;
			waitingForX = 2;
			break;
		case CLICK_PLANT_MENU:
			// TODO: the menu shouldn'y dissapear if you click elsewhere
			description = "Now choose a type of seed.";
			descriptionX = (float) (Gdx.graphics.getWidth() * .35);
			descriptionY = (float) (Gdx.graphics.getHeight() * .7);
			foundClick = false;
			plantMenuClicksDisabled = false;
			break;
		case CLICK_PLANT_PLOT:
			// should make sure the plot clicked is available for plants!
			description = "Now choose a plot to plant on.";
			descriptionX = (float) (Gdx.graphics.getWidth() * .35);
			descriptionY = (float) (Gdx.graphics.getHeight() * .7);
			foundClick = false;
			fieldClicksDisabled = false;
			break;
		case WAIT_PLANT_PLOT:
			description = "One of your workers has taken up the task.";
			descriptionX = (float) (Gdx.graphics.getWidth() * .25);
			break;
		case DESCRIBE_HARVEST:
			description = "This is the harvesting\ntool which will harvest\ngrown plants.";
			descriptionX = (float) (Gdx.graphics.getWidth() * .55);
			descriptionY = (float) (Gdx.graphics.getHeight() * .13);
			break;
		case CLICK_HARVEST:
			description = "Click the harvest tool.";
			descriptionX = (float) (Gdx.graphics.getWidth() * .60);
			descriptionY = (float) (Gdx.graphics.getHeight() * .13);
			toolBarClicksDisabled = false;
			foundClick = false;
			waitingForX = 3;
			break;
		case CLICK_HARVEST_PLOT:
			// TODO: should make sure the harvested plot had a plant!
			description = "Now click a plot to harvest it.";
			descriptionX = (float) (Gdx.graphics.getWidth() * .35);
			descriptionY = (float) (Gdx.graphics.getHeight() * .7);
			foundClick = false;
			irrigationMenuClicksDisabled = false;
			fieldClicksDisabled = false;
			break;
		case WAIT_HARVEST_PLOT:
			description = "One of your workers has taken up the task.";
			descriptionX = (float) (Gdx.graphics.getWidth() * .25);
			break;
		case DESCRIBE_INVENTORY:
			description = "This is inventory\nand market button.";
			descriptionX = (float) (Gdx.graphics.getWidth() * .85);
			descriptionY = (float) (Gdx.graphics.getHeight() * .13);
			break;
		case CLICK_INVENTORY:
			// TODO: stopped here. market isn't in this build
			description = "Click the inventory\nand market button.";
			descriptionX = (float) (Gdx.graphics.getWidth() * .85);
			descriptionY = (float) (Gdx.graphics.getHeight() * .13);
			toolBarClicksDisabled = false;
			inventoryClicksDisabled = false;
			foundClick = false;
			waitingForX = 4;
			break;
		case DESCRIBE_INVENTORY_SCREEN:
			description = "This is your inventory and market screen.";
			descriptionX = (float) (Gdx.graphics.getWidth() * .25);
			descriptionY = (float) (Gdx.graphics.getHeight() * .7);
			break;
		case DESCRIBE_QUANTITY:
			description = "This is the quantity\nthat you own of\na certain item.";
			descriptionX = (float) (Gdx.graphics.getWidth() * .25);
			descriptionY = (float) (Gdx.graphics.getHeight() * .95);
			break;
		case DESCRIBE_BUY_AND_SELL:
			description = "These are the buy and sell items that\nallow you to purchase and sell items.";
			descriptionX = (float) (Gdx.graphics.getWidth() * .60);
			descriptionY = (float) (Gdx.graphics.getHeight() * .95);
			break;
		case DESCRIBE_END:
			description = "Good job! Now try out a real farm.";
			descriptionX = (float) (Gdx.graphics.getWidth() * .30);
			descriptionY = (float) (Gdx.graphics.getHeight() * .7);
			break;
		case END:
			gameOver = true;
			break;
		default:
			description = "Ran out of states!";
			descriptionX = (float) (Gdx.graphics.getWidth() * .25);
			descriptionY = (float) (Gdx.graphics.getHeight() * .8);
			break;
		}
	}

	public void updateDescription() {
		Label description = new Label(this.description, style2);
		descriptionWindow.add(description);

		if (!states[stateIndex].toString().toLowerCase().contains("click")) {
			TextButton nextButton = new TextButton("Next!", skin);
			nextButton.addListener(new InputListener() {
				public boolean touchDown(InputEvent event, float x, float y,
						int pointer, int button) {
					transitionState();
					return true;
				}
			});
			descriptionWindow.add(nextButton);
		}

		descriptionWindow.pack();

		descriptionWindow.setPosition(descriptionX, descriptionY);
	}

	public void endTutorial() {
		map.dispose();
		renderer.dispose();
		FarmAdventure.getInstance().setScreen(new WorldScreen());
	}

	public void transitionState() {
		descriptionWindow.clear();
		disableAllGameClicks();
		if (!(stateIndex + 1 > states.length)) {
			stateIndex++;
		} else {
			endTutorial();
		}
		getDescription();
		updateDescription();
	}

	/**
	 * Takes in an x, and y value (cell-based) that represents user input, as
	 * well as the type of cell that was clicked. The options for this is ground
	 * or tool bar. If the user clicked on the tool bar, that tool is selected
	 * and the state updates. If a plot is clicked, the tool bar that was
	 * previously selected will affect the status of the plot and the state will
	 * update.
	 * 
	 * @param x
	 * @param y
	 * @param property
	 */
	@Override
	public void updateState(int x, int y) {
		if (!foundClick) {
			if (y >= FIELD_STARTING_Y && !fieldClicksDisabled) {
				Plot plot = farm.getPlot(x, y - FIELD_STARTING_Y);
				if (plot.isUsable()) {
					boolean harvested = selection instanceof AbstractHarvestTool
							&& plot.hasCrop();
					boolean irrigated = selection instanceof AbstractIrrigationTool
							&& ((AbstractIrrigationTool) selection)
									.getIrrigationChoice() != null;
					boolean planted = selection instanceof AbstractPlantTool
							&& !plot.isGrass() && !plot.isUnplowed()
							&& plot.isIrrigated() && !plot.hasCrop();
					boolean plowed = selection instanceof AbstractPlowTool
							&& (plot.isGrass() || plot.isUnplowed());
					if (harvested || irrigated || planted || plowed) {
						foundClick = true;
					}
					super.updateState(x, y);
				}
			} else if (y == 0 && !toolBarClicksDisabled) {
				super.updateState(x, y);
				if (selection != null && selection.equals(farm.getTool(waitingForX, y))) {
					foundClick = true;
				}
				//THIS SHOULD BE TAKEN OUT LATER - IT IS JUST A QUICK FIX
				else if (selection == null) {
					foundClick = true;
				}
			}
		} else {
			super.updateState(x, y);
		}
	}

	/**
	 * Updates the window for selecting irrigation.
	 * 
	 * @param x
	 *            the x coordinate of the selected plot
	 * @param y
	 *            the y coordinate of the selected plot
	 */
	@Override
	public void updateIrrigationWindow(final int x, final int y) {
		irrigationWindow.clear();

		if (states[stateIndex] == State.CLICK_IRRIGATE_PLOT) {
			foundClick = true;
		}

		Iterator<Irrigation> iterator = farm.getIrrigationChoices(x, y)
				.iterator();
		for (; iterator.hasNext();) {
			Irrigation irrigation = iterator.next();
			Texture irrigationTexture = new Texture(
					Gdx.files.internal(TextureHelper
							.getIrrigationTextureFileName(EnumSet
									.of(irrigation))));
			TextureRegion irrigationImage = new TextureRegion(irrigationTexture);
			Button irrigationButton = new Button(new Image(irrigationImage),
					skin);
			/*
			 * creates an input listener that additionally has the fields for
			 * the selected X and Y. This way, when the listener is called, it
			 * will know which X and Y it pertains to.
			 */
			irrigationButton.addListener(new IrrigationListener(x, y,
					irrigation) {
				public boolean touchDown(InputEvent event, float x, float y,
						int pointer, int button) {
					if (selection instanceof AbstractIrrigationTool) {
						((AbstractIrrigationTool) selection)
								.setIrrigationChoice(this.getIrrigation());
						state = state.update(
								farm.getPlot(this.getX(), this.getY()),
								farm.getInventory());
					}
					if (states[stateIndex] == State.CLICK_IRRIGATE_MENU) {
						foundClick = true;
					}
					irrigationWindow.setVisible(false);
					return true;
				}
			});
			irrigationWindow.add(irrigationButton);
		}

		irrigationWindow.pack();
	}

	/**
	 * Sets up the window to choose a seed to plant
	 */
	@Override
	public void updatePlantWindow() {
		plantWindow.clear();
		boolean inventoryHasCarrot = true;
		boolean inventoryHasBeet = true;
		boolean inventoryHasRice = true;
		boolean inventoryHasBanana = true;

		if (inventoryHasCarrot) {
			Texture carrot = new Texture(
					Gdx.files.internal("textures/carrotCrop.png"));
			TextureRegion carrotImage = new TextureRegion(carrot);
			Button carrotButton = new Button(new Image(carrotImage), skin);
			plantWindow.add(carrotButton);
			carrotButton.addListener(new InputListener() {
				public boolean touchDown(InputEvent event, float x, float y,
						int pointer, int button) {
					((AbstractPlantTool) farm.getTool(PLANT_TOOL_X,
							PLANT_TOOL_Y)).setSeed(new CarrotCrop());
					if (states[stateIndex] == State.CLICK_PLANT_MENU) {
						foundClick = true;
					}
					plantWindow.setVisible(false);
					return true;
				}
			});
		}

		if (inventoryHasBeet) {
			Texture beet = new Texture(Gdx.files.internal("textures/beetCrop.png"));
			TextureRegion beetImage = new TextureRegion(beet);
			Button beetButton = new Button(new Image(beetImage), skin);
			plantWindow.add(beetButton);
			beetButton.addListener(new InputListener() {
				public boolean touchDown(InputEvent event, float x, float y,
						int pointer, int button) {
					((AbstractPlantTool) farm.getTool(PLANT_TOOL_X,
							PLANT_TOOL_Y)).setSeed(new BeetCrop());
					if (states[stateIndex] == State.CLICK_PLANT_MENU) {
						foundClick = true;
					}
					plantWindow.setVisible(false);
					return true;
				}
			});
		}

		if (inventoryHasRice) {
			Texture rice = new Texture(Gdx.files.internal("textures/riceCrop.png"));
			TextureRegion riceImage = new TextureRegion(rice);
			Button riceButton = new Button(new Image(riceImage), skin);
			plantWindow.add(riceButton);
			riceButton.addListener(new InputListener() {
				public boolean touchDown(InputEvent event, float x, float y,
						int pointer, int button) {
					((AbstractPlantTool) farm.getTool(PLANT_TOOL_X,
							PLANT_TOOL_Y)).setSeed(new RiceCrop());
					if (states[stateIndex] == State.CLICK_PLANT_MENU) {
						foundClick = true;
					}
					plantWindow.setVisible(false);
					return true;
				}
			});
		}

		if (inventoryHasBanana) {
			Texture banana = new Texture(
					Gdx.files.internal("textures/bananaCrop.png"));
			TextureRegion bananaImage = new TextureRegion(banana);
			Button bananaButton = new Button(new Image(bananaImage), skin);
			plantWindow.add(bananaButton);
			bananaButton.addListener(new InputListener() {
				public boolean touchDown(InputEvent event, float x, float y,
						int pointer, int button) {
					((AbstractPlantTool) farm.getTool(PLANT_TOOL_X,
							PLANT_TOOL_Y)).setSeed(new BananaCrop());
					if (states[stateIndex] == State.CLICK_PLANT_MENU) {
						foundClick = true;
					}
					plantWindow.setVisible(false);
					return true;
				}
			});
		}

		plantWindow.pack();
	}
}