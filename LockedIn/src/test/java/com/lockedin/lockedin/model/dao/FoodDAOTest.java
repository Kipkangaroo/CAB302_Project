package com.lockedin.lockedin.model.dao;

import com.lockedin.lockedin.model.entity.Food;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.Connection;
import java.sql.DriverManager;
import java.util.Date;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
/**
 * Unit tests for FoodDAO using an in‑memory SQLite database.
 * Verifies CRUD operations and daily nutritional totals.
 */
public class FoodDAOTest {
    private static final String IN_MEMORY_DB = "jdbc:sqlite::memory:";
    private FoodDAO foodDAO;
    private static final int USER_ID = 1;
    private Date today;

    @BeforeEach
    void setUp() throws Exception {
        Connection conn = DriverManager.getConnection(IN_MEMORY_DB);
        foodDAO = new FoodDAO(conn);
        today = new Date();
    }

    private Food makeFood(String name, int calories, int protein, int carbs, int fats) {
        return new Food(0, USER_ID, name, calories, protein, carbs, fats);
    }

    @Test
    void addFood_assignsGeneratedId() {
        Food food = makeFood("Apple", 95, 0, 25, 0);
        foodDAO.addFood(food);
        assertTrue(food.getId() > 0);
    }

    @Test
    void getFoodsByDate_returnsAddedFood() {
        foodDAO.addFood(makeFood("Banana", 105, 1, 27, 0));
        List<Food> foods = foodDAO.getFoodsByDate(today, USER_ID);
        assertEquals(1, foods.size());
        assertEquals("Banana", foods.get(0).getName());
    }

    @Test
    void getFoodsByDate_returnsEmpty_forDifferentDate() {
        foodDAO.addFood(makeFood("Banana", 105, 1, 27, 0));
        Date yesterday = new Date(today.getTime() - 86_400_000L);
        List<Food> foods = foodDAO.getFoodsByDate(yesterday, USER_ID);
        assertTrue(foods.isEmpty());
    }

    @Test
    void getFoodsByDate_filtersBy_userId() {
        foodDAO.addFood(makeFood("Chicken", 200, 40, 0, 5));
        Food otherUserFood = new Food(0, 99, "Rice", 200, 4, 44, 1);
        foodDAO.addFood(otherUserFood);
        List<Food> foods = foodDAO.getFoodsByDate(today, USER_ID);
        assertEquals(1, foods.size());
        assertEquals("Chicken", foods.get(0).getName());
    }

    @Test
    void removeFood_removesEntry() {
        Food food = makeFood("Egg", 78, 6, 1, 5);
        foodDAO.addFood(food);
        foodDAO.removeFood(food.getId());
        assertTrue(foodDAO.getFoodsByDate(today, USER_ID).isEmpty());
    }

    @Test
    void getDailyTotalByAttribute_sumCalories() {
        foodDAO.addFood(makeFood("A", 100, 0, 0, 0));
        foodDAO.addFood(makeFood("B", 200, 0, 0, 0));
        assertEquals(300, foodDAO.getDailyTotalByAttribute(today, "calories", USER_ID));
    }

    @Test
    void getDailyTotalByAttribute_returnsZero_whenNoFood() {
        assertEquals(0, foodDAO.getDailyTotalByAttribute(today, "calories", USER_ID));
    }

    @Test
    void getDailyTotalByAttribute_sumProtein() {
        foodDAO.addFood(makeFood("Tuna", 120, 28, 0, 1));
        foodDAO.addFood(makeFood("Eggs", 156, 12, 2, 10));
        assertEquals(40, foodDAO.getDailyTotalByAttribute(today, "protein", USER_ID));
    }

    @Test
    void getDailyTotalByAttribute_sumMultipleFoods_carbs() {
        foodDAO.addFood(makeFood("Rice", 200, 4, 44, 1));
        foodDAO.addFood(makeFood("Bread", 80, 3, 15, 1));
        assertEquals(59, foodDAO.getDailyTotalByAttribute(today, "carbs", USER_ID));
    }

    @Test
    void getFoodsByDate_returnedFoodHasCorrectNutrients() {
        foodDAO.addFood(makeFood("Oats", 300, 10, 54, 6));
        Food food = foodDAO.getFoodsByDate(today, USER_ID).get(0);
        assertEquals(300, food.getCalories());
        assertEquals(10, food.getProtein());
        assertEquals(54, food.getCarbs());
        assertEquals(6, food.getFats());
    }
}