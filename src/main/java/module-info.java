module com.jogosfx.jogosfx {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;
    requires mysql.connector.j;
    requires jbcrypt;

    opens com.jogosfx to javafx.fxml;
    exports com.jogosfx;
    exports com.jogosfx.util;
}