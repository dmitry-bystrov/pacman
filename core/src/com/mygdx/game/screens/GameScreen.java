package com.mygdx.game.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.mygdx.game.Assets;
import com.mygdx.game.GameMap;
import com.mygdx.game.creatures.Ghost;
import com.mygdx.game.creatures.Pacman;

public class GameScreen implements Screen {
    public static final int WORLD_CELL_PX = 80;
    public static final float BASE_SPEED = 200;
    private static final int EATABLE_GHOSTS_TIMER = 5;
    private static final int PACMAN_ATTACK_TIMER = 5;

    private SpriteBatch batch;
    private Camera camera;
    private GameMap gameMap;
    private Pacman pacMan;
    private Ghost[] ghosts;
    private BitmapFont font48;
    private float eatableGhostsTimer;
    private float packmanAttackTimer;
    private StringBuilder guiHelper;

    public GameScreen(SpriteBatch batch, Camera camera) {
        this.batch = batch;
        this.camera = camera;
        this.guiHelper = new StringBuilder(100);
    }

    @Override
    public void show() {
        font48 = Assets.getInstance().getAssetManager().get("zorque48.ttf");
        gameMap = new GameMap();
        pacMan = new Pacman(gameMap);
        ghosts = new Ghost[4];
        ghosts[0] = new Ghost(gameMap, Ghost.GhostType.RED);
        ghosts[1] = new Ghost(gameMap, Ghost.GhostType.GREEN);
        ghosts[2] = new Ghost(gameMap, Ghost.GhostType.BLUE);
        ghosts[3] = new Ghost(gameMap, Ghost.GhostType.PURPLE);
        resetCamera();
        initGameLevel();
    }

    private void initGameLevel() {
        eatableGhostsTimer = 0;
        packmanAttackTimer = 0;
        gameMap.initMap();
        pacMan.initPosition();
        for (int i = 0; i < ghosts.length; i++) {
            ghosts[i].setEatable(false);
            ghosts[i].initPosition();
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
        for (int i = 0; i < ghosts.length; i++) {
            ghosts[i].render(batch);
        }
        renderGUI(batch, font48);
        batch.end();
    }

    public void renderGUI(SpriteBatch batch, BitmapFont font) {
        guiHelper.setLength(0);
        guiHelper.append("Lives: ").append(pacMan.getLives()).append("\nScore: ").append(pacMan.getScore());

        resetCamera();
        batch.setProjectionMatrix(camera.combined);
        font.draw(batch, guiHelper, 20, 700);
    }

    public void resetCamera() {
        camera.position.set(ScreenManager.VIEWPORT_WIDTH / 2, ScreenManager.VIEWPORT_HEIGHT / 2, 0);
        camera.update();
    }

    private void update(float dt) {

        packmanAttackTimer += dt;
        if (packmanAttackTimer >= PACMAN_ATTACK_TIMER) {
            for (int i = 0; i < ghosts.length; i++) {
                ghosts[i].setTargetCell(pacMan.getCurrentMapPosition());
            }
            packmanAttackTimer = 0;
        }

        if (eatableGhostsTimer > 0) {
            eatableGhostsTimer -= dt;
            if (eatableGhostsTimer <= 0) {
                for (int i = 0; i < ghosts.length; i++) {
                    ghosts[i].setEatable(false);
                }
            }
        }

        if (pacMan.checkFoodEating()) {
            for (int i = 0; i < ghosts.length; i++) {
                ghosts[i].setEatable(true);
                eatableGhostsTimer = EATABLE_GHOSTS_TIMER;
            }
        }

        if (gameMap.getFoodCount() == 0) {
            System.out.println("Pacman WIN!");
            initGameLevel();
        }

        for (int i = 0; i < ghosts.length; i++) {
            switch (ghosts[i].checkContact(pacMan.getCurrentWorldPosition())) {
                case GHOST:
                    System.out.println("Ghost is killed!");
                    ghosts[i].initPosition();
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

            ghosts[i].update(dt);
        }
        pacMan.update(dt);
        cameraTrackPackman();

        if (Gdx.input.justTouched() && Gdx.input.getY() < 50) {
            ScreenManager.getInstance().changeScreen(ScreenManager.ScreenType.MENU);
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
        if (camera.position.x > gameMap.getMapWidht() - ScreenManager.VIEWPORT_WIDTH / 2) {
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
