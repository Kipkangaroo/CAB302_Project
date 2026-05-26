package com.lockedin.lockedin.controller.workout;

import com.lockedin.lockedin.controller.layout.LayoutController;
import com.lockedin.lockedin.controller.navigation.PageNavigator;
import com.lockedin.lockedin.model.dao.ExercisesDAO;
import com.lockedin.lockedin.model.entity.workout.Exercise;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

import org.controlsfx.control.SearchableComboBox;

/**
 * JavaFX controller for the exercise view screen.
 * @author LockedIn Team
 * @version 1.0
 */
public class ExerciseViewController {
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
            exercises = new ExercisesDAO().getAllExercises();
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
     * Handle back button.
     */

    @FXML
    public void handleBackButton() {
        PageNavigator.loadPage(backButton, LayoutController.WORKOUT_VIEW);
    }

            /**
     * Returns the selected exercise id.
     * @return selected exercise id
     */
    public Integer getSelectedExerciseId() {
        return selectedExerciseId;
    }

        /**
     * Update exercise details.
     * @param exercise exercise
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
