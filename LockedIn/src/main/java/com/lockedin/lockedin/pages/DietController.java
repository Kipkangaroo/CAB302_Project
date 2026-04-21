package com.lockedin.lockedin.pages;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;

public class DietController {
    //Add code to control aspects within the Diet page

    @FXML private TextField foodField;
    @FXML private TextField caloriesField;
    @FXML private TextField proteinField;
    @FXML private TextField carbsField;
    @FXML private TextField fatsField;

    @FXML private ListView<String> foodList;
    @FXML private Label totalLabel;

    private int totalCalories = 0;
    private int totalProtein = 0;
    private int totalCarbs = 0;
    private int totalFats = 0;

    @FXML private Label proteinTotalLabel;
    @FXML private Label carbsTotalLabel;
    @FXML private Label fatsTotalLabel;

    @FXML
    private void handleAddFood() {
        String food = foodField.getText();
        String caloriesText = caloriesField.getText();

        if (food.isEmpty() || caloriesText.isEmpty()) {
            return;
        }

        int calories = Integer.parseInt(caloriesText);
        int protein = Integer.parseInt(proteinField.getText());
        int carbs = Integer.parseInt(carbsField.getText());
        int fats = Integer.parseInt(fatsField.getText());

        totalCalories += calories;
        totalProtein += protein;
        totalCarbs += carbs;
        totalFats += fats;

        proteinTotalLabel.setText("Protein: " + totalProtein + " g");
        carbsTotalLabel.setText("Carbs: " + totalCarbs + " g");
        fatsTotalLabel.setText("Fats: " + totalFats + " g");
        totalLabel.setText("Total: " + totalCalories + " kcal");

        foodList.getItems().add(
                food + " - " + calories + " kcal | P: " + protein + "g C: " + carbs + "g F: " + fats + "g");

        // clear fields afte adding
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
