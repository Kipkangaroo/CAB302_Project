package com.lockedin.lockedin.controller.profile;

import com.lockedin.lockedin.controller.auth.Authentication;
import com.lockedin.lockedin.model.entity.User;
import com.lockedin.lockedin.model.session.CurrentUser;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;

import java.io.IOException;

/**
 * Controller for the Profile page. Displays the currently logged-in user's personal information.
 */
public class ProfileController {
    private static final String LOGIN_VIEW = "/com/lockedin/lockedin/pages/auth/login-view.fxml";

    private User user;
    @FXML private Button logoutBtn;
    @FXML private Label nameLabel;
    @FXML private Label ageLabel;
    @FXML private Label heightLabel;
    @FXML private Label weightLabel;
    @FXML private Label fitnessGoalLabel;

    @FXML
    private void handleLogout() throws IOException {
        CurrentUser.clear();
        Authentication.switchScene(logoutBtn, LOGIN_VIEW);
    }

    @FXML
    private void initialize() {
        user = CurrentUser.get();
        ageLabel.setText("Age: " + user.getAge());
        heightLabel.setText("Height: " + user.getHeight() + " cm");
        weightLabel.setText("Weight: " + user.getWeight() + " kg");
        fitnessGoalLabel.setText("Fitness Goal: " + user.getFitnessGoal());
        nameLabel.setText("Hello " + user.getFirstName() + "!");
    }
}
