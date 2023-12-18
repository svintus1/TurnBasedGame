package application;

import java.awt.image.BufferedImage;
import java.io.*;
import java.util.Random;
import javax.imageio.ImageIO;

/**
 * Класс GameElements представляет элементы игрового мира, такие как игровая
 * карта, изображения ячеек, и методы для их инициализации, создания изображений
 * и выполнения шагов нового дня в игре.
 */
public class GameElements {
    public int size;
    public Cell[][] map;
    public int[] position = new int[2];
    public int[] positionAi = new int[2];
    private BufferedImage player;
    private BufferedImage playerFon;
    private BufferedImage playerAiFon;
    private Random random = new Random();
    boolean isJar = Main.class.getProtectionDomain().getCodeSource().getLocation().getPath().endsWith(".jar");

    /**
     * Конструктор класса GameElements.
     *
     * @param size Размер игровой карты (количество ячеек по горизонтали и
     *             вертикали).
     */
    public GameElements(int size) {
        this.size = size;
        position[0] = size - 1;
        positionAi[1] = size - 1;
        if (!isJar) {
            try {
                player = ImageIO.read(new File("images/Main Window/Player.png"));
                playerFon = ImageIO.read(new File("images/Main Window/PlayerFon.png"));
                playerAiFon = ImageIO.read(new File("images/Main Window/PlayerAiFon.png"));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Инициализирует игровую карту на основе параметров воды и риса.
     *
     * @param waterSlider Значение ползунка для воды (в процентах).
     * @param riceSlider  Значение ползунка для риса (в процентах).
     */
    public void init(double waterSlider, double riceSlider) {
        map = new Cell[size][size];
        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                map[i][j] = new Cell();
                map[i][j].needOfHouses = size - Math.abs(i - j) + random.nextInt(2) + 1;
                map[i][j].needOfHouses -= (random.nextDouble() <= 0.8 && size / 2 <= Math.abs(i - j))
                        ? random.nextInt(2) + 1
                        : 0; // С вероятностью 0.8 в ближних к старту ячейках нужно домов меньше на 1 или 2
                if (random.nextDouble() <= waterSlider / 100) {
                    map[i][j].waterCell = true;
                    map[i][j].riceCell = random.nextDouble() <= riceSlider / 100;
                }
                map[i][j].biome = !map[i][j].waterCell ? "Grass" : map[i][j].riceCell ? "Rice" : "Water";
                Double k = random.nextDouble();
                map[i][j].tree = (k <= 0.4) ? true : false;
                map[i][j].forest = (k > 0.4 && k <= 0.8) ? true : false;
            }
        }

        map[size - 1][0].needOfHouses = 1;
        map[size - 1][0].waterCell = true;
        map[size - 1][0].riceCell = true;
        map[size - 1][0].protection = true;
        map[size - 1][0].biome = "Rice";

        map[0][size - 1].needOfHouses = 1;
        map[0][size - 1].waterCell = true;
        map[0][size - 1].riceCell = true;
        map[0][size - 1].protection = true;
        map[0][size - 1].biome = "Rice";
    }

    /**
     * Создает изображение карты.
     *
     * @param player Объект игрока.
     */
    public void makeImage(Player player) {
        String sourceFolderPath = "images/imagesCell";
        String destinationFolderPath = "map";
        String fileName;
        String sourceFilePath;
        String destinationFilePath;
        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                fileName = "image" + Integer.toString(i) + Integer.toString(j) + ".png";
                sourceFilePath = sourceFolderPath + File.separator + cellSelection(map[i][j]);
                destinationFilePath = destinationFolderPath + File.separator + fileName;
                try {
                    BufferedImage image = ImageIO.read(new File(sourceFilePath));
                    ImageIO.write(image, "png", new File(destinationFilePath));
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        creatingMap(player);
    }

    /**
     * Выбирает изображение для ячейки на основе её свойств.
     *
     * @param cell Ячейка, для которой выбирается изображение.
     * @return Название файла изображения для ячейки.
     */
    private String cellSelection(Cell cell) {
        if (cell.protection) {
            return (cell.biome.equals("Grass") ? "Grass" : (cell.biome.equals("Water") ? "Water" : "Rice")) + "Protect"
                    + ".png";
        } else {
            return (cell.biome.equals("Grass") ? "Grass" : (cell.biome.equals("Water") ? "Water" : "Rice"))
                    + (cell.numberOfHouses == 0 ? "0"
                    : (cell.numberOfHouses <= size / 3 + 2 ? "1"
                    : (cell.numberOfHouses <= 2 * size / 3 + 2 ? "2" : "3")))
                    + (cell.forest ? "1" : (cell.tree ? "2" : "0")) + ".png";
        }
    }

    /**
     * Создает общее изображение карты с отметкой игрока и владельцев ячеек.
     *
     * @param player Объект игрока.
     */
    private void creatingMap(Player player) {
        BufferedImage combinedImage = new BufferedImage(size * 100, size * 100, BufferedImage.TYPE_INT_ARGB);
        try {
            for (int i = 0; i < size; i++) {
                for (int j = 0; j < size; j++) {
                    BufferedImage image = ImageIO.read(new File(
                            "map" + File.separator + "image" + Integer.toString(i) + Integer.toString(j) + ".png"));
                    if (i == player.position[0] && j == player.position[1])
                        image.getGraphics().drawImage(this.player, 30, 10, null);
                    if (map[i][j].ownerFlag.equals("Player"))
                        image.getGraphics().drawImage(playerFon, 0, 0, null);
                    if (map[i][j].ownerFlag.equals("PlayerAi"))
                        image.getGraphics().drawImage(playerAiFon, 0, 0, null);
                    combinedImage.getGraphics().drawImage(image, j * 100, i * 100, null);
                }
            }
            ImageIO.write(combinedImage, "png", new File("images/combinedImage.png"));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Подсчитывает количество ячеек, принадлежащих игроку и противнику.
     *
     * @return Массив, содержащий количество ячеек игрока и противника в порядке
     *         [игрок, противник].
     */
    public int[] countingСells() {
        int count1 = 0, count2 = 0;
        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                count1 += map[i][j].ownerFlag.equals("Player") ? 1 : 0;
                count2 += map[i][j].ownerFlag.equals("PlayerAi") ? 1 : 0;
            }
        }
        int[] results = { count1, count2 };
        return results;
    }

    /**
     * Выполняет ежедневные изменения количества юнитов у игроков, включая
     * рождаемость и случайную смерть юнитов.
     *
     * @param player   Объект игрока.
     * @param playerAi Объект противника.
     */
    public void newDay(Player player, PlayerAi playerAi) {
        // Рождаемость (роботы строят себеподобных немного быстрее)
        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                player.numberOfUnits += map[i][j].ownerFlag.equals("Player") && random.nextDouble() <= 0.3
                        ? random.nextInt(map[i][j].numberOfHouses + 1)
                        : 0;
                playerAi.numberOfUnits += map[i][j].ownerFlag.equals("PlayerAi") && random.nextDouble() <= 0.4
                        ? random.nextInt(map[i][j].numberOfHouses + 1)
                        : 0;
            }
        }
        // Случайная смерть (у роботов больше шанс сломаться)
        player.numberOfUnits -= random.nextDouble() <= 0.05 ? random.nextInt(player.numberOfUnits / 10 + 1) : 0;
        playerAi.numberOfUnits -= random.nextDouble() <= 0.08 ? random.nextInt(playerAi.numberOfUnits / 10 + 1) : 0;
    }
}