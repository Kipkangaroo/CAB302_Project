package com.lockedin.lockedin.controller.workout;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import java.io.IOException;
import java.util.Objects;

public class WorkoutController {
    private static final String EXERCISE_LIST_VIEW = "/com/lockedin/lockedin/pages/workout/exercise-list-view.fxml";

    @FXML
    public Button viewExercisesButton;

    @FXML
    public void handleExercises() throws IOException {
        Pane exercisesPage = FXMLLoader.load(Objects.requireNonNull(
                getClass().getResource(EXERCISE_LIST_VIEW)
        ));
        StackPane pageContainer = (StackPane) viewExercisesButton.getScene().lookup("#pageContainer");
        if (pageContainer != null) {
            pageContainer.getChildren().setAll(exercisesPage);
        }
    }
}
