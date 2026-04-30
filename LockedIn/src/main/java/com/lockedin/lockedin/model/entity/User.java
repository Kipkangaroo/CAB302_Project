package com.lockedin.lockedin.model.entity;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

/**
 * Represents a user profile, including personal details,
 * authentication data, and fitness goal calculations.
 * Provides helper methods for computing TDEE and macro targets.
 */
public class User {
    private static final double CALORIES_PER_GRAM_PROTEIN = 4.0;
    private static final double CALORIES_PER_GRAM_CARBS = 4.0;
    private static final double CALORIES_PER_GRAM_FATS = 9.0;

    private int id;
    private String firstName;
    private String lastName;
    private LocalDate dateOfBirth;
    private double height;
    private double weight;
    private String email;
    private String fitnessGoal;
    private String passwordHash;

    public User() {
    }

    public User(int id, String firstName, String lastName, String email, LocalDate dateOfBirth, double height, double weight,
            String password, String fitnessGoal) {
        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
        this.dateOfBirth = dateOfBirth;
        this.height = height;
        this.weight = weight;
        this.email = email;
        this.fitnessGoal = fitnessGoal;
        this.passwordHash = getHash(password);
    }

    public static String getHash(String password) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hashBytes = digest.digest(password.getBytes(StandardCharsets.UTF_8));
            StringBuilder hexString = new StringBuilder();
            for (byte hashByte : hashBytes) {
                hexString.append(String.format("%02x", hashByte));
            }
            return hexString.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException("SHA-256 algorithm not available", e);
        }
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public LocalDate getDateOfBirth() {
        return dateOfBirth;
    }

    public void setDateOfBirth(LocalDate dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
    }

    public String getFitnessGoal() {
        return fitnessGoal;
    }

    public void setFitnessGoal(String fitnessGoal) {
        this.fitnessGoal = fitnessGoal;
    }

    public String getPasswordHash() {
        return passwordHash;
    }

    public void setPasswordHash(String passwordHash) {
        this.passwordHash = passwordHash;
    }

    public double getHeight() {
        return height;
    }

    public void setHeight(double height) {
        this.height = height;
    }

    public double getWeight() {
        return weight;
    }

    public void setWeight(double weight) {
        this.weight = weight;
    }

    // ChronoUnit.YEARS.between() accounts for whether the birthday has occurred yet
    // this year; simple year subtraction overstates age before the birthday.
    public int getAge() {
        return (int) ChronoUnit.YEARS.between(dateOfBirth, LocalDate.now());
    }

    public double getTDEE() {
        //Mifflin-St Jeor BMR formula
        double BMR = 10 * weight + 6.25 * height - 5 * getAge() + 5;
        double TDEE = BMR * 1.2;
        return TDEE;
    }

    public double getTargetCalories() {
        switch (this.fitnessGoal) {
            case "Lose Weight":
                return getTDEE() - 500;
            case "Build Muscle":
                return getTDEE() + 500;
            case "Maintain Fitness":
                return getTDEE();
            default:
                return getTDEE();
        }
    }

    public double getTargetProtein() {
        return (getTargetCalories() * getProteinRatio()) / CALORIES_PER_GRAM_PROTEIN;
    }

    public double getTargetCarbs() {
        return (getTargetCalories() * getCarbsRatio()) / CALORIES_PER_GRAM_CARBS;
    }

    public double getTargetFats() {
        return (getTargetCalories() * getFatsRatio()) / CALORIES_PER_GRAM_FATS;
    }

    private double getProteinRatio() {
        switch (this.fitnessGoal) {
            case "Lose Weight":
                return 0.35;
            case "Build Muscle":
                return 0.30;
            case "Maintain Fitness":
                return 0.25;
            default:
                return 0.25;
        }
    }

    private double getCarbsRatio() {
        switch (this.fitnessGoal) {
            case "Lose Weight":
                return 0.35;
            case "Build Muscle":
                return 0.50;
            case "Maintain Fitness":
                return 0.45;
            default:
                return 0.45;
        }
    }

    private double getFatsRatio() {
        switch (this.fitnessGoal) {
            case "Lose Weight":
                return 0.30;
            case "Build Muscle":
                return 0.20;
            case "Maintain Fitness":
                return 0.30;
            default:
                return 0.30;
        }
    }
}
