package com.mygdx.game.gui;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.mygdx.game.screens.MenuScreen;
import com.mygdx.game.screens.ScreenManager;

public class MenuGUI extends SimpleGUI {

    private MenuScreen menuScreen;
    private Button btnNewGame;
    private Button btnExitGame;
    private Button btnMenu;

    public MenuGUI(MenuScreen menuScreen) {
        super(menuScreen.getBatch());
        this.menuScreen = menuScreen;
    }

    @Override
    protected void setupSkin() {
        super.setupSkin();
    }

    @Override
    protected void setupStage() {
        super.setupStage();

        btnNewGame = new TextButton("Start New Game", skin, "simpleSkin");
        btnExitGame = new TextButton("Exit Game", skin, "simpleSkin");
        btnMenu = new TextButton("Return To Menu", skin, "simpleSkin");
        btnNewGame.setPosition(VIEWPORT_WIDTH / 2 - 160, 170);
        btnExitGame.setPosition(VIEWPORT_WIDTH / 2 - 160, 60);
        btnMenu.setPosition(VIEWPORT_WIDTH / 2 - 160, SECOND_SCREEN_Y0 + 20);
        stage.addActor(btnNewGame);
        stage.addActor(btnExitGame);
        stage.addActor(btnMenu);
    }

    @Override
    protected void setupListeners() {
        btnNewGame.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                menuScreen.moveCameraDown();
//                ScreenManager.getInstance().setGameLevel(GameLevel.LEVEL1);
//                ScreenManager.getInstance().changeScreen(ScreenManager.ScreenType.GAME);
            }
        });

        btnExitGame.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                Gdx.app.exit();
            }
        });

        btnMenu.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                menuScreen.moveCameraUp();
            }
        });

    }
}
