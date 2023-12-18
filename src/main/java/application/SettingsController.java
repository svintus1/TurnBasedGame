package application;

import java.io.*;
import java.net.URL;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ResourceBundle;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.stage.Stage;

/**
 * Контроллер для окна с настройками игры.
 */
public class SettingsController {
    Audio audio;
    Image icon;
    private static final Logger logger = LogManager.getLogger(SettingsController.class.getName());
    boolean noJar = !Main.class.getProtectionDomain().getCodeSource().getLocation().getPath().endsWith(".jar");

    /**
     * Конструктор класса SettingsController. Создает экземпляр класса Audio и
     * запускает воспроизведение музыкального файла "settingsAudio.wav".
     */
    public SettingsController() {
        audio = new Audio();
        audio.playMusic("settingsAudio.wav");
    }

    @FXML
    private ResourceBundle resources;

    @FXML
    private URL location;

    @FXML
    private Slider riceSlider;

    @FXML
    private Slider waterSlider;

    @FXML
    private Slider ymSlider;

    @FXML
    private Label valueSlide1;

    @FXML
    private Label valueSlide2;

    @FXML
    private Label valueSlide3;

    @FXML
    private TextField fieldSize;

    @FXML
    private Button startGameButton;

    @FXML
    private Button lastGameButton;

    /**
     * Инициализация контроллера при загрузке FXML-файла. Настройка слайдеров и
     * добавление обработчиков событий для кнопок.
     */
    @FXML
    void initialize() {
        // Код настройки слайдеров
        waterSlider.setMajorTickUnit(10);
        waterSlider.setSnapToTicks(true);
        waterSlider.setShowTickMarks(true);
        waterSlider.setShowTickLabels(true);
        riceSlider.setMajorTickUnit(10);
        riceSlider.setSnapToTicks(true);
        riceSlider.setShowTickMarks(true);
        riceSlider.setShowTickLabels(true);
        ymSlider.setMajorTickUnit(10);
        ymSlider.setSnapToTicks(true);
        ymSlider.setShowTickMarks(true);
        ymSlider.setShowTickLabels(true);
        waterSlider.valueProperty().addListener((observable, oldValue, newValue) -> {
            valueSlide1.setText(String.valueOf(newValue.intValue()));
        });
        riceSlider.valueProperty().addListener((observable, oldValue, newValue) -> {
            valueSlide2.setText(String.valueOf(newValue.intValue()));
        });
        ymSlider.valueProperty().addListener((observable, oldValue, newValue) -> {
            valueSlide3.setText(String.valueOf(newValue.intValue()));
        });

        startGameButton.setOnAction(event -> {
            audio.stopMusic();
            if (noJar)
                audio.playMusic("click.wav");
            logger.info("Нажата кнопка играть.");

            try {
                int fieldSizeValue = Integer.parseInt(fieldSize.getText());
                if (fieldSizeValue <= 0) {
                    logger.error("Размер карты должен быть целым числом больше нуля.");
                    return;
                }
            } catch (NumberFormatException e) {
                logger.error("Некорректный формат введенного размера карты.");
                return;
            }

            // Сохранение настроек игры в файл "Data.txt"
            saveGameData();

            // Закрытие текущего окна и открытие нового окна игры
            openNewGame();
        });

        lastGameButton.setOnAction(event -> {
            audio.stopMusic();
            if (noJar)
                audio.playMusic("click.wav");

            logger.info("Загружена прошлая игра.");

            // Закрытие текущего окна и открытие окна последней сохраненной игры
            openLastGame();
        });

    }

    /**
     * Сохраняет настройки игры в файл "Data.txt". Если программа выполняется не как JAR-файл,
     * используется локальный путь, в противном случае сохранение в директорию JAR-файла.
     */
    private void saveGameData() {
        if (noJar) {
            try {
                File file = new File("Data.txt");
                FileWriter fileWriter = new FileWriter(file);
                BufferedWriter writer = new BufferedWriter(fileWriter);
                writer.write(waterSlider.getValue() + " " + riceSlider.getValue() + " " + ymSlider.getValue() + " "
                        + Integer.parseInt(fieldSize.getText()));
                writer.close();
                logger.info("Настройки игры: " + "Ячейки с водой - " + waterSlider.getValue() + " Ячейки с рисом - "
                        + riceSlider.getValue() + " Ярость зайца - " + ymSlider.getValue() + " Размер карты - "
                        + fieldSize.getText());
            } catch (IOException e) {
                e.printStackTrace();
                logger.error("Файл Data.txt не найден.");
            }
        } else
            writeToFileInDirectory("Data.txt", waterSlider.getValue() + " " + riceSlider.getValue()
                    + " " + ymSlider.getValue() + " " + Integer.parseInt(fieldSize.getText()));
    }

    /**
     * Записывает указанный текст в файл в директории JAR-файла.
     *
     * @param fileName Имя файла.
     * @param content  Текст для записи в файл.
     */
    public static void writeToFileInDirectory(String fileName, String content) {
        try {
            // Получаем путь к расположению JAR-файла
            String jarPath = Main.class.getProtectionDomain().getCodeSource().getLocation().toURI().getPath();
            Path jarDirectory = Paths.get(new File(jarPath).getAbsolutePath()).getParent();

            // Формируем путь к файлу в той же папке
            Path filePath = jarDirectory.resolve(fileName);

            // Создаем файл и записываем в него текст
            try (BufferedWriter writer = new BufferedWriter(new FileWriter(filePath.toFile()))) {
                writer.write(content);
            }

            logger.info("Настройки карты успешно записаны");
        } catch (IOException | SecurityException | java.net.URISyntaxException e) {
            e.printStackTrace();
            logger.error("Ошибка при записи данных карты");
        }
    }

    /**
     * Открывает новое окно игры, закрывая текущее окно настроек.
     */
    private void openNewGame() {
        startGameButton.getScene().getWindow().hide();
        FXMLLoader loader = new FXMLLoader();
        if (noJar) {
            loader.setLocation(getClass().getResource("/application/GamePrototype.fxml"));
        } else {
            loader.setLocation(getClass().getResource("/application/JarEdition.fxml"));
        }
        try {
            Parent root = loader.load();
            Stage stage = new Stage();
            stage.setTitle("Pudge vs. Robo-Bunny");
            if (noJar) {
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
     * Открывает окно последней сохраненной игры, закрывая текущее окно настроек.
     */
    private void openLastGame() {
        lastGameButton.getScene().getWindow().hide();
        FXMLLoader loader = new FXMLLoader();
        if (noJar) {
            loader.setLocation(getClass().getResource("/application/GamePrototype.fxml"));
        } else {
            loader.setLocation(getClass().getResource("/application/JarEdition.fxml"));
        }
        try {
            Parent root = loader.load();
            Stage stage = new Stage();
            stage.setTitle("Pudge vs. Robo-Bunny");
            if (noJar) {
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

}