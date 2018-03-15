package com.mygdx.game.creatures;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.mygdx.game.GameMap;
import com.mygdx.game.screens.GameScreen;

public abstract class Creature {
    protected enum Action {WAITING, MOVING, DIEING }
    public final int SIZE = GameScreen.WORLD_CELL_PX;
    public final int HALF_SIZE = GameScreen.WORLD_CELL_PX / 2;

    protected GameMap gameMap;
    protected GameMap.MapObject mapObject; 
    protected TextureRegion[] textureRegions;
    protected Vector2 currentWorldPosition;
    protected Vector2 currentMapPosition;
    protected Vector2 direction;
    protected Vector2 destination;
    protected Vector2 velocity;
    protected Action action;
    protected float baseSpeed;
    protected float currentSpeed;
    protected float animationTimer;
    protected float secPerFrame;
    protected int rotation;

    public Creature(GameMap gameMap, float baseSpeed, GameMap.MapObject mapObject) {
        this.gameMap = gameMap;
        this.mapObject = mapObject;
        this.baseSpeed = baseSpeed;
        this.currentSpeed = baseSpeed;
        this.currentWorldPosition = new Vector2();
        this.currentMapPosition = new Vector2();
        this.destination = new Vector2();
        this.velocity = new Vector2();
        this.direction = new Vector2();
        this.animationTimer = 0.0f;
        this.secPerFrame = 0.08f;
    }

    public void initPosition() {
        currentMapPosition.set(gameMap.getStartPosition(mapObject));
        currentWorldPosition.set(currentMapPosition).scl(SIZE);
        destination.set(currentWorldPosition);
        velocity.set(0, 0);
        direction.set(0, 0);
        action = Action.WAITING;
        rotation = 0;
    }

    public Vector2 getCurrentMapPosition() {
        return currentMapPosition;
    }

    public Vector2 getCurrentWorldPosition() {
        return currentWorldPosition;
    }

    public int getCurrentFrame() {
        return (int) (animationTimer / secPerFrame);
    }

    public void render(SpriteBatch batch) {
        if ((rotation == 180) != textureRegions[getCurrentFrame()].isFlipY()) {
            textureRegions[getCurrentFrame()].flip(false, true);
        }
        batch.draw(textureRegions[getCurrentFrame()], currentWorldPosition.x, currentWorldPosition.y, HALF_SIZE, HALF_SIZE, SIZE, SIZE, 1, 1, rotation);
    }

    public void update(float dt) {
        animationTimer += dt;
        if (animationTimer >= textureRegions.length * secPerFrame) {
            animationTimer = 0.0f;
        }

        if (action == Action.WAITING)
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
            float oldDistance = currentWorldPosition.dst(destination);
            currentWorldPosition.mulAdd(velocity, dt);
            if (currentWorldPosition.dst(destination) > oldDistance)  {
                currentWorldPosition.x = destination.x;
                currentWorldPosition.y = destination.y;
                currentMapPosition.x = (int) currentWorldPosition.x / SIZE;
                currentMapPosition.y = (int) currentWorldPosition.y / SIZE;
                action = Action.WAITING;
            }
        }
    }

    private void updateVelocity() {
        velocity.set(destination).sub(currentWorldPosition).nor().scl(baseSpeed);
    }

    protected abstract void getDirection();
    protected abstract void updateRotation();
}
