package com.fwumdesoft.project8;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Polygon;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Pool.Poolable;

public class Particle implements Poolable
{
	public final Polygon position;
	public TextureRegion texture;
	public final Vector2 velocity;
	public float rotationalVelocity, scaleVelocity, opacity, deltaOpacity;
	public int lifetime;

	public Particle()
	{
		position = new Polygon();
		velocity = new Vector2();		
	}
	
	public void tick()
	{
		lifetime--;
		position.translate(velocity.x, velocity.y);
		position.rotate(rotationalVelocity);
		position.scale(scaleVelocity);
		opacity += deltaOpacity;
	}
	
	public void draw(SpriteBatch batch)
	{
		batch.draw(texture, position.getX(), position.getY(), position.getOriginX(), 
				position.getOriginY(), texture.getRegionWidth(), texture.getRegionHeight(), 
				position.getScaleX(), position.getScaleY(), position.getRotation());
	}
	
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
