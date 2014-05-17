package com.aa_software.farm_adventure.model.player;

import com.aa_software.farm_adventure.model.audio.Sounds;
import com.aa_software.farm_adventure.model.item.AbstractItem;
import com.badlogic.gdx.Gdx;

/**
 * Represents the player, with his preferences and stats. This is a singleton
 * class.
 * 
 * @author Bebop
 * 
 */
public class Player {

	public static final int STARTING_BANKROLL = 100;

	private static Player Instance;

	public static Player getInstance() {
		if (Instance == null) {
			Instance = new Player();
		}
		return Instance;
	}

	private Stats stats;

	private Preferences preferences;

	private Player() {
		stats = new Stats();
		this.preferences = new Preferences();
	}

	public Boolean buyItem(AbstractItem item) {
		int itemCost = item.getCost();
		if (this.stats.getScore() < itemCost) {
			// TODO: Let the player know they do not have the funds.
			return false;
		} else {
			this.stats.setScore(this.stats.getScore() - itemCost);
			return true;
		}
	}

	public int getBankroll() {
		return this.stats.getScore();
	}

	public Preferences getPreferences() {
		return preferences;
	}

	public void loadData() {
		com.badlogic.gdx.Preferences prefs = Gdx.app
				.getPreferences("FarmAdventure.Player.Preferences");
		Sounds s = Sounds.getInstance();

		if (prefs.contains("BANKROLL")) {
			stats.setScore(prefs.getInteger("BANKROLL", STARTING_BANKROLL));
			s.setMasterVolume(prefs.getFloat("MASTERVOLUME", 1));
			s.setMusicVolume(prefs.getFloat("MUSICVOLUME", 1));
			s.setSoundVolume(prefs.getFloat("SOUNDVOLUME", 1));
		}
	}

	public void saveData() {
		com.badlogic.gdx.Preferences prefs = Gdx.app
				.getPreferences("FarmAdventure.Player.Preferences");
		Sounds s = Sounds.getInstance();

		prefs.putInteger("BANKROLL", stats.getScore());
		prefs.putFloat("MASTERVOLUME", s.getMasterVolume());
		prefs.putFloat("MUSICVOLUME", s.getMusicVolume());
		prefs.putFloat("SOUNDVOLUME", s.getSoundVolume());

		// bulk update your preferences
		prefs.flush();
	}

	public void sellItem(AbstractItem item) {
		this.stats.setScore(this.stats.getScore() + item.getValue());
	}

	public void setBankroll(int bankroll) {
		this.stats.setScore(bankroll);
	}

	public void setPreferences(Preferences preferences) {
		this.preferences = preferences;
	}
}
