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
        Direction[] directions = {Direction.UP, Direction.DOWN, Direction.LEFT, Direction.RIGHT};
        directionVector.x = 0;
        directionVector.y = 0;

        if (chaseMode && !eatable) {
            if (targetPosition.dst(currentWorldPosition) > 0) {
                float[] distance = new float[4];
                int minDistance = -1;
                for (int i = 0; i < 4; i++) {
                    if (gameMap.isCellEmpty((int)currentMapPosition.x + directions[i].getX(),
                            (int)currentMapPosition.y + directions[i].getY())) {
                        distance[i] = targetPosition.dst(destinationPoint.x + directions[i].getX() * SIZE,
                                destinationPoint.y + directions[i].getY() * SIZE);
                        if (minDistance == -1) {
                            minDistance = i;
                        } else {
                            if (distance[i] < distance[minDistance]) minDistance = i;
                        }
                    }
                }
                if (minDistance != -1) {
                    directionVector.x = directions[minDistance].getX();
                    directionVector.y = directions[minDistance].getY();
                    return;
                }
            } else {
                chaseMode = false;
            }
        }

        updateDirection(directions[MathUtils.random(3)]);
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
        if (directionVector.y == 1) rotation = 0;
        if (directionVector.x == -1) rotation = 180;
        if (directionVector.y == -1) rotation = 0;
    }
}
