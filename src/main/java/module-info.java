module com.jogosfx.jogosfx {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;
    requires mysql.connector.j;


    opens com.jogosfx to javafx.fxml;
    exports com.jogosfx;
}