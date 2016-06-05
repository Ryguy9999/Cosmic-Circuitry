package com.fwumdesoft.project8;

import java.util.Arrays;
import java.util.List;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
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
	private TransitionManager transition;
	private MusicPlayer music;
	
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
		
		ParticleSystem.init(assets);
		music = new MusicPlayer(assets);
	}

	public boolean isCircuit = false;

	@Override
	public void render()
	{
		//Clear the screen
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		Gdx.gl.glClearColor(0, 0, 0, 1);
		transition.startDraw();
		//TODO: Developer shortcut, remove from final build
		if (Gdx.input.isKeyJustPressed(Keys.GRAVE))
			isCircuit = !isCircuit;
		music.update(isCircuit);
		if (isCircuit)
		{
			//Calculate the cursor position in the circuit
			mousePosition.set(Gdx.input.getX(), Gdx.input.getY());
			viewport.unproject(mousePosition);
			int circuitX = (int) ((mousePosition.x + circuitCamera.x) / 64);
			int circuitY = (int) ((mousePosition.y + circuitCamera.y) / 64);
			if(transition.shouldUpdate())
			{
				//Update the circuit accordingly
				input.update(circuitX, circuitY);
			}
			//Draw the circuit
			rend.renderCircuit(input.getCircuit(), inventory, circuitX, circuitY);
			//Handle exiting
			if(Gdx.input.isKeyJustPressed(Keys.ESCAPE))
			{
				//Exiting a finished circuit means it was still a success
				if(input.getCircuit().isSolved())
					world.circuitSuccess();
				else
					world.circuitFail();
				isCircuit = false;
				rend.resetCircuitCamera(); //Ensure that the circuit camera will be centered next time
				startScreenTransition(-20); //Transition back into the overworld
			}
		} else if(world.gameWon)
		{
			//The game is over, draw credits
			rend.renderCredits();
		}
		else {
			//Advance the simulation by 1 frame
			if(transition.shouldUpdate())
				overInput.step();
			//Draw the game
			rend.renderOverworld(world, inventory);
			//Handle switching to a circuit
			if(world.currentCircuit != null) 
			{
				input.setCircuit(world.currentCircuit);
				isCircuit = true;
				world.currentCircuit = null;
				startScreenTransition(20);
			}
			//The game has been won in this frame, so transition to the credits
			if(world.gameWon)
				startScreenTransition(10);
		}
		//Draw the particle system
		ParticleSystem.tick();
		batch.begin();
		ParticleSystem.draw(batch);
		batch.end();
		transition.endDraw();
	}
	
	public void startScreenTransition(int deltaX)
	{
		transition.transition(deltaX);
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
		assetsFiles.stream().map(file -> file.name()).filter(string -> string.endsWith("mp3"))
		.forEach(name -> assets.load(name, Music.class));
		assetsFiles.stream().map(file -> file.name()).filter(string -> string.endsWith("circuit"))
				.forEach(name -> assets.load(name, Circuit.class));
		assets.finishLoading();
		transition = new TransitionManager(this, assets, batch);
	}
	
	/***
	 * Call when a game over should be displayed
	 */
	public void gameOver()
	{
		transition.gameOver();
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
		transition.dispose();
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
