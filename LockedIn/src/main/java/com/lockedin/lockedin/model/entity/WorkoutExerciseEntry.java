package com.lockedin.lockedin.model.entity;

/**
 * Represents a single exercise entry inside a workout routine. Stores the exercise reference (ID +
 * name) along with sets, reps, and rest time. Used both when building routines and when loading
 * them from the database.
 */
public class WorkoutExerciseEntry {
    private final int id;
    private final int exerciseId;
    private final String exerciseName;
    private final int sets;
    private final int reps;
    private final int restSeconds;

    /** Used when creating a new entry before persisting to DB. */
    public WorkoutExerciseEntry(int exerciseId, String exerciseName, int sets, int reps) {
        this(0, exerciseId, exerciseName, sets, reps, 60);
    }

    /** Used when loading an existing entry from DB. */
    public WorkoutExerciseEntry(
            int id, int exerciseId, String exerciseName, int sets, int reps, int restSeconds) {
        this.id = id;
        this.exerciseId = exerciseId;
        this.exerciseName = exerciseName;
        this.sets = sets;
        this.reps = reps;
        this.restSeconds = restSeconds;
    }

    public int getId() {
        return id;
    }

    public int getExerciseId() {
        return exerciseId;
    }

    public String getExerciseName() {
        return exerciseName;
    }

    public int getSets() {
        return sets;
    }

    public int getReps() {
        return reps;
    }

    public int getRestSeconds() {
        return restSeconds;
    }

    @Override
    public String toString() {
        return exerciseName + "  \u2013  " + sets + " sets \u00d7 " + reps + " reps";
    }
}
