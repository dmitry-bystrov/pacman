package com.mygdx.game.screens;

import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.mygdx.game.MyGdxGame;

public class ScreenManager {
    public static final int VIEWPORT_WIDTH = 1280;
    public static final int VIEWPORT_HEIGHT = 720;

    public enum ScreenType {
        MENU, GAME
    }

    private MyGdxGame game;
    private GameScreen gameScreen;
    private MenuScreen menuScreen;
    private Viewport viewport;

    private static ScreenManager ourInstance = new ScreenManager();

    public static ScreenManager getInstance() {
        return ourInstance;
    }

    private ScreenManager() {
    }

    public void init(MyGdxGame game, SpriteBatch batch) {
        this.game = game;
        this.gameScreen = new GameScreen(batch);
        this.menuScreen = new MenuScreen(batch);
        this.viewport = new FitViewport(VIEWPORT_WIDTH, VIEWPORT_HEIGHT);
    }

    public void resize(int width, int height) {
        viewport.update(width, height);
        viewport.apply();
    }

    public void changeScreen(ScreenType type) {
        Screen currentScreen = game.getScreen();
        if (currentScreen != null) {
            currentScreen.dispose();
        }
        switch (type) {
            case MENU:
                game.setScreen(menuScreen);
                break;
            case GAME:
                game.setScreen(gameScreen);
                break;
        }
    }
}
