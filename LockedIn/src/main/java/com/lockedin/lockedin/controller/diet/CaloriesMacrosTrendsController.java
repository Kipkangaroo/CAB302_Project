package com.lockedin.lockedin.controller.diet;

import com.lockedin.lockedin.model.dao.FoodDAO;
import com.lockedin.lockedin.model.dao.UserProgressDAO;
import com.lockedin.lockedin.model.entity.user.FitnessGoal;
import com.lockedin.lockedin.model.entity.user.User;
import com.lockedin.lockedin.model.session.CurrentUser;

import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Button;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;

import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.function.ToDoubleFunction;

/**
 * JavaFX controller for the calories and macros trends screen.
 * @author LockedIn Team
 * @version 1.0
 */
public class CaloriesMacrosTrendsController {

    private static final String DIET_VIEW =
            "/com/lockedin/lockedin/pages/diet/diet-view.fxml";
    private static final DateTimeFormatter CHART_DATE_FORMAT = DateTimeFormatter.ofPattern("d/M");
    private static final int CHART_LABEL_INTERVAL = 5;

    @FXML private Button backButton;
    @FXML private Button btn7Days;
    @FXML private Button btn30Days;
    @FXML private LineChart<String, Number> calorieChart;
    @FXML private LineChart<String, Number> proteinChart;
    @FXML private LineChart<String, Number> carbsChart;
    @FXML private LineChart<String, Number> fatsChart;

    private final FoodDAO foodDAO = new FoodDAO();
    private final UserProgressDAO progressDAO = new UserProgressDAO();
    private int rangeDays = 7;

    /**
     * Initializes FXML-bound UI components after the view loads.
     */
    @FXML
    public void initialize() {
        loadCharts();
    }

    /**
     * Switches to the 7-day view.
     */
    @FXML
    public void handle7Days() {
        rangeDays = 7;
        setActive(btn7Days, btn30Days);
        loadCharts();
    }

    /**
     * Switches to the 30-day view.
     */
    @FXML
    public void handle30Days() {
        rangeDays = 30;
        setActive(btn30Days, btn7Days);
        loadCharts();
    }

    private void loadCharts() {
        int userId = CurrentUser.getId();
        User user = CurrentUser.get();
        FitnessGoal fitnessGoal = user != null ? user.getFitnessGoal() : null;
        LocalDate end = LocalDate.now();

        populateDualSeriesChart(
                calorieChart,
                end,
                date -> foodDAO.getDailyTotalByAttribute(date, "calories", userId),
                date -> progressDAO.getDailyTargetCalories(userId, date));

        populateDualSeriesChart(
                proteinChart,
                end,
                date -> foodDAO.getDailyTotalByAttribute(date, "protein", userId),
                date -> macroGoal(user, fitnessGoal, userId, date, User::getTargetProtein));

        populateDualSeriesChart(
                carbsChart,
                end,
                date -> foodDAO.getDailyTotalByAttribute(date, "carbs", userId),
                date -> macroGoal(user, fitnessGoal, userId, date, User::getTargetCarbs));

        populateDualSeriesChart(
                fatsChart,
                end,
                date -> foodDAO.getDailyTotalByAttribute(date, "fats", userId),
                date -> macroGoal(user, fitnessGoal, userId, date, User::getTargetFats));
    }

    private double macroGoal(
            User user,
            FitnessGoal fitnessGoal,
            int userId,
            LocalDate date,
            MacroTargetFn targetFn) {
        if (user == null) {
            return 0.0;
        }
        double targetCalories = progressDAO.getDailyTargetCalories(userId, date);
        return targetFn.apply(user, targetCalories, fitnessGoal);
    }

    private void populateDualSeriesChart(
            LineChart<String, Number> chart,
            LocalDate end,
            ToDoubleFunction<LocalDate> actualFn,
            ToDoubleFunction<LocalDate> goalFn) {
        chart.getData().clear();

        XYChart.Series<String, Number> actualSeries = new XYChart.Series<>();
        actualSeries.setName("Actual");
        XYChart.Series<String, Number> goalSeries = new XYChart.Series<>();
        goalSeries.setName("Goal");
        List<String> categories = new ArrayList<>();

        // Oldest date first, today last (rightmost). No dates after today.
        for (int daysAgo = rangeDays - 1; daysAgo >= 0; daysAgo--) {
            LocalDate date = end.minusDays(daysAgo);
            String category = chartCategory(date, daysAgo);
            categories.add(category);
            actualSeries.getData().add(new XYChart.Data<>(category, actualFn.applyAsDouble(date)));
            goalSeries.getData().add(new XYChart.Data<>(category, goalFn.applyAsDouble(date)));
        }

        chart.getData().add(actualSeries);
        chart.getData().add(goalSeries);
        CategoryAxis xAxis = (CategoryAxis) chart.getXAxis();
        xAxis.setCategories(FXCollections.observableArrayList(categories));
    }

    /**
     * Unique category per day (oldest → today) so the axis keeps chronological order.
     * Seven-day view labels every day; thirty-day view uses sparse labels plus
     * zero-width padding on unlabeled days so ticks do not collapse or reorder.
     */
    private String chartCategory(LocalDate date, int daysAgo) {
        if (daysAgo == 0) {
            return "Today";
        }
        if (rangeDays <= 7 || daysAgo == rangeDays - 1 || daysAgo % CHART_LABEL_INTERVAL == 0) {
            return date.format(CHART_DATE_FORMAT);
        }
        return "\u200B".repeat(daysAgo);
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

    @FunctionalInterface
    private interface MacroTargetFn {
        double apply(User user, double targetCalories, FitnessGoal fitnessGoal);
    }
}
