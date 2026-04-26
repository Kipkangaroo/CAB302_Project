package com.lockedin.lockedin.controller.workout;

import com.lockedin.lockedin.model.dao.DBExercisesDAO;
import com.lockedin.lockedin.model.entity.Exercise;
import java.io.IOException;
import java.util.Objects;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import org.controlsfx.control.SearchableComboBox;

public class ExerciseViewController {
    private static final String WORKOUT_VIEW = "/com/lockedin/lockedin/pages/workout/workout-view.fxml";
    private Integer selectedExerciseId;

    @FXML
    private ScrollPane exerciseDetails;
    @FXML
    private Button backButton;
    @FXML
    private SearchableComboBox<Exercise> exerciseSearch;
    @FXML
    private Label exerciseName;
    @FXML
    private Label primaryMuscle;
    @FXML
    private Label instructions;

    @FXML
    public void initialize() {
        ObservableList<Exercise> exercises;
        try {
            //load exercises from database
            exercises = new DBExercisesDAO().getAllExercises();
        } catch (RuntimeException exception) {
            exercises = FXCollections.observableArrayList();
            System.err.println("Failed to load exercises from database: " + exception.getMessage());
        }
        //set exercises to the search box
        exerciseSearch.setItems(exercises);
        exerciseSearch.setVisibleRowCount(4);

        exerciseDetails.visibleProperty().bind(exerciseSearch.valueProperty().isNotNull());
        exerciseDetails.managedProperty().bind(exerciseDetails.visibleProperty());
        
        //when an exercise is selected, set the details to the exercise
        exerciseSearch.valueProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                selectedExerciseId = newValue.getId();
                exerciseName.setText(newValue.getName());
                primaryMuscle.setText(newValue.getPrimaryMuscle());
                instructions.setText(newValue.getInstruction());
            } else {
                selectedExerciseId = null;
            }
        });
    }

    @FXML
    public void handleBackButton() throws IOException {
        Pane workoutPage = FXMLLoader.load(Objects.requireNonNull(
                getClass().getResource(WORKOUT_VIEW)));
        StackPane pageContainer = (StackPane) backButton.getScene().lookup("#pageContainer");
        if (pageContainer != null) {
            pageContainer.getChildren().setAll(workoutPage);
        }
    }

    public Integer getSelectedExerciseId() {
        return selectedExerciseId;
    }
}
