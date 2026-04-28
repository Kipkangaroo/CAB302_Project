package com.lockedin.lockedin.controller.auth;

import java.io.IOException;
import java.time.LocalDate;
import com.lockedin.lockedin.model.entity.User;
import com.lockedin.lockedin.model.dao.UserDAO;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.control.PasswordField;
import javafx.stage.Stage;

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
    private void handleBackButton() throws IOException {
        FXMLLoader loader = new FXMLLoader(
                getClass().getResource("/com/lockedin/lockedin/pages/auth/login-view.fxml"));
        Scene scene = new Scene(loader.load(), 410, 650);
        Stage stage = (Stage) backBtn.getScene().getWindow();
        stage.setScene(scene);
    }

    @FXML
    private void handleSignup() {
        String firstName = firstNameField.getText().trim();
        String lastName = lastNameField.getText().trim();
        String email = emailField.getText().trim();
        String password = passwordField.getText().trim();
        String confirmPassword = confirmPasswordField.getText().trim();
        if (!password.equals(confirmPassword)) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setHeaderText("Passwords do not match");
            alert.setContentText("Please enter the same password in both fields.");
            alert.showAndWait();
            return;
        }
        if (firstName.isEmpty() || lastName.isEmpty() || email.isEmpty() || password.isEmpty() || confirmPassword.isEmpty()) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setHeaderText("All fields are required");
            alert.setContentText("Please fill in all fields.");
            alert.showAndWait();
            return;
        }
        if (!email.matches("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$")) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setHeaderText("Invalid email");
            alert.setContentText("Please enter a valid email format.");
            alert.showAndWait();
            return;
        }
        if (new UserDAO().getUserByEmail(email).isPresent()) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setHeaderText("Email already exists");
            alert.setContentText("Please log in to your account.");
            alert.showAndWait();
            return;
        }
        if (password.length() < 8 || !password.matches("^(?=.*[a-z])(?=.*[A-Z])(?=.*[^A-Za-z0-9]).{8,}$")) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setHeaderText("Invalid password");
            alert.setContentText("Please enter a valid password. It must be at least 8 characters long and contain at least one uppercase letter, one lowercase letter, and one special character.");
            alert.showAndWait();
            return;
        }
        if (new UserDAO().createUser(new User(0, firstName, lastName, email, LocalDate.now(), 0, 0, password, ""))) {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setHeaderText("Signup successful");
            alert.setContentText("You can now log in to your account.");
            alert.showAndWait();
        } 
    }
}   
