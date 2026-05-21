package com.lockedin.lockedin.controller.workout;

import com.lockedin.lockedin.model.dao.DBExercisesDAO;
import com.lockedin.lockedin.model.entity.workout.Exercise;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;

import org.controlsfx.control.SearchableComboBox;

import java.io.IOException;
import java.util.Objects;

/**
 * JavaFX controller for the exercise view screen.
 * @author LockedIn Team
 * @version 1.0
 */
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
    private ImageView formImage1;
    @FXML
    private ImageView formImage2;

    /**
     * Initializes the exercise search view: - Loads exercises from the database -
     * Configures
     * visibility of the details pane - Adds listener to update details when an
     * exercise is selected
     */
    @FXML
    public void initialize() {
        ObservableList<Exercise> exercises;
        try {
            // load exercises from database
            exercises = new DBExercisesDAO().getAllExercises();
        } catch (RuntimeException exception) {
            exercises = FXCollections.observableArrayList();
            System.err.println("Failed to load exercises from database: " + exception.getMessage());
        }
        // set exercises to the search box
        exerciseSearch.setItems(exercises);
        exerciseSearch.setVisibleRowCount(4);
        exerciseDetails.visibleProperty().bind(exerciseSearch.valueProperty().isNotNull());
        exerciseDetails.managedProperty().bind(exerciseDetails.visibleProperty());
        instructions.setWrapText(true);

        // when an exercise is selected, set the details to the exercise
        exerciseSearch
                .valueProperty()
                .addListener(
                        (observable, oldValue, newValue) -> {
                            if (newValue != null) {
                                updateExerciseDetails(newValue);
                            } else {
                                selectedExerciseId = null;
                            }
                        });
    }
    /**
     * Performs handle back button.
     * @throws IOException If the operation fails.
     */

    @FXML
    public void handleBackButton() throws IOException {
        Pane workoutPage = FXMLLoader.load(Objects.requireNonNull(getClass().getResource(WORKOUT_VIEW)));
        StackPane pageContainer = (StackPane) backButton.getScene().lookup("#pageContainer");
        if (pageContainer != null) {
            pageContainer.getChildren().setAll(workoutPage);
        }
    }

    /**
     * Returns the selected exercise id.
     * @return The selected exercise id.
     */
    public Integer getSelectedExerciseId() {
        return selectedExerciseId;
    }

    /**
     * Performs update exercise details.
     * @param exercise The exercise.
     */
    private void updateExerciseDetails(Exercise exercise) {
        selectedExerciseId = exercise.getId();
        exerciseName.setText(exercise.getName());
        primaryMuscle.setText(exercise.getPrimaryMuscle());
        instructions.setText(
                exercise.getInstruction() == null
                        ? ""
                        : exercise.getInstruction().replace("\\n", "\n"));
        formImage1.setImage(new Image(exercise.getExerciseImageUrl(0)));
        formImage2.setImage(new Image(exercise.getExerciseImageUrl(1)));
    }
}
