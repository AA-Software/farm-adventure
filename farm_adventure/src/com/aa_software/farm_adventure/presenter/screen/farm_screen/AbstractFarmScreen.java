package com.aa_software.farm_adventure.presenter.screen.farm_screen;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.EnumSet;


import java.util.concurrent.TimeUnit;

import com.aa_software.farm_adventure.model.Field;
import com.aa_software.farm_adventure.model.Player;
import com.aa_software.farm_adventure.model.ToolBar;
import com.aa_software.farm_adventure.model.farm.AbstractFarm;
import com.aa_software.farm_adventure.model.farm.SnowFarm;

import com.aa_software.farm_adventure.model.item.AbstractItem;
import com.aa_software.farm_adventure.model.item.crop.BananaCrop;
import com.aa_software.farm_adventure.model.item.crop.BeetCrop;
import com.aa_software.farm_adventure.model.item.crop.CarrotCrop;
import com.aa_software.farm_adventure.model.item.crop.RiceCrop;
import com.aa_software.farm_adventure.model.item.tool.AbstractTool;
import com.aa_software.farm_adventure.model.item.tool.irrigate.AbstractIrrigationTool;
import com.aa_software.farm_adventure.model.item.tool.plant.AbstractPlantTool;
import com.aa_software.farm_adventure.model.plot.Irrigation;

import com.aa_software.farm_adventure.presenter.FarmAdventure;
import com.aa_software.farm_adventure.presenter.IrrigationListener;
import com.aa_software.farm_adventure.presenter.TextureHelper;
import com.aa_software.farm_adventure.presenter.screen.AbstractScreen;
import com.aa_software.farm_adventure.presenter.screen.MainMenuScreen;
import com.aa_software.farm_adventure.presenter.state.DefaultSelectionState;
import com.aa_software.farm_adventure.presenter.state.ISelectionState;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL10;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapTile;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer.Cell;
import com.badlogic.gdx.maps.tiled.TiledMapTileSet;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Label.LabelStyle;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.Window;
import com.badlogic.gdx.scenes.scene2d.utils.Align;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;

public abstract class AbstractFarmScreen extends AbstractScreen {

	/* Game */
	public static final long GAME_TIME_MILLIS = 120000;
	long gameStartTime;

	/* Player */
	public static final Player PLAYER = Player.getInstance();

	/* Tile */
	public static final String TILE_MAP_NAME = "tilemap/tileMap128.tmx";
	public static final String TILE_SET_NAME = "tileSet128";
	private static final int TILE_SIZE = 128;

	public static final String SKIN_JSON_UI = "skin/uiskin.json";

	/* Layer */
	/*
	 * For each layer, please provide a method of syncing the model with the
	 * presenter
	 */
	private final String[] allLayers = { "ground", "water", "plant", "tool",
			"seed", "status", "select", "transparent" };
	public static final int PLANT_TOOL_X = 2, PLANT_TOOL_Y = 0, IRRIGATION_TOOL_X = 1,
			IRRIGATION_TOOL_Y = 0, STATUS_BAR_Y = 1, FIELD_STARTING_Y = 2;

	/* Stage */
	public static final float FONT_SCALE = (float) (Gdx.graphics.getHeight() * .0001);
	public static final float BANK_LABEL_X = (float) (Gdx.graphics.getWidth() * .03),
			BANK_LABEL_Y = (float) (Gdx.graphics.getHeight() * .18),
			TIME_LABEL_X = (float) (Gdx.graphics.getWidth() * .38),
			TIME_LABEL_Y = (float) (Gdx.graphics.getHeight() * .18),
			WORKER_LABEL_X = (float) (Gdx.graphics.getWidth() * .78),
			WORKER_LABEL_Y = (float) (Gdx.graphics.getHeight() * .18),
			WINDOW_X = (float) (Gdx.graphics.getWidth() * .25),
			WINDOW_Y = (float) (Gdx.graphics.getHeight() * .13),
			INVENTORY_HEIGHT = Gdx.graphics.getHeight()-15*(TILE_SIZE/10);

	protected AbstractItem selection;
	protected ISelectionState state;
	protected AbstractFarm farm;


