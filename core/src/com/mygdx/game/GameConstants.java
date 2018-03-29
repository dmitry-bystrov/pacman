package com.mygdx.game;

public interface GameConstants {
    int MAX_LIVES = 3;
    int WORLD_CELL_PX = 80;
    float BASE_SPEED = 220;

    int VIEWPORT_WIDTH = 1280;
    int VIEWPORT_HEIGHT = 720;

    enum ScreenType { MENU, GAME, GAME_OVER }
    enum Action { WAITING, MOVING, DIEING, RECOVERING }

    enum Difficulty {
        NEWBIE(4, 8, 8, 1.2f, 0.4f, false),
        MIDDLE(4, 6, 6, 1.4f, 0.6f, true),
        EXPERT(2, 4, 4, 1.6f, 0.8f, true),
        NIGHTMARE(1, 2, 2, 1.8f, 1.0f, true);

        private int pacmanAttackTimer;
        private int eatableGhostTimer;
        private int recoveryTimer;
        private float ghostsAcceleration;
        private float ghostsDeceleration;
        private boolean smartAI;

        Difficulty(int pacmanAttackTimer, int eatableGhostTimer, int recoveryTimer, float ghostsAcceleration, float ghostsDeceleration, boolean smartAI) {
            this.pacmanAttackTimer = pacmanAttackTimer;
            this.eatableGhostTimer = eatableGhostTimer;
            this.recoveryTimer = recoveryTimer;
            this.ghostsAcceleration = ghostsAcceleration;
            this.ghostsDeceleration = ghostsDeceleration;
            this.smartAI = smartAI;
        }

        public int getPacmanAttackTimer() {
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
    }

    enum Direction {
        LEFT(-1, 0), RIGHT(1, 0), UP(0, 1), DOWN(0, -1);

        private final int x;
        private final int y;

        Direction(int x, int y) {
            this.x = x;
            this.y = y;
        }

        public int getX() {
            return x;
        }

        public int getY() {
            return y;
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
        PACMAN('s', true, false, 0, "pacman", 0),
        FOOD('_', false, true, 5, "food", 0),
        XFOOD('*', false, true, 100, "xfood", 0),
        RED_GHOST('r', true, false, 500, "ghosts", 0),
        GREEN_GHOST('g', true, false, 400, "ghosts", 1),
        BLUE_GHOST('b', true, false, 300, "ghosts", 2),
        PURPLE_GHOST('p', true, false, 200, "ghosts", 3),
        WALL('1', false, false, 0, "wall", 0),
        EMPTY_CELL('0', false, false, 0, "ground", 0);

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
