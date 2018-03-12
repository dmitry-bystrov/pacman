package com.mygdx.game;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;

import java.util.ArrayList;

public class MyGdxGame extends ApplicationAdapter {

    public static final float PACMAN_SPEED = 120;
    public static final int VIEWPORT_WIDTH = 1280;
    public static final int VIEWPORT_HEIGHT = 720;

    SpriteBatch batch;
    GameMap gameMap;
    Viewport viewport;
    Camera camera;

    Creature pacMan;
    ArrayList<Creature> creatures;

    @Override
    public void create() {
        batch = new SpriteBatch();
        gameMap = new GameMap();
        creatures = new ArrayList<>();
        pacMan = new Pacman(gameMap, 1, 1, PACMAN_SPEED, "pacman.png");
        creatures.add(new Ghost(gameMap, 7, 7, PACMAN_SPEED * 1.1f, "purple_ghost.png"));
        creatures.add(new Ghost(gameMap, 7, 7, PACMAN_SPEED * 1.1f, "purple_ghost.png"));
        creatures.add(new Ghost(gameMap, 7, 7, PACMAN_SPEED * 1.2f, "blue_ghost.png"));
        creatures.add(new Ghost(gameMap, 7, 7, PACMAN_SPEED * 1.2f, "blue_ghost.png"));
        creatures.add(new Ghost(gameMap, 7, 7, PACMAN_SPEED * 1.3f, "green_ghost.png"));
        creatures.add(new Ghost(gameMap, 7, 7, PACMAN_SPEED * 1.3f, "green_ghost.png"));
        creatures.add(new Ghost(gameMap, 7, 7, PACMAN_SPEED * 1.4f, "red_ghost.png"));
        creatures.add(new Ghost(gameMap, 7, 7, PACMAN_SPEED * 1.4f, "red_ghost.png"));
        creatures.add(pacMan);

        camera = new OrthographicCamera();
        viewport = new FitViewport(VIEWPORT_WIDTH, VIEWPORT_HEIGHT, camera);
        viewport.apply();
    }

    @Override
    public void render() {
        float dt = Gdx.graphics.getDeltaTime();
        update(dt);

        Gdx.gl.glClearColor(1, 1, 1, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        batch.setProjectionMatrix(camera.combined);

        batch.begin();
        gameMap.render(batch);
        for (int i = 0; i < creatures.size(); i++) {
            creatures.get(i).render(batch);
        }
        batch.end();
    }

    public void update(float dt) {
        for (int i = 0; i < creatures.size(); i++) {
            creatures.get(i).update(dt);
        }
        camera.position.set(pacMan.getCX(), pacMan.getCY(), 0);
        if (camera.position.x < VIEWPORT_WIDTH / 2) {
            camera.position.x = VIEWPORT_WIDTH / 2;
        }
        if (camera.position.y < VIEWPORT_HEIGHT / 2) {
            camera.position.y = VIEWPORT_HEIGHT / 2;
        }
        if (camera.position.x > GameMap.WORLD_CELLS_SIZE * GameMap.CELL_SIZE_PX - VIEWPORT_WIDTH / 2) {
            camera.position.x = GameMap.WORLD_CELLS_SIZE * GameMap.CELL_SIZE_PX - VIEWPORT_WIDTH / 2;
        }
        if (camera.position.y > GameMap.WORLD_CELLS_SIZE * GameMap.CELL_SIZE_PX - VIEWPORT_HEIGHT / 2) {
            camera.position.y = GameMap.WORLD_CELLS_SIZE * GameMap.CELL_SIZE_PX - VIEWPORT_HEIGHT / 2;
        }
        camera.update();
    }

    @Override
    public void dispose() {
        batch.dispose();
    }

    @Override
    public void resize(int width, int height) {
        viewport.update(width, height);
        viewport.apply();
    }
}
