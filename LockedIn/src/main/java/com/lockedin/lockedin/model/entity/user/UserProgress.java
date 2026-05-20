package com.lockedin.lockedin.model.entity.user;

import java.time.LocalDate;

public class UserProgress {
    private final int userId;
    private int id;
    private FitnessGoal fitnessGoal;
    private double weight;
    private double targetCalories;
    private LocalDate effectiveFrom;

    public UserProgress(int userId) {
        this.userId = userId;
    }

    public UserProgress(
            int id,
            int userId,
            FitnessGoal fitnessGoal,
            double weight,
            double targetCalories,
            LocalDate effectiveFrom) {
        this.id = id;
        this.userId = userId;
        this.fitnessGoal = fitnessGoal;
        this.weight = weight;
        this.targetCalories = targetCalories;
        this.effectiveFrom = effectiveFrom;
    }

    public int getUserId() {
        return userId;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public FitnessGoal getFitnessGoal() {
        return fitnessGoal;
    }

    public void setFitnessGoal(FitnessGoal fitnessGoal) {
        this.fitnessGoal = fitnessGoal;
    }

    public double getWeight() {
        return weight;
    }

    public void setWeight(double weight) {
        this.weight = weight;
    }

    public double getTargetCalories() {
        return targetCalories;
    }

    public void setTargetCalories(double targetCalories) {
        this.targetCalories = targetCalories;
    }

    public LocalDate getEffectiveFrom() {
        return effectiveFrom;
    }

    public void setEffectiveFrom(LocalDate effectiveFrom) {
        this.effectiveFrom = effectiveFrom;
    }
}
