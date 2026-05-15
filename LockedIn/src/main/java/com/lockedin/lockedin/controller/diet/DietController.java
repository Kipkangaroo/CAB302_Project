package com.lockedin.lockedin.controller.diet;

import com.lockedin.lockedin.logic.DietLogic;
import com.lockedin.lockedin.model.dao.FoodDAO;
import com.lockedin.lockedin.model.entity.Food;
import com.lockedin.lockedin.model.entity.User;
import com.lockedin.lockedin.model.session.CurrentUser;

import com.lockedin.lockedin.service.AiModelService;
import javafx.beans.value.ChangeListener;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.FileChooser;
import javafx.stage.Window;
import java.io.File;

import java.io.IOException;
import java.nio.file.Path;
import java.time.LocalDate;
import java.util.List;
import java.util.Objects;

// JavaFX controller: handles user input, updates totals, and manages Diet page UI
public class DietController {
    private final DietLogic dietLogic = new DietLogic();
    private final FoodDAO foodDAO = new FoodDAO();
    private final int currentUserID = CurrentUser.getId();
    public DatePicker foodDatePicker;
    AiModelService apiHandler;
    private double targetCalories;
    private double targetProtein;
    private double targetCarbs;
    private double targetFats;
    @FXML
    private ProgressBar caloriesProgressBar;
    @FXML
    private ProgressBar proteinProgressBar;
    @FXML
    private ProgressBar fatProgressBar;
    @FXML
    private ProgressBar carbsProgressBar;

    // -----------------------------
    // UI ELEMENT REFERENCES (FXML)
    // -----------------------------
    @FXML
    private TextField foodField;
    @FXML
    private TextField caloriesField;
    @FXML
    private TextField proteinField;
    @FXML
    private TextField carbsField;
    @FXML
    private TextField fatsField;

    @FXML
    private ListView<Food> foodList;

    @FXML
    private Label caloriesProgressLabel;
    @FXML
    private Label proteinProgressLabel;
    @FXML
    private Label carbsProgressLabel;
    @FXML
    private Label fatsProgressLabel;
    @FXML
    private Label inputErrorLabel;

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
    private void initialize() throws IOException {
        foodDatePicker.setValue(LocalDate.now());
        foodDatePicker.valueProperty().addListener(onDateSelected());
        this.apiHandler = new AiModelService(currentUserID);
        User currentUser = CurrentUser.get();
        targetCalories = currentUser.getTargetCalories();
        targetProtein = currentUser.getTargetProtein();
        targetCarbs = currentUser.getTargetCarbs();
        targetFats = currentUser.getTargetFats();
        setFoodsOnList(LocalDate.now());
        updateGUI(foodDatePicker.getValue());
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
        foodDAO.addFood(food, foodDatePicker.getValue());
        updateGUI(foodDatePicker.getValue());
        foodList.getItems().add(food);
        clearInputs();
    }

    private void setFoodsOnList(LocalDate date) {
        List<Food> currentFood = foodDAO.getFoodsByDate(date, currentUserID);
        foodList.getItems().setAll(currentFood);
        updateGUI(date);
    }

    private ChangeListener<LocalDate> onDateSelected() {
        return (obs, oldDate, newDate) -> {
            if (newDate != null) {
                setFoodsOnList(newDate);
            }
        };
    }

    private void updateGUI(LocalDate date) {
        updateTotals(date);
        refreshTotalsLabels();
        updateProgressBars();
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
        Alert confirm =
                new Alert(
                        Alert.AlertType.CONFIRMATION,
                        "Remove \"" + selectedFood.getName() + "\" from this day?",
                        ButtonType.YES,
                        ButtonType.NO);
        confirm.setTitle("Remove food");
        confirm.setHeaderText(null);
        confirm.initOwner(foodList.getScene().getWindow());
        confirm.showAndWait()
                .ifPresent(
                        bt -> {
                            if (bt == ButtonType.YES) {
                                foodDAO.removeFood(selectedFood.getId());
                                foodList.getItems().remove(selectedFood);
                                updateGUI(foodDatePicker.getValue());
                            }
                        });
    }

    @FXML
    private void handleAiLogoClick() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Choose your meal photo");
        fileChooser.getExtensionFilters().add(
                new FileChooser.ExtensionFilter("Image Files", "*.png", "*.jpg", "*.jpeg"));
        File picturesDir = new File(System.getProperty("user.home"), "Desktop");
        if (picturesDir.exists()) {
            fileChooser.setInitialDirectory(picturesDir);
        }
        Window window = foodList.getScene().getWindow(); // any node from this scene works
        File selectedFile = fileChooser.showOpenDialog(window);
        if (selectedFile != null) {
            Path imagePath = Path.of(selectedFile.getAbsolutePath());
            Task<Food> task = apiHandler.createAnalyzeImageTask(imagePath, foodDatePicker.getValue());
            task.setOnSucceeded(e -> {
                Food food = task.getValue();
                if (food != null) {
                    foodDAO.addFood(food, food.getDate());
                    if (Objects.equals(foodDatePicker.getValue(), food.getDate())) {
                        foodList.getItems().add(food);
                    }
                    updateGUI(foodDatePicker.getValue());
                }
            });
            task.setOnFailed(e -> task.getException().printStackTrace());
            Thread worker = new Thread(task, "ai-food-image");
            worker.setDaemon(true);
            worker.start();
        }
    }

    private void updateTotals(LocalDate date) {
        totalCalories = foodDAO.getDailyTotalByAttribute(date, "calories", currentUserID);
        totalProtein = foodDAO.getDailyTotalByAttribute(date, "protein", currentUserID);
        totalCarbs = foodDAO.getDailyTotalByAttribute(date, "carbs", currentUserID);
        totalFats = foodDAO.getDailyTotalByAttribute(date, "fats", currentUserID);
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
