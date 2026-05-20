package com.lockedin.lockedin.model.entity.user;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

/**
 * Represents a user profile with authentication data and fitness calculation helpers.
 *
 * @author LockedIn Team
 * @version 1.0
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

    /**
     * Creates a new User.
     */
    public User() {
    }

    /**
     * Creates a new User.
     * @param id The id.
     * @param firstName The first name.
     * @param lastName The last name.
     * @param email The email.
     * @param dateOfBirth The date of birth.
     * @param height The height.
     * @param weight The weight.
     * @param sex The sex.
     * @param activityLevel The activity level.
     * @param fitnessGoal The fitness goal.
     * @param password The password.
     */
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
    }

    /**
     * Returns the hash.
     * @param password The password.
     * @return The hash.
     */
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

    /**
     * Returns the protein ratio.
     * @param fitnessGoal The fitness goal.
     * @return The protein ratio.
     */
    private static double getProteinRatio(FitnessGoal fitnessGoal) {
        return fitnessGoal == null ? 0.25 : fitnessGoal.getProteinRatio();
    }

    /**
     * Returns the carbs ratio.
     * @param fitnessGoal The fitness goal.
     * @return The carbs ratio.
     */
    private static double getCarbsRatio(FitnessGoal fitnessGoal) {
        return fitnessGoal == null ? 0.45 : fitnessGoal.getCarbsRatio();
    }

    /**
     * Returns the fats ratio.
     * @param fitnessGoal The fitness goal.
     * @return The fats ratio.
     */
    private static double getFatsRatio(FitnessGoal fitnessGoal) {
        return fitnessGoal == null ? 0.30 : fitnessGoal.getFatsRatio();
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
     * Returns the first name.
     * @return The first name.
     */
    public String getFirstName() {
        return firstName;
    }

    /**
     * Sets the first name.
     * @param firstName The first name.
     */
    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    /**
     * Returns the last name.
     * @return The last name.
     */
    public String getLastName() {
        return lastName;
    }

    /**
     * Sets the last name.
     * @param lastName The last name.
     */
    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    /**
     * Returns the email.
     * @return The email.
     */
    public String getEmail() {
        return email;
    }

    /**
     * Sets the email.
     * @param email The email.
     */
    public void setEmail(String email) {
        this.email = email;
    }

    /**
     * Returns the date of birth.
     * @return The date of birth.
     */
    public LocalDate getDateOfBirth() {
        return dateOfBirth;
    }

    /**
     * Sets the date of birth.
     * @param dateOfBirth The date of birth.
     */
    public void setDateOfBirth(LocalDate dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
    }

    /**
     * Returns the fitness goal.
     * @return The fitness goal.
     */
    public FitnessGoal getFitnessGoal() {
        return this.fitnessGoal;
    }

    /**
     * Sets the fitness goal.
     * @param fitnessGoal The fitness goal.
     */
    public void setFitnessGoal(FitnessGoal fitnessGoal) {
        this.fitnessGoal = fitnessGoal;
    }

    /**
     * Returns the password hash.
     * @return The password hash.
     */
    public String getPasswordHash() {
        return passwordHash;
    }

    /**
     * Sets the password hash.
     * @param passwordHash The password hash.
     */
    public void setPasswordHash(String passwordHash) {
        this.passwordHash = passwordHash;
    }

    /**
     * Returns the height.
     * @return The height.
     */
    public double getHeight() {
        return height;
    }

    /**
     * Sets the height.
     * @param height The height.
     */
    public void setHeight(double height) {
        this.height = height;
    }

    /**
     * Returns the weight.
     * @return The weight.
     */
    public double getWeight() {
        return this.weight;
    }

    /**
     * Sets the weight.
     * @param weight The weight.
     */
    public void setWeight(double weight) {
        this.weight = weight;
    }

    /**
     * Returns the sex.
     * @return The sex.
     */
    public String getSex() {
        return sex;
    }

    /**
     * Sets the sex.
     * @param sex The sex.
     */
    public void setSex(String sex) {
        this.sex = sex;
    }

    /**
     * Returns the activity level.
     * @return The activity level.
     */
    public ActivityLevel getActivityLevel() {
        return activityLevel;
    }

    /**
     * Sets the activity level.
     * @param activityLevel The activity level.
     */
    public void setActivityLevel(ActivityLevel activityLevel) {
        this.activityLevel = activityLevel;
    }

    /**
     * Returns the age.
     * @return The age.
     */
    public int getAge() {
        return (int) ChronoUnit.YEARS.between(dateOfBirth, LocalDate.now());
    }

    /**
     * Returns the bmr.
     * @return The bmr.
     */
    public double getBMR() {
        return switch (this.sex) {
            case "Male" -> (10 * weight) + (6.25 * height) - (5 * getAge()) + 5;
            case "Female" -> (10 * weight) + (6.25 * height) - (5 * getAge()) - 161;
            default -> 0;
        };
    }

    /**
     * Returns the tdee.
     * @return The tdee.
     */
    public double getTDEE() {
        double bmr = getBMR();
        if (activityLevel == null) {
            return bmr;
        }
        return bmr * activityLevel.getTdeeMultiplier();
    }

    /**
     * Returns the target calories.
     * @return The target calories.
     */
    public double getTargetCalories() {
        double tdee = getTDEE();
        FitnessGoal goal = getFitnessGoal();
        if (goal == null) {
            return tdee;
        }
        return tdee + goal.getCalorieAdjustment();
    }

    /**
     * Returns the target protein.
     * @param totalCalories The total calories.
     * @param fitnessGoal The fitness goal.
     * @return The target protein.
     */
    public double getTargetProtein(double totalCalories, FitnessGoal fitnessGoal) {
        return (totalCalories * getProteinRatio(fitnessGoal)) / CALORIES_PER_GRAM_PROTEIN;
    }

    /**
     * Returns the target carbs.
     * @param totalCalories The total calories.
     * @param fitnessGoal The fitness goal.
     * @return The target carbs.
     */
    public double getTargetCarbs(double totalCalories, FitnessGoal fitnessGoal) {
        return (totalCalories * getCarbsRatio(fitnessGoal)) / CALORIES_PER_GRAM_CARBS;
    }

    /**
     * Returns the target fats.
     * @param totalCalories The total calories.
     * @param fitnessGoal The fitness goal.
     * @return The target fats.
     */
    public double getTargetFats(double totalCalories, FitnessGoal fitnessGoal) {
        return (totalCalories * getFatsRatio(fitnessGoal)) / CALORIES_PER_GRAM_FATS;
    }

}
