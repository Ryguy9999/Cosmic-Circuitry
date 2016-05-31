package com.fwumdesoft.project8;

import java.util.ArrayList;
import java.util.Arrays;
import com.badlogic.gdx.math.Vector2;
import com.fwumdesoft.project8.CircuitComponent.Type;

public class CircuitSolver
{
	public static void main(String[] args)
	{
		SystemOfEquations system = new SystemOfEquations();
		Branch b1 = new Branch(null, null, null, null), b2 = new Branch(null, null, null, null),
				b3 = new Branch(null, null, null, null);
		Equation e1 = new Equation(new ArrayList<Term>(Arrays.asList(new Term[]
		{ new Term(0, b1), new Term(1, b2), new Term(1, b3) })), 5);
		Equation e2 = new Equation(new ArrayList<Term>(Arrays.asList(new Term[]
		{ new Term(1, b1), new Term(2, b2), new Term(3, b3) })), 14);
		Equation e3 = new Equation(new ArrayList<Term>(Arrays.asList(new Term[]
		{ new Term(7, b1), new Term(4, b2), new Term(9, b3) })), 42);
		Equation e4 = new Equation(new ArrayList<Term>(Arrays.asList(new Term[]
		{ new Term(7, b1), new Term(4, b2), new Term(0, b3) })), 15);
		system.equations.add(e1);
		system.equations.add(e2);
		system.equations.add(e3);
		system.equations.add(e4);
//		System.out.println(system.solve());
		
		CircuitComponent n = null;
		CircuitComponent w = CircuitComponent.wire();
		CircuitComponent v = CircuitComponent.battery();
		CircuitComponent r = CircuitComponent.resistor();
		v.voltageDif = 10;
		r.resistance = 5;
		CircuitComponent[][] circuit = new CircuitComponent[][]
		{
				{ w, w, w, v, w, w, w },
				{ w, n, n, n, n, n, w },
				{ w, n, n, n, n, n, w },
				{ w, n, n, n, n, n, w },
				{ w, w, w, r, w, w, w } };

		ArrayList<Vector2> junctions = new ArrayList<Vector2>();
		ArrayList<Branch> branches = new ArrayList<Branch>();
		buildJunctions(circuit, junctions);
		buildBranches(circuit, junctions, branches);
		System.out.println(junctions);
		System.out.println(branches);

		// System.out.println(buildEquation(circuit, junctions, branches,
		// branches.get(0).startDirection, branches.get(0).start,
		// branches.get(0), branches.get(0), new ArrayList<Branch>()));

		solve(circuit);
		System.out.println(r.current + " " + v.current);
	}

	/**
	 * Takes in a 2D array representing a PROPER circuit, and sets each
	 * component's current value to its proper amount
	 * 
	 * @param circuit
	 *            Represents the 2D of a circuit to be solved
	 */
	public static void solve(CircuitComponent[][] circuit)
	{
		SystemOfEquations system = new SystemOfEquations();
		ArrayList<Vector2> junctions = new ArrayList<Vector2>();
		ArrayList<Branch> branches = new ArrayList<Branch>();
		buildJunctions(circuit, junctions);
		buildBranches(circuit, junctions, branches);

		// Make an equation for each junction using Kirchoff's Junction Rule
		for (Vector2 junction : junctions)
		{
			Equation equation = new Equation();
			for (Branch branch : branches)
			{
				if (branch.start.equals(junction))
					equation.terms.add(new Term(1, branch));
				if (branch.end.equals(junction))
					equation.terms.add(new Term(-1, branch));
			}

			if (!system.equations.contains(equation))
				system.equations.add(equation);
		}

		ArrayList<Branch> remainingBranches = new ArrayList<Branch>(branches);
		// Make equations using Kirchoff's Loop Rule, ensuring each branch is
		// used at least once
		while (remainingBranches.size() > 0)
		{
			Branch branch = remainingBranches.remove(0);

			ArrayList<Term> terms = buildEquation(circuit, junctions, branches, branch.start, branch.startDirection,
					branch, branch, new ArrayList<Branch>());
			int constant = 0;
			for (int t = 0; t < terms.size(); t++)
				if (terms.get(t).branch == null)
				{
					constant -= terms.get(t).coefficient;
					terms.remove(t--);
				}

			Equation equation = new Equation(terms, constant);
			for (Term term : terms)
				remainingBranches.remove(term.branch);

			system.equations.add(equation);
		}

		// Organize each equation's terms, including 0's for branchs that dont
		// exist
		for (Equation equation : system.equations)
			equation.sort(branches);

		ArrayList<Double> result = system.solve();

		// With the branch results (finally!) put the numbers into each branch's
		// component(s)
		for (int i = 0; i < branches.size(); i++)
			fillBranch(circuit, branches.get(i), result.get(i));
	}

