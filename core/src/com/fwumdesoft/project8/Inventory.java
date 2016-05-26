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
}
