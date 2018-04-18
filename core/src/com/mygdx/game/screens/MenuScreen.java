package com.mygdx.game.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.mygdx.game.Assets;
import com.mygdx.game.GameConstants;
import com.mygdx.game.HighScoreSystem;
import com.mygdx.game.MusicManager;
import com.mygdx.game.gui.MenuGUI;

import java.util.HashMap;

public class MenuScreen implements Screen, GameConstants {
    private static final int CAMERA_SPEED = 1200;

    private SpriteBatch batch;
    private Camera camera;
    private BitmapFont font32;
    private BitmapFont font48;
    private BitmapFont font96;
    private MenuGUI menuGUI;
    private HashMap<GameObject, TextureRegion[]> textures;

    private TextureRegion[] stars;
    private TextureRegion lock;
    private TextureRegion pane;
    private int[] levelStars;

    private Vector2 firstCameraPosition;
    private Vector2 currentCameraPosition;
    private Vector2 secondCameraPosition;
    private Vector2 cameraSpeed;
    private boolean moveCamera;

    public MenuScreen(SpriteBatch batch, Camera camera) {
        this.batch = batch;
        this.camera = camera;
        this.textures = new HashMap<>();

        this.levelStars = new int[GameLevel.values().length];
        this.firstCameraPosition = new Vector2();
        this.secondCameraPosition = new Vector2();
        this.currentCameraPosition = new Vector2();
        this.cameraSpeed = new Vector2();
    }

    @Override
    public void show() {
        MusicManager.playMusic();
        this.currentCameraPosition.set(VIEWPORT_WIDTH / 2, VIEWPORT_HEIGHT / 2);
        this.moveCamera = false;
        updateCamera();

        this.font32 = Assets.getInstance().getAssetManager().get("zorque32.ttf", BitmapFont.class);
        this.font48 = Assets.getInstance().getAssetManager().get("zorque48.ttf", BitmapFont.class);
        this.font96 = Assets.getInstance().getAssetManager().get("zorque96.ttf", BitmapFont.class);
        putTexture(GameObject.EMPTY_CELL);

        pane = Assets.getInstance().getAtlas().findRegion("backTile");
        lock = Assets.getInstance().getAtlas().findRegion("lock");
        stars = Assets.getInstance().getAtlas().findRegion("smallStars").split(60, 60)[0];
        loadLevelStarts();

        this.menuGUI = new MenuGUI(this);
    }

    public void moveCameraDown() {
        firstCameraPosition.set(VIEWPORT_WIDTH / 2, VIEWPORT_HEIGHT / 2);
        secondCameraPosition.set(VIEWPORT_WIDTH / 2, SECOND_SCREEN_Y0 + VIEWPORT_HEIGHT / 2);
        this.cameraSpeed = new Vector2(0, -1 * CAMERA_SPEED);
        moveCamera = true;
    }

    public void moveCameraUp() {
        firstCameraPosition.set(VIEWPORT_WIDTH / 2, SECOND_SCREEN_Y0 + VIEWPORT_HEIGHT / 2);
        secondCameraPosition.set(VIEWPORT_WIDTH / 2, VIEWPORT_HEIGHT / 2);
        this.cameraSpeed = new Vector2(0, CAMERA_SPEED);
        moveCamera = true;
    }

    public void loadLevelStarts() {
        for (GameLevel lvl : GameLevel.values()) {
            HighScoreSystem.loadResult(lvl.getScoreFileName());
            levelStars[lvl.ordinal()] = HighScoreSystem.getMaxStars();
        }
    }

    public int[] getLevelStars() {
        return levelStars;
    }

    public SpriteBatch getBatch() {
        return batch;
    }

    private void putTexture(GameObject gameObject) {
        textures.put(gameObject, Assets.getInstance().getAtlas().findRegion(gameObject.getTextureName()).split(WORLD_CELL_PX, WORLD_CELL_PX)[gameObject.getTextureRegionIndex()]);
    }

    private TextureRegion[] getTexture(GameObject gameObject) {
        return textures.get(gameObject);
    }

    private void fillBackground() {
        for (int x = 0; x <= VIEWPORT_WIDTH - WORLD_CELL_PX; x += WORLD_CELL_PX) {
            for (int y = SECOND_SCREEN_Y0; y <= VIEWPORT_HEIGHT - WORLD_CELL_PX; y += WORLD_CELL_PX) {
                batch.draw(getTexture(GameObject.EMPTY_CELL)[0], x, y, WORLD_CELL_PX / 2, WORLD_CELL_PX / 2, WORLD_CELL_PX, WORLD_CELL_PX,1,1,0);
            }
        }
    }

    @Override
    public void render(float delta) {
        update(delta);
        Gdx.gl.glClearColor(0.4f, 0.4f, 1, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        batch.begin();
        fillBackground();
        font96.draw(batch, "Pac-Man 2018", 0, 600, VIEWPORT_WIDTH, 1, false);
        font48.draw(batch, "Arcade", 0, 480, VIEWPORT_WIDTH, 1, false);
        renderLevelPanes(batch);
        batch.end();
        menuGUI.renderStage();
    }

    private void renderLevelPanes(SpriteBatch batch) {
        int posX;
        int posY;
        int levelNumber;

        for (int y = 0; y < 2; y++) {
            for (int x = 0; x < 4; x++) {
                posX = 35 + x * 310;
                posY = SECOND_SCREEN_Y0 + VIEWPORT_HEIGHT - 190 - y * 290;

                levelNumber = x + 1 + (4 * y);

                if (levelNumber == 1 || levelStars[levelNumber - 2] > 0) {
                    drawLevelNameAtPosition(posX, posY, levelNumber);
                    drawStarsAtPosition(posX, posY, levelStars[levelNumber - 1]);
                } else {
                    drawLockAtPosition(posX, posY);
                }
            }
        }
    }

    private void drawLockAtPosition(int x, int y) {
        final int lockSize = 128;
        batch.draw(lock, x + 74, y + 28, lockSize, lockSize, lockSize, lockSize,1,1,0);
    }

    private void drawLevelNameAtPosition(int x, int y, int number) {
        font48.draw(batch, GameLevel.values()[number - 1].getLevelName(), x, y + 140, 280, 1, false);
    }

    private void drawStarsAtPosition(int x, int y, int starsCount) {
        final int starSize = 60;

        for (int i = 0; i < 5; i++) {
            batch.draw(stars[(starsCount > i?1:0)], x + 35 * i + 40, y + 25, starSize, starSize, starSize, starSize, 1, 1, 0);
        }
    }

    public void update(float dt) {
        if (moveCamera) {
            float distance = currentCameraPosition.dst(secondCameraPosition);
            currentCameraPosition.mulAdd(cameraSpeed, dt);
            if (currentCameraPosition.dst(secondCameraPosition) > distance) {
                currentCameraPosition.set(secondCameraPosition);
                moveCamera = false;
            }

            updateCamera();
        }

        menuGUI.update(dt);
    }

    private void updateCamera() {
        camera.position.set(currentCameraPosition.x, currentCameraPosition.y, 0);
        camera.update();
    }

    @Override
    public void resize(int width, int height) {
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
