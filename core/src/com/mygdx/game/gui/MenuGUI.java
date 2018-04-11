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
        btnNewGame.setPosition(VIEWPORT_WIDTH / 2 - 160, 170);
        btnExitGame.setPosition(VIEWPORT_WIDTH / 2 - 160, 60);
        stage.addActor(btnNewGame);
        stage.addActor(btnExitGame);
    }

    @Override
    protected void setupListeners() {
        btnNewGame.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                ScreenManager.getInstance().setGameLevel(GameLevel.LEVEL1);
                ScreenManager.getInstance().changeScreen(ScreenManager.ScreenType.GAME);
            }
        });
        btnExitGame.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                Gdx.app.exit();
            }
        });

    }
}
