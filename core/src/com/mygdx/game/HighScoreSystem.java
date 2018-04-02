package com.mygdx.game;

import com.badlogic.gdx.Gdx;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.Writer;
import java.util.LinkedList;
import java.util.List;

public class HighScoreSystem {

    public static final String TOP_SCORE_FILE_NAME = "score.dat";

    public static void saveResult(List<String> result) {
        Writer writer = null;
        try {
            writer = Gdx.files.local(TOP_SCORE_FILE_NAME).writer(false);
            for (int i = 0; i < result.size(); i++) {
                writer.write(result.get(i));
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

    public static void loadResult(List<String> result) {
        result.clear();

        if (Gdx.files.local(TOP_SCORE_FILE_NAME).exists()) {
            BufferedReader br = null;
            try {
                br = Gdx.files.local(TOP_SCORE_FILE_NAME).reader(8192);

                String str;
                while ((str = br.readLine()) != null) {
                    result.add(str);
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
