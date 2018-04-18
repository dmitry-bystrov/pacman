package com.mygdx.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Music;

public class MusicManager {
    private Music music;

    private static MusicManager ourInstance = new MusicManager();
    public static MusicManager getInstance() {
        return ourInstance;
    }

    private MusicManager() {
        music = Gdx.audio.newMusic(Gdx.files.internal("music.mp3"));
        music.setVolume(0.75f);
        music.setLooping(true);
    }

    public Music getMusic() {
        return music;
    }

    public static void playMusic() {
        if (GameSettings.isMusic()) getInstance().getMusic().play();
    }

    public static void stopMusic() {
        getInstance().getMusic().stop();
    }
}
