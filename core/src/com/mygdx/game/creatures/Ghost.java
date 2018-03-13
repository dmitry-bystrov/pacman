package com.mygdx.game.creatures;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.mygdx.game.GameMap;
import com.mygdx.game.screens.GameScreen;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

public class Ghost extends Creature {
    public enum GhostType {
        RED(0, 1.3f), GREEN(1, 1.2f), BLUE(2, 1.1f), PURPLE(3, 1.0f), EATABLE(4, 0.8f);
        private final int textureRegionNumber;
        private final float speed;

        GhostType(int textureRegionNumber, float speed) {
            this.textureRegionNumber = textureRegionNumber;
            this.speed = speed;
        }

        public int getTextureRegionNumber() {
            return textureRegionNumber;
        }

        public float getSpeed() {
            return speed;
        }
    }

    public enum WhoIsKilled { PACMAN, GHOST, NOBODY }

    private GhostType currentGhostType;
    private GhostType originalGhostType;
    private TextureRegion[] originalTextureRegions;
    private TextureRegion[] eatableTextureRegions;
    private Vector2 targetPosition;
    private boolean chaseMode;

    public Ghost(GameMap gameMap, int posX, int posY, float baseSpeed, TextureAtlas atlas, GhostType ghostType) {
        super(gameMap, posX, posY, baseSpeed);
        this.originalTextureRegions = atlas.findRegion("ghosts").split(SIZE, SIZE)[ghostType.getTextureRegionNumber()];
        this.eatableTextureRegions = atlas.findRegion("ghosts").split(SIZE, SIZE)[GhostType.EATABLE.getTextureRegionNumber()];
        this.originalGhostType = ghostType;
        this.chaseMode = false;
        targetPosition = new Vector2(startX * SIZE, startY * SIZE);
        setEatable(false);
        secPerFrame = 0.3f;
    }

    public void setTargetPosition(int x, int y) {
        targetPosition.set(x * SIZE, y * SIZE);
        chaseMode = true;
    }

    public void setEatable(boolean eatable) {
        if (eatable) {
            currentGhostType = GhostType.EATABLE;
            this.textureRegions = eatableTextureRegions;
        } else {
            currentGhostType = originalGhostType;
            this.textureRegions = originalTextureRegions;
        }
        currentSpeed = baseSpeed * currentGhostType.getSpeed();
    }

    public WhoIsKilled checkContact(Vector2 pacmanPosition) {
        if (pacmanPosition.dst(position) < HALF_SIZE) {
            if (currentGhostType == GhostType.EATABLE) {
                return WhoIsKilled.GHOST;
            } else {
                return WhoIsKilled.PACMAN;
            }
        }
        return WhoIsKilled.NOBODY;
    }

    @Override
    protected void getDirection() {
        direction.x = 0;
        direction.y = 0;

        if (chaseMode && currentGhostType != GhostType.EATABLE) {
            if (targetPosition.dst(position) > 0) {
                int[] xDirections = {0,  0, -1, 1};
                int[] yDirections = {1, -1,  0, 0};
                float[] distance = new float[4];
                int minDistance = -1;
                for (int i = 0; i < 4; i++) {
                    if (gameMap.isCellEmpty(mapX + xDirections[i],mapY + yDirections[i])) {
                        distance[i] = targetPosition.dst(destination.x + xDirections[i] * SIZE,
                                destination.y + yDirections[i] * SIZE);
                        if (minDistance == -1) {
                            minDistance = i;
                        } else {
                            if (distance[i] < distance[minDistance]) minDistance = i;
                        }
                    }
                }
                if (minDistance != -1) {
                    direction.x = xDirections[minDistance];
                    direction.y = yDirections[minDistance];
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
        if (gameMap.isCellEmpty(mapX + x,mapY + y)) {
            direction.x = x;
            direction.y = y;
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
