package com.mygdx.game;

import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.assets.loaders.FileHandleResolver;
import com.badlogic.gdx.assets.loaders.resolvers.InternalFileHandleResolver;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGeneratorLoader;
import com.badlogic.gdx.graphics.g2d.freetype.FreetypeFontLoader;
import com.mygdx.game.screens.ScreenManager;

public class Assets implements GameConstants {
    private static final Assets ourInstance = new Assets();
    public static final String PACMAN_PACK = "pacman.pack";
    private AssetManager assetManager;
    private TextureAtlas textureAtlas;

    public static Assets getInstance() {
        return ourInstance;
    }

    public TextureAtlas getAtlas() {
        return textureAtlas;
    }

    public AssetManager getAssetManager() {
        return assetManager;
    }

    private Assets() {
        assetManager = new AssetManager();
    }

    public void loadAssets(ScreenManager.ScreenType type) {
        switch (type) {
            case MENU:
                assetManager.load(PACMAN_PACK, TextureAtlas.class);
                assetManager.load(GameSound.CLICK.getFilename(), Sound.class);
                createStandardFont(32);
                createStandardFont(48);
                createStandardFont(96);
                break;
            case GAME:
                assetManager.load(PACMAN_PACK, TextureAtlas.class);
                assetManager.load(GameSound.CLICK.getFilename(), Sound.class);
                assetManager.load(GameSound.FOOD.getFilename(), Sound.class);
                assetManager.load(GameSound.FRUIT_APPEARANCE.getFilename(), Sound.class);
                assetManager.load(GameSound.FRUIT_COLLECTED.getFilename(), Sound.class);
                assetManager.load(GameSound.GHOST.getFilename(), Sound.class);
                assetManager.load(GameSound.GHOST_KILLED.getFilename(), Sound.class);
                assetManager.load(GameSound.PACMAN_KILLED.getFilename(), Sound.class);
                assetManager.load(GameSound.POWERED.getFilename(), Sound.class);
                assetManager.load(GameSound.TELEPORT.getFilename(), Sound.class);

                createStandardFont(32);
                createStandardFont(48);
                break;
            case LEVEL_COMPLETE:
                assetManager.load(PACMAN_PACK, TextureAtlas.class);
                assetManager.load(GameSound.CLICK.getFilename(), Sound.class);
                assetManager.load(GameSound.COIN.getFilename(), Sound.class);
                assetManager.load(GameSound.STAR.getFilename(), Sound.class);
                createStandardFont(32);
                createStandardFont(48);
                createStandardFont(96);
                break;
        }
    }

    public void createStandardFont(int size) {
        FileHandleResolver resolver = new InternalFileHandleResolver();
        assetManager.setLoader(FreeTypeFontGenerator.class, new FreeTypeFontGeneratorLoader(resolver));
        assetManager.setLoader(BitmapFont.class, ".ttf", new FreetypeFontLoader(resolver));
        FreetypeFontLoader.FreeTypeFontLoaderParameter fontParameter = new FreetypeFontLoader.FreeTypeFontLoaderParameter();
        fontParameter.fontFileName = "zorque.ttf";
        fontParameter.fontParameters.size = size;
        fontParameter.fontParameters.color = Color.WHITE;
        fontParameter.fontParameters.borderWidth = 1;
        fontParameter.fontParameters.borderColor = Color.BLACK;
        fontParameter.fontParameters.shadowOffsetX = 1;
        fontParameter.fontParameters.shadowOffsetY = 1;
        fontParameter.fontParameters.shadowColor = Color.BLACK;
        assetManager.load("zorque" + size + ".ttf", BitmapFont.class, fontParameter);
    }

    public void makeLinks() {
        textureAtlas = assetManager.get(PACMAN_PACK, TextureAtlas.class);
    }

    public void clear() {
        assetManager.clear();
    }
}
