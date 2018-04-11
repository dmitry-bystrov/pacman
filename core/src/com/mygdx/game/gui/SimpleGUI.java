package com.mygdx.game.gui;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.mygdx.game.Assets;
import com.mygdx.game.GameConstants;
import com.mygdx.game.screens.ScreenManager;

public class SimpleGUI implements GameConstants {

    protected BitmapFont font32;
    protected BitmapFont font48;
    protected StringBuilder stringBuilder;

    protected Stage stage;
    protected Skin skin;
    protected Batch batch;

    public SimpleGUI(Batch batch) {
        this.batch = batch;
        this.stringBuilder = new StringBuilder(100);
        this.font32 = Assets.getInstance().getAssetManager().get("zorque32.ttf");
        this.font48 = Assets.getInstance().getAssetManager().get("zorque48.ttf");

        this.setupSkin();
        this.setupStage();
        this.setupListeners();
    }

    protected void setupSkin() {
        skin = new Skin();
        skin.addRegions(Assets.getInstance().getAtlas());
        skin.add("font32", font32);
        skin.add("font48", font48);
        skin.add("font96", font48);

        TextButton.TextButtonStyle textButtonStyle = new TextButton.TextButtonStyle();
        textButtonStyle.up = skin.getDrawable("simpleButton");
        textButtonStyle.font = font32;
        skin.add("simpleSkin", textButtonStyle);

        TextButton.TextButtonStyle textGreyButtonStyle = new TextButton.TextButtonStyle();
        textGreyButtonStyle.up = skin.getDrawable("simpleButtonGrey");
        textGreyButtonStyle.font = font32;
        skin.add("simpleGreySkin", textGreyButtonStyle);

        TextButton.TextButtonStyle shortButtonStyle = new TextButton.TextButtonStyle();
        shortButtonStyle.up = skin.getDrawable("shortButton");
        shortButtonStyle.font = font48;
        skin.add("shortButtonSkin", shortButtonStyle);
    }

    protected void setupStage() {
        stage = new Stage(ScreenManager.getInstance().getViewport(), batch);
        Gdx.input.setInputProcessor(stage);
    }

    protected void setupListeners() {

    }

    public void renderStage() {
        stage.draw();
    }

    public void update(float dt) {
        stage.act(dt);
    }

}
