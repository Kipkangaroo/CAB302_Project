package com.lockedin.lockedin.controller.workout;

import com.lockedin.lockedin.model.dao.DBExercisesDAO;
import com.lockedin.lockedin.model.dao.WorkoutRoutineDAO;
import com.lockedin.lockedin.model.entity.workout.Exercise;
import com.lockedin.lockedin.model.entity.workout.WorkoutExerciseEntry;
import com.lockedin.lockedin.model.session.CurrentUser;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * JavaFX controller for the start workout screen.
 * @author LockedIn Team
 * @version 1.0
 */
public class StartWorkoutController {
    private static final String WORKOUT_DETAIL_VIEW = "/com/lockedin/lockedin/pages/workout/workout-detail-view.fxml";

    private static int currentRoutineId = -1;
    private final Map<Integer, Exercise> exerciseCache = new HashMap<>();
    private final List<WorkoutRoutineDAO.CompletedSetData> completedSets = new ArrayList<>();
    @FXML
    private Button backButton;
    @FXML
    private Label workoutNameLabel;
    @FXML
    private Label progressTextLabel;
    @FXML
    private ProgressBar workoutProgressBar;
    @FXML
    private Label exerciseNameLabel;
    @FXML
    private Label setsLabel;
    @FXML
    private Label repsLabel;
    @FXML
    private Label restLabel;
    @FXML
    private Label setLabel;
    @FXML
    private Label targetLabel;
    @FXML
    private TextField completedRepsField;
    @FXML
    private Button completeSetButton;
    @FXML
    private HBox upNextBox;
    @FXML
    private Label upNextLabel;
    @FXML
    private ImageView exerciseImageView;
    private WorkoutRoutineDAO routineDAO;
    private DBExercisesDAO exercisesDAO;
    private WorkoutRoutineDAO.RoutineData routine;
    private int exerciseIndex;
    private int setIndex;
    private int totalSets;

    /**
     * Sets the current routine id.
     * @param routineId The routine id.
     */
    public static void setCurrentRoutineId(int routineId) {
        currentRoutineId = routineId;
    }
    /**
     * Initializes FXML-bound UI components after the view loads.
     */

    @FXML
    public void initialize() {
        routineDAO = new WorkoutRoutineDAO();
        exercisesDAO = new DBExercisesDAO();
        routine = routineDAO.getRoutineById(currentRoutineId);
        if (routine == null || routine.exercises.isEmpty()) {
            showError("This workout has no exercises to start.");
            navigateBack();
            return;
        }

        totalSets = routine.exercises.stream().mapToInt(WorkoutExerciseEntry::getSets).sum();
        workoutNameLabel.setText(routine.name);
        showCurrentSet();
    }

    /**
     * Handles completion of a set: - Validates reps input - Records the completed
     * set - Moves to
     * the next set or finishes the workout
     */
    @FXML
    public void handleCompleteSet() {
        WorkoutExerciseEntry exercise = currentExercise();
        int completedReps;
        try {
            String repsText = completedRepsField.getText().trim();
            completedReps = repsText.isEmpty() ? exercise.getReps() : Integer.parseInt(repsText);
            if (completedReps < 0) {
                throw new NumberFormatException();
            }
        } catch (NumberFormatException exception) {
            showError("Reps completed must be a whole number.");
            return;
        }

        completedSets.add(
                new WorkoutRoutineDAO.CompletedSetData(
                        exercise.getExerciseId(),
                        exercise.getExerciseName(),
                        setIndex + 1,
                        exercise.getReps(),
                        completedReps,
                        exercise.getRestSeconds()));

        if (isLastSet()) {
            finishWorkout();
            return;
        }

        advanceSet();
        showCurrentSet();
    }
    /**
     * Performs handle back.
     */

    @FXML
    public void handleBack() {
        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setHeaderText("Leave workout?");
        confirm.setContentText("Progress from this workout will not be saved.");
        confirm.showAndWait()
                .ifPresent(
                        buttonType -> {
                            if (buttonType == javafx.scene.control.ButtonType.OK) {
                                navigateBack();
                            }
                        });
    }

