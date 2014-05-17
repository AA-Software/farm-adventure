package com.aa_software.farm_adventure.presenter.screen.farm_screen;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import com.aa_software.farm_adventure.model.Field;
import com.aa_software.farm_adventure.model.ToolBar;
import com.aa_software.farm_adventure.model.audio.Sounds;
import com.aa_software.farm_adventure.model.farm.Biome;
import com.aa_software.farm_adventure.model.farm.Farm;
import com.aa_software.farm_adventure.model.item.AbstractItem;
import com.aa_software.farm_adventure.model.item.crop.AbstractCrop;
import com.aa_software.farm_adventure.model.item.seed.AbstractSeed;
import com.aa_software.farm_adventure.model.item.tool.AbstractTool;
import com.aa_software.farm_adventure.model.item.tool.irrigate.AbstractIrrigationTool;
import com.aa_software.farm_adventure.model.item.tool.plant.AbstractPlantTool;
import com.aa_software.farm_adventure.model.item.worker.DefaultWorker;
import com.aa_software.farm_adventure.model.player.Player;
import com.aa_software.farm_adventure.model.player.Stats;
import com.aa_software.farm_adventure.model.plot.Irrigation;
import com.aa_software.farm_adventure.model.plot.TaskType;
import com.aa_software.farm_adventure.presenter.FarmAdventure;
import com.aa_software.farm_adventure.presenter.screen.AbstractScreen;
import com.aa_software.farm_adventure.presenter.screen.ScoreScreen;
import com.aa_software.farm_adventure.presenter.utility.TextureHelper;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.graphics.Color;
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
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.Window;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;

public class FarmScreen extends AbstractScreen {

	protected enum Actions {
		BUY, SELL
	}

	/**
	 * 
	 * @author FarmAdventure Devs
	 * 
	 */
	protected class BuyClickListener extends ClickListener {
		AbstractItem item;
		Label itemInvCount;
		Boolean isUpgrade;

		public BuyClickListener(AbstractItem item, Label itemInvCount,
				Boolean isUpgrade) {
			this.item = item;
			this.itemInvCount = itemInvCount;
			this.isUpgrade = isUpgrade;
		}

		/**
		 * On button touch the item is bought and the item quantity is updated
		 * in the inventory
		 */
		@Override
		public boolean touchDown(InputEvent event, float x, float y,
				int pointer, int button) {
			if (this.isUpgrade) {
				AbstractTool preTool = ((AbstractTool) item).getPredecessor();
				int cost = item.getCost() - preTool.getCost();
				item.setCost(cost);
				if (PLAYER.buyItem(this.item)) {
					farm.getInventory().addItem(item);
					farm.getMarket().addItem(item);
					farm.getInventory().removeItem(preTool);
					farm.getMarket().removeItem(preTool);
					((AbstractTool) item).setPredecessor(null);
					updateInventoryTable();
					farm.updateToolBar();
				}
			} else if (PLAYER.buyItem(this.item)) {
				if (item instanceof DefaultWorker)
					item = new DefaultWorker();
				farm.getInventory().addItem(item);
				itemInvCount.setText(Integer.toString(farm.getInventory()
						.getCount(this.item)));
				SOUNDS.playMoney();
			}
			return true;
		}
	}

	/**
	 * 
	 * @author FarmAdventure Devs
	 * 
	 */
	protected class InfoClickListener extends ClickListener {
		AbstractItem item;

		public InfoClickListener(AbstractItem item) {
			this.item = item;
		}

		/**
		 * On button touch the item is bought and the item quantity is updated
		 * in the inventory
		 */
		@Override
		public boolean touchDown(InputEvent event, float x, float y,
				int pointer, int button) {
			infoWindow.clear();
			Label description = new Label(this.item.getDescription(), skin);
			description.setColor(Color.ORANGE);
			infoWindow.add(description);
			TextButton closeButton = new TextButton("CLOSE", skin);
			closeButton.setColor(Color.ORANGE);
			closeButton.addListener(new InputListener() {
				@Override
				public boolean touchDown(InputEvent event, float x, float y,
						int pointer, int button) {
					infoWindow.setVisible(false);
					Gdx.input.setInputProcessor(marketMultiplexer);
					return true;
				}
			});
			infoWindow.add(closeButton).padLeft(10);
			infoWindow.pack();
			infoWindow.setVisible(true);
			Gdx.input.setInputProcessor(infoStage);
			SOUNDS.playClick();
			return true;
		}
	}

	protected class IrrigationListener extends InputListener {
		private final int x;
		private final int y;
		private final Irrigation irrigation;
		private final TaskType task;

		public IrrigationListener(int x, int y, Irrigation irrigation,
				TaskType task) {
			this.x = x;
			this.y = y;
			this.irrigation = irrigation;
			this.task = task;
		}

		public Irrigation getIrrigation() {
			return irrigation;
		}

		public TaskType getTaskType() {
			return task;
		}

		public int getX() {
			return x;
		}

		public int getY() {
			return y;
		}
	}

	/**
	 * 
	 * @author FarmAdventure Devs
	 * 
	 */
	protected class SeedClickListener extends ClickListener {
		AbstractItem item;

