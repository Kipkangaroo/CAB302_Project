package com.lockedin.lockedin.controller.auth;

import java.io.IOException;
import java.util.Optional;

import com.lockedin.lockedin.model.dao.UserDAO;
import com.lockedin.lockedin.model.entity.User;

import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.stage.Stage;

public class Authentication {
    private Authentication() {
    }

    public static boolean isValidEmail(String email) {
        return email.matches("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$");
    }

    public static boolean isValidPassword(String password) {
        return password.length() >= 8 && password.matches("^(?=.*[a-z])(?=.*[A-Z])(?=.*[^A-Za-z0-9]).{8,}$");
    }

    public static void showError(String header, String content) {
        showAlert(Alert.AlertType.ERROR, header, content);
    }

    public static void showInfo(String header, String content) {
        showAlert(Alert.AlertType.INFORMATION, header, content);
    }

    public static void switchScene(Button sourceButton, String fxmlPath) throws IOException {
        FXMLLoader loader = new FXMLLoader(Authentication.class.getResource(fxmlPath));
        Scene scene = new Scene(loader.load(), 410, 650);
        Stage stage = (Stage) sourceButton.getScene().getWindow();
        stage.setScene(scene);
    }

    public static Optional<User> authenticate(String email, String password) {
        UserDAO userDAO = new UserDAO();
        Optional<User> user = userDAO.getUserByEmail(email);
        boolean authenticated = user.isPresent() && userDAO.authenticate(email, password);
        return authenticated ? user : Optional.empty();
    }

    public static Optional<User> authenticate(String email, String password, UserDAO userDAO) {
        Optional<User> user = userDAO.getUserByEmail(email);
        boolean authenticated = user.isPresent() && userDAO.authenticate(email, password);
        return authenticated ? user : Optional.empty();
    }

    private static void showAlert(Alert.AlertType type, String header, String content) {
        Alert alert = new Alert(type);
        alert.setHeaderText(header);
        alert.setContentText(content);
        alert.showAndWait();
    }

}
