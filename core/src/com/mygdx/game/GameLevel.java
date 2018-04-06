package com.mygdx.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.mygdx.game.creatures.Ghost;
import com.mygdx.game.creatures.Pacman;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;

public class GameLevel implements GameConstants, Serializable {

    private static final String MAP_FILE_NAME = "original_map.dat";
    private GameObject[][] mapData;
    private GameObject[][] fruitsMap;
    private transient HashMap<GameObject, TextureRegion> mapObjectsTextures;
    private transient HashMap<Integer, TextureRegion> pipesTextures;
    private HashMap<GameObject, Vector2> startPositions;

    private int mapWidht;
    private int mapHeight;
    private int foodCount;
    private final GameObject fruits[] = {GameObject.APPLE, GameObject.ORANGE, GameObject.BANANA};
    private final int pipeIndex[] = {1, 10, 11, 100, 101, 110, 111, 1000, 1001, 1010, 1011, 1100, 1101, 1110, 1111};

    private transient Pacman pacMan;
    private transient Ghost[] ghosts;
    private Difficulty difficulty;
    private boolean ghostsEatable;
    private float eatableGhostsTimer;
    private float packmanAttackTimer;


    public GameLevel(Difficulty difficulty) {
        this.difficulty = difficulty;
        this.loadResources();

        this.pacMan = new Pacman(this, difficulty);
        this.pacMan.initStats();

        this.ghosts = new Ghost[4];
        this.ghosts[0] = new Ghost(this, GameObject.RED_GHOST, difficulty);
        this.ghosts[1] = new Ghost(this, GameObject.GREEN_GHOST, difficulty);
        this.ghosts[2] = new Ghost(this, GameObject.BLUE_GHOST, difficulty);
        this.ghosts[3] = new Ghost(this, GameObject.PURPLE_GHOST, difficulty);
    }

    private void loadResources() {
        this.mapObjectsTextures = new HashMap<>();
        this.pipesTextures = new HashMap<>();

        putTexture(GameObject.EMPTY_CELL);
        putTexture(GameObject.FOOD);
        putTexture(GameObject.XFOOD);
        putTexture(GameObject.PIPE);
        putTexture(GameObject.ORANGE);
        putTexture(GameObject.APPLE);
        putTexture(GameObject.BANANA);
    }

    public void startNewLevel(int level) {
        ghostsEatable = false;
        eatableGhostsTimer = 0;
        packmanAttackTimer = 0;

        initMap();
        pacMan.initStats();
        pacMan.initPosition();
        for (int i = 0; i < ghosts.length; i++) {
            ghosts[i].initPosition();
            ghosts[i].initRouteMap();
            ghosts[i].setEatable(ghostsEatable);
        }
    }

    public void restoreSession(GameSession gs) {
        this.pacMan = gs.getPacMan();
        this.ghosts = gs.getGhosts();

        this.loadResources();
        this.pacMan.loadResources(this);
        for (int i = 0; i < this.ghosts.length; i++) {
            this.ghosts[i].loadResources(this);
        }
    }

    public Ghost[] getGhosts() {
        return ghosts;
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

    public boolean isOutOfBounds(int x, int y) {
        return (x < 0 || x >= mapWidht || y < 0 || y >= mapHeight);
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

    private void initMap() {
        loadMap(MAP_FILE_NAME);
        fruitsMap = new GameObject[mapWidht][mapHeight];
        for (int i = 0; i < mapWidht; i++) {
            for (int j = 0; j < mapHeight; j++) {
                fruitsMap[i][j] = GameObject.EMPTY_CELL;
            }
        }
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

        pacMan.render(batch);
        for (int i = 0; i < ghosts.length; i++) {
            ghosts[i].render(batch);
        }
    }

    public boolean isCellEmpty(int cellX, int cellY) {
        if (cellX < 0 || cellY < 0 || cellX >= mapData.length || cellY >= mapData[cellX].length) return false;
        return mapData[cellX][cellY] != GameObject.PIPE;
    }

    public void addRandomFruit() {
        int cellsCount = 0;
        for (int i = 0; i < mapWidht; i++) {
            for (int j = 0; j < mapHeight; j++) {
                if (fruitsMap[i][j] == GameObject.FOOD) cellsCount++;
            }
        }

        int targetCell = MathUtils.random(cellsCount);
        for (int i = 0; i < mapWidht; i++) {
            for (int j = 0; j < mapHeight; j++) {
                if (fruitsMap[i][j] == GameObject.FOOD) {
                    if (targetCell == 0) {
                        mapData[i][j] = fruits[MathUtils.random(fruits.length - 1)];
                        fruitsMap[i][j] = GameObject.EMPTY_CELL;
                        return;
                    }

                    targetCell--;
                }
            }
        }
    }

    public GameObject checkFood(int x, int y) {
        GameObject gameObject = mapData[x][y];
        if (gameObject.isFood()) {
            mapData[x][y] = GameObject.EMPTY_CELL;
            fruitsMap[x][y] = GameObject.FOOD;
            if (gameObject == GameObject.FOOD) foodCount--;
        }
        return gameObject;
    }

    public void update(float dt) {

        updateContacts();
        updateGhostsTargetCell(dt);
        updatePacmanBeastModeState(dt);

        for (int i = 0; i < ghosts.length; i++) {
            ghosts[i].update(dt);
        }

        pacMan.update(dt);

    }

    public Pacman getPacMan() {
        return pacMan;
    }

    private void updateGhostsTargetCell(float dt) {
        packmanAttackTimer += dt;
        if (packmanAttackTimer >= difficulty.getPacmanAttackTimer()) {
            for (int i = 0; i < ghosts.length; i++) {
                if (pacMan.getAction() != Action.RECOVERING) {
                    ghosts[i].setTargetCell(pacMan.getCurrentMapPosition());
                } else {
                    ghosts[i].setTargetCell(getStartPosition(ghosts[i].getGameObject()));
                }
            }
            packmanAttackTimer = 0;
        }
    }

    private void updatePacmanBeastModeState(float dt) {
        if (ghostsEatable) {
            eatableGhostsTimer -= dt;
            if (eatableGhostsTimer <= 0) {
                ghostsEatable = false;
                for (int i = 0; i < ghosts.length; i++) {
                    ghosts[i].setEatable(ghostsEatable);
                }
            }
        }

        if (pacMan.getAction() == Action.WAITING) {
            if (pacMan.checkFoodEating()) {
                ghostsEatable = true;
                eatableGhostsTimer = difficulty.getEatableGhostTimer();
                for (int i = 0; i < ghosts.length; i++) {
                    ghosts[i].setEatable(ghostsEatable);
                }
            }
        }
    }

    private void updateContacts() {
        if (pacMan.getAction() == Action.RECOVERING) return;
        for (int i = 0; i < ghosts.length; i++) {
            if (ghosts[i].getAction() == Action.RECOVERING) continue;
            if (pacMan.getCurrentWorldPosition().dst(ghosts[i].getCurrentWorldPosition()) < pacMan.HALF_SIZE) {
                if (ghostsEatable) {
                    pacMan.eatObject(ghosts[i].getGameObject());
                    ghosts[i].respawn();
                } else {
                    pacMan.decreaseLives();
                    if (pacMan.getLives() > 0) pacMan.respawn();
                }
            }
        }
    }

}
