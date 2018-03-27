package com.mygdx.game;

public interface GameConstants {
    int MAX_LIVES = 3;
    int WORLD_CELL_PX = 80;
    float BASE_SPEED = 200;
    int EATABLE_GHOSTS_TIMER = 5;
    int PACMAN_ATTACK_TIMER = 1;
    int GHOST_RECOVERY_TIMER = 5;
    int VIEWPORT_WIDTH = 1280;
    int VIEWPORT_HEIGHT = 720;

    enum ScreenType { MENU, GAME, GAME_OVER }
    enum Action { WAITING, MOVING, DIEING, RECOVERING }

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
    }

    enum GameObject {
        PACMAN('s', true, false, 1.0f, 0, "pacman", 0),
        FOOD('_', false, true, .0f, 5, "food", 0),
        XFOOD('*', false, true, .0f, 10, "xfood", 0),
        RED_GHOST('r', true, false, 1.4f, 50, "ghosts", 0),
        GREEN_GHOST('g', true, false, 1.3f, 40, "ghosts", 1),
        BLUE_GHOST('b', true, false, 1.2f, 30, "ghosts", 2),
        PURPLE_GHOST('p', true, false, 1.1f, 20, "ghosts", 3),
        WALL('1', false, false, .0f, 0, "wall", 0),
        EMPTY_CELL('0', false, false, .0f, 0, "ground", 0);

        private final char mapSymbol;
        private final boolean isCreature;
        private final boolean isFood;
        private final float speed;
        private final int score;
        private final String textureName;
        private final int textureRegionIndex;

        GameObject(char mapSymbol, boolean isCreature, boolean isFood, float speed, int score, String textureName, int textureRegionIndex) {
            this.mapSymbol = mapSymbol;
            this.isCreature = isCreature;
            this.isFood = isFood;
            this.speed = speed;
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

        public float getSpeed() {
            return speed;
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
