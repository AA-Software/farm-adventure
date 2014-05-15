package com.aa_software.farm_adventure.presenter.screen;

import java.util.ArrayList;
import java.util.List;

import com.aa_software.farm_adventure.model.audio.Sounds;
import com.aa_software.farm_adventure.model.farm.Biome;
import com.aa_software.farm_adventure.model.season.Season;
import com.aa_software.farm_adventure.presenter.FarmAdventure;
import com.aa_software.farm_adventure.presenter.screen.farm_screen.FarmScreen;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.Window;

/**
 * Creates a new WorldScreen and displays it. The screen consists of the
 * background map and clickable buttons which take the player to the appropriate
 * farm.
 * 
 * @author AA Software
 * 
 */
public class WorldScreen extends AbstractScreen {

	/* Sound */
	public static final Sounds SOUNDS = Sounds.getInstance();

	public static final float WINDOW_X = (float) (STAGE_WIDTH * .32);
	public static final float WINDOW_Y = (float) (STAGE_HEIGHT * .40);

	protected float screenWidth = STAGE_WIDTH;
	protected float screenHeight = STAGE_HEIGHT;

	protected Window seasonWindow;
	protected Texture spring, summer, winter, fall;
	protected Texture background;
	protected Texture tutorialTexture, rainforestTexture, desertTexture,
			snowTexture;

	protected static final Biome.Type[] BIOMES = { Biome.Type.GRASSLAND,
			Biome.Type.TROPICAL, Biome.Type.TEMPERATE, Biome.Type.BOREAL };

	/**
	 * Constructs a WorldScreen based on the current game.
	 * 
	 * @param game
	 *            the current FarmAdventure class that is being played
	 */
	public WorldScreen() {
		super();
	}

	private void checkBackButton() {
		if (Gdx.input.isKeyPressed(Keys.BACK)) {
			Gdx.input.setCatchBackKey(true);
			FarmAdventure.getInstance().setScreen(new MainMenuScreen());
			dispose();
		}
	}

	@Override
	public void dispose() {
		super.dispose();
		spring.dispose();
		summer.dispose();
		fall.dispose();
		winter.dispose();
		background.dispose();
		tutorialTexture.dispose();
		rainforestTexture.dispose();
		desertTexture.dispose();
		snowTexture.dispose();
	}

	@Override
	public void render(float delta) {
		super.render(delta);
		checkBackButton();
	}

	/**
	 * Sets up the window to display the seasons for the currently selected farm
	 * and a button to start the selected farm.
	 */
	public void setupSeasonMenu(final Biome.Type biome) {

		spring = new Texture(Gdx.files.internal("textures/spring.png"));
		summer = new Texture(Gdx.files.internal("textures/summer.png"));
		fall = new Texture(Gdx.files.internal("textures/fall.png"));
		winter = new Texture(Gdx.files.internal("textures/winter.png"));

		TextureRegion springImage = new TextureRegion(spring);
		TextureRegion summerImage = new TextureRegion(summer);
		TextureRegion fallImage = new TextureRegion(fall);
		TextureRegion winterImage = new TextureRegion(winter);

		TextButton playFarmButton = new TextButton("Play Farm", skin);

		seasonWindow = new Window("Season cycle for this farm", skin);
		seasonWindow.setModal(false);
		seasonWindow.setMovable(false);
		seasonWindow.setVisible(false);
		seasonWindow.setPosition(WINDOW_X, WINDOW_Y);
		seasonWindow.defaults().spaceBottom(10);
		seasonWindow.row().fill().expandX();

		/* Decide the season order */
		Button seasonButton;
		for (Season.Type s : Biome.getSeasons(biome)) {
			switch (s) {
			case SPRING:
				seasonButton = new Button(new Image(springImage), skin);
				seasonButton.padBottom(0).padLeft(0).padRight(0).padTop(0);
				seasonWindow.add(seasonButton).size(75, 115);
				break;
			case SUMMER:
				seasonButton = new Button(new Image(summerImage), skin);
				seasonButton.padBottom(0).padLeft(0).padRight(0).padTop(0);
				seasonWindow.add(seasonButton).size(75, 115);
				break;
			case FALL:
				seasonButton = new Button(new Image(fallImage), skin);
				seasonButton.padBottom(0).padLeft(0).padRight(0).padTop(0);
				seasonWindow.add(seasonButton).size(75, 115);
				break;
			case WINTER:
				seasonButton = new Button(new Image(winterImage), skin);
				seasonButton.padBottom(0).padLeft(0).padRight(0).padTop(0);
				seasonWindow.add(seasonButton).size(75, 115);
				break;
			default:
				seasonWindow.add(new Button(new Image(springImage), skin))
						.size(75, 115);
				break;
			}
		}
		seasonWindow.row();
		seasonWindow.add(playFarmButton).colspan(4).width(200);
		seasonWindow.pack();
		stage.addActor(seasonWindow);

		playFarmButton.addListener(new InputListener() {
			@Override
			public boolean touchDown(InputEvent event, float x, float y,
					int pointer, int button) {
				FarmAdventure.getInstance().setScreen(new FarmScreen(biome));
				SOUNDS.playClick();
				dispose();
				return true;
			}
		});

	}

