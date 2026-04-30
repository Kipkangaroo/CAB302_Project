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
import javafx.scene.control.TextField;
import javafx.scene.control.PasswordField;

/**
 * Controller responsible for handling user registration.
 * Validates input fields, creates a new User entity, and stores it via UserDAO.
 */
public class SignUpController {
    @FXML
    private Button backBtn;
    @FXML
    private TextField firstNameField;
    @FXML
    private TextField lastNameField;
    @FXML
    private TextField emailField;
    @FXML
    private PasswordField passwordField;
    @FXML
    private PasswordField confirmPasswordField;
    @FXML
    private Button signupBtn;
    @FXML
    private DatePicker dobPicker;
    @FXML
    private TextField heightField;
    @FXML
    private TextField weightField;
    @FXML
    private ComboBox<String> fitnessGoalCombo;

    /**
     * Initializes the sign-up form by populating the fitness goal dropdown.
     */
    @FXML
    private void initialize() {
        fitnessGoalCombo.setItems(FXCollections.observableArrayList(
                "Lose Weight",
                "Build Muscle",
                "Maintain Fitness"
        ));
        // Trigger signup when pressing Enter
        signupBtn.setDefaultButton(true);
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
        String heightText = heightField.getText().trim();
        String weightText = weightField.getText().trim();
        String fitnessGoal = fitnessGoalCombo.getValue();
        LocalDate dob = dobPicker.getValue();
        double height;
        double weight;
        if (!password.equals(confirmPassword)) {
            Authentication.showError("Passwords do not match", "Please enter the same password in both fields.");
            return;
        }
        if (firstName.isEmpty() || lastName.isEmpty() || email.isEmpty() || password.isEmpty() || confirmPassword.isEmpty() || dobPicker.getValue() == null || heightText.isEmpty() || weightText.isEmpty() || fitnessGoal == null) {
            Authentication.showError("All fields are required", "Please fill in all fields.");
            return;
        }
        if (!firstName.matches("^[A-Za-z]+$") || !lastName.matches("^[A-Za-z]+$")) {
            Authentication.showError("Invalid name", "First name and last name must contain letters only.");
            return;
        }
        if (dob.plusYears(18).isAfter(LocalDate.now())) {
            Authentication.showError("Age restriction", "You must be at least 18 years old to sign up.");
            return;
        }
        if (!Authentication.isValidEmail(email)) {
            Authentication.showError("Invalid email", "Please enter a valid email format.");
            return;
        }
        if (new UserDAO().getUserByEmail(email).isPresent()) {
            Authentication.showError("Email already exists", "Please log in to your account.");
            return;
        }
        if (!Authentication.isValidPassword(password)) {
            Authentication.showError("Invalid password", "Please enter a valid password. It must be at least 8 characters long and contain at least one uppercase letter, one lowercase letter, and one special character.");
            return;
        }
        try {
            height = Double.parseDouble(heightText);
            weight = Double.parseDouble(weightText);
        } catch (NumberFormatException e) {
            Authentication.showError("Invalid height or weight", "Height and weight must be valid numbers.");
            return;
        }
        if (height <= 0 || weight <= 0) {
            Authentication.showError("Invalid height or weight", "Height and weight must be greater than 0.");
            return;
        }
        if (new UserDAO().createUser(new User(0, firstName, lastName, email, dobPicker.getValue(), height, weight, password, fitnessGoal))) {
            Authentication.showInfo("Signup successful", "You can now log in to your account.");
            try {
                Authentication.switchScene(backBtn, "/com/lockedin/lockedin/pages/auth/login-view.fxml");
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            Authentication.showError("Signup failed", "Please try again.");
        }
    }

}   
