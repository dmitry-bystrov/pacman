package com.mygdx.game;

public interface GameConstants {
    int MAX_LIVES = 3;
    int WORLD_CELL_PX = 80;
    float BASE_SPEED = 220;
    int FRUITS_DROP_FREQUENCY = 20;

    int VIEWPORT_WIDTH = 1280;
    int VIEWPORT_HEIGHT = 720;

    enum ScreenType { MENU, GAME, LEVEL_COMPLETE}
    enum Action { WAITING, MOVING, DIEING, RECOVERING }

    enum Difficulty {
        NEWBIE(3.0f, 8, 6, 1.0f, 0.4f, false),
        MIDDLE(3.0f, 6, 5, 1.2f, 0.6f, true),
        EXPERT(2.5f, 4, 4, 1.4f, 0.8f, true),
        NIGHTMARE(2.0f, 2, 3, 1.6f, 1.0f, true);

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
        PACMAN('s', true, false, 0, "pacman", 0),
        FOOD('_', false, true, 5, "food", 0),
        XFOOD('*', false, true, 50, "xfood", 0),
        BANANA('n', false, true, 200, "banana", 0),
        RED_GHOST('r', true, false, 100, "ghosts", 0),
        GREEN_GHOST('g', true, false, 100, "ghosts", 1),
        APPLE('a', false, true, 200, "apple", 0),
        BLUE_GHOST('b', true, false, 100, "ghosts", 2),
        PURPLE_GHOST('p', true, false, 100, "ghosts", 3),
        ORANGE('o', false, true, 200, "orange", 0),
        PIPE('1', false, false, 0, "pipe", 0),
        STAR('2', false, false, 0, "stars", 0),
        HEART('3', false, false, 0, "heart", 0),
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
