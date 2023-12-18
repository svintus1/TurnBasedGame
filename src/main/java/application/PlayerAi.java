package application;

import java.util.ArrayList;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Класс PlayerAi содержит информацию о пизиции противника и методы,
 * определяющие его поведение. Использует методы из класса Player, в некоторых
 * из них меняет потребление и получение ресурсов.
 */
public class PlayerAi extends Player {
    public double ym;
    private Player player;
    private static final Logger logger = LogManager.getLogger(PlayerAi.class.getName());

    /**
     * Конструктор класса PlayerAi. Инициализирует объект PlayerAi.
     *
     * @param gameElements Экземпляр класса GameElements, представляющий игровые
     *                     элементы.
     * @param player       Объект игрока.
     * @param ym           Вероятность выбора оптимального хода противника.
     */
    public PlayerAi(GameElements gameElements, Player player, double ym) {
        super(gameElements);
        super.myFlag = "PlayerAi";
        super.opFlag = "Player";
        super.currentCell = gameElements.map[gameElements.positionAi[0]][gameElements.positionAi[1]];
        super.position = gameElements.positionAi;
        this.player = player;
        this.ym = ym;
    }

    /**
     * Переопределенный метод для обработки действия "набрать всю воду" для всех
     * ячеек противника. Увеличивает количество воды в каждой водной ячейке на 2.
     */
    public void drawAllWater() {
        for (int i = 0; i < gameElements.size; i++) {
            for (int j = 0; j < gameElements.size; j++) {
                if (gameElements.map[i][j].ownerFlag.equals("PlayerAi") && gameElements.map[i][j].waterCell)
                    gameElements.map[i][j].numberOfWater += 2;
            }
        }
        logger.info("[" + myFlag + "]" + " Набирает всю воду.");
    }

    /**
     * Переопределенный метод для обработки действия "полить рис" для всех ячеек
     * противника. Уменьшает количество воды в каждой рисовой ячейке на 1 и
     * увеличивает количество риса на 4.
     */
    public void waterAllRice() {
        for (int i = 0; i < gameElements.size; i++) {
            for (int j = 0; j < gameElements.size; j++) {
                if (gameElements.map[i][j].ownerFlag.equals(myFlag) && gameElements.map[i][j].riceCell
                        && gameElements.map[i][j].numberOfWater >= 1) {
                    gameElements.map[i][j].numberOfWater--;
                    gameElements.map[i][j].numberOfRice += 4;
                }
            }
        }
        logger.info("[" + myFlag + "]" + " Поливает весь рис.");
    }

    /**
     * Переопределенный метод для обработки действия "защитить позицию" для текущей
     * ячейки противника. Уменьшает количество риса и воды, а также количество
     * юнитов для защиты позиции.
     */
    public void protectPosition() {
        if (currentCell.numberOfRice >= 8 && currentCell.numberOfWater >= 4 && numberOfUnits >= 8
                && currentCell.ownerFlag.equals(myFlag)) {
            currentCell.numberOfRice -= 8;
            currentCell.numberOfWater -= 4;
            numberOfUnits -= 8;
            currentCell.protection = true;
            logger.info("[" + myFlag + "]" + " Защищает позицию.");
        } else
            logger.error("[" + myFlag + "]" + " Не может защитить позицию.");
    }

    /**
     * Метод для вычисления оценки для возможного следующего хода в указанную
     * ячейку.
     *
     * @param nextRow Следующая строка.
     * @param nextCol Следующая колонка.
     * @return Оценка для указанной ячейки.
     */
    private int moveMark(int nextRow, int nextCol) {
        int mark = 0;
        if (nextRow >= 0 && nextRow < gameElements.size && nextCol >= 0 && nextCol < gameElements.size) {
            Cell nextPosition = gameElements.map[nextRow][nextCol];
            mark += currentCell.ownerFlag.equals("PlayerAi") && nextPosition.ownerFlag.equals("Free") ? 100 : 0;
            mark += currentCell.ownerFlag.equals("PlayerAi") && nextPosition.ownerFlag.equals("Player")
                    && numberOfUnits > nextPosition.needOfHouses * 2 ? 80 : 0;
            mark += currentCell.ownerFlag.equals("PlayerAi") && nextPosition.waterCell ? 30 : 0;
            mark += currentCell.ownerFlag.equals("PlayerAi") && nextPosition.riceCell ? 40 : 0;
            mark += currentCell.ownerFlag.equals("Player") && nextPosition.ownerFlag.equals("PlayerAi") ? 100 : 0;
            mark -= currentCell.ownerFlag.equals("PlayerAi") && !nextPosition.ownerFlag.equals("PlayerAi")
                    ? nextPosition.needOfHouses * 11
                    : 0;
            mark = currentCell.ownerFlag.equals("PlayerAi") && nextPosition.ownerFlag.equals("PlayerAi") ? 10 : mark;
            mark = currentCell.ownerFlag.equals("PlayerAi") && nextPosition.ownerFlag.equals("Player")
                    && numberOfUnits <= nextPosition.needOfHouses * 2 ? 0 : mark;
            mark = nextPosition.ownerFlag.equals("Player") && nextPosition.protection ? 0 : mark;
            mark = currentCell.ownerFlag.equals("Free") ? 11 : mark;
        }
        return mark;
    }

