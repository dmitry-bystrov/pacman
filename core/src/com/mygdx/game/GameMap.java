package com.mygdx.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.math.Vector2;
import com.mygdx.game.screens.GameScreen;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

public class GameMap {

    public enum MapObject {
        PACMAN('s', true),
        RED_GHOST('r', true),
        GREEN_GHOST('g', true),
        BLUE_GHOST('b', true),
        PURPLE_GHOST('p', true),
        WALL('1', false),
        FOOD('_', false),
        XFOOD('*', false),
        EMPTY('0', false);

        private final char symbol;
        private final boolean isCreature;

        MapObject(char symbol, boolean isCreature) {
            this.symbol = symbol;
            this.isCreature = isCreature;
        }

        public static MapObject getObject(char symbol) {
            for (MapObject o:MapObject.values()) {
                if (o.getSymbol() == symbol) return o;
            }
            return MapObject.EMPTY;
        }

        public boolean isCreature() {
            return isCreature;
        }

        public char getSymbol() {
            return symbol;
        }
    }

    private MapObject[][] mapData;
    private TextureRegion textureGround;
    private TextureRegion textureWall;
    private TextureRegion textureFood;
    private TextureRegion textureXFood;
    private int mapWidht;
    private int mapHeight;
    private int foodCount;
    private HashMap<MapObject, Vector2> startPositions;

    public GameMap() {
        textureGround = Assets.getInstance().getAtlas().findRegion("ground");
        textureWall = Assets.getInstance().getAtlas().findRegion("wall");
        textureFood = Assets.getInstance().getAtlas().findRegion("food");
        textureXFood = Assets.getInstance().getAtlas().findRegion("xfood");
        initMap();
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

    public Vector2 getStartPosition(MapObject mapObject) {
        return startPositions.get(mapObject);
    }

    public void initMap() {
        loadMap("map.dat");
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
        mapHeight = list.size();
        mapData = new MapObject[mapWidht][mapHeight];
        for (int y = 0; y < list.size(); y++) {
            for (int x = 0; x < list.get(y).length(); x++) {
                mapData[x][y] = MapObject.getObject(list.get(y).charAt(x));
                if (mapData[x][y] == MapObject.FOOD || mapData[x][y] == MapObject.XFOOD) {
                    foodCount++;
                }
                if (mapData[x][y].isCreature()) {
                    startPositions.put(mapData[x][y], new Vector2(x, y));
                    mapData[x][y] = MapObject.EMPTY;
                }
            }
        }
    }

    public void render(SpriteBatch batch) {
        for (int i = 0; i < mapWidht; i++) {
            for (int j = 0; j < mapHeight; j++) {
                batch.draw(textureGround, i * GameScreen.WORLD_CELL_PX, j * GameScreen.WORLD_CELL_PX);
                switch (mapData[i][j]) {
                    case WALL:
                        batch.draw(textureWall, i * GameScreen.WORLD_CELL_PX, j * GameScreen.WORLD_CELL_PX);
                        break;
                    case FOOD:
                        batch.draw(textureFood, i * GameScreen.WORLD_CELL_PX, j * GameScreen.WORLD_CELL_PX);
                        break;
                    case XFOOD:
                        batch.draw(textureXFood, i * GameScreen.WORLD_CELL_PX, j * GameScreen.WORLD_CELL_PX);
                        break;
                }
            }
        }
    }

    public boolean isCellEmpty(int cellX, int cellY) {
        return mapData[cellX][cellY] != MapObject.WALL;
    }

    public MapObject checkFood(int x, int y) {
        MapObject mapObject = mapData[x][y];
        if (mapObject == MapObject.FOOD || mapObject == MapObject.XFOOD) {
            mapData[x][y] = MapObject.EMPTY;
            foodCount--;
        }
        return mapObject;
    }
}
