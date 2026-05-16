package com.lockedin.lockedin.model.entity.diet;

import java.time.LocalDate;

/**
 * Represents a single food entry logged by a user. Stores nutritional
 * information (calories and
 * macros), the user who logged it, and the date of consumption.
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

    public Food() {
        this.date = LocalDate.now();
    }

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

    public String getName() {
        return name;
    }

    public int getCalories() {
        return calories;
    }

    public int getProtein() {
        return protein;
    }

    public int getCarbs() {
        return carbs;
    }

    public int getFats() {
        return fats;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setCalories(int calories) {
        this.calories = calories;
    }

    public void setProtein(int protein) {
        this.protein = protein;
    }

    public void setCarbs(int carbs) {
        this.carbs = carbs;
    }

    public void setFats(int fats) {
        this.fats = fats;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public void setUserID(int userID) {
        this.userID = userID;
    }

    public int getUserID() {
        return userID;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    @Override
    public String toString() {
        return String.format(
                "%s - %dkcal | %dg protein | %dg carbs | %dg fats",
                name, calories, protein, carbs, fats);
    }
}
