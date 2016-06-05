package com.fwumdesoft.project8;

import java.io.Serializable;

/**
 * A single item in a circuit May be a wire, a battery, a resistor, or a lamp
 */
public class CircuitComponent implements Serializable
{
	private static final long serialVersionUID = 1L;

	/**
	 * The three main types of components when it comes to the equations and
	 * solutions
	 */
	public enum Type
	{
		WIRE, BATTERY, RESISTOR
	};

	/**
	 * If the component is a lamp </br>
	 * Only applicable if the component is a resistor
	 */
	public boolean isLamp;
	/**
	 * If the component is active </br>
	 * Only applicable if the component is a lamp
	 */
	public boolean isActive;
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
	 * The goal current of the component</br>
	 * Intended for lamps whose target current must be reached to "solve" the
	 * circuit
	 */
	public double targetCurrent;
	/**
	 * The allowed margin for the target current</br>
	 * This value goes with the targetCurrent for lamps, allowing a margin of
	 * error for the player
	 */
	public double targetMargin;

	/**
	 * Create a component
	 * 
	 * @param type
	 *            The type of the component
	 */
	private CircuitComponent(Type type)
	{
		isChangeable = false;
		isLamp = false;
		isActive = false;
		voltageDif = current = resistance = targetCurrent = targetMargin = 0;
		this.type = type;
		if (hasMainValue())
			setMainValue(1);
	}
	
	/**
	 * A copy constructor
	 * @param comp The original to copy
	 */
	public CircuitComponent(CircuitComponent comp)
	{
		isLamp = comp.isLamp;
		isActive = comp.isActive;
		isChangeable = comp.isChangeable;
		type = comp.type;
		voltageDif = comp.voltageDif;
		current = comp.current;
		resistance = comp.resistance;
		targetCurrent = comp.targetCurrent;
		targetMargin = comp.targetMargin;
		if(isLamp)
		{
			if(targetCurrent <= 1)
				targetMargin = 0.75;
			else if(targetCurrent <= 4)
				targetMargin = 1;
			else
				targetMargin = 2;
		}
	}
	
	/**
	 * @return A new random component (typically from bags)
	 */
	public static CircuitComponent randomComponent()
	{
		Type t = null;
		CircuitComponent comp = new CircuitComponent(t);
		switch((int)(Math.random()*3))
		{
		case 0:
			comp = resistor();
			break;
		case 1:
			comp = battery();
			break;
		case 2:
			comp = lamp();
			break;
		}
		
		comp.setMainValue((int)(Math.random()*9) + 1);
		
		return comp;
	}

	/**
	 * @return A new blank component
	 */
	public static CircuitComponent blank()
	{
		Type t = null;
		CircuitComponent comp = new CircuitComponent(t);
		comp.isChangeable = true;
		return comp;
	}

	/**
	 * @return A new wire component
	 */
	public static CircuitComponent wire()
	{
		return new CircuitComponent(Type.WIRE);
	}

	/**
	 * @return A new resistor component
	 */
	public static CircuitComponent resistor()
	{
		return new CircuitComponent(Type.RESISTOR);
	}

	/**
	 * @return A new lamp component
	 */
	public static CircuitComponent lamp()
	{
		CircuitComponent comp = new CircuitComponent(Type.RESISTOR);
		comp.isLamp = true;
		comp.resistance = 1;
		comp.targetMargin = 0.75f;
		comp.targetCurrent = 1;
		return comp;
	}

	/**
	 * @return A new battery component
	 */
	public static CircuitComponent battery()
	{
		return new CircuitComponent(Type.BATTERY);
	}

	/**
	 * Set the main value (resistance for a resistor, voltage for a battery)
	 * 
	 * @param value
	 *            The value
	 */
	public void setMainValue(double value)
	{
		switch (type)
		{
		case BATTERY:
			voltageDif = value;
			break;
		case RESISTOR:
			if(isLamp)
			{
				targetCurrent = value;
				if(targetCurrent <= 1)
					targetMargin = 0.75;
				else if(targetCurrent <= 4)
					targetMargin = 1;
				else
					targetMargin = 2;
			}
			else
				resistance = value;
			break;
		default:
			throw new RuntimeException(this + " has no main value");
		}
	}

	/**
	 * Get the resistance for a resistor, voltage for a battery, etc.
	 * 
	 * @return The value
	 */
	public double getMainValue()
	{
		System.out.println(type + " " + isLamp);
		switch (type)
		{
		case BATTERY:
			System.out.println(voltageDif);
			return voltageDif;
		case RESISTOR:
			System.out.println(resistance + " " + targetCurrent);
			if(!isLamp)
				return resistance;
			else
				return targetCurrent;
		default:
			throw new RuntimeException(this + " has no main value");
		}
	}

	/**
	 * Finds if the component has a main value
	 * 
	 * @return If the component has a main value
	 */
	public boolean hasMainValue()
	{
		return type == Type.BATTERY || type == Type.RESISTOR;
	}
}
