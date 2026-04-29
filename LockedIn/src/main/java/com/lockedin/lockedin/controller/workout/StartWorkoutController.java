package com.lockedin.lockedin.controller.workout;

import com.lockedin.lockedin.model.dao.WorkoutRoutineDAO;
import com.lockedin.lockedin.model.entity.WorkoutExerciseEntry;
import com.lockedin.lockedin.model.session.CurrentUser;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class StartWorkoutController {
    private static final String WORKOUT_DETAIL_VIEW =
            "/com/lockedin/lockedin/pages/workout/workout-detail-view.fxml";

    private static int currentRoutineId = -1;

    public static void setCurrentRoutineId(int routineId) {
        currentRoutineId = routineId;
    }

    @FXML private Button backButton;
    @FXML private Label workoutNameLabel;
    @FXML private Label progressTextLabel;
    @FXML private ProgressBar workoutProgressBar;
    @FXML private Label exerciseNameLabel;
    @FXML private Label setsLabel;
    @FXML private Label repsLabel;
    @FXML private Label restLabel;
    @FXML private Label setLabel;
    @FXML private Label targetLabel;
    @FXML private TextField completedRepsField;
    @FXML private Button completeSetButton;
    @FXML private HBox upNextBox;
    @FXML private Label upNextLabel;

    private WorkoutRoutineDAO routineDAO;
    private WorkoutRoutineDAO.RoutineData routine;
    private final List<WorkoutRoutineDAO.CompletedSetData> completedSets = new ArrayList<>();
    private int exerciseIndex;
    private int setIndex;
    private int totalSets;

    @FXML
    public void initialize() {
        routineDAO = new WorkoutRoutineDAO();
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

        completedSets.add(new WorkoutRoutineDAO.CompletedSetData(
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

    @FXML
    public void handleBack() {
        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setHeaderText("Leave workout?");
        confirm.setContentText("Progress from this workout will not be saved.");
        confirm.showAndWait().ifPresent(buttonType -> {
            if (buttonType == javafx.scene.control.ButtonType.OK) {
                navigateBack();
            }
        });
    }

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

        progressTextLabel.setText("Exercise " + (exerciseIndex + 1) + " of " + routine.exercises.size());
        workoutProgressBar.setProgress(totalSets == 0 ? 0 : (double) completed / totalSets);
        completeSetButton.setText(isLastSet() ? "\u2713  Finish Workout" : "\u2713  Complete Set");

        String upNext = getUpNextText();
        upNextLabel.setText(upNext);
        upNextBox.setVisible(upNext != null);
        upNextBox.setManaged(upNext != null);
    }

    private WorkoutExerciseEntry currentExercise() {
        return routine.exercises.get(exerciseIndex);
    }

    private boolean isLastSet() {
        WorkoutExerciseEntry exercise = currentExercise();
        return exerciseIndex == routine.exercises.size() - 1 && setIndex == exercise.getSets() - 1;
    }

    private void advanceSet() {
        WorkoutExerciseEntry exercise = currentExercise();
        if (setIndex < exercise.getSets() - 1) {
            setIndex++;
        } else {
            exerciseIndex++;
            setIndex = 0;
        }
    }

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

    private void showError(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
