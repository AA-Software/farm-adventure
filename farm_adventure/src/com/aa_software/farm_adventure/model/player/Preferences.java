package com.aa_software.farm_adventure.model.player;

/**
 * 
 * Holds the player's preferences from the option menu.
 * 
 * @author Bebop
 * 
 */
public class Preferences {
	private static boolean isMuted = false;
	private float soundVolume;
	private float masterVolume;
	private float musicVolume;

	public float getMasterVolume() {
		return masterVolume;
	}

	public float getSoundVolume() {
		return soundVolume;
	}

	public boolean isMuted() {
		return isMuted;
	}

	public void setMasterVolume(float masterVolume) {
		this.masterVolume = masterVolume;
	}

	public void setMuted(boolean muted) {
		isMuted = muted;
	}

	public void setSoundVolume(float soundVolume) {
		this.soundVolume = soundVolume;
	}

	public float getMusicVolume() {
		return musicVolume;
	}

	public void setMusicVolume(float musicVolume) {
		this.musicVolume = musicVolume;
	}
}
