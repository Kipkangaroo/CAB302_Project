package com.lockedin.lockedin.controller.profile;

import com.lockedin.lockedin.logic.ProgressCalculator;
import com.lockedin.lockedin.model.dao.FoodDAO;
import com.lockedin.lockedin.model.dao.UserProgressDAO;
import com.lockedin.lockedin.model.dao.WorkoutRoutineDAO;
import com.lockedin.lockedin.model.entity.user.FitnessGoal;
import com.lockedin.lockedin.model.entity.user.UserProgress;
import com.lockedin.lockedin.model.session.CurrentUser;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;

import java.io.IOException;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.List;

/**
 * JavaFX controller for the goals progress screen.
 * @author LockedIn Team
 * @version 1.0
 */
public class ProgressController {

    private static final String PROFILE_VIEW =
            "/com/lockedin/lockedin/pages/profile/profile-view.fxml";
    private static final int SESSION_TARGET = 3;
    private static final String GREEN_BAR = "-fx-accent: #4caf50;";

    @FXML private Button backButton;
    @FXML private ProgressBar sessionsBar;
    @FXML private Label sessionsLabel;
    @FXML private ProgressBar caloriesBar;
    @FXML private Label caloriesLabel;
    @FXML private ProgressBar weightBar;
    @FXML private Label weightLabel;

    private final WorkoutRoutineDAO routineDAO = new WorkoutRoutineDAO();
    private final FoodDAO foodDAO = new FoodDAO();
    private final UserProgressDAO progressDAO = new UserProgressDAO();

    /**
     * Initializes FXML-bound UI components after the view loads.
     */
    @FXML
    public void initialize() {
        int userId = CurrentUser.getId();
        loadSessionsSection(userId);
        loadCaloriesSection(userId);
        loadWeightSection(userId);
    }

    private void loadSessionsSection(int userId) {
        LocalDate monday = LocalDate.now().with(DayOfWeek.MONDAY);
        LocalDate today = LocalDate.now();
        int actual = routineDAO.getCompletedWorkoutsByUserBetween(userId, monday, today).size();
        double pct = ProgressCalculator.calculatePercentage(actual, SESSION_TARGET);
        sessionsBar.setProgress(pct / 100.0);
        styleBar(sessionsBar, pct);
        sessionsLabel.setText(
                String.format("%d / %d sessions (%.0f%%)", actual, SESSION_TARGET, pct));
    }

    private void loadCaloriesSection(int userId) {
        LocalDate today = LocalDate.now();
        double actual = foodDAO.getDailyTotalByAttribute(today, "calories", userId);
        double target = progressDAO.getDailyTargetCalories(userId, today);
        double pct = ProgressCalculator.calculatePercentage(actual, target);
        caloriesBar.setProgress(pct / 100.0);
        styleBar(caloriesBar, pct);
        caloriesLabel.setText(
                String.format("%.0f / %.0f kcal (%.0f%%)", actual, target, pct));
    }

    private void loadWeightSection(int userId) {
        List<UserProgress> history = progressDAO.getUserProgressHistory(userId);

        if (history.isEmpty()) {
            weightBar.setProgress(0);
            weightLabel.setText("No weight data recorded yet");
            return;
        }

        // getUserProgressHistory returns DESC order: index 0 = newest, last index = oldest
        double currentWeight = history.get(0).getWeight();
        double startWeight = history.get(history.size() - 1).getWeight();
        FitnessGoal goal = history.get(0).getFitnessGoal();
        if (goal == null) goal = CurrentUser.get().getFitnessGoal();
        if (goal == null) goal = FitnessGoal.MAINTAIN_FITNESS;

        double actual, goalKg, pct;
        String labelText;

        switch (goal) {
            case LOSE_WEIGHT -> {
                goalKg = 5.0;
                actual = Math.max(0.0, startWeight - currentWeight);
                pct = ProgressCalculator.calculatePercentage(actual, goalKg);
                labelText = String.format("Lost %.1f / %.1f kg (%.0f%%)", actual, goalKg, pct);
            }
            case BUILD_MUSCLE -> {
                goalKg = 3.0;
                actual = Math.max(0.0, currentWeight - startWeight);
                pct = ProgressCalculator.calculatePercentage(actual, goalKg);
                labelText = String.format("Gained %.1f / %.1f kg (%.0f%%)", actual, goalKg, pct);
            }
            default -> { // MAINTAIN_FITNESS
                pct = 100.0;
                labelText = String.format("Maintaining at %.1f kg", currentWeight);
            }
        }

        weightBar.setProgress(pct / 100.0);
        styleBar(weightBar, pct);
        weightLabel.setText(labelText);
    }

    private void styleBar(ProgressBar bar, double pct) {
        bar.setStyle(pct >= 100.0 ? GREEN_BAR : "");
    }

    /**
     * Navigates back to the profile screen.
     */
    @FXML
    public void handleBack() {
        try {
            Pane page = FXMLLoader.load(getClass().getResource(PROFILE_VIEW));
            StackPane pc = (StackPane) backButton.getScene().lookup("#pageContainer");
            if (pc != null) {
                pc.getChildren().setAll(page);
            }
        } catch (IOException e) {
            throw new RuntimeException("Failed to navigate back to profile", e);
        }
    }
}
