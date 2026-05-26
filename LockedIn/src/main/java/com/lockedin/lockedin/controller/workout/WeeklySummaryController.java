package com.lockedin.lockedin.controller.workout;

import com.lockedin.lockedin.controller.layout.LayoutController;
import com.lockedin.lockedin.controller.navigation.PageNavigator;
import com.lockedin.lockedin.model.dao.WorkoutRoutineDAO;
import com.lockedin.lockedin.model.session.CurrentUser;

import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * JavaFX controller for the weekly summary screen.
 * @author LockedIn Team
 * @version 1.0
 */
public class WeeklySummaryController {

    @FXML private Button backButton;
    @FXML private Label weekRangeLabel;
    @FXML private Label sessionsValue;
    @FXML private Label setsValue;
    @FXML private Label activeDaysValue;
    @FXML private VBox workoutsContainer;

    private WorkoutRoutineDAO routineDAO;
    private int weekOffset = 0;

    /**
     * Initializes FXML-bound UI components after the view loads.
     */
    @FXML
    public void initialize() {
        routineDAO = new WorkoutRoutineDAO();
        loadWeek();
    }

    /**
     * Navigates to the previous week.
     */
    @FXML
    public void handlePrevWeek() {
        weekOffset--;
        loadWeek();
    }

    /**
     * Navigates to the next week (capped at the current week).
     */
    @FXML
    public void handleNextWeek() {
        if (weekOffset < 0) {
            weekOffset++;
            loadWeek();
        }
    }

    /**
     * Loads completed workouts and summary stats for the week indicated by {@code weekOffset}.
     */
    private void loadWeek() {
        final DateTimeFormatter rangeFmt = DateTimeFormatter.ofPattern("d MMM");
        final DateTimeFormatter rangeFmtYear = DateTimeFormatter.ofPattern("d MMM yyyy");
        LocalDate monday = LocalDate.now().with(DayOfWeek.MONDAY).plusWeeks(weekOffset);
        LocalDate sunday = monday.plusDays(6);

        weekRangeLabel.setText(monday.format(rangeFmt) + " – " + sunday.format(rangeFmtYear));

        List<WorkoutRoutineDAO.CompletedWorkoutData> workouts =
                routineDAO.getCompletedWorkoutsByUserBetween(CurrentUser.getId(), monday, sunday);

        int sessions = workouts.size();
        int totalSets = workouts.stream().mapToInt(w -> w.totalSets).sum();
        Set<String> activeDates = workouts.stream()
                .map(w -> parseDate(w.completedAt))
                .collect(Collectors.toSet());

        sessionsValue.setText(String.valueOf(sessions));
        setsValue.setText(String.valueOf(totalSets));
        activeDaysValue.setText(String.valueOf(activeDates.size()));

        workoutsContainer.getChildren().clear();
        if (workouts.isEmpty()) {
            Label empty = new Label("No workouts logged this week.");
            empty.setStyle("-fx-text-fill: #757575; -fx-font-size: 13px;");
            workoutsContainer.getChildren().add(empty);
        } else {
            for (WorkoutRoutineDAO.CompletedWorkoutData completedWorkout : workouts) {
                workoutsContainer.getChildren().add(buildCard(completedWorkout));
            }
        }
    }

    /**
     * Extracts the local date portion from a completed-at timestamp string.
     *
     * @param completedAt ISO or legacy timestamp text
     * @return date as yyyy-MM-dd when parseable
     */
    private String parseDate(String completedAt) {
        try {
            return LocalDateTime.parse(completedAt).toLocalDate().toString();
        } catch (DateTimeParseException e) {
            return completedAt.length() >= 10 ? completedAt.substring(0, 10) : completedAt;
        }
    }

    /**
     * Builds a summary card UI node for one completed workout.
     *
     * @param workout completed workout data
     * @return styled card container
     */
    private VBox buildCard(WorkoutRoutineDAO.CompletedWorkoutData workout) {
        final DateTimeFormatter cardFmt = DateTimeFormatter.ofPattern("EEE d MMM, h:mm a");
        Label nameLabel = new Label(workout.routineName);
        nameLabel.setStyle(
                "-fx-font-size: 17px; -fx-font-weight: bold; -fx-text-fill: #061424;");
        nameLabel.setWrapText(true);

        String formattedDate;
        try {
            formattedDate = LocalDateTime.parse(workout.completedAt).format(cardFmt);
        } catch (DateTimeParseException e) {
            formattedDate = workout.completedAt;
        }

        Label dateLabel = new Label(formattedDate);
        dateLabel.setStyle("-fx-font-size: 12px; -fx-text-fill: #65758a;");

        VBox titleCol = new VBox(3, nameLabel, dateLabel);
        HBox.setHgrow(titleCol, Priority.ALWAYS);

        Label badge = new Label(
                workout.totalExercises + " exercises  –  " + workout.totalSets + " sets");
        badge.getStyleClass().add("exercise-badge");

        HBox topRow = new HBox(8, titleCol, badge);
        topRow.setAlignment(Pos.TOP_LEFT);

        VBox card = new VBox(10, topRow);
        card.getStyleClass().add("workout-card");
        return card;
    }

    /**
     * Navigates back to the main workout list.
     */
    @FXML
    public void handleBack() {
        PageNavigator.loadPage(backButton, LayoutController.WORKOUT_VIEW);
    }
}
