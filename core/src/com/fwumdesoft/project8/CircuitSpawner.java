package com.fwumdesoft.project8;

import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.utils.Array;
import com.fwumdesoft.project8.CircuitComponent.Type;

public class CircuitSpawner {
	private Array<CircuitComponent[][]> circuits;
	
	public CircuitSpawner(AssetManager manager) {
		circuits = new Array<>();
		manager.getAll(CircuitComponent[][].class, circuits);
	}
	
	public CircuitComponent[][] newCircuit() {
		return circuits.pop();
	}
	
	public boolean finished(CircuitComponent[][] circuit) {
		CircuitSolver.solve(circuit);
		for(CircuitComponent[] row : circuit) 
			for(CircuitComponent item : row)
				if(item.type == Type.RESISTOR && item.isLamp && Math.abs(item.current - item.targetCurrent) > item.targetMargin)
					return false;
		return true;
	}
	
	public void abort(CircuitComponent[][] circuit, Inventory inventory) {
		for(int y = 0; y < circuit.length; y++)
			for(int x = 0; x < circuit[y].length; x++)
				if(circuit[y][x].isChangeable && circuit[y][x].type != null) {
					inventory.addComponent(circuit[y][x]);
					circuit[y][x] = CircuitComponent.blank();
				}
	}
}
