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
import java.io.ByteArrayInputStream;
import java.util.Optional;
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
    private static final DateTimeFormatter CHART_DATE_FORMAT = DateTimeFormatter.ofPattern("d/M");
    private static final int WEIGHT_CHART_DAYS = 30;
    private static final int CHART_LABEL_INTERVAL = 5;
    private static final Paint COMPLETED_FILL = Paint.valueOf(BLUE_FILL);
    private static final Paint MISSED_FILL = Paint.valueOf(WHITE_FILL);
    private final Authentication authentication = new Authentication();
    private final UserDAO userDAO = new UserDAO();
    private final FoodDAO foodDAO = new FoodDAO();
    private final WorkoutRoutineDAO workoutDAO = new WorkoutRoutineDAO();
    private final UserProgressDAO progressDAO = new UserProgressDAO();
    private final UserImageDAO imageDAO = new UserImageDAO();
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
    @FXML
    private LineChart<String, Number> weightChart;
    private Image editImage;
    private Image saveImage;

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
        loadWeightChart();
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
        loadWeightChart();
        fitnessGoalCombo.getItems().setAll(GOAL_MAP.keySet());
        fitnessGoalCombo.setValue(GOAL_LABELS.get(user.getFitnessGoal()));
        setFitnessGoalEditing(false);
        loadProfileImage();
    }
    private void loadProfileImage() {
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
        if (daysAgo == 0) {
            return "Today";
        }
        if (daysAgo == WEIGHT_CHART_DAYS - 1 || daysAgo % CHART_LABEL_INTERVAL == 0) {
            return date.format(CHART_DATE_FORMAT);
        }
        return "";
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

