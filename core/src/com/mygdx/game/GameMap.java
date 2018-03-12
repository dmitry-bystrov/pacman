package com.mygdx.game;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

public class GameMap {
    public static final int WORLD_CELLS_SIZE = 17;
    public static final int CELL_SIZE_PX = 80;
    public static final int SYMB_CELL_WALL = 9;
    public static final int SYMB_FOOD = 5;

    private byte[][] mapData;
    private Texture textureGround;
    private Texture textureWall;
    private Texture textureFood;
    private int foodCount;

    public int getFoodCount() {
        return foodCount;
    }

    public GameMap() {
        mapData = new byte[WORLD_CELLS_SIZE][WORLD_CELLS_SIZE];
        textureGround = new Texture("ground.png");
        textureWall = new Texture("wall.png");
        textureFood = new Texture("food.png");
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
        mapData[1][1] = -1;
    }

    public void render(SpriteBatch batch) {
        for (int i = 0; i < WORLD_CELLS_SIZE; i++) {
            for (int j = 0; j < WORLD_CELLS_SIZE; j++) {
                batch.draw(textureGround, i * CELL_SIZE_PX, j * CELL_SIZE_PX);
                if (mapData[i][j] == SYMB_CELL_WALL) {
                    batch.draw(textureWall, i * CELL_SIZE_PX, j * CELL_SIZE_PX);
                }
                if (mapData[i][j] == SYMB_FOOD) {
                    batch.draw(textureFood, i * CELL_SIZE_PX, j * CELL_SIZE_PX);
                }
            }
        }
    }

    public boolean isCellEmpty(int cellX, int cellY) {
        return mapData[cellX][cellY] != SYMB_CELL_WALL;
    }

    public boolean checkFoodEating(float x, float y) {
        if (mapData[(int) x][(int) y] == SYMB_FOOD) {
            mapData[(int) x][(int) y] = 0;
            foodCount--;
            return true;
        }
        return false;
    }
}
