package application;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javafx.util.Duration;
import javafx.animation.ParallelTransition;
import javafx.animation.PauseTransition;
import javafx.animation.ScaleTransition;
import javafx.animation.SequentialTransition;
import javafx.animation.TranslateTransition;
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

/**
 * Класс `Controller` является основным контроллером для игрового приложения. Он
 * управляет:
 * <ul>
 * <li>Логикой игры</li>
 * <li>Обновлением пользовательского интерфейса</li>
 * <li>Взаимодействием между игроком и искусственным интеллектом</li>
 * </ul>
 * Класс отвечает за инициализацию состояния игры, обработку ввода пользователя
 * и обновление элементов игры.
 */
public class Controller {
    Image icon;
    Random random = new Random();
    Audio audio = new Audio();;
    Audio music = new Audio();;
    Player player1;
    PlayerAi player2;
    GameElements map;
    double waterSlider, riceSlider, ymSlider;
    int fiendSize;
    String textToWrite = null;
    int hookKd = 15;
    Map<Integer, Integer> cells = new HashMap<>();
    Map<Integer, Integer> units = new HashMap<>();
    Map<Integer, Integer> cellsAi = new HashMap<>();
    Map<Integer, Integer> unitsAi = new HashMap<>();
    private static final Logger logger = LogManager.getLogger(Controller.class.getName());
    int counter = 0;

    /**
     * Конструктор класса `Controller`. Инициализирует экземпляры класса Audio и
     * состояние игры. Если в файл "Data.txt" были переданы настройки для новой
     * игры, она загружается. В противном случае инициализируется последняя
     * сохраненная игра.
     */
    public Controller() {
        music.playMusic("gameAudio.wav");

       if (new File("Data.txt").length() == 0 && new File("dataMap.txt").exists()
                && new File("dataMap.txt").length() != 0) {
            initLastGame();
        } else if (new File("Data.txt").length() != 0) {
            initThisGame();
        } else {
            // Добавил этот метод уже после отправки курсовой
            // Если до этого момента не было сохраненных игр, либо файл "dataMap.txt" просто удален,
            // то инициализируется игра с рандомными настройками карты
            initRandomGame();
        }

        audio.playMusic("/pudge/вот и за мной пришли.wav");
        CompletableFuture.delayedExecutor(3, TimeUnit.SECONDS).execute(() -> {
            audio.playMusic("материя.wav");
        });
        
        map.makeImage(player1);
    }

    @FXML
    private Slider audioSlider;

    @FXML
    private Button allResourcesToPositionButton;

    @FXML
    private Button buildHouseButton;

    @FXML
    private ChoiceBox<String> choiceMove;

    @FXML
    private Button drawAllWaterButton;

    @FXML
    private Button drawWaterButton;

    @FXML
    private Label labelRes;

    @FXML
    private Button newTerritoryButton;

    @FXML
    private Button protectPositionButton;

    @FXML
    private Button waterAllRiceButton;

    @FXML
    private Button waterRiceButton;

    @FXML
    private ImageView mapIMG;

    @FXML
    private Label unitScore;

    @FXML
    private Label сellСount;

    @FXML
    private ImageView volumeIMG;

    @FXML
    private Button hookButton;

    @FXML
    private ImageView ropeIMG;

    @FXML
    private ImageView hookIMG;

    @FXML
    private ImageView playerAiIMG;

    @FXML
    private Button saveAndExitButton;

    @FXML
    private ImageView headIMG;

    @FXML
    private ImageView boomIMG;

    @FXML
    private ImageView meteorIMG;

