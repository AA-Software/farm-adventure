package com.aa_software.farm_adventure.model.audio;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;

/**
 * A Singleton class. Contains all the functions necessary for a method to
 * access sound effects and music. Plays the sounds and music through libGDX.
 * 
 * @author AA Software
 * 
 */
public class Sounds {
	/**
	 * Represents the sound made when clicking on any button in the game.
	 */
	public final Sound click = Gdx.audio.newSound(Gdx.files
			.internal("sounds/click.mp3"));
	/**
	 * Represents the sound made when buying or selling an item in the market.
	 */
	public final Sound money = Gdx.audio.newSound(Gdx.files
			.internal("sounds/coinClink.mp3"));
	/**
	 * Represents the music played throughout the game.
	 */
	public final Music mainMusic = Gdx.audio.newMusic(Gdx.files
			.internal("sounds/musicTrack1.mp3"));
	/**
	 * Represents the volume level of game sounds, such as "click" and "money".
	 */
	protected float soundVolume;

	/**
	 * Scales the volume of the game.
	 */
	protected float masterVolume;

	private static Sounds Instance;

	/**
	 * Creates a Sounds instance if it doesn't exist, or uses an already
	 * existing one.
	 * 
	 * @return a Singleton instance of the Sounds class
	 */
	public static Sounds getInstance() {
		if (Instance == null) {
			Instance = new Sounds();
		}
		return Instance;
	}

	/**
	 * Constructs a Sounds object with the appropriate sounds and music.
	 */
	private Sounds() {
		soundVolume = 1;
		masterVolume = 1;
	}

	public void dispose() {
		click.dispose();
		money.dispose();
		mainMusic.dispose();
	}

	/**
	 * Returns the master volume of the game.
	 * 
	 * @return masterVolume
	 */
	public float getMasterVolume() {
		return masterVolume;
	}

	public float getMusicVolume() {
		return mainMusic.getVolume();
	}

	/**
	 * Returns the volume of sounds within the game.
	 * 
	 * @return soundVolume
	 */
	public float getSoundVolume() {
		return soundVolume;
	}

	/**
	 * Pauses the music.
	 */
	public void pauseMusic() {
		mainMusic.pause();
	}

	/**
	 * Plays the click sound using libGDX.
	 */
	public void playClick() {
		click.setVolume(click.play(), soundVolume);
	}

	/**
	 * Plays the money sound using libGDX.
	 */
	public void playMoney() {
		money.setVolume(money.play(), soundVolume);
	}

	/**
	 * Plays the main music using libGDX.
	 */
	public void playMusic() {
		mainMusic.setLooping(true);
		mainMusic.play();
	}

	/**
	 * Sets the volume of the master volume
	 * 
	 * @param vol
	 *            a float in the range [0,1]. 0 is the lowest volume and 1 is
	 *            the highest.
	 */
	public void setMasterVolume(float vol) {
		masterVolume = vol;
	}

	/**
	 * Sets the volume of the music.
	 * 
	 * @param vol
	 *            a float in the range [0,1]. 0 is the lowest volume and 1 is
	 *            the highest.
	 */
	public void setMusicVolume(float vol) {
		mainMusic.setVolume(masterVolume * vol);
	}

	/**
	 * Sets the volume of the sounds.
	 * 
	 * @param vol
	 *            a float in the range [0,1]. 0 is the lowest volume and 1 is
	 *            the highest.
	 */
	public void setSoundVolume(float vol) {
		soundVolume = masterVolume * vol;
	}
}