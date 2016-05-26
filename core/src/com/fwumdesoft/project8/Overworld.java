package com.fwumdesoft.project8;

import java.awt.Point;
import java.util.ArrayList;
import java.util.List;

//TODO: Document and clean up, because it's a bit of a ported mess
public class Overworld {
	public static enum tiles {space, wall, door, floor};
	public static enum mods {none, doorClosed, doorBroken, fireSuppression, destroyedWall, componentBag, fire, vacuum}
	tiles[][] map; 
	mods[][] modifiers;
	Point playerPos;
	
	public Overworld(int size) {
		// contains permanent tiles
		map = new tiles[size][size];
		// contains temporary modifiers or stuff that goes on walls
		modifiers = new mods[size][size];
		for(int i = 0; i < size; i++)
		{
			for(int j = 0; j < size; j++)
			{
				map[i][j] = tiles.space;
				modifiers[i][j] = mods.none;
			}
		}
		
		// Generates a station by generating rooms with doors and connecting
		// rooms to said doors
		ArrayList<Door> doors = new ArrayList<>();
		Door door = new Door(size / 2, size / 2, 0);
		generateRoom(door, doors);
		playerPos = new Point(size / 2 - 1, size / 2);
		for(int i = 0; i < 25; i++) {
			door = generateRoom(door, doors);
		}
	}
	
	public String toString() {
		String str = "[";
		for(int i = 0; i < map.length; i++) {
			str += "[";
			for(int j = 0; j < map[i].length; j++) {
				str += map[i][j] + ",";
			}
			str += "],";
		}
		str += "]";
		return str;
	}
	
	public void isOpen(int x, int y) {
	
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
	public boolean movePlayer(int xAmt, int yAmt) {
		boolean spotFree = map[playerPos.y + yAmt][playerPos.x + xAmt] != tiles.wall;
		if(spotFree) {
			playerPos.x += xAmt;
			playerPos.y += yAmt;
		}
		return spotFree;
	}
	
	private Door generateRoom(Door door, List<Door> doors) {
		// The room faces the opposite direction of the door it is connected to
		// 0 - left, 1 - top, 2 - right, 3 - bottom
		door.facing = (door.facing + 2) % 4;
		// Must be odd for door to be in middle of a wall
		int x = door.x, y = door.y;
		// Uses facing direction to determine top left coordinate of room
		switch (door.facing) {
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
		if(Math.random() < 0.25) {
			map[door.y][door.x] = tiles.floor;
			switch (door.facing) {
				case 0:
					map[door.y + 1][door.x] = tiles.floor;
					map[door.y - 1][door.x] = tiles.floor;
					break;
				case 1:
					map[door.y][door.x + 1] = tiles.floor;
					map[door.y][door.x - 1] = tiles.floor;
					break;
				case 2:
					map[door.y + 1][door.x] = tiles.floor;
					map[door.y - 1][door.x] = tiles.floor;
					break;
				case 3:
					y -= 4;
					map[door.y][door.x + 1] = tiles.floor;
					map[door.y][door.x - 1] = tiles.floor;
					break;
			}
		}
		else {
			map[door.y][door.x] = tiles.door;
			modifiers[door.x][door.y] = (Math.random() < 0.5) ? mods.none : mods.doorBroken;
		}
		// randomly generates 1 more door (can overlap, but not with first door)
		// for(int i = 0; i < 2; i++) {
		int position = (int)(Math.random() * 4);
		while(position == door.facing)
			position = (int)(Math.random() * 4);
		int xOffset = 0;
		int yOffset = 0;
		switch (position) {
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
		modifiers[door.x][door.y] = (Math.random() < 0.5) ? mods.none : mods.doorBroken;
		Door nextDoor = new Door(x + xOffset, y + yOffset, position);
		// }
		// Fills in walls and floor
		for(int i = x; i < x + 5; i++) {
			for(int j = y; j < y + 5; j++) {
				if(map[j][i] == tiles.space) {
					// Wall
					if(i == x || i == x + 4 || j == y || j == y + 4) {
						if(map[j][i] != tiles.door) {
							map[j][i] = tiles.wall;
						}
						// Floor
					}
					else {
						map[j][i] = tiles.floor;
					}
				}
			}
		}
		return nextDoor;
	}
	
	private class Door {
		public int x, y, facing;
		public boolean open, locked;
		
		public Door(int x, int y, int facing) {
			this.x = x;
			this.y = y;
			this.facing = facing;
			this.open = false;
		}
		
		public int getFacingX() {
			switch (facing) {
				case 0:
					return -1;
				case 2:
					return 1;
				default:
					return 0;
			}
		}
		
		public int getFacingY() {
			switch (facing) {
				case 1:
					return 1;
				case 3:
					return -1;
				default:
					return 0;
			}
		}
	}
}
