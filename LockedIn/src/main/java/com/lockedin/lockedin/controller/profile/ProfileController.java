package com.lockedin.lockedin.controller.profile;

import com.lockedin.lockedin.controller.auth.Authentication;
import com.lockedin.lockedin.controller.auth.SignUpController;
import com.lockedin.lockedin.model.dao.FoodDAO;
import com.lockedin.lockedin.model.dao.UserProgressDAO;
import com.lockedin.lockedin.model.dao.WorkoutRoutineDAO;
import com.lockedin.lockedin.model.dao.UserDAO;
import com.lockedin.lockedin.model.entity.user.User;
import com.lockedin.lockedin.model.entity.user.UserProgress;
import com.lockedin.lockedin.model.session.CurrentUser;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Circle;

import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

/**
 * Controller for the Profile page. Displays the currently logged-in user's
 * personal information.
 */
public class ProfileController {
    private static final String BLUE_FILL = "#028ee1";
    private static final String WHITE_FILL = "#FFFFFF";
    private static final String LOGIN_VIEW = "/com/lockedin/lockedin/pages/auth/login-view.fxml";
    private static final String EDIT_ICON = "/com/lockedin/lockedin/images/edit-icon.png";
    private static final String SAVE_ICON = "/com/lockedin/lockedin/images/save-icon.png";
    private static final double ICON_SIZE = 46;
    private static final DateTimeFormatter DAY_LABEL_FORMAT = DateTimeFormatter.ofPattern("dd/MM");
    private static final Paint COMPLETED_FILL = Paint.valueOf(BLUE_FILL);
    private static final Paint MISSED_FILL = Paint.valueOf(WHITE_FILL);
    private final Authentication authentication = new Authentication();
    private final UserDAO userDAO = new UserDAO();
    private final FoodDAO foodDAO = new FoodDAO();
    private final WorkoutRoutineDAO workoutDAO = new WorkoutRoutineDAO();
    private final UserProgressDAO progressDAO = new UserProgressDAO();
    private User user;
    private boolean editingDetails;
    @FXML
    private Button logoutBtn;
    @FXML
    private Label ageLabel;
    @FXML
    private Label heightLabel;
    @FXML
    private TextField weightField;
    @FXML
    private TextField fitnessGoalField;
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
    private void handleLogout() throws IOException {
        CurrentUser.clear();
        authentication.switchScene(logoutBtn, LOGIN_VIEW);
    }

    @FXML
    private void handleEditDetails() {
        if (editingDetails) {
            exitEditMode();
        } else {
            enterEditMode();
        }
    }

    private void enterEditMode() {
        editingDetails = true;
        updateEditIcon();
        double weight = user.getWeight();
        weightField.setText(weight == (long) weight ? String.valueOf((long) weight) : String.valueOf(weight));
        fitnessGoalField.setText(String.valueOf(user.getFitnessGoal()));
        setFieldEditing(weightField, true);
        setFieldEditing(fitnessGoalField, true);
        weightField.requestFocus();
        weightField.selectAll();
    }

    private void exitEditMode() {
        Double weight = SignUpController.parseValidDouble(weightField.getText());
        if (weight == null) {
            authentication.showError(
                    "Invalid weight", "Weight cannot be empty and must be greater than 0.");
            return;
        }
        editingDetails = false;
        updateEditIcon();
        user.setWeight(weight);
        userDAO.updateWeight(user.getId(), weight);
        progressDAO.addUserProgress(new UserProgress(0, user.getId(), user.getFitnessGoal(), weight,
                user.getTargetCalories(), LocalDate.now()));
        setFieldEditing(weightField, false);
        setFieldEditing(fitnessGoalField, false);
        refreshDetailFields();
    }

    private void refreshDetailFields() {
        weightField.setText("Weight: " + user.getWeight() + " kg");
        fitnessGoalField.setText("Fitness Goal: " + user.getFitnessGoal());
    }

    private void updateEditIcon() {
        editActionIcon.setFitWidth(ICON_SIZE);
        editActionIcon.setFitHeight(ICON_SIZE);
        Image icon = editingDetails ? saveImage : editImage;
        if (icon != null) {
            editActionIcon.setImage(icon);
        }
    }

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
    }

    private void updateTrackingStreaks() {
        updateStreak(calorieStreakRow, foodDAO.getWeeklyCalorieTracking(user.getId()));
        updateStreak(workoutStreakRow, workoutDAO.getWeeklyWorkoutTracking(user.getId()));
    }

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
}
