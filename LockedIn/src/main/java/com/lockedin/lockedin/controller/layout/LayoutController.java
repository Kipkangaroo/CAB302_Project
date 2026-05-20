package com.lockedin.lockedin.controller.layout;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;

import java.io.IOException;
import java.util.Objects;

/**
 * JavaFX controller for the layout screen.
 * @author LockedIn Team
 * @version 1.0
 */
public class LayoutController {

    /** The currently logged-in user, retrieved after login */

    public static final String WORKOUT_VIEW = "/com/lockedin/lockedin/pages/workout/workout-view.fxml";
    public static final String DIET_VIEW = "/com/lockedin/lockedin/pages/diet/diet-view.fxml";
    public static final String PROFILE_VIEW = "/com/lockedin/lockedin/pages/profile/profile-view.fxml";
    private static final String INACTIVE = "-fx-scale-x: 1; -fx-scale-y: 1; -fx-opacity: 1;";
    private static final String ACTIVE = "-fx-scale-x: 0.95; -fx-scale-y: 0.95; -fx-opacity: 0.8; -fx-border-color: transparent"
            + " transparent #378ADD transparent;-fx-border-width: 0 0 2 0;";
    @FXML
    public StackPane pageContainer;
    @FXML
    public Button btnWorkout;
    @FXML
    public Button btnDiet;
    @FXML
    public Button btnProfile;
    /**
     * Initializes FXML-bound UI components after the view loads.
     */

    @FXML
    public void initialize() {
        showWorkoutPage();
    }
    /**
     * Performs show workout page.
     */

    @FXML
    private void showWorkoutPage() {
        loadPage(WORKOUT_VIEW);
        setActive(btnWorkout);
    }
    /**
     * Performs show diet page.
     */

    @FXML
    private void showDietPage() {
        loadPage(DIET_VIEW);
        setActive(btnDiet);
    }
    /**
     * Performs show profile page.
     */

    @FXML
    private void showProfilePage() {
        loadPage(PROFILE_VIEW);
        setActive(btnProfile);
    }

    /**
     * Performs load page.
     * @param page The page.
     */
    private void loadPage(String page) {
        try {
            Pane appPage = FXMLLoader.load(Objects.requireNonNull(getClass().getResource(page)));
            pageContainer.getChildren().setAll(appPage);
        } catch (IOException e) {
            throw new RuntimeException("Failed to load page: " + page, e);
        }
    }

    /**
     * Sets the active.
     * @param active The active.
     */
    private void setActive(Button active) {
        btnWorkout.setStyle(INACTIVE);
        btnDiet.setStyle(INACTIVE);
        btnProfile.setStyle(INACTIVE);
        active.setStyle(ACTIVE);
    }
}
