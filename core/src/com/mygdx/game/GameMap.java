package com.mygdx.game;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.mygdx.game.screens.GameScreen;

public class GameMap {
    public static final int WORLD_CELLS_SIZE = 9;
    private static final int SYMB_CELL_WALL = 9;
    private static final int SYMB_FOOD = 5;
    private static final int SYMB_XFOOD = 6;

    private byte[][] mapData;
    private TextureRegion textureGround;
    private TextureRegion textureWall;
    private TextureRegion textureFood;
    private TextureRegion textureXFood;
    private int foodCount;

    public int getFoodCount() {
        return foodCount;
    }

    public GameMap(TextureAtlas atlas) {
        mapData = new byte[WORLD_CELLS_SIZE][WORLD_CELLS_SIZE];
        textureGround = atlas.findRegion("ground");
        textureWall = atlas.findRegion("wall");
        textureFood = atlas.findRegion("food");
        textureXFood = atlas.findRegion("xfood");
    }

    public void init() {
        foodCount = 0;

        for (int i = 0; i < WORLD_CELLS_SIZE; i++) {
            mapData[i][0] = SYMB_CELL_WALL;
            mapData[0][i] = SYMB_CELL_WALL;
            mapData[i][WORLD_CELLS_SIZE - 1] = SYMB_CELL_WALL;
            mapData[WORLD_CELLS_SIZE - 1][i] = SYMB_CELL_WALL;
        }
        for (int i = 0; i < WORLD_CELLS_SIZE; i++) {
            for (int j = 0; j < WORLD_CELLS_SIZE; j++) {
                if (i % 2 == 0 && j % 2 == 0) {
                    mapData[i][j] = SYMB_CELL_WALL;
                }
                if (mapData[i][j] != SYMB_CELL_WALL) {
                    mapData[i][j] = SYMB_FOOD;
                    foodCount++;
                }
            }
        }

        mapData[1][1] = 0;
        foodCount--;
        mapData[1][5] = 6;
        mapData[5][1] = 6;
    }

    public void render(SpriteBatch batch) {
        for (int i = 0; i < WORLD_CELLS_SIZE; i++) {
            for (int j = 0; j < WORLD_CELLS_SIZE; j++) {
                batch.draw(textureGround, i * GameScreen.WORLD_CELL_PX, j * GameScreen.WORLD_CELL_PX);
                if (mapData[i][j] == SYMB_CELL_WALL) {
                    batch.draw(textureWall, i * GameScreen.WORLD_CELL_PX, j * GameScreen.WORLD_CELL_PX);
                }
                if (mapData[i][j] == SYMB_FOOD) {
                    batch.draw(textureFood, i * GameScreen.WORLD_CELL_PX, j * GameScreen.WORLD_CELL_PX);
                }
                if (mapData[i][j] == SYMB_XFOOD) {
                    batch.draw(textureXFood, i * GameScreen.WORLD_CELL_PX, j * GameScreen.WORLD_CELL_PX);
                }
            }
        }
    }

    public boolean isCellEmpty(int cellX, int cellY) {
        return mapData[cellX][cellY] != SYMB_CELL_WALL;
    }

    public boolean checkFoodEating(int x, int y) {
        boolean xfood = mapData[x][y] == SYMB_XFOOD;
        if (mapData[x][y] == SYMB_FOOD || xfood) {
            mapData[x][y] = 0;
            foodCount--;
        }

        return xfood;
    }
}