    /**
     * Performs show current set.
     */
    private void showCurrentSet() {
        WorkoutExerciseEntry exercise = currentExercise();
        int completed = completedSets.size();

        exerciseNameLabel.setText(exercise.getExerciseName());
        setsLabel.setText(String.valueOf(exercise.getSets()));
        repsLabel.setText(String.valueOf(exercise.getReps()));
        restLabel.setText(exercise.getRestSeconds() + "s");
        setLabel.setText("Set " + (setIndex + 1) + " of " + exercise.getSets());
        targetLabel.setText("Target: " + exercise.getReps() + " reps");
        completedRepsField.setText("");
        completedRepsField.setPromptText("Target: " + exercise.getReps() + " reps");
        updateExerciseImage(exercise);

        progressTextLabel.setText(
                "Exercise " + (exerciseIndex + 1) + " of " + routine.exercises.size());
        workoutProgressBar.setProgress(totalSets == 0 ? 0 : (double) completed / totalSets);
        completeSetButton.setText(isLastSet() ? "\u2713  Finish Workout" : "\u2713  Complete Set");

        String upNext = getUpNextText();
        upNextLabel.setText(upNext);
        upNextBox.setVisible(upNext != null);
        upNextBox.setManaged(upNext != null);
    }

    /**
     * Performs update exercise image.
     * @param entry The entry.
     */
    private void updateExerciseImage(WorkoutExerciseEntry entry) {
        Exercise exercise = exerciseCache.computeIfAbsent(
                entry.getExerciseId(), exerciseId -> exercisesDAO.getExerciseById(exerciseId));
        if (exercise == null
                || exercise.getExerciseImageId() == null
                || exercise.getExerciseImageId().isBlank()) {
            exerciseImageView.setImage(null);
            return;
        }
        exerciseImageView.setImage(new Image(exercise.getExerciseImageUrl(0), true));
    }

    /**
     * Performs current exercise.
     */
    private WorkoutExerciseEntry currentExercise() {
        return routine.exercises.get(exerciseIndex);
    }

    /**
     * Returns whether last set.
     * @return true if the condition holds; otherwise false.
     */
    private boolean isLastSet() {
        WorkoutExerciseEntry exercise = currentExercise();
        return exerciseIndex == routine.exercises.size() - 1 && setIndex == exercise.getSets() - 1;
    }

    /**
     * Performs advance set.
     */
    private void advanceSet() {
        WorkoutExerciseEntry exercise = currentExercise();
        if (setIndex < exercise.getSets() - 1) {
            setIndex++;
        } else {
            exerciseIndex++;
            setIndex = 0;
        }
    }

    /**
     * Returns the up next text.
     * @return The up next text.
     */
    private String getUpNextText() {
        WorkoutExerciseEntry exercise = currentExercise();
        if (setIndex < exercise.getSets() - 1) {
            return exercise.getExerciseName() + " - Set " + (setIndex + 2);
        }
        if (exerciseIndex < routine.exercises.size() - 1) {
            return routine.exercises.get(exerciseIndex + 1).getExerciseName();
        }
        return null;
    }

    /**
     * Performs finish workout.
     */
    private void finishWorkout() {
        workoutProgressBar.setProgress(1);
        int historyId = routineDAO.saveCompletedWorkout(CurrentUser.getId(), routine, completedSets);
        if (historyId < 0) {
            showError("Workout completed, but it could not be saved to history.");
            return;
        }

        Alert done = new Alert(Alert.AlertType.INFORMATION);
        done.setHeaderText("Workout complete");
        done.setContentText("Saved to workout history.");
        done.showAndWait();
        navigateBack();
    }

    /**
     * Performs navigate back.
     */
    private void navigateBack() {
        try {
            WorkoutDetailController.setCurrentRoutineId(currentRoutineId);
            Pane page = FXMLLoader.load(getClass().getResource(WORKOUT_DETAIL_VIEW));
            StackPane pc = (StackPane) backButton.getScene().lookup("#pageContainer");
            if (pc != null) {
                pc.getChildren().setAll(page);
            }
        } catch (IOException e) {
            throw new RuntimeException("Failed to navigate back to workout details", e);
        }
    }

    /**
     * Performs show error.
     * @param message The message.
     */
    private void showError(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
