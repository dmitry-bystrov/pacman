package com.mygdx.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;

public class Joystick implements GameConstants {
    private static final int POS_X = 160;
    private static final int POS_Y = 160;
    private static final int BIG_RADIUS = 160;
    private static final int SMALL_RADIUS = 80;

    private Vector2 centerPosition;
    private Vector2 currentPosition;
    private Vector2 directionVector;

    private TextureRegion bigCircle;
    private TextureRegion smallCircle;

    public Joystick() {
        bigCircle = Assets.getInstance().getAtlas().findRegion("jbig");
        smallCircle = Assets.getInstance().getAtlas().findRegion("jsmall");

        reset();
    }

    public void reset() {
        centerPosition = new Vector2(POS_X, POS_Y);
        currentPosition = new Vector2(POS_X, POS_Y);
        directionVector = new Vector2(POS_X, POS_Y);
    }

    public Direction getDirection() {
        if (currentPosition.dst(centerPosition) > 0.001) {
            float angle = directionVector.angle();
            if (angle > 45 && angle <= 135) return Direction.UP;
            if (angle > 135 && angle <= 225) return Direction.LEFT;
            if (angle > 225 && angle <= 315) return Direction.DOWN;
            if ((angle > 315 && angle <= 360) || (angle > 0 && angle <= 45)) return Direction.RIGHT;
        }

        return null;
    }

    public void update(float dt) {
        if (Gdx.input.isTouched()) {
            float x = Gdx.input.getX();
            float y = VIEWPORT_HEIGHT - Gdx.input.getY();
            if (centerPosition.dst(x, y) > BIG_RADIUS * 2) return;
            currentPosition.set(x, y);
            directionVector.set(x, y).sub(centerPosition);

            if (centerPosition.dst(currentPosition) > SMALL_RADIUS - 5) {
                currentPosition.sub(centerPosition).nor().scl(SMALL_RADIUS - 5).add(centerPosition);
            }
        }
    }

    public void render(SpriteBatch batch) {
        batch.draw(bigCircle, centerPosition.x - BIG_RADIUS, centerPosition.y - BIG_RADIUS);
        batch.draw(smallCircle, currentPosition.x - SMALL_RADIUS, currentPosition.y - SMALL_RADIUS);
    }
}
