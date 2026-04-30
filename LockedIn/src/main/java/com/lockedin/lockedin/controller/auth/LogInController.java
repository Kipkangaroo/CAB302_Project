package com.lockedin.lockedin.controller.auth;

import java.io.IOException;
import com.lockedin.lockedin.model.session.CurrentUser;
import com.lockedin.lockedin.model.entity.User;
import java.util.Optional;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;

/**
 * Controller responsible for handling user login actions.
 * Validates input, authenticates the user, and redirects to the main view.
 */
public class LogInController {
    private static final String MAIN_VIEW = "/com/lockedin/lockedin/pages/layout/main-view.fxml";
    private static User loggedInUser;
    @FXML
    private Button loginBtn;
    @FXML
    private TextField emailField;
    @FXML
    private PasswordField passwordField;

    /**
     * Handles the login button action:
     * -Validates email and password
     * -Authenticates the user via Authentication utility
     * -Updates CurrentUser session
     * -Redirects to main view on success
     */
    @FXML
    protected void handleLogIn() throws IOException {
        String email = emailField.getText().trim();
        if (!Authentication.isValidEmail(email)) {
            Authentication.showError("Invalid email", "Please enter a valid email format.");
            return;
        }
        if (passwordField.getText().trim().isEmpty()) {
            Authentication.showError("Invalid password", "Password field is empty!");
            return;
        }
        Optional<User> user = Authentication.authenticate(email, passwordField.getText().trim());
        if (user.isPresent()) {
            CurrentUser.set(user.get());
            loggedInUser = user.get();
            successfulLogin();
        } else {
            loggedInUser = null;
            failedLogin();
        }
    }

    @FXML
    private void initialize() {
        loginBtn.setDefaultButton(true);
    }

    @FXML
    private void handleSignup() throws IOException {
        Authentication.switchScene(loginBtn, "/com/lockedin/lockedin/pages/auth/signup-view.fxml");
    }

    private void successfulLogin() throws IOException {
        Authentication.switchScene(loginBtn, MAIN_VIEW);
    }

    public static User getLoggedInUser() {
        return loggedInUser;
    }

    private void failedLogin() {
        Authentication.showError("Invalid email or password", "Please enter a valid email and password.");
    }

}
