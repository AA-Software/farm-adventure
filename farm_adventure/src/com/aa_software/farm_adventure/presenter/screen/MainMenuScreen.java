package com.aa_software.farm_adventure.presenter.screen;

import com.aa_software.farm_adventure.model.audio.Sounds;
import com.aa_software.farm_adventure.presenter.FarmAdventure;
import com.aa_software.farm_adventure.presenter.screen.farm_screen.TutorialFarmScreen;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;

public class MainMenuScreen extends AbstractScreen {

	public static final Sounds SOUNDS = Sounds.getInstance();
	private static final int BUTTON_WIDTH = 300;
	private static final int BUTTON_HEIGHT = 60;
	private static final int TITLE_SPACE = 50;
	private static final int BUTTON_SPACE = 10;

	private Table table;

	public MainMenuScreen() {
		super();
	}

	protected void checkBackButton() {
		if (Gdx.input.isKeyPressed(Keys.BACK)) {
			Gdx.input.setCatchBackKey(true);
			Gdx.app.exit();
		}
	}

	@Override
	public void dispose() {
		super.dispose();
	}

	@Override
	public void render(float delta) {
		super.render(delta);
		checkBackButton();
	}

	private void setupMainMenu() {
		// Create table
		table = new Table(skin);
		table.setFillParent(true);

		stage.addActor(table);

		// Add label to table
		Label heading = new Label("Welcome to FarmAdventure for Android!", skin);
		table.add(heading).spaceBottom(TITLE_SPACE);
		table.row();

		// register the button "start game"
		TextButton startGameButton = new TextButton("Start game", skin);

		// Start Music
		SOUNDS.playMusic();

		// This line of code will take the user to the world screen on click or
		// touch
		startGameButton.addListener(new InputListener() {
			@Override
			public boolean touchDown(InputEvent event, float x, float y,
					int pointer, int button) {
				SOUNDS.playClick();
				FarmAdventure.getInstance().setScreen(new WorldScreen());
				dispose();
				return true;
			}
		});

		table.add(startGameButton).size(BUTTON_WIDTH, BUTTON_HEIGHT).uniform()
				.spaceBottom(BUTTON_SPACE);
		table.row();

		// register tutorial button
		TextButton tutorialButton = new TextButton("Tutorial", skin);

		// Listener to send to tutorial game
		tutorialButton.addListener(new InputListener() {
			@Override
			public boolean touchDown(InputEvent event, float x, float y,
					int pointer, int button) {
				SOUNDS.playClick();
				FarmAdventure.getInstance().setScreen(new TutorialFarmScreen());
				dispose();
				return true;
			}
		});

		table.add(tutorialButton).size(BUTTON_WIDTH, BUTTON_HEIGHT).uniform()
				.spaceBottom(BUTTON_SPACE);
		table.row();

		// register tutorial button
		TextButton optionsButton = new TextButton("Settings", skin);

		// Listener to send to tutorial game
		optionsButton.addListener(new InputListener() {
			@Override
			public boolean touchDown(InputEvent event, float x, float y,
					int pointer, int button) {
				SOUNDS.playClick();
				FarmAdventure.getInstance().setScreen(new OptionsScreen());
				dispose();
				return true;
			}
		});
		table.add(optionsButton).size(BUTTON_WIDTH, BUTTON_HEIGHT).uniform()
				.spaceBottom(BUTTON_SPACE);
		table.row();
	}

	@Override
	public void show() {
		super.show();
		setupMainMenu();
	}

}
