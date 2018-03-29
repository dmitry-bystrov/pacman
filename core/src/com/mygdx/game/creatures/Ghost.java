package com.mygdx.game.creatures;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.mygdx.game.Assets;
import com.mygdx.game.GameMap;

import java.util.LinkedList;
import java.util.List;

public class Ghost extends Creature {
    public static final int ROUTE_TARGET_CELL = -100;
    public static final int ROUTE_EMPTY_CELL = -9;
    public static final int ROUTE_WALL_CELL = -1;

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
        if (routingMode == RoutingMode.SMART_ROUTING) return;

        targetPosition.set(targetCell).scl(SIZE);
        routingMode = RoutingMode.SIMPLE_ROUTING;
    }

    public void setEatable(boolean eatable) {
        this.routingMode = RoutingMode.RANDOM_DIRECTION;

        if (eatable) {
            textureRegions = eatableTextureRegions;
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

        if (routingMode != RoutingMode.RANDOM_DIRECTION && targetPosition.dst(currentWorldPosition) < HALF_SIZE) {
            routingMode = RoutingMode.RANDOM_DIRECTION;
        }

        if (routingMode == RoutingMode.SIMPLE_ROUTING) {
            float shortestDistance = targetPosition.dst(currentWorldPosition.x, currentWorldPosition.y);
            Direction bestDirection = null;

            for (Direction direction:Direction.values()) {
                if (gameMap.isCellEmpty((int)currentMapPosition.x + direction.getX(),(int)currentMapPosition.y + direction.getY())) {
                    float newDirectionDistance = targetPosition.dst(currentWorldPosition.x + direction.getX() * SIZE, currentWorldPosition.y + direction.getX() * SIZE);
                    if (newDirectionDistance < shortestDistance) {
                        shortestDistance = newDirectionDistance;
                        bestDirection = direction;
                    }
                }
            }

            if (bestDirection != null) {
                directionVector.x = bestDirection.getX();
                directionVector.y = bestDirection.getY();
            } else {
                // призрак перебрал все направления и оказалось, что куда бы он ни пошел, расстояние до пакмана будет увеличиваться
                // если призрак умный, то он поймёт, что перед ним препятствие и попытается его обойти
                // в противном случае он просто выберет какое-нибудь случайное направление и пойдёт туда
                if (difficulty.isSmartAI()) {
                    routeToTarget(targetPosition);
                } else {
                    routingMode = RoutingMode.RANDOM_DIRECTION;
                }
            }
        }

        if (routingMode == RoutingMode.SMART_ROUTING && route.size() > 0) {
            directionVector.x = route.get(route.size() - 1).getX();
            directionVector.y = route.get(route.size() - 1).getY();
            route.remove(route.size() - 1);
        }

        if (routingMode == RoutingMode.RANDOM_DIRECTION) getRandomDirection();
    }

    private void routeToTarget(Vector2 targetPosition) {
        route.clear();
        for (int i = 0; i < routeMap.length; i++) {
            for (int j = 0; j < routeMap[i].length; j++) {
                routeMap[i][j] = ROUTE_EMPTY_CELL;
            }
        }

        routeMap[(int)targetPosition.x / SIZE][(int)targetPosition.y / SIZE] = ROUTE_TARGET_CELL;
        routeMap[(int) currentMapPosition.x][(int) currentMapPosition.y] = 0;

        fillRouteMap((int) currentMapPosition.x, (int) currentMapPosition.y, routeMap, 1);
        routingMode = RoutingMode.SMART_ROUTING;
    }

    private boolean fillRouteMap(int cellX, int cellY, int routeMap[][], int step) {
        for (Direction direction:Direction.values()) {
            int x = cellX + direction.getX();
            int y = cellY + direction.getY();

            if (x < 0 || y < 0 || x >= routeMap.length || y >= routeMap[x].length) continue;

            if (routeMap[x][y] == ROUTE_TARGET_CELL) {
                fillRouteList(x, y, routeMap, step);
                return true;
            }
            if (routeMap[x][y] == ROUTE_EMPTY_CELL) {
                if (gameMap.isCellEmpty(x, y)) {
                    routeMap[x][y] = step;
                } else {
                    routeMap[x][y] = ROUTE_WALL_CELL;
                    continue;
                }

                if (fillRouteMap(x, y, routeMap, step + 1)) return true;
            }
        }

        return false;
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
