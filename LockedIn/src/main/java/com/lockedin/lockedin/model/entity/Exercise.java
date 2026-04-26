package com.lockedin.lockedin.model.entity;

public class Exercise {
    private int id;
    private String name;
    private String instruction;
    private String category;
    private String primaryMuscle;
    private String exerciseImageId;
    private static final String baseImageUrl = "https://raw.githubusercontent.com/yuhonas/free-exercise-db/main/exercises/";

    public Exercise(int id, String name, String instruction, String category, String primaryMuscle, String exerciseImageId) {
        this.id = id;
        this.name = name;
        this.instruction = instruction;
        this.category = category;
        this.primaryMuscle = primaryMuscle;
        this.exerciseImageId = exerciseImageId;
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

    public String getInstruction() {
        return instruction;
    }

    public void setInstruction(String instruction) {
        this.instruction = instruction;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getPrimaryMuscle() {
        return primaryMuscle;
    }

    public void setPrimaryMuscle(String primaryMuscle) {
        this.primaryMuscle = primaryMuscle;
    }

    public String getExerciseImageId() {
        return exerciseImageId;
    }

    public void setExerciseImageId(String exerciseImageId) {
        this.exerciseImageId = exerciseImageId;
    }

    public String getExerciseImageUrl(int option) {
        return baseImageUrl + exerciseImageId + "/" + option + ".jpg";
    }

    @Override
    public String toString() {
        return name;
    }
}
