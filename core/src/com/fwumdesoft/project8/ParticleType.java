package com.fwumdesoft.project8;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.function.Consumer;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.Pool;

public class ParticleType
{
	private final List<Particle> particles;
	private final Pool<Particle> particlePool;
	
	public ParticleType(Consumer<Particle> initializer, int max)
	{
		particles = new ArrayList<>(max);
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
		particles.forEach(particle -> particle.draw(batch));
	}
	
	public void clear()
	{
		while(!particles.isEmpty())
		{
			particlePool.free(particles.remove(0));
		}
	}
}
