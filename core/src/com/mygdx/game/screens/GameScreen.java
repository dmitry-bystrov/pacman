package com.mygdx.game.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.mygdx.game.GameMap;
import com.mygdx.game.creatures.Ghost;
import com.mygdx.game.creatures.Pacman;

import java.util.ArrayList;

public class GameScreen implements Screen {
    public static final int WORLD_CELL_PX = 80;
    public static final float BASE_SPEED = 160;
    public static final int EATABLE_GHOSTS_TIMER = 5;
    public static final int PACMAN_ATTACK_TIMER = 5;

    private SpriteBatch batch;
    private Camera camera;
    private GameMap gameMap;
    private Pacman pacMan;
    private ArrayList<Ghost> ghosts;
    private float eatableGhostsTimer;
    private float packmanAttackTimer;

    public GameScreen(SpriteBatch batch) {
        this.batch = batch;
    }

    @Override
    public void show() {
        TextureAtlas atlas = new TextureAtlas(Gdx.files.internal("pacman.pack"));
        gameMap = new GameMap(atlas);
        ghosts = new ArrayList<>();
        pacMan = new Pacman(gameMap, 8, 8, BASE_SPEED, atlas);
        ghosts.add(new Ghost(gameMap, 1, 1, BASE_SPEED, atlas, Ghost.GhostType.RED));
        ghosts.add(new Ghost(gameMap, 1, 15, BASE_SPEED, atlas, Ghost.GhostType.GREEN));
        ghosts.add(new Ghost(gameMap, 15, 1, BASE_SPEED, atlas, Ghost.GhostType.BLUE));
        ghosts.add(new Ghost(gameMap, 15, 15, BASE_SPEED, atlas, Ghost.GhostType.PURPLE));

        camera = new OrthographicCamera();
        Viewport viewport = new FitViewport(ScreenManager.VIEWPORT_WIDTH, ScreenManager.VIEWPORT_HEIGHT, camera);
        viewport.apply();

        initGameLevel();
    }

    private void initGameLevel() {
        eatableGhostsTimer = 0;
        packmanAttackTimer = 0;
        gameMap.initMap();
        pacMan.initPosition();
        for (int i = 0; i < ghosts.size(); i++) {
            ghosts.get(i).setEatable(false);
            ghosts.get(i).initPosition();
            ghosts.get(i).setTargetPosition(8,8);
        }
    }

    private void update(float dt) {

        packmanAttackTimer += dt;
        if (packmanAttackTimer >= PACMAN_ATTACK_TIMER) {
            for (int i = 0; i < ghosts.size(); i++) {
                ghosts.get(i).setTargetPosition(pacMan.getMapX(), pacMan.getMapY());
            }
            packmanAttackTimer = 0;
        }

        if (eatableGhostsTimer > 0) {
            eatableGhostsTimer -= dt;
            if (eatableGhostsTimer <= 0) {
                for (int i = 0; i < ghosts.size(); i++) {
                    ghosts.get(i).setEatable(false);
                }
            }
        }

        if (pacMan.checkFoodEating()) {
            for (int i = 0; i < ghosts.size(); i++) {
                ghosts.get(i).setEatable(true);
                eatableGhostsTimer = EATABLE_GHOSTS_TIMER;
            }
        }

        if (gameMap.getFoodCount() == 0) {
            System.out.println("Pacman WIN!");
            initGameLevel();
        }

        for (int i = 0; i < ghosts.size(); i++) {
            switch (ghosts.get(i).checkContact(pacMan.getPosition())) {
                case GHOST:
                    System.out.println("Ghost is killed!");
                    ghosts.get(i).initPosition();
                    break;
                case PACMAN:
                    System.out.println("Packman is killed!");
                    pacMan.decreaseLives();
                    if (pacMan.getLives() == 0) {
                        System.out.println("Game over!");
                        ScreenManager.getInstance().changeScreen(ScreenManager.ScreenType.MENU);
                    } else {
                        pacMan.initPosition();
                    }
            }

            ghosts.get(i).update(dt);
        }
        pacMan.update(dt);

        camera.position.set(pacMan.getCX(), pacMan.getCY(), 0);
        if (camera.position.x < ScreenManager.VIEWPORT_WIDTH / 2) {
            camera.position.x = ScreenManager.VIEWPORT_WIDTH / 2;
        }
        if (camera.position.y < ScreenManager.VIEWPORT_HEIGHT / 2) {
            camera.position.y = ScreenManager.VIEWPORT_HEIGHT / 2;
        }
        if (camera.position.x > GameMap.WORLD_CELLS_SIZE * WORLD_CELL_PX - ScreenManager.VIEWPORT_WIDTH / 2) {
            camera.position.x = GameMap.WORLD_CELLS_SIZE * WORLD_CELL_PX - ScreenManager.VIEWPORT_WIDTH / 2;
        }
        if (camera.position.y > GameMap.WORLD_CELLS_SIZE * WORLD_CELL_PX - ScreenManager.VIEWPORT_HEIGHT / 2) {
            camera.position.y = GameMap.WORLD_CELLS_SIZE * WORLD_CELL_PX - ScreenManager.VIEWPORT_HEIGHT / 2;
        }
        camera.update();

        if (Gdx.input.justTouched() && Gdx.input.getY() < 50) {
            ScreenManager.getInstance().changeScreen(ScreenManager.ScreenType.MENU);
        }
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
        for (int i = 0; i < ghosts.size(); i++) {
            ghosts.get(i).render(batch);
        }
        batch.end();
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
