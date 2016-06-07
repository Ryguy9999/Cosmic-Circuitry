package com.fwumdesoft.project8;

import java.util.List;

import javax.swing.JOptionPane;
import javax.swing.JPanel;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.math.Vector2;
import com.fwumdesoft.project8.CircuitComponent.Type;

/**
 * An editor for circuits
 */
/*
 * Keybindings: END: Activate 1: Place wire 2: Place resistor 3: Place lamp 4:
 * Place battery 5: Delete S: Save L: Load
 */
public class CircuitInput
{
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
	 * The location of the circuit camera
	 */
	private Vector2 camera;
	/**
	 * If the mouse was pressed previoiusly
	 */
	private boolean previousPress;
	/**
	 * The number of pixels the camera can move per frame
	 */
	private final int CAMERA_SPEED = 2;

	/**
	 * Create a new circuit designer
	 * 
	 * @param assets
	 *            The game assets
	 * @param width
	 *            The width of the circuit
	 * @param height
	 *            The height of the circuit
	 */
	public CircuitInput(Circuit circuit, AssetManager assets, Inventory inventory, Vector2 camera)
	{
		this.circuit = circuit;
		this.inventory = inventory;
		this.editing = false;
		this.assets = assets;
		this.camera = camera;
	}

	/**
	 * Take input and edit the circuit accordingly
	 * 
	 * @param cursorX
	 *            The square the mouse is hovering over
	 * @param cursorY
	 *            The square the mouse is hovering over
	 */
	public void update(int cursorX, int cursorY)
	{
		// TODO: Remove developer tools
		if (Gdx.input.isKeyJustPressed(Keys.END)) editing = !editing;
		// Camera controls
		if (Gdx.input.isKeyPressed(Keys.LEFT)) camera.x -= CAMERA_SPEED;
		if (Gdx.input.isKeyPressed(Keys.RIGHT)) camera.x += CAMERA_SPEED;
		if (Gdx.input.isKeyPressed(Keys.UP)) camera.y += CAMERA_SPEED;
		if (Gdx.input.isKeyPressed(Keys.DOWN)) camera.y -= CAMERA_SPEED;
		if (Gdx.input.isKeyPressed(Keys.SHIFT_RIGHT)) camera.set(0, 0);
		// TODO: remove developer tools, put body of interact here
		if (editing) edit(cursorX, cursorY);
		else interact(cursorX, cursorY);
	}

	private void edit(int cursorX, int cursorY)
	{
		if (Gdx.input.isKeyJustPressed(Keys.NUM_1))
		{
			putComponent(CircuitComponent.wire(), cursorX, cursorY);
		}
		if (Gdx.input.isKeyJustPressed(Keys.NUM_2))
		{
			putComponent(CircuitComponent.resistor(), cursorX, cursorY);
		}
		if (Gdx.input.isKeyJustPressed(Keys.NUM_3))
		{
			putComponent(CircuitComponent.lamp(), cursorX, cursorY);
		}
		if (Gdx.input.isKeyJustPressed(Keys.NUM_4))
		{
			putComponent(CircuitComponent.battery(), cursorX, cursorY);
		}
		if (Gdx.input.isKeyJustPressed(Keys.NUM_5))
		{
			putComponent(CircuitComponent.blank(), cursorX, cursorY);
		}
		if (Gdx.input.isKeyJustPressed(Keys.NUM_6))
		{
			putComponent(null, cursorX, cursorY);
		}
		if (Gdx.input.isKeyJustPressed(Keys.L))
		{
			String circuitName = JOptionPane.showInputDialog("Enter the name of the circuit to load.");
			circuitName += ".circuit";
			circuit = assets.get(circuitName, Circuit.class);
		}
		if (Gdx.input.isKeyJustPressed(Keys.S))
		{
			String circuitName = JOptionPane.showInputDialog("Enter the name of the circuit to save.");
			circuitName += ".circuit";
			circuit.name = circuitName.substring(0, circuitName.indexOf(".circuit"));
			CircuitIO.write(assets.getFileHandleResolver().resolve(circuitName), circuit);
		}
		if (Gdx.input.isKeyJustPressed(Keys.N))
		{
			String input = JOptionPane.showInputDialog("Enter the number of lamps required to win");
			try
			{
				circuit.goalLamps = Integer.parseInt(input);
			}
			catch (NumberFormatException | NullPointerException e)
			{
				JOptionPane.showMessageDialog(null, "Failed to parse number.");
			}
		}
	}

