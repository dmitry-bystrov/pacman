package com.mygdx.game.creatures;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.mygdx.game.Assets;
import com.mygdx.game.GameConstants;
import com.mygdx.game.GameMap;

public abstract class Creature implements GameConstants {

    public final int SIZE = WORLD_CELL_PX;
    public final int HALF_SIZE = WORLD_CELL_PX / 2;

    protected GameMap gameMap;
    protected GameObject gameObject;
    protected TextureRegion[] textureRegions;
    protected Vector2 currentWorldPosition;
    protected Vector2 currentMapPosition;
    protected Vector2 directionVector;
    protected Vector2 destinationPoint;
    protected Vector2 velocityVector;
    protected Action action;
    protected float currentSpeed;
    protected float animationTimer;
    protected float secPerFrame;
    protected int rotation;
    protected float recoveryTimer;
    protected static Texture healthBarFill;
    protected static Texture healthBarBorder;
    protected Difficulty difficulty;


    public Creature(GameMap gameMap, GameObject gameObject, Difficulty difficulty) {
        this.gameMap = gameMap;
        this.gameObject = gameObject;
        this.difficulty = difficulty;
        this.currentSpeed = BASE_SPEED;
        this.currentWorldPosition = new Vector2();
        this.currentMapPosition = new Vector2();
        this.destinationPoint = new Vector2();
        this.velocityVector = new Vector2();
        this.directionVector = new Vector2();
        this.animationTimer = 0.0f;
        this.secPerFrame = 0.08f;
        this.recoveryTimer = 0;
        this.textureRegions = Assets.getInstance().getAtlas().findRegion(gameObject.getTextureName()).split(SIZE, SIZE)[gameObject.getTextureRegionIndex()];

        if (healthBarFill == null) {
            Pixmap pixmap = new Pixmap(SIZE, 10, Pixmap.Format.RGBA8888);
            pixmap.setColor(Color.rgba8888(0.68f, 0.92f, 0.28f, 1.0f));
            pixmap.fill();
            healthBarFill = new Texture(pixmap);
        }
        if (healthBarBorder == null) {
            Pixmap pixmap = new Pixmap(SIZE, 10, Pixmap.Format.RGBA8888);
            pixmap.setColor(Color.rgba8888(0.32f, 0.48f, 0.08f, 1.0f));
            pixmap.drawRectangle(0,0,SIZE,10);
            pixmap.drawRectangle(1,1,SIZE - 2,8);
            healthBarBorder = new Texture(pixmap);
        }
    }

    public void initPosition() {
        currentMapPosition.set(gameMap.getStartPosition(gameObject));
        currentWorldPosition.set(currentMapPosition).scl(SIZE);
        destinationPoint.set(currentWorldPosition);
        velocityVector.set(0, 0);
        directionVector.set(0, 0);
        action = Action.WAITING;
        rotation = 0;
    }

    public void respawn() {
        initPosition();
        setAction(Action.RECOVERING);
    }

    public Vector2 getCurrentMapPosition() {
        return currentMapPosition;
    }

    public Vector2 getCurrentWorldPosition() {
        return currentWorldPosition;
    }

    public GameObject getGameObject() {
        return gameObject;
    }

    public int getCurrentFrame() {
        return (int) (animationTimer / secPerFrame);
    }

    public void render(SpriteBatch batch) {
        if ((rotation == 180) != textureRegions[getCurrentFrame()].isFlipY()) {
            textureRegions[getCurrentFrame()].flip(false, true);
        }

        if (action == Action.RECOVERING) {
            batch.draw(healthBarFill, currentWorldPosition.x, currentWorldPosition.y + 90, 80 * (recoveryTimer / difficulty.getRecoveryTimer()), 10);
            batch.draw(healthBarBorder, currentWorldPosition.x, currentWorldPosition.y + 90, 80, 10);
            if (animationTimer % 0.2f > 0.1f) return;
        }
        batch.draw(textureRegions[getCurrentFrame()], currentWorldPosition.x, currentWorldPosition.y, HALF_SIZE, HALF_SIZE, SIZE, SIZE, 1, 1, rotation);
    }

    public void update(float dt) {
        animationTimer += dt;
        if (animationTimer >= textureRegions.length * secPerFrame) {
            animationTimer = 0.0f;
        }

        if (action == Action.RECOVERING) {
            recoveryTimer += dt;
            if (recoveryTimer > difficulty.getRecoveryTimer()) {
                action = Action.WAITING;
                recoveryTimer = 0;
            }
            return;
        }

        if (action == Action.WAITING)
        {
            getDirection();
            if (directionVector.len() != 0) {
                destinationPoint.x += SIZE * directionVector.x;
                destinationPoint.y += SIZE * directionVector.y;
                updateRotation();
                action = Action.MOVING;
                updateVelocity();
            }
        }

        if (action == Action.MOVING) {
            float oldDistance = currentWorldPosition.dst(destinationPoint);
            currentWorldPosition.mulAdd(velocityVector, dt);
            if (currentWorldPosition.dst(destinationPoint) > oldDistance)  {
                currentWorldPosition.x = destinationPoint.x;
                currentWorldPosition.y = destinationPoint.y;
                currentMapPosition.x = (int) currentWorldPosition.x / SIZE;
                currentMapPosition.y = (int) currentWorldPosition.y / SIZE;

                if (currentMapPosition.x == -1 || currentMapPosition.x == gameMap.getMapWidht()) {
                    currentMapPosition.x = (gameMap.getMapWidht() - currentMapPosition.x) - 1;
                    currentWorldPosition.x = currentMapPosition.x * SIZE;
                    destinationPoint.x = currentWorldPosition.x + SIZE * directionVector.x;
                } else {
                    action = Action.WAITING;
                }
            }
        }
    }

    private void updateVelocity() {
        velocityVector.set(destinationPoint).sub(currentWorldPosition).nor().scl(currentSpeed);
    }

    public void setAction(Action action) {
        this.action = action;
    }

    public Action getAction() { return action; }

    protected abstract void getDirection();
    protected abstract void updateRotation();
}
