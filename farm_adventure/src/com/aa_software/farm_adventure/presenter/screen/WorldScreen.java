package com.aa_software.farm_adventure.presenter.screen;

import com.aa_software.farm_adventure.model.farm.AbstractFarm;
import com.aa_software.farm_adventure.model.farm.DesertFarm;
import com.aa_software.farm_adventure.model.farm.RainforestFarm;
import com.aa_software.farm_adventure.model.farm.SnowFarm;
import com.aa_software.farm_adventure.model.farm.TutorialFarm;
import com.aa_software.farm_adventure.model.season.Season;
import com.aa_software.farm_adventure.model.season.SeasonType;
import com.aa_software.farm_adventure.model.selectable.item.crop.CarrotCrop;
import com.aa_software.farm_adventure.model.selectable.item.tool.plant.AbstractPlantTool;
import com.aa_software.farm_adventure.presenter.FarmAdventure;
import com.aa_software.farm_adventure.presenter.screen.farm_screen.AbstractFarmScreen;
import com.aa_software.farm_adventure.presenter.screen.farm_screen.DesertFarmScreen;
import com.aa_software.farm_adventure.presenter.screen.farm_screen.RainforestFarmScreen;
import com.aa_software.farm_adventure.presenter.screen.farm_screen.SnowFarmScreen;
import com.aa_software.farm_adventure.presenter.screen.farm_screen.TutorialFarmScreen;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.Window;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;

/**
 * Creates a new WorldScreen and displays it. The screen consists of the
 * background map and clickable buttons which take the player to the appropriate
 * farm.
 * 
 * @author AA Software
 * 
 */
public class WorldScreen extends AbstractScreen {

	public static final String SKIN_JSON_UI = "skin/uiskin.json";
	public static final float WINDOW_X = (float) (Gdx.graphics.getWidth() * .25);
	public static final float WINDOW_Y = (float) (Gdx.graphics.getHeight() * .40);

	protected Skin skin;
	protected Stage plantMenuStage;
	protected Window seasonWindow;

	protected AbstractFarm selectedFarm;
	protected AbstractFarmScreen selectedScreen;

	/**
	 * Constructs a WorldScreen based on the current game.
	 * 
	 * @param game
	 *            the current FarmAdventure class that is being played
	 */
	public WorldScreen(FarmAdventure game) {
		super(game);
	}

	/**
	 * Creates and displays the world map and buttons. Handles the on click for
	 * the buttons - starting up a new farm.
	 */
	@Override
	public void show() {
		super.show();
		skin = new Skin(Gdx.files.internal(SKIN_JSON_UI));
		
		// Create background and table
		Image background = new Image(new Texture(
				Gdx.files.internal("world/WorldMap.png")));
		Table table = new Table();
		background.setFillParent(true);
		// if(FarmAdventure.DEV_MODE)
		// table.debug();
		super.addActor(background);
		super.addActor(table);
		table.setFillParent(true);

		// Set up the texture for the button
		final Texture tutorialTex = new Texture(
				Gdx.files.internal("world/TutorialFarm.png"));
		final Texture rainforestTex = new Texture(
				Gdx.files.internal("world/RainforestFarm.png"));
		final Texture desertTex = new Texture(
				Gdx.files.internal("world/DesertFarm.png"));
		final Texture snowTex = new Texture(
				Gdx.files.internal("world/SnowFarm.png"));
		//final int width = t.getWidth() - 2;
		//final int height = t.getHeight() - 2;
		//TextureRegion patch = new TextureRegion(t, 1, 1, width, height);
		
		//TextureRegion tutImage = new TextureRegion(t);

		// Create the style for the button
		//TextButton.TextButtonStyle buttonStyle = new TextButton.TextButtonStyle();
		//buttonStyle.up = new TextureRegionDrawable(patch);
		//buttonStyle.down = new TextureRegionDrawable(patch);
		//buttonStyle.font = new BitmapFont();

		// Create buttons
		Button tutorialFarmButton = new Button(new Image(tutorialTex), skin);
		Button rainforestFarmButton = new Button(new Image(rainforestTex), skin);
		Button desertFarmButton = new Button(new Image(desertTex), skin);
		Button snowFarmButton = new Button(new Image(snowTex), skin);

		// Add buttons to the correct position
		super.addActor(tutorialFarmButton);
		table.add(tutorialFarmButton).size(100, 100).top().left().padTop(50)
				.padLeft(80);
		table.row();
		super.addActor(rainforestFarmButton);
		table.add(rainforestFarmButton).size(100, 100).expand().top().right()
				.padTop(200).padRight(60);
		table.row();
		super.addActor(desertFarmButton);
		table.add(desertFarmButton).size(100, 100).expand().bottom().left()
				.padBottom(150).padLeft(50);
		table.row();
		super.addActor(snowFarmButton);
		table.add(snowFarmButton).size(100, 100).expand().bottom().right()
				.padBottom(100).padRight(120);
		table.row();

		// This line of code will take the user to the farm on click or touch of
		// the tutorial farm
		tutorialFarmButton.addListener(new InputListener() {
			public boolean touchDown(InputEvent event, float x, float y,
					int pointer, int button) {
				selectedFarm = new TutorialFarm();
				selectedScreen = new TutorialFarmScreen(game);
				setupSeasonMenu();
				seasonWindow.setVisible(true);
				return true;
			}
		});

		// This line of code will take the user to the farm on click or touch of
		// the rainforest farm
		rainforestFarmButton.addListener(new InputListener() {
			public boolean touchDown(InputEvent event, float x, float y,
					int pointer, int button) {
				selectedFarm = new RainforestFarm();
				selectedScreen = new RainforestFarmScreen(game);
				setupSeasonMenu();
				seasonWindow.setVisible(true);
				// AbstractFarmScreen afs = new AbstractFarmScreen(game);
				// game.setScreen(afs);
				return true;
			}
		});

		// This line of code will take the user to the farm on click or touch of
		// the desert farm
		desertFarmButton.addListener(new InputListener() {
			@Override
			public boolean touchDown(InputEvent event, float x, float y,
					int pointer, int button) {
				selectedFarm = new DesertFarm();
				selectedScreen = new DesertFarmScreen(game);
				setupSeasonMenu();
				seasonWindow.setVisible(true);
				//AbstractFarmScreen afs = new AbstractFarmScreen(game);
				//game.setScreen(afs);
				return true;
			}
		});

		// This line of code will take the user to the farm on click or touch of
		// the snow farm
		snowFarmButton.addListener(new InputListener() {
			public boolean touchDown(InputEvent event, float x, float y,
					int pointer, int button) {
				selectedFarm = new SnowFarm();
				selectedScreen = new SnowFarmScreen(game);
				setupSeasonMenu();
				seasonWindow.setVisible(true);
				//AbstractFarmScreen afs = new AbstractFarmScreen(game);
				//game.setScreen(afs);
				return true;
			}
		});
		// Table.drawDebug(stage);
	}

