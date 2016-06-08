package com.fwumdesoft.project8;

import java.util.List;
import java.util.ArrayList;

/**
 * A class to represent the player's picked up items
 */
public class Inventory
{
	/**
	 * The resistors the player currently holds
	 */
	public final List<CircuitComponent> resistors;
	/**
	 * The chips the player currently holds
	 */
	public final List<CircuitComponent> lamps;
	/**
	 * The batteries the player currently holds
	 */
	public final List<CircuitComponent> batteries;

	public Inventory()
	{
		resistors = new ArrayList<>();
		lamps = new ArrayList<>();
		batteries = new ArrayList<>();
		// Start the player off with some basic gear
		addComponent(CircuitComponent.battery());
		addComponent(CircuitComponent.battery());
		addComponent(CircuitComponent.lamp());
		addComponent(CircuitComponent.lamp());
		addComponent(CircuitComponent.resistor());
		addComponent(CircuitComponent.resistor());

	}

	/**
	 * Add a component to the inventory </br>
	 * Resets the relevant values
	 * 
	 * @param comp
	 *            The component to add
	 */
	public void addComponent(CircuitComponent comp)
	{
		// Reset values so that the component can be used correctly
		comp.isChangeable = true;
		comp.isActive = false;
		// Choose the correct place to put the component
		switch (comp.type)
		{
		case RESISTOR:
			if (comp.isLamp) lamps.add(comp);
			else resistors.add(comp);
			break;
		case BATTERY:
			batteries.add(comp);
			break;
		case WIRE:
			comp.isChangeable = false;
			throw new IllegalArgumentException("Wire cannot be added to the inventory.");
		}
	}

	/**
	 * Remove a component from the inventory
	 * 
	 * @param comp
	 *            The component to remove
	 */
	public void removeComponent(CircuitComponent comp)
	{
		switch (comp.type)
		{
		case RESISTOR:
			if (comp.isLamp) lamps.remove(comp);
			else resistors.remove(comp);
			break;
		case BATTERY:
			batteries.remove(comp);
			break;
		case WIRE:
			throw new IllegalArgumentException("Wire cannot be removed from the inventory.");
		}
	}
}
