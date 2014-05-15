package com.aa_software.farm_adventure.presenter.screen;

import com.aa_software.farm_adventure.model.audio.Sounds;
import com.aa_software.farm_adventure.presenter.FarmAdventure;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Slider;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;

public class OptionsScreen extends AbstractScreen {

	public final Sounds sounds = Sounds.getInstance();

	private Slider masterVolumeSlider;
	private Slider gameVolumeSlider;
	private Slider musicVolumeSlider;
	private Label masterValueLabel;
	private Label gameValueLabel;
	private Label musicValueLabel;

	public OptionsScreen() {
		super();
	}

	private void checkBackButton() {
		if (Gdx.input.isKeyPressed(Keys.BACK)) {
			Gdx.input.setCatchBackKey(true);
			FarmAdventure.getInstance().setScreen(new MainMenuScreen());
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

	private void setupOptionsMenu() {
		// Create table
		Table table = new Table(skin);
		table.setFillParent(true);
		stage.addActor(table);

		// Add label to table
		table.add("Option Screen").spaceBottom(50).colspan(3);
		table.row();
		table.row().spaceBottom(50);

		// Create masterVolume label
		table.add("Master Volume: ");
		masterVolumeSlider = new Slider(0, 100, 5, false, skin);
		masterVolumeSlider.setValue(sounds.getMasterVolume());

		// Add listener
		masterVolumeSlider.addListener(new ChangeListener() {
			@Override
			public void changed(ChangeEvent event, Actor actor) {
				masterValueLabel.setText((int) masterVolumeSlider.getValue()
						+ "%");
				sounds.setMusicVolume((masterVolumeSlider.getValue() / 100)
						* (musicVolumeSlider.getValue() / 100));
				sounds.setSoundVolume((masterVolumeSlider.getValue() / 100)
						* (gameVolumeSlider.getValue() / 100));
			}
		});
		table.add(masterVolumeSlider);
		masterValueLabel = new Label((int) masterVolumeSlider.getValue() + "%",
				skin);
		table.add(masterValueLabel);
		table.row();

		table.row().spaceBottom(50);

		// Create gameVolume label
		table.add("Game Volume: ");
		gameVolumeSlider = new Slider(0, 100, 5, false, skin);
		gameVolumeSlider.setValue(100 * sounds.getSoundVolume());

		// Add listener
		gameVolumeSlider.addListener(new ChangeListener() {
			@Override
			public void changed(ChangeEvent event, Actor actor) {
				gameValueLabel.setText((int) gameVolumeSlider.getValue() + "%");
				sounds.setSoundVolume((masterVolumeSlider.getValue() / 100)
						* (gameVolumeSlider.getValue() / 100));
			}
		});
		table.add(gameVolumeSlider);

		gameValueLabel = new Label((int) gameVolumeSlider.getValue() + "%",
				skin);
		table.add(gameValueLabel);
		table.row();

		// Create musicVolume label
		table.add("Music Volume: ");
		musicVolumeSlider = new Slider(0, 100, 5, false, skin);
		musicVolumeSlider.setValue(100 * sounds.getMusicVolume());

		// Add listener
		musicVolumeSlider.addListener(new ChangeListener() {
			@Override
			public void changed(ChangeEvent event, Actor actor) {
				musicValueLabel.setText("" + (int) musicVolumeSlider.getValue()
						+ "%");
				sounds.setMusicVolume((masterVolumeSlider.getValue() / 100)
						* (musicVolumeSlider.getValue() / 100));
			}
		});
		table.add(musicVolumeSlider);
		musicValueLabel = new Label("" + (int) musicVolumeSlider.getValue()
				+ "%", skin);
		table.add(musicValueLabel);
		table.row();

		// back button
		TextButton backButton = new TextButton("Back", skin);
		backButton.addListener(new InputListener() {
			@Override
			public boolean touchDown(InputEvent event, float x, float y,
					int pointer, int button) {
				FarmAdventure.getInstance().setScreen(new MainMenuScreen());
				dispose();
				return true;
			}
		});
		table.add(backButton).spaceTop(200).colspan(3).width(300);
	}

	@Override
	public void show() {
		super.show();
		setupOptionsMenu();
	}

}
