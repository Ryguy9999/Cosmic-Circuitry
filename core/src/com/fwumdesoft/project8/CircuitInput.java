package com.fwumdesoft.project8;

import java.util.List;
import javax.swing.JOptionPane;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.assets.AssetManager;
import com.fwumdesoft.project8.CircuitComponent.Type;

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
public class CircuitInput {
	/**
	 * The circuit to edit
	 */
	private Circuit circuit;
	/**
	 * If the designer is currently editing
	 */
	private boolean editing;
	/**
	 * The game assets to load the circuit from
	 */
	private AssetManager assets;
	/**
	 * The inventory of the player
	 */
	private Inventory inventory;
	
	/**
	 * Create a new circuit designer
	 * @param assets The game assets
	 * @param width The width of the circuit
	 * @param height The height of the circuit
	 */
	public CircuitInput(Circuit circuit, AssetManager assets, Inventory inventory) {
		this.circuit = circuit;
		this.inventory = inventory;
		this.editing = false;
		this.assets = assets;
	}
	
	/**
	 * Take input and edit the circuit accordingly
	 * @param cursorX The square the mouse is hovering over
	 * @param cursorY The square the mouse is hovering over
	 */
	public void update(int cursorX, int cursorY) {
		if(Gdx.input.isKeyJustPressed(Keys.END))
			editing = !editing;
		if(editing) {
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
				putComponent(CircuitComponent.blank(), cursorX, cursorY);
			}
			if(Gdx.input.isKeyJustPressed(Keys.NUM_6)) {
				putComponent(null, cursorX, cursorY);
			}
			if(Gdx.input.isKeyJustPressed(Keys.L)) {
				String circuitName = JOptionPane.showInputDialog("Enter the name of the circuit to load.");
				circuitName += ".circuit";
				circuit = assets.get(circuitName, Circuit.class);
			}
			if(Gdx.input.isKeyJustPressed(Keys.S)) {
				String circuitName = JOptionPane.showInputDialog("Enter the name of the circuit to save.");
				circuitName += ".circuit";
				CircuitIO.write(assets.getFileHandleResolver().resolve(circuitName), circuit);
			}
		} else {
			if(Gdx.input.isKeyJustPressed(Keys.SPACE)) {
				if(!(cursorY >= 0 && cursorY < circuit.grid.length && cursorX >= 0 && cursorX < circuit.grid[cursorY].length)
						|| !circuit.grid[cursorY][cursorX].isChangeable)
					return;
				inventory.addComponent(circuit.grid[cursorY][cursorX]);
				circuit.grid[cursorY][cursorX] = CircuitComponent.blank();
			}
			List<CircuitComponent> type = null;
			String componentName = "";
			if(Gdx.input.isKeyJustPressed(Keys.R)) {
				type = inventory.resistors;
				componentName = "resistor";
			} else if(Gdx.input.isKeyJustPressed(Keys.B)) {
				type = inventory.batteries;
				componentName = "battery";
			} else if(Gdx.input.isKeyJustPressed(Keys.L)) {
				type = inventory.chips;
				componentName = "lamp";
			}
			CircuitComponent place = null;
			if(type != null) {
				if(!(cursorY >= 0 && cursorY < circuit.grid.length && cursorX >= 0 && cursorX < circuit.grid[cursorY].length)
						|| !circuit.grid[cursorY][cursorX].isChangeable)
					return;
				String input = JOptionPane.showInputDialog("Enter the value of the " + componentName + " to place.");
				try {
					double value = Double.parseDouble(input);
					for(CircuitComponent comp : type) {
						if(comp.getMainValue() == value)
							place = comp;
					}
				} catch(NumberFormatException | NullPointerException e) {
					JOptionPane.showMessageDialog(null, "Failed to parse number.");
					return;
				}
			} else {
				return;
			}
			if(place != null) {
				CircuitComponent old = circuit.grid[cursorY][cursorX];
				if(old.type != null)
					inventory.addComponent(old);
				circuit.grid[cursorY][cursorX] = place;
				inventory.removeComponent(place);
			} else {
				JOptionPane.showMessageDialog(null, "No " + componentName + " of that value was found.");
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
		if(cursorY < circuit.grid.length && cursorX < circuit.grid[cursorY].length) {
			try {
				if(component != null && component.type != null && component.type != Type.WIRE) {
					String input = JOptionPane.showInputDialog("Enter the value of the component.");
					double value = Double.parseDouble(input);
					component.setMainValue(value);
				}
				circuit.grid[cursorY][cursorX] = component;
			} catch(NumberFormatException | NullPointerException e) {
				JOptionPane.showMessageDialog(null, "Failed to parse number.");
			}
		}
	}
	
	/**
	 * Get the circuit that is being edited
	 * @return The circuit object
	 */
	public CircuitComponent[][] getCircuit() {
		return circuit.grid;
	}
}
