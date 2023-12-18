package application;

import java.util.Random;
import org.apache.logging.log4j.*;

/**
 * Класс Player содержит информацию о пизиции игрока и методы для управления его
 * действиями в игре.
 */
public class Player {
    public int numberOfUnits = 3;
    public Cell currentCell;
    public int[] position = new int[2];
    public String myFlag = "Player", opFlag = "PlayerAi";
    public GameElements gameElements;
    public Random random = new Random();
    public Audio audio = new Audio();
    private static final Logger logger = LogManager.getLogger(Player.class.getName());

    boolean noJar = !Main.class.getProtectionDomain().getCodeSource().getLocation().getPath().endsWith(".jar");

    /**
     * Конструктор класса Player.
     *
     * @param gameElements Объект игровых элементов, содержащий карту.
     */
    public Player(GameElements gameElements) {
        this.gameElements = gameElements;
        currentCell = gameElements.map[gameElements.position[0]][gameElements.position[1]];
        position = gameElements.position;
    }

    /**
     * Пытается добыь воду в текущей ячейке. Увеличивает количество воды в ячейке и
     * проигрывает случайные реплики. В случае ошибки (ячейка не содержит воды)
     * проигрывает звук ошибки.
     */
    public void draw_Water() {
        if (currentCell.waterCell) {
            currentCell.numberOfWater += 2;
            randomReplicas(0.03);
            logger.info("[" + myFlag + "]" + " Набирает воду.");
        } else {
            errorReplicas();
            logger.error("[" + myFlag + "]" + " Не может набрать воду.");
        }
    }

    /**
     * Пытается полить рис в текущей ячейке. Уменьшает количество воды и увеличивает
     * количество риса в ячейке. Проигрывает случайные реплики. В случае ошибки
     * (ячейка не содержит риса или не хватает воды) проигрывает звук ошибки.
     */
    public void waterTheRice() {
        if (currentCell.riceCell && currentCell.numberOfWater >= 1) {
            currentCell.numberOfWater--;
            currentCell.numberOfRice += 4;
            randomReplicas(0.03);
            logger.info("[" + myFlag + "]" + " Поливает рис.");
        } else {
            errorReplicas();
            logger.error("[" + myFlag + "]" + " Не может полить рис.");
        }
    }

    /**
     * Пытается построить дом в текущей ячейке. Уменьшает количество воды, риса и
     * юнитов, увеличивает количество домов. Проигрывает случайные реплики. В случае
     * ошибки (недостаточно ресурсов или места для дома) проигрывает звук ошибки.
     */
    public void buildHouse() {
        if (currentCell.numberOfHouses < currentCell.needOfHouses && currentCell.numberOfWater >= 4
                && currentCell.numberOfRice >= 8 && numberOfUnits >= 2) {
            currentCell.numberOfWater -= 4;
            currentCell.numberOfRice -= 8;
            numberOfUnits -= 2;
            currentCell.numberOfHouses++;
            if (currentCell.numberOfHouses == currentCell.needOfHouses)
                currentCell.ownerFlag = myFlag;
            randomReplicas(0.03);
            logger.info("[" + myFlag + "]" + " Построил дом.");
        } else {
            errorReplicas();
            logger.error("[" + myFlag + "]" + " Не может построить дом.");
        }
    }

    /**
     * Пытается добыть воду во всех ячейках, принадлежащих игроку. Увеличивает
     * количество воды в каждой ячейке. Проигрывает случайные реплики.
     */
    public void drawAllWater() {
        for (int i = 0; i < gameElements.size; i++) {
            for (int j = 0; j < gameElements.size; j++) {
                gameElements.map[i][j].numberOfWater += (gameElements.map[i][j].ownerFlag.equals(myFlag)
                        && gameElements.map[i][j].waterCell) ? 1 : 0;
            }
        }
        randomReplicas(0.03);
        logger.info("[" + myFlag + "]" + " Набирает всю воду.");
    }

    /**
     * Пытается полить рис во всех ячейках, принадлежащих игроку. Уменьшает
     * количество воды и увеличивает количество риса в каждой ячейке. Проигрывает
     * случайные реплики.
     */
    public void waterAllRice() {
        for (int i = 0; i < gameElements.size; i++) {
            for (int j = 0; j < gameElements.size; j++) {
                if (gameElements.map[i][j].ownerFlag.equals(myFlag) && gameElements.map[i][j].riceCell
                        && gameElements.map[i][j].numberOfWater >= 2) {
                    gameElements.map[i][j].numberOfWater -= 2;
                    gameElements.map[i][j].numberOfRice += 4;
                }
            }
        }
        randomReplicas(0.03);
        logger.info("[" + myFlag + "]" + " Набирает весь рис.");
    }

