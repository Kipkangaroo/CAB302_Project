package com.lockedin.lockedin.controller.profile;

import com.lockedin.lockedin.controller.auth.Authentication;
import com.lockedin.lockedin.controller.auth.SignUpController;
import com.lockedin.lockedin.model.dao.*;
import com.lockedin.lockedin.model.entity.user.FitnessGoal;
import com.lockedin.lockedin.model.entity.user.User;
import com.lockedin.lockedin.model.entity.user.UserProgress;
import com.lockedin.lockedin.model.session.CurrentUser;

import javafx.geometry.Pos;
import javafx.fxml.FXML;
import javafx.scene.chart.*;
import javafx.scene.control.*;
import javafx.scene.image.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Circle;

import java.io.IOException;
import java.io.ByteArrayInputStream;
import java.util.Optional;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Map;

/**
 * JavaFX controller for the profile screen.
 * @author LockedIn Team
 * @version 1.0
 */
public class ProfileController {
    private static final String LOGIN_VIEW = "/com/lockedin/lockedin/pages/auth/login-view.fxml";
    private static final int WEIGHT_CHART_DAYS = 30;
    private final Authentication authentication = new Authentication();
    private final UserDAO userDAO = new UserDAO();
    private final UserProgressDAO progressDAO = new UserProgressDAO();
    @FXML
    private ImageView imageView;
    @FXML
    private StackPane profilePhotoPane;
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
    private Button logoutButton;
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
    @FXML
    private LineChart<String, Number> weightChart;
    private Image editImage;
    private Image saveImage;

        /**
     * Handle logout.
     * 
     * @throws IOException If the operation fails.
     */

    @FXML
    private void handleLogout() throws IOException {
        CurrentUser.clear();
        authentication.switchScene(logoutButton, LOGIN_VIEW);
    }

        /**
     * Handle edit details.
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
     * Enter edit mode.
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
     * Exit edit mode.
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
        loadWeightChart();
    }

        /**
     * Refresh detail fields.
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
     * Update edit icon.
     */
    private void updateEditIcon() {
        final double iconSize = 46;
        editActionIcon.setFitWidth(iconSize);
        editActionIcon.setFitHeight(iconSize);
        Image icon = editingDetails ? saveImage : editImage;
        if (icon != null) {
            editActionIcon.setImage(icon);
        }
    }

        /**
     * Sets the field editing.
     * 
     * @param field   The field.
     * @param editing editing
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
     * Applies a circular clip to the profile photo pane.
     */
    private void applyCircularProfileClip() {
        double width = profilePhotoPane.getPrefWidth();
        double height = profilePhotoPane.getPrefHeight();
        double radius = Math.min(width, height) / 2.0;
        profilePhotoPane.setClip(new Circle(width / 2.0, height / 2.0, radius));
    }

    /**
     * Initializes FXML-bound UI components after the view loads.
     */

    @FXML
    private void initialize() {
        final String editIcon = "/com/lockedin/lockedin/graphics/icons/edit-icon.png";
        final String saveIcon = "/com/lockedin/lockedin/graphics/icons/save-icon.png";
        var editIconUrl = getClass().getResource(editIcon);
        var saveIconUrl = getClass().getResource(saveIcon);
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
        loadWeightChart();
        fitnessGoalCombo.getItems().setAll(GOAL_MAP.keySet());
        fitnessGoalCombo.setValue(GOAL_LABELS.get(user.getFitnessGoal()));
        setFitnessGoalEditing(false);
        loadProfileImage();
    }

    /**
     * Loads the user's profile image from storage or the default placeholder.
     */
    private void loadProfileImage() {
        UserImageDAO imageDAO = new UserImageDAO();
        Optional<byte[]> imageData = imageDAO.getImageByUserId(user.getId());
        Image image = imageData.isPresent()
                ? new Image(new ByteArrayInputStream(imageData.get()))
                : new Image(getClass().getResourceAsStream("/com/lockedin/lockedin/graphics/images/profileimage.png"));
        imageView.setImage(image);
        StackPane.setAlignment(imageView, Pos.CENTER);
        if (image.getProgress() < 1 && !image.isError()) {
            image.progressProperty().addListener((obs, oldProgress, newProgress) -> {
                if (newProgress.doubleValue() >= 1.0) {
                    StackPane.setAlignment(imageView, Pos.CENTER);
                    applyCircularProfileClip();
                }
            });
        }
        applyCircularProfileClip();
    }

    /**
     * Loads the 30-day weight progress line chart from user_progress history.
     */
    private void loadWeightChart() {
        weightChart.getData().clear();

        LocalDate end = LocalDate.now();
        LocalDate start = end.minusDays(WEIGHT_CHART_DAYS - 1);
        Map<LocalDate, Double> dailyWeights =
                progressDAO.getDailyWeightForRange(user.getId(), start, end);

        XYChart.Series<String, Number> series = new XYChart.Series<>();
        series.setName("Weight");

        for (int daysAgo = WEIGHT_CHART_DAYS - 1; daysAgo >= 0; daysAgo--) {
            LocalDate date = end.minusDays(daysAgo);
            String label = chartAxisLabel(date, daysAgo);
            series.getData().add(new XYChart.Data<>(label, dailyWeights.getOrDefault(date, user.getWeight())));
        }

        weightChart.getData().add(series);
    }

    /**
     * Returns a visible x-axis label only on selected days so the chart stays readable.
     */
    private String chartAxisLabel(LocalDate date, int daysAgo) {
        final DateTimeFormatter chartDateFormat = DateTimeFormatter.ofPattern("d/M");
        final int chartLabelInterval = 5;
        if (daysAgo == 0) {
            return "Today";
        }
        if (daysAgo == WEIGHT_CHART_DAYS - 1 || daysAgo % chartLabelInterval == 0) {
            return date.format(chartDateFormat);
        }
        return "";
    }

        /**
     * Update tracking streaks.
     */
    private void updateTrackingStreaks() {
        FoodDAO foodDAO = new FoodDAO();
        WorkoutRoutineDAO workoutDAO = new WorkoutRoutineDAO();
        updateStreak(calorieStreakRow, foodDAO.getWeeklyCalorieTracking(user.getId()));
        updateStreak(workoutStreakRow, workoutDAO.getWeeklyWorkoutTracking(user.getId()));
    }

        /**
     * Update streak.
     * 
     * @param row       The row.
     * @param completed completed
     */
    private void updateStreak(HBox row, boolean[] completed) {
        final DateTimeFormatter dayLabelFormat = DateTimeFormatter.ofPattern("dd/MM");
        final Paint completedFill = Paint.valueOf("#028ee1");
        final Paint missedFill = Paint.valueOf("#FFFFFF");
        LocalDate today = LocalDate.now();
        for (int j = 0; j < row.getChildren().size(); j++) {
            VBox day = (VBox) row.getChildren().get(j);
            Circle circle = (Circle) day.getChildren().get(0);
            Label label = (Label) day.getChildren().get(1);

            int daysAgo = 6 - j;
            label.setText(daysAgo == 0 ? "Today" : today.minusDays(daysAgo).format(dayLabelFormat));
            circle.setFill(completed[daysAgo] ? completedFill : missedFill);
        }
    }

    /**
     * Confirms and deletes the current user's account, then returns to login.
     *
     * @param event the mouse click event
     * @throws IOException if navigation to the login view fails
     */
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

