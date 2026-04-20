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

    @FXML
    private void handleAddFood() {
        String food = foodField.getText();
        String caloriesText = caloriesField.getText();

        if (food.isEmpty() || caloriesText.isEmpty()) {
            return;
        }

        int calories = Integer.parseInt(caloriesText);

        totalCalories += calories;
        totalLabel.setText("Total: " + totalCalories + " kcal");

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