		public SeedClickListener(AbstractItem item) {
			this.item = item;
		}

		@Override
		public boolean touchDown(InputEvent event, float x, float y,
				int pointer, int button) {
			plantWindow.setVisible(false);
			((AbstractPlantTool) farm.getTool(PLANT_TOOL_X, PLANT_TOOL_Y))
					.setSeed(((AbstractSeed) this.item));
			SOUNDS.playClick();
			return true;
		}
	}

	/**
	 * 
	 * @author FarmAdventure Devs
	 * 
	 */
	protected class SellClickListener extends ClickListener {
		AbstractItem item;
		Label itemInvCount;

		public SellClickListener(AbstractItem item, Label itemInvCount) {
			this.item = item;
			this.itemInvCount = itemInvCount;
		}

		/**
		 * On button touch the item is bought and the item quantity is updated
		 * in the inventory
		 */
		@Override
		public boolean touchDown(InputEvent event, float x, float y,
				int pointer, int button) {
			if (farm.getInventory().removeItem(item)) {
				PLAYER.sellItem(this.item);
				itemInvCount.setText(Integer.toString(farm.getInventory()
						.getCount(this.item)));
				SOUNDS.playMoney();
			}
			return true;
		}
	}

	/**
	 * 
	 * @author FarmAdventure Devs
	 * 
	 */
	protected class WorkerClickListener extends ClickListener {
		int selectionIndex;
		DefaultWorker worker;

		public WorkerClickListener(int workerIndex, Button workerButton,
				DefaultWorker worker) {
			this.selectionIndex = workerIndex;
			this.worker = worker;
		}

		/**
		 * On button touch the item is bought and the item quantity is updated
		 * in the inventory
		 */
		@Override
		public boolean touchDown(InputEvent event, float x, float y,
				int pointer, int button) {
			if (selectedWorker >= 0) {
				((DefaultWorker) farm.getInventory().getAllWorkers()
						.get(selectedWorker)).resetTexture();
			}
			selectedWorker = this.selectionIndex;
			this.worker.setSelectTexture();
			SOUNDS.playClick();
			return true;
		}
	}

	/* Game */
	protected static final long GAME_TIME_MILLIS = 240000; // 240000 = 4 minutes

	protected long gameStartTime;

	/* Player */
	protected static final Player PLAYER = Player.getInstance();

	/* Sound */
	protected static final Sounds SOUNDS = Sounds.getInstance();

	/* Tile */
	protected static final String TILE_MAP_NAME = "tilemap/tileMap128.tmx";
	protected static final String TILE_SET_NAME = "tileSet128";

	protected static final int TILE_SIZE = 128;

	/* Layer */
	/*
	 * For each layer, please provide a method of syncing the model with the
	 * presenter
	 */
	protected final String[] allLayers = { "ground", "water", "plant", "tool",
			"seed", "status", "select", "transparent", "task" };
	protected static final int PLANT_TOOL_X = 2, PLANT_TOOL_Y = 0,
			IRRIGATION_TOOL_X = 1, IRRIGATION_TOOL_Y = 0, STATUS_BAR_Y = 1,
			FIELD_STARTING_Y = 2, MARKET_X = 4;

	public static final int UNSELECT = -1;

	/* Stage */
	public static final float FONT_SCALE = 1.5f;

	public static final float BANK_LABEL_X = (float) (SCREEN_WIDTH * .03),
			BANK_LABEL_Y = (float) (SCREEN_HEIGHT * .22),
			TIME_LABEL_X = (float) (SCREEN_WIDTH * .33),
			TIME_LABEL_Y = (float) (SCREEN_HEIGHT * .245),
			WORKER_LABEL_X = (float) (SCREEN_WIDTH * .80),
			WORKER_LABEL_Y = (float) (SCREEN_HEIGHT * .22),
			WINDOW_X = (float) (SCREEN_WIDTH * .25),
			WINDOW_Y = (float) (SCREEN_HEIGHT * .13),
			// INVENTORY_HEIGHT = SCREEN_HEIGHT - 14 * (TILE_SIZE / 10),
			INVENTORY_HEIGHT = (float) (SCREEN_HEIGHT * .63),
			// WORKER_HEIGHT = 70,
			INFO_X = (float) (SCREEN_WIDTH * .25),
			INFO_Y = (float) (SCREEN_HEIGHT * .5),
			INFO_WIDGTH = (float) (SCREEN_WIDTH * .25);

	protected AbstractItem selection;

	protected final Farm farm;

	protected OrthogonalTiledMapRenderer renderer;

	protected TiledMap map;

	protected TiledMapTileSet tileSet;
	protected HashMap<String, Integer> tileMap;
	protected InputMultiplexer inputMultiplexer;
	protected InputMultiplexer marketMultiplexer;

	protected Window plantWindow;
	protected Window inventoryWindow;
	protected Window irrigationWindow;
	protected Window infoWindow;

	protected Stage inventoryStage;
	protected Stage plantMenuStage;
	protected Stage irrigationMenuStage;
	protected Stage statusBarStage;
	protected Stage workerStage;
	protected Stage infoStage;

