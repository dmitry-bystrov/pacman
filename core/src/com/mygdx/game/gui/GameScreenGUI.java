package com.mygdx.game.gui;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.mygdx.game.Assets;
import com.mygdx.game.SoundManager;
import com.mygdx.game.screens.GameScreen;
import com.mygdx.game.screens.ScreenManager;

import java.util.HashMap;

public class GameScreenGUI extends SimpleGUI {

    private GameScreen gameScreen;
    private Group flowPanel;
    private Button btnPause;
    private Button btnMenu;
    private Button btnContinue;
    private Button btnRestart;
    private Label pauseLabel;
    private Label gameOverLabel;
    private Image image;
    private HashMap<GameObject, TextureRegion[]> textures;

    public GameScreenGUI(GameScreen gameScreen) {
        super(gameScreen.getBatch());

        this.gameScreen = gameScreen;
        this.textures = new HashMap<>();
        this.putTexture(GameObject.HEART);
    }

    @Override
    protected void setupSkin() {
        super.setupSkin();

        Pixmap pixmap = new Pixmap(360, 380, Pixmap.Format.RGB888);
        pixmap.setColor(0.0f, 0.0f, 0.3f, 1.0f);
        pixmap.fill();
        Texture texturePanel = new Texture(pixmap);
        skin.add("texturePanel", texturePanel);
    }

    @Override
    protected void setupStage() {
        super.setupStage();

        flowPanel = new Group();
        Label.LabelStyle ls = new Label.LabelStyle(font48, Color.WHITE);
        btnPause = new TextButton("II", skin, SHORT_BUTTON_SKIN);
        btnMenu = new TextButton("Return to Menu", skin, SIMPLE_SKIN);
        btnContinue = new TextButton("Continue Game", skin, SIMPLE_SKIN);
        btnRestart = new TextButton("Restart Level", skin, SIMPLE_SKIN);
        pauseLabel = new Label("Paused", ls);
        gameOverLabel = new Label("Game Over", ls);
        image = new Image(skin, "texturePanel");

        btnPause.setPosition(VIEWPORT_WIDTH - 100, 20);
        stage.addActor(btnPause);

        flowPanel.setVisible(false);
        flowPanel.setPosition(VIEWPORT_WIDTH / 2 - 220, VIEWPORT_HEIGHT / 2 - 160);

        pauseLabel.setPosition(90, 310);
        gameOverLabel.setPosition(36, 290);
        btnContinue.setPosition(20, 220);
        btnRestart.setPosition(20, 120);
        btnMenu.setPosition(20, 20);

        stage.addActor(flowPanel);
    }

    @Override
    protected void setupListeners() {
        btnPause.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                showPausePanel();
                SoundManager.playSound(GameSound.CLICK);
            }
        });

        btnMenu.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                ScreenManager.getInstance().changeScreen(ScreenManager.ScreenType.MENU);
            }
        });

        btnContinue.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                gameScreen.setGamePaused(false);
                flowPanel.setVisible(false);
                btnPause.setVisible(true);
                SoundManager.playSound(GameSound.CLICK);
            }
        });

        btnRestart.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                gameScreen.restartGameLevel();
                gameScreen.setGamePaused(false);
                flowPanel.setVisible(false);
                btnPause.setVisible(true);
                SoundManager.playSound(GameSound.CLICK);
            }
        });
    }

    private void putTexture(GameObject gameObject) {
        float textureSize = 1;

        if (gameObject == GameObject.HEART) {
            textureSize = 0.75f;
        }

        textures.put(gameObject, Assets.getInstance().getAtlas().findRegion(gameObject.getTextureName()).split((int)(WORLD_CELL_PX * textureSize), (int)(WORLD_CELL_PX * textureSize))[gameObject.getTextureRegionIndex()]);
    }

    private TextureRegion[] getTexture(GameObject gameObject) {
        return textures.get(gameObject);
    }

    private void showPausePanel() {
        gameScreen.setGamePaused(true);
        btnPause.setVisible(false);
        flowPanel.clearChildren();
        flowPanel.addActor(image);
        flowPanel.addActor(pauseLabel);
        flowPanel.addActor(btnContinue);
        flowPanel.addActor(btnRestart);
        flowPanel.addActor(btnMenu);
        flowPanel.setVisible(true);
    }

    public void showGameOverPanel() {
        gameScreen.setGamePaused(true);
        btnPause.setVisible(false);
        flowPanel.clearChildren();
        flowPanel.addActor(image);
        flowPanel.addActor(gameOverLabel);
        flowPanel.addActor(btnRestart);
        flowPanel.addActor(btnMenu);
        flowPanel.setVisible(true);
    }

    public void renderStats() {
        stringBuilder.setLength(0);
        stringBuilder.append("Score: ").append(gameScreen.getGameManager().getPacMan().getScore());
        font48.draw(gameScreen.getBatch(), stringBuilder, 20, VIEWPORT_HEIGHT - 20);
        for (int i = 0; i < MAX_LIVES; i++) {
            gameScreen.getBatch().draw(getTexture(GameObject.HEART)[(gameScreen.getGameManager().getPacMan().getLives() > i?1:0)], VIEWPORT_WIDTH - 80 * (i + 1), VIEWPORT_HEIGHT - 80, WORLD_CELL_PX * 0.75f, WORLD_CELL_PX * 0.75f, WORLD_CELL_PX * 0.75f, WORLD_CELL_PX * 0.75f, 1, 1, 0);
        }
    }
}
