package com.lockedin.lockedin.controller.auth;

import java.io.IOException;
import java.time.LocalDate;
import com.lockedin.lockedin.model.entity.User;
import com.lockedin.lockedin.model.dao.UserDAO;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;

public class SignUpController {
    private static final String PASSWORD_REQUIREMENTS_MESSAGE =
            "Please enter a valid password. It must be at least 8 characters long and contain at least one uppercase letter, one lowercase letter, and one special character.";

    @FXML private Button backBtn;
    @FXML private TextField firstNameField;
    @FXML private TextField lastNameField;
    @FXML private TextField emailField;
    @FXML private PasswordField passwordField;
    @FXML private PasswordField confirmPasswordField;
    @FXML private DatePicker dobPicker;
    @FXML private TextField heightField;
    @FXML private TextField weightField;
    @FXML private ComboBox<String> fitnessGoalCombo;
    @FXML private Button signupBtn;

    @FXML
    private void initialize() {
        fitnessGoalCombo.setItems(FXCollections.observableArrayList(
                "Lose Weight", "Build Muscle", "Maintain Fitness"
        ));
    }

    @FXML
    private void handleBackButton() throws IOException {
        Authentication.switchScene(backBtn, "/com/lockedin/lockedin/pages/auth/login-view.fxml");
    }

    @FXML
    private void handleSignup() {
        String firstName = firstNameField.getText().trim();
        String lastName = lastNameField.getText().trim();
        String email = emailField.getText().trim();
        String password = passwordField.getText().trim();
        String confirmPassword = confirmPasswordField.getText().trim();
        LocalDate dob = dobPicker.getValue();
        String heightText = heightField.getText().trim();
        String weightText = weightField.getText().trim();
        String fitnessGoal = fitnessGoalCombo.getValue();

        if (firstName.isEmpty() || lastName.isEmpty() || email.isEmpty()
                || password.isEmpty() || confirmPassword.isEmpty()
                || heightText.isEmpty() || weightText.isEmpty()) {
            Authentication.showError("All fields are required", "Please fill in all fields.");
            return;
        }

        if (dob == null) {
            Authentication.showError("Date of birth required", "Please select your date of birth.");
            return;
        }

        if (dob.isAfter(LocalDate.now())) {
            Authentication.showError("Invalid date of birth", "Date of birth cannot be in the future.");
            return;
        }

        if (fitnessGoal == null) {
            Authentication.showError("Fitness goal required", "Please select a fitness goal.");
            return;
        }

        double height, weight;
        try {
            height = Double.parseDouble(heightText);
            weight = Double.parseDouble(weightText);
        } catch (NumberFormatException e) {
            Authentication.showError("Invalid input", "Height and weight must be valid numbers.");
            return;
        }

        if (height <= 0 || weight <= 0) {
            Authentication.showError("Invalid input", "Height and weight must be positive values.");
            return;
        }

        if (!password.equals(confirmPassword)) {
            Authentication.showError("Passwords do not match", "Please enter the same password in both fields.");
            return;
        }

        if (!Authentication.isValidEmail(email)) {
            Authentication.showError("Invalid email", "Please enter a valid email format.");
            return;
        }

        UserDAO userDAO = new UserDAO();
        if (userDAO.getUserByEmail(email).isPresent()) {
            Authentication.showError("Email already exists", "Please log in to your account.");
            return;
        }

        if (!Authentication.isValidPassword(password)) {
            Authentication.showError("Invalid password", PASSWORD_REQUIREMENTS_MESSAGE);
            return;
        }

        if (userDAO.createUser(new User(0, firstName, lastName, email, dob, height, weight, password, fitnessGoal))) {
            Authentication.showInfo("Signup successful", "You can now log in to your account.");
        }
    }
}