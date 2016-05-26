package com.fwumdesoft.project8;

import java.util.Arrays;
import java.util.ArrayList;
import java.util.List;
import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.utils.Disposable;
import com.fwumdesoft.project8.CircuitComponent.Type;
 
/**
 * The main entry point of the game
 */
public class Project8 extends ApplicationAdapter {
	Renderer rend;
	Overworld world;
	List<Disposable> manualCleanup;
	Inventory inventory;
	CircuitComponent[][] circuit;
	
	@Override
	public void create () {
		CircuitComponent n = null;
		CircuitComponent w = new CircuitComponent();
		CircuitComponent v = new CircuitComponent();
		CircuitComponent r = new CircuitComponent();
		w.type = Type.WIRE;
		v.type = Type.BATTERY;
		v.voltageDif = 10;
		r.type = Type.RESISTOR;
		r.resistance = 5;
		circuit = new CircuitComponent[][]{{n, n, n, n, n, n, n},
														{w, w, w, v, w, w, n},
														{w, n, w, n, n, w, n},
														{w, w, w, r, w, w, n},
														{n, n, n, n, n, n, n}};
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
		
		Gdx.input.setInputProcessor(new OverworldInput(world));
	}

	@Override
	public void render () {
		Gdx.gl.glClearColor(0, 0, 0, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		rend.renderOverworld(world, inventory);
		//rend.renderCircuit(circuit, inventory);
	}
	
	@Override
	public void dispose() {
		manualCleanup.forEach(x -> x.dispose());
	}
}
