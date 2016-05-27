package com.fwumdesoft.project8;

public class CircuitComponent{
	public enum Type {WIRE, BATTERY, RESISTOR };
	public boolean isLamp;
	public boolean isChangeable;
	Type type;
	public double voltageDif, current, resistance;
	
	private CircuitComponent(Type type){
		isChangeable = false;
		isLamp = false;
		voltageDif = current = resistance = 0;
		this.type = type;
	}
	
	public static CircuitComponent wire() {
		return new CircuitComponent(Type.WIRE);
	}
	
	public static CircuitComponent resistor() {
		return new CircuitComponent(Type.RESISTOR);
	}
	
	public static CircuitComponent lamp() {
		CircuitComponent comp = new CircuitComponent(Type.WIRE);
		comp.isLamp = true;
		return comp;
	}
	
	public static CircuitComponent battery() {
		return new CircuitComponent(Type.BATTERY);
	}
}
