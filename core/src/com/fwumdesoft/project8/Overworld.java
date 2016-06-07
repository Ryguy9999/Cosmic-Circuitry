package com.fwumdesoft.project8;

import java.awt.Point;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.utils.Array;

public class Overworld
{
	/**
	 * Types of tiles
	 */
	public static enum tiles
	{
		space, wall, door, floor, fireSuppression, componentMachine, terminal, pod
	}
	
	/**
	 * Tile modifiers such as being on fire
	 */
	public static enum mods
	{
		none, broken, componentPile, fire
	}

	App app;
	tiles[][] map;
	mods[][] modifiers;
	Point playerPos, playerFace, previousPlayerPos;
	Array<Circuit> circuits;
	HashMap<Point, Circuit> worldCircuits;
	Circuit currentCircuit;
	Inventory inventory;
	boolean gameWon;
	private boolean noClip;
	public boolean playFire;
	public boolean playerMoving;
	int playerHealth;
	final int MAX_PLAYER_HEALTH = 5;
	final int FIRE_SUPPRESSION_RANGE = 12, TERMINAL_COUNT = 3;
	final double FIRE_SUPPRESSION_EFFECTIVENESS = 0.15;
	final double FIRE_SPREAD_CHANCE = 0.30;
	final int CELL_SIZE = 32;

	public Overworld(App app, int size, Array<Circuit> circuits, Inventory inventory)
	{
		this.inventory = inventory;
		// contains permanent tiles
		map = new tiles[size][size];
		// contains temporary modifiers or stuff that goes on walls
		modifiers = new mods[size][size];
		for (int i = 0; i < size; i++)
		{
			for (int j = 0; j < size; j++)
			{
				map[i][j] = tiles.space;
				modifiers[i][j] = mods.none;
			}
		}

		// Generates a station by generating rooms with doors and connecting
		// rooms to said doors
		Door door = new Door(size / 2, size / 2, 0);
		playerPos = new Point(size / 2 - 2, size / 2);
		previousPlayerPos = new Point(playerPos);
		boolean firstDoor = true;
		this.playerFace = new Point();
		playerMoving = false;
		this.app = app;
		worldCircuits = new HashMap<Point, Circuit>();
		noClip = false;
		currentCircuit = null;

		for (int i = 0; i < 100; i++)
		{
			door = generateRoom(door, firstDoor);
			firstDoor = false;
		}
		this.circuits = circuits;
		removeStrayDoors();
		spawnFireSuppression();
		spawnTerminals();
		spawnProducerMachines();
		//Remove fire near spawn
		for (int x = -5; x < 6; x++)
		{
			for (int y = -5; y < 6; y++)
			{
				if (modifiers[playerPos.y + y][playerPos.x + x] == mods.fire)
					modifiers[playerPos.y + y][playerPos.x + x] = mods.none;
			}
		}
		
		map[playerPos.y + 1][playerPos.x + 1] = tiles.fireSuppression;
		map[playerPos.y + 1][playerPos.x - 1] = tiles.componentMachine;
		modifiers[playerPos.y - 1][playerPos.x + 1] = mods.componentPile;
		modifiers[playerPos.y - 1][playerPos.x - 1] = mods.componentPile;
		spawnEscapePod();
		distributeCircuits();
		gameWon = false;
		playerHealth = MAX_PLAYER_HEALTH;
	}

	public String toString()
	{
		String str = "[";
		for (int i = 0; i < map.length; i++)
		{
			str += "[";
			for (int j = 0; j < map[i].length; j++)
			{
				str += map[i][j] + ",";
			}
			str += "],";
		}
		str += "]";
		return str;
	}

