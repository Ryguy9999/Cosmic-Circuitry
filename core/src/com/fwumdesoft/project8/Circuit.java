package com.fwumdesoft.project8;

import java.io.Serializable;

public class Circuit implements Serializable
{
	private static final long serialVersionUID = 1L;
	public CircuitComponent[][] grid;
	public transient String name;
	public int goalLamps;
	
	public Circuit(Circuit original)
	{
		this.goalLamps = original.goalLamps;
		grid = new CircuitComponent[original.grid.length][];
		for(int i = 0; i < grid.length; i++)
		{
			grid[i] = new CircuitComponent[original.grid[i].length];
			for(int j = 0; j < grid[i].length; j++)
				if(original.grid[i][j] != null)
					grid[i][j] = new CircuitComponent(original.grid[i][j]);
		}
	}

	public Circuit(CircuitComponent[][] grid, int goalLamps)
	{
		this.grid = grid;
		this.goalLamps = goalLamps;
	}

	public boolean isSolved()
	{
		try
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
		} catch(Exception e)
		{
			return false;
		}
	}
}
