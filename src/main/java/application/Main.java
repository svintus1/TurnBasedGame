package application;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.layout.AnchorPane;

import java.io.IOException;
import java.io.InputStream;

/**
 * Главный класс приложения, расширяющий {@link Application}. Запускает
 * графический интерфейс приложения.
 */
public class Main extends Application {
    Image icon;

    /**
     * Метод, вызывающийся при запуске приложения. Загружает графический интерфейс
     * из файла "Start.fxml" и отображает его в окне приложения.
     *
     * @param primaryStage Стартовое окно приложения.
     */
    @Override
    public void start(Stage primaryStage) {
        if (!Main.class.getProtectionDomain().getCodeSource().getLocation().getPath().endsWith(".jar")) {
            try {
                AnchorPane root = FXMLLoader.load(getClass().getResource("Start.fxml"));
                Scene scene = new Scene(root, 950, 650);
                primaryStage.setTitle("Pudge vs. Robo-Bunny");
                icon = new Image("file:images/icon.png");
                primaryStage.getIcons().add(icon);
                primaryStage.setScene(scene);
                primaryStage.show();
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            startGameJar();
        }
    }

    /**
     * Главный метод, запускающий приложение.
     *
     * @param args Аргументы командной строки.
     */
    public static void main(String[] args) {
        System.setProperty("log4j.configurationFile", "log4j2.xml");
        launch(args);
    }

    /**
     * Запускает игру при запуске JAR файла, загружая начальный интерфейс из файла Disclaimer.fxml.
     * Создает новое окно, устанавливает название и иконку окна.
     */
    private void startGameJar() {
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(MainApplication.class.getResource("/application/Disclaimer.fxml"));
        try {
            loader.load();
        } catch (IOException e) {
            e.printStackTrace();
        }
        Parent root = loader.getRoot();
        Stage stage = new Stage();
        stage.setTitle("Pudge vs. Robo-Bunny");
        InputStream imageStream = MainApplication.class.getClassLoader().getResourceAsStream("images/icon.png");
        icon = new Image(imageStream);
        stage.getIcons().add(icon);
        stage.setScene(new Scene(root));
        stage.show();
    }
}