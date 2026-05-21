package com.lockedin.lockedin.controller.diet;

import com.lockedin.lockedin.logic.DietLogic;
import com.lockedin.lockedin.model.dao.FoodDAO;
import com.lockedin.lockedin.model.dao.UserProgressDAO;
import com.lockedin.lockedin.model.entity.diet.Food;
import com.lockedin.lockedin.model.entity.user.FitnessGoal;
import com.lockedin.lockedin.model.entity.user.User;
import com.lockedin.lockedin.model.session.CurrentUser;

import com.lockedin.lockedin.service.AiModelService;
import javafx.beans.value.ChangeListener;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.stage.FileChooser;
import javafx.stage.Window;
import java.io.File;

import java.io.IOException;
import java.nio.file.Path;
import java.time.LocalDate;
import java.util.List;
import java.util.Objects;

// JavaFX controller: handles user input, updates totals, and manages Diet page UI
/**
 * JavaFX controller for the diet screen.
 * @author LockedIn Team
 * @version 1.0
 */
public class DietController {

    private static final String CALORIE_TRENDS_VIEW =
            "/com/lockedin/lockedin/pages/diet/calorie-trends-view.fxml";
    private final DietLogic dietLogic = new DietLogic();
    private final FoodDAO foodDAO = new FoodDAO();
    private final int currentUserID = CurrentUser.getId();
    public DatePicker foodDatePicker;
    AiModelService apiHandler;
    private User currentUser;
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
    /**
     * Initializes FXML-bound UI components after the view loads.
     * @throws IOException If the operation fails.
     */
    @FXML
    private void initialize() throws IOException {
        foodDatePicker.setValue(LocalDate.now());
        foodDatePicker.valueProperty().addListener(onDateSelected());
        this.apiHandler = new AiModelService(currentUserID);
        this.currentUser = CurrentUser.get();
        setFoodsOnList(LocalDate.now());
        foodList.setOnMouseClicked(event -> {
            if (event.getClickCount() == 2) {
                Food selected = foodList.getSelectionModel().getSelectedItem();
                if (selected != null) {
                    showEditFoodDialog(selected);
                }
            }
        });
    }
    /**
     * Performs handle add food.
     */

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

    /**
     * Sets the foods on list.
     * @param date The date.
     */
    private void setFoodsOnList(LocalDate date) {
        List<Food> currentFood = foodDAO.getFoodsByDate(date, currentUserID);
        foodList.getItems().setAll(currentFood);
        updateGUI(date);
    }

    /**
     * Handles the date selected UI action.
     * @return A list of matching records.
     */
    private ChangeListener<LocalDate> onDateSelected() {
        return (obs, oldDate, newDate) -> {
            if (newDate != null) {
                setFoodsOnList(newDate);
            }
        };
    }

    /**
     * Performs update gui.
     * @param date The date.
     */
    private void updateGUI(LocalDate date) {
        if (date != null && currentUser != null) {
            targetCalories = new UserProgressDAO().getDailyTargetCalories(currentUserID, date);
            FitnessGoal fitnessGoal = currentUser.getFitnessGoal();
            targetProtein = currentUser.getTargetProtein(targetCalories, fitnessGoal);
            targetCarbs = currentUser.getTargetCarbs(targetCalories, fitnessGoal);
            targetFats = currentUser.getTargetFats(targetCalories, fitnessGoal);
        }
        updateTotals(date);
        refreshTotalsLabels();
        updateProgressBars();
    }

    /**
     * Performs show input error.
     * @param message The message.
     */
    private void showInputError(String message) {
        inputErrorLabel.setText(message);
        inputErrorLabel.setVisible(true);
        inputErrorLabel.setManaged(true);
    }
    /**
     * Performs handle reset.
     * @param actionEvent The action event.
     */

