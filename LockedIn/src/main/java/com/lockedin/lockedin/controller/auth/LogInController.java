package com.lockedin.lockedin.controller.auth;

import com.lockedin.lockedin.model.entity.user.User;
import com.lockedin.lockedin.model.session.CurrentUser;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

import java.io.IOException;
import java.util.Optional;

/**
 * JavaFX controller for the log in screen.
 * @author LockedIn Team
 * @version 1.0
 */
public class LoginController {
    private static User loggedInUser;
    private final Authentication authentication = new Authentication();
    @FXML
    private Button loginButton;
    @FXML
    private TextField emailField;
    @FXML
    private PasswordField passwordField;
    @FXML
    private TextField visiblePasswordField;
    @FXML
    private ImageView togglePasswordIcon;
    private boolean passwordVisible;
    private Image eyeIcon;
    private Image eyeOffIcon;

            /**
     * Returns the logged in user.
     * @return logged in user
     */
    public static User getLoggedInUser() {
        return loggedInUser;
    }
        /**
     * Handle log in.
     * @throws IOException If the operation fails.
     */

    @FXML
    protected void handleLogin() throws IOException {
        String email = emailField.getText().trim();
        if (!authentication.isValidEmail(email)) {
            authentication.showError("Invalid email", "Please enter a valid email format.");
            return;
        }
        if (getPasswordText().trim().isEmpty()) {
            authentication.showError("Invalid password", "Password field is empty!");
            return;
        }
        authenticate(email, getPasswordText().trim());
    }

    /**
     * Initializes FXML-bound components after the login view loads.
     */
    @FXML
    private void initialize() {
        loginButton.setDefaultButton(true);
        emailField.setText("john.demo@lockedin.app");
        passwordField.setText("Password1!");
        eyeIcon = new Image(getClass().getResourceAsStream(
                "/com/lockedin/lockedin/graphics/icons/eye-icon.png"));
        eyeOffIcon = new Image(getClass().getResourceAsStream(
                "/com/lockedin/lockedin/graphics/icons/eye-off-icon.png"));
    }

    /**
     * Toggles visibility between the password field and plain-text field.
     */
    @FXML
    private void handleTogglePassword() {
        if (passwordVisible) {
            passwordField.setText(visiblePasswordField.getText());
            visiblePasswordField.setVisible(false);
            visiblePasswordField.setManaged(false);
            passwordField.setVisible(true);
            passwordField.setManaged(true);
            togglePasswordIcon.setImage(eyeIcon);
        } else {
            visiblePasswordField.setText(passwordField.getText());
            passwordField.setVisible(false);
            passwordField.setManaged(false);
            visiblePasswordField.setVisible(true);
            visiblePasswordField.setManaged(true);
            togglePasswordIcon.setImage(eyeOffIcon);
        }
        passwordVisible = !passwordVisible;
        (passwordVisible ? visiblePasswordField : passwordField).requestFocus();
    }

    /**
     * Returns the password text from whichever input field is currently visible.
     *
     * @return the entered password
     */
    private String getPasswordText() {
        return passwordVisible ? visiblePasswordField.getText() : passwordField.getText();
    }

        /**
     * Successful login.
     * @throws IOException If the operation fails.
     */
    private void successfulLogin() throws IOException {
        final String mainView = "/com/lockedin/lockedin/pages/layout/main-view.fxml";
        authentication.switchScene(loginButton, mainView);
    }

        /**
     * Authenticate.
     * @param email email
     * @param password password
     */
    private void authenticate(String email, String password) {
        Optional<User> user = authentication.authenticate(email, password);
        if (user.isPresent()) {
            CurrentUser.set(user.get());
            loggedInUser = user.get();
            try {
                successfulLogin();
            } catch (IOException exception) {
                throw new RuntimeException("Failed to open main view after login", exception);
            }
        } else {
            loggedInUser = null;
            failedLogin();
        }
    }

        /**
     * Failed login.
     */
    private void failedLogin() {
        authentication.showError(
                "Invalid email or password", "Please enter a valid email and password.");
    }
        /**
     * Handle forgot password.
     * @throws IOException If the operation fails.
     */

    @FXML
    private void handleForgotPassword() throws IOException {
        final String forgotPasswordView = "/com/lockedin/lockedin/pages/auth/forgot-password-view.fxml";
        authentication.switchScene(loginButton, forgotPasswordView);
    }
        /**
     * Handle signup.
     * @throws IOException If the operation fails.
     */

    @FXML
    private void handleSignup() throws IOException {
        authentication.switchScene(
                loginButton, "/com/lockedin/lockedin/pages/auth/signup-view.fxml");
    }
}