    /**
     * Метод вызывается автоматически при загрузке "GamePrototypeFX.fxml" и
     * предназначен для установки начальных параметров и обработчиков событий.
     * Инициализирует начальное состояние окна при запуске приложения. Устанавливает
     * начальные значения ползунков и изображений для игровых объектов. Задает
     * обработчик событий для слайдера звука. Устанавливает обработчик события для
     * кнопок.
     */
    @FXML
    void initialize() {
        initElementsFX();
        updateImage();

        drawWaterButton.setOnAction(event -> {
            audio.playMusic("waterButton.wav");
            player1.draw_Water();
            dailyActivities();
        });

        waterRiceButton.setOnAction(event -> {
            audio.playMusic("click.wav");
            player1.waterTheRice();
            dailyActivities();
        });

        buildHouseButton.setOnAction(event -> {
            audio.playMusic("buildButton.wav");
            player1.buildHouse();
            dailyActivities();
        });

        newTerritoryButton.setOnAction(event -> {
            audio.playMusic("click.wav");
            if (choiceMove.getValue() == null || choiceMove.getValue().isEmpty()) {
                logger.error("[Player] Попытка сменить позицию без выбора направления.");
                return;
            }
            player1.newTerritory(choiceMove.getValue());
            dailyActivities();
        });

        drawAllWaterButton.setOnAction(event -> {
            audio.playMusic("waterButton.wav");
            player1.drawAllWater();
            dailyActivities();
        });

        waterAllRiceButton.setOnAction(event -> {
            audio.playMusic("click.wav");
            player1.waterAllRice();
            dailyActivities();
        });

        allResourcesToPositionButton.setOnAction(event -> {
            audio.playMusic("click.wav");
            player1.allResourcesToPosition();
            dailyActivities();
        });

        protectPositionButton.setOnAction(event -> {
            audio.playMusic("click.wav");
            player1.protectPosition();
            dailyActivities();
        });

        hookButton.setOnAction(event -> {
            hook();
        });

        saveAndExitButton.setOnAction(event -> {
            saveGameData();
        });
    }

    /**
     * Выполняет действие игрока "бросок крюка". Проверяет условия для выполнения
     * действия: игроки должны находиться на одной горизонтальной линии, расстояние
     * между ними должно быть положительным и не превышать текущий cooldown умения.
     * В случае успешного выполнения, проигрывает звуковые эффекты и анимации для
     * игрока и его противника, обновляет позицию противника и его текущую ячейку.
     * Использует TranslateTransition, ScaleTransition и ParallelTransition для
     * создания плавных анимаций передвижения крюка и игрока. В конце выполнения
     * умения проигрывает случайный звуковой эффект. После завершения всех анимаций,
     * вызывает метод dailyActivities() для выполнения ежедневных действий в игре.
     */
    private void hook() {
        int numberOfCells = player2.position[1] - player1.position[1];
        double cellLength = 500.0 / (double) map.size;
        if ((player1.position[0] == player2.position[0])
                && ((player1.position[1] + numberOfCells) == player2.position[1]) && hookKd >= 15
                && numberOfCells > 0) {
            hookKd = 0;

            audio.playMusic("click.wav");
            audio.playMusic("/pudge/хук.wav");

            hookIMG.setVisible(true);
            ropeIMG.setVisible(true);

            hookIMG.setTranslateX(cellLength * player1.position[1]);
            hookIMG.setTranslateY(cellLength * player1.position[0]);
            ropeIMG.setTranslateX(cellLength * player1.position[1]);
            ropeIMG.setTranslateY(cellLength * player1.position[0]);

            double currentPlayerAiX = playerAiIMG.getTranslateX();
            double currentPlayerAiY = playerAiIMG.getTranslateY();
            double currentXhook = hookIMG.getTranslateX();
            double currentXrope = ropeIMG.getTranslateX();
            double ropeWidth = ropeIMG.getFitWidth();

            SequentialTransition playerAiTransitions = getSequentialTransition2(currentPlayerAiX, cellLength, currentPlayerAiY);

            SequentialTransition sequentialTransition1 = getSequentialTransition3(currentXhook, numberOfCells, cellLength);

            ScaleTransition scaleTransition = new ScaleTransition(Duration.millis(1000), ropeIMG);
            scaleTransition.setToX(numberOfCells * cellLength / ropeWidth);
            TranslateTransition ropeTransition = new TranslateTransition(Duration.millis(1000), ropeIMG);
            ropeTransition.setToX(currentXrope + numberOfCells * cellLength / 2 - ropeWidth / 2);
            ParallelTransition parallelTransition1 = new ParallelTransition(scaleTransition, ropeTransition);

            ScaleTransition scaleReverseTransition = new ScaleTransition(Duration.millis(300), ropeIMG);
            scaleReverseTransition.setToX((numberOfCells - 1) * cellLength / ropeWidth);
            TranslateTransition ropeReverseTransition = new TranslateTransition(Duration.millis(300), ropeIMG);
            ropeReverseTransition.setToX(currentXrope + ((numberOfCells - 1) * cellLength) / 2 - ropeWidth / 2);
            SequentialTransition sequentialTransition2 = getSequentialTransition4(scaleReverseTransition, ropeReverseTransition, parallelTransition1);

            playerAiTransitions.setOnFinished(eventFinished -> {
                dailyActivities();
            });

            sequentialTransition1.play();
            sequentialTransition2.play();
            playerAiTransitions.play();
            player2.position[1]--;
            player2.currentCell = map.map[player2.position[0]][player2.position[1]];
        } else
            dailyActivities();
    }

