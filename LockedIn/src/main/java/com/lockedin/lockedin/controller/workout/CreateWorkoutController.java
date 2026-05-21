package com.lockedin.lockedin.controller.workout;

import com.lockedin.lockedin.model.dao.DBExercisesDAO;
import com.lockedin.lockedin.model.dao.WorkoutRoutineDAO;
import com.lockedin.lockedin.model.entity.workout.Exercise;
import com.lockedin.lockedin.model.entity.workout.WorkoutExerciseEntry;
import com.lockedin.lockedin.model.session.CurrentUser;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.StackPane;

import org.controlsfx.control.SearchableComboBox;

import java.io.IOException;

/**
 * JavaFX controller for the create workout screen.
 * @author LockedIn Team
 * @version 1.0
 */
public class CreateWorkoutController {

    private static final String WORKOUT_VIEW = "/com/lockedin/lockedin/pages/workout/workout-view.fxml";
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
    private WorkoutRoutineDAO routineDAO;

    /**
     * Initializes the form: - Loads exercises from DB - Sets default values -
     * Configures the
     * exercise list cell factory
     */
    @FXML
    public void initialize() {
        routineDAO = new WorkoutRoutineDAO();

        exerciseList.setItems(entries);
        exerciseList.setCellFactory(lv -> new ExerciseEntryCell());
        exerciseList.setPlaceholder(new Label("No exercises added yet."));

        DBExercisesDAO exercisesDAO = new DBExercisesDAO();
        exerciseCombo.getItems().addAll(exercisesDAO.getAllExercises());

        setsField.setText("3");
        repsField.setText("10");
        restField.setText("60");
    }
    /**
     * Performs handle add exercise.
     */

    @FXML
    public void handleAddExercise() {
        Exercise selected = exerciseCombo.getValue();
        if (selected == null) {
            showError("Please select an exercise.");
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
            showError("Sets and reps must be positive numbers; rest \u2265 0.");
        }
    }
    /**
     * Performs handle save.
     */

    @FXML
    public void handleSave() {
        String name = workoutNameField.getText().trim();
        if (name.isEmpty()) {
            showError("Please enter a workout name.");
            return;
        }
        if (entries.isEmpty()) {
            showError("Please add at least one exercise.");
            return;
        }
        routineDAO.saveRoutine(
                CurrentUser.getId(), name, workoutNotesField.getText().trim(), entries);
        navigateBack();
    }
    /**
     * Performs handle back.
     */

    @FXML
    public void handleBack() {
        navigateBack();
    }

    /**
     * Performs navigate back.
     */
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

    /**
     * Performs show error.
     * @param msg The msg.
     */
    private void showError(String msg) {
        Alert a = new Alert(Alert.AlertType.ERROR);
        a.setHeaderText(null);
        a.setContentText(msg);
        a.showAndWait();
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
        private final Button removeBtn;

        ExerciseEntryCell() {
            label = new Label();
            label.setStyle("-fx-font-size: 13px;");
            HBox.setHgrow(label, Priority.ALWAYS);

            removeBtn = new Button("\u2715");
            removeBtn.getStyleClass().add("icon-btn");
            removeBtn.setStyle("-fx-text-fill: #e53935;");

            content = new HBox(8, label, removeBtn);
            content.setAlignment(Pos.CENTER_LEFT);
            content.setPadding(new Insets(4, 4, 4, 4));
        }
        /**
         * Performs update item.
         * @param item The item.
         * @param empty The empty.
         */

        @Override
        protected void updateItem(WorkoutExerciseEntry item, boolean empty) {
            super.updateItem(item, empty);
            if (empty || item == null) {
                setGraphic(null);
                return;
            }
            label.setText(item.toString());
            removeBtn.setOnAction(e -> entries.remove(item));
            setGraphic(content);
        }
    }
}