    @FXML
    private void handleReset(ActionEvent actionEvent) {
        clearInputs();
    }
    /**
     * Opens an edit dialog pre-populated with the selected food's current values.
     * On confirm, persists the changes and refreshes the list and daily totals.
     * @param food The food to edit.
     */
    private void showEditFoodDialog(Food food) {
        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.setTitle("Edit Food");
        dialog.setHeaderText(food.getName());
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

        TextField nameInput = new TextField(food.getName());
        TextField calInput = new TextField(String.valueOf(food.getCalories()));
        TextField proInput = new TextField(String.valueOf(food.getProtein()));
        TextField carbInput = new TextField(String.valueOf(food.getCarbs()));
        TextField fatInput = new TextField(String.valueOf(food.getFats()));

        GridPane grid = new GridPane();
        grid.setHgap(12);
        grid.setVgap(10);
        grid.setPadding(new Insets(14));
        grid.addRow(0, new Label("Name:"), nameInput);
        grid.addRow(1, new Label("Calories:"), calInput);
        grid.addRow(2, new Label("Protein:"), proInput);
        grid.addRow(3, new Label("Carbs:"), carbInput);
        grid.addRow(4, new Label("Fats:"), fatInput);

        dialog.getDialogPane().setContent(grid);

        dialog.showAndWait()
                .ifPresent(bt -> {
                    if (bt != ButtonType.OK) return;
                    String name = nameInput.getText().trim();
                    if (name.isEmpty()) return;
                    try {
                        int calories = Integer.parseInt(calInput.getText().trim());
                        int protein = Integer.parseInt(proInput.getText().trim());
                        int carbs = Integer.parseInt(carbInput.getText().trim());
                        int fats = Integer.parseInt(fatInput.getText().trim());
                        if (calories >= 0 && protein >= 0 && carbs >= 0 && fats >= 0) {
                            foodDAO.updateFood(food.getId(), name, calories, protein, carbs, fats);
                            setFoodsOnList(foodDatePicker.getValue());
                        }
                    } catch (NumberFormatException ignored) {
                    }
                });
    }

    /**
     * Performs handle remove food.
     * @param actionEvent The action event.
     */

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
    /**
     * Performs handle ai logo click.
     */

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

    /**
     * Performs update totals.
     * @param date The date.
     */
    private void updateTotals(LocalDate date) {
        if (date == null) {
            return;
        }
        totalCalories = foodDAO.getDailyTotalByAttribute(date, "calories", currentUserID);
        totalProtein = foodDAO.getDailyTotalByAttribute(date, "protein", currentUserID);
        totalCarbs = foodDAO.getDailyTotalByAttribute(date, "carbs", currentUserID);
        totalFats = foodDAO.getDailyTotalByAttribute(date, "fats", currentUserID);
    }

    /**
     * Performs refresh totals labels.
     */
    private void refreshTotalsLabels() {
        caloriesProgressLabel.setText(
                String.format("%.0f/%.0fkcal", totalCalories, targetCalories));
        proteinProgressLabel.setText(
                String.format("%.0f/%.0fg protein", totalProtein, targetProtein));
        carbsProgressLabel.setText(String.format("%.0f/%.0fg carbs", totalCarbs, targetCarbs));
        fatsProgressLabel.setText(String.format("%.0f/%.0fg fats", totalFats, targetFats));
    }

    /**
     * Performs update progress bars.
     */
    private void updateProgressBars() {
        caloriesProgressBar.setProgress(clamp(totalCalories / targetCalories));
        proteinProgressBar.setProgress(clamp(totalProtein / targetProtein));
        carbsProgressBar.setProgress(clamp(totalCarbs / targetCarbs));
        fatProgressBar.setProgress(clamp(totalFats / targetFats));
    }

    /**
     * Performs clamp.
     * @param value The value.
     * @return The computed value.
     */
    private double clamp(double value) {
        if (Double.isNaN(value) || Double.isInfinite(value)) {
            return 0;
        }
        return Math.max(0, Math.min(1, value));
    }

    /**
     * Navigates to the calorie trends screen.
     */
    @FXML
    public void handleCalorieTrends() {
        try {
            Pane page = FXMLLoader.load(getClass().getResource(CALORIE_TRENDS_VIEW));
            StackPane pc = (StackPane) foodList.getScene().lookup("#pageContainer");
            if (pc != null) {
                pc.getChildren().setAll(page);
            }
        } catch (IOException e) {
            throw new RuntimeException("Failed to open calorie trends", e);
        }
    }

    /**
     * Performs clear inputs.
     */
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
