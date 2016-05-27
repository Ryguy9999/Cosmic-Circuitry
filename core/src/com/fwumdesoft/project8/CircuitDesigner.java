package com.fwumdesoft.project8;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;

public class CircuitDesigner {
	private CircuitComponent[][] circuit;
	private boolean active;
	
	public CircuitDesigner(int width, int height) {
		circuit = new CircuitComponent[height][width];
		active = false;
	}
	
	
	public void update(int cursorX, int cursorY) {
		if(Gdx.input.isKeyJustPressed(Keys.END))
			active = !active;
		if(active) {
			if(Gdx.input.isKeyJustPressed(Keys.NUM_1)) {
				putComponent(CircuitComponent.wire(), cursorX, cursorY);
			}
			if(Gdx.input.isKeyJustPressed(Keys.NUM_2)) {
				putComponent(CircuitComponent.resistor(), cursorX, cursorY);
			}
			if(Gdx.input.isKeyJustPressed(Keys.NUM_3)) {
				putComponent(CircuitComponent.lamp(), cursorX, cursorY);
			}
			if(Gdx.input.isKeyJustPressed(Keys.NUM_4)) {
				putComponent(CircuitComponent.battery(), cursorX, cursorY);
			}
			if(Gdx.input.isKeyJustPressed(Keys.NUM_5)) {
				putComponent(null, cursorX, cursorY);
			}
		}
	}
	
	private void putComponent(CircuitComponent component, int cursorX, int cursorY) {
		if(cursorY < circuit.length && cursorX < circuit[cursorY].length)
			circuit[cursorY][cursorX] = component;
	}
	
	public CircuitComponent[][] getCircuit() {
		return circuit;
	}
}