	protected Table inventoryScrollTable;
	protected Table workerQueue;
	protected Table workerTable;

	protected Boolean inventoryOpen = false;
	protected int selectedWorker = UNSELECT;

	protected boolean workerClicksDisabled;
	protected boolean toolBarClicksDisabled;
	protected boolean irrigationMenuClicksDisabled;
	protected boolean plantMenuClicksDisabled;
	protected boolean fieldClicksDisabled;
	protected boolean inventoryClicksDisabled;

	protected boolean disableGameTime;

	protected Stats stats;

	protected Texture beetTexture;
	protected Texture cabbageTexture;
	protected Texture carrotTexture;
	protected Texture potatoTexture;
	protected Texture info;
	protected final ArrayList<Texture> workerTextures, irrigationTextures,
			itemTextures;

	protected BitmapFont font;

	public FarmScreen(Biome.Type biome) {
		farm = new Farm(biome);
		workerTextures = new ArrayList<Texture>();
		irrigationTextures = new ArrayList<Texture>();
		itemTextures = new ArrayList<Texture>();
	}

	/**
	 * Adds a button to the plantWindow which matches the given seed.
	 * 
	 * @param seed
	 *            The button will be made to reference this seed.
	 */
	protected void addSeedButton(AbstractSeed seed) {
		Table seedTable = new Table();
		TextureRegion seedImage = null;
		if (seed.getTextureName() == "beetSeed") {
			seedImage = new TextureRegion(beetTexture);
		} else if (seed.getTextureName() == "cabbageSeed") {
			seedImage = new TextureRegion(cabbageTexture);
		} else if (seed.getTextureName() == "carrotSeed") {
			seedImage = new TextureRegion(carrotTexture);
		} else if (seed.getTextureName() == "potatoSeed") {
			seedImage = new TextureRegion(potatoTexture);
		}
		seedTable.row();
		seedTable.add(new Image(seedImage));
		Label seedQuantity = new Label("" + farm.getInventory().getCount(seed),
				skin);
		seedTable.row();
		seedTable.add(seedQuantity);
		Button seedButton = new Button(seedTable, skin);
		plantWindow.add(seedButton);
		seedButton.addListener(new SeedClickListener(seed));
	}

	/**
	 * Calculates the score of the player based off of the items in his/her
	 * inventory.
	 * 
	 * @return The calculated score based off the items in the player's
	 *         inventory.
	 */
	public int calculateScore() {
		// TODO: When the player comes to the farm, he should be deducted for
		// what starts in his inventory (primarily, the seeds) or
		// he shouldn't start with seeds or workers at all.
		int score = 0;
		Map<String, ArrayList<AbstractItem>> inventoryMap = farm.getInventory()
				.getItems();
		List<ArrayList<AbstractItem>> inventoryMapValues = new ArrayList<ArrayList<AbstractItem>>(
				inventoryMap.values());
		for (ArrayList<AbstractItem> itemList : inventoryMapValues) {
			for (AbstractItem item : itemList) {
				score += item.getValue();
			}
		}
		score = score - Player.STARTING_BANKROLL;
		if (score < 0) {
			score = 0;
		}

		return score;
	}

	/**
	 * Uses the libgdx library to get the x, y location of a users touch if
	 * there was one. This value is used to check if the user clicked over the
	 * plots or over the tool bar. If the user clicks the status bar, there is
	 * no change. If it is a plot, the x , y , and ground string are used to
	 * update the state. If the tool bar was clicked, the x, y, and tool bar
	 * string are used to update the state.
	 */
	public void checkTouch() {
		if (Gdx.input.justTouched()) {
			Vector3 touchPos = new Vector3();
			touchPos.set(Gdx.input.getX(), Gdx.input.getY(), 0);
			camera.unproject(touchPos);
			int xCell = (int) (touchPos.x / TILE_SIZE);
			int yCell = (int) (touchPos.y / TILE_SIZE);
			updateState(xCell, yCell);
		}
		if (Gdx.input.isKeyPressed(Keys.BACK)) {
			Gdx.input.setCatchBackKey(true);
			// TODO add a pop up asking if they want to quit
		}
	}

	/**
	 * This method disposes of our left over libGDX elements, updates the
	 * player's score, and returns the player to the main menu.
	 */
	@Override
	public void dispose() {
		super.dispose();
		// stages
		inventoryStage.dispose();
		plantMenuStage.dispose();
		irrigationMenuStage.dispose();
		statusBarStage.dispose();
		workerStage.dispose();
		infoStage.dispose();
		// textures
		beetTexture.dispose();
		cabbageTexture.dispose();
		carrotTexture.dispose();
		potatoTexture.dispose();
		if (info != null) // TODO: need to do this?
			info.dispose();
		for (Texture texture : workerTextures)
			texture.dispose();
		for (Texture texture : irrigationTextures)
			texture.dispose();
		for (Texture texture : itemTextures)
			texture.dispose();
		// other
		farm.disposeOfTimers();
		map.dispose();
		renderer.dispose();
		font.dispose();
	}

