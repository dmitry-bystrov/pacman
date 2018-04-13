package com.mygdx.game;

import com.badlogic.gdx.Gdx;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;

public class GameSettings implements Serializable {
    private static final String GAME_SETTINGS_FILE = "settings.dat";

    private GameConstants.Difficulty difficulty;
    private boolean music;
    private boolean sounds;

    private static GameSettings ourInstance = new GameSettings();

    public static GameSettings getInstance() {
        return ourInstance;
    }

    private GameSettings() {
        this.difficulty = GameConstants.Difficulty.MIDDLE;
        this.music = true;
        this.sounds = true;
    }

    public static void loadSettings() {
        ObjectInputStream in = null;
        try {
            if (!Gdx.files.local(GAME_SETTINGS_FILE).exists()) return;

            in = new ObjectInputStream(Gdx.files.local(GAME_SETTINGS_FILE).read());
            ourInstance = (GameSettings)in.readObject();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (in != null) in.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static void saveSettings() {
        ObjectOutputStream out = null;
        try {
            out = new ObjectOutputStream(Gdx.files.local(GAME_SETTINGS_FILE).write(false));
            out.writeObject(getInstance());
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (out != null) out.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static GameConstants.Difficulty getDifficulty() {
        return getInstance().difficulty;
    }

    public static void setDifficulty(GameConstants.Difficulty difficultyNewValue) {
        getInstance().difficulty = difficultyNewValue;
    }

    public static boolean isMusic() {
        return getInstance().music;
    }

    public static void setMusic(boolean musicNewValue) {
        getInstance().music = musicNewValue;
    }

    public static boolean isSounds() {
        return getInstance().sounds;
    }

    public static void setSounds(boolean soundsNewValue) {
        getInstance().sounds = soundsNewValue;
    }
}
