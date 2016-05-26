package com.fwumdesoft.project8;

import java.awt.Point;
import java.util.ArrayList;
import java.util.List;

//TODO: Document and clean up, because it's a bit of a ported mess
public class Overworld {
	int[][] map, modifiers;
	Point playerPos;
	
	public Overworld(int size) {
		// contains permanent tiles
		map = new int[size][size];
		// contains temporary modifiers or stuff that goes on walls
		modifiers = new int[size][size];
		
		// Generates a station by generating rooms with doors and connecting
		// rooms to said doors
		ArrayList<Door> doors = new ArrayList<>();
		Door door = new Door(size / 2, size / 2, 0);
		generateRoom(door, doors);
		playerPos = new Point(size / 2 - 1, size / 2);
		for(int i = 0; i < 25; i++) {
			if(!doors.isEmpty()) {
				// randomly pick a door to connect to
				door = doors.remove((int)(Math.random() * doors.size()));
				generateRoom(door, doors);
			}
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
		boolean spotFree = map[playerPos.y + yAmt][playerPos.x + xAmt] != 1;
		if(spotFree) {
			playerPos.x += xAmt;
			playerPos.y += yAmt;
		}
		return spotFree;
	}
	
	/*
	 * map Space - 0 Wall - 1 Door - 2 Floor - 3 modifiers None - 0 Door closed
	 * - 1 Door broken - 2 Fire suppression - 3 Destroyed wall - 4 Component Bag
	 * - 5 Fire - 6 Vacuum - 7
	 */
	private void generateRoom(Door door, List<Door> doors) {
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
		// If doors is 0 in size, we are generating the first room
		if(Math.random() < 0.25 && doors.size() != 0) {
			map[door.y][door.x] = 3;
			switch (door.facing) {
				case 0:
					map[door.y + 1][door.x] = 3;
					map[door.y - 1][door.x] = 3;
					break;
				case 1:
					map[door.y][door.x + 1] = 3;
					map[door.y][door.x - 1] = 3;
					break;
				case 2:
					map[door.y + 1][door.x] = 3;
					map[door.y - 1][door.x] = 3;
					break;
				case 3:
					y -= 4;
					map[door.y][door.x + 1] = 3;
					map[door.y][door.x - 1] = 3;
					break;
			}
		}
		else {
			map[door.y][door.x] = 2;
			modifiers[door.x][door.y] = (Math.random() < 0.5) ? 1 : 2;
		}
		// randomly generates 2 more doors (can overlap)
		for(int i = 0; i < 2; i++) {
			int position = (int)(Math.random() * 4);
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
			System.out.println((x + xOffset) + "\t" + (y + yOffset));
			map[y + yOffset][x + xOffset] = 2;
			modifiers[door.x][door.y] = (Math.random() < 0.5) ? 1 : 2;
			doors.add(new Door(x + xOffset, y + yOffset, position));
		}
		// Fills in walls and floor
		for(int i = x; i < x + 5; i++) {
			for(int j = y; j < y + 5; j++) {
				if(map[j][i] == 0) {
					// Wall
					if(i == x || i == x + 4 || j == y || j == y + 4) {
						if(map[j][i] != 2) {
							map[j][i] = 1;
						}
						// Floor
					}
					else {
						map[j][i] = 3;
					}
				}
			}
		}
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