	protected TiledMap map;
	protected TiledMapTileSet tileSet;
	protected HashMap<String, Integer> tileMap;
	
	protected InputMultiplexer inputMultiplexer;
	
	protected Skin skin;
	protected Stage plantMenuStage;
	protected Window plantWindow;
	
	protected Skin inventory_market_Skin;
	protected Stage inventory_market_Stage;
	protected Stage inventoryStage;
	protected Stage marketStage;
	protected Window inventory_market_Window;
	
	protected Table invScrollTable;
	
	protected Boolean inventoryMarketVisible;

	protected final Stage irrigationMenuStage;
	protected final Stage statusBarStage;
	protected final Window irrigationWindow;


	protected boolean gameOver;
	
	protected enum Actions {BUY, SELL}

	public AbstractFarmScreen() {
		gameOver = false;
		this.selection = null;
		this.state = new DefaultSelectionState();

		map = new TmxMapLoader().load(TILE_MAP_NAME);
		tileSet = map.getTileSets().getTileSet(TILE_SET_NAME);
		/* Push all of the tiles for each layer into the tile map */
		Iterator<TiledMapTile> tileIterator;
		tileMap = new HashMap<String, Integer>();
		for (int i = 0; i < allLayers.length; i++) {
			for (tileIterator = tileSet.iterator(); tileIterator.hasNext();) {
				TiledMapTile tile = tileIterator.next();
				tileMap.put(tile.getProperties()
						.get(allLayers[i], String.class), tile.getId());
			}
		}
		
		skin = new Skin(Gdx.files.internal(SKIN_JSON_UI));
		plantMenuStage = new Stage(Gdx.graphics.getWidth(),
				Gdx.graphics.getHeight(), true);
		irrigationMenuStage = new Stage(Gdx.graphics.getWidth(),
				Gdx.graphics.getHeight(), true);
		statusBarStage = new Stage(Gdx.graphics.getWidth(),
				Gdx.graphics.getHeight(), true);
		inventory_market_Stage = new Stage(Gdx.graphics.getWidth(),
				Gdx.graphics.getHeight(), true);
		
		irrigationWindow = new Window("Pick a Side to Irrigate", skin);
		irrigationWindow.setModal(false);
		irrigationWindow.setMovable(false);
		irrigationWindow.setVisible(false);
		irrigationWindow.setPosition(WINDOW_X, WINDOW_Y);
		irrigationWindow.defaults().spaceBottom(10);
		irrigationWindow.row().fill().expandX();
	}

	/**
	 * Uses the libgdx library to get the x, y location of a users touch if
	 * there was one. This value is used to check if the user clicked over the
	 * plots or over the tool bar. If the user clicks the status bar, there is no
	 * change. If it is a plot, the x , y , and ground string are used to update
	 * the state. If the tool bar was clicked, the x, y, and tool bar string are
	 * used to update the state.
	 */
	public void checkTouch() {
		if (Gdx.input.justTouched()) {
			Vector3 touchPos = new Vector3();
			touchPos.set(Gdx.input.getX(), Gdx.input.getY(), 0);
			camera.unproject(touchPos);
			int xCell = (int) (touchPos.x / TILE_SIZE);
			int yCell = (int) (touchPos.y / TILE_SIZE);
			plantWindow.setVisible(false);
			irrigationWindow.setVisible(false);
			updateState(xCell, yCell);
		}
	}

	/**
	 * This method disposes of our left over libGDX elements, updates the
	 * player's score, and returns the player to the main menu.
	 */
	@Override
	public void dispose() {
		// TODO: score evaluation for player bankroll
		PLAYER.setBankroll(PLAYER.getBankroll() + 100);
		map.dispose();
		renderer.dispose();
		FarmAdventure.getInstance().setScreen(new MainMenuScreen());
	}

	@Override
	public void hide() {

	}

	@Override
	public void pause() {

	}

