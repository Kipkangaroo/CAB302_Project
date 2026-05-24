package com.lockedin.lockedin.controller.profile;

import com.lockedin.lockedin.controller.auth.Authentication;
import com.lockedin.lockedin.controller.auth.SignUpController;
import com.lockedin.lockedin.model.dao.*;
import com.lockedin.lockedin.model.entity.user.FitnessGoal;
import com.lockedin.lockedin.model.entity.user.Measurement;
import com.lockedin.lockedin.model.entity.user.User;
import com.lockedin.lockedin.model.entity.user.UserProgress;
import com.lockedin.lockedin.model.session.CurrentUser;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Circle;

import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Map;
import java.util.Objects;

/**
 * JavaFX controller for the profile screen.
 * @author LockedIn Team
 * @version 1.0
 */
public class ProfileController {
    private static final String BLUE_FILL = "#028ee1";
    private static final String WHITE_FILL = "#FFFFFF";
    private static final String LOGIN_VIEW = "/com/lockedin/lockedin/pages/auth/login-view.fxml";
    private static final String PROGRESS_VIEW = "/com/lockedin/lockedin/pages/profile/progress-view.fxml";
    private static final String EDIT_ICON = "/com/lockedin/lockedin/graphics/icons/edit-icon.png";
    private static final String SAVE_ICON = "/com/lockedin/lockedin/graphics/icons/save-icon.png";
    private static final double ICON_SIZE = 46;
    private static final DateTimeFormatter DAY_LABEL_FORMAT = DateTimeFormatter.ofPattern("dd/MM");
    private static final Paint COMPLETED_FILL = Paint.valueOf(BLUE_FILL);
    private static final Paint MISSED_FILL = Paint.valueOf(WHITE_FILL);
    private final Authentication authentication = new Authentication();
    private final UserDAO userDAO = new UserDAO();
    private final FoodDAO foodDAO = new FoodDAO();
    private final WorkoutRoutineDAO workoutDAO = new WorkoutRoutineDAO();
    private final UserProgressDAO progressDAO = new UserProgressDAO();
    private final MeasurementDAO measurementDAO = new MeasurementDAO();
    public ImageView imageView;
    private String selectedRange = "ALL"; // ALL, 7, 30
    private User user;
    private boolean editingDetails;
    private static final Map<String, FitnessGoal> GOAL_MAP = Map.of(
            "Lose Weight", FitnessGoal.LOSE_WEIGHT,
            "Build Muscle", FitnessGoal.BUILD_MUSCLE,
            "Maintain Fitness", FitnessGoal.MAINTAIN_FITNESS
    );
    private static final Map<FitnessGoal, String> GOAL_LABELS = Map.of(
            FitnessGoal.LOSE_WEIGHT, "Lose Weight",
            FitnessGoal.BUILD_MUSCLE, "Build Muscle",
            FitnessGoal.MAINTAIN_FITNESS, "Maintain Fitness"
    );

    @FXML
    private Button logoutBtn;
    @FXML
    private Label ageLabel;
    @FXML
    private Label heightLabel;
    @FXML
    private TextField weightField;
    @FXML
    private Label fitnessGoalLabel;
    @FXML
    private ComboBox<String> fitnessGoalCombo;
    @FXML
    private Label firstNameLabel;
    @FXML
    private HBox calorieStreakRow;
    @FXML
    private HBox workoutStreakRow;
    @FXML
    private ImageView editActionIcon;
    private Image editImage;
    private Image saveImage;
    @FXML
    private ComboBox<String> measurementTypeCombo;
    @FXML
    private TextField newMeasurementField;
    @FXML
    private VBox measurementHistoryBox;
    @FXML
    private LineChart<String, Number> measurementTrendChart;
    @FXML
    private void handleRange7Days() {
        selectedRange = "7";
        loadMeasurementTrend();
    }
    @FXML
    private void handleRange30Days() {
        selectedRange = "30";
        loadMeasurementTrend();
    }
    @FXML
    private void handleRangeAllTime() {
        selectedRange = "ALL";
        loadMeasurementTrend();
    }

    /**
     * Navigates to the goals progress screen.
     */
    @FXML
    private void handleGoalsProgress() {
        try {
            Pane page = FXMLLoader.load(Objects.requireNonNull(getClass().getResource(PROGRESS_VIEW)));
            StackPane pc = (StackPane) logoutBtn.getScene().lookup("#pageContainer");
            if (pc != null) {
                pc.getChildren().setAll(page);
            }
        } catch (IOException e) {
            throw new RuntimeException("Failed to open goals progress", e);
        }
    }

    /**
     * Performs handle logout.
     *
     * @throws IOException If the operation fails.
     */