	public void setupSeasonMenu() {
		
		// Gdx.input.setInputProcessor(plantMenuStage);

		Texture spring = new Texture(Gdx.files.internal("textures/spring.png"));
		Texture summer = new Texture(Gdx.files.internal("textures/summer.png"));
		Texture fall = new Texture(Gdx.files.internal("textures/fall.png"));
		Texture winter = new Texture(Gdx.files.internal("textures/winter.png"));

		TextureRegion springImage = new TextureRegion(spring);
		TextureRegion summerImage = new TextureRegion(summer);
		TextureRegion fallImage = new TextureRegion(fall);
		TextureRegion winterImage = new TextureRegion(winter);

		Button springButton = new Button(new Image(springImage), skin);
		Button summerButton = new Button(new Image(summerImage), skin);
		Button fallButton = new Button(new Image(fallImage), skin);
		Button winterButton = new Button(new Image(winterImage), skin);
		TextButton playFarmButton = new TextButton("Play Farm", skin);

		seasonWindow = new Window("Seasons for this farm", skin);
		seasonWindow.setModal(false);
		seasonWindow.setMovable(false);
		seasonWindow.setVisible(false);
		seasonWindow.setPosition(WINDOW_X, WINDOW_Y);
		seasonWindow.defaults().spaceBottom(10);
		seasonWindow.row().fill().expandX();

		/* Decide the season order */
		for (Season s : selectedFarm.getSeasons()) {
			switch (s.getSeasonType()) {
			case SPRING:
				seasonWindow.add(new Button(new Image(springImage), skin)).size(75, 115);
				break;
			case SUMMER:
				seasonWindow.add(new Button(new Image(summerImage), skin)).size(75, 115);
				break;
			case FALL:
				seasonWindow.add(new Button(new Image(fallImage), skin)).size(75, 115);
				break;
			case WINTER:
				seasonWindow.add(new Button(new Image(winterImage), skin)).size(75, 115);
				break;
			default:
				seasonWindow.add(new Button(new Image(springImage), skin)).size(75, 115);
				break;
			}
		}
		seasonWindow.row();
		seasonWindow.add(playFarmButton).colspan(selectedFarm.getSeasons().length).width(200);
		seasonWindow.pack();
		super.addActor(seasonWindow);

		playFarmButton.addListener(new InputListener() {
			public boolean touchDown(InputEvent event, float x, float y,
					int pointer, int button) {
				//seasonWindow.setVisible(false);
				game.setScreen(selectedScreen);
				return true;
			}
		});
	}

}
