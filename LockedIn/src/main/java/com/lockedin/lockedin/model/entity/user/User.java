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
     * Constructs a User using default application dependencies.
     */
    public User() {
    }

        /**
     * Constructs a User using default application dependencies.
     * @param id id
     * @param firstName The first name.
     * @param lastName The last name.
     * @param email email
     * @param dateOfBirth The date of birth.
     * @param height height
     * @param weight weight
     * @param sex sex
     * @param activityLevel The activity level.
     * @param fitnessGoal The fitness goal.
     * @param password password
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
     * @param password password
     * @return hash
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
     * @return protein ratio
     */
    private static double getProteinRatio(FitnessGoal fitnessGoal) {
        return fitnessGoal == null ? 0.25 : fitnessGoal.getProteinRatio();
    }

            /**
     * Returns the carbs ratio.
     * @param fitnessGoal The fitness goal.
     * @return carbs ratio
     */
    private static double getCarbsRatio(FitnessGoal fitnessGoal) {
        return fitnessGoal == null ? 0.45 : fitnessGoal.getCarbsRatio();
    }

            /**
     * Returns the fats ratio.
     * @param fitnessGoal The fitness goal.
     * @return fats ratio
     */
    private static double getFatsRatio(FitnessGoal fitnessGoal) {
        return fitnessGoal == null ? 0.30 : fitnessGoal.getFatsRatio();
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
     * Returns the first name.
     * @return first name
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
     * @return last name
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
     * @return email
     */
    public String getEmail() {
        return email;
    }

        /**
     * Sets the email.
     * @param email email
     */
    public void setEmail(String email) {
        this.email = email;
    }

            /**
     * Returns the date of birth.
     * @return date of birth
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
     * @return fitness goal
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
     * @return password hash
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
     * @return height
     */
    public double getHeight() {
        return height;
    }

        /**
     * Sets the height.
     * @param height height
     */
    public void setHeight(double height) {
        this.height = height;
    }

            /**
     * Returns the weight.
     * @return weight
     */
    public double getWeight() {
        return this.weight;
    }

        /**
     * Sets the weight.
     * @param weight weight
     */
    public void setWeight(double weight) {
        this.weight = weight;
    }

            /**
     * Returns the sex.
     * @return sex
     */
    public String getSex() {
        return sex;
    }

        /**
     * Sets the sex.
     * @param sex sex
     */
    public void setSex(String sex) {
        this.sex = sex;
    }

            /**
     * Returns the activity level.
     * @return activity level
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
     * @return age
     */
    public int getAge() {
        return (int) ChronoUnit.YEARS.between(dateOfBirth, LocalDate.now());
    }

            /**
     * Returns the bmr.
     * @return bmr
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
     * @return tdee
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
     * @return target calories
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
     * @return target protein
     */
    public double getTargetProtein(double totalCalories, FitnessGoal fitnessGoal) {
        final double caloriesPerGramProtein = 4.0;
        return (totalCalories * getProteinRatio(fitnessGoal)) / caloriesPerGramProtein;
    }

            /**
     * Returns the target carbs.
     * @param totalCalories The total calories.
     * @param fitnessGoal The fitness goal.
     * @return target carbs
     */
    public double getTargetCarbs(double totalCalories, FitnessGoal fitnessGoal) {
        final double caloriesPerGramCarbs = 4.0;
        return (totalCalories * getCarbsRatio(fitnessGoal)) / caloriesPerGramCarbs;
    }

            /**
     * Returns the target fats.
     * @param totalCalories The total calories.
     * @param fitnessGoal The fitness goal.
     * @return target fats
     */
    public double getTargetFats(double totalCalories, FitnessGoal fitnessGoal) {
        final double caloriesPerGramFats = 9.0;
        return (totalCalories * getFatsRatio(fitnessGoal)) / caloriesPerGramFats;
    }

}
