package com.lockedin.lockedin;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import java.io.IOException;

public class LockedIn extends Application {
    @Override
    public void start(Stage stage) throws IOException {
        //loads the initial frame (log-in page) and set window details
        FXMLLoader fxmlLoader = new FXMLLoader(LockedIn.class.getResource("login-view.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 410, 650);
        stage.setTitle("LockedIN");
        stage.setScene(scene);
        stage.show();
    }
    public static void main(String[] args) {
        //launch the log-in page
        Application.launch(LockedIn.class, args);
    }
}
