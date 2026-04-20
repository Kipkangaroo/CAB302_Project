module com.lockedin.lockedin {
    requires javafx.controls;
    requires javafx.fxml;
    opens com.lockedin.lockedin to javafx.fxml;
    exports com.lockedin.lockedin;
    exports com.lockedin.lockedin.pages;
    opens com.lockedin.lockedin.pages to javafx.fxml;
}