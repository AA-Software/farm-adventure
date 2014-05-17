package com.aa_software.farm_adventure.presenter.screen;

import com.aa_software.farm_adventure.presenter.FarmAdventure;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;

public abstract class AbstractScreen implements Screen {

	public static final int STAGE_WIDTH = 480;
	public static final int STAGE_HEIGHT = 800;
	public static final int SCREEN_WIDTH = 640;
	public static final int SCREEN_HEIGHT = 1024;

	protected OrthographicCamera camera;
	protected Skin skin;
	protected Stage stage;

	@Override
	public void dispose() {
		skin.dispose();
		stage.dispose();
	}

	protected String getName() {
		return getClass().getSimpleName();
	}

	@Override
	public void hide() {
		FarmAdventure.log("disposing: " + getName());
		dispose();
	}

	@Override
	public void pause() {

	}

	@Override
	public void render(float delta) {
		Gdx.gl.glClearColor(0f, 0f, 0f, 1f);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		stage.act(delta);
		stage.draw();
	}

	@Override
	public void resize(int width, int height) {
	}

	@Override
	public void resume() {
	}

	@Override
	public void show() {
		stage = new Stage();
		Gdx.input.setInputProcessor(stage);

		skin = new Skin(Gdx.files.internal("skin/uiskin.json"));

		camera = new OrthographicCamera();
		camera.setToOrtho(false, SCREEN_WIDTH, SCREEN_HEIGHT);

		FarmAdventure.log("Showing screen: " + getName());
	}
}
