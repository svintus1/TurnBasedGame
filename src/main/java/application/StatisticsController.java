package application;

import java.io.*;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ResourceBundle;

import javafx.fxml.FXML;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.XYChart;
import javafx.scene.layout.StackPane;

/**
 * Контроллер для окна статистики с соответствующим FXML-файлом
 * "Statistics.fxml". Отвечает за отображение графиков статистики игры.
 */
public class StatisticsController {
    String[] playerNumCells;
    String[] playerAiNumCells;
    String[] playerNumUnits;
    String[] playerAiNumUnits;

    @FXML
    private ResourceBundle resources;

    @FXML
    private URL location;

    @FXML
    private LineChart<String, Number> lineChartCell;

    @FXML
    private LineChart<String, Number> lineChartUnits;

    /**
     * Инициализирует контроллер окна статистики. Метод считывает данные из файла
     * "dataGraph.txt" и строит линейные графики для количества ячеек и юнитов у
     * игрока и ИИ. Графики окрашены в разные цвета для легкости восприятия (синий и
     * красный). Для каждого типа данных (количества ячеек и юнитов) создается
     * график.
     */
    @FXML
    void initialize() {
        readFile();
        XYChart.Series<String, Number> series = new XYChart.Series<>();
        for (int i = 0; i < playerNumCells.length; i++)
            series.getData().add(new XYChart.Data<>(String.valueOf(i + 1), Integer.parseInt(playerNumCells[i])));
        lineChartCell.getData().add(series);
        lineChartCell.lookup(".default-color0.chart-series-line").setStyle("-fx-stroke: #4682B4;");
        setNodeStyle(series, "-fx-background-color: #4682B4;");

        XYChart.Series<String, Number> series2 = new XYChart.Series<>();
        for (int i = 0; i < playerAiNumCells.length; i++)
            series2.getData().add(new XYChart.Data<>(String.valueOf(i + 1), Integer.parseInt(playerAiNumCells[i])));
        lineChartCell.getData().add(series2);
        lineChartCell.lookup(".default-color1.chart-series-line").setStyle("-fx-stroke: #8B0000;");
        setNodeStyle(series2, "-fx-background-color: #8B0000;");

        XYChart.Series<String, Number> series3 = new XYChart.Series<>();
        for (int i = 0; i < playerAiNumCells.length; i++)
            series3.getData().add(new XYChart.Data<>(String.valueOf(i + 1), Integer.parseInt(playerNumUnits[i])));
        lineChartUnits.getData().add(series3);
        lineChartUnits.lookup(".default-color0.chart-series-line").setStyle("-fx-stroke: #4682B4;");
        setNodeStyle(series3, "-fx-background-color: #4682B4;");

        XYChart.Series<String, Number> series4 = new XYChart.Series<>();
        for (int i = 0; i < playerAiNumCells.length; i++)
            series4.getData().add(new XYChart.Data<>(String.valueOf(i + 1), Integer.parseInt(playerAiNumUnits[i])));
        lineChartUnits.getData().add(series4);
        lineChartUnits.lookup(".default-color1.chart-series-line").setStyle("-fx-stroke: #8B0000;");
        setNodeStyle(series4, "-fx-background-color: #8B0000;");
    }

    /**
     * Устанавливает стиль для узлов данных графика.
     *
     * @param series График, для которого устанавливается стиль.
     * @param style  Стиль, который будет применен к узлам данных.
     */
    private void setNodeStyle(XYChart.Series<String, Number> series, String style) {
        for (XYChart.Data<String, Number> data : series.getData()) {
            StackPane stackPane = (StackPane) data.getNode();
            stackPane.setStyle(style);
        }
    }

    /**
     * Считывает данные из файла "dataGraph.txt".
     */
    private void readFile() {
        if (Main.class.getProtectionDomain().getCodeSource().getLocation().getPath().endsWith(".jar")) {
            try {
                // Получаем путь к расположению JAR-файла
                String jarPath = Main.class.getProtectionDomain().getCodeSource().getLocation().toURI().getPath();
                Path jarDirectory = Paths.get(new File(jarPath).getParent());

                // Формируем путь к файлу в той же папке
                Path filePath = jarDirectory.resolve("dataGraph.txt");

                try (InputStream inputStream = Files.newInputStream(filePath);
                     BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream))) {
                    playerNumCells = bufferedReader.readLine().split(" ");
                    playerAiNumCells = bufferedReader.readLine().split(" ");
                    playerNumUnits = bufferedReader.readLine().split(" ");
                    playerAiNumUnits = bufferedReader.readLine().split(" ");
                }
            } catch (IOException | URISyntaxException e) {
                e.printStackTrace();
            }
        } else {
            try {
                FileReader fileReader = new FileReader("dataGraph.txt");
                BufferedReader bufferedReader = new BufferedReader(fileReader);
                playerNumCells = bufferedReader.readLine().split(" ");
                playerAiNumCells = bufferedReader.readLine().split(" ");
                playerNumUnits = bufferedReader.readLine().split(" ");
                playerAiNumUnits = bufferedReader.readLine().split(" ");
                bufferedReader.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

}