	/**
	 * Checks if there is a window visible OTHER THAN worker window (which is
	 * always visible).
	 */
	public boolean hasVisibleWindow() {
		if (plantWindow.isVisible() || infoWindow.isVisible()
				|| irrigationWindow.isVisible() || inventoryWindow.isVisible()) {
			return true;
		}
		return false;
	}

	/**
	 * Removes the cost of each worker from the player's bankroll. If paying a
	 * worker causes the players' bankroll to drop into the negatives, the
	 * worker is fired.
	 */
	public void payWorker() {
		int totalWorkerCost = 0;
		ArrayList<AbstractItem> workers = farm.getInventory().getWorkers();
		if (workers != null && workers.size() > 1) {
			int workersToFire = 0;
			for (AbstractItem w : workers) {
				totalWorkerCost += w.getCost();
				if (PLAYER.getBankroll() - totalWorkerCost < 0) {
					// TODO: Create a methond to remove a specific worker,
					// instead of a random one, when workers can have different
					// stats. Also, add a notification when workers are fired.
					workersToFire++;

				}
			}
			for (int i = 0; i < workersToFire; i++) {
				farm.getInventory().removeItem(workers.get(0));
			}
		}

		PLAYER.setBankroll(PLAYER.getBankroll() - totalWorkerCost);
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
			super.render(delta);

			if (hasVisibleWindow() == false) {
				Gdx.input.setInputProcessor(workerStage);
			}

			checkTouch();
			farm.getField().syncAllIrrigation();
			farm.applySeasonalEffects();
			syncWaterTiles();
			syncPlantTiles();
			syncGroundTiles();
			syncToolTiles();
			syncStatusTiles();
			syncSeedTile();
			syncTaskTiles();
			// syncMoney();
			camera.update();
			renderer.setView(camera);
			renderer.render();

			updateStatusBar();
			statusBarStage.act(delta);
			statusBarStage.draw();

			updateWorkerQueue();
			workerStage.act(delta);
			workerStage.draw();

			plantMenuStage.act(delta);
			plantMenuStage.draw();

			inventoryStage.act(delta);
			inventoryStage.draw();

			irrigationMenuStage.act(delta);
			irrigationMenuStage.draw();

			infoStage.act(delta);
			infoStage.draw();
	}

	@Override
	public void resize(int width, int height) {
		super.resize(width, height);
		workerStage.setViewport(STAGE_WIDTH, STAGE_HEIGHT, true);
		// plantMenuStage.setViewport(width, height);
		// irrigationMenuStage.setViewport(width, height);
	}

	/**
	 * Sets game clicks on or off.
	 * 
	 * @param bool
	 *            True will set the game clicks off, False will set them on.
	 */
	public void setAllGameClicksDisabled(boolean bool) {
		workerClicksDisabled = bool;
		toolBarClicksDisabled = bool;
		fieldClicksDisabled = bool;
		irrigationMenuClicksDisabled = bool;
		plantMenuClicksDisabled = bool;
		inventoryClicksDisabled = bool;
	}

	/**
	 * Sets up the window to view inventory and market
	 */
	public void setupInventoryWindow() {

		Table marketTable = new Table();
		marketTable.layout();
		inventoryStage.addActor(marketTable);
		inventoryScrollTable.pack();
		ScrollPane inventorySP = new ScrollPane(inventoryScrollTable, skin);
		marketTable.row();
		marketTable.add(new Label("INVENTORY", skin))
				.padLeft((float) (SCREEN_WIDTH * .4))
				.padRight((float) (SCREEN_WIDTH * .15))
				.width((float) (SCREEN_WIDTH * .33));

		TextButton exitButton = new TextButton("EXIT", skin);
		exitButton.setColor(Color.RED);
		exitButton.addListener(new InputListener() {
			@Override
			public boolean touchDown(InputEvent event, float x, float y,
					int pointer, int button) {
				inventoryWindow.setVisible(false);
				inventoryOpen = false;
				SOUNDS.playClick();
				return true;
			}
		});
		marketTable.add(exitButton).width((float) (SCREEN_WIDTH * .1));
		marketTable.row();
		marketTable.add(inventorySP).colspan(2).width(SCREEN_WIDTH);
		marketTable.pack();
		marketTable.layout();
		inventoryWindow = new Window("", skin);
		inventoryWindow.setModal(true);
		inventoryWindow.setMovable(false);
		inventoryWindow.setVisible(false);
		inventoryOpen = false;
		inventoryWindow.setSize(SCREEN_WIDTH, INVENTORY_HEIGHT);
		inventoryWindow.setPosition(0, SCREEN_HEIGHT);
		inventoryWindow.defaults().spaceBottom(10);
		inventoryWindow.row().fill().expandX();
		inventoryWindow.add(marketTable).fill().expand().colspan(4)
				.maxHeight(SCREEN_HEIGHT);
		inventoryStage.addActor(inventoryWindow);
	}