	/**
	 * This method acts as our "game loop". Checks for touches (so that they can
	 * be handled), syncs each layer's tiles with their respective model pieces,
	 * and finally updates and draws the stage for the status bar. However, if
	 * the game is over, this method will call for a disposal of the screen.
	 * 
	 */
	@Override
	public void render(float delta) {
		if (gameOver) {
			dispose();
		} else {
			/* Draw the base map to the screen */
			Gdx.gl.glClearColor(0, 0, 0.2f, 1);
			Gdx.gl.glClear(GL10.GL_COLOR_BUFFER_BIT);

			checkTouch();
			farm.getField().syncAllIrrigation();
			syncWaterTiles();
			syncPlantTiles();
			syncGroundTiles();
			syncToolTiles();
			syncStatusTiles();
			syncSeedTile();
			camera.update();
			renderer.setView(camera);
			renderer.render();

			updateStatusBar();
			statusBarStage.draw();
			plantMenuStage.draw();

			inventory_market_Stage.draw();
			inventory_market_Stage.act();

			irrigationMenuStage.draw();

		}
	}

	@Override
	public void resize(int width, int height) {
	}

	@Override
	public void resume() {

	}

	/**
	 * Sets up the window to choose a seed to plant
	 */
	public final void setupPlantWindow() {

		Texture carrot = new Texture(Gdx.files.internal("textures/carrotCrop.png"));
		Texture beet = new Texture(Gdx.files.internal("textures/beetCrop.png"));
		Texture rice = new Texture(Gdx.files.internal("textures/riceCrop.png"));
		Texture banana = new Texture(Gdx.files.internal("textures/bananaCrop.png"));

		TextureRegion carrotImage = new TextureRegion(carrot);
		TextureRegion beetImage = new TextureRegion(beet);
		TextureRegion riceImage = new TextureRegion(rice);
		TextureRegion bananaImage = new TextureRegion(banana);

		Button carrotButton = new Button(new Image(carrotImage), skin);
		Button beetButton = new Button(new Image(beetImage), skin);
		Button riceButton = new Button(new Image(riceImage), skin);
		Button bananaButton = new Button(new Image(bananaImage), skin);

		plantWindow = new Window("Pick a Type of Seed", skin);
		plantWindow.setModal(false);
		plantWindow.setMovable(false);
		plantWindow.setVisible(false);
		plantWindow.setPosition(WINDOW_X, WINDOW_Y);
		plantWindow.defaults().spaceBottom(10);
		plantWindow.row().fill().expandX();
		plantWindow.add(carrotButton);
		plantWindow.add(beetButton);
		plantWindow.add(riceButton);
		plantWindow.add(bananaButton);
		plantWindow.pack();
		plantMenuStage.addActor(plantWindow);

		carrotButton.addListener(new InputListener() {
			public boolean touchDown(InputEvent event, float x, float y,
					int pointer, int button) {
				((AbstractPlantTool) farm.getTool(PLANT_TOOL_X, PLANT_TOOL_Y))
						.setSeed(new CarrotCrop());
				return true;
			}
		});

		beetButton.addListener(new InputListener() {
			public boolean touchDown(InputEvent event, float x, float y,
					int pointer, int button) {
				((AbstractPlantTool) farm.getTool(PLANT_TOOL_X, PLANT_TOOL_Y))
						.setSeed(new BeetCrop());
				return true;
			}
		});

		riceButton.addListener(new InputListener() {
			public boolean touchDown(InputEvent event, float x, float y,
					int pointer, int button) {
				((AbstractPlantTool) farm.getTool(PLANT_TOOL_X, PLANT_TOOL_Y))
						.setSeed(new RiceCrop());
				return true;
			}
		});

		bananaButton.addListener(new InputListener() {
			public boolean touchDown(InputEvent event, float x, float y,
					int pointer, int button) {
				((AbstractPlantTool) farm.getTool(PLANT_TOOL_X, PLANT_TOOL_Y))
						.setSeed(new BananaCrop());
				return true;
			}
		});
	}

	@Override
	public void show() {
		renderer = new OrthogonalTiledMapRenderer(map);
		camera = new OrthographicCamera();
		camera.setToOrtho(false, 640, 1024);

		gameStartTime = System.currentTimeMillis();

		this.selection = null;
		this.state = new DefaultSelectionState();
		farm = new SnowFarm();

		/* Push all of the tiles for each layer into the tile map */
		Iterator<TiledMapTile> tileIterator;
		tileMap = new HashMap<String, Integer>();
		for (int i = 0; i < allLayers.length; i++) {
			for (tileIterator = tileSet.iterator(); tileIterator.hasNext();) {
				TiledMapTile tile = tileIterator.next();
				tileMap.put(tile.getProperties()
						.get(allLayers[i], String.class), tile.getId());
			}
		}
		
		inputMultiplexer = new InputMultiplexer();	
		invScrollTable = new Table();
		setupPlantWindow();
		setupSeedWindow();
		setupInventoryWindow();
		Gdx.input.setInputProcessor(inputMultiplexer);

	}

