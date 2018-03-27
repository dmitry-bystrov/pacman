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
    private float recoveryTimer;
    private static Texture healthBarFill;
    private static Texture healthBarBorder;


    public Creature(GameMap gameMap, GameObject gameObject) {
        this.gameMap = gameMap;
        this.gameObject = gameObject;
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
            Pixmap pixmap = new Pixmap(SIZE, 10, Pixmap.Format.RGB888);
            pixmap.setColor(Color.GREEN);
            pixmap.fill();
            healthBarFill = new Texture(pixmap);
        }
        if (healthBarBorder == null) {
            Pixmap pixmap = new Pixmap(SIZE, 10, Pixmap.Format.RGBA8888);
            pixmap.setColor(Color.GREEN);
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
        batch.draw(textureRegions[getCurrentFrame()], currentWorldPosition.x, currentWorldPosition.y, HALF_SIZE, HALF_SIZE, SIZE, SIZE, 1, 1, rotation);
        if (action == Action.RECOVERING) {
            batch.draw(healthBarFill, currentWorldPosition.x, currentWorldPosition.y + 90, 80 * (recoveryTimer / GHOST_RECOVERY_TIMER), 10);
            batch.draw(healthBarBorder, currentWorldPosition.x, currentWorldPosition.y + 90, 80, 10);
        }
    }

    public void update(float dt) {
        if (action == Action.RECOVERING) {
            recoveryTimer += dt;
            if (recoveryTimer > GHOST_RECOVERY_TIMER) {
                action = Action.WAITING;
                recoveryTimer = 0;
            }
            return;
        }

        animationTimer += dt;
        if (animationTimer >= textureRegions.length * secPerFrame) {
            animationTimer = 0.0f;
        }

        if (action == Action.WAITING)
        {
            getDirection();
            if (directionVector.len() != 0) {
                destinationPoint.x += WORLD_CELL_PX * directionVector.x;
                destinationPoint.y += WORLD_CELL_PX * directionVector.y;
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
                action = Action.WAITING;
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
