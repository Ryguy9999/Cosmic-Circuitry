package com.fwumdesoft.project8;

import java.io.Serializable;

/**
 * A single item in a circuit
 * May be a wire, a battery, a resistor, or a lamp
 */
public class CircuitComponent implements Serializable {
	private static final long serialVersionUID = 1L;

	/**
	 * The three main types of components when it comes to the equations and solutions
	 */
	public enum Type {WIRE, BATTERY, RESISTOR };
	/**
	 * If the component is a lamp </br>
	 * Only applicable if the component is a resistor
	 */
	public boolean isLamp;
	/**
	 * If the component can be edited by the player
	 */
	public boolean isChangeable;
	/**
	 * The type of the component
	 */
	Type type;
	/**
	 * The volatage of the component </br>
	 * For a battery, it is a given </br>
	 * For other components, it is calculated
	 */
	public double voltageDif;
	/**
	 * The current across the component </br>
	 * Calculated by CircuitSolver
	 */
	public double current;
	/**
	 * The resistance of a component </br>
	 * Given for resistors and lamps, should be 0 otherwise
	 */
	public double resistance;
	
	/**
	 * Create a component
	 * @param type The type of the component
	 */
	private CircuitComponent(Type type){
		isChangeable = false;
		isLamp = false;
		voltageDif = current = resistance = 0;
		this.type = type;
	}
	
	/**
	 * @return A new wire component
	 */
	public static CircuitComponent wire() {
		return new CircuitComponent(Type.WIRE);
	}
	
	/**
	 * @return A new resistor component
	 */
	public static CircuitComponent resistor() {
		return new CircuitComponent(Type.RESISTOR);
	}
	
	/**
	 * @return A new lamp component
	 */
	public static CircuitComponent lamp() {
		CircuitComponent comp = new CircuitComponent(Type.WIRE);
		comp.isLamp = true;
		return comp;
	}
	
	/**
	 * @return A new battery component
	 */
	public static CircuitComponent battery() {
		return new CircuitComponent(Type.BATTERY);
	}
	
	public void setMainValue(double value) {
		switch(type) {
		case BATTERY:
			voltageDif = value;
			break;
		case RESISTOR:
			resistance = value;
			break;
		case WIRE:
			throw new RuntimeException("Wire has no main value");
		}
	}
	
	public double getMainValue() {
		switch(type) {
		case BATTERY:
			return voltageDif;
		case RESISTOR:
			return resistance;
		default:
			throw new RuntimeException("Wire has no main value");
		}
	}
}