	/**
	 * Syncs each cell's ground tile with the field.
	 * 
	 */
	public void syncGroundTiles() {
		TiledMapTileLayer layer = (TiledMapTileLayer) map.getLayers().get(
				"ground");
		for (int y = 0; y < Field.ROWS; y++) {
			for (int x = 0; x < Field.COLUMNS; x++) {
				/* Get the cell we wish to update, converting field Y to cell Y */
				Cell cell = layer.getCell(x, y + FIELD_STARTING_Y);
				/* By default, make the tile transparent */
				TiledMapTile tile = tileSet.getTile(tileMap.get("transparent"));
				/* Get the ground texture for our cell to use */
				tile = tileSet.getTile(tileMap.get(farm.getPlot(x, y)
						.getTextureName()));
				cell.setTile(tile);
			}
		}
	}

	/**
	 * Syncs each cell's plant tile with the field.
	 * 
	 */
	public void syncPlantTiles() {
		TiledMapTileLayer layer = (TiledMapTileLayer) map.getLayers().get(
				"plant");
		for (int y = 0; y < Field.ROWS; y++) {
			for (int x = 0; x < Field.COLUMNS; x++) {
				/* Get the cell we wish to update */
				Cell cell = layer.getCell(x, y + FIELD_STARTING_Y);
				/* By default, make the tile transparent */
				TiledMapTile tile = tileSet.getTile(tileMap.get("transparent"));
				/*
				 * If there is a plant on the plot, get its texture for our cell
				 * to use
				 */
				if (farm.getPlot(x, y).getCrop() != null) {
					tile = tileSet.getTile(tileMap.get(farm.getPlot(x, y)
							.getCrop().getTextureName()));
				}
				cell.setTile(tile);
			}
		}
	}

	/**
	 * Syncs the tool bar cell in the tool bar to match the current seed
	 */
	public void syncSeedTile() {
		TiledMapTileLayer layer = (TiledMapTileLayer) map.getLayers().get(
				"seed");
		Cell cell = layer.getCell(PLANT_TOOL_X, PLANT_TOOL_Y);
		TiledMapTile tile = tileSet.getTile(tileMap
				.get(((AbstractPlantTool) farm.getTool(PLANT_TOOL_X,
						PLANT_TOOL_Y)).getSeed().getSeedName()));
		cell.setTile(tile);
	}

	/**
	 * Syncs the player's tool bar selection. Gives the selected position the
	 * "select" tile and gives everything else a transparent tile.
	 * 
	 * @param x
	 *            the x-coordinate of the cell that the player clicked.
	 */
	public void syncSelectTiles(int x) {
		TiledMapTileLayer selected = (TiledMapTileLayer) map.getLayers().get(
				"select");
		TiledMapTile tile;

		for (int i = 0; i < selected.getWidth(); i++) {
			if (x == i) {
				tile = tileSet.getTile(tileMap.get("select"));
			} else {
				tile = tileSet.getTile(tileMap.get("transparent"));
			}
			selected.getCell(i, 0).setTile(tile);
		}
	}

	/**
	 * Syncs each cell in the status bar to match the current season
	 */
	public void syncStatusTiles() {
		TiledMapTileLayer layer = (TiledMapTileLayer) map.getLayers().get(
				"status");
		for (int x = 0; x < Field.COLUMNS; x++) {
			Cell cell = layer.getCell(x, STATUS_BAR_Y);
			TiledMapTile tile = tileSet.getTile(tileMap.get(farm
					.getCurrentSeason().getSeasonType().toString()
					.toLowerCase()
					+ "" + x));
			cell.setTile(tile);
		}
	}

