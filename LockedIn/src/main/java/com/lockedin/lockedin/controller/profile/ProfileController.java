package com.lockedin.lockedin.controller.profile;

import com.lockedin.lockedin.model.entity.User;
import com.lockedin.lockedin.model.session.CurrentUser;
import javafx.fxml.FXML;
import javafx.scene.control.Label;

/**
 * Controller for the Profile page.
 * Displays the currently logged-in user's personal information.
 */
public class ProfileController {
    private User user;
    @FXML
    private Label nameLabel;
    @FXML
    private Label ageLabel;
    @FXML
    private Label heightLabel;
    @FXML
    private Label weightLabel;
    @FXML
    private Label fitnessGoalLabel;
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
