package com.mygdx.game.screens;

import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.mygdx.game.Assets;
import com.mygdx.game.GameConstants;
import com.mygdx.game.MyGdxGame;

public class ScreenManager implements GameConstants {

    private MyGdxGame game;
    private SpriteBatch batch;
    private GameScreen gameScreen;
    private LoadingScreen loadingScreen;
    private MenuScreen menuScreen;
    private LevelComleteScreen levelComleteScreen;
    private Screen targetScreen;
    private Viewport viewport;
    private Camera camera;
    private GameLevel gameLevel;

    private static ScreenManager ourInstance = new ScreenManager();

    public static ScreenManager getInstance() {
        return ourInstance;
    }

    public Viewport getViewport() {
        return viewport;
    }

    private ScreenManager() {
    }

    public void init(MyGdxGame game, SpriteBatch batch) {
        this.game = game;
        this.batch = batch;
        this.camera = new OrthographicCamera(VIEWPORT_WIDTH, VIEWPORT_HEIGHT);
        this.viewport = new FitViewport(VIEWPORT_WIDTH, VIEWPORT_HEIGHT, camera);
        this.gameScreen = new GameScreen(batch, camera);
        this.menuScreen = new MenuScreen(batch, camera);
        this.levelComleteScreen = new LevelComleteScreen(batch, camera);
        this.loadingScreen = new LoadingScreen(batch);
    }

    public GameLevel getGameLevel() {
        return gameLevel;
    }

    public void setGameLevel(GameLevel gameLevel) {
        this.gameLevel = gameLevel;
    }

    public void resize(int width, int height) {
        viewport.update(width, height);
        viewport.apply();
    }

    public void resetCamera() {
        camera.position.set(VIEWPORT_WIDTH / 2, VIEWPORT_HEIGHT / 2, 0);
        camera.update();
        batch.setProjectionMatrix(camera.combined);
    }

    public void changeScreen(ScreenType type) {
        if (type == ScreenType.LEVEL_COMPLETE) {
            levelComleteScreen.setGameStats(gameScreen.getGameStats());
        }

        Screen screen = game.getScreen();
        Assets.getInstance().clear();
        if (screen != null) {
            screen.dispose();
        }
        resetCamera();

        switch (type) {
            case MENU:
                game.setScreen(loadingScreen);
                targetScreen = menuScreen;
                Assets.getInstance().loadAssets(ScreenType.MENU);
                break;
            case GAME:
                game.setScreen(loadingScreen);
                targetScreen = gameScreen;
                Assets.getInstance().loadAssets(ScreenType.GAME);
                break;
            case LEVEL_COMPLETE:
                game.setScreen(loadingScreen);
                targetScreen = levelComleteScreen;
                Assets.getInstance().loadAssets(ScreenType.LEVEL_COMPLETE);
                break;
        }
    }

    public void goToTarget() {
        game.setScreen(targetScreen);
    }
}