	/**
	 * Sets the current value in each component in the branch to the given value
	 * 
	 * @param circuit
	 *            The 2D array representing circuit
	 * @param branch
	 *            The branch currently being filled
	 * @param loc
	 *            The start location of branch iteration
	 * @param prev
	 *            The junction "previous" to the start location
	 * @param current
	 *            The current value flowing through this branch
	 */
	private static void fillBranch(CircuitComponent[][] circuit, Branch branch, double current)
	{
		Vector2 loc = new Vector2(branch.startDirection), prev = new Vector2(branch.start);

		while (!loc.equals(branch.end))
		{
			if (circuit[(int) loc.x][(int) loc.y].type != Type.WIRE)
			{
				circuit[(int) loc.x][(int) loc.y].current = Math.abs(current);
				if (circuit[(int) loc.x][(int) loc.y].isLamp && Math.abs(Math.abs(current)
						- circuit[(int) loc.x][(int) loc.y].targetCurrent) < circuit[(int) loc.x][(int) loc.y].targetMargin)
					circuit[(int) loc.x][(int) loc.y].isActive = true;
			}

			if (loc.x + 1 < circuit.length && !new Vector2(loc.x + 1, loc.y).equals(prev)
					&& circuit[(int) loc.x + 1][(int) loc.y] != null)
			{
				prev.set(loc);
				loc.x++;
			} else if (loc.y + 1 < circuit[0].length && !new Vector2(loc.x, loc.y + 1).equals(prev)
					&& circuit[(int) loc.x][(int) loc.y + 1] != null)
			{
				prev.set(loc);
				loc.y++;
			} else if (loc.x - 1 >= 0 && !new Vector2(loc.x - 1, loc.y).equals(prev)
					&& circuit[(int) loc.x - 1][(int) loc.y] != null)
			{
				prev.set(loc);
				loc.x--;
			} else if (loc.y - 1 >= 0 && !new Vector2(loc.x, loc.y - 1).equals(prev)
					&& circuit[(int) loc.x][(int) loc.y - 1] != null)
			{
				prev.set(loc);
				loc.y--;
			}
		}
	}

