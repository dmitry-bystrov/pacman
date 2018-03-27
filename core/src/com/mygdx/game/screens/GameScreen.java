package com.mygdx.game.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.mygdx.game.Assets;
import com.mygdx.game.GameConstants;
import com.mygdx.game.GameMap;
import com.mygdx.game.creatures.Ghost;
import com.mygdx.game.creatures.Pacman;

import java.util.LinkedHashMap;

public class GameScreen implements Screen, GameConstants {
    private SpriteBatch batch;
    private Camera camera;
    private GameMap gameMap;
    private Pacman pacMan;
    private Ghost[] ghosts;
    private BitmapFont font48;
    private boolean ghostsEatable;
    private boolean levelComplete;
    private float eatableGhostsTimer;
    private float packmanAttackTimer;
    private float levelCompleteTimer;
    private StringBuilder guiHelper;
    private int level;

    public GameScreen(SpriteBatch batch, Camera camera) {
        this.batch = batch;
        this.camera = camera;
        this.guiHelper = new StringBuilder(100);
    }

    @Override
    public void show() {
        level = 0;
        gameMap = new GameMap();
        pacMan = new Pacman(gameMap);
        ghosts = new Ghost[4];
        ghosts[0] = new Ghost(gameMap, GameObject.RED_GHOST);
        ghosts[1] = new Ghost(gameMap, GameObject.GREEN_GHOST);
        ghosts[2] = new Ghost(gameMap, GameObject.BLUE_GHOST);
        ghosts[3] = new Ghost(gameMap, GameObject.PURPLE_GHOST);
        resetCamera();
        initGameLevel();
        pacMan.initStats();
        font48 = Assets.getInstance().getAssetManager().get("zorque48.ttf");
    }

    private void initGameLevel() {
        level++;
        levelComplete = false;
        levelCompleteTimer = 0;
        ghostsEatable = false;
        eatableGhostsTimer = 0;
        packmanAttackTimer = 0;
        gameMap.initMap();
        pacMan.initPosition();
        for (int i = 0; i < ghosts.length; i++) {
            ghosts[i].initPosition();
            ghosts[i].setEatable(ghostsEatable);
        }
    }

    public LinkedHashMap<GameObject, Integer> getGameStats() {
        return pacMan.getStats();
    }

    public int getLevel() {
        return level;
    }

    @Override
    public void render(float delta) {
        update(delta);
        Gdx.gl.glClearColor(1, 1, 1, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        batch.setProjectionMatrix(camera.combined);

        batch.begin();
        gameMap.render(batch);
        pacMan.render(batch);
        for (int i = 0; i < ghosts.length; i++) {
            ghosts[i].render(batch);
        }
        renderGUI(batch);
        batch.end();
    }

    public void renderGUI(SpriteBatch batch) {
        resetCamera();
        batch.setProjectionMatrix(camera.combined);
        guiHelper.setLength(0);
        guiHelper.append("Level: ").append(level).append("\nLives: ").append(pacMan.getLives()).append("\nScore: ").append(pacMan.getScore());
        font48.draw(batch, guiHelper, 20, 700);
        if (levelComplete) {
            font48.draw(batch, "Level Complete", 0, 600, VIEWPORT_WIDTH, 1, false);
        }
    }

    public void resetCamera() {
        camera.position.set(VIEWPORT_WIDTH / 2, VIEWPORT_HEIGHT / 2, 0);
        camera.update();
    }

    private void updateGhostsTargetCell(float dt) {
        packmanAttackTimer += dt;
        if (packmanAttackTimer >= PACMAN_ATTACK_TIMER) {
            for (int i = 0; i < ghosts.length; i++) {
                if (pacMan.getAction() != Action.RECOVERING) {
                    ghosts[i].setTargetCell(pacMan.getCurrentMapPosition());
                } else {
                    ghosts[i].setTargetCell(gameMap.getStartPosition(ghosts[i].getGameObject()));
                }
            }
            packmanAttackTimer = 0;
        }
    }

    private void updatePacmanBeastModeState(float dt) {
        if (ghostsEatable) {
            eatableGhostsTimer -= dt;
            if (eatableGhostsTimer <= 0) {
                ghostsEatable = false;
                for (int i = 0; i < ghosts.length; i++) {
                    ghosts[i].setEatable(ghostsEatable);
                }
            }
        }

        if (pacMan.getAction() == Action.WAITING) {
            if (pacMan.checkFoodEating()) {
                ghostsEatable = true;
                eatableGhostsTimer = EATABLE_GHOSTS_TIMER;
                for (int i = 0; i < ghosts.length; i++) {
                    ghosts[i].setEatable(ghostsEatable);
                }
            }
        }
    }

    private void updateContacts(float dt) {
        if (pacMan.getAction() == Action.RECOVERING) return;
        for (int i = 0; i < ghosts.length; i++) {
            if (ghosts[i].getAction() == Action.RECOVERING) continue;
            if (pacMan.getCurrentWorldPosition().dst(ghosts[i].getCurrentWorldPosition()) < pacMan.HALF_SIZE) {
                if (ghostsEatable) {
                    pacMan.eatObject(ghosts[i].getGameObject());
                    ghosts[i].respawn();
                } else {
                    pacMan.decreaseLives();
                    pacMan.respawn();
                    if (pacMan.getLives() == 0) {
                        ScreenManager.getInstance().changeScreen(ScreenType.GAME_OVER);
                    }
                }
            }
        }
    }

    private void update(float dt) {
        cameraTrackPackman();
        if (Gdx.input.justTouched() && Gdx.input.getY() < 50) {
            ScreenManager.getInstance().changeScreen(ScreenManager.ScreenType.MENU);
        }

        if (levelComplete) {
            levelCompleteTimer += dt;
            if (levelCompleteTimer > 1) {
                initGameLevel();
            }
            return;
        }

        //if (true) return;

        updateContacts(dt);
        updateGhostsTargetCell(dt);
        updatePacmanBeastModeState(dt);

        for (int i = 0; i < ghosts.length; i++) {
            ghosts[i].update(dt);
        }

        pacMan.update(dt);
        if (gameMap.getFoodCount() == 0) {
            levelComplete = true;
        }
    }

    private void cameraTrackPackman() {
        camera.position.set(pacMan.getCurrentWorldPosition().x + pacMan.HALF_SIZE,
                pacMan.getCurrentWorldPosition().y + pacMan.HALF_SIZE, 0);
        if (camera.position.x < ScreenManager.VIEWPORT_WIDTH / 2) {
            camera.position.x = ScreenManager.VIEWPORT_WIDTH / 2;
        }
        if (camera.position.y < ScreenManager.VIEWPORT_HEIGHT / 2) {
            camera.position.y = ScreenManager.VIEWPORT_HEIGHT / 2;
        }
        if (camera.position.x > gameMap.getMapWidht() * WORLD_CELL_PX - ScreenManager.VIEWPORT_WIDTH / 2) {
            camera.position.x = gameMap.getMapWidht() * WORLD_CELL_PX - ScreenManager.VIEWPORT_WIDTH / 2;
        }
        if (camera.position.y > gameMap.getMapHeight() * WORLD_CELL_PX - ScreenManager.VIEWPORT_HEIGHT / 2) {
            camera.position.y = gameMap.getMapHeight() * WORLD_CELL_PX - ScreenManager.VIEWPORT_HEIGHT / 2;
        }
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
