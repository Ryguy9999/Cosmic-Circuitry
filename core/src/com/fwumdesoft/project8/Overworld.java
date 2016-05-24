package com.fwumdesoft.project8;

import java.awt.Point;
import java.util.ArrayList;
import java.util.List;
//TODO: Document and clean up, because it's a bit of a ported mess
public class Overworld {
	int[][] map;
	Point playerPos;

	public Overworld(int size) {
		map = new int[size][size];
		ArrayList<Door> doors = new ArrayList<>();
		Door door = new Door(size / 2, size / 2, 0);
		generateRoom(door, 4, 4, 6, 6, doors);
		playerPos = new Point(size / 2, size / 2);
		for(int i = 0; i < 5; i++) {
			if(!doors.isEmpty()) {
				door = doors.remove((int)(Math.random() * doors.size()));
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
		//Must be odd for door to be in middle
		int width = (int)((minWidth + (Math.random() * (maxWidth - minWidth))) / 2) * 2 + 1;
		int height = (int)((minHeight + (Math.random() * (maxHeight - minHeight))) / 2) * 2 + 1;
		int x = door.x, y = door.y;
		switch(door.facing) {
			case 0:
				y -= (height / 2);
				break;
			case 1:
				x -= (width / 2);
				break;
			case 2:
				y -= (height / 2);
				x -= width - 1;
				break;
			case 3:
				y -= height - 1;
				x -= (width / 2);
				break;
		}
		map[door.x][door.y] = 2;
		for(int i = 0; i < 2; i++) {
			int position = (int)(Math.random() * 4);
			int xOffset = 0;
			int yOffset = 0;
			switch(position) {
			case 0:
				yOffset =  (height / 2);
				break;
			case 1:
				xOffset= (width / 2);
				break;
			case 2:
				yOffset = (height / 2);
				xOffset = width - 1;
				break;
			case 3:
				yOffset = height - 1;
				xOffset = (width / 2);
				break;
			}
			map[y + yOffset][x + xOffset] = 2;
			doors.add(new Door(x + xOffset, y + yOffset, position));
		}
		for(int i = x; i < x + width; i++) {
			for(int j = y; j < y + height; j++) {
				if(map[j][i] == 0) {
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


