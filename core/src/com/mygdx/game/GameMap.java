package com.mygdx.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.math.Vector2;
import com.mygdx.game.screens.GameScreen;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

public class GameMap implements GameConstants {

    public static final String MAP_FILE_NAME = "map2.dat";
    private GameObject[][] mapData;
    private HashMap<GameObject, TextureRegion> textures;
    private HashMap<GameObject, Vector2> startPositions;

    private int mapWidht;
    private int mapHeight;
    private int foodCount;

    public GameMap() {
        textures = new HashMap<>();
        putTexture(GameObject.EMPTY_CELL);
        putTexture(GameObject.WALL);
        putTexture(GameObject.FOOD);
        putTexture(GameObject.XFOOD);
        initMap();
    }

    private void putTexture(GameObject gameObject) {
        textures.put(gameObject, Assets.getInstance().getAtlas().findRegion(gameObject.getTextureName()));
    }

    private TextureRegion getTexture(GameObject gameObject) {
        return textures.get(gameObject);
    }

    public int getFoodCount() {
        return foodCount;
    }

    public int getMapWidht() {
        return mapWidht;
    }

    public int getMapHeight() {
        return mapHeight;
    }

    public Vector2 getStartPosition(GameObject gameObject) {
        return startPositions.get(gameObject);
    }

    public void initMap() {
        loadMap(MAP_FILE_NAME);
    }

    private void loadMap(String name) {
        startPositions = new HashMap<>();
        ArrayList<String> list = new ArrayList<>();
        BufferedReader br;
        try {
            br = Gdx.files.internal(name).reader(8192);
            String str;
            while ((str = br.readLine()) != null) {
                list.add(str);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        mapWidht = list.get(0).length();
        foodCount = 0;
        mapHeight = list.size();
        mapData = new GameObject[mapWidht][mapHeight];
        for (int y = 0; y < list.size(); y++) {
            for (int x = 0; x < list.get(y).length(); x++) {
                mapData[x][y] = GameObject.getObject(list.get(y).charAt(x));
                if (mapData[x][y].isFood()) {
                    foodCount++;
                }
                if (mapData[x][y].isCreature()) {
                    startPositions.put(mapData[x][y], new Vector2(x, y));
                    mapData[x][y] = GameObject.EMPTY_CELL;
                }
            }
        }
    }

    public void render(SpriteBatch batch) {
        for (int i = 0; i < mapWidht; i++) {
            for (int j = 0; j < mapHeight; j++) {
                batch.draw(getTexture(GameObject.EMPTY_CELL), i * WORLD_CELL_PX, j * WORLD_CELL_PX);
                if (mapData[i][j] != GameObject.EMPTY_CELL) {
                    batch.draw(getTexture(mapData[i][j]), i * WORLD_CELL_PX, j * WORLD_CELL_PX);
                }
            }
        }
    }

    public boolean isCellEmpty(int cellX, int cellY) {
        return mapData[cellX][cellY] != GameObject.WALL;
    }

    public GameObject checkFood(int x, int y) {
        GameObject gameObject = mapData[x][y];
        if (gameObject.isFood()) {
            mapData[x][y] = GameObject.EMPTY_CELL;
            foodCount--;
            System.out.println(foodCount);
        }
        return gameObject;
    }
}
