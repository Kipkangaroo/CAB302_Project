package com.lockedin.lockedin.model.entity;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.Optional;

import com.lockedin.lockedin.model.dao.UserDetailSnapshotDAO;


/**
 * Represents a user profile, including personal details, authentication data,
 * and fitness goal
 * calculations. Provides helper methods for computing TDEE and macro targets.
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
    private String sex;
    private String fitnessGoal;
    private String activityLevel;
    private String passwordHash;
    private UserDetailSnapshotDAO snapshotDAO;

    public User() {
        this.snapshotDAO = new UserDetailSnapshotDAO();
    }

    public User(
            int id,
            String firstName,
            String lastName,
            String email,
            LocalDate dateOfBirth,
            double height,
            double weight,
            String sex,
            String activityLevel,
            String fitnessGoal,
            String password) {
        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
        this.dateOfBirth = dateOfBirth;
        this.height = height;
        this.weight = weight;
        this.email = email;
        this.sex = sex;
        this.fitnessGoal = fitnessGoal;
        this.activityLevel = activityLevel;
        this.passwordHash = getHash(password);
        this.snapshotDAO = new UserDetailSnapshotDAO();
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

    public String getFitnessGoal(LocalDate date) {
        if (date == null) {
            return this.fitnessGoal;
        }
        Optional<UserDetailSnapshot> activeSnapshot = snapshotDAO.getActiveUserDetailSnapshot(this.id, date);
        if (activeSnapshot.isPresent()) {
            return activeSnapshot.get().getFitnessGoal();
        }
        return this.fitnessGoal;
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

    public double getWeight(LocalDate date) {
        if (date == null) {
            return this.weight;
        }
        Optional<UserDetailSnapshot> activeSnapshot = snapshotDAO.getActiveUserDetailSnapshot(this.id, date);
        if (activeSnapshot.isPresent()) {
            return activeSnapshot.get().getWeight();
        }
        return this.weight;
    }

    public void setWeight(double weight) {
        this.weight = weight;
    }

    public String getSex() {
        return sex;
    }

    public void setSex(String sex) {
        this.sex = sex;
    }

    public String getActivityLevel() {
        return activityLevel;
    }

    public void setActivityLevel(String activityLevel) {
        this.activityLevel = activityLevel;
    }

    // ChronoUnit.YEARS.between() accounts for whether the birthday has occurred yet
    // this year; simple year subtraction overstates age before the birthday.
    public int getAge() {
        return (int) ChronoUnit.YEARS.between(dateOfBirth, LocalDate.now());
    }

    /**
     * BMR for a given calendar day uses weight (including snapshot history) and age as of that day.
     * {@code onDate == null} means "today" for weight/age.
     */
    public double getBMR(LocalDate onDate) {
        double w = onDate == null ? this.weight : getWeight(onDate);
        int ageYears =
                onDate == null
                        ? getAge()
                        : (int) ChronoUnit.YEARS.between(dateOfBirth, onDate);
        return switch (this.sex) {
            case "Male" -> (10 * w) + (6.25 * height) - (5 * ageYears) + 5;
            case "Female" -> (10 * w) + (6.25 * height) - (5 * ageYears) - 161;
            default -> 0;
        };
    }

    /** {@code onDate == null} means "today" for BMR inputs (weight/age). */
    public double getTDEE(LocalDate onDate) {
        double bmr = getBMR(onDate);
        return switch (this.activityLevel) {
            case "Sedentary (little/no exercise)" -> bmr * 1.2;
            case "Lightly active (1–3 days/week)" -> bmr * 1.375;
            case "Moderately active (3–5 days/week)" -> bmr * 1.55;
            case "Very active (6–7 days/week)" -> bmr * 1.725;
            case "Extra active (physical job + exercise)" -> bmr * 1.9;
            default -> bmr;
        };
    }

    public double getTargetCalories(LocalDate date) {
        double tdee = getTDEE(date);
        return switch (getFitnessGoal(date)) {
            case "Lose Weight" -> tdee - 500;
            case "Build Muscle" -> tdee + 500;
            case "Maintain Fitness" -> tdee;
            default -> tdee;
        };
    }

    public double getTargetProtein(LocalDate date) {
        return (getTargetCalories(date) * getProteinRatio(date)) / CALORIES_PER_GRAM_PROTEIN;
    }

    public double getTargetCarbs(LocalDate date) {
        return (getTargetCalories(date) * getCarbsRatio(date)) / CALORIES_PER_GRAM_CARBS;
    }

    public double getTargetFats(LocalDate date) {
        return (getTargetCalories(date) * getFatsRatio(date)) / CALORIES_PER_GRAM_FATS;
    }

    private double getProteinRatio(LocalDate date) {
        return switch (getFitnessGoal(date)) {
            case "Lose Weight" -> 0.35;
            case "Build Muscle" -> 0.30;
            case "Maintain Fitness" -> 0.25;
            default -> 0.25;
        };
    }

    private double getCarbsRatio(LocalDate date) {
        return switch (getFitnessGoal(date)) {
            case "Lose Weight" -> 0.35;
            case "Build Muscle" -> 0.50;
            case "Maintain Fitness" -> 0.45;
            default -> 0.45;
        };
    }

    private double getFatsRatio(LocalDate date) {
        return switch (getFitnessGoal(date)) {
            case "Lose Weight" -> 0.30;
            case "Build Muscle" -> 0.20;
            case "Maintain Fitness" -> 0.30;
            default -> 0.30;
        };
    }

}
