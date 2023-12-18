package application;

import java.io.*;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;
import java.util.ResourceBundle;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;
import javafx.scene.control.TextArea;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import static java.lang.Double.parseDouble;
import static java.lang.Integer.parseInt;

/**
 * Контроллер для основного игрового окна, при запске JAR-файла. Отвечает за взаимодействие
 * пользователя с игровым интерфейсом и обработку событий.
 */
public class JarEditionController {
    Image icon;
    Audio music;
    Player player1;
    PlayerAi player2;
    GameElements map;
    double waterSlider, riceSlider, ymSlider;
    int fiendSize;
    Map<Integer, Integer> cells = new HashMap<>();
    Map<Integer, Integer> units = new HashMap<>();
    Map<Integer, Integer> cellsAi = new HashMap<>();
    Map<Integer, Integer> unitsAi = new HashMap<>();
    private static final Logger logger = LogManager.getLogger(Controller.class.getName());
    int counter = 0;
    StringBuilder textMapSb;
    int indent = 1;
    int textSize = 12;

    @FXML
    private ResourceBundle resources;

    @FXML
    private URL location;

    @FXML
    private Button allResourcesToPositionButton;

    @FXML
    private Slider audioSlider;

    @FXML
    private Button buildHouseButton;

    @FXML
    private ChoiceBox<String> choiceMove;

    @FXML
    private Button drawAllWaterButton;

    @FXML
    private Button drawWaterButton;

    @FXML
    private Button newTerritoryButton;

    @FXML
    private Button protectPositionButton;

    @FXML
    private Button saveAndExitButton;

    @FXML
    private TextArea textMap;

    @FXML
    private Label unitScore;

    @FXML
    private ImageView volumeIMG;

    @FXML
    private Button waterAllRiceButton;

    @FXML
    private Button waterRiceButton;

    @FXML
    private Label сellСount;

    @FXML
    private Button indentMnus;

    @FXML
    private Button indentPlus;

    @FXML
    private Button textSizeMinus;

    @FXML
    private Button textSizePlus;

    /**
     * Конструктор класса `JarEditionController`. Инициализирует экземпляры классов `Audio` и `GameElements`
     * и запускает воспроизведение музыкального файла "gameAudio.wav". Затем производит инициализацию
     * текущей игры, вызывая методы `initLastGame()` или `initThisGame()` в зависимости от наличия данных
     * в файле "Data.txt".
     */
    public JarEditionController() {
        music = new Audio();
        music.playMusic("gameAudio.wav");

        if (readFromFile("Data.txt").isEmpty()) {
            initLastGame();
        } else
            initThisGame();
    }

    /**
     * Инициализация контроллера при загрузке FXML-файла. Настраивает элементы интерфейса и добавляет
     * обработчики событий.
     */
    @FXML
    void initialize() {
        creatingTextMap();
        choiceMove.getItems().add("Вверх");
        choiceMove.getItems().add("Вправо");
        choiceMove.getItems().add("Влево");
        choiceMove.getItems().add("Вниз");

        audioSlider.valueProperty().addListener((observable, oldValue, newValue) -> {
            music.setVolumePercent(newValue.intValue());
            if (newValue.intValue() == 0) {
                Image newImage = new Image("file:images/Main Window/volumeNo.png");
                this.volumeIMG.setImage(newImage);
            } else {
                Image newImage = new Image("file:images/Main Window/volumeYes.png");
                this.volumeIMG.setImage(newImage);
            }
        });

        drawWaterButton.setOnAction(event -> {
            player1.draw_Water();
            dailyActivities();
        });

        waterRiceButton.setOnAction(event -> {
            player1.waterTheRice();
            dailyActivities();
        });

        buildHouseButton.setOnAction(event -> {
            player1.buildHouse();
            dailyActivities();
        });

        newTerritoryButton.setOnAction(event -> {
            if (choiceMove.getValue() == null || choiceMove.getValue().isEmpty()) {
                logger.error("[Player] Попытка сменить позицию без выбора направления.");
                return;
            }
            player1.newTerritory(choiceMove.getValue());
            dailyActivities();
        });

        drawAllWaterButton.setOnAction(event -> {
            player1.drawAllWater();
            dailyActivities();
        });

        waterAllRiceButton.setOnAction(event -> {
            player1.waterAllRice();
            dailyActivities();
        });

        allResourcesToPositionButton.setOnAction(event -> {
            player1.allResourcesToPosition();
            dailyActivities();
        });

        protectPositionButton.setOnAction(event -> {
            player1.protectPosition();
            dailyActivities();
        });

        saveAndExitButton.setOnAction(event -> {
            saveGameData();
        });

        textSizePlus.setOnAction(event -> {
            textSize++;
            creatingTextMap();
        });

        textSizeMinus.setOnAction(event -> {
            if (textSize - 1 >= 1)
                textSize--;
            creatingTextMap();
        });

        indentPlus.setOnAction(event -> {
            indent++;
            creatingTextMap();
        });

        indentMnus.setOnAction(event -> {
            if (indent - 1 >= 1)
                indent--;
            creatingTextMap();
        });
    }

