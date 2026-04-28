package com.lockedin.lockedin.controller.diet;

import com.lockedin.lockedin.logic.DietLogic;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;

// JavaFX controller: handles user input, updates totals, and manages Diet page UI
public class DietController {

    // -----------------------------
    // UI ELEMENT REFERENCES (FXML)
    // -----------------------------
    @FXML private TextField foodField;
    @FXML private TextField caloriesField;
    @FXML private TextField proteinField;
    @FXML private TextField carbsField;
    @FXML private TextField fatsField;

    @FXML private ListView<String> foodList;
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
    // LOGIC LAYER (TDD)
    // -----------------------------
    // DietLogic handles validation + arithmetic so it can be unit tested
    private DietLogic logic = new DietLogic();

    // -----------------------------
    // ADD FOOD BUTTON HANDLER
    // -----------------------------
    @FXML
    private void handleAddFood() {

        // Read user input from text fields
        String food = foodField.getText();
        String caloriesText = caloriesField.getText();
        String proteinText = proteinField.getText();
        String carbsText = carbsField.getText();
        String fatsText = fatsField.getText();

        // Validate: food name must not be empty AND all numbers must be valid
        if (food.isEmpty() ||
                !logic.isValidNumber(caloriesText) ||
                !logic.isValidNumber(proteinText) ||
                !logic.isValidNumber(carbsText) ||
                !logic.isValidNumber(fatsText))
        {
            // If validation fails, do nothing (prevents crashes)
            return;
        }

        // Convert validated text input into numeric values
        double calories = Double.parseDouble(caloriesText);
        double protein = Double.parseDouble(proteinText);
        double carbs = Double.parseDouble(carbsText);
        double fats = Double.parseDouble(fatsText);

        // Update totals using logic class (supports TDD)
        totalCalories = logic.add(totalCalories, calories);
        totalProtein = logic.add(totalProtein, protein);
        totalCarbs = logic.add(totalCarbs, carbs);
        totalFats = logic.add(totalFats, fats);

        // Update UI labels to reflect new totals
        proteinTotalLabel.setText("Protein: " + totalProtein + " g");
        carbsTotalLabel.setText("Carbs: " + totalCarbs + " g");
        fatsTotalLabel.setText("Fats: " + totalFats + " g");
        totalLabel.setText("Total: " + totalCalories + " kcal");

        // Add formatted entry to the list view
        foodList.getItems().add(
                food + " - " + calories + " kcal | P: " + protein + "g C: " + carbs + "g F: " + fats + "g"
        );

        // Clear input fields for next entry
        foodField.clear();
        caloriesField.clear();
        proteinField.clear();
        carbsField.clear();
        fatsField.clear();
    }

    public void handleReset(ActionEvent actionEvent) {
    }
}