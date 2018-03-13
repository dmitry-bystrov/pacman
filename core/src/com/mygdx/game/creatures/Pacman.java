package com.mygdx.game.creatures;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.mygdx.game.GameMap;
import com.mygdx.game.screens.GameScreen;

public class Pacman extends Creature {
    public static final int MAX_LIVES = 3;
    private int lives;

    public Pacman(GameMap gameMap, int posX, int posY, float baseSpeed, TextureAtlas atlas) {
        super(gameMap, posX, posY, baseSpeed);
        this.textureRegions = atlas.findRegion("pacman").split(SIZE, SIZE)[0];
        lives = MAX_LIVES;
    }

    public int getLives() {
        return lives;
    }

    public boolean checkFoodEating() {
        if (action == Action.IDLE) {
            return gameMap.checkFoodEating(mapX, mapY);
        }
        return false;
    }

    public void decreaseLives() {
        lives--;
    }

    @Override
    protected void getDirection() {
        direction.x = 0;
        direction.y = 0;

        if (Gdx.input.isKeyPressed(Input.Keys.W)) {
            updateDirection(0 ,1);
            return;
        }
        if (Gdx.input.isKeyPressed(Input.Keys.S)) {
            updateDirection(0 ,-1);
            return;
        }
        if (Gdx.input.isKeyPressed(Input.Keys.A)) {
            updateDirection(-1 ,0);
            return;
        }
        if (Gdx.input.isKeyPressed(Input.Keys.D)) {
            updateDirection(1 ,0);
            return;
        }
    }

    private void updateDirection(int x, int y) {
        if (gameMap.isCellEmpty(mapX + x,mapY + y)) {
            direction.x = x;
            direction.y = y;
        }
    }

    @Override
    protected void updateRotation() {
        if (direction.x == 1) rotation = 0;
        if (direction.y == 1) rotation = 90;
        if (direction.x == -1) rotation = 180;
        if (direction.y == -1) rotation = 270;
    }
}
