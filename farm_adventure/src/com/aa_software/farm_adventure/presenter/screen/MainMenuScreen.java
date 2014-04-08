package com.aa_software.farm_adventure.presenter.screen;

import com.aa_software.farm_adventure.model.audio.Sounds;
import com.aa_software.farm_adventure.presenter.FarmAdventure;
import com.aa_software.farm_adventure.presenter.screen.farm_screen.TutorialFarmScreen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.Label.LabelStyle;

public class MainMenuScreen extends AbstractScreen {

	public static final Sounds sounds = Sounds.getInstance();
	private Skin skin;
	private static final float FONT_SCALE = 1;
	BitmapFont fontType = new BitmapFont();
	LabelStyle style1 = new LabelStyle(fontType, Color.WHITE);
	private static final int BUTTON_WIDTH = 600;
	private static final int BUTTON_HEIGHT = 120;
	private static final int TITLE_SPACE = 100;
	private static final int BUTTON_SPACE = 20;
	
	public MainMenuScreen() {
		super();
	}

	@Override
	public void show() {
		super.show();
		skin = super.getSkin();
		skin.getFont("default-font").scale(2);
		// Create table
		Table table = new Table(skin);
		table.setFillParent(true);

		super.addActor(table);

		// Add label to table
		table.add("Welcome to FarmAdventure for Android!")
		.spaceBottom(TITLE_SPACE);
		table.row();

		// register the button "start game"
		TextButton startGameButton = new TextButton("Start game",skin);

		// Start Music
		sounds.playMusic();

		// This line of code will take the user to the world screen on click or
		// touch
		startGameButton.addListener(new InputListener() {
			@Override
			public boolean touchDown(InputEvent event, float x, float y,
					int pointer, int button) {
				FarmAdventure.getInstance().setScreen(new WorldScreen());
				sounds.playClick();
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
				FarmAdventure.getInstance().setScreen(new TutorialFarmScreen());
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
				FarmAdventure.getInstance().setScreen(new OptionsScreen());
				return true;
			}
		});
		table.add(optionsButton).size(BUTTON_WIDTH, BUTTON_HEIGHT).uniform()
		.spaceBottom(BUTTON_SPACE);
		table.row();
	}

}