	/**
	 * Move the player in the game, if the space is free </br>
	 * Does not check intervening spaces
	 * 
	 * @param xAmt
	 *            The x translation
	 * @param yAmt
	 *            The y translation
	 * @return If the player moved or not
	 */
	public boolean movePlayer(int xAmt, int yAmt)
	{
		boolean spotFree = isOpen(playerPos.x + xAmt, playerPos.y + yAmt);
		playerFace.setLocation(xAmt, yAmt);
		if (spotFree)
		{
			App.playSound(App.sounds.walking, 1);
			previousPlayerPos = new Point(playerPos);
			playerPos.x += xAmt;
			playerPos.y += yAmt;
			turn();
			playerMoving = true;
		}
		return spotFree;
	}
	
	/**
	 * Interact with the tile the player is facing
	 */
	public void interact()
	{
		Point lookAt = new Point(playerPos.x + playerFace.x, playerPos.y + playerFace.y);
		if(worldCircuits.containsKey(lookAt))
			currentCircuit = worldCircuits.get(lookAt);
		else
			currentCircuit = null;
	}
	
	public void circuitSuccess() 
	{
		Point lookAt = new Point(playerPos.x + playerFace.x, playerPos.y + playerFace.y);
		switch(modifiers[lookAt.y][lookAt.x])
		{
		case broken:
			if(map[lookAt.y][lookAt.x] == tiles.door || map[lookAt.y][lookAt.x] == tiles.fireSuppression ||
			map[lookAt.y][lookAt.x] == tiles.terminal)
				modifiers[lookAt.y][lookAt.x] = mods.none;
			break;
		default:
			break;
		}
	}
	
	public void circuitFail()
	{
		Point lookAt = new Point(playerPos.x + playerFace.x, playerPos.y + playerFace.y);
		switch(modifiers[lookAt.y][lookAt.x])
		{
		case none:
			if(map[lookAt.y][lookAt.x] == tiles.door || map[lookAt.y][lookAt.x] == tiles.fireSuppression)
				modifiers[lookAt.y][lookAt.x] = mods.broken;
			break;
		default:
			break;
		}
	}
	
	/**
	 * Causes the player to rest. Health regenerates and one turn passes
	 */
	public void rest()
	{
		if(playerHealth < MAX_PLAYER_HEALTH)
			playerHealth++;
		turn();
	}
	
	/**
	 * A turn that occurs after moving or resting, allows time to progress
	 */
	private void turn() {
		//check if player is within 5 tiles of fire
		playFire = false;
		for(int y = -1 * ((Gdx.graphics.getWidth() / (CELL_SIZE * 2)) + 3);
				y < ((Gdx.graphics.getWidth() / (CELL_SIZE * 2)) + 4); y++)
		{
			for(int x = -1 * ((Gdx.graphics.getHeight() / (CELL_SIZE * 2)) + 3);
					x < ((Gdx.graphics.getHeight() / (CELL_SIZE * 2)) + 4); x++)
			{
				playFire = playFire || modifiers[playerPos.y + y][playerPos.x + x] == mods.fire;
			}
		}
		
		//Changes to modifiers
		for(int y = 0; y < modifiers.length; y++)
		{
			for(int x = 0; x < modifiers[y].length; x++)
			{
				//Spread fire
				if(modifiers[y][x] == mods.fire)
				{
					if(Math.random() < FIRE_SPREAD_CHANCE)
					{
						int spreadX = (int)(Math.random() * 3) - 1;
						int spreadY = (int)(Math.random() * 3) - 1;
						if(map[y + spreadY][x + spreadX] == tiles.floor)//Regular spread
							modifiers[y + spreadY][x + spreadX] = mods.fire;
						else if(map[y + spreadY][x + spreadX] == tiles.door && Math.random() < 0.2)//Spread through door
							modifiers[y + spreadY][x + spreadX] = mods.fire;
					}
					else if(Math.random() < FIRE_SPREAD_CHANCE / 6)
					{
						modifiers[y][x] = mods.none;
						int drawX = (x - playerPos.x) * 32 + Gdx.graphics.getWidth() / 2;
						int drawY = (y - playerPos.y) * 32 + Gdx.graphics.getHeight() / 2;
						ParticleSystem.burst("smoke", drawX, drawY, 4);
					}
				}
				
				//Fire suppression
				if(map[y][x] == tiles.fireSuppression && modifiers[y][x] != mods.broken)
					for(int c = 0; c < Math.pow(FIRE_SUPPRESSION_RANGE*2+1, 2) * FIRE_SUPPRESSION_EFFECTIVENESS; c++)
					{
						int j = (int)(Math.random() * FIRE_SUPPRESSION_RANGE * 2 + 1) + y - FIRE_SUPPRESSION_RANGE;
						int i = (int)(Math.random() * FIRE_SUPPRESSION_RANGE * 2 + 1) + x - FIRE_SUPPRESSION_RANGE;
						if(modifiers[j][i] == mods.fire)
						{
							modifiers[j][i] = mods.none;
						}
					}
				
				//Component machine
				if(map[y][x] == tiles.componentMachine && Math.random() < 0.1)
					if(y-1 >= 0 && modifiers[y-1][x] == mods.none)
					{
						modifiers[y-1][x] = mods.componentPile;
						App.playSound(App.sounds.componentMachine, (float)playerPos.distance(x, y));
					}
			}
		}
		
		//Damage/ death by fire
		if(modifiers[playerPos.y][playerPos.x]== mods.fire)
			playerHealth -= 2;
		if(playerHealth <= 0)
			app.gameOver();
		
		//Pick up bags
		if(modifiers[playerPos.y][playerPos.x] == mods.componentPile)
		{
			modifiers[playerPos.y][playerPos.x] = mods.none;
			inventory.addComponent(CircuitComponent.randomComponent());
			while(Math.random() < 1.0/3.0)
				inventory.addComponent(CircuitComponent.randomComponent());

		}
		
		//victory
		if(map[playerPos.y][playerPos.x] == tiles.pod)
			gameWon = true;
	}
	
