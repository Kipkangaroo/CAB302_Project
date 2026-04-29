package com.lockedin.lockedin.controller.diet;

import com.lockedin.lockedin.logic.DietLogic;
import com.lockedin.lockedin.model.dao.FoodDAO;
import com.lockedin.lockedin.model.entity.Food;
import com.lockedin.lockedin.model.session.CurrentUser;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;

import java.util.Date;
import java.util.List;

// JavaFX controller: handles user input, updates totals, and manages Diet page UI
public class DietController {
    private final FoodDAO foodDAO = new FoodDAO();
    private final int currentUserID = CurrentUser.getId();

    // -----------------------------
    // UI ELEMENT REFERENCES (FXML)
    // -----------------------------
    @FXML private TextField foodField;
    @FXML private TextField caloriesField;
    @FXML private TextField proteinField;
    @FXML private TextField carbsField;
    @FXML private TextField fatsField;

    @FXML private ListView<Food> foodList;
    @FXML private Label totalLabel;

    @FXML private Label proteinTotalLabel;
    @FXML private Label carbsTotalLabel;
    @FXML private Label fatsTotalLabel;

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
        List<Food> todaysFoods = foodDAO.getFoodsByDate(new Date(), currentUserID);
        foodList.getItems().setAll(todaysFoods);
        updateTotals();
        refreshTotalsLabels();
    }

    @FXML
    private void handleAddFood() {
        String name = foodField.getText();
        int calories = Integer.parseInt(caloriesField.getText());
        int protein = Integer.parseInt(proteinField.getText());
        int carbs = Integer.parseInt(carbsField.getText());
        int fats = Integer.parseInt(fatsField.getText());
        Food food = new Food(0,currentUserID, name, calories, protein, carbs, fats);
        foodDAO.addFood(food);
        updateTotals();
        refreshTotalsLabels();
        foodList.getItems().add(food);
        clearInputs();
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
    }

    private void updateTotals() {
        totalCalories = foodDAO.getDailyTotalByAttribute(new Date(), "calories", currentUserID);
        totalProtein = foodDAO.getDailyTotalByAttribute(new Date(), "protein", currentUserID);
        totalCarbs = foodDAO.getDailyTotalByAttribute(new Date(), "carbs", currentUserID);
        totalFats = foodDAO.getDailyTotalByAttribute(new Date(), "fats", currentUserID);
    }

    private void refreshTotalsLabels() {
        totalLabel.setText(String.format("Total: %.0f kcal", totalCalories));
        proteinTotalLabel.setText(String.format("Protein: %.0f g", totalProtein));
        carbsTotalLabel.setText(String.format("Carbs: %.0f g", totalCarbs));
        fatsTotalLabel.setText(String.format("Fats: %.0f g", totalFats));
    }

    private void clearInputs() {
        foodField.clear();
        caloriesField.clear();
        proteinField.clear();
        carbsField.clear();
        fatsField.clear();
    }

}