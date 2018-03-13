package com.mygdx.game.creatures;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.mygdx.game.GameMap;
import com.mygdx.game.screens.GameScreen;

public abstract class Creature {
    protected enum Action { IDLE, MOVING, DIEING }
    protected final int SIZE = GameScreen.WORLD_CELL_PX;
    protected final int HALF_SIZE = GameScreen.WORLD_CELL_PX / 2;

    protected GameMap gameMap;
    protected TextureRegion[] textureRegions;
    protected Vector2 position;
    protected Vector2 direction;
    protected Vector2 destination;
    protected Vector2 velocity;
    protected Action action;
    protected float baseSpeed;
    protected float currentSpeed;
    protected float animationTimer;
    protected float secPerFrame;
    protected int rotation;
    protected int mapX;
    protected int mapY;
    protected final int startX;
    protected final int startY;

    public Creature(GameMap gameMap, int posX, int posY, float baseSpeed) {
        this.startX = posX;
        this.startY = posY;
        this.mapX = posX;
        this.mapY = posY;
        this.gameMap = gameMap;
        this.baseSpeed = baseSpeed;
        this.currentSpeed = baseSpeed;
        this.position = new Vector2(startX * SIZE, startY * SIZE);
        this.destination = new Vector2(startX * SIZE, startY * SIZE);
        this.velocity = new Vector2(0,0);
        this.direction = new Vector2(0,0);
        this.animationTimer = 0.0f;
        this.secPerFrame = 0.1f;
    }

    public void initPosition() {
        position.set(startX * SIZE, startY * SIZE);
        destination.set(position);
        velocity.set(0, 0);
        direction.set(0, 0);
        mapX = startX;
        mapY = startY;
        action = Action.IDLE;
        rotation = 0;
    }

    public float getCX() {
        return position.x + HALF_SIZE;
    }

    public float getCY() {
        return position.y + HALF_SIZE;
    }

    public int getMapX() {
        return mapX;
    }

    public int getMapY() {
        return mapY;
    }

    public Vector2 getPosition() {
        return position;
    }

    public int getCurrentFrame() {
        return (int) (animationTimer / secPerFrame);
    }

    public void render(SpriteBatch batch) {
        if ((rotation == 180) != textureRegions[getCurrentFrame()].isFlipY()) {
            textureRegions[getCurrentFrame()].flip(false, true);
        }
        batch.draw(textureRegions[getCurrentFrame()], position.x, position.y, HALF_SIZE, HALF_SIZE, SIZE, SIZE, 1, 1, rotation);
    }

    public void update(float dt) {
        animationTimer += dt;
        if (animationTimer >= textureRegions.length * secPerFrame) {
            animationTimer = 0.0f;
        }

        if (action == Action.IDLE)
        {
            getDirection();
            if (direction.len() != 0) {
                destination.x += GameScreen.WORLD_CELL_PX * direction.x;
                destination.y += GameScreen.WORLD_CELL_PX * direction.y;
                updateRotation();
                action = Action.MOVING;
                updateVelocity();
            }
        }

        if (action == Action.MOVING) {
            float oldDistance = position.dst(destination);
            position.mulAdd(velocity, dt);
            if (position.dst(destination) > oldDistance)  {
                position.x = destination.x;
                position.y = destination.y;
                mapX = (int)position.x / SIZE;
                mapY = (int)position.y / SIZE;
                action = Action.IDLE;
            }
        }
    }

    private void updateVelocity() {
        velocity.set(destination).sub(position).nor().scl(baseSpeed);
    }

    protected abstract void getDirection();
    protected abstract void updateRotation();
}
