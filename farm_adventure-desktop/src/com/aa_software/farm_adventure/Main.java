package com.aa_software.farm_adventure;

import com.aa_software.farm_adventure.presenter.FarmAdventure;
import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;

public class Main {
	public static void main(String[] args) {
		LwjglApplicationConfiguration cfg = new LwjglApplicationConfiguration();
		cfg.title = "Farm Adventure";
		cfg.width = 600;
		cfg.height = 800;

		new LwjglApplication(FarmAdventure.getInstance(), cfg);
	}
}