	public boolean equals(Overworld ow)
	{
		return ow != null && Arrays.deepEquals(map, ow.map) && 
				Arrays.deepEquals(modifiers, ow.modifiers) && playerPos.equals(ow.playerPos) && 
				playerFace.equals(ow.playerFace) && worldCircuits.equals(ow.worldCircuits);
	}
	
	private void distributeCircuits()
	{
		List<Circuit> circuits = Arrays.asList(this.circuits.toArray());
		Function<String, List<Circuit>> getCircuits = suffix -> circuits.stream().filter(circuit ->
					circuit.name.endsWith(suffix)).collect(Collectors.toList());
		List<Circuit> doorCircuits = getCircuits.apply("door");
		List<Circuit> fireSuppression = getCircuits.apply("fire");
		List<Circuit> terminalCircuits = getCircuits.apply("terminal");
		List<Circuit> solvedDoorCircuits = getCircuits.apply("door_solved");
		List<Circuit> solvedFireSuppression = getCircuits.apply("fire_solved");

		for(int y = 0; y < modifiers.length; y++)
			for(int x = 0; x < modifiers[y].length; x++)
			{
				Circuit c = null;
				switch(modifiers[y][x])
				{
				case broken:
					if(map[y][x] == tiles.door)
						c = new Circuit(getRandom(doorCircuits));
					else if(map[y][x] == tiles.fireSuppression)
						c = new Circuit(getRandom(fireSuppression));
					else if(map[y][x] == tiles.terminal)
						c = new Circuit(terminalCircuits.remove(0));
					break;
				case none:
					if(map[y][x] == tiles.door)
						c = new Circuit(getRandom(solvedDoorCircuits));
					else if(map[y][x] == tiles.fireSuppression)
						c = new Circuit(getRandom(solvedFireSuppression));
					break;
				default:
					break;
				}
				if(c != null)
					worldCircuits.put(new Point(x, y), c);
			}
	}
	
	private <T> T getRandom(List<T> list)
	{
		return list.get((int)(Math.random() * list.size()));
	}
	
