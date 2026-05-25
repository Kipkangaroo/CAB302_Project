package com.lockedin.lockedin.controller.auth;

import com.lockedin.lockedin.model.dao.UserDAO;
import com.lockedin.lockedin.model.dao.UserImageDAO;
import com.lockedin.lockedin.model.dao.UserProgressDAO;
import com.lockedin.lockedin.model.entity.user.ActivityLevel;
import com.lockedin.lockedin.model.entity.user.FitnessGoal;
import com.lockedin.lockedin.model.entity.user.User;
import com.lockedin.lockedin.model.entity.user.UserProgress;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.StackPane;
import javafx.scene.shape.Circle;
import javafx.stage.FileChooser;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.time.LocalDate;
import java.util.Optional;

/**
 * JavaFX controller for the sign up screen.
 * 
 * @author LockedIn Team
 * @version 1.0
 */
public class SignUpController {
    private final Authentication authentication = new Authentication();
    private Image selectedProfileImage;
    private File selectedProfileImageFile;
    @FXML
    private ImageView logoImageView;
    @FXML
    private StackPane profilePhotoPane;
    @FXML
    private ImageView profilePlaceholderImageView;
    @FXML
    private TextField firstNameField;
    @FXML
    private TextField lastNameField;
    @FXML
    private TextField emailField;
    @FXML
    private PasswordField passwordField;
    @FXML
    private TextField visiblePasswordField;
    @FXML
    private ImageView togglePasswordIcon;
    @FXML
    private PasswordField confirmPasswordField;
    @FXML
    private TextField visibleConfirmPasswordField;
    @FXML
    private ImageView toggleConfirmPasswordIcon;
    private boolean passwordVisible;
    private boolean confirmPasswordVisible;
    private Image eyeIcon;
    private Image eyeOffIcon;
    @FXML
    private Button signUpButton;
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

    /**
     * Performs parse valid double.
     * 
     * @param text The text.
     */
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
        eyeIcon = new Image(getClass().getResourceAsStream(
                "/com/lockedin/lockedin/graphics/icons/eye-icon.png"));
        eyeOffIcon = new Image(getClass().getResourceAsStream(
                "/com/lockedin/lockedin/graphics/icons/eye-off-icon.png"));
        signUpButton.setDefaultButton(true);
    }

    private void applyCircularProfileClip() {
        double width = profilePhotoPane.getPrefWidth();
        double height = profilePhotoPane.getPrefHeight();
        double radius = Math.min(width, height) / 2.0;
        profilePhotoPane.setClip(new Circle(width / 2.0, height / 2.0, radius));
    }

    @FXML
    private void handleProfilePlaceholderClick(MouseEvent event) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Choose profile photo");
        fileChooser
                .getExtensionFilters()
                .add(new FileChooser.ExtensionFilter("Image Files", "*.png", "*.jpg", "*.jpeg"));
        File selectedFile = fileChooser.showOpenDialog(profilePlaceholderImageView.getScene().getWindow());
        if (selectedFile != null) {
            selectedProfileImageFile = selectedFile;
            selectedProfileImage = new Image(selectedFile.toURI().toString(), true);
            profilePlaceholderImageView.setImage(selectedProfileImage);
            applyCircularProfileClip();
        }
    }

    @FXML
    private void handleTogglePassword() {
        passwordVisible = togglePasswordVisibility(
                passwordField, visiblePasswordField, togglePasswordIcon, passwordVisible);
    }

    @FXML
    private void handleToggleConfirmPassword() {
        confirmPasswordVisible = togglePasswordVisibility(
                confirmPasswordField,
                visibleConfirmPasswordField,
                toggleConfirmPasswordIcon,
                confirmPasswordVisible);
    }

    private boolean togglePasswordVisibility(
            PasswordField passwordField,
            TextField visiblePasswordField,
            ImageView toggleIcon,
            boolean currentlyVisible) {
        if (currentlyVisible) {
            passwordField.setText(visiblePasswordField.getText());
            visiblePasswordField.setVisible(false);
            visiblePasswordField.setManaged(false);
            passwordField.setVisible(true);
            passwordField.setManaged(true);
            toggleIcon.setImage(eyeIcon);
        } else {
            visiblePasswordField.setText(passwordField.getText());
            passwordField.setVisible(false);
            passwordField.setManaged(false);
            visiblePasswordField.setVisible(true);
            visiblePasswordField.setManaged(true);
            toggleIcon.setImage(eyeOffIcon);
        }
        boolean nowVisible = !currentlyVisible;
        (nowVisible ? visiblePasswordField : passwordField).requestFocus();
        return nowVisible;
    }

    private String getPasswordText(
            PasswordField passwordField, TextField visiblePasswordField, boolean isVisible) {
        return isVisible ? visiblePasswordField.getText() : passwordField.getText();
    }

    /**
     * Performs handle back button.
     * 
     * @param event The event.
     * @throws IOException If the operation fails.
     */

    @FXML
    private void handleBackButton(MouseEvent event) throws IOException {
        final String loginView = "/com/lockedin/lockedin/pages/auth/login-view.fxml";
        authentication.switchScene(logoImageView, loginView);
    }

    /**
     * Performs handle signup.
     */

    @FXML
    private void handleSignup() {
        String firstName = firstNameField.getText().trim();
        String lastName = lastNameField.getText().trim();
        String email = emailField.getText().trim();
        String password = getPasswordText(passwordField, visiblePasswordField, passwordVisible).trim();
        String confirmPassword = getPasswordText(
                confirmPasswordField, visibleConfirmPasswordField, confirmPasswordVisible)
                .trim();
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
        if (selectedProfileImage == null) {
            authentication.showError(
                    "Profile photo required",
                    "Please upload a profile photo above your name.");
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
                try {
                    new UserImageDAO()
                            .saveOrReplaceImage(
                                    u.getId(), Files.readAllBytes(selectedProfileImageFile.toPath()));
                } catch (IOException e) {
                    authentication.showError(
                            "Profile photo failed to save",
                            "Your account was created, but the profile photo could not be stored.");
                }
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
                        signUpButton, "/com/lockedin/lockedin/pages/auth/login-view.fxml");
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            authentication.showError("Signup failed", "Please try again.");
        }
    }

    /**
     * Performs capitalize.
     * 
     * @param string The string.
     * @return The resulting text.
     */
    private String capitalize(String string) {
        return string.substring(0, 1).toUpperCase() + string.substring(1).toLowerCase();
    }
}
