package com.lockedin.lockedin.model.dao;

import com.lockedin.lockedin.model.db.SqliteConnection;
import com.lockedin.lockedin.model.entity.diet.Food;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Data access object for food records.
 * @author LockedIn Team
 * @version 1.0
 */
public class FoodDAO {
    private static final String FOOD_DB_FILE = "food.db";
    private final Connection connection;

        /**
     * Constructs a FoodDAO using default application dependencies.
     */
    public FoodDAO() {
        this.connection = SqliteConnection.getInstance(FOOD_DB_FILE);
        createFoodTable();
    }

        /**
     * Constructs a FoodDAO using default application dependencies.
     * @param connection connection
     */
    public FoodDAO(Connection connection) {
        this.connection = connection;
        createFoodTable();
    }

        /**
     * Create food table.
     */
    private void createFoodTable() {
        String sql = "CREATE TABLE IF NOT EXISTS foods ("
                + "id INTEGER PRIMARY KEY AUTOINCREMENT, "
                + "user_id INTEGER NOT NULL, "
                + "name TEXT NOT NULL, "
                + "calories INT NOT NULL, "
                + "protein INT NOT NULL, "
                + "carbs INT NOT NULL, "
                + "fats INT NOT NULL, "
                + "date TEXT NOT NULL"
                + ")";

        try (Statement statement = connection.createStatement()) {
            statement.execute(sql);
        } catch (SQLException e) {
            throw new RuntimeException("Failed to create food table", e);
        }
    }

        /**
     * Add food.
     * @param food food
     * @param date date
     */
    public void addFood(Food food, LocalDate date) {
        String sql = "INSERT INTO foods (user_id, name, calories, protein, carbs, fats, date) VALUES (?,"
                + " ?, ?, ?, ?, ?, ?)";
        try (PreparedStatement statement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            statement.setInt(1, food.getUserId());
            statement.setString(2, food.getName());
            statement.setInt(3, food.getCalories());
            statement.setInt(4, food.getProtein());
            statement.setInt(5, food.getCarbs());
            statement.setInt(6, food.getFats());
            statement.setString(7, date.toString());
            statement.executeUpdate();
            try (ResultSet generatedKeys = statement.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    food.setId(generatedKeys.getInt(1));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed to add food", e);
        }
    }

            /**
     * Returns the foods by date.
     * @param targetDate The target date.
     * @param userId The user id.
     * @return foods by date
     */
    public List<Food> getFoodsByDate(LocalDate targetDate, int userId) {
        String sql = "SELECT id, user_id, name, calories, protein, carbs, fats, date FROM foods WHERE"
                + " date = ? AND user_id = ? ORDER BY name";
        List<Food> foods = new ArrayList<>();
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, targetDate.toString());
            statement.setInt(2, userId);
            try (ResultSet rs = statement.executeQuery()) {
                while (rs.next()) {
                    foods.add(mapRowToFood(rs));
                }
                return foods;
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed to get foods by date", e);
        }
    }

        /**
     * Updates an existing food entry's name and nutritional values.
     * @param id The food id.
     * @param name name
     * @param calories calories
     * @param protein protein
     * @param carbs carbs
     * @param fats fats
     */
    public void updateFood(int id, String name, double calories, double protein,
                           double carbs, double fats) {
        String sql = "UPDATE foods SET name = ?, calories = ?, protein = ?, carbs = ?, fats = ?"
                + " WHERE id = ?";
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, name);
            statement.setInt(2, (int) calories);
            statement.setInt(3, (int) protein);
            statement.setInt(4, (int) carbs);
            statement.setInt(5, (int) fats);
            statement.setInt(6, id);
            statement.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Failed to update food", e);
        }
    }

        /**
     * Remove food.
     * @param id id
     */
    public void removeFood(int id) {
        String sql = "DELETE FROM foods WHERE id = ?";
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, id);
            statement.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Failed to remove food", e);
        }
    }

        /**
     * Delete all for user.
     * @param userId The user id.
     */
    public void deleteAllForUser(int userId) {
        String sql = "DELETE FROM foods WHERE user_id = ?";
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, userId);
            statement.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Failed to delete foods for user " + userId, e);
        }
    }

            /**
     * Returns the weekly calorie tracking.
     * @param userId The user id.
     * @return weekly calorie tracking
     */
    public boolean[] getWeeklyCalorieTracking(int userId) {
        boolean[] streakDays = new boolean[7];
        for (int i = 0; i < 7; i++) {
            LocalDate date = LocalDate.now().minusDays(i);
            List<Food> foods = getFoodsByDate(date, userId);
            streakDays[i] = !foods.isEmpty();
        }
        return streakDays;
    }

            /**
     * Returns the daily total by attribute.
     * @param targetDate The target date.
     * @param attribute attribute
     * @param userId The user id.
     * @return daily total by attribute
     */
    public int getDailyTotalByAttribute(LocalDate targetDate, String attribute, int userId) {
        String sql = "SELECT COALESCE(SUM("
                + attribute
                + "), 0) AS total FROM foods WHERE date = ? AND user_id = ?";
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, targetDate.toString());
            statement.setInt(2, userId);
            try (ResultSet rs = statement.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("total");
                }
                return 0;
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed to get daily total for " + attribute, e);
        }
    }

    /**
     * Returns total calories per day for a user within a date range (inclusive),
     * ordered by date ascending. Days with no food logged are omitted from the map.
     * @param start The start date.
     * @param end The end date.
     * @param userId The user id.
     * @return Map of date to total calories for that day.
     */
    public Map<LocalDate, Double> getDailyTotalsForRange(
            LocalDate start, LocalDate end, int userId) {
        Map<LocalDate, Double> totals = new LinkedHashMap<>();
        String sql = """
                SELECT date, SUM(calories) AS total
                FROM foods
                WHERE user_id=? AND date BETWEEN ? AND ?
                GROUP BY date
                ORDER BY date
                """;
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, userId);
            statement.setString(2, start.toString());
            statement.setString(3, end.toString());
            try (ResultSet rs = statement.executeQuery()) {
                while (rs.next()) {
                    totals.put(LocalDate.parse(rs.getString("date")), rs.getDouble("total"));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed to get daily calorie totals for range", e);
        }
        return totals;
    }

        /**
     * Map row to food.
     * @param rs rs
     * @throws SQLException If the operation fails.
     */
    private Food mapRowToFood(ResultSet rs) throws SQLException {
        Food food = new Food();
        food.setId(rs.getInt("id"));
        food.setUserId(rs.getInt("user_id"));
        food.setName(rs.getString("name"));
        food.setCalories(rs.getInt("calories"));
        food.setProtein(rs.getInt("protein"));
        food.setCarbs(rs.getInt("carbs"));
        food.setFats(rs.getInt("fats"));
        food.setDate(LocalDate.parse(rs.getString("date")));
        return food;
    }
}
