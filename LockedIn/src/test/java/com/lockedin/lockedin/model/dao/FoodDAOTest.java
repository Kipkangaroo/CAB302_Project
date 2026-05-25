package com.lockedin.lockedin.model.dao;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.lockedin.lockedin.model.entity.diet.Food;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.Connection;
import java.sql.DriverManager;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
/**
 * Unit tests for FoodDAO using an in-memory SQLite database.
 *
 * @author LockedIn Team
 * @version 1.0
 */
public class FoodDAOTest {
    private static final String IN_MEMORY_DB = "jdbc:sqlite::memory:";
    private static final int USER_ID = 1;
    private FoodDAO foodDAO;
    private LocalDate today;

    /**
     * Prepares fixtures before each test method runs.
     */
    @BeforeEach
    void setUp() throws Exception {
        Connection conn = DriverManager.getConnection(IN_MEMORY_DB);
        foodDAO = new FoodDAO(conn);
        today = LocalDate.now();
    }

    /**
     * Creates a food entity with the given macros for test insertion.
     */
    private Food makeFood(String name, int calories, int protein, int carbs, int fats) {
        return new Food(0, USER_ID, name, calories, protein, carbs, fats);
    }


    /**
     * Verifies addFood: assigns Generated Id.
     */
    @Test
    void addFood_assignsGeneratedId() {
        Food food = makeFood("Apple", 95, 0, 25, 0);
        foodDAO.addFood(food, today);
        assertTrue(food.getId() > 0);
    }


    /**
     * Verifies getFoodsByDate: returns Added Food.
     */
    @Test
    void getFoodsByDate_returnsAddedFood() {
        foodDAO.addFood(makeFood("Banana", 105, 1, 27, 0), today);
        List<Food> foods = foodDAO.getFoodsByDate(today, USER_ID);
        assertEquals(1, foods.size());
        assertEquals("Banana", foods.get(0).getName());
    }


    /**
     * Verifies getFoodsByDate: returns Empty for Different Date.
     */
    @Test
    void getFoodsByDate_returnsEmpty_forDifferentDate() {
        foodDAO.addFood(makeFood("Banana", 105, 1, 27, 0), today);
        LocalDate yesterday = LocalDate.now().minusDays(1);
        List<Food> foods = foodDAO.getFoodsByDate(yesterday, USER_ID);
        assertTrue(foods.isEmpty());
    }


    /**
     * Verifies getFoodsByDate: filters By user Id.
     */
    @Test
    void getFoodsByDate_filtersBy_userId() {
        foodDAO.addFood(makeFood("Chicken", 200, 40, 0, 5), today);
        Food otherUserFood = new Food(0, 99, "Rice", 200, 4, 44, 1);
        foodDAO.addFood(otherUserFood, today);
        List<Food> foods = foodDAO.getFoodsByDate(today, USER_ID);
        assertEquals(1, foods.size());
        assertEquals("Chicken", foods.get(0).getName());
    }


    /**
     * Verifies removeFood: removes Entry.
     */
    @Test
    void removeFood_removesEntry() {
        Food food = makeFood("Egg", 78, 6, 1, 5);
        foodDAO.addFood(food, today);
        foodDAO.removeFood(food.getId());
        assertTrue(foodDAO.getFoodsByDate(today, USER_ID).isEmpty());
    }


    /**
     * Verifies getDailyTotalByAttribute: sum Calories.
     */
    @Test
    void getDailyTotalByAttribute_sumCalories() {
        foodDAO.addFood(makeFood("A", 100, 0, 0, 0), today);
        foodDAO.addFood(makeFood("B", 200, 0, 0, 0), today);
        assertEquals(300, foodDAO.getDailyTotalByAttribute(today, "calories", USER_ID));
    }


    /**
     * Verifies getDailyTotalByAttribute: returns Zero when No Food.
     */
    @Test
    void getDailyTotalByAttribute_returnsZero_whenNoFood() {
        assertEquals(0, foodDAO.getDailyTotalByAttribute(today, "calories", USER_ID));
    }


    /**
     * Verifies getDailyTotalByAttribute: sum Protein.
     */
    @Test
    void getDailyTotalByAttribute_sumProtein() {
        foodDAO.addFood(makeFood("Tuna", 120, 28, 0, 1), today);
        foodDAO.addFood(makeFood("Eggs", 156, 12, 2, 10), today);
        assertEquals(40, foodDAO.getDailyTotalByAttribute(today, "protein", USER_ID));
    }


    /**
     * Verifies getDailyTotalByAttribute: sum Multiple Foods carbs.
     */
    @Test
    void getDailyTotalByAttribute_sumMultipleFoods_carbs() {
        foodDAO.addFood(makeFood("Rice", 200, 4, 44, 1), today);
        foodDAO.addFood(makeFood("Bread", 80, 3, 15, 1), today);
        assertEquals(59, foodDAO.getDailyTotalByAttribute(today, "carbs", USER_ID));
    }


    /**
     * Verifies getFoodsByDate: returned Food Has Correct Nutrients.
     */
    @Test
    void getFoodsByDate_returnedFoodHasCorrectNutrients() {
        foodDAO.addFood(makeFood("Oats", 300, 10, 54, 6), today);
        Food food = foodDAO.getFoodsByDate(today, USER_ID).get(0);
        assertEquals(300, food.getCalories());
        assertEquals(10, food.getProtein());
        assertEquals(54, food.getCarbs());
        assertEquals(6, food.getFats());
    }


    /**
     * Verifies updateFood: persists New Values.
     */
    @Test
    void updateFood_persistsNewValues() {
        Food food = makeFood("Chicken", 200, 30, 0, 5);
        foodDAO.addFood(food, today);

        foodDAO.updateFood(food.getId(), "Grilled Chicken", 180, 35, 2, 4);

        Food updated = foodDAO.getFoodsByDate(today, USER_ID).get(0);
        assertEquals("Grilled Chicken", updated.getName());
        assertEquals(180, updated.getCalories());
        assertEquals(35, updated.getProtein());
        assertEquals(2, updated.getCarbs());
        assertEquals(4, updated.getFats());
    }


    /**
     * Verifies getDailyTotalsForRange: returns Only Dates In Range.
     */
    @Test
    void getDailyTotalsForRange_returnsOnlyDatesInRange() {
        LocalDate yesterday = today.minusDays(1);
        LocalDate fiveDaysAgo = today.minusDays(5);

        foodDAO.addFood(makeFood("Chicken", 300, 30, 0, 10), today);
        foodDAO.addFood(makeFood("Rice", 200, 4, 44, 1), yesterday);
        foodDAO.addFood(makeFood("Oats", 100, 5, 20, 2), fiveDaysAgo);

        Map<LocalDate, Double> result =
                foodDAO.getDailyTotalsForRange(yesterday, today, USER_ID);

        assertEquals(2, result.size());
        assertFalse(result.containsKey(fiveDaysAgo));
        assertEquals(300.0, result.get(today), 0.0);
        assertEquals(200.0, result.get(yesterday), 0.0);
    }
}
