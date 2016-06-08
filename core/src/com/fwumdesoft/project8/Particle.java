package com.fwumdesoft.project8;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Polygon;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Pool.Poolable;

/**
 * A class that represents a single particle
 */
public class Particle implements Poolable
{
	/**
	 * The bounds of the particle, including rotation, scale, origin, etc.
	 */
	public final Polygon position;
	/**
	 * The texture for the particle to draw
	 */
	public TextureRegion texture;
	/**
	 * The velocity of the particle
	 */
	public final Vector2 velocity;
	/**
	 * The rotational velocity of the particle
	 */
	public float rotationalVelocity;
	/**
	 * The amount the particle grows or shrinks per tick
	 */
	public float scaleVelocity;
	/**
	 * The alpha value of the particle
	 */
	public float opacity;
	/**
	 * The change in opacity per tick
	 */
	public float deltaOpacity;
	/**
	 * The remaining ticks before the paritcle ceases to exist
	 */
	public int lifetime;

	/**
	 * Create a new particle
	 */
	public Particle()
	{
		position = new Polygon();
		velocity = new Vector2();
	}

	/**
	 * Duplicate this particle
	 * 
	 * @param x
	 *            The position that the particle will be created at
	 * @param y
	 *            The position that the particle will be created at
	 * @return The cloned particle
	 */
	public Particle cloneTo(float x, float y)
	{
		Particle p = new Particle();
		p.position.setVertices(position.getVertices());
		p.position.setPosition(x, y);
		p.position.setOrigin(position.getOriginX(), position.getOriginY());
		p.texture = texture;
		p.velocity.set(velocity);
		p.rotationalVelocity = rotationalVelocity;
		p.scaleVelocity = scaleVelocity;
		p.opacity = opacity;
		p.deltaOpacity = deltaOpacity;
		p.lifetime = lifetime;
		return p;
	}

	/**
	 * Tick the particle forward
	 */
	public void tick()
	{
		lifetime--;
		position.translate(velocity.x, velocity.y);
		position.rotate(rotationalVelocity);
		position.scale(scaleVelocity);
		opacity += deltaOpacity;
	}

	/**
	 * Draw the particle
	 * 
	 * @param batch
	 *            A batch where begin has been called and end has not
	 * @param displacement
	 *            The displacement from the origin
	 */
	public void draw(SpriteBatch batch)
	{
		batch.draw(texture, position.getX(), position.getY(), position.getOriginX(),
				position.getOriginY(), texture.getRegionWidth(), texture.getRegionHeight(), position.getScaleX(),
				position.getScaleY(), position.getRotation());
	}

	/**
	 * @return If the particle is dead
	 */
	public boolean isDead()
	{
		return lifetime <= 0;
	}

	@Override
	public void reset()
	{
		position.setPosition(0, 0);
		texture = null;
		velocity.set(0, 0);
		rotationalVelocity = 0;
		scaleVelocity = 0;
		opacity = 1;
		deltaOpacity = 0;
		lifetime = 0;
	}
}
