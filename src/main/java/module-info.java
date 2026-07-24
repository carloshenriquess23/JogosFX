module com.jogosfx.jogosfx {
    requires javafx.controls;
    requires javafx.fxml;


    opens com.jogosfx to javafx.fxml;
    exports com.jogosfx;
}