    /**
     * Возвращает последовательность транзиций для анимации завершения действия крюка.
     * Используется в методе hook для создания эффекта завершения движения крюка и веревки.
     *
     * @param scaleReverseTransition Транзиция изменения масштаба для анимации уменьшения веревки.
     * @param ropeReverseTransition Транзиция движения веревки для анимации уменьшения веревки.
     * @param parallelTransition1 Параллельная транзиция для анимации увеличения веревки и ее движения.
     * @return Последовательность транзиций для анимации завершения действия крюка.
     */
    private SequentialTransition getSequentialTransition4(ScaleTransition scaleReverseTransition, TranslateTransition ropeReverseTransition, ParallelTransition parallelTransition1) {
        ParallelTransition parallelTransition2 = new ParallelTransition(scaleReverseTransition,
                ropeReverseTransition);

        SequentialTransition sequentialTransition2 = new SequentialTransition(parallelTransition1,
                parallelTransition2);

        sequentialTransition2.setOnFinished(eventFinished -> {
            if (random.nextBoolean()) {
                audio.playMusic("/pudge/консервы.wav");
            } else
                audio.playMusic("что ты творишь.wav");
            hookIMG.setVisible(false);
            ropeIMG.setVisible(false);
            hookIMG.setTranslateX(0);
            ropeIMG.setScaleX(5 / (double) map.size);
            ropeIMG.setTranslateX(0);
        });
        return sequentialTransition2;
    }

    /**
     * Возвращает последовательность транзиций для анимации движения крюка при использовании крюка.
     * Используется в методе hook для создания эффекта движения крюка.
     *
     * @param currentXhook Текущая координата X крюка.
     * @param numberOfCells Количество ячеек, на которые движется крюк.
     * @param cellLength Длина ячейки на игровом поле.
     * @return Последовательность транзиций для анимации движения крюка.
     */
    private SequentialTransition getSequentialTransition3(double currentXhook, int numberOfCells, double cellLength) {
        TranslateTransition translateTransition = new TranslateTransition(Duration.millis(1000), hookIMG);
        translateTransition.setToX(currentXhook + numberOfCells * cellLength);
        TranslateTransition reverseTransition = new TranslateTransition(Duration.millis(300), hookIMG);
        reverseTransition.setToX(currentXhook + (numberOfCells - 1) * cellLength);
        SequentialTransition sequentialTransition1 = new SequentialTransition(translateTransition,
                reverseTransition);
        return sequentialTransition1;
    }

    /**
     * Возвращает последовательную транзицию для анимации перемещения игрока AI
     * при использовании хука. Используется в методе hook для создания эффекта
     * перемещения игрока AI во время использования хука.
     *
     * @param currentPlayerAiX Текущая координата X игрока AI.
     * @param cellLength        Длина одной ячейки на игровом поле.
     * @param currentPlayerAiY Текущая координата Y игрока AI.
     * @return Последовательная транзиция для анимации перемещения игрока AI.
     */
    private SequentialTransition getSequentialTransition2(double currentPlayerAiX, double cellLength, double currentPlayerAiY) {
        TranslateTransition playerAiTransition1 = new TranslateTransition(Duration.millis(1000), playerAiIMG);
        playerAiTransition1.setToX(currentPlayerAiX - cellLength * 0.1);
        playerAiTransition1.setToY(currentPlayerAiY + cellLength * 0.076);
        TranslateTransition playerAiTransition2 = new TranslateTransition(Duration.millis(300), playerAiIMG);
        playerAiTransition2.setToX(currentPlayerAiX - cellLength - cellLength * 0.15);

        TranslateTransition playerAiTransition3 = new TranslateTransition(Duration.millis(600), playerAiIMG);
        playerAiTransition3.setToX(currentPlayerAiX - cellLength);
        playerAiTransition3.setToY(currentPlayerAiY);

        PauseTransition pause = new PauseTransition(Duration.millis(500));

        SequentialTransition playerAiTransitions = new SequentialTransition(playerAiTransition1,
                playerAiTransition2, pause, playerAiTransition3);
        return playerAiTransitions;
    }

