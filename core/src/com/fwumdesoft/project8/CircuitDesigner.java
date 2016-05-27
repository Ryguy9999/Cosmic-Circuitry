package com.fwumdesoft.project8;

import javax.swing.JOptionPane;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.assets.AssetManager;

public class CircuitDesigner {
	private CircuitComponent[][] circuit;
	private boolean active;
	private AssetManager assets;
	
	public CircuitDesigner(AssetManager assets, int width, int height) {
		circuit = new CircuitComponent[height][width];
		active = false;
		this.assets = assets;
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
			if(Gdx.input.isKeyJustPressed(Keys.L)) {
				String circuitName = JOptionPane.showInputDialog("Enter the name of the circuit to load.");
				circuitName += ".circuit";
				circuit = assets.get(circuitName, CircuitComponent[][].class);
			}
			if(Gdx.input.isKeyJustPressed(Keys.S)) {
				String circuitName = JOptionPane.showInputDialog("Enter the name of the circuit to save.");
				circuitName += ".circuit";
				CircuitIO.write(assets.getFileHandleResolver().resolve(circuitName), circuit);
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
