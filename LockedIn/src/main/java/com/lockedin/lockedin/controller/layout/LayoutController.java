package com.lockedin.lockedin.controller.layout;

import com.lockedin.lockedin.controller.navigation.PageNavigator;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.layout.StackPane;

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
    @FXML
    public StackPane pageContainer;
    @FXML
    public Button workoutNavButton;
    @FXML
    public Button dietNavButton;
    @FXML
    public Button profileNavButton;
    /**
     * Initializes FXML-bound UI components after the view loads.
     */

    @FXML
    public void initialize() {
        showWorkoutPage();
    }
        /**
     * Show workout page.
     */

    @FXML
    private void showWorkoutPage() {
        loadPage(WORKOUT_VIEW);
        setActive(workoutNavButton);
    }
        /**
     * Show diet page.
     */

    @FXML
    private void showDietPage() {
        loadPage(DIET_VIEW);
        setActive(dietNavButton);
    }
        /**
     * Show profile page.
     */

    @FXML
    private void showProfilePage() {
        loadPage(PROFILE_VIEW);
        setActive(profileNavButton);
    }

        /**
     * Load page.
     * @param page page
     */
    private void loadPage(String page) {
        PageNavigator.loadInto(pageContainer, getClass(), page);
    }

        /**
     * Sets the active.
     * @param active active
     */
    private void setActive(Button active) {
        final String inactive = "-fx-scale-x: 1; -fx-scale-y: 1; -fx-opacity: 1;";
        final String activeStyle = "-fx-scale-x: 0.95; -fx-scale-y: 0.95; -fx-opacity: 0.8; -fx-border-color: transparent"
                + " transparent #378ADD transparent;-fx-border-width: 0 0 2 0;";
        workoutNavButton.setStyle(inactive);
        dietNavButton.setStyle(inactive);
        profileNavButton.setStyle(inactive);
        active.setStyle(activeStyle);
    }
}
