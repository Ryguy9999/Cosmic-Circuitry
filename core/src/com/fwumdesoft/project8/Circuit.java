package com.fwumdesoft.project8;

import java.io.Serializable;

public class Circuit implements Serializable
{
	private static final long serialVersionUID = 1L;
	public CircuitComponent[][] grid;
	public int goalLamps;

	public Circuit(CircuitComponent[][] grid, int goalLamps)
	{
		this.grid = grid;
		this.goalLamps = goalLamps;
	}

	public boolean isSolved()
	{
		for(CircuitComponent[] row : grid)
			for(CircuitComponent item : row)
				if(item != null && item.type == null)
					return false;
		
		CircuitSolver.solve(grid);

		int count = 0;
		for (CircuitComponent[] row : grid)
			for (CircuitComponent comp : row)
				if (comp != null && comp.isActive)
					count++;

		return count == goalLamps;
	}
}
