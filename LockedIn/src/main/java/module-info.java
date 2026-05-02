module com.lockedin.lockedin {
    requires javafx.controls;
    requires javafx.fxml;
    requires org.xerial.sqlitejdbc;
    requires java.sql;
    requires org.controlsfx.controls;

    exports com.lockedin.lockedin.app;

    opens com.lockedin.lockedin.controller.auth to
            javafx.fxml;
    opens com.lockedin.lockedin.controller.layout to
            javafx.fxml;
    opens com.lockedin.lockedin.controller.workout to
            javafx.fxml;
    opens com.lockedin.lockedin.controller.diet to
            javafx.fxml;
    opens com.lockedin.lockedin.controller.profile to
            javafx.fxml;
}
