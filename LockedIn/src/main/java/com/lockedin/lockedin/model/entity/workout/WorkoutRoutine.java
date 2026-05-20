package com.lockedin.lockedin.model.entity.workout;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Represents a workout routine with metadata and tracked sets per exercise.
 *
 * @author LockedIn Team
 * @version 1.0
 */
public class WorkoutRoutine {
    private int id;
    private String name;
    private String duration;
    private String intensity;
    private String notes;
    private HashMap<Exercise, ArrayList<ExerciseSet>> exerciseTracking;

    /**
     * Creates a new WorkoutRoutine.
     */
    public WorkoutRoutine() {
        this.exerciseTracking = new HashMap<>();
    }

    /**
     * Creates a new WorkoutRoutine.
     * @param id The id.
     * @param name The name.
     * @param duration The duration.
     * @param intensity The intensity.
     * @param notes The notes.
     * @param exerciseTracking The exercise tracking.
     */
    public WorkoutRoutine(
            int id,
            String name,
            String duration,
            String intensity,
            String notes,
            Map<Exercise, ArrayList<ExerciseSet>> exerciseTracking) {
        this.id = id;
        this.name = name;
        this.duration = duration;
        this.intensity = intensity;
        this.notes = notes;
        this.exerciseTracking = new HashMap<>();
    }

    /**
     * Returns the id.
     * @return The id.
     */
    public int getId() {
        return id;
    }

    /**
     * Sets the id.
     * @param id The id.
     */
    public void setId(int id) {
        this.id = id;
    }

    /**
     * Returns the name.
     * @return The name.
     */
    public String getName() {
        return name;
    }

    /**
     * Sets the name.
     * @param name The name.
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Returns the duration.
     * @return The duration.
     */
    public String getDuration() {
        return duration;
    }

    /**
     * Sets the duration.
     * @param duration The duration.
     */
    public void setDuration(String duration) {
        this.duration = duration;
    }

    /**
     * Returns the intensity.
     * @return The intensity.
     */
    public String getIntensity() {
        return intensity;
    }

    /**
     * Sets the intensity.
     * @param intensity The intensity.
     */
    public void setIntensity(String intensity) {
        this.intensity = intensity;
    }

    /**
     * Returns the notes.
     * @return The notes.
     */
    public String getNotes() {
        return notes;
    }

    /**
     * Sets the notes.
     * @param notes The notes.
     */
    public void setNotes(String notes) {
        this.notes = notes;
    }

    /**
     * Returns the exercises.
     * @return The exercises.
     */
    public List<Exercise> getExercises() {
        return new ArrayList<>(exerciseTracking.keySet());
    }

    /**
     * Returns the exercise sets.
     * @param exercise The exercise.
     * @return The exercise sets.
     */
    public ArrayList<ExerciseSet> getExerciseSets(Exercise exercise) {
        return exerciseTracking.get(exercise);
    }

    /**
     * Performs add exercise set.
     * @param exercise The exercise.
     * @param exerciseSet The exercise set.
     */
    public void addExerciseSet(Exercise exercise, ExerciseSet exerciseSet) {
        exerciseTracking.get(exercise).add(exerciseSet);
    }

    /**
     * Performs remove exercise set.
     * @param exercise The exercise.
     * @param exerciseSet The exercise set.
     */
    public void removeExerciseSet(Exercise exercise, ExerciseSet exerciseSet) {
        exerciseTracking.get(exercise).remove(exerciseSet);
    }

    /**
     * Returns the total sets.
     * @param exercise The exercise.
     * @return The total sets.
     */
    public int getTotalSets(Exercise exercise) {
        return exerciseTracking.get(exercise).size();
    }

    /**
     * Returns the total volume.
     * @return The total volume.
     */
    public double getTotalVolume() {
        int totalVolume = 0;
        for (Exercise exercise : exerciseTracking.keySet()) {
            for (ExerciseSet exerciseSet : exerciseTracking.get(exercise)) {
                totalVolume += exerciseSet.getWeight() * exerciseSet.getReps();
            }
        }
        return totalVolume;
    }
}
