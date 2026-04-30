package com.lockedin.lockedin.model.entity;

/**
 * Represents a single set performed for a specific exercise.
 * Stores the exercise reference along with reps and weight used.
 */
public class ExerciseSet {
    private Exercise exercise;
    private int reps;
    private double weight;

    public ExerciseSet(Exercise exercise, int reps, double weight) {
        this.exercise = exercise;
        this.reps = reps;
        this.weight = weight;
    }

    public ExerciseSet(Exercise exercise) {
        this.exercise = exercise;
    }

    public Exercise getExercise() {
        return exercise;
    }

    public void setExercise(Exercise exercise) {
        this.exercise = exercise;
    }

    public int getReps() {
        return reps;
    }

    public void setReps(int reps) {
        this.reps = reps;
    }

    public double getWeight() {
        return weight;
    }

    public void setWeight(double weight) {
        this.weight = weight;
    }
}
