package com.mygdx.game.gui;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.mygdx.game.screens.MenuScreen;
import com.mygdx.game.screens.ScreenManager;

public class MenuGUI extends SimpleGUI {

    private MenuScreen menuScreen;
    private Button btnNewGame;
    private Button btnExitGame;
    private Button btnMenu;

    public MenuGUI(MenuScreen menuScreen) {
        super(menuScreen.getBatch());
        this.menuScreen = menuScreen;
        setupLevelButtons();
    }

    @Override
    protected void setupSkin() {
        super.setupSkin();

        TextButton.TextButtonStyle textButtonMiddleStyle = new TextButton.TextButtonStyle();
        textButtonMiddleStyle.up = skin.getDrawable("middleButton");
        textButtonMiddleStyle.font = font32;
        skin.add("middleButtonSkin", textButtonMiddleStyle);

        TextButton.TextButtonStyle textButtonGreyStyle = new TextButton.TextButtonStyle();
        textButtonGreyStyle.up = skin.getDrawable("middleButtonGrey");
        textButtonGreyStyle.font = font32;
        skin.add("middleButtonGreySkin", textButtonGreyStyle);
    }

    @Override
    protected void setupStage() {
        super.setupStage();

        btnNewGame = new TextButton("Start New Game", skin, "simpleSkin");
        btnExitGame = new TextButton("Exit Game", skin, "simpleSkin");
        btnMenu = new TextButton("Return To Menu", skin, "simpleSkin");
        btnNewGame.setPosition(VIEWPORT_WIDTH / 2 - 160, 170);
        btnExitGame.setPosition(VIEWPORT_WIDTH / 2 - 160, 60);
        btnMenu.setPosition(VIEWPORT_WIDTH / 2 - 160, SECOND_SCREEN_Y0 + 20);

        stage.addActor(btnNewGame);
        stage.addActor(btnExitGame);
        stage.addActor(btnMenu);
    }

    private void setupLevelButtons() {
        int levelNumber;
        boolean unlocked = true;

        for (int y = 0; y < 2; y++) {
            for (int x = 0; x < 4; x++) {
                levelNumber = x + 1 + (4 * y);
                if (levelNumber > 1) unlocked = menuScreen.getLevelStars()[levelNumber - 2] > 0;
                Button button = new TextButton("Play", skin, unlocked?"middleButtonSkin":"middleButtonGreySkin");
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
