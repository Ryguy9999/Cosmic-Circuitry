package com.fwumdesoft.project8;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.assets.AssetManager;

public class Renderer {
	private SpriteBatch batch;
	private int cellSize, screenWidth, screenHeight;
	private Texture player, wall, floor, door;

	public Renderer(SpriteBatch batch, AssetManager assets, int cellSize, int screenWidth, int screenHeight) {
		this.batch = batch;
		this.cellSize = cellSize;
		this.screenWidth = screenWidth;
		this.screenHeight = screenHeight;
		this.player = assets.get("player.png", Texture.class);
		this.wall = assets.get("station_wall.png", Texture.class);
		this.floor = assets.get("station_floor.png", Texture.class);
		this.door = assets.get("door.png", Texture.class);
	}

	public void renderOverworld(Overworld world) {
		GridPoint player = world.playerPos;
		int halfGridWidth = (screenWidth / cellSize) / 2;
		int halfGridHeight = (screenHeight / cellSize) / 2;
		int xStart = Math.max(0, player.x - halfGridWidth);
		int xEnd = Math.min(world.map.length, player.x + halfGridWidth);
		int yStart = Math.max(0, player.y - halfGridHeight);
		int yEnd = Math.min(world.map[0].length, player.y + halfGridHeight);
		batch.begin();
		for(int y = yStart; y < yEnd; y++) {
			for(int x = xEnd; x < xEnd; x++) {
				int drawX = (x - player.x + halfGridWidth) * cellSize;
				int drawY = (y - player.y + halfGridHeight) * cellSize;
				switch(world.map[y][x]) {
				case 1:
					batch.draw(floor, drawX, drawY);
					break;
				case 2:
					batch.draw(door, drawX, drawY);
					break;
				case 3:
					batch.draw(wall, drawX, drawY);
					break;
				}
			}
		}
		batch.draw(player, halfGridWidth * cellSize, halfGridHeight * cellSize);
		batch.end();
	}
}