	/**
	 * Syncs each cell's tool tile with the tool bar.
	 * 
	 */
	public void syncToolTiles() {
		TiledMapTileLayer layer = (TiledMapTileLayer) map.getLayers().get(
				"tool");
		for (int y = 0; y < ToolBar.ROWS; y++) {
			for (int x = 0; x < ToolBar.COLUMNS; x++) {
				/* Get the cell we wish to update */
				Cell cell = layer.getCell(x, y);
				/* By default, make the tile transparent */
				TiledMapTile tile = tileSet.getTile(tileMap.get("transparent"));
				/* Get the ground texture for our cell to use */
				if (x != ToolBar.COLUMNS - 1) {
					tile = tileSet.getTile(tileMap.get(farm.getTool(x, y)
							.getTextureName()));
				} else { // TODO: we're hard coding the market texture here
							// because there is no tool to .getTextureName()
							// from.
					tile = tileSet.getTile(tileMap.get("market"));
				}
				cell.setTile(tile);
			}
		}
	}

	/**
	 * Syncs each cell's water tile with the field.
	 * 
	 */
	public void syncWaterTiles() {
		TiledMapTileLayer layer = (TiledMapTileLayer) map.getLayers().get(
				"water");
		for (int y = 0; y < Field.ROWS; y++) {
			for (int x = 0; x < Field.COLUMNS; x++) {
				Cell cell = layer.getCell(x, y + FIELD_STARTING_Y);
				/* By default, make the tile transparent */
				TiledMapTile tile = tileSet.getTile(tileMap.get("transparent"));
				if (!farm.getPlot(x, y).getIrrigation().isEmpty()) {
					tile = tileSet.getTile(tileMap.get(farm.getPlot(x, y)
							.getIrrigationTextureName()));
				}
				cell.setTile(tile);
			}
		}
	}

