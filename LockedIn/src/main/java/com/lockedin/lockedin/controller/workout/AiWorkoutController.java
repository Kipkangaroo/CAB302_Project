package com.lockedin.lockedin.controller.workout;

import com.lockedin.lockedin.model.dao.WorkoutRoutineDAO;
import com.lockedin.lockedin.model.entity.workout.WorkoutExerciseEntry;
import com.lockedin.lockedin.model.session.CurrentUser;
import com.lockedin.lockedin.service.AiWorkoutService;
import com.lockedin.lockedin.service.AiWorkoutService.WorkoutResult;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.*;

import java.io.IOException;
import java.util.List;

public class AiWorkoutController {

    private static final String WORKOUT_VIEW =
            "/com/lockedin/lockedin/pages/workout/workout-view.fxml";

    @FXML private Button backButton;
    @FXML private ComboBox<String> experienceCombo;
    @FXML private ComboBox<String> timeCombo;
    @FXML private ComboBox<String> muscleGroupCombo;
    @FXML private ComboBox<String> goalCombo;
    @FXML private Button generateButton;
    @FXML private ProgressIndicator loadingIndicator;
    @FXML private Label statusLabel;
    @FXML private VBox resultCard;
    @FXML private Label routineNameLabel;
    @FXML private VBox exerciseListContainer;
    @FXML private Button saveButton;

    private WorkoutResult generatedResult;
    private final WorkoutRoutineDAO routineDAO = new WorkoutRoutineDAO();
    private final AiWorkoutService aiService = new AiWorkoutService();

    @FXML
    public void initialize() {
        experienceCombo.getItems().addAll("Beginner", "Intermediate", "Advanced");
        timeCombo.getItems().addAll("30", "45", "60", "90");
        muscleGroupCombo.getItems().addAll("Upper Body", "Core", "Legs", "Full Body", "Mix");
        goalCombo.getItems().addAll(
                "Lose Weight", "Build Muscle", "Improve Endurance",
                "Increase Strength", "Maintain Fitness");

        experienceCombo.setValue("Intermediate");
        timeCombo.setValue("45");
        muscleGroupCombo.setValue("Full Body");
        goalCombo.setValue("Build Muscle");

        loadingIndicator.setVisible(false);
        resultCard.setVisible(false);
        resultCard.setManaged(false);
        saveButton.setDisable(true);
    }

    @FXML
    public void handleGenerate() {
        String experience = experienceCombo.getValue();
        String time = timeCombo.getValue();
        String muscleGroup = muscleGroupCombo.getValue();
        String goal = goalCombo.getValue();

        if (experience == null || time == null || muscleGroup == null || goal == null) {
            showStatus("Please fill in all fields before generating.", true);
            return;
        }

        setGeneratingState(true);

        var task = aiService.createGenerateWorkoutTask(experience, time, muscleGroup, goal);

        task.setOnSucceeded(e -> Platform.runLater(() -> {
            setGeneratingState(false);
            WorkoutResult result = task.getValue();
            if (result != null) {
                generatedResult = result;
                displayResult(result);
            } else {
                showStatus("Failed to generate workout. Please try again.", true);
            }
        }));

        task.setOnFailed(e -> Platform.runLater(() -> {
            setGeneratingState(false);
            task.getException().printStackTrace();
            showStatus("An error occurred. Please check your connection and try again.", true);
        }));

        Thread worker = new Thread(task, "ai-workout-generator");
        worker.setDaemon(true);
        worker.start();
    }

    @FXML
    public void handleSave() {
        if (generatedResult == null) return;
        routineDAO.saveRoutine(
                CurrentUser.getId(),
                generatedResult.routineName,
                "AI Generated Workout",
                generatedResult.exercises);
        navigateBack();
    }

    @FXML
    public void handleBack() {
        navigateBack();
    }

    private void displayResult(WorkoutResult result) {
        routineNameLabel.setText(result.routineName);
        exerciseListContainer.getChildren().clear();

        for (WorkoutExerciseEntry entry : result.exercises) {
            exerciseListContainer.getChildren().add(buildExerciseRow(entry));
        }

        resultCard.setVisible(true);
        resultCard.setManaged(true);
        saveButton.setDisable(false);
        statusLabel.setText("");
    }

    private HBox buildExerciseRow(WorkoutExerciseEntry entry) {
        Label nameLabel = new Label(entry.getExerciseName());
        nameLabel.setStyle("-fx-font-size: 13px; -fx-font-weight: bold;");
        HBox.setHgrow(nameLabel, Priority.ALWAYS);
        nameLabel.setWrapText(true);

        Label detailLabel = new Label(
                entry.getSets() + " sets \u00d7 " + entry.getReps()
                + " reps  |  " + entry.getRestSeconds() + "s rest");
        detailLabel.setStyle("-fx-font-size: 12px; -fx-text-fill: #757575;");

        VBox textBox = new VBox(2, nameLabel, detailLabel);
        HBox.setHgrow(textBox, Priority.ALWAYS);

        HBox row = new HBox(textBox);
        row.setAlignment(Pos.CENTER_LEFT);
        row.setPadding(new Insets(6, 8, 6, 8));
        row.setStyle("-fx-background-color: #f5f5f5; -fx-background-radius: 6;");
        return row;
    }

    private void setGeneratingState(boolean generating) {
        loadingIndicator.setVisible(generating);
        generateButton.setDisable(generating);
        if (generating) {
            statusLabel.setText("Generating your personalised workout...");
            statusLabel.setStyle("-fx-text-fill: #1565C0;");
            resultCard.setVisible(false);
            resultCard.setManaged(false);
            saveButton.setDisable(true);
        }
    }

    private void showStatus(String message, boolean isError) {
        statusLabel.setText(message);
        statusLabel.setStyle(isError ? "-fx-text-fill: #e53935;" : "-fx-text-fill: #1565C0;");
    }

    private void navigateBack() {
        try {
            Pane page = FXMLLoader.load(getClass().getResource(WORKOUT_VIEW));
            StackPane pc = (StackPane) backButton.getScene().lookup("#pageContainer");
            if (pc != null) pc.getChildren().setAll(page);
        } catch (IOException e) {
            throw new RuntimeException("Failed to navigate back", e);
        }
    }
}
