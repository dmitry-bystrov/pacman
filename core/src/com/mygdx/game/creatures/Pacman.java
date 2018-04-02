package com.mygdx.game.creatures;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.mygdx.game.GameMap;

import java.util.LinkedHashMap;

public class Pacman extends Creature {
    private int lives;
    private int score;
    private LinkedHashMap<GameObject, Integer> eatenObjects;

    public Pacman(GameMap gameMap, Difficulty difficulty) {
        super(gameMap, GameObject.PACMAN, difficulty);
        this.lives = MAX_LIVES;
        this.score = 0;
        this.eatenObjects = new LinkedHashMap<>();
        initStats();
    }

    public void initStats() {
        score = 0;
        eatenObjects.clear();
        for (GameObject o:GameObject.values()) {
            if (o.isFood() || o.isCreature() && o != this.gameObject) {
                eatenObjects.put(o, 0);
            }
        }
    }

    public LinkedHashMap<GameObject, Integer> getStats() {
        return eatenObjects;
    }

    public void eatObject(GameObject gameObject) {
        eatenObjects.put(gameObject, eatenObjects.get(gameObject) + 1);
        if (gameObject == GameObject.FOOD && eatenObjects.get(gameObject) % FRUITS_DROP_FREQUENCY == 0) {
            gameMap.addRandomFruit();
        }
        score += gameObject.getScore();
    }

    public int getLives() {
        return lives;
    }

    public int getScore() {
        return score;
    }

    public boolean checkFoodEating() {
        GameObject uncknownObject = gameMap.checkFood((int)currentMapPosition.x, (int)currentMapPosition.y);
        if (uncknownObject.isFood()) {
            eatObject(uncknownObject);
        }
        return uncknownObject == GameObject.XFOOD;
    }

    public void decreaseLives() {
        lives--;
    }

    @Override
    protected void getDirection() {
        directionVector.x = 0;
        directionVector.y = 0;

        if (Gdx.input.isKeyPressed(Input.Keys.W)) {
            updateDirection(Direction.UP);
            return;
        }
        if (Gdx.input.isKeyPressed(Input.Keys.S)) {
            updateDirection(Direction.DOWN);
            return;
        }
        if (Gdx.input.isKeyPressed(Input.Keys.A)) {
            updateDirection(Direction.LEFT);
            return;
        }
        if (Gdx.input.isKeyPressed(Input.Keys.D)) {
            updateDirection(Direction.RIGHT);
            return;
        }
    }

    private void updateDirection(Direction d) {
        int x = (int)currentMapPosition.x + d.getX();
        int y = (int)currentMapPosition.y + d.getY();

        if (gameMap.isCellEmpty(x,y) || (x < 0 || x >= gameMap.getMapWidht())) {
            directionVector.x = d.getX();
            directionVector.y = d.getY();
        }
    }

    @Override
    protected void updateRotation() {
        if (directionVector.x == 1) rotation = 0;
        if (directionVector.y == 1) rotation = 90;
        if (directionVector.x == -1) rotation = 180;
        if (directionVector.y == -1) rotation = 270;
    }
}
