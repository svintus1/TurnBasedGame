package application;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

import javafx.animation.FadeTransition;
import javafx.animation.ScaleTransition;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;
import javafx.util.Duration;

/**
 * Контроллер для окна поражения с соответствующим FXML-файлом "Loss.fxml".
 * Отвечает за отображение анимаций и звуковых эффектов при поражении игрока в
 * игре.
 */
public class LossController {
    Audio audio = new Audio();
    String[] playerNumCells;
    String[] playerAiNumCells;
    String[] playerNumUnits;
    String[] playerAiNumUnits;

    /**
     * Конструктор класса LossController. Проигрывает звук "дыра.wav". Через
     * определенное время (8 секунд) запускает звук "исчезнет твоя самость.wav".
     */
    public LossController() {
        audio.playMusic("дыра.wav");
        CompletableFuture.delayedExecutor(8, TimeUnit.SECONDS).execute(() -> {
            audio.playMusic("исчезнет твоя самость.wav");
        });
    }

    @FXML
    private ResourceBundle resources;

    @FXML
    private URL location;

    @FXML
    private ImageView spaceIMG;

    @FXML
    private ImageView playerAiIMG;

    @FXML
    private Button statisticsButton;

    /**
     * Инициализирует контроллер окна поражения. Запускает анимацию увеличения
     * изображения пространства и вражеского персонажа. По завершении анимации
     * вражеского персонажа запускает анимацию затухания и отображает кнопку
     * статистики. При нажатии на кнопку статистики проигрывает звук "click.wav" и
     * переходит к окну статистики, закрывая текущее окно поражения.
     */
    @FXML
    void initialize() {
        statisticsButton.setVisible(false);
        ScaleTransition scaleTransition = new ScaleTransition(Duration.millis(15000), spaceIMG);
        scaleTransition.setToX(12);
        scaleTransition.setToY(12);
        ScaleTransition scaleTransition2 = new ScaleTransition(Duration.millis(10000), playerAiIMG);
        scaleTransition2.setToX(6);
        scaleTransition2.setToY(6);
        scaleTransition2.setOnFinished(eventFinished -> {
            CompletableFuture.delayedExecutor(1, TimeUnit.SECONDS).execute(() -> {
                FadeTransition fadeOut = new FadeTransition(Duration.seconds(3), playerAiIMG);
                fadeOut.setToValue(0.0);
                fadeOut.play();
                fadeOut.setOnFinished(event -> {
                    statisticsButton.setVisible(true);
                });
            });
        });
        scaleTransition.play();
        scaleTransition2.play();

        statisticsButton.setOnAction(event -> {
            audio.playMusic("click.wav");
            statisticsButton.setVisible(false);
            statisticsButton.getScene().getWindow().hide();
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(getClass().getResource("/application/Statistics.fxml"));
            try {
                Parent root = loader.load();
                Stage stage = new Stage();
                stage.setTitle("Pudge vs. Robo-Bunny");
                Image icon = new Image("file:images/Main Window/icon.png");
                stage.getIcons().add(icon);
                stage.setScene(new Scene(root));
                stage.show();
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
    }
}
