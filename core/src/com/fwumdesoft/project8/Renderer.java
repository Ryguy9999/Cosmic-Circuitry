package com.fwumdesoft.project8;

import java.awt.Point;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;

/**
 * Draws the game to separate drawing from simulation
 */
public class Renderer {
	private SpriteBatch batch;
	private ShapeRenderer shapes;
	private BitmapFont font;
	/**
	 * The number of pixels of the side of an overworld square
	 */
	private int cellSize;
	/**
	 * The number of pixels of the screen's width
	 */
	private int screenWidth;
	/**
	 * The number of pixels of the screen's height
	 */
	private int screenHeight;
	private Texture player, wall, floor, door, resistor;

	/**
	 * Create a Renderer
	 * @param batch A SpriteBatch which should be disposed of when the Renderer is unnecessary
	 * @param font The font used to render the UI
	 * @param assets An AssetManager with the game assets loaded
	 * @param cellSize The size of an overworld square
	 * @param screenWidth The width of the screen
	 * @param screenHeight The height of the screen
	 */
	public Renderer(SpriteBatch batch, BitmapFont font, AssetManager assets, int cellSize, int screenWidth, int screenHeight) {
		this.batch = batch;
		this.font = font;
		this.shapes = new ShapeRenderer();
		this.cellSize = cellSize;
		this.screenWidth = screenWidth;
		this.screenHeight = screenHeight;
		this.player = assets.get("player.png", Texture.class);
		this.wall = assets.get("station_wall.png", Texture.class);
		this.floor = assets.get("station_floor.png", Texture.class);
		this.door = assets.get("station_door.png", Texture.class);
		this.resistor = assets.get("resistor.png", Texture.class);
	}

	/**
	 * Draw the current state of the overworld
	 * @param world
	 */
	public void renderOverworld(Overworld world, Inventory inventory) {
		Point player = world.playerPos;
		int halfGridWidth = (screenWidth / cellSize) / 2;
		int halfGridHeight = (screenHeight / cellSize) / 2;
		int xStart = Math.max(0, player.x - halfGridWidth);
		int xEnd = Math.min(world.map.length, player.x + halfGridWidth);
		int yStart = Math.max(0, player.y - halfGridHeight);
		int yEnd = Math.min(world.map[0].length, player.y + halfGridHeight);
		batch.begin();
		for(int y = yStart; y < yEnd; y++) {
			for(int x = xStart; x < xEnd; x++) {
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
		batch.draw(this.player, halfGridWidth * cellSize, halfGridHeight * cellSize);
		batch.end();
		renderInventory(inventory);
	}

	private void renderInventory(Inventory inventory) {
		shapes.begin(ShapeRenderer.ShapeType.Filled);	
		shapes.setColor(0.5f, 0.5f, 0.5f, 0.75f);
		shapes.rect(0, 0, 128, 32);
		shapes.end();
		batch.begin();
		batch.draw(resistor, 0, 0, 32, 32);
		font.draw(batch, "" + inventory.resistors, 32, 0);
		//TODO: Draw computer chip
		font.draw(batch, "" + inventory.computerChips, 96, 0);
		batch.end();
	}
}
