package com.mygdx.game.creatures;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.mygdx.game.Assets;
import com.mygdx.game.GameMap;

public class Ghost extends Creature {
    private TextureRegion[] originalTextureRegions;
    private TextureRegion[] eatableTextureRegions;
    private Vector2 targetPosition;
    private boolean chaseMode;
    private boolean eatable;

    public Ghost(GameMap gameMap, GameObject gameObject) {
        super(gameMap, gameObject);
        this.originalTextureRegions = textureRegions;
        this.eatableTextureRegions = Assets.getInstance().getAtlas().findRegion("ghosts").split(SIZE, SIZE)[4];
        this.targetPosition = new Vector2();
        this.chaseMode = false;
        secPerFrame = 0.3f;
    }

    public void setTargetCell(Vector2 targetCell) {
        targetPosition.set(targetCell).scl(SIZE);
        chaseMode = true;
    }

    public void setEatable(boolean eatable) {
        this.eatable = eatable;
        if (eatable) {
            textureRegions = eatableTextureRegions;
            currentSpeed = BASE_SPEED * (gameObject.getSpeed() * 0.5f);
        } else {
            textureRegions = originalTextureRegions;
            currentSpeed = BASE_SPEED * gameObject.getSpeed();
        }
    }

    @Override
    protected void getDirection() {
        directionVector.x = 0;
        directionVector.y = 0;

        if (chaseMode && !eatable) {
            if (targetPosition.dst(currentWorldPosition) > 0) {
                int[] xDirections = {0,  0, -1, 1};
                int[] yDirections = {1, -1,  0, 0};
                float[] distance = new float[4];
                int minDistance = -1;
                for (int i = 0; i < 4; i++) {
                    if (gameMap.isCellEmpty((int)currentMapPosition.x + xDirections[i],
                            (int)currentMapPosition.y + yDirections[i])) {
                        distance[i] = targetPosition.dst(destinationPoint.x + xDirections[i] * SIZE,
                                destinationPoint.y + yDirections[i] * SIZE);
                        if (minDistance == -1) {
                            minDistance = i;
                        } else {
                            if (distance[i] < distance[minDistance]) minDistance = i;
                        }
                    }
                }
                if (minDistance != -1) {
                    directionVector.x = xDirections[minDistance];
                    directionVector.y = yDirections[minDistance];
                    return;
                }
            } else {
                chaseMode = false;
            }
        }

        int randomDirection = MathUtils.random(3);

        if (randomDirection == 1) {
            updateDirection(0 ,1);
            return;
        }
        if (randomDirection == 2) {
            updateDirection(0 ,-1);
            return;
        }
        if (randomDirection == 3) {
            updateDirection(-1 ,0);
            return;
        }
        if (randomDirection == 0) {
            updateDirection(1 ,0);
            return;
        }
    }

    private void updateDirection(int x, int y) {
        if (gameMap.isCellEmpty((int)currentMapPosition.x + x,(int)currentMapPosition.y + y)) {
            directionVector.x = x;
            directionVector.y = y;
        }
    }

    @Override
    protected void updateRotation() {
        if (directionVector.x == 1) rotation = 0;
        if (directionVector.y == 1) rotation = 0;
        if (directionVector.x == -1) rotation = 180;
        if (directionVector.y == -1) rotation = 0;
    }
}