    @FXML
    private void handleLogout() throws IOException {
        CurrentUser.clear();
        authentication.switchScene(logoutBtn, LOGIN_VIEW);
    }

    /**
     * Performs handle edit details.
     */

    @FXML
    private void handleEditDetails() {
        if (editingDetails) {
            exitEditMode();
        } else {
            enterEditMode();
        }
    }

    /**
     * Performs enter edit mode.
     */
    private void enterEditMode() {
        editingDetails = true;
        updateEditIcon();
        double weight = user.getWeight();
        weightField.setText(weight == (long) weight ? String.valueOf((long) weight) : String.valueOf(weight));
        setFieldEditing(weightField, true);
        setFitnessGoalEditing(true);
        weightField.requestFocus();
        weightField.selectAll();
    }

    /**
     * Performs exit edit mode.
     */
    private void exitEditMode() {
        Double weight = SignUpController.parseValidDouble(weightField.getText());
        if (weight == null) {
            authentication.showError(
                    "Invalid weight", "Weight cannot be empty and must be greater than 0.");
            return;
        }
        String selectedLabel = fitnessGoalCombo.getValue();
        FitnessGoal newGoal = GOAL_MAP.get(selectedLabel);
        user.setFitnessGoal(newGoal);
        userDAO.updateFitnessGoal(user.getId(), newGoal);
        editingDetails = false;
        updateEditIcon();
        user.setWeight(weight);
        userDAO.updateWeight(user.getId(), weight);
        progressDAO.addUserProgress(new UserProgress(0, user.getId(), user.getFitnessGoal(), weight,
                user.getTargetCalories(), LocalDate.now()));
        setFieldEditing(weightField, false);
        refreshDetailFields();
        setFitnessGoalEditing(false);
    }

    /**
     * Performs refresh detail fields.
     */
    private void refreshDetailFields() {
        weightField.setText("Weight: " + user.getWeight() + " kg");
        String goalLabel = GOAL_LABELS.get(user.getFitnessGoal());
        fitnessGoalLabel.setText("Fitness Goal: " + goalLabel);
        fitnessGoalCombo.setValue(goalLabel);
    }

    /**
     * Toggles fitness goal between read-only label and editable combobox.
     *
     * @param editing true when the personal-info card is in edit mode
     */
    private void setFitnessGoalEditing(boolean editing) {
        fitnessGoalLabel.setVisible(!editing);
        fitnessGoalLabel.setManaged(!editing);
        fitnessGoalCombo.setVisible(editing);
        fitnessGoalCombo.setManaged(editing);
        fitnessGoalCombo.setDisable(!editing);
    }

    /**
     * Performs update edit icon.
     */
    private void updateEditIcon() {
        editActionIcon.setFitWidth(ICON_SIZE);
        editActionIcon.setFitHeight(ICON_SIZE);
        Image icon = editingDetails ? saveImage : editImage;
        if (icon != null) {
            editActionIcon.setImage(icon);
        }
    }

    /**
     * Sets the field editing.
     *
     * @param field   The field.
     * @param editing The editing.
     */
    private void setFieldEditing(TextField field, boolean editing) {
        field.setEditable(editing);
        field.setFocusTraversable(editing);
        if (editing) {
            if (!field.getStyleClass().contains("profile-detail-field-editing")) {
                field.getStyleClass().add("profile-detail-field-editing");
            }
        } else {
            field.getStyleClass().remove("profile-detail-field-editing");
        }
    }

    /**
     * Initializes FXML-bound UI components after the view loads.
     */

    @FXML
    private void initialize() {
        var editIconUrl = getClass().getResource(EDIT_ICON);
        var saveIconUrl = getClass().getResource(SAVE_ICON);
        if (editIconUrl != null) {
            editImage = new Image(editIconUrl.toExternalForm());
        }
        if (saveIconUrl != null) {
            saveImage = new Image(saveIconUrl.toExternalForm());
        }
        user = CurrentUser.get();
        ageLabel.setText("Age: " + user.getAge());
        heightLabel.setText("Height: " + user.getHeight() + " cm");
        refreshDetailFields();
        firstNameLabel.setText("Hello " + user.getFirstName() + "!");
        updateEditIcon();
        updateTrackingStreaks();
        measurementTypeCombo.getSelectionModel().select("Weight");
        measurementTypeCombo.valueProperty().addListener((obs, oldVal, newVal) -> {
            loadMeasurements();
            loadMeasurementTrend();
        });

        loadMeasurements();
        loadMeasurementTrend();
        fitnessGoalCombo.getItems().setAll(GOAL_MAP.keySet());
        fitnessGoalCombo.setValue(GOAL_LABELS.get(user.getFitnessGoal()));
        setFitnessGoalEditing(false);
    }

