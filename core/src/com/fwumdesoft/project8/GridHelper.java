package com.fwumdesoft.project8;

/**
 * Various helper utilities for a grid
 */
public class GridHelper
{
	@FunctionalInterface
	/**
	 * Describes a function that operators on a point of a grid </br>
	 * Passing the grid would be redudant because any use of GridFunction will
	 * require having the grid, so it can be wrapped in the lambda
	 */
	public static interface GridFunction
	{
		public void run(int x, int y);
	}

	/**
	 * Do an action in all the cardinal directions on a grid
	 * 
	 * @param grid
	 *            The grid object
	 * @param x
	 *            The initial x
	 * @param y
	 *            The initial y
	 * @param func
	 *            The function to apply to each space
	 */
	public static <T> void doCardinal(T[][] grid, int x, int y, GridFunction func)
	{
		if (y > 0)
			func.run(x, y - 1);
		if (y < grid.length - 1)
			func.run(x, y + 1);
		if (x > 0)
			func.run(x - 1, y);
		if (x < grid[y].length - 1)
			func.run(x + 1, y);

	}
}
