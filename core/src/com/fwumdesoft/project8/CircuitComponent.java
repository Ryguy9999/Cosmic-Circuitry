package com.fwumdesoft.project8;

public class CircuitComponent{
	public boolean isWire, isBattery, isResistor, isChangeable;
	public double voltageDif, current, resistance;
	
	public CircuitComponent(){
		isWire = isBattery = isResistor = isChangeable = false;
		voltageDif = current = resistance = 0;
	}
}
