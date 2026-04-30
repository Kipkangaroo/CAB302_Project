package com.lockedin.lockedin.controller.workout;

import com.lockedin.lockedin.model.dao.WorkoutRoutineDAO;
import com.lockedin.lockedin.model.session.CurrentUser;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
/**
 * Controller for the Workout History page.
 * Displays all completed workouts for the current user,
 * grouped with exercise summaries and completion timestamps.
 */
public class WorkoutHistoryController {
    private static final String WORKOUT_VIEW =
            "/com/lockedin/lockedin/pages/workout/workout-view.fxml";
    private static final DateTimeFormatter DISPLAY_DATE =
            DateTimeFormatter.ofPattern("dd MMM yyyy, h:mm a");

    @FXML private Button backButton;
    @FXML private Label historyCountLabel;
    @FXML private VBox historyContainer;

    private WorkoutRoutineDAO routineDAO;

    @FXML
    public void initialize() {
        routineDAO = new WorkoutRoutineDAO();
        loadHistory();
    }

    private void loadHistory() {
        List<WorkoutRoutineDAO.CompletedWorkoutData> workouts =
                routineDAO.getCompletedWorkoutsByUser(CurrentUser.getId());

        historyContainer.getChildren().clear();
        historyCountLabel.setText(workouts.isEmpty()
                ? "No completed workouts yet"
                : workouts.size() + " completed workout" + (workouts.size() == 1 ? "" : "s"));

        if (workouts.isEmpty()) {
            Label empty = new Label("Complete a workout to see it here.");
            empty.setStyle("-fx-text-fill: #757575; -fx-font-size: 13px;");
            empty.setWrapText(true);
            historyContainer.getChildren().add(empty);
            return;
        }

        for (WorkoutRoutineDAO.CompletedWorkoutData workout : workouts) {
            historyContainer.getChildren().add(buildHistoryCard(workout));
        }
    }

    private VBox buildHistoryCard(WorkoutRoutineDAO.CompletedWorkoutData workout) {
        Label nameLabel = new Label(workout.routineName);
        nameLabel.setStyle("-fx-font-size: 17px; -fx-font-weight: bold; -fx-text-fill: #061424;");
        nameLabel.setWrapText(true);

        Label dateLabel = new Label(formatCompletedAt(workout.completedAt));
        dateLabel.setStyle("-fx-font-size: 12px; -fx-text-fill: #65758a;");

        VBox titleColumn = new VBox(3, nameLabel, dateLabel);
        HBox.setHgrow(titleColumn, Priority.ALWAYS);

        Label summaryBadge = new Label(workout.totalExercises + " exercises  -  " + workout.totalSets + " sets");
        summaryBadge.getStyleClass().add("exercise-badge");

        HBox topRow = new HBox(8, titleColumn, summaryBadge);
        topRow.setAlignment(Pos.TOP_LEFT);

        VBox exerciseSummary = new VBox(6);
        Map<String, ExerciseTotals> totals = summarizeExercises(workout.sets);
        totals.forEach((exerciseName, total) -> exerciseSummary.getChildren().add(
                buildExerciseSummaryRow(exerciseName, total)));

        VBox card = new VBox(12, topRow, exerciseSummary);
        card.getStyleClass().add("workout-card");
        return card;
    }

    private HBox buildExerciseSummaryRow(String exerciseName, ExerciseTotals total) {
        Label exerciseLabel = new Label(exerciseName);
        exerciseLabel.setStyle("-fx-font-size: 13px; -fx-font-weight: bold; -fx-text-fill: #263241;");
        exerciseLabel.setWrapText(true);
        HBox.setHgrow(exerciseLabel, Priority.ALWAYS);

        Label detailLabel = new Label(total.sets + " sets  -  " + total.completedReps + "/" + total.targetReps + " reps");
        detailLabel.setStyle("-fx-font-size: 12px; -fx-text-fill: #1565C0; -fx-font-weight: bold;");

        HBox row = new HBox(8, exerciseLabel, detailLabel);
        row.setAlignment(Pos.CENTER_LEFT);
        return row;
    }

    private Map<String, ExerciseTotals> summarizeExercises(List<WorkoutRoutineDAO.CompletedSetData> sets) {
        Map<String, ExerciseTotals> totals = new LinkedHashMap<>();
        for (WorkoutRoutineDAO.CompletedSetData set : sets) {
            ExerciseTotals total = totals.computeIfAbsent(set.exerciseName, ignored -> new ExerciseTotals());
            total.sets++;
            total.targetReps += set.targetReps;
            total.completedReps += set.completedReps;
        }
        return totals;
    }

    private String formatCompletedAt(String completedAt) {
        try {
            return LocalDateTime.parse(completedAt).format(DISPLAY_DATE);
        } catch (DateTimeParseException exception) {
            return completedAt;
        }
    }

    @FXML
    public void handleBack() {
        try {
            Pane page = FXMLLoader.load(getClass().getResource(WORKOUT_VIEW));
            StackPane pc = (StackPane) backButton.getScene().lookup("#pageContainer");
            if (pc != null) {
                pc.getChildren().setAll(page);
            }
        } catch (IOException e) {
            throw new RuntimeException("Failed to navigate back to workouts", e);
        }
    }

    private static class ExerciseTotals {
        int sets;
        int targetReps;
        int completedReps;
    }
}
