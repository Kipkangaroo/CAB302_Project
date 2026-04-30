package com.lockedin.lockedin.app;

import com.lockedin.lockedin.model.dao.DBExercisesDAO;
import com.lockedin.lockedin.model.dao.UserDAO;
import com.lockedin.lockedin.model.dao.WorkoutRoutineDAO;
import java.io.IOException;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class LockedIn extends Application {
    @Override
    public void start(Stage stage) throws IOException {
        new DBExercisesDAO();
        new UserDAO();
        new WorkoutRoutineDAO();
        FXMLLoader fxmlLoader = new FXMLLoader(
                LockedIn.class.getResource("/com/lockedin/lockedin/pages/auth/login-view.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 410, 700);
        stage.setTitle("LockedIN");
        stage.setResizable(false);
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        Application.launch(LockedIn.class, args);
    }
}
