package com.lockedin.lockedin.model.entity.workout;

/**
 * Represents one exercise slot in a routine with sets, reps, and rest time.
 *
 * @author LockedIn Team
 * @version 1.0
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

    /**
     * Returns the id.
     * @return The id.
     */
    public int getId() {
        return id;
    }

    /**
     * Returns the exercise id.
     * @return The exercise id.
     */
    public int getExerciseId() {
        return exerciseId;
    }

    /**
     * Returns the exercise name.
     * @return The exercise name.
     */
    public String getExerciseName() {
        return exerciseName;
    }

    /**
     * Returns the sets.
     * @return The sets.
     */
    public int getSets() {
        return sets;
    }

    /**
     * Returns the reps.
     * @return The reps.
     */
    public int getReps() {
        return reps;
    }

    /**
     * Returns the rest seconds.
     * @return The rest seconds.
     */
    public int getRestSeconds() {
        return restSeconds;
    }
    /**
     * Returns a string representation of this object.
     * @return The resulting text.
     */

    @Override
    public String toString() {
        return exerciseName + "  \u2013  " + sets + " sets \u00d7 " + reps + " reps";
    }
}
