package com.fwumdesoft.project8;

import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.audio.Music;

public class MusicPlayer
{
	private Music overworld, circuit;
	
	public MusicPlayer(AssetManager assets)
	{
		overworld = assets.get("overworld.mp3", Music.class);
		circuit = assets.get("circuit.mp3", Music.class);
		overworld.setLooping(true);
		circuit.setLooping(true);
	}
	
	
	public void update(boolean playAnything, boolean isCircuit)
	{
		if(!playAnything)
		{
			circuit.stop();
			overworld.stop();
		}
		else if(isCircuit)
		{
			if(!circuit.isPlaying())
			{
				circuit.play();
				overworld.stop();
			}
		}
		else
		{
			if(!overworld.isPlaying())
			{
				overworld.play();
				circuit.stop();
			}
		}
	}
}
