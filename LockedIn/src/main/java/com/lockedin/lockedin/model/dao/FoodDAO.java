package com.lockedin.lockedin.model.dao;

import com.lockedin.lockedin.model.db.SqliteConnection;
import com.lockedin.lockedin.model.entity.Food;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class FoodDAO {
    private static final String FOOD_DB_FILE = "food.db";
    private final Connection connection;

    public FoodDAO() {
        this.connection = SqliteConnection.getInstance(FOOD_DB_FILE);
        createFoodTable();
    }

    /** For testing: accepts an existing (e.g. in-memory) connection. */
    public FoodDAO(Connection connection) {
        this.connection = connection;
        createFoodTable();
    }

    private void createFoodTable() {
        String sql = "CREATE TABLE IF NOT EXISTS foods (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "user_id INTEGER NOT NULL, " +
                "name TEXT NOT NULL, " +
                "calories INT NOT NULL, " +
                "protein INT NOT NULL, " +
                "carbs INT NOT NULL, " +
                "fats INT NOT NULL, " +
                "date TEXT NOT NULL" +
                ")";

        try (Statement statement = connection.createStatement()) {
            statement.execute(sql);
        } catch (SQLException e) {
            throw new RuntimeException("Failed to create food table", e);
        }
    }

    public void addFood(Food food) {
        String sql = "INSERT INTO foods (user_id, name, calories, protein, carbs, fats, date) VALUES (?, ?, ?, ?, ?, ?, ?)";
        try (PreparedStatement statement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            statement.setInt(1, food.getUserID());
            statement.setString(2, food.getName());
            statement.setInt(3, food.getCalories());
            statement.setInt(4, food.getProtein());
            statement.setInt(5, food.getCarbs());
            statement.setInt(6, food.getFats());
            statement.setString(7, new Date(food.getDate().getTime()).toString());
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

    public List<Food> getFoodsByDate(java.util.Date targetDate, int userID) {
        String sql = "SELECT id, user_id, name, calories, protein, carbs, fats, date FROM foods WHERE date = ? AND user_id = ? ORDER BY name";
        List<Food> foods = new ArrayList<>();
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, new Date(targetDate.getTime()).toString());
            statement.setInt(2, userID);
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

    public void removeFood(int id) {
        String sql = "DELETE FROM foods WHERE id = ?";
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, id);
            statement.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Failed to remove food", e);
        }
    }

    public int getDailyTotalByAttribute(java.util.Date targetDate, String attribute, int userID) {
        String sql = "SELECT COALESCE(SUM(" + attribute + "), 0) AS total FROM foods WHERE date = ? AND user_id = ?";
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, new Date(targetDate.getTime()).toString());
            statement.setInt(2, userID);
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

    private Food mapRowToFood(ResultSet rs) throws SQLException {
        Food food = new Food();
        food.setId(rs.getInt("id"));
        food.setUserID(rs.getInt("user_id"));
        food.setName(rs.getString("name"));
        food.setCalories(rs.getInt("calories"));
        food.setProtein(rs.getInt("protein"));
        food.setCarbs(rs.getInt("carbs"));
        food.setFats(rs.getInt("fats"));
        food.setDate(Date.valueOf(rs.getString("date")));
        return food;
    }
}

