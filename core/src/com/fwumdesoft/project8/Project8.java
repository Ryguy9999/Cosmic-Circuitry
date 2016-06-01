package com.fwumdesoft.project8;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Disposable;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;

/**
 * The main entry point of the game
 */
public class Project8 extends ApplicationAdapter
{
	Renderer rend;
	Overworld world;
	List<Disposable> manualCleanup;
	Inventory inventory;
	CircuitInput input;
	Viewport viewport;
	Vector2 mousePosition;

	@Override
	public void create()
	{
		manualCleanup = new ArrayList<>();

		SpriteBatch batch = new SpriteBatch();
		manualCleanup.add(batch);

		List<FileHandle> assetsFiles = Arrays.asList(Gdx.files.internal(".").list());

		AssetManager assets = new AssetManager();
		assets.setLoader(Circuit.class, new CircuitIO(assets.getFileHandleResolver()));
		assetsFiles.stream().map(file -> file.name()).filter(string -> string.endsWith("png") || string.endsWith("jpg"))
				.forEach(name -> assets.load(name, Texture.class));
		assetsFiles.stream().map(file -> file.name()).filter(string -> string.endsWith("circuit"))
				.forEach(name -> assets.load(name, Circuit.class));
		assets.finishLoading();
		manualCleanup.add(assets);

		world = new Overworld(500, assets.getAll(Circuit.class, new Array<>()));
		rend = new Renderer(batch, new BitmapFont(), assets, 32, 64, 640, 480);

		inventory = new Inventory();
		inventory.addComponent(CircuitComponent.battery());
		inventory.addComponent(CircuitComponent.resistor());

		Camera camera = new OrthographicCamera(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
		camera.position.x = Gdx.graphics.getWidth() / 2;
		camera.position.y = Gdx.graphics.getHeight() / 2;
		viewport = new FitViewport(640, 480, camera);

		Gdx.input.setInputProcessor(new OverworldInput(world));

		input = new CircuitInput(new Circuit(new CircuitComponent[10][5], 0), assets, inventory);
		mousePosition = new Vector2();
	}

	private boolean isCircuit = true;

	@Override
	public void render()
	{
		if (Gdx.input.isKeyJustPressed(Keys.GRAVE))
			isCircuit = !isCircuit;
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		if (isCircuit)
		{
			Gdx.gl.glClearColor(1, 1, 1, 1);
			mousePosition.set(Gdx.input.getX(), Gdx.input.getY());
			viewport.unproject(mousePosition);
			int circuitX = (int) (mousePosition.x / 64);
			int circuitY = (int) (mousePosition.y / 64);
			input.update(circuitX, circuitY);
			rend.renderCircuit(input.getCircuit(), inventory, circuitX, circuitY);
		} else
		{
			Gdx.gl.glClearColor(0, 0, 0, 1);
			rend.renderOverworld(world, inventory);
			if(world.currentCircuit != null) 
			{
				input.setCircuit(world.currentCircuit);
				isCircuit = true;
				world.currentCircuit = null;
			}
		}
	}

	@Override
	public void resize(int width, int height)
	{
		viewport.update(width, height);
	}

	@Override
	public void dispose()
	{
		manualCleanup.forEach(x -> x.dispose());
	}
}