	/**
	 * Updates the window for selecting irrigation.
	 * 
	 * @param x		the x coordinate of the selected plot
	 * @param y		the y coordinate of the selected plot
	 */
		public void updateIrrigationWindow(final int x, final int y) {

			irrigationWindow.clear();
			
			Iterator<Irrigation> iterator = farm.getIrrigationChoices(x, y).iterator();
			for( ; iterator.hasNext(); ) {
				Irrigation irrigation = iterator.next();
				Texture irrigationTexture = new Texture(Gdx.files.internal(
						TextureHelper.getIrrigationTextureFileName(
								EnumSet.of(irrigation))));
				TextureRegion irrigationImage = new TextureRegion(irrigationTexture);
				Button irrigationButton = new Button(new Image(irrigationImage), skin);
				/* creates an input listener that additionally has the fields for 
				 * the selected X and Y. This way, when the listener is called, it will
				 * know which X and Y it pertains to. */
				irrigationButton.addListener(new IrrigationListener(x, y, irrigation) {
					public boolean touchDown(InputEvent event, float x, float y,
							int pointer, int button) {
						if(selection instanceof AbstractIrrigationTool) {
							((AbstractIrrigationTool) selection).setIrrigationChoice(this.getIrrigation());
							System.out.println("HERE");
							state = state.update(farm.getPlot(this.getX(), this.getY()), farm.getInventory());
						}
						return true;
					}
				});
				irrigationWindow.add(irrigationButton);
			}

			irrigationWindow.pack();
			irrigationMenuStage.addActor(irrigationWindow);

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
	public void updateState(int x, int y) {
		if (y >= FIELD_STARTING_Y) {
			System.out.println("PLOT	x: "+x+" y: "+y);
			if(selection instanceof AbstractIrrigationTool) {
				updateIrrigationWindow(x, y - FIELD_STARTING_Y);
				if(irrigationWindow.getChildren().size > 0) {
						irrigationWindow.setVisible(true);
						Gdx.input.setInputProcessor(irrigationMenuStage);
				}
			} else {
				state = state.update(farm.getPlot(x, y - FIELD_STARTING_Y), farm.getInventory());	
				updateInventoryTable();
			}
		} else if (y==0){
			System.out.println("TOOL	x: "+x+" y: "+y);
			if (selection != null && selection.equals(farm.getTool(x, y))) {
				if (selection instanceof AbstractPlantTool) {
					plantWindow.setVisible(true);
					Gdx.input.setInputProcessor(plantMenuStage);
				}
				
				if (inventoryMarketVisible){
					inventory_market_Window.setVisible(false);
					inventoryMarketVisible = false;		
				}
					
			} else if (farm.getTool(x, y) instanceof AbstractTool){
				if (inventoryMarketVisible){
					inventory_market_Window.setVisible(false);
					inventoryMarketVisible = false;		
				}
				selection = farm.getTool(x, y);
				state = state.update((AbstractTool) selection);	
				syncSelectTiles(x);				
			} else {		
				//TO-DO spell and Upgrade Selection
				/**
				selection = farm.getTool(x, y);
				if (selection instanceof AbstractSpell) {
					state = state.update((AbstractCrop) selection);
				} else if (selection instanceof AbstractTool) {
					state = state.update((AbstractTool) selection);
				} else if (selection instanceof AbstractWorker) {
					state = state.update((AbstractWorker) selection);
				} else if (selection instanceof AbstractUpgrade) {
					state = state.update((AbstractUpgrade) selection);
				} else if (selection instanceof AbstractCrop) {
					state = state.update((AbstractCrop) selection);
				}
				*/	
				selection = null;
				state = new DefaultSelectionState();
				syncSelectTiles(x);
				inventory_market_Window.setVisible(true);
				Gdx.input.setInputProcessor(inventory_market_Stage);
				inventoryMarketVisible = true;
			}
		}
	}

	/**
	 * Clears the Status Bar stage. Then, writes the player's bankroll, worker
	 * count, and time remaining in the current game.
	 */
	public void updateStatusBar() {

		/* Stage setup */
		statusBarStage.clear();

		/* Font setup */
		BitmapFont fontType = new BitmapFont();
		fontType.scale(FONT_SCALE);
		LabelStyle style1 = new LabelStyle(fontType, Color.BLACK);

		/* Bankroll label setup */
		Label bankBalance = new Label("Bank Balance: $" + PLAYER.getBankroll(),
				style1);
		bankBalance.setPosition(BANK_LABEL_X, BANK_LABEL_Y);

		/* Time label setup */
		long curTime = gameStartTime + GAME_TIME_MILLIS
				- System.currentTimeMillis();
		if (curTime < 0) {
			gameOver = true;
		}
		//TODO: May want to make this more concrete...
		if(!(curTime < 1000)) {
			farm.checkSeasonTimer();
		}
		String time = String.format("%02d:%02d",
				TimeUnit.MILLISECONDS.toMinutes(curTime),
				TimeUnit.MILLISECONDS.toSeconds(curTime)
						- TimeUnit.MILLISECONDS.toMinutes(curTime) * 60);
		Label timeRemaining = new Label("Time Remaining: " + time, style1);
		timeRemaining.setPosition(TIME_LABEL_X, TIME_LABEL_Y);

		/* Worker label setup */
		Label workers = new Label("Workers: "
				+ farm.getInventory().getWorkerCount(), style1);
		workers.setPosition(WORKER_LABEL_X, WORKER_LABEL_Y);

		/* Stage setup */
		statusBarStage.addActor(bankBalance);
		statusBarStage.addActor(timeRemaining);
		statusBarStage.addActor(workers);
	}
	
	/**
	 * Sets up the window to choose a seed to plant
	 */
	public void setupSeedWindow(){
		
		skin = new Skin(Gdx.files.internal(SKIN_JSON_UI));
		
		Texture carrot = new Texture(Gdx.files.internal("textures/carrotCrop.png"));
		Texture beet = new Texture(Gdx.files.internal("textures/beetCrop.png"));
		Texture rice = new Texture(Gdx.files.internal("textures/riceCrop.png"));
		Texture banana = new Texture(Gdx.files.internal("textures/bananaCrop.png"));

		TextureRegion carrotImage = new TextureRegion(carrot);
		TextureRegion beetImage = new TextureRegion(beet);
		TextureRegion riceImage = new TextureRegion(rice);
		TextureRegion bananaImage = new TextureRegion(banana);

		Button carrotButton = new Button(new Image(carrotImage), skin);
		Button beetButton = new Button(new Image(beetImage), skin);
		Button riceButton = new Button(new Image(riceImage), skin);
		Button bananaButton = new Button(new Image(bananaImage), skin);

		plantWindow = new Window("Pick a Type of Seed", skin);
		plantWindow.setModal(true);
		plantWindow.setMovable(false);
		plantWindow.setVisible(false);
		plantWindow.setPosition(WINDOW_X, WINDOW_Y);
		plantWindow.defaults().spaceBottom(10);
		plantWindow.row().fill().expandX();
		plantWindow.add(carrotButton);
		plantWindow.add(beetButton);
		plantWindow.add(riceButton);
		plantWindow.add(bananaButton);
		plantWindow.pack();
		plantMenuStage.addActor(plantWindow);

		carrotButton.addListener(new InputListener() {
			public boolean touchDown(InputEvent event, float x, float y,
					int pointer, int button) {
				plantWindow.setVisible(false);
				((AbstractPlantTool)farm.getTool(PLANT_TOOL_X, PLANT_TOOL_Y)).setSeed(new CarrotCrop());
				return true;
			}
		});

		beetButton.addListener(new InputListener() {
			public boolean touchDown(InputEvent event, float x, float y,
					int pointer, int button) {
				plantWindow.setVisible(false);
				((AbstractPlantTool)farm.getTool(PLANT_TOOL_X, PLANT_TOOL_Y)).setSeed(new BeetCrop());
				return true;
			}
		});

		riceButton.addListener(new InputListener() {
			public boolean touchDown(InputEvent event, float x, float y,
					int pointer, int button) {
				plantWindow.setVisible(false);
				((AbstractPlantTool)farm.getTool(PLANT_TOOL_X, PLANT_TOOL_Y)).setSeed(new RiceCrop());
				return true;
			}
		});

		bananaButton.addListener(new InputListener() {
			public boolean touchDown(InputEvent event, float x, float y,
					int pointer, int button) {
				plantWindow.setVisible(false);
				((AbstractPlantTool)farm.getTool(PLANT_TOOL_X, PLANT_TOOL_Y)).setSeed(new BananaCrop());
				return true;
			}
		});
	}
	
	/**
	 * Sets up the window to view inventory and market
	 */
	public void setupInventoryWindow(){
		
		inventory_market_Skin = new Skin(Gdx.files.internal(SKIN_JSON_UI));

		Table marketTable = new Table();
		marketTable.layout();
		inventory_market_Stage.addActor(marketTable);
		
		// add inventory items
		updateInventoryTable();
		
		invScrollTable.pack();
		ScrollPane inventorySP = new ScrollPane(invScrollTable, inventory_market_Skin);	
		marketTable.row();
		marketTable.add(new Label("MARKETPLACE", inventory_market_Skin));
		marketTable.row();
		marketTable.add(inventorySP).expand().fill().align(Align.left).left();
		marketTable.pack();	
		marketTable.layout();
	
		inventory_market_Window = new Window("", inventory_market_Skin);
		inventory_market_Window.setModal(true);
		inventory_market_Window.setMovable(false);
		inventory_market_Window.setVisible(false);
		inventoryMarketVisible = false;
		inventory_market_Window.setSize(Gdx.graphics.getWidth(), INVENTORY_HEIGHT);
		inventory_market_Window.setPosition(0, Gdx.graphics.getHeight());
		inventory_market_Window.defaults().spaceBottom(10);
		inventory_market_Window.row().fill().expandX();
		inventory_market_Window.add(marketTable).fill().expand().colspan(4).maxHeight(INVENTORY_HEIGHT);
		inventory_market_Stage.addActor(inventory_market_Window);	 
	}
	
	/**
	 * Populates the inventory/market table with item image, inventory quantity, buy cost, and sell value
	 */
	public void updateInventoryTable(){
		invScrollTable.clear();
		invScrollTable.layout();
		// inventory Stuff	
		Map<String, ArrayList<AbstractItem>> marketItems = this.farm.getMarket().getItems();		
		
		Object [] keyset = marketItems.keySet().toArray();
		int typeCount = keyset.length;
		Label type;
		for (int j = 0; j<typeCount; j++){
			type = new Label(keyset[j].toString(), inventory_market_Skin, "default-font", Color.ORANGE);
			type.setWrap(true);
			invScrollTable.row();
			invScrollTable.add(type).left();
			int itemCount = marketItems.get(keyset[j]).size();	
			int cost = 0;
			int value = 0;
			int inventoryTypeCount = 0;
			for (int i=0; i<itemCount; i++){
				
				Texture itemTexture = new Texture(Gdx.files.internal("textures/"+marketItems.get(keyset[j]).get(i).getTextureName()+".png"));
				TextureRegion itemImage = new TextureRegion(itemTexture);
				Button carrotButton = new Button(new Image(itemImage), inventory_market_Skin);
				carrotButton.setDisabled(true);
				
				inventoryTypeCount = farm.getInventory().getCount(marketItems.get(keyset[j]).get(i));	
				System.out.println("Farm inventory count: "+farm.getInventory().getItemsCount());
				cost = marketItems.get(keyset[j]).get(i).getCost();
				value = marketItems.get(keyset[j]).get(i).getValue();
				invScrollTable.row();
				invScrollTable.add(carrotButton).left();
				Label itemName = new Label(marketItems.get(keyset[j]).get(i).toString(), inventory_market_Skin);
				invScrollTable.add(itemName).width((float) (Gdx.graphics.getWidth()*.2)).left();
				Label itemInvCount = new Label(Integer.toString(inventoryTypeCount), inventory_market_Skin);
				invScrollTable.add(itemInvCount).width((float) (Gdx.graphics.getWidth()*.2)).left();	
				Button buyButton, sellButton;
				if (type.getText().toString().equals("WORKERS")){
					buyButton = new TextButton( "HIRE "+" $"+ cost, inventory_market_Skin, "default");
					sellButton = new TextButton("FIRE "+" $"+ value, inventory_market_Skin, "default");

				} else {
					buyButton = new TextButton( "BUY "+" $"+ cost, inventory_market_Skin, "default");
					sellButton = new TextButton("SELL "+" $"+ value, inventory_market_Skin, "default");
				}
				
				buyButton.addListener(new BuyClickListener(marketItems.get(keyset[j]).get(i), itemInvCount));
				invScrollTable.add(buyButton).width((float) (Gdx.graphics.getWidth()*.2)).left();
				
				
				sellButton.addListener(new SellClickListener(marketItems.get(keyset[j]).get(i), itemInvCount));
				invScrollTable.add(sellButton).width((float) (Gdx.graphics.getWidth()*.2)).left();
				
			}
		}
	}
	
	
	/**
	 * 
	 * @author FarmAdventure Devs
	 *
	 */
	private class BuyClickListener extends ClickListener {
	    AbstractItem item;
	    Label itemInvCount;
	    
	    /**
	     * This button enables buying of the item and updating of the item quantity
	     * @param item
	     * @param itemInvCount
	     */
	    public BuyClickListener(AbstractItem item, Label itemInvCount) {
	        this.item = item;
	        this.itemInvCount = itemInvCount;
	    }
	    
	    /**
	     * On button touch the item is bought and the item quantity is updated in the inventory
	     */
	    public boolean touchDown (InputEvent event, float x, float y, int pointer, int button) {
	    	if (PLAYER.buyItem(this.item)){
		    	farm.getInventory().addItem(item);
	    		itemInvCount.setText(Integer.toString(farm.getInventory().getCount(this.item)));
	    	}        
            return true;
        }
	}
	
	/**
	 * 
	 * @author FarmAdventure Devs
	 *
	 */
	private class SellClickListener extends ClickListener {
	    AbstractItem item;
	    Label itemInvCount;

	    /**
	     * This button enables selling of the item and updating of the item quantity
	     * @param item
	     * @param itemInvCount
	     */
	    public SellClickListener(AbstractItem item, Label itemInvCount) {
	        this.item = item;
	        this.itemInvCount = itemInvCount;
	    }
	    
	    /**
	     * On button touch the item is bought and the item quantity is updated in the inventory
	     */
	    public boolean touchDown (InputEvent event, float x, float y, int pointer, int button) {
	    	if (farm.getInventory().removeItem(item)){
	    		PLAYER.sellItem(this.item);
	    		itemInvCount.setText(Integer.toString(farm.getInventory().getCount(this.item)));
	    	}
            return true;
        }
	}
}