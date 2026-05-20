package com.lockedin.lockedin.controller.auth;

import com.lockedin.lockedin.model.dao.UserDAO;
import com.lockedin.lockedin.model.dao.UserProgressDAO;
import com.lockedin.lockedin.model.entity.user.ActivityLevel;
import com.lockedin.lockedin.model.entity.user.FitnessGoal;
import com.lockedin.lockedin.model.entity.user.User;
import com.lockedin.lockedin.model.entity.user.UserProgress;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;

import java.io.IOException;
import java.time.LocalDate;
import java.util.Optional;

/**
 * Controller responsible for handling user registration. Validates input
 * fields, creates a new User
 * entity, and stores it via UserDAO.
 */
public class SignUpController {
    private static final String LOGIN_VIEW = "/com/lockedin/lockedin/pages/auth/login-view.fxml";
    private final Authentication authentication = new Authentication();
    @FXML
    private ImageView logoImageView;
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
    private ComboBox<String> sexCombo;
    @FXML
    private ComboBox<ActivityLevel> activityLevelCombo;
    @FXML
    private ComboBox<FitnessGoal> fitnessGoalCombo;

    public static Double parseValidDouble(String text) {
        if (text == null || text.trim().isEmpty()) {
            return null;
        }
        try {
            double value = Double.parseDouble(text.trim());
            if (value <= 0 || Double.isNaN(value) || Double.isInfinite(value)) {
                return null;
            }
            return value;
        } catch (NumberFormatException e) {
            return null;
        }
    }

    /** Initializes the sign-up form by populating dropdowns. */
    @FXML
    private void initialize() {
        sexCombo.setItems(FXCollections.observableArrayList("Male", "Female"));
        activityLevelCombo.setItems(FXCollections.observableArrayList(ActivityLevel.values()));
        fitnessGoalCombo.setItems(FXCollections.observableArrayList(FitnessGoal.values()));
        // Trigger signup when pressing Enter
        signupBtn.setDefaultButton(true);
    }

    @FXML
    private void handleBackButton(MouseEvent event) throws IOException {
        authentication.switchScene(
                logoImageView, LOGIN_VIEW);
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
        FitnessGoal fitnessGoal = fitnessGoalCombo.getValue();
        String sex = sexCombo.getValue();
        ActivityLevel activityLevel = activityLevelCombo.getValue();
        LocalDate dob = dobPicker.getValue();
        double height;
        double weight;
        if (!password.equals(confirmPassword)) {
            authentication.showError(
                    "Passwords do not match", "Please enter the same password in both fields.");
            return;
        }
        if (firstName.isEmpty()
                || lastName.isEmpty()
                || email.isEmpty()
                || password.isEmpty()
                || confirmPassword.isEmpty()
                || dobPicker.getValue() == null
                || heightText.isEmpty()
                || weightText.isEmpty()
                || sex == null
                || activityLevel == null
                || fitnessGoal == null) {
            authentication.showError("All fields are required", "Please fill in all fields.");
            return;
        }
        if (!firstName.matches("^[A-Za-z]+$") || !lastName.matches("^[A-Za-z]+$")) {
            authentication.showError(
                    "Invalid name", "First name and last name must contain letters only.");
            return;
        }
        if (dob.plusYears(18).isAfter(LocalDate.now())) {
            authentication.showError(
                    "Age restriction", "You must be at least 18 years old to sign up.");
            return;
        }
        if (!authentication.isValidEmail(email)) {
            authentication.showError("Invalid email", "Please enter a valid email format.");
            return;
        }
        if (new UserDAO().getUserByEmail(email).isPresent()) {
            authentication.showError("Email already exists", "Please log in to your account.");
            return;
        }
        if (!authentication.isValidPassword(password)) {
            authentication.showError(
                    "Invalid password",
                    "Please enter a valid password. It must be at least 8 characters long and"
                            + " contain at least one uppercase letter, one lowercase letter, and one"
                            + " special character.");
            return;
        }
        Double parsedHeight = parseValidDouble(heightText);
        Double parsedWeight = parseValidDouble(weightText);
        if (parsedHeight == null || parsedWeight == null) {
            authentication.showError(
                    "Invalid height or weight", "Height and weight must be valid real numbers greater than 0.");
            return;
        }
        height = parsedHeight;
        weight = parsedWeight;
        if (new UserDAO()
                .createUser(
                        new User(
                                0,
                                capitalize(firstName),
                                capitalize(lastName),
                                email,
                                dob,
                                height,
                                weight,
                                sex,
                                activityLevel,
                                fitnessGoal,
                                password))) {
            Optional<User> addedUser = new UserDAO().getUserByEmail(email);
            if (addedUser.isPresent()) {
                User u = addedUser.get();
                // Add initial user progress
                new UserProgressDAO()
                        .addUserProgress(
                                new UserProgress(
                                        0,
                                        u.getId(),
                                        u.getFitnessGoal(),
                                        u.getWeight(),
                                        u.getTargetCalories(),
                                        LocalDate.now()));
            }
            authentication.showInfo("Signup successful", "You can now log in to your account.");
            try {
                authentication.switchScene(
                        signupBtn, "/com/lockedin/lockedin/pages/auth/login-view.fxml");
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            authentication.showError("Signup failed", "Please try again.");
        }
    }

    private String capitalize(String string) {
        return string.substring(0, 1).toUpperCase() + string.substring(1).toLowerCase();
    }
}
