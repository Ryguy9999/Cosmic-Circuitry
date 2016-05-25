package com.fwumdesoft.project8;

import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.InputAdapter;

public class OverworldInput extends InputAdapter {
	private Overworld world;
	
	public OverworldInput(Overworld world) {
		this.world = world;
	}
	
	public boolean keyDown (int keycode) {
		switch(keycode) {
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
		default:
			return false;
		}
		return true;
	}
}
