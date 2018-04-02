package com.mygdx.game;

import com.badlogic.gdx.Gdx;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.Writer;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;

public class HighScoreSystem {

    private static final String TOP_SCORE_FILE_NAME = "score.dat";
    private static final String ONE_SPACE = " ";

    public static void saveResult(LinkedList<String> topPlayers, LinkedList<Integer> topScores) {
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

    public static void loadResult(LinkedList<String> topPlayers, LinkedList<Integer> topScores) {
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
                    } catch (NumberFormatException e) {
                        e.printStackTrace();
                    }
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
}
