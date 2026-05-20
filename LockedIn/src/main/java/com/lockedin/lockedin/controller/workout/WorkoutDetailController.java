package com.lockedin.lockedin.controller.workout;

import com.lockedin.lockedin.model.dao.DBExercisesDAO;
import com.lockedin.lockedin.model.dao.WorkoutRoutineDAO;
import com.lockedin.lockedin.model.entity.workout.Exercise;
import com.lockedin.lockedin.model.entity.workout.WorkoutExerciseEntry;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.control.*;
import javafx.scene.layout.*;

import org.controlsfx.control.SearchableComboBox;

import java.io.IOException;
import java.util.List;

/**
 * Controller for the Workout Detail page. Displays all exercises in a selected
 * routine and allows
 * editing, deleting, and adding exercises, as well as starting the workout.
 */
public class WorkoutDetailController {

    private static final String WORKOUT_VIEW = "/com/lockedin/lockedin/pages/workout/workout-view.fxml";
    private static final String START_WORKOUT_VIEW = "/com/lockedin/lockedin/pages/workout/start-workout-view.fxml";

    private static int currentRoutineId = -1;
    @FXML
    private Button backButton;
    @FXML
    private Label workoutNameLabel;
    @FXML
    private Label workoutNotesLabel;
    @FXML
    private VBox exercisesContainer;
    private WorkoutRoutineDAO routineDAO;
    private DBExercisesDAO exercisesDAO;
    private WorkoutRoutineDAO.RoutineData currentRoutine;

    public static void setCurrentRoutineId(int id) {
        currentRoutineId = id;
    }

    @FXML
    public void initialize() {
        routineDAO = new WorkoutRoutineDAO();
        exercisesDAO = new DBExercisesDAO();
        loadRoutine();
    }

    private void loadRoutine() {
        currentRoutine = routineDAO.getRoutineById(currentRoutineId);
        if (currentRoutine == null) {
            navigateBack();
            return;
        }

        workoutNameLabel.setText(currentRoutine.name);
        workoutNotesLabel.setText(currentRoutine.notes);
        workoutNotesLabel.setVisible(!currentRoutine.notes.isEmpty());
        workoutNotesLabel.setManaged(!currentRoutine.notes.isEmpty());

        buildExerciseCards(currentRoutine.exercises);
    }

    private void buildExerciseCards(List<WorkoutExerciseEntry> exercises) {
        exercisesContainer.getChildren().clear();

        if (exercises.isEmpty()) {
            Label empty = new Label("No exercises yet – tap '+ Add Exercise' below.");
            empty.setStyle("-fx-text-fill: #999; -fx-font-size: 13px;");
            exercisesContainer.getChildren().add(empty);
            return;
        }

        for (int i = 0; i < exercises.size(); i++) {
            exercisesContainer.getChildren().add(buildExerciseCard(exercises.get(i), i + 1));
        }
    }

    private HBox buildExerciseCard(WorkoutExerciseEntry entry, int index) {
        // Numbered circle
        Label numCircle = new Label(String.valueOf(index));
        numCircle.getStyleClass().add("number-circle");

        // Exercise name + detail line
        Label nameLabel = new Label(entry.getExerciseName());
        nameLabel.setStyle("-fx-font-weight: bold; -fx-font-size: 15px; -fx-text-fill: #1a1a1a;");
        nameLabel.setWrapText(true);

        Label detailLabel = new Label(
                "\u2299  "
                        + entry.getSets()
                        + " \u00D7 "
                        + entry.getReps()
                        + "    \u23F1  "
                        + entry.getRestSeconds()
                        + "s");
        detailLabel.setStyle("-fx-text-fill: #1565C0; -fx-font-size: 12px;");

        VBox textCol = new VBox(4, nameLabel, detailLabel);
        HBox.setHgrow(textCol, Priority.ALWAYS);

        // Edit button
        Button editBtn = new Button("\u270F");
        editBtn.getStyleClass().add("icon-btn");
        editBtn.setStyle("-fx-text-fill: #757575;");
        editBtn.setOnAction(e -> showEditDialog(entry));

        // Delete button
        Button delBtn = new Button("\u2715");
        delBtn.getStyleClass().add("icon-btn");
        delBtn.setStyle("-fx-text-fill: #e53935;");
        delBtn.setOnAction(
                e -> {
                    routineDAO.removeExerciseFromRoutine(entry.getId());
                    loadRoutine();
                });

        HBox card = new HBox(12, numCircle, textCol, editBtn, delBtn);
        card.getStyleClass().add("workout-card");
        card.setAlignment(Pos.CENTER_LEFT);
        card.setCursor(Cursor.DEFAULT);
        return card;
    }

    // ── Edit exercise dialog ─────────────────────────────────────────────────

