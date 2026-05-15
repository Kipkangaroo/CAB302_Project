package com.lockedin.lockedin.model.entity.user;

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
    private FitnessGoal fitnessGoal;
    private ActivityLevel activityLevel;
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

    public FitnessGoal getFitnessGoal(LocalDate date) {
        if (date == null) {
            return this.fitnessGoal;
        }
        Optional<UserDetailSnapshot> activeSnapshot = snapshotDAO.getActiveUserDetailSnapshot(this.id, date);
        if (activeSnapshot.isPresent()) {
            return activeSnapshot.get().getFitnessGoal();
        }
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

    public ActivityLevel getActivityLevel() {
        return activityLevel;
    }

    public void setActivityLevel(ActivityLevel activityLevel) {
        this.activityLevel = activityLevel;
    }

    public int getAge() {
        return (int) ChronoUnit.YEARS.between(dateOfBirth, LocalDate.now());
    }

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
        if (activityLevel == null) {
            return bmr;
        }
        return bmr * activityLevel.getTdeeMultiplier();
    }

    public double getTargetCalories(LocalDate date) {
        double tdee = getTDEE(date);
        FitnessGoal goal = getFitnessGoal(date);
        if (goal == null) {
            return tdee;
        }
        return tdee + goal.getCalorieAdjustment();
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
        FitnessGoal goal = getFitnessGoal(date);
        return goal == null ? 0.25 : goal.getProteinRatio();
    }

    private double getCarbsRatio(LocalDate date) {
        FitnessGoal goal = getFitnessGoal(date);
        return goal == null ? 0.45 : goal.getCarbsRatio();
    }

    private double getFatsRatio(LocalDate date) {
        FitnessGoal goal = getFitnessGoal(date);
        return goal == null ? 0.30 : goal.getFatsRatio();
    }

}
