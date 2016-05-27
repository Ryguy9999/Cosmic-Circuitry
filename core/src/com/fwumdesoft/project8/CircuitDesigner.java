package com.fwumdesoft.project8;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.viewport.Viewport;

public class CircuitDesigner {
	private CircuitComponent[][] circuit;
	private boolean active;
	private Vector2 mousePosition;
	private int squareSize;
	
	public CircuitDesigner(int width, int height, int size) {
		circuit = new CircuitComponent[height][width];
		active = false;
		mousePosition = new Vector2();
		squareSize = size;
	}
	
	
	public void update(Viewport port) {
		if(Gdx.input.isKeyJustPressed(Keys.END))
			active = !active;
		if(active) {
			mousePosition.set(Gdx.input.getX(), Gdx.input.getY());
			port.unproject(mousePosition);
			if(Gdx.input.isKeyJustPressed(Keys.NUM_1)) {
				putComponent(CircuitComponent.wire(), mousePosition);
			}
			if(Gdx.input.isKeyJustPressed(Keys.NUM_2)) {
				putComponent(CircuitComponent.resistor(), mousePosition);
			}
			if(Gdx.input.isKeyJustPressed(Keys.NUM_3)) {
				putComponent(CircuitComponent.lamp(), mousePosition);
			}
			if(Gdx.input.isKeyJustPressed(Keys.NUM_4)) {
				putComponent(CircuitComponent.battery(), mousePosition);
			}
			if(Gdx.input.isKeyJustPressed(Keys.NUM_5)) {
				putComponent(null, mousePosition);
			}
		}
	}
	
	private void putComponent(CircuitComponent component, Vector2 mouse) {
		int x = (int)(mouse.x / squareSize);
		int y = (int)(mouse.y / squareSize);
		if(y < circuit.length && x < circuit[y].length)
			circuit[y][x] = component;
	}
	
	public CircuitComponent[][] getCircuit() {
		return circuit;
	}
}