    /**
     * Сохраняет текущее состояние игры в файл "dataMap.txt". Записывает размер
     * карты, описание каждой ячейки карты, позиции игроков, количество юнитов у
     * каждого игрока и текущий коoldown умения "крюк". В случае возникновения
     * ошибок ввода, выводит стек вызовов ошибки в консоль.
     */
    private void saveGameData() {
        try {
            File file = new File("dataMap.txt");
            FileWriter fileWriter = new FileWriter(file);
            BufferedWriter writer = new BufferedWriter(fileWriter);
            writer.write(String.valueOf(map.size));
            writer.newLine();
            for (int i = 0; i < map.size; i++) {
                for (int j = 0; j < map.size; j++) {
                    writer.write(map.map[i][j].shortDescription() + (j < map.size - 1 ? " " : ""));
                }
                writer.newLine();
            }
            writer.write(
                    player1.position[0] + " " + player1.position[1] + " " + player1.numberOfUnits + " " + player2.ym);
            writer.newLine();
            writer.write(player2.position[0] + " " + player2.position[1] + " " + player2.numberOfUnits);
            writer.newLine();
            writer.write(String.valueOf(hookKd));
            writer.close();
            logger.info("Игра успешно сохранена.");
        } catch (IOException e) {
            logger.error("Ошибка при сохранении игры.");
            e.printStackTrace();
        }
    }

    /**
     * Обновляет изображения игровых элементов и отображает текущее состояние игры.
     */
    private void updateImage() {
        playerAiIMG.setTranslateX(0);
        playerAiIMG.setLayoutX(
                226 + player2.position[1] * (500.0 / (double) map.size) + 0.61 * (500.0 / (double) map.size));
        playerAiIMG.setLayoutY(
                61 + player2.position[0] * (500.0 / (double) map.size) + 0.29 * (500.0 / (double) map.size));

        map.makeImage(player1);

        Image newImage = new Image("file:images/combinedImage.png");
        mapIMG.setImage(newImage);

        сellСount.setText(map.countingСells()[0] + " : " + map.countingСells()[1]);
        unitScore.setText(player1.numberOfUnits + " : " + player2.numberOfUnits);
        labelRes.setText("Вода: " + map.map[player1.position[0]][player1.position[1]].numberOfWater + " Рис: "
                + map.map[player1.position[0]][player1.position[1]].numberOfRice + " Дома/Нужно домов: "
                + map.map[player1.position[0]][player1.position[1]].numberOfHouses + "/"
                + map.map[player1.position[0]][player1.position[1]].needOfHouses);
        logger.info("Изображение обновлено.");
    }

    /**
     * Обрабатывает логику игры для действия искуственного интеллекта "прыжка",
     * включая анимации и звуковые эффекты. PlayerAi перемещается в любую свободную
     * ячейку, это сопровождается анимацией взлета и посадки.
     */
    private void jump() {
        audio.playMusic("ракета.wav");

        double cellLength = 500.0 / (double) map.size;

        headIMG.setVisible(true);
        playerAiIMG.setVisible(false);

        headIMG.setTranslateX(cellLength * player2.position[1]);
        headIMG.setTranslateY(cellLength * player2.position[0]);

        player2.teleportation();

        headIMG.setScaleX(0);
        headIMG.setScaleY(0);

        SequentialTransition scaleTransitions = getSequentialTransition();

        double newX = cellLength * player2.position[1];
        double newY = cellLength * player2.position[0];

        TranslateTransition headIMGTransition = new TranslateTransition(Duration.millis(4000), headIMG);
        headIMGTransition.setToX(newX);
        headIMGTransition.setToY(newY);

        scaleTransitions.play();
        headIMGTransition.play();

        headIMGTransition.setOnFinished(event -> {
            if (random.nextBoolean()) {
                audio.playMusic("силы выше.wav");
            } else
                audio.playMusic("существо.wav");
            headIMG.setVisible(false);
            playerAiIMG.setVisible(true);
        });
    }
    
