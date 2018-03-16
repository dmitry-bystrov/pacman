package com.mygdx.game.creatures;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.mygdx.game.GameMap;

import java.util.LinkedHashMap;

public class Pacman extends Creature {
    private int lives;
    private int score;
    private LinkedHashMap<GameObject, Integer> eatenObjects;

    public Pacman(GameMap gameMap) {
        super(gameMap, GameObject.PACMAN);
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
        score += gameObject.getScore();
    }

    public int getLives() {
        return lives;
    }

    public int getScore() {
        return score;
    }

    public Action getAction() { return action; }

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
        if (gameMap.isCellEmpty((int)currentMapPosition.x + d.getX(),(int)currentMapPosition.y + d.getY())) {
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
