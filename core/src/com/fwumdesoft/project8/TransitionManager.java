package com.fwumdesoft.project8;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.Pixmap.Format;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.FrameBuffer;
import com.badlogic.gdx.utils.Disposable;

/**
 * A class to manage switching between different screens
 */
public class TransitionManager implements Disposable
{
	/**
	 * Framebuffer to store screens to show during transition
	 */
	private FrameBuffer transition, current;
	/**
	 * TextureRegion to use to make drawing the frame buffer easier
	 */
	private final TextureRegion transitionRegion;
	/**
	 * Flag to manage transition state
	 */
	private boolean transitioning = false, transitionStarted = false;
	/**
	 * The current progress of the transition
	 */
	private int transitionX = 0;
	/**
	 * The progress each frame of the transition
	 */
	private int deltaTransitionX = 0;
	/**
	 * A timer for a game over screen
	 */
	private int gameOverTimer = 0;
	/**
	 * Texture for the game over screen
	 */
	private final Texture gameOver, gameOverBkg;
	/**
	 * The batch to draw the frame buffer to the screen
	 */
	private final SpriteBatch batch;
	/**
	 * The maximum time for the game over screen to be up
	 */
	final int MAX_GAME_OVER = 120;
	/**
	 * The app object to restart when necessary
	 */
	private Project8 app;
	
	/**
	 * Create a new TransitionManager
	 * @param app The main class to use to restart
	 * @param assets The asset manager to load textures with
	 * @param batch The sprite batch to use to draw textures
	 */
	public TransitionManager(Project8 app, AssetManager assets, SpriteBatch batch)
	{
		this.app = app;
		transition = new FrameBuffer(Format.RGBA8888, Gdx.graphics.getWidth(), Gdx.graphics.getHeight(), false);
		current = new FrameBuffer(Format.RGBA8888, Gdx.graphics.getWidth(), Gdx.graphics.getHeight(), false);
		transitionRegion = new TextureRegion();
		this.batch = batch;
		gameOver = assets.get("game_over.png", Texture.class);
		gameOverBkg = assets.get("game_over_bkg.png", Texture.class);
	}
	
	/**
	 * Start a wipe transition
	 * @param xSlidePerFrame The amount to move every frame
	 */
	public void transition(int xSlidePerFrame)
	{
		transitioning = true;
		transitionStarted = false;
		transitionX = 0;
		deltaTransitionX = xSlidePerFrame;
	}
	
	/**
	 * Start the draw
	 */
	public void startDraw()
	{
		current.begin();
	}
	
	/**
	 * End the draw
	 */
	public void endDraw()
	{
		batch.begin();
		if(gameOverTimer > 0)
		{
			batch.setColor(1, 1, 1, 0.5f);
			batch.draw(gameOverBkg, 0, 0);
			batch.setColor(Color.WHITE);
			batch.draw(gameOver, 0, 0);
		}
		batch.end();
		//Stop drawing to the frame buffer
		current.end();
		//Draw the frame buffer to the screen, possibly using a transition effect
		batch.begin();
		if(transitioning)
		{
			if(!transitionStarted)
			{
				//Switch the buffers
				FrameBuffer temp = current;
				current = transition;
				transition = temp;
				transitionStarted = true;
			}
			drawFrameBuffer(transition, transitionX, 0);
			//Place the target screen in the correct place
			if(deltaTransitionX > 0)
				drawFrameBuffer(current, transitionX - Gdx.graphics.getWidth(), 0);
			else
				drawFrameBuffer(current, transitionX + Gdx.graphics.getWidth(), 0);
			//Slide the transition along
			transitionX += deltaTransitionX;
			if(transitionX >= Gdx.graphics.getWidth() || transitionX <= -Gdx.graphics.getWidth())
				transitioning = false;
		}
		else
			drawFrameBuffer(current, 0, 0);
		batch.end();
		if(gameOverTimer > 0)
		{
			gameOverTimer--;
			if(gameOverTimer <= 0)
				app.restart();
		}
	}
	
	
	/**
	 * Begin a game over state
	 */
	public void gameOver()
	{
		gameOverTimer = MAX_GAME_OVER;
	}
	
	/**
	 * @return If game updates should be performed
	 */
	public boolean shouldUpdate()
	{
		return gameOverTimer <= 0 && !transitioning;
	}
	
	/**
	 * A helper method to draw a frame buffer to the screen
	 * @param buffer The buffer to draw
	 * @param x The position to draw it
	 * @param y The position to draw it
	 */
	private void drawFrameBuffer(FrameBuffer buffer, int x, int y)
	{
		transitionRegion.setRegion(buffer.getColorBufferTexture());
		transitionRegion.flip(false, true);
		batch.draw(transitionRegion, x, y);
	}
	
	/**
	 * Dispose of transition manager internals
	 */
	public void dispose()
	{
		current.dispose();
		transition.dispose();
	}
}
