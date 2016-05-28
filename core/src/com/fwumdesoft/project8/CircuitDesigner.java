package com.fwumdesoft.project8;

import javax.swing.JOptionPane;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.assets.AssetManager;
/**
 * An editor for circuits
 */
/*
 * Keybindings:
 * END: Activate
 * 1: Place wire
 * 2: Place resistor
 * 3: Place lamp
 * 4: Place battery
 * 5: Delete
 * S: Save
 * L: Load
 */
public class CircuitDesigner {
	/**
	 * The circuit to edit
	 */
	private CircuitComponent[][] circuit;
	/**
	 * If the designer is currently editing
	 */
	private boolean active;
	/**
	 * The game assets to load the circuit from
	 */
	private AssetManager assets;
	
	/**
	 * Create a new circuit designer
	 * @param assets The game assets
	 * @param width The width of the circuit
	 * @param height The height of the circuit
	 */
	public CircuitDesigner(AssetManager assets, int width, int height) {
		circuit = new CircuitComponent[height][width];
		active = false;
		this.assets = assets;
	}
	
	/**
	 * Take input and edit the circuit accordingly
	 * @param cursorX The square the mouse is hovering over
	 * @param cursorY The square the mouse is hovering over
	 */
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
	
	/**
	 * Place a component onto the grid, if possible
	 * @param component The component object
	 * @param cursorX The position of the cursor
	 * @param cursorY The position of the cursor
	 */
	private void putComponent(CircuitComponent component, int cursorX, int cursorY) {
		if(cursorY < circuit.length && cursorX < circuit[cursorY].length)
			circuit[cursorY][cursorX] = component;
	}
	
	/**
	 * Get the circuit that is being edited
	 * @return The circuit object
	 */
	public CircuitComponent[][] getCircuit() {
		return circuit;
	}
}
