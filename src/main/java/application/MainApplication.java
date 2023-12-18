package application;

/**
 * Дополнительный класс с входной точкой, для запуска JAR-архива.
 * Этот класс служит для вызова метода main из основного класса Main.
 */
public class MainApplication {
    /**
     * Точка входа в программу (через дополнительный класс).
     *
     * @param args Аргументы командной строки, переданные при запуске программы.
     */
    public static void main(String[] args){
        Main.main(args);
    }
}