    /**
     * Сохраняет текущее состояние игры в файл "dataMap.txt". Записывает информацию о размере карты,
     * состоянии ячеек, позициях игроков и других параметрах.
     */
    private void saveGameData() {
        try {
            // Получаем данные для записи в файл
            StringBuilder content = new StringBuilder();
            content.append(map.size).append(System.lineSeparator());
            for (int i = 0; i < map.size; i++) {
                for (int j = 0; j < map.size; j++) {
                    content.append(map.map[i][j].shortDescription()).append(j < map.size - 1 ? " " : "");
                }
                content.append(System.lineSeparator());
            }
            content.append(player1.position[0]).append(" ")
                    .append(player1.position[1]).append(" ")
                    .append(player1.numberOfUnits).append(" ")
                    .append(player2.ym).append(System.lineSeparator());
            content.append(player2.position[0]).append(" ")
                    .append(player2.position[1]).append(" ")
                    .append(player2.numberOfUnits).append(System.lineSeparator());

            // Записываем данные в файл
            new SettingsController().writeToFileInDirectory("dataMap.txt", content.toString());
            logger.info("Игра успешно сохранена.");
        } catch (Exception e) {
            logger.error("Ошибка при сохранении игры.");
            e.printStackTrace();
        }
    }

    /**
     * Инициализирует состояние игры на основе данных, сохраненных в файле
     * "dataMap.txt"(последняя сохраненная игра).
     */
    private void initLastGame() {
        try {
            // Получаем путь к расположению JAR-файла
            String jarPath = Main.class.getProtectionDomain().getCodeSource().getLocation().toURI().getPath();
            Path jarDirectory = Paths.get(new File(jarPath).getAbsolutePath()).getParent();

            // Формируем путь к файлу в той же папке
            Path filePath = jarDirectory.resolve("dataMap.txt");

            // Проверяем, существует ли файл
            if (Files.exists(filePath)) {
                try (FileReader fileReader = new FileReader(filePath.toFile());
                     BufferedReader bufferedReader = new BufferedReader(fileReader)) {
                     int mapSize = Integer.parseInt(bufferedReader.readLine());
                     map = new GameElements(mapSize);
                     map.map = new Cell[mapSize][mapSize];

                     for (int i = 0; i < map.size; i++) {
                         String tempLine = bufferedReader.readLine();
                         String[] tempMassiveCell = tempLine.split(" ");
                         for (int j = 0; j < map.size; j++) {
                             String[] tempMassiveInf = tempMassiveCell[j].split("_");
                             map.map[i][j] = new Cell();
                             map.map[i][j].ownerFlag = tempMassiveInf[0];
                             map.map[i][j].needOfHouses = Integer.parseInt(tempMassiveInf[1]);
                             map.map[i][j].numberOfWater = Integer.parseInt(tempMassiveInf[2]);
                             map.map[i][j].numberOfRice = Integer.parseInt(tempMassiveInf[3]);
                             map.map[i][j].numberOfHouses = Integer.parseInt(tempMassiveInf[4]);
                             map.map[i][j].waterCell = tempMassiveInf[5].equals("T");
                             map.map[i][j].riceCell = tempMassiveInf[6].equals("T");
                             map.map[i][j].protection = tempMassiveInf[7].equals("T");
                             map.map[i][j].biome = tempMassiveInf[8];
                             map.map[i][j].forest = tempMassiveInf[9].equals("T");
                             map.map[i][j].tree = tempMassiveInf[10].equals("T");
                         }
                    }

                    player1 = new Player(map);
                    String[] tempPlayerMassive = bufferedReader.readLine().split(" ");
                    player1.position[0] = Integer.parseInt(tempPlayerMassive[0]);
                    player1.position[1] = Integer.parseInt(tempPlayerMassive[1]);
                    player1.numberOfUnits = Integer.parseInt(tempPlayerMassive[2]);
                    player1.currentCell = map.map[player1.position[0]][player1.position[1]];

                    player2 = new PlayerAi(map, player1, Double.parseDouble(tempPlayerMassive[3]));

                    tempPlayerMassive = bufferedReader.readLine().split(" ");
                    player2.position[0] = Integer.parseInt(tempPlayerMassive[0]);
                    player2.position[1] = Integer.parseInt(tempPlayerMassive[1]);
                    player2.numberOfUnits = Integer.parseInt(tempPlayerMassive[2]);
                    player2.currentCell = map.map[player2.position[0]][player2.position[1]];

                    logger.info("Сохраненная игра успешно инициализирована.");
                }
            } else {
                logger.error("Файл dataMap.txt не существует в той же папке, что и JAR-файл.");
            }
        } catch (IOException | URISyntaxException e) {
            logger.error("Ошибка при инициализации сохраненной игры.");
            e.printStackTrace();
        }
    }

