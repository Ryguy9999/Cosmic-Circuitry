package com.fwumdesoft.project8;

import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.utils.Array;
import com.fwumdesoft.project8.CircuitComponent.Type;

public class CircuitSpawner {
	private Array<Circuit> circuits;
	
	public CircuitSpawner(AssetManager manager) {
		circuits = new Array<>();
		manager.getAll(Circuit.class, circuits);
	}
	
	public Circuit newCircuit() {
		return circuits.pop();
	}
	
	public boolean finished(Circuit circuit) {
		for(CircuitComponent[] row : circuit.grid) 
			for(CircuitComponent item : row)
				if(item.type == Type.RESISTOR && item.isLamp && Math.abs(item.current - item.targetCurrent) > item.targetMargin)
					return false;
		return true;
	}
	
	public void abort(Circuit circuit, Inventory inventory) {
		for(int y = 0; y < circuit.grid.length; y++)
			for(int x = 0; x < circuit.grid[y].length; x++)
				if(circuit.grid[y][x].isChangeable && circuit.grid[y][x].type != null) {
					inventory.addComponent(circuit.grid[y][x]);
					circuit.grid[y][x] = CircuitComponent.blank();
				}
	}
}
