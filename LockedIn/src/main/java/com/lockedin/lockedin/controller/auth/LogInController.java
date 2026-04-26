package com.lockedin.lockedin.controller.auth;

import java.io.IOException;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.stage.Stage;

public class LogInController {
    private static final String MAIN_VIEW = "/com/lockedin/lockedin/pages/layout/main-view.fxml";

    @FXML
    private Button loginBtn;

    @FXML
    protected void handleLogIn() throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource(MAIN_VIEW));
        Scene scene = new Scene(loader.load(), 410, 650);
        Stage stage = (Stage) loginBtn.getScene().getWindow();
        stage.setScene(scene);
    }
}
