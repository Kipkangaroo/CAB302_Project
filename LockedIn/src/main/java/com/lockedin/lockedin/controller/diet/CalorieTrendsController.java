package com.lockedin.lockedin.controller.diet;

import com.lockedin.lockedin.model.dao.FoodDAO;
import com.lockedin.lockedin.model.dao.UserProgressDAO;
import com.lockedin.lockedin.model.session.CurrentUser;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Button;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;

import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Map;

/**
 * JavaFX controller for the calorie trends screen.
 * @author LockedIn Team
 * @version 1.0
 */
public class CalorieTrendsController {

    private static final String DIET_VIEW =
            "/com/lockedin/lockedin/pages/diet/diet-view.fxml";
    private static final DateTimeFormatter AXIS_FMT = DateTimeFormatter.ofPattern("d/M");

    @FXML private Button backButton;
    @FXML private Button btn7Days;
    @FXML private Button btn30Days;
    @FXML private LineChart<String, Number> calorieChart;

    private final FoodDAO foodDAO = new FoodDAO();
    private final UserProgressDAO progressDAO = new UserProgressDAO();
    private int rangeDays = 7;

    /**
     * Initializes FXML-bound UI components after the view loads.
     */
    @FXML
    public void initialize() {
        loadChart();
    }

    /**
     * Switches to the 7-day view.
     */
    @FXML
    public void handle7Days() {
        rangeDays = 7;
        setActive(btn7Days, btn30Days);
        loadChart();
    }

    /**
     * Switches to the 30-day view.
     */
    @FXML
    public void handle30Days() {
        rangeDays = 30;
        setActive(btn30Days, btn7Days);
        loadChart();
    }

    private void loadChart() {
        calorieChart.getData().clear();

        LocalDate end = LocalDate.now();
        LocalDate start = end.minusDays(rangeDays - 1);

        int userId = CurrentUser.getId();
        Map<LocalDate, Double> actuals = foodDAO.getDailyTotalsForRange(start, end, userId);

        XYChart.Series<String, Number> actualSeries = new XYChart.Series<>();
        actualSeries.setName("Actual");
        XYChart.Series<String, Number> goalSeries = new XYChart.Series<>();
        goalSeries.setName("Goal");

        for (int i = rangeDays - 1; i >= 0; i--) {
            LocalDate date = end.minusDays(i);
            String label = date.format(AXIS_FMT);
            double actual = actuals.getOrDefault(date, 0.0);
            double goal = progressDAO.getDailyTargetCalories(userId, date);
            actualSeries.getData().add(new XYChart.Data<>(label, actual));
            goalSeries.getData().add(new XYChart.Data<>(label, goal));
        }

        calorieChart.getData().addAll(actualSeries, goalSeries);
    }

    private void setActive(Button active, Button inactive) {
        active.getStyleClass().remove("diet-secondary-btn");
        if (!active.getStyleClass().contains("primary-btn")) {
            active.getStyleClass().add("primary-btn");
        }
        inactive.getStyleClass().remove("primary-btn");
        if (!inactive.getStyleClass().contains("diet-secondary-btn")) {
            inactive.getStyleClass().add("diet-secondary-btn");
        }
    }

    /**
     * Navigates back to the diet page.
     */
    @FXML
    public void handleBack() {
        try {
            Pane page = FXMLLoader.load(getClass().getResource(DIET_VIEW));
            StackPane pc = (StackPane) backButton.getScene().lookup("#pageContainer");
            if (pc != null) {
                pc.getChildren().setAll(page);
            }
        } catch (IOException e) {
            throw new RuntimeException("Failed to navigate back to diet", e);
        }
    }
}