	protected Window setupWindow(String text, float x, float y) {
		Window window = new Window(text, skin);
		window.setModal(false);
		window.setMovable(false);
		window.setVisible(false);
		window.setPosition(x, y);
		window.defaults().spaceBottom(10);
		window.row().fill().expandX();
		return window;
	}

	/**
	 * Sets up the table to display available workers
	 */
	public final void setupWorkerTable() {

		updateWorkerQueue();
		workerQueue.pack();
		workerTable = new Table();
		workerTable.add(workerQueue).fill().expandX();
		workerTable.setPosition((float) (SCREEN_WIDTH * .495),
				(float) (SCREEN_HEIGHT * .19));
		workerStage.addActor(workerTable);
	}

	/**
	 * Called when the FarmScreen is first rendered.
	 */
	@Override
	public void show() {
		super.show();

		disableGameTime = false;
		setAllGameClicksDisabled(false);

		selection = null;

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

		plantMenuStage = new Stage(STAGE_WIDTH, STAGE_HEIGHT, true);
		irrigationMenuStage = new Stage(STAGE_WIDTH, STAGE_HEIGHT, true);
		statusBarStage = new Stage(SCREEN_WIDTH, SCREEN_HEIGHT, true);
		inventoryStage = new Stage(STAGE_WIDTH, STAGE_HEIGHT, true);
		workerStage = new Stage();
		infoStage = new Stage(STAGE_WIDTH, STAGE_HEIGHT, true);

		marketMultiplexer = new InputMultiplexer();
		marketMultiplexer.addProcessor(inventoryStage);
		marketMultiplexer.addProcessor(workerStage);

		stats = new Stats();

		irrigationWindow = setupWindow(" Pick a side to irrigate: ", WINDOW_X,
				WINDOW_Y);
		irrigationMenuStage.addActor(irrigationWindow);

		plantWindow = setupWindow(" Pick a type of seed: ", WINDOW_X, WINDOW_Y);
		plantMenuStage.addActor(plantWindow);

		infoWindow = setupWindow(" Information ", INFO_X, INFO_Y);
		infoStage.addActor(infoWindow);

		workerQueue = new Table();
		setupWorkerTable();
		inventoryScrollTable = new Table();
		setupInventoryWindow();

		renderer = new OrthogonalTiledMapRenderer(map);

		gameStartTime = System.currentTimeMillis();

		/* Push all of the tiles for each layer into the tile map */
		tileMap = new HashMap<String, Integer>();
		for (int i = 0; i < allLayers.length; i++) {
			for (tileIterator = tileSet.iterator(); tileIterator.hasNext();) {
				TiledMapTile tile = tileIterator.next();
				tileMap.put(tile.getProperties()
						.get(allLayers[i], String.class), tile.getId());
			}
		}

		potatoTexture = new Texture(Gdx.files.internal("textures/"
				+ "potatoSeed" + ".png"));
		carrotTexture = new Texture(Gdx.files.internal("textures/"
				+ "carrotSeed" + ".png"));
		cabbageTexture = new Texture(Gdx.files.internal("textures/"
				+ "cabbageSeed" + ".png"));
		beetTexture = new Texture(Gdx.files.internal("textures/" + "beetSeed"
				+ ".png"));

		font = new BitmapFont();
		font.setScale(FONT_SCALE);
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
				AbstractCrop crop = farm.getPlot(x, y).getCrop();
				if (crop != null) {
					tile = tileSet.getTile(tileMap.get(crop.getTextureName()));
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
		if (((AbstractPlantTool) farm.getTool(PLANT_TOOL_X, PLANT_TOOL_Y))
				.getSeed() != null) {
			TiledMapTile tile = tileSet.getTile(tileMap
					.get(((AbstractPlantTool) farm.getTool(PLANT_TOOL_X,
							PLANT_TOOL_Y)).getSeed().getTextureName()));
			cell.setTile(tile);
		} else {
			TiledMapTile tile = tileSet.getTile(tileMap.get("transparent"));
			cell.setTile(tile);

		}
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
					.getCurrentSeason().getType().toString().toLowerCase()
					+ "" + x));
			cell.setTile(tile);
		}
	}

	/**
	 * Syncs each cell's ground tile with the field.
	 * 
	 */
	public void syncTaskTiles() {
		TiledMapTileLayer layer = (TiledMapTileLayer) map.getLayers().get(
				"task");
		for (int y = 0; y < Field.ROWS; y++) {
			for (int x = 0; x < Field.COLUMNS; x++) {
				/* Get the cell we wish to update, converting field Y to cell Y */
				Cell cell = layer.getCell(x, y + FIELD_STARTING_Y);
				/* By default, make the tile transparent */
				TiledMapTile tile = tileSet.getTile(tileMap.get("transparent"));
				/* Get the ground texture for our cell to use */
				if (farm.getPlot(x, y).getTaskTextureName() != null) {
					tile = tileSet.getTile(tileMap.get(farm.getPlot(x, y)
							.getTaskTextureName()));
				}
				cell.setTile(tile);
			}
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
	 * Unselects the current worker and selection.
	 */
	protected void unselect() {
		if (selectedWorker >= 0) {
			((DefaultWorker) farm.getInventory().getAllWorkers()
					.get(selectedWorker)).resetTexture();
			selectedWorker = UNSELECT;
		}
		syncSelectTiles(UNSELECT);
		selection = null;
	}

	/**
	 * Populates the inventory/market table with item image, inventory quantity,
	 * buy cost, and sell value
	 */
	public void updateInventoryTable() {
		inventoryScrollTable.clear();
		inventoryScrollTable.layout();

		// inventory Stuff
		Map<String, ArrayList<AbstractItem>> marketItems = this.farm
				.getMarket().getItems();

		ArrayList<String> typeSet = new ArrayList<String>(Arrays.asList(
				"WORKERS", "SEEDS", "CROPS", "PLOW TOOLS", "IRRIGATION TOOLS",
				"PLANT TOOLS", "HARVEST TOOLS"));
		int typeCount = typeSet.size();
		Label type;
		for (int j = 0; j < typeCount; j++) {
			type = new Label(typeSet.get(j), skin, "default-font", Color.ORANGE);
			type.setWrap(true);
			inventoryScrollTable.row();
			inventoryScrollTable.add(type).left();
			int itemCount = marketItems.get(typeSet.get(j)).size();

			int cost = 0;
			int value = 0;
			int inventoryTypeCount = 0;
			for (int i = 0; i < itemCount; i++) {
				Texture itemTexture = new Texture(
						Gdx.files.internal("textures/"
								+ marketItems.get(typeSet.get(j)).get(i)
										.getTextureName() + ".png"));
				itemTextures.add(itemTexture);
				TextureRegion itemImage = new TextureRegion(itemTexture);
				Button itemButton = new Button(new Image(itemImage), skin);
				itemButton.setDisabled(true);

				inventoryTypeCount = farm.getInventory().getCount(
						marketItems.get(typeSet.get(j)).get(i));
				cost = marketItems.get(typeSet.get(j)).get(i).getCost();
				value = marketItems.get(typeSet.get(j)).get(i).getValue();
				inventoryScrollTable.row();
				inventoryScrollTable.add(itemButton).left();
				Label itemName = new Label(marketItems.get(typeSet.get(j))
						.get(i).toString(), skin);
				inventoryScrollTable.add(itemName)
						.width((float) (SCREEN_WIDTH * .2)).left();
				Label itemInvCount = new Label(
						Integer.toString(inventoryTypeCount), skin);
				inventoryScrollTable.add(itemInvCount)
						.width((float) (SCREEN_WIDTH * .05)).left().padLeft(1);
				info = new Texture(Gdx.files.internal("textures/info.png"));

				TextureRegion infoReg = new TextureRegion(info);
				Button infoButton = new Button(new Image(infoReg), skin);
				infoButton.padBottom(2);
				infoButton.padLeft(2);
				infoButton.padRight(4);
				infoButton.padTop(2);

				Button buyButton = null;
				Button sellButton = null;
				// if there are workers then add hire or layoff buttons
				if (type.getText().toString().equals(DefaultWorker.TYPE)) {
					DefaultWorker tempWorker = new DefaultWorker();
					cost = tempWorker.getCost();
					buyButton = new TextButton("HIRE " + " $" + cost
							+ "/season", skin, "default");
					buyButton.addListener(new BuyClickListener(tempWorker,
							itemInvCount, false));
					inventoryScrollTable.add(buyButton)
							.width((float) (SCREEN_WIDTH * .4)).left();
					infoButton.addListener(new InfoClickListener(marketItems
							.get(typeSet.get(j)).get(i)));
					inventoryScrollTable.add(infoButton).left()
							.padLeft((float) (SCREEN_WIDTH * .04));
				} else if (type.getText().toString().equals(AbstractSeed.TYPE)) {
					buyButton = new TextButton("BUY " + " $" + cost, skin,
							"default");
					buyButton.addListener(new BuyClickListener(marketItems.get(
							typeSet.get(j)).get(i), itemInvCount, false));
					inventoryScrollTable.add(buyButton)
							.width((float) (SCREEN_WIDTH * .4)).left();
					infoButton.addListener(new InfoClickListener(marketItems
							.get(typeSet.get(j)).get(i)));
					inventoryScrollTable.add(infoButton).left()
							.padLeft((float) (SCREEN_WIDTH * .04));
				} else if (j > 2 && j < 7) { // if there are tools then
					if (inventoryTypeCount > 0) {
						AbstractTool toolUpgrade = ((AbstractTool) marketItems
								.get(typeSet.get(j)).get(i)).getUpgrade();

						if (toolUpgrade != null) { // check if upgrades apply
							cost = toolUpgrade.getCost()
									- ((AbstractTool) marketItems.get(
											typeSet.get(j)).get(i)).getCost();
							String upgradeName = toolUpgrade.getName();
							buyButton = new TextButton(upgradeName
									+ " upgrade $" + cost, skin, "default");

							buyButton.addListener(new BuyClickListener(
									toolUpgrade, itemInvCount, true));
							inventoryScrollTable.add(buyButton)
									.width((float) (SCREEN_WIDTH * .4)).left();
							infoButton.addListener(new InfoClickListener(
									toolUpgrade));
							inventoryScrollTable.add(infoButton).left()
									.padLeft((float) (SCREEN_WIDTH * .04));

						} else {
							Label noUpgrade = new Label("No Upgrade", skin);
							inventoryScrollTable.add(noUpgrade)
									.width((float) (SCREEN_WIDTH * .4)).left();
						}
					} else { // else buy or trade for the current tool
						buyButton = new TextButton("BUY " + " $" + cost, skin,
								"default");
						buyButton.addListener(new BuyClickListener(marketItems
								.get(typeSet.get(j)).get(i), itemInvCount,
								false));
						inventoryScrollTable.add(buyButton)
								.width((float) (SCREEN_WIDTH * .4)).left();
						infoButton.addListener(new InfoClickListener(
								marketItems.get(typeSet.get(j)).get(i)));
						inventoryScrollTable.add(infoButton).left()
								.padLeft((float) (SCREEN_WIDTH * .04));
					}
				} else {
					sellButton = new TextButton("SELL " + " $" + value, skin,
							"default");
					sellButton.addListener(new SellClickListener(marketItems
							.get(typeSet.get(j)).get(i), itemInvCount));
					inventoryScrollTable.add(sellButton)
							.width((float) (SCREEN_WIDTH * .4)).left();
					infoButton.addListener(new InfoClickListener(marketItems
							.get(typeSet.get(j)).get(i)));
					inventoryScrollTable.add(infoButton).left()
							.padLeft((float) (SCREEN_WIDTH * .04));

				}

			}
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
	public void updateIrrigationWindow(final int x, final int y) {

		irrigationWindow.clear();
		Iterator<Irrigation> iterator = farm.getIrrigationChoices(x, y)
				.iterator();
		while (iterator.hasNext()) {
			Irrigation irrigation = iterator.next();
			TaskType task = farm.getTaskType(x, y, irrigation);
			Texture irrigationTexture = new Texture(
					Gdx.files.internal(TextureHelper
							.getIrrigationTextureFileName(EnumSet
									.of(irrigation))));
			irrigationTextures.add(irrigationTexture);
			TextureRegion irrigationImage = new TextureRegion(irrigationTexture);
			Button irrigationButton = new Button(new Image(irrigationImage),
					skin);
			/*
			 * creates an input listener that additionally has the fields for
			 * the selected X and Y. This way, when the listener is called, it
			 * will know which X and Y it pertains to.
			 */
			irrigationButton.addListener(new IrrigationListener(x, y,
					irrigation, task) {
				@Override
				public boolean touchDown(InputEvent event, float x, float y,
						int pointer, int button) {
					if (selection instanceof AbstractIrrigationTool) {
						((AbstractIrrigationTool) selection)
								.setIrrigationChoice(this.getIrrigation());
						((AbstractIrrigationTool) selection).setTaskType(this
								.getTaskType());
						selection.update(
								farm.getPlot(this.getX(), this.getY()),
								farm.getInventory());
						unselect();
						SOUNDS.playClick();
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
	public void updatePlantWindow() {
		plantWindow.clear();
		if (farm.getInventory().getItems().get("SEEDS") != null) {
			ArrayList<AbstractSeed> seedKey = new ArrayList<AbstractSeed>();
			AbstractSeed tempSeed;
			int seedNum = farm.getInventory().getItems().get("SEEDS").size();
			for (int i = 0; i < seedNum; i++) {
				tempSeed = (AbstractSeed) farm.getInventory().getItems()
						.get("SEEDS").get(i);
				if (seedKey.size() == 0) {
					seedKey.add(tempSeed);
					addSeedButton(tempSeed);
				} else {
					Boolean inSeedKey = false;
					for (int j = 0; j < seedKey.size(); j++) {
						if (seedKey.get(j).compareTo(tempSeed) == 0) {
							inSeedKey = true;
							j = seedKey.size();
						}
					}
					if (!inSeedKey) {
						seedKey.add(tempSeed);
						addSeedButton(tempSeed);
					}
				}
			}
		}
		plantWindow.pack();
	}

	/**
	 * Takes in an x, and y value (cell-based) that represents user input.
	 * Determines the what type of action should be taken based on this x and y.
	 * 
	 * If the player clicked the field, it should check if the player has
	 * previously selected an item, and if so it should send the clicked plot to
	 * visit the previous selection.
	 * 
	 * If the player clicked the tool bar, it should update their current
	 * selection or bring up the market place, depending on the X coordinate of
	 * their click.
	 * 
	 * @param x
	 *            the tile/cell-based x coordinate clicked by the player.
	 * @param ythe
	 *            tile/cell-based y coordinate clicked by the player.
	 * 
	 */
	public void updateState(int x, int y) {
		if (!inventoryOpen) {
			if (y >= FIELD_STARTING_Y && !fieldClicksDisabled
					&& selection != null) {
				/*
				 * Player clicked the field.
				 */
				plantWindow.setVisible(false);
				irrigationWindow.setVisible(false);
				if (selection instanceof AbstractIrrigationTool) {
					if (!irrigationMenuClicksDisabled) {
						updateIrrigationWindow(x, y - FIELD_STARTING_Y);
						if (irrigationWindow.getChildren().size > 0) {
							irrigationWindow.setVisible(true);
							Gdx.input.setInputProcessor(irrigationMenuStage);
							SOUNDS.playClick();
						} else {
							unselect();
						}
					}
				} else {
					selection.update(farm.getPlot(x, y - FIELD_STARTING_Y),
							farm.getInventory());
					unselect();
					// updateInventoryTable();
				}
			} else if (y == 0 && !toolBarClicksDisabled) {
				/*
				 * Player clicked the tool bar.
				 */
				plantWindow.setVisible(false);
				irrigationWindow.setVisible(false);
				inventoryWindow.setVisible(false);

				if (selection != null && selection.equals(farm.getTool(x, y))) {
					/*
					 * Player double clicked his selection
					 */
					if (selection instanceof AbstractPlantTool) {
						if (!plantMenuClicksDisabled) {
							updatePlantWindow();
							if (plantWindow.getChildren().size > 0) {
								plantWindow.setVisible(true);
								Gdx.input.setInputProcessor(plantMenuStage);
							}
						}
						SOUNDS.playClick();
					}
				} else if (x != MARKET_X) {
					/*
					 * Player clicked a tool other than the market/inventory.
					 */
					if (selectedWorker >= 0) {
						selection = farm.getTool(x, y);
						((AbstractTool) selection)
								.setWorkerIndex(selectedWorker);
						syncSelectTiles(x);
						SOUNDS.playClick();
					}
				} else {
					/*
					 * Player clicked the market/inventory.
					 */
					updateInventoryTable();
					if (!inventoryClicksDisabled) {
						unselect();
						syncSelectTiles(x);
						inventoryWindow.setVisible(true);
						inventoryOpen = true;
						Gdx.input.setInputProcessor(marketMultiplexer);
						SOUNDS.playClick();
					}
				}
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

		/* Bankroll label setup */
		LabelStyle labelStyle = new LabelStyle();
		labelStyle.font = font;
		Label bankBalance = new Label("Bank Balance: \n       $"
				+ PLAYER.getBankroll(), labelStyle);
		bankBalance.setPosition(BANK_LABEL_X, BANK_LABEL_Y);

		/* Time label setup */
		String time = null;
		if (!disableGameTime) {
			long curTime = gameStartTime + GAME_TIME_MILLIS
					- System.currentTimeMillis();
			if (curTime < 0) {
				stats.setScore(PLAYER.getBankroll() + calculateScore());
				PLAYER.setBankroll(PLAYER.getBankroll() + calculateScore());
				FarmAdventure.getInstance().setScreen(new ScoreScreen(stats));
			}
			/* 1000 milliseconds = 1 second */
			if (!(curTime < 1000)) {
				boolean seasonHasChanged = farm.checkSeasonTimer();
				if (seasonHasChanged) {
					payWorker();
				}
			}
			time = String.format("%02d:%02d",
					TimeUnit.MILLISECONDS.toMinutes(curTime),
					TimeUnit.MILLISECONDS.toSeconds(curTime)
							- TimeUnit.MILLISECONDS.toMinutes(curTime) * 60);
		} else {
			time = "INFINITE!";
		}
		Label timeRemaining = new Label("Time Remaining: " + time, labelStyle);
		timeRemaining.setPosition(TIME_LABEL_X, TIME_LABEL_Y);

		/* Worker label setup */
		Label workers = new Label("Workers: \n       "
				+ farm.getInventory().getWorkerCount(), labelStyle);
		workers.setPosition(WORKER_LABEL_X, WORKER_LABEL_Y);

		/* Stage setup */
		statusBarStage.addActor(bankBalance);
		statusBarStage.addActor(timeRemaining);
		statusBarStage.addActor(workers);
	}

	/**
	 * Clears the worker window and fills it out with the
	 */
	public void updateWorkerQueue() {
		workerQueue.clear();
		workerQueue.layout();
		Map<String, ArrayList<AbstractItem>> inventoryItems = this.farm
				.getInventory().getItems();
		ArrayList<AbstractItem> invWorkers = inventoryItems.get("WORKERS");
		int workerCount = invWorkers.size();
		workerQueue.row();
		for (int i = 0; i < workerCount; i++) {
			if (!((DefaultWorker) invWorkers.get(i)).isBusy()) {
				Texture workerTexture = new Texture(
						Gdx.files.internal("textures/"
								+ invWorkers.get(i).getTextureName() + ".png"));
				workerTextures.add(workerTexture);
				TextureRegion workerTRigion = new TextureRegion(workerTexture);
				Image workerImage = new Image(workerTRigion);
				Button workerButton = new Button(workerImage, skin);
				workerButton.padBottom(2).padLeft(2).padRight(2).padTop(2);
				workerButton.addListener(new WorkerClickListener(i,
						workerButton, (DefaultWorker) invWorkers.get(i)));
				workerQueue.add(workerButton).left().padLeft(3);
			}

		}
	}
}
