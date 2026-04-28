package com.lockedin.lockedin.controller.auth;

import java.io.IOException;
import com.lockedin.lockedin.model.dao.UserDAO;
import com.lockedin.lockedin.model.session.CurrentUser;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

public class LogInController {
    private static final String MAIN_VIEW = "/com/lockedin/lockedin/pages/layout/main-view.fxml";
    @FXML
    private Button loginBtn;
    @FXML
    private TextField emailField;
    @FXML
    private PasswordField passwordField;

    @FXML
    protected void handleLogIn() throws IOException {
        String email = emailField.getText().trim();
        if (!validateEmail(email)) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setHeaderText("Invalid email");
            alert.setContentText("Please enter a valid email format.");
            alert.showAndWait();
            return;
        }
        if (passwordField.getText().trim().isEmpty()) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setHeaderText("Invalid password");
            alert.setContentText("Password field is empty!");
            alert.showAndWait();
            return;
        }
        authenticate(email, passwordField.getText().trim());
    }

    private void successfulLogin() throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource(MAIN_VIEW));
        Scene scene = new Scene(loader.load(), 410, 650);
        Stage stage = (Stage) loginBtn.getScene().getWindow();
        stage.setScene(scene);
    }

    private void authenticate(String email, String password) {
        UserDAO dao = new UserDAO();
        boolean authenticated = dao.authenticate(email, password);
        if (authenticated) {
            dao.getUserByEmail(email).ifPresent(CurrentUser::set);
            try {
                successfulLogin();
            } catch (IOException exception) {
                throw new RuntimeException("Failed to open main view after login", exception);
            }
        } else {
            failedLogin();
        }
    }
    private boolean validateEmail(String email) {
        return email.matches("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$");
    }

    private void failedLogin() {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setHeaderText("Invalid email or password");
        alert.setContentText("Please enter a valid email and password.");
        alert.showAndWait();
    }

}
