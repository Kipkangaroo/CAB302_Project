package com.lockedin.lockedin.controller.profile;

import com.lockedin.lockedin.controller.auth.Authentication;
import com.lockedin.lockedin.model.entity.User;
import com.lockedin.lockedin.model.session.CurrentUser;
import com.lockedin.lockedin.model.dao.FoodDAO;
import com.lockedin.lockedin.model.dao.WorkoutRoutineDAO;


import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
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
    private static final String LOGIN_VIEW = "/com/lockedin/lockedin/pages/auth/login-view.fxml";
    private final FoodDAO foodDAO = new FoodDAO();
    private final WorkoutRoutineDAO workoutDAO = new WorkoutRoutineDAO();
    private User user;
    @FXML
    private Button logoutBtn;
    @FXML
    private Label ageLabel;
    @FXML
    private Label heightLabel;
    @FXML
    private Label weightLabel;
    @FXML
    private Label fitnessGoalLabel;
    @FXML
    private TextField firstNameField;
    @FXML
    private Label calTracking0;
    @FXML
    private Label calTracking1;
    @FXML
    private Label calTracking2;
    @FXML
    private Label calTracking3;
    @FXML
    private Label calTracking4;
    @FXML
    private Label calTracking5;
    @FXML
    private Label calTracking6;
    @FXML
    private Circle calTrackingCircle0;
    @FXML
    private Circle calTrackingCircle1;
    @FXML
    private Circle calTrackingCircle2;
    @FXML
    private Circle calTrackingCircle3;
    @FXML
    private Circle calTrackingCircle4;
    @FXML
    private Circle calTrackingCircle5;
    @FXML
    private Circle calTrackingCircle6;
    @FXML
    private Label workoutTracking0;
    @FXML
    private Label workoutTracking1;
    @FXML
    private Label workoutTracking2;
    @FXML
    private Label workoutTracking3;
    @FXML
    private Label workoutTracking4;
    @FXML
    private Label workoutTracking5;
    @FXML
    private Label workoutTracking6;
    @FXML
    private Circle workoutTrackingCircle0;
    @FXML
    private Circle workoutTrackingCircle1;
    @FXML
    private Circle workoutTrackingCircle2;
    @FXML
    private Circle workoutTrackingCircle3;
    @FXML
    private Circle workoutTrackingCircle4;
    @FXML
    private Circle workoutTrackingCircle5;
    @FXML
    private Circle workoutTrackingCircle6;

    @FXML
    private void handleLogout() throws IOException {
        CurrentUser.clear();
        Authentication.switchScene(logoutBtn, LOGIN_VIEW);
    }

    @FXML
    private void handleEditName() {
        firstNameField.setEditable(true);
    }

    @FXML
    private void initialize() {
        user = CurrentUser.get();
        ageLabel.setText("Age: " + user.getAge());
        heightLabel.setText("Height: " + user.getHeight() + " cm");
        weightLabel.setText("Weight: " + user.getWeight() + " kg");
        fitnessGoalLabel.setText("Fitness Goal: " + user.getFitnessGoal());
        firstNameField.setText("Hello " + user.getFirstName() + "!");
        updateCalorieTrackingStreak();
    }

    private void updateCalorieTrackingStreak() {
        Label[] calTrackingLabels = {
                calTracking0, calTracking1, calTracking2, calTracking3, calTracking4, calTracking5, calTracking6};
        Circle[] calTrackingCircles = {
                calTrackingCircle0, calTrackingCircle1, calTrackingCircle2, calTrackingCircle3, calTrackingCircle4, calTrackingCircle5, calTrackingCircle6};
        Label[] workoutTrackingLabels = {
                workoutTracking0, workoutTracking1, workoutTracking2, workoutTracking3, workoutTracking4, workoutTracking5, workoutTracking6};
        Circle[] workoutTrackingCircles = {
                workoutTrackingCircle0, workoutTrackingCircle1, workoutTrackingCircle2, workoutTrackingCircle3, workoutTrackingCircle4, workoutTrackingCircle5, workoutTrackingCircle6};
        boolean[] calorieStreakDays = foodDAO.getWeeklyCalorieTracking(user.getId());
        boolean[] workoutStreakDays = workoutDAO.getWeeklyWorkoutTracking(user.getId());
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM");
        for (int j = 0; j < 7; j++) {
            int daysAgo = 6 - j;
            LocalDate date = LocalDate.now().minusDays(daysAgo);
            if (daysAgo == 0) {
                calTrackingLabels[j].setText("Today");
                workoutTrackingLabels[j].setText("Today");
            } else {
                calTrackingLabels[j].setText(date.format(formatter));
                workoutTrackingLabels[j].setText(date.format(formatter));
            }
            if (calorieStreakDays[daysAgo]) {
                calTrackingCircles[j].setFill(Paint.valueOf("#2cda86"));
            } else {
                calTrackingCircles[j].setFill(Paint.valueOf("#ff0000"));
            }
            if (workoutStreakDays[daysAgo]) {
                workoutTrackingCircles[j].setFill(Paint.valueOf("#028ee1"));
            } else {
                workoutTrackingCircles[j].setFill(Paint.valueOf("#FFFFFF"));
            }
        }
    }
}