    private void showEditDialog(WorkoutExerciseEntry entry) {
        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.setTitle("Edit Exercise");
        dialog.setHeaderText(entry.getExerciseName());
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

        TextField setsField = new TextField(String.valueOf(entry.getSets()));
        TextField repsField = new TextField(String.valueOf(entry.getReps()));
        TextField restField = new TextField(String.valueOf(entry.getRestSeconds()));

        GridPane grid = new GridPane();
        grid.setHgap(12);
        grid.setVgap(10);
        grid.setPadding(new Insets(14));
        grid.addRow(0, new Label("Sets:"), setsField);
        grid.addRow(1, new Label("Reps:"), repsField);
        grid.addRow(2, new Label("Rest (s):"), restField);

        dialog.getDialogPane().setContent(grid);

        dialog.showAndWait()
                .ifPresent(
                        bt -> {
                            if (bt != ButtonType.OK)
                                return;
                            try {
                                int sets = Integer.parseInt(setsField.getText().trim());
                                int reps = Integer.parseInt(repsField.getText().trim());
                                int rest = Integer.parseInt(restField.getText().trim());
                                if (sets > 0 && reps > 0 && rest >= 0) {
                                    routineDAO.updateExercise(entry.getId(), sets, reps, rest);
                                    loadRoutine();
                                }
                            } catch (NumberFormatException ignored) {
                            }
                        });
    }

    // ── Add exercise dialog ──────────────────────────────────────────────────

    @FXML
    public void handleAddExercise() {
        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.setTitle("Add Exercise");
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

        SearchableComboBox<Exercise> combo = new SearchableComboBox<>();
        combo.getItems().addAll(exercisesDAO.getAllExercises());
        combo.setPromptText("Search exercises...");

        TextField setsField = new TextField("3");
        TextField repsField = new TextField("10");
        TextField restField = new TextField("60");

        GridPane grid = new GridPane();
        grid.setHgap(12);
        grid.setVgap(10);
        grid.setPadding(new Insets(14));
        grid.add(new Label("Exercise:"), 0, 0);
        grid.add(combo, 1, 0);
        GridPane.setHgrow(combo, Priority.ALWAYS);
        combo.setMinWidth(280);
        grid.addRow(1, new Label("Sets:"), setsField);
        grid.addRow(2, new Label("Reps:"), repsField);
        grid.addRow(3, new Label("Rest (s):"), restField);
        dialog.getDialogPane().setContent(grid);
        dialog.getDialogPane().setMinWidth(380);

        dialog.showAndWait()
                .ifPresent(
                        bt -> {
                            if (bt != ButtonType.OK)
                                return;
                            Exercise sel = combo.getValue();
                            if (sel == null)
                                return;
                            try {
                                int sets = Integer.parseInt(setsField.getText().trim());
                                int reps = Integer.parseInt(repsField.getText().trim());
                                int rest = Integer.parseInt(restField.getText().trim());
                                if (sets > 0 && reps > 0 && rest >= 0) {
                                    routineDAO.addExerciseToRoutine(
                                            currentRoutineId,
                                            sel.getId(),
                                            sel.getName(),
                                            sets,
                                            reps,
                                            rest);
                                    loadRoutine();
                                }
                            } catch (NumberFormatException ignored) {
                            }
                        });
    }

    // ── Edit workout name/notes dialog ───────────────────────────────────────

    @FXML
    public void handleEditWorkoutName() {
        Alert a = new Alert(Alert.AlertType.INFORMATION);
        a.setHeaderText("Coming Soon");
        a.setContentText("Editing the workout name will be available in a future update.");
        a.showAndWait();
    }

    @FXML
    public void handleStartWorkout() {
        if (currentRoutine == null || currentRoutine.exercises.isEmpty()) {
            Alert a = new Alert(Alert.AlertType.ERROR);
            a.setHeaderText(null);
            a.setContentText("Add at least one exercise before starting this workout.");
            a.showAndWait();
            return;
        }

        try {
            StartWorkoutController.setCurrentRoutineId(currentRoutineId);
            Pane page = FXMLLoader.load(getClass().getResource(START_WORKOUT_VIEW));
            StackPane pc = (StackPane) backButton.getScene().lookup("#pageContainer");
            if (pc != null)
                pc.getChildren().setAll(page);
        } catch (IOException e) {
            throw new RuntimeException("Failed to start workout", e);
        }
    }

    @FXML
    public void handleBack() {
        navigateBack();
    }

    private void navigateBack() {
        try {
            Pane page = FXMLLoader.load(getClass().getResource(WORKOUT_VIEW));
            StackPane pc = (StackPane) backButton.getScene().lookup("#pageContainer");
            if (pc != null)
                pc.getChildren().setAll(page);
        } catch (IOException e) {
            throw new RuntimeException("Failed to navigate back", e);
        }
    }
}