	/**
	 * Using Kirchoff's Loop Rule, will build an equation recursively from the
	 * startBranch
	 * 
	 * @param circuit
	 *            The 2D array representing the circuit
	 * @param junctions
	 *            The list of junctions in the circuit
	 * @param branches
	 *            The list of branches in the circuit
	 * @param loc
	 *            The start location of this call
	 * @param prev
	 *            The "previous" location to loc (used to move in the proper
	 *            direction)
	 * @param currentBranch
	 *            The branch this call is iterating on
	 * @param startBranch
	 *            The initial branch, used as the base case
	 * @param checkedBranches
	 *            The branches already being checked, used to make sure the path
	 *            doesn't loop where it shouldn't
	 * @return An ArrayList of Terms for an equation, not necessarily in order
	 *         according to "branches"
	 */
	private static ArrayList<Term> buildEquation(CircuitComponent[][] circuit, ArrayList<Vector2> junctions,
			ArrayList<Branch> branches, Vector2 prev, Vector2 loc, Branch currentBranch, Branch startBranch,
			ArrayList<Branch> checkedBranches)
	{
		ArrayList<Term> workingSet = new ArrayList<Term>();
		checkedBranches.add(currentBranch);
		int currentFactor = prev.equals(currentBranch.end) ? -1 : 1;
		loc = new Vector2(loc);
		prev = new Vector2(prev);

		// Iterate over the current branch until we hit a junction, adding terms
		// for components in the process
		while (!junctions.contains(loc))
		{
			switch (circuit[(int) loc.x][(int) loc.y].type)
			{
			case RESISTOR:
				workingSet.add(new Term(currentFactor * circuit[(int) loc.x][(int) loc.y].resistance, currentBranch));
				break;
			case BATTERY:
				workingSet.add(new Term(-currentFactor * circuit[(int) loc.x][(int) loc.y].voltageDif, null));
				break;
			default:
				break;
			}

			if (loc.x + 1 < circuit.length && !new Vector2(loc.x + 1, loc.y).equals(prev)
					&& circuit[(int) loc.x + 1][(int) loc.y] != null)
			{
				prev.set(loc);
				loc.x++;
			} else if (loc.y + 1 < circuit[0].length && !new Vector2(loc.x, loc.y + 1).equals(prev)
					&& circuit[(int) loc.x][(int) loc.y + 1] != null)
			{
				prev.set(loc);
				loc.y++;
			} else if (loc.x - 1 >= 0 && !new Vector2(loc.x - 1, loc.y).equals(prev)
					&& circuit[(int) loc.x - 1][(int) loc.y] != null)
			{
				prev.set(loc);
				loc.x--;
			} else if (loc.y - 1 >= 0 && !new Vector2(loc.x, loc.y - 1).equals(prev)
					&& circuit[(int) loc.x][(int) loc.y - 1] != null)
			{
				prev.set(loc);
				loc.y--;
			}
		}

		// If the tracing brings us back to the startBranch, then collapse the
		// recursion
		if (startBranch.start.equals(loc) || startBranch.start.equals(loc) || startBranch.start.equals(loc)
				|| startBranch.start.equals(loc))
			return workingSet;

		// Attempt to recur on each branch in the junction until we find a
		// success
		ArrayList<Term> result = null;
		for (Branch branch : branches)
		{
			if (!checkedBranches.contains(branch) && (branch.start.equals(loc) || branch.end.equals(loc)))
			{
				checkedBranches.add(branch);
				result = buildEquation(circuit, junctions, branches, loc,
						new Vector2(branch.start.equals(loc) ? branch.startDirection : branch.endDirection), branch,
						startBranch, checkedBranches);
				if (result != null)
					break;
			}
		}

		// If the recursion fails (looped into itself) fail this call
		if (result == null)
		{
			checkedBranches.remove(currentBranch);
			return null;
		} else
		{// Otherwise, succeed by trace-back
			workingSet.addAll(result);
			return workingSet;
		}
	}

	/**
	 * Fills the junctions list with references to each junction in the circuit
	 * 
	 * @param circuit
	 *            The 2D array representing the circuit
	 * @param junctions
	 *            The list of Vector2s to be filled with junctions
	 */
	private static void buildJunctions(CircuitComponent[][] circuit, ArrayList<Vector2> junctions)
	{
		Vector2 backup = null;
		for (int x = 0; x < circuit.length; x++)
			for (int y = 0; y < circuit[x].length; y++)
				if (circuit[x][y] != null)
					if (circuit[x][y].type != Type.WIRE)
					{
						int count = 0;
						if (x + 1 < circuit.length && circuit[x + 1][y] != null)
							count++;
						if (y + 1 < circuit[x].length && circuit[x][y + 1] != null)
							count++;
						if (x - 1 >= 0 && circuit[x - 1][y] != null)
							count++;
						if (y - 1 >= 0 && circuit[x][y - 1] != null)
							count++;

						if (count > 2)
							junctions.add(new Vector2(x, y));
					} else if (backup == null)
						backup = new Vector2(x, y);

		if (junctions.size() == 0)
			junctions.add(backup);
	}

