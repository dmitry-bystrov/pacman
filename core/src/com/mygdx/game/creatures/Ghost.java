package com.mygdx.game.creatures;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.MathUtils;
import com.mygdx.game.GameMap;
import com.mygdx.game.screens.GameScreen;

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

    private GhostType currentGhostType;
    private GhostType originalGhostType;
    private TextureRegion[] originalTextureRegions;
    private TextureRegion[] eatableTextureRegions;
    private Pacman pacman;

    public Ghost(GameMap gameMap, Pacman pacman, int posX, int posY, float baseSpeed, TextureAtlas atlas, GhostType ghostType) {
        super(gameMap, posX, posY, baseSpeed);
        this.pacman = pacman;
        this.originalTextureRegions = atlas.findRegion("ghosts").split(SIZE, SIZE)[ghostType.getTextureRegionNumber()];
        this.eatableTextureRegions = atlas.findRegion("ghosts").split(SIZE, SIZE)[GhostType.EATABLE.getTextureRegionNumber()];
        this.originalGhostType = ghostType;
        setEatable(false);
        secPerFrame = 0.3f;
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

    @Override
    public void update(float dt) {
        if (pacman.getPosition().dst(position) < HALF_SIZE) {
            if (currentGhostType == GhostType.EATABLE) {
                System.out.println("Ghost is killed!");
                init();
            } else {
                System.out.println("Pacman is killed!");
                pacman.decreaseLives();
                if (pacman.getLives() == 0) {
                    System.out.println("Game over!");
                    Gdx.app.exit();
                } else {
                    pacman.init();
                }
            }
        }
        super.update(dt);
    }

    @Override
    protected void getDirection() {
        direction.x = 0;
        direction.y = 0;

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
        if (gameMap.isCellEmpty((int)destination.x / GameScreen.WORLD_CELL_PX + x,
                (int)destination.y / GameScreen.WORLD_CELL_PX + y)) {
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
