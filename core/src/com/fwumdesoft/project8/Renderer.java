package com.fwumdesoft.project8;

import java.awt.Point;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
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
	 * All of the individual wire tileset images
	 * [right][top][left][bottom]
	 */
	private TextureRegion[][][][] wireTiles;
	private TextureRegion unconnectedWire;
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
		//Initialize member variables
		this.batch = batch;
		this.font = font;
		this.shapes = new ShapeRenderer();
		this.cellSize = cellSize;
		this.componentSize = componentSize;
		this.screenWidth = screenWidth;
		this.screenHeight = screenHeight;
		//Retrieve image assets
		this.player = assets.get("player.png", Texture.class);
		this.wall = assets.get("station_wall.png", Texture.class);
		this.floor = assets.get("station_floor.png", Texture.class);
		this.door = assets.get("station_door.png", Texture.class);
		this.resistor = assets.get("resistor.png", Texture.class);
		this.lamp = assets.get("lamp.png", Texture.class);
		this.battery = assets.get("battery.png", Texture.class);
		//Create wire tileset
		Texture wires = assets.get("wires.png", Texture.class);
		wireTiles = new TextureRegion[2][2][2][2];
		int size = 64;
		unconnectedWire = new TextureRegion(wires, 0, size * 2, size, size);
		wireTiles[1][0][1][0] = new TextureRegion(wires, 0, 0, size, size);
		wireTiles[0][1][0][1] = new TextureRegion(wires, 0, size, size, size);
		wireTiles[1][0][0][1] = new TextureRegion(wires, size, 0, size, size);
		wireTiles[1][0][1][1] = new TextureRegion(wires, size * 2, 0, size, size);
		wireTiles[0][0][1][1] = new TextureRegion(wires, size * 3, 0, size, size);
		wireTiles[1][1][0][1] = new TextureRegion(wires, size, size, size, size);
		wireTiles[1][1][1][1] = new TextureRegion(wires, size  * 2, size, size, size);
		wireTiles[0][1][1][1] = new TextureRegion(wires, size * 3, size, size, size);
		wireTiles[1][1][0][0] = new TextureRegion(wires, size, size * 2, size, size);
		wireTiles[1][1][1][0] = new TextureRegion(wires, size * 2, size * 2, size, size);
		wireTiles[0][1][1][0] = new TextureRegion(wires, size * 3, size * 2, size, size);
	}

	/**
	 * Draw the current state of the overworld
	 * @param world
	 */
	public void renderOverworld(Overworld world, Inventory inventory) {
		Point player = world.playerPos;
		//Establish the drawable region
		int halfGridWidth = (screenWidth / cellSize) / 2;
		int halfGridHeight = (screenHeight / cellSize) / 2;
		int xStart = Math.max(0, player.x - halfGridWidth);
		int xEnd = Math.min(world.map.length, player.x + halfGridWidth);
		int yStart = Math.max(0, player.y - halfGridHeight);
		int yEnd = Math.min(world.map[0].length, player.y + halfGridHeight + cellSize);
		batch.begin();
		for(int y = yStart; y < yEnd; y++) {
			for(int x = xStart; x < xEnd; x++) {
				//Find the position where the square will draw
				int drawX = (x - player.x + halfGridWidth) * cellSize;
				int drawY = (y - player.y + halfGridHeight) * cellSize;
				//Draw the correct texture
				switch(world.map[y][x]) {
				case wall:
					batch.draw(floor, drawX, drawY);
					break;
				case door:
					batch.draw(door, drawX, drawY);
					break;
				case floor:
					batch.draw(wall, drawX, drawY);
					break;
				default:
					break;
				}
			}
		}
		//Draw the player, centered on the screen
		//Because all drawing is centered on the player is guaranteed to be centered
		batch.draw(this.player, halfGridWidth * cellSize, halfGridHeight * cellSize);
		batch.end();
		renderInventory(inventory);
	}
	
	public void renderCircuit(CircuitComponent[][] circuit, Inventory inventory, int cursorX, int cursorY) {
		batch.begin();
		for(int y = 0; y < circuit.length; y++) {
			for(int x = 0; x < circuit[y].length; x++) {
				int drawX = x * componentSize;
				int drawY = y * componentSize + 32; //Draw the circuit over the inventory
				CircuitComponent comp = circuit[y][x];
				if(comp == null) {
					continue;
				}
				if(comp.type == Type.WIRE) {
					boolean top, left, right, bottom;
					bottom = y > 0 && circuit[y - 1][x] != null;
					left = x > 0 && circuit[y][x - 1] != null;
					right = x < circuit[y].length - 1 && circuit[y][x + 1] != null;
					top = y < circuit.length - 1 && circuit[y + 1][x] != null;
					TextureRegion region = wireTiles[right ? 1 : 0][top ? 1 : 0][left ? 1 : 0][bottom ? 1 : 0];
					region = region != null ? region : unconnectedWire;
					batch.draw(region, drawX, drawY);
				} else {
					if(comp.type == Type.RESISTOR) {
						batch.draw(comp.isLamp ? lamp : resistor, drawX, drawY);
					} else {
						batch.draw(battery, drawX, drawY);
					}
				}
			}
		}
		batch.end();
		renderInventory(inventory);
	}

	private void renderInventory(Inventory inventory) {
		//Draw a background for the overlay
		shapes.begin(ShapeRenderer.ShapeType.Filled);	
		shapes.setColor(0.5f, 0.5f, 0.5f, 0.75f);
		shapes.rect(0, 0, 192, 32);
		shapes.end();
		batch.begin();
		//Draw each icon followed by the quantity
		batch.draw(resistor, 0, 0, 32, 32);
		font.draw(batch, "" + inventory.resistors.size(), 32, 24, 32, Align.center, false);
		batch.draw(lamp, 64, 0, 32, 32);
		font.draw(batch, "" + inventory.chips.size(), 96, 24, 32, Align.center, false);
		batch.draw(battery, 128, 0, 32, 32);
		font.draw(batch, "" + inventory.batteries.size(), 160, 24, 32, Align.center, false);
		batch.end();
	}
}
