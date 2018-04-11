package com.mygdx.game.gui;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.mygdx.game.HighScoreSystem;
import com.mygdx.game.screens.LevelComleteScreen;
import com.mygdx.game.screens.ScreenManager;

public class LevelCompleteGUI extends SimpleGUI {

    private LevelComleteScreen levelComleteScreen;
    private Image image;
    private Group flowPanel;
    private TextField field;
    private Button btnSaveResults;
    private Button btnMenu;
    private Button btnNextLevel;

    public LevelCompleteGUI(LevelComleteScreen levelComleteScreen) {
        super(levelComleteScreen.getBatch());
        this.levelComleteScreen = levelComleteScreen;
    }

    @Override
    protected void setupSkin() {
        super.setupSkin();

        Pixmap pixmap = new Pixmap(700, 120, Pixmap.Format.RGB888);
        pixmap.setColor(0.0f, 0.0f, 0.3f, 1.0f);
        pixmap.fill();
        Texture texturePanel = new Texture(pixmap);
        skin.add("texturePanel", texturePanel);
        skin.add("nameField", new Texture("nameField.bmp"));
        skin.add("cursor", new Texture("cursor.bmp"));

        TextField.TextFieldStyle tfs = new TextField.TextFieldStyle();
        tfs.font = font32;
        tfs.background = skin.getDrawable("nameField");
        tfs.fontColor = Color.WHITE;
        tfs.cursor = skin.getDrawable("cursor");
        skin.add("textFieldStyle", tfs);
    }

    @Override
    protected void setupStage() {
        super.setupStage();

        btnNextLevel = new TextButton("Next Level", skin, "simpleSkin");
        btnMenu = new TextButton("Return To Menu", skin, "simpleSkin");

        btnNextLevel.setPosition(VIEWPORT_WIDTH / 2 - 330, LevelComleteScreen.SECOND_SCREEN_Y0 + 30);
        btnMenu.setPosition(VIEWPORT_WIDTH / 2 + 10, LevelComleteScreen.SECOND_SCREEN_Y0 + 30);

        stage.addActor(btnNextLevel);
        stage.addActor(btnMenu);

        flowPanel = new Group();
        image = new Image(skin, "texturePanel");
        field = new TextField("Player", skin, "textFieldStyle");
        btnSaveResults = new TextButton("OK", skin, "shortButtonSkin");

        field.setWidth(560);
        field.setPosition(20, 35);
        btnSaveResults.setPosition(600, 20);

        flowPanel.addActor(image);
        flowPanel.addActor(field);
        flowPanel.addActor(btnSaveResults);

        flowPanel.setVisible(false);
        flowPanel.setPosition(VIEWPORT_WIDTH / 2 - 350, LevelComleteScreen.SECOND_SCREEN_Y0 + 405);
        stage.addActor(flowPanel);
    }

    @Override
    protected void setupListeners() {
        btnNextLevel.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                if (ScreenManager.getInstance().getGameLevel().getNext() != null) {
                    ScreenManager.getInstance().setGameLevel(ScreenManager.getInstance().getGameLevel().getNext());
                }

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
                HighScoreSystem.saveResult(field.getText(), levelComleteScreen.getTotalScore(), levelComleteScreen.getStarsCount(), ScreenManager.getInstance().getGameLevel().getScoreFileName());
                flowPanel.setVisible(false);
            }
        });
    }

    public void showFlowInputPanel() {
        flowPanel.setVisible(true);
    }
}
