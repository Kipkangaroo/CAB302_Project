package com.lockedin.lockedin.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class WorkoutRoutine {
    private int id;
    private String name;
    private String duration;
    private String intensity;
    private String notes;
    private HashMap<Exercise, ArrayList<ExerciseSet>> exerciseTracking;

    public WorkoutRoutine() {
        this.exerciseTracking = new HashMap<>();
    }

    public WorkoutRoutine(int id, String name, String duration, String intensity, String notes, Map<Exercise, ArrayList<ExerciseSet>> exerciseTracking) {
        this.id = id;
        this.name = name;
        this.duration = duration;
        this.intensity = intensity;
        this.notes = notes;
        this.exerciseTracking = new HashMap<>();
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDuration() {
        return duration;
    }

    public void setDuration(String duration) {
        this.duration = duration;
    }

    public String getIntensity() {
        return intensity;
    }

    public void setIntensity(String intensity) {
        this.intensity = intensity;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    //return list of exercises in the workout routine
    public List<Exercise> getExercises() {
        return new ArrayList<>(exerciseTracking.keySet());
    }
    //return list of exercise sets for a given exercise
    public ArrayList<ExerciseSet> getExerciseSets(Exercise exercise) {
        return exerciseTracking.get(exercise);
    }
    //add a new exercise set to the workout routine
    public void addExerciseSet(Exercise exercise, ExerciseSet exerciseSet) {
        exerciseTracking.get(exercise).add(exerciseSet);
    }
    //remove exercise set from the workout routine
    public void removeExerciseSet(Exercise exercise, ExerciseSet exerciseSet) {
        exerciseTracking.get(exercise).remove(exerciseSet);
    }
    public int getTotalSets(Exercise exercise) {
        return exerciseTracking.get(exercise).size();
    }
    public double getTotalVolume() {
        int totalVolume = 0;
        for (Exercise exercise: exerciseTracking.keySet()) {
            for (ExerciseSet exerciseSet: exerciseTracking.get(exercise)) {
                totalVolume += exerciseSet.getWeight() * exerciseSet.getReps();
            }
            }
        return totalVolume;
    }
}

