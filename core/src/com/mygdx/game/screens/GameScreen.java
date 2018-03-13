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
    public static final float PACMAN_SPEED = 160;
    public static final int EATABLE_GHOSTS_TIMER = 5;

    private SpriteBatch batch;
    private Camera camera;
    private Viewport viewport;
    private GameMap gameMap;
    private Pacman pacMan;
    private ArrayList<Ghost> ghosts;
    private TextureAtlas atlas;
    private float eatableGhostsTimer;

    public GameScreen(SpriteBatch batch) {
        this.batch = batch;
    }

    @Override
    public void show() {
        atlas = new TextureAtlas(Gdx.files.internal("pacman.pack"));
        gameMap = new GameMap(atlas);
        ghosts = new ArrayList<>();
        pacMan = new Pacman(gameMap, 1, 1, PACMAN_SPEED, atlas);
        ghosts.add(new Ghost(gameMap, pacMan, 7, 7, PACMAN_SPEED, atlas, Ghost.GhostType.RED));
        ghosts.add(new Ghost(gameMap, pacMan, 7, 7, PACMAN_SPEED, atlas, Ghost.GhostType.RED));
        ghosts.add(new Ghost(gameMap, pacMan, 7, 7, PACMAN_SPEED, atlas, Ghost.GhostType.GREEN));
        ghosts.add(new Ghost(gameMap, pacMan, 7, 7, PACMAN_SPEED, atlas, Ghost.GhostType.GREEN));
        ghosts.add(new Ghost(gameMap, pacMan, 7, 7, PACMAN_SPEED, atlas, Ghost.GhostType.BLUE));
        ghosts.add(new Ghost(gameMap, pacMan, 7, 7, PACMAN_SPEED, atlas, Ghost.GhostType.BLUE));
        ghosts.add(new Ghost(gameMap, pacMan, 7, 7, PACMAN_SPEED, atlas, Ghost.GhostType.PURPLE));
        ghosts.add(new Ghost(gameMap, pacMan, 7, 7, PACMAN_SPEED, atlas, Ghost.GhostType.PURPLE));

        camera = new OrthographicCamera();
        viewport = new FitViewport(ScreenManager.VIEWPORT_WIDTH, ScreenManager.VIEWPORT_HEIGHT, camera);
        viewport.apply();

        startNewGame();
    }

    public void startNewGame() {
        eatableGhostsTimer = 0;
        gameMap.init();
        pacMan.init();
        for (int i = 0; i < ghosts.size(); i++) {
            ghosts.get(i).setEatable(false);
            ghosts.get(i).init();
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

    public void update(float dt) {

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
            startNewGame();
        }

        pacMan.update(dt);
        for (int i = 0; i < ghosts.size(); i++) {
            ghosts.get(i).update(dt);
        }

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