    /**
     * Метод для определения лучшего хода среди возможных. Возвращает массив,
     * содержащий два элемента: - первый элемент - случайный лучший ход (w, d, a,
     * s), - второй элемент - оценка лучшего хода.
     *
     * @return Массив строк с лучшим ходом и оценкой.
     */
    public String[] bestMove() {
        int bestMark = Integer.MIN_VALUE;
        ArrayList<String> bestMoves = new ArrayList<>();
        String[] WDAS = { "w", "d", "a", "s" };
        for (String i : WDAS) {
            int mark = 0;
            int nextRow = position[0];
            int nextCol = position[1];

            switch (i) {
                case "w":
                    nextRow--;
                    break;
                case "a":
                    nextCol--;
                    break;
                case "d":
                    nextCol++;
                    break;
                case "s":
                    nextRow++;
                    break;
            }

            mark = moveMark(nextRow, nextCol);

            if (mark > bestMark) {
                bestMoves.clear();
                bestMoves.add(i);
                bestMark = mark;
            } else if (mark == bestMark)
                bestMoves.add(i);
        }

        String[] result = new String[2];
        result[0] = bestMoves.get(random.nextInt(bestMoves.size()));
        result[1] = Integer.toString(bestMark);
        return result;
    }

    /**
     * Метод для выполнения хода.
     *
     * @param move Номер выбранного хода.
     */
    private void executionMove(int move) {
        switch (move) {
            case 1:
                draw_Water();
                break;
            case 2:
                waterTheRice();
                break;
            case 3:
                buildHouse();
                break;
            case 4:
                newTerritory();
                break;
            case 5:
                drawAllWater();
                break;
            case 6:
                waterAllRice();
                break;
            case 7:
                allResourcesToPosition();
                break;
            case 8:
                protectPosition();
                break;
        }
    }

    /**
     * Метод для выбора и выполнения лучшего хода. Выбирает случайный ход из лучших
     * и выполняет его.
     */
    public void makeMove() {
        int move = random.nextInt(8) + 1;
        if (currentCell.ownerFlag.equals("Free")) {
            if (currentCell.numberOfWater >= 4 && currentCell.numberOfRice >= 8 && numberOfUnits >= 2
                    && random.nextDouble() <= ym) {
                move = 3;
            } else if (currentCell.numberOfWater < 4 && random.nextDouble() <= ym) {
                move = (currentCell.waterCell ? 1
                        : (gameElements.map[0][gameElements.size - 1].numberOfWater < 4 ? 5 : 7));
            } else if (currentCell.numberOfRice < 8 && random.nextDouble() <= ym)
                move = (currentCell.riceCell ? 2
                        : (gameElements.map[0][gameElements.size - 1].numberOfWater < 2 ? 5
                        : (gameElements.map[0][gameElements.size - 1].numberOfRice < 8 ? 6 : 7)));
        } else if (currentCell.ownerFlag.equals("Player")) {
            move = 4;
        } else {
            if (currentCell.numberOfRice >= 8 && currentCell.numberOfWater >= 4 && numberOfUnits >= 8
                    && random.nextDouble() <= 0.8) {
                move = 8;
            } else if (numberOfUnits >= 1 && random.nextDouble() <= ym)
                move = 4;
        }
        if (player.position[0] == position[0] && player.position[1] == position[1]
                && currentCell.needOfHouses - 1 > currentCell.numberOfHouses) {
            move = random.nextBoolean() ? 5 : 6;
        } else if (player.position[0] == position[0] && player.position[1] == position[1]
                && currentCell.needOfHouses - 1 == currentCell.numberOfHouses && currentCell.numberOfWater >= 4
                && currentCell.numberOfRice >= 8 && numberOfUnits >= 2) {
            move = 3;
        } else if (player.position[0] == position[0] && player.position[1] == position[1]
                && currentCell.needOfHouses - 1 == currentCell.numberOfHouses)
            move = random.nextDouble() <= 0.55 ? 5 : 6;

        executionMove(move);
    }

    /**
     * Метод для освоения территории с учетом лучшего хода. Переопределенный метод
     * для добавления функциональности противнику.
     */
    public void newTerritory() {
        switch (bestMove()[0]) {
            case "w":
                if (canMoveToCell("w")) {
                    position[0]--;
                    currentCell = gameElements.map[position[0]][position[1]];
                    cellCapture("w");
                    logger.info("[" + myFlag + "]" + " Снемил позицию " + bestMove()[0] + ".");
                }
                break;
            case "d":
                if (canMoveToCell("d")) {
                    position[1]++;
                    currentCell = gameElements.map[position[0]][position[1]];
                    cellCapture("d");
                    logger.info("[" + myFlag + "]" + " Снемил позицию " + bestMove()[0] + ".");
                }
                break;
            case "a":
                if (canMoveToCell("a")) {
                    position[1]--;
                    currentCell = gameElements.map[position[0]][position[1]];
                    cellCapture("a");
                    logger.info("[" + myFlag + "]" + " Снемил позицию " + bestMove()[0] + ".");
                }
                break;
            case "s":
                if (canMoveToCell("s")) {
                    position[0]++;
                    currentCell = gameElements.map[position[0]][position[1]];
                    cellCapture("s");
                    logger.info("[" + myFlag + "]" + " Снемил позицию " + bestMove()[0] + ".");
                }
                break;
        }
        currentCell = gameElements.map[position[0]][position[1]];
    }

    /**
     * Метод для телепортации противника в случайную свободную ячейку на карте.
     * Используется для выполнения прыжка противником.
     */
    public void teleportation() {
        List<int[]> cellsToTeleport = new ArrayList<>();
        for (int i = 0; i < gameElements.size; i++) {
            for (int j = 0; j < gameElements.size; j++) {
                if (gameElements.map[i][j].ownerFlag.equals("Free")) {
                    int[] cellToTeleport = { i, j };
                    cellsToTeleport.add(cellToTeleport);
                }
            }
        }
        int[] cellToTeleport = cellsToTeleport.get(random.nextInt(cellsToTeleport.size()));
        position = cellToTeleport;
        currentCell = gameElements.map[position[0]][position[1]];
        logger.info("[" + myFlag + "]" + " Совершил прыжок.");
    }
}
