package com.fwumdesoft.project8;

import java.util.HashMap;
import java.util.function.Consumer;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;

public class ParticleSystem
{
	private ParticleSystem() {}
	
	private static HashMap<String, ParticleType> types = new HashMap<>();
	
	private static final int MAX_PARTICLE_SIZE = 1_000;
	
	public static void addParticleType(String name, Consumer<Particle> spawner)
	{
		types.put(name, new ParticleType(spawner, MAX_PARTICLE_SIZE));
	}
	
	public static void burst(String name, float x, float y, int amt)
	{
		types.get(name).burst(x, y, amt);
	}
	
	public static void tick()
	{
		types.values().forEach(pt -> pt.tick());
	}
	
	public static void draw(SpriteBatch batch)
	{
		types.values().forEach(pt -> pt.draw(batch));
	}
	
	public static void clear()
	{
		types.values().forEach(pt -> pt.clear());
	}
}
