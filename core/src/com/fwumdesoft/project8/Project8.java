package com.fwumdesoft.project8;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.Disposable;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
 
/**
 * The main entry point of the game
 */
public class Project8 extends ApplicationAdapter {
	Renderer rend;
	Overworld world;
	List<Disposable> manualCleanup;
	Inventory inventory;
	CircuitDesigner designer;
	Viewport viewport;
	
	@Override
	public void create () {
		manualCleanup = new ArrayList<>();
		
		SpriteBatch batch = new SpriteBatch();
		manualCleanup.add(batch);
		
		world = new Overworld(200);
		
		List<FileHandle> assetsFiles = Arrays.asList(Gdx.files.internal(".").list());

		AssetManager assets = new AssetManager();
		assetsFiles.stream()
			.map(file -> file.name())
			.filter(string -> string.endsWith("png") || string.endsWith("jpg"))
			.forEach(name -> assets.load(name, Texture.class));
		assets.finishLoading();
		manualCleanup.add(assets);
		
		rend = new Renderer(batch, new BitmapFont(), assets, 32, 64, 640, 480);

		inventory = new Inventory();
		
		Camera camera = new OrthographicCamera(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
		camera.position.x = Gdx.graphics.getWidth() / 2;
		camera.position.y = Gdx.graphics.getHeight() / 2;
		viewport = new FitViewport(640, 480, camera);
		
		Gdx.input.setInputProcessor(new OverworldInput(world));
		
		designer = new CircuitDesigner(640, 480, 64);
	}

	@Override
	public void render () {
		Gdx.gl.glClearColor(1, 1, 1, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
//		rend.renderOverworld(world, inventory);
		designer.update(viewport);
		rend.renderCircuit(designer.getCircuit(), inventory);
	}
	
	@Override
	public void resize(int width, int height) {
		viewport.update(width, height);
	}
	
	@Override
	public void dispose() {
		manualCleanup.forEach(x -> x.dispose());
	}
}
