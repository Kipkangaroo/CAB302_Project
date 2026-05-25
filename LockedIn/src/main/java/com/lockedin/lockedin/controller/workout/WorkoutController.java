package com.lockedin.lockedin.controller.workout;

import com.lockedin.lockedin.controller.auth.Authentication;
import com.lockedin.lockedin.controller.navigation.PageNavigator;
import com.lockedin.lockedin.model.dao.WorkoutRoutineDAO;
import com.lockedin.lockedin.model.session.CurrentUser;

import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.control.*;
import javafx.scene.layout.*;

import java.util.List;

/**
 * JavaFX controller for the workout screen.
 * @author LockedIn Team
 * @version 1.0
 */
public class WorkoutController {

    private static final String AI_WORKOUT_VIEW =
            "/com/lockedin/lockedin/pages/workout/ai-workout-view.fxml";

    @FXML
    private Label workoutCountLabel;
    @FXML
    private VBox workoutsContainer;

    private WorkoutRoutineDAO routineDAO;
    /**
     * Initializes FXML-bound UI components after the view loads.
     */

    @FXML
    public void initialize() {
        routineDAO = new WorkoutRoutineDAO();
        loadWorkouts();
    }

    /**
     * Performs load workouts.
     */
    private void loadWorkouts() {
        List<WorkoutRoutineDAO.RoutineData> routines = routineDAO.getRoutinesByUser(CurrentUser.getId());

        int routineCount = routines.size();
        workoutCountLabel.setText(
                routineCount == 0
                        ? "No workouts yet"
                        : routineCount == 1 ? "1 workout ready" : routineCount + " workouts ready");

        workoutsContainer.getChildren().clear();

        if (routines.isEmpty()) {
            Label empty = new Label("Tap + or 'Create Workout' to add your first workout.");
            empty.setStyle("-fx-text-fill: #999; -fx-font-size: 13px;");
            empty.setWrapText(true);
            workoutsContainer.getChildren().add(empty);
        } else {
            for (WorkoutRoutineDAO.RoutineData routine : routines) {
                workoutsContainer.getChildren().add(buildWorkoutCard(routine));
            }
        }
    }

    /**
     * Builds a UI card representing a single workout routine. Includes name, notes,
     * exercise count,
     * and edit/delete actions.
     */
    private VBox buildWorkoutCard(WorkoutRoutineDAO.RoutineData routine) {
        Authentication authentication = new Authentication();
        // ── Name + subtitle ──
        Label nameLabel = new Label(routine.name);
        nameLabel.setStyle("-fx-font-weight: bold; -fx-font-size: 16px; -fx-text-fill: #1a1a1a;");
        nameLabel.setWrapText(true);

        Label notesLabel = new Label(routine.notes.isEmpty() ? "" : routine.notes);
        notesLabel.setStyle("-fx-text-fill: #757575; -fx-font-size: 13px;");
        notesLabel.setWrapText(true);
        notesLabel.setVisible(!routine.notes.isEmpty());
        notesLabel.setManaged(!routine.notes.isEmpty());

        VBox textCol = new VBox(3, nameLabel, notesLabel);
        HBox.setHgrow(textCol, Priority.ALWAYS);

        // ── Edit / Delete icon buttons ──
        Button editButton = new Button("\u270F");
        Button deleteButton = new Button("\uD83D\uDDD1");
        editButton.getStyleClass().add("icon-btn");
        deleteButton.getStyleClass().add("icon-btn");
        deleteButton.setStyle("-fx-text-fill: #e53935; -fx-font-size: 14px;");

        HBox topRow = new HBox(4, textCol, editButton, deleteButton);
        topRow.setAlignment(Pos.TOP_LEFT);

        // ── Exercise count badge ──
        int count = routine.exercises.size();
        Label badge = new Label("\u21BA  " + count + " exercise" + (count != 1 ? "s" : ""));
        badge.getStyleClass().add("exercise-badge");

        VBox card = new VBox(8, topRow, badge);
        card.getStyleClass().add("workout-card");
        card.setCursor(Cursor.HAND);
        VBox.setMargin(card, new Insets(0));

        // ── Click actions ──
        card.setOnMouseClicked(e -> openDetail(routine.id));
        editButton.setOnAction(
                e -> {
                    e.consume();
                    openDetail(routine.id);
                });
        deleteButton.setOnAction(
                e -> {
                    e.consume();
                    if (authentication.confirmYesNo("Delete \"" + routine.name + "\"?")) {
                        routineDAO.deleteRoutine(routine.id);
                        loadWorkouts();
                    }
                });

        return card;
    }

    /**
     * Performs open detail.
     * @param routineId The routine id.
     */
    private void openDetail(int routineId) {
        final String detailView = "/com/lockedin/lockedin/pages/workout/workout-detail-view.fxml";
        WorkoutDetailController.setCurrentRoutineId(routineId);
        PageNavigator.loadPage(workoutsContainer, detailView);
    }
    /**
     * Performs handle create workout.
     */

    @FXML
    public void handleCreateWorkout() {
        final String createView = "/com/lockedin/lockedin/pages/workout/create-workout-view.fxml";
        PageNavigator.loadPage(workoutsContainer, createView);
    }
    /**
     * Performs handle history.
     */

    @FXML
    public void handleHistory() {
        final String historyView = "/com/lockedin/lockedin/pages/workout/workout-history-view.fxml";
        PageNavigator.loadPage(workoutsContainer, historyView);
    }
    /**
     * Performs handle weekly summary.
     */

    @FXML
    public void handleWeeklySummary() {
        final String weeklySummaryView = "/com/lockedin/lockedin/pages/workout/weekly-summary-view.fxml";
        PageNavigator.loadPage(workoutsContainer, weeklySummaryView);
    }

    /**
     * Opens the AI workout generator screen.
     */

    @FXML
    public void handleAIGenerator() {
        PageNavigator.loadPage(workoutsContainer, AI_WORKOUT_VIEW);
    }
}
