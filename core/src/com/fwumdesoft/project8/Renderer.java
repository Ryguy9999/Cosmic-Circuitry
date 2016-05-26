package com.fwumdesoft.project8;

import java.awt.Point;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.badlogic.gdx.utils.Align;
import com.fwumdesoft.project8.CircuitComponent.Type;

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
	 * The number of pixels of the side of a circuit component
	 */
	private int componentSize;
	/**
	 * The number of pixels of the screen's width
	 */
	private int screenWidth;
	/**
	 * The number of pixels of the screen's height
	 */
	private int screenHeight;
	private Texture player, wall, floor, door, resistor, lamp, battery;

	/**
	 * Create a Renderer
	 * @param batch A SpriteBatch which should be disposed of when the Renderer is unnecessary
	 * @param font The font used to render the UI
	 * @param assets An AssetManager with the game assets loaded
	 * @param cellSize The size of an overworld square
	 * @param componentSize The size of a circuit component
	 * @param screenWidth The width of the screen
	 * @param screenHeight The height of the screen
	 */
	public Renderer(SpriteBatch batch, BitmapFont font, AssetManager assets, int cellSize, int componentSize, int screenWidth, int screenHeight) {
		this.batch = batch;
		this.font = font;
		this.shapes = new ShapeRenderer();
		this.cellSize = cellSize;
		this.componentSize = componentSize;
		this.screenWidth = screenWidth;
		this.screenHeight = screenHeight;
		this.player = assets.get("player.png", Texture.class);
		this.wall = assets.get("station_wall.png", Texture.class);
		this.floor = assets.get("station_floor.png", Texture.class);
		this.door = assets.get("station_door.png", Texture.class);
		this.resistor = assets.get("resistor.png", Texture.class);
		this.lamp = assets.get("lamp.png", Texture.class);
		this.battery = assets.get("battery.png", Texture.class);
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
		int yEnd = Math.min(world.map[0].length, player.y + halfGridHeight + cellSize);
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
	
	public void renderCircuit(CircuitComponent[][] circuit, Inventory inventory) {
		//TODO: Clean up the render function
		batch.begin();
		Texture texture;
		for(int y = 0; y < circuit.length; y++) {
			for(int x = 0; x < circuit[y].length; x++) {
				int drawX = x * componentSize;
				int drawY = y * componentSize + 32; //Draw the circuit over the inventory
				CircuitComponent comp = circuit[y][x];
				if(comp == null || comp.type == Type.WIRE) {
					continue;
				}
				switch(comp.type) {
				case RESISTOR:
					texture = resistor;//comp.isLamp ? lamp : resistor;
					break;
				case BATTERY:
					texture = battery;
					break;
				default:
					texture = null;
					break;
				}
				batch.draw(texture, drawX, drawY);
			}
		}
		batch.end();
		shapes.begin(ShapeType.Filled);
		shapes.setColor(Color.BLACK);
		for(int y = 0; y < circuit.length; y++) {
			for(int x = 0; x < circuit[y].length; x++) {
				int drawX = x * componentSize;
				int drawY = y * componentSize + 32; //Draw the circuit over the inventory
				CircuitComponent comp = circuit[y][x];
				if(comp == null || comp.type != Type.WIRE) {
					continue;
				}
				int centerX = drawX + componentSize / 2;
				int centerY = drawY + componentSize / 2;
				GridHelper.doCardinal(circuit, x, y, (newX, newY) -> {
					if(circuit[newY][newX] != null) {
						shapes.rect(centerX - 1, centerY - 1, 2, 2);
					}
				});
			}
		}
		shapes.end();
		renderInventory(inventory);
	}

	private void renderInventory(Inventory inventory) {
		shapes.begin(ShapeRenderer.ShapeType.Filled);	
		shapes.setColor(0.5f, 0.5f, 0.5f, 0.75f);
		shapes.rect(0, 0, 128, 32);
		shapes.end();
		batch.begin();
		batch.draw(resistor, 0, 0, 32, 32);
		String resistors = "" + inventory.resistors;
		font.draw(batch, resistors, 32, 24, 32, Align.center, false);
		batch.draw(lamp, 64, 0, 32, 32);
		font.draw(batch, "" + inventory.computerChips, 96, 24, 32, Align.center, false);
		batch.end();
	}
}
