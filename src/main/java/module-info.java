module application.mygame {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.desktop;
    requires org.apache.logging.log4j;


    opens application to javafx.fxml;
    exports application;
}