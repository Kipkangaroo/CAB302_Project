package com.lockedin.lockedin.app;

import java.io.IOException;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class LockedIn extends Application {
    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(
                LockedIn.class.getResource("/com/lockedin/lockedin/pages/auth/login-view.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 410, 650);
        stage.setTitle("LockedIN");
        stage.setResizable(false);
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        Application.launch(LockedIn.class, args);
    }
}
