package com.fwumdesoft.project8;

import java.util.Stack;

import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.InputAdapter;

/**
 * The class to manage input for the overworld
 */
public class OverworldInput extends InputAdapter
{
	/**
	 * The overworld to direct input to
	 */
	private Overworld world;
	/**
	 * The top-level app that manages things like restarting the game </br>
	 * Used to ensure that the overworld is active when input is received
	 */
	private Project8 topLevel;
	/**
	 * The keys that are being held
	 */
	private Stack<Integer> heldKeys;
	/**
	 * A cooldown before the next input will be directed </br>
	 * If inputs are directed too often animation and turn timing will be
	 * nonfunctional
	 */
	private int cooldown = 0;
	/**
	 * The maximum cooldown for a turn
	 */
	public static final int MAX_COOLDOWN = 10;

	/**
	 * Create a new overworld input
	 * 
	 * @param topLevel
	 *            The top-level app
	 * @param world
	 *            The overworld to direct input to
	 */
	public OverworldInput(Project8 topLevel, Overworld world)
	{
		this.topLevel = topLevel;
		this.world = world;
		heldKeys = new Stack<>();
	}

	public boolean keyDown(int keycode)
	{
		// Don't direct input unless the overworld is active
		if (topLevel.isCircuit) return false;
		// Ensure there are no duplicates in heldKeys
		if (heldKeys.contains(keycode)) heldKeys.remove(new Integer(keycode));
		// Add this key to the stack
		heldKeys.push(keycode);
		// If the cooldown is active, decrease it and return false
		if (cooldown > 0)
		{
			cooldown -= 1;
			return false;
		}
		// Choose the correct key
		switch (keycode)
		{
		case Keys.A:
			world.movePlayer(-1, 0);
			break;
		case Keys.D:
			world.movePlayer(1, 0);
			break;
		case Keys.W:
			world.movePlayer(0, 1);
			break;
		case Keys.S:
			world.movePlayer(0, -1);
			break;
		case Keys.R:
			world.rest();
			break;
		case Keys.SPACE:
			world.interact();
			break;
		default:
			return false;
		}
		cooldown = MAX_COOLDOWN;
		return true;
	}

	@Override
	public boolean keyUp(int keycode)
	{
		return heldKeys.remove(new Integer(keycode));
	}

	/**
	 * Step the overworld input forward </br>
	 * Handles held keys
	 */
	public void step()
	{
		if (!heldKeys.isEmpty()) keyDown(heldKeys.peek());
		else cooldown = 0;
	}
}
