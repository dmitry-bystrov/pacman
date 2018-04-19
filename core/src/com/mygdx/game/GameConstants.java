package com.mygdx.game;

public interface GameConstants {
    int MAX_LIVES = 3;
    int WORLD_CELL_PX = 80;
    float BASE_SPEED = 220;
    int FRUITS_DROP_FREQUENCY = 20;

    int VIEWPORT_WIDTH = 1280;
    int VIEWPORT_HEIGHT = 720;
    int SECOND_SCREEN_Y0 = 0 - VIEWPORT_HEIGHT;

    enum ScreenType { MENU, GAME, LEVEL_COMPLETE}
    enum Action { WAITING, MOVING, DIEING, RECOVERING }

    enum GameSound {
        CLICK("click.ogg", 1),
        COIN("coin.wav", 0.4f),
        STAR("star.wav", 0.75f),
        FRUIT_APPEARANCE("fruit_appearance.wav", 0.5f),
        FRUIT_COLLECTED("fruit_collected.wav", 0.5f),
        GHOST("ghost.wav", 0.6f),
        GHOST_KILLED("ghost_killed.wav", 0.5f),
        PACMAN_KILLED("pacman_killed.wav", 0.5f),
        POWERED("powered.wav", 0.6f),
        TELEPORT("teleport.wav", 0.6f),
        FOOD("food.wav", 0.5f);

        private final String filename;
        private final float volume;

        GameSound(String filename, float volume) {
            this.filename = filename;
            this.volume = volume;
        }

        public String getFilename() {
            return filename;
        }

        public float getVolume() {
            return volume;
        }
    }

    enum GameLevel {
        LEVEL1("level1.map", "level1.dat", "Level 1"),
        LEVEL2("level2.map", "level2.dat", "Level 2"),
        LEVEL3("level3.map", "level3.dat", "Level 3"),
        LEVEL4("level4.map", "level4.dat", "Level 4"),
        LEVEL5("level5.map", "level5.dat", "Level 5"),
        LEVEL6("level6.map", "level6.dat", "Level 6"),
        LEVEL7("level7.map", "level7.dat", "Level 7"),
        LEVEL8("level8.map", "level8.dat", "Level 8");

        private final String mapFileName;
        private final String scoreFileName;
        private final String levelName;

        GameLevel(String mapFileName, String scoreFileName, String levelName) {
            this.mapFileName = mapFileName;
            this.scoreFileName = scoreFileName;
            this.levelName = levelName;
        }

        public String getMapFileName() {
            return mapFileName;
        }

        public String getScoreFileName() {
            return scoreFileName;
        }

        public String getLevelName() {
            return levelName;
        }

        public GameLevel getNext() {
            if (this.ordinal() == GameLevel.values().length - 1) return null;
            return GameLevel.values()[this.ordinal() + 1];
        }
    }

    enum Difficulty {
        NEWBIE   (2.8f, 10, 6, 1.0f, 0.4f, true),
        MIDDLE   (2.6f, 8,  5, 1.1f, 0.5f, true),
        EXPERT   (2.2f, 6,  4, 1.2f, 0.6f, true),
        NIGHTMARE(2.0f, 4,  3, 1.3f, 0.7f, true);

        private final float pacmanAttackTimer;
        private final int eatableGhostTimer;
        private final int recoveryTimer;
        private final float ghostsAcceleration;
        private final float ghostsDeceleration;
        private final boolean smartAI;

        Difficulty(float pacmanAttackTimer, int eatableGhostTimer, int recoveryTimer, float ghostsAcceleration, float ghostsDeceleration, boolean smartAI) {
            this.pacmanAttackTimer = pacmanAttackTimer;
            this.eatableGhostTimer = eatableGhostTimer;
            this.recoveryTimer = recoveryTimer;
            this.ghostsAcceleration = ghostsAcceleration;
            this.ghostsDeceleration = ghostsDeceleration;
            this.smartAI = smartAI;
        }

        public float getPacmanAttackTimer() {
            return pacmanAttackTimer;
        }

        public int getEatableGhostTimer() {
            return eatableGhostTimer;
        }

        public int getRecoveryTimer() {
            return recoveryTimer;
        }

        public float getGhostsAcceleration() {
            return ghostsAcceleration;
        }

        public float getGhostsDeceleration() {
            return ghostsDeceleration;
        }

        public boolean isSmartAI() {
            return smartAI;
        }

        public Difficulty getNext() {
            if (this.ordinal() == Difficulty.values().length - 1) return Difficulty.values()[0];
            return Difficulty.values()[this.ordinal() + 1];
        }
    }

    enum Direction {
        LEFT(-1, 0, 1), RIGHT(1, 0, 100), UP(0, 1, 10), DOWN(0, -1, 1000);

        private final int x;
        private final int y;
        private final int cost;

        Direction(int x, int y, int cost) {
            this.x = x;
            this.y = y;
            this.cost = cost;
        }

        public int getX() {
            return x;
        }

        public int getY() {
            return y;
        }

        public int getCost() {
            return cost;
        }

        public Direction invert() {
            switch (this) {
                case RIGHT: return LEFT;
                case LEFT: return RIGHT;
                case UP: return DOWN;
                case DOWN: return UP;
            }

            return null;
        }
    }

    enum GameObject {
        PACMAN      ('s', true,  false, 0, "pacman", 0),
        FOOD        ('_', false, true,  5, "food", 0),
        XFOOD       ('*', false, true,  20, "xfood", 0),
        BANANA      ('n', false, true,  200, "banana", 0),
        RED_GHOST   ('r', true,  false, 150, "ghosts", 0),
        GREEN_GHOST ('g', true,  false, 150, "ghosts", 1),
        APPLE       ('a', false, true,  200, "apple", 0),
        BLUE_GHOST  ('b', true,  false, 150, "ghosts", 2),
        PURPLE_GHOST('p', true,  false, 150, "ghosts", 3),
        ORANGE      ('o', false, true,  200, "orange", 0),
        PIPE        ('1', false, false, 0, "pipe", 0),
        STAR        ('2', false, false, 0, "stars", 0),
        HEART       ('3', false, false, 0, "heart", 0),
        EMPTY_CELL  ('0', false, false, 0, "ground", 0);

        private final char mapSymbol;
        private final boolean isCreature;
        private final boolean isFood;
        private final int score;
        private final String textureName;
        private final int textureRegionIndex;

        GameObject(char mapSymbol, boolean isCreature, boolean isFood, int score, String textureName, int textureRegionIndex) {
            this.mapSymbol = mapSymbol;
            this.isCreature = isCreature;
            this.isFood = isFood;
            this.score = score;
            this.textureName = textureName;
            this.textureRegionIndex = textureRegionIndex;
        }

        public static GameObject getObject(char symbol) {
            for (GameObject o: GameObject.values()) {
                if (o.getMapSymbol() == symbol) return o;
            }
            return GameObject.EMPTY_CELL;
        }

        public char getMapSymbol() {
            return mapSymbol;
        }

        public boolean isCreature() {
            return isCreature;
        }

        public boolean isFood() {
            return isFood;
        }

        public int getScore() {
            return score;
        }

        public String getTextureName() {
            return textureName;
        }

        public int getTextureRegionIndex() {
            return textureRegionIndex;
        }
    }
}