    /**
     * Переносит все ресурсы (воду и рис) из остальных ячеек, принадлежащих игроку,
     * в текущую ячейку. Проигрывает с некоторой вероятностью звук "в кучу.wav".
     */
    public void allResourcesToPosition() {
        for (int i = 0; i < gameElements.size; i++) {
            for (int j = 0; j < gameElements.size; j++) {
                if (gameElements.map[i][j].ownerFlag.equals(myFlag) && (i != position[0] || j != position[1])) {
                    currentCell.numberOfWater += gameElements.map[i][j].numberOfWater;
                    gameElements.map[i][j].numberOfWater = 0;
                    currentCell.numberOfRice += gameElements.map[i][j].numberOfRice;
                    gameElements.map[i][j].numberOfRice = 0;
                }
            }
        }
        if (myFlag.equals("Player") && random.nextBoolean() && noJar)
            audio.playMusic("/pudge/в кучу.wav");
        logger.info("[" + myFlag + "]" + " Переносит ресрсы в позицию.");
    }

    /**
     * Пытается построить защиту в текущей ячейке. Уменьшает количество воды, риса и
     * юнитов, устанавливает защиту в ячейке. Проигрывает случайные реплики. В
     * случае ошибки (недостаточно ресурсов) проигрывает звук ошибки.
     */
    public void protectPosition() {
        if (currentCell.numberOfRice >= 32 && currentCell.numberOfWater >= 16 && numberOfUnits >= 16
                && currentCell.ownerFlag.equals(myFlag)) {
            currentCell.numberOfRice -= 32;
            currentCell.numberOfWater -= 16;
            numberOfUnits -= 16;
            currentCell.protection = true;
            randomReplicas(0.8);
            logger.info("[" + myFlag + "]" + " Защищает позицию.");
        } else {
            errorReplicas();
            logger.error("[" + myFlag + "]" + " Не может защитить позицию.");
        }
    }

    /**
     * Проверяет возможность перемещения в указанном направлении (вверх, вниз,
     * влево, вправо). Условия включают в себя наличие юнитов, принадлежность ячейки
     * игроку, отсутствие защиты в ячейке, а так же существование ячейки.
     *
     * @param WDAS Направление движения (w - вверх, d - вправо, a - влево, s -
     *             вниз).
     * @return true, если перемещение возможно, иначе false.
     */
    public boolean canMoveToCell(String WDAS) {
        switch (WDAS) {
            case "w":
                return position[0] - 1 >= 0
                        && (currentCell.ownerFlag.equals(myFlag)
                        || gameElements.map[position[0] - 1][position[1]].ownerFlag.equals(myFlag))
                        && numberOfUnits > 0 && (!(gameElements.map[position[0] - 1][position[1]].protection)
                        || gameElements.map[position[0] - 1][position[1]].ownerFlag.equals(myFlag));
            case "d":
                return position[1] + 1 < gameElements.size
                        && (currentCell.ownerFlag.equals(myFlag)
                        || gameElements.map[position[0]][position[1] + 1].ownerFlag.equals(myFlag))
                        && numberOfUnits > 0 && (!(gameElements.map[position[0]][position[1] + 1].protection)
                        || gameElements.map[position[0]][position[1] + 1].ownerFlag.equals(myFlag));
            case "a":
                return position[1] - 1 >= 0
                        && (currentCell.ownerFlag.equals(myFlag)
                        || gameElements.map[position[0]][position[1] - 1].ownerFlag.equals(myFlag))
                        && numberOfUnits > 0 && (!(gameElements.map[position[0]][position[1] - 1].protection)
                        || gameElements.map[position[0]][position[1] - 1].ownerFlag.equals(myFlag));
            case "s":
                return position[0] + 1 < gameElements.size
                        && (currentCell.ownerFlag.equals(myFlag)
                        || gameElements.map[position[0] + 1][position[1]].ownerFlag.equals(myFlag))
                        && numberOfUnits > 0 && (!(gameElements.map[position[0] + 1][position[1]].protection)
                        || gameElements.map[position[0] + 1][position[1]].ownerFlag.equals(myFlag));
            default:
                return false;
        }
    }

    /**
     * Захватывает соседнюю ячейку, если она принадлежит противнику и количество
     * юнитов игрока больше двойного количества домов в ячейке. В случае успешного
     * захвата уменьшает количество юнитов игрока и делает ячейку свободной, обнуляя
     * количество домов. Если захваченная ячейка принадлежит противнику и количество
     * домов больше или равно двойному количеству юнитов игрока, игрок перемещается
     * назад.
     *
     * @param WDAS Направление движения (w - вверх, d - вправо, a - влево, s -
     *             вниз).
     */
    public void cellCapture(String WDAS) {
        if (currentCell.ownerFlag.equals(opFlag) && numberOfUnits > currentCell.numberOfHouses * 2) {
            numberOfUnits -= currentCell.numberOfHouses * 2;
            currentCell.numberOfHouses = 0;
            currentCell.ownerFlag = "Free";
        } else if (currentCell.ownerFlag.equals(opFlag) && currentCell.numberOfHouses * 2 >= numberOfUnits) {
            switch (WDAS) {
                case "w":
                    position[0]++;
                    break;
                case "d":
                    position[1]--;
                    break;
                case "a":
                    position[1]++;
                    break;
                case "s":
                    position[0]--;
                    break;
            }
        }
    }

