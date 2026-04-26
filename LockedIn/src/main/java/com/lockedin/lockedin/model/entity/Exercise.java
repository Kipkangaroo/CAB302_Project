package com.lockedin.lockedin.model.entity;

public class Exercise {
    private int id;
    private String name;
    private String instruction;
    private String category;
    private String primaryMuscle;
    private static final String baseImageUrl = "https://raw.githubusercontent.com/yuhonas/free-exercise-db/main/exercises/";

    public Exercise(int id, String name, String instruction, String category, String primaryMuscle) {
        this.id = id;
        this.name = name;
        this.instruction = instruction;
        this.category = category;
        this.primaryMuscle = primaryMuscle;
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

    @Override
    public String toString() {
        return name;
    }
}