	/**
	 * Sets up the world map screen with a background map and farm buttons. When
	 * a farm button is selected, the type of farm selected is saved, and
	 * setupSeasonMenu is called.
	 */
	public void setupWorldMap() {
		// Create background and table
		background = new Texture(Gdx.files.internal("world/WorldMap.png"));
		Image backgroundImage = new Image(background);
		backgroundImage.setFillParent(true);
		stage.addActor(backgroundImage);

		Table table = new Table();
		stage.addActor(table);
		table.setFillParent(true);

		// Set up the texture for the button
		tutorialTexture = new Texture(
				Gdx.files.internal("world/TutorialFarm.png"));
		rainforestTexture = new Texture(
				Gdx.files.internal("world/RainforestFarm.png"));
		desertTexture = new Texture(Gdx.files.internal("world/DesertFarm.png"));
		snowTexture = new Texture(Gdx.files.internal("world/SnowFarm.png"));

		// Create buttons
		List<Button> farmButtons = new ArrayList<Button>();
		Button grasslandFarmButton = new Button(new Image(tutorialTexture),
				skin);
		farmButtons.add(grasslandFarmButton);
		Button tropicalFarmButton = new Button(new Image(rainforestTexture),
				skin);
		farmButtons.add(tropicalFarmButton);
		Button temperateFarmButton = new Button(new Image(desertTexture), skin);
		farmButtons.add(temperateFarmButton);
		Button borealFarmButton = new Button(new Image(snowTexture), skin);
		farmButtons.add(borealFarmButton);

		int index = 0;
		for (final Biome.Type biome : BIOMES) {
			farmButtons.get(index).addListener(new InputListener() {
				@Override
				public boolean touchDown(InputEvent event, float x, float y,
						int pointer, int button) {
					setupSeasonMenu(biome);
					seasonWindow.setVisible(true);
					SOUNDS.playClick();
					return true;
				}
			});
			index++;
		}
		// Add buttons to the correct position
		table.add(grasslandFarmButton).size(100, 100).top().left()
				.padTop((float) (.05 * screenHeight))
				.padLeft((float) (.15 * screenWidth));
		table.row();
		table.add(tropicalFarmButton).size(100, 100).expand().top().right()
				.padTop((float) (.20 * screenHeight))
				.padRight((float) (.10 * screenWidth));
		table.row();
		table.add(temperateFarmButton).size(100, 100).expand().bottom().left()
				.padBottom((float) (.05 * screenHeight))
				.padLeft((float) (.07 * screenWidth));
		table.row();
		table.add(borealFarmButton).size(100, 100).expand().bottom().right()
				.padBottom((float) (.10 * screenHeight))
				.padRight((float) (.15 * screenWidth));
		table.row();
	}

	/**
	 * Creates and displays the world map and buttons. Handles the on click for
	 * the buttons - starting up a new farm.
	 */
	@Override
	public void show() {
		super.show();
		setupWorldMap();
	}

}