    /**
     * Осуществляет перемещение игрока на новую территорию в указанном направлении
     * (вверх, вниз, влево, вправо). При перемещении проверяет возможность захвата
     * соседней ячейки. Проигрывает звук перемещения. В случае успешного захвата
     * обнуляет количество домов в захваченной ячейке и меняет владельца на "Free".
     *
     * @param move Направление перемещения (Вверх, Вниз, Влево, Вправо).
     */
    public void newTerritory(String move) {
        switch (move) {
            case "Вверх":
                if (canMoveToCell("w")) {
                    position[0]--;
                    currentCell = gameElements.map[position[0]][position[1]];
                    cellCapture("w");
                    if (myFlag.equals("Player") && random.nextDouble() <= 0.35 && noJar)
                        audio.playMusic("/pudge/выкатываюсь.wav");
                    logger.info("[" + myFlag + "]" + " Снемил позицию " + move + ".");
                } else {
                    logger.error("[" + myFlag + "]" + " Не смог сменить позицию " + move + ".");
                    errorReplicas();
                }
                break;
            case "Вправо":
                if (canMoveToCell("d")) {
                    position[1]++;
                    currentCell = gameElements.map[position[0]][position[1]];
                    cellCapture("d");
                    if (myFlag.equals("Player") && random.nextDouble() <= 0.35 && noJar)
                        audio.playMusic("/pudge/выкатываюсь.wav");
                    logger.info("[" + myFlag + "]" + " Снемил позицию " + move + ".");
                } else {
                    logger.error("[" + myFlag + "]" + " Не смог сменить позицию " + move + ".");
                    errorReplicas();
                }
                break;
            case "Влево":
                if (canMoveToCell("a")) {
                    position[1]--;
                    currentCell = gameElements.map[position[0]][position[1]];
                    cellCapture("a");
                    if (myFlag.equals("Player") && random.nextDouble() <= 0.35 && noJar)
                        audio.playMusic("/pudge/выкатываюсь.wav");
                    logger.info("[" + myFlag + "]" + " Снемил позицию " + move + ".");
                } else {
                    logger.error("[" + myFlag + "]" + " Не смог сменить позицию " + move + ".");
                    errorReplicas();
                }
                break;
            case "Вниз":
                if (canMoveToCell("s")) {
                    position[0]++;
                    currentCell = gameElements.map[position[0]][position[1]];
                    cellCapture("s");
                    if (myFlag.equals("Player") && random.nextDouble() <= 0.35 && noJar)
                        audio.playMusic("/pudge/выкатываюсь.wav");
                    logger.info("[" + myFlag + "]" + " Снемил позицию " + move + ".");
                } else {
                    logger.error("[" + myFlag + "]" + " Не смог сменить позицию " + move + ".");
                    errorReplicas();
                }
                break;
            default:
                logger.error("[" + myFlag + "]" + " Попытался сменить позицию неверно");
                errorReplicas();
                break;
        }
        currentCell = gameElements.map[position[0]][position[1]];
    }

    /**
     * Проигрывает случайные реплики об ошибке (недостатке ресурсов, места и др.).
     */
    private void errorReplicas() {
        if (noJar) {
            if (myFlag.equals("Player")) {
                double randomValue = random.nextDouble();
                if (randomValue <= 0.5 && myFlag.equals("Player")) {
                    audio.playMusic("/pudge/ой.wav");
                } else if (randomValue <= 0.75 && myFlag.equals("Player")) {
                    audio.playMusic("/pudge/я исправлюсь.wav");
                } else if (myFlag.equals("Player"))
                    audio.playMusic("/pudge/я специально.wav");
            }
        }
    }

    /**
     * Проигрывает случайные реплики в ответ на успешные действия.
     *
     * @param chance Вероятность проигрывания случайных звуков.
     */
    private void randomReplicas(double chance) {
        if (noJar) {
            if (chance >= random.nextDouble() && myFlag.equals("Player")) {
                double randomValue = random.nextDouble();
                if (randomValue <= 0.15) {
                    audio.playMusic("/pudge/животик урчит.wav");
                } else if (randomValue <= 0.3) {
                    audio.playMusic("/pudge/шмакодявка.wav");
                } else if (randomValue <= 0.5) {
                    audio.playMusic("/pudge/ребро.wav");
                } else if (randomValue <= 0.8)
                    audio.playMusic("/pudge/смех.wav");
            }
        }
    }
}
