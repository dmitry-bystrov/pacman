package com.mygdx.game.screens;

import com.badlogic.gdx.Application;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.*;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.mygdx.game.GameConstants;
import com.mygdx.game.GameLevel;
import com.mygdx.game.creatures.Pacman;

import java.util.LinkedHashMap;

public class GameScreen implements Screen, GameConstants {
    private SpriteBatch batch;
    private Camera camera;
    private GameLevel gameLevel;
    private SimpleGUI simpleGUI;
    private float cameraZoom;
    private int levelNumber;
    private boolean gamePaused;

    public GameScreen(SpriteBatch batch, Camera camera) {
        this.levelNumber = 1;
        this.batch = batch;
        this.camera = camera;
    }

    @Override
    public void show() {
        this.gameLevel = new GameLevel(Difficulty.EXPERT);
        this.simpleGUI = new SimpleGUI(this);

        this.cameraZoom = 1;
        this.gamePaused = false;

        this.resetCamera();
        this.gameLevel.startNewLevel(levelNumber);
    }

    public void restartGameLevel() {
    }

    public void setGamePaused(boolean gamePaused) {
        this.gamePaused = gamePaused;
    }

    public SpriteBatch getBatch() {
        return batch;
    }

    public GameLevel getGameLevel() {
        return gameLevel;
    }

    public LinkedHashMap<GameObject, Integer> getGameStats() {
        return gameLevel.getPacMan().getStats();
    }

    @Override
    public void render(float delta) {
        update(delta);
        Gdx.gl.glClearColor(0.2f, 0.2f, 0.2f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        batch.setProjectionMatrix(camera.combined);

        batch.begin();
        gameLevel.render(batch);

        resetCamera();
        batch.setProjectionMatrix(camera.combined);
        simpleGUI.renderStats();
        batch.end();
        simpleGUI.renderStage();
    }

    private void resetCamera() {
        camera.position.set(VIEWPORT_WIDTH / 2, VIEWPORT_HEIGHT / 2, 0);
        ((OrthographicCamera)camera).zoom = 1;
        camera.update();
    }

    private void update(float dt) {

        if (Gdx.app.getType() == Application.ApplicationType.Desktop) {
            if (Gdx.input.isKeyPressed(Input.Keys.NUMPAD_9)) {
                cameraZoom -= 0.4f * dt;
            }

            if (Gdx.input.isKeyPressed(Input.Keys.NUMPAD_6)) {
                cameraZoom += 0.4f * dt;
            }
        }

        cameraTrackPackman(gameLevel.getPacMan());

        if (!gamePaused) {
            gameLevel.update(dt);

            if (gameLevel.getFoodCount() == 0) {
                ScreenManager.getInstance().changeScreen(ScreenType.LEVEL_COMPLETE);
            }
        }

        if (gameLevel.getPacMan().getLives() <= 0) {
            simpleGUI.showGameOverPanel();
        }

        simpleGUI.update(dt);
    }

    private void cameraTrackPackman(Pacman pacMan) {
        camera.position.set(pacMan.getCurrentWorldPosition().x + pacMan.HALF_SIZE,
                pacMan.getCurrentWorldPosition().y + pacMan.HALF_SIZE, 0);
        if (camera.position.x < VIEWPORT_WIDTH / 2) {
            camera.position.x = VIEWPORT_WIDTH / 2;
        }
        if (camera.position.y < VIEWPORT_HEIGHT / 2) {
            camera.position.y = VIEWPORT_HEIGHT / 2;
        }
        if (camera.position.x > gameLevel.getMapWidht() * WORLD_CELL_PX - VIEWPORT_WIDTH / 2) {
            camera.position.x = gameLevel.getMapWidht() * WORLD_CELL_PX - VIEWPORT_WIDTH / 2;
        }
        if (camera.position.y > gameLevel.getMapHeight() * WORLD_CELL_PX - VIEWPORT_HEIGHT / 2) {
            camera.position.y = gameLevel.getMapHeight() * WORLD_CELL_PX - VIEWPORT_HEIGHT / 2;
        }

        ((OrthographicCamera)camera).zoom = cameraZoom;
        camera.update();
    }

    @Override
    public void resize(int width, int height)
    {
        ScreenManager.getInstance().resize(width, height);
    }

    @Override
    public void pause() {

    }

    @Override
    public void resume() {

    }

    @Override
    public void hide() {

    }

    @Override
    public void dispose() {

    }
}
