package application;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ResourceBundle;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import javafx.scene.Parent;
import javafx.scene.Scene;

/**
 * Контроллер для стартового окна приложения.
 */
public class StartController {
    Image icon;
    Audio audio;
    private static final Logger logger = LogManager.getLogger(StartController.class.getName());

    /**
     * Конструктор класса StartController. Создает экземпляр класса Audio и
     * запускает воспроизведение музыкального файла "startAudio.wav".
     */
    public StartController() {
        audio = new Audio();
        audio.playMusic("startAudio.wav");
    }

    @FXML
    private ResourceBundle resources;

    @FXML
    private URL location;

    @FXML
    private Button startButton;

    /**
     * Инициализация контроллера при загрузке FXML-файла. Устанавливает обработчик
     * события для кнопки "startButton".
     */
    @FXML
    void initialize() {
        startButton.setOnAction(event -> {
            audio.stopMusic();

            if (!Main.class.getProtectionDomain().getCodeSource().getLocation().getPath().endsWith(".jar"))
                audio.playMusic("click.wav");

            startButton.getScene().getWindow().hide();

            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(getClass().getResource("/application/Settings.fxml"));
            try {
                loader.load();
            } catch (IOException e) {
                e.printStackTrace();
            }
            Parent root = loader.getRoot();
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
            logger.info("Нажата кнопка начать игру.");
        });
    }

}
