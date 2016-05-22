package com.fwumdesoft.project8;

import java.util.ArrayList;
import java.util.List;
import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.Disposable;

/**
 * The main entry point of the game
 */
public class Project8 extends ApplicationAdapter {
	Renderer rend;
	Overworld world;
	List<Disposable> manualCleanup;
	@Override
	public void create () {
		manualCleanup = new ArrayList<>();
		
		SpriteBatch batch = new SpriteBatch();
		manualCleanup.add(batch);
		
		world = new Overworld(40);
		
		AssetManager assets = new AssetManager();
		assets.load("player.png", Texture.class);
		assets.load("station_wall.png", Texture.class);
		assets.load("station_door.png", Texture.class);
		assets.load("station_floor.png", Texture.class);
		assets.finishLoading();
		manualCleanup.add(assets);
		
		rend = new Renderer(batch, assets, 32, 640, 480);
	}

	@Override
	public void render () {
		Gdx.gl.glClearColor(1, 0, 0, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		rend.renderOverworld(world);
	}
	
	@Override
	public void dispose() {
		manualCleanup.forEach(x -> x.dispose());
	}
}
