module com.lockedin.lockedin {
    requires javafx.controls;
    requires javafx.fxml;
    requires org.xerial.sqlitejdbc;
    opens com.lockedin.lockedin to javafx.fxml;
    exports com.lockedin.lockedin;
    exports com.lockedin.lockedin.pages;
    opens com.lockedin.lockedin.pages to javafx.fxml;
}