    /**
     * Возвращает последовательную транзицию для анимации масштабирования изображения.
     * Используется в методе jump для создания эффекта масштабирования головы.
     *
     * @return Последовательная транзиция для анимации масштабирования.
     */
    private SequentialTransition getSequentialTransition() {
        ScaleTransition headTransition = new ScaleTransition(Duration.millis(1600), headIMG);
        headTransition.setToX(150 / (29 * (2 / (double) map.size)));
        headTransition.setToY(159 / (36 * (2 / (double) map.size)));

        ScaleTransition headTransitionReverse = new ScaleTransition(Duration.millis(1600), headIMG);
        headTransitionReverse.setToX(0);
        headTransitionReverse.setToY(0);

        PauseTransition pause = new PauseTransition(Duration.millis(800));
        SequentialTransition scaleTransitions = new SequentialTransition(headTransition, pause, headTransitionReverse);
        return scaleTransitions;
    }

    /**
     * Выполняет ход ИИ в зависимости от позиции. Если для ИИ нет возможности
     * сменить ячейку, что выполняется метод jump(), либо если вокруг позиции ИИ нет
     * свободной ячейки, с вероятностью 0.1 также выполнится jump(). С вероятностью
     * 0.005 на ячейку игрока упадет метеорит. Если ничего из выше перечисленного не
     * выполнится, то ИИ сделает свой обычный ход, определяемый методами класса
     * PlayerAi.
     */
   private void rabbitMove() {
        if ((Integer.parseInt(player2.bestMove()[1]) <= 10 && random.nextDouble() <= 0.10
                && player2.ym >= 50 || Integer.parseInt(player2.bestMove()[1]) == 0) && (map.countingСells()[0] + map.countingСells()[1]) < Math.pow(map.size, 2)) {
            jump();
        } else if (random.nextDouble() <= 0.005 && map.countingСells()[0] > 1 && player2.ym >= 70) {
            meteorFall();
        } else
            player2.makeMove();
    }

    /**
     * Инициализирует состояние игры на основе данных, сохраненных в файле
     * "dataMap.txt"(последняя сохраненная игра).
     */
    private void initLastGame() {
        try {
            FileReader fileReader = new FileReader("dataMap.txt");
            @SuppressWarnings("resource")
            BufferedReader bufferedReader = new BufferedReader(fileReader);

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
            hookKd = Integer.parseInt(bufferedReader.readLine());
            logger.info("Сохраненная игра успешно инициализированна.");
        } catch (IOException e) {
            logger.error("Ошибка при инициализации сохраненной игры.");
            e.printStackTrace();
        }
    }

    /**
     * Инициализирует новое состояние игры на основе данных, сохраненных в файле
     * "Data.txt"(настройки игры).
     */
    private void initThisGame() {
        try {
            FileReader fileReader = new FileReader("Data.txt");
            BufferedReader bufferedReader = new BufferedReader(fileReader);
            textToWrite = bufferedReader.readLine();
            bufferedReader.close();
            FileWriter fileWriter = new FileWriter("Data.txt", false);
            BufferedWriter bufferedWriter = new BufferedWriter(fileWriter);
            bufferedWriter.close();
            logger.info("Игра успешно инициализирована.");
        } catch (IOException e) {
            logger.error("Ошибка при инициализации.");
            e.printStackTrace();
        }
        String[] words = textToWrite.split(" ");
        waterSlider = Double.parseDouble(words[0]);
        riceSlider = Double.parseDouble(words[1]);
        ymSlider = Double.parseDouble(words[2]);
        fiendSize = Integer.parseInt(words[3]);
        map = new GameElements(fiendSize);
        map.init(waterSlider, riceSlider);
        player1 = new Player(map);
        player2 = new PlayerAi(map, player1, ymSlider);
    }

