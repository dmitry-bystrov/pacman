package com.mygdx.game.creatures;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.mygdx.game.Assets;
import com.mygdx.game.GameMap;

import java.util.LinkedList;
import java.util.List;

public class Ghost extends Creature {
    private static final int ROUTE_TARGET_CELL = -5;
    private static final int ROUTE_EMPTY_CELL = -9;
    private static final int ROUTE_WALL_CELL = -1;
    private static final int MAX_ROUTE_STEP_COUNT = 50;
    private static final int ROUND_UP_DISTANCE = 5                     ;

    enum RoutingMode { RANDOM_DIRECTION, SIMPLE_ROUTING, SMART_ROUTING }

    private TextureRegion[] originalTextureRegions;
    private TextureRegion[] eatableTextureRegions;
    private Vector2 targetPosition;

    private RoutingMode routingMode;
    private List<Direction> route;
    private int routeMap[][];

    public Ghost(GameMap gameMap, GameObject gameObject, Difficulty difficulty) {
        super(gameMap, gameObject, difficulty);

        this.originalTextureRegions = textureRegions;
        this.eatableTextureRegions = Assets.getInstance().getAtlas().findRegion("ghosts").split(SIZE, SIZE)[4];
        this.targetPosition = new Vector2();
        this.secPerFrame = 0.3f;

        this.route = new LinkedList<>();
        this.routingMode = RoutingMode.RANDOM_DIRECTION;
        this.routeMap = new int[gameMap.getMapWidht()][gameMap.getMapHeight()];
    }

    public void setTargetCell(Vector2 targetCell) {
        if (gameMap.isOutOfBounds((int)targetCell.x, (int)targetCell.y)) return;

        if (difficulty.isSmartAI()) {
            roundUpTarget((int)targetCell.x, (int)targetCell.y);
            routingMode = RoutingMode.SMART_ROUTING;
        } else {
            targetPosition.set(targetCell).scl(SIZE);
            routingMode = RoutingMode.SIMPLE_ROUTING;
        }
    }

    private void roundUpTarget(int targetX, int targetY) {
        if (Vector2.dst(currentMapPosition.x, currentMapPosition.y, targetX, targetY) > ROUND_UP_DISTANCE) {
            Direction direction;

            do {
                direction = Direction.values()[MathUtils.random(3)];
            } while (!gameMap.isCellEmpty(targetX + direction.getX(),targetY + direction.getY()));

            int x;
            int y;
            int step = 0;

            do {
                step++;
                x = targetX + direction.getX() * step;
                y = targetY + direction.getY() * step;
                if (gameMap.isOutOfBounds(x + direction.getX(), y + direction.getY())) break;
            } while(gameMap.isCellEmpty(x + direction.getX(), y + direction.getY()) && step < ROUND_UP_DISTANCE);

            targetX = x;
            targetY = y;
        }

        findRouteToTargetCell(targetX, targetY);
    }

    public void setEatable(boolean eatable) {
        routingMode = RoutingMode.RANDOM_DIRECTION;
        if (eatable) {
            textureRegions = eatableTextureRegions;
            routingMode = RoutingMode.RANDOM_DIRECTION;
            currentSpeed = BASE_SPEED * difficulty.getGhostsDeceleration();
        } else {
            textureRegions = originalTextureRegions;
            currentSpeed = BASE_SPEED * difficulty.getGhostsAcceleration();
        }
    }

