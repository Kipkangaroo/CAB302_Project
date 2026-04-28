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

    }

    public void handleReset(ActionEvent actionEvent) {
    }
}