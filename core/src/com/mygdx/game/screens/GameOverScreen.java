package com.mygdx.game.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.mygdx.game.Assets;
import com.mygdx.game.GameConstants;

import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;

public class GameOverScreen implements Screen, GameConstants {
    private SpriteBatch batch;
    private Stage stage;
    private Skin skin;
    private BitmapFont font32;
    private BitmapFont font48;
    private BitmapFont font96;
    private int totalScore;
    private int maxLevel;
    private StringBuilder guiHelper;
    private HashMap<GameObject, TextureRegion[]> textures;
    private LinkedHashMap<GameObject, Integer> gameStats;

    public GameOverScreen(SpriteBatch batch) {
        this.batch = batch;
        this.maxLevel = 0;
        this.totalScore = 0;
        this.textures = new HashMap<>();
        this.gameStats = new LinkedHashMap<>();
        this.guiHelper = new StringBuilder(100);
    }

    @Override
    public void show() {
        font32 = Assets.getInstance().getAssetManager().get("zorque32.ttf", BitmapFont.class);
        font48 = Assets.getInstance().getAssetManager().get("zorque48.ttf", BitmapFont.class);
        font96 = Assets.getInstance().getAssetManager().get("zorque96.ttf", BitmapFont.class);
        Iterator<Map.Entry<GameObject, Integer>> iter = gameStats.entrySet().iterator();
        while (iter.hasNext()) {
            Map.Entry<GameObject, Integer> entry = iter.next();
            putTexture(entry.getKey());
        }
        putTexture(GameObject.EMPTY_CELL);
        createGUI();
    }

    public void setMaxLevel(int maxLevel) {
        this.maxLevel = maxLevel;
    }

    public void setGameStats(LinkedHashMap<GameObject, Integer> gameStats) {
        totalScore = 0;
        this.gameStats.clear();
        this.gameStats.putAll(gameStats);
        Iterator<Map.Entry<GameObject, Integer>> iter = gameStats.entrySet().iterator();
        while (iter.hasNext()) {
            Map.Entry<GameObject, Integer> entry = iter.next();
            totalScore += entry.getKey().getScore() * entry.getValue();
        }
    }

    private void putTexture(GameObject gameObject) {
        textures.put(gameObject, Assets.getInstance().getAtlas().findRegion(gameObject.getTextureName()).split(WORLD_CELL_PX, WORLD_CELL_PX)[gameObject.getTextureRegionIndex()]);

    }

    private TextureRegion[] getTexture(GameObject gameObject) {
        return textures.get(gameObject);
    }

    private void fillBackground() {
        for (int x = 0; x <= VIEWPORT_WIDTH - WORLD_CELL_PX; x += WORLD_CELL_PX) {
            for (int y = 0; y <= VIEWPORT_HEIGHT - WORLD_CELL_PX; y += WORLD_CELL_PX) {
                batch.draw(getTexture(GameObject.EMPTY_CELL)[0], x, y, WORLD_CELL_PX / 2, WORLD_CELL_PX / 2, WORLD_CELL_PX, WORLD_CELL_PX,1,1,0);
            }
        }
    }

    private void drawStats() {
        font96.draw(batch, "Game Over", 0, 670, VIEWPORT_WIDTH, 1, false);
        guiHelper.setLength(0);
        guiHelper.append("Max level: ").append(maxLevel);
        font48.draw(batch, guiHelper, 0, 570, VIEWPORT_WIDTH, 1, false);
        guiHelper.setLength(0);
        guiHelper.append("Total score: ").append(totalScore);
        font48.draw(batch, guiHelper, 0, 520, VIEWPORT_WIDTH, 1, false);
        Iterator<Map.Entry<GameObject, Integer>> iter = gameStats.entrySet().iterator();
        int xLine = 0;
        int yLine = 0;
        while (iter.hasNext()) {
            Map.Entry<GameObject, Integer> entry = iter.next();
            float imageX = 120 + xLine * 350;
            float imageY = 400 - (yLine * WORLD_CELL_PX + 5);
            float textX = 120 + xLine * 350 + WORLD_CELL_PX + 5;
            float textY = 400 - (yLine * WORLD_CELL_PX + 5) + WORLD_CELL_PX / 2;
            batch.draw(getTexture(entry.getKey())[0], imageX, imageY, WORLD_CELL_PX / 2, WORLD_CELL_PX / 2, WORLD_CELL_PX, WORLD_CELL_PX,1,1,0);
            guiHelper.setLength(0);
            guiHelper.append( ": ").append(entry.getValue()).append("x").append(entry.getKey().getScore());
            font48.draw(batch, guiHelper, textX, textY, 0, -1, false);
            xLine++;
            if (xLine == 3) {
                xLine = 0;
                yLine ++;
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
        drawStats();
        batch.end();
        stage.draw();
    }

    public void update(float dt) {
        stage.act(dt);
    }

    public void createGUI() {
        stage = new Stage(ScreenManager.getInstance().getViewport(), batch);
        Gdx.input.setInputProcessor(stage);
        skin = new Skin();
        skin.addRegions(Assets.getInstance().getAtlas());
        skin.add("font32", font32);
        TextButton.TextButtonStyle textButtonStyle = new TextButton.TextButtonStyle();
        textButtonStyle.up = skin.getDrawable("simpleButton");
        textButtonStyle.font = font32;
        skin.add("simpleSkin", textButtonStyle);

        Button btnNewGame = new TextButton("Start New Game", skin, "simpleSkin");
        Button btnMenu = new TextButton("Return To Menu", skin, "simpleSkin");
        btnNewGame.setPosition(VIEWPORT_WIDTH / 2 - 160, 130);
        btnMenu.setPosition(VIEWPORT_WIDTH / 2 - 160, 30);
        stage.addActor(btnNewGame);
        stage.addActor(btnMenu);
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
