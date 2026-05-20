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
     * Creates a new ExerciseSet.
     * @param exercise The exercise.
     * @param reps The reps.
     * @param weight The weight.
     */
    public ExerciseSet(Exercise exercise, int reps, double weight) {
        this.exercise = exercise;
        this.reps = reps;
        this.weight = weight;
    }

    /**
     * Creates a new ExerciseSet.
     * @param exercise The exercise.
     */
    public ExerciseSet(Exercise exercise) {
        this.exercise = exercise;
    }

    /**
     * Returns the exercise.
     * @return The exercise.
     */
    public Exercise getExercise() {
        return exercise;
    }

    /**
     * Sets the exercise.
     * @param exercise The exercise.
     */
    public void setExercise(Exercise exercise) {
        this.exercise = exercise;
    }

    /**
     * Returns the reps.
     * @return The reps.
     */
    public int getReps() {
        return reps;
    }

    /**
     * Sets the reps.
     * @param reps The reps.
     */
    public void setReps(int reps) {
        this.reps = reps;
    }

    /**
     * Returns the weight.
     * @return The weight.
     */
    public double getWeight() {
        return weight;
    }

    /**
     * Sets the weight.
     * @param weight The weight.
     */
    public void setWeight(double weight) {
        this.weight = weight;
    }
}
