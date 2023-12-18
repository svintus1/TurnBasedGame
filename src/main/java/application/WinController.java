package application;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

import javafx.animation.ScaleTransition;
import javafx.animation.TranslateTransition;
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
 * Контроллер для окна победы с соответствующим FXML-файлом "Victory.fxml".
 * Отвечает за отображение анимаций и звуковых эффектов при победе игрока в
 * игре.
 */
public class WinController {
    Audio audio = new Audio();

    @FXML
    private ResourceBundle resources;

    @FXML
    private URL location;

    @FXML
    private ImageView pudgeGif1;

    @FXML
    private ImageView pudgeGif2;

    @FXML
    private ImageView fireGIF;

    @FXML
    private ImageView spaceGIF;

    @FXML
    private Button statisticsButton;

    /**
     * Инициализирует контроллер окна победы. Запускает цепочку анимаций и звуковых
     * эффектов, завершающуюся отображением кнопки статистики. При нажатии на кнопку
     * статистики проигрывает звук "click.wav" и переходит к окну статистики,
     * закрывая текущее окно победы.
     */
    @FXML
    void initialize() {
        audio.playMusic("/pudge/смех.wav");
        pudgeGif2.setVisible(false);
        fireGIF.setVisible(false);
        spaceGIF.setVisible(false);
        statisticsButton.setVisible(false);
        spaceGIF.setScaleX(50);
        spaceGIF.setScaleY(50);
        TranslateTransition pudgeTransition = new TranslateTransition(Duration.millis(4620), pudgeGif1);
        pudgeTransition.setToX(-215);
        pudgeTransition.play();
        pudgeTransition.setOnFinished(eventFinished -> {
            pudgeGif1.setVisible(false);
            audio.playMusic("дай освежую.wav");
            CompletableFuture.delayedExecutor(3000, TimeUnit.MILLISECONDS).execute(() -> {
                audio.playMusic("уходит к истоку.wav");
            });
            CompletableFuture.delayedExecutor(5900, TimeUnit.MILLISECONDS).execute(() -> {
                audio.playMusic("черт.wav");
            });
            CompletableFuture.delayedExecutor(8000, TimeUnit.MILLISECONDS).execute(() -> {
                audio.playMusic("последняя трапеза.wav");
            });
            CompletableFuture.delayedExecutor(11650, TimeUnit.MILLISECONDS).execute(() -> {
                CompletableFuture.delayedExecutor(500, TimeUnit.MILLISECONDS).execute(() -> {
                    audio.playMusic("НЕЕТ.wav");
                });
                pudgeGif2.setVisible(true);
                ScaleTransition scaleTransition = new ScaleTransition(Duration.millis(2290), pudgeGif2);
                scaleTransition.setToX(3);
                scaleTransition.setToY(3);
                scaleTransition.play();

                ScaleTransition spaceTransition = new ScaleTransition(Duration.millis(15000), spaceGIF);
                spaceTransition.setToX(1);
                spaceTransition.setToY(1);

                scaleTransition.setOnFinished(event -> {
                    pudgeGif2.setVisible(false);
                    fireGIF.setVisible(true);
                    audio.playMusic("взрыв.wav");
                    CompletableFuture.delayedExecutor(450, TimeUnit.MILLISECONDS).execute(() -> {
                        audio.playMusic("дыра.wav");
                        CompletableFuture.delayedExecutor(5500, TimeUnit.MILLISECONDS).execute(() -> {
                            audio.playMusic("раскол.wav");
                        });
                        fireGIF.setVisible(false);
                        spaceGIF.setVisible(true);
                        spaceTransition.play();
                        spaceTransition.setOnFinished(event1 -> {
                            CompletableFuture.delayedExecutor(1000, TimeUnit.MILLISECONDS).execute(() -> {
                                statisticsButton.setVisible(true);
                            });
                        });
                    });
                });
            });
        });

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
                Image icon = new Image("file:images/icon.png");
                stage.getIcons().add(icon);
                stage.setScene(new Scene(root));
                stage.show();
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
    }

}

