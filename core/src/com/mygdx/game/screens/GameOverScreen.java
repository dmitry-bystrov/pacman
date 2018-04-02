package com.mygdx.game.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.TextField;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.mygdx.game.Assets;
import com.mygdx.game.GameConstants;
import com.mygdx.game.HighScoreSystem;

import java.util.*;

public class GameOverScreen implements Screen, GameConstants {
    private static final float STATS_DELAY = 0.75f;
    private static final int MAX_STATS_COUNT = 9;
    private static final float SCORE_DELAY = 0.05f;
    private static final int SECOND_SCREEN_Y0 = 0 - VIEWPORT_HEIGHT;
    private static final int CAMERA_SPEED = 250;
    private static final int TOP_LIST_SIZE = 10;
    private static final String TWENTY_DASHES = "__________________";
    private static final int MAX_PLAYER_NAME_LENGTH = 20;

    private SpriteBatch batch;
    private Stage stage;
    private Camera camera;
    private Skin skin;
    private BitmapFont font32;
    private BitmapFont font48;
    private BitmapFont font96;
    private int maxLevel;
    private StringBuilder guiHelper;
    private HashMap<GameObject, TextureRegion[]> textures;
    private LinkedHashMap<GameObject, Integer> gameStats;

    private float statsDelay;
    private float scoreDelay;
    private int statsCount;
    private int statsCounted;
    private int totalScore;
    private int totalScoreToDraw;

    private Vector2 firstCameraPosition;
    private Vector2 currentCameraPosition;
    private Vector2 secondCameraPosition;
    private Vector2 cameraSpeed;
    private boolean moveCamera;

    private LinkedList<String> topPlayers;
    private LinkedList<Integer> topScores;

    public GameOverScreen(SpriteBatch batch, Camera camera) {
        this.batch = batch;
        this.camera = camera;
        this.textures = new HashMap<>();
        this.gameStats = new LinkedHashMap<>();
        this.guiHelper = new StringBuilder(100);
        this.firstCameraPosition = new Vector2(VIEWPORT_WIDTH / 2, VIEWPORT_HEIGHT / 2);
        this.currentCameraPosition = firstCameraPosition.cpy();
        this.secondCameraPosition = new Vector2(VIEWPORT_WIDTH / 2, SECOND_SCREEN_Y0 + VIEWPORT_HEIGHT / 2);
        this.cameraSpeed = new Vector2(0, -1 * CAMERA_SPEED);
        this.topPlayers = new LinkedList<>();
        this.topScores = new LinkedList<>();
    }

    @Override
    public void show() {
        this.currentCameraPosition.set(firstCameraPosition);
        moveCamera = true;
        updateCamera();

        font32 = Assets.getInstance().getAssetManager().get("zorque32.ttf", BitmapFont.class);
        font48 = Assets.getInstance().getAssetManager().get("zorque48.ttf", BitmapFont.class);
        font96 = Assets.getInstance().getAssetManager().get("zorque96.ttf", BitmapFont.class);

        Iterator<Map.Entry<GameObject, Integer>> iter = gameStats.entrySet().iterator();
        while (iter.hasNext()) {
            Map.Entry<GameObject, Integer> entry = iter.next();
            putTexture(entry.getKey());
        }
        putTexture(GameObject.EMPTY_CELL);
        loadTopScores();
        createGUI();

        this.maxLevel = 0;
        this.statsDelay = 0;
        this.scoreDelay = 0;
        this.statsCount = 0;
        this.statsCounted = 0;
        this.totalScore = 0;
        this.totalScoreToDraw = 0;
    }

    private void loadTopScores() {
        HighScoreSystem.loadResult(topPlayers, topScores);

        for (int i = topPlayers.size() - 1; i < TOP_LIST_SIZE; i++) {
            topPlayers.add(TWENTY_DASHES);
            topScores.add(0);
        }
    }

    public void setMaxLevel(int maxLevel) {
        this.maxLevel = maxLevel;
    }

