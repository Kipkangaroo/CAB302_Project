package com.lockedin.lockedin.model.entity;

import java.time.LocalDate;

public class UserDetailSnapshot {
    private int id;
    private final int userId;
    private String fitnessGoal;
    private double weight;
    private LocalDate effectiveFrom;

    public UserDetailSnapshot(int userId) {
        this.userId = userId;
    }

    public UserDetailSnapshot(int id, int userId, String fitnessGoal, double weight, LocalDate effectiveFrom) {
        this.id = id;
        this.userId = userId;
        this.fitnessGoal = fitnessGoal;
        this.weight = weight;
        this.effectiveFrom = effectiveFrom;
    }

    public int getUserId() {
        return userId;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getFitnessGoal() {
        return fitnessGoal;
    }

    public double getWeight() {
        return weight;
    }

    public LocalDate getEffectiveFrom() {
        return effectiveFrom;
    }

    public void setFitnessGoal(String fitnessGoal) {
        this.fitnessGoal = fitnessGoal;
    }

    public void setWeight(double weight) {
        this.weight = weight;
    }
    
    public void setEffectiveFrom(LocalDate effectiveFrom) {
        this.effectiveFrom = effectiveFrom;
    }
}
