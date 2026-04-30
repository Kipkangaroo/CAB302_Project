package com.lockedin.lockedin.controller.profile;

import java.time.LocalDate;

import com.lockedin.lockedin.model.entity.User;
import com.lockedin.lockedin.model.session.CurrentUser;
import javafx.fxml.FXML;
import javafx.scene.control.Label;

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
        int age = LocalDate.now().getYear() - user.getDateOfBirth().getYear();
        ageLabel.setText("Age: " + age);
        heightLabel.setText("Height: " + user.getHeight() + " cm");
        weightLabel.setText("Weight: " + user.getWeight() + " kg");
        fitnessGoalLabel.setText("Fitness Goal: " + user.getFitnessGoal());
        nameLabel.setText("Hello " + user.getFirstName() + "!");
    }
}
