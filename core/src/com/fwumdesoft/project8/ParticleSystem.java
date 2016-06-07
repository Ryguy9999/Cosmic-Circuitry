package com.fwumdesoft.project8;

import java.util.HashMap;
import java.util.function.Consumer;

import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

public class ParticleSystem
{
	private ParticleSystem() {}
	
	private static HashMap<String, ParticleType> types = new HashMap<>();
	
	private static final int MAX_PARTICLE_SIZE = 1_000;
	
	public static void init(AssetManager assets)
	{
		addParticleType("spark", p -> {
			p.position.setVertices(new float[]{0,0, 1,0, 0,1, 1,1});
			p.position.setScale(1, 1);
			p.lifetime = 10;
			p.texture = new TextureRegion(assets.get("broken.png", Texture.class));
			p.velocity.set(((float)Math.random() - 0.5f) * 2.5f, ((float)Math.random() - 0.5f) * 2.5f);
		});
		addParticleType("electricity", p -> {
			p.position.setVertices(new float[]{0,0, 4,0, 0,4, 4,4});
			p.position.setScale(1, 1);
			p.position.setOrigin(2, 2);
			p.lifetime = 10 + (int)(Math.random() * 15);
			p.texture = new TextureRegion(assets.get("electricity_particle.png", Texture.class));
			p.velocity.set(((float)Math.random() - 0.5f) * 5, ((float)Math.random() - 0.5f) * 5);
			p.rotationalVelocity = 30;
			p.scaleVelocity = 0;
		});
		addParticleType("smoke", p -> {
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
	
	public static void displace(float x, float y)
	{
		types.values().forEach(pt -> pt.displace(x, y));
	}
	
	public static void clearDisplace()
	{
		types.values().forEach(pt -> pt.undisplace());
	}
}
