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
    private HashMap<Exercise, ArrayList<ExerciseSet>>  exerciseTracking;

        /**
     * Constructs a WorkoutRoutine using default application dependencies.
     */
    public WorkoutRoutine() {
        this.exerciseTracking = new HashMap<>();
    }

        /**
     * Constructs a WorkoutRoutine using default application dependencies.
     * @param id id
     * @param name name
     * @param duration duration
     * @param intensity intensity
     * @param notes notes
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
     * Returns the name.
     * @return name
     */
    public String getName() {
        return name;
    }

        /**
     * Sets the name.
     * @param name name
     */
    public void setName(String name) {
        this.name = name;
    }

            /**
     * Returns the duration.
     * @return duration
     */
    public String getDuration() {
        return duration;
    }

        /**
     * Sets the duration.
     * @param duration duration
     */
    public void setDuration(String duration) {
        this.duration = duration;
    }

            /**
     * Returns the intensity.
     * @return intensity
     */
    public String getIntensity() {
        return intensity;
    }

        /**
     * Sets the intensity.
     * @param intensity intensity
     */
    public void setIntensity(String intensity) {
        this.intensity = intensity;
    }

            /**
     * Returns the notes.
     * @return notes
     */
    public String getNotes() {
        return notes;
    }

        /**
     * Sets the notes.
     * @param notes notes
     */
    public void setNotes(String notes) {
        this.notes = notes;
    }

            /**
     * Returns the exercises.
     * @return exercises
     */
    public List<Exercise> getExercises() {
        return new ArrayList<>(exerciseTracking.keySet());
    }

            /**
     * Returns the exercise sets.
     * @param exercise exercise
     * @return exercise sets
     */
    public ArrayList<ExerciseSet> getExerciseSets(Exercise exercise) {
        return exerciseTracking.get(exercise);
    }

        /**
     * Add exercise set.
     * @param exercise exercise
     * @param exerciseSet The exercise set.
     */
    public void addExerciseSet(Exercise exercise, ExerciseSet exerciseSet) {
        exerciseTracking.get(exercise).add(exerciseSet);
    }

        /**
     * Remove exercise set.
     * @param exercise exercise
     * @param exerciseSet The exercise set.
     */
    public void removeExerciseSet(Exercise exercise, ExerciseSet exerciseSet) {
        exerciseTracking.get(exercise).remove(exerciseSet);
    }

            /**
     * Returns the total sets.
     * @param exercise exercise
     * @return total sets
     */
    public int getTotalSets(Exercise exercise) {
        return exerciseTracking.get(exercise).size();
    }

            /**
     * Returns the total volume.
     * @return total volume
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