	/**
	 * Fills branches with references to branches in the circuit, with
	 * branch.start on the start junction and branch.end just before the end
	 * junction
	 * 
	 * @param circuit
	 *            The 2D array representing the circuit
	 * @param junctions
	 *            The list of junctions in the circuit
	 * @param branches
	 *            The list to be filled with branches
	 */
	private static void buildBranches(CircuitComponent[][] circuit, ArrayList<Vector2> junctions,
			ArrayList<Branch> branches)
	{
		for (Vector2 junction : junctions)
		{
			if (junction.x + 1 < circuit.length && circuit[(int) junction.x + 1][(int) junction.y] != null)
				buildBranch(circuit, junctions, branches, new Vector2(junction),
						new Vector2(junction.x + 1, junction.y));
			if (junction.y + 1 < circuit[0].length && circuit[(int) junction.x][(int) junction.y + 1] != null)
				buildBranch(circuit, junctions, branches, new Vector2(junction),
						new Vector2(junction.x, junction.y + 1));
			if (junction.x - 1 >= 0 && circuit[(int) junction.x - 1][(int) junction.y] != null)
				buildBranch(circuit, junctions, branches, new Vector2(junction),
						new Vector2(junction.x - 1, junction.y));
			if (junction.y - 1 >= 0 && circuit[(int) junction.x][(int) junction.y - 1] != null)
				buildBranch(circuit, junctions, branches, new Vector2(junction),
						new Vector2(junction.x, junction.y - 1));
		}
	}

	private static void buildBranch(CircuitComponent[][] circuit, ArrayList<Vector2> junctions,
			ArrayList<Branch> branches, Vector2 prev, Vector2 loc)
	{
		// If the branch already exists, dont make again
		for (Branch branch : branches)
			if (branch.endDirection.equals(loc))
				return;

		Vector2 start = new Vector2(prev);
		Vector2 startDir = new Vector2(loc);

		// Find the end of the branch by iterating until we hit a junction
		boolean cont = true;
		while (cont)
			if (junctions.contains(loc))
				cont = false;
			else if (loc.x + 1 < circuit.length && !new Vector2(loc.x + 1, loc.y).equals(prev)
					&& circuit[(int) loc.x + 1][(int) loc.y] != null)
			{
				prev.set(loc);
				loc.x++;
			} else if (loc.y + 1 < circuit[(int) loc.x].length && !new Vector2(loc.x, loc.y + 1).equals(prev)
					&& circuit[(int) loc.x][(int) loc.y + 1] != null)
			{
				prev.set(loc);
				loc.y++;
			} else if (loc.x - 1 >= 0 && !new Vector2(loc.x - 1, loc.y).equals(prev)
					&& circuit[(int) loc.x - 1][(int) loc.y] != null)
			{
				prev.set(loc);
				loc.x--;
			} else if (loc.y - 1 >= 0 && !new Vector2(loc.x, loc.y - 1).equals(prev)
					&& circuit[(int) loc.x][(int) loc.y - 1] != null)
			{
				prev.set(loc);
				loc.y--;
			}

		// In the special case of two adjacent junctions, don't make them a
		// branch!
		if (!junctions.contains(prev))
			branches.add(new Branch(start, startDir, loc, prev));
	}

	/**
	 * Represents a system of equations
	 *
	 */
	private static class SystemOfEquations
	{
		public ArrayList<Equation> equations;

		public SystemOfEquations()
		{
			equations = new ArrayList<Equation>();
		}

		/**
		 * Solves the system
		 * 
		 * @return An ArrayList of Doubles which are the currents through each
		 *         branch
		 */
		public ArrayList<Double> solve()
		{
			int numVars = equations.get(0).terms.size();
			// By the end of this loop, the first "numVars" equations will be in
			// Reduced Row Echeleon Form
			for (int row = 0; row < numVars; row++)
			{
				// Row echeleon form needs something other than zero in diagonal
				if (equations.get(row).terms.get(row).coefficient == 0)
					for (int e = row + 1; e < equations.size(); e++)
						if (equations.get(e).terms.get(row).coefficient != 0)
						{
							equations.set(row, equations.set(e, equations.get(row)));
							break;
						}

				if (equations.get(row).terms.get(row).coefficient != 1)
					equations.set(row, equations.get(row).scalar(1.0 / equations.get(row).terms.get(row).coefficient));

				// Zero everything above and below the 1, for reduced row
				// echeleon
				for (int e = 0; e < equations.size(); e++)
					if (e == row)
						continue;
					else if (equations.get(e).terms.get(row).coefficient != 0)
						equations.set(e, equations.get(row).scalar(-equations.get(e).terms.get(row).coefficient)
								.add(equations.get(e)));
			}

			ArrayList<Double> result = new ArrayList<Double>();
			for (int b = 0; b < numVars; b++)
				result.add(equations.get(b).constant);

			return result;
		}

