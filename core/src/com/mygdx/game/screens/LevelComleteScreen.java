package com.mygdx.game.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.mygdx.game.*;
import com.mygdx.game.gui.LevelCompleteGUI;

import java.util.*;

public class LevelComleteScreen implements Screen, GameConstants {
    private static final float STATS_DELAY = 0.75f;
    private static final int MAX_STATS_COUNT = 9;
    private static final float SCORE_DELAY = 0.05f;
    private static final int CAMERA_SPEED = 600;

    private SpriteBatch batch;
    private Camera camera;
    private boolean gamePaused;

    private BitmapFont font32;
    private BitmapFont font48;
    private BitmapFont font96;

    private StringBuilder stringBuilder;
    private HashMap<GameObject, TextureRegion[]> textures;
    private LinkedHashMap<GameObject, Integer> gameStats;

    private float statsDelay;
    private float scoreDelay;
    private int statsCount;
    private int statsCounted;
    private int starsCount;
    private int totalScore;
    private int totalScoreToDraw;
    private LevelCompleteGUI levelCompleteGUI;

    private Vector2 firstCameraPosition;
    private Vector2 currentCameraPosition;
    private Vector2 secondCameraPosition;
    private Vector2 cameraSpeed;
    private boolean moveCamera;

    public LevelComleteScreen(SpriteBatch batch, Camera camera) {
        this.batch = batch;
        this.camera = camera;
        this.textures = new HashMap<>();
        this.gameStats = new LinkedHashMap<>();
        this.stringBuilder = new StringBuilder(100);
        this.firstCameraPosition = new Vector2(VIEWPORT_WIDTH / 2, VIEWPORT_HEIGHT / 2);
        this.currentCameraPosition = firstCameraPosition.cpy();
        this.secondCameraPosition = new Vector2(VIEWPORT_WIDTH / 2, SECOND_SCREEN_Y0 + VIEWPORT_HEIGHT / 2);
        this.cameraSpeed = new Vector2(0, -1 * CAMERA_SPEED);
    }

    @Override
    public void show() {
        MusicManager.playMusic();
        this.currentCameraPosition.set(firstCameraPosition);
        this.moveCamera = true;
        this.gamePaused = false;
        updateCamera();

        this.font32 = Assets.getInstance().getAssetManager().get("zorque32.ttf");
        this.font48 = Assets.getInstance().getAssetManager().get("zorque48.ttf");
        this.font96 = Assets.getInstance().getAssetManager().get("zorque96.ttf");

        for (Map.Entry<GameObject, Integer> entry : gameStats.entrySet()) {
            putTexture(entry.getKey());
        }

        putTexture(GameObject.EMPTY_CELL);
        putTexture(GameObject.STAR);
        putTexture(GameObject.PIPE);

        loadTopScores();
        this.levelCompleteGUI = new LevelCompleteGUI(this);

        this.statsDelay = 0;
        this.scoreDelay = 0;
        this.statsCount = 0;
        this.statsCounted = 0;
        this.starsCount = 0;
        this.totalScore = 0;
        this.totalScoreToDraw = 0;
    }

    public SpriteBatch getBatch() {
        return batch;
    }

    public int getStarsCount() {
        return starsCount;
    }

    public int getTotalScore() {
        return totalScore;
    }

    private void loadTopScores() {
        HighScoreSystem.loadResult(ScreenManager.getInstance().getGameLevel().getScoreFileName());
    }

    public void setGameStats(LinkedHashMap<GameObject, Integer> gameStats) {
        this.gameStats.clear();
        this.gameStats.putAll(gameStats);
    }

    private void putTexture(GameObject gameObject) {
        int textureIndex = -1;
        int textureSize = 1;

        if (gameObject == GameObject.PIPE) {
            textureIndex = 1010;
        }

        if (gameObject == GameObject.STAR) {
            textureSize = 2;
        }

        textures.put(gameObject, Assets.getInstance().getAtlas().findRegion(gameObject.getTextureName(), textureIndex).split(WORLD_CELL_PX * textureSize, WORLD_CELL_PX * textureSize)[gameObject.getTextureRegionIndex()]);
    }

    private TextureRegion[] getTexture(GameObject gameObject) {
        return textures.get(gameObject);
    }

    private void fillBackground() {
        for (int x = 0; x <= VIEWPORT_WIDTH - WORLD_CELL_PX; x += WORLD_CELL_PX) {
            for (int y = SECOND_SCREEN_Y0; y <= VIEWPORT_HEIGHT - WORLD_CELL_PX; y += WORLD_CELL_PX) {
                batch.draw(getTexture(GameObject.EMPTY_CELL)[0], x, y, WORLD_CELL_PX / 2, WORLD_CELL_PX / 2, WORLD_CELL_PX, WORLD_CELL_PX,1,1,0);
                if (x == 0 || x == VIEWPORT_WIDTH - WORLD_CELL_PX) {
                    batch.draw(getTexture(GameObject.PIPE)[0], x, y, WORLD_CELL_PX / 2, WORLD_CELL_PX / 2, WORLD_CELL_PX, WORLD_CELL_PX,1,1,0);
                }
            }
        }
    }

