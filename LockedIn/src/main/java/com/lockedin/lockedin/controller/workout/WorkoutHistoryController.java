package com.lockedin.lockedin.controller.workout;

import com.lockedin.lockedin.model.dao.WorkoutRoutineDAO;
import com.lockedin.lockedin.model.session.CurrentUser;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.chart.*;
import javafx.scene.control.Button;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.layout.*;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * JavaFX controller for the workout history screen.
 * @author LockedIn Team
 * @version 1.0
 */
public class WorkoutHistoryController {
    private static final String WORKOUT_VIEW = "/com/lockedin/lockedin/pages/workout/workout-view.fxml";
    private static final DateTimeFormatter DISPLAY_DATE = DateTimeFormatter.ofPattern("dd MMM yyyy, h:mm a");
    private static final DateTimeFormatter CHART_DATE_FORMAT = DateTimeFormatter.ofPattern("d/M");
    private static final int REPS_CHART_DAYS = 7;

    @FXML
    private Button backButton;
    @FXML
    private Label historyCountLabel;
    @FXML
    private VBox chartSection;
    @FXML
    private LineChart<String, Number> repsChart;
    @FXML
    private VBox historyContainer;
    @FXML
    private DatePicker filterFromDate;
    @FXML
    private DatePicker filterToDate;

    private WorkoutRoutineDAO routineDAO;
    /**
     * Initializes FXML-bound UI components after the view loads.
     */

    @FXML
    public void initialize() {
        routineDAO = new WorkoutRoutineDAO();
        loadHistory();
    }

    /**
     * Performs load history.
     */
    private void loadHistory() {
        List<WorkoutRoutineDAO.CompletedWorkoutData> workouts = routineDAO
                .getCompletedWorkoutsByUser(CurrentUser.getId());

        if (workouts.isEmpty()) {
            chartSection.setVisible(false);
            chartSection.setManaged(false);
        } else {
            chartSection.setVisible(true);
            chartSection.setManaged(true);
            loadRepsChart(computeRepsPerDay(workouts));
        }

        showCards(workouts, "No completed workouts yet", "Complete a workout to see it here.");
    }

    /**
     * Rebuilds the history cards list with the given workouts.
     * @param workouts The workouts to display.
     * @param emptyCountText Text for historyCountLabel when list is empty.
     * @param emptyBodyText Text shown inside the list when empty.
     */
    private void showCards(List<WorkoutRoutineDAO.CompletedWorkoutData> workouts,
                           String emptyCountText, String emptyBodyText) {
        historyContainer.getChildren().clear();
        historyCountLabel.setText(
                workouts.isEmpty()
                        ? emptyCountText
                        : workouts.size()
                                + " completed workout"
                                + (workouts.size() == 1 ? "" : "s"));

        if (workouts.isEmpty()) {
            Label empty = new Label(emptyBodyText);
            empty.setStyle("-fx-text-fill: #757575; -fx-font-size: 13px;");
            empty.setWrapText(true);
            historyContainer.getChildren().add(empty);
            return;
        }

        for (WorkoutRoutineDAO.CompletedWorkoutData workout : workouts) {
            historyContainer.getChildren().add(buildHistoryCard(workout));
        }
    }

    /**
     * Filters the history list to workouts completed within the selected date range.
     */
    @FXML
    public void handleFilter() {
        LocalDate from = filterFromDate.getValue();
        LocalDate to = filterToDate.getValue();
        if (from == null || to == null) return;

        List<WorkoutRoutineDAO.CompletedWorkoutData> filtered =
                routineDAO.getCompletedWorkoutsByUserBetween(CurrentUser.getId(), from, to);

        showCards(filtered,
                "No workouts found for this period.",
                "No workouts found for this period.");
    }

    /**
     * Clears the date filter and reloads all workouts.
     */
    @FXML
    public void handleClearFilter() {
        filterFromDate.setValue(null);
        filterToDate.setValue(null);
        loadHistory();
    }

