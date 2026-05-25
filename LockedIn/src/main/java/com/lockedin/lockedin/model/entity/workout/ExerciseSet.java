package com.lockedin.lockedin.model.entity.workout;

/**
 * Represents a single set performed for an exercise, including reps and weight.
 *
 * @author LockedIn Team
 * @version 1.0
 */
public class ExerciseSet {
    private Exercise exercise;
    private int reps;
    private double weight;

        /**
     * Constructs a ExerciseSet using default application dependencies.
     * @param exercise exercise
     * @param reps reps
     * @param weight weight
     */
    public ExerciseSet(Exercise exercise, int reps, double weight) {
        this.exercise = exercise;
        this.reps = reps;
        this.weight = weight;
    }

        /**
     * Constructs a ExerciseSet using default application dependencies.
     * @param exercise exercise
     */
    public ExerciseSet(Exercise exercise) {
        this.exercise = exercise;
    }

            /**
     * Returns the exercise.
     * @return exercise
     */
    public Exercise getExercise() {
        return exercise;
    }

        /**
     * Sets the exercise.
     * @param exercise exercise
     */
    public void setExercise(Exercise exercise) {
        this.exercise = exercise;
    }

            /**
     * Returns the reps.
     * @return reps
     */
    public int getReps() {
        return reps;
    }

        /**
     * Sets the reps.
     * @param reps reps
     */
    public void setReps(int reps) {
        this.reps = reps;
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
}
