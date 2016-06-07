package com.fwumdesoft.project8;

import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.audio.Music;

/**
 * A class that plays music
 */
public class MusicPlayer
{
	/**
	 * The music tracks for the game
	 */
	private Music overworld, circuit;

	/**
	 * Create a new music player
	 * 
	 * @param assets
	 *            The AssetManager with all assets loaded
	 */
	public MusicPlayer(AssetManager assets)
	{
		overworld = assets.get("overworld.mp3", Music.class);
		circuit = assets.get("circuit.mp3", Music.class);
		overworld.setLooping(true);
		circuit.setLooping(true);
	}

	/**
	 * Update the state of the music player
	 * 
	 * @param playAnything
	 *            Whether any music should be played or not
	 * @param isCircuit
	 *            Whether the game is currently on a circuit
	 */
	public void update(boolean playAnything, boolean isCircuit)
	{
		if (!playAnything)
		{
			circuit.stop();
			overworld.stop();
		}
		else if (isCircuit)
		{
			if (!circuit.isPlaying())
			{
				circuit.play();
				overworld.stop();
			}
		}
		else
		{
			if (!overworld.isPlaying())
			{
				overworld.play();
				circuit.stop();
			}
		}
	}
}