	/**
	 * Generate room connected to door, with a chance to remove the connecting
	 * wall to combine the rooms. Will also generate a new door for further
	 * connections
	 * 
	 * @param door
	 * @return New door generated
	 */
	private Door generateRoom(Door door, boolean firstDoor)
	{
		// The room faces the opposite direction of the door it is connected to
		// 0 - left, 1 - top, 2 - right, 3 - bottom
		int x = door.x, y = door.y;
		// Uses facing direction to determine top left coordinate of room
		switch ((door.facing + 2) % 4)
		{
		case 0:
			y -= 2;
			break;
		case 1:
			x -= 2;
			break;
		case 2:
			y -= 2;
			x -= 4;
			break;
		case 3:
			y -= 4;
			x -= 2;
			break;
		}
		// randomly generates 1 more door (can overlap, but not with first door)
		int position = (int) (Math.random() * 4);
		while (position == (door.facing + 2) % 4)
			position = (int) (Math.random() * 4);
		int xOffset = 0;
		int yOffset = 0;
		switch (position)
		{
		case 0:
			yOffset = 2;
			break;
		case 1:
			xOffset = 2;
			break;
		case 2:
			yOffset = 2;
			xOffset = 4;
			break;
		case 3:
			yOffset = 4;
			xOffset = 2;
			break;
		}
		map[y + yOffset][x + xOffset] = tiles.door;
		Door nextDoor = new Door(x + xOffset, y + yOffset, position);
		modifiers[y + yOffset][x + xOffset] = (Math.random() < 0.2)? mods.broken : mods.none;
		// Sometimes wall will not generate to make larger connected rooms
		//Will only knock out wall of first door to ensure it is not opening into space
		if (Math.random() < 0.25 && !firstDoor)
		{
			switch ((door.facing + 2) % 4)
			{
			case 0:
			case 2:
				map[door.y + 1][door.x] = tiles.floor;
				map[door.y][door.x] = tiles.floor;
				map[door.y - 1][door.x] = tiles.floor;
				break;
			case 1:
			case 3:
				map[door.y][door.x + 1] = tiles.floor;
				map[door.y][door.x] = tiles.floor;
				map[door.y][door.x - 1] = tiles.floor;
				break;
			}
		}
		// Fills in walls and floor
		for (int i = x; i < x + 5; i++)
		{
			for (int j = y; j < y + 5; j++)
			{
				if (i == x || i == x + 4 || j == y || j == y + 4)
				{
					if (map[j][i] == tiles.space)
					{
						map[j][i] = tiles.wall;
					}
				}
				// Floor
				else
				{
					map[j][i] = tiles.floor;
					if(Math.random() < 0.2)
						modifiers[j][i] = mods.fire;
					else if(Math.random() < 0.1)
						modifiers[j][i] = mods.componentPile;
					else
						modifiers[j][i] = mods.none;
				}
			}
		}
		return nextDoor;
	}
	
	/**
	 * Spawns terminals throughout the map.
	 */
	private void spawnTerminals()
	{
		for(int t = 0; t < TERMINAL_COUNT; t++)
		{
			int y = (int)(Math.random() * map.length);
			int x = (int)(Math.random() * map[y].length);
			
			while(map[y][x] != tiles.floor)
			{
				y = (int)(Math.random() * map.length);
				x = (int)(Math.random() * map[y].length);
			}
			
			while(map[y][x] == tiles.floor)
				y++;
			
			if(map[y][x] == tiles.wall)
			{
				map[y][x] = tiles.terminal;
				modifiers[y][x] = mods.broken;
			}
			else
				t--;
		}
	}
	
	/**
	 * Checks if all terminals are solved.
	 * @return true if all terminals are solved, false otherwise.
	 */
	private boolean allTerminalsSolved()
	{
		boolean solved = true;
		for(int y = 0; y < map.length; y++)
		{
			for(int x = 0; x < map[y].length; x++)
			{
				if(map[y][x] == tiles.terminal && modifiers[y][x] == mods.broken)
					solved = false;
			}
		}
		return solved;
	}
	
