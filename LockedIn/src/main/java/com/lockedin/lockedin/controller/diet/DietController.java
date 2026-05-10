package com.lockedin.lockedin.controller.diet;

import com.lockedin.lockedin.logic.DietLogic;
import com.lockedin.lockedin.model.dao.FoodDAO;
import com.lockedin.lockedin.model.entity.Food;
import com.lockedin.lockedin.model.entity.User;
import com.lockedin.lockedin.model.session.CurrentUser;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.stage.FileChooser;
import javafx.stage.Window;
import java.io.File;

import java.util.Date;
import java.util.List;

// JavaFX controller: handles user input, updates totals, and manages Diet page UI
public class DietController {
    private final DietLogic dietLogic = new DietLogic();
    private final FoodDAO foodDAO = new FoodDAO();
    private final int currentUserID = CurrentUser.getId();
    private double targetCalories;
    private double targetProtein;
    private double targetCarbs;
    private double targetFats;
    @FXML private ProgressBar caloriesProgressBar;
    @FXML private ProgressBar proteinProgressBar;
    @FXML private ProgressBar fatProgressBar;
    @FXML private ProgressBar carbsProgressBar;

    // -----------------------------
    // UI ELEMENT REFERENCES (FXML)
    // -----------------------------
    @FXML private TextField foodField;
    @FXML private TextField caloriesField;
    @FXML private TextField proteinField;
    @FXML private TextField carbsField;
    @FXML private TextField fatsField;

    @FXML private ListView<Food> foodList;

    @FXML private Label caloriesProgressLabel;
    @FXML private Label proteinProgressLabel;
    @FXML private Label carbsProgressLabel;
    @FXML private Label fatsProgressLabel;
    @FXML private Label inputErrorLabel;

    // -----------------------------
    // ACCUMULATED TOTALS
    // -----------------------------
    // These track the running totals for the day
    private double totalCalories = 0;
    private double totalProtein = 0;
    private double totalCarbs = 0;
    private double totalFats = 0;

    // -----------------------------
    // ADD FOOD BUTTON HANDLER
    // -----------------------------
    @FXML
    private void initialize() {
        User currentUser = CurrentUser.get();
        targetCalories = currentUser.getTargetCalories();
        targetProtein = currentUser.getTargetProtein();
        targetCarbs = currentUser.getTargetCarbs();
        targetFats = currentUser.getTargetFats();
        List<Food> todaysFoods = foodDAO.getFoodsByDate(new Date(), currentUserID);
        foodList.getItems().setAll(todaysFoods);
        updateTotals();
        refreshTotalsLabels();
        updateProgressBars();
    }

    @FXML
    private void handleAddFood() {
        inputErrorLabel.setVisible(false);
        inputErrorLabel.setManaged(false);

        String name = foodField.getText().trim();
        if (name.isEmpty()) {
            showInputError("Food name cannot be empty.");
            return;
        }

        String caloriesText = caloriesField.getText().trim();
        String proteinText = proteinField.getText().trim();
        String carbsText = carbsField.getText().trim();
        String fatsText = fatsField.getText().trim();

        if (!dietLogic.isValidNumber(caloriesText)
                || !dietLogic.isValidNumber(proteinText)
                || !dietLogic.isValidNumber(carbsText)
                || !dietLogic.isValidNumber(fatsText)) {
            showInputError("Calories, protein, carbs, and fats must be non-negative numbers.");
            return;
        }

        int calories, protein, carbs, fats;
        try {
            calories = Integer.parseInt(caloriesText);
            protein = Integer.parseInt(proteinText);
            carbs = Integer.parseInt(carbsText);
            fats = Integer.parseInt(fatsText);
        } catch (NumberFormatException e) {
            showInputError("Calories, protein, carbs, and fats must be whole numbers.");
            return;
        }

        Food food = new Food(0, currentUserID, name, calories, protein, carbs, fats);
        foodDAO.addFood(food);
        updateTotals();
        refreshTotalsLabels();
        updateProgressBars();
        foodList.getItems().add(food);
        clearInputs();
    }

    private void showInputError(String message) {
        inputErrorLabel.setText(message);
        inputErrorLabel.setVisible(true);
        inputErrorLabel.setManaged(true);
    }

    @FXML
    private void handleReset(ActionEvent actionEvent) {
        clearInputs();
    }

    @FXML
    private void handleRemoveFood(ActionEvent actionEvent) {
        Food selectedFood = foodList.getSelectionModel().getSelectedItem();
        if (selectedFood == null) {
            return;
        }
        foodDAO.removeFood(selectedFood.getId());
        foodList.getItems().remove(selectedFood);
        updateTotals();
        refreshTotalsLabels();
        updateProgressBars();
    }

    @FXML
    private void handleAiLogoClick() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Choose your meal photo");
        fileChooser.getExtensionFilters().add(
                new FileChooser.ExtensionFilter("Image Files", "*.png", "*.jpg", "*.jpeg", "*.gif", "*.webp"));
        File picturesDir = new File(System.getProperty("user.home"), "Desktop");
        if (picturesDir.exists()) {
            fileChooser.setInitialDirectory(picturesDir);
        }
        Window window = foodList.getScene().getWindow(); // any node from this scene works
        File selectedFile = fileChooser.showOpenDialog(window);
        if (selectedFile != null) {
            System.out.println("Selected: " + selectedFile.getAbsolutePath());
        }
    }

    private void updateTotals() {
        totalCalories = foodDAO.getDailyTotalByAttribute(new Date(), "calories", currentUserID);
        totalProtein = foodDAO.getDailyTotalByAttribute(new Date(), "protein", currentUserID);
        totalCarbs = foodDAO.getDailyTotalByAttribute(new Date(), "carbs", currentUserID);
        totalFats = foodDAO.getDailyTotalByAttribute(new Date(), "fats", currentUserID);
    }

    private void refreshTotalsLabels() {
        caloriesProgressLabel.setText(
                String.format("%.0f/%.0fkcal", totalCalories, targetCalories));
        proteinProgressLabel.setText(
                String.format("%.0f/%.0fg protein", totalProtein, targetProtein));
        carbsProgressLabel.setText(String.format("%.0f/%.0fg carbs", totalCarbs, targetCarbs));
        fatsProgressLabel.setText(String.format("%.0f/%.0fg fats", totalFats, targetFats));
    }

    private void updateProgressBars() {
        caloriesProgressBar.setProgress(clamp(totalCalories / targetCalories));
        proteinProgressBar.setProgress(clamp(totalProtein / targetProtein));
        carbsProgressBar.setProgress(clamp(totalCarbs / targetCarbs));
        fatProgressBar.setProgress(clamp(totalFats / targetFats));
    }

    private double clamp(double value) {
        if (Double.isNaN(value) || Double.isInfinite(value)) {
            return 0;
        }
        return Math.max(0, Math.min(1, value));
    }

    private void clearInputs() {
        foodField.clear();
        caloriesField.clear();
        proteinField.clear();
        carbsField.clear();
        fatsField.clear();
        inputErrorLabel.setVisible(false);
        inputErrorLabel.setManaged(false);
    }
}
