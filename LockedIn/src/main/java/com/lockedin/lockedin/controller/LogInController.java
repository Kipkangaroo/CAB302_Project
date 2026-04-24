package com.lockedin.lockedin.controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.stage.Stage;

import java.io.IOException;

public class LogInController {
    private static final String MAIN_VIEW = "/com/lockedin/lockedin/main-view.fxml";
    @FXML
    private Button loginBtn;

    @FXML
    protected void handleLogIn() throws IOException {
        //switches to the main-view frame after log-in button press
        FXMLLoader loader = new FXMLLoader(getClass().getResource(MAIN_VIEW));
        Scene scene = new Scene(loader.load(), 410, 650);
        Stage stage = (Stage) loginBtn.getScene().getWindow();
        stage.setScene(scene);
    }
}
