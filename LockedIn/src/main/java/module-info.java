module com.lockedin.lockedin {
    requires javafx.controls;
    requires javafx.fxml;
    requires org.xerial.sqlitejdbc;
    requires java.sql;
    opens com.lockedin.lockedin to javafx.fxml;
    exports com.lockedin.lockedin.controller;
    opens com.lockedin.lockedin.controller to javafx.fxml;
    exports com.lockedin.lockedin;
}