package com.mygdx.game;

import com.badlogic.gdx.audio.Sound;

public class SoundManager implements GameConstants {
    public static void playSound(GameSound gameSound) {
        if (GameSettings.isSounds()) {
            Assets.getInstance().getAssetManager().get(gameSound.getFilename(), Sound.class).play(gameSound.getVolume());
        }
    }
}
