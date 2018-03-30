package com.mygdx.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

public class GameMap implements GameConstants {

    public static final String MAP_FILE_NAME = "original_map.dat";
    private GameObject[][] mapData;
    private HashMap<GameObject, TextureRegion> mapObjectsTextures;
    private HashMap<Integer, TextureRegion> pipesTextures;
    private HashMap<GameObject, Vector2> startPositions;

    private int mapWidht;
    private int mapHeight;
    private int foodCount;
    private final GameObject fruits[] = {GameObject.APPLE, GameObject.ORANGE, GameObject.BANANA};
    private final int pipeIndex[] = {1, 10, 11, 100, 101, 110, 111, 1000, 1001, 1010, 1011, 1100, 1101, 1110, 1111};

    public GameMap() {
        this.mapObjectsTextures = new HashMap<>();
        this.pipesTextures = new HashMap<>();
        putTexture(GameObject.EMPTY_CELL);
        putTexture(GameObject.FOOD);
        putTexture(GameObject.XFOOD);
        putTexture(GameObject.PIPE);
        putTexture(GameObject.ORANGE);
        putTexture(GameObject.APPLE);
        putTexture(GameObject.BANANA);
        initMap();
    }

    private void putTexture(GameObject gameObject) {
        if (gameObject != GameObject.PIPE) {
            mapObjectsTextures.put(gameObject, Assets.getInstance().getAtlas().findRegion(gameObject.getTextureName()));
        } else {
            for (int i = 0; i < pipeIndex.length; i++) {
                pipesTextures.put(pipeIndex[i], Assets.getInstance().getAtlas().findRegion(gameObject.getTextureName(), pipeIndex[i]));
            }
        }
    }

    private TextureRegion getTexture(GameObject gameObject) {
        return mapObjectsTextures.get(gameObject);
    }

    private TextureRegion getPipeTexture(int pipeNumber) {
        return pipesTextures.get(pipeNumber);
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
                if (mapData[x][y] == GameObject.FOOD) {
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
        int cellX;
        int cellY;
        int pipeNumber;

        for (int i = 0; i < mapWidht; i++) {
            for (int j = 0; j < mapHeight; j++) {
                batch.draw(getTexture(GameObject.EMPTY_CELL), i * WORLD_CELL_PX, j * WORLD_CELL_PX);
                if (mapData[i][j] != GameObject.EMPTY_CELL) {
                    if (mapData[i][j] != GameObject.PIPE){
                        batch.draw(getTexture(mapData[i][j]), i * WORLD_CELL_PX, j * WORLD_CELL_PX);
                    } else {
                        pipeNumber = 0;
                        for (Direction direction:Direction.values()) {
                            cellX = i + direction.getX();
                            cellY = j + direction.getY();
                            if (cellX < 0 || cellY < 0 || cellX >= mapData.length || cellY >= mapData[cellX].length) continue;

                            if (mapData[cellX][cellY] == GameObject.PIPE) {
                                pipeNumber += direction.getCost();
                            }
                        }
                        if (pipeNumber == 0) pipeNumber = 1111;
                        batch.draw(getPipeTexture(pipeNumber), i * WORLD_CELL_PX, j * WORLD_CELL_PX);
                    }
                }
            }
        }
    }

    public boolean isCellEmpty(int cellX, int cellY) {
        return mapData[cellX][cellY] != GameObject.PIPE;
    }

    public void addRandomFruit() {
        int randomX = 0;
        int randomY = 0;

        do {
            randomX = MathUtils.random(mapData.length - 1);
            randomY = MathUtils.random(mapData[randomX].length - 1);
        } while (mapData[randomX][randomY] != GameObject.EMPTY_CELL);

        mapData[randomX][randomY] = fruits[MathUtils.random(fruits.length - 1)];
    }

    public GameObject checkFood(int x, int y) {
        GameObject gameObject = mapData[x][y];
        if (gameObject.isFood()) {
            mapData[x][y] = GameObject.EMPTY_CELL;
            if (gameObject == GameObject.FOOD) foodCount--;
        }
        return gameObject;
    }
}
