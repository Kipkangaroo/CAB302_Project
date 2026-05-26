package com.lockedin.lockedin.controller.workout;

import com.lockedin.lockedin.controller.auth.Authentication;
import com.lockedin.lockedin.controller.layout.LayoutController;
import com.lockedin.lockedin.controller.navigation.PageNavigator;
import com.lockedin.lockedin.model.dao.ExercisesDAO;
import com.lockedin.lockedin.model.dao.WorkoutRoutineDAO;
import com.lockedin.lockedin.model.entity.workout.Exercise;
import com.lockedin.lockedin.model.entity.workout.WorkoutExerciseEntry;
import com.lockedin.lockedin.model.session.CurrentUser;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.*;

import org.controlsfx.control.SearchableComboBox;

/**
 * JavaFX controller for the create workout screen.
 * @author LockedIn Team
 * @version 1.0
 */
public class CreateWorkoutController {

    private final Authentication authentication = new Authentication();
    private final ObservableList<WorkoutExerciseEntry> entries = FXCollections.observableArrayList();
    @FXML
    private Button backButton;
    @FXML
    private TextField workoutNameField;
    @FXML
    private TextField workoutNotesField;
    @FXML
    private SearchableComboBox<Exercise> exerciseCombo;
    @FXML
    private TextField setsField;
    @FXML
    private TextField repsField;
    @FXML
    private TextField restField;
    @FXML
    private ListView<WorkoutExerciseEntry> exerciseList;

    /**
     * Initializes the form: - Loads exercises from DB - Sets default values -
     * Configures the
     * exercise list cell factory
     */
    @FXML
    public void initialize() {
        exerciseList.setItems(entries);
        exerciseList.setCellFactory(lv -> new ExerciseEntryCell());
        exerciseList.setPlaceholder(new Label("No exercises added yet."));

        ExercisesDAO exercisesDAO = new ExercisesDAO();
        exerciseCombo.getItems().addAll(exercisesDAO.getAllExercises());

        setsField.setText("3");
        repsField.setText("10");
        restField.setText("60");
    }
        /**
     * Handle add exercise.
     */

    @FXML
    public void handleAddExercise() {
        Exercise selected = exerciseCombo.getValue();
        if (selected == null) {
            authentication.showError("Please select an exercise.");
            return;
        }
        try {
            int sets = Integer.parseInt(setsField.getText().trim());
            int reps = Integer.parseInt(repsField.getText().trim());
            int rest = Integer.parseInt(restField.getText().trim());
            if (sets <= 0 || reps <= 0 || rest < 0)
                throw new NumberFormatException();
            entries.add(
                    new WorkoutExerciseEntry(
                            0, selected.getId(), selected.getName(), sets, reps, rest));
        } catch (NumberFormatException e) {
            authentication.showError("Sets and reps must be positive numbers; rest \u2265 0.");
        }
    }
        /**
     * Handle save.
     */

    @FXML
    public void handleSave() {
        String name = workoutNameField.getText().trim();
        if (name.isEmpty()) {
            authentication.showError("Please enter a workout name.");
            return;
        }
        if (entries.isEmpty()) {
            authentication.showError("Please add at least one exercise.");
            return;
        }
        WorkoutRoutineDAO routineDAO = new WorkoutRoutineDAO();
        routineDAO.saveRoutine(
                CurrentUser.getId(), name, workoutNotesField.getText().trim(), entries);
        navigateBack();
    }
        /**
     * Handle back.
     */

    @FXML
    public void handleBack() {
        navigateBack();
    }

        /**
     * Navigate back.
     */
    private void navigateBack() {
        PageNavigator.loadPage(backButton, LayoutController.WORKOUT_VIEW);
    }

    // ── Custom cell ──────────────────────────────────────────────────────────

    /**
     * Provides exercise entry cell functionality for LockedIn.
     * @author LockedIn Team
     * @version 1.0
     */
    private class ExerciseEntryCell extends ListCell<WorkoutExerciseEntry> {
        private final HBox content;
        private final Label label;
        private final Button removeButton;

        ExerciseEntryCell() {
            label = new Label();
            label.setStyle("-fx-font-size: 13px;");
            HBox.setHgrow(label, Priority.ALWAYS);

            removeButton = new Button("\u2715");
            removeButton.getStyleClass().add("icon-btn");
            removeButton.setStyle("-fx-text-fill: #e53935;");

            content = new HBox(8, label, removeButton);
            content.setAlignment(Pos.CENTER_LEFT);
            content.setPadding(new Insets(4, 4, 4, 4));
        }
        /**
 * Update item.
 * @param item item
 * @param empty empty
 */

        @Override
        protected void updateItem(WorkoutExerciseEntry item, boolean empty) {
            super.updateItem(item, empty);
            if (empty || item == null) {
                setGraphic(null);
                return;
            }
            label.setText(item.toString());
            removeButton.setOnAction(e -> entries.remove(item));
            setGraphic(content);
        }
    }
}
