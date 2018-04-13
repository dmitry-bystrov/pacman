package com.mygdx.game.gui;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.mygdx.game.GameSettings;
import com.mygdx.game.screens.MenuScreen;
import com.mygdx.game.screens.ScreenManager;

public class MenuGUI extends SimpleGUI {

    public static final String MUSIC_OFF = "Music Off";
    public static final String MUSIC_ON = "Music On";
    public static final String SOUNDS_ON = "Sounds On";
    public static final String SOUNDS_OFF = "Sounds Off";
    public static final String PLAY_BUTTON_TEXT = "Play";
    private MenuScreen menuScreen;
    private Button btnNewGame;
    private Button btnExitGame;
    private Button btnMenu;
    private Button btnSettings;

    private Image image;
    private Group flowPanel;
    private TextButton btnDifficulty;
    private TextButton btnMusic;
    private TextButton btnSounds;
    private Button btnSaveSettings;

    public MenuGUI(MenuScreen menuScreen) {
        super(menuScreen.getBatch());
        this.menuScreen = menuScreen;
        setupLevelButtons();
    }

    @Override
    protected void setupSkin() {
        super.setupSkin();

        Pixmap pixmap = new Pixmap(400, 500, Pixmap.Format.RGB888);
        pixmap.setColor(0.0f, 0.0f, 0.3f, 1.0f);
        pixmap.fill();
        Texture texturePanel = new Texture(pixmap);
        skin.add("texturePanel", texturePanel);

        TextButton.TextButtonStyle textButtonGearStyle = new TextButton.TextButtonStyle();
        textButtonGearStyle.up = skin.getDrawable("gearButton");
        textButtonGearStyle.font = font32;
        skin.add(GEAR_BUTTON_SKIN, textButtonGearStyle);

        TextButton.TextButtonStyle textButtonMiddleStyle = new TextButton.TextButtonStyle();
        textButtonMiddleStyle.up = skin.getDrawable("middleButton");
        textButtonMiddleStyle.font = font32;
        skin.add(MIDDLE_BUTTON_SKIN, textButtonMiddleStyle);

        TextButton.TextButtonStyle textButtonGreyStyle = new TextButton.TextButtonStyle();
        textButtonGreyStyle.up = skin.getDrawable("middleButtonGrey");
        textButtonGreyStyle.font = font32;
        skin.add(MIDDLE_BUTTON_GREY_SKIN, textButtonGreyStyle);
    }

    @Override
    protected void setupStage() {
        super.setupStage();

        btnNewGame = new TextButton("Start New Game", skin, SIMPLE_SKIN);
        btnExitGame = new TextButton("Exit Game", skin, SIMPLE_SKIN);
        btnMenu = new TextButton("Return To Menu", skin, SIMPLE_SKIN);
        btnSettings = new TextButton("", skin, GEAR_BUTTON_SKIN);
        btnNewGame.setPosition(VIEWPORT_WIDTH / 2 - 160, 240);
        btnExitGame.setPosition(VIEWPORT_WIDTH / 2 - 160, 120);
        btnMenu.setPosition(VIEWPORT_WIDTH / 2 - 160, SECOND_SCREEN_Y0 + 20);
        btnSettings.setPosition(VIEWPORT_WIDTH - 100, 20);

        stage.addActor(btnNewGame);
        stage.addActor(btnExitGame);
        stage.addActor(btnMenu);
        stage.addActor(btnSettings);

        flowPanel = new Group();
        flowPanel.setVisible(false);
        flowPanel.setPosition(VIEWPORT_WIDTH / 2 - 200, VIEWPORT_HEIGHT / 2 - 300);

        image = new Image(skin, "texturePanel");
        btnDifficulty = new TextButton(GameSettings.getDifficulty().toString(), skin, MIDDLE_BUTTON_SKIN);
        btnMusic = new TextButton(GameSettings.isMusic()? MUSIC_ON : MUSIC_OFF, skin, GameSettings.isMusic()? MIDDLE_BUTTON_SKIN : MIDDLE_BUTTON_GREY_SKIN);
        btnSounds = new TextButton(GameSettings.isSounds()? SOUNDS_ON : SOUNDS_OFF, skin, GameSettings.isSounds()? MIDDLE_BUTTON_SKIN : MIDDLE_BUTTON_GREY_SKIN);
        btnSaveSettings = new TextButton("Return", skin, MIDDLE_BUTTON_SKIN);

        btnDifficulty.setPosition(60, 380);
        btnMusic.setPosition(60, 280);
        btnSounds.setPosition(60, 180);
        btnSaveSettings.setPosition(60, 50);

        flowPanel.addActor(image);
        flowPanel.addActor(btnDifficulty);
        flowPanel.addActor(btnMusic);
        flowPanel.addActor(btnSounds);
        flowPanel.addActor(btnSaveSettings);

        stage.addActor(flowPanel);
    }

