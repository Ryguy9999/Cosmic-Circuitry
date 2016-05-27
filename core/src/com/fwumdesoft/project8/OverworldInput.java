package com.fwumdesoft.project8;

import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.InputAdapter;

public class OverworldInput extends InputAdapter {
	private Overworld world;
	private int faceX, faceY;
	
	public OverworldInput(Overworld world) {
		this.world = world;
	}
	
	public boolean keyDown (int keycode) {
		switch(keycode) {
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
		case Keys.SPACE:
			world.interactWith(faceX, faceY);
			break;
		default:
			return false;
		}
		return true;
	}
	
	private void move(int x, int y) {
		world.movePlayer(x, y);
		faceX = x;
		faceY = y;
	}
}
