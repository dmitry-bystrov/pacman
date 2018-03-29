package com.mygdx.game.creatures;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.mygdx.game.Assets;
import com.mygdx.game.GameMap;

import java.util.LinkedList;
import java.util.List;

public class Ghost extends Creature {
    private static final int ROUTE_TARGET_CELL = -100;
    private static final int ROUTE_EMPTY_CELL = -9;
    private static final int ROUTE_WALL_CELL = -1;

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

    // когда призрак получает координаты ячейки, в которую он должен перейти,
    // выбираем алгоритм поиска пути в зависимости от настроек уровня сложности
    public void setTargetCell(Vector2 targetCell) {
        if (difficulty.isSmartAI()) {
            findRouteToTargetCell(targetCell);
            routingMode = RoutingMode.SMART_ROUTING;
        } else {
            targetPosition.set(targetCell).scl(SIZE);
            routingMode = RoutingMode.SIMPLE_ROUTING;
        }
    }

    public void setEatable(boolean eatable) {
        routingMode = RoutingMode.RANDOM_DIRECTION;
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

        // в зависимости от уровня сложности и текущей игровой ситуации, призрак может использовать один
        // из трёх алгоритмов для выбора направления движения
        //
        // RANDOM_DIRECTION - выбор направления случайным образом
        //
        // SIMPLE_ROUTING - призрак перебирает все возможные направления и выбирает то, движение в котором
        // сократит расстояние между ним и целевой ячейкой (если на пути движения втретится препятствие,
        // обойти его он не сможет, а просто выберет какое-нибудь случайное направление и пойдёт туда)
        //
        // SMART_ROUTING - призрак строит карту маршрута, выбирает самый кратчайший путь до цели и следует по нему

        switch (routingMode) {
            case SIMPLE_ROUTING:
                float shortestDistance = targetPosition.dst(currentWorldPosition.x, currentWorldPosition.y);
                float newDirectionDistance;
                int bestDirection = -1;

                for (Direction direction:Direction.values()) {
                    if (gameMap.isCellEmpty((int)currentMapPosition.x + direction.getX(),(int)currentMapPosition.y + direction.getY())) {
                        newDirectionDistance = targetPosition.dst(currentWorldPosition.x + direction.getX() * SIZE, currentWorldPosition.y + direction.getX() * SIZE);
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

    // очищаем маршрутный лист, подготавливаем карту и строим новый маршрут
    private void findRouteToTargetCell(Vector2 targetPosition) {
        route.clear();
        for (int i = 0; i < routeMap.length; i++) {
            for (int j = 0; j < routeMap[i].length; j++) {
                routeMap[i][j] = ROUTE_EMPTY_CELL;
            }
        }

        int startPointX = (int) destinationPoint.x / SIZE;
        int startPointY = (int) destinationPoint.y / SIZE;

        routeMap[(int)targetPosition.x][(int)targetPosition.y] = ROUTE_TARGET_CELL;
        routeMap[startPointX][startPointY] = 0;

        fillRouteMap(startPointX, startPointY, routeMap, 1);
    }

    // рекурсивный обход карты для поиска кратчайшего маршрута
    private boolean fillRouteMap(int cellX, int cellY, int routeMap[][], int step) {
        int x;
        int y;

        for (Direction direction:Direction.values()) {
            x = cellX + direction.getX();
            y = cellY + direction.getY();

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

    // когда маршрут найден - проходим по нему в обратном порядке и заполняем маршрутный лист
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

    // выбор случайного направления
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