    /**
     * Инициализирует новое состояние игры на основе данных, сохраненных в файле
     * "Data.txt"(настройки игры).
     */
    private void initThisGame() {
        String[] words = readFromFile("Data.txt").split("\\s+");

        if (words.length >= 4) {
            waterSlider = parseDouble(words[0]);
            riceSlider = parseDouble(words[1]);
            ymSlider = parseDouble(words[2]);
            fiendSize = parseInt(words[3]);

            map = new GameElements(fiendSize);
            map.init(waterSlider, riceSlider);
            player1 = new Player(map);
            player2 = new PlayerAi(map, player1, ymSlider);
            clearFile("Data.txt");
        } else {
            System.err.println("Ошибка при чтении данных из файла. Неверный формат данных.");
        }
    }

    /**
     * Читает содержимое файла и возвращает его в виде строки.
     *
     * @param fileName Имя файла, который необходимо прочитать.
     * @return Строка с содержимым файла.
     */
    private static String readFromFile(String fileName) {
        StringBuilder content = new StringBuilder();
        try {
            // Получаем путь к расположению JAR-файла
            String jarPath = Main.class.getProtectionDomain().getCodeSource().getLocation().toURI().getPath();
            Path jarDirectory = Paths.get(new File(jarPath).getAbsolutePath()).getParent();

            // Формируем путь к файлу в той же папке
            Path filePath = jarDirectory.resolve(fileName);

            // Читаем содержимое файла
            try (BufferedReader reader = new BufferedReader(new FileReader(filePath.toFile()))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    content.append(line).append(System.lineSeparator());
                }
            }
        } catch (IOException | java.net.URISyntaxException e) {
            e.printStackTrace();
            logger.error("Ошибка при чтении файла");
        }

