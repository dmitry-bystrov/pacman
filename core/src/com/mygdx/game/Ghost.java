package com.mygdx.game;

import com.badlogic.gdx.math.MathUtils;

public class Ghost extends Creature {
    public Ghost(GameMap gameMap, int posX, int posY, float speed, String textureName) {
        super(gameMap, posX, posY, speed, textureName);
        secPerFrame = 0.3f;
    }

    @Override
    protected void getDirection() {
        direction.x = 0;
        direction.y = 0;

        int randomDirection = MathUtils.random(3);

        if (randomDirection == 1) {
            direction.y = 1;
            return;
        }
        if (randomDirection == 2) {
            direction.y = -1;
            return;
        }
        if (randomDirection == 3) {
            direction.x = -1;
            return;
        }
        if (randomDirection == 0) {
            direction.x = 1;
            return;
        }
    }

    @Override
    protected void updateRotation() {
        if (direction.x == 1) rotation = 0;
        if (direction.y == 1) rotation = 0;
        if (direction.x == -1) rotation = 180;
        if (direction.y == -1) rotation = 0;
    }
}