		public String toString()
		{
			return equations.toString().replace("[", "").replace("]", "").replace(", ", "\n");
		}
	}

	/**
	 * Represents an equation for a system of equations
	 *
	 */
	private static class Equation
	{
		public ArrayList<Term> terms;
		public double constant;

		public Equation()
		{
			this(new ArrayList<Term>(), 0);
		}

		public Equation(ArrayList<Term> terms, double constant)
		{
			this.terms = terms;
			this.constant = constant;
		}

		/**
		 * Rearranges the terms according to branches and adds terms of 0 for
		 * missing branches
		 * 
		 * @param branches
		 *            The list of branches to be rearranged according to
		 */
		public void sort(ArrayList<Branch> branches)
		{
			for (int i = 0; i < terms.size() - 1; i++)
				for (int j = i + 1; j < terms.size(); j++)
					if (terms.get(i).branch == terms.get(j).branch)
					{
						terms.set(i, terms.get(i).add(terms.get(j)));
						terms.remove(j--);
					}

			for (int b = 0; b < branches.size(); b++)
			{
				int i = 0;
				while (i < terms.size())
				{
					if (terms.get(i).branch.equals(branches.get(b)))
						break;
					i++;
				}

				if (i < terms.size())
					terms.set(b, terms.set(i, terms.get(b)));
				else
					terms.add(b, new Term(0, branches.get(b)));
			}
		}

		/**
		 * Makes a new equation multiplied by a given value
		 * 
		 * @param scale
		 *            The value to multiply the equation by
		 * @return The new Equation
		 */
		public Equation scalar(double scale)
		{
			Equation result = new Equation(new ArrayList<Term>(), constant * scale);

			for (Term term : terms)
				result.terms.add(new Term(term.coefficient * scale, term.branch));

			return result;
		}

		/**
		 * Adds two equations together and returns the result, combining like
		 * terms
		 * 
		 * @param equation
		 *            The other equation to be added
		 * @return The resulting equation
		 */
		public Equation add(Equation equation)
		{
			Equation result = new Equation(new ArrayList<Term>(), constant + equation.constant);
			for (Term term : terms)
				result.terms.add(term);
			for (Term term : equation.terms)
				result.terms.add(term);

			for (int i = 0; i < result.terms.size() - 1; i++)
				for (int j = i + 1; j < result.terms.size(); j++)
					if (result.terms.get(i).branch == result.terms.get(j).branch)
					{
						result.terms.set(i, result.terms.get(i).add(result.terms.get(j)));
						result.terms.remove(j--);
					}

			return result;
		}

		@SuppressWarnings("unused")
		public boolean equals(Equation equation)
		{
			return terms.equals(equation.terms) && constant == equation.constant;
		}

		public String toString()
		{
			return terms.toString().replace("[", "").replace("]", "").replace(", ", " + ") + " = " + constant;
		}
	}

	/**
	 * Represents a term in an equation
	 *
	 */
	private static class Term
	{
		public double coefficient;
		public Branch branch;

		public Term(double coefficient, Branch branch)
		{
			this.coefficient = coefficient;
			this.branch = branch;
		}

		public Term add(Term term)
		{
			return new Term(coefficient + term.coefficient, branch);
		}

		@SuppressWarnings("unused")
		public boolean equals(Term term)
		{
			return coefficient == term.coefficient && branch == term.branch;
		}

		public String toString()
		{
			return coefficient + "*" + branch;
		}
	}

	/**
	 * Represents a branch in a circuit
	 *
	 */
	private static class Branch
	{
		public Vector2 start, startDirection, end, endDirection;

		public Branch(Vector2 start, Vector2 startDirection, Vector2 end, Vector2 endDirection)
		{
			this.start = start;
			this.end = end;
			this.startDirection = startDirection;
			this.endDirection = endDirection;
		}

		public boolean equals(Branch branch)
		{
			return startDirection.equals(branch.startDirection) && endDirection.equals(branch.endDirection);
		}

		public String toString()
		{
			return start + ":" + startDirection + "->" + endDirection;
		}
	}
}
