package com.lockedin.lockedin.controller.auth;

import java.io.IOException;

import com.lockedin.lockedin.model.dao.OtpDAO;
import com.lockedin.lockedin.model.dao.UserDAO;
import com.lockedin.lockedin.model.entity.user.Otp;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;

/**
 * JavaFX controller for the forgot password screen.
 * 
 * @author LockedIn Team
 * @version 1.0
 */
public class ForgotPasswordController {
    private static final String LOGIN_VIEW = "/com/lockedin/lockedin/pages/auth/login-view.fxml";
    private final Authentication authentication = new Authentication();
    private boolean awaitingOtp;
    private boolean resettingPassword;
    private String email;
    @FXML
    private ImageView backImageView;
    @FXML
    private Label emailLabel;
    @FXML
    private TextField emailField;
    @FXML
    private PasswordField passwordField;
    @FXML
    private Label confirmPasswordLabel;
    @FXML
    private PasswordField confirmPasswordField;
    @FXML
    private Button getOtpButton;

    /**
     * Initializes FXML-bound UI components after the view loads.
     */

    @FXML
    private void initialize() {
        getOtpButton.setDefaultButton(true);
        setPasswordEntryVisible(false);
    }

    /**
     * Performs handle back button.
     * 
     * @param event The event.
     * @throws IOException If the operation fails.
     */

    @FXML
    private void handleBackButton(MouseEvent event) throws IOException {
        authentication.switchScene(backImageView, LOGIN_VIEW);
    }

    /**
     * Performs handle get otp.
     */

    @FXML
    private void handleGetOtp() {
        if (resettingPassword) {
            handleResetPassword();
            return;
        }
        if (awaitingOtp) {
            handleVerifyOtp();
            return;
        }
        String email = emailField.getText().trim();
        if (!authentication.isValidEmail(email)) {
            authentication.showError("Invalid email", "Please enter a valid email format.");
            return;
        }
        this.email = email;
        Otp userOtp = new Otp(email);
        userOtp.sendOtpToEmail();
        authentication.showInfo(
                "OTP sent",
                "If an account exists with that email, check for your OTP.");
        switchToOtpEntryMode();
    }

    /**
     * Performs switch to otp entry mode.
     */
    private void switchToOtpEntryMode() {
        awaitingOtp = true;
        resettingPassword = false;
        setPasswordEntryVisible(false);
        emailField.clear();
        passwordField.clear();
        confirmPasswordField.clear();
        emailLabel.setText("OTP:");
        emailField.setPromptText("Enter your OTP here");
        getOtpButton.setText("Verify OTP");
    }

    /**
     * Performs handle verify otp.
     */
    private void handleVerifyOtp() {
        OtpDAO otpDAO = new OtpDAO();
        String otpText = emailField.getText().trim();
        if (otpText.isEmpty()) {
            authentication.showError("Invalid OTP", "Please enter your OTP.");
            return;
        }
        try {
            if (!otpDAO.verifyOtp(this.email, Integer.parseInt(otpText))) {
                authentication.showError("Invalid OTP", "Please enter your OTP.");
                return;
            }
        } catch (NumberFormatException e) {
            authentication.showError("Invalid OTP", "Please enter your OTP.");
            return;
        }

        authentication.showInfo("OTP verified", "You can now reset your password.");
        switchToPasswordResetMode();
    }

    /**
     * Performs switch to password reset mode.
     */
    private void switchToPasswordResetMode() {
        awaitingOtp = false;
        resettingPassword = true;
        passwordField.clear();
        confirmPasswordField.clear();
        emailLabel.setText("New Password:");
        setPasswordEntryVisible(true);
        getOtpButton.setText("Reset Password");
    }

    /**
     * Performs handle reset password.
     */
    private void handleResetPassword() {
        UserDAO userDAO = new UserDAO();
        String password = passwordField.getText().trim();
        String confirmPassword = confirmPasswordField.getText().trim();
        if (password.isEmpty() || confirmPassword.isEmpty()) {
            authentication.showError("All fields are required", "Please fill in both password fields.");
            return;
        }
        if (!password.equals(confirmPassword)) {
            authentication.showError(
                    "Passwords do not match", "Please enter the same password in both fields.");
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
        if (!userDAO.updatePassword(email, password)) {
            authentication.showError("Reset failed", "Could not update your password. Please try again.");
            return;
        }
        authentication.showInfo("Password reset", "Your password has been updated. You can now log in.");
        try {
            authentication.switchScene(getOtpButton, LOGIN_VIEW);
        } catch (IOException e) {
            throw new RuntimeException("Failed to navigate to login", e);
        }
    }

    /**
     * Sets the password entry visible.
     * 
     * @param visible The visible.
     */
    private void setPasswordEntryVisible(boolean visible) {
        emailField.setVisible(!visible);
        emailField.setManaged(!visible);
        passwordField.setVisible(visible);
        passwordField.setManaged(visible);
        confirmPasswordLabel.setVisible(visible);
        confirmPasswordLabel.setManaged(visible);
        confirmPasswordField.setVisible(visible);
        confirmPasswordField.setManaged(visible);
    }
}
