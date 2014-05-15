package com.aa_software.farm_adventure.presenter;

import com.aa_software.farm_adventure.model.audio.Sounds;
import com.aa_software.farm_adventure.model.player.Player;
import com.aa_software.farm_adventure.presenter.screen.MainMenuScreen;
import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.FPSLogger;
import com.badlogic.gdx.graphics.GLTexture;

public class FarmAdventure extends Game {

	public static final String LOG = FarmAdventure.class.getSimpleName();
	public static final boolean DEV_MODE = false;
	private static FPSLogger FpsLogger;

	private static FarmAdventure Instance = null;

	public static FarmAdventure getInstance() {
		if (Instance == null) {
			Instance = new FarmAdventure();
		}
		return Instance;
	}

	/**
	 * Whenever the game is in developer mode everything is logged in console or
	 * logcat
	 * 
	 * @param message
	 *            message to be logged
	 */
	public static void log(String message) {
		if (DEV_MODE)
			Gdx.app.log(FarmAdventure.class.getSimpleName(), message);
	}

	/**
	 * Logs FPS whenever in DEV_MODE
	 */
	public static void logFPS() {
		if (DEV_MODE)
			FpsLogger.log();
	}

	protected FarmAdventure() {
	}

	@Override
	public void create() {
		GLTexture.setEnforcePotImages(false);
		if (DEV_MODE)
			FpsLogger = new FPSLogger();

		Sounds.getInstance();

		Player.getInstance().loadData();

		MainMenuScreen mms = new MainMenuScreen();
		super.setScreen(mms);
	}

	@Override
	public void dispose() {
		super.dispose();
		Player.getInstance().saveData();
		Sounds.getInstance().dispose();
	}

	@Override
	public void pause() {
		super.pause();
	}

	@Override
	public void render() {
		super.render();
		if (DEV_MODE)
			logFPS();
	}

	@Override
	public void resize(int width, int height) {
		super.resize(width, height);
	}

	@Override
	public void resume() {
		super.resume();
	}

	@Override
	public void setScreen(Screen screen) {
		super.setScreen(screen);
	}
}
