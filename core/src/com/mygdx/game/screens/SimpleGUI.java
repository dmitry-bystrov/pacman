package com.mygdx.game.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.mygdx.game.Assets;
import com.mygdx.game.GameConstants;

public class SimpleGUI implements GameConstants {

    private GameScreen gameScreen;

    private BitmapFont font32;
    private BitmapFont font48;
    private StringBuilder stringBuilder;

    private Stage stage;
    private Skin skin;

    private Group flowPanel = new Group();
    private Button btnPause;
    private Button btnMenu;
    private Button btnContinue;
    private Button btnRestart;
    private Label pauseLabel;
    private Label gameOverLabel;
    private Image image;

    public SimpleGUI(GameScreen gameScreen) {
        this.gameScreen = gameScreen;
        this.stringBuilder = new StringBuilder(100);
        this.font32 = Assets.getInstance().getAssetManager().get("zorque32.ttf");
        this.font48 = Assets.getInstance().getAssetManager().get("zorque48.ttf");

        this.setupSkin();
        this.setupStage();
        this.setupListeners();
    }

    private void setupSkin() {
        skin = new Skin();
        skin.addRegions(Assets.getInstance().getAtlas());
        skin.add("font32", font32);
        skin.add("font48", font48);

        TextButton.TextButtonStyle textButtonStyle = new TextButton.TextButtonStyle();
        textButtonStyle.up = skin.getDrawable("simpleButton");
        textButtonStyle.font = font32;
        skin.add("simpleSkin", textButtonStyle);

        TextButton.TextButtonStyle shortButtonStyle = new TextButton.TextButtonStyle();
        shortButtonStyle.up = skin.getDrawable("shortButton");
        shortButtonStyle.font = font48;
        skin.add("shortButtonSkin", shortButtonStyle);

        Pixmap pixmap = new Pixmap(360, 380, Pixmap.Format.RGB888);
        pixmap.setColor(0.0f, 0.0f, 0.3f, 1.0f);
        pixmap.fill();
        Texture texturePanel = new Texture(pixmap);
        skin.add("texturePanel", texturePanel);
    }

    private void setupStage() {
        stage = new Stage(ScreenManager.getInstance().getViewport(), gameScreen.getBatch());
        Gdx.input.setInputProcessor(stage);

        Label.LabelStyle ls = new Label.LabelStyle(font48, Color.WHITE);
        btnPause = new TextButton("II", skin, "shortButtonSkin");
        btnMenu = new TextButton("Return to Menu", skin, "simpleSkin");
        btnContinue = new TextButton("Continue Game", skin, "simpleSkin");
        btnRestart = new TextButton("Restart Level", skin, "simpleSkin");
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

    public void showPausePanel() {
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

    private void setupListeners() {
        btnPause.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                showPausePanel();
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
            }
        });

        btnRestart.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                gameScreen.restartGameLevel();
                gameScreen.setGamePaused(false);
                flowPanel.setVisible(false);
                btnPause.setVisible(true);
            }
        });
    }

    public void renderStage() {
        stage.draw();
    }

    public void renderStats() {
        stringBuilder.setLength(0);
        stringBuilder.append("Lives: ").append(gameScreen.getGameLevel().getPacMan().getLives()).append("\nScore: ").append(gameScreen.getGameLevel().getPacMan().getScore());
        font48.draw(gameScreen.getBatch(), stringBuilder, 20, VIEWPORT_HEIGHT - 20);
    }


    public void update(float dt) {
        stage.act(dt);
    }

}
