package com.lockedin.lockedin.controller.workout;

import com.lockedin.lockedin.model.dao.WorkoutRoutineDAO;
import com.lockedin.lockedin.model.session.CurrentUser;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.layout.*;

import java.io.IOException;
import java.util.List;

/**
 * Controller for the main Workout page. Displays all workout routines created
 * by the user, and
 * provides navigation to create, view, or delete routines.
 */
public class WorkoutController {

    private static final String CREATE_VIEW = "/com/lockedin/lockedin/pages/workout/create-workout-view.fxml";
    private static final String DETAIL_VIEW = "/com/lockedin/lockedin/pages/workout/workout-detail-view.fxml";
    private static final String HISTORY_VIEW = "/com/lockedin/lockedin/pages/workout/workout-history-view.fxml";
    private static final String AI_WORKOUT_VIEW = "/com/lockedin/lockedin/pages/workout/ai-workout-view.fxml";

    @FXML
    private Label workoutCountLabel;
    @FXML
    private VBox workoutsContainer;

    private WorkoutRoutineDAO routineDAO;

    @FXML
    public void initialize() {
        routineDAO = new WorkoutRoutineDAO();
        loadWorkouts();
    }

    private void loadWorkouts() {
        List<WorkoutRoutineDAO.RoutineData> routines = routineDAO.getRoutinesByUser(CurrentUser.getId());

        int n = routines.size();
        workoutCountLabel.setText(
                n == 0 ? "No workouts yet" : n == 1 ? "1 workout ready" : n + " workouts ready");

        workoutsContainer.getChildren().clear();

        if (routines.isEmpty()) {
            Label empty = new Label("Tap + or 'Create Workout' to add your first workout.");
            empty.setStyle("-fx-text-fill: #999; -fx-font-size: 13px;");
            empty.setWrapText(true);
            workoutsContainer.getChildren().add(empty);
        } else {
            for (WorkoutRoutineDAO.RoutineData r : routines) {
                workoutsContainer.getChildren().add(buildWorkoutCard(r));
            }
        }
    }

    /**
     * Builds a UI card representing a single workout routine. Includes name, notes,
     * exercise count,
     * and edit/delete actions.
     */
    private VBox buildWorkoutCard(WorkoutRoutineDAO.RoutineData routine) {
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
        Button editBtn = new Button("\u270F");
        Button deleteBtn = new Button("\uD83D\uDDD1");
        editBtn.getStyleClass().add("icon-btn");
        deleteBtn.getStyleClass().add("icon-btn");
        deleteBtn.setStyle("-fx-text-fill: #e53935; -fx-font-size: 14px;");

        HBox topRow = new HBox(4, textCol, editBtn, deleteBtn);
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
        editBtn.setOnAction(
                e -> {
                    e.consume();
                    openDetail(routine.id);
                });
        deleteBtn.setOnAction(
                e -> {
                    e.consume();
                    Alert confirm = new Alert(
                            Alert.AlertType.CONFIRMATION,
                            "Delete \"" + routine.name + "\"?",
                            ButtonType.YES,
                            ButtonType.NO);
                    confirm.setHeaderText(null);
                    confirm.showAndWait()
                            .ifPresent(
                                    bt -> {
                                        if (bt == ButtonType.YES) {
                                            routineDAO.deleteRoutine(routine.id);
                                            loadWorkouts();
                                        }
                                    });
                });

        return card;
    }

    private void openDetail(int routineId) {
        try {
            WorkoutDetailController.setCurrentRoutineId(routineId);
            Pane page = FXMLLoader.load(getClass().getResource(DETAIL_VIEW));
            stackPane().getChildren().setAll(page);
        } catch (IOException e) {
            throw new RuntimeException("Failed to open workout detail", e);
        }
    }

    @FXML
    public void handleCreateWorkout() {
        try {
            Pane page = FXMLLoader.load(getClass().getResource(CREATE_VIEW));
            stackPane().getChildren().setAll(page);
        } catch (IOException e) {
            throw new RuntimeException("Failed to open create workout page", e);
        }
    }

    @FXML
    public void handleHistory() {
        try {
            Pane page = FXMLLoader.load(getClass().getResource(HISTORY_VIEW));
            stackPane().getChildren().setAll(page);
        } catch (IOException e) {
            throw new RuntimeException("Failed to open workout history", e);
        }
    }

    @FXML
    public void handleAiGenerator() {
        try {
            Pane page = FXMLLoader.load(getClass().getResource(AI_WORKOUT_VIEW));
            stackPane().getChildren().setAll(page);
        } catch (IOException e) {
            throw new RuntimeException("Failed to open AI workout generator", e);
        }
    }

    private StackPane stackPane() {
        return (StackPane) workoutsContainer.getScene().lookup("#pageContainer");
    }
}
