package com.mygdx.game.creatures;

import com.badlogic.gdx.Application;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.mygdx.game.GameManager;
import com.mygdx.game.SoundManager;

import java.util.LinkedHashMap;

public class Pacman extends Creature {
    private int lives;
    private int score;
    private LinkedHashMap<GameObject, Integer> eatenObjects;

    public Pacman(GameManager gameManager, Difficulty difficulty) {
        super(gameManager, GameObject.PACMAN, difficulty);
        this.eatenObjects = new LinkedHashMap<>();
        initStats();
    }

    public void initStats() {
        this.lives = MAX_LIVES;
        this.score = 0;
        this.eatenObjects.clear();
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
        switch (gameObject) {
            case FOOD:
                SoundManager.playSound(GameSound.FOOD);
                break;
            case XFOOD:
                SoundManager.playSound(GameSound.POWERED);
                break;
            case APPLE:
            case BANANA:
            case ORANGE:
                SoundManager.playSound(GameSound.FRUIT_COLLECTED);
                break;
            default:
                SoundManager.playSound(GameSound.GHOST_KILLED);
        }

        eatenObjects.put(gameObject, eatenObjects.get(gameObject) + 1);
        if (gameObject == GameObject.FOOD && eatenObjects.get(gameObject) % FRUITS_DROP_FREQUENCY == 0) {
            gameManager.addRandomFruit();
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
        GameObject uncknownObject = gameManager.checkFood((int)currentMapPosition.x, (int)currentMapPosition.y);
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

        updateDirection(gameManager.getJoystick().getDirection());

        if (Gdx.app.getType() == Application.ApplicationType.Desktop) {

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
    }

    private void updateDirection(Direction d) {
        if (d == null) return;

        int x = (int)currentMapPosition.x + d.getX();
        int y = (int)currentMapPosition.y + d.getY();

        if (gameManager.isCellEmpty(x,y) || (x < 0 || x >= gameManager.getMapWidth())) {
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
