package com.lockedin.lockedin.controller.auth;

import java.io.IOException;
import java.util.Objects;
import java.util.Optional;

import com.lockedin.lockedin.model.dao.UserDAO;
import com.lockedin.lockedin.model.entity.user.User;

import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.stage.Stage;

/**
 * Coordinates credential validation, login, and shared authentication UI
 * helpers.
 *
 * @author LockedIn Team
 * @version 1.0
 */
public class Authentication {

    public static final double SCENE_WIDTH = 410;
    public static final double SCENE_HEIGHT = 750;

    private final UserDAO userDAO;

    /**
     * Creates an authentication helper using the application default user database.
     */
    public Authentication() {
        this(new UserDAO());
    }

    /**
     * Creates an authentication helper with the supplied user data access object.
     *
     * @param userDAO data access for login; supplied in tests with an in-memory
     *                database
     */
    public Authentication(UserDAO userDAO) {
        this.userDAO = Objects.requireNonNull(userDAO, "userDAO");
    }

        /**
     * Show alert.
     * 
     * @param type    The type.
     * @param header  The header.
     * @param content content
     */
    private static void showAlert(Alert.AlertType type, String header, String content) {
        Alert alert = new Alert(type);
        alert.setHeaderText(header);
        alert.setContentText(content);
        alert.showAndWait();
    }

        /**
     * Returns whether valid email.
     * 
     * @param email email
     * @return true if the condition holds; otherwise false.
     */
    public boolean isValidEmail(String email) {
        return email.matches("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$");
    }

        /**
     * Returns whether valid password.
     * 
     * @param password password
     * @return true if the condition holds; otherwise false.
     */
    public boolean isValidPassword(String password) {
        return password.length() >= 8
                && password.matches("^(?=.*[a-z])(?=.*[A-Z])(?=.*[^A-Za-z0-9]).{8,}$");
    }

        /**
     * Show error.
     * 
     * @param header  The header.
     * @param content content
     */
    public void showError(String header, String content) {
        showAlert(Alert.AlertType.ERROR, header, content);
    }

    /**
     * Shows an error alert with only a content message.
     *
     * @param content message body
     */
    public void showError(String content) {
        showAlert(Alert.AlertType.ERROR, null, content);
    }

    /**
     * Shows a confirmation dialog and runs {@code onConfirm} when the user chooses
     * OK.
     *
     * @param message   confirmation message
     * @param onConfirm action if confirmed
     */
    public void confirm(String message, Runnable onConfirm) {
        if (confirm(message)) {
            onConfirm.run();
        }
    }

    /**
     * Shows a confirmation dialog.
     *
     * @param message confirmation message
     * @return true if the user confirmed
     */
    public boolean confirm(String message) {
        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setHeaderText(null);
        confirm.setContentText(message);
        return confirm.showAndWait().filter(bt -> bt == ButtonType.OK).isPresent();
    }

    /**
     * Shows a yes/no confirmation dialog.
     *
     * @param message confirmation message
     * @return true if the user chose Yes
     */
    public boolean confirmYesNo(String message) {
        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION, message, ButtonType.YES, ButtonType.NO);
        confirm.setHeaderText(null);
        return confirm.showAndWait().filter(bt -> bt == ButtonType.YES).isPresent();
    }

        /**
     * Show info.
     * 
     * @param header  The header.
     * @param content content
     */
    public void showInfo(String header, String content) {
        showAlert(Alert.AlertType.INFORMATION, header, content);
    }

        /**
     * Switch scene.
     * 
     * @param sourceButton The source button.
     * @param fxmlPath     The fxml path.
     * @throws IOException If the operation fails.
     */
    public void switchScene(Button sourceButton, String fxmlPath) throws IOException {
        switchScene((Node) sourceButton, fxmlPath);
    }

        /**
     * Switch scene.
     * 
     * @param source   The source.
     * @param fxmlPath The fxml path.
     * @throws IOException If the operation fails.
     */
    public void switchScene(Node source, String fxmlPath) throws IOException {
        FXMLLoader loader = new FXMLLoader(Authentication.class.getResource(fxmlPath));
        Scene scene = new Scene(loader.load(), SCENE_WIDTH, SCENE_HEIGHT);
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
        boolean credentialsValid = user.isPresent() && userDAO.authenticate(email, password);
        return credentialsValid ? user : Optional.empty();
    }
}
