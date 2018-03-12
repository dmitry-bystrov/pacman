package com.mygdx.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;

public class Pacman extends Creature {
    public Pacman(GameMap gameMap, int posX, int posY, float speed, String textureName) {
        super(gameMap, posX, posY, speed, textureName);
        canEatFoood = true;
    }

    @Override
    protected void getDirection() {
        direction.x = 0;
        direction.y = 0;

        if (Gdx.input.isKeyPressed(Input.Keys.W)) {
            direction.y = 1;
            return;
        }
        if (Gdx.input.isKeyPressed(Input.Keys.S)) {
            direction.y = -1;
            return;
        }
        if (Gdx.input.isKeyPressed(Input.Keys.A)) {
            direction.x = -1;
            return;
        }
        if (Gdx.input.isKeyPressed(Input.Keys.D)) {
            direction.x = 1;
            return;
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