    /**
     * Performs build history card.
     * @param workout The workout.
     */
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
        totals.forEach(
                (exerciseName, total) -> exerciseSummary
                        .getChildren()
                        .add(buildExerciseSummaryRow(exerciseName, total)));

        VBox card = new VBox(12, topRow, exerciseSummary);
        card.getStyleClass().add("workout-card");
        return card;
    }

    /**
     * Performs build exercise summary row.
     * @param exerciseName The exercise name.
     * @param total The total.
     */
    private HBox buildExerciseSummaryRow(String exerciseName, ExerciseTotals total) {
        Label exerciseLabel = new Label(exerciseName);
        exerciseLabel.setStyle(
                "-fx-font-size: 13px; -fx-font-weight: bold; -fx-text-fill: #263241;");
        exerciseLabel.setWrapText(true);
        HBox.setHgrow(exerciseLabel, Priority.ALWAYS);

        Label detailLabel = new Label(
                total.sets
                        + " sets  -  "
                        + total.completedReps
                        + "/"
                        + total.targetReps
                        + " reps");
        detailLabel.setStyle("-fx-font-size: 12px; -fx-text-fill: #1565C0; -fx-font-weight: bold;");

        HBox row = new HBox(8, exerciseLabel, detailLabel);
        row.setAlignment(Pos.CENTER_LEFT);
        return row;
    }

    /**
     * Performs summarize exercises.
     * @param sets The sets.
     * @return The resulting text.
     */
    private Map<String, ExerciseTotals> summarizeExercises(
            List<WorkoutRoutineDAO.CompletedSetData> sets) {
        Map<String, ExerciseTotals> totals = new LinkedHashMap<>();
        for (WorkoutRoutineDAO.CompletedSetData set : sets) {
            ExerciseTotals total = totals.computeIfAbsent(set.exerciseName, ignored -> new ExerciseTotals());
            total.sets++;
            total.targetReps += set.targetReps;
            total.completedReps += set.completedReps;
        }
        return totals;
    }

    /**
     * Performs format completed at.
     * @param completedAt The completed at.
     * @return The resulting text.
     */
    private String formatCompletedAt(String completedAt) {
        try {
            return LocalDateTime.parse(completedAt).format(DISPLAY_DATE);
        } catch (DateTimeParseException exception) {
            return completedAt;
        }
    }

    private Map<LocalDate, Integer> computeRepsPerDay(List<WorkoutRoutineDAO.CompletedWorkoutData> workouts) {
        Map<LocalDate, Integer> repsPerDay = new LinkedHashMap<>();
        for (WorkoutRoutineDAO.CompletedWorkoutData workout : workouts) {
            LocalDate date = LocalDateTime.parse(workout.completedAt).toLocalDate();
            int reps = workout.sets.stream().mapToInt(s -> s.completedReps).sum();
            repsPerDay.merge(date, reps, Integer::sum);
        }
        return repsPerDay;
    }

    /**
     * Populates the reps line chart (oldest date left, today right).
     */
    private void loadRepsChart(Map<LocalDate, Integer> data) {
        repsChart.getData().clear();

        XYChart.Series<String, Number> series = new XYChart.Series<>();
        series.setName("Reps");

        LocalDate today = LocalDate.now();
        for (int daysAgo = REPS_CHART_DAYS - 1; daysAgo >= 0; daysAgo--) {
            LocalDate date = today.minusDays(daysAgo);
            String label = daysAgo == 0 ? "Today" : date.format(CHART_DATE_FORMAT);
            series.getData().add(new XYChart.Data<>(label, data.getOrDefault(date, 0)));
        }

        repsChart.getData().add(series);
    }
    /**
     * Performs handle back.
     */

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

    /**
     * Provides exercise totals functionality for LockedIn.
     * @author LockedIn Team
     * @version 1.0
     */
    private static class ExerciseTotals {
        int sets;
        int targetReps;
        int completedReps;
    }
}
