package com.lockedin.lockedin.controller.profile;

import java.time.LocalDate;
import java.util.Date;

import com.lockedin.lockedin.model.dao.FoodDAO;
import com.lockedin.lockedin.model.dao.WorkoutRoutineDAO;
import com.lockedin.lockedin.model.entity.User;
import com.lockedin.lockedin.model.session.CurrentUser;
import javafx.fxml.FXML;
import javafx.scene.control.Label;

public class ProfileController {
    private User user;
    @FXML
    private Label nameLabel;
    @FXML
    private Label ageLabel;
    @FXML
    private Label heightLabel;
    @FXML
    private Label weightLabel;
    @FXML
    private Label fitnessGoalLabel;
    @FXML
    private Label totalWorkoutsLabel;
    @FXML
    private Label avgCalLabel;
    @FXML

    private WorkoutRoutineDAO routineDAO;
    private FoodDAO foodDAO;

    @FXML
    private void initialize() {
        routineDAO = new WorkoutRoutineDAO();
        foodDAO = new FoodDAO();

        user = CurrentUser.get();
        int age = LocalDate.now().getYear() - user.getDateOfBirth().getYear();
        ageLabel.setText("Age: " + age);
        heightLabel.setText("Height: " + user.getHeight() + " cm");
        weightLabel.setText("Weight: " + user.getWeight() + " kg");
        fitnessGoalLabel.setText("Fitness Goal: " + user.getFitnessGoal());
        nameLabel.setText("Hello " + user.getFirstName() + "!");
        totalWorkoutsLabel.setText("Total Workouts: " + routineDAO.getTotalWorkoutsByUser(user.getId()));
        // Need to update to actually get average over the previous seven days, when currently only gets today's total calories
        avgCalLabel.setText("Avg Calories: " + foodDAO.getDailyTotalByAttribute(new Date(), "calories", user.getId()));
    }
}
