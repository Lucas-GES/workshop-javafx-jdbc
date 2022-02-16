module application.javafx2 {
    requires javafx.controls;
    requires javafx.fxml;


    opens application.javafx2 to javafx.fxml;
    exports application.javafx2;

}