package com.fwumdesoft.project8;

import java.util.Stack;

import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.InputAdapter;

public class OverworldInput extends InputAdapter
{
	private Overworld world;
	public Overworld previousWorld;
	private Project8 topLevel;
	private Stack<Integer> heldKeys;
	private int cooldown = 0;
	private final int MAX_COOLDOWN = 15;
	
	public OverworldInput(Project8 topLevel, Overworld world)
	{
		this.topLevel = topLevel;
		this.world = world;
		this.previousWorld = world;
		heldKeys = new Stack<>();
	}

	public boolean keyDown(int keycode)
	{
		if(topLevel.isCircuit)
			return false;
		if(heldKeys.contains(keycode))
			heldKeys.remove(new Integer(keycode));
		heldKeys.push(keycode);
		if(cooldown > 0)
		{
			cooldown -= 1;
			return false;
		}
		switch (keycode)
		{
		case Keys.A:
			move(-1, 0);
			break;
		case Keys.D:
			move(1, 0);
			break;
		case Keys.W:
			move(0, 1);
			break;
		case Keys.S:
			move(0, -1);
			break;
		case Keys.R:
			world.turn();
			break;
		case Keys.SPACE:
			world.interact();
			break;
		default:
			return false;
		}
		previousWorld = world.getStateCopy();
		cooldown = MAX_COOLDOWN;
		return true;
	}
	
	@Override
	public boolean keyUp(int keycode)
	{
		return heldKeys.remove(new Integer(keycode));
	}
	
	public void step()
	{
		if(!heldKeys.isEmpty())
			keyDown(heldKeys.peek());
		else
			cooldown = 0;
	}

	private void move(int x, int y)
	{
		world.movePlayer(x, y);
	}
}
