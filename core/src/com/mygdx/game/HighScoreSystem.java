package com.mygdx.game;

import com.badlogic.gdx.Gdx;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.Writer;
import java.util.LinkedList;

public class HighScoreSystem {

    private static final String TOP_SCORE_FILE_NAME = "score.dat";
    private static final String ONE_SPACE = " ";
    private static final int TOP_LIST_SIZE = 10;
    private static final String DOTS = ".............................................";
    private static final int MAX_PLAYER_NAME_LENGTH = 20;

    private static LinkedList<String> topPlayers;
    private static LinkedList<Integer> topScores;
    private static StringBuilder stringBuilder;

    static {
        topPlayers = new LinkedList<>();
        topScores = new LinkedList<>();
        stringBuilder = new StringBuilder(250);
    }

    public static void saveResult(String player, int score) {

        if (player.length() > MAX_PLAYER_NAME_LENGTH) {
            player = player.substring(0, MAX_PLAYER_NAME_LENGTH);
        }

        for (int i = 0; i < topScores.size(); i++) {
            if (topScores.get(i) < score) {
                topScores.add(i, score);
                topPlayers.add(i, player);
                break;
            }
        }

        if (topScores.size() > TOP_LIST_SIZE) {
            topScores.remove(topScores.size() - 1);
            topPlayers.remove(topPlayers.size() - 1);
        }

        Writer writer = null;
        try {
            writer = Gdx.files.local(TOP_SCORE_FILE_NAME).writer(false);
            for (int i = 0; i < topPlayers.size(); i++) {
                writer.write(topPlayers.get(i).replace(' ', '^') + ONE_SPACE + topScores.get(i));
                writer.write("\n");
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (writer != null) writer.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static void loadResult() {
        topPlayers.clear();
        topScores.clear();

        if (Gdx.files.local(TOP_SCORE_FILE_NAME).exists()) {
            BufferedReader br = null;
            try {
                br = Gdx.files.local(TOP_SCORE_FILE_NAME).reader(8192);

                String line;
                while ((line = br.readLine()) != null) {
                    String[] values = line.split("\\s");
                    try {
                        topPlayers.add(values[0].replace('^', ' '));
                        topScores.add(Integer.valueOf(values[1]));
                    } catch (NumberFormatException | ArrayIndexOutOfBoundsException e) {
                        e.printStackTrace();
                    }
                }

                while (topPlayers.size() < TOP_LIST_SIZE) {
                    topPlayers.add(DOTS);
                    topScores.add(0);
                }

            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                try {
                    if (br != null) br.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public static CharSequence getListNumbersColumn() {
        stringBuilder.setLength(0);
        for (int i = 0; i < TOP_LIST_SIZE; i++) {
            stringBuilder.append(i + 1).append(":\n");
        }

        return stringBuilder;
    }

    public static CharSequence getListPlayersColumn() {
        stringBuilder.setLength(0);
        for (int i = 0; i < TOP_LIST_SIZE; i++) {
            stringBuilder.append(topPlayers.get(i)).append("\n");
        }

        return stringBuilder;
    }

    public static CharSequence getListScoresColumn() {
        stringBuilder.setLength(0);
        for (int i = 0; i < TOP_LIST_SIZE; i++) {
            stringBuilder.append(topScores.get(i)).append("\n");
        }

        return stringBuilder;
    }

    public static int getMinScore() {
        return topScores.getLast();
    }
}