    /**
     * Инициализирует графические элементы интерфейса игры, такие как изображения
     * игрока, крюка и звуковые ползунки.
     */
    private void initElementsFX() {
        playerAiIMG.setFitWidth(21 * (5 / (double) map.size));
        playerAiIMG.setFitHeight(19 * (5 / (double) map.size));
        playerAiIMG.setLayoutX(
                226 + player2.position[1] * (500.0 / (double) map.size) + 0.61 * (500.0 / (double) map.size));
        playerAiIMG.setLayoutY(
                61 + player2.position[0] * (500.0 / (double) map.size) + 0.29 * (500.0 / (double) map.size));

        ropeIMG.setFitWidth(5 * (5 / (double) map.size));
        ropeIMG.setFitHeight(36 * (5 / (double) map.size));
        hookIMG.setFitWidth(29 * (5 / (double) map.size));
        hookIMG.setFitHeight(19 * (5 / (double) map.size));
        ropeIMG.setLayoutX(226 + 0.42 * (500.0 / (double) map.size));
        ropeIMG.setLayoutY(61 + 0.36 * (500.0 / (double) map.size));
        hookIMG.setLayoutX(226 + 0.40 * (500.0 / (double) map.size));
        hookIMG.setLayoutY(61 + 0.41 * (500.0 / (double) map.size));
        hookIMG.setVisible(false);
        ropeIMG.setVisible(false);

        headIMG.setFitWidth(29 * (2 / (double) map.size));
        headIMG.setFitHeight(36 * (2 / (double) map.size));
        headIMG.setLayoutX(226 + 0.63 * (500.0 / (double) map.size));
        headIMG.setLayoutY(61 + 0.31 * (500.0 / (double) map.size));
        headIMG.setVisible(false);

        boomIMG.setVisible(false);
        meteorIMG.setVisible(false);
        boomIMG.setFitWidth(84 * (5 / (double) map.size));
        boomIMG.setFitHeight(84 * (5 / (double) map.size));
        meteorIMG.setFitWidth(29 * (5 / (double) map.size));
        meteorIMG.setFitHeight(46 * (5 / (double) map.size));
        meteorIMG.setLayoutX(226 + 0.18 * (500.0 / (double) map.size));
        meteorIMG.setLayoutY(-meteorIMG.getFitHeight());

        audioSlider.setMajorTickUnit(10);
        audioSlider.setSnapToTicks(true);
        audioSlider.setShowTickMarks(true);
        audioSlider.setShowTickLabels(true);

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

        logger.info("Инициализация графических элементов прошла успешно.");
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
        try {
            File fileGraph = new File("dataGraph.txt");
            FileWriter fileWriter = new FileWriter(fileGraph);
            BufferedWriter writer = new BufferedWriter(fileWriter);
            for (Map.Entry<Integer, Integer> entry : cells.entrySet()) {
                writer.write(entry.getValue() + " ");
            }
            writer.newLine();
            for (Map.Entry<Integer, Integer> entry : cellsAi.entrySet()) {
                writer.write(entry.getValue() + " ");
            }
            writer.newLine();
            for (Map.Entry<Integer, Integer> entry : units.entrySet()) {
                writer.write(entry.getValue() + " ");
            }
            writer.newLine();
            for (Map.Entry<Integer, Integer> entry : unitsAi.entrySet()) {
                writer.write(entry.getValue() + " ");
            }
            writer.close();
            logger.info("Данные для графика успешно сохранены.");
        } catch (IOException e) {
            logger.error("Ошибка при сохраненнии данных для графика.");
            e.printStackTrace();
        }
    }