        return content.toString();
    }

    /**
     * Создает текстовое представление игровой карты и обновляет соответствующие элементы интерфейса.
     */
    public void creatingTextMap() {
        textMapSb = new StringBuilder();
        textMap.clear();
        for (int i = 0; i < map.size; i++) {
            for (int j = 0; j < map.size; j++) {
                if (i == player1.position[0] && j == player1.position[1]) {
                    textMapSb.append("^_^");
                } else if (i == player2.position[0] && j == player2.position[1]) {
                    textMapSb.append("*_*");
                } else
                    textMapSb.append("   ");
                textMapSb.append(map.map[i][j].descriptionToTextMap()).append(j < map.size - 1 ? "\t" : "");
            }
            textMapSb.append(System.lineSeparator().repeat(indent));
        }
        textMap.setStyle("-fx-font-size: " + textSize + "pt;");
        textMap.setText(textMapSb.toString());

        сellСount.setText(map.countingСells()[0] + " : " + map.countingСells()[1]);
        unitScore.setText(player1.numberOfUnits + " : " + player2.numberOfUnits);
        logger.info("Карта успешно обновлена");
    }

    /**
     * Очищает содержимое указанного файла.
     *
     * @param fileName Имя файла, который необходимо очистить.
     */
    private static void clearFile(String fileName) {
        try {
            // Получаем путь к расположению JAR-файла
            String jarPath = Main.class.getProtectionDomain().getCodeSource().getLocation().toURI().getPath();
            Path jarDirectory = Paths.get(new File(jarPath).getAbsolutePath()).getParent();

            // Формируем путь к файлу в той же папке
            Path filePath = jarDirectory.resolve(fileName);

            // Очищаем содержимое файла
            try (PrintWriter writer = new PrintWriter(filePath.toFile())) {
                writer.print("");
            }

        } catch (IOException | java.net.URISyntaxException e) {
            e.printStackTrace();
            logger.error("Ошибка при очистке файла");
        }
    }

    /**
     * Контролирует победу или поражение в игре. Если количество ячеек,
     * принадлежащих игроку, превышает половину размера карты в квадрате, открывает
     * соответствующее окно и записывает данные для графика.
     */
    private void winLossControl() {
        if (map.countingСells()[0] > Math.pow(map.size, 2) / 2) {
            windowOpen("Victory.fxml");
            recordingDataGraph();
            logger.info("Игрок победил.");
        } else if (map.countingСells()[1] > Math.pow(map.size, 2) / 2) {
            windowOpen("Loss.fxml");
            logger.info("Игрок проиграл.");
            recordingDataGraph();
        }
    }

    /**
     * Записывает данные для графика в файл "dataGraph.txt" после окончания игры.
     * Записываются данные о количестве ячеек и юнитов у обоих игроков.
     */
    private void recordingDataGraph() {
        StringBuilder dataGraph = new StringBuilder();

        for (Map.Entry<Integer, Integer> entry : cells.entrySet()) {
            dataGraph.append(entry.getValue() + " ");
        }
        dataGraph.append(System.lineSeparator());
        for (Map.Entry<Integer, Integer> entry : cellsAi.entrySet()) {
            dataGraph.append(entry.getValue() + " ");
        }
        dataGraph.append(System.lineSeparator());
        for (Map.Entry<Integer, Integer> entry : units.entrySet()) {
            dataGraph.append(entry.getValue() + " ");
        }
        dataGraph.append(System.lineSeparator());
        for (Map.Entry<Integer, Integer> entry : unitsAi.entrySet()) {
            dataGraph.append(entry.getValue() + " ");
        }
        new SettingsController().writeToFileInDirectory("dataGraph.txt", dataGraph.toString());
        logger.info("Данные для графика успешно сохранены.");
    }

    /**
     * Открывает новое окно при завершении игры, останавливает музыку.
     *
     * @param FXMLFileName Имя файла FXML для нового окна.
     */
    private void windowOpen(String FXMLFileName) {
        music.stopMusic();
        textMap.getScene().getWindow().hide();
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(getClass().getResource("/application/" + FXMLFileName));
        try {
            Parent root = loader.load();
            Stage stage = new Stage();
            stage.setTitle("Pudge vs. Robo-Bunny");
            if (!Main.class.getProtectionDomain().getCodeSource().getLocation().getPath().endsWith(".jar")) {
                icon = new Image("file:images/icon.png");
            } else {
                InputStream imageStream = getClass().getClassLoader().getResourceAsStream("images/icon.png");
                icon = new Image(imageStream);
            }
            stage.getIcons().add(icon);
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Выполняет ежедневные активности в игре, такие как ход ИИ, обновление
     * изображения и проверка условий победы. Также записывает данные для графика в
     * HashMap.
     */
    private void dailyActivities() {
        Platform.runLater(() -> {
            player2.makeMove();
            map.newDay(player1, player2);
            winLossControl();
            cells.put(counter, map.countingСells()[0]);
            cellsAi.put(counter, map.countingСells()[1]);
            units.put(counter, player1.numberOfUnits);
            unitsAi.put(counter, player2.numberOfUnits);
            counter++;
            logger.info("Выполнились ежедневные активности.");
            creatingTextMap();
        });
    }

}