    /**
     * Performs update tracking streaks.
     */
    private void updateTrackingStreaks() {
        updateStreak(calorieStreakRow, foodDAO.getWeeklyCalorieTracking(user.getId()));
        updateStreak(workoutStreakRow, workoutDAO.getWeeklyWorkoutTracking(user.getId()));
    }

    /**
     * Performs update streak.
     *
     * @param row       The row.
     * @param completed The completed.
     */
    private void updateStreak(HBox row, boolean[] completed) {
        LocalDate today = LocalDate.now();
        for (int j = 0; j < row.getChildren().size(); j++) {
            VBox day = (VBox) row.getChildren().get(j);
            Circle circle = (Circle) day.getChildren().get(0);
            Label label = (Label) day.getChildren().get(1);

            int daysAgo = 6 - j;
            label.setText(daysAgo == 0 ? "Today" : today.minusDays(daysAgo).format(DAY_LABEL_FORMAT));
            circle.setFill(completed[daysAgo] ? COMPLETED_FILL : MISSED_FILL);
        }
    }
    /**
     * Loads and displays the measurement history for the selected type.
     */
    private void loadMeasurements() {
        measurementHistoryBox.getChildren().clear();

        String type = measurementTypeCombo.getValue();
        var list = measurementDAO.getMeasurements(type);

        for (Measurement m : list) {
            HBox row = new HBox(10);

            Label entry = new Label(m.getDate() + " — " + m.getValue() + " (" + m.getType() + ")");
            entry.getStyleClass().add("measurement-entry");

            Button deleteBtn = new Button("X");
            deleteBtn.getStyleClass().add("delete-measurement-btn");

            deleteBtn.setOnAction(e -> {
                Alert alert = new Alert(Alert.AlertType.CONFIRMATION,
                        "Delete this measurement?");
                alert.setHeaderText("Confirm Deletion");

                var result = alert.showAndWait();
                if (result.isPresent() && result.get() == ButtonType.OK) {
                    measurementDAO.deleteMeasurement(m.getId());
                    loadMeasurements();
                    loadMeasurementTrend();
                }
            });

            row.getChildren().addAll(entry, deleteBtn);
            measurementHistoryBox.getChildren().add(row);
        }
    }
    /**
     * Updates the line chart to show the trend for the selected measurement type and time range.
     */
    private void loadMeasurementTrend() {
        measurementTrendChart.getData().clear();

        String type = measurementTypeCombo.getValue();
        var list = measurementDAO.getMeasurements(type);

        LocalDate now = LocalDate.now();

        XYChart.Series<String, Number> series = new XYChart.Series<>();
        series.setName(type + " Trend");

        for (Measurement m : list) {

            boolean include = switch (selectedRange) {
                case "7" -> !m.getDate().isBefore(now.minusDays(7));
                case "30" -> !m.getDate().isBefore(now.minusDays(30));
                default -> true; // ALL
            };

            if (include) {
                series.getData().add(new XYChart.Data<>(m.getDate().toString(), m.getValue()));
            }
        }

        measurementTrendChart.getData().add(series);
    }
    /**
     * Adds a new measurement entry for the selected type and refreshes the history and trend.
     */
    @FXML
    private void handleAddMeasurement() {
        String type = measurementTypeCombo.getValue();
        String input = newMeasurementField.getText();

        if (input.isBlank()) {
            authentication.showError("Invalid Input", "Please enter a measurement value.");
            return;
        }

        double value;
        try {
            value = Double.parseDouble(input);
        } catch (NumberFormatException e) {
            authentication.showError("Invalid Number", "Please enter a valid numeric value.");
            return;
        }

        Measurement m = new Measurement(
                CurrentUser.get().getId(),
                value,
                type,
                LocalDate.now()
        );

        measurementDAO.addMeasurement(m);
        newMeasurementField.clear();
        loadMeasurements();
    }


    @FXML
    public void handleDeleteAccount(javafx.scene.input.MouseEvent event) throws IOException {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION,
                "Are you sure you want to delete your account? This action cannot be undone.");
        alert.setHeaderText("Confirm Account Deletion");

        var result = alert.showAndWait();
        if (result.isEmpty() || result.get() != ButtonType.OK) {
            return;
        }

        boolean success = userDAO.deleteUser(user.getId());

        if (success) {
            CurrentUser.clear();
            authentication.switchScene((javafx.scene.Node) event.getSource(), LOGIN_VIEW);
        } else {
            authentication.showError("Error", "Failed to delete user.");
        }
    }

}

