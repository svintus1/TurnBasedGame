package application;

import java.awt.*;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import javafx.scene.Parent;


/**
 * Класс контроллера для окна "Disclaimer".
 * Предоставляет действия для элементов пользовательского интерфейса.
 */
public class DisclaimerController {
    Image icon;

    @FXML
    private ResourceBundle resources;

    @FXML
    private URL location;

    @FXML
    private Hyperlink hyperlink;

    @FXML
    private Button nextButton;

    /**
     * Инициализация контроллера DisclaimerController.
     * Настраивает обработчики событий для Hyperlink и Next Button.
     */
    @FXML
    void initialize() {
        hyperlink.setOnAction(event ->
                openWebsite("https://www.jetbrains.com/ru-ru/idea/download/?section=windows")
        );

        nextButton.setOnAction(event -> {
            nextButton.getScene().getWindow().hide();
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(getClass().getResource("/application/Start.fxml"));
            try {
                loader.load();
            } catch (IOException e) {
                e.printStackTrace();
            }
            Parent root = loader.getRoot();
            Stage stage = new Stage();
            stage.setTitle("Pudge vs. Robo-Bunny");
            InputStream imageStream = getClass().getClassLoader().getResourceAsStream("images/icon.png");
            icon = new Image(imageStream);
            stage.getIcons().add(icon);
            stage.setScene(new Scene(root));
            stage.show();
        });
    }

    /**
     * Открывает указанный веб-сайт в браузере пользователя по умолчанию.
     *
     * @param url URL веб-сайта для открытия.
     */
    private void openWebsite(String url) {
        try {
            Desktop.getDesktop().browse(new URI(url));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
