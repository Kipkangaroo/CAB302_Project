package com.lockedin.lockedin.model.entity.user;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

import com.lockedin.lockedin.model.dao.UserProgressDAO;

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
    private FitnessGoal fitnessGoal;
    private ActivityLevel activityLevel;
    private String passwordHash;
    private UserProgressDAO progressDAO;

    public User() {
        this.progressDAO = new UserProgressDAO();
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
            ActivityLevel activityLevel,
            FitnessGoal fitnessGoal,
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
        this.progressDAO = new UserProgressDAO();
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

    public FitnessGoal getFitnessGoal() {
        return this.fitnessGoal;
    }

    public void setFitnessGoal(FitnessGoal fitnessGoal) {
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

    public ActivityLevel getActivityLevel() {
        return activityLevel;
    }

    public void setActivityLevel(ActivityLevel activityLevel) {
        this.activityLevel = activityLevel;
    }

    public int getAge() {
        return (int) ChronoUnit.YEARS.between(dateOfBirth, LocalDate.now());
    }

    public double getBMR() {
        return switch (this.sex) {
            case "Male" -> (10 * weight) + (6.25 * height) - (5 * getAge()) + 5;
            case "Female" -> (10 * weight) + (6.25 * height) - (5 * getAge()) - 161;
            default -> 0;
        };
    }

    public double getTDEE() {
        double bmr = getBMR();
        if (activityLevel == null) {
            return bmr;
        }
        return bmr * activityLevel.getTdeeMultiplier();
    }

    public double getTargetCalories() {
        double tdee = getTDEE();
        FitnessGoal goal = getFitnessGoal();
        if (goal == null) {
            return tdee;
        }
        return tdee + goal.getCalorieAdjustment();
    }

    public double getTargetProtein(double totalCalories, FitnessGoal fitnessGoal) {
        return (totalCalories * getProteinRatio(fitnessGoal)) / CALORIES_PER_GRAM_PROTEIN;
    }

    public double getTargetCarbs(double totalCalories, FitnessGoal fitnessGoal) {
        return (totalCalories * getCarbsRatio(fitnessGoal)) / CALORIES_PER_GRAM_CARBS;
    }

    public double getTargetFats(double totalCalories, FitnessGoal fitnessGoal) {
        return (totalCalories * getFatsRatio(fitnessGoal)) / CALORIES_PER_GRAM_FATS;
    }

    private static double getProteinRatio(FitnessGoal fitnessGoal) {
        return fitnessGoal == null ? 0.25 : fitnessGoal.getProteinRatio();
    }

    private static double getCarbsRatio(FitnessGoal fitnessGoal) {
        return fitnessGoal == null ? 0.45 : fitnessGoal.getCarbsRatio();
    }

    private static double getFatsRatio(FitnessGoal fitnessGoal) {
        return fitnessGoal == null ? 0.30 : fitnessGoal.getFatsRatio();
    }

}
