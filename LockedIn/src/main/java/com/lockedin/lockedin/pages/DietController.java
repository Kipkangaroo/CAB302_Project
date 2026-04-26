package com.lockedin.lockedin.pages;

import com.lockedin.lockedin.logic.DietLogic;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;

public class DietController {
    // Add code to control aspects within the Diet page

    @FXML private TextField foodField;
    @FXML private TextField caloriesField;
    @FXML private TextField proteinField;
    @FXML private TextField carbsField;
    @FXML private TextField fatsField;

    @FXML private ListView<String> foodList;
    @FXML private Label totalLabel;

    private double totalCalories = 0;
    private double totalProtein = 0;
    private double totalCarbs = 0;
    private double totalFats = 0;

    @FXML private Label proteinTotalLabel;
    @FXML private Label carbsTotalLabel;
    @FXML private Label fatsTotalLabel;

    // Logic class for TDD

    private DietLogic logic = new DietLogic();

    @FXML
    private void handleAddFood() {
        String food = foodField.getText();
        String caloriesText = caloriesField.getText();
        String proteinText = proteinField.getText();
        String carbsText = carbsField.getText();
        String fatsText = fatsField.getText();

        if (food.isEmpty() || !logic.isValidNumber(caloriesText) || !logic.isValidNumber(proteinText) ||
            !logic.isValidNumber(carbsText) || !logic.isValidNumber(fatsText))
        {
            return;
        }

        double calories = Double.parseDouble(caloriesText);
        double protein = Double.parseDouble(proteinText);
        double carbs = Double.parseDouble(carbsText);
        double fats = Double.parseDouble(fatsText);

        totalCalories = logic.add(totalCalories, calories);
        totalProtein = logic.add(totalProtein, protein);
        totalCarbs = logic.add(totalCarbs, carbs);
        totalFats = logic.add(totalFats, fats);

        proteinTotalLabel.setText("Protein: " + totalProtein + " g");
        carbsTotalLabel.setText("Carbs: " + totalCarbs + " g");
        fatsTotalLabel.setText("Fats: " + totalFats + " g");
        totalLabel.setText("Total: " + totalCalories + " kcal");

        foodList.getItems().add(
                food + " - " + calories + " kcal | P: " + protein + "g C: " + carbs + "g F: " + fats + "g");

        // clear fields after adding
        foodField.clear();
        caloriesField.clear();
        proteinField.clear();
        carbsField.clear();
        fatsField.clear();

    }

    @FXML
    private void handleReset() {
        foodField.clear();
        caloriesField.clear();
        proteinField.clear();
        carbsField.clear();
        fatsField.clear();
    }

}