    public void update(float dt) {
        if (gamePaused) return;

        if (totalScore > totalScoreToDraw) {
            scoreDelay += dt;
            if (scoreDelay >= SCORE_DELAY) {
                scoreDelay = 0;
                int increment = 1;
                if (totalScore - totalScoreToDraw > 5) increment = 5;
                if (totalScore - totalScoreToDraw > 50) increment = 10;
                if (totalScore - totalScoreToDraw > 500) increment = 100;
                if (totalScore - totalScoreToDraw > 5000) increment = 1000;
                totalScoreToDraw += increment;
                SoundManager.playSound(GameSound.COIN);

                int old_starsCount = starsCount;
                starsCount = totalScoreToDraw / (gameStats.get(GameObject.FOOD) * GameObject.FOOD.getScore());
                if (starsCount > 5) starsCount = 5;
                if (old_starsCount < starsCount) {
                    SoundManager.playSound(GameSound.STAR);
                }
            }
        }

        if (statsCount < MAX_STATS_COUNT) {
            statsDelay += dt;
            if (statsDelay >= STATS_DELAY) {
                statsDelay = 0;
                statsCount++;
            }
        }

        if (statsCount == MAX_STATS_COUNT && totalScore == totalScoreToDraw && moveCamera) {
            float distance = currentCameraPosition.dst(secondCameraPosition);
            currentCameraPosition.mulAdd(cameraSpeed, dt);
            if (currentCameraPosition.dst(secondCameraPosition) > distance) {
                currentCameraPosition.set(secondCameraPosition);
                moveCamera = false;
            }

            updateCamera();
        }

        levelCompleteGUI.update(dt);
    }

    private void updateCamera() {
        camera.position.set(currentCameraPosition.x, currentCameraPosition.y, 0);
        camera.update();
    }

    private void drawStats() {
        font96.draw(batch, "Level Complete", 0, 670, VIEWPORT_WIDTH, 1, false);
        stringBuilder.setLength(0);
        stringBuilder.append("Total score: ").append(totalScoreToDraw);
        font48.draw(batch, stringBuilder, 420, 370, 0, -1, false);

        for (int i = 0; i < 5; i++) {
            batch.draw(getTexture(GameObject.STAR)[(starsCount > i?1:0)], 200 + 180 * i, 420, WORLD_CELL_PX * 2, WORLD_CELL_PX * 2, WORLD_CELL_PX * 2, WORLD_CELL_PX * 2, 1, 1, 0);
        }

        int xLine = 0;
        int yLine = 0;
        int count = 0;

        for (Map.Entry<GameObject, Integer> entry : gameStats.entrySet()) {

            if (count > statsCount) break;
            count++;

            if (statsCounted < count) {
                totalScore += entry.getKey().getScore() * entry.getValue();
                if (totalScore > HighScoreSystem.getMinScore()) {
                    levelCompleteGUI.showFlowInputPanel();
                }

                statsCounted++;
            }

            float imageX = 140 + xLine * 350;
            float imageY = 250 - yLine * (WORLD_CELL_PX + 15);
            float textX = 140 + xLine * 350 + WORLD_CELL_PX + 5;
            float textY = 250 - yLine * (WORLD_CELL_PX + 15) + WORLD_CELL_PX / 2;

            batch.draw(getTexture(entry.getKey())[0], imageX, imageY, WORLD_CELL_PX / 2, WORLD_CELL_PX / 2, WORLD_CELL_PX, WORLD_CELL_PX, 1, 1, 0);
            stringBuilder.setLength(0);
            stringBuilder.append(": ").append(entry.getValue()).append("x").append(entry.getKey().getScore());
            font48.draw(batch, stringBuilder, textX, textY, 0, -1, false);

            xLine++;
            if (xLine == 3) {
                xLine = 0;
                yLine++;
            }
        }

        font48.draw(batch, "Top Players", 0, SECOND_SCREEN_Y0 + 630, VIEWPORT_WIDTH, 1, false);
        font32.draw(batch, HighScoreSystem.getListNumbersColumn(), 360, SECOND_SCREEN_Y0 + 560, 40, 0, false);
        font32.draw(batch, HighScoreSystem.getListPlayersColumn(), 420, SECOND_SCREEN_Y0 + 560, 440, -1, false);
        font32.draw(batch, HighScoreSystem.getListScoresColumn(), 860, SECOND_SCREEN_Y0 + 560, VIEWPORT_WIDTH - 860, -1, false);
    }

    @Override
    public void render(float delta) {
        update(delta);
        Gdx.gl.glClearColor(0.4f, 0.4f, 1, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        batch.begin();
        fillBackground();
        drawStats();
        batch.end();
        levelCompleteGUI.renderStage();
    }

    @Override
    public void resize(int width, int height) {
        ScreenManager.getInstance().resize(width, height);
    }

    @Override
    public void pause() {
        gamePaused = true;
    }

    @Override
    public void resume() {
        gamePaused = false;
    }

    @Override
    public void hide() {

    }

    @Override
    public void dispose() {

    }

}
