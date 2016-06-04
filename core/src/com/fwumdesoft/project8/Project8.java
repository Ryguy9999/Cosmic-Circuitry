package com.fwumdesoft.project8;

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
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;

/**
 * The main entry point of the game
 */
public class Project8 extends ApplicationAdapter
{
	private Renderer rend;
	private Overworld world;
	private Inventory inventory;
	private CircuitInput input;
	private Viewport viewport;
	private Vector2 mousePosition;
	private AssetManager assets;
	private OverworldInput overInput;
	private Vector2 circuitCamera;
	private SpriteBatch batch;
	
	@Override
	public void create()
	{
		batch = new SpriteBatch();

		loadAssets();
		initSimulation();
		
		Camera camera = new OrthographicCamera(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
		camera.position.x = Gdx.graphics.getWidth() / 2;
		camera.position.y = Gdx.graphics.getHeight() / 2;
		viewport = new FitViewport(640, 480, camera);
		
		ParticleSystem.addParticleType("electricity", p -> {
			p.position.setVertices(new float[]{0,0, 4,0, 0,4, 4,4});
			p.position.setScale(1, 1);
			p.position.setOrigin(2, 2);
			p.lifetime = 10 + (int)(Math.random() * 15);
			p.texture = new TextureRegion(assets.get("electricity_particle.png", Texture.class));
			p.velocity.set(((float)Math.random() - 0.5f) * 5, ((float)Math.random() - 0.5f) * 5);
			p.rotationalVelocity = 30;
			p.scaleVelocity = 0;
		});
		ParticleSystem.addParticleType("smoke", p -> {
			p.position.setVertices(new float[]{0,0, 16,0, 0,16, 16,16});
			p.position.setScale(1, 1);
			p.position.setOrigin(8, 8);
			p.rotationalVelocity = (int)(Math.random() * 20);
			p.lifetime = (int)(Math.random() * 25);
			p.deltaOpacity = -0.05f;
			p.scaleVelocity = (float)Math.random() / 2;
			p.texture = new TextureRegion(assets.get("smoke_puff.png", Texture.class));
			p.velocity.set(((float)Math.random() - 0.5f) * 2, ((float)Math.random()) * 2);
			p.rotationalVelocity = 30;
			p.scaleVelocity = -0.05f;
		});
	}

	public boolean isCircuit = false;

	@Override
	public void render()
	{
		if (Gdx.input.isKeyJustPressed(Keys.GRAVE))
			isCircuit = !isCircuit;
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		if (isCircuit)
		{
			Gdx.gl.glClearColor(0.9f, 0.9f, 0.9f, 1);
			mousePosition.set(Gdx.input.getX(), Gdx.input.getY());
			viewport.unproject(mousePosition);
			int circuitX = (int) ((mousePosition.x + circuitCamera.x) / 64);
			int circuitY = (int) ((mousePosition.y + circuitCamera.y) / 64);
			input.update(circuitX, circuitY);
			rend.renderCircuit(input.getCircuit(), inventory, circuitX, circuitY);
			if(Gdx.input.isKeyJustPressed(Keys.ESCAPE))
			{
				//Exiting a finished circuit means it was still a success
				if(input.getCircuit().isSolved())
					world.circuitSuccess();
				else
					world.circuitFail();
				isCircuit = false;
				rend.resetCircuitCamera();
			}
		} else if(world.gameWon)
			rend.renderCredits();
		else {
			Gdx.gl.glClearColor(0, 0, 0, 1);
			overInput.step();
			rend.renderOverworld(world, inventory);
			if(world.currentCircuit != null) 
			{
				input.setCircuit(world.currentCircuit);
				isCircuit = true;
				world.currentCircuit = null;
			}
		}
		ParticleSystem.tick();
		batch.begin();
		ParticleSystem.draw(batch);
		batch.end();
	}
	
	private void initSimulation()
	{
		inventory = new Inventory();

		world = new Overworld(this, 1000, assets.getAll(Circuit.class, new Array<>()), inventory, true);
		circuitCamera = new Vector2();
		rend = new Renderer(batch, new BitmapFont(), assets, 32, 64, 640, 480, circuitCamera);
		
		Gdx.input.setInputProcessor(overInput = new OverworldInput(this, world));

		input = new CircuitInput(new Circuit(new CircuitComponent[10][20], 0), assets, inventory, circuitCamera);
		mousePosition = new Vector2();
	}
	
	/**
	 * Use to load or refresh game assets
	 */
	private void loadAssets()
	{
		if(assets != null)
		{
			assets.dispose();
		}
		assets = new AssetManager();
		assets.setLoader(Circuit.class, new CircuitIO(assets.getFileHandleResolver()));
		List<FileHandle> assetsFiles = Arrays.asList(Gdx.files.internal(".").list());
		assetsFiles.stream().map(file -> file.name()).filter(string -> string.endsWith("png") || string.endsWith("jpg"))
				.forEach(name -> assets.load(name, Texture.class));
		assetsFiles.stream().map(file -> file.name()).filter(string -> string.endsWith("circuit"))
				.forEach(name -> assets.load(name, Circuit.class));
		assets.finishLoading();
	}
	
	/**
	 * Call when the game should be restarted
	 */
	public void restart()
	{
		initSimulation();
	}
	
	/**
	 * Call when things should be disposed but the application should not end
	 */
	public void diposeAssets()
	{
		assets.dispose();
		batch.dispose();
	}

	@Override
	public void resize(int width, int height)
	{
		viewport.update(width, height);
	}

	@Override
	public void dispose()
	{
		diposeAssets();
	}
}
