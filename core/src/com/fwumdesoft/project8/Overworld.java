package com.fwumdesoft.project8;

import java.util.List;
import java.util.ArrayList;

public class Overworld {
	int[][] map;
	GridPoint playerPos;

	public Overworld(int size) {
		map = new int[size][size];
		ArrayList<Door> doors = new ArrayList<>();
		Door door = new Door(size / 2, size / 2, 0);
		generateRoom(door, 4, 4, 6, 6, doors);
		playerPos = new GridPoint(size / 2, size / 2);
		for(int i = 0; i < 25; i++) {
			if(!doors.isEmpty()) {
				door = doors.remove(0);
				generateRoom(door, 4, 4, 6, 6, doors);
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

	private void generateRoom(Door door, int minWidth, int minHeight, int maxWidth, int maxHeight, List<Door> doors) {
		door.facing = (door.facing + 2) % 4;
		int width = (int)(Math.random() * minWidth) + (maxWidth - minWidth);
		int height = (int)(Math.random() * minHeight) + (maxHeight - minHeight);
		int x = door.x, y = door.y;
		x += door.getFacingX();
		y += door.getFacingY();
		map[door.x][door.y] = 2;
		for(int i = 0; i < 3; i++) {
			int position = (int)(Math.random() * 4);
			int xOffset = 0;
			int yOffset = 0;
			int doorX = x, doorY = y;
			switch(door.facing) {
			case 0:
				yOffset = height / 2;
				break;
			case 1:
				xOffset= width / 2;
				break;
			case 2:
				yOffset = height / 2;
				xOffset = width - 1;
				break;
			case 3:
				yOffset = height - 1;
				xOffset = width / 2;
				break;
			}
			map[y + yOffset][x + xOffset] = 2;
			doors.add(new Door(x + xOffset, y + yOffset, door.facing));
		}
		for(int i = x; i < x + width; i++) {
			for(int j = y; j < y + height; j++) {
				if(i == x || i == x + width - 1 || j == y || j == y + height - 1) {
					if(map[j][i] != 2) {
						map[j][i] = 1;
					}
				} else {
					map[j][i] = 3;
				}
			}
		}
	}
	
	private class Door {
		public int x, y, facing;

		public Door(int x, int y, int facing) {
			this.x = x;
			this.y = y;
			this.facing = facing;
		}

		public int getFacingX() {
			switch(facing) {
			case 0:
				return -1;
			case 2:
				return 1;
			default:
				return 0;
			}
		}

		public int getFacingY() {
			switch(facing) {
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


