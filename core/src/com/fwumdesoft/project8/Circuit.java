package com.fwumdesoft.project8;

public class Circuit
{
	public CircuitComponent[][] grid;
	public int goalLamps;
	
	public Circuit(CircuitComponent[][] grid, int goalLamps)
	{
		this.grid = grid;
		this.goalLamps = goalLamps;
	}
	
	public boolean isSolved()
	{
		CircuitSolver.solve(grid);
		
		int count = 0;
		for(CircuitComponent[] row : grid)
			for(CircuitComponent comp : row)
				if(comp.isActive)
					count++;
		
		return count == goalLamps;
	}
}
