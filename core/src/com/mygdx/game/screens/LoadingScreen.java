package com.mygdx.game.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.mygdx.game.Assets;

public class LoadingScreen implements Screen {
    private SpriteBatch batch;
    private Texture texture;

    public LoadingScreen(SpriteBatch batch) {
        this.batch = batch;
        Pixmap pixmap = new Pixmap(1280, 40, Pixmap.Format.RGB888);
        pixmap.setColor(Color.GREEN);
        pixmap.fill();
        texture = new Texture(pixmap);
    }

    @Override
    public void show() {
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0.01f, 0.01f, 0.01f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        if (Assets.getInstance().getAssetManager().update()) {
            Assets.getInstance().makeLinks();
            ScreenManager.getInstance().goToTarget();
        }
        batch.begin();
        batch.draw(texture, 0, 0, 1280 * Assets.getInstance().getAssetManager().getProgress(), 40);
        batch.end();
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
        texture.dispose();
    }
}
