package com.lockedin.lockedin.model.entity.user;

import java.time.LocalDate;

/**
 * Provides user progress functionality for LockedIn.
 * @author LockedIn Team
 * @version 1.0
 */
public class UserProgress {
    private final int userId;
    private int id;
    private FitnessGoal fitnessGoal;
    private double weight;
    private double targetCalories;
    private LocalDate effectiveFrom;

        /**
     * Constructs a UserProgress using default application dependencies.
     * @param userId The user id.
     */
    public UserProgress(int userId) {
        this.userId = userId;
    }

        /**
     * Constructs a UserProgress using default application dependencies.
     * @param id id
     * @param userId The user id.
     * @param fitnessGoal The fitness goal.
     * @param weight weight
     * @param targetCalories The target calories.
     * @param effectiveFrom The effective from.
     */
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

            /**
     * Returns the user id.
     * @return user id
     */
    public int getUserId() {
        return userId;
    }

            /**
     * Returns the id.
     * @return id
     */
    public int getId() {
        return id;
    }

        /**
     * Sets the id.
     * @param id id
     */
    public void setId(int id) {
        this.id = id;
    }

            /**
     * Returns the fitness goal.
     * @return fitness goal
     */
    public FitnessGoal getFitnessGoal() {
        return fitnessGoal;
    }

    /**
     * Sets the fitness goal.
     * @param fitnessGoal The fitness goal.
     */
    public void setFitnessGoal(FitnessGoal fitnessGoal) {
        this.fitnessGoal = fitnessGoal;
    }

            /**
     * Returns the weight.
     * @return weight
     */
    public double getWeight() {
        return weight;
    }

        /**
     * Sets the weight.
     * @param weight weight
     */
    public void setWeight(double weight) {
        this.weight = weight;
    }

            /**
     * Returns the target calories.
     * @return target calories
     */
    public double getTargetCalories() {
        return targetCalories;
    }

    /**
     * Sets the target calories.
     * @param targetCalories The target calories.
     */
    public void setTargetCalories(double targetCalories) {
        this.targetCalories = targetCalories;
    }

            /**
     * Returns the effective from.
     * @return effective from
     */
    public LocalDate getEffectiveFrom() {
        return effectiveFrom;
    }

    /**
     * Sets the effective from.
     * @param effectiveFrom The effective from.
     */
    public void setEffectiveFrom(LocalDate effectiveFrom) {
        this.effectiveFrom = effectiveFrom;
    }
}