    public void setGameStats(LinkedHashMap<GameObject, Integer> gameStats) {
        this.gameStats.clear();
        this.gameStats.putAll(gameStats);
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

    public void update(float dt) {
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

        stage.act(dt);
    }

    private void updateCamera() {
        camera.position.set(currentCameraPosition.x, currentCameraPosition.y, 0);
        camera.update();
    }

    private void drawStats() {
        font96.draw(batch, "Game Over", 0, 670, VIEWPORT_WIDTH, 1, false);
        guiHelper.setLength(0);
        guiHelper.append("Total score: ").append(totalScoreToDraw);
        font48.draw(batch, guiHelper, 420, 370, 0, -1, false);

        int xLine = 0;
        int yLine = 0;
        int count = 0;

        for (Map.Entry<GameObject, Integer> entry : gameStats.entrySet()) {

            if (count > statsCount) break;
            count++;

            if (statsCounted < count) {
                totalScore += entry.getKey().getScore() * entry.getValue();
                statsCounted++;
            }

            float imageX = 120 + xLine * 350;
            float imageY = 250 - (yLine * WORLD_CELL_PX + 5);
            float textX = 120 + xLine * 350 + WORLD_CELL_PX + 5;
            float textY = 250 - (yLine * WORLD_CELL_PX + 5) + WORLD_CELL_PX / 2;
            batch.draw(getTexture(entry.getKey())[0], imageX, imageY, WORLD_CELL_PX / 2, WORLD_CELL_PX / 2, WORLD_CELL_PX, WORLD_CELL_PX, 1, 1, 0);
            guiHelper.setLength(0);
            guiHelper.append(": ").append(entry.getValue()).append("x").append(entry.getKey().getScore());
            font48.draw(batch, guiHelper, textX, textY, 0, -1, false);
            xLine++;
            if (xLine == 3) {
                xLine = 0;
                yLine++;
            }
        }

        drawTopPlayersList();
    }

    private void drawTopPlayersList() {
        font48.draw(batch, "Top Players", 0, SECOND_SCREEN_Y0 + 670, VIEWPORT_WIDTH, 1, false);
        guiHelper.setLength(0);
        for (int i = 0; i < TOP_LIST_SIZE; i++) {
            guiHelper.append(i + 1).append(":\n");
        }
        font32.draw(batch, guiHelper, 360, SECOND_SCREEN_Y0 + 600, 40, 0, false);

        guiHelper.setLength(0);
        for (int i = 0; i < TOP_LIST_SIZE; i++) {
            guiHelper.append(topPlayers.get(i)).append("\n");
        }
        font32.draw(batch, guiHelper, 420, SECOND_SCREEN_Y0 + 600, 440, -1, false);

        guiHelper.setLength(0);
        for (int i = 0; i < TOP_LIST_SIZE; i++) {
            guiHelper.append(topScores.get(i)).append("\n");
        }
        font32.draw(batch, guiHelper, 860, SECOND_SCREEN_Y0 + 600, VIEWPORT_WIDTH - 860, -1, false);
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
        stage.draw();
    }

    public void createGUI() {
        stage = new Stage(ScreenManager.getInstance().getViewport(), batch);
        Gdx.input.setInputProcessor(stage);
        skin = new Skin();

        skin.addRegions(Assets.getInstance().getAtlas());
        skin.add("nameField", new Texture("nameField.bmp"));
        skin.add("cursor", new Texture("cursor.bmp"));
        skin.add("font32", font32);

        TextButton.TextButtonStyle textButtonStyle = new TextButton.TextButtonStyle();
        textButtonStyle.up = skin.getDrawable("simpleButton");
        textButtonStyle.font = font32;
        skin.add("simpleSkin", textButtonStyle);

        TextButton.TextButtonStyle shortTextButtonStyle = new TextButton.TextButtonStyle();
        shortTextButtonStyle.up = skin.getDrawable("shortButton");
        shortTextButtonStyle.font = font32;
        skin.add("shortButtonStyle", shortTextButtonStyle);

        TextField.TextFieldStyle tfs = new TextField.TextFieldStyle();
        tfs.font = font32;
        tfs.background = skin.getDrawable("nameField");
        tfs.fontColor = Color.WHITE;
        tfs.cursor = skin.getDrawable("cursor");
        skin.add("textFieldStyle", tfs);

        final TextField field = new TextField("Player", skin, "textFieldStyle");
        field.setWidth(560);
        field.setPosition(640 - 560 / 2, SECOND_SCREEN_Y0 + 150);

        Button btnNewGame = new TextButton("Start New Game", skin, "simpleSkin");
        Button btnMenu = new TextButton("Return To Menu", skin, "simpleSkin");
        final Button btnSaveResults = new TextButton("OK", skin, "shortButtonStyle");
        btnNewGame.setPosition(VIEWPORT_WIDTH / 2 - 330, SECOND_SCREEN_Y0 + 30);
        btnMenu.setPosition(VIEWPORT_WIDTH / 2 + 10, SECOND_SCREEN_Y0 + 30);
        btnSaveResults.setPosition(640 + 560 / 2 + 40, SECOND_SCREEN_Y0 + 140);

        stage.addActor(btnNewGame);
        stage.addActor(btnMenu);
        stage.addActor(field);
        stage.addActor(btnSaveResults);

        int playerScores = 0;
        for (Map.Entry<GameObject, Integer> entry : gameStats.entrySet()) {
            playerScores += entry.getKey().getScore() * entry.getValue();
        }

        if (topScores.getLast() >= playerScores) {
            btnSaveResults.setVisible(false);
            field.setVisible(false);
        }

        btnNewGame.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                ScreenManager.getInstance().changeScreen(ScreenManager.ScreenType.GAME);
            }
        });

        btnMenu.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                ScreenManager.getInstance().changeScreen(ScreenType.MENU);
            }
        });

        btnSaveResults.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {

                String playerName = field.getText();

                if (playerName.length() > MAX_PLAYER_NAME_LENGTH) {
                    playerName = playerName.substring(0, MAX_PLAYER_NAME_LENGTH);
                }

                for (int i = 0; i < topScores.size(); i++) {
                    if (topScores.get(i) < totalScore) {
                        topScores.add(i, totalScore);
                        topPlayers.add(i, playerName);
                        break;
                    }
                }

                if (topScores.size() > TOP_LIST_SIZE) {
                    topScores.remove(topScores.size() - 1);
                    topPlayers.remove(topPlayers.size() - 1);
                }

                HighScoreSystem.saveResult(topPlayers, topScores);

                btnSaveResults.setVisible(false);
                field.setVisible(false);
            }
        });
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
