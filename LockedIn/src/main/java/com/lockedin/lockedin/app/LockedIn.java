package com.lockedin.lockedin.app;

import com.lockedin.lockedin.model.dao.DBExercisesDAO;
import com.lockedin.lockedin.model.dao.UserDAO;
import com.lockedin.lockedin.model.dao.WorkoutRoutineDAO;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.Objects;

/**
 * Provides locked in functionality for LockedIn.
 * @author LockedIn Team
 * @version 1.0
 */
public class LockedIn extends Application {
        /**
         * Application entry point that launches the JavaFX runtime.
         * @param args The args.
         */
        public static void main(String[] args) {
                Application.launch(LockedIn.class, args);
        }
        /**
         * Performs start.
         * @param stage The stage.
         * @throws IOException If the operation fails.
         */

        @Override
        public void start(Stage stage) throws IOException {
                new DBExercisesDAO();
                new UserDAO();
                new WorkoutRoutineDAO();
                JohnDemoSeeder.seedIfAbsent();
                FXMLLoader fxmlLoader = new FXMLLoader(
                                LockedIn.class.getResource(
                                                "/com/lockedin/lockedin/pages/auth/login-view.fxml"));
                Scene scene = new Scene(fxmlLoader.load(), 410, 750);
                stage.setTitle("LockedIN");
                stage.getIcons()
                                .add(
                                                new Image(
                                                                Objects.requireNonNull(
                                                                                LockedIn.class.getResource(
                                                                                                "/com/lockedin/lockedin/graphics/images/logo.png"),
                                                                                "logo.png missing from classpath")
                                                                                .toExternalForm()));
                stage.setResizable(false);
                stage.setScene(scene);
                stage.show();
        }
}
