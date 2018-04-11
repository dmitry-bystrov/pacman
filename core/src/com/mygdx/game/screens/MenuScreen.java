package com.mygdx.game.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
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
import com.mygdx.game.gui.MenuGUI;

import java.util.HashMap;

public class MenuScreen implements Screen, GameConstants {
    private SpriteBatch batch;
    private BitmapFont font32;
    private BitmapFont font48;
    private BitmapFont font96;
    private MenuGUI menuGUI;
    private HashMap<GameObject, TextureRegion[]> textures;

    public MenuScreen(SpriteBatch batch) {
        this.batch = batch;
        this.textures = new HashMap<>();
    }

    @Override
    public void show() {
        font32 = Assets.getInstance().getAssetManager().get("zorque32.ttf", BitmapFont.class);
        font48 = Assets.getInstance().getAssetManager().get("zorque48.ttf", BitmapFont.class);
        font96 = Assets.getInstance().getAssetManager().get("zorque96.ttf", BitmapFont.class);
        putTexture(GameObject.EMPTY_CELL);

        menuGUI = new MenuGUI(this);
    }

    public SpriteBatch getBatch() {
        return batch;
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

    @Override
    public void render(float delta) {
        update(delta);
        Gdx.gl.glClearColor(0.4f, 0.4f, 1, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        batch.begin();
        fillBackground();
        font96.draw(batch, "Pac-Man 2018", 0, 600, VIEWPORT_WIDTH, 1, false);
        font48.draw(batch, "Arcade", 0, 480, VIEWPORT_WIDTH, 1, false);
        batch.end();
        menuGUI.renderStage();
    }

    public void update(float dt) {
        menuGUI.update(dt);
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
