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
    private int userId;
    private String name;
    private int calories;
    private int protein;
    private int carbs;
    private int fats;
    private LocalDate date;

        /**
     * Constructs a Food using default application dependencies.
     */
    public Food() {
        this.date = LocalDate.now();
    }

        /**
     * Constructs a Food using default application dependencies.
     * @param id id
     * @param userId The user id.
     * @param name name
     * @param calories calories
     * @param protein protein
     * @param carbs carbs
     * @param fats fats
     */
    public Food(int id, int userId, String name, int calories, int protein, int carbs, int fats) {
        this.id = id;
        this.userId = userId;
        this.name = name;
        this.calories = calories;
        this.date = LocalDate.now();
        this.protein = protein;
        this.carbs = carbs;
        this.fats = fats;
    }

            /**
     * Returns the name.
     * @return name
     */
    public String getName() {
        return name;
    }

        /**
     * Sets the name.
     * @param name name
     */
    public void setName(String name) {
        this.name = name;
    }

            /**
     * Returns the calories.
     * @return calories
     */
    public int getCalories() {
        return calories;
    }

        /**
     * Sets the calories.
     * @param calories calories
     */
    public void setCalories(int calories) {
        this.calories = calories;
    }

            /**
     * Returns the protein.
     * @return protein
     */
    public int getProtein() {
        return protein;
    }

        /**
     * Sets the protein.
     * @param protein protein
     */
    public void setProtein(int protein) {
        this.protein = protein;
    }

            /**
     * Returns the carbs.
     * @return carbs
     */
    public int getCarbs() {
        return carbs;
    }

        /**
     * Sets the carbs.
     * @param carbs carbs
     */
    public void setCarbs(int carbs) {
        this.carbs = carbs;
    }

            /**
     * Returns the fats.
     * @return fats
     */
    public int getFats() {
        return fats;
    }

        /**
     * Sets the fats.
     * @param fats fats
     */
    public void setFats(int fats) {
        this.fats = fats;
    }

            /**
     * Returns the date.
     * @return date
     */
    public LocalDate getDate() {
        return date;
    }

        /**
     * Sets the date.
     * @param date date
     */
    public void setDate(LocalDate date) {
        this.date = date;
    }

            /**
     * Returns the user id.
     * @return user id
     */
    public int getUserId() {
        return userId;
    }

    /**
     * Sets the user id.
     * @param userId The user id.
     */
    public void setUserId(int userId) {
        this.userId = userId;
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
