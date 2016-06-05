package com.fwumdesoft.project8;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
/**
 * A slideshow </br>
 * It does what it says on the tin
 */
public class Slideshow
{
	/**
	 * The slides in the show
	 */
	private final TextureRegion[] textures;
	/**
	 * The pixels per frame of the slideshow transitions
	 */
	private final int transitionSpeed;
	/**
	 * The TransitionManager to handle transitions
	 */
	private final TransitionManager transition;
	/**
	 * The index of the current slide
	 */
	private int current;
	
	/**
	 * Create a new slideshow
	 * @param speed The pixels per frame of the transition slide
	 * @param transition 
	 * @param regions The slides in the show
	 */
	public Slideshow(int speed, TransitionManager transition, TextureRegion... regions)
	{
		this.transition = transition;
		textures = regions;
		transitionSpeed = speed;
		current = 0;
	}
	
	/**
	 * Draw the current frame of the slideshow
	 * @param batch A SpriteBatch that has been started and not ended
	 */
	public void draw(SpriteBatch batch)
	{
		batch.draw(textures[current], 0, 0);
	}
	
	/**
	 * Go to the next slide
	 * @return If there are more slides
	 */
	public boolean next()
	{
		current++;
		if(current >= textures.length)
		{
			transition.transition(transitionSpeed);
			return false;
		}
		else
		{
			transition.transition(transitionSpeed);
			return true;
		}
	}
}