    private void setupLevelButtons() {
        int levelNumber;
        boolean unlocked = true;

        for (int i = 0; i < stage.getActors().size; i++) {
            if (stage.getActors().get(i).getClass() == TextButton.class) {
                if (((TextButton)stage.getActors().get(i)).getText().equals(PLAY_BUTTON_TEXT)) {
                    stage.getActors().removeIndex(i);
                }
            }
        }

        for (int y = 0; y < 2; y++) {
            for (int x = 0; x < 4; x++) {
                levelNumber = x + 1 + (4 * y);
                if (levelNumber > 1) unlocked = menuScreen.getLevelStars()[levelNumber - 2] > 0;
                Button button = new TextButton(PLAY_BUTTON_TEXT, skin, unlocked?"middleButtonSkin":"middleButtonGreySkin");
                button.setPosition(35 + x * 310, SECOND_SCREEN_Y0 + VIEWPORT_HEIGHT - 260 - y * 290);
                stage.addActor(button);

                if (unlocked) {
                    final int lvl = levelNumber - 1;
                    button.addListener(new ChangeListener() {
                        @Override
                        public void changed(ChangeEvent event, Actor actor) {
                            ScreenManager.getInstance().setGameLevel(GameLevel.values()[lvl]);
                            ScreenManager.getInstance().changeScreen(ScreenManager.ScreenType.GAME);
                        }
                    });
                }
            }
        }
    }

    @Override
    protected void setupListeners() {

        btnDifficulty.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                GameSettings.setDifficulty(GameSettings.getDifficulty().getNext());
                btnDifficulty.setText(GameSettings.getDifficulty().toString());
            }
        });

        btnMusic.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                GameSettings.setMusic(!GameSettings.isMusic());
                btnMusic.setText(GameSettings.isMusic()? MUSIC_ON : MUSIC_OFF);
                btnMusic.setStyle(skin.get(GameSettings.isMusic()? MIDDLE_BUTTON_SKIN : MIDDLE_BUTTON_GREY_SKIN, TextButton.TextButtonStyle.class));
            }
        });

        btnSounds.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                GameSettings.setSounds(!GameSettings.isSounds());
                btnSounds.setText(GameSettings.isSounds()? SOUNDS_ON : SOUNDS_OFF);
                btnSounds.setStyle(skin.get(GameSettings.isSounds()? MIDDLE_BUTTON_SKIN : MIDDLE_BUTTON_GREY_SKIN, TextButton.TextButtonStyle.class));
            }
        });

        btnSettings.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                flowPanel.setVisible(true);
                btnSettings.setVisible(false);
            }
        });

        btnSaveSettings.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                GameSettings.saveSettings();
                flowPanel.setVisible(false);
                btnSettings.setVisible(true);
                menuScreen.loadLevelStarts();
                setupLevelButtons();
            }
        });

        btnNewGame.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                menuScreen.moveCameraDown();
            }
        });

        btnExitGame.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                Gdx.app.exit();
            }
        });

        btnMenu.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                menuScreen.moveCameraUp();
            }
        });

    }
}
