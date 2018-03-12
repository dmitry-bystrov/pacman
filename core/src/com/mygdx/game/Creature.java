package com.mygdx.game;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;

public abstract class Creature {
    protected enum Action { IDLE, MOVING, DIEING }

    protected GameMap gameMap;
    protected Texture texture;
    protected Vector2 position;
    protected Vector2 direction;
    protected Vector2 destination;
    protected Vector2 velocity;
    protected Action action;
    protected float speed;
    protected float animationTimer;
    protected float secPerFrame;
    protected int maxFrames;
    protected int rotation;
    protected boolean canEatFoood;

    public Creature(GameMap gameMap, int posX, int posY, float speed, String textureName) {
        this.gameMap = gameMap;
        this.speed = speed;
        this.texture = new Texture(textureName);
        position = new Vector2(posX * GameMap.CELL_SIZE_PX, posY * GameMap.CELL_SIZE_PX);
        velocity = new Vector2(0, 0);
        direction = new Vector2(0, 0);
        destination = position.cpy();
        action = Action.IDLE;
        rotation = 0;
        canEatFoood = false;

        this.animationTimer = 0.0f;
        this.secPerFrame = 0.1f;
        this.maxFrames = this.texture.getWidth() / GameMap.CELL_SIZE_PX;
    }

    public float getCX() {
        return position.x + GameMap.CELL_SIZE_PX / 2;
    }

    public float getCY() {
        return position.y + GameMap.CELL_SIZE_PX / 2;
    }

    public void render(SpriteBatch batch) {
        int currentFrame = (int) (animationTimer / secPerFrame);
        int origin = GameMap.CELL_SIZE_PX / 2;
        int width = GameMap.CELL_SIZE_PX;
        int height = GameMap.CELL_SIZE_PX;
        boolean flipY = (rotation == 180);
        batch.draw(texture, position.x, position.y, origin, origin, width, height, 1, 1, rotation, currentFrame * width, 0, width, height, false, flipY);
    }

    public void update(float dt) {
        animationTimer += dt;
        if (animationTimer >= maxFrames * secPerFrame) {
            animationTimer = 0.0f;
        }

        if (action == Action.IDLE)
        {
            getDirection();
            if (direction.len() != 0) {
                if (gameMap.isCellEmpty((int)destination.x / GameMap.CELL_SIZE_PX + (int)direction.x,
                        (int)destination.y / GameMap.CELL_SIZE_PX + (int)direction.y)) {
                    destination.x += GameMap.CELL_SIZE_PX * direction.x;
                    destination.y += GameMap.CELL_SIZE_PX * direction.y;
                    updateRotation();
                    action = Action.MOVING;
                    updateVelocity();
                }
            }
        }

        if (action == Action.MOVING) {
            float oldDistance = position.dst(destination);
            position.mulAdd(velocity, dt);
            if (position.dst(destination) > oldDistance)  {
                position.x = destination.x;
                position.y = destination.y;
                if (canEatFoood) gameMap.checkFoodEating((int)position.x / GameMap.CELL_SIZE_PX, (int)position.y / GameMap.CELL_SIZE_PX);
                action = Action.IDLE;
            }
        }
    }

    private void updateVelocity() {
        velocity = destination.cpy().sub(position).nor().scl(speed);
    }

    protected abstract void getDirection();
    protected abstract void updateRotation();
}