    @Override
    protected void getDirection() {
        directionVector.x = 0;
        directionVector.y = 0;

        switch (routingMode) {
            case SIMPLE_ROUTING:
                float shortestDistance = targetPosition.dst(currentWorldPosition.x, currentWorldPosition.y);
                float newDirectionDistance;
                int bestDirection = -1;

                for (Direction direction:Direction.values()) {
                    if (gameMap.isCellEmpty((int)currentMapPosition.x + direction.getX(),(int)currentMapPosition.y + direction.getY())) {
                        newDirectionDistance = targetPosition.dst(currentWorldPosition.x + direction.getX() * SIZE, currentWorldPosition.y + direction.getY() * SIZE);

                        if (newDirectionDistance < shortestDistance) {
                            shortestDistance = newDirectionDistance;
                            bestDirection = direction.ordinal();
                        }
                    }
                }

                if (bestDirection == -1) {
                    routingMode = RoutingMode.RANDOM_DIRECTION;
                } else {
                    directionVector.x = Direction.values()[bestDirection].getX();
                    directionVector.y = Direction.values()[bestDirection].getY();
                }
                break;
            case SMART_ROUTING:
                if (route.size() > 0) {
                    directionVector.x = route.get(route.size() - 1).getX();
                    directionVector.y = route.get(route.size() - 1).getY();
                    route.remove(route.size() - 1);
                } else {
                    routingMode = RoutingMode.RANDOM_DIRECTION;
                }
                break;
            case RANDOM_DIRECTION:
                getRandomDirection();
                break;
        }
    }

    private void findRouteToTargetCell(int targetX, int targetY) {
        route.clear();
        for (int i = 0; i < routeMap.length; i++) {
            for (int j = 0; j < routeMap[i].length; j++) {
                routeMap[i][j] = ROUTE_EMPTY_CELL;
            }
        }

        int startPointX = (int) destinationPoint.x / SIZE;
        int startPointY = (int) destinationPoint.y / SIZE;

        routeMap[targetX][targetY] = ROUTE_TARGET_CELL;
        if (routeMap[startPointX][startPointY] == ROUTE_TARGET_CELL) return;
        routeMap[startPointX][startPointY] = 0;

        fillRouteMap(routeMap);
    }

    private void fillRouteMap(int routeMap[][]) {
        int x;
        int y;

        for (int step = 1; step < MAX_ROUTE_STEP_COUNT; step++) {
            for (int i = 0; i < routeMap.length; i++) {
                for (int j = 0; j < routeMap[i].length; j++) {
                    if (routeMap[i][j] != step - 1) continue;

                    for (Direction direction : Direction.values()) {
                        x = i + direction.getX();
                        y = j + direction.getY();

                        if (x < 0 || y < 0 || x >= routeMap.length || y >= routeMap[x].length) continue;

                        if (routeMap[x][y] == ROUTE_TARGET_CELL) {
                            fillRouteList(x, y, routeMap, step);
                            return;
                        }

                        if (routeMap[x][y] == ROUTE_EMPTY_CELL) {
                            if (gameMap.isCellEmpty(x, y)) {
                                routeMap[x][y] = step;
                            } else {
                                routeMap[x][y] = ROUTE_WALL_CELL;
                            }
                        }
                    }
                }
            }
        }
    }

    private void fillRouteList(int cellX, int cellY, int[][] routeMap, int step) {
        int x;
        int y;

        for (int i = step - 1; i >= 0; i--) {
            for (Direction direction : Direction.values()) {
                x = cellX + direction.getX();
                y = cellY + direction.getY();

                if (x < 0 || y < 0 || x >= routeMap.length || y >= routeMap[x].length) continue;

                if (routeMap[x][y] == i) {
                    route.add(direction.invert());
                    cellX += direction.getX();
                    cellY += direction.getY();
                    break;
                }
            }
        }
    }

    private void getRandomDirection() {
        Direction direction;

        do {
            direction = Direction.values()[MathUtils.random(3)];
        } while (!gameMap.isCellEmpty((int)currentMapPosition.x + direction.getX(),(int)currentMapPosition.y + direction.getY()));

        directionVector.x = direction.getX();
        directionVector.y = direction.getY();
    }

    @Override
    protected void updateRotation() {
        if (directionVector.x == 1) rotation = 0;
        if (directionVector.y == 1) rotation = 0;
        if (directionVector.x == -1) rotation = 180;
        if (directionVector.y == -1) rotation = 0;
    }
}