    /**
     * Открывает новое окно при завершении игры, останавливает музыку.
     *
     * @param FXMLFileName Имя файла FXML для нового окна.
     */
    private void windowOpen(String FXMLFileName) {
        music.stopMusic();
        audio.stopMusic();
        mapIMG.getScene().getWindow().hide();
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
            if (player1.position[0] == player2.position[0] && player1.position[1] == player2.position[1]
                    && random.nextBoolean())
                audio.playMusic("/pudge/с дороги.wav");
            rabbitMove();
            map.newDay(player1, player2);
            updateImage();
            winLossControl();
            hookKd++;
            cells.put(counter, map.countingСells()[0]);
            cellsAi.put(counter, map.countingСells()[1]);
            units.put(counter, player1.numberOfUnits);
            unitsAi.put(counter, player2.numberOfUnits);
            counter++;
            logger.info("Выполнились ежедневные активности.");
        });
    }

    /**
     * Выполняет сценарий падения метеорита в игре. Метеорит падает на любую ячейку
     * игрока, кроме стартовой, разрушая при этом укрепления, если они есть.
     * Проигрывает звук метеорита, определяет ячейку для падения метеорита,
     * анимирует падение и взрыв, обновляет состояние карты.
     */
    private void meteorFall() {
        Platform.runLater(() -> {
            audio.playMusic("meteor.wav");
            List<int[]> cellsToMeteor = new ArrayList<>();
            for (int i = 0; i < map.size; i++) {
                for (int j = 0; j < map.size; j++) {
                    if (map.map[i][j].ownerFlag.equals("Player") && (i != map.size - 1 || j != 0)) {
                        int[] cellToTeleport = { i, j };
                        cellsToMeteor.add(cellToTeleport);
                    }
                }
            }
            int[] cellToMeteor = cellsToMeteor.get(random.nextInt(cellsToMeteor.size()));

            double cellLength = 500.0 / (double) map.size;
            Image boomImage = new Image("file:images/Main Window/boom.gif");

            boomIMG.setTranslateX(cellLength * cellToMeteor[1]);
            boomIMG.setTranslateY(cellLength * cellToMeteor[0]);
            meteorIMG.setTranslateX(cellLength * cellToMeteor[1] + cellLength * 0.13);

            meteorIMG.setVisible(true);
            TranslateTransition translateTransition = new TranslateTransition(Duration.millis(1500), meteorIMG);
            translateTransition.setToY(61 + meteorIMG.getFitHeight() + cellLength * cellToMeteor[0]);
            translateTransition.play();
            translateTransition.setOnFinished(eventFinished -> {
                audio.playMusic("взрыв.wav");
                meteorIMG.setVisible(false);
                boomIMG.setVisible(true);
                boomIMG.setImage(boomImage);
                map.map[cellToMeteor[0]][cellToMeteor[1]].numberOfHouses = 0;
                map.map[cellToMeteor[0]][cellToMeteor[1]].ownerFlag = "Free";
                map.map[cellToMeteor[0]][cellToMeteor[1]].protection = false;
                map.map[cellToMeteor[0]][cellToMeteor[1]].forest = false;
                map.map[cellToMeteor[0]][cellToMeteor[1]].tree = false;
                updateImage();
                CompletableFuture.delayedExecutor(1000, TimeUnit.MILLISECONDS).execute(() -> {
                    boomIMG.setVisible(false);
                    meteorIMG.setTranslateX(0);
                    meteorIMG.setTranslateY(0);
                    boomIMG.setTranslateX(0);
                    boomIMG.setTranslateY(0);
                    CompletableFuture.delayedExecutor(6000, TimeUnit.MILLISECONDS).execute(() -> {
                        if (random.nextBoolean()) {
                            audio.playMusic("пир или чума.wav");
                        } else
                            audio.playMusic("помощь предвечного.wav");
                    });
                });
            });
        });
    }

    /**
     * Инициализирует игру с рандомными настройками карты
     */
    private void initRandomGame() {
        waterSlider = random.nextInt(51) + 30;
		riceSlider = random.nextInt(51) + 30;
		ymSlider = random.nextInt(31) + 70;
		fiendSize = random.nextInt(3) + 4;
		map = new GameElements(fiendSize);
		map.init(waterSlider, riceSlider);
		player1 = new Player(map);
		player2 = new PlayerAi(map, player1, ymSlider);
        logger.info("Инициализирована игра с рандомными настройками карты.");
    }
}
