package com.lockedin.lockedin;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;

import java.io.IOException;
import java.util.Objects;

public class MainController {
    @FXML
    public StackPane pageContainer;
    @FXML
    public Button btnWorkout;
    @FXML
    public Button btnDiet;
    @FXML
    public Button btnProfile;

    private static final String ACTIVE   = "-fx-scale-x: 0.95; -fx-scale-y: 0.95; -fx-opacity: 0.8;";
    private static final String INACTIVE = "-fx-scale-x: 1; -fx-scale-y: 1; -fx-opacity: 1;";

    @FXML
    public void initialize() {
        //app opens to workout page (after log in)
        showWorkoutPage();
    }

    //handlers to switch between views/pages (on nav bar)
    @FXML
    private void showWorkoutPage() {
        loadPage("pages/workout-view.fxml");
        setActive(btnWorkout);
    }

    @FXML
    private void showDietPage() {
        loadPage("pages/diet-view.fxml");
        setActive(btnDiet);
    }

    @FXML
    private void showProfilePage() {
        loadPage("pages/profile-view.fxml");
        setActive(btnProfile);
    }

    //loads given page
    private void loadPage(String page) {
        try {
            Pane appPage = FXMLLoader.load(Objects.requireNonNull(getClass().getResource(page)));
            pageContainer.getChildren().setAll(appPage);
        } catch (IOException e) {
        }
    }

    private void setActive(Button active) {
        btnWorkout.setStyle(INACTIVE);
        btnDiet.setStyle(INACTIVE);
        btnProfile.setStyle(INACTIVE);
        active.setStyle(ACTIVE);
    }
}