	private void interact(int cursorX, int cursorY)
	{
		// If the cursor is off the screen no input needs to be processed
		if (cursorY < 0 || cursorX < 0 || cursorY >= circuit.grid.length || cursorX >= circuit.grid[cursorY].length
				|| circuit.grid[cursorY][cursorX] == null)
			return;
		List<CircuitComponent> type = null;
		String componentName = "";
		// If there has been a click/tap
		if (Gdx.input.isTouched() && !previousPress)
		{
			// There is nothing to interact with if the component isn't
			// changeable
			if (!circuit.grid[cursorY][cursorX].isChangeable) return;
			// If the component slot is empty
			if (circuit.grid[cursorY][cursorX].type == null)
			{
				Object[] options =
				{ "Battery", "Lamp", "Resistor" };
				JPanel typePanel = new JPanel();
				// Prompt the player for what type of component to place
				int result = JOptionPane.showOptionDialog(null, typePanel, "Choose a type",
						JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE, null, options, null);
				// If the player clicked the first, second, or third option or
				// closed the prompt
				switch (result)
				{
				case JOptionPane.YES_OPTION:
					type = inventory.batteries;
					componentName = "battery";
					break;
				case JOptionPane.NO_OPTION:
					type = inventory.lamps;
					componentName = "lamp";

					break;
				case JOptionPane.CANCEL_OPTION:
					type = inventory.resistors;
					componentName = "resistor";
					break;
				case JOptionPane.CLOSED_OPTION:
					return;
				}
			}
			else
			{
				// Pick up the component in the slot
				inventory.addComponent(circuit.grid[cursorY][cursorX]);
				circuit.grid[cursorY][cursorX] = CircuitComponent.blank();
			}
			previousPress = true;
		}
		else
		{
			// Keep track of when mouse clicks occur
			previousPress = false;
		}
		// If no component has been set up to place
		if (type == null) return;
		CircuitComponent place = null; // The circuit to place
		if (!(cursorY >= 0 && cursorY < circuit.grid.length && cursorX >= 0 && cursorX < circuit.grid[cursorY].length)
				|| !circuit.grid[cursorY][cursorX].isChangeable)
			return;
		// Prompt for the value of the component to place
		String input = JOptionPane.showInputDialog("Enter the value of the " + componentName + " to place.");
		try
		{
			double value = Double.parseDouble(input);
			// Find the component to place
			for (CircuitComponent comp : type)
			{
				if (comp.getMainValue() == value) place = comp;
			}
		}
		catch (NumberFormatException e)
		{
			// The user did not enter a valid double, so produce an error
			JOptionPane.showMessageDialog(null, "Failed to parse number.");
			return;
		}
		catch (NullPointerException e)
		{
			// This means that the user closed the value prompt, so no error box
			// is required
			return;
		}
		if (place != null)
		{
			// Swap out the components
			CircuitComponent old = circuit.grid[cursorY][cursorX];
			if (old.type != null) inventory.addComponent(old);
			circuit.grid[cursorY][cursorX] = place;
			inventory.removeComponent(place);
		}
		else
		{
			// Produce an error message because there is no component to place
			JOptionPane.showMessageDialog(null, "No " + componentName + " of that value was found.");
		}
	}

	/**
	 * Place a component onto the grid, if possible
	 * 
	 * @param component
	 *            The component object
	 * @param cursorX
	 *            The position of the cursor
	 * @param cursorY
	 *            The position of the cursor
	 */
	// TODO: REMOVE THIS METHOD, ONLY USED IN DEV FUNCTIONS
	private void putComponent(CircuitComponent component, int cursorX, int cursorY)
	{
		if (Gdx.input.isKeyPressed(Keys.CONTROL_LEFT)) component.isChangeable = true;
		if (cursorY >= 0 && cursorX >= 0 && cursorY < circuit.grid.length && cursorX < circuit.grid[cursorY].length)
		{
			try
			{
				if (component != null && component.type != null && component.type != Type.WIRE)
				{
					String input = JOptionPane.showInputDialog("Enter the value of the component.");
					double value = Double.parseDouble(input);
					component.setMainValue(value);
				}
				circuit.grid[cursorY][cursorX] = component;
			}
			catch (NumberFormatException | NullPointerException e)
			{
				JOptionPane.showMessageDialog(null, "Failed to parse number.");
			}
		}
	}

	/**
	 * Get the circuit that is being edited
	 * 
	 * @return The circuit object
	 */
	public Circuit getCircuit()
	{
		return circuit;
	}

	/**
	 * Set the circuit to direct input to
	 * 
	 * @param circ
	 *            The new circuit
	 */
	public void setCircuit(Circuit circ)
	{
		circuit = circ;
	}
}
