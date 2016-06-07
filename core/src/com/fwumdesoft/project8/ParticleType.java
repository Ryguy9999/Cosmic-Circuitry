package com.fwumdesoft.project8;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.function.Consumer;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Pool;

public class ParticleType
{
	private final List<Particle> particles;
	private final Pool<Particle> particlePool;
	private final Vector2 displacement;
	
	public ParticleType(Consumer<Particle> initializer, int max)
	{
		particles = new ArrayList<>(max);
		displacement = new Vector2();
		this.particlePool = new Pool<Particle>(max){
			@Override
			public Particle obtain()
			{
				Particle p = super.obtain();
				initializer.accept(p);
				return p;
			}
			@Override
			protected Particle newObject()
			{
				return new Particle();
			}
		};
	}
	
	public void burst(float x, float y, int number)
	{
		for(int i = 0; i < number; i++)
		{
			Particle p = particlePool.obtain();
			p.position.setPosition(x, y);
			particles.add(p);
		}
	}
	
	public void tick()
	{
		particles.parallelStream().forEach(particle -> particle.tick());
		for(Iterator<Particle> part = particles.iterator(); part.hasNext();)
		{
			Particle p = part.next();
			if(p.isDead())
			{
				particlePool.free(p);
				part.remove();
			}
		}
	}
	
	public void draw(SpriteBatch batch)
	{
		particles.forEach(particle -> particle.draw(batch, displacement));
	}
	
	public void clear()
	{
		displacement.set(0, 0);
		while(!particles.isEmpty())
		{
			particlePool.free(particles.remove(0));
		}
	}
	
	public void displace(float x, float y)
	{
		displacement.add(x, y);
	}
}
