package com.mygdx.game;

import com.badlogic.gdx.Gdx;
import com.mygdx.game.creatures.Ghost;
import com.mygdx.game.creatures.Pacman;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;

public class GameSession implements Serializable {
    private static final String GAME_SESSION_FILE = "game_session.dat";
    private Pacman pacMan;
    private GameLevel gameLevel;
    private Ghost[] ghosts;

    public GameLevel getGameLevel() {
        return gameLevel;
    }

    public GameSession() {
    }

    public GameSession(GameLevel gameLevel) {
        this.gameLevel = gameLevel;
        this.pacMan = gameLevel.getPacMan();
        this.ghosts = gameLevel.getGhosts();
    }

    public Pacman getPacMan() {
        return pacMan;
    }

    public Ghost[] getGhosts() {
        return ghosts;
    }

    public void saveSession() {
        ObjectOutputStream out = null;
        try {
            out = new ObjectOutputStream(Gdx.files.local(GAME_SESSION_FILE).write(false));
            out.writeObject(gameLevel);
            out.writeObject(pacMan);
            for (int i = 0; i < 4; i++) {
                out.writeObject(ghosts[i]);
            }
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

    public void loadSession() {
        ObjectInputStream in = null;
        try {
            in = new ObjectInputStream(Gdx.files.local(GAME_SESSION_FILE).read());
            gameLevel = (GameLevel)in.readObject();
            pacMan = (Pacman)in.readObject();
            ghosts = new Ghost[4];
            for (int i = 0; i < 4; i++) {
                ghosts[i] = (Ghost) in.readObject();
            }
            gameLevel.restoreSession(this);
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
}

