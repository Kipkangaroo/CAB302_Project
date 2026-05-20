package com.lockedin.lockedin.controller.auth;

import com.lockedin.lockedin.model.dao.UserDAO;
import com.lockedin.lockedin.model.entity.user.User;

import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.Objects;
import java.util.Optional;

/**
 * Coordinates credential checks, login against the database, and common auth UI
 * actions (alerts,
 * scene changes). Holds a {@link UserDAO} for data access.
 */
public class Authentication {
    private final UserDAO userDAO;

    /** Uses the application default user database. */
    public Authentication() {
        this(new UserDAO());
    }

    /**
     * @param userDAO data access for login; supplied in tests with an in-memory
     *                database.
     */
    public Authentication(UserDAO userDAO) {
        this.userDAO = Objects.requireNonNull(userDAO, "userDAO");
    }

    private static void showAlert(Alert.AlertType type, String header, String content) {
        Alert alert = new Alert(type);
        alert.setHeaderText(header);
        alert.setContentText(content);
        alert.showAndWait();
    }

    public boolean isValidEmail(String email) {
        return email.matches("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$");
    }

    public boolean isValidPassword(String password) {
        return password.length() >= 8
                && password.matches("^(?=.*[a-z])(?=.*[A-Z])(?=.*[^A-Za-z0-9]).{8,}$");
    }

    public void showError(String header, String content) {
        showAlert(Alert.AlertType.ERROR, header, content);
    }

    public void showInfo(String header, String content) {
        showAlert(Alert.AlertType.INFORMATION, header, content);
    }

    public void switchScene(Button sourceButton, String fxmlPath) throws IOException {
        switchScene((Node) sourceButton, fxmlPath);
    }

    public void switchScene(Node source, String fxmlPath) throws IOException {
        FXMLLoader loader = new FXMLLoader(Authentication.class.getResource(fxmlPath));
        Scene scene = new Scene(loader.load(), 410, 750);
        Stage stage = (Stage) source.getScene().getWindow();
        stage.setScene(scene);
    }

    /**
     * Attempts to authenticate a user by email and password using this instance's
     * {@link
     * UserDAO}.
     *
     * @return the user if credentials match, otherwise empty.
     */
    public Optional<User> authenticate(String email, String password) {
        Optional<User> user = userDAO.getUserByEmail(email);
        boolean ok = user.isPresent() && userDAO.authenticate(email, password);
        return ok ? user : Optional.empty();
    }
}