	/**
	 * Spawns fire suppression throughout the map.
	 */
	private void spawnFireSuppression()
	{
		for(int y = 0; y < map.length; y += FIRE_SUPPRESSION_RANGE)
		{
			for(int x = 0; x < map[y].length; x++)
			{
				if(map[y][x] == tiles.floor && !(map[y + 1][x] == tiles.door || map[y - 1][x] == tiles.door ||
						map[y][x - 1] == tiles.door || map[y][x + 1] == tiles.door))
				{
					map[y][x] = tiles.fireSuppression;
					modifiers[y][x] = (Math.random() < 0.75)? mods.broken: mods.none;
					x += (Math.random() * (FIRE_SUPPRESSION_RANGE / 2)) + (FIRE_SUPPRESSION_RANGE);
				}
			}
		}
	}
	
	/**
	 * Spawns component producers throughout the map.
	 */
	private void spawnProducerMachines()
	{
		for(int y = 0; y < map.length; y += 15)
		{
			for(int x = 0; x < map[y].length; x++)
			{
				if(map[y][x] == tiles.floor && map[y - 1][x] == tiles.floor && Math.random() < 0.75)
				{
					map[y][x] = tiles.componentMachine;
					x += (Math.random() * 20) + (20);
				}
			}
		}
	}
	
	/**
	 * Spawns the escape pod and a door leading to it. The pod is
	 * on the exterior of the station.
	 */
	private void spawnEscapePod()
	{
		//randomly pick spots until a wall next to space is picked
		int x, y;
		boolean valid = false;
		do
		{
			y = (int)(Math.random() * map.length);
			x = (int)(Math.random() * map[y].length);
			if(map[y][x] == tiles.wall)
			{
				for(int i = -1; i < 2 && !valid; i++)
				{
					for(int j = -1; j < 2 && !valid; j++)
					{
						if((i == 0 || j == 0) && (map[y + i][x + j] == tiles.space && map[y + (i * -1)][x + (j * -1)] == tiles.floor))
						{
							map[y + i][x + j] = tiles.pod;
							map[y][x] = tiles.door;
							valid = true;
						}
					}
				}
			}
		}while(!valid);
	}

	/**
	 * Removes doors that are left over from room generation. Makes sure doors are
	 * connected to walls.
	 * 
	 * Captain's log: Star Date sometime I have given up. The doors won't go
	 * away. We've tried everything. I have accepted that this is as good a
	 * solution as any for the time being. Save yourself, don't try to fix the
	 * doors. *static* Or else *static* will *static* <end transmission>
	 */
	private void removeStrayDoors()
	{
		for (int i = 0; i < map.length; i++)
		{
			for (int j = 0; j < map[i].length; j++)
			{
				if (map[j][i] == tiles.door)
				{
					boolean adjacentWall = false, adjacentSpace = false;
					for (int x = -1; x < 2; x++)
					{
						for (int y = -1; y < 2; y++)
						{
							if (map[j + y][i + x] == tiles.space)
								adjacentSpace = true;
							else if (map[j + y][i + x] == tiles.wall)
								adjacentWall =true;
						}
					}
					if (!adjacentWall)
						map[j][i] = tiles.floor;
					if(adjacentSpace)
						map[j][i] = tiles.wall;
				}
			}
		}
	}

	/**
	 * 
	 * @param x
	 *            x coordinate of tile
	 * @param y
	 *            y coordinate of tile
	 * @return true if the player, fire, vacuum, etc. can spread or move through
	 *         this tile
	 */
	public boolean isOpen(int x, int y)
	{
		return noClip || map[y][x] == tiles.floor || map[y][x] == tiles.door && modifiers[y][x] != mods.broken 
				|| map[y][x] == tiles.pod && allTerminalsSolved();
	}

	private class Door
	{
		public int x, y, facing;

		public Door(int x, int y, int facing)
		{
			this.x = x;
			this.y = y;
			this.facing = facing;
		}
	}
}
