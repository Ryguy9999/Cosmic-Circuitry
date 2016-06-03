package com.fwumdesoft.project8;

import java.awt.Point;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

import com.badlogic.gdx.utils.Array;

public class Overworld
{
	public static enum tiles
	{
		space, wall, door, floor
	}

	public static enum mods
	{
		none, doorClosed, doorBroken, fireSuppression, destroyedWall, componentPile, fire, vacuum
	}

	Project8 app;
	tiles[][] map;
	mods[][] modifiers;
	Point playerPos, playerFace;
	Array<Circuit> circuits;
	HashMap<Point, Circuit> worldCircuits;
	Circuit currentCircuit;
	Inventory inventory;
	final int FIRE_SUPPRESSION_RANGE = 10;
	final double FIRE_SUPPRESSION_EFFECTIVENESS = 0.25;
	private Overworld previous;
	
	public Overworld(Project8 app, int size, Array<Circuit> circuits, Inventory inventory)
	{
		previous = new Overworld(app, size, circuits, inventory);
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
		playerPos = new Point(size / 2 - 1, size / 2);
		boolean firstDoor = true;
		this.playerFace = new Point();
		this.app = app;
		worldCircuits = new HashMap<Point, Circuit>();
		currentCircuit = null;

		for (int i = 0; i < 100; i++)
		{
			door = generateRoom(door, firstDoor);
			firstDoor = false;
		}

		this.circuits = circuits;
		removeStrayDoors();
		distributeCircuits();
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
			playerPos.x += xAmt;
			playerPos.y += yAmt;
			turn();
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
		case doorBroken:
			modifiers[lookAt.y][lookAt.x] = mods.none;
			break;
		case fireSuppression:
			//TODO: Add broken, fixable fire suppression
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
			if(map[lookAt.y][lookAt.x] == tiles.door)
				modifiers[lookAt.y][lookAt.x] = mods.doorBroken;
			break;
		case fireSuppression:
			//TODO: Add broken, fixable fire suppression
			break;
		default:
			break;
		}
	}
	
	public void turn() {
		for(int y = 0; y < modifiers.length; y++)
		{
			for(int x = 0; x < modifiers[y].length; x++)
			{
				//spread fire
				if(modifiers[y][x] == mods.fire && Math.random() < 0.05)
				{
					int spreadX = (int)(Math.random() * 3) - 1;
					int spreadY = (int)(Math.random() * 3) - 1;
					if(map[y + spreadY][x + spreadX] == tiles.floor)
						modifiers[y + spreadY][x + spreadX] = mods.fire;
					else if(map[y + spreadY][x + spreadX] == tiles.door && Math.abs(spreadY)+Math.abs(spreadX) <= 1 &&
							Math.random() < 0.2)
						modifiers[y + spreadY*2][x + spreadX*2] = mods.fire;
				}
				
				//Fire suppression
				if(modifiers[y][x]== mods.fireSuppression)
					for(int c = 0; c < Math.pow(FIRE_SUPPRESSION_RANGE*2+1, 2) * FIRE_SUPPRESSION_EFFECTIVENESS; c++)
					{
						int j = (int)(Math.random() * FIRE_SUPPRESSION_RANGE * 2 + 1) + y - FIRE_SUPPRESSION_RANGE;
						int i = (int)(Math.random() * FIRE_SUPPRESSION_RANGE * 2 + 1) + x - FIRE_SUPPRESSION_RANGE;
						if(modifiers[j][i] == mods.fire)
							modifiers[j][i] = null;
					}
			}
		}
		
		//Death by fire
		if(modifiers[playerPos.y][playerPos.x]== mods.fire)
			app.restart();
		
		//Pick up bags
		if(modifiers[playerPos.y][playerPos.x] == mods.componentPile)
		{
			modifiers[playerPos.y][playerPos.x] = null;
			inventory.addComponent(CircuitComponent.randomComponent());
			while(Math.random() < 1.0/3.0)
				inventory.addComponent(CircuitComponent.randomComponent());

		}
	}
	
	@SuppressWarnings("unchecked")
	public Overworld getStateCopy()
	{
		previous.currentCircuit = currentCircuit;
		previous.map = deepCopy(map);
		previous.modifiers = deepCopy(modifiers);
		previous.playerFace = new Point(playerFace);
		previous.playerPos = new Point(playerPos);
		previous.worldCircuits = (HashMap<Point, Circuit>)worldCircuits.clone();
		return previous;
	}
	
	public boolean equals(Overworld ow)
	{
		return ow != null && Arrays.deepEquals(map, ow.map) && 
				Arrays.deepEquals(modifiers, previous.modifiers) && playerPos.equals(ow.playerPos) && 
				playerFace.equals(ow.playerFace) && worldCircuits.equals(previous.worldCircuits);
	}
	
	private void distributeCircuits()
	{
		List<Circuit> circuits = Arrays.asList(this.circuits.toArray());
		List<Circuit> doorCircuits = circuits.stream()
				.filter(circuit -> circuit.name.endsWith("_door"))
				.collect(Collectors.toList());
		for(int y = 0; y < modifiers.length; y++)
			for(int x = 0; x < modifiers[y].length; x++)
				switch(modifiers[y][x])
				{
				case doorBroken:
					worldCircuits.put(new Point(x, y), new Circuit(getRandom(doorCircuits)));
					break;
				case fireSuppression:
					//TODO: Fire suppression circuits
					break;
				case none:
					//TODO: Unbroken doors
					//^ Actually this is every other type of tile
					break;
				default:
					break;
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
		door.facing = (door.facing + 2) % 4;
		// Must be odd for door to be in middle of a wall
		int x = door.x, y = door.y;
		// Uses facing direction to determine top left coordinate of room
		switch (door.facing)
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
		// Sometimes wall will not generate to make larger connected rooms
		if (Math.random() < 0.25 && !firstDoor)
		{
			switch (door.facing)
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
		// randomly generates 1 more door (can overlap, but not with first door)
		int position = (int) (Math.random() * 4);
		while (position == door.facing)
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
		modifiers[door.y][door.x] = (Math.random() < 0.2) ? mods.doorBroken : mods.none;
		// Fills in walls and floor
		for (int i = x; i < x + 5; i++)
		{
			for (int j = y; j < y + 5; j++)
			{
				if (i == x || i == x + 4 || j == y || j == y + 4)
				{
					if (map[j][i] != tiles.door && map[j][i] != tiles.floor)
					{
						map[j][i] = tiles.wall;
					}
				}
				// Floor
				else
				{
					map[j][i] = tiles.floor;
					if(Math.random() < 0.005)
						modifiers[j][i] = mods.fire;
					else if(Math.random() < 0.1)
						modifiers[j][i] = mods.componentPile;
					else if(Math.random() < 0.007)
						modifiers[j][i] = mods.fireSuppression;
					else
						modifiers[j][i] = mods.none;
				}
			}
		}
		return nextDoor;
	}
	
	@SuppressWarnings("unchecked")
	private <T> T[][] deepCopy(T[][] original)
	{
		T[][] copy = (T[][])new Object[original.length][];
		for(int i = 0; i < original.length; i++)
		{
			copy[i] = (T[])new Object[original[i].length];
			for(int j = 0; j < original[i].length; j++)
				copy[i][j] = original[i][j];
		}
		return copy;
	}

	/**
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
					boolean connectingWall = false;
					for (int x = -1; x < 2; x++)
					{
						for (int y = -1; y < 2; y++)
						{
							if (map[j + y][i + x] == tiles.wall)
								connectingWall = true;
						}
					}
					if (!connectingWall)
						map[j][i] = tiles.floor;
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
		return (map[y][x] == tiles.floor || (map[y][x] == tiles.door && modifiers[y][x] != mods.doorBroken)) &&
				modifiers[y][x] != mods.fire;
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
