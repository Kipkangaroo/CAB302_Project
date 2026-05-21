package com.lockedin.lockedin.model.entity.diet;

import java.time.LocalDate;

/**
 * Represents a food entry logged by a user, including macros and the consumption date.
 *
 * @author LockedIn Team
 * @version 1.0
 */
public class Food {
    private int id;
    private int userID;
    private String name;
    private int calories;
    private int protein;
    private int carbs;
    private int fats;
    private LocalDate date;

    /**
     * Creates a new Food.
     */
    public Food() {
        this.date = LocalDate.now();
    }

    /**
     * Creates a new Food.
     * @param id The id.
     * @param userID The user id.
     * @param name The name.
     * @param calories The calories.
     * @param protein The protein.
     * @param carbs The carbs.
     * @param fats The fats.
     */
    public Food(int id, int userID, String name, int calories, int protein, int carbs, int fats) {
        this.id = id;
        this.userID = userID;
        this.name = name;
        this.calories = calories;
        this.date = LocalDate.now();
        this.protein = protein;
        this.carbs = carbs;
        this.fats = fats;
    }

    /**
     * Returns the name.
     * @return The name.
     */
    public String getName() {
        return name;
    }

    /**
     * Sets the name.
     * @param name The name.
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Returns the calories.
     * @return The calories.
     */
    public int getCalories() {
        return calories;
    }

    /**
     * Sets the calories.
     * @param calories The calories.
     */
    public void setCalories(int calories) {
        this.calories = calories;
    }

    /**
     * Returns the protein.
     * @return The protein.
     */
    public int getProtein() {
        return protein;
    }

    /**
     * Sets the protein.
     * @param protein The protein.
     */
    public void setProtein(int protein) {
        this.protein = protein;
    }

    /**
     * Returns the carbs.
     * @return The carbs.
     */
    public int getCarbs() {
        return carbs;
    }

    /**
     * Sets the carbs.
     * @param carbs The carbs.
     */
    public void setCarbs(int carbs) {
        this.carbs = carbs;
    }

    /**
     * Returns the fats.
     * @return The fats.
     */
    public int getFats() {
        return fats;
    }

    /**
     * Sets the fats.
     * @param fats The fats.
     */
    public void setFats(int fats) {
        this.fats = fats;
    }

    /**
     * Returns the date.
     * @return The date.
     */
    public LocalDate getDate() {
        return date;
    }

    /**
     * Sets the date.
     * @param date The date.
     */
    public void setDate(LocalDate date) {
        this.date = date;
    }

    /**
     * Returns the user id.
     * @return The user id.
     */
    public int getUserID() {
        return userID;
    }

    /**
     * Sets the user id.
     * @param userID The user id.
     */
    public void setUserID(int userID) {
        this.userID = userID;
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
     * Returns a string representation of this object.
     * @return The resulting text.
     */

    @Override
    public String toString() {
        return String.format(
                "%s - %dkcal | %dg protein | %dg carbs | %dg fats",
                name, calories, protein, carbs, fats);
    }
}
