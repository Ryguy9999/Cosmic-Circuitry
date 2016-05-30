package com.fwumdesoft.project8;

import java.util.List;
import java.util.ArrayList;

/**
 * A class to represent the player's picked up items
 */
public class Inventory {
	/**
	 * The resistors the player currently holds
	 */
	public List<CircuitComponent> resistors; 
	/**
	 * The chips the player currently holds
	 */
	public List<CircuitComponent> chips;
	/**
	 * The batteries the player currently holds
	 */
	public List<CircuitComponent> batteries;

	public Inventory() {
		resistors = new ArrayList<>();
		chips = new ArrayList<>();
		batteries = new ArrayList<>();
	}
	
	public void addComponent(CircuitComponent comp) {
		switch(comp.type) {
		case RESISTOR:
			if(comp.isLamp)
				chips.add(comp);
			else
				resistors.add(comp);
			break;
		case BATTERY:
			batteries.add(comp);
			break;
		case WIRE:
			throw new IllegalArgumentException("Wire cannot be added to the inventory.");
		}
	}